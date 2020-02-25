package asm

import asm.instructions.{Immediate}
import scala.collection.mutable.Map

object Utilities {

    // data label: string literal
    val strings: Map[String, String] = Map[String, String]()

    // adds a string literal, returns its data label
    /* e.g.:
        val msgLabel: Immediate = Utilities.addString("hello world")
    Then msgLabel can be used in Load/etc as immediate
    */
    // TODO: this should have a signature Immediate[String]
    def addString(s: String): Immediate = {
        val dataLabel: String = "msg_" + strings.size
        strings += (dataLabel -> s)
        new Immediate(dataLabel)
    }

}