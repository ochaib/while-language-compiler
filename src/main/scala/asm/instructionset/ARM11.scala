package asm.instructionset

import asm.instructions._
import asm.instructions.ByteType
import asm.registers.Register
import asm.utilities._

import scala.collection.mutable.{ListBuffer, Map}

sealed trait ARM11Register extends Register
case object R0 extends ARM11Register { val registerID = "r0" }
case object R1 extends ARM11Register { val registerID = "r1" }
case object R2 extends ARM11Register { val registerID = "r2" }
case object R3 extends ARM11Register { val registerID = "r3" }
case object R4 extends ARM11Register { val registerID = "r4" }
case object R5 extends ARM11Register { val registerID = "r5" }
case object R6 extends ARM11Register { val registerID = "r6" }
case object R7 extends ARM11Register { val registerID = "r7" }
case object R8 extends ARM11Register { val registerID = "r8" }
case object R9 extends ARM11Register { val registerID = "r9" }
case object R10 extends ARM11Register { val registerID = "r10" }
case object R11 extends ARM11Register { val registerID = "r11" }
case object R12 extends ARM11Register { val registerID = "r12" }
// Stack pointer (R13)
case object SP extends ARM11Register { val registerID = "sp" }
// Link register (R14)
case object LR extends ARM11Register { val registerID = "lr" }
// Program counter (R15)
case object PC extends ARM11Register { val registerID = "pc" }

// TODO: implement
object ARM11 extends InstructionSet {

  // Particular register set for the instruction.
  val registers: ListBuffer[Register] = ListBuffer[Register](
    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8,
    R9,
    R10,
    R11,
    R12,
    SP,
    LR,
    PC
  )

  // Return registers that are saved by the function e.g. r4-r11, r13
  // SP is the stack pointer and needs to be saved as well
  def getCalleeSaved: ListBuffer[Register] = SP +: getVariableRegisters

  // Return registers that are saved by the thing that calls the function
  // R12 is the IPC scratch register and needs to be saved as well
  def getCallerSaved: ListBuffer[Register] = R12 +: getArgumentRegisters

  // Return stack pointer.
  def getSP: Register = SP
  // Return program counter.
  def getPC: Register = PC
  // Return the link register.
  def getLR: Register = LR
  // Return the return register, r0.
  def getReturn: Register = R0
  // Return the argument registers, r0-r3.
  def getArgumentRegisters: ListBuffer[Register] = ListBuffer[Register](
    R0,
    R1,
    R2,
    R3
  )
  // Return the variable registers
  def getVariableRegisters: ListBuffer[Register] = ListBuffer[Register](
    R4,
    R5,
    R6,
    R7,
    R8,
    R9,
    R10,
    R11
  )

  // Print the instructions to a string with the instruction set's syntax
  def print(instructions: IndexedSeq[Instruction]): String = {

    ".data\n\n" +
      Utilities.strings.map[String](Function.tupled(printStringLiteral _)).mkString("\n") +
      "\n\n.text\n\n" +
      ".global main\n" +
      instructions.map(print).mkString("\n")
  }

  def printStringLiteral(msgLabel: Label, stringLiteral: StringLiteral): String = {
    // TODO: escape escape sequences?
    s"${msgLabel.label}:\n" + s"\t.word ${stringLiteral.length}\n" + s"\t.ascii ${stringLiteral.string}"
  }

  def print(instruction: Instruction): String = instruction match {
    // ARM 11 syntax as per ref manual:

    // OP{COND} *ARGS
    case Push(condition, registers) =>
      s"\tPUSH${printCondition(condition)} {" + registers
        .map(_.registerID)
        .mkString(", ") + "}"

    case Pop(condition, registers) =>
      s"\tPOP${printCondition(condition)} {" + registers
        .map(_.registerID)
        .mkString(", ") + "}"

    // LDR{cond}{B|Type} Rd, [Rn]
    case Load(
        condition,
        asmType,
        dest,
        Some(src),
        None,
        None,
        None,
        None
        ) =>
      s"\tLDR${printCondition(condition)}${printType(asmType)} ${print(dest)}, [${print(src)}]"

    // LDR{cond}{B|Type} Rd, [Rn, FlexOffset]{!}
    case Load(
        condition,
        asmType,
        dest,
        Some(src),
        Some(flexOffset),
        Some(registerWriteBack),
        None,
        None
        ) =>
      s"\tLDR${printCondition(condition)}${printType(asmType)}" +
        s" ${print(dest)}, [${print(src)}, ${print(flexOffset)}]${if (registerWriteBack) "!"
        else ""}"

    // LDR{cond}{B|Type} Rd, label
    case Load(
        condition,
        asmType,
        dest,
        None,
        None,
        None,
        None,
        Some(label)
        ) =>
      s"\tLDR${printCondition(condition)}${printType(asmType)} ${print(dest)}, =${label.label}"
      // FIXME: hi there, its priansh, i changed this to = from print(label), lmk if this breaks anything

    // LDR{cond}{B|Type} Rd, [Rn], FlexOffset
    case Load(
        condition,
    asmType: Option[ASMType],
        dest,
        Some(src),
        Some(flexOffset),
        None,
        None,
        None
        ) =>
      s"\tLDR${printCondition(condition)}${printType(asmType)} ${print(dest)}, [${print(src)}], ${print(flexOffset)}"

    // LDR{cond}{B|Type} register, =[expr | label-expr]
    case Load(
        condition,
        asmType,
        dest,
        None,
        None,
        None,
        Some(loadable),
        None
        ) =>
      s"\tLDR${printCondition(condition)}${printType(asmType)} ${print(dest)}, " +
        s"=${
          loadable match {
            case Label(label) => label
            case expression: LoadableExpression => expression.num.toString
            case _ => assert(assertion = false, "Loadable not accounted for")
          }}"

    // Invalid LDR case
    case Load(_, _, _, _, _, _, _, _) =>
      assert(
        assertion = false,
        "print for this LoadDirect configuration is undefined"
      )
      ""

    // STR{cond}{B} Rd, [Rn]
    case Store(condition, byteType: Option[ASMType], dest, Some(src), None, None, None) =>
      s"\tSTR${printCondition(condition)}${printType(byteType)} ${print(dest)}, [${print(src)}]"

    // STR{cond}{B} Rd, [Rn, FlexOffset]{!}
    case Store(
        condition,
        byteType: Option[ASMType],
        dest,
        Some(src),
        Some(flexOffset),
        Some(registerWriteBack),
        None
        ) =>
      s"\tSTR${printCondition(condition)}${printType(byteType)}" +
        s" ${print(dest)}, [${print(src)}, ${print(flexOffset)}]" +
        s"${if (registerWriteBack) "!" else ""}"

    // STR{cond}{B} Rd, label
    case Store(condition, byteType: Option[ASMType], dest, None, None, None, Some(label)) =>
      s"\tSTR${printCondition(condition)}${printType(byteType)}" +
        s" ${print(dest)}, ${print(label)}"

    // STR{cond}{B} Rd, [Rn], FlexOffset
    case Store(
        condition,
        byteType: Option[ASMType],
        dest,
        Some(src),
        Some(flexOffset),
        None,
        None
        ) =>
        s"\tSTR${printCondition(condition)}${printType(byteType)} ${print(dest)}, [${print(src)}], ${print(flexOffset)}"

    // Invalid STR case
    case Store(_, _, _, _, _, _, _) =>
      assert(
        assertion = false,
        "print for this Store configuration is undefined"
      )
      ""

    // op{cond}{S} Rd, Rn, Operand2
    case process: DataProcess =>
      process match {
        case Add(condition, conditionFlag, dest, src1, src2) =>
          s"\tADD${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
        case Subtract(condition, conditionFlag, dest, src1, src2) =>
          s"\tSUB${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
        case And(condition, conditionFlag, dest, src1, src2) =>
          s"\tAND${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
        case Or(condition, conditionFlag, dest, src1, src2) =>
          s"\tORR${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
        case ExclusiveOr(condition, conditionFlag, dest, src1, src2) =>
          s"\tEOR${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
        case RSBS(condition, conditionFlag, dest, src1, src2) =>
          s"\tRSBS${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
            s" ${print(dest)}, ${print(src1)}, ${print(src2)}"
      }

    // SMULL{S}{cond} RdLo, RdHi, Rn, Rm
    case SMull(condition, conditionFlag, dest1, dest2, src1, src2) =>
      s"\tSMULL${printCondition(condition)}${conditionFlagToString(conditionFlag)}" +
        s" ${print(dest1)}, ${print(dest2)}, ${print(src1)}, ${print(src2)}"

    // MOV{cond}{S} Rd, Operand2
    case Move(condition, dest, src) =>
      s"\tMOV${printCondition(condition)} ${print(dest)}, ${print(src)}"

    // CMP{cond} Rn, Operand2
    case Compare(condition, operand1, operand2) =>
      s"\tCMP${printCondition(condition)} ${print(operand1)}, ${print(operand2)}"

    case Label(s) => s + ":"

    // B{cond} label
    case Branch(condition, label) => s"\tB${printCondition(condition)} ${label.label}"
    case BranchLink(condition, label) =>
      s"\tBL${printCondition(condition)} ${label.label}"

    case EndFunction() => s"\t.ltorg"

  }

  def print(immediate: Immediate): String = s"#${immediate.toString}"
  
  def print(op: FlexibleSndOp): String = op match {
    case immediate: Immediate        => print(immediate)
    // Temp fix for multiply.
    case shiftedRegister: ShiftedRegister =>
      if (shiftedRegister.shift.isDefined)
        print(shiftedRegister.register) + ", " + printString(shiftedRegister.shiftType) +
                                          " #" + print(shiftedRegister.shift)
      else
        print(shiftedRegister.register)
    case _ =>
      assert(assertion = false, "print for FlexibleSndOp type is undefined")
      ""
  }

  def print(int: Option[Int]): String = int match {
    case Some(int) => int.toString
    case _ => ""
  }

  def printString(string: Option[String]): String = string match {
    case Some(string) => string.toString
    case _ => ""
  }

  def conditionFlagToString(conditionFlag: Boolean): String =
    if (conditionFlag) "S" else ""

  def printType(asmType: Option[ASMType]): String = asmType match {
    case Some(SignedByte) => "SB"
    case Some(ByteType) => "B"
    case None    => ""
  }

  def print(label: Label): String = ":" + label.label

  def print(flexOffset: FlexOffset): String = flexOffset match {
    case immediate: Immediate        => print(immediate)
    case _ =>
      assert(assertion = false, "print for FlexOffset type is undefined")
      ""
  }
  def print(register: Register): String = register match {
    case register: ARM11Register => register.registerID
    case _ =>
      assert(assertion = false, "print for register type is undefined")
      ""
  }

  def printCondition(condition: Option[Condition]): String =
    if (condition.isDefined) {
      condition.get match {
        case Equal        => "EQ"
        case NotEqual     => "NE"
        case HigherSame   => "CS"
        case Lower        => "CC"
        case Negative     => "MI"
        case NonNegative  => "PL"
        case Overflow     => "VS"
        case NoOverflow   => "VC"
        case Higher       => "HI"
        case LowerSame    => "LS"
        case GreaterEqual => "GE"
        case LessThan     => "LT"
        case GreaterThan  => "GT"
        case LessEqual    => "LE"
        case Anything     => "AL"
      }
    } else ""

}
