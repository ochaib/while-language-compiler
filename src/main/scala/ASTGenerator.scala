import WACCParser._
import org.antlr.v4.runtime._

// Class used to traverse the parse tree built by ANTLR
class ASTGenerator extends WACCParserBaseVisitor[ASTNode] {

  override def visitProgram(ctx: WACCParser.ProgramContext): ASTNode = {
    // Need to retrieve program information from parser context,
    // Need to visit all the functions (as many as there are due to the *)
    // Need to visit the statement "node".
    val functions: Array[FuncNode] = // Need to get array of functions
    val stat: StatNode = // Need to get stat node from the parse context

    // Then create program node from the two
    new ProgramNode(stat, functions)
  }

  override def visitFunc(ctx: WACCParser.FuncContext): ASTNode = {


  }

  override def visitParam_list(ctx: WACCParser.Param_listContext): ASTNode = {
    // ⟨param ⟩ ( ‘,’ ⟨param ⟩ )*
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
    // ⟨type ⟩ ⟨ident ⟩
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

  def visitAssignRHS(ctx: WACCParser.AssignRHSExprContext): ASTNode = {
    val RHSExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
  }

  def visitAssignRHSLiteral(ctx: WACCParser.AssignRHSLiteralContext): ASTNode = {
    val RHSLiteral: ArrayLiteralNode = visit(ctx.getChild(0)).asInstanceOf[ArrayLiteralNode]
  }

  def visitAssignRHSNewPair(ctx: WACCParser.AssignRHSNewPairContext): ASTNode = {
    // ‘newpair’ ‘(’ ⟨expr ⟩ ‘,’ ⟨expr ⟩ ‘)’
    val newPairFst: NewPairNode = visit(ctx.getChild(2)).asInstanceOf[NewPairNode]
    val newPairSnd: NewPairNode = visit(ctx.getChild(4)).asInstanceOf[NewPairNode]

    NewPairNode(newPairFst, newPairSnd)
  }

  def visitAssignRHSPairElem(ctx: WACCParser.AssignRHSPairElemContext): ASTNode = {
    // ⟨pair-elem ⟩
    val RHSPairElem: PairElemNode = visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  def visitAssignRHSCall(ctx: WACCParser.AssignRHSCallContext): ASTNode = {
    // ‘call’ ⟨ident ⟩ ‘(’ ⟨arg-list ⟩? ‘)’
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    val argList: NewPairNode = //

    CallNode(ident, )
  }
  
  override def visitArg_list(ctx: WACCParser.Arg_listContext): ASTNode = {
    
  }

  override def visitPair_elem(ctx: WACCParser.Pair_elemContext): ASTNode = {
    
  }

  override def visitType(ctx: WACCParser.TypeContext): ASTNode = {

  }

  override def visitBase_type(ctx: WACCParser.Base_typeContext): ASTNode = {

  }

  override def visitArray_type(ctx: WACCParser.Array_typeContext): ASTNode = {

  }

  override def visitPair_type(ctx: WACCParser.Pair_typeContext): ASTNode = {

  }

  override def visitPair_elem_type(ctx: WACCParser.Pair_elemContext): ASTNode = {

  }

  override def visitExpr(ctx: WACCParser.ExprContext): ASTNode = {

  }

  override def visitUnary_oper(ctx: WACCParser.Unary_operContext): ASTNode = {

  }

  override def visitBinary_oper(ctx: WACCParser.Binary_operContext): ASTNode = {

  }

  override def visitArray_elem(ctx: WACCParser.Array_elemContext): ASTNode = {

  }

  override def visitArray_liter(ctx: WACCParser.Array_literContext): ASTNode = {

  }

  // Need to add remaining visiting methods for nodes defined in ASTNode however the
  // WACCParserVisitor doesn't have these methods so they shouldn't be overrides.
  // But then where do I get their contexts from.





}