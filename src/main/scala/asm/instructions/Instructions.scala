package asm.instructions

import asm.AssemblyLine
import asm.registers.Register

// could condition be an Option here instead of having an Any condition?
sealed abstract class Instruction(condition: Option[Condition])
    extends AssemblyLine

// Push and Pop
case class Push(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)
case class Pop(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)

// Load and Store
case class Load private (
    condition: Option[Condition],
    asmType: Option[ASMType],
    dest: Register,
    src: Option[Register],
    offset: Option[FlexOffset],
    registerWriteBack: Option[Boolean], // the "!"
    loadable: Option[Loadable],
    label: Option[Label]
) extends Instruction(condition) {

  // LDR{cond}{B|Type} Rd, [Rn]
  def this(
      condition: Option[Condition],
      asmType: ASMType,
      dest: Register,
      src: Register
  ) =
    this(
      condition,
      Some(asmType),
      dest,
      Some(src),
      None,
      None,
      None,
      None
    )
  // LDR{cond}{B|Type} Rd, [Rn, FlexOffset]{!}
  def this(
      condition: Option[Condition],
      asmType: ASMType,
      dest: Register,
      src: Register,
      flexOffset: FlexOffset,
      registerWriteBack: Boolean
  ) =
    this(
      condition,
      Some(asmType),
      dest,
      Some(src),
      Some(flexOffset),
      Some(registerWriteBack),
      None,
      None
    )
  // LDR{cond}{B|Type} Rd, label
  def this(
      condition: Option[Condition],
      asmType: WordType,
      dest: Register,
      label: Label
  ) =
    this(
      condition,
      Some(asmType),
      dest,
      None,
      None,
      None,
      None,
      Some(label)
    )
  // LDR{cond}{B|Type} Rd, [Rn], FlexOffset
  def this(
      condition: Option[Condition],
      asmType: ASMType,
      dest: Register,
      src: Register,
      flexOffset: FlexOffset
  ) =
    this(
      condition,
      Some(asmType),
      dest,
      Some(src),
      Some(flexOffset),
      None,
      None,
      None
    )
  // LDR{cond}{B|Type} register, =[expr | label-expr]
  def this(
      condition: Option[Condition],
      asmType: ASMType,
      dest: Register,
      loadable: Loadable,
      label: Option[Label]
  ) =
    this(
      condition,
      Some(asmType),
      dest,
      None,
      None,
      None,
      Some(loadable),
      label
    )
}

case class Store private (
    condition: Option[Condition],
    asmType: Option[ASMType],
    dest: Register,
    src: Option[Register],
    offset: Option[FlexOffset],
    registerWriteBack: Boolean, // the "!"
    label: Option[Label]
) extends Instruction(condition) {
  assert(
    label.isDefined || src.isDefined,
    "Either a label or source must be defined"
  )
  // STR{cond}{B} Rd, [Rn]
  def this(
      condition: Option[Condition],
      asmType: Option[ASMType],
      dest: Register,
      src: Register
  ) =
    this(
      condition,
      asmType,
      dest,
      Some(src),
      None,
      registerWriteBack = false,
      None
    )
  // STR{cond}{B} Rd, [Rn, FlexOffset]{!}
  def this(
      condition: Option[Condition],
      asmType: Option[ASMType],
      dest: Register,
      src: Register,
      flexOffset: FlexOffset,
      registerWriteBack: Boolean
  ) =
    this(
      condition,
      asmType,
      dest,
      Some(src),
      Some(flexOffset),
      registerWriteBack,
      None
    )
  // STR{cond}{B} Rd, label
  def this(
      condition: Option[Condition],
      asmType: Option[ASMType],
      dest: Register,
      label: Label
  ) =
    this(
      condition,
      asmType,
      dest,
      None,
      None,
      registerWriteBack = false,
      Some(label)
    )
  // STR{cond}{B} Rd, [Rn], FlexOffset
  def this(
      condition: Option[Condition],
      asmType: Option[ASMType],
      dest: Register,
      src: Register,
      flexOffset: FlexOffset
  ) =
    this(
      condition,
      asmType,
      dest,
      Some(src),
      Some(flexOffset),
      registerWriteBack = false,
      None
    )
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

case class Move(
    condition: Option[Condition],
    dest: Register,
    src: FlexibleSndOp
) extends Instruction(condition)

case class Compare(
    condition: Option[Condition],
    operand1: Register,
    operand2: FlexibleSndOp
) extends Instruction(condition)

case class Branch(condition: Option[Condition], label: Label)
    extends Instruction(condition)

case class BranchLabel(condition: Option[Condition], label: Label)
    extends Instruction(condition)

case class NewBranch(label: Label) extends Instruction(None)
case class EndBranch() extends Instruction(None)
