package main.scala

class Register(var registerID: Register.RegisterID) {
  def getRegID: Register.RegisterID = registerID

  override def toString: String = this match {
    case _   => toLowerCase()
    case R13 => "sp"
    case R14 => "lr"
    case R15 => "pc"

    //      registerID.toString
  }

  object RegisterID extends Enumeration {
    type RegisterID = Value
    val R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15 = Value
  }

}
