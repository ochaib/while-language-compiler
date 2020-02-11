import json
import os
import re

# get the ASM output from reference compiler if we don't already have it
if not os.path.exists('referenceASM.txt'):
    print("[*] Forming testcases using the reference compiler...")
    os.system('./refCompile -d testcases/valid -a > referenceASM.txt')

with open('referenceASM.txt') as f:
    refASM = f.read()

# strip the first line which is just the directory it was run on
refASM = refASM[refASM.find('\n')+1:]
results = [_ for _ in refASM.split('calling the reference compiler on ') if _]

# filename is first line
get_filename = lambda result: result[:result.find('\n')]
# pieces separated by ====, the assembly is the second piece
get_assembled = lambda result: result.split('===========================================================')[1]

# asm starts with line num and an indent we need to remove that
RE_LINE_OFFSET = re.compile('^[0-9]\t?')
clean_assembled = lambda assembled: '\n'.join([
    re.sub(RE_LINE_OFFSET, '', line)
    for line in assembled.strip().split('\n')
])

programs = {
    get_filename(result): clean_assembled(get_assembled(result))
    for result in results
}

with open('asm-tests.json', 'w') as f:
    json.dump(programs, f)