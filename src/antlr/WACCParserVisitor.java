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
	 * Visit a parse tree produced by {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(WACCParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_lhs(WACCParser.Assign_lhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_rhs(WACCParser.Assign_rhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(WACCParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#pair_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_elem(WACCParser.Pair_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(WACCParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_type(WACCParser.Base_typeContext ctx);
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
	 * Visit a parse tree produced by {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair_elem_type(WACCParser.Pair_elem_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(WACCParser.ExprContext ctx);
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