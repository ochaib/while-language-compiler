package asm.utilities

import asm.instructions.Label

class StringLiteral(string: String, length: Int)

sealed abstract class CommonFunction { val label: Label }
case object PrintString extends CommonFunction { val label: Label = new Label("p_print_string") }
case object PrintLn extends CommonFunction { val label: Label = new Label("p_print_ln") }