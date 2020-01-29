// Generated from ./WACCParser.g4 by ANTLR 4.7
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WACCParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WACCParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WACCParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(WACCParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(WACCParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#param_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_list(WACCParser.Param_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(WACCParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Return}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(WACCParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code While}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(WACCParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Sequence}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequence(WACCParser.SequenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Free}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFree(WACCParser.FreeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Assignment}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(WACCParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Read}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRead(WACCParser.ReadContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Print}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(WACCParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Skip}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip(WACCParser.SkipContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Begin}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBegin(WACCParser.BeginContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Println}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintln(WACCParser.PrintlnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Declaration}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(WACCParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code If}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(WACCParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Exit}
	 * labeled alternative in {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExit(WACCParser.ExitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LHSIdent}
	 * labeled alternative in {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLHSIdent(WACCParser.LHSIdentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LHSArrayElem}
	 * labeled alternative in {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLHSArrayElem(WACCParser.LHSArrayElemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LHSPairElem}
	 * labeled alternative in {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLHSPairElem(WACCParser.LHSPairElemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RHSExpr}
	 * labeled alternative in {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRHSExpr(WACCParser.RHSExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RHSLiteral}
	 * labeled alternative in {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRHSLiteral(WACCParser.RHSLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RHSNewPair}
	 * labeled alternative in {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRHSNewPair(WACCParser.RHSNewPairContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RHSPairElem}
	 * labeled alternative in {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRHSPairElem(WACCParser.RHSPairElemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RHSCall}
	 * labeled alternative in {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRHSCall(WACCParser.RHSCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(WACCParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PairFst}
	 * labeled alternative in {@link WACCParser#pair_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairFst(WACCParser.PairFstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PairSnd}
	 * labeled alternative in {@link WACCParser#pair_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPairSnd(WACCParser.PairSndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeBase_type}
	 * labeled alternative in {@link WACCParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeBase_type(WACCParser.TypeBase_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypePair_type}
	 * labeled alternative in {@link WACCParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypePair_type(WACCParser.TypePair_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeArray_type}
	 * labeled alternative in {@link WACCParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArray_type(WACCParser.TypeArray_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntBase_type}
	 * labeled alternative in {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntBase_type(WACCParser.IntBase_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BoolBase_type}
	 * labeled alternative in {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolBase_type(WACCParser.BoolBase_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CharBase_type}
	 * labeled alternative in {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharBase_type(WACCParser.CharBase_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringBase_type}
	 * labeled alternative in {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringBase_type(WACCParser.StringBase_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#array_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_type(WACCParser.Array_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#pair_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_type(WACCParser.Pair_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PETBaseType}
	 * labeled alternative in {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPETBaseType(WACCParser.PETBaseTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PETArrayType}
	 * labeled alternative in {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPETArrayType(WACCParser.PETArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PETPair}
	 * labeled alternative in {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPETPair(WACCParser.PETPairContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprPairLiter}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprPairLiter(WACCParser.ExprPairLiterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBinaryOper}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBinaryOper(WACCParser.ExprBinaryOperContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStringLiter}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStringLiter(WACCParser.ExprStringLiterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprArrayElem}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprArrayElem(WACCParser.ExprArrayElemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprCharLiter}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprCharLiter(WACCParser.ExprCharLiterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BracketExpr}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketExpr(WACCParser.BracketExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBoolLiter}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBoolLiter(WACCParser.ExprBoolLiterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprIntLiter}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprIntLiter(WACCParser.ExprIntLiterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprIdent}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprIdent(WACCParser.ExprIdentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprUnaryOper}
	 * labeled alternative in {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprUnaryOper(WACCParser.ExprUnaryOperContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#unary_oper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_oper(WACCParser.Unary_operContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#binary_oper}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_oper(WACCParser.Binary_operContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#array_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_elem(WACCParser.Array_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#array_liter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_liter(WACCParser.Array_literContext ctx);
}