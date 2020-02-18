package asm.generator

import asm.instructions.Instruction
import ast.nodes.ASTNode

abstract class Generator {
    def generate(program: ASTNode): IndexedSeq[Instruction]
}