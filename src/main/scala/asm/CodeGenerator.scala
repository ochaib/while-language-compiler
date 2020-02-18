package asm

import asm.instructions._
import asm.instructionset.InstructionSet
import asm.registers.RegisterManager
import ast.nodes._


object CodeGenerator {

  def generate(program: ProgramNode, instructionSet: InstructionSet): IndexedSeq[Instruction] = {
    val RM: RegisterManager = new RegisterManager(instructionSet)

    // Final sequence of instructions generated for printing.
    val generatedInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    // Traverse list of funcNodes associated with programNode and generate code for them.
    val functionCode: IndexedSeq[Instruction] = program.functions.flatMap(generateFunc(_, instructionSet, RM))

    val mainLabel: Label = new Label("main")

    generatedInstructions ++ functionCode ++ IndexedSeq[Instruction](
      new BranchLabel(mainLabel),
      Push(_, List(instructionSet.getLR)),
      // TODO: generate stats
      new LoadDirect(_, instructionSet.getReturn, null, false, null, new Immediate(0)),
      Pop(Anything, List(instructionSet.getPC)),
      new EndBranch
    )
  }

  def generateFunc(func: FuncNode, instructionSet: InstructionSet, RM: RegisterManager) = {
    IndexedSeq[Instruction](
      new LabelBranch(new Label(s"f_${func.identNode.ident}")),
      new Push(Anything, List(instructionSet.getLR)),
      // TODO: generate stats
      new Pop(Anything, List(instructionSet.getPC)),
      new Pop(Anything, List(instructionSet.getPC)),
      new EndBranch
    )
  }

}



