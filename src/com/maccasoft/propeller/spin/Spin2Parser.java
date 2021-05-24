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
		ASSIGN=20, ADD_ASSIGN=21, ELLIPSIS=22, BIN_AND=23, BIN_OR=24, BIN_XOR=25, 
		AT=26, POUND=27, DOLLAR=28, PERCENT=29, EQUAL=30, DOT=31, COMMA=32, BACKSLASH=33, 
		PLUS=34, MINUS=35, STAR=36, DIV=37, QUESTION=38, COLON=39, TILDE=40, UNDERSCORE=41, 
		OPEN_BRACKET=42, CLOSE_BRACKET=43, OPEN_PAREN=44, CLOSE_PAREN=45, CON_START=46, 
		VAR_START=47, OBJ_START=48, PUB_START=49, PRI_START=50, DAT_START=51, 
		REPEAT=52, FROM=53, TO=54, STEP=55, WHILE=56, UNTIL=57, ELSEIFNOT=58, 
		ELSEIF=59, ELSE=60, IFNOT=61, IF=62, CASE=63, OTHER=64, ADDPINS=65, ADDBITS=66, 
		FUNCTIONS=67, ORG=68, ORGH=69, RES=70, ALIGN=71, TYPE=72, CONDITION=73, 
		IDENTIFIER=74;
	public static final int
		RULE_prog = 0, RULE_constantsSection = 1, RULE_constantAssign = 2, RULE_constantEnum = 3, 
		RULE_constantEnumName = 4, RULE_objectsSection = 5, RULE_object = 6, RULE_variablesSection = 7, 
		RULE_variable = 8, RULE_method = 9, RULE_parameters = 10, RULE_result = 11, 
		RULE_localvars = 12, RULE_localvar = 13, RULE_statement = 14, RULE_assignment = 15, 
		RULE_function = 16, RULE_repeatLoop = 17, RULE_conditional = 18, RULE_elseConditional = 19, 
		RULE_caseConditional = 20, RULE_caseConditionalMatch = 21, RULE_caseConditionalOther = 22, 
		RULE_data = 23, RULE_dataLine = 24, RULE_label = 25, RULE_condition = 26, 
		RULE_opcode = 27, RULE_argument = 28, RULE_prefix = 29, RULE_effect = 30, 
		RULE_dataValue = 31, RULE_expression = 32, RULE_atom = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objectsSection", "object", "variablesSection", "variable", "method", 
			"parameters", "result", "localvars", "localvar", "statement", "assignment", 
			"function", "repeatLoop", "conditional", "elseConditional", "caseConditional", 
			"caseConditionalMatch", "caseConditionalOther", "data", "dataLine", "label", 
			"condition", "opcode", "argument", "prefix", "effect", "dataValue", "expression", 
			"atom"
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
		public List<ObjectsSectionContext> objectsSection() {
			return getRuleContexts(ObjectsSectionContext.class);
		}
		public ObjectsSectionContext objectsSection(int i) {
			return getRuleContext(ObjectsSectionContext.class,i);
		}
		public List<VariablesSectionContext> variablesSection() {
			return getRuleContexts(VariablesSectionContext.class);
		}
		public VariablesSectionContext variablesSection(int i) {
			return getRuleContext(VariablesSectionContext.class,i);
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
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(68);
				match(NL);
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(79);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(74);
					constantsSection();
					}
					break;
				case OBJ_START:
					{
					setState(75);
					objectsSection();
					}
					break;
				case VAR_START:
					{
					setState(76);
					variablesSection();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(77);
					method();
					}
					break;
				case DAT_START:
					{
					setState(78);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(84);
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
			setState(87); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(86);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(89); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(91);
				match(NL);
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==POUND || _la==IDENTIFIER) {
				{
				setState(114);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					{
					setState(97);
					constantAssign();
					setState(102);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(98);
						match(COMMA);
						setState(99);
						constantAssign();
						}
						}
						setState(104);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case 2:
					{
					setState(105);
					constantEnum();
					}
					break;
				case 3:
					{
					{
					setState(106);
					constantEnumName();
					setState(111);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(107);
						match(COMMA);
						setState(108);
						constantEnumName();
						}
						}
						setState(113);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				}
				}
				setState(118);
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
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(119);
				match(INDENT);
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125);
			((ConstantAssignContext)_localctx).name = match(IDENTIFIER);
			setState(126);
			match(EQUAL);
			setState(127);
			((ConstantAssignContext)_localctx).exp = expression(0);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(128);
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
				setState(133);
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
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(134);
				match(INDENT);
				}
				}
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(140);
			match(POUND);
			setState(141);
			((ConstantEnumContext)_localctx).start = expression(0);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(142);
				match(OPEN_BRACKET);
				setState(143);
				((ConstantEnumContext)_localctx).step = expression(0);
				setState(144);
				match(CLOSE_BRACKET);
				}
			}

			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(148);
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
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(154);
				match(COMMA);
				setState(155);
				constantEnumName();
				}
				}
				setState(160);
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
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(161);
				match(INDENT);
				}
				}
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(167);
			((ConstantEnumNameContext)_localctx).name = match(IDENTIFIER);
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(168);
				match(OPEN_BRACKET);
				setState(169);
				((ConstantEnumNameContext)_localctx).multiplier = expression(0);
				setState(170);
				match(CLOSE_BRACKET);
				}
			}

			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(174);
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
				setState(179);
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

	public static class ObjectsSectionContext extends ParserRuleContext {
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
		public ObjectsSectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectsSection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterObjectsSection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitObjectsSection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitObjectsSection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectsSectionContext objectsSection() throws RecognitionException {
		ObjectsSectionContext _localctx = new ObjectsSectionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_objectsSection);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(181); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(180);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(183); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(185);
				match(NL);
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==IDENTIFIER) {
				{
				{
				setState(191);
				object();
				}
				}
				setState(196);
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
		public Token name;
		public ExpressionContext count;
		public Token filename;
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(Spin2Parser.STRING, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(Spin2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(Spin2Parser.OPEN_BRACKET, i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(Spin2Parser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(Spin2Parser.CLOSE_BRACKET, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
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
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(197);
				match(INDENT);
				}
				}
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203);
			((ObjectContext)_localctx).name = match(IDENTIFIER);
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(204);
				match(OPEN_BRACKET);
				setState(205);
				((ObjectContext)_localctx).count = expression(0);
				setState(206);
				match(CLOSE_BRACKET);
				}
				}
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(213);
			match(COLON);
			setState(214);
			((ObjectContext)_localctx).filename = match(STRING);
			setState(216); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(215);
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
				setState(218); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DEDENT || _la==NL );
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

	public static class VariablesSectionContext extends ParserRuleContext {
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
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public VariablesSectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variablesSection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterVariablesSection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitVariablesSection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitVariablesSection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariablesSectionContext variablesSection() throws RecognitionException {
		VariablesSectionContext _localctx = new VariablesSectionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_variablesSection);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(221); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(220);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(223); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(225);
				match(NL);
				}
				}
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==TYPE || _la==IDENTIFIER) {
				{
				{
				setState(231);
				variable();
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(232);
					match(COMMA);
					setState(233);
					variable();
					}
					}
					setState(238);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(243);
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
		public Token name;
		public ExpressionContext size;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
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
		enterRule(_localctx, 16, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(244);
				match(INDENT);
				}
				}
				setState(249);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(250);
				((VariableContext)_localctx).type = match(TYPE);
				}
			}

			setState(253);
			((VariableContext)_localctx).name = match(IDENTIFIER);
			setState(258);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(254);
				match(OPEN_BRACKET);
				setState(255);
				((VariableContext)_localctx).size = expression(0);
				setState(256);
				match(CLOSE_BRACKET);
				}
			}

			setState(261); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(260);
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
				setState(263); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DEDENT || _la==NL );
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
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		enterRule(_localctx, 18, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(266);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(267);
			match(OPEN_PAREN);
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(268);
				parameters();
				}
			}

			setState(271);
			match(CLOSE_PAREN);
			setState(274);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(272);
				match(COLON);
				setState(273);
				result();
				}
			}

			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(276);
				match(BIN_OR);
				setState(277);
				localvars();
				}
			}

			setState(281); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(280);
				match(NL);
				}
				}
				setState(283); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(285);
			match(INDENT);
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
				{
				{
				setState(286);
				statement();
				}
				}
				setState(291);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(292);
			match(DEDENT);
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
		enterRule(_localctx, 20, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(IDENTIFIER);
			setState(299);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(295);
				match(COMMA);
				setState(296);
				match(IDENTIFIER);
				}
				}
				setState(301);
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
		enterRule(_localctx, 22, RULE_result);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			match(IDENTIFIER);
			setState(307);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(303);
				match(COMMA);
				setState(304);
				match(IDENTIFIER);
				}
				}
				setState(309);
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
		enterRule(_localctx, 24, RULE_localvars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			localvar();
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(311);
				match(COMMA);
				setState(312);
				localvar();
				}
				}
				setState(317);
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
		enterRule(_localctx, 26, RULE_localvar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(318);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(322);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(321);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(324);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(329);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(325);
				match(OPEN_BRACKET);
				setState(326);
				((LocalvarContext)_localctx).count = expression(0);
				setState(327);
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
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public RepeatLoopContext repeatLoop() {
			return getRuleContext(RepeatLoopContext.class,0);
		}
		public ConditionalContext conditional() {
			return getRuleContext(ConditionalContext.class,0);
		}
		public CaseConditionalContext caseConditional() {
			return getRuleContext(CaseConditionalContext.class,0);
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
		enterRule(_localctx, 28, RULE_statement);
		int _la;
		try {
			setState(346);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(331);
				assignment();
				setState(333); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(332);
					match(NL);
					}
					}
					setState(335); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(337);
				function();
				setState(339); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(338);
					match(NL);
					}
					}
					setState(341); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(343);
				repeatLoop();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(344);
				conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(345);
				caseConditional();
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
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
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
		enterRule(_localctx, 30, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			match(IDENTIFIER);
			setState(353);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(349);
				match(COMMA);
				setState(350);
				match(IDENTIFIER);
				}
				}
				setState(355);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(356);
			_la = _input.LA(1);
			if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(359);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(357);
				function();
				}
				break;
			case 2:
				{
				setState(358);
				expression(0);
				}
				break;
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

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public TerminalNode BACKSLASH() { return getToken(Spin2Parser.BACKSLASH, 0); }
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
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
		enterRule(_localctx, 32, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(361);
				match(BACKSLASH);
				}
			}

			setState(364);
			match(IDENTIFIER);
			setState(365);
			match(OPEN_PAREN);
			setState(378);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(366);
				assignment();
				}
				break;
			case 2:
				{
				setState(367);
				function();
				}
				break;
			case 3:
				{
				setState(368);
				expression(0);
				setState(375);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << COMMA) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(373);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
					case 1:
						{
						setState(369);
						match(COMMA);
						setState(370);
						assignment();
						}
						break;
					case 2:
						{
						setState(371);
						function();
						}
						break;
					case 3:
						{
						setState(372);
						expression(0);
						}
						break;
					}
					}
					setState(377);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(380);
			match(CLOSE_PAREN);
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

	public static class RepeatLoopContext extends ParserRuleContext {
		public TerminalNode REPEAT() { return getToken(Spin2Parser.REPEAT, 0); }
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public TerminalNode WHILE() { return getToken(Spin2Parser.WHILE, 0); }
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
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
		public TerminalNode UNTIL() { return getToken(Spin2Parser.UNTIL, 0); }
		public TerminalNode FROM() { return getToken(Spin2Parser.FROM, 0); }
		public TerminalNode TO() { return getToken(Spin2Parser.TO, 0); }
		public TerminalNode STEP() { return getToken(Spin2Parser.STEP, 0); }
		public RepeatLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_repeatLoop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterRepeatLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitRepeatLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitRepeatLoop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RepeatLoopContext repeatLoop() throws RecognitionException {
		RepeatLoopContext _localctx = new RepeatLoopContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_repeatLoop);
		int _la;
		try {
			setState(522);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				match(REPEAT);
				setState(384); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(383);
					match(NL);
					}
					}
					setState(386); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(388);
				match(INDENT);
				setState(392);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(389);
					statement();
					}
					}
					setState(394);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(395);
				match(DEDENT);
				setState(396);
				match(WHILE);
				setState(399);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
				case 1:
					{
					setState(397);
					function();
					}
					break;
				case 2:
					{
					setState(398);
					expression(0);
					}
					break;
				}
				setState(402); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(401);
					match(NL);
					}
					}
					setState(404); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(406);
				match(REPEAT);
				setState(408); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(407);
					match(NL);
					}
					}
					setState(410); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(412);
				match(INDENT);
				setState(416);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(413);
					statement();
					}
					}
					setState(418);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(419);
				match(DEDENT);
				setState(420);
				match(UNTIL);
				setState(423);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(421);
					function();
					}
					break;
				case 2:
					{
					setState(422);
					expression(0);
					}
					break;
				}
				setState(426); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(425);
					match(NL);
					}
					}
					setState(428); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(430);
				match(REPEAT);
				setState(431);
				match(FROM);
				setState(434);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
				case 1:
					{
					setState(432);
					function();
					}
					break;
				case 2:
					{
					setState(433);
					expression(0);
					}
					break;
				}
				setState(448);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(436);
					match(TO);
					setState(439);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
					case 1:
						{
						setState(437);
						function();
						}
						break;
					case 2:
						{
						setState(438);
						expression(0);
						}
						break;
					}
					setState(446);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(441);
						match(STEP);
						setState(444);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
						case 1:
							{
							setState(442);
							function();
							}
							break;
						case 2:
							{
							setState(443);
							expression(0);
							}
							break;
						}
						}
					}

					}
				}

				setState(451); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(450);
					match(NL);
					}
					}
					setState(453); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(455);
				match(INDENT);
				setState(459);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(456);
					statement();
					}
					}
					setState(461);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(462);
				match(DEDENT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(464);
				match(REPEAT);
				setState(465);
				match(WHILE);
				setState(468);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(466);
					function();
					}
					break;
				case 2:
					{
					setState(467);
					expression(0);
					}
					break;
				}
				setState(471); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(470);
					match(NL);
					}
					}
					setState(473); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(475);
				match(INDENT);
				setState(479);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(476);
					statement();
					}
					}
					setState(481);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(482);
				match(DEDENT);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(484);
				match(REPEAT);
				setState(485);
				match(UNTIL);
				setState(488);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
				case 1:
					{
					setState(486);
					function();
					}
					break;
				case 2:
					{
					setState(487);
					expression(0);
					}
					break;
				}
				setState(491); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(490);
					match(NL);
					}
					}
					setState(493); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(495);
				match(INDENT);
				setState(499);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(496);
					statement();
					}
					}
					setState(501);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(502);
				match(DEDENT);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(504);
				match(REPEAT);
				setState(507);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(505);
					function();
					}
					break;
				case 2:
					{
					setState(506);
					expression(0);
					}
					break;
				}
				setState(510); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(509);
					match(NL);
					}
					}
					setState(512); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(514);
				match(INDENT);
				setState(518);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(515);
					statement();
					}
					}
					setState(520);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(521);
				match(DEDENT);
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

	public static class ConditionalContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(Spin2Parser.IF, 0); }
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<ElseConditionalContext> elseConditional() {
			return getRuleContexts(ElseConditionalContext.class);
		}
		public ElseConditionalContext elseConditional(int i) {
			return getRuleContext(ElseConditionalContext.class,i);
		}
		public TerminalNode IFNOT() { return getToken(Spin2Parser.IFNOT, 0); }
		public ConditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConditional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalContext conditional() throws RecognitionException {
		ConditionalContext _localctx = new ConditionalContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_conditional);
		int _la;
		try {
			setState(572);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(524);
				match(IF);
				setState(527);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
				case 1:
					{
					setState(525);
					function();
					}
					break;
				case 2:
					{
					setState(526);
					expression(0);
					}
					break;
				}
				setState(530); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(529);
					match(NL);
					}
					}
					setState(532); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(534);
				match(INDENT);
				setState(538);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(535);
					statement();
					}
					}
					setState(540);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(541);
				match(DEDENT);
				setState(545);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(542);
					elseConditional();
					}
					}
					setState(547);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case IFNOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(548);
				match(IFNOT);
				setState(551);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
				case 1:
					{
					setState(549);
					function();
					}
					break;
				case 2:
					{
					setState(550);
					expression(0);
					}
					break;
				}
				setState(554); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(553);
					match(NL);
					}
					}
					setState(556); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(558);
				match(INDENT);
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(559);
					statement();
					}
					}
					setState(564);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(565);
				match(DEDENT);
				setState(569);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(566);
					elseConditional();
					}
					}
					setState(571);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	public static class ElseConditionalContext extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(Spin2Parser.ELSE, 0); }
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode ELSEIF() { return getToken(Spin2Parser.ELSEIF, 0); }
		public TerminalNode ELSEIFNOT() { return getToken(Spin2Parser.ELSEIFNOT, 0); }
		public ElseConditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseConditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterElseConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitElseConditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitElseConditional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseConditionalContext elseConditional() throws RecognitionException {
		ElseConditionalContext _localctx = new ElseConditionalContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_elseConditional);
		int _la;
		try {
			setState(628);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ELSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(574);
				match(ELSE);
				setState(577);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(575);
					function();
					}
					break;
				case 2:
					{
					setState(576);
					expression(0);
					}
					break;
				}
				setState(580); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(579);
					match(NL);
					}
					}
					setState(582); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(584);
				match(INDENT);
				setState(588);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(585);
					statement();
					}
					}
					setState(590);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(591);
				match(DEDENT);
				}
				break;
			case ELSEIF:
				enterOuterAlt(_localctx, 2);
				{
				setState(592);
				match(ELSEIF);
				setState(595);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
				case 1:
					{
					setState(593);
					function();
					}
					break;
				case 2:
					{
					setState(594);
					expression(0);
					}
					break;
				}
				setState(598); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(597);
					match(NL);
					}
					}
					setState(600); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(602);
				match(INDENT);
				setState(606);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(603);
					statement();
					}
					}
					setState(608);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(609);
				match(DEDENT);
				}
				break;
			case ELSEIFNOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(610);
				match(ELSEIFNOT);
				setState(613);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
				case 1:
					{
					setState(611);
					function();
					}
					break;
				case 2:
					{
					setState(612);
					expression(0);
					}
					break;
				}
				setState(616); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(615);
					match(NL);
					}
					}
					setState(618); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(620);
				match(INDENT);
				setState(624);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
					{
					{
					setState(621);
					statement();
					}
					}
					setState(626);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(627);
				match(DEDENT);
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

	public static class CaseConditionalContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(Spin2Parser.CASE, 0); }
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<CaseConditionalMatchContext> caseConditionalMatch() {
			return getRuleContexts(CaseConditionalMatchContext.class);
		}
		public CaseConditionalMatchContext caseConditionalMatch(int i) {
			return getRuleContext(CaseConditionalMatchContext.class,i);
		}
		public List<CaseConditionalOtherContext> caseConditionalOther() {
			return getRuleContexts(CaseConditionalOtherContext.class);
		}
		public CaseConditionalOtherContext caseConditionalOther(int i) {
			return getRuleContext(CaseConditionalOtherContext.class,i);
		}
		public CaseConditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseConditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterCaseConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitCaseConditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitCaseConditional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseConditionalContext caseConditional() throws RecognitionException {
		CaseConditionalContext _localctx = new CaseConditionalContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_caseConditional);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(630);
			match(CASE);
			setState(633);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				{
				setState(631);
				function();
				}
				break;
			case 2:
				{
				setState(632);
				expression(0);
				}
				break;
			}
			setState(636); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(635);
				match(NL);
				}
				}
				setState(638); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(640);
			match(INDENT);
			setState(644);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
				{
				{
				setState(641);
				statement();
				}
				}
				setState(646);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(647);
			match(DEDENT);
			setState(652);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(650);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
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
					case PLUS:
					case MINUS:
					case TILDE:
					case OPEN_PAREN:
					case FUNCTIONS:
					case IDENTIFIER:
						{
						setState(648);
						caseConditionalMatch();
						}
						break;
					case OTHER:
						{
						setState(649);
						caseConditionalOther();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(654);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
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

	public static class CaseConditionalMatchContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public List<TerminalNode> ELLIPSIS() { return getTokens(Spin2Parser.ELLIPSIS); }
		public TerminalNode ELLIPSIS(int i) {
			return getToken(Spin2Parser.ELLIPSIS, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public CaseConditionalMatchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseConditionalMatch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterCaseConditionalMatch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitCaseConditionalMatch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitCaseConditionalMatch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseConditionalMatchContext caseConditionalMatch() throws RecognitionException {
		CaseConditionalMatchContext _localctx = new CaseConditionalMatchContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_caseConditionalMatch);
		int _la;
		try {
			setState(712);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(655);
				expression(0);
				setState(658);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELLIPSIS) {
					{
					setState(656);
					match(ELLIPSIS);
					setState(657);
					expression(0);
					}
				}

				setState(660);
				match(COLON);
				setState(663);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
				case 1:
					{
					setState(661);
					assignment();
					}
					break;
				case 2:
					{
					setState(662);
					function();
					}
					break;
				}
				setState(666); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(665);
					match(NL);
					}
					}
					setState(668); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(678);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(670);
					match(INDENT);
					setState(674);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
						{
						{
						setState(671);
						statement();
						}
						}
						setState(676);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(677);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(680);
				expression(0);
				setState(683);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELLIPSIS) {
					{
					setState(681);
					match(ELLIPSIS);
					setState(682);
					expression(0);
					}
				}

				setState(693);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(685);
					match(COMMA);
					setState(686);
					expression(0);
					setState(689);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ELLIPSIS) {
						{
						setState(687);
						match(ELLIPSIS);
						setState(688);
						expression(0);
						}
					}

					}
					}
					setState(695);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(696);
				match(COLON);
				setState(698); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(697);
					match(NL);
					}
					}
					setState(700); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(710);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(702);
					match(INDENT);
					setState(706);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
						{
						{
						setState(703);
						statement();
						}
						}
						setState(708);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(709);
					match(DEDENT);
					}
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

	public static class CaseConditionalOtherContext extends ParserRuleContext {
		public TerminalNode OTHER() { return getToken(Spin2Parser.OTHER, 0); }
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public CaseConditionalOtherContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseConditionalOther; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterCaseConditionalOther(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitCaseConditionalOther(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitCaseConditionalOther(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseConditionalOtherContext caseConditionalOther() throws RecognitionException {
		CaseConditionalOtherContext _localctx = new CaseConditionalOtherContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_caseConditionalOther);
		int _la;
		try {
			setState(752);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(714);
				match(OTHER);
				setState(715);
				match(COLON);
				setState(718);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
				case 1:
					{
					setState(716);
					assignment();
					}
					break;
				case 2:
					{
					setState(717);
					function();
					}
					break;
				}
				setState(721); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(720);
					match(NL);
					}
					}
					setState(723); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(733);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(725);
					match(INDENT);
					setState(729);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
						{
						{
						setState(726);
						statement();
						}
						}
						setState(731);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(732);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(735);
				match(OTHER);
				setState(736);
				match(COLON);
				setState(738); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(737);
					match(NL);
					}
					}
					setState(740); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(750);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(742);
					match(INDENT);
					setState(746);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 33)) & ~0x3f) == 0 && ((1L << (_la - 33)) & ((1L << (BACKSLASH - 33)) | (1L << (REPEAT - 33)) | (1L << (IFNOT - 33)) | (1L << (IF - 33)) | (1L << (CASE - 33)) | (1L << (IDENTIFIER - 33)))) != 0)) {
						{
						{
						setState(743);
						statement();
						}
						}
						setState(748);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(749);
					match(DEDENT);
					}
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
		enterRule(_localctx, 46, RULE_data);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(755); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(754);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(757); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(762);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(759);
					match(NL);
					}
					} 
				}
				setState(764);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
			}
			setState(768);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(765);
					dataLine();
					}
					} 
				}
				setState(770);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
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
		enterRule(_localctx, 48, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(976);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,164,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(771);
				label();
				setState(773); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(772);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(775); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,124,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(780);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(777);
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
					setState(782);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(786);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(783);
					match(INDENT);
					}
					}
					setState(788);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(789);
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
				setState(795);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(790);
					expression(0);
					setState(793);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(791);
						match(COMMA);
						setState(792);
						expression(0);
						}
					}

					}
				}

				setState(798); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(797);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(800); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(805);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(802);
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
					setState(807);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(811);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(808);
						match(INDENT);
						}
						} 
					}
					setState(813);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				}
				setState(815);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
				case 1:
					{
					setState(814);
					label();
					}
					break;
				}
				setState(817);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(818);
				dataValue();
				setState(823);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(819);
					match(COMMA);
					setState(820);
					dataValue();
					}
					}
					setState(825);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(827); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(826);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(829); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(834);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(831);
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
					setState(836);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(840);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(837);
						match(INDENT);
						}
						} 
					}
					setState(842);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				}
				setState(844);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
				case 1:
					{
					setState(843);
					label();
					}
					break;
				}
				setState(846);
				((DataLineContext)_localctx).directive = match(RES);
				setState(847);
				dataValue();
				setState(849); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(848);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(851); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(856);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(853);
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
					setState(858);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(862);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(859);
						match(INDENT);
						}
						} 
					}
					setState(864);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
				}
				setState(866);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
				case 1:
					{
					setState(865);
					label();
					}
					break;
				}
				setState(869);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
				case 1:
					{
					setState(868);
					condition();
					}
					break;
				}
				setState(871);
				opcode();
				setState(872);
				argument();
				setState(873);
				match(COMMA);
				setState(874);
				argument();
				setState(875);
				match(COMMA);
				setState(876);
				argument();
				setState(878);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,143,_ctx) ) {
				case 1:
					{
					setState(877);
					effect();
					}
					break;
				}
				setState(881); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(880);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(883); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,144,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(888);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(885);
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
					setState(890);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(894);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,146,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(891);
						match(INDENT);
						}
						} 
					}
					setState(896);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,146,_ctx);
				}
				setState(898);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
				case 1:
					{
					setState(897);
					label();
					}
					break;
				}
				setState(901);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,148,_ctx) ) {
				case 1:
					{
					setState(900);
					condition();
					}
					break;
				}
				setState(903);
				opcode();
				setState(904);
				argument();
				setState(905);
				match(COMMA);
				setState(906);
				argument();
				setState(908);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
				case 1:
					{
					setState(907);
					effect();
					}
					break;
				}
				setState(911); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(910);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(913); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,150,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(918);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(915);
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
					setState(920);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(924);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,152,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(921);
						match(INDENT);
						}
						} 
					}
					setState(926);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,152,_ctx);
				}
				setState(928);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
				case 1:
					{
					setState(927);
					label();
					}
					break;
				}
				setState(931);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,154,_ctx) ) {
				case 1:
					{
					setState(930);
					condition();
					}
					break;
				}
				setState(933);
				opcode();
				setState(934);
				argument();
				setState(936);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,155,_ctx) ) {
				case 1:
					{
					setState(935);
					effect();
					}
					break;
				}
				setState(939); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(938);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(941); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,156,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(946);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,157,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(943);
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
					setState(948);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,157,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(952);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(949);
						match(INDENT);
						}
						} 
					}
					setState(954);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
				}
				setState(956);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,159,_ctx) ) {
				case 1:
					{
					setState(955);
					label();
					}
					break;
				}
				setState(959);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
				case 1:
					{
					setState(958);
					condition();
					}
					break;
				}
				setState(961);
				opcode();
				setState(963);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
				case 1:
					{
					setState(962);
					effect();
					}
					break;
				}
				setState(966); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(965);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(968); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,162,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(973);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,163,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(970);
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
					setState(975);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,163,_ctx);
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
		enterRule(_localctx, 50, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(978);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(980);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(979);
				match(DOT);
				}
			}

			setState(982);
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
		enterRule(_localctx, 52, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(984);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(985);
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
		enterRule(_localctx, 54, RULE_opcode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(987);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(988);
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
		enterRule(_localctx, 56, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==POUND_POUND || _la==POUND) {
				{
				setState(990);
				prefix();
				}
			}

			setState(993);
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
		enterRule(_localctx, 58, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(995);
			_la = _input.LA(1);
			if ( !(_la==POUND_POUND || _la==POUND) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(997);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(996);
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
		enterRule(_localctx, 60, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(999);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1000);
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
		enterRule(_localctx, 62, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1002);
			expression(0);
			setState(1007);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1003);
				match(OPEN_BRACKET);
				setState(1004);
				((DataValueContext)_localctx).count = expression(0);
				setState(1005);
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
		public TerminalNode FUNCTIONS() { return getToken(Spin2Parser.FUNCTIONS, 0); }
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
		public TerminalNode ADDBITS() { return getToken(Spin2Parser.ADDBITS, 0); }
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1025);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1010);
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
				setState(1011);
				((ExpressionContext)_localctx).exp = expression(15);
				}
				break;
			case FUNCTIONS:
				{
				setState(1012);
				((ExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1013);
				match(OPEN_PAREN);
				setState(1014);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1015);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1017);
				match(OPEN_PAREN);
				setState(1018);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1019);
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
			case IDENTIFIER:
				{
				setState(1022);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1021);
					match(AT);
					}
				}

				setState(1024);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1065);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,172,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1063);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1027);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1028);
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
						setState(1029);
						((ExpressionContext)_localctx).right = expression(15);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1030);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1031);
						((ExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1032);
						((ExpressionContext)_localctx).right = expression(14);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1033);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1034);
						((ExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1035);
						((ExpressionContext)_localctx).right = expression(13);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1036);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1037);
						((ExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1038);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1039);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1040);
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
						setState(1041);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1042);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1043);
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
						setState(1044);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1045);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1046);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADDPINS || _la==ADDBITS) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1047);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1048);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1049);
						((ExpressionContext)_localctx).operator = match(LOGICAL_AND);
						setState(1050);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1051);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1052);
						((ExpressionContext)_localctx).operator = match(LOGICAL_XOR);
						setState(1053);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1054);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1055);
						((ExpressionContext)_localctx).operator = match(LOGICAL_OR);
						setState(1056);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1057);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1058);
						((ExpressionContext)_localctx).operator = match(QUESTION);
						setState(1059);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(1060);
						((ExpressionContext)_localctx).operator = match(COLON);
						setState(1061);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(1067);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,172,_ctx);
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
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode PLUS_PLUS() { return getToken(Spin2Parser.PLUS_PLUS, 0); }
		public TerminalNode MINUS_MINUS() { return getToken(Spin2Parser.MINUS_MINUS, 0); }
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
		enterRule(_localctx, 66, RULE_atom);
		int _la;
		try {
			setState(1109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,178,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1068);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1069);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1070);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1071);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1072);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1073);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1074);
				match(IDENTIFIER);
				setState(1079);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
				case 1:
					{
					setState(1075);
					match(OPEN_BRACKET);
					setState(1076);
					expression(0);
					setState(1077);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1081);
				match(IDENTIFIER);
				setState(1082);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1087);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
				case 1:
					{
					setState(1083);
					match(OPEN_BRACKET);
					setState(1084);
					expression(0);
					setState(1085);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1089);
				match(IDENTIFIER);
				setState(1094);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1090);
					match(OPEN_BRACKET);
					setState(1091);
					expression(0);
					setState(1092);
					match(CLOSE_BRACKET);
					}
				}

				setState(1096);
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
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1097);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1098);
				match(IDENTIFIER);
				setState(1103);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,176,_ctx) ) {
				case 1:
					{
					setState(1099);
					match(OPEN_BRACKET);
					setState(1100);
					expression(0);
					setState(1101);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1106);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1105);
					match(DOT);
					}
				}

				setState(1108);
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
		case 25:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 26:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 27:
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 30:
			return effect_sempred((EffectContext)_localctx, predIndex);
		case 32:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3L\u045a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\7\2H\n\2\f\2\16\2K\13\2\3\2\3\2\3\2\3\2\3\2\7\2"+
		"R\n\2\f\2\16\2U\13\2\3\2\3\2\3\3\6\3Z\n\3\r\3\16\3[\3\3\7\3_\n\3\f\3\16"+
		"\3b\13\3\3\3\3\3\3\3\7\3g\n\3\f\3\16\3j\13\3\3\3\3\3\3\3\3\3\7\3p\n\3"+
		"\f\3\16\3s\13\3\7\3u\n\3\f\3\16\3x\13\3\3\4\7\4{\n\4\f\4\16\4~\13\4\3"+
		"\4\3\4\3\4\3\4\7\4\u0084\n\4\f\4\16\4\u0087\13\4\3\5\7\5\u008a\n\5\f\5"+
		"\16\5\u008d\13\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0095\n\5\3\5\7\5\u0098\n"+
		"\5\f\5\16\5\u009b\13\5\3\5\3\5\7\5\u009f\n\5\f\5\16\5\u00a2\13\5\3\6\7"+
		"\6\u00a5\n\6\f\6\16\6\u00a8\13\6\3\6\3\6\3\6\3\6\3\6\5\6\u00af\n\6\3\6"+
		"\7\6\u00b2\n\6\f\6\16\6\u00b5\13\6\3\7\6\7\u00b8\n\7\r\7\16\7\u00b9\3"+
		"\7\7\7\u00bd\n\7\f\7\16\7\u00c0\13\7\3\7\7\7\u00c3\n\7\f\7\16\7\u00c6"+
		"\13\7\3\b\7\b\u00c9\n\b\f\b\16\b\u00cc\13\b\3\b\3\b\3\b\3\b\3\b\7\b\u00d3"+
		"\n\b\f\b\16\b\u00d6\13\b\3\b\3\b\3\b\6\b\u00db\n\b\r\b\16\b\u00dc\3\t"+
		"\6\t\u00e0\n\t\r\t\16\t\u00e1\3\t\7\t\u00e5\n\t\f\t\16\t\u00e8\13\t\3"+
		"\t\3\t\3\t\7\t\u00ed\n\t\f\t\16\t\u00f0\13\t\7\t\u00f2\n\t\f\t\16\t\u00f5"+
		"\13\t\3\n\7\n\u00f8\n\n\f\n\16\n\u00fb\13\n\3\n\5\n\u00fe\n\n\3\n\3\n"+
		"\3\n\3\n\3\n\5\n\u0105\n\n\3\n\6\n\u0108\n\n\r\n\16\n\u0109\3\13\3\13"+
		"\3\13\3\13\5\13\u0110\n\13\3\13\3\13\3\13\5\13\u0115\n\13\3\13\3\13\5"+
		"\13\u0119\n\13\3\13\6\13\u011c\n\13\r\13\16\13\u011d\3\13\3\13\7\13\u0122"+
		"\n\13\f\13\16\13\u0125\13\13\3\13\3\13\3\f\3\f\3\f\7\f\u012c\n\f\f\f\16"+
		"\f\u012f\13\f\3\r\3\r\3\r\7\r\u0134\n\r\f\r\16\r\u0137\13\r\3\16\3\16"+
		"\3\16\7\16\u013c\n\16\f\16\16\16\u013f\13\16\3\17\5\17\u0142\n\17\3\17"+
		"\5\17\u0145\n\17\3\17\3\17\3\17\3\17\3\17\5\17\u014c\n\17\3\20\3\20\6"+
		"\20\u0150\n\20\r\20\16\20\u0151\3\20\3\20\6\20\u0156\n\20\r\20\16\20\u0157"+
		"\3\20\3\20\3\20\5\20\u015d\n\20\3\21\3\21\3\21\7\21\u0162\n\21\f\21\16"+
		"\21\u0165\13\21\3\21\3\21\3\21\5\21\u016a\n\21\3\22\5\22\u016d\n\22\3"+
		"\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u0178\n\22\f\22\16\22"+
		"\u017b\13\22\5\22\u017d\n\22\3\22\3\22\3\23\3\23\6\23\u0183\n\23\r\23"+
		"\16\23\u0184\3\23\3\23\7\23\u0189\n\23\f\23\16\23\u018c\13\23\3\23\3\23"+
		"\3\23\3\23\5\23\u0192\n\23\3\23\6\23\u0195\n\23\r\23\16\23\u0196\3\23"+
		"\3\23\6\23\u019b\n\23\r\23\16\23\u019c\3\23\3\23\7\23\u01a1\n\23\f\23"+
		"\16\23\u01a4\13\23\3\23\3\23\3\23\3\23\5\23\u01aa\n\23\3\23\6\23\u01ad"+
		"\n\23\r\23\16\23\u01ae\3\23\3\23\3\23\3\23\5\23\u01b5\n\23\3\23\3\23\3"+
		"\23\5\23\u01ba\n\23\3\23\3\23\3\23\5\23\u01bf\n\23\5\23\u01c1\n\23\5\23"+
		"\u01c3\n\23\3\23\6\23\u01c6\n\23\r\23\16\23\u01c7\3\23\3\23\7\23\u01cc"+
		"\n\23\f\23\16\23\u01cf\13\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u01d7"+
		"\n\23\3\23\6\23\u01da\n\23\r\23\16\23\u01db\3\23\3\23\7\23\u01e0\n\23"+
		"\f\23\16\23\u01e3\13\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u01eb\n\23"+
		"\3\23\6\23\u01ee\n\23\r\23\16\23\u01ef\3\23\3\23\7\23\u01f4\n\23\f\23"+
		"\16\23\u01f7\13\23\3\23\3\23\3\23\3\23\3\23\5\23\u01fe\n\23\3\23\6\23"+
		"\u0201\n\23\r\23\16\23\u0202\3\23\3\23\7\23\u0207\n\23\f\23\16\23\u020a"+
		"\13\23\3\23\5\23\u020d\n\23\3\24\3\24\3\24\5\24\u0212\n\24\3\24\6\24\u0215"+
		"\n\24\r\24\16\24\u0216\3\24\3\24\7\24\u021b\n\24\f\24\16\24\u021e\13\24"+
		"\3\24\3\24\7\24\u0222\n\24\f\24\16\24\u0225\13\24\3\24\3\24\3\24\5\24"+
		"\u022a\n\24\3\24\6\24\u022d\n\24\r\24\16\24\u022e\3\24\3\24\7\24\u0233"+
		"\n\24\f\24\16\24\u0236\13\24\3\24\3\24\7\24\u023a\n\24\f\24\16\24\u023d"+
		"\13\24\5\24\u023f\n\24\3\25\3\25\3\25\5\25\u0244\n\25\3\25\6\25\u0247"+
		"\n\25\r\25\16\25\u0248\3\25\3\25\7\25\u024d\n\25\f\25\16\25\u0250\13\25"+
		"\3\25\3\25\3\25\3\25\5\25\u0256\n\25\3\25\6\25\u0259\n\25\r\25\16\25\u025a"+
		"\3\25\3\25\7\25\u025f\n\25\f\25\16\25\u0262\13\25\3\25\3\25\3\25\3\25"+
		"\5\25\u0268\n\25\3\25\6\25\u026b\n\25\r\25\16\25\u026c\3\25\3\25\7\25"+
		"\u0271\n\25\f\25\16\25\u0274\13\25\3\25\5\25\u0277\n\25\3\26\3\26\3\26"+
		"\5\26\u027c\n\26\3\26\6\26\u027f\n\26\r\26\16\26\u0280\3\26\3\26\7\26"+
		"\u0285\n\26\f\26\16\26\u0288\13\26\3\26\3\26\3\26\7\26\u028d\n\26\f\26"+
		"\16\26\u0290\13\26\3\27\3\27\3\27\5\27\u0295\n\27\3\27\3\27\3\27\5\27"+
		"\u029a\n\27\3\27\6\27\u029d\n\27\r\27\16\27\u029e\3\27\3\27\7\27\u02a3"+
		"\n\27\f\27\16\27\u02a6\13\27\3\27\5\27\u02a9\n\27\3\27\3\27\3\27\5\27"+
		"\u02ae\n\27\3\27\3\27\3\27\3\27\5\27\u02b4\n\27\7\27\u02b6\n\27\f\27\16"+
		"\27\u02b9\13\27\3\27\3\27\6\27\u02bd\n\27\r\27\16\27\u02be\3\27\3\27\7"+
		"\27\u02c3\n\27\f\27\16\27\u02c6\13\27\3\27\5\27\u02c9\n\27\5\27\u02cb"+
		"\n\27\3\30\3\30\3\30\3\30\5\30\u02d1\n\30\3\30\6\30\u02d4\n\30\r\30\16"+
		"\30\u02d5\3\30\3\30\7\30\u02da\n\30\f\30\16\30\u02dd\13\30\3\30\5\30\u02e0"+
		"\n\30\3\30\3\30\3\30\6\30\u02e5\n\30\r\30\16\30\u02e6\3\30\3\30\7\30\u02eb"+
		"\n\30\f\30\16\30\u02ee\13\30\3\30\5\30\u02f1\n\30\5\30\u02f3\n\30\3\31"+
		"\6\31\u02f6\n\31\r\31\16\31\u02f7\3\31\7\31\u02fb\n\31\f\31\16\31\u02fe"+
		"\13\31\3\31\7\31\u0301\n\31\f\31\16\31\u0304\13\31\3\32\3\32\6\32\u0308"+
		"\n\32\r\32\16\32\u0309\3\32\7\32\u030d\n\32\f\32\16\32\u0310\13\32\3\32"+
		"\7\32\u0313\n\32\f\32\16\32\u0316\13\32\3\32\3\32\3\32\3\32\5\32\u031c"+
		"\n\32\5\32\u031e\n\32\3\32\6\32\u0321\n\32\r\32\16\32\u0322\3\32\7\32"+
		"\u0326\n\32\f\32\16\32\u0329\13\32\3\32\7\32\u032c\n\32\f\32\16\32\u032f"+
		"\13\32\3\32\5\32\u0332\n\32\3\32\3\32\3\32\3\32\7\32\u0338\n\32\f\32\16"+
		"\32\u033b\13\32\3\32\6\32\u033e\n\32\r\32\16\32\u033f\3\32\7\32\u0343"+
		"\n\32\f\32\16\32\u0346\13\32\3\32\7\32\u0349\n\32\f\32\16\32\u034c\13"+
		"\32\3\32\5\32\u034f\n\32\3\32\3\32\3\32\6\32\u0354\n\32\r\32\16\32\u0355"+
		"\3\32\7\32\u0359\n\32\f\32\16\32\u035c\13\32\3\32\7\32\u035f\n\32\f\32"+
		"\16\32\u0362\13\32\3\32\5\32\u0365\n\32\3\32\5\32\u0368\n\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u0371\n\32\3\32\6\32\u0374\n\32\r\32\16"+
		"\32\u0375\3\32\7\32\u0379\n\32\f\32\16\32\u037c\13\32\3\32\7\32\u037f"+
		"\n\32\f\32\16\32\u0382\13\32\3\32\5\32\u0385\n\32\3\32\5\32\u0388\n\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u038f\n\32\3\32\6\32\u0392\n\32\r\32\16"+
		"\32\u0393\3\32\7\32\u0397\n\32\f\32\16\32\u039a\13\32\3\32\7\32\u039d"+
		"\n\32\f\32\16\32\u03a0\13\32\3\32\5\32\u03a3\n\32\3\32\5\32\u03a6\n\32"+
		"\3\32\3\32\3\32\5\32\u03ab\n\32\3\32\6\32\u03ae\n\32\r\32\16\32\u03af"+
		"\3\32\7\32\u03b3\n\32\f\32\16\32\u03b6\13\32\3\32\7\32\u03b9\n\32\f\32"+
		"\16\32\u03bc\13\32\3\32\5\32\u03bf\n\32\3\32\5\32\u03c2\n\32\3\32\3\32"+
		"\5\32\u03c6\n\32\3\32\6\32\u03c9\n\32\r\32\16\32\u03ca\3\32\7\32\u03ce"+
		"\n\32\f\32\16\32\u03d1\13\32\5\32\u03d3\n\32\3\33\3\33\5\33\u03d7\n\33"+
		"\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\5\36\u03e2\n\36\3\36\3\36"+
		"\3\37\3\37\5\37\u03e8\n\37\3 \3 \3 \3!\3!\3!\3!\3!\5!\u03f2\n!\3\"\3\""+
		"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u0401\n\"\3\"\5\"\u0404"+
		"\n\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\""+
		"\3\"\3\"\7\"\u042a\n\"\f\"\16\"\u042d\13\"\3#\3#\3#\3#\3#\3#\3#\3#\3#"+
		"\3#\3#\5#\u043a\n#\3#\3#\3#\3#\3#\3#\5#\u0442\n#\3#\3#\3#\3#\3#\5#\u0449"+
		"\n#\3#\3#\3#\3#\3#\3#\3#\5#\u0452\n#\3#\5#\u0455\n#\3#\5#\u0458\n#\3#"+
		"\2\3B$\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<"+
		">@BD\2\r\4\2\4\4\7\7\3\2\63\64\3\2\26\27\3\2FG\4\2\21\21\35\35\4\2$%*"+
		"*\3\2\22\23\3\2&\'\3\2$%\3\2CD\3\2\24\25\2\u051a\2I\3\2\2\2\4Y\3\2\2\2"+
		"\6|\3\2\2\2\b\u008b\3\2\2\2\n\u00a6\3\2\2\2\f\u00b7\3\2\2\2\16\u00ca\3"+
		"\2\2\2\20\u00df\3\2\2\2\22\u00f9\3\2\2\2\24\u010b\3\2\2\2\26\u0128\3\2"+
		"\2\2\30\u0130\3\2\2\2\32\u0138\3\2\2\2\34\u0141\3\2\2\2\36\u015c\3\2\2"+
		"\2 \u015e\3\2\2\2\"\u016c\3\2\2\2$\u020c\3\2\2\2&\u023e\3\2\2\2(\u0276"+
		"\3\2\2\2*\u0278\3\2\2\2,\u02ca\3\2\2\2.\u02f2\3\2\2\2\60\u02f5\3\2\2\2"+
		"\62\u03d2\3\2\2\2\64\u03d4\3\2\2\2\66\u03da\3\2\2\28\u03dd\3\2\2\2:\u03e1"+
		"\3\2\2\2<\u03e5\3\2\2\2>\u03e9\3\2\2\2@\u03ec\3\2\2\2B\u0403\3\2\2\2D"+
		"\u0457\3\2\2\2FH\7\7\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JS\3\2"+
		"\2\2KI\3\2\2\2LR\5\4\3\2MR\5\f\7\2NR\5\20\t\2OR\5\24\13\2PR\5\60\31\2"+
		"QL\3\2\2\2QM\3\2\2\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2"+
		"ST\3\2\2\2TV\3\2\2\2US\3\2\2\2VW\7\2\2\3W\3\3\2\2\2XZ\7\60\2\2YX\3\2\2"+
		"\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\`\3\2\2\2]_\7\7\2\2^]\3\2\2\2_b\3\2"+
		"\2\2`^\3\2\2\2`a\3\2\2\2av\3\2\2\2b`\3\2\2\2ch\5\6\4\2de\7\"\2\2eg\5\6"+
		"\4\2fd\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2iu\3\2\2\2jh\3\2\2\2ku\5\b"+
		"\5\2lq\5\n\6\2mn\7\"\2\2np\5\n\6\2om\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2"+
		"\2\2ru\3\2\2\2sq\3\2\2\2tc\3\2\2\2tk\3\2\2\2tl\3\2\2\2ux\3\2\2\2vt\3\2"+
		"\2\2vw\3\2\2\2w\5\3\2\2\2xv\3\2\2\2y{\7\3\2\2zy\3\2\2\2{~\3\2\2\2|z\3"+
		"\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7L\2\2\u0080\u0081\7"+
		" \2\2\u0081\u0085\5B\"\2\u0082\u0084\t\2\2\2\u0083\u0082\3\2\2\2\u0084"+
		"\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\7\3\2\2\2"+
		"\u0087\u0085\3\2\2\2\u0088\u008a\7\3\2\2\u0089\u0088\3\2\2\2\u008a\u008d"+
		"\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008e\3\2\2\2\u008d"+
		"\u008b\3\2\2\2\u008e\u008f\7\35\2\2\u008f\u0094\5B\"\2\u0090\u0091\7,"+
		"\2\2\u0091\u0092\5B\"\2\u0092\u0093\7-\2\2\u0093\u0095\3\2\2\2\u0094\u0090"+
		"\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0099\3\2\2\2\u0096\u0098\t\2\2\2\u0097"+
		"\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2"+
		"\2\2\u009a\u00a0\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\7\"\2\2\u009d"+
		"\u009f\5\n\6\2\u009e\u009c\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2"+
		"\2\2\u00a0\u00a1\3\2\2\2\u00a1\t\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3\u00a5"+
		"\7\3\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6"+
		"\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\u00ae\7L"+
		"\2\2\u00aa\u00ab\7,\2\2\u00ab\u00ac\5B\"\2\u00ac\u00ad\7-\2\2\u00ad\u00af"+
		"\3\2\2\2\u00ae\u00aa\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b3\3\2\2\2\u00b0"+
		"\u00b2\t\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4\13\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6\u00b8"+
		"\7\62\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00b7\3\2\2\2"+
		"\u00b9\u00ba\3\2\2\2\u00ba\u00be\3\2\2\2\u00bb\u00bd\7\7\2\2\u00bc\u00bb"+
		"\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		"\u00c4\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c3\5\16\b\2\u00c2\u00c1\3"+
		"\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"\r\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c9\7\3\2\2\u00c8\u00c7\3\2\2\2"+
		"\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cd"+
		"\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00d4\7L\2\2\u00ce\u00cf\7,\2\2\u00cf"+
		"\u00d0\5B\"\2\u00d0\u00d1\7-\2\2\u00d1\u00d3\3\2\2\2\u00d2\u00ce\3\2\2"+
		"\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d7"+
		"\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00d8\7)\2\2\u00d8\u00da\7\t\2\2\u00d9"+
		"\u00db\t\2\2\2\u00da\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00da\3\2"+
		"\2\2\u00dc\u00dd\3\2\2\2\u00dd\17\3\2\2\2\u00de\u00e0\7\61\2\2\u00df\u00de"+
		"\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2"+
		"\u00e6\3\2\2\2\u00e3\u00e5\7\7\2\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2"+
		"\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00f3\3\2\2\2\u00e8"+
		"\u00e6\3\2\2\2\u00e9\u00ee\5\22\n\2\u00ea\u00eb\7\"\2\2\u00eb\u00ed\5"+
		"\22\n\2\u00ec\u00ea\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee"+
		"\u00ef\3\2\2\2\u00ef\u00f2\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00e9\3\2"+
		"\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4"+
		"\21\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6\u00f8\7\3\2\2\u00f7\u00f6\3\2\2"+
		"\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fd"+
		"\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc\u00fe\7J\2\2\u00fd\u00fc\3\2\2\2\u00fd"+
		"\u00fe\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0104\7L\2\2\u0100\u0101\7,\2"+
		"\2\u0101\u0102\5B\"\2\u0102\u0103\7-\2\2\u0103\u0105\3\2\2\2\u0104\u0100"+
		"\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0107\3\2\2\2\u0106\u0108\t\2\2\2\u0107"+
		"\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2"+
		"\2\2\u010a\23\3\2\2\2\u010b\u010c\t\3\2\2\u010c\u010d\7L\2\2\u010d\u010f"+
		"\7.\2\2\u010e\u0110\5\26\f\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"\u0111\3\2\2\2\u0111\u0114\7/\2\2\u0112\u0113\7)\2\2\u0113\u0115\5\30"+
		"\r\2\u0114\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0118\3\2\2\2\u0116"+
		"\u0117\7\32\2\2\u0117\u0119\5\32\16\2\u0118\u0116\3\2\2\2\u0118\u0119"+
		"\3\2\2\2\u0119\u011b\3\2\2\2\u011a\u011c\7\7\2\2\u011b\u011a\3\2\2\2\u011c"+
		"\u011d\3\2\2\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u011f\3\2"+
		"\2\2\u011f\u0123\7\3\2\2\u0120\u0122\5\36\20\2\u0121\u0120\3\2\2\2\u0122"+
		"\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0126\3\2"+
		"\2\2\u0125\u0123\3\2\2\2\u0126\u0127\7\4\2\2\u0127\25\3\2\2\2\u0128\u012d"+
		"\7L\2\2\u0129\u012a\7\"\2\2\u012a\u012c\7L\2\2\u012b\u0129\3\2\2\2\u012c"+
		"\u012f\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\27\3\2\2"+
		"\2\u012f\u012d\3\2\2\2\u0130\u0135\7L\2\2\u0131\u0132\7\"\2\2\u0132\u0134"+
		"\7L\2\2\u0133\u0131\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135"+
		"\u0136\3\2\2\2\u0136\31\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u013d\5\34\17"+
		"\2\u0139\u013a\7\"\2\2\u013a\u013c\5\34\17\2\u013b\u0139\3\2\2\2\u013c"+
		"\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\33\3\2\2"+
		"\2\u013f\u013d\3\2\2\2\u0140\u0142\7I\2\2\u0141\u0140\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0145\7J\2\2\u0144\u0143\3\2\2\2\u0144"+
		"\u0145\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u014b\7L\2\2\u0147\u0148\7,\2"+
		"\2\u0148\u0149\5B\"\2\u0149\u014a\7-\2\2\u014a\u014c\3\2\2\2\u014b\u0147"+
		"\3\2\2\2\u014b\u014c\3\2\2\2\u014c\35\3\2\2\2\u014d\u014f\5 \21\2\u014e"+
		"\u0150\7\7\2\2\u014f\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u014f\3\2"+
		"\2\2\u0151\u0152\3\2\2\2\u0152\u015d\3\2\2\2\u0153\u0155\5\"\22\2\u0154"+
		"\u0156\7\7\2\2\u0155\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0155\3\2"+
		"\2\2\u0157\u0158\3\2\2\2\u0158\u015d\3\2\2\2\u0159\u015d\5$\23\2\u015a"+
		"\u015d\5&\24\2\u015b\u015d\5*\26\2\u015c\u014d\3\2\2\2\u015c\u0153\3\2"+
		"\2\2\u015c\u0159\3\2\2\2\u015c\u015a\3\2\2\2\u015c\u015b\3\2\2\2\u015d"+
		"\37\3\2\2\2\u015e\u0163\7L\2\2\u015f\u0160\7\"\2\2\u0160\u0162\7L\2\2"+
		"\u0161\u015f\3\2\2\2\u0162\u0165\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164"+
		"\3\2\2\2\u0164\u0166\3\2\2\2\u0165\u0163\3\2\2\2\u0166\u0169\t\4\2\2\u0167"+
		"\u016a\5\"\22\2\u0168\u016a\5B\"\2\u0169\u0167\3\2\2\2\u0169\u0168\3\2"+
		"\2\2\u016a!\3\2\2\2\u016b\u016d\7#\2\2\u016c\u016b\3\2\2\2\u016c\u016d"+
		"\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\7L\2\2\u016f\u017c\7.\2\2\u0170"+
		"\u017d\5 \21\2\u0171\u017d\5\"\22\2\u0172\u0179\5B\"\2\u0173\u0174\7\""+
		"\2\2\u0174\u0178\5 \21\2\u0175\u0178\5\"\22\2\u0176\u0178\5B\"\2\u0177"+
		"\u0173\3\2\2\2\u0177\u0175\3\2\2\2\u0177\u0176\3\2\2\2\u0178\u017b\3\2"+
		"\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u017d\3\2\2\2\u017b"+
		"\u0179\3\2\2\2\u017c\u0170\3\2\2\2\u017c\u0171\3\2\2\2\u017c\u0172\3\2"+
		"\2\2\u017c\u017d\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017f\7/\2\2\u017f"+
		"#\3\2\2\2\u0180\u0182\7\66\2\2\u0181\u0183\7\7\2\2\u0182\u0181\3\2\2\2"+
		"\u0183\u0184\3\2\2\2\u0184\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0186"+
		"\3\2\2\2\u0186\u018a\7\3\2\2\u0187\u0189\5\36\20\2\u0188\u0187\3\2\2\2"+
		"\u0189\u018c\3\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018d"+
		"\3\2\2\2\u018c\u018a\3\2\2\2\u018d\u018e\7\4\2\2\u018e\u0191\7:\2\2\u018f"+
		"\u0192\5\"\22\2\u0190\u0192\5B\"\2\u0191\u018f\3\2\2\2\u0191\u0190\3\2"+
		"\2\2\u0192\u0194\3\2\2\2\u0193\u0195\7\7\2\2\u0194\u0193\3\2\2\2\u0195"+
		"\u0196\3\2\2\2\u0196\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u020d\3\2"+
		"\2\2\u0198\u019a\7\66\2\2\u0199\u019b\7\7\2\2\u019a\u0199\3\2\2\2\u019b"+
		"\u019c\3\2\2\2\u019c\u019a\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019e\3\2"+
		"\2\2\u019e\u01a2\7\3\2\2\u019f\u01a1\5\36\20\2\u01a0\u019f\3\2\2\2\u01a1"+
		"\u01a4\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a5\3\2"+
		"\2\2\u01a4\u01a2\3\2\2\2\u01a5\u01a6\7\4\2\2\u01a6\u01a9\7;\2\2\u01a7"+
		"\u01aa\5\"\22\2\u01a8\u01aa\5B\"\2\u01a9\u01a7\3\2\2\2\u01a9\u01a8\3\2"+
		"\2\2\u01aa\u01ac\3\2\2\2\u01ab\u01ad\7\7\2\2\u01ac\u01ab\3\2\2\2\u01ad"+
		"\u01ae\3\2\2\2\u01ae\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u020d\3\2"+
		"\2\2\u01b0\u01b1\7\66\2\2\u01b1\u01b4\7\67\2\2\u01b2\u01b5\5\"\22\2\u01b3"+
		"\u01b5\5B\"\2\u01b4\u01b2\3\2\2\2\u01b4\u01b3\3\2\2\2\u01b5\u01c2\3\2"+
		"\2\2\u01b6\u01b9\78\2\2\u01b7\u01ba\5\"\22\2\u01b8\u01ba\5B\"\2\u01b9"+
		"\u01b7\3\2\2\2\u01b9\u01b8\3\2\2\2\u01ba\u01c0\3\2\2\2\u01bb\u01be\79"+
		"\2\2\u01bc\u01bf\5\"\22\2\u01bd\u01bf\5B\"\2\u01be\u01bc\3\2\2\2\u01be"+
		"\u01bd\3\2\2\2\u01bf\u01c1\3\2\2\2\u01c0\u01bb\3\2\2\2\u01c0\u01c1\3\2"+
		"\2\2\u01c1\u01c3\3\2\2\2\u01c2\u01b6\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3"+
		"\u01c5\3\2\2\2\u01c4\u01c6\7\7\2\2\u01c5\u01c4\3\2\2\2\u01c6\u01c7\3\2"+
		"\2\2\u01c7\u01c5\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"\u01cd\7\3\2\2\u01ca\u01cc\5\36\20\2\u01cb\u01ca\3\2\2\2\u01cc\u01cf\3"+
		"\2\2\2\u01cd\u01cb\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01d0\3\2\2\2\u01cf"+
		"\u01cd\3\2\2\2\u01d0\u01d1\7\4\2\2\u01d1\u020d\3\2\2\2\u01d2\u01d3\7\66"+
		"\2\2\u01d3\u01d6\7:\2\2\u01d4\u01d7\5\"\22\2\u01d5\u01d7\5B\"\2\u01d6"+
		"\u01d4\3\2\2\2\u01d6\u01d5\3\2\2\2\u01d7\u01d9\3\2\2\2\u01d8\u01da\7\7"+
		"\2\2\u01d9\u01d8\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01d9\3\2\2\2\u01db"+
		"\u01dc\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd\u01e1\7\3\2\2\u01de\u01e0\5\36"+
		"\20\2\u01df\u01de\3\2\2\2\u01e0\u01e3\3\2\2\2\u01e1\u01df\3\2\2\2\u01e1"+
		"\u01e2\3\2\2\2\u01e2\u01e4\3\2\2\2\u01e3\u01e1\3\2\2\2\u01e4\u01e5\7\4"+
		"\2\2\u01e5\u020d\3\2\2\2\u01e6\u01e7\7\66\2\2\u01e7\u01ea\7;\2\2\u01e8"+
		"\u01eb\5\"\22\2\u01e9\u01eb\5B\"\2\u01ea\u01e8\3\2\2\2\u01ea\u01e9\3\2"+
		"\2\2\u01eb\u01ed\3\2\2\2\u01ec\u01ee\7\7\2\2\u01ed\u01ec\3\2\2\2\u01ee"+
		"\u01ef\3\2\2\2\u01ef\u01ed\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0\u01f1\3\2"+
		"\2\2\u01f1\u01f5\7\3\2\2\u01f2\u01f4\5\36\20\2\u01f3\u01f2\3\2\2\2\u01f4"+
		"\u01f7\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f8\3\2"+
		"\2\2\u01f7\u01f5\3\2\2\2\u01f8\u01f9\7\4\2\2\u01f9\u020d\3\2\2\2\u01fa"+
		"\u01fd\7\66\2\2\u01fb\u01fe\5\"\22\2\u01fc\u01fe\5B\"\2\u01fd\u01fb\3"+
		"\2\2\2\u01fd\u01fc\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0200\3\2\2\2\u01ff"+
		"\u0201\7\7\2\2\u0200\u01ff\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0200\3\2"+
		"\2\2\u0202\u0203\3\2\2\2\u0203\u0204\3\2\2\2\u0204\u0208\7\3\2\2\u0205"+
		"\u0207\5\36\20\2\u0206\u0205\3\2\2\2\u0207\u020a\3\2\2\2\u0208\u0206\3"+
		"\2\2\2\u0208\u0209\3\2\2\2\u0209\u020b\3\2\2\2\u020a\u0208\3\2\2\2\u020b"+
		"\u020d\7\4\2\2\u020c\u0180\3\2\2\2\u020c\u0198\3\2\2\2\u020c\u01b0\3\2"+
		"\2\2\u020c\u01d2\3\2\2\2\u020c\u01e6\3\2\2\2\u020c\u01fa\3\2\2\2\u020d"+
		"%\3\2\2\2\u020e\u0211\7@\2\2\u020f\u0212\5\"\22\2\u0210\u0212\5B\"\2\u0211"+
		"\u020f\3\2\2\2\u0211\u0210\3\2\2\2\u0211\u0212\3\2\2\2\u0212\u0214\3\2"+
		"\2\2\u0213\u0215\7\7\2\2\u0214\u0213\3\2\2\2\u0215\u0216\3\2\2\2\u0216"+
		"\u0214\3\2\2\2\u0216\u0217\3\2\2\2\u0217\u0218\3\2\2\2\u0218\u021c\7\3"+
		"\2\2\u0219\u021b\5\36\20\2\u021a\u0219\3\2\2\2\u021b\u021e\3\2\2\2\u021c"+
		"\u021a\3\2\2\2\u021c\u021d\3\2\2\2\u021d\u021f\3\2\2\2\u021e\u021c\3\2"+
		"\2\2\u021f\u0223\7\4\2\2\u0220\u0222\5(\25\2\u0221\u0220\3\2\2\2\u0222"+
		"\u0225\3\2\2\2\u0223\u0221\3\2\2\2\u0223\u0224\3\2\2\2\u0224\u023f\3\2"+
		"\2\2\u0225\u0223\3\2\2\2\u0226\u0229\7?\2\2\u0227\u022a\5\"\22\2\u0228"+
		"\u022a\5B\"\2\u0229\u0227\3\2\2\2\u0229\u0228\3\2\2\2\u0229\u022a\3\2"+
		"\2\2\u022a\u022c\3\2\2\2\u022b\u022d\7\7\2\2\u022c\u022b\3\2\2\2\u022d"+
		"\u022e\3\2\2\2\u022e\u022c\3\2\2\2\u022e\u022f\3\2\2\2\u022f\u0230\3\2"+
		"\2\2\u0230\u0234\7\3\2\2\u0231\u0233\5\36\20\2\u0232\u0231\3\2\2\2\u0233"+
		"\u0236\3\2\2\2\u0234\u0232\3\2\2\2\u0234\u0235\3\2\2\2\u0235\u0237\3\2"+
		"\2\2\u0236\u0234\3\2\2\2\u0237\u023b\7\4\2\2\u0238\u023a\5(\25\2\u0239"+
		"\u0238\3\2\2\2\u023a\u023d\3\2\2\2\u023b\u0239\3\2\2\2\u023b\u023c\3\2"+
		"\2\2\u023c\u023f\3\2\2\2\u023d\u023b\3\2\2\2\u023e\u020e\3\2\2\2\u023e"+
		"\u0226\3\2\2\2\u023f\'\3\2\2\2\u0240\u0243\7>\2\2\u0241\u0244\5\"\22\2"+
		"\u0242\u0244\5B\"\2\u0243\u0241\3\2\2\2\u0243\u0242\3\2\2\2\u0243\u0244"+
		"\3\2\2\2\u0244\u0246\3\2\2\2\u0245\u0247\7\7\2\2\u0246\u0245\3\2\2\2\u0247"+
		"\u0248\3\2\2\2\u0248\u0246\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024a\3\2"+
		"\2\2\u024a\u024e\7\3\2\2\u024b\u024d\5\36\20\2\u024c\u024b\3\2\2\2\u024d"+
		"\u0250\3\2\2\2\u024e\u024c\3\2\2\2\u024e\u024f\3\2\2\2\u024f\u0251\3\2"+
		"\2\2\u0250\u024e\3\2\2\2\u0251\u0277\7\4\2\2\u0252\u0255\7=\2\2\u0253"+
		"\u0256\5\"\22\2\u0254\u0256\5B\"\2\u0255\u0253\3\2\2\2\u0255\u0254\3\2"+
		"\2\2\u0255\u0256\3\2\2\2\u0256\u0258\3\2\2\2\u0257\u0259\7\7\2\2\u0258"+
		"\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u0258\3\2\2\2\u025a\u025b\3\2"+
		"\2\2\u025b\u025c\3\2\2\2\u025c\u0260\7\3\2\2\u025d\u025f\5\36\20\2\u025e"+
		"\u025d\3\2\2\2\u025f\u0262\3\2\2\2\u0260\u025e\3\2\2\2\u0260\u0261\3\2"+
		"\2\2\u0261\u0263\3\2\2\2\u0262\u0260\3\2\2\2\u0263\u0277\7\4\2\2\u0264"+
		"\u0267\7<\2\2\u0265\u0268\5\"\22\2\u0266\u0268\5B\"\2\u0267\u0265\3\2"+
		"\2\2\u0267\u0266\3\2\2\2\u0267\u0268\3\2\2\2\u0268\u026a\3\2\2\2\u0269"+
		"\u026b\7\7\2\2\u026a\u0269\3\2\2\2\u026b\u026c\3\2\2\2\u026c\u026a\3\2"+
		"\2\2\u026c\u026d\3\2\2\2\u026d\u026e\3\2\2\2\u026e\u0272\7\3\2\2\u026f"+
		"\u0271\5\36\20\2\u0270\u026f\3\2\2\2\u0271\u0274\3\2\2\2\u0272\u0270\3"+
		"\2\2\2\u0272\u0273\3\2\2\2\u0273\u0275\3\2\2\2\u0274\u0272\3\2\2\2\u0275"+
		"\u0277\7\4\2\2\u0276\u0240\3\2\2\2\u0276\u0252\3\2\2\2\u0276\u0264\3\2"+
		"\2\2\u0277)\3\2\2\2\u0278\u027b\7A\2\2\u0279\u027c\5\"\22\2\u027a\u027c"+
		"\5B\"\2\u027b\u0279\3\2\2\2\u027b\u027a\3\2\2\2\u027b\u027c\3\2\2\2\u027c"+
		"\u027e\3\2\2\2\u027d\u027f\7\7\2\2\u027e\u027d\3\2\2\2\u027f\u0280\3\2"+
		"\2\2\u0280\u027e\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u0282\3\2\2\2\u0282"+
		"\u0286\7\3\2\2\u0283\u0285\5\36\20\2\u0284\u0283\3\2\2\2\u0285\u0288\3"+
		"\2\2\2\u0286\u0284\3\2\2\2\u0286\u0287\3\2\2\2\u0287\u0289\3\2\2\2\u0288"+
		"\u0286\3\2\2\2\u0289\u028e\7\4\2\2\u028a\u028d\5,\27\2\u028b\u028d\5."+
		"\30\2\u028c\u028a\3\2\2\2\u028c\u028b\3\2\2\2\u028d\u0290\3\2\2\2\u028e"+
		"\u028c\3\2\2\2\u028e\u028f\3\2\2\2\u028f+\3\2\2\2\u0290\u028e\3\2\2\2"+
		"\u0291\u0294\5B\"\2\u0292\u0293\7\30\2\2\u0293\u0295\5B\"\2\u0294\u0292"+
		"\3\2\2\2\u0294\u0295\3\2\2\2\u0295\u0296\3\2\2\2\u0296\u0299\7)\2\2\u0297"+
		"\u029a\5 \21\2\u0298\u029a\5\"\22\2\u0299\u0297\3\2\2\2\u0299\u0298\3"+
		"\2\2\2\u029a\u029c\3\2\2\2\u029b\u029d\7\7\2\2\u029c\u029b\3\2\2\2\u029d"+
		"\u029e\3\2\2\2\u029e\u029c\3\2\2\2\u029e\u029f\3\2\2\2\u029f\u02a8\3\2"+
		"\2\2\u02a0\u02a4\7\3\2\2\u02a1\u02a3\5\36\20\2\u02a2\u02a1\3\2\2\2\u02a3"+
		"\u02a6\3\2\2\2\u02a4\u02a2\3\2\2\2\u02a4\u02a5\3\2\2\2\u02a5\u02a7\3\2"+
		"\2\2\u02a6\u02a4\3\2\2\2\u02a7\u02a9\7\4\2\2\u02a8\u02a0\3\2\2\2\u02a8"+
		"\u02a9\3\2\2\2\u02a9\u02cb\3\2\2\2\u02aa\u02ad\5B\"\2\u02ab\u02ac\7\30"+
		"\2\2\u02ac\u02ae\5B\"\2\u02ad\u02ab\3\2\2\2\u02ad\u02ae\3\2\2\2\u02ae"+
		"\u02b7\3\2\2\2\u02af\u02b0\7\"\2\2\u02b0\u02b3\5B\"\2\u02b1\u02b2\7\30"+
		"\2\2\u02b2\u02b4\5B\"\2\u02b3\u02b1\3\2\2\2\u02b3\u02b4\3\2\2\2\u02b4"+
		"\u02b6\3\2\2\2\u02b5\u02af\3\2\2\2\u02b6\u02b9\3\2\2\2\u02b7\u02b5\3\2"+
		"\2\2\u02b7\u02b8\3\2\2\2\u02b8\u02ba\3\2\2\2\u02b9\u02b7\3\2\2\2\u02ba"+
		"\u02bc\7)\2\2\u02bb\u02bd\7\7\2\2\u02bc\u02bb\3\2\2\2\u02bd\u02be\3\2"+
		"\2\2\u02be\u02bc\3\2\2\2\u02be\u02bf\3\2\2\2\u02bf\u02c8\3\2\2\2\u02c0"+
		"\u02c4\7\3\2\2\u02c1\u02c3\5\36\20\2\u02c2\u02c1\3\2\2\2\u02c3\u02c6\3"+
		"\2\2\2\u02c4\u02c2\3\2\2\2\u02c4\u02c5\3\2\2\2\u02c5\u02c7\3\2\2\2\u02c6"+
		"\u02c4\3\2\2\2\u02c7\u02c9\7\4\2\2\u02c8\u02c0\3\2\2\2\u02c8\u02c9\3\2"+
		"\2\2\u02c9\u02cb\3\2\2\2\u02ca\u0291\3\2\2\2\u02ca\u02aa\3\2\2\2\u02cb"+
		"-\3\2\2\2\u02cc\u02cd\7B\2\2\u02cd\u02d0\7)\2\2\u02ce\u02d1\5 \21\2\u02cf"+
		"\u02d1\5\"\22\2\u02d0\u02ce\3\2\2\2\u02d0\u02cf\3\2\2\2\u02d1\u02d3\3"+
		"\2\2\2\u02d2\u02d4\7\7\2\2\u02d3\u02d2\3\2\2\2\u02d4\u02d5\3\2\2\2\u02d5"+
		"\u02d3\3\2\2\2\u02d5\u02d6\3\2\2\2\u02d6\u02df\3\2\2\2\u02d7\u02db\7\3"+
		"\2\2\u02d8\u02da\5\36\20\2\u02d9\u02d8\3\2\2\2\u02da\u02dd\3\2\2\2\u02db"+
		"\u02d9\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc\u02de\3\2\2\2\u02dd\u02db\3\2"+
		"\2\2\u02de\u02e0\7\4\2\2\u02df\u02d7\3\2\2\2\u02df\u02e0\3\2\2\2\u02e0"+
		"\u02f3\3\2\2\2\u02e1\u02e2\7B\2\2\u02e2\u02e4\7)\2\2\u02e3\u02e5\7\7\2"+
		"\2\u02e4\u02e3\3\2\2\2\u02e5\u02e6\3\2\2\2\u02e6\u02e4\3\2\2\2\u02e6\u02e7"+
		"\3\2\2\2\u02e7\u02f0\3\2\2\2\u02e8\u02ec\7\3\2\2\u02e9\u02eb\5\36\20\2"+
		"\u02ea\u02e9\3\2\2\2\u02eb\u02ee\3\2\2\2\u02ec\u02ea\3\2\2\2\u02ec\u02ed"+
		"\3\2\2\2\u02ed\u02ef\3\2\2\2\u02ee\u02ec\3\2\2\2\u02ef\u02f1\7\4\2\2\u02f0"+
		"\u02e8\3\2\2\2\u02f0\u02f1\3\2\2\2\u02f1\u02f3\3\2\2\2\u02f2\u02cc\3\2"+
		"\2\2\u02f2\u02e1\3\2\2\2\u02f3/\3\2\2\2\u02f4\u02f6\7\65\2\2\u02f5\u02f4"+
		"\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7\u02f5\3\2\2\2\u02f7\u02f8\3\2\2\2\u02f8"+
		"\u02fc\3\2\2\2\u02f9\u02fb\7\7\2\2\u02fa\u02f9\3\2\2\2\u02fb\u02fe\3\2"+
		"\2\2\u02fc\u02fa\3\2\2\2\u02fc\u02fd\3\2\2\2\u02fd\u0302\3\2\2\2\u02fe"+
		"\u02fc\3\2\2\2\u02ff\u0301\5\62\32\2\u0300\u02ff\3\2\2\2\u0301\u0304\3"+
		"\2\2\2\u0302\u0300\3\2\2\2\u0302\u0303\3\2\2\2\u0303\61\3\2\2\2\u0304"+
		"\u0302\3\2\2\2\u0305\u0307\5\64\33\2\u0306\u0308\7\7\2\2\u0307\u0306\3"+
		"\2\2\2\u0308\u0309\3\2\2\2\u0309\u0307\3\2\2\2\u0309\u030a\3\2\2\2\u030a"+
		"\u030e\3\2\2\2\u030b\u030d\t\2\2\2\u030c\u030b\3\2\2\2\u030d\u0310\3\2"+
		"\2\2\u030e\u030c\3\2\2\2\u030e\u030f\3\2\2\2\u030f\u03d3\3\2\2\2\u0310"+
		"\u030e\3\2\2\2\u0311\u0313\7\3\2\2\u0312\u0311\3\2\2\2\u0313\u0316\3\2"+
		"\2\2\u0314\u0312\3\2\2\2\u0314\u0315\3\2\2\2\u0315\u0317\3\2\2\2\u0316"+
		"\u0314\3\2\2\2\u0317\u031d\t\5\2\2\u0318\u031b\5B\"\2\u0319\u031a\7\""+
		"\2\2\u031a\u031c\5B\"\2\u031b\u0319\3\2\2\2\u031b\u031c\3\2\2\2\u031c"+
		"\u031e\3\2\2\2\u031d\u0318\3\2\2\2\u031d\u031e\3\2\2\2\u031e\u0320\3\2"+
		"\2\2\u031f\u0321\7\7\2\2\u0320\u031f\3\2\2\2\u0321\u0322\3\2\2\2\u0322"+
		"\u0320\3\2\2\2\u0322\u0323\3\2\2\2\u0323\u0327\3\2\2\2\u0324\u0326\t\2"+
		"\2\2\u0325\u0324\3\2\2\2\u0326\u0329\3\2\2\2\u0327\u0325\3\2\2\2\u0327"+
		"\u0328\3\2\2\2\u0328\u03d3\3\2\2\2\u0329\u0327\3\2\2\2\u032a\u032c\7\3"+
		"\2\2\u032b\u032a\3\2\2\2\u032c\u032f\3\2\2\2\u032d\u032b\3\2\2\2\u032d"+
		"\u032e\3\2\2\2\u032e\u0331\3\2\2\2\u032f\u032d\3\2\2\2\u0330\u0332\5\64"+
		"\33\2\u0331\u0330\3\2\2\2\u0331\u0332\3\2\2\2\u0332\u0333\3\2\2\2\u0333"+
		"\u0334\7J\2\2\u0334\u0339\5@!\2\u0335\u0336\7\"\2\2\u0336\u0338\5@!\2"+
		"\u0337\u0335\3\2\2\2\u0338\u033b\3\2\2\2\u0339\u0337\3\2\2\2\u0339\u033a"+
		"\3\2\2\2\u033a\u033d\3\2\2\2\u033b\u0339\3\2\2\2\u033c\u033e\7\7\2\2\u033d"+
		"\u033c\3\2\2\2\u033e\u033f\3\2\2\2\u033f\u033d\3\2\2\2\u033f\u0340\3\2"+
		"\2\2\u0340\u0344\3\2\2\2\u0341\u0343\t\2\2\2\u0342\u0341\3\2\2\2\u0343"+
		"\u0346\3\2\2\2\u0344\u0342\3\2\2\2\u0344\u0345\3\2\2\2\u0345\u03d3\3\2"+
		"\2\2\u0346\u0344\3\2\2\2\u0347\u0349\7\3\2\2\u0348\u0347\3\2\2\2\u0349"+
		"\u034c\3\2\2\2\u034a\u0348\3\2\2\2\u034a\u034b\3\2\2\2\u034b\u034e\3\2"+
		"\2\2\u034c\u034a\3\2\2\2\u034d\u034f\5\64\33\2\u034e\u034d\3\2\2\2\u034e"+
		"\u034f\3\2\2\2\u034f\u0350\3\2\2\2\u0350\u0351\7H\2\2\u0351\u0353\5@!"+
		"\2\u0352\u0354\7\7\2\2\u0353\u0352\3\2\2\2\u0354\u0355\3\2\2\2\u0355\u0353"+
		"\3\2\2\2\u0355\u0356\3\2\2\2\u0356\u035a\3\2\2\2\u0357\u0359\t\2\2\2\u0358"+
		"\u0357\3\2\2\2\u0359\u035c\3\2\2\2\u035a\u0358\3\2\2\2\u035a\u035b\3\2"+
		"\2\2\u035b\u03d3\3\2\2\2\u035c\u035a\3\2\2\2\u035d\u035f\7\3\2\2\u035e"+
		"\u035d\3\2\2\2\u035f\u0362\3\2\2\2\u0360\u035e\3\2\2\2\u0360\u0361\3\2"+
		"\2\2\u0361\u0364\3\2\2\2\u0362\u0360\3\2\2\2\u0363\u0365\5\64\33\2\u0364"+
		"\u0363\3\2\2\2\u0364\u0365\3\2\2\2\u0365\u0367\3\2\2\2\u0366\u0368\5\66"+
		"\34\2\u0367\u0366\3\2\2\2\u0367\u0368\3\2\2\2\u0368\u0369\3\2\2\2\u0369"+
		"\u036a\58\35\2\u036a\u036b\5:\36\2\u036b\u036c\7\"\2\2\u036c\u036d\5:"+
		"\36\2\u036d\u036e\7\"\2\2\u036e\u0370\5:\36\2\u036f\u0371\5> \2\u0370"+
		"\u036f\3\2\2\2\u0370\u0371\3\2\2\2\u0371\u0373\3\2\2\2\u0372\u0374\7\7"+
		"\2\2\u0373\u0372\3\2\2\2\u0374\u0375\3\2\2\2\u0375\u0373\3\2\2\2\u0375"+
		"\u0376\3\2\2\2\u0376\u037a\3\2\2\2\u0377\u0379\t\2\2\2\u0378\u0377\3\2"+
		"\2\2\u0379\u037c\3\2\2\2\u037a\u0378\3\2\2\2\u037a\u037b\3\2\2\2\u037b"+
		"\u03d3\3\2\2\2\u037c\u037a\3\2\2\2\u037d\u037f\7\3\2\2\u037e\u037d\3\2"+
		"\2\2\u037f\u0382\3\2\2\2\u0380\u037e\3\2\2\2\u0380\u0381\3\2\2\2\u0381"+
		"\u0384\3\2\2\2\u0382\u0380\3\2\2\2\u0383\u0385\5\64\33\2\u0384\u0383\3"+
		"\2\2\2\u0384\u0385\3\2\2\2\u0385\u0387\3\2\2\2\u0386\u0388\5\66\34\2\u0387"+
		"\u0386\3\2\2\2\u0387\u0388\3\2\2\2\u0388\u0389\3\2\2\2\u0389\u038a\58"+
		"\35\2\u038a\u038b\5:\36\2\u038b\u038c\7\"\2\2\u038c\u038e\5:\36\2\u038d"+
		"\u038f\5> \2\u038e\u038d\3\2\2\2\u038e\u038f\3\2\2\2\u038f\u0391\3\2\2"+
		"\2\u0390\u0392\7\7\2\2\u0391\u0390\3\2\2\2\u0392\u0393\3\2\2\2\u0393\u0391"+
		"\3\2\2\2\u0393\u0394\3\2\2\2\u0394\u0398\3\2\2\2\u0395\u0397\t\2\2\2\u0396"+
		"\u0395\3\2\2\2\u0397\u039a\3\2\2\2\u0398\u0396\3\2\2\2\u0398\u0399\3\2"+
		"\2\2\u0399\u03d3\3\2\2\2\u039a\u0398\3\2\2\2\u039b\u039d\7\3\2\2\u039c"+
		"\u039b\3\2\2\2\u039d\u03a0\3\2\2\2\u039e\u039c\3\2\2\2\u039e\u039f\3\2"+
		"\2\2\u039f\u03a2\3\2\2\2\u03a0\u039e\3\2\2\2\u03a1\u03a3\5\64\33\2\u03a2"+
		"\u03a1\3\2\2\2\u03a2\u03a3\3\2\2\2\u03a3\u03a5\3\2\2\2\u03a4\u03a6\5\66"+
		"\34\2\u03a5\u03a4\3\2\2\2\u03a5\u03a6\3\2\2\2\u03a6\u03a7\3\2\2\2\u03a7"+
		"\u03a8\58\35\2\u03a8\u03aa\5:\36\2\u03a9\u03ab\5> \2\u03aa\u03a9\3\2\2"+
		"\2\u03aa\u03ab\3\2\2\2\u03ab\u03ad\3\2\2\2\u03ac\u03ae\7\7\2\2\u03ad\u03ac"+
		"\3\2\2\2\u03ae\u03af\3\2\2\2\u03af\u03ad\3\2\2\2\u03af\u03b0\3\2\2\2\u03b0"+
		"\u03b4\3\2\2\2\u03b1\u03b3\t\2\2\2\u03b2\u03b1\3\2\2\2\u03b3\u03b6\3\2"+
		"\2\2\u03b4\u03b2\3\2\2\2\u03b4\u03b5\3\2\2\2\u03b5\u03d3\3\2\2\2\u03b6"+
		"\u03b4\3\2\2\2\u03b7\u03b9\7\3\2\2\u03b8\u03b7\3\2\2\2\u03b9\u03bc\3\2"+
		"\2\2\u03ba\u03b8\3\2\2\2\u03ba\u03bb\3\2\2\2\u03bb\u03be\3\2\2\2\u03bc"+
		"\u03ba\3\2\2\2\u03bd\u03bf\5\64\33\2\u03be\u03bd\3\2\2\2\u03be\u03bf\3"+
		"\2\2\2\u03bf\u03c1\3\2\2\2\u03c0\u03c2\5\66\34\2\u03c1\u03c0\3\2\2\2\u03c1"+
		"\u03c2\3\2\2\2\u03c2\u03c3\3\2\2\2\u03c3\u03c5\58\35\2\u03c4\u03c6\5>"+
		" \2\u03c5\u03c4\3\2\2\2\u03c5\u03c6\3\2\2\2\u03c6\u03c8\3\2\2\2\u03c7"+
		"\u03c9\7\7\2\2\u03c8\u03c7\3\2\2\2\u03c9\u03ca\3\2\2\2\u03ca\u03c8\3\2"+
		"\2\2\u03ca\u03cb\3\2\2\2\u03cb\u03cf\3\2\2\2\u03cc\u03ce\t\2\2\2\u03cd"+
		"\u03cc\3\2\2\2\u03ce\u03d1\3\2\2\2\u03cf\u03cd\3\2\2\2\u03cf\u03d0\3\2"+
		"\2\2\u03d0\u03d3\3\2\2\2\u03d1\u03cf\3\2\2\2\u03d2\u0305\3\2\2\2\u03d2"+
		"\u0314\3\2\2\2\u03d2\u032d\3\2\2\2\u03d2\u034a\3\2\2\2\u03d2\u0360\3\2"+
		"\2\2\u03d2\u0380\3\2\2\2\u03d2\u039e\3\2\2\2\u03d2\u03ba\3\2\2\2\u03d3"+
		"\63\3\2\2\2\u03d4\u03d6\6\33\2\2\u03d5\u03d7\7!\2\2\u03d6\u03d5\3\2\2"+
		"\2\u03d6\u03d7\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8\u03d9\7L\2\2\u03d9\65"+
		"\3\2\2\2\u03da\u03db\6\34\3\2\u03db\u03dc\7K\2\2\u03dc\67\3\2\2\2\u03dd"+
		"\u03de\6\35\4\2\u03de\u03df\7L\2\2\u03df9\3\2\2\2\u03e0\u03e2\5<\37\2"+
		"\u03e1\u03e0\3\2\2\2\u03e1\u03e2\3\2\2\2\u03e2\u03e3\3\2\2\2\u03e3\u03e4"+
		"\5B\"\2\u03e4;\3\2\2\2\u03e5\u03e7\t\6\2\2\u03e6\u03e8\7#\2\2\u03e7\u03e6"+
		"\3\2\2\2\u03e7\u03e8\3\2\2\2\u03e8=\3\2\2\2\u03e9\u03ea\6 \5\2\u03ea\u03eb"+
		"\7L\2\2\u03eb?\3\2\2\2\u03ec\u03f1\5B\"\2\u03ed\u03ee\7,\2\2\u03ee\u03ef"+
		"\5B\"\2\u03ef\u03f0\7-\2\2\u03f0\u03f2\3\2\2\2\u03f1\u03ed\3\2\2\2\u03f1"+
		"\u03f2\3\2\2\2\u03f2A\3\2\2\2\u03f3\u03f4\b\"\1\2\u03f4\u03f5\t\7\2\2"+
		"\u03f5\u0404\5B\"\21\u03f6\u03f7\7E\2\2\u03f7\u03f8\7.\2\2\u03f8\u03f9"+
		"\5B\"\2\u03f9\u03fa\7/\2\2\u03fa\u0404\3\2\2\2\u03fb\u03fc\7.\2\2\u03fc"+
		"\u03fd\5B\"\2\u03fd\u03fe\7/\2\2\u03fe\u0404\3\2\2\2\u03ff\u0401\7\34"+
		"\2\2\u0400\u03ff\3\2\2\2\u0400\u0401\3\2\2\2\u0401\u0402\3\2\2\2\u0402"+
		"\u0404\5D#\2\u0403\u03f3\3\2\2\2\u0403\u03f6\3\2\2\2\u0403\u03fb\3\2\2"+
		"\2\u0403\u0400\3\2\2\2\u0404\u042b\3\2\2\2\u0405\u0406\f\20\2\2\u0406"+
		"\u0407\t\b\2\2\u0407\u042a\5B\"\21\u0408\u0409\f\17\2\2\u0409\u040a\7"+
		"\31\2\2\u040a\u042a\5B\"\20\u040b\u040c\f\16\2\2\u040c\u040d\7\33\2\2"+
		"\u040d\u042a\5B\"\17\u040e\u040f\f\r\2\2\u040f\u0410\7\32\2\2\u0410\u042a"+
		"\5B\"\16\u0411\u0412\f\f\2\2\u0412\u0413\t\t\2\2\u0413\u042a\5B\"\r\u0414"+
		"\u0415\f\13\2\2\u0415\u0416\t\n\2\2\u0416\u042a\5B\"\f\u0417\u0418\f\n"+
		"\2\2\u0418\u0419\t\13\2\2\u0419\u042a\5B\"\13\u041a\u041b\f\t\2\2\u041b"+
		"\u041c\7\16\2\2\u041c\u042a\5B\"\n\u041d\u041e\f\b\2\2\u041e\u041f\7\20"+
		"\2\2\u041f\u042a\5B\"\t\u0420\u0421\f\7\2\2\u0421\u0422\7\17\2\2\u0422"+
		"\u042a\5B\"\b\u0423\u0424\f\6\2\2\u0424\u0425\7(\2\2\u0425\u0426\5B\""+
		"\2\u0426\u0427\7)\2\2\u0427\u0428\5B\"\7\u0428\u042a\3\2\2\2\u0429\u0405"+
		"\3\2\2\2\u0429\u0408\3\2\2\2\u0429\u040b\3\2\2\2\u0429\u040e\3\2\2\2\u0429"+
		"\u0411\3\2\2\2\u0429\u0414\3\2\2\2\u0429\u0417\3\2\2\2\u0429\u041a\3\2"+
		"\2\2\u0429\u041d\3\2\2\2\u0429\u0420\3\2\2\2\u0429\u0423\3\2\2\2\u042a"+
		"\u042d\3\2\2\2\u042b\u0429\3\2\2\2\u042b\u042c\3\2\2\2\u042cC\3\2\2\2"+
		"\u042d\u042b\3\2\2\2\u042e\u0458\7\r\2\2\u042f\u0458\7\f\2\2\u0430\u0458"+
		"\7\13\2\2\u0431\u0458\7\n\2\2\u0432\u0458\7\t\2\2\u0433\u0458\7\36\2\2"+
		"\u0434\u0439\7L\2\2\u0435\u0436\7,\2\2\u0436\u0437\5B\"\2\u0437\u0438"+
		"\7-\2\2\u0438\u043a\3\2\2\2\u0439\u0435\3\2\2\2\u0439\u043a\3\2\2\2\u043a"+
		"\u0458\3\2\2\2\u043b\u043c\7L\2\2\u043c\u0441\t\f\2\2\u043d\u043e\7,\2"+
		"\2\u043e\u043f\5B\"\2\u043f\u0440\7-\2\2\u0440\u0442\3\2\2\2\u0441\u043d"+
		"\3\2\2\2\u0441\u0442\3\2\2\2\u0442\u0458\3\2\2\2\u0443\u0448\7L\2\2\u0444"+
		"\u0445\7,\2\2\u0445\u0446\5B\"\2\u0446\u0447\7-\2\2\u0447\u0449\3\2\2"+
		"\2\u0448\u0444\3\2\2\2\u0448\u0449\3\2\2\2\u0449\u044a\3\2\2\2\u044a\u0458"+
		"\t\f\2\2\u044b\u044c\t\f\2\2\u044c\u0451\7L\2\2\u044d\u044e\7,\2\2\u044e"+
		"\u044f\5B\"\2\u044f\u0450\7-\2\2\u0450\u0452\3\2\2\2\u0451\u044d\3\2\2"+
		"\2\u0451\u0452\3\2\2\2\u0452\u0458\3\2\2\2\u0453\u0455\7!\2\2\u0454\u0453"+
		"\3\2\2\2\u0454\u0455\3\2\2\2\u0455\u0456\3\2\2\2\u0456\u0458\7L\2\2\u0457"+
		"\u042e\3\2\2\2\u0457\u042f\3\2\2\2\u0457\u0430\3\2\2\2\u0457\u0431\3\2"+
		"\2\2\u0457\u0432\3\2\2\2\u0457\u0433\3\2\2\2\u0457\u0434\3\2\2\2\u0457"+
		"\u043b\3\2\2\2\u0457\u0443\3\2\2\2\u0457\u044b\3\2\2\2\u0457\u0454\3\2"+
		"\2\2\u0458E\3\2\2\2\u00b5IQS[`hqtv|\u0085\u008b\u0094\u0099\u00a0\u00a6"+
		"\u00ae\u00b3\u00b9\u00be\u00c4\u00ca\u00d4\u00dc\u00e1\u00e6\u00ee\u00f3"+
		"\u00f9\u00fd\u0104\u0109\u010f\u0114\u0118\u011d\u0123\u012d\u0135\u013d"+
		"\u0141\u0144\u014b\u0151\u0157\u015c\u0163\u0169\u016c\u0177\u0179\u017c"+
		"\u0184\u018a\u0191\u0196\u019c\u01a2\u01a9\u01ae\u01b4\u01b9\u01be\u01c0"+
		"\u01c2\u01c7\u01cd\u01d6\u01db\u01e1\u01ea\u01ef\u01f5\u01fd\u0202\u0208"+
		"\u020c\u0211\u0216\u021c\u0223\u0229\u022e\u0234\u023b\u023e\u0243\u0248"+
		"\u024e\u0255\u025a\u0260\u0267\u026c\u0272\u0276\u027b\u0280\u0286\u028c"+
		"\u028e\u0294\u0299\u029e\u02a4\u02a8\u02ad\u02b3\u02b7\u02be\u02c4\u02c8"+
		"\u02ca\u02d0\u02d5\u02db\u02df\u02e6\u02ec\u02f0\u02f2\u02f7\u02fc\u0302"+
		"\u0309\u030e\u0314\u031b\u031d\u0322\u0327\u032d\u0331\u0339\u033f\u0344"+
		"\u034a\u034e\u0355\u035a\u0360\u0364\u0367\u0370\u0375\u037a\u0380\u0384"+
		"\u0387\u038e\u0393\u0398\u039e\u03a2\u03a5\u03aa\u03af\u03b4\u03ba\u03be"+
		"\u03c1\u03c5\u03ca\u03cf\u03d2\u03d6\u03e1\u03e7\u03f1\u0400\u0403\u0429"+
		"\u042b\u0439\u0441\u0448\u0451\u0454\u0457";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}