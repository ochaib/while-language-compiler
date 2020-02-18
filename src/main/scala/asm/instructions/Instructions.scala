package asm.instructions

import asm.registers.Register

// could condition be an Option here instead of having an Any condition?
sealed abstract class Instruction(condition: Condition)

case class Push(condition: Condition, registers: List[Register])
    extends Instruction(condition)
case class Pop(condition: Condition, registers: List[Register])
    extends Instruction(condition)

// NOTE: a case class can't inherit a case class
// the workaround is to make them `sealed abstract` so that
// these non-leaf classes can't be pattern matched on
// alternatively, we could turn these into traits
case class LoadDirect private(
    condition: Condition,
    dest: Register,
    src: Register,
    offset: FlexOffset,
    registerWriteBack: Boolean, // the "!"
    loadable: Loadable,
    label: Label
) extends Instruction(condition) {
  // LDR{cond} Rd, [Rn]
  def this(condition: Condition, dest: Register, src: Register) =
    this(condition, dest, src, null, null, null, null)
  // LDR{cond} Rd, [Rn, FlexOffset]{!}
  def this(condition: Condition, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean) =
    this(condition, dest, src, flexOffset, registerWriteBack, null, null)
  // LDR{cond} Rd, label
  def this(condition: Condition, dest: Register, label: Label) =
    this(condition, dest, null, null, null, null, label)
  // LDR{cond} Rd, [Rn], FlexOffset
  def this(condition: Condition, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, dest, src, offset, null, null, null)
  // LDR{cond} register, =[expr | label-expr]
  def this(condition: Condition, dest: Register, loadable: Loadable) =
    this(condition, dest, null, null, null, loadable, null)
}
case class Store(
                  condition: Condition,
                  dest: Register,
                  src: Register,
                  offset: FlexOffset,
                  registerWriteBack: Boolean, // the "!"
                  label: Label
    ) extends Instruction(condition) {
  // STR{cond} Rd, [Rn]
  def this(condition: Condition, dest: Register, src: Register) =
    this(condition, dest, src, null, null, null)
  // STR{cond} Rd, [Rn, FlexOffset]{!}
  def this(condition: Condition, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean) =
    this(condition, dest, src, flexOffset, registerWriteBack, null)
  // STR{cond} Rd, label
  def this(condition: Condition, dest: Register, label: Label) =
    this(condition, dest, null, null, null, label)
  // STR{cond} Rd, [Rn], FlexOffset
  def this(condition: Condition, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, dest, src, offset, null, null)
}

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

case class Move(condition: Condition, dest: Register, src: FlexibleSndOp
               ) extends Instruction(condition)

case class Compare(condition: Condition, operand1: Register, operand2: FlexibleSndOp
                  ) extends Instruction(condition)

case class Branch(condition: Condition, label: Label
                 ) extends Instruction(condition)

case class BranchLabel(condition: Condition, label: Label
                      ) extends Instruction(condition)