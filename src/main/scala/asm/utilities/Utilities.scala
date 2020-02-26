package asm.utilities

import asm.instructions._
import asm.registers._
import scala.collection.mutable.{Map, Set}

object Utilities {

    var RM: RegisterManager = _

    // data label: string literal
    val strings: Map[Label, StringLiteral] = Map[Label, StringLiteral]()

    // common functions in use
    val commonFunctions: Set[CommonFunction] = Set[CommonFunction]()

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
        val dataLabel: Label = new Label("msg_" + strings.size)
        strings += (dataLabel -> new StringLiteral(s, len))
        dataLabel
    }

    def printString(s: String): IndexedSeq[Instruction] = {
        val msgLabel: Label = addString(s)
        // Load R4, =msg_#
        // Mov R0, R4
        // BL p_print_string
        val reg: Register = RM.peekVariableRegister
        if (commonFunctions.add(PrintString))
            strings += (new Label("msg_print_string") -> new StringLiteral("%.*s\\0", 5))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, label=PrintString.label),
            new Move(condition=None, dest=RM.instructionSet.getReturn, src=new ShiftedRegister(reg)),
            new BranchLink(condition=None, label=PrintString.label)
        )
    }

    def printNewline: IndexedSeq[Instruction] = {
        // BL p_print_ln
        if (commonFunctions.add(PrintLn))
            strings += (new Label("msg_print_ln") -> new StringLiteral("\\0", 1))
        IndexedSeq[Instruction](
            new BranchLink(condition=None, label=PrintLn.label)
        )
    }

    def printBool(b: Boolean): IndexedSeq[Instruction] = {
        // Mov r4, (#1 if true else #0)
        // Mov R0, R4
        // BL p_print_bool
        val reg: Register = RM.peekVariableRegister
        if (commonFunctions.add(PrintBool)) {
            strings += (new Label("msg_print_bool_true") -> new StringLiteral("true\\0", 5))
            strings += (new Label("msg_print_bool_false") -> new StringLiteral("false\\0", 6))
        }
        IndexedSeq[Instruction](
            new Move(condition=None, dest=reg, src=new Immediate(if (b) 1 else 0)),
            new Move(condition=None, dest=RM.instructionSet.getReturn, src=new ShiftedRegister(reg)),
            new BranchLink(condition=None, label=PrintBool.label)
        )
    }

    def printChar(c: Char): IndexedSeq[Instruction] = {
        // Mov R4, #'c'
        // Mov R0, R4
        // BL putchar
        val reg: Register = RM.peekVariableRegister
        IndexedSeq[Instruction](
            new Move(condition=None, dest=reg, src=new Immediate(c)),
            new Move(condition=None, dest=RM.instructionSet.getReturn, src=new ShiftedRegister(reg)),
            new BranchLink(condition=None, label=PutChar.label)
        )
    }

    // TODO: how are we handling array prints?
    // IMO we should stray from ref compiler and just print each elem one by one
    // not represented here as it should just call the correct typed print function

    def printInt(i: Int): IndexedSeq[Instruction] = {
        // Load R4, =i
        // Mov R0, R4
        // BL p_print_int
        val reg: Register = RM.peekVariableRegister
        if (commonFunctions.add(PrintInt))
            strings += (new Label("msg_print_int") -> new StringLiteral("%d\\0", 3))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, loadable=new Immediate(i)),
            new Move(condition=None, dest=RM.instructionSet.getReturn, src=new ShiftedRegister(reg)),
            new BranchLink(condition=None, label=PrintInt.label)
        )
    }

}