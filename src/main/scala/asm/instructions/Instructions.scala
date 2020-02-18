package asm.instructions

import asm.AssemblyLine
import asm.registers.Register

// could condition be an Option here instead of having an Any condition?
sealed abstract class Instruction(condition: Option[Condition]) extends AssemblyLine

case class Push(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)
case class Pop(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)

// NOTE: a case class can't inherit a case class
// the workaround is to make them `sealed abstract` so that
// these non-leaf classes can't be pattern matched on
// alternatively, we could turn these into traits
case class LoadDirect private(
                               condition: Option[Condition],
                               byteType: Boolean,
                               _type: Option[WordType],
                               dest: Register,
                               src: Option[Register],
                               offset: Option[FlexOffset],
                               registerWriteBack: Option[Boolean], // the "!"
                               loadable: Option[Loadable],
                               label: Option[Label]
) extends Instruction(condition) {
  assert(!(byteType && _type.isDefined), "Can't simultaneously be a byteType and another type")
  assert(label.isDefined || src.isDefined, "Either a label or source must be defined")
  // LDR{cond}{B|Type} Rd, [Rn]
  def this(condition: Option[Condition], byteType: Boolean = false, _type: WordType, dest: Register, src: Register) =
    this(condition, byteType, Some(_type), dest, Some(src), None, None, None, None)
  // LDR{cond}{B|Type} Rd, [Rn, FlexOffset]{!}
  def this(condition: Option[Condition], byteType: Boolean = false, _type: WordType, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean = false) =
    this(condition, byteType, Some(_type), dest, Some(src), Some(flexOffset), Some(registerWriteBack), None, None)
  // LDR{cond}{B|Type} Rd, label
  def this(condition: Option[Condition], byteType: Boolean = false, _type: WordType, dest: Register, label: Label) =
    this(condition, byteType, Some(_type), dest, None, None, None, None, Some(label))
  // LDR{cond}{B|Type} Rd, [Rn], FlexOffset
  def this(condition: Option[Condition], byteType: Boolean = false, _type: WordType, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, byteType, Some(_type), dest, Some(src), Some(offset), None, None, None)
  // LDR{cond}{B|Type} register, =[expr | label-expr]
  def this(condition: Option[Condition], byteType: Boolean = false, _type: WordType, dest: Register, loadable: Loadable) =
    this(condition, byteType, Some(_type), dest, None, None, None, Some(loadable), null)
}

case class Store private(
                          condition: Option[Condition],
                          byteType: Boolean,
                          dest: Register,
                          src: Option[Register],
                          offset: Option[FlexOffset],
                          registerWriteBack: Boolean, // the "!"
                          label: Option[Label]
                        ) extends Instruction(condition) {
  assert(label.isDefined || src.isDefined, "Either a label or source must be defined")
  // STR{cond}{B} Rd, [Rn]
  def this(condition: Option[Condition], byteType: Boolean = false, dest: Register, src: Register) =
    this(condition, byteType, dest, Some(src), None, registerWriteBack = false, None)
  // STR{cond}{B} Rd, [Rn, FlexOffset]{!}
  def this(condition: Option[Condition], byteType: Boolean = false, dest: Register, src: Register, flexOffset: FlexOffset, registerWriteBack: Boolean) =
    this(condition, byteType, dest, Some(src), Some(flexOffset), registerWriteBack, None)
  // STR{cond}{B} Rd, label
  def this(condition: Option[Condition], byteType: Boolean = false, dest: Register, label: Label) =
    this(condition, byteType, dest, None, None, registerWriteBack = false, Some(label))
  // STR{cond}{B} Rd, [Rn], FlexOffset
  def this(condition: Option[Condition], byteType: Boolean = false, dest: Register, src: Register, offset: FlexOffset) =
    this(condition, byteType, dest, Some(src), Some(offset), registerWriteBack = false, None)
}

// Data Process Instructions include ADD, SUB, ORR, EOR
sealed abstract class DataProcess(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends Instruction(condition)
case class Add(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Subtract(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class And(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Or(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class ExclusiveOr(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class Move(condition: Option[Condition], dest: Register, src: FlexibleSndOp
               ) extends Instruction(condition)

case class Compare(condition: Option[Condition], operand1: Register, operand2: FlexibleSndOp
                  ) extends Instruction(condition)

case class Branch(condition: Option[Condition], label: Label
                 ) extends Instruction(condition)

case class BranchLabel(condition: Option[Condition], label: Label
                      ) extends Instruction(condition)