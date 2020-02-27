package asm.utilities

import asm.instructions.Label

class StringLiteral(string: String, length: Int)

sealed abstract class CommonFunction { val label: Label }

// print funcs
case object PrintString extends CommonFunction { val label: Label = new Label("p_print_string") }
case object PrintLn extends CommonFunction { val label: Label = new Label("p_print_ln") }
case object PrintBool extends CommonFunction { val label: Label = new Label("p_print_bool") }
case object PrintInt extends CommonFunction { val label: Label = new Label("p_print_int") }
case object PrintFreePair extends CommonFunction { val label: Label = new Label("p_free_pair") }

// error funcs
case object PrintOverflowError extends CommonFunction { val label: Label = new Label("p_throw_overflow_error") }
case object PrintRuntimeError extends CommonFunction { val label: Label = new Label("p_throw_runtime_error") }
case object PrintCheckArrayBounds extends CommonFunction { val label: Label = new Label("p_check_array_bounds") }

// NOTE: the functions below DO NOT need to be generated
// they come with the assembler (at least in ARM 11)
// TODO: replace existing uses of these with the object
abstract class StandardFunction { val label: Label }
object PutChar { val label: Label = new Label("putchar") }
object Puts { val label: Label = new Label("puts") }
object Flush { val label: Label = new Label("fflush") }
object Printf { val label: Label = new Label("printf") }
object Malloc { val label: Label = new Label("malloc") }
object Exit { val label: Label = new Label("exit") }
object Scanf { val label: Label = new Label("scanf") }
object Free { val label: Label = new Label("free") }