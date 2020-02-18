package asm.instructions

sealed abstract class Condition

case object Equal extends Condition
case object NotEqual extends Condition
case object HigherSame extends Condition
case object Lower extends Condition
case object Negative extends Condition
case object NonNegative extends Condition
case object Overflow extends Condition
case object NoOverflow extends Condition
case object Higher extends Condition
case object LowerSame extends Condition
case object GreaterEqual extends Condition
case object LessThan extends Condition
case object GreaterThan extends Condition
case object LessEqual extends Condition
case object Anything extends Condition
