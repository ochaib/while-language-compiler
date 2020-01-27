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

  }

  override def visitParam(ctx: WACCParser.ParamContext): ASTNode = {

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
    val typeNode: TypeNode = TypeNode(visit(ctx.getChild(0)))
    val ident: IdentNode = IdentNode(visit(ctx.getChild(1)))
    val assignRHS: AssignRHSNode = AssignRHSNode(visit(ctx.getChild(3)))

    // Then create DeclarationNode with the above as parameters for constructor
    DeclarationNode(typeNode, ident, assignRHS)
  }

  override def visitAssignment(ctx: WACCParser.AssignmentContext): ASTNode = {
    // Get assignLHS and assignRHS then construct AssignNode with them
    val lhs: AssignLHSNode = AssignLHSNode(visit(ctx.getChild(0)))
    // Have to consider the equals
    val rhs: AssignRHSNode = AssignRHSNode(visit(ctx.getChild(2)))

    AssignmentNode(lhs, rhs)
  }

  override def visitRead(ctx: WACCParser.ReadContext): ASTNode = {
    // ‘read’ ⟨assign-lhs⟩
    val lhs: AssignLHSNode = AssignLHSNode(visit(ctx.getChild(1)))

    ReadNode(lhs)
  }

  override def visitFree(ctx: WACCParser.FreeContext): ASTNode = {
    // ‘free’ ⟨expr ⟩
    val freeExpr: ExprNode = ExprNode(visit(ctx.getChild(1)))

    FreeNode(freeExpr)
  }

  override def visitReturn(ctx: WACCParser.ReturnContext): ASTNode = {
    val returnExpr: ExprNode = ExprNode(visit(ctx.getChild(1)))

    ReturnNode(returnExpr)
  }

  override def visitExit(ctx: WACCParser.ExitContext): ASTNode = {
    val exitExpr: ExprNode = ExprNode(visit(ctx.getChild(1)))

    ExitNode(exitExpr)
  }

  override def visitPrint(ctx: WACCParser.PrintContext): ASTNode = {
    val printExpr: ExprNode = ExprNode(visit(ctx.getChild(1)))

    PrintNode(printExpr)
  }

  override def visitPrintln(ctx: WACCParser.PrintlnContext): ASTNode = {
    val printlnExpr: ExprNode = ExprNode(visit(ctx.getChild(1)))

    PrintlnNode(printlnExpr)
  }

  override def visitIf(ctx: WACCParser.IfContext): ASTNode = {

  }

  override def visitWhile(ctx: WACCParser.WhileContext): ASTNode = {

  }

  override def visitBegin(ctx: WACCParser.BeginContext): ASTNode = {

  }

  override def visitSequence(ctx: WACCParser.SequenceContext): ASTNode = {

  }

  override def visitAssign_lhs (ctx: WACCParser.Assign_lhsContext): ASTNode = {
    
  }

  override def visitAssign_rhs(ctx: WACCParser.Assign_rhsContext): ASTNode = {
    
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