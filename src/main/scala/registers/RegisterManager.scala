package registers

import instructions._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class RegisterManager(instruction: Instruction) {

  // Particular instruction set that RegisterManager conforms to.
  val instructionSet: Instruction = instruction
  // List of all registers that are available.
  var availableRegisters: ListBuffer[Register] = new ListBuffer[Register]
  // Memory stack, tracks available registers in each scope.
  // Can replace with list/listbuffer because scala complains about deprecation.
  val memoryStack: mutable.Stack[ListBuffer[Register]] = new mutable.Stack[ListBuffer[Register]]

  def initARMRegisters(): Unit = {
    availableRegisters = Registers.availableARMRegisters
  }

  def next(): Register = {
    availableRegisters.remove(0)
  }

  def free(register: Register): Unit = {
    availableRegisters += register
  }

  // Upon entering a new scope (function call) we must save the previous register state.
  def save(): Unit = {
    memoryStack.push(availableRegisters)
  }

  // Upon exiting a scope (function returns/exits) we must restore the outer register state.
  def restore(): Unit = {
    availableRegisters = memoryStack.pop()
  }



}
