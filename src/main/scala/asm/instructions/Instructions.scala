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
                               byteType: Boolean,
                               _type: Option[WordType],
                               dest: Register,
                               src: Option[Register],
                               offset: Option[FlexOffset],
                               registerWriteBack: Boolean, // the "!"
                               loadable: Option[Loadable],
                               label: Option[Label]
) extends Instruction(condition) {
  assert(!(byteType && _type.isDefined), "Can't simultaneously be a byteType and another type")
  // LDR{cond}{B|Type} Rd, [Rn]
  def this(condition: Condition, byteType: Boolean = false, _type: WordType, dest: Register, src: Register) =
    this(condition, byteType, Some(_type), dest, Some(src), None, registerWriteBack = false, None, None)
  // LDR{cond}{B|Type} Rd, [Rn, FlexOffset]{!}
  def this(condition: Condition, byteType: Boolean = false, _type: WordType, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean = false) =
    this(condition, byteType, Some(_type), dest, Some(src), Some(flexOffset), registerWriteBack, None, None)
  // LDR{cond}{B|Type} Rd, label
  def this(condition: Condition, byteType: Boolean = false, _type: WordType, dest: Register, label: Label) =
    this(condition, byteType, Some(_type), dest, None, None, registerWriteBack = false, None, Some(label))
  // LDR{cond}{B|Type} Rd, [Rn], FlexOffset
  def this(condition: Condition, byteType: Boolean = false, _type: WordType, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, byteType, Some(_type), dest, Some(src), Some(offset), registerWriteBack = false, None, None)
  // LDR{cond}{B|Type} register, =[expr | label-expr]
  def this(condition: Condition, byteType: Boolean = false, _type: WordType, dest: Register, loadable: Loadable) =
    this(condition, byteType, Some(_type), dest, None, None, registerWriteBack = false, Some(loadable), null)
}

case class Store private(
                          condition: Condition,
                          byteType: Boolean,
                          dest: Register,
                          src: Option[Register],
                          offset: Option[FlexOffset],
                          registerWriteBack: Boolean, // the "!"
                          label: Option[Label]
                        ) extends Instruction(condition) {
  // STR{cond}{B} Rd, [Rn]
  def this(condition: Condition, byteType: Boolean = false, dest: Register, src: Register) =
    this(condition, byteType, dest, Some(src), None, registerWriteBack = false, None)
  // STR{cond}{B} Rd, [Rn, FlexOffset]{!}
  def this(condition: Condition, byteType: Boolean = false, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean) =
    this(condition, byteType, dest, Some(src), Some(flexOffset), registerWriteBack, None)
  // STR{cond}{B} Rd, label
  def this(condition: Condition, byteType: Boolean = false, dest: Register, label: Label) =
    this(condition, byteType, dest, None, None, registerWriteBack = false, Some(label))
  // STR{cond}{B} Rd, [Rn], FlexOffset
  def this(condition: Condition, byteType: Boolean = false, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, byteType, dest, Some(src), Some(offset), registerWriteBack = false, None)
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