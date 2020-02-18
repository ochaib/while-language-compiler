package asm.instructions

import asm.registers.Register

// could condition be an Option here instead of having an Any condition?
sealed abstract class Instruction(condition: Condition)

case class Push(condition: Condition, registers: List[Register])
    extends Instruction(condition)
case class Pop(condition: Condition, registers: List[Register])
    extends Instruction(condition)

case class LabelBranch(label: Label) extends Instruction(Anything)
case class Branch(condition: Condition, label: Label)
    extends Instruction(condition)
case class EndBranch() extends Instruction(Anything)

// NOTE: a case class can't inherit a case class
// the workaround is to make them `sealed abstract` so that
// these non-leaf classes can't be pattern matched on
// alternatively, we could turn these into traits
// TODO: we should have default arguments everywhere so we
// don't have to define every single argument
sealed abstract class MemAccess(
    condition: Condition,
    dest: Register,
    src: Register,
    includeOffset: Boolean,
    offset: FlexOffset,
    loadable: Loadable
) extends Instruction(condition)
case class LoadDirect(
    condition: Condition,
    dest: Register,
    src: Register,
    includeOffset: Boolean,
    offset: FlexOffset,
    loadable: Loadable
) extends MemAccess(condition, dest, src, includeOffset, offset, loadable)
case class Store(
    condition: Condition,
    dest: Register,
    src: Register,
    includeOffset: Boolean,
    offset: FlexOffset,
    loadable: Loadable
) extends MemAccess(condition, dest, src, includeOffset, offset, loadable)

// Data Process Instructions include ADD, SUB, ORR, EOR
sealed abstract class DataProcess(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
)
case class Add(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Subtract(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class And(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Or(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class ExclusiveOr(
    condition: Condition,
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Move(condition: Condition, dest: Register, src: FlexibleSndOp)

case class Custom(str: String) extends Instruction(Anything)