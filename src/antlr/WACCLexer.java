// Generated from ./WACCLexer.g4 by ANTLR 4.7
package antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCLexer extends Lexer {
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
		GT=44, GTE=45, LT=46, LTE=47, EE=48, NE=49, AND=50, OR=51, IDENT=52, SIGN=53, 
		INT_LIT=54, PAIR_LIT=55, BOOL_LIT=56, CHAR_LIT=57, STR_LIT=58, PAIT_LIT=59, 
		COMMENT=60;
	public static final int
		COMMENTS=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN", "COMMENTS"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"IGNORE", "BEGIN", "END", "IS", "SKIP_", "READ", "FREE", "RETURN", "EXIT", 
		"PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", 
		"NEWPAIR", "CALL", "FST", "SND", "INT", "BOOL", "CHAR", "STRING", "PAIR", 
		"OPEN_PARENTHESES", "CLOSE_PARENTHESES", "COMMA", "EQUALS", "SEMICOLON", 
		"OPEN_BRACKET", "CLOSE_BRACKET", "NOT", "LEN", "ORD", "CHR", "MULTIPLY", 
		"DIVIDE", "MODULO", "PLUS", "MINUS", "GT", "GTE", "LT", "LTE", "EE", "NE", 
		"AND", "OR", "UNDERSCORE", "DIGIT", "LOWERCASE", "UPPERCASE", "IDENT", 
		"SIGN", "INT_LIT", "PAIR_LIT", "TRUE", "FALSE", "BOOL_LIT", "SINGLEQUOTE", 
		"DOUBLEQUOTE", "SLASH", "ESCAPED_CHAR", "CHARACTER", "CHAR_LIT", "STR_LIT", 
		"PAIT_LIT", "EOL", "COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'begin'", "'end'", "'is'", "'skip'", "'read'", "'free'", 
		"'return'", "'exit'", "'print'", "'println'", "'if'", "'then'", "'else'", 
		"'fi'", "'while'", "'do'", "'done'", "'newpair'", "'call'", "'fst'", "'snd'", 
		"'int'", "'bool'", "'char'", "'string'", "'pair'", "'('", "')'", "','", 
		"'='", "';'", "'['", "']'", "'!'", "'len'", "'ord'", "'chr'", "'*'", "'/'", 
		"'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", "'=='", "'!='", "'&&'", 
		"'||'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "IGNORE", "BEGIN", "END", "IS", "SKIP_", "READ", "FREE", "RETURN", 
		"EXIT", "PRINT", "PRINTLN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", 
		"DONE", "NEWPAIR", "CALL", "FST", "SND", "INT", "BOOL", "CHAR", "STRING", 
		"PAIR", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "COMMA", "EQUALS", "SEMICOLON", 
		"OPEN_BRACKET", "CLOSE_BRACKET", "NOT", "LEN", "ORD", "CHR", "MULTIPLY", 
		"DIVIDE", "MODULO", "PLUS", "MINUS", "GT", "GTE", "LT", "LTE", "EE", "NE", 
		"AND", "OR", "IDENT", "SIGN", "INT_LIT", "PAIR_LIT", "BOOL_LIT", "CHAR_LIT", 
		"STR_LIT", "PAIT_LIT", "COMMENT"
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


	public WACCLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WACCLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2>\u01c0\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\3\2\6\2\u0095\n\2\r\2\16\2\u0096\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31"+
		"\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34"+
		"\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\""+
		"\3#\3#\3$\3$\3%\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3)\3)\3*\3"+
		"*\3+\3+\3,\3,\3-\3-\3.\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62"+
		"\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67"+
		"\38\38\39\39\39\59\u0165\n9\39\39\39\39\79\u016b\n9\f9\169\u016e\139\3"+
		":\3:\5:\u0172\n:\3;\5;\u0175\n;\3;\6;\u0178\n;\r;\16;\u0179\3<\3<\3<\3"+
		"<\3<\3=\3=\3=\3=\3=\3>\3>\3>\3>\3>\3>\3?\3?\5?\u018e\n?\3@\3@\3A\3A\3"+
		"B\3B\3C\3C\3C\3C\5C\u019a\nC\3D\3D\3D\3D\5D\u01a0\nD\3E\3E\3E\3E\3F\3"+
		"F\7F\u01a8\nF\fF\16F\u01ab\13F\3F\3F\3G\3G\3G\3G\3G\3H\3H\3I\3I\7I\u01b8"+
		"\nI\fI\16I\u01bb\13I\3I\3I\3I\3I\2\2J\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.["+
		"/]\60_\61a\62c\63e\64g\65i\2k\2m\2o\2q\66s\67u8w9y\2{\2}:\177\2\u0081"+
		"\2\u0083\2\u0085\2\u0087\2\u0089;\u008b<\u008d=\u008f\2\u0091>\3\2\6\5"+
		"\2\13\f\17\17\"\"\b\2\62\62ddhhppttvv\6\2$$))^^``\4\2\f\f\17\17\2\u01c4"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2"+
		"\2\2w\3\2\2\2\2}\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u0091\3\2\2\2\3\u0094\3\2\2\2\5\u009a\3\2\2\2\7\u00a0\3\2\2\2\t\u00a4"+
		"\3\2\2\2\13\u00a7\3\2\2\2\r\u00ac\3\2\2\2\17\u00b1\3\2\2\2\21\u00b6\3"+
		"\2\2\2\23\u00bd\3\2\2\2\25\u00c2\3\2\2\2\27\u00c8\3\2\2\2\31\u00d0\3\2"+
		"\2\2\33\u00d3\3\2\2\2\35\u00d8\3\2\2\2\37\u00dd\3\2\2\2!\u00e0\3\2\2\2"+
		"#\u00e6\3\2\2\2%\u00e9\3\2\2\2\'\u00ee\3\2\2\2)\u00f6\3\2\2\2+\u00fb\3"+
		"\2\2\2-\u00ff\3\2\2\2/\u0103\3\2\2\2\61\u0107\3\2\2\2\63\u010c\3\2\2\2"+
		"\65\u0111\3\2\2\2\67\u0118\3\2\2\29\u011d\3\2\2\2;\u011f\3\2\2\2=\u0121"+
		"\3\2\2\2?\u0123\3\2\2\2A\u0125\3\2\2\2C\u0127\3\2\2\2E\u0129\3\2\2\2G"+
		"\u012b\3\2\2\2I\u012d\3\2\2\2K\u0131\3\2\2\2M\u0135\3\2\2\2O\u0139\3\2"+
		"\2\2Q\u013b\3\2\2\2S\u013d\3\2\2\2U\u013f\3\2\2\2W\u0141\3\2\2\2Y\u0143"+
		"\3\2\2\2[\u0145\3\2\2\2]\u0148\3\2\2\2_\u014a\3\2\2\2a\u014d\3\2\2\2c"+
		"\u0150\3\2\2\2e\u0153\3\2\2\2g\u0156\3\2\2\2i\u0159\3\2\2\2k\u015b\3\2"+
		"\2\2m\u015d\3\2\2\2o\u015f\3\2\2\2q\u0164\3\2\2\2s\u0171\3\2\2\2u\u0174"+
		"\3\2\2\2w\u017b\3\2\2\2y\u0180\3\2\2\2{\u0185\3\2\2\2}\u018d\3\2\2\2\177"+
		"\u018f\3\2\2\2\u0081\u0191\3\2\2\2\u0083\u0193\3\2\2\2\u0085\u0199\3\2"+
		"\2\2\u0087\u019f\3\2\2\2\u0089\u01a1\3\2\2\2\u008b\u01a5\3\2\2\2\u008d"+
		"\u01ae\3\2\2\2\u008f\u01b3\3\2\2\2\u0091\u01b5\3\2\2\2\u0093\u0095\t\2"+
		"\2\2\u0094\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0094\3\2\2\2\u0096"+
		"\u0097\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0099\b\2\2\2\u0099\4\3\2\2\2"+
		"\u009a\u009b\7d\2\2\u009b\u009c\7g\2\2\u009c\u009d\7i\2\2\u009d\u009e"+
		"\7k\2\2\u009e\u009f\7p\2\2\u009f\6\3\2\2\2\u00a0\u00a1\7g\2\2\u00a1\u00a2"+
		"\7p\2\2\u00a2\u00a3\7f\2\2\u00a3\b\3\2\2\2\u00a4\u00a5\7k\2\2\u00a5\u00a6"+
		"\7u\2\2\u00a6\n\3\2\2\2\u00a7\u00a8\7u\2\2\u00a8\u00a9\7m\2\2\u00a9\u00aa"+
		"\7k\2\2\u00aa\u00ab\7r\2\2\u00ab\f\3\2\2\2\u00ac\u00ad\7t\2\2\u00ad\u00ae"+
		"\7g\2\2\u00ae\u00af\7c\2\2\u00af\u00b0\7f\2\2\u00b0\16\3\2\2\2\u00b1\u00b2"+
		"\7h\2\2\u00b2\u00b3\7t\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5\7g\2\2\u00b5"+
		"\20\3\2\2\2\u00b6\u00b7\7t\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7v\2\2\u00b9"+
		"\u00ba\7w\2\2\u00ba\u00bb\7t\2\2\u00bb\u00bc\7p\2\2\u00bc\22\3\2\2\2\u00bd"+
		"\u00be\7g\2\2\u00be\u00bf\7z\2\2\u00bf\u00c0\7k\2\2\u00c0\u00c1\7v\2\2"+
		"\u00c1\24\3\2\2\2\u00c2\u00c3\7r\2\2\u00c3\u00c4\7t\2\2\u00c4\u00c5\7"+
		"k\2\2\u00c5\u00c6\7p\2\2\u00c6\u00c7\7v\2\2\u00c7\26\3\2\2\2\u00c8\u00c9"+
		"\7r\2\2\u00c9\u00ca\7t\2\2\u00ca\u00cb\7k\2\2\u00cb\u00cc\7p\2\2\u00cc"+
		"\u00cd\7v\2\2\u00cd\u00ce\7n\2\2\u00ce\u00cf\7p\2\2\u00cf\30\3\2\2\2\u00d0"+
		"\u00d1\7k\2\2\u00d1\u00d2\7h\2\2\u00d2\32\3\2\2\2\u00d3\u00d4\7v\2\2\u00d4"+
		"\u00d5\7j\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7\7p\2\2\u00d7\34\3\2\2\2\u00d8"+
		"\u00d9\7g\2\2\u00d9\u00da\7n\2\2\u00da\u00db\7u\2\2\u00db\u00dc\7g\2\2"+
		"\u00dc\36\3\2\2\2\u00dd\u00de\7h\2\2\u00de\u00df\7k\2\2\u00df \3\2\2\2"+
		"\u00e0\u00e1\7y\2\2\u00e1\u00e2\7j\2\2\u00e2\u00e3\7k\2\2\u00e3\u00e4"+
		"\7n\2\2\u00e4\u00e5\7g\2\2\u00e5\"\3\2\2\2\u00e6\u00e7\7f\2\2\u00e7\u00e8"+
		"\7q\2\2\u00e8$\3\2\2\2\u00e9\u00ea\7f\2\2\u00ea\u00eb\7q\2\2\u00eb\u00ec"+
		"\7p\2\2\u00ec\u00ed\7g\2\2\u00ed&\3\2\2\2\u00ee\u00ef\7p\2\2\u00ef\u00f0"+
		"\7g\2\2\u00f0\u00f1\7y\2\2\u00f1\u00f2\7r\2\2\u00f2\u00f3\7c\2\2\u00f3"+
		"\u00f4\7k\2\2\u00f4\u00f5\7t\2\2\u00f5(\3\2\2\2\u00f6\u00f7\7e\2\2\u00f7"+
		"\u00f8\7c\2\2\u00f8\u00f9\7n\2\2\u00f9\u00fa\7n\2\2\u00fa*\3\2\2\2\u00fb"+
		"\u00fc\7h\2\2\u00fc\u00fd\7u\2\2\u00fd\u00fe\7v\2\2\u00fe,\3\2\2\2\u00ff"+
		"\u0100\7u\2\2\u0100\u0101\7p\2\2\u0101\u0102\7f\2\2\u0102.\3\2\2\2\u0103"+
		"\u0104\7k\2\2\u0104\u0105\7p\2\2\u0105\u0106\7v\2\2\u0106\60\3\2\2\2\u0107"+
		"\u0108\7d\2\2\u0108\u0109\7q\2\2\u0109\u010a\7q\2\2\u010a\u010b\7n\2\2"+
		"\u010b\62\3\2\2\2\u010c\u010d\7e\2\2\u010d\u010e\7j\2\2\u010e\u010f\7"+
		"c\2\2\u010f\u0110\7t\2\2\u0110\64\3\2\2\2\u0111\u0112\7u\2\2\u0112\u0113"+
		"\7v\2\2\u0113\u0114\7t\2\2\u0114\u0115\7k\2\2\u0115\u0116\7p\2\2\u0116"+
		"\u0117\7i\2\2\u0117\66\3\2\2\2\u0118\u0119\7r\2\2\u0119\u011a\7c\2\2\u011a"+
		"\u011b\7k\2\2\u011b\u011c\7t\2\2\u011c8\3\2\2\2\u011d\u011e\7*\2\2\u011e"+
		":\3\2\2\2\u011f\u0120\7+\2\2\u0120<\3\2\2\2\u0121\u0122\7.\2\2\u0122>"+
		"\3\2\2\2\u0123\u0124\7?\2\2\u0124@\3\2\2\2\u0125\u0126\7=\2\2\u0126B\3"+
		"\2\2\2\u0127\u0128\7]\2\2\u0128D\3\2\2\2\u0129\u012a\7_\2\2\u012aF\3\2"+
		"\2\2\u012b\u012c\7#\2\2\u012cH\3\2\2\2\u012d\u012e\7n\2\2\u012e\u012f"+
		"\7g\2\2\u012f\u0130\7p\2\2\u0130J\3\2\2\2\u0131\u0132\7q\2\2\u0132\u0133"+
		"\7t\2\2\u0133\u0134\7f\2\2\u0134L\3\2\2\2\u0135\u0136\7e\2\2\u0136\u0137"+
		"\7j\2\2\u0137\u0138\7t\2\2\u0138N\3\2\2\2\u0139\u013a\7,\2\2\u013aP\3"+
		"\2\2\2\u013b\u013c\7\61\2\2\u013cR\3\2\2\2\u013d\u013e\7\'\2\2\u013eT"+
		"\3\2\2\2\u013f\u0140\7-\2\2\u0140V\3\2\2\2\u0141\u0142\7/\2\2\u0142X\3"+
		"\2\2\2\u0143\u0144\7@\2\2\u0144Z\3\2\2\2\u0145\u0146\7@\2\2\u0146\u0147"+
		"\7?\2\2\u0147\\\3\2\2\2\u0148\u0149\7>\2\2\u0149^\3\2\2\2\u014a\u014b"+
		"\7>\2\2\u014b\u014c\7?\2\2\u014c`\3\2\2\2\u014d\u014e\7?\2\2\u014e\u014f"+
		"\7?\2\2\u014fb\3\2\2\2\u0150\u0151\7#\2\2\u0151\u0152\7?\2\2\u0152d\3"+
		"\2\2\2\u0153\u0154\7(\2\2\u0154\u0155\7(\2\2\u0155f\3\2\2\2\u0156\u0157"+
		"\7~\2\2\u0157\u0158\7~\2\2\u0158h\3\2\2\2\u0159\u015a\7a\2\2\u015aj\3"+
		"\2\2\2\u015b\u015c\4\62;\2\u015cl\3\2\2\2\u015d\u015e\4c|\2\u015en\3\2"+
		"\2\2\u015f\u0160\4C\\\2\u0160p\3\2\2\2\u0161\u0165\5i\65\2\u0162\u0165"+
		"\5m\67\2\u0163\u0165\5o8\2\u0164\u0161\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0163\3\2\2\2\u0165\u016c\3\2\2\2\u0166\u016b\5i\65\2\u0167\u016b\5m"+
		"\67\2\u0168\u016b\5o8\2\u0169\u016b\5k\66\2\u016a\u0166\3\2\2\2\u016a"+
		"\u0167\3\2\2\2\u016a\u0168\3\2\2\2\u016a\u0169\3\2\2\2\u016b\u016e\3\2"+
		"\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016dr\3\2\2\2\u016e\u016c"+
		"\3\2\2\2\u016f\u0172\5U+\2\u0170\u0172\5W,\2\u0171\u016f\3\2\2\2\u0171"+
		"\u0170\3\2\2\2\u0172t\3\2\2\2\u0173\u0175\5s:\2\u0174\u0173\3\2\2\2\u0174"+
		"\u0175\3\2\2\2\u0175\u0177\3\2\2\2\u0176\u0178\5k\66\2\u0177\u0176\3\2"+
		"\2\2\u0178\u0179\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a"+
		"v\3\2\2\2\u017b\u017c\7p\2\2\u017c\u017d\7w\2\2\u017d\u017e\7n\2\2\u017e"+
		"\u017f\7n\2\2\u017fx\3\2\2\2\u0180\u0181\7v\2\2\u0181\u0182\7t\2\2\u0182"+
		"\u0183\7w\2\2\u0183\u0184\7g\2\2\u0184z\3\2\2\2\u0185\u0186\7h\2\2\u0186"+
		"\u0187\7c\2\2\u0187\u0188\7n\2\2\u0188\u0189\7u\2\2\u0189\u018a\7g\2\2"+
		"\u018a|\3\2\2\2\u018b\u018e\5y=\2\u018c\u018e\5{>\2\u018d\u018b\3\2\2"+
		"\2\u018d\u018c\3\2\2\2\u018e~\3\2\2\2\u018f\u0190\7)\2\2\u0190\u0080\3"+
		"\2\2\2\u0191\u0192\7$\2\2\u0192\u0082\3\2\2\2\u0193\u0194\7^\2\2\u0194"+
		"\u0084\3\2\2\2\u0195\u019a\t\3\2\2\u0196\u019a\5\u0081A\2\u0197\u019a"+
		"\5\177@\2\u0198\u019a\5\u0083B\2\u0199\u0195\3\2\2\2\u0199\u0196\3\2\2"+
		"\2\u0199\u0197\3\2\2\2\u0199\u0198\3\2\2\2\u019a\u0086\3\2\2\2\u019b\u01a0"+
		"\t\4\2\2\u019c\u019d\5\u0083B\2\u019d\u019e\5\u0085C\2\u019e\u01a0\3\2"+
		"\2\2\u019f\u019b\3\2\2\2\u019f\u019c\3\2\2\2\u01a0\u0088\3\2\2\2\u01a1"+
		"\u01a2\5\177@\2\u01a2\u01a3\5\u0087D\2\u01a3\u01a4\5\177@\2\u01a4\u008a"+
		"\3\2\2\2\u01a5\u01a9\5\u0081A\2\u01a6\u01a8\5\u0087D\2\u01a7\u01a6\3\2"+
		"\2\2\u01a8\u01ab\3\2\2\2\u01a9\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa"+
		"\u01ac\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ac\u01ad\5\u0081A\2\u01ad\u008c"+
		"\3\2\2\2\u01ae\u01af\7p\2\2\u01af\u01b0\7w\2\2\u01b0\u01b1\7n\2\2\u01b1"+
		"\u01b2\7n\2\2\u01b2\u008e\3\2\2\2\u01b3\u01b4\t\5\2\2\u01b4\u0090\3\2"+
		"\2\2\u01b5\u01b9\7%\2\2\u01b6\u01b8\n\5\2\2\u01b7\u01b6\3\2\2\2\u01b8"+
		"\u01bb\3\2\2\2\u01b9\u01b7\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba\u01bc\3\2"+
		"\2\2\u01bb\u01b9\3\2\2\2\u01bc\u01bd\5\u008fH\2\u01bd\u01be\3\2\2\2\u01be"+
		"\u01bf\bI\3\2\u01bf\u0092\3\2\2\2\17\2\u0096\u0164\u016a\u016c\u0171\u0174"+
		"\u0179\u018d\u0199\u019f\u01a9\u01b9\4\b\2\2\2\4\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}