package registers

import scala.collection.mutable.ListBuffer

trait Instruction {

  // Particular register set for the instruction.
  val registerObject: Register

  // Return registers that are saved by the function e.g. r4-r11, r13
  def getCalleeSaved: ListBuffer[Register]
  // Return registers that are saved by the thing that calls the function e.g. r0-r3, r12
  def getCallerSaved: ListBuffer[Register]

  def instructionPrinter: String
  // Return stack pointer.
  def getSP: Register
  // Return program counter.
  def getPC: Register
  // Return the link register.
  def getLR: Register
  // Return the return registers, r0 or r1.
  def getReturn: Register
  // Return the argument registers, ro-r3.
  def getArgumentRegisters: ListBuffer[Register]
  // Return the variable? registers, possibly not necessary.
  def getVariableRegisters: ListBuffer[Register]

}
