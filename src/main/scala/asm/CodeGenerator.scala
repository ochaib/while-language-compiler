package asm

import asm.instructions._
import asm.instructionset._
import asm.registers.RegisterManager
import ast.nodes._

object CodeGenerator {

  var instructionSet: InstructionSet = _
  var RM: RegisterManager = _

  def useInstructionSet(_instructionSet: InstructionSet): Unit = {
    instructionSet = _instructionSet
    RM = new RegisterManager(instructionSet)
  }

  // Common instructions
  def pushLR: Instruction = Push(None, List(instructionSet.getLR))
  def popPC: Instruction = Pop(None, List(instructionSet.getPC))
  def zeroReturn: Instruction = new Load(
    condition = None,
    asmType = new SignedByte,
    dest = instructionSet.getReturn,
    loadable = new Immediate(0),
    label = None
  )

  def generate(program: ProgramNode): IndexedSeq[Instruction] = {
    // Generated code for functions
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generate)

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    functions ++ IndexedSeq[Instruction](
      NewBranch(new Label("main")),
      pushLR,
      // TODO: generate stats
      zeroReturn,
      popPC,
      new EndBranch
    )
  }

  def generate(func: FuncNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      NewBranch(new Label(s"f_${func.identNode.ident}")),
      pushLR,
      // TODO: generate stats
      popPC,
      new EndBranch
    )
  }

}



