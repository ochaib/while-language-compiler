package asm.instructionset

import asm.instructions._
import asm.registers.Register
import scala.collection.mutable.ListBuffer

sealed trait ARM11Register extends Register
case object R0 extends ARM11Register { val registerID = "R0" }
case object R1 extends ARM11Register { val registerID = "R1" }
case object R2 extends ARM11Register { val registerID = "R2" }
case object R3 extends ARM11Register { val registerID = "R3" }
case object R4 extends ARM11Register { val registerID = "R4" }
case object R5 extends ARM11Register { val registerID = "R5" }
case object R6 extends ARM11Register { val registerID = "R6" }
case object R7 extends ARM11Register { val registerID = "R7" }
case object R8 extends ARM11Register { val registerID = "R8" }
case object R9 extends ARM11Register { val registerID = "R9" }
case object R10 extends ARM11Register { val registerID = "R10" }
case object R11 extends ARM11Register { val registerID = "R11" }
case object R12 extends ARM11Register { val registerID = "R12" }
// Stack pointer (R13)
case object SP extends ARM11Register { val registerID = "SP" }
// Link register (R14)
case object LR extends ARM11Register { val registerID = "LR" }
// Program counter (R15)
case object PC extends ARM11Register { val registerID = "PC" }

// TODO: implement
object ARM11 extends InstructionSet {

  // Particular register set for the instruction.
  val registers: ListBuffer[Register] = ListBuffer[Register](
    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8,
    R9,
    R10,
    R11,
    R12,
    SP,
    LR,
    PC
  )

  // Return registers that are saved by the function e.g. r4-r11, r13
  // SP is the stack pointer and needs to be saved as well
  def getCalleeSaved: ListBuffer[Register] = SP +: getVariableRegisters

  // Return registers that are saved by the thing that calls the function
  // R12 is the IPC scratch register and needs to be saved as well
  def getCallerSaved: ListBuffer[Register] = R12 +: getArgumentRegisters

  // Return stack pointer.
  def getSP: Register = SP
  // Return program counter.
  def getPC: Register = PC
  // Return the link register.
  def getLR: Register = LR
  // Return the return registers, r0 or r1.
  def getReturn: Register = R0 // by convention
  // Return the argument registers, ro-r3.
  def getArgumentRegisters: ListBuffer[Register] = ListBuffer[Register](
    R0,
    R1,
    R2,
    R3
  )
  // Return the variable registers
  def getVariableRegisters: ListBuffer[Register] = ListBuffer[Register](
    R4,
    R5,
    R6,
    R7,
    R8,
    R9,
    R10,
    R11
  )

  // Print the instructions to a string with the instruction set's syntax
  def print(instructions: IndexedSeq[Instruction]): String =
    ".text\n\n" +
      ".global main\n" +
      instructions.map(print).mkString("\n")

  def print(instruction: Instruction): String = instruction match {
    // ARM 11 syntax as per ref manual:
    // OP{COND} *ARGS

    case Push(condition, registers) =>
      s"\tPUSH${print(condition)} {" + registers.map(_.registerID).mkString(", ") + "}"
    case Pop(condition, registers) =>
      s"\tPOP${print(condition)} {" + registers.map(_.registerID).mkString(", ") + "}"

    case LabelBranch(label) => label.label + ":"
    case Branch(condition, label) =>
      s"\tBL${print(condition)} " + label.label
    case EndBranch() => s"\t.ltorg"

    // TODO: registers need to be loadable or else this is going to be really messy
    case LoadDirect(condition, dest, src, includeOffset, offset, loadable) =>
      s"\tLDR${print(condition)} ???"

    case _ => "<undefined instruction>"

  }

  def print(condition: Condition): String = condition match {
    // TODO: implement
    case Anything => ""
    case _        => " <condition>"
  }

}
