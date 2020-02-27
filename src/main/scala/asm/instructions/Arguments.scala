package asm.instructions

import asm.registers.Register

trait FlexOffset
trait FlexibleSndOp
trait Loadable

// TODO: this should take an abstract type parameter T instead of hard-coding every type
class Immediate private(
  val immediate_int: Option[Int],
  val immediate_char: Option[Char],
  val immediate_str: Option[String]
) extends FlexibleSndOp with FlexOffset {
  def this(immediate_int: Int) = this(Some(immediate_int), None, None)
  def this(immediate_char: Char) = this(None, Some(immediate_char), None)
  def this(immediate_str: String) = this(None, None, Some(immediate_str))
  override def toString: String = {
    assert(immediate_int.isDefined || immediate_char.isDefined || immediate_str.isDefined, "Immediate must have a value")
    if (immediate_char.isDefined) return s"'${immediate_char.get.toString}'"
    if (immediate_int.isDefined) return immediate_int.get.toString
    if (immediate_str.isDefined) return immediate_str.get
    throw new NoSuchFieldError
  }
}

class ShiftedRegister(val register: Register,  val shift: Option[Int]) extends FlexibleSndOp {
  def this(register: Register) = this(register, None)
  def this(register: Register, shift: Int) = this(register, Some(shift))
}

class LoadableExpression(val num: Int) extends Loadable

sealed abstract class ASMType
object ByteType extends ASMType
sealed abstract class WordType extends ASMType
object SignedByte extends WordType