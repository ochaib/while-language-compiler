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
            case None => s.length
            case Some(l) => l
        }
        val dataLabel: Label = Label("msg_" + strings.size)
        strings += (dataLabel -> new StringLiteral(s, len))
        dataLabel
    }

    def printString(s: String): IndexedSeq[Instruction] = {
        val msgLabel: Label = addString(s)
        // Load R4, =msg_#
        // Mov R0, R4
        // BL p_print_string
        val reg: Register = RM.peekVariableRegister()
        if (commonFunctions.add(PrintString))
            strings += (Label("msg_print_string") -> new StringLiteral("%.*s\\0", 5))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, label=PrintString.label),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PrintString.label)
        )
    }

    def printNewline: IndexedSeq[Instruction] = {
        // BL p_print_ln
        if (commonFunctions.add(PrintLn))
            strings += (Label("msg_print_ln") -> new StringLiteral("\\0", 1))
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
            strings += (Label("msg_print_bool_true") -> new StringLiteral("true\\0", 5))
            strings += (Label("msg_print_bool_false") -> new StringLiteral("false\\0", 6))
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
            strings += (Label("msg_print_int") -> new StringLiteral("%d\\0", 3))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, loadable = new LoadableExpression(i)),
            Move(condition = None, dest = RM.instructionSet.getReturn, src = new ShiftedRegister(reg)),
            BranchLink(condition = None, label = PrintInt.label)
        )
    }

    def printFreePair: IndexedSeq[Instruction] = {
        // BL p_free_pair
        if (commonFunctions.add(PrintFreePair))
            strings += (Label("msg_free_pair") ->
                new StringLiteral("NullReferenceError: dereference a null reference\\n\\0", 50)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintFreePair.label)
        )
    }

    def printReadChar: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintReadChar))
            strings += (Label("msg_read_char") ->
                new StringLiteral(" %c\\0", 4)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintReadChar.label)
        )
    }

    def printCheckNullPointer: IndexedSeq[Instruction] = {
        if (commonFunctions.add(PrintCheckNullPointer))
            strings += (Label("msg_check_null_pointer") ->
                new StringLiteral("NullReferenceError: dereference a null reference\\n\\0", 50)
            )
        IndexedSeq[Instruction](
            BranchLink(condition=None, label=PrintCheckNullPointer.label)
        )
    }

    def printOverflowError: IndexedSeq[Instruction] = {
        // BLVS p_throw_overflow_error
        if (commonFunctions.add(PrintOverflowError))
            strings += (new Label("msg_throw_overflow_error") ->
                new StringLiteral("OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n", 82)
            )
        printRuntimeError // make sure print runtime error is added to common functions
        IndexedSeq[Instruction](
            BranchLink(Some(Overflow), label = PrintOverflowError.label)
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
            strings += (new Label("msg_negative_index") ->
                new StringLiteral("ArrayIndexOutOfBoundsError: negative index\\n\\0", 44))
            strings += (new Label("msg_index_too_large") ->
                new StringLiteral("ArrayIndexOutOfBoundsError: index too large\\n\\0", 45))
        }
        printRuntimeError
        IndexedSeq[Instruction](
            BranchLink(None, PrintCheckArrayBounds.label)
        )
    }


    def generateCommonFunction(func: CommonFunction): IndexedSeq[Instruction] = func match {
        case PrintLn => IndexedSeq[Instruction](
            pushLR,
            Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn, src1=instructionSet.getReturn, src2=new Immediate(4)),
            BranchLink(condition=None, label=Puts.label),
            Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
            BranchLink(condition=None, label=Flush.label),
            popPC
        )
        case PrintRuntimeError => IndexedSeq[Instruction](
            BranchLink(condition=None, PrintString.label),
            Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(-1)),
            BranchLink(None, Exit.label)
        )
        case PrintString => IndexedSeq[Instruction](
            pushLR,
            new Load(condition=None, asmType=None, dest=instructionSet.getArgumentRegisters(1), src=instructionSet.getReturn),
            Add(condition=None, conditionFlag=false, dest=instructionSet.getArgumentRegisters(2), src1=instructionSet.getReturn, src2=new Immediate(4)),
            BranchLink(condition=None, label=Printf.label),
            Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
            BranchLink(condition=None, label=Flush.label),
            popPC
        )
        case PrintFreePair => IndexedSeq[Instruction](
            pushLR,
            Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
            new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_free_pair")),
            Branch(condition=Some(Equal), label=PrintRuntimeError.label),
            Push(condition=None, List(instructionSet.getReturn)),
            new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn),
            BranchLink(condition=None, label=Free.label),
            new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getSP),
            new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn, flexOffset=new Immediate(4)),
            BranchLink(condition=None, label=Free.label),
            Pop(condition=None, List(instructionSet.getReturn)),
            BranchLink(None, label=Free.label),
            popPC
        )
        case PrintReadChar => IndexedSeq[Instruction](
            pushLR,
            Move(condition=None, dest=instructionSet.getArgumentRegisters(1), src=new ShiftedRegister(instructionSet.getReturn)),
            new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_read_char")),
            new Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn, src1=instructionSet.getReturn, src2=new Immediate(4)),
            BranchLink(condition=None, label=Scanf.label),
            popPC
        )
        case PrintCheckNullPointer => IndexedSeq[Instruction](
            pushLR,
            Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
            new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_check_null_pointer")),
            BranchLink(condition=Some(Equal), PrintRuntimeError.label),
            popPC
        )
    }

}