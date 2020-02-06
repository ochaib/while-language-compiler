package util

import scala.collection.mutable

abstract class ErrorLog {

  val errorType: String
  val errorLog: mutable.Stack[String] = new mutable.Stack[String]()

  def add(string: String): Unit = {
    errorLog.push(string)
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
