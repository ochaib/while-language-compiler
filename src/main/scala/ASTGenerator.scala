import WACCParser._

// Class used to traverse the parse tree built by ANTLR
class ASTGenerator extends WACCParserBaseVisitor[T] {

  override def visitProgram(ctx: WACCParser.ProgramContext): T = {
    // Need to retrieve program information from parser context,
    // Need to visit all the functions (as many as there are due to the *)
    // Need to visit the statement "node".
  }

  override def visitFunc(ctx: WACCParser.FuncContext): T = {


  }

  override def visitParam_list(ctx: WACCParser.Param_listContext): T = {


  }

  override def visitParam(ctx: WACCParser.ParamContext): T = {


  }

  override def visitStat(ctx: WACCParser.StatContext): T = {

  }

  override def visitAssign_lhs (ctx: WACCParser.Assign_lhsContext): T = {
    
  }

  override def visitAssign_rhs(ctx: WACCParser.Assign_rhsContext): T = {
    
  }
  
  override def visitArg_list(ctx: WACCParser.Arg_listContext): T = {
    
  }

  override def visitPair_elem(ctx: WACCParser.Pair_elemContext): T = {
    
  }

  override def visitType(ctx: WACCParser.TypeContext): T = {

  }

  override def visitBase_type(ctx: WACCParser.Base_typeContext): T = {

  }

  override def visitArray_type(ctx: WACCParser.Array_typeContext): T = {

  }

  override def visitPair_type(ctx: WACCParser.Pair_typeContext): T = {

  }

  override def visitPair_elem_type(ctx: WACCParser.Pair_elemContext): T = {

  }

  override def visitExpr(ctx: WACCParser.ExprContext): T = {

  }

  override def visitUnary_oper(ctx: WACCParser.Unary_operContext): T = {

  }

  override def visitBinary_oper(ctx: WACCParser.Binary_operContext): T = {

  }

  override def visitArray_elem(ctx: WACCParser.Array_elemContext): T = {

  }

  override def visitArray_liter(ctx: WACCParser.Array_literContext): T = {

  }

  


}