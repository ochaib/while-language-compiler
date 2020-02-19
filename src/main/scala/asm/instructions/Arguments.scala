package asm.instructions

import asm.registers.Register

trait FlexOffset
trait FlexibleSndOp
trait Loadable

class Immediate(val immediate: Int) extends Loadable with FlexibleSndOp with FlexOffset
class ShiftedRegister(val register: Register /* Unimplemented ARM fields */) extends FlexibleSndOp

sealed abstract class ASMType
class ByteType extends ASMType
sealed abstract class WordType extends ASMType
class SignedByte extends WordType