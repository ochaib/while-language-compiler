package asm.registers

import asm.instructionset.InstructionSet

import scala.collection.mutable

trait Register {def registerID: String; }
class RegisterManager(_instructionSet: InstructionSet) {

  // Particular instruction set that RegisterManager conforms to.
  val instructionSet: InstructionSet = _instructionSet

  // Test.
  var argumentRegisters: mutable.ListBuffer[Register] = instructionSet.getArgumentRegisters
  var variableRegisters: mutable.ListBuffer[Register] = instructionSet.getVariableRegisters

  // List of all registers that are available to use.
  var availableRegisters: mutable.ListBuffer[Register] = argumentRegisters ++ variableRegisters

  // Memory stack, tracks available registers in each scope.
  // Can replace with list/listbuffer because scala complains about deprecation.
  val memoryStack = new mutable.Stack[mutable.ListBuffer[Register]]

  def next(): Register = {
    availableRegisters.remove(0)
  }

  def peek: Register = {
    availableRegisters.head
  }

  def nextArgumentRegister(): Register = {
    argumentRegisters.remove(0)
  }

  def peekArgumentRegister: Register = {
    argumentRegisters.head
  }

  def nextVariableRegister(): Register = {
    if (variableRegisters.isEmpty){
      peekArgumentRegister
    } else {
      variableRegisters.remove(0)
    }
  }

  def peekVariableRegister: Register = {
    if (variableRegisters.isEmpty){
      peekArgumentRegister
    } else {
      variableRegisters.head
    }
  }

  // Free's are not adding registers back in initial order.
  def free(register: Register): Unit = {
    availableRegisters.prepend(register)
  }

  def freeArgumentRegister(register: Register): Unit = {
    argumentRegisters.prepend(register)
  }

  def freeVariableRegister(register: Register): Unit = {
    variableRegisters.prepend(register)
  }

  // Upon entering a new scope (function call) we must save the previous register state.
  def save(): mutable.ListBuffer[Register] = {
    memoryStack.push(availableRegisters)
    // Need to pass new instructions to the scope once the previous register state is saved.
    instructionSet.registers
  }

  // Upon exiting a scope (function returns/exits) we must restore the outer register state.
  def restore(): Unit = {
    availableRegisters = memoryStack.pop()
  }

}
