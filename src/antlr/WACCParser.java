// Generated from ./WACCParser.g4 by ANTLR 4.7
package antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IGNORE=1, BEGIN=2, END=3, IS=4, SKIP_=5, READ=6, FREE=7, RETURN=8, EXIT=9, 
		PRINT=10, PRINTLN=11, IF=12, THEN=13, ELSE=14, FI=15, WHILE=16, DO=17, 
		DONE=18, NEWPAIR=19, CALL=20, FST=21, SND=22, INT=23, BOOL=24, CHAR=25, 
		STRING=26, PAIR=27, OPEN_PARENTHESES=28, CLOSE_PARENTHESES=29, COMMA=30, 
		EQUALS=31, SEMICOLON=32, OPEN_BRACKET=33, CLOSE_BRACKET=34, NOT=35, LEN=36, 
		ORD=37, CHR=38, MULTIPLY=39, DIVIDE=40, MODULO=41, PLUS=42, MINUS=43, 
		GT=44, GTE=45, LT=46, LTE=47, EE=48, NE=49, AND=50, OR=51, SIGN=52, INT_LIT=53, 
		PAIR_LIT=54, BOOL_LIT=55, CHAR_LIT=56, STR_LIT=57, IDENT=58, COMMENT=59;
	public static final int
		RULE_program = 0, RULE_func = 1, RULE_param_list = 2, RULE_param = 3, 
		RULE_stat = 4, RULE_assign_lhs = 5, RULE_assign_rhs = 6, RULE_arg_list = 7, 
		RULE_pair_elem = 8, RULE_type = 9, RULE_base_type = 10, RULE_array_type = 11, 
		RULE_pair_type = 12, RULE_pair_elem_type = 13, RULE_expr = 14, RULE_unary_oper = 15, 
		RULE_binary_oper = 16, RULE_array_elem = 17, RULE_array_liter = 18;
	public static final String[] ruleNames = {
		"program", "func", "param_list", "param", "stat", "assign_lhs", "assign_rhs", 
		"arg_list", "pair_elem", "type", "base_type", "array_type", "pair_type", 
		"pair_elem_type", "expr", "unary_oper", "binary_oper", "array_elem", "array_liter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'begin'", "'end'", "'is'", "'skip'", "'read'", "'free'", 
		"'return'", "'exit'", "'print'", "'println'", "'if'", "'then'", "'else'", 
		"'fi'", "'while'", "'do'", "'done'", "'newpair'", "'call'", "'fst'", "'snd'", 
		"'int'", "'bool'", "'char'", "'string'", "'pair'", "'('", "')'", "','", 
		"'='", "';'", "'['", "']'", "'!'", "'len'", "'ord'", "'chr'", "'*'", "'/'", 
		"'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", "'=='", "'!='", "'&&'", 
		"'||'", null, null, "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "IGNORE", "BEGIN", "END", "IS", "SKIP_", "READ", "FREE", "RETURN", 
		"EXIT", "PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", 
		"DONE", "NEWPAIR", "CALL", "FST", "SND", "INT", "BOOL", "CHAR", "STRING", 
		"PAIR", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "COMMA", "EQUALS", "SEMICOLON", 
		"OPEN_BRACKET", "CLOSE_BRACKET", "NOT", "LEN", "ORD", "CHR", "MULTIPLY", 
		"DIVIDE", "MODULO", "PLUS", "MINUS", "GT", "GTE", "LT", "LTE", "EE", "NE", 
		"AND", "OR", "SIGN", "INT_LIT", "PAIR_LIT", "BOOL_LIT", "CHAR_LIT", "STR_LIT", 
		"IDENT", "COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "WACCParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WACCParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode BEGIN() { return getToken(WACCParser.BEGIN, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public TerminalNode EOF() { return getToken(WACCParser.EOF, 0); }
		public List<FuncContext> func() {
			return getRuleContexts(FuncContext.class);
		}
		public FuncContext func(int i) {
			return getRuleContext(FuncContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(BEGIN);
			setState(42);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(39);
					func();
					}
					}
				}
				setState(44);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(45);
			stat(0);
			setState(46);
			match(END);
			setState(47);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public TerminalNode IS() { return getToken(WACCParser.IS, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public Param_listContext param_list() {
			return getRuleContext(Param_listContext.class,0);
		}
		public FuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncContext func() throws RecognitionException {
		FuncContext _localctx = new FuncContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_func);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			type(0);
			setState(50);
			match(IDENT);
			setState(51);
			match(OPEN_PARENTHESES);
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << CHAR) | (1L << STRING) | (1L << PAIR))) != 0)) {
				{
				setState(52);
				param_list();
				}
			}

			setState(55);
			match(CLOSE_PARENTHESES);
			setState(56);
			match(IS);
			setState(57);
			stat(0);
			setState(58);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_listContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Param_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitParam_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_listContext param_list() throws RecognitionException {
		Param_listContext _localctx = new Param_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_param_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			param();
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(61);
				match(COMMA);
				setState(62);
				param();
				}
				}
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			type(0);
			setState(69);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatContext extends ParserRuleContext {
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }

		public StatContext() { }
		public void copyFrom(StatContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ReturnContext extends StatContext {
		public TerminalNode RETURN() { return getToken(WACCParser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitReturn(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileContext extends StatContext {
		public TerminalNode WHILE() { return getToken(WACCParser.WHILE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode DO() { return getToken(WACCParser.DO, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode DONE() { return getToken(WACCParser.DONE, 0); }
		public WhileContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SequenceContext extends StatContext {
		public List<StatContext> stat() {
			return getRuleContexts(StatContext.class);
		}
		public StatContext stat(int i) {
			return getRuleContext(StatContext.class,i);
		}
		public TerminalNode SEMICOLON() { return getToken(WACCParser.SEMICOLON, 0); }
		public SequenceContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitSequence(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FreeContext extends StatContext {
		public TerminalNode FREE() { return getToken(WACCParser.FREE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FreeContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitFree(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignmentContext extends StatContext {
		public Assign_lhsContext assign_lhs() {
			return getRuleContext(Assign_lhsContext.class,0);
		}
		public TerminalNode EQUALS() { return getToken(WACCParser.EQUALS, 0); }
		public Assign_rhsContext assign_rhs() {
			return getRuleContext(Assign_rhsContext.class,0);
		}
		public AssignmentContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReadContext extends StatContext {
		public TerminalNode READ() { return getToken(WACCParser.READ, 0); }
		public Assign_lhsContext assign_lhs() {
			return getRuleContext(Assign_lhsContext.class,0);
		}
		public ReadContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRead(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrintContext extends StatContext {
		public TerminalNode PRINT() { return getToken(WACCParser.PRINT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PrintContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPrint(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SkipContext extends StatContext {
		public TerminalNode SKIP_() { return getToken(WACCParser.SKIP_, 0); }
		public SkipContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitSkip(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BeginContext extends StatContext {
		public TerminalNode BEGIN() { return getToken(WACCParser.BEGIN, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public BeginContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitBegin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrintlnContext extends StatContext {
		public TerminalNode PRINTLN() { return getToken(WACCParser.PRINTLN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PrintlnContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPrintln(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeclarationContext extends StatContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public TerminalNode EQUALS() { return getToken(WACCParser.EQUALS, 0); }
		public Assign_rhsContext assign_rhs() {
			return getRuleContext(Assign_rhsContext.class,0);
		}
		public DeclarationContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfContext extends StatContext {
		public TerminalNode IF() { return getToken(WACCParser.IF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode THEN() { return getToken(WACCParser.THEN, 0); }
		public List<StatContext> stat() {
			return getRuleContexts(StatContext.class);
		}
		public StatContext stat(int i) {
			return getRuleContext(StatContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(WACCParser.ELSE, 0); }
		public TerminalNode FI() { return getToken(WACCParser.FI, 0); }
		public IfContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExitContext extends StatContext {
		public TerminalNode EXIT() { return getToken(WACCParser.EXIT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExitContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		return stat(0);
	}

	private StatContext stat(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		StatContext _localctx = new StatContext(_ctx, _parentState);
		StatContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_stat, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SKIP_:
				{
				_localctx = new SkipContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(72);
				match(SKIP_);
				}
				break;
			case INT:
			case BOOL:
			case CHAR:
			case STRING:
			case PAIR:
				{
				_localctx = new DeclarationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(73);
				type(0);
				setState(74);
				match(IDENT);
				setState(75);
				match(EQUALS);
				setState(76);
				assign_rhs();
				}
				break;
			case FST:
			case SND:
			case IDENT:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(78);
				assign_lhs();
				setState(79);
				match(EQUALS);
				setState(80);
				assign_rhs();
				}
				break;
			case READ:
				{
				_localctx = new ReadContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(82);
				match(READ);
				setState(83);
				assign_lhs();
				}
				break;
			case FREE:
				{
				_localctx = new FreeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84);
				match(FREE);
				setState(85);
				expr(0);
				}
				break;
			case RETURN:
				{
				_localctx = new ReturnContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(86);
				match(RETURN);
				setState(87);
				expr(0);
				}
				break;
			case EXIT:
				{
				_localctx = new ExitContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(88);
				match(EXIT);
				setState(89);
				expr(0);
				}
				break;
			case PRINT:
				{
				_localctx = new PrintContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(90);
				match(PRINT);
				setState(91);
				expr(0);
				}
				break;
			case PRINTLN:
				{
				_localctx = new PrintlnContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(92);
				match(PRINTLN);
				setState(93);
				expr(0);
				}
				break;
			case IF:
				{
				_localctx = new IfContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(94);
				match(IF);
				setState(95);
				expr(0);
				setState(96);
				match(THEN);
				setState(97);
				stat(0);
				setState(98);
				match(ELSE);
				setState(99);
				stat(0);
				setState(100);
				match(FI);
				}
				break;
			case WHILE:
				{
				_localctx = new WhileContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				match(WHILE);
				setState(103);
				expr(0);
				setState(104);
				match(DO);
				setState(105);
				stat(0);
				setState(106);
				match(DONE);
				}
				break;
			case BEGIN:
				{
				_localctx = new BeginContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				match(BEGIN);
				setState(109);
				stat(0);
				setState(110);
				match(END);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(119);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SequenceContext(new StatContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_stat);
					setState(114);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(115);
					match(SEMICOLON);
					setState(116);
					stat(2);
					}
					}
				}
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Assign_lhsContext extends ParserRuleContext {
		public Assign_lhsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_lhs; }

		public Assign_lhsContext() { }
		public void copyFrom(Assign_lhsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LHSIdentContext extends Assign_lhsContext {
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public LHSIdentContext(Assign_lhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitLHSIdent(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LHSArrayElemContext extends Assign_lhsContext {
		public Array_elemContext array_elem() {
			return getRuleContext(Array_elemContext.class,0);
		}
		public LHSArrayElemContext(Assign_lhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitLHSArrayElem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LHSPairElemContext extends Assign_lhsContext {
		public Pair_elemContext pair_elem() {
			return getRuleContext(Pair_elemContext.class,0);
		}
		public LHSPairElemContext(Assign_lhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitLHSPairElem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assign_lhsContext assign_lhs() throws RecognitionException {
		Assign_lhsContext _localctx = new Assign_lhsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_assign_lhs);
		try {
			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new LHSIdentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				match(IDENT);
				}
				break;
			case 2:
				_localctx = new LHSArrayElemContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(123);
				array_elem();
				}
				break;
			case 3:
				_localctx = new LHSPairElemContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(124);
				pair_elem();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_rhsContext extends ParserRuleContext {
		public Assign_rhsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_rhs; }

		public Assign_rhsContext() { }
		public void copyFrom(Assign_rhsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RHSPairElemContext extends Assign_rhsContext {
		public Pair_elemContext pair_elem() {
			return getRuleContext(Pair_elemContext.class,0);
		}
		public RHSPairElemContext(Assign_rhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRHSPairElem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RHSNewPairContext extends Assign_rhsContext {
		public TerminalNode NEWPAIR() { return getToken(WACCParser.NEWPAIR, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(WACCParser.COMMA, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public RHSNewPairContext(Assign_rhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRHSNewPair(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RHSCallContext extends Assign_rhsContext {
		public TerminalNode CALL() { return getToken(WACCParser.CALL, 0); }
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public RHSCallContext(Assign_rhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRHSCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RHSExprContext extends Assign_rhsContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RHSExprContext(Assign_rhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRHSExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RHSLiteralContext extends Assign_rhsContext {
		public Array_literContext array_liter() {
			return getRuleContext(Array_literContext.class,0);
		}
		public RHSLiteralContext(Assign_rhsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitRHSLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assign_rhsContext assign_rhs() throws RecognitionException {
		Assign_rhsContext _localctx = new Assign_rhsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assign_rhs);
		int _la;
		try {
			setState(144);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OPEN_PARENTHESES:
			case NOT:
			case LEN:
			case ORD:
			case CHR:
			case MINUS:
			case INT_LIT:
			case PAIR_LIT:
			case BOOL_LIT:
			case CHAR_LIT:
			case STR_LIT:
			case IDENT:
				_localctx = new RHSExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(127);
				expr(0);
				}
				break;
			case OPEN_BRACKET:
				_localctx = new RHSLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(128);
				array_liter();
				}
				break;
			case NEWPAIR:
				_localctx = new RHSNewPairContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(129);
				match(NEWPAIR);
				setState(130);
				match(OPEN_PARENTHESES);
				setState(131);
				expr(0);
				setState(132);
				match(COMMA);
				setState(133);
				expr(0);
				setState(134);
				match(CLOSE_PARENTHESES);
				}
				break;
			case FST:
			case SND:
				_localctx = new RHSPairElemContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(136);
				pair_elem();
				}
				break;
			case CALL:
				_localctx = new RHSCallContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(137);
				match(CALL);
				setState(138);
				match(IDENT);
				setState(139);
				match(OPEN_PARENTHESES);
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPEN_PARENTHESES) | (1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR) | (1L << MINUS) | (1L << INT_LIT) | (1L << PAIR_LIT) | (1L << BOOL_LIT) | (1L << CHAR_LIT) | (1L << STR_LIT) | (1L << IDENT))) != 0)) {
					{
					setState(140);
					arg_list();
					}
				}

				setState(143);
				match(CLOSE_PARENTHESES);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arg_listContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Arg_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_list; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitArg_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arg_listContext arg_list() throws RecognitionException {
		Arg_listContext _localctx = new Arg_listContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			expr(0);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(147);
				match(COMMA);
				setState(148);
				expr(0);
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_elemContext extends ParserRuleContext {
		public Pair_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_elem; }

		public Pair_elemContext() { }
		public void copyFrom(Pair_elemContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PairFstContext extends Pair_elemContext {
		public TerminalNode FST() { return getToken(WACCParser.FST, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PairFstContext(Pair_elemContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPairFst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PairSndContext extends Pair_elemContext {
		public TerminalNode SND() { return getToken(WACCParser.SND, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PairSndContext(Pair_elemContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPairSnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pair_elemContext pair_elem() throws RecognitionException {
		Pair_elemContext _localctx = new Pair_elemContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_pair_elem);
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FST:
				_localctx = new PairFstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				match(FST);
				setState(155);
				expr(0);
				}
				break;
			case SND:
				_localctx = new PairSndContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(156);
				match(SND);
				setState(157);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }

		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TypeBase_typeContext extends TypeContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public TypeBase_typeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitTypeBase_type(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypePair_typeContext extends TypeContext {
		public Pair_typeContext pair_type() {
			return getRuleContext(Pair_typeContext.class,0);
		}
		public TypePair_typeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitTypePair_type(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeArray_typeContext extends TypeContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(WACCParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(WACCParser.CLOSE_BRACKET, 0); }
		public TypeArray_typeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitTypeArray_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case BOOL:
			case CHAR:
			case STRING:
				{
				_localctx = new TypeBase_typeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(161);
				base_type();
				}
				break;
			case PAIR:
				{
				_localctx = new TypePair_typeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(162);
				pair_type();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(170);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TypeArray_typeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(165);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(166);
					match(OPEN_BRACKET);
					setState(167);
					match(CLOSE_BRACKET);
					}
					}
				}
				setState(172);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Base_typeContext extends ParserRuleContext {
		public Base_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base_type; }

		public Base_typeContext() { }
		public void copyFrom(Base_typeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringBase_typeContext extends Base_typeContext {
		public TerminalNode STRING() { return getToken(WACCParser.STRING, 0); }
		public StringBase_typeContext(Base_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitStringBase_type(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntBase_typeContext extends Base_typeContext {
		public TerminalNode INT() { return getToken(WACCParser.INT, 0); }
		public IntBase_typeContext(Base_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitIntBase_type(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CharBase_typeContext extends Base_typeContext {
		public TerminalNode CHAR() { return getToken(WACCParser.CHAR, 0); }
		public CharBase_typeContext(Base_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitCharBase_type(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolBase_typeContext extends Base_typeContext {
		public TerminalNode BOOL() { return getToken(WACCParser.BOOL, 0); }
		public BoolBase_typeContext(Base_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitBoolBase_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Base_typeContext base_type() throws RecognitionException {
		Base_typeContext _localctx = new Base_typeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_base_type);
		try {
			setState(177);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				_localctx = new IntBase_typeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(173);
				match(INT);
				}
				break;
			case BOOL:
				_localctx = new BoolBase_typeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(174);
				match(BOOL);
				}
				break;
			case CHAR:
				_localctx = new CharBase_typeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(175);
				match(CHAR);
				}
				break;
			case STRING:
				_localctx = new StringBase_typeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(176);
				match(STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_typeContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(WACCParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(WACCParser.CLOSE_BRACKET, 0); }
		public Array_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitArray_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Array_typeContext array_type() throws RecognitionException {
		Array_typeContext _localctx = new Array_typeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_array_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			type(0);
			setState(180);
			match(OPEN_BRACKET);
			setState(181);
			match(CLOSE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_typeContext extends ParserRuleContext {
		public TerminalNode PAIR() { return getToken(WACCParser.PAIR, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public List<Pair_elem_typeContext> pair_elem_type() {
			return getRuleContexts(Pair_elem_typeContext.class);
		}
		public Pair_elem_typeContext pair_elem_type(int i) {
			return getRuleContext(Pair_elem_typeContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(WACCParser.COMMA, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public Pair_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPair_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pair_typeContext pair_type() throws RecognitionException {
		Pair_typeContext _localctx = new Pair_typeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pair_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(PAIR);
			setState(184);
			match(OPEN_PARENTHESES);
			setState(185);
			pair_elem_type();
			setState(186);
			match(COMMA);
			setState(187);
			pair_elem_type();
			setState(188);
			match(CLOSE_PARENTHESES);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_elem_typeContext extends ParserRuleContext {
		public Pair_elem_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_elem_type; }

		public Pair_elem_typeContext() { }
		public void copyFrom(Pair_elem_typeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PETArrayTypeContext extends Pair_elem_typeContext {
		public Array_typeContext array_type() {
			return getRuleContext(Array_typeContext.class,0);
		}
		public PETArrayTypeContext(Pair_elem_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPETArrayType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PETBaseTypeContext extends Pair_elem_typeContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public PETBaseTypeContext(Pair_elem_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPETBaseType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PETPairContext extends Pair_elem_typeContext {
		public TerminalNode PAIR() { return getToken(WACCParser.PAIR, 0); }
		public PETPairContext(Pair_elem_typeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitPETPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pair_elem_typeContext pair_elem_type() throws RecognitionException {
		Pair_elem_typeContext _localctx = new Pair_elem_typeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pair_elem_type);
		try {
			setState(193);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new PETBaseTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				base_type();
				}
				break;
			case 2:
				_localctx = new PETArrayTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(191);
				array_type();
				}
				break;
			case 3:
				_localctx = new PETPairContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(192);
				match(PAIR);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }

		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprPairLiterContext extends ExprContext {
		public TerminalNode PAIR_LIT() { return getToken(WACCParser.PAIR_LIT, 0); }
		public ExprPairLiterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprPairLiter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBinaryOperContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Binary_operContext binary_oper() {
			return getRuleContext(Binary_operContext.class,0);
		}
		public ExprBinaryOperContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprBinaryOper(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprStringLiterContext extends ExprContext {
		public TerminalNode STR_LIT() { return getToken(WACCParser.STR_LIT, 0); }
		public ExprStringLiterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprStringLiter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprArrayElemContext extends ExprContext {
		public Array_elemContext array_elem() {
			return getRuleContext(Array_elemContext.class,0);
		}
		public ExprArrayElemContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprArrayElem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprCharLiterContext extends ExprContext {
		public TerminalNode CHAR_LIT() { return getToken(WACCParser.CHAR_LIT, 0); }
		public ExprCharLiterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprCharLiter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BracketExprContext extends ExprContext {
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public BracketExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitBracketExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBoolLiterContext extends ExprContext {
		public TerminalNode BOOL_LIT() { return getToken(WACCParser.BOOL_LIT, 0); }
		public ExprBoolLiterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprBoolLiter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprIntLiterContext extends ExprContext {
		public TerminalNode INT_LIT() { return getToken(WACCParser.INT_LIT, 0); }
		public ExprIntLiterContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprIntLiter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprIdentContext extends ExprContext {
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public ExprIdentContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprIdent(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprUnaryOperContext extends ExprContext {
		public Unary_operContext unary_oper() {
			return getRuleContext(Unary_operContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprUnaryOperContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitExprUnaryOper(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new ExprIntLiterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(196);
				match(INT_LIT);
				}
				break;
			case 2:
				{
				_localctx = new ExprBoolLiterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(197);
				match(BOOL_LIT);
				}
				break;
			case 3:
				{
				_localctx = new ExprCharLiterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(198);
				match(CHAR_LIT);
				}
				break;
			case 4:
				{
				_localctx = new ExprStringLiterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(199);
				match(STR_LIT);
				}
				break;
			case 5:
				{
				_localctx = new ExprPairLiterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(200);
				match(PAIR_LIT);
				}
				break;
			case 6:
				{
				_localctx = new ExprIdentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(201);
				match(IDENT);
				}
				break;
			case 7:
				{
				_localctx = new ExprArrayElemContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(202);
				array_elem();
				}
				break;
			case 8:
				{
				_localctx = new ExprUnaryOperContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(203);
				unary_oper();
				setState(204);
				expr(3);
				}
				break;
			case 9:
				{
				_localctx = new BracketExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(206);
				match(OPEN_PARENTHESES);
				setState(207);
				expr(0);
				setState(208);
				match(CLOSE_PARENTHESES);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(218);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprBinaryOperContext(new ExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(212);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(213);
					binary_oper();
					setState(214);
					expr(3);
					}
					}
				}
				setState(220);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Unary_operContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(WACCParser.NOT, 0); }
		public TerminalNode MINUS() { return getToken(WACCParser.MINUS, 0); }
		public TerminalNode LEN() { return getToken(WACCParser.LEN, 0); }
		public TerminalNode ORD() { return getToken(WACCParser.ORD, 0); }
		public TerminalNode CHR() { return getToken(WACCParser.CHR, 0); }
		public Unary_operContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_oper; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitUnary_oper(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unary_operContext unary_oper() throws RecognitionException {
		Unary_operContext _localctx = new Unary_operContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_unary_oper);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR) | (1L << MINUS))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Binary_operContext extends ParserRuleContext {
		public TerminalNode MULTIPLY() { return getToken(WACCParser.MULTIPLY, 0); }
		public TerminalNode DIVIDE() { return getToken(WACCParser.DIVIDE, 0); }
		public TerminalNode MODULO() { return getToken(WACCParser.MODULO, 0); }
		public TerminalNode PLUS() { return getToken(WACCParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(WACCParser.MINUS, 0); }
		public TerminalNode GT() { return getToken(WACCParser.GT, 0); }
		public TerminalNode GTE() { return getToken(WACCParser.GTE, 0); }
		public TerminalNode LT() { return getToken(WACCParser.LT, 0); }
		public TerminalNode LTE() { return getToken(WACCParser.LTE, 0); }
		public TerminalNode EE() { return getToken(WACCParser.EE, 0); }
		public TerminalNode NE() { return getToken(WACCParser.NE, 0); }
		public TerminalNode AND() { return getToken(WACCParser.AND, 0); }
		public TerminalNode OR() { return getToken(WACCParser.OR, 0); }
		public Binary_operContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary_oper; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitBinary_oper(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Binary_operContext binary_oper() throws RecognitionException {
		Binary_operContext _localctx = new Binary_operContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_binary_oper);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULTIPLY) | (1L << DIVIDE) | (1L << MODULO) | (1L << PLUS) | (1L << MINUS) | (1L << GT) | (1L << GTE) | (1L << LT) | (1L << LTE) | (1L << EE) | (1L << NE) | (1L << AND) | (1L << OR))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_elemContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(WACCParser.IDENT, 0); }
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(WACCParser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(WACCParser.OPEN_BRACKET, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(WACCParser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(WACCParser.CLOSE_BRACKET, i);
		}
		public Array_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_elem; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitArray_elem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Array_elemContext array_elem() throws RecognitionException {
		Array_elemContext _localctx = new Array_elemContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_array_elem);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			match(IDENT);
			setState(230);
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(226);
					match(OPEN_BRACKET);
					setState(227);
					expr(0);
					setState(228);
					match(CLOSE_BRACKET);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(232);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_literContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(WACCParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(WACCParser.CLOSE_BRACKET, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Array_literContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_liter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WACCParserVisitor ) return ((WACCParserVisitor<? extends T>)visitor).visitArray_liter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Array_literContext array_liter() throws RecognitionException {
		Array_literContext _localctx = new Array_literContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_array_liter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			match(OPEN_BRACKET);
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPEN_PARENTHESES) | (1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR) | (1L << MINUS) | (1L << INT_LIT) | (1L << PAIR_LIT) | (1L << BOOL_LIT) | (1L << CHAR_LIT) | (1L << STR_LIT) | (1L << IDENT))) != 0)) {
				{
				setState(235);
				expr(0);
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(236);
					match(COMMA);
					setState(237);
					expr(0);
					}
					}
					setState(242);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(245);
			match(CLOSE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 4:
			return stat_sempred((StatContext)_localctx, predIndex);
		case 9:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 14:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean stat_sempred(StatContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3=\u00fa\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\7\2+\n\2\f\2\16\2.\13\2\3\2\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\5\38\n\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4B\n\4\f\4\16"+
		"\4E\13\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6s\n\6\3\6\3\6\3\6\7\6"+
		"x\n\6\f\6\16\6{\13\6\3\7\3\7\3\7\5\7\u0080\n\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0090\n\b\3\b\5\b\u0093\n\b\3\t\3"+
		"\t\3\t\7\t\u0098\n\t\f\t\16\t\u009b\13\t\3\n\3\n\3\n\3\n\5\n\u00a1\n\n"+
		"\3\13\3\13\3\13\5\13\u00a6\n\13\3\13\3\13\3\13\7\13\u00ab\n\13\f\13\16"+
		"\13\u00ae\13\13\3\f\3\f\3\f\3\f\5\f\u00b4\n\f\3\r\3\r\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\5\17\u00c4\n\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00d5"+
		"\n\20\3\20\3\20\3\20\3\20\7\20\u00db\n\20\f\20\16\20\u00de\13\20\3\21"+
		"\3\21\3\22\3\22\3\23\3\23\3\23\3\23\3\23\6\23\u00e9\n\23\r\23\16\23\u00ea"+
		"\3\24\3\24\3\24\3\24\7\24\u00f1\n\24\f\24\16\24\u00f4\13\24\5\24\u00f6"+
		"\n\24\3\24\3\24\3\24\2\5\n\24\36\25\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&\2\4\4\2%(--\3\2)\65\2\u0111\2(\3\2\2\2\4\63\3\2\2\2\6>\3\2\2"+
		"\2\bF\3\2\2\2\nr\3\2\2\2\f\177\3\2\2\2\16\u0092\3\2\2\2\20\u0094\3\2\2"+
		"\2\22\u00a0\3\2\2\2\24\u00a5\3\2\2\2\26\u00b3\3\2\2\2\30\u00b5\3\2\2\2"+
		"\32\u00b9\3\2\2\2\34\u00c3\3\2\2\2\36\u00d4\3\2\2\2 \u00df\3\2\2\2\"\u00e1"+
		"\3\2\2\2$\u00e3\3\2\2\2&\u00ec\3\2\2\2(,\7\4\2\2)+\5\4\3\2*)\3\2\2\2+"+
		".\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2\2.,\3\2\2\2/\60\5\n\6\2\60\61\7"+
		"\5\2\2\61\62\7\2\2\3\62\3\3\2\2\2\63\64\5\24\13\2\64\65\7<\2\2\65\67\7"+
		"\36\2\2\668\5\6\4\2\67\66\3\2\2\2\678\3\2\2\289\3\2\2\29:\7\37\2\2:;\7"+
		"\6\2\2;<\5\n\6\2<=\7\5\2\2=\5\3\2\2\2>C\5\b\5\2?@\7 \2\2@B\5\b\5\2A?\3"+
		"\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2D\7\3\2\2\2EC\3\2\2\2FG\5\24\13\2"+
		"GH\7<\2\2H\t\3\2\2\2IJ\b\6\1\2Js\7\7\2\2KL\5\24\13\2LM\7<\2\2MN\7!\2\2"+
		"NO\5\16\b\2Os\3\2\2\2PQ\5\f\7\2QR\7!\2\2RS\5\16\b\2Ss\3\2\2\2TU\7\b\2"+
		"\2Us\5\f\7\2VW\7\t\2\2Ws\5\36\20\2XY\7\n\2\2Ys\5\36\20\2Z[\7\13\2\2[s"+
		"\5\36\20\2\\]\7\f\2\2]s\5\36\20\2^_\7\r\2\2_s\5\36\20\2`a\7\16\2\2ab\5"+
		"\36\20\2bc\7\17\2\2cd\5\n\6\2de\7\20\2\2ef\5\n\6\2fg\7\21\2\2gs\3\2\2"+
		"\2hi\7\22\2\2ij\5\36\20\2jk\7\23\2\2kl\5\n\6\2lm\7\24\2\2ms\3\2\2\2no"+
		"\7\4\2\2op\5\n\6\2pq\7\5\2\2qs\3\2\2\2rI\3\2\2\2rK\3\2\2\2rP\3\2\2\2r"+
		"T\3\2\2\2rV\3\2\2\2rX\3\2\2\2rZ\3\2\2\2r\\\3\2\2\2r^\3\2\2\2r`\3\2\2\2"+
		"rh\3\2\2\2rn\3\2\2\2sy\3\2\2\2tu\f\3\2\2uv\7\"\2\2vx\5\n\6\4wt\3\2\2\2"+
		"x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z\13\3\2\2\2{y\3\2\2\2|\u0080\7<\2\2}\u0080"+
		"\5$\23\2~\u0080\5\22\n\2\177|\3\2\2\2\177}\3\2\2\2\177~\3\2\2\2\u0080"+
		"\r\3\2\2\2\u0081\u0093\5\36\20\2\u0082\u0093\5&\24\2\u0083\u0084\7\25"+
		"\2\2\u0084\u0085\7\36\2\2\u0085\u0086\5\36\20\2\u0086\u0087\7 \2\2\u0087"+
		"\u0088\5\36\20\2\u0088\u0089\7\37\2\2\u0089\u0093\3\2\2\2\u008a\u0093"+
		"\5\22\n\2\u008b\u008c\7\26\2\2\u008c\u008d\7<\2\2\u008d\u008f\7\36\2\2"+
		"\u008e\u0090\5\20\t\2\u008f\u008e\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091"+
		"\3\2\2\2\u0091\u0093\7\37\2\2\u0092\u0081\3\2\2\2\u0092\u0082\3\2\2\2"+
		"\u0092\u0083\3\2\2\2\u0092\u008a\3\2\2\2\u0092\u008b\3\2\2\2\u0093\17"+
		"\3\2\2\2\u0094\u0099\5\36\20\2\u0095\u0096\7 \2\2\u0096\u0098\5\36\20"+
		"\2\u0097\u0095\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a"+
		"\3\2\2\2\u009a\21\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\7\27\2\2\u009d"+
		"\u00a1\5\36\20\2\u009e\u009f\7\30\2\2\u009f\u00a1\5\36\20\2\u00a0\u009c"+
		"\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\23\3\2\2\2\u00a2\u00a3\b\13\1\2\u00a3"+
		"\u00a6\5\26\f\2\u00a4\u00a6\5\32\16\2\u00a5\u00a2\3\2\2\2\u00a5\u00a4"+
		"\3\2\2\2\u00a6\u00ac\3\2\2\2\u00a7\u00a8\f\4\2\2\u00a8\u00a9\7#\2\2\u00a9"+
		"\u00ab\7$\2\2\u00aa\u00a7\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2"+
		"\2\2\u00ac\u00ad\3\2\2\2\u00ad\25\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b4"+
		"\7\31\2\2\u00b0\u00b4\7\32\2\2\u00b1\u00b4\7\33\2\2\u00b2\u00b4\7\34\2"+
		"\2\u00b3\u00af\3\2\2\2\u00b3\u00b0\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b2"+
		"\3\2\2\2\u00b4\27\3\2\2\2\u00b5\u00b6\5\24\13\2\u00b6\u00b7\7#\2\2\u00b7"+
		"\u00b8\7$\2\2\u00b8\31\3\2\2\2\u00b9\u00ba\7\35\2\2\u00ba\u00bb\7\36\2"+
		"\2\u00bb\u00bc\5\34\17\2\u00bc\u00bd\7 \2\2\u00bd\u00be\5\34\17\2\u00be"+
		"\u00bf\7\37\2\2\u00bf\33\3\2\2\2\u00c0\u00c4\5\26\f\2\u00c1\u00c4\5\30"+
		"\r\2\u00c2\u00c4\7\35\2\2\u00c3\u00c0\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3"+
		"\u00c2\3\2\2\2\u00c4\35\3\2\2\2\u00c5\u00c6\b\20\1\2\u00c6\u00d5\7\67"+
		"\2\2\u00c7\u00d5\79\2\2\u00c8\u00d5\7:\2\2\u00c9\u00d5\7;\2\2\u00ca\u00d5"+
		"\78\2\2\u00cb\u00d5\7<\2\2\u00cc\u00d5\5$\23\2\u00cd\u00ce\5 \21\2\u00ce"+
		"\u00cf\5\36\20\5\u00cf\u00d5\3\2\2\2\u00d0\u00d1\7\36\2\2\u00d1\u00d2"+
		"\5\36\20\2\u00d2\u00d3\7\37\2\2\u00d3\u00d5\3\2\2\2\u00d4\u00c5\3\2\2"+
		"\2\u00d4\u00c7\3\2\2\2\u00d4\u00c8\3\2\2\2\u00d4\u00c9\3\2\2\2\u00d4\u00ca"+
		"\3\2\2\2\u00d4\u00cb\3\2\2\2\u00d4\u00cc\3\2\2\2\u00d4\u00cd\3\2\2\2\u00d4"+
		"\u00d0\3\2\2\2\u00d5\u00dc\3\2\2\2\u00d6\u00d7\f\4\2\2\u00d7\u00d8\5\""+
		"\22\2\u00d8\u00d9\5\36\20\5\u00d9\u00db\3\2\2\2\u00da\u00d6\3\2\2\2\u00db"+
		"\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\37\3\2\2"+
		"\2\u00de\u00dc\3\2\2\2\u00df\u00e0\t\2\2\2\u00e0!\3\2\2\2\u00e1\u00e2"+
		"\t\3\2\2\u00e2#\3\2\2\2\u00e3\u00e8\7<\2\2\u00e4\u00e5\7#\2\2\u00e5\u00e6"+
		"\5\36\20\2\u00e6\u00e7\7$\2\2\u00e7\u00e9\3\2\2\2\u00e8\u00e4\3\2\2\2"+
		"\u00e9\u00ea\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb%\3"+
		"\2\2\2\u00ec\u00f5\7#\2\2\u00ed\u00f2\5\36\20\2\u00ee\u00ef\7 \2\2\u00ef"+
		"\u00f1\5\36\20\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3"+
		"\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5"+
		"\u00ed\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\7$"+
		"\2\2\u00f8\'\3\2\2\2\25,\67Cry\177\u008f\u0092\u0099\u00a0\u00a5\u00ac"+
		"\u00b3\u00c3\u00d4\u00dc\u00ea\u00f2\u00f5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}