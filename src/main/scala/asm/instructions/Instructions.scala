package asm.instructions

import asm.registers.Register

sealed abstract class Instruction(condition: Option[Condition])

// Push and Pop
case class Push(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)
case class Pop(condition: Option[Condition], registers: List[Register])
    extends Instruction(condition)

// Load
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
      None,
      None,
      None
    )
  // LDR{cond}{B|Type} Rd, [Rn, FlexOffset]{!}
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
      Instructions.checkFlexOffset(flexOffset),
      {
        // if the flexOffset is 0 this constructor creates the same result as the constructor for
        // LDR{cond}{B|Type} Rd, [Rn]
        if (Instructions.checkFlexOffset(flexOffset).isDefined)
          Some(registerWriteBack)
        else
          None
      },
      None,
      None
    )
  // LDR{cond}{B|Type} Rd, label
  def this(
      condition: Option[Condition],
      // Was wordtype.
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
      None,
      None,
      Some(label)
    )
  // LDR{cond}{B|Type} Rd, [Rn], FlexOffset
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
      Instructions.checkFlexOffset(flexOffset),
      None,
      None,
      None
    )
  // LDR{cond}{B|Type} register, =[expr | label-expr]
  def this(
      condition: Option[Condition],
      asmType: Option[ASMType],
      dest: Register,
      loadable: Loadable
  ) =
    this(
      condition,
      asmType,
      dest,
      None,
      None,
      None,
      Some(loadable),
      None
    )
}

// Store
case class Store private (
    condition: Option[Condition],
    byteType: Option[ASMType],
    dest: Register,
    src: Option[Register],
    offset: Option[FlexOffset],
    registerWriteBack: Option[Boolean], // the "!"
    label: Option[Label]
) extends Instruction(condition) {
  assert(
    label.isDefined || src.isDefined,
    "Either a label or source must be defined"
  )
  assert(!(byteType.isDefined && byteType.get == SignedByte), "Cannot store signed bytes")
  // STR{cond}{B} Rd, [Rn]
  def this(
      condition: Option[Condition],
      byteType: Option[ASMType],
      dest: Register,
      src: Register
  ) =
    this(
      condition,
      byteType,
      dest,
      Some(src),
      None,
      None,
      None
    )
  // STR{cond}{B} Rd, [Rn, FlexOffset]{!}
  def this(
      condition: Option[Condition],
      byteType: Option[ASMType],
      dest: Register,
      src: Register,
      flexOffset: FlexOffset,
      registerWriteBack: Boolean
  ) = {
    this(
      condition,
      byteType,
      dest,
      Some(src),
      Instructions.checkFlexOffset(flexOffset),
      {
        // if the flexOffset is 0 this constructor creates the same result as the constructor for
        // STR{cond}{B|Type} Rd, [Rn]
        if (Instructions.checkFlexOffset(flexOffset).isDefined)
          Some(registerWriteBack)
        else
          None
      },
      None
    )
  }



  // STR{cond}{B} Rd, label
  def this(
      condition: Option[Condition],
      byteType: Option[ASMType],
      dest: Register,
      label: Label
  ) =
    this(
      condition,
      byteType,
      dest,
      None,
      None,
      None,
      Some(label)
    )
  // STR{cond}{B} Rd, [Rn], FlexOffset
  def this(
      condition: Option[Condition],
      byteType: Option[ASMType],
      dest: Register,
      src: Register,
      flexOffset: FlexOffset
  ) =
    this(
      condition,
      byteType,
      dest,
      Some(src),
      Instructions.checkFlexOffset(flexOffset),
      None,
      None
    )
}

object Instructions {
  def checkFlexOffset(flexOffset: FlexOffset): Option[FlexOffset] = {
    if (flexOffset.isInstanceOf[Immediate] && flexOffset.asInstanceOf[Immediate].immediate_int.get == 0) None
    else Some(flexOffset)
  }
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

// Reverse Subtract, RSB{S}{cond} {Rd}, Rn, Operand2
case class RSBS(
    condition: Option[Condition],
    conditionFlag: Boolean,
    dest: Register,
    src1: Register,
    src2: FlexibleSndOp
) extends DataProcess(condition, conditionFlag, dest, src1, src2)

case class SMull(
     condition: Option[Condition],
     conditionFlag: Boolean,
     dest1: Register,
     dest2: Register,
     src1: Register,
     src2: Register
) extends Instruction(condition)

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

case class Label(label: String) extends Instruction(None) with Loadable

case class Branch(condition: Option[Condition], label: Label)
    extends Instruction(condition)

case class BranchLink(condition: Option[Condition], label: Label)
    extends Instruction(condition)

case class EndFunction() extends Instruction(None)
