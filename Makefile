# Locations

ANTLR_DIR	:= antlr_config
SOURCE_DIR	:= src

# Tools

ANTLR	:= antlrBuild
RM	:= rm -rf
SBT := sbt
PYTHON := python3.6

# the make rules

all: antlr sources bin tests

# test some
test:
	$(PYTHON) test-some.py
testall:
	$(PYTHON) test.py

# runs the antlr build script
antlr:
	cd $(ANTLR_DIR) && ./$(ANTLR)

# compiles SBT project
sources:
	$(SBT) clean compile

# makes assembly compiler JAR
bin:
	$(SBT) assembly

# compiles test files
tests:
	$(PYTHON) make_asm_tests.py

clean:
	$(RM) rules $(SOURCE_DIR)/main/java/antlr
	$(SBT) clean
	$(RM) *.s

.PHONY: all rules clean sources antlr bin tests