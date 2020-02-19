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
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generate)

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = ???

    val mainLabel: Label = new Label("main")

    functions ++ IndexedSeq[Instruction](
      new NewBranch(mainLabel),
      pushLR,
      // TODO: generate stats
      new Load(None, new SignedByte, instructionSet.getReturn, new Immediate(0)),
      popPC,
      new EndBranch
    )
  }

  def generate(func: FuncNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      new NewBranch(new Label(s"f_${func.identNode.ident}")),
      pushLR,
      // TODO: generate stats
      popPC,
      popPC,
      new EndBranch
    )
  }

}



