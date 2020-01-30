import org.antlr.v4.runtime.{
  CharStream => ANTLRCharStream,
  CharStreams => ANTLRCharStreams
}

object Compiler extends App {
  if (args.length == 0) {
    println("Error: No filename provided")
    System.exit(1)
  }

  println("Compiling: " + args(0))
}