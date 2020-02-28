package asm.utilities

import asm.instructions._
import asm.registers._

import scala.collection.mutable

object Utilities {

    var RM: RegisterManager = _

    // data label: string literal
    val strings: mutable.Map[Label, StringLiteral] = mutable.Map[Label, StringLiteral]()

    // common functions in use
    val commonFunctions: mutable.Set[CommonFunction] = mutable.Set[CommonFunction]()

    // needs a RM for common funcs
    def useRegisterManager(_RM: RegisterManager): Unit = {
        RM = _RM
    }

    // adds a string literal, returns its data label
    /* e.g.:
        val msgLabel: Label = Utilities.addString("hello world")
    Then msgLabel can be used in Load/etc as Label is Loadable
    */
    /* When adding an escaped value please set length yourself,
    as it will not be calculated correctly otherwise */
    def addString(s: String, length: Option[Int]=None): Label = {
        val len: Int = length match {
            // we don't count the \\ as it is just creating an escape sequence
            // we subtract 2 because the string is double quoted which accounts for 2 chars
            case None => (for (c<-s if c!='\\') yield c).length - 2
            case Some(l) => l
        }
        val dataLabel: Label = Label("msg_" + strings.size)
        strings += (dataLabel -> new StringLiteral(s, len))
        dataLabel
    }

    def printString(s: String): IndexedSeq[Instruction] = {
        val msgLabel: Label = if (s.length > 0) addString(s) else Label(s)
        // Load R4, =msg_#
        // Mov R0, R4
        // BL p_print_string
        val reg: Register = RM.peekVariableRegister()
        if (commonFunctions.add(PrintString))
            strings += (Label("msg_print_string") -> new StringLiteral("\"%.*s\\0\"", 5))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, label=msgLabel),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PrintString.label)
        )
    }

    def printNewline: IndexedSeq[Instruction] = {
        // BL p_print_ln
        if (commonFunctions.add(PrintLn))
            strings += (Label("msg_print_ln") -> new StringLiteral("\"\\0\"", 1))
        IndexedSeq[Instruction](
            BranchLink(condition = None, label = PrintLn.label)
        )
    }

    def printBool(b: Boolean): IndexedSeq[Instruction] = {
        // Mov r4, (#1 if true else #0)
        // Mov R0, R4
        // BL p_print_bool
        val reg: Register = RM.peekVariableRegister()
        if (commonFunctions.add(PrintBool)) {
            strings += (Label("msg_print_bool_true") -> new StringLiteral("\"true\\0\"", 5))
            strings += (Label("msg_print_bool_false") -> new StringLiteral("\"false\\0\"", 6))
        }
        IndexedSeq[Instruction](
            Move(condition = None, dest = reg, src = new Immediate(if (b) 1 else 0)),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PrintBool.label)
        )
    }

    def printChar(c: Char): IndexedSeq[Instruction] = {
        // Mov R4, #'c'
        // Mov R0, R4
        // BL putchar
        val reg: Register = RM.peekVariableRegister()
        IndexedSeq[Instruction](
            Move(condition = None, dest = reg, src = new Immediate(c)),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PutChar.label)
        )
    }

    // TODO: how are we handling array prints?
    // IMO we should stray from ref compiler and just print each elem one by one
    // not represented here as it should just call the correct typed print function

    def printInt(i: Int): IndexedSeq[Instruction] = {
        // Load R4, =i
        // Mov R0, R4
        // BL p_print_int
        val reg: Register = RM.peekVariableRegister()
        if (commonFunctions.add(PrintInt))
            strings += (Label("msg_print_int") -> new StringLiteral("\"%d\\0\"", 3))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, loadable = new LoadableExpression(i)),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PrintInt.label)
        )
    }

    def printFreePair: IndexedSeq[Instruction] = {
        // BL p_free_pair
        printRuntimeError
        if (commonFunctions.add(PrintFreePair))
            strings += (Label("msg_free_pair") ->
                new StringLiteral("\"NullReferenceError: dereference a null reference\\n\\0\"", 50)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintFreePair.label)
        )
    }

    def printReadChar: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintReadChar))
            strings += (Label("msg_read_char") ->
                new StringLiteral("\" %c\\0\"", 4)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintReadChar.label)
        )
    }

    def printReadInt: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintReadInt))
            strings += (Label("msg_read_int") ->
                new StringLiteral("\"%d\\0\"", 3)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintReadInt.label)
        )
    }

    def printReference: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintReference))
            strings += (Label("msg_print_reference") ->
                new StringLiteral("\"%p\\0\"", 3)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintReference.label)
        )
    }

    def printCheckNullPointer: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintCheckNullPointer))
            strings += (Label("msg_check_null_pointer") ->
                new StringLiteral("\"NullReferenceError: dereference a null reference\\n\\0\"", 50)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintCheckNullPointer.label)
        )
    }

    def printOverflowError(condition: Option[Condition]): IndexedSeq[Instruction] = {
        // BLVS p_throw_overflow_error
        if (commonFunctions.add(PrintOverflowError))
            strings += (Label("msg_throw_overflow_error") ->
                new StringLiteral("\"OverflowError: the result is too small/large " +
                                  "to store in a 4-byte signed-integer.\\n\"", 82)
            )
        printRuntimeError // make sure print runtime error is added to common functions
        IndexedSeq[Instruction](
            BranchLink(condition, label = PrintOverflowError.label)
        )
    }

    def printRuntimeError: IndexedSeq[Instruction] = {
        // BL p_throw_runtime_error
        commonFunctions.add(PrintRuntimeError)
        printString("") // just make sure print string is added to common functions
        IndexedSeq[Instruction](
            BranchLink(Some(Overflow), PrintRuntimeError.label)
        )
    }

    def printCheckArrayBounds: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintCheckArrayBounds)) {
            strings += (Label("msg_negative_index") ->
                new StringLiteral("\"ArrayIndexOutOfBoundsError: negative index\\n\\0\"", 44))
            strings += (Label("msg_index_too_large") ->
                new StringLiteral("\"ArrayIndexOutOfBoundsError: index too large\\n\\0\"", 45))
        }
        printRuntimeError
        IndexedSeq[Instruction](
            BranchLink(None, PrintCheckArrayBounds.label)
        )
    }

    def printDivideByZero: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintDivideByZero)) {
            strings += (Label("msg_divide_by_zero") ->
              new StringLiteral("\"DivideByZeroError: divide or modulo by zero\\n\\0\"", 45))
        }
        printRuntimeError
        IndexedSeq[Instruction](
            BranchLink(None, PrintDivideByZero.label)
        )
    }

}