import os
import re
from subprocess import run, PIPE
from termcolor import colored
from time import time
from tqdm import tqdm
import json

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
    }
}


##### TESTS #####
def every_valid_program_should_compile(results):
    test_func = lambda res: res['valid']
    return {
        "name": "compiles valid programs",
        "description": "every valid program should compile successfully (i.e. with exit status 0)",
        "test": test_func,
        "testable": results['valid']
    }
    # every valid program should compile successfully

def every_syntax_error_should_fail(results):
    test_func = lambda res: res['syntax error']
    return {
        "name": "syntax errors don't compile",
        "description": "every invalid program due to a syntax error should exit 100 to indicate a syntax error",
        "test": test_func,
        "testable": results['invalid']['syntax']
    }

def every_semantic_error_should_fail(results):
    test_func = lambda res: res['semantic error']
    return {
        "name": "semantic errors don't compile",
        "description": "every invalid program due to a semantic error should exit 200 to indicate a semantic error",
        "test": test_func,
        "testable": results['invalid']['semantic']
    }

### TEST RUN HELPER ###
def run_test(make_test_def, results):
    test_def = make_test_def(results)

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
            print(colored('> ' + failure['file'], color='blue'))
            print(colored("Exit status: " + str(failure['compiled'].returncode), color="red"))

            # print out any errors in compilation
            if failure['compiler error']:
                print(colored('Compiler Errors:', color='red'))
                print(failure['compiler output'])
                print()

            # print out any syntax/semantic errors
            print(colored('Program Errors:', color='red'))
            if failure['semantic error']:
                print(colored('Semantic Error', color="red"))
            if failure['syntax error']:
                print(colored('Syntax Error', color="red"))
            print(failure['output'])

    return passed, total


# run in bulk
def run_tests(*test_defs, results=results):
    passed, total = 0, 0
    for test_def in test_defs:
        p, t = run_test(test_def, results)
        passed += p
        total += t
    return passed, total

# every valid program should compile and invalid program should not compile
passed, total = run_tests(
    every_valid_program_should_compile,
    every_syntax_error_should_fail,
    every_semantic_error_should_fail
)

coverage = passed/total
print(f'coverage: {coverage}% ({passed}/{total})')