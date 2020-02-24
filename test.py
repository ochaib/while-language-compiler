from difflib import Differ
import os
from subprocess import Popen, PIPE
from termcolor import colored
from time import time
from tqdm import tqdm
MAX_POOL_SIZE=40 # be careful
EMU_MAX_POOL_SIZE=5 # be EVEN MORE careful

# Collect testcases
def get_testcases():
    if not os.path.exists('testcases'):
        error("Could not find testcases directory, please run from project root")
        exit(-1)

    programs = set(
        f'{dir}/{file}'
            for dir, _, files in os.walk('testcases')
                for file in files
                    if file.endswith('.wacc')
    )

    valid_programs = set(program for program in programs if program.startswith('testcases/valid/'))
    invalid_programs = programs ^ valid_programs
    semantic_error_programs = set(
        program for program in invalid_programs
            if program.startswith('testcases/invalid/semanticErr')
    )
    syntax_error_programs = invalid_programs ^ semantic_error_programs
    return {
        "valid": valid_programs,
        "semantic": semantic_error_programs,
        "syntax": syntax_error_programs
    }

# Run our compiler on a program
# Runs in batch mode, so our assembly files will be under `assembly/`
compile_program = lambda prog: Popen(["./compile", prog + " --batch"], stdout=PIPE, stderr=PIPE)
# Assembled a single file
assemble_file = lambda asm_fn, exe_fn: Popen([
    "arm-linux-gnueabi-gcc",
    f"-o{exe_fn}",
    "-mcpu=arm1176jzf-s",
    "-mtune=arm1176jzf-s",
    asm_fn
], stdout=PIPE, stderr=PIPE)
# Emulate an ARM executable
emulate_ARM = lambda exe: Popen([
    'qemu-arm',
    '-L /usr/arm-linux/gnueabi/',
    exe
], stdout=PIPE, stderr=PIPE)

# Compilation
def compile_batch(testcases):
    testcases = list(testcases) # we need to chunk it, must be a list
    compiled = []
    # we start several processes at a time
    # if you run too many at once the docker container crashes
    print(f"> Compilation in batches of {MAX_POOL_SIZE}")
    for i in tqdm(range(0, len(testcases), MAX_POOL_SIZE)):
        compiling = []
        for testcase in testcases[i:i+MAX_POOL_SIZE]:
            compiling.append((testcase, compile_program(testcase)))
        for _, p in compiling:
            p.wait()
            p.stdout.close()
            p.stderr.close()
            p.kill()
        compiled.extend(compiling)
    return {t: p for t, p in compiled}
def compile_all(testcases):
    print("Compiling valid programs:")
    t = time()
    testcases["valid"] = compile_batch(testcases["valid"])
    print("Compiling semantic programs:")
    testcases["semantic"] = compile_batch(testcases["semantic"])
    print("Compiling syntax programs")
    testcases["syntax"] = compile_batch(testcases["syntax"])
    t = time() - t
    print(f"Full compilation took {t}s")
    return testcases
# Assembly
# Input should be a list of tuples (fn, asm_fn, exe_fn)
def assemble_batch(asm_exe_fns):
    print("Assembling assembly files:")
    t = time()
    assembled = []
    # we start several processes at a time
    # if you run too many at once the docker container crashes
    print(f"> Assembling in batches of {MAX_POOL_SIZE}")
    for i in tqdm(range(0, len(asm_exe_fns), MAX_POOL_SIZE)):
        assembling = []
        for fn, asm_fn, exe_fn in asm_exe_fns[i:i+MAX_POOL_SIZE]:
            assembling.append((fn, assemble_file(asm_fn, exe_fn)))
        for _, p in assembling:
            p.wait()
            p.stdout.close()
            p.stderr.close()
            p.kill()
        assembled.extend(assembling)
    t = time() - t
    print(f"Assembly took {t}s")
    return {f: p for f, p in assembled}
# Emulator
# Input should be a list of tuples (fn, exe_fn, ref_exe_fn)
def emulate_batch(exe_fns):
    print("Emulating executables:")
    t = time()
    emulated = []
    # we start several processes at a time
    # if you run too many at once the docker container crashes
    print(f"> Emulating in batches of {EMU_MAX_POOL_SIZE}")
    for i in tqdm(range(0, len(exe_fns), EMU_MAX_POOL_SIZE)):
        emulating = []
        for fn, exe_fn, ref_exe_fn in exe_fns[i:i+MAX_POOL_SIZE]:
            emulating.append((fn, emulate_ARM(exe_fn), emulate_ARM(ref_exe_fn)))
        for _, p, p_ref in emulating:
            p.wait()
            p.stdout.close()
            p.stderr.close()
            p.kill()
            p_ref.wait()
            p_ref.stdout.close()
            p_ref.stderr.close()
            p_ref.kill()
        emulated.extend(emulating)
    t = time() - t
    print(f"Emulation took {t}s")
    return {f: (p, p_ref) for f, p, p_ref in emulated}


# Helper function to run tests
# each test should return a tuple (passed, total tested)
def get_coverage(*tests):
    testcases = get_testcases()
    compiled = compile_all(testcases)
    passed, total = (0, 0)
    for test in tests:
        print('-'*10)
        p, t = test(compiled)
        passed += p
        total += t
    return (passed, total)


# Quick message
log = lambda msg: print(colored(msg, color="yellow"))
error = lambda msg: print(colored(msg, color="red"))

# Check for status as per spec:
# Valid = exit code 0, Syntax Error = exit code 100, Semantic Error = exit code 200
check_valid = lambda proc: proc.returncode == 0
check_syntax = lambda proc: proc.returncode == 100
check_semantic = lambda proc: proc.returncode == 200

def every_valid_program_should_compile(compiled):
    log("[TEST] All valid programs should compile")
    passed = 0
    for fn, proc in compiled["valid"].items():
        if check_valid(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["valid"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_syntax_error_should_fail(compiled):
    log("[TEST] All programs with syntax errors should fail to compile with a syntax error return code")
    passed = 0
    for fn, proc in compiled["syntax"].items():
        if check_syntax(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["syntax"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_semantic_error_should_fail(compiled):
    log("[TEST] All programs with semantic errors should fail to compile with a semantic error return code")
    passed = 0
    for fn, proc in compiled["semantic"].items():
        if check_semantic(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["semantic"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_valid_program_generates_assembly(compiled):
    log("[TEST] All valid programs should generate assembly files when compiled")
    passed = 0
    for fn, proc in compiled["valid"].items():
        asm_fn = fn[fn.rfind('/')+1:fn.rfind('.')] + '.s'
        if os.path.exists(f'assembly/{asm_fn}'): passed += 1
        else: error(f"FAILED {fn}: MISSING {asm_fn}")
    total = len(compiled["valid"])
    print(f"Passed {passed}/{total}")
    return passed, total

def generated_assembly_has_same_output(compiled):
    log("[TEST] All generated assembly files should run the same as the ones made by the reference compiler.")
    passed = 0
    # Create list of assembly files
    can_assemble = []
    for fn, proc in compiled["valid"].items():
        asm_fn = fn[fn.rfind('/')+1:fn.rfind('.')] + '.s'
        exe_fn = asm_fn[:-len('.s')]
        if os.path.exists(f'assembly/{asm_fn}'):
            can_assemble.append((fn, f'assembly/{asm_fn}', f'assembly/{exe_fn}'))
        else:
            error(f"FAILED {fn}: MISSING {asm_fn} so can't assemble")
    # Assemble them in parallel
    assembled = assemble_batch(can_assemble)
    # Create list of executable files
    can_emulate = []
    for fn, proc in assembled.items():
        ref_exe_fn = fn[:fn.rfind('.')]
        exe_fn = ref_exe_fn[ref_exe_fn.rfind('/'):]
        if proc.returncode != 0:
            error(f"FAILED {fn}: GCC exited with {proc.returncode}")
        elif not os.path.exists(ref_exe_fn):
            error(f"FAILED {fn}: No matching executable to use as reference")
        else:
            can_emulate.append((fn, f'assembly/{exe_fn}', ref_exe_fn))
    # Emulate them in parallel
    emulated = emulate_batch(can_emulate)
    # Compare to reference
    differ = Differ()
    for fn, proc, proc_ref in emulated.items():
        # Return code should match
        if proc.returncode != proc_ref.returncode:
            error(f"FAILED {fn}: Executable exited with {proc.returncode} but was expecting {proc_ref.returncode}")
        elif proc.stdout != proc_ref.stdout:
            # Diff the output
            proc_output = proc.stdout.decode('utf-8')
            proc_ref_output = proc_ref.stdout.decode('utf-8')
            diff = ''.join(
                differ.compare(
                    proc_output.splitlines(1),
                    proc_ref_output.splitlines(1)
                )
            )
            diff_fn = fn[fn.rfind('/')+1:fn.rfind('.')] + '.diff'
            with open(f'test_logs/{diff_fn}', 'w') as f:
                f.write(diff)
            error(f"FAILED {fn}: Output did not match, stored diff in test_logs/{diff_fn}")
        else: passed += 1
    total = len(compiled["valid"])
    print(f"Passed {passed}/{total}")
    return passed, total

passed, total = get_coverage(
    every_valid_program_should_compile,
    every_syntax_error_should_fail,
    every_semantic_error_should_fail,
    every_valid_program_generates_assembly,
    generated_assembly_has_same_output
)
coverage = "%.2f" % (passed/total*100)
print('-'*10)
print(f'coverage: {coverage}% ({passed}/{total})')