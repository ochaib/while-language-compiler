import os
import re
from subprocess import run, PIPE
from termcolor import colored
from time import time

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

    valid = check_validity(compiled)
    syntax_error = check_syntax_error(compiled)
    semantic_error = check_semantic_error(compiled)

    return {
        'file': prog,
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
    failures = (res for res in results['valid'] if not res['valid'])
    return {
        "name": "compiles valid programs",
        "description": "every valid program should compile successfully (i.e. with exit status 0)",
        "failures": failures,
        "total": results['valid']
    }
    # every valid program should compile successfully


def run_test(test_func, results=results, valid=True):
    result = test_func(results)

    name = result['name']
    description = result['description']
    print(colored(f'{"-"*9} {name} {"-"*9}', color="magenta"))
    print(colored(f'Description: {description}', color="magenta"))

    # when we list(generator) it will eval, so we time this as test time
    t = time()
    total = len(list(result['total']))
    failures = list(result['failures'])
    t = time() - t
    print(colored(f'Took {t}ms', color="yellow"))

    # if the test is meant to fail then a failure = a success
    passed = (total - len(failures)) if valid else len(failures)
    print(f'Passed {passed}/{total}')

    # print out any failures
    if passed < total:
        error('{"-"*6} TEST FAILURES {"-"*6}')
        for failure in failures:
            print(colored('> ' + failure['file'], color='blue'))

            # print out any errors in compilation
            if failure['compiler error']:
                print(colored('Compiler Errors:', color='red'))
                print(failure['compiler output'])
                print()

            # print out any syntax/semantic errors
            print('Program Errors:')
            print(failure['output'])

run_test(every_valid_program_should_compile)