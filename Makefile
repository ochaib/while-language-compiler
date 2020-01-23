# Locations

ANTLR_DIR	:= antlr_config
SOURCE_DIR	:= src

# Tools

ANTLR	:= antlrBuild
RM	:= rm -rf
SBT := sbt

# the make rules

all: rules

# runs the antlr build script then attempts to compile all .java files within src
rules:
	cd $(ANTLR_DIR) && ./$(ANTLR)
	$(SBT) clean compile

clean:
	$(RM) rules $(SOURCE_DIR)/main/java/antlr
	$(SBT) clean

.PHONY: all rules clean