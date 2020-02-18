package asm

import asm.instructions._
import asm.instructionset._
import asm.registers.RegisterManager
import ast.nodes._

object CodeGenerator {
  def generate(
      program: ProgramNode,
      instructionSet: InstructionSet
  ): IndexedSeq[Instruction] = {
    val RM: RegisterManager = new RegisterManager(instructionSet)

    val functions: IndexedSeq[Instruction] =
      program.functions.flatMap(generateFunc(_, instructionSet, RM))
    val mainLabel: Label = new Label("main")
    functions ++ IndexedSeq[Instruction](
      new LabelBranch(mainLabel),
      new Push(Anything, List(instructionSet.getLR)),
      // TODO: generate stats
      new LoadDirect(
        Anything,
        instructionSet.getReturn,
        null,
        false,
        null,
        new Immediate(0)
      ),
      new Pop(Anything, List(instructionSet.getPC)),
      new EndBranch
    )
  }

  def generateFunc(
      func: FuncNode,
      instructionSet: InstructionSet,
      RM: RegisterManager
  ) =
    IndexedSeq[Instruction](
      new LabelBranch(new Label(s"f_${func.identNode.ident}")),
      new Push(Anything, List(instructionSet.getLR)),
      // TODO: generate stats
      new Pop(Anything, List(instructionSet.getPC)),
      new Pop(Anything, List(instructionSet.getPC)),
      new EndBranch
    )

}
