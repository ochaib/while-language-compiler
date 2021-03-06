# That's so WACC

[![pipeline status](https://gitlab.doc.ic.ac.uk/lab1920_spring/wacc_11/badges/master/pipeline.svg)](https://gitlab.doc.ic.ac.uk/lab1920_spring/wacc_11/commits/master)

[![coverage report](https://gitlab.doc.ic.ac.uk/lab1920_spring/wacc_11/badges/master/coverage.svg)](https://gitlab.doc.ic.ac.uk/lab1920_spring/wacc_11/commits/master)

WACC compiler written in Scala.

----------------------------
Running
----------------------------

Call `make` to compile everything. Then you can run interactively with sbt shell (enter using `sbt`):

```
[hikari-pc]/wacc_11 $ sbt
sbt:wacc-compiler> run <filename>
```

Example:
```
[hikari-pc]/wacc_11 $ sbt
sbt:wacc-compiler> run testcases/valid/basic/exit/exitBasic.wacc
```

----------------------------
Provided files/directories
----------------------------

> antlr_config <

The antlr_config directory contains the WACC ANTLR lexer and parser
specification files WACCLexer.g4 and WACCParser.g4, along with a script
antlrBuild that builds the corresponding Java class files using the
ANTLR libraries (more details below).

> lib <

The lib directory contains the ANTLR library files in antlr-4.7-complete.jar.
You should not need to make any changes in this directory.

> src <

The Scala project for the compiler.

> grun <

The grun script allows you to run the ANTLR TestRig program that can assist you
in debugging you lexer and parser.

> Makefile <

A Makefile that contains rules to compile ANTLR as well as the SBT source.

----------------------------
Using the provided scripts
----------------------------

> antlrBuild <

This script takes a pair of ANTLR lexer and parser configuration files (set
within the script) and creates the corresponding Java classes that you can use
in your compiler. The Java files are written to the src/antlr directory and
should not be modified by hand. By default this script is set up to generate a
parse tree and a listerner pattern for traversing this tree, but you can modify
the compilation options within the script if you want to produce different
outputs

Important! - running the antlrBuild script will overwrite the antlr directory in
             the src directory. We heavily suggest you do not write any of your
             code within the src/antlr directory

> grun <

This script provides access to the ANTLR TestRig program. You will probably find
this helpful for testing your lexer and parser. The script is just a wrapper for
the TestRig in the project environment. You need to tell it what grammar to use
what rule to start parsing with and what kind of output you want.

For example:
  ./grun antlr.WACC program testcases/valid/basic/exit/exitBasic.wacc
will run the TestRig using the 'WACC' grammar, on the program in the file
provided and outputting the tokens seen by the lexer. To see how the parser
groups these tokens you can use the -tree or -gui options instead, such as:
  ./grun antlr.WACC program testcases/valid/basic/exit/exitBasic.wacc -gui

> compile <

To compile the .wacc file:
```
./compile mysourcecode.wacc
```

Generates `mysourcecode.s` in the root of the repository.