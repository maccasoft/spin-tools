// Generated from Spin2Parser.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Spin2Parser extends Parser {
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
	public static final int
		RULE_prog = 0, RULE_constantsSection = 1, RULE_constantAssign = 2, RULE_constantEnum = 3, 
		RULE_constantEnumName = 4, RULE_objects = 5, RULE_object = 6, RULE_reference = 7, 
		RULE_filename = 8, RULE_variables = 9, RULE_variable = 10, RULE_method = 11, 
		RULE_parameters = 12, RULE_result = 13, RULE_localvars = 14, RULE_localvar = 15, 
		RULE_statement = 16, RULE_assignment = 17, RULE_function = 18, RULE_data = 19, 
		RULE_dataLine = 20, RULE_label = 21, RULE_condition = 22, RULE_opcode = 23, 
		RULE_argument = 24, RULE_prefix = 25, RULE_effect = 26, RULE_dataValue = 27, 
		RULE_expression = 28, RULE_atom = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objects", "object", "reference", "filename", "variables", "variable", 
			"method", "parameters", "result", "localvars", "localvar", "statement", 
			"assignment", "function", "data", "dataLine", "label", "condition", "opcode", 
			"argument", "prefix", "effect", "dataValue", "expression", "atom"
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

	@Override
	public String getGrammarFileName() { return "Spin2Parser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Spin2Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(Spin2Parser.EOF, 0); }
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<ConstantsSectionContext> constantsSection() {
			return getRuleContexts(ConstantsSectionContext.class);
		}
		public ConstantsSectionContext constantsSection(int i) {
			return getRuleContext(ConstantsSectionContext.class,i);
		}
		public List<ObjectsContext> objects() {
			return getRuleContexts(ObjectsContext.class);
		}
		public ObjectsContext objects(int i) {
			return getRuleContext(ObjectsContext.class,i);
		}
		public List<VariablesContext> variables() {
			return getRuleContexts(VariablesContext.class);
		}
		public VariablesContext variables(int i) {
			return getRuleContext(VariablesContext.class,i);
		}
		public List<MethodContext> method() {
			return getRuleContexts(MethodContext.class);
		}
		public MethodContext method(int i) {
			return getRuleContext(MethodContext.class,i);
		}
		public List<DataContext> data() {
			return getRuleContexts(DataContext.class);
		}
		public DataContext data(int i) {
			return getRuleContext(DataContext.class,i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(60);
				match(NL);
				}
				}
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(71);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(66);
					constantsSection();
					}
					break;
				case OBJ_START:
					{
					setState(67);
					objects();
					}
					break;
				case VAR_START:
					{
					setState(68);
					variables();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(69);
					method();
					}
					break;
				case DAT_START:
					{
					setState(70);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(76);
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

	public static class ConstantsSectionContext extends ParserRuleContext {
		public List<TerminalNode> CON_START() { return getTokens(Spin2Parser.CON_START); }
		public TerminalNode CON_START(int i) {
			return getToken(Spin2Parser.CON_START, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<ConstantEnumContext> constantEnum() {
			return getRuleContexts(ConstantEnumContext.class);
		}
		public ConstantEnumContext constantEnum(int i) {
			return getRuleContext(ConstantEnumContext.class,i);
		}
		public List<ConstantAssignContext> constantAssign() {
			return getRuleContexts(ConstantAssignContext.class);
		}
		public ConstantAssignContext constantAssign(int i) {
			return getRuleContext(ConstantAssignContext.class,i);
		}
		public List<ConstantEnumNameContext> constantEnumName() {
			return getRuleContexts(ConstantEnumNameContext.class);
		}
		public ConstantEnumNameContext constantEnumName(int i) {
			return getRuleContext(ConstantEnumNameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public ConstantsSectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantsSection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstantsSection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstantsSection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstantsSection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantsSectionContext constantsSection() throws RecognitionException {
		ConstantsSectionContext _localctx = new ConstantsSectionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_constantsSection);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(79); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(78);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(81); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(83);
				match(NL);
				}
				}
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INDENT) | (1L << POUND) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(106);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					{
					setState(89);
					constantAssign();
					setState(94);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(90);
						match(COMMA);
						setState(91);
						constantAssign();
						}
						}
						setState(96);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case 2:
					{
					setState(97);
					constantEnum();
					}
					break;
				case 3:
					{
					{
					setState(98);
					constantEnumName();
					setState(103);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(99);
						match(COMMA);
						setState(100);
						constantEnumName();
						}
						}
						setState(105);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				}
				}
				setState(110);
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

	public static class ConstantAssignContext extends ParserRuleContext {
		public Token name;
		public ExpressionContext exp;
		public TerminalNode EQUAL() { return getToken(Spin2Parser.EQUAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public ConstantAssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantAssign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstantAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstantAssign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstantAssign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantAssignContext constantAssign() throws RecognitionException {
		ConstantAssignContext _localctx = new ConstantAssignContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constantAssign);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(111);
				match(INDENT);
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
			((ConstantAssignContext)_localctx).name = match(IDENTIFIER);
			setState(118);
			match(EQUAL);
			setState(119);
			((ConstantAssignContext)_localctx).exp = expression(0);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(120);
				_la = _input.LA(1);
				if ( !(_la==DEDENT || _la==NL) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(125);
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

	public static class ConstantEnumContext extends ParserRuleContext {
		public ExpressionContext start;
		public ExpressionContext step;
		public TerminalNode POUND() { return getToken(Spin2Parser.POUND, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public List<ConstantEnumNameContext> constantEnumName() {
			return getRuleContexts(ConstantEnumNameContext.class);
		}
		public ConstantEnumNameContext constantEnumName(int i) {
			return getRuleContext(ConstantEnumNameContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public ConstantEnumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantEnum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstantEnum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstantEnum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstantEnum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantEnumContext constantEnum() throws RecognitionException {
		ConstantEnumContext _localctx = new ConstantEnumContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_constantEnum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(126);
				match(INDENT);
				}
				}
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(132);
			match(POUND);
			setState(133);
			((ConstantEnumContext)_localctx).start = expression(0);
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(134);
				match(OPEN_BRACKET);
				setState(135);
				((ConstantEnumContext)_localctx).step = expression(0);
				setState(136);
				match(CLOSE_BRACKET);
				}
			}

			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(140);
				_la = _input.LA(1);
				if ( !(_la==DEDENT || _la==NL) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(146);
				match(COMMA);
				setState(147);
				constantEnumName();
				}
				}
				setState(152);
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

	public static class ConstantEnumNameContext extends ParserRuleContext {
		public Token name;
		public ExpressionContext multiplier;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public ConstantEnumNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantEnumName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstantEnumName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstantEnumName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstantEnumName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantEnumNameContext constantEnumName() throws RecognitionException {
		ConstantEnumNameContext _localctx = new ConstantEnumNameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_constantEnumName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(153);
				match(INDENT);
				}
				}
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(159);
			((ConstantEnumNameContext)_localctx).name = match(IDENTIFIER);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(160);
				match(OPEN_BRACKET);
				setState(161);
				((ConstantEnumNameContext)_localctx).multiplier = expression(0);
				setState(162);
				match(CLOSE_BRACKET);
				}
			}

			setState(169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(166);
				_la = _input.LA(1);
				if ( !(_la==DEDENT || _la==NL) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(171);
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

	public static class ObjectsContext extends ParserRuleContext {
		public List<TerminalNode> OBJ_START() { return getTokens(Spin2Parser.OBJ_START); }
		public TerminalNode OBJ_START(int i) {
			return getToken(Spin2Parser.OBJ_START, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<ObjectContext> object() {
			return getRuleContexts(ObjectContext.class);
		}
		public ObjectContext object(int i) {
			return getRuleContext(ObjectContext.class,i);
		}
		public ObjectsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objects; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterObjects(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitObjects(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitObjects(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectsContext objects() throws RecognitionException {
		ObjectsContext _localctx = new ObjectsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_objects);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(173); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(172);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(175); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(177);
				match(NL);
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER) {
				{
				{
				setState(183);
				object();
				}
				}
				setState(188);
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

	public static class ObjectContext extends ParserRuleContext {
		public ReferenceContext reference() {
			return getRuleContext(ReferenceContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public FilenameContext filename() {
			return getRuleContext(FilenameContext.class,0);
		}
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(Spin2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(Spin2Parser.OPEN_BRACKET, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(Spin2Parser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(Spin2Parser.CLOSE_BRACKET, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_object);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			reference();
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(190);
				match(OPEN_BRACKET);
				setState(191);
				expression(0);
				setState(192);
				match(CLOSE_BRACKET);
				}
				}
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(199);
			match(COLON);
			setState(200);
			filename();
			setState(202); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(201);
				match(NL);
				}
				}
				setState(204); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
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

	public static class ReferenceContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public ReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReferenceContext reference() throws RecognitionException {
		ReferenceContext _localctx = new ReferenceContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(IDENTIFIER);
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

	public static class FilenameContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(Spin2Parser.STRING, 0); }
		public FilenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterFilename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitFilename(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitFilename(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilenameContext filename() throws RecognitionException {
		FilenameContext _localctx = new FilenameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_filename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(STRING);
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

	public static class VariablesContext extends ParserRuleContext {
		public List<TerminalNode> VAR_START() { return getTokens(Spin2Parser.VAR_START); }
		public TerminalNode VAR_START(int i) {
			return getToken(Spin2Parser.VAR_START, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public VariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitVariables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariablesContext variables() throws RecognitionException {
		VariablesContext _localctx = new VariablesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_variables);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(211); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(210);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(213); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(215);
				match(NL);
				}
				}
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(224);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(221);
				variable();
				}
				}
				setState(226);
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

	public static class VariableContext extends ParserRuleContext {
		public Token type;
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(Spin2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(Spin2Parser.OPEN_BRACKET, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(Spin2Parser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(Spin2Parser.CLOSE_BRACKET, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			((VariableContext)_localctx).type = match(TYPE);
			setState(228);
			match(IDENTIFIER);
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(229);
				match(OPEN_BRACKET);
				setState(230);
				expression(0);
				setState(231);
				match(CLOSE_BRACKET);
				}
			}

			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(235);
				match(COMMA);
				setState(236);
				match(IDENTIFIER);
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(237);
					match(OPEN_BRACKET);
					setState(238);
					expression(0);
					setState(239);
					match(CLOSE_BRACKET);
					}
				}

				}
				}
				setState(247);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(249); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(248);
				match(NL);
				}
				}
				setState(251); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
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

	public static class MethodContext extends ParserRuleContext {
		public Token name;
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public TerminalNode PUB_START() { return getToken(Spin2Parser.PUB_START, 0); }
		public TerminalNode PRI_START() { return getToken(Spin2Parser.PRI_START, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
		public TerminalNode BIN_OR() { return getToken(Spin2Parser.BIN_OR, 0); }
		public LocalvarsContext localvars() {
			return getRuleContext(LocalvarsContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(254);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(255);
			match(OPEN_PAREN);
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(256);
				parameters();
				}
			}

			setState(259);
			match(CLOSE_PAREN);
			setState(262);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(260);
				match(COLON);
				setState(261);
				result();
				}
			}

			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(264);
				match(BIN_OR);
				setState(265);
				localvars();
				}
			}

			setState(269); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(268);
				match(NL);
				}
				}
				setState(271); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(276);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==REPEAT || _la==IDENTIFIER) {
				{
				{
				setState(273);
				statement();
				}
				}
				setState(278);
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

	public static class ParametersContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(279);
			match(IDENTIFIER);
			setState(284);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(280);
				match(COMMA);
				setState(281);
				match(IDENTIFIER);
				}
				}
				setState(286);
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

	public static class ResultContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public ResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitResult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitResult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_result);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			match(IDENTIFIER);
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(288);
				match(COMMA);
				setState(289);
				match(IDENTIFIER);
				}
				}
				setState(294);
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

	public static class LocalvarsContext extends ParserRuleContext {
		public List<LocalvarContext> localvar() {
			return getRuleContexts(LocalvarContext.class);
		}
		public LocalvarContext localvar(int i) {
			return getRuleContext(LocalvarContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public LocalvarsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localvars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterLocalvars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitLocalvars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitLocalvars(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocalvarsContext localvars() throws RecognitionException {
		LocalvarsContext _localctx = new LocalvarsContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_localvars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			localvar();
			setState(300);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(296);
				match(COMMA);
				setState(297);
				localvar();
				}
				}
				setState(302);
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

	public static class LocalvarContext extends ParserRuleContext {
		public Token align;
		public Token vartype;
		public Token name;
		public ExpressionContext count;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode ALIGN() { return getToken(Spin2Parser.ALIGN, 0); }
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LocalvarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localvar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterLocalvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitLocalvar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitLocalvar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocalvarContext localvar() throws RecognitionException {
		LocalvarContext _localctx = new LocalvarContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_localvar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(303);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(307);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(306);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(309);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(310);
				match(OPEN_BRACKET);
				setState(311);
				((LocalvarContext)_localctx).count = expression(0);
				setState(312);
				match(CLOSE_BRACKET);
				}
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

	public static class StatementContext extends ParserRuleContext {
		public TerminalNode REPEAT() { return getToken(Spin2Parser.REPEAT, 0); }
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_statement);
		int _la;
		try {
			setState(324);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				match(REPEAT);
				setState(318); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(317);
					match(NL);
					}
					}
					setState(320); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				assignment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(323);
				function();
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

	public static class AssignmentContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode ASSIGN() { return getToken(Spin2Parser.ASSIGN, 0); }
		public TerminalNode ADD_ASSIGN() { return getToken(Spin2Parser.ADD_ASSIGN, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(IDENTIFIER);
			setState(327);
			_la = _input.LA(1);
			if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(331);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(328);
				function();
				}
				break;
			case 2:
				{
				setState(329);
				match(IDENTIFIER);
				}
				break;
			case 3:
				{
				setState(330);
				expression(0);
				}
				break;
			}
			setState(334); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(333);
				match(NL);
				}
				}
				setState(336); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
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

	public static class FunctionContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_function);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(338);
			match(IDENTIFIER);
			setState(339);
			match(OPEN_PAREN);
			setState(348);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(340);
				match(IDENTIFIER);
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(341);
					match(COMMA);
					setState(342);
					match(IDENTIFIER);
					}
					}
					setState(347);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(350);
			match(CLOSE_PAREN);
			setState(352); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(351);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(354); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class DataContext extends ParserRuleContext {
		public List<TerminalNode> DAT_START() { return getTokens(Spin2Parser.DAT_START); }
		public TerminalNode DAT_START(int i) {
			return getToken(Spin2Parser.DAT_START, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<DataLineContext> dataLine() {
			return getRuleContexts(DataLineContext.class);
		}
		public DataLineContext dataLine(int i) {
			return getRuleContext(DataLineContext.class,i);
		}
		public DataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterData(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitData(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitData(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataContext data() throws RecognitionException {
		DataContext _localctx = new DataContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_data);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(357); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(356);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(359); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(364);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(361);
					match(NL);
					}
					} 
				}
				setState(366);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			}
			setState(370);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(367);
					dataLine();
					}
					} 
				}
				setState(372);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
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

	public static class DataLineContext extends ParserRuleContext {
		public Token directive;
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public TerminalNode ORG() { return getToken(Spin2Parser.ORG, 0); }
		public TerminalNode ORGH() { return getToken(Spin2Parser.ORGH, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public List<DataValueContext> dataValue() {
			return getRuleContexts(DataValueContext.class);
		}
		public DataValueContext dataValue(int i) {
			return getRuleContext(DataValueContext.class,i);
		}
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public TerminalNode RES() { return getToken(Spin2Parser.RES, 0); }
		public OpcodeContext opcode() {
			return getRuleContext(OpcodeContext.class,0);
		}
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public DataLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterDataLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitDataLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitDataLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataLineContext dataLine() throws RecognitionException {
		DataLineContext _localctx = new DataLineContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(578);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(373);
				label();
				setState(375); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(374);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(377); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(382);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(379);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(384);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(388);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(385);
					match(INDENT);
					}
					}
					setState(390);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(391);
				((DataLineContext)_localctx).directive = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ORG || _la==ORGH) ) {
					((DataLineContext)_localctx).directive = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(397);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << FLOAT) | (1L << ROUND) | (1L << TRUNC) | (1L << PTR) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(392);
					expression(0);
					setState(395);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(393);
						match(COMMA);
						setState(394);
						expression(0);
						}
					}

					}
				}

				setState(400); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(399);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(402); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(407);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(404);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(409);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(413);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(410);
						match(INDENT);
						}
						} 
					}
					setState(415);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
				}
				setState(417);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(416);
					label();
					}
					break;
				}
				setState(419);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(420);
				dataValue();
				setState(425);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(421);
					match(COMMA);
					setState(422);
					dataValue();
					}
					}
					setState(427);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(429); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(428);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(431); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(436);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(433);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(438);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(442);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(439);
						match(INDENT);
						}
						} 
					}
					setState(444);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
				}
				setState(446);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
				case 1:
					{
					setState(445);
					label();
					}
					break;
				}
				setState(448);
				((DataLineContext)_localctx).directive = match(RES);
				setState(449);
				dataValue();
				setState(451); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(450);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(453); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(458);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(455);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(460);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(464);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(461);
						match(INDENT);
						}
						} 
					}
					setState(466);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
				}
				setState(468);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
				case 1:
					{
					setState(467);
					label();
					}
					break;
				}
				setState(471);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
				case 1:
					{
					setState(470);
					condition();
					}
					break;
				}
				setState(473);
				opcode();
				setState(474);
				argument();
				setState(475);
				match(COMMA);
				setState(476);
				argument();
				setState(477);
				match(COMMA);
				setState(478);
				argument();
				setState(480);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
				case 1:
					{
					setState(479);
					effect();
					}
					break;
				}
				setState(483); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(482);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(485); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(490);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(487);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(492);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(496);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(493);
						match(INDENT);
						}
						} 
					}
					setState(498);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
				}
				setState(500);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
				case 1:
					{
					setState(499);
					label();
					}
					break;
				}
				setState(503);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					setState(502);
					condition();
					}
					break;
				}
				setState(505);
				opcode();
				setState(506);
				argument();
				setState(507);
				match(COMMA);
				setState(508);
				argument();
				setState(510);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(509);
					effect();
					}
					break;
				}
				setState(513); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(512);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(515); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(520);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(517);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(522);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(526);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,79,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(523);
						match(INDENT);
						}
						} 
					}
					setState(528);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,79,_ctx);
				}
				setState(530);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
				case 1:
					{
					setState(529);
					label();
					}
					break;
				}
				setState(533);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
				case 1:
					{
					setState(532);
					condition();
					}
					break;
				}
				setState(535);
				opcode();
				setState(536);
				argument();
				setState(538);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
				case 1:
					{
					setState(537);
					effect();
					}
					break;
				}
				setState(541); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(540);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(543); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(548);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(545);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(550);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(554);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(551);
						match(INDENT);
						}
						} 
					}
					setState(556);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
				}
				setState(558);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(557);
					label();
					}
					break;
				}
				setState(561);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(560);
					condition();
					}
					break;
				}
				setState(563);
				opcode();
				setState(565);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
				case 1:
					{
					setState(564);
					effect();
					}
					break;
				}
				setState(568); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(567);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(570); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(575);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(572);
						_la = _input.LA(1);
						if ( !(_la==DEDENT || _la==NL) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						} 
					}
					setState(577);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				}
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

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode DOT() { return getToken(Spin2Parser.DOT, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(580);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(582);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(581);
				match(DOT);
				}
			}

			setState(584);
			match(IDENTIFIER);
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

	public static class ConditionContext extends ParserRuleContext {
		public TerminalNode CONDITION() { return getToken(Spin2Parser.CONDITION, 0); }
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(587);
			match(CONDITION);
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

	public static class OpcodeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public OpcodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_opcode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterOpcode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitOpcode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitOpcode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpcodeContext opcode() throws RecognitionException {
		OpcodeContext _localctx = new OpcodeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_opcode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(590);
			match(IDENTIFIER);
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

	public static class ArgumentContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PrefixContext prefix() {
			return getRuleContext(PrefixContext.class,0);
		}
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(593);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==POUND_POUND || _la==POUND) {
				{
				setState(592);
				prefix();
				}
			}

			setState(595);
			expression(0);
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

	public static class PrefixContext extends ParserRuleContext {
		public TerminalNode POUND_POUND() { return getToken(Spin2Parser.POUND_POUND, 0); }
		public TerminalNode POUND() { return getToken(Spin2Parser.POUND, 0); }
		public TerminalNode BACKSLASH() { return getToken(Spin2Parser.BACKSLASH, 0); }
		public PrefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitPrefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitPrefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrefixContext prefix() throws RecognitionException {
		PrefixContext _localctx = new PrefixContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(597);
			_la = _input.LA(1);
			if ( !(_la==POUND_POUND || _la==POUND) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(599);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(598);
				match(BACKSLASH);
				}
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

	public static class EffectContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(601);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(602);
			match(IDENTIFIER);
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

	public static class DataValueContext extends ParserRuleContext {
		public ExpressionContext count;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public DataValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterDataValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitDataValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitDataValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataValueContext dataValue() throws RecognitionException {
		DataValueContext _localctx = new DataValueContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			expression(0);
			setState(609);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(605);
				match(OPEN_BRACKET);
				setState(606);
				((DataValueContext)_localctx).count = expression(0);
				setState(607);
				match(CLOSE_BRACKET);
				}
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

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext left;
		public Token operator;
		public ExpressionContext exp;
		public ExpressionContext right;
		public ExpressionContext middle;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(Spin2Parser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(Spin2Parser.MINUS, 0); }
		public TerminalNode TILDE() { return getToken(Spin2Parser.TILDE, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public TerminalNode FLOAT() { return getToken(Spin2Parser.FLOAT, 0); }
		public TerminalNode ROUND() { return getToken(Spin2Parser.ROUND, 0); }
		public TerminalNode TRUNC() { return getToken(Spin2Parser.TRUNC, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode AT() { return getToken(Spin2Parser.AT, 0); }
		public TerminalNode LEFT_SHIFT() { return getToken(Spin2Parser.LEFT_SHIFT, 0); }
		public TerminalNode RIGHT_SHIFT() { return getToken(Spin2Parser.RIGHT_SHIFT, 0); }
		public TerminalNode BIN_AND() { return getToken(Spin2Parser.BIN_AND, 0); }
		public TerminalNode BIN_XOR() { return getToken(Spin2Parser.BIN_XOR, 0); }
		public TerminalNode BIN_OR() { return getToken(Spin2Parser.BIN_OR, 0); }
		public TerminalNode STAR() { return getToken(Spin2Parser.STAR, 0); }
		public TerminalNode DIV() { return getToken(Spin2Parser.DIV, 0); }
		public TerminalNode ADDPINS() { return getToken(Spin2Parser.ADDPINS, 0); }
		public TerminalNode LOGICAL_AND() { return getToken(Spin2Parser.LOGICAL_AND, 0); }
		public TerminalNode LOGICAL_XOR() { return getToken(Spin2Parser.LOGICAL_XOR, 0); }
		public TerminalNode LOGICAL_OR() { return getToken(Spin2Parser.LOGICAL_OR, 0); }
		public TerminalNode QUESTION() { return getToken(Spin2Parser.QUESTION, 0); }
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(612);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TILDE))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(613);
				((ExpressionContext)_localctx).exp = expression(15);
				}
				break;
			case FLOAT:
			case ROUND:
			case TRUNC:
				{
				setState(614);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FLOAT) | (1L << ROUND) | (1L << TRUNC))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(615);
				match(OPEN_PAREN);
				setState(616);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(617);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(619);
				match(OPEN_PAREN);
				setState(620);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(621);
				match(CLOSE_PAREN);
				}
				break;
			case STRING:
			case QUAD:
			case BIN:
			case HEX:
			case NUMBER:
			case PLUS_PLUS:
			case MINUS_MINUS:
			case AT:
			case DOLLAR:
			case DOT:
			case PTR:
			case IDENTIFIER:
				{
				setState(624);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(623);
					match(AT);
					}
				}

				setState(626);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(667);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,99,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(665);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(629);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(630);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LEFT_SHIFT || _la==RIGHT_SHIFT) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(631);
						((ExpressionContext)_localctx).right = expression(15);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(632);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(633);
						((ExpressionContext)_localctx).operator = match(BIN_AND);
						setState(634);
						((ExpressionContext)_localctx).right = expression(14);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(635);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(636);
						((ExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(637);
						((ExpressionContext)_localctx).right = expression(13);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(638);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(639);
						((ExpressionContext)_localctx).operator = match(BIN_OR);
						setState(640);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(641);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(642);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==STAR || _la==DIV) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(643);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(644);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(645);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(646);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(647);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(648);
						((ExpressionContext)_localctx).operator = match(ADDPINS);
						setState(649);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(650);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(651);
						((ExpressionContext)_localctx).operator = match(LOGICAL_AND);
						setState(652);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(653);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(654);
						((ExpressionContext)_localctx).operator = match(LOGICAL_XOR);
						setState(655);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(656);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(657);
						((ExpressionContext)_localctx).operator = match(LOGICAL_OR);
						setState(658);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(659);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(660);
						((ExpressionContext)_localctx).operator = match(QUESTION);
						setState(661);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(662);
						((ExpressionContext)_localctx).operator = match(COLON);
						setState(663);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(669);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,99,_ctx);
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

	public static class AtomContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(Spin2Parser.NUMBER, 0); }
		public TerminalNode HEX() { return getToken(Spin2Parser.HEX, 0); }
		public TerminalNode BIN() { return getToken(Spin2Parser.BIN, 0); }
		public TerminalNode QUAD() { return getToken(Spin2Parser.QUAD, 0); }
		public TerminalNode STRING() { return getToken(Spin2Parser.STRING, 0); }
		public TerminalNode DOLLAR() { return getToken(Spin2Parser.DOLLAR, 0); }
		public TerminalNode PTR() { return getToken(Spin2Parser.PTR, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode PLUS_PLUS() { return getToken(Spin2Parser.PLUS_PLUS, 0); }
		public TerminalNode MINUS_MINUS() { return getToken(Spin2Parser.MINUS_MINUS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode DOT() { return getToken(Spin2Parser.DOT, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_atom);
		int _la;
		try {
			setState(712);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(670);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(671);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(672);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(673);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(674);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(675);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(676);
				match(PTR);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(677);
				match(PTR);
				setState(682);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
				case 1:
					{
					setState(678);
					match(OPEN_BRACKET);
					setState(679);
					expression(0);
					setState(680);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(684);
				match(PTR);
				setState(685);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(690);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
				case 1:
					{
					setState(686);
					match(OPEN_BRACKET);
					setState(687);
					expression(0);
					setState(688);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(692);
				match(PTR);
				setState(697);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(693);
					match(OPEN_BRACKET);
					setState(694);
					expression(0);
					setState(695);
					match(CLOSE_BRACKET);
					}
				}

				setState(699);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(700);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(701);
				match(PTR);
				setState(706);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
				case 1:
					{
					setState(702);
					match(OPEN_BRACKET);
					setState(703);
					expression(0);
					setState(704);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(709);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(708);
					match(DOT);
					}
				}

				setState(711);
				match(IDENTIFIER);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 21:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 22:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 23:
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 26:
			return effect_sempred((EffectContext)_localctx, predIndex);
		case 28:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean label_sempred(LabelContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return _input.LT(1).getCharPositionInLine() == 0;
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean opcode_sempred(OpcodeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean effect_sempred(EffectContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 14);
		case 5:
			return precpred(_ctx, 13);
		case 6:
			return precpred(_ctx, 12);
		case 7:
			return precpred(_ctx, 11);
		case 8:
			return precpred(_ctx, 10);
		case 9:
			return precpred(_ctx, 9);
		case 10:
			return precpred(_ctx, 8);
		case 11:
			return precpred(_ctx, 7);
		case 12:
			return precpred(_ctx, 6);
		case 13:
			return precpred(_ctx, 5);
		case 14:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3A\u02cd\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\7\2@"+
		"\n\2\f\2\16\2C\13\2\3\2\3\2\3\2\3\2\3\2\7\2J\n\2\f\2\16\2M\13\2\3\2\3"+
		"\2\3\3\6\3R\n\3\r\3\16\3S\3\3\7\3W\n\3\f\3\16\3Z\13\3\3\3\3\3\3\3\7\3"+
		"_\n\3\f\3\16\3b\13\3\3\3\3\3\3\3\3\3\7\3h\n\3\f\3\16\3k\13\3\7\3m\n\3"+
		"\f\3\16\3p\13\3\3\4\7\4s\n\4\f\4\16\4v\13\4\3\4\3\4\3\4\3\4\7\4|\n\4\f"+
		"\4\16\4\177\13\4\3\5\7\5\u0082\n\5\f\5\16\5\u0085\13\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\5\5\u008d\n\5\3\5\7\5\u0090\n\5\f\5\16\5\u0093\13\5\3\5\3\5\7"+
		"\5\u0097\n\5\f\5\16\5\u009a\13\5\3\6\7\6\u009d\n\6\f\6\16\6\u00a0\13\6"+
		"\3\6\3\6\3\6\3\6\3\6\5\6\u00a7\n\6\3\6\7\6\u00aa\n\6\f\6\16\6\u00ad\13"+
		"\6\3\7\6\7\u00b0\n\7\r\7\16\7\u00b1\3\7\7\7\u00b5\n\7\f\7\16\7\u00b8\13"+
		"\7\3\7\7\7\u00bb\n\7\f\7\16\7\u00be\13\7\3\b\3\b\3\b\3\b\3\b\7\b\u00c5"+
		"\n\b\f\b\16\b\u00c8\13\b\3\b\3\b\3\b\6\b\u00cd\n\b\r\b\16\b\u00ce\3\t"+
		"\3\t\3\n\3\n\3\13\6\13\u00d6\n\13\r\13\16\13\u00d7\3\13\7\13\u00db\n\13"+
		"\f\13\16\13\u00de\13\13\3\13\7\13\u00e1\n\13\f\13\16\13\u00e4\13\13\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\5\f\u00ec\n\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00f4"+
		"\n\f\7\f\u00f6\n\f\f\f\16\f\u00f9\13\f\3\f\6\f\u00fc\n\f\r\f\16\f\u00fd"+
		"\3\r\3\r\3\r\3\r\5\r\u0104\n\r\3\r\3\r\3\r\5\r\u0109\n\r\3\r\3\r\5\r\u010d"+
		"\n\r\3\r\6\r\u0110\n\r\r\r\16\r\u0111\3\r\7\r\u0115\n\r\f\r\16\r\u0118"+
		"\13\r\3\16\3\16\3\16\7\16\u011d\n\16\f\16\16\16\u0120\13\16\3\17\3\17"+
		"\3\17\7\17\u0125\n\17\f\17\16\17\u0128\13\17\3\20\3\20\3\20\7\20\u012d"+
		"\n\20\f\20\16\20\u0130\13\20\3\21\5\21\u0133\n\21\3\21\5\21\u0136\n\21"+
		"\3\21\3\21\3\21\3\21\3\21\5\21\u013d\n\21\3\22\3\22\6\22\u0141\n\22\r"+
		"\22\16\22\u0142\3\22\3\22\5\22\u0147\n\22\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u014e\n\23\3\23\6\23\u0151\n\23\r\23\16\23\u0152\3\24\3\24\3\24\3\24"+
		"\3\24\7\24\u015a\n\24\f\24\16\24\u015d\13\24\5\24\u015f\n\24\3\24\3\24"+
		"\6\24\u0163\n\24\r\24\16\24\u0164\3\25\6\25\u0168\n\25\r\25\16\25\u0169"+
		"\3\25\7\25\u016d\n\25\f\25\16\25\u0170\13\25\3\25\7\25\u0173\n\25\f\25"+
		"\16\25\u0176\13\25\3\26\3\26\6\26\u017a\n\26\r\26\16\26\u017b\3\26\7\26"+
		"\u017f\n\26\f\26\16\26\u0182\13\26\3\26\7\26\u0185\n\26\f\26\16\26\u0188"+
		"\13\26\3\26\3\26\3\26\3\26\5\26\u018e\n\26\5\26\u0190\n\26\3\26\6\26\u0193"+
		"\n\26\r\26\16\26\u0194\3\26\7\26\u0198\n\26\f\26\16\26\u019b\13\26\3\26"+
		"\7\26\u019e\n\26\f\26\16\26\u01a1\13\26\3\26\5\26\u01a4\n\26\3\26\3\26"+
		"\3\26\3\26\7\26\u01aa\n\26\f\26\16\26\u01ad\13\26\3\26\6\26\u01b0\n\26"+
		"\r\26\16\26\u01b1\3\26\7\26\u01b5\n\26\f\26\16\26\u01b8\13\26\3\26\7\26"+
		"\u01bb\n\26\f\26\16\26\u01be\13\26\3\26\5\26\u01c1\n\26\3\26\3\26\3\26"+
		"\6\26\u01c6\n\26\r\26\16\26\u01c7\3\26\7\26\u01cb\n\26\f\26\16\26\u01ce"+
		"\13\26\3\26\7\26\u01d1\n\26\f\26\16\26\u01d4\13\26\3\26\5\26\u01d7\n\26"+
		"\3\26\5\26\u01da\n\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u01e3\n"+
		"\26\3\26\6\26\u01e6\n\26\r\26\16\26\u01e7\3\26\7\26\u01eb\n\26\f\26\16"+
		"\26\u01ee\13\26\3\26\7\26\u01f1\n\26\f\26\16\26\u01f4\13\26\3\26\5\26"+
		"\u01f7\n\26\3\26\5\26\u01fa\n\26\3\26\3\26\3\26\3\26\3\26\5\26\u0201\n"+
		"\26\3\26\6\26\u0204\n\26\r\26\16\26\u0205\3\26\7\26\u0209\n\26\f\26\16"+
		"\26\u020c\13\26\3\26\7\26\u020f\n\26\f\26\16\26\u0212\13\26\3\26\5\26"+
		"\u0215\n\26\3\26\5\26\u0218\n\26\3\26\3\26\3\26\5\26\u021d\n\26\3\26\6"+
		"\26\u0220\n\26\r\26\16\26\u0221\3\26\7\26\u0225\n\26\f\26\16\26\u0228"+
		"\13\26\3\26\7\26\u022b\n\26\f\26\16\26\u022e\13\26\3\26\5\26\u0231\n\26"+
		"\3\26\5\26\u0234\n\26\3\26\3\26\5\26\u0238\n\26\3\26\6\26\u023b\n\26\r"+
		"\26\16\26\u023c\3\26\7\26\u0240\n\26\f\26\16\26\u0243\13\26\5\26\u0245"+
		"\n\26\3\27\3\27\5\27\u0249\n\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\32\5\32\u0254\n\32\3\32\3\32\3\33\3\33\5\33\u025a\n\33\3\34\3\34\3"+
		"\34\3\35\3\35\3\35\3\35\3\35\5\35\u0264\n\35\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\5\36\u0273\n\36\3\36\5\36\u0276"+
		"\n\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\7\36\u029c\n\36\f\36\16"+
		"\36\u029f\13\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\5\37\u02ad\n\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u02b5\n\37\3"+
		"\37\3\37\3\37\3\37\3\37\5\37\u02bc\n\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\5\37\u02c5\n\37\3\37\5\37\u02c8\n\37\3\37\5\37\u02cb\n\37\3\37\2"+
		"\3: \2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<\2"+
		"\r\4\2\4\4\7\7\3\2\62\63\3\2\26\27\3\2:;\4\2\21\21\34\34\4\2#$))\3\2\67"+
		"9\3\2\22\23\3\2%&\3\2#$\3\2\24\25\2\u0339\2A\3\2\2\2\4Q\3\2\2\2\6t\3\2"+
		"\2\2\b\u0083\3\2\2\2\n\u009e\3\2\2\2\f\u00af\3\2\2\2\16\u00bf\3\2\2\2"+
		"\20\u00d0\3\2\2\2\22\u00d2\3\2\2\2\24\u00d5\3\2\2\2\26\u00e5\3\2\2\2\30"+
		"\u00ff\3\2\2\2\32\u0119\3\2\2\2\34\u0121\3\2\2\2\36\u0129\3\2\2\2 \u0132"+
		"\3\2\2\2\"\u0146\3\2\2\2$\u0148\3\2\2\2&\u0154\3\2\2\2(\u0167\3\2\2\2"+
		"*\u0244\3\2\2\2,\u0246\3\2\2\2.\u024c\3\2\2\2\60\u024f\3\2\2\2\62\u0253"+
		"\3\2\2\2\64\u0257\3\2\2\2\66\u025b\3\2\2\28\u025e\3\2\2\2:\u0275\3\2\2"+
		"\2<\u02ca\3\2\2\2>@\7\7\2\2?>\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2BK"+
		"\3\2\2\2CA\3\2\2\2DJ\5\4\3\2EJ\5\f\7\2FJ\5\24\13\2GJ\5\30\r\2HJ\5(\25"+
		"\2ID\3\2\2\2IE\3\2\2\2IF\3\2\2\2IG\3\2\2\2IH\3\2\2\2JM\3\2\2\2KI\3\2\2"+
		"\2KL\3\2\2\2LN\3\2\2\2MK\3\2\2\2NO\7\2\2\3O\3\3\2\2\2PR\7/\2\2QP\3\2\2"+
		"\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TX\3\2\2\2UW\7\7\2\2VU\3\2\2\2WZ\3\2\2"+
		"\2XV\3\2\2\2XY\3\2\2\2Yn\3\2\2\2ZX\3\2\2\2[`\5\6\4\2\\]\7!\2\2]_\5\6\4"+
		"\2^\\\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2am\3\2\2\2b`\3\2\2\2cm\5\b"+
		"\5\2di\5\n\6\2ef\7!\2\2fh\5\n\6\2ge\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2"+
		"\2\2jm\3\2\2\2ki\3\2\2\2l[\3\2\2\2lc\3\2\2\2ld\3\2\2\2mp\3\2\2\2nl\3\2"+
		"\2\2no\3\2\2\2o\5\3\2\2\2pn\3\2\2\2qs\7\3\2\2rq\3\2\2\2sv\3\2\2\2tr\3"+
		"\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3\2\2\2wx\7A\2\2xy\7\37\2\2y}\5:\36\2z|\t"+
		"\2\2\2{z\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\7\3\2\2\2\177}\3\2"+
		"\2\2\u0080\u0082\7\3\2\2\u0081\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083"+
		"\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0083\3\2"+
		"\2\2\u0086\u0087\7\34\2\2\u0087\u008c\5:\36\2\u0088\u0089\7+\2\2\u0089"+
		"\u008a\5:\36\2\u008a\u008b\7,\2\2\u008b\u008d\3\2\2\2\u008c\u0088\3\2"+
		"\2\2\u008c\u008d\3\2\2\2\u008d\u0091\3\2\2\2\u008e\u0090\t\2\2\2\u008f"+
		"\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2"+
		"\2\2\u0092\u0098\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7!\2\2\u0095"+
		"\u0097\5\n\6\2\u0096\u0094\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2"+
		"\2\2\u0098\u0099\3\2\2\2\u0099\t\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009d"+
		"\7\3\2\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e"+
		"\u009f\3\2\2\2\u009f\u00a1\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a6\7A"+
		"\2\2\u00a2\u00a3\7+\2\2\u00a3\u00a4\5:\36\2\u00a4\u00a5\7,\2\2\u00a5\u00a7"+
		"\3\2\2\2\u00a6\u00a2\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00ab\3\2\2\2\u00a8"+
		"\u00aa\t\2\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2"+
		"\2\2\u00ab\u00ac\3\2\2\2\u00ac\13\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b0"+
		"\7\61\2\2\u00af\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00af\3\2\2\2"+
		"\u00b1\u00b2\3\2\2\2\u00b2\u00b6\3\2\2\2\u00b3\u00b5\7\7\2\2\u00b4\u00b3"+
		"\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7"+
		"\u00bc\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9\u00bb\5\16\b\2\u00ba\u00b9\3"+
		"\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\r\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c6\5\20\t\2\u00c0\u00c1\7+\2\2"+
		"\u00c1\u00c2\5:\36\2\u00c2\u00c3\7,\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c0"+
		"\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7"+
		"\u00c9\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c9\u00ca\7(\2\2\u00ca\u00cc\5\22"+
		"\n\2\u00cb\u00cd\7\7\2\2\u00cc\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\17\3\2\2\2\u00d0\u00d1\7A\2\2"+
		"\u00d1\21\3\2\2\2\u00d2\u00d3\7\t\2\2\u00d3\23\3\2\2\2\u00d4\u00d6\7\60"+
		"\2\2\u00d5\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7"+
		"\u00d8\3\2\2\2\u00d8\u00dc\3\2\2\2\u00d9\u00db\7\7\2\2\u00da\u00d9\3\2"+
		"\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd"+
		"\u00e2\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e1\5\26\f\2\u00e0\u00df\3"+
		"\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3"+
		"\25\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00e6\7>\2\2\u00e6\u00eb\7A\2\2"+
		"\u00e7\u00e8\7+\2\2\u00e8\u00e9\5:\36\2\u00e9\u00ea\7,\2\2\u00ea\u00ec"+
		"\3\2\2\2\u00eb\u00e7\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00f7\3\2\2\2\u00ed"+
		"\u00ee\7!\2\2\u00ee\u00f3\7A\2\2\u00ef\u00f0\7+\2\2\u00f0\u00f1\5:\36"+
		"\2\u00f1\u00f2\7,\2\2\u00f2\u00f4\3\2\2\2\u00f3\u00ef\3\2\2\2\u00f3\u00f4"+
		"\3\2\2\2\u00f4\u00f6\3\2\2\2\u00f5\u00ed\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7"+
		"\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2"+
		"\2\2\u00fa\u00fc\7\7\2\2\u00fb\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd"+
		"\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\27\3\2\2\2\u00ff\u0100\t\3\2"+
		"\2\u0100\u0101\7A\2\2\u0101\u0103\7-\2\2\u0102\u0104\5\32\16\2\u0103\u0102"+
		"\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0108\7.\2\2\u0106"+
		"\u0107\7(\2\2\u0107\u0109\5\34\17\2\u0108\u0106\3\2\2\2\u0108\u0109\3"+
		"\2\2\2\u0109\u010c\3\2\2\2\u010a\u010b\7\31\2\2\u010b\u010d\5\36\20\2"+
		"\u010c\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010f\3\2\2\2\u010e\u0110"+
		"\7\7\2\2\u010f\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2\2\2\u0111"+
		"\u0112\3\2\2\2\u0112\u0116\3\2\2\2\u0113\u0115\5\"\22\2\u0114\u0113\3"+
		"\2\2\2\u0115\u0118\3\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117"+
		"\31\3\2\2\2\u0118\u0116\3\2\2\2\u0119\u011e\7A\2\2\u011a\u011b\7!\2\2"+
		"\u011b\u011d\7A\2\2\u011c\u011a\3\2\2\2\u011d\u0120\3\2\2\2\u011e\u011c"+
		"\3\2\2\2\u011e\u011f\3\2\2\2\u011f\33\3\2\2\2\u0120\u011e\3\2\2\2\u0121"+
		"\u0126\7A\2\2\u0122\u0123\7!\2\2\u0123\u0125\7A\2\2\u0124\u0122\3\2\2"+
		"\2\u0125\u0128\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\35"+
		"\3\2\2\2\u0128\u0126\3\2\2\2\u0129\u012e\5 \21\2\u012a\u012b\7!\2\2\u012b"+
		"\u012d\5 \21\2\u012c\u012a\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3\2"+
		"\2\2\u012e\u012f\3\2\2\2\u012f\37\3\2\2\2\u0130\u012e\3\2\2\2\u0131\u0133"+
		"\7=\2\2\u0132\u0131\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134"+
		"\u0136\7>\2\2\u0135\u0134\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\3\2"+
		"\2\2\u0137\u013c\7A\2\2\u0138\u0139\7+\2\2\u0139\u013a\5:\36\2\u013a\u013b"+
		"\7,\2\2\u013b\u013d\3\2\2\2\u013c\u0138\3\2\2\2\u013c\u013d\3\2\2\2\u013d"+
		"!\3\2\2\2\u013e\u0140\7\65\2\2\u013f\u0141\7\7\2\2\u0140\u013f\3\2\2\2"+
		"\u0141\u0142\3\2\2\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0147"+
		"\3\2\2\2\u0144\u0147\5$\23\2\u0145\u0147\5&\24\2\u0146\u013e\3\2\2\2\u0146"+
		"\u0144\3\2\2\2\u0146\u0145\3\2\2\2\u0147#\3\2\2\2\u0148\u0149\7A\2\2\u0149"+
		"\u014d\t\4\2\2\u014a\u014e\5&\24\2\u014b\u014e\7A\2\2\u014c\u014e\5:\36"+
		"\2\u014d\u014a\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014c\3\2\2\2\u014e\u0150"+
		"\3\2\2\2\u014f\u0151\7\7\2\2\u0150\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152"+
		"\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153%\3\2\2\2\u0154\u0155\7A\2\2\u0155"+
		"\u015e\7-\2\2\u0156\u015b\7A\2\2\u0157\u0158\7!\2\2\u0158\u015a\7A\2\2"+
		"\u0159\u0157\3\2\2\2\u015a\u015d\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c"+
		"\3\2\2\2\u015c\u015f\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0156\3\2\2\2\u015e"+
		"\u015f\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0162\7.\2\2\u0161\u0163\7\7"+
		"\2\2\u0162\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0165\3\2\2\2\u0165\'\3\2\2\2\u0166\u0168\7\64\2\2\u0167\u0166\3\2\2"+
		"\2\u0168\u0169\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016e"+
		"\3\2\2\2\u016b\u016d\7\7\2\2\u016c\u016b\3\2\2\2\u016d\u0170\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0174\3\2\2\2\u0170\u016e\3\2"+
		"\2\2\u0171\u0173\5*\26\2\u0172\u0171\3\2\2\2\u0173\u0176\3\2\2\2\u0174"+
		"\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175)\3\2\2\2\u0176\u0174\3\2\2\2"+
		"\u0177\u0179\5,\27\2\u0178\u017a\7\7\2\2\u0179\u0178\3\2\2\2\u017a\u017b"+
		"\3\2\2\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u0180\3\2\2\2\u017d"+
		"\u017f\t\2\2\2\u017e\u017d\3\2\2\2\u017f\u0182\3\2\2\2\u0180\u017e\3\2"+
		"\2\2\u0180\u0181\3\2\2\2\u0181\u0245\3\2\2\2\u0182\u0180\3\2\2\2\u0183"+
		"\u0185\7\3\2\2\u0184\u0183\3\2\2\2\u0185\u0188\3\2\2\2\u0186\u0184\3\2"+
		"\2\2\u0186\u0187\3\2\2\2\u0187\u0189\3\2\2\2\u0188\u0186\3\2\2\2\u0189"+
		"\u018f\t\5\2\2\u018a\u018d\5:\36\2\u018b\u018c\7!\2\2\u018c\u018e\5:\36"+
		"\2\u018d\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u0190\3\2\2\2\u018f\u018a"+
		"\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0192\3\2\2\2\u0191\u0193\7\7\2\2\u0192"+
		"\u0191\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0192\3\2\2\2\u0194\u0195\3\2"+
		"\2\2\u0195\u0199\3\2\2\2\u0196\u0198\t\2\2\2\u0197\u0196\3\2\2\2\u0198"+
		"\u019b\3\2\2\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u0245\3\2"+
		"\2\2\u019b\u0199\3\2\2\2\u019c\u019e\7\3\2\2\u019d\u019c\3\2\2\2\u019e"+
		"\u01a1\3\2\2\2\u019f\u019d\3\2\2\2\u019f\u01a0\3\2\2\2\u01a0\u01a3\3\2"+
		"\2\2\u01a1\u019f\3\2\2\2\u01a2\u01a4\5,\27\2\u01a3\u01a2\3\2\2\2\u01a3"+
		"\u01a4\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\7>\2\2\u01a6\u01ab\58\35"+
		"\2\u01a7\u01a8\7!\2\2\u01a8\u01aa\58\35\2\u01a9\u01a7\3\2\2\2\u01aa\u01ad"+
		"\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ab\u01ac\3\2\2\2\u01ac\u01af\3\2\2\2\u01ad"+
		"\u01ab\3\2\2\2\u01ae\u01b0\7\7\2\2\u01af\u01ae\3\2\2\2\u01b0\u01b1\3\2"+
		"\2\2\u01b1\u01af\3\2\2\2\u01b1\u01b2\3\2\2\2\u01b2\u01b6\3\2\2\2\u01b3"+
		"\u01b5\t\2\2\2\u01b4\u01b3\3\2\2\2\u01b5\u01b8\3\2\2\2\u01b6\u01b4\3\2"+
		"\2\2\u01b6\u01b7\3\2\2\2\u01b7\u0245\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b9"+
		"\u01bb\7\3\2\2\u01ba\u01b9\3\2\2\2\u01bb\u01be\3\2\2\2\u01bc\u01ba\3\2"+
		"\2\2\u01bc\u01bd\3\2\2\2\u01bd\u01c0\3\2\2\2\u01be\u01bc\3\2\2\2\u01bf"+
		"\u01c1\5,\27\2\u01c0\u01bf\3\2\2\2\u01c0\u01c1\3\2\2\2\u01c1\u01c2\3\2"+
		"\2\2\u01c2\u01c3\7<\2\2\u01c3\u01c5\58\35\2\u01c4\u01c6\7\7\2\2\u01c5"+
		"\u01c4\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7\u01c5\3\2\2\2\u01c7\u01c8\3\2"+
		"\2\2\u01c8\u01cc\3\2\2\2\u01c9\u01cb\t\2\2\2\u01ca\u01c9\3\2\2\2\u01cb"+
		"\u01ce\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u0245\3\2"+
		"\2\2\u01ce\u01cc\3\2\2\2\u01cf\u01d1\7\3\2\2\u01d0\u01cf\3\2\2\2\u01d1"+
		"\u01d4\3\2\2\2\u01d2\u01d0\3\2\2\2\u01d2\u01d3\3\2\2\2\u01d3\u01d6\3\2"+
		"\2\2\u01d4\u01d2\3\2\2\2\u01d5\u01d7\5,\27\2\u01d6\u01d5\3\2\2\2\u01d6"+
		"\u01d7\3\2\2\2\u01d7\u01d9\3\2\2\2\u01d8\u01da\5.\30\2\u01d9\u01d8\3\2"+
		"\2\2\u01d9\u01da\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01dc\5\60\31\2\u01dc"+
		"\u01dd\5\62\32\2\u01dd\u01de\7!\2\2\u01de\u01df\5\62\32\2\u01df\u01e0"+
		"\7!\2\2\u01e0\u01e2\5\62\32\2\u01e1\u01e3\5\66\34\2\u01e2\u01e1\3\2\2"+
		"\2\u01e2\u01e3\3\2\2\2\u01e3\u01e5\3\2\2\2\u01e4\u01e6\7\7\2\2\u01e5\u01e4"+
		"\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7\u01e5\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8"+
		"\u01ec\3\2\2\2\u01e9\u01eb\t\2\2\2\u01ea\u01e9\3\2\2\2\u01eb\u01ee\3\2"+
		"\2\2\u01ec\u01ea\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u0245\3\2\2\2\u01ee"+
		"\u01ec\3\2\2\2\u01ef\u01f1\7\3\2\2\u01f0\u01ef\3\2\2\2\u01f1\u01f4\3\2"+
		"\2\2\u01f2\u01f0\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f6\3\2\2\2\u01f4"+
		"\u01f2\3\2\2\2\u01f5\u01f7\5,\27\2\u01f6\u01f5\3\2\2\2\u01f6\u01f7\3\2"+
		"\2\2\u01f7\u01f9\3\2\2\2\u01f8\u01fa\5.\30\2\u01f9\u01f8\3\2\2\2\u01f9"+
		"\u01fa\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01fc\5\60\31\2\u01fc\u01fd\5"+
		"\62\32\2\u01fd\u01fe\7!\2\2\u01fe\u0200\5\62\32\2\u01ff\u0201\5\66\34"+
		"\2\u0200\u01ff\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u0203\3\2\2\2\u0202\u0204"+
		"\7\7\2\2\u0203\u0202\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u0203\3\2\2\2\u0205"+
		"\u0206\3\2\2\2\u0206\u020a\3\2\2\2\u0207\u0209\t\2\2\2\u0208\u0207\3\2"+
		"\2\2\u0209\u020c\3\2\2\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b"+
		"\u0245\3\2\2\2\u020c\u020a\3\2\2\2\u020d\u020f\7\3\2\2\u020e\u020d\3\2"+
		"\2\2\u020f\u0212\3\2\2\2\u0210\u020e\3\2\2\2\u0210\u0211\3\2\2\2\u0211"+
		"\u0214\3\2\2\2\u0212\u0210\3\2\2\2\u0213\u0215\5,\27\2\u0214\u0213\3\2"+
		"\2\2\u0214\u0215\3\2\2\2\u0215\u0217\3\2\2\2\u0216\u0218\5.\30\2\u0217"+
		"\u0216\3\2\2\2\u0217\u0218\3\2\2\2\u0218\u0219\3\2\2\2\u0219\u021a\5\60"+
		"\31\2\u021a\u021c\5\62\32\2\u021b\u021d\5\66\34\2\u021c\u021b\3\2\2\2"+
		"\u021c\u021d\3\2\2\2\u021d\u021f\3\2\2\2\u021e\u0220\7\7\2\2\u021f\u021e"+
		"\3\2\2\2\u0220\u0221\3\2\2\2\u0221\u021f\3\2\2\2\u0221\u0222\3\2\2\2\u0222"+
		"\u0226\3\2\2\2\u0223\u0225\t\2\2\2\u0224\u0223\3\2\2\2\u0225\u0228\3\2"+
		"\2\2\u0226\u0224\3\2\2\2\u0226\u0227\3\2\2\2\u0227\u0245\3\2\2\2\u0228"+
		"\u0226\3\2\2\2\u0229\u022b\7\3\2\2\u022a\u0229\3\2\2\2\u022b\u022e\3\2"+
		"\2\2\u022c\u022a\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u0230\3\2\2\2\u022e"+
		"\u022c\3\2\2\2\u022f\u0231\5,\27\2\u0230\u022f\3\2\2\2\u0230\u0231\3\2"+
		"\2\2\u0231\u0233\3\2\2\2\u0232\u0234\5.\30\2\u0233\u0232\3\2\2\2\u0233"+
		"\u0234\3\2\2\2\u0234\u0235\3\2\2\2\u0235\u0237\5\60\31\2\u0236\u0238\5"+
		"\66\34\2\u0237\u0236\3\2\2\2\u0237\u0238\3\2\2\2\u0238\u023a\3\2\2\2\u0239"+
		"\u023b\7\7\2\2\u023a\u0239\3\2\2\2\u023b\u023c\3\2\2\2\u023c\u023a\3\2"+
		"\2\2\u023c\u023d\3\2\2\2\u023d\u0241\3\2\2\2\u023e\u0240\t\2\2\2\u023f"+
		"\u023e\3\2\2\2\u0240\u0243\3\2\2\2\u0241\u023f\3\2\2\2\u0241\u0242\3\2"+
		"\2\2\u0242\u0245\3\2\2\2\u0243\u0241\3\2\2\2\u0244\u0177\3\2\2\2\u0244"+
		"\u0186\3\2\2\2\u0244\u019f\3\2\2\2\u0244\u01bc\3\2\2\2\u0244\u01d2\3\2"+
		"\2\2\u0244\u01f2\3\2\2\2\u0244\u0210\3\2\2\2\u0244\u022c\3\2\2\2\u0245"+
		"+\3\2\2\2\u0246\u0248\6\27\2\2\u0247\u0249\7 \2\2\u0248\u0247\3\2\2\2"+
		"\u0248\u0249\3\2\2\2\u0249\u024a\3\2\2\2\u024a\u024b\7A\2\2\u024b-\3\2"+
		"\2\2\u024c\u024d\6\30\3\2\u024d\u024e\7@\2\2\u024e/\3\2\2\2\u024f\u0250"+
		"\6\31\4\2\u0250\u0251\7A\2\2\u0251\61\3\2\2\2\u0252\u0254\5\64\33\2\u0253"+
		"\u0252\3\2\2\2\u0253\u0254\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\5:"+
		"\36\2\u0256\63\3\2\2\2\u0257\u0259\t\6\2\2\u0258\u025a\7\"\2\2\u0259\u0258"+
		"\3\2\2\2\u0259\u025a\3\2\2\2\u025a\65\3\2\2\2\u025b\u025c\6\34\5\2\u025c"+
		"\u025d\7A\2\2\u025d\67\3\2\2\2\u025e\u0263\5:\36\2\u025f\u0260\7+\2\2"+
		"\u0260\u0261\5:\36\2\u0261\u0262\7,\2\2\u0262\u0264\3\2\2\2\u0263\u025f"+
		"\3\2\2\2\u0263\u0264\3\2\2\2\u02649\3\2\2\2\u0265\u0266\b\36\1\2\u0266"+
		"\u0267\t\7\2\2\u0267\u0276\5:\36\21\u0268\u0269\t\b\2\2\u0269\u026a\7"+
		"-\2\2\u026a\u026b\5:\36\2\u026b\u026c\7.\2\2\u026c\u0276\3\2\2\2\u026d"+
		"\u026e\7-\2\2\u026e\u026f\5:\36\2\u026f\u0270\7.\2\2\u0270\u0276\3\2\2"+
		"\2\u0271\u0273\7\33\2\2\u0272\u0271\3\2\2\2\u0272\u0273\3\2\2\2\u0273"+
		"\u0274\3\2\2\2\u0274\u0276\5<\37\2\u0275\u0265\3\2\2\2\u0275\u0268\3\2"+
		"\2\2\u0275\u026d\3\2\2\2\u0275\u0272\3\2\2\2\u0276\u029d\3\2\2\2\u0277"+
		"\u0278\f\20\2\2\u0278\u0279\t\t\2\2\u0279\u029c\5:\36\21\u027a\u027b\f"+
		"\17\2\2\u027b\u027c\7\30\2\2\u027c\u029c\5:\36\20\u027d\u027e\f\16\2\2"+
		"\u027e\u027f\7\32\2\2\u027f\u029c\5:\36\17\u0280\u0281\f\r\2\2\u0281\u0282"+
		"\7\31\2\2\u0282\u029c\5:\36\16\u0283\u0284\f\f\2\2\u0284\u0285\t\n\2\2"+
		"\u0285\u029c\5:\36\r\u0286\u0287\f\13\2\2\u0287\u0288\t\13\2\2\u0288\u029c"+
		"\5:\36\f\u0289\u028a\f\n\2\2\u028a\u028b\7\66\2\2\u028b\u029c\5:\36\13"+
		"\u028c\u028d\f\t\2\2\u028d\u028e\7\16\2\2\u028e\u029c\5:\36\n\u028f\u0290"+
		"\f\b\2\2\u0290\u0291\7\20\2\2\u0291\u029c\5:\36\t\u0292\u0293\f\7\2\2"+
		"\u0293\u0294\7\17\2\2\u0294\u029c\5:\36\b\u0295\u0296\f\6\2\2\u0296\u0297"+
		"\7\'\2\2\u0297\u0298\5:\36\2\u0298\u0299\7(\2\2\u0299\u029a\5:\36\7\u029a"+
		"\u029c\3\2\2\2\u029b\u0277\3\2\2\2\u029b\u027a\3\2\2\2\u029b\u027d\3\2"+
		"\2\2\u029b\u0280\3\2\2\2\u029b\u0283\3\2\2\2\u029b\u0286\3\2\2\2\u029b"+
		"\u0289\3\2\2\2\u029b\u028c\3\2\2\2\u029b\u028f\3\2\2\2\u029b\u0292\3\2"+
		"\2\2\u029b\u0295\3\2\2\2\u029c\u029f\3\2\2\2\u029d\u029b\3\2\2\2\u029d"+
		"\u029e\3\2\2\2\u029e;\3\2\2\2\u029f\u029d\3\2\2\2\u02a0\u02cb\7\r\2\2"+
		"\u02a1\u02cb\7\f\2\2\u02a2\u02cb\7\13\2\2\u02a3\u02cb\7\n\2\2\u02a4\u02cb"+
		"\7\t\2\2\u02a5\u02cb\7\35\2\2\u02a6\u02cb\7?\2\2\u02a7\u02ac\7?\2\2\u02a8"+
		"\u02a9\7+\2\2\u02a9\u02aa\5:\36\2\u02aa\u02ab\7,\2\2\u02ab\u02ad\3\2\2"+
		"\2\u02ac\u02a8\3\2\2\2\u02ac\u02ad\3\2\2\2\u02ad\u02cb\3\2\2\2\u02ae\u02af"+
		"\7?\2\2\u02af\u02b4\t\f\2\2\u02b0\u02b1\7+\2\2\u02b1\u02b2\5:\36\2\u02b2"+
		"\u02b3\7,\2\2\u02b3\u02b5\3\2\2\2\u02b4\u02b0\3\2\2\2\u02b4\u02b5\3\2"+
		"\2\2\u02b5\u02cb\3\2\2\2\u02b6\u02bb\7?\2\2\u02b7\u02b8\7+\2\2\u02b8\u02b9"+
		"\5:\36\2\u02b9\u02ba\7,\2\2\u02ba\u02bc\3\2\2\2\u02bb\u02b7\3\2\2\2\u02bb"+
		"\u02bc\3\2\2\2\u02bc\u02bd\3\2\2\2\u02bd\u02cb\t\f\2\2\u02be\u02bf\t\f"+
		"\2\2\u02bf\u02c4\7?\2\2\u02c0\u02c1\7+\2\2\u02c1\u02c2\5:\36\2\u02c2\u02c3"+
		"\7,\2\2\u02c3\u02c5\3\2\2\2\u02c4\u02c0\3\2\2\2\u02c4\u02c5\3\2\2\2\u02c5"+
		"\u02cb\3\2\2\2\u02c6\u02c8\7 \2\2\u02c7\u02c6\3\2\2\2\u02c7\u02c8\3\2"+
		"\2\2\u02c8\u02c9\3\2\2\2\u02c9\u02cb\7A\2\2\u02ca\u02a0\3\2\2\2\u02ca"+
		"\u02a1\3\2\2\2\u02ca\u02a2\3\2\2\2\u02ca\u02a3\3\2\2\2\u02ca\u02a4\3\2"+
		"\2\2\u02ca\u02a5\3\2\2\2\u02ca\u02a6\3\2\2\2\u02ca\u02a7\3\2\2\2\u02ca"+
		"\u02ae\3\2\2\2\u02ca\u02b6\3\2\2\2\u02ca\u02be\3\2\2\2\u02ca\u02c7\3\2"+
		"\2\2\u02cb=\3\2\2\2lAIKSX`ilnt}\u0083\u008c\u0091\u0098\u009e\u00a6\u00ab"+
		"\u00b1\u00b6\u00bc\u00c6\u00ce\u00d7\u00dc\u00e2\u00eb\u00f3\u00f7\u00fd"+
		"\u0103\u0108\u010c\u0111\u0116\u011e\u0126\u012e\u0132\u0135\u013c\u0142"+
		"\u0146\u014d\u0152\u015b\u015e\u0164\u0169\u016e\u0174\u017b\u0180\u0186"+
		"\u018d\u018f\u0194\u0199\u019f\u01a3\u01ab\u01b1\u01b6\u01bc\u01c0\u01c7"+
		"\u01cc\u01d2\u01d6\u01d9\u01e2\u01e7\u01ec\u01f2\u01f6\u01f9\u0200\u0205"+
		"\u020a\u0210\u0214\u0217\u021c\u0221\u0226\u022c\u0230\u0233\u0237\u023c"+
		"\u0241\u0244\u0248\u0253\u0259\u0263\u0272\u0275\u029b\u029d\u02ac\u02b4"+
		"\u02bb\u02c4\u02c7\u02ca";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}