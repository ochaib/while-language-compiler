package asm

import asm.instructions.{Label}
import scala.collection.mutable.Map

object Utilities {

    // data label: string literal
    val strings: Map[Label, String] = Map[Label, String]()

    // adds a string literal, returns its data label
    /* e.g.:
        val msgLabel: Label = Utilities.addString("hello world")
    Then msgLabel can be used in Load/etc as Label is Loadable
    */
    def addString(s: String): Label = {
        val dataLabel: Label = new Label("msg_" + strings.size)
        strings += (dataLabel -> s)
        dataLabel
    }

}