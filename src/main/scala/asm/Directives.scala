package asm

trait AssemblyLine

sealed abstract class Directive extends AssemblyLine

case object TextSection extends Directive
case object DataSection extends Directive
case class Global(symbol: String) extends Directive
case class Word(length: Int) extends Directive
case class Ascii(string: String) extends Directive
