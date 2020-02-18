package asm

import asm.instructions._
import asm.instructionset.InstructionSet
import asm.registers.RegisterManager
import ast.nodes._

object CodeGenerator {
    def generate(program: ASTNode, instructionSet: InstructionSet): IndexedSeq[Instruction] = {
        // TODO
        IndexedSeq[Instruction]()
    }
}