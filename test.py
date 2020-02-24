import os
import json
import re
from subprocess import Popen, PIPE
import sys
from termcolor import colored
from time import time
from tqdm import tqdm
MAX_POOL_SIZE=20

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
compile_program = lambda prog: Popen(["./compile", prog, "--batch"], stdout=PIPE, stderr=PIPE)

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
        for _, p in compiling: p.wait()
        compiled.extend(compiling)
    return {t: p for t, p in compiled}
def compile_all(testcases):
    print("Compiling valid programs:")
    testcases["valid"] = compile_batch(testcases["valid"])
    print("Compiling semantic programs:")
    testcases["semantic"] = compile_batch(testcases["semantic"])
    print("Compiling syntax programs")
    testcases["syntax"] = compile_batch(testcases["syntax"])
    return testcases

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
    for (fn, proc) in compiled["valid"].items():
        if check_valid(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["valid"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_syntax_error_should_fail(compiled):
    log("[TEST] All programs with syntax errors should fail to compile with a syntax error return code")
    passed = 0
    for (fn, proc) in compiled["syntax"].items():
        if check_syntax(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["syntax"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_semantic_error_should_fail(compiled):
    log("[TEST] All programs with semantic errors should fail to compile with a semantic error return code")
    passed = 0
    for (fn, proc) in compiled["semantic"].items():
        if check_semantic(proc): passed += 1
        else: error(f"FAILED {fn}: {proc.returncode}")
    total = len(compiled["semantic"])
    print(f"Passed {passed}/{total}")
    return passed, total

def every_valid_program_generates_assembly(compiled):
    log("[TEST] All valid programs should generate assembly files when compiled")
    passed = 0
    for (fn, proc) in compiled["valid"].items():
        asm_fn = fn[fn.rfind('/')+1:fn.rfind('.')] + '.s'
        if os.path.exists(f'assembly/{asm_fn}'): passed += 1
        else: error(f"FAILED {fn}: MISSING {asm_fn}")
    total = len(compiled["valid"])
    print(f"Passed {passed}/{total}")
    return passed, total

passed, total = get_coverage(
    every_valid_program_should_compile,
    every_syntax_error_should_fail,
    every_semantic_error_should_fail,
    every_valid_program_generates_assembly
)
coverage = "%.2f" % (passed/total*100)
print('-'*10)
print(f'coverage: {coverage}% ({passed}/{total})')