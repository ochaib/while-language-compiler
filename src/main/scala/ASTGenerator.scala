import WACCParser._
import org.antlr.v4.runtime._

// Class used to traverse the parse tree built by ANTLR
class ASTGenerator extends WACCParserBaseVisitor[ASTNode] {

  override def visitProgram(ctx: WACCParser.ProgramContext): ASTNode = {
    // Need to retrieve program information from parser context,
    // Need to visit all the functions (as many as there are due to the *)
    // Need to visit the statement "node".
    // ‘begin’ ⟨func⟩* ⟨stat⟩ ‘end’
    //    0     1-n     n+1    n+2
    val childCount = ctx.getChildCount
    val functions: IndexedSeq[FuncNode] = IndexedSeq[FuncNode]()
    val stat: StatNode = visit(ctx.getChild(childCount - 1)).asInstanceOf[StatNode]

    for (i <- 1 to childCount - 2) {
      functions :+ visit(ctx.getChild(i)).asInstanceOf[FuncNode]
    }

    // Then create program node from the two
    new ProgramNode(stat, functions)
  }

  override def visitFunc(ctx: WACCParser.FuncContext): ASTNode = {

  }

  override def visitParam_list(ctx: WACCParser.Param_listContext): ASTNode = {
    // ⟨param⟩ ( ‘,’ ⟨param⟩ )*
    // Multiple params... a param list
    val childCount = ctx.getChildCount
    // Apparently IndexedSeq is much better than an array so I'll use it instead
    val paramList: IndexedSeq[ParamNode] = IndexedSeq[ParamNode]()

    // Hope this is how you do it.
    for (i <- 0 to childCount - 1) {
      paramList :+ visit(ctx.getChild(i)).asInstanceOf[ParamNode]
    }

    ParamListNode(paramList)
  }

  override def visitParam(ctx: WACCParser.ParamContext): ASTNode = {
    // ⟨type⟩ ⟨ident⟩
    val paramType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]

    ParamNode(paramType, ident)
  }

  override def visitStat(ctx: WACCParser.StatContext): ASTNode = {

  }

  // Individual components of stat that must be visited.
  override def visitSkip(ctx: WACCParser.SkipContext): ASTNode = {
    println("Skip stat.")
    // Or something.
    SkipNode()
  }

  override def visitDeclaration(ctx: WACCParser.DeclarationContext): ASTNode = {
    // Need to get each individual child of the declaration context as its own type.
    val typeNode: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    val assignRHS: AssignRHSNode = visit(ctx.getChild(3)).asInstanceOf[AssignRHSNode]

    // Then create DeclarationNode with the above as parameters for constructor
    DeclarationNode(typeNode, ident, assignRHS)
  }

  override def visitAssignment(ctx: WACCParser.AssignmentContext): ASTNode = {
    // Get assignLHS and assignRHS then construct AssignNode with them
    val lhs: AssignLHSNode = visit(ctx.getChild(0)).asInstanceOf[AssignLHSNode]
    // Have to consider the equals
    val rhs: AssignRHSNode = visit(ctx.getChild(2)).asInstanceOf[AssignRHSNode]

    AssignmentNode(lhs, rhs)
  }

  override def visitRead(ctx: WACCParser.ReadContext): ASTNode = {
    // ‘read’ ⟨assign-lhs⟩
    val lhs: AssignLHSNode = visit(ctx.getChild(1)).asInstanceOf[AssignLHSNode]

    ReadNode(lhs)
  }

  override def visitFree(ctx: WACCParser.FreeContext): ASTNode = {
    // ‘free’ ⟨expr⟩
    val freeExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    FreeNode(freeExpr)
  }

  override def visitReturn(ctx: WACCParser.ReturnContext): ASTNode = {
    // ‘return’ ⟨expr ⟩
    val returnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    ReturnNode(returnExpr)
  }

  override def visitExit(ctx: WACCParser.ExitContext): ASTNode = {
    //  ‘exit’ ⟨expr ⟩
    val exitExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    ExitNode(exitExpr)
  }

  override def visitPrint(ctx: WACCParser.PrintContext): ASTNode = {
    // ‘print’ ⟨expr ⟩
    val printExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    PrintNode(printExpr)
  }

  override def visitPrintln(ctx: WACCParser.PrintlnContext): ASTNode = {
    // ‘println’ ⟨expr ⟩
    val printlnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    PrintlnNode(printlnExpr)
  }

  override def visitIf(ctx: WACCParser.IfContext): ASTNode = {
  // ‘if’ ⟨expr ⟩ ‘then’ ⟨stat ⟩ ‘else’ ⟨stat ⟩ ‘fi’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val thenStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]
    val elseStat: StatNode = visit(ctx.getChild(5)).asInstanceOf[StatNode]

    IfNode(conditionExpr, thenStat, elseStat)
  }

  override def visitWhile(ctx: WACCParser.WhileContext): ASTNode = {
    // ‘while’ ⟨expr ⟩ ‘do’ ⟨stat ⟩ ‘done’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val doStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]

    WhileNode(conditionExpr, doStat)
  }

  override def visitBegin(ctx: WACCParser.BeginContext): ASTNode = {
    // ‘begin’ ⟨stat ⟩ ‘end’
    val beginStat: StatNode = visit(ctx.getChild(1)).asInstanceOf[StatNode]

    BeginNode(beginStat)
  }

  override def visitSequence(ctx: WACCParser.SequenceContext): ASTNode = {
    // ⟨stat ⟩ ‘;’ ⟨stat ⟩
    val statOne: StatNode = visit(ctx.getChild(0)).asInstanceOf[StatNode]
    val statTwo: StatNode = visit(ctx.getChild(2)).asInstanceOf[StatNode]

    new SequenceNode(statOne, statTwo)
  }

  // Need to traverse each possible option of assign-lhs
  override def visitAssign_lhs(ctx: WACCParser.Assign_lhsContext): ASTNode = {
    
  }

  def visitAssignLHSIdent(ctx: WACCParser.AssignLHSIdentContext): ASTNode = {
    val LHSIdent: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    // Now what?
  }

  def visitAssignLHSArrayElem(ctx: WACCParser.AssignLHSArrayElemContext): ASTNode = {
    val LHSArrayElem: ArrayElemNode = visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

  def visitAssignLHSPairElem(ctx: WACCParser.AssignLHSPairElemContext): ASTNode = {
    val LHSPairElem: PairElemNode = visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  override def visitAssign_rhs(ctx: WACCParser.Assign_rhsContext): ASTNode = {

  }

  def visitAssignRHSExpr(ctx: WACCParser.AssignRHSExprContext): ASTNode = {
    val RHSExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
  }

  def visitAssignRHSLiteral(ctx: WACCParser.AssignRHSLiteralContext): ASTNode = {
    val RHSLiteral: ArrayLiteralNode = visit(ctx.getChild(0)).asInstanceOf[ArrayLiteralNode]
  }

  def visitAssignRHSNewPair(ctx: WACCParser.AssignRHSNewPairContext): ASTNode = {
    // ‘newpair’ ‘(’ ⟨expr ⟩ ‘,’ ⟨expr ⟩ ‘)’
    val newPairFst: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]
    val newPairSnd: ExprNode = visit(ctx.getChild(4)).asInstanceOf[ExprNode]

    NewPairNode(newPairFst, newPairSnd)
  }

  def visitAssignRHSPairElem(ctx: WACCParser.AssignRHSPairElemContext): ASTNode = {
    // ⟨pair-elem ⟩
    val RHSPairElem: PairElemNode = visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  def visitAssignRHSCall(ctx: WACCParser.AssignRHSCallContext): ASTNode = {
    // ‘call’ ⟨ident ⟩ ‘(’ ⟨arg-list ⟩? ‘)’
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    // Have to make this optional.
    val argList: ArgListNode = visit(ctx.getChild(3)).asInstanceOf[ArgListNode]

    CallNode(ident, argList)
  }
  
  override def visitArg_list(ctx: WACCParser.Arg_listContext): ASTNode = {
    // ⟨expr ⟩ (‘,’ ⟨expr ⟩ )*
    val childCount = ctx.getChildCount

    val exprChildren: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    for (i <- 0 to childCount - 1) {
      exprChildren :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    ArgListNode(exprChildren)
  }

  override def visitPair_elem(ctx: WACCParser.Pair_elemContext): ASTNode = {
    
  }

  def visitPairFst(ctx: WACCParser.PairFstContext): ASTNode = {
    // ‘fst’ ⟨expr ⟩
    val pairFstExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    FstNode(pairFstExpr)
  }

  def visitPairSnd(ctx: WACCParser.PairSndContext): ASTNode = {
    // ‘snd’ ⟨expr ⟩
    val pairSndExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    SndNode(pairSndExpr)
  }

  override def visitType(ctx: WACCParser.TypeContext): ASTNode = {
    // ⟨type⟩
  }

  def visitTypeBase_type(ctx: WACCParser.TypeBase_typeContext): ASTNode = {
    // ⟨base-type⟩
    val baseType: BaseTypeNode = visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  def visitTypeArray_type(ctx: WACCParser.TypeArray_typeContext): ASTNode = {
    // ⟨array-type⟩
    val arrayType: ArrayTypeNode = visit(ctx.getChild(0)).asInstanceOf[ArrayTypeNode]
  }

  def visitTypePair_type(ctx: WACCParser.TypePair_typeContext): ASTNode = {
    // ⟨pair-type⟩
    val pairType: PairTypeNode = visit(ctx.getChild(0)).asInstanceOf[PairTypeNode]
  }

  override def visitBase_type(ctx: WACCParser.Base_typeContext): ASTNode = {

  }

  def visitIntBase_type(ctx: WACCParser.IntBase_typeContext): ASTNode = {
    // 'int'
    IntTypeNode()
  }

  def visitBoolBase_type(ctx: WACCParser.BoolBase_typeContext): ASTNode = {
    // 'bool'
    BoolTypeNode()
  }

  def visitCharBase_type(ctx: WACCParser.CharBase_typeContext): ASTNode = {
    // 'char'
    CharTypeNode()
  }

  def visitStringBase_type(ctx: WACCParser.StringBase_typeContext): ASTNode = {
    // 'string'
    StringTypeNode()
  }

  override def visitArray_type(ctx: WACCParser.Array_typeContext): ASTNode = {
    // ⟨type ⟩ ‘[’ ‘]’
    val arrayType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]

    ArrayTypeNode(arrayType)
  }

  override def visitPair_type(ctx: WACCParser.Pair_typeContext): ASTNode = {
    // ‘pair’ ‘(’ ⟨pair-elem-type ⟩ ‘,’ ⟨pair-elem-type ⟩ ‘)’

    val fstPairElem: PairElemTypeNode = visit(ctx.getChild(2)).asInstanceOf[PairElemTypeNode]
    val sndPairElem: PairElemTypeNode = visit(ctx.getChild(4)).asInstanceOf[PairElemTypeNode]

    PairTypeNode(fstPairElem, sndPairElem)
  }

  override def visitPair_elem_type(ctx: WACCParser.Pair_elemContext): ASTNode = {
    // ⟨pair-elem-type⟩
  }

  def visitPETBaseType(ctx: WACCParser.PETBaseTypeContext): ASTNode = {
    // ⟨base-type⟩
    val baseType: BaseTypeNode = visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  def visitPETArrayType(ctx: WACCParser.PETArrayTypeContext): ASTNode = {
    // ⟨array-type⟩
    val arrayType: ArrayTypeNode = visit(ctx.getChild(0)).asInstanceOf[ArrayTypeNode]
  }

  // I doubt one is needed for the ⟨pair-elem-type⟩ 'pair'. Surely just a string?
  def visitPETPair(ctx: WACCParser.PETPairContext): ASTNode = {
    // 'pair'
    //  PairNode()
  }

  override def visitExpr(ctx: WACCParser.ExprContext): ASTNode = {

  }

  def visitExprIntLiter(ctx: WACCParser.ExprIntLiterContext): ASTNode = {
    // ⟨int-liter⟩
    val intLiter: IntLiteralNode = visit(ctx.getChild(0)).asInstanceOf[IntLiteralNode]
  }

  def visitIntLiteral(ctx: WACCParser.IntLiteralContext): ASTNode = {
    // ⟨int-sign⟩? ⟨digit⟩+
    // Need to create node for sign, needs to be optional.
    val intSign: Char
    val childCount = ctx.getChildCount
    // Need to create list of digitNodes, or just chars...
//    val digits: IndexedSeq[DigitNode] = IndexedSeq[DigitNode]()
    val digits: IndexedSeq[Char] = IndexedSeq[Char]()

    for (i <- 1 to childCount) {
      digits :+ visit(ctx.getChild(i))
    }

    IntLiteralNode(intSign, digits)
  }

  def visitExprBoolLiter(ctx: WACCParser.ExprBoolLiterContext): ASTNode = {
    // ⟨bool-liter⟩
    val boolLiter: BoolLiteralNode = visit(ctx.getChild(0)).asInstanceOf[BoolLiteralNode]
  }

  def visitBoolLiteral(ctx: WACCParser.BoolLiteralContext): ASTNode = {
    // ‘true’ | ‘false’
    val boolValue: String = ctx.getChild(0).toBoolean

    BoolLiteralNode(boolValue)
  }

  def visitExprCharLiter(ctx: WACCParser.ExprCharLiterContext): ASTNode = {
    // ⟨char-liter⟩
    val charLiter: CharLiteralNode = visit(ctx.getChild(0)).asInstanceOf[CharLiteralNode]
  }

  def visitCharLiteral(ctx: WACCParser.CharLiteralContext): ASTNode = {
    // ‘’’ ⟨character ⟩ ‘’’
    val charValue: CharLiteralNode = ctx.getChild(1).charAt(0)

    CharLiteralNode(charValue)
  }

  def visitExprStringLiter(ctx: WACCParser.ExprStringLiterContext): ASTNode = {
    // ⟨string-liter⟩
    val stringLiter: StringLiteralNode = visit(ctx.getChild(0)).asInstanceOf[StringLiteralNode]
  }

  def visitStringLiteral(ctx: WACCParser.StringLiteralContext): ASTNode = {
    // ‘"’ ⟨character⟩* ‘"’
    val childCount = ctx.getChildCount
    val charList: IndexedSeq[Char] = IndexedSeq[Char]()

    for (i <- 1 to childCount - 1) {
      charList :+ visit(ctx.getChild(i))
    }

    StringLiteralNode(charList)
  }

  def visitExprPairLiter(ctx: WACCParser.ExprPairLiterContext): ASTNode = {
    // ⟨pair-liter⟩
    val pairLiter: PairLiteralNode = visit(ctx.getChild(0)).asInstanceOf[PairLiteralNode]
  }

  def visitExprIdent(ctx: WACCParser.ExprIdentContext): ASTNode = {
    // ⟨ident⟩
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
  }

  def visitExprArrayElem(ctx: WACCParser.ExprArrayElemContext): ASTNode = {
    // ⟨array-elem⟩
    val arrayElem: ArrayElemNode = visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

  override def visitUnary_oper(ctx: WACCParser.Unary_operContext): ASTNode = {
    // This is not a node so it shouldn't be visited, find out how to extract
    // information/variables from child.
    // ⟨unary-oper⟩ ⟨expr⟩
    val unaryOperator: Char = ctx.getChild(0)
    val expr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    // Now I need to figure out which unary operator it is from the context
    // then construct the relevant node.
  }

  override def visitBinary_oper(ctx: WACCParser.Binary_operContext): ASTNode = {
    // ⟨expr⟩ ⟨binary-oper⟩ ⟨expr⟩
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: Char = ctx.getChild(1)
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    // Need to deduce binary operator from context and construct node.
  }

  def visitBracketExpr(ctx: WACCParser.BracketExprContext): ASTNode = {
    // ‘(’ ⟨expr⟩ ‘)’
    val expr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
  }

  def visitIdent(ctx: WACCParser.IdentContext): ASTNode = {
    // Research how to get information from top level context because it would be
    // inefficient to traverse each ctx.child in order to have to construct it
    // again.
    val stringStream: String

    IdentNode(stringStream)
  }

  override def visitArray_elem(ctx: WACCParser.Array_elemContext): ASTNode = {
    // ⟨ident⟩ (‘[’ ⟨expr⟩ ‘]’)+
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    val childCount = ctx.getChildCount
    val exprList: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    // To get every expr in the exprList
    for (i <- 2 to childCount - 1) {
      exprList :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    ArrayElemNode(ident, exprList)
  }

  override def visitArray_liter(ctx: WACCParser.Array_literContext): ASTNode = {
    // ‘[’ ( ⟨expr⟩ (‘,’ ⟨expr⟩)* )? ‘]’
    val childCount = ctx.getChildCount
    val exprList: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    // To get every expr in the exprList but I don't think it works here because
    // it would be separated by commas, need to get every next expr after comma.
    for (i <- 0 to childCount - 1) {
      exprList :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    ArrayLiteralNode(exprList)
  }

  // Need to add remaining visiting methods for nodes defined in ASTNode however the
  // WACCParserVisitor doesn't have these methods so they shouldn't be overrides.
  // But then where do I get their contexts from.



}