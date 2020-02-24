import json
import os
import re
from tqdm import tqdm

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
# .partition doesn't cut it here as 'finished' comes after this boundary
get_assembled = lambda result: result.split('===========================================================')[1]

# asm starts with line num and an indent we need to remove that
RE_LINE_OFFSET = re.compile('^[0-9]+\t?')
clean_assembled = lambda assembled: '\n'.join([
    re.sub(RE_LINE_OFFSET, '', line)
    for line in assembled.strip().split('\n')
])

def assemble(result):
    asm = clean_assembled(get_assembled(result))
    asm_fn = get_filename(result)[:-len('.wacc')] + '.s'
    exe_fn = asm_fn[:-len('.s')]
    with open(asm_fn, 'w') as f:
        f.write(asm)
    os.system(f'arm-linux-gnueabi-gcc -o {exe_fn} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s {asm_fn}')
    return exe_fn

for result in tqdm(results):
    assemble(result)
