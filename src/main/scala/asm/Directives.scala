package asm

sealed abstract class Directive

case object TextSection extends Directive
case object DataSection extends Directive
case class Global(symbol: String) extends Directive
case class Word(length: Int) extends Directive
case class Ascii(string: String) extends Directive
