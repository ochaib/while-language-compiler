# Locations

ANTLR_DIR	:= antlr_config
SOURCE_DIR	:= src

# Tools

ANTLR	:= antlrBuild
RM	:= rm -rf
SBT := sbt

# the make rules

all: antlr sources

# runs the antlr build script then attempts to compile all .java files within src
sources:
	$(SBT) clean compile

antlr:
	cd $(ANTLR_DIR) && ./$(ANTLR)

clean:
	$(RM) rules $(SOURCE_DIR)/main/java/antlr
	$(SBT) clean

.PHONY: all rules clean