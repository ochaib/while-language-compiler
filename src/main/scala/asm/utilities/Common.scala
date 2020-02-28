package asm.utilities

import asm.instructions.Label

class StringLiteral(string: String, length: Int)

sealed abstract class CommonFunction { val label: Label }

// print funcs
case object PrintString extends CommonFunction { val label: Label = new Label("p_print_string") }
case object PrintLn extends CommonFunction { val label: Label = new Label("p_print_ln") }
case object PrintBool extends CommonFunction { val label: Label = new Label("p_print_bool") }
case object PrintInt extends CommonFunction { val label: Label = new Label("p_print_int") }
// TODO: printreference needs a utility function and a generation
case object PrintReference extends CommonFunction { val label: Label = new Label("p_print_reference") }

// util funcs
case object PrintFreePair extends CommonFunction { val label: Label = new Label("p_free_pair") }
case object PrintReadChar extends CommonFunction { val label: Label = new Label("p_read_char") }
case object PrintCheckNullPointer extends CommonFunction { val label: Label = new Label("p_check_null_pointer") }

// error funcs
case object PrintOverflowError extends CommonFunction { val label: Label = Label("p_throw_overflow_error") }
case object PrintRuntimeError extends CommonFunction { val label: Label = Label("p_throw_runtime_error") }
case object PrintCheckArrayBounds extends CommonFunction { val label: Label = Label("p_check_array_bounds") }
case object PrintDivideByZero extends CommonFunction { val label: Label = Label("p_check_divide_by_zero")}

// NOTE: the functions below DO NOT need to be generated
// they come with the assembler (at least in ARM 11)
// TODO: replace existing uses of these with the object
abstract class StandardFunction { val label: Label }
object PutChar { val label: Label = Label("putchar") }
object Puts { val label: Label = Label("puts") }
object Flush { val label: Label = Label("fflush") }
object Printf { val label: Label = Label("printf") }
object Malloc { val label: Label = Label("malloc") }
object Exit { val label: Label = Label("exit") }
object Scanf { val label: Label = Label("scanf") }
object Free { val label: Label = Label("free") }
object DivMod { val label: Label = Label("__aeabi_idivmod")}