package asm.instructions

import asm.registers.Register

sealed abstract class Instructions(condition: Condition)

case class Push(condition: Condition, registers: List[Register]) extends Instructions(condition)
case class Pop(condition: Condition, registers: List[Register]) extends Instructions(condition)

sealed abstract class MemAccess(condition: Condition, dest: Register, src: Register, includeOffset: Boolean, offset: FlexOffset,
                     loadable: Loadable) extends Instructions(condition)
case class LoadDirect(condition: Condition, dest: Register, src: Register, includeOffset: Boolean,
                      offset: FlexOffset, loadable: Loadable)
  extends MemAccess(condition, dest, src, includeOffset, offset, loadable)
case class Store(condition: Condition, dest: Register, src: Register, includeOffset: Boolean,
                      offset: FlexOffset, loadable: Loadable)
  extends MemAccess(condition, dest, src, includeOffset, offset, loadable)


// Data Process instructions include ADD, SUB, ORR, EOR
case abstract class DataProcess(condition: Condition, conditionFlag: Boolean, dest: Register,
                                src1: Register, src2: FlexibleSndOp)
case class Add(condition: Condition, conditionFlag: Boolean, dest: Register,
               src1: Register, src2: FlexibleSndOp)
  extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Subtract(condition: Condition, conditionFlag: Boolean, dest: Register,
               src1: Register, src2: FlexibleSndOp)
  extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class And(condition: Condition, conditionFlag: Boolean, dest: Register,
               src1: Register, src2: FlexibleSndOp)
  extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Or(condition: Condition, conditionFlag: Boolean, dest: Register,
               src1: Register, src2: FlexibleSndOp)
  extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class ExclusiveOr(condition: Condition, conditionFlag: Boolean, dest: Register,
               src1: Register, src2: FlexibleSndOp)
  extends DataProcess(condition, conditionFlag, dest, src1, src2)


case class Move(condition: Condition, dest: Register, src: FlexibleSndOp)
