import org.antlr.v4.runtime._

object Compiler extends App {
  println("WACC Compiler!")
  for(arg<-args) {
      println(arg);
  }
}