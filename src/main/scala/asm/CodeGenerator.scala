package asm

import asm.instructions._
import asm.instructionset._
import asm.registers.RegisterManager
import ast.nodes._
/*
object CodeGenerator {
    def generate(program: ASTNode, instructionSet: InstructionSet): IndexedSeq[Instruction] = {
        val RM: RegisterManager = new RegisterManager(instructionSet)

        // TODO: generate functions
        val mainLabel: Label = new Label("main")
        IndexedSeq[Instruction](
            new LabelBranch(mainLabel),
            new Push(Anything, List(instructionSet.getLR)),
            // TODO: generate stats
            new LoadDirect(Anything, instructionSet.getReturn, null, false, null, new Immediate(0)),
            new Pop(Anything, List(instructionSet.getPC)),
            new EndBranch,
        )
    }
}*/