package main.scala

//sealed trait Register {def registerID: String; }
//object Register {
//  case object R1 extends Register {val registerID = "R1"}
//  case object R2 extends Register {val registerID = "R2"}
//  case object R3 extends Register {val registerID = "R3"}
//  case object R4 extends Register {val registerID = "R4"}
//  case object R5 extends Register {val registerID = "R5"}
//  case object R6 extends Register {val registerID = "R6"}
//  case object R7 extends Register {val registerID = "R7"}
//  case object R8 extends Register {val registerID = "R8"}
//  case object R9 extends Register {val registerID = "R9"}
//  case object R10 extends Register {val registerID = "R10"}
//  case object R11 extends Register {val registerID = "R11"}
//  case object R12 extends Register {val registerID = "R12"}
//  // Stack pointer (R13)
//  case object SP extends Register {val registerID = "SP"}
//  // Load register (R14)
//  case object LR extends Register {val registerID = "LR"}
//  // Program counter (R15)
//  case object PC extends Register {val registerID = "PC"}
//
//}

object Register extends Enumeration {
  type Register = Value
  val R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12 = Value
  val R13 = Value("sp")
  val R14 = Value("lr")
  val R15 = Value("pc")
}