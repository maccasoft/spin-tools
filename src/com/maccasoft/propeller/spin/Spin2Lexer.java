// Generated from Spin2Lexer.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Spin2Lexer extends Spin2LexerBase {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INDENT=1, DEDENT=2, BLOCK_COMMENT=3, COMMENT=4, NL=5, WS=6, STRING=7, 
		QUAD=8, BIN=9, HEX=10, NUMBER=11, LOGICAL_AND=12, LOGICAL_OR=13, LOGICAL_XOR=14, 
		POUND_POUND=15, LEFT_SHIFT=16, RIGHT_SHIFT=17, PLUS_PLUS=18, MINUS_MINUS=19, 
		ASSIGN=20, ADD_ASSIGN=21, ELLIPSIS=22, BIN_AND=23, BIN_OR=24, BIN_XOR=25, 
		AT=26, POUND=27, DOLLAR=28, PERCENT=29, EQUAL=30, DOT=31, COMMA=32, BACKSLASH=33, 
		PLUS=34, MINUS=35, STAR=36, DIV=37, QUESTION=38, COLON=39, TILDE=40, UNDERSCORE=41, 
		OPEN_BRACKET=42, CLOSE_BRACKET=43, OPEN_PAREN=44, CLOSE_PAREN=45, CON_START=46, 
		VAR_START=47, OBJ_START=48, PUB_START=49, PRI_START=50, DAT_START=51, 
		REPEAT=52, FROM=53, TO=54, STEP=55, WHILE=56, UNTIL=57, ELSEIFNOT=58, 
		ELSEIF=59, ELSE=60, IFNOT=61, IF=62, CASE=63, OTHER=64, ADDPINS=65, ADDBITS=66, 
		FUNCTIONS=67, ORG=68, ORGH=69, RES=70, ALIGN=71, TYPE=72, CONDITION=73, 
		IDENTIFIER=74;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LETTER", "DIGIT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", 
			"POUND_POUND", "LEFT_SHIFT", "RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", 
			"ASSIGN", "ADD_ASSIGN", "ELLIPSIS", "BIN_AND", "BIN_OR", "BIN_XOR", "AT", 
			"POUND", "DOLLAR", "PERCENT", "EQUAL", "DOT", "COMMA", "BACKSLASH", "PLUS", 
			"MINUS", "STAR", "DIV", "QUESTION", "COLON", "TILDE", "UNDERSCORE", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "CON_START", "VAR_START", 
			"OBJ_START", "PUB_START", "PRI_START", "DAT_START", "REPEAT", "FROM", 
			"TO", "STEP", "WHILE", "UNTIL", "ELSEIFNOT", "ELSEIF", "ELSE", "IFNOT", 
			"IF", "CASE", "OTHER", "ADDPINS", "ADDBITS", "FUNCTIONS", "ORG", "ORGH", 
			"RES", "ALIGN", "TYPE", "CONDITION", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'&&'", "'||'", "'^^'", "'##'", "'<<'", "'>>'", "'++'", "'--'", "':='", 
			"'+='", "'..'", "'&'", "'|'", "'^'", "'@'", "'#'", "'$'", "'%'", "'='", 
			"'.'", "','", "'\\'", "'+'", "'-'", "'*'", "'/'", "'?'", "':'", "'~'", 
			"'_'", "'['", "']'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", 
			"POUND_POUND", "LEFT_SHIFT", "RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", 
			"ASSIGN", "ADD_ASSIGN", "ELLIPSIS", "BIN_AND", "BIN_OR", "BIN_XOR", "AT", 
			"POUND", "DOLLAR", "PERCENT", "EQUAL", "DOT", "COMMA", "BACKSLASH", "PLUS", 
			"MINUS", "STAR", "DIV", "QUESTION", "COLON", "TILDE", "UNDERSCORE", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "CON_START", "VAR_START", 
			"OBJ_START", "PUB_START", "PRI_START", "DAT_START", "REPEAT", "FROM", 
			"TO", "STEP", "WHILE", "UNTIL", "ELSEIFNOT", "ELSEIF", "ELSE", "IFNOT", 
			"IF", "CASE", "OTHER", "ADDPINS", "ADDBITS", "FUNCTIONS", "ORG", "ORGH", 
			"RES", "ALIGN", "TYPE", "CONDITION", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public Spin2Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Spin2Lexer.g4"; }

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

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 4:
			NL_action((RuleContext)_localctx, actionIndex);
			break;
		case 5:
			WS_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void NL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 HandleNewLine(); 
			break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 HandleSpaces(); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2L\u02b7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\3\2\3\2\3\3\3\3\3\4\3\4\7\4\u009e\n\4\f\4\16\4\u00a1\13"+
		"\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5\u00a9\n\5\f\5\16\5\u00ac\13\5\3\5\3\5\3"+
		"\6\6\6\u00b1\n\6\r\6\16\6\u00b2\3\6\3\6\3\7\6\7\u00b8\n\7\r\7\16\7\u00b9"+
		"\3\7\3\7\3\7\3\7\3\b\3\b\7\b\u00c2\n\b\f\b\16\b\u00c5\13\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\7\t\u00cf\n\t\f\t\16\t\u00d2\13\t\3\n\3\n\3\n\3\n"+
		"\7\n\u00d8\n\n\f\n\16\n\u00db\13\n\3\13\3\13\3\13\5\13\u00e0\n\13\3\13"+
		"\3\13\3\13\7\13\u00e5\n\13\f\13\16\13\u00e8\13\13\3\f\3\f\3\f\3\f\3\f"+
		"\7\f\u00ef\n\f\f\f\16\f\u00f2\13\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3"+
		" \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3"+
		"+\3,\3,\3-\3-\3.\3.\3/\3/\3/\3/\3/\3/\5/\u0149\n/\3\60\3\60\3\60\3\60"+
		"\3\60\3\60\5\60\u0151\n\60\3\61\3\61\3\61\3\61\3\61\3\61\5\61\u0159\n"+
		"\61\3\62\3\62\3\62\3\62\3\62\3\62\5\62\u0161\n\62\3\63\3\63\3\63\3\63"+
		"\3\63\3\63\5\63\u0169\n\63\3\64\3\64\3\64\3\64\3\64\3\64\5\64\u0171\n"+
		"\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\5\65\u017f"+
		"\n\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u0189\n\66\3\67\3\67"+
		"\3\67\3\67\5\67\u018f\n\67\38\38\38\38\38\38\38\38\58\u0199\n8\39\39\3"+
		"9\39\39\39\39\39\39\39\59\u01a5\n9\3:\3:\3:\3:\3:\3:\3:\3:\3:\3:\5:\u01b1"+
		"\n:\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\5;\u01c5\n;"+
		"\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\5<\u01d3\n<\3=\3=\3=\3=\3=\3=\3="+
		"\3=\5=\u01dd\n=\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\5>\u01e9\n>\3?\3?\3?\3?"+
		"\5?\u01ef\n?\3@\3@\3@\3@\3@\3@\3@\3@\5@\u01f9\n@\3A\3A\3A\3A\3A\3A\3A"+
		"\3A\3A\3A\5A\u0205\nA\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\5B\u0215"+
		"\nB\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C\u0225\nC\3D\3D\3D\3D"+
		"\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D"+
		"\3D\3D\3D\5D\u0245\nD\3E\3E\3E\3E\3E\3E\5E\u024d\nE\3F\3F\3F\3F\3F\3F"+
		"\3F\3F\5F\u0257\nF\3G\3G\3G\3G\3G\3G\5G\u025f\nG\3H\3H\3H\3H\3H\3H\3H"+
		"\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\5H\u0279\nH\3I\3I"+
		"\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\3I\5I"+
		"\u0293\nI\3J\3J\3J\3J\3J\3J\5J\u029b\nJ\3J\3J\7J\u029f\nJ\fJ\16J\u02a2"+
		"\13J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\5J\u02ae\nJ\3K\3K\3K\7K\u02b3\nK\f"+
		"K\16K\u02b6\13K\4\u009f\u00c3\2L\3\2\5\2\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60"+
		"_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085"+
		"D\u0087E\u0089F\u008bG\u008dH\u008fI\u0091J\u0093K\u0095L\3\2\6\5\2C\\"+
		"aac|\3\2\62;\4\2\f\f\17\17\4\2\13\13\"\"\2\u02f1\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3"+
		"\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2"+
		"\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2"+
		"w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2"+
		"\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\3\u0097\3\2\2\2\5\u0099\3\2\2\2\7\u009b\3\2\2\2\t\u00a6"+
		"\3\2\2\2\13\u00b0\3\2\2\2\r\u00b7\3\2\2\2\17\u00bf\3\2\2\2\21\u00c8\3"+
		"\2\2\2\23\u00d3\3\2\2\2\25\u00dc\3\2\2\2\27\u00e9\3\2\2\2\31\u00f3\3\2"+
		"\2\2\33\u00f6\3\2\2\2\35\u00f9\3\2\2\2\37\u00fc\3\2\2\2!\u00ff\3\2\2\2"+
		"#\u0102\3\2\2\2%\u0105\3\2\2\2\'\u0108\3\2\2\2)\u010b\3\2\2\2+\u010e\3"+
		"\2\2\2-\u0111\3\2\2\2/\u0114\3\2\2\2\61\u0116\3\2\2\2\63\u0118\3\2\2\2"+
		"\65\u011a\3\2\2\2\67\u011c\3\2\2\29\u011e\3\2\2\2;\u0120\3\2\2\2=\u0122"+
		"\3\2\2\2?\u0124\3\2\2\2A\u0126\3\2\2\2C\u0128\3\2\2\2E\u012a\3\2\2\2G"+
		"\u012c\3\2\2\2I\u012e\3\2\2\2K\u0130\3\2\2\2M\u0132\3\2\2\2O\u0134\3\2"+
		"\2\2Q\u0136\3\2\2\2S\u0138\3\2\2\2U\u013a\3\2\2\2W\u013c\3\2\2\2Y\u013e"+
		"\3\2\2\2[\u0140\3\2\2\2]\u0148\3\2\2\2_\u0150\3\2\2\2a\u0158\3\2\2\2c"+
		"\u0160\3\2\2\2e\u0168\3\2\2\2g\u0170\3\2\2\2i\u017e\3\2\2\2k\u0188\3\2"+
		"\2\2m\u018e\3\2\2\2o\u0198\3\2\2\2q\u01a4\3\2\2\2s\u01b0\3\2\2\2u\u01c4"+
		"\3\2\2\2w\u01d2\3\2\2\2y\u01dc\3\2\2\2{\u01e8\3\2\2\2}\u01ee\3\2\2\2\177"+
		"\u01f8\3\2\2\2\u0081\u0204\3\2\2\2\u0083\u0214\3\2\2\2\u0085\u0224\3\2"+
		"\2\2\u0087\u0244\3\2\2\2\u0089\u024c\3\2\2\2\u008b\u0256\3\2\2\2\u008d"+
		"\u025e\3\2\2\2\u008f\u0278\3\2\2\2\u0091\u0292\3\2\2\2\u0093\u02ad\3\2"+
		"\2\2\u0095\u02af\3\2\2\2\u0097\u0098\t\2\2\2\u0098\4\3\2\2\2\u0099\u009a"+
		"\t\3\2\2\u009a\6\3\2\2\2\u009b\u009f\7}\2\2\u009c\u009e\13\2\2\2\u009d"+
		"\u009c\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u00a0\3\2\2\2\u009f\u009d\3\2"+
		"\2\2\u00a0\u00a2\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a3\7\177\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00a5\b\4\2\2\u00a5\b\3\2\2\2\u00a6\u00aa\7)\2\2"+
		"\u00a7\u00a9\n\4\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00a8"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad"+
		"\u00ae\b\5\2\2\u00ae\n\3\2\2\2\u00af\u00b1\t\4\2\2\u00b0\u00af\3\2\2\2"+
		"\u00b1\u00b2\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b4"+
		"\3\2\2\2\u00b4\u00b5\b\6\3\2\u00b5\f\3\2\2\2\u00b6\u00b8\t\5\2\2\u00b7"+
		"\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2"+
		"\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\b\7\4\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00be\b\7\2\2\u00be\16\3\2\2\2\u00bf\u00c3\7$\2\2\u00c0\u00c2\13\2\2"+
		"\2\u00c1\u00c0\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c3\u00c1"+
		"\3\2\2\2\u00c4\u00c6\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c7\7$\2\2\u00c7"+
		"\20\3\2\2\2\u00c8\u00c9\7\'\2\2\u00c9\u00ca\7\'\2\2\u00ca\u00cb\3\2\2"+
		"\2\u00cb\u00d0\5\5\3\2\u00cc\u00cf\5\5\3\2\u00cd\u00cf\5S*\2\u00ce\u00cc"+
		"\3\2\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0"+
		"\u00d1\3\2\2\2\u00d1\22\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00d4\7\'\2"+
		"\2\u00d4\u00d9\5\5\3\2\u00d5\u00d8\5\5\3\2\u00d6\u00d8\5S*\2\u00d7\u00d5"+
		"\3\2\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da\24\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00df\7&\2\2"+
		"\u00dd\u00e0\5\5\3\2\u00de\u00e0\5\3\2\2\u00df\u00dd\3\2\2\2\u00df\u00de"+
		"\3\2\2\2\u00e0\u00e6\3\2\2\2\u00e1\u00e5\5\5\3\2\u00e2\u00e5\5\3\2\2\u00e3"+
		"\u00e5\5S*\2\u00e4\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e3\3\2\2"+
		"\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\26"+
		"\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00f0\5\5\3\2\u00ea\u00ef\5\5\3\2\u00eb"+
		"\u00ef\5? \2\u00ec\u00ef\5\3\2\2\u00ed\u00ef\5S*\2\u00ee\u00ea\3\2\2\2"+
		"\u00ee\u00eb\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ed\3\2\2\2\u00ef\u00f2"+
		"\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\30\3\2\2\2\u00f2"+
		"\u00f0\3\2\2\2\u00f3\u00f4\7(\2\2\u00f4\u00f5\7(\2\2\u00f5\32\3\2\2\2"+
		"\u00f6\u00f7\7~\2\2\u00f7\u00f8\7~\2\2\u00f8\34\3\2\2\2\u00f9\u00fa\7"+
		"`\2\2\u00fa\u00fb\7`\2\2\u00fb\36\3\2\2\2\u00fc\u00fd\7%\2\2\u00fd\u00fe"+
		"\7%\2\2\u00fe \3\2\2\2\u00ff\u0100\7>\2\2\u0100\u0101\7>\2\2\u0101\"\3"+
		"\2\2\2\u0102\u0103\7@\2\2\u0103\u0104\7@\2\2\u0104$\3\2\2\2\u0105\u0106"+
		"\7-\2\2\u0106\u0107\7-\2\2\u0107&\3\2\2\2\u0108\u0109\7/\2\2\u0109\u010a"+
		"\7/\2\2\u010a(\3\2\2\2\u010b\u010c\7<\2\2\u010c\u010d\7?\2\2\u010d*\3"+
		"\2\2\2\u010e\u010f\7-\2\2\u010f\u0110\7?\2\2\u0110,\3\2\2\2\u0111\u0112"+
		"\7\60\2\2\u0112\u0113\7\60\2\2\u0113.\3\2\2\2\u0114\u0115\7(\2\2\u0115"+
		"\60\3\2\2\2\u0116\u0117\7~\2\2\u0117\62\3\2\2\2\u0118\u0119\7`\2\2\u0119"+
		"\64\3\2\2\2\u011a\u011b\7B\2\2\u011b\66\3\2\2\2\u011c\u011d\7%\2\2\u011d"+
		"8\3\2\2\2\u011e\u011f\7&\2\2\u011f:\3\2\2\2\u0120\u0121\7\'\2\2\u0121"+
		"<\3\2\2\2\u0122\u0123\7?\2\2\u0123>\3\2\2\2\u0124\u0125\7\60\2\2\u0125"+
		"@\3\2\2\2\u0126\u0127\7.\2\2\u0127B\3\2\2\2\u0128\u0129\7^\2\2\u0129D"+
		"\3\2\2\2\u012a\u012b\7-\2\2\u012bF\3\2\2\2\u012c\u012d\7/\2\2\u012dH\3"+
		"\2\2\2\u012e\u012f\7,\2\2\u012fJ\3\2\2\2\u0130\u0131\7\61\2\2\u0131L\3"+
		"\2\2\2\u0132\u0133\7A\2\2\u0133N\3\2\2\2\u0134\u0135\7<\2\2\u0135P\3\2"+
		"\2\2\u0136\u0137\7\u0080\2\2\u0137R\3\2\2\2\u0138\u0139\7a\2\2\u0139T"+
		"\3\2\2\2\u013a\u013b\7]\2\2\u013bV\3\2\2\2\u013c\u013d\7_\2\2\u013dX\3"+
		"\2\2\2\u013e\u013f\7*\2\2\u013fZ\3\2\2\2\u0140\u0141\7+\2\2\u0141\\\3"+
		"\2\2\2\u0142\u0143\7E\2\2\u0143\u0144\7Q\2\2\u0144\u0149\7P\2\2\u0145"+
		"\u0146\7e\2\2\u0146\u0147\7q\2\2\u0147\u0149\7p\2\2\u0148\u0142\3\2\2"+
		"\2\u0148\u0145\3\2\2\2\u0149^\3\2\2\2\u014a\u014b\7X\2\2\u014b\u014c\7"+
		"C\2\2\u014c\u0151\7T\2\2\u014d\u014e\7x\2\2\u014e\u014f\7c\2\2\u014f\u0151"+
		"\7t\2\2\u0150\u014a\3\2\2\2\u0150\u014d\3\2\2\2\u0151`\3\2\2\2\u0152\u0153"+
		"\7Q\2\2\u0153\u0154\7D\2\2\u0154\u0159\7L\2\2\u0155\u0156\7q\2\2\u0156"+
		"\u0157\7d\2\2\u0157\u0159\7l\2\2\u0158\u0152\3\2\2\2\u0158\u0155\3\2\2"+
		"\2\u0159b\3\2\2\2\u015a\u015b\7R\2\2\u015b\u015c\7W\2\2\u015c\u0161\7"+
		"D\2\2\u015d\u015e\7r\2\2\u015e\u015f\7w\2\2\u015f\u0161\7d\2\2\u0160\u015a"+
		"\3\2\2\2\u0160\u015d\3\2\2\2\u0161d\3\2\2\2\u0162\u0163\7R\2\2\u0163\u0164"+
		"\7T\2\2\u0164\u0169\7K\2\2\u0165\u0166\7r\2\2\u0166\u0167\7t\2\2\u0167"+
		"\u0169\7k\2\2\u0168\u0162\3\2\2\2\u0168\u0165\3\2\2\2\u0169f\3\2\2\2\u016a"+
		"\u016b\7F\2\2\u016b\u016c\7C\2\2\u016c\u0171\7V\2\2\u016d\u016e\7f\2\2"+
		"\u016e\u016f\7c\2\2\u016f\u0171\7v\2\2\u0170\u016a\3\2\2\2\u0170\u016d"+
		"\3\2\2\2\u0171h\3\2\2\2\u0172\u0173\7T\2\2\u0173\u0174\7G\2\2\u0174\u0175"+
		"\7R\2\2\u0175\u0176\7G\2\2\u0176\u0177\7C\2\2\u0177\u017f\7V\2\2\u0178"+
		"\u0179\7t\2\2\u0179\u017a\7g\2\2\u017a\u017b\7r\2\2\u017b\u017c\7g\2\2"+
		"\u017c\u017d\7c\2\2\u017d\u017f\7v\2\2\u017e\u0172\3\2\2\2\u017e\u0178"+
		"\3\2\2\2\u017fj\3\2\2\2\u0180\u0181\7H\2\2\u0181\u0182\7T\2\2\u0182\u0183"+
		"\7Q\2\2\u0183\u0189\7O\2\2\u0184\u0185\7h\2\2\u0185\u0186\7t\2\2\u0186"+
		"\u0187\7q\2\2\u0187\u0189\7o\2\2\u0188\u0180\3\2\2\2\u0188\u0184\3\2\2"+
		"\2\u0189l\3\2\2\2\u018a\u018b\7V\2\2\u018b\u018f\7Q\2\2\u018c\u018d\7"+
		"v\2\2\u018d\u018f\7q\2\2\u018e\u018a\3\2\2\2\u018e\u018c\3\2\2\2\u018f"+
		"n\3\2\2\2\u0190\u0191\7U\2\2\u0191\u0192\7V\2\2\u0192\u0193\7G\2\2\u0193"+
		"\u0199\7R\2\2\u0194\u0195\7u\2\2\u0195\u0196\7v\2\2\u0196\u0197\7g\2\2"+
		"\u0197\u0199\7r\2\2\u0198\u0190\3\2\2\2\u0198\u0194\3\2\2\2\u0199p\3\2"+
		"\2\2\u019a\u019b\7Y\2\2\u019b\u019c\7J\2\2\u019c\u019d\7K\2\2\u019d\u019e"+
		"\7N\2\2\u019e\u01a5\7G\2\2\u019f\u01a0\7y\2\2\u01a0\u01a1\7j\2\2\u01a1"+
		"\u01a2\7k\2\2\u01a2\u01a3\7n\2\2\u01a3\u01a5\7g\2\2\u01a4\u019a\3\2\2"+
		"\2\u01a4\u019f\3\2\2\2\u01a5r\3\2\2\2\u01a6\u01a7\7W\2\2\u01a7\u01a8\7"+
		"P\2\2\u01a8\u01a9\7V\2\2\u01a9\u01aa\7K\2\2\u01aa\u01b1\7N\2\2\u01ab\u01ac"+
		"\7w\2\2\u01ac\u01ad\7p\2\2\u01ad\u01ae\7v\2\2\u01ae\u01af\7k\2\2\u01af"+
		"\u01b1\7n\2\2\u01b0\u01a6\3\2\2\2\u01b0\u01ab\3\2\2\2\u01b1t\3\2\2\2\u01b2"+
		"\u01b3\7G\2\2\u01b3\u01b4\7N\2\2\u01b4\u01b5\7U\2\2\u01b5\u01b6\7G\2\2"+
		"\u01b6\u01b7\7K\2\2\u01b7\u01b8\7H\2\2\u01b8\u01b9\7P\2\2\u01b9\u01ba"+
		"\7Q\2\2\u01ba\u01c5\7V\2\2\u01bb\u01bc\7g\2\2\u01bc\u01bd\7n\2\2\u01bd"+
		"\u01be\7u\2\2\u01be\u01bf\7g\2\2\u01bf\u01c0\7k\2\2\u01c0\u01c1\7h\2\2"+
		"\u01c1\u01c2\7p\2\2\u01c2\u01c3\7q\2\2\u01c3\u01c5\7v\2\2\u01c4\u01b2"+
		"\3\2\2\2\u01c4\u01bb\3\2\2\2\u01c5v\3\2\2\2\u01c6\u01c7\7G\2\2\u01c7\u01c8"+
		"\7N\2\2\u01c8\u01c9\7U\2\2\u01c9\u01ca\7G\2\2\u01ca\u01cb\7K\2\2\u01cb"+
		"\u01d3\7H\2\2\u01cc\u01cd\7g\2\2\u01cd\u01ce\7n\2\2\u01ce\u01cf\7u\2\2"+
		"\u01cf\u01d0\7g\2\2\u01d0\u01d1\7k\2\2\u01d1\u01d3\7h\2\2\u01d2\u01c6"+
		"\3\2\2\2\u01d2\u01cc\3\2\2\2\u01d3x\3\2\2\2\u01d4\u01d5\7G\2\2\u01d5\u01d6"+
		"\7N\2\2\u01d6\u01d7\7U\2\2\u01d7\u01dd\7G\2\2\u01d8\u01d9\7g\2\2\u01d9"+
		"\u01da\7n\2\2\u01da\u01db\7u\2\2\u01db\u01dd\7g\2\2\u01dc\u01d4\3\2\2"+
		"\2\u01dc\u01d8\3\2\2\2\u01ddz\3\2\2\2\u01de\u01df\7K\2\2\u01df\u01e0\7"+
		"H\2\2\u01e0\u01e1\7P\2\2\u01e1\u01e2\7Q\2\2\u01e2\u01e9\7V\2\2\u01e3\u01e4"+
		"\7k\2\2\u01e4\u01e5\7h\2\2\u01e5\u01e6\7p\2\2\u01e6\u01e7\7q\2\2\u01e7"+
		"\u01e9\7v\2\2\u01e8\u01de\3\2\2\2\u01e8\u01e3\3\2\2\2\u01e9|\3\2\2\2\u01ea"+
		"\u01eb\7K\2\2\u01eb\u01ef\7H\2\2\u01ec\u01ed\7k\2\2\u01ed\u01ef\7h\2\2"+
		"\u01ee\u01ea\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ef~\3\2\2\2\u01f0\u01f1\7"+
		"E\2\2\u01f1\u01f2\7C\2\2\u01f2\u01f3\7U\2\2\u01f3\u01f9\7G\2\2\u01f4\u01f5"+
		"\7e\2\2\u01f5\u01f6\7c\2\2\u01f6\u01f7\7u\2\2\u01f7\u01f9\7g\2\2\u01f8"+
		"\u01f0\3\2\2\2\u01f8\u01f4\3\2\2\2\u01f9\u0080\3\2\2\2\u01fa\u01fb\7Q"+
		"\2\2\u01fb\u01fc\7V\2\2\u01fc\u01fd\7J\2\2\u01fd\u01fe\7G\2\2\u01fe\u0205"+
		"\7T\2\2\u01ff\u0200\7q\2\2\u0200\u0201\7v\2\2\u0201\u0202\7j\2\2\u0202"+
		"\u0203\7g\2\2\u0203\u0205\7t\2\2\u0204\u01fa\3\2\2\2\u0204\u01ff\3\2\2"+
		"\2\u0205\u0082\3\2\2\2\u0206\u0207\7C\2\2\u0207\u0208\7F\2\2\u0208\u0209"+
		"\7F\2\2\u0209\u020a\7R\2\2\u020a\u020b\7K\2\2\u020b\u020c\7P\2\2\u020c"+
		"\u0215\7U\2\2\u020d\u020e\7c\2\2\u020e\u020f\7f\2\2\u020f\u0210\7f\2\2"+
		"\u0210\u0211\7r\2\2\u0211\u0212\7k\2\2\u0212\u0213\7p\2\2\u0213\u0215"+
		"\7u\2\2\u0214\u0206\3\2\2\2\u0214\u020d\3\2\2\2\u0215\u0084\3\2\2\2\u0216"+
		"\u0217\7C\2\2\u0217\u0218\7F\2\2\u0218\u0219\7F\2\2\u0219\u021a\7D\2\2"+
		"\u021a\u021b\7K\2\2\u021b\u021c\7V\2\2\u021c\u0225\7U\2\2\u021d\u021e"+
		"\7c\2\2\u021e\u021f\7f\2\2\u021f\u0220\7f\2\2\u0220\u0221\7d\2\2\u0221"+
		"\u0222\7k\2\2\u0222\u0223\7v\2\2\u0223\u0225\7u\2\2\u0224\u0216\3\2\2"+
		"\2\u0224\u021d\3\2\2\2\u0225\u0086\3\2\2\2\u0226\u0227\7T\2\2\u0227\u0228"+
		"\7Q\2\2\u0228\u0229\7W\2\2\u0229\u022a\7P\2\2\u022a\u0245\7F\2\2\u022b"+
		"\u022c\7t\2\2\u022c\u022d\7q\2\2\u022d\u022e\7w\2\2\u022e\u022f\7p\2\2"+
		"\u022f\u0245\7f\2\2\u0230\u0231\7H\2\2\u0231\u0232\7N\2\2\u0232\u0233"+
		"\7Q\2\2\u0233\u0234\7C\2\2\u0234\u0245\7V\2\2\u0235\u0236\7h\2\2\u0236"+
		"\u0237\7n\2\2\u0237\u0238\7q\2\2\u0238\u0239\7c\2\2\u0239\u0245\7v\2\2"+
		"\u023a\u023b\7V\2\2\u023b\u023c\7T\2\2\u023c\u023d\7W\2\2\u023d\u023e"+
		"\7P\2\2\u023e\u0245\7E\2\2\u023f\u0240\7v\2\2\u0240\u0241\7t\2\2\u0241"+
		"\u0242\7w\2\2\u0242\u0243\7p\2\2\u0243\u0245\7e\2\2\u0244\u0226\3\2\2"+
		"\2\u0244\u022b\3\2\2\2\u0244\u0230\3\2\2\2\u0244\u0235\3\2\2\2\u0244\u023a"+
		"\3\2\2\2\u0244\u023f\3\2\2\2\u0245\u0088\3\2\2\2\u0246\u0247\7Q\2\2\u0247"+
		"\u0248\7T\2\2\u0248\u024d\7I\2\2\u0249\u024a\7q\2\2\u024a\u024b\7t\2\2"+
		"\u024b\u024d\7i\2\2\u024c\u0246\3\2\2\2\u024c\u0249\3\2\2\2\u024d\u008a"+
		"\3\2\2\2\u024e\u024f\7Q\2\2\u024f\u0250\7T\2\2\u0250\u0251\7I\2\2\u0251"+
		"\u0257\7J\2\2\u0252\u0253\7q\2\2\u0253\u0254\7t\2\2\u0254\u0255\7i\2\2"+
		"\u0255\u0257\7j\2\2\u0256\u024e\3\2\2\2\u0256\u0252\3\2\2\2\u0257\u008c"+
		"\3\2\2\2\u0258\u0259\7T\2\2\u0259\u025a\7G\2\2\u025a\u025f\7U\2\2\u025b"+
		"\u025c\7t\2\2\u025c\u025d\7g\2\2\u025d\u025f\7u\2\2\u025e\u0258\3\2\2"+
		"\2\u025e\u025b\3\2\2\2\u025f\u008e\3\2\2\2\u0260\u0261\7C\2\2\u0261\u0262"+
		"\7N\2\2\u0262\u0263\7K\2\2\u0263\u0264\7I\2\2\u0264\u0265\7P\2\2\u0265"+
		"\u0279\7Y\2\2\u0266\u0267\7c\2\2\u0267\u0268\7n\2\2\u0268\u0269\7k\2\2"+
		"\u0269\u026a\7i\2\2\u026a\u026b\7p\2\2\u026b\u0279\7y\2\2\u026c\u026d"+
		"\7C\2\2\u026d\u026e\7N\2\2\u026e\u026f\7K\2\2\u026f\u0270\7I\2\2\u0270"+
		"\u0271\7P\2\2\u0271\u0279\7N\2\2\u0272\u0273\7c\2\2\u0273\u0274\7n\2\2"+
		"\u0274\u0275\7k\2\2\u0275\u0276\7i\2\2\u0276\u0277\7p\2\2\u0277\u0279"+
		"\7n\2\2\u0278\u0260\3\2\2\2\u0278\u0266\3\2\2\2\u0278\u026c\3\2\2\2\u0278"+
		"\u0272\3\2\2\2\u0279\u0090\3\2\2\2\u027a\u027b\7N\2\2\u027b\u027c\7Q\2"+
		"\2\u027c\u027d\7P\2\2\u027d\u0293\7I\2\2\u027e\u027f\7n\2\2\u027f\u0280"+
		"\7q\2\2\u0280\u0281\7p\2\2\u0281\u0293\7i\2\2\u0282\u0283\7Y\2\2\u0283"+
		"\u0284\7Q\2\2\u0284\u0285\7T\2\2\u0285\u0293\7F\2\2\u0286\u0287\7y\2\2"+
		"\u0287\u0288\7q\2\2\u0288\u0289\7t\2\2\u0289\u0293\7f\2\2\u028a\u028b"+
		"\7D\2\2\u028b\u028c\7[\2\2\u028c\u028d\7V\2\2\u028d\u0293\7G\2\2\u028e"+
		"\u028f\7d\2\2\u028f\u0290\7{\2\2\u0290\u0291\7v\2\2\u0291\u0293\7g\2\2"+
		"\u0292\u027a\3\2\2\2\u0292\u027e\3\2\2\2\u0292\u0282\3\2\2\2\u0292\u0286"+
		"\3\2\2\2\u0292\u028a\3\2\2\2\u0292\u028e\3\2\2\2\u0293\u0092\3\2\2\2\u0294"+
		"\u0295\7K\2\2\u0295\u0296\7H\2\2\u0296\u029b\7a\2\2\u0297\u0298\7k\2\2"+
		"\u0298\u0299\7h\2\2\u0299\u029b\7a\2\2\u029a\u0294\3\2\2\2\u029a\u0297"+
		"\3\2\2\2\u029b\u02a0\3\2\2\2\u029c\u029f\5\3\2\2\u029d\u029f\5\5\3\2\u029e"+
		"\u029c\3\2\2\2\u029e\u029d\3\2\2\2\u029f\u02a2\3\2\2\2\u02a0\u029e\3\2"+
		"\2\2\u02a0\u02a1\3\2\2\2\u02a1\u02ae\3\2\2\2\u02a2\u02a0\3\2\2\2\u02a3"+
		"\u02a4\7a\2\2\u02a4\u02a5\7T\2\2\u02a5\u02a6\7G\2\2\u02a6\u02a7\7V\2\2"+
		"\u02a7\u02ae\7a\2\2\u02a8\u02a9\7a\2\2\u02a9\u02aa\7t\2\2\u02aa\u02ab"+
		"\7g\2\2\u02ab\u02ac\7v\2\2\u02ac\u02ae\7a\2\2\u02ad\u029a\3\2\2\2\u02ad"+
		"\u02a3\3\2\2\2\u02ad\u02a8\3\2\2\2\u02ae\u0094\3\2\2\2\u02af\u02b4\5\3"+
		"\2\2\u02b0\u02b3\5\3\2\2\u02b1\u02b3\5\5\3\2\u02b2\u02b0\3\2\2\2\u02b2"+
		"\u02b1\3\2\2\2\u02b3\u02b6\3\2\2\2\u02b4\u02b2\3\2\2\2\u02b4\u02b5\3\2"+
		"\2\2\u02b5\u0096\3\2\2\2\u02b6\u02b4\3\2\2\2\62\2\u009f\u00aa\u00b2\u00b9"+
		"\u00c3\u00ce\u00d0\u00d7\u00d9\u00df\u00e4\u00e6\u00ee\u00f0\u0148\u0150"+
		"\u0158\u0160\u0168\u0170\u017e\u0188\u018e\u0198\u01a4\u01b0\u01c4\u01d2"+
		"\u01dc\u01e8\u01ee\u01f8\u0204\u0214\u0224\u0244\u024c\u0256\u025e\u0278"+
		"\u0292\u029a\u029e\u02a0\u02ad\u02b2\u02b4\5\2\3\2\3\6\2\3\7\3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}