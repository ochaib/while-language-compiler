package asm

import asm.instructions._
import asm.instructionset._
import asm.registers.RegisterManager
import ast.nodes._


object CodeGenerator {

  var instructionSet: InstructionSet = null
  var RM: RegisterManager = null

  def useInstructionSet(_instructionSet: InstructionSet) = {
    instructionSet = _instructionSet
    RM = new RegisterManager(instructionSet)
  }

  def pushLR: Instruction = new Push(None, List(instructionSet.getLR))
  def popPC: Instruction = new Pop(None, List(instructionSet.getPC))

  def generate(program: ProgramNode): IndexedSeq[Instruction] = {
    // Generated code for functions
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunc)

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = ???

    val mainLabel: Label = new Label("main")

    functionCode ++ IndexedSeq[Instruction](
      new BranchLabel(mainLabel),
      pushLR,
      // TODO: generate stats
      new LoadDirect(_, instructionSet.getReturn, null, false, null, new Immediate(0)),
      popPC,
      new EndBranch
    )
  }

  def generate(func: FuncNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      new LabelBranch(new Label(s"f_${func.identNode.ident}")),
      pushLR,
      // TODO: generate stats
      popPC,
      popPC,
      new EndBranch
    )
  }

}



