package util
import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}

class ErrorListener extends BaseErrorListener {

  override def syntaxError(recognizer: Recognizer[_, _], offendingSymbol: AnyRef, line: Int,
                           charPositionInLine: Int, msg: String, e: RecognitionException) {

    println("Syntax Error on Line " + line + ":" + charPositionInLine + " " + msg)
    // ErrorLog.add
  }

}
