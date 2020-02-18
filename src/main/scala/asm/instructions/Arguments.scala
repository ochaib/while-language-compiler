package asm.instructions

import asm.AssemblyLine
import asm.registers.Register

class Immediate(val immediate: Int) extends Loadable with FlexibleSndOp with FlexOffset
class Label(val label: String) extends Loadable with AssemblyLine
class ShiftedRegister(val register: Register /* Unimplemented ARM fields */) extends FlexibleSndOp

sealed class WordType
class SignedByte extends WordType
// There are other word types but I dont think they are used in WACC
