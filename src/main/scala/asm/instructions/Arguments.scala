package asm.instructions

import asm.registers.Register

class Immediate(val immediate: Int) extends Loadable with FlexibleSndOp with FlexOffset
// FIXME: branches shouldn't be loadable right?
class Label(val label: String) extends Loadable
class ShiftedRegister(val register: Register /* Unimplemented ARM fields */) extends FlexibleSndOp
