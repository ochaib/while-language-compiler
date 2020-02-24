from difflib import Differ
import json
import os
import re
from subprocess import run, PIPE
import sys
from termcolor import colored
from time import time
from tqdm import tqdm

differ = Differ()
error = lambda msg: print(colored(msg, color="white", on_color="on_red"))

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

# Compile Program using our compiler
compile_program = lambda prog: run(["./compile", prog], stdout=PIPE, stderr=PIPE)
# Clean out any ANSI escape sequences in stdout
# they take format \x1B[<code>m where code is [0-9;]* e.g. \x1B[99;33;44;14m etc
clean_stdout = lambda stdout: re.sub(r'\x1B\[[0-9;]*m', '', stdout)
# Check for status as per spec:
# Valid = exit code 0, Syntax Error = exit code 100, Semantic Error = exit code 200
check_validity = lambda proc: proc.returncode == 0
check_syntax_error = lambda proc: proc.returncode == 100
check_semantic_error = lambda proc: proc.returncode == 200
check_compiler_error = lambda proc: proc.returncode == 1

def test(prog):
    compiled = compile_program(prog)

    output = compiled.stdout.decode('utf-8')
    output = clean_stdout(output)

    compiler_errors = compiled.stderr.decode('utf-8')
    compiler_errors = clean_stdout(compiler_errors)

    return {
        'file': prog,
        'compiled': compiled,
        # Parsed output
        'output': output,
        'compiler output': compiler_errors,
        # Check status
        'valid': check_validity(compiled),
        'syntax error': check_syntax_error(compiled),
        'semantic error': check_semantic_error(compiled),
        'compiler error': check_compiler_error(compiled)
    }

# using generator for lazy eval
test_many = lambda progs: (test(prog) for prog in progs)

results = {
    'valid': test_many(valid_programs),
    'invalid': {
        'semantic': test_many(semantic_error_programs),
        'syntax': test_many(syntax_error_programs)
    },
    'assembled': []
}


##### TESTS #####
def every_valid_program_should_compile():
    test_func = lambda res: res['valid']
    return {
        "name": "compiles valid programs",
        "description": "every valid program should compile successfully (i.e. with exit status 0)",
        "test": test_func,
        "testable": results['valid']
    }
    # every valid program should compile successfully

def every_syntax_error_should_fail():
    test_func = lambda res: res['syntax error']
    return {
        "name": "syntax errors don't compile",
        "description": "every invalid program due to a syntax error should exit 100 to indicate a syntax error",
        "test": test_func,
        "testable": results['invalid']['syntax']
    }

def every_semantic_error_should_fail():
    test_func = lambda res: res['semantic error']
    return {
        "name": "semantic errors don't compile",
        "description": "every invalid program due to a semantic error should exit 200 to indicate a semantic error",
        "test": test_func,
        "testable": results['invalid']['semantic']
    }

def every_valid_program_generates_assembly():
    def test_func(result):
        # if x/y/z/prog.wacc is assembled it creates
        # a file in the root directory called prog.s
        path = result['file']
        out_file = path[path.rfind('/')+1:path.rfind('.')] + '.s'
        if os.path.exists(out_file):
            result['assembly'] = out_file
            results['assembled'].append(result)
            return True
        return False
    return {
        "name": "every valid program generates assembly",
        "description": "every valid program should compile to an assembly script in the root directory",
        "test": test_func,
        "testable": results['valid']
    }

def generated_assembly_matches_reference():
    # this relies on a prior test to check which programs are assembled
    if len(results['assembled']) == 0:
        run_test(every_valid_program_generates_assembly)

    # and also on the ASM reference tests being generated
    if not os.path.exists('asm-tests.json'):
        error("ASM tests have not been generated, running make_asm_tests.py...")
        os.system('python make_asm_tests.py')

    with open('asm-tests.json') as f:
        references = json.load(f)
    diffs = {}

    def test_func(result):
        if result['file'] not in references:
            error(result['file'] + ' is missing from asm-tests.json')
            diffs[result['file']] = 'MISSING TEST'
            return False
        reference = references[result['file']]
        with open(result['assembly']) as f:
            generated = f.read()
        if generated != reference:
            # make diff, we use splitlines to keep the newline
            generated_lines = generated.splitlines(1)
            reference_lines = reference.splitlines(1)
            diffs[result['file']] = ''.join(differ.compare(generated_lines, reference_lines))
            return False
        return True

    def handle_failure(result):
        print(colored(result['file'] + ' did not match reference, diff:', color="red"))

        # get matching diff
        diff = diffs[result['file']]

        # print out diff
        print('#'*5)
        print(diff)
        print('#'*5)

        # save diff to test log directory
        diff_out_dir = 'test_logs/' + result['file'][:result['file'].rfind('/')]
        diff_filename = result['file'].split('/')[-1].replace('.wacc', '.diff')
        if not os.path.exists(diff_out_dir):
            os.makedirs(diff_out_dir)
        with open(f'{diff_out_dir}/{diff_filename}', 'w') as diff_file:
            diff_file.write(diff)

    return {
        "name": "generated assembly matches reference",
        "description": "the generated assembly script from the compiler should match the reference compiler",
        "test": test_func,
        "testable": results['assembled'],
        "failure handler": handle_failure
    }

### TEST RUN HELPER ###
def run_test(make_test_def):
    test_def = make_test_def()

    name = test_def['name']
    description = test_def['description']
    print(colored(f'{"-"*9} {name} {"-"*9}', color="magenta"))
    print(colored(f'Description: {description}', color="magenta"))

    # when we exhaust the generator it will eval, so we time this as test time
    t = time()
    # exhaust the generator to compile
    test_def['testable'] = [testcase for testcase in tqdm(test_def['testable'])]
    # for each testcase in the test definition, run the test func
    # if the test func returns false that indicates a failure
    failures = [testcase for testcase in tqdm(test_def['testable']) if not test_def['test'](testcase)]
    t = time() - t
    print(colored(f'Took {t}ms', color="yellow"))

    total = len(test_def['testable'])
    passed = total - len(failures)
    print(f'Passed {passed}/{total}')

    # print out any failures
    if passed < total:
        error(f'{"-"*6} TEST FAILURES {"-"*6}')
        for failure in failures:
            if "failure handler" in test_def:
                test_def["failure handler"](failure)
                continue
            print(colored('> ' + failure['file'], color='blue'))
            print(colored("Exit status: " + str(failure['compiled'].returncode), color="red"))

            # print out any errors in compilation
            if failure['compiler error']:
                print(colored('Compiler Errors:', color='red'))
                print(failure['compiler output'])
                print()

            # print out any other errors
            print(colored('Errors:', color='red'))
            if failure['semantic error']:
                print(colored('Semantic Error', color="red"))
            if failure['syntax error']:
                print(colored('Syntax Error', color="red"))
            print(failure['output'])

    return passed, total

# run in bulk
def run_tests(*test_defs):
    passed, total = 0, 0
    for test_def in test_defs:
        p, t = run_test(test_def)
        passed += p
        total += t
    return passed, total

# every valid program should compile and invalid program should not compile
parse_tests = [
    every_valid_program_should_compile,
    every_syntax_error_should_fail,
    every_semantic_error_should_fail
]

# compiler should generate correct assembly
compile_tests = [
    every_valid_program_generates_assembly,
    generated_assembly_matches_reference
]

tests = []
if '--skip-parse' not in sys.argv:
    tests.extend(parse_tests)
if '--skip-compile' not in sys.argv:
    tests.extend(compile_tests)

passed, total = run_tests(*tests)

coverage = "%.2f" % (passed/total*100)
print(f'coverage: {coverage}% ({passed}/{total})')