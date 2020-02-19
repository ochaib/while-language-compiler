package asm.instructions

import asm.AssemblyLine
import asm.registers.Register

trait FlexOffset
trait FlexibleSndOp
trait Loadable

class Immediate(val immediate: Int) extends Loadable with FlexibleSndOp with FlexOffset
class Label(val label: String) extends Loadable with AssemblyLine
class ShiftedRegister(val register: Register /* Unimplemented ARM fields */) extends FlexibleSndOp

sealed abstract class ASMType
class ByteType extends ASMType
sealed abstract class WordType extends ASMType
class SignedByte extends WordType