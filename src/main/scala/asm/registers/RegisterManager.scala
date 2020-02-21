package asm.registers

import asm.instructionset.InstructionSet

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait Register {def registerID: String; }
class RegisterManager(_instructionSet: InstructionSet) {

  // Particular instruction set that RegisterManager conforms to.
  val instructionSet: InstructionSet = _instructionSet
  // List of all registers that are available to use.
  var availableRegisters: ListBuffer[Register] =
    instructionSet.getArgumentRegisters ++ instructionSet.getVariableRegisters
  // Memory stack, tracks available registers in each scope.
  // Can replace with list/listbuffer because scala complains about deprecation.
  val memoryStack: mutable.Stack[ListBuffer[Register]] =
    new mutable.Stack[ListBuffer[Register]]

  def next(): Register = {
    availableRegisters.remove(0)
  }

  def nextArgumentRegister(): Register = {
    instructionSet.getArgumentRegisters.remove(0)
  }

  def nextVariableRegister(): Register = {
    instructionSet.getVariableRegisters.remove(0)
  }

  def free(register: Register): Unit = {
    availableRegisters += register
  }

  // Upon entering a new scope (function call) we must save the previous register state.
  def save(): ListBuffer[Register] = {
    memoryStack.push(availableRegisters)
    // Need to pass new instructions to the scope once the previous register state is saved.
    instructionSet.registers
  }

  // Upon exiting a scope (function returns/exits) we must restore the outer register state.
  def restore(): Unit = {
    availableRegisters = memoryStack.pop()
  }

}
