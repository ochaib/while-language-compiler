package asm.instructions

import asm.registers.Register

trait FlexOffset
trait FlexibleSndOp
trait Loadable

class Immediate private(val immediate_int: Option[Int], val immediate_char: Option[Char]) extends FlexibleSndOp with FlexOffset {
  def this(immediate_int: Int) = this(Some(immediate_int), None)
  def this(immediate_char: Char) = this(None, Some(immediate_char))
  override def toString: String = {
    assert(immediate_int.isDefined || immediate_char.isDefined, "Immediate must have a value")
    if (immediate_char.isDefined) return s"'${immediate_char.get.toString}'"
    if (immediate_int.isDefined) return immediate_int.get.toString
    throw new NoSuchFieldError
  }
}

class ShiftedRegister(val register: Register /* Unimplemented ARM fields */) extends FlexibleSndOp

class LoadableExpression(val num: Int) extends Loadable

sealed abstract class ASMType
object ByteType extends ASMType
sealed abstract class WordType extends ASMType
object SignedByte extends WordType