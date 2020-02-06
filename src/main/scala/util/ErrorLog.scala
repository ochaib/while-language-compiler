package util

import scala.collection.mutable

abstract class ErrorLog {

  val errorType: String
  var errorCheck: Boolean = false
  val errorLog: mutable.Queue[String] = new mutable.Queue[String]()

  def add(string: String): Unit = {
    errorLog += string
    errorCheck = true
  }

  def printAllErrors(): Unit = {
    while (errorLog.nonEmpty) {
      println(errorType + errorLog.dequeue)
    }
  }
}

object SyntaxErrorLog extends ErrorLog {
  override val errorType = "Syntax Error:"
}

object SemanticErrorLog extends ErrorLog {
  override val errorType = "Semantic Error:"
}