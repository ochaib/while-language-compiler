package util

import scala.collection.mutable

abstract class ErrorLog {

  val errorType: String
  var errorCheck: Boolean = false
  val errorLog: mutable.Stack[String] = new mutable.Stack[String]()

  def add(string: String): Unit = {
    errorLog.push(string)
    errorCheck = true
  }

  def printAllErrors(): Unit = {
    while (errorLog.nonEmpty) {
      println(errorType + errorLog.pop())
    }
  }
}

object SyntaxErrorLog extends ErrorLog {
  override val errorType = "Syntactic Error:"
}

object SemanticErrorLog extends ErrorLog {
  override val errorType = "Semantic Error:"
}
