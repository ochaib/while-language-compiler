# Locations

ANTLR_DIR	:= antlr_config
SOURCE_DIR	:= src

# Tools

ANTLR	:= antlrBuild
RM	:= rm -rf
SBT := sbt

# the make rules

all: antlr sources bin

# runs the antlr build script
antlr:
	cd $(ANTLR_DIR) && ./$(ANTLR)

# compiles SBT project
sources:
	$(SBT) clean compile

# makes assembly compiler JAR
bin:
	$(SBT) assembly

clean:
	$(RM) rules $(SOURCE_DIR)/main/java/antlr
	$(SBT) clean

.PHONY: all rules clean sources antlr bin