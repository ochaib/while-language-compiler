package asm.utilities

import asm.instructions.Label

class StringLiteral(string: String, length: Int)

sealed abstract class CommonFunction { val label: Label }
case object PrintString extends CommonFunction { val label: Label = new Label("p_print_string") }
case object PrintLn extends CommonFunction { val label: Label = new Label("p_print_ln") }
case object PrintBool extends CommonFunction { val label: Label = new Label("p_print_bool") }


// NOTE: the functions below DO NOT need to be generated
// they come with the assembler (at least in ARM 11)
abstract class StandardFunction { val label: Label }
object PutChar { val label: Label = new Label("putchar") }
object Puts { val label: Label = new Label("puts") }
object Flush { val label: Label = new Label("fflush") }
object Printf { val label: Label = new Label("printf") }
object Malloc { val label: Label = new Label("malloc") }