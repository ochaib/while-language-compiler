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
            addString("%.*s\\0", length=Some(5))
        IndexedSeq[Instruction](
            new Load(condition=None, asmType=None, dest=reg, label=PrintString.label),
            new Move(condition=None, dest=RM.instructionSet.getReturn, src=new ShiftedRegister(reg)),
            new BranchLink(condition=None, label=PrintString.label)
        )
    }

}