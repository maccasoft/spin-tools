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
		ASSIGN=20, ADD_ASSIGN=21, BIN_AND=22, BIN_OR=23, BIN_XOR=24, AT=25, POUND=26, 
		DOLLAR=27, PERCENT=28, EQUAL=29, DOT=30, COMMA=31, BACKSLASH=32, PLUS=33, 
		MINUS=34, STAR=35, DIV=36, QUESTION=37, COLON=38, TILDE=39, UNDERSCORE=40, 
		OPEN_BRACKET=41, CLOSE_BRACKET=42, OPEN_PAREN=43, CLOSE_PAREN=44, CON_START=45, 
		VAR_START=46, OBJ_START=47, PUB_START=48, PRI_START=49, DAT_START=50, 
		REPEAT=51, ADDPINS=52, FLOAT=53, ROUND=54, TRUNC=55, ORG=56, ORGH=57, 
		RES=58, ALIGN=59, TYPE=60, PTR=61, CONDITION=62, IDENTIFIER=63;
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
			"ASSIGN", "ADD_ASSIGN", "BIN_AND", "BIN_OR", "BIN_XOR", "AT", "POUND", 
			"DOLLAR", "PERCENT", "EQUAL", "DOT", "COMMA", "BACKSLASH", "PLUS", "MINUS", 
			"STAR", "DIV", "QUESTION", "COLON", "TILDE", "UNDERSCORE", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "CON_START", "VAR_START", 
			"OBJ_START", "PUB_START", "PRI_START", "DAT_START", "REPEAT", "ADDPINS", 
			"FLOAT", "ROUND", "TRUNC", "ORG", "ORGH", "RES", "ALIGN", "TYPE", "PTR", 
			"CONDITION", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'&&'", "'||'", "'^^'", "'##'", "'<<'", "'>>'", "'++'", "'--'", "':='", 
			"'+='", "'&'", "'|'", "'^'", "'@'", "'#'", "'$'", "'%'", "'='", "'.'", 
			"','", "'\\'", "'+'", "'-'", "'*'", "'/'", "'?'", "':'", "'~'", "'_'", 
			"'['", "']'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", 
			"POUND_POUND", "LEFT_SHIFT", "RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", 
			"ASSIGN", "ADD_ASSIGN", "BIN_AND", "BIN_OR", "BIN_XOR", "AT", "POUND", 
			"DOLLAR", "PERCENT", "EQUAL", "DOT", "COMMA", "BACKSLASH", "PLUS", "MINUS", 
			"STAR", "DIV", "QUESTION", "COLON", "TILDE", "UNDERSCORE", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "CON_START", "VAR_START", 
			"OBJ_START", "PUB_START", "PRI_START", "DAT_START", "REPEAT", "ADDPINS", 
			"FLOAT", "ROUND", "TRUNC", "ORG", "ORGH", "RES", "ALIGN", "TYPE", "PTR", 
			"CONDITION", "IDENTIFIER"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2A\u021e\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\3\3\3\3\3\4\3\4\7\4\u0088\n\4\f\4\16\4\u008b"+
		"\13\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5\u0093\n\5\f\5\16\5\u0096\13\5\3\5\3"+
		"\5\3\6\6\6\u009b\n\6\r\6\16\6\u009c\3\6\3\6\3\7\6\7\u00a2\n\7\r\7\16\7"+
		"\u00a3\3\7\3\7\3\7\3\7\3\b\3\b\7\b\u00ac\n\b\f\b\16\b\u00af\13\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\7\t\u00b9\n\t\f\t\16\t\u00bc\13\t\3\n\3\n\3"+
		"\n\3\n\7\n\u00c2\n\n\f\n\16\n\u00c5\13\n\3\13\3\13\3\13\5\13\u00ca\n\13"+
		"\3\13\3\13\3\13\7\13\u00cf\n\13\f\13\16\13\u00d2\13\13\3\f\3\f\3\f\3\f"+
		"\3\f\7\f\u00d9\n\f\f\f\16\f\u00dc\13\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3"+
		" \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3"+
		"+\3,\3,\3-\3-\3.\3.\3.\3.\3.\3.\5.\u0130\n.\3/\3/\3/\3/\3/\3/\5/\u0138"+
		"\n/\3\60\3\60\3\60\3\60\3\60\3\60\5\60\u0140\n\60\3\61\3\61\3\61\3\61"+
		"\3\61\3\61\5\61\u0148\n\61\3\62\3\62\3\62\3\62\3\62\3\62\5\62\u0150\n"+
		"\62\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u0158\n\63\3\64\3\64\3\64\3\64"+
		"\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\5\64\u0166\n\64\3\65\3\65\3\65"+
		"\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\5\65\u0176\n\65"+
		"\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u0182\n\66\3\67"+
		"\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\5\67\u018e\n\67\38\38\3"+
		"8\38\38\38\38\38\38\38\58\u019a\n8\39\39\39\39\39\39\59\u01a2\n9\3:\3"+
		":\3:\3:\3:\3:\3:\3:\5:\u01ac\n:\3;\3;\3;\3;\3;\3;\5;\u01b4\n;\3<\3<\3"+
		"<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\3<\5<\u01ce"+
		"\n<\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3="+
		"\3=\3=\5=\u01e8\n=\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\5>"+
		"\u01fa\n>\3?\3?\3?\3?\3?\3?\5?\u0202\n?\3?\3?\7?\u0206\n?\f?\16?\u0209"+
		"\13?\3?\3?\3?\3?\3?\3?\3?\3?\3?\3?\5?\u0215\n?\3@\3@\3@\7@\u021a\n@\f"+
		"@\16@\u021d\13@\4\u0089\u00ad\2A\3\2\5\2\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60"+
		"_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\3\2\6\5\2C\\aac|\3"+
		"\2\62;\4\2\f\f\17\17\4\2\13\13\"\"\2\u024c\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\3\u0081\3\2\2\2\5"+
		"\u0083\3\2\2\2\7\u0085\3\2\2\2\t\u0090\3\2\2\2\13\u009a\3\2\2\2\r\u00a1"+
		"\3\2\2\2\17\u00a9\3\2\2\2\21\u00b2\3\2\2\2\23\u00bd\3\2\2\2\25\u00c6\3"+
		"\2\2\2\27\u00d3\3\2\2\2\31\u00dd\3\2\2\2\33\u00e0\3\2\2\2\35\u00e3\3\2"+
		"\2\2\37\u00e6\3\2\2\2!\u00e9\3\2\2\2#\u00ec\3\2\2\2%\u00ef\3\2\2\2\'\u00f2"+
		"\3\2\2\2)\u00f5\3\2\2\2+\u00f8\3\2\2\2-\u00fb\3\2\2\2/\u00fd\3\2\2\2\61"+
		"\u00ff\3\2\2\2\63\u0101\3\2\2\2\65\u0103\3\2\2\2\67\u0105\3\2\2\29\u0107"+
		"\3\2\2\2;\u0109\3\2\2\2=\u010b\3\2\2\2?\u010d\3\2\2\2A\u010f\3\2\2\2C"+
		"\u0111\3\2\2\2E\u0113\3\2\2\2G\u0115\3\2\2\2I\u0117\3\2\2\2K\u0119\3\2"+
		"\2\2M\u011b\3\2\2\2O\u011d\3\2\2\2Q\u011f\3\2\2\2S\u0121\3\2\2\2U\u0123"+
		"\3\2\2\2W\u0125\3\2\2\2Y\u0127\3\2\2\2[\u012f\3\2\2\2]\u0137\3\2\2\2_"+
		"\u013f\3\2\2\2a\u0147\3\2\2\2c\u014f\3\2\2\2e\u0157\3\2\2\2g\u0165\3\2"+
		"\2\2i\u0175\3\2\2\2k\u0181\3\2\2\2m\u018d\3\2\2\2o\u0199\3\2\2\2q\u01a1"+
		"\3\2\2\2s\u01ab\3\2\2\2u\u01b3\3\2\2\2w\u01cd\3\2\2\2y\u01e7\3\2\2\2{"+
		"\u01f9\3\2\2\2}\u0214\3\2\2\2\177\u0216\3\2\2\2\u0081\u0082\t\2\2\2\u0082"+
		"\4\3\2\2\2\u0083\u0084\t\3\2\2\u0084\6\3\2\2\2\u0085\u0089\7}\2\2\u0086"+
		"\u0088\13\2\2\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u008a\3"+
		"\2\2\2\u0089\u0087\3\2\2\2\u008a\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c"+
		"\u008d\7\177\2\2\u008d\u008e\3\2\2\2\u008e\u008f\b\4\2\2\u008f\b\3\2\2"+
		"\2\u0090\u0094\7)\2\2\u0091\u0093\n\4\2\2\u0092\u0091\3\2\2\2\u0093\u0096"+
		"\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0097\3\2\2\2\u0096"+
		"\u0094\3\2\2\2\u0097\u0098\b\5\2\2\u0098\n\3\2\2\2\u0099\u009b\t\4\2\2"+
		"\u009a\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f\b\6\3\2\u009f\f\3\2\2\2\u00a0"+
		"\u00a2\t\5\2\2\u00a1\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a1\3\2"+
		"\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a6\b\7\4\2\u00a6"+
		"\u00a7\3\2\2\2\u00a7\u00a8\b\7\2\2\u00a8\16\3\2\2\2\u00a9\u00ad\7$\2\2"+
		"\u00aa\u00ac\13\2\2\2\u00ab\u00aa\3\2\2\2\u00ac\u00af\3\2\2\2\u00ad\u00ae"+
		"\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b0\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0"+
		"\u00b1\7$\2\2\u00b1\20\3\2\2\2\u00b2\u00b3\7\'\2\2\u00b3\u00b4\7\'\2\2"+
		"\u00b4\u00b5\3\2\2\2\u00b5\u00ba\5\5\3\2\u00b6\u00b9\5\5\3\2\u00b7\u00b9"+
		"\5Q)\2\u00b8\u00b6\3\2\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba"+
		"\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\22\3\2\2\2\u00bc\u00ba\3\2\2"+
		"\2\u00bd\u00be\7\'\2\2\u00be\u00c3\5\5\3\2\u00bf\u00c2\5\5\3\2\u00c0\u00c2"+
		"\5Q)\2\u00c1\u00bf\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3"+
		"\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\24\3\2\2\2\u00c5\u00c3\3\2\2"+
		"\2\u00c6\u00c9\7&\2\2\u00c7\u00ca\5\5\3\2\u00c8\u00ca\5\3\2\2\u00c9\u00c7"+
		"\3\2\2\2\u00c9\u00c8\3\2\2\2\u00ca\u00d0\3\2\2\2\u00cb\u00cf\5\5\3\2\u00cc"+
		"\u00cf\5\3\2\2\u00cd\u00cf\5Q)\2\u00ce\u00cb\3\2\2\2\u00ce\u00cc\3\2\2"+
		"\2\u00ce\u00cd\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1\26\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00da\5\5\3\2\u00d4"+
		"\u00d9\5\5\3\2\u00d5\u00d9\5=\37\2\u00d6\u00d9\5\3\2\2\u00d7\u00d9\5Q"+
		")\2\u00d8\u00d4\3\2\2\2\u00d8\u00d5\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8"+
		"\u00d7\3\2\2\2\u00d9\u00dc\3\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00db\3\2"+
		"\2\2\u00db\30\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd\u00de\7(\2\2\u00de\u00df"+
		"\7(\2\2\u00df\32\3\2\2\2\u00e0\u00e1\7~\2\2\u00e1\u00e2\7~\2\2\u00e2\34"+
		"\3\2\2\2\u00e3\u00e4\7`\2\2\u00e4\u00e5\7`\2\2\u00e5\36\3\2\2\2\u00e6"+
		"\u00e7\7%\2\2\u00e7\u00e8\7%\2\2\u00e8 \3\2\2\2\u00e9\u00ea\7>\2\2\u00ea"+
		"\u00eb\7>\2\2\u00eb\"\3\2\2\2\u00ec\u00ed\7@\2\2\u00ed\u00ee\7@\2\2\u00ee"+
		"$\3\2\2\2\u00ef\u00f0\7-\2\2\u00f0\u00f1\7-\2\2\u00f1&\3\2\2\2\u00f2\u00f3"+
		"\7/\2\2\u00f3\u00f4\7/\2\2\u00f4(\3\2\2\2\u00f5\u00f6\7<\2\2\u00f6\u00f7"+
		"\7?\2\2\u00f7*\3\2\2\2\u00f8\u00f9\7-\2\2\u00f9\u00fa\7?\2\2\u00fa,\3"+
		"\2\2\2\u00fb\u00fc\7(\2\2\u00fc.\3\2\2\2\u00fd\u00fe\7~\2\2\u00fe\60\3"+
		"\2\2\2\u00ff\u0100\7`\2\2\u0100\62\3\2\2\2\u0101\u0102\7B\2\2\u0102\64"+
		"\3\2\2\2\u0103\u0104\7%\2\2\u0104\66\3\2\2\2\u0105\u0106\7&\2\2\u0106"+
		"8\3\2\2\2\u0107\u0108\7\'\2\2\u0108:\3\2\2\2\u0109\u010a\7?\2\2\u010a"+
		"<\3\2\2\2\u010b\u010c\7\60\2\2\u010c>\3\2\2\2\u010d\u010e\7.\2\2\u010e"+
		"@\3\2\2\2\u010f\u0110\7^\2\2\u0110B\3\2\2\2\u0111\u0112\7-\2\2\u0112D"+
		"\3\2\2\2\u0113\u0114\7/\2\2\u0114F\3\2\2\2\u0115\u0116\7,\2\2\u0116H\3"+
		"\2\2\2\u0117\u0118\7\61\2\2\u0118J\3\2\2\2\u0119\u011a\7A\2\2\u011aL\3"+
		"\2\2\2\u011b\u011c\7<\2\2\u011cN\3\2\2\2\u011d\u011e\7\u0080\2\2\u011e"+
		"P\3\2\2\2\u011f\u0120\7a\2\2\u0120R\3\2\2\2\u0121\u0122\7]\2\2\u0122T"+
		"\3\2\2\2\u0123\u0124\7_\2\2\u0124V\3\2\2\2\u0125\u0126\7*\2\2\u0126X\3"+
		"\2\2\2\u0127\u0128\7+\2\2\u0128Z\3\2\2\2\u0129\u012a\7E\2\2\u012a\u012b"+
		"\7Q\2\2\u012b\u0130\7P\2\2\u012c\u012d\7e\2\2\u012d\u012e\7q\2\2\u012e"+
		"\u0130\7p\2\2\u012f\u0129\3\2\2\2\u012f\u012c\3\2\2\2\u0130\\\3\2\2\2"+
		"\u0131\u0132\7X\2\2\u0132\u0133\7C\2\2\u0133\u0138\7T\2\2\u0134\u0135"+
		"\7x\2\2\u0135\u0136\7c\2\2\u0136\u0138\7t\2\2\u0137\u0131\3\2\2\2\u0137"+
		"\u0134\3\2\2\2\u0138^\3\2\2\2\u0139\u013a\7Q\2\2\u013a\u013b\7D\2\2\u013b"+
		"\u0140\7L\2\2\u013c\u013d\7q\2\2\u013d\u013e\7d\2\2\u013e\u0140\7l\2\2"+
		"\u013f\u0139\3\2\2\2\u013f\u013c\3\2\2\2\u0140`\3\2\2\2\u0141\u0142\7"+
		"R\2\2\u0142\u0143\7W\2\2\u0143\u0148\7D\2\2\u0144\u0145\7r\2\2\u0145\u0146"+
		"\7w\2\2\u0146\u0148\7d\2\2\u0147\u0141\3\2\2\2\u0147\u0144\3\2\2\2\u0148"+
		"b\3\2\2\2\u0149\u014a\7R\2\2\u014a\u014b\7T\2\2\u014b\u0150\7K\2\2\u014c"+
		"\u014d\7r\2\2\u014d\u014e\7t\2\2\u014e\u0150\7k\2\2\u014f\u0149\3\2\2"+
		"\2\u014f\u014c\3\2\2\2\u0150d\3\2\2\2\u0151\u0152\7F\2\2\u0152\u0153\7"+
		"C\2\2\u0153\u0158\7V\2\2\u0154\u0155\7f\2\2\u0155\u0156\7c\2\2\u0156\u0158"+
		"\7v\2\2\u0157\u0151\3\2\2\2\u0157\u0154\3\2\2\2\u0158f\3\2\2\2\u0159\u015a"+
		"\7T\2\2\u015a\u015b\7G\2\2\u015b\u015c\7R\2\2\u015c\u015d\7G\2\2\u015d"+
		"\u015e\7C\2\2\u015e\u0166\7V\2\2\u015f\u0160\7t\2\2\u0160\u0161\7g\2\2"+
		"\u0161\u0162\7r\2\2\u0162\u0163\7g\2\2\u0163\u0164\7c\2\2\u0164\u0166"+
		"\7v\2\2\u0165\u0159\3\2\2\2\u0165\u015f\3\2\2\2\u0166h\3\2\2\2\u0167\u0168"+
		"\7C\2\2\u0168\u0169\7F\2\2\u0169\u016a\7F\2\2\u016a\u016b\7R\2\2\u016b"+
		"\u016c\7K\2\2\u016c\u016d\7P\2\2\u016d\u0176\7U\2\2\u016e\u016f\7c\2\2"+
		"\u016f\u0170\7f\2\2\u0170\u0171\7f\2\2\u0171\u0172\7r\2\2\u0172\u0173"+
		"\7k\2\2\u0173\u0174\7p\2\2\u0174\u0176\7u\2\2\u0175\u0167\3\2\2\2\u0175"+
		"\u016e\3\2\2\2\u0176j\3\2\2\2\u0177\u0178\7H\2\2\u0178\u0179\7N\2\2\u0179"+
		"\u017a\7Q\2\2\u017a\u017b\7C\2\2\u017b\u0182\7V\2\2\u017c\u017d\7h\2\2"+
		"\u017d\u017e\7n\2\2\u017e\u017f\7q\2\2\u017f\u0180\7c\2\2\u0180\u0182"+
		"\7v\2\2\u0181\u0177\3\2\2\2\u0181\u017c\3\2\2\2\u0182l\3\2\2\2\u0183\u0184"+
		"\7T\2\2\u0184\u0185\7Q\2\2\u0185\u0186\7W\2\2\u0186\u0187\7P\2\2\u0187"+
		"\u018e\7F\2\2\u0188\u0189\7t\2\2\u0189\u018a\7q\2\2\u018a\u018b\7w\2\2"+
		"\u018b\u018c\7p\2\2\u018c\u018e\7f\2\2\u018d\u0183\3\2\2\2\u018d\u0188"+
		"\3\2\2\2\u018en\3\2\2\2\u018f\u0190\7V\2\2\u0190\u0191\7T\2\2\u0191\u0192"+
		"\7W\2\2\u0192\u0193\7P\2\2\u0193\u019a\7E\2\2\u0194\u0195\7v\2\2\u0195"+
		"\u0196\7t\2\2\u0196\u0197\7w\2\2\u0197\u0198\7p\2\2\u0198\u019a\7e\2\2"+
		"\u0199\u018f\3\2\2\2\u0199\u0194\3\2\2\2\u019ap\3\2\2\2\u019b\u019c\7"+
		"Q\2\2\u019c\u019d\7T\2\2\u019d\u01a2\7I\2\2\u019e\u019f\7q\2\2\u019f\u01a0"+
		"\7t\2\2\u01a0\u01a2\7i\2\2\u01a1\u019b\3\2\2\2\u01a1\u019e\3\2\2\2\u01a2"+
		"r\3\2\2\2\u01a3\u01a4\7Q\2\2\u01a4\u01a5\7T\2\2\u01a5\u01a6\7I\2\2\u01a6"+
		"\u01ac\7J\2\2\u01a7\u01a8\7q\2\2\u01a8\u01a9\7t\2\2\u01a9\u01aa\7i\2\2"+
		"\u01aa\u01ac\7j\2\2\u01ab\u01a3\3\2\2\2\u01ab\u01a7\3\2\2\2\u01act\3\2"+
		"\2\2\u01ad\u01ae\7T\2\2\u01ae\u01af\7G\2\2\u01af\u01b4\7U\2\2\u01b0\u01b1"+
		"\7t\2\2\u01b1\u01b2\7g\2\2\u01b2\u01b4\7u\2\2\u01b3\u01ad\3\2\2\2\u01b3"+
		"\u01b0\3\2\2\2\u01b4v\3\2\2\2\u01b5\u01b6\7C\2\2\u01b6\u01b7\7N\2\2\u01b7"+
		"\u01b8\7K\2\2\u01b8\u01b9\7I\2\2\u01b9\u01ba\7P\2\2\u01ba\u01ce\7Y\2\2"+
		"\u01bb\u01bc\7c\2\2\u01bc\u01bd\7n\2\2\u01bd\u01be\7k\2\2\u01be\u01bf"+
		"\7i\2\2\u01bf\u01c0\7p\2\2\u01c0\u01ce\7y\2\2\u01c1\u01c2\7C\2\2\u01c2"+
		"\u01c3\7N\2\2\u01c3\u01c4\7K\2\2\u01c4\u01c5\7I\2\2\u01c5\u01c6\7P\2\2"+
		"\u01c6\u01ce\7N\2\2\u01c7\u01c8\7c\2\2\u01c8\u01c9\7n\2\2\u01c9\u01ca"+
		"\7k\2\2\u01ca\u01cb\7i\2\2\u01cb\u01cc\7p\2\2\u01cc\u01ce\7n\2\2\u01cd"+
		"\u01b5\3\2\2\2\u01cd\u01bb\3\2\2\2\u01cd\u01c1\3\2\2\2\u01cd\u01c7\3\2"+
		"\2\2\u01cex\3\2\2\2\u01cf\u01d0\7N\2\2\u01d0\u01d1\7Q\2\2\u01d1\u01d2"+
		"\7P\2\2\u01d2\u01e8\7I\2\2\u01d3\u01d4\7n\2\2\u01d4\u01d5\7q\2\2\u01d5"+
		"\u01d6\7p\2\2\u01d6\u01e8\7i\2\2\u01d7\u01d8\7Y\2\2\u01d8\u01d9\7Q\2\2"+
		"\u01d9\u01da\7T\2\2\u01da\u01e8\7F\2\2\u01db\u01dc\7y\2\2\u01dc\u01dd"+
		"\7q\2\2\u01dd\u01de\7t\2\2\u01de\u01e8\7f\2\2\u01df\u01e0\7D\2\2\u01e0"+
		"\u01e1\7[\2\2\u01e1\u01e2\7V\2\2\u01e2\u01e8\7G\2\2\u01e3\u01e4\7d\2\2"+
		"\u01e4\u01e5\7{\2\2\u01e5\u01e6\7v\2\2\u01e6\u01e8\7g\2\2\u01e7\u01cf"+
		"\3\2\2\2\u01e7\u01d3\3\2\2\2\u01e7\u01d7\3\2\2\2\u01e7\u01db\3\2\2\2\u01e7"+
		"\u01df\3\2\2\2\u01e7\u01e3\3\2\2\2\u01e8z\3\2\2\2\u01e9\u01ea\7R\2\2\u01ea"+
		"\u01eb\7V\2\2\u01eb\u01ec\7T\2\2\u01ec\u01fa\7C\2\2\u01ed\u01ee\7r\2\2"+
		"\u01ee\u01ef\7v\2\2\u01ef\u01f0\7t\2\2\u01f0\u01fa\7c\2\2\u01f1\u01f2"+
		"\7R\2\2\u01f2\u01f3\7V\2\2\u01f3\u01f4\7T\2\2\u01f4\u01fa\7D\2\2\u01f5"+
		"\u01f6\7r\2\2\u01f6\u01f7\7v\2\2\u01f7\u01f8\7t\2\2\u01f8\u01fa\7d\2\2"+
		"\u01f9\u01e9\3\2\2\2\u01f9\u01ed\3\2\2\2\u01f9\u01f1\3\2\2\2\u01f9\u01f5"+
		"\3\2\2\2\u01fa|\3\2\2\2\u01fb\u01fc\7K\2\2\u01fc\u01fd\7H\2\2\u01fd\u0202"+
		"\7a\2\2\u01fe\u01ff\7k\2\2\u01ff\u0200\7h\2\2\u0200\u0202\7a\2\2\u0201"+
		"\u01fb\3\2\2\2\u0201\u01fe\3\2\2\2\u0202\u0207\3\2\2\2\u0203\u0206\5\3"+
		"\2\2\u0204\u0206\5\5\3\2\u0205\u0203\3\2\2\2\u0205\u0204\3\2\2\2\u0206"+
		"\u0209\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0208\3\2\2\2\u0208\u0215\3\2"+
		"\2\2\u0209\u0207\3\2\2\2\u020a\u020b\7a\2\2\u020b\u020c\7T\2\2\u020c\u020d"+
		"\7G\2\2\u020d\u020e\7V\2\2\u020e\u0215\7a\2\2\u020f\u0210\7a\2\2\u0210"+
		"\u0211\7t\2\2\u0211\u0212\7g\2\2\u0212\u0213\7v\2\2\u0213\u0215\7a\2\2"+
		"\u0214\u0201\3\2\2\2\u0214\u020a\3\2\2\2\u0214\u020f\3\2\2\2\u0215~\3"+
		"\2\2\2\u0216\u021b\5\3\2\2\u0217\u021a\5\3\2\2\u0218\u021a\5\5\3\2\u0219"+
		"\u0217\3\2\2\2\u0219\u0218\3\2\2\2\u021a\u021d\3\2\2\2\u021b\u0219\3\2"+
		"\2\2\u021b\u021c\3\2\2\2\u021c\u0080\3\2\2\2\u021d\u021b\3\2\2\2(\2\u0089"+
		"\u0094\u009c\u00a3\u00ad\u00b8\u00ba\u00c1\u00c3\u00c9\u00ce\u00d0\u00d8"+
		"\u00da\u012f\u0137\u013f\u0147\u014f\u0157\u0165\u0175\u0181\u018d\u0199"+
		"\u01a1\u01ab\u01b3\u01cd\u01e7\u01f9\u0201\u0205\u0207\u0214\u0219\u021b"+
		"\5\2\3\2\3\6\2\3\7\3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}