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
		ASSIGN=20, ELLIPSIS=21, EQUALS=22, NOT_EQUALS=23, BIN_AND=24, BIN_OR=25, 
		BIN_XOR=26, AT=27, POUND=28, DOLLAR=29, PERCENT=30, EQUAL=31, DOT=32, 
		COMMA=33, BACKSLASH=34, PLUS=35, MINUS=36, STAR=37, DIV=38, QUESTION=39, 
		COLON=40, TILDE=41, UNDERSCORE=42, OPEN_BRACKET=43, CLOSE_BRACKET=44, 
		OPEN_PAREN=45, CLOSE_PAREN=46, CON_START=47, VAR_START=48, OBJ_START=49, 
		PUB_START=50, PRI_START=51, DAT_START=52, REPEAT=53, FROM=54, TO=55, STEP=56, 
		WHILE=57, UNTIL=58, ELSEIFNOT=59, ELSEIF=60, ELSE=61, IFNOT=62, IF=63, 
		CASE=64, OTHER=65, ADDPINS=66, ADDBITS=67, FRAC=68, ENCOD=69, DECOD=70, 
		FUNCTIONS=71, AND=72, NOT=73, XOR=74, OR=75, ORG=76, ORGH=77, RES=78, 
		ALIGN=79, TYPE=80, CONDITION=81, MODIFIER=82, IDENTIFIER=83;
	public static final int
		RULE_prog = 0, RULE_constantsSection = 1, RULE_constantAssign = 2, RULE_constantEnum = 3, 
		RULE_constantEnumName = 4, RULE_objectsSection = 5, RULE_object = 6, RULE_variablesSection = 7, 
		RULE_variable = 8, RULE_method = 9, RULE_parameters = 10, RULE_result = 11, 
		RULE_localvars = 12, RULE_localvar = 13, RULE_statement = 14, RULE_function = 15, 
		RULE_functionArgument = 16, RULE_identifier = 17, RULE_repeatLoop = 18, 
		RULE_conditional = 19, RULE_elseConditional = 20, RULE_caseConditional = 21, 
		RULE_caseConditionalMatch = 22, RULE_caseConditionalOther = 23, RULE_spinExpression = 24, 
		RULE_experssionAtom = 25, RULE_data = 26, RULE_dataLine = 27, RULE_label = 28, 
		RULE_condition = 29, RULE_opcode = 30, RULE_argument = 31, RULE_prefix = 32, 
		RULE_effect = 33, RULE_dataValue = 34, RULE_expression = 35, RULE_atom = 36;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objectsSection", "object", "variablesSection", "variable", "method", 
			"parameters", "result", "localvars", "localvar", "statement", "function", 
			"functionArgument", "identifier", "repeatLoop", "conditional", "elseConditional", 
			"caseConditional", "caseConditionalMatch", "caseConditionalOther", "spinExpression", 
			"experssionAtom", "data", "dataLine", "label", "condition", "opcode", 
			"argument", "prefix", "effect", "dataValue", "expression", "atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'&&'", "'||'", "'^^'", "'##'", "'<<'", "'>>'", "'++'", "'--'", null, 
			"'..'", "'=='", "'<>'", "'&'", "'|'", "'^'", "'@'", "'#'", "'$'", "'%'", 
			"'='", "'.'", "','", "'\\'", "'+'", "'-'", "'*'", "'/'", "'?'", "':'", 
			"'~'", "'_'", "'['", "']'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", 
			"POUND_POUND", "LEFT_SHIFT", "RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", 
			"ASSIGN", "ELLIPSIS", "EQUALS", "NOT_EQUALS", "BIN_AND", "BIN_OR", "BIN_XOR", 
			"AT", "POUND", "DOLLAR", "PERCENT", "EQUAL", "DOT", "COMMA", "BACKSLASH", 
			"PLUS", "MINUS", "STAR", "DIV", "QUESTION", "COLON", "TILDE", "UNDERSCORE", 
			"OPEN_BRACKET", "CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "CON_START", 
			"VAR_START", "OBJ_START", "PUB_START", "PRI_START", "DAT_START", "REPEAT", 
			"FROM", "TO", "STEP", "WHILE", "UNTIL", "ELSEIFNOT", "ELSEIF", "ELSE", 
			"IFNOT", "IF", "CASE", "OTHER", "ADDPINS", "ADDBITS", "FRAC", "ENCOD", 
			"DECOD", "FUNCTIONS", "AND", "NOT", "XOR", "OR", "ORG", "ORGH", "RES", 
			"ALIGN", "TYPE", "CONDITION", "MODIFIER", "IDENTIFIER"
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
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(74);
				match(NL);
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(85);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(80);
					constantsSection();
					}
					break;
				case OBJ_START:
					{
					setState(81);
					objectsSection();
					}
					break;
				case VAR_START:
					{
					setState(82);
					variablesSection();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(83);
					method();
					}
					break;
				case DAT_START:
					{
					setState(84);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(90);
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
			setState(93); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(92);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(95); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(97);
				match(NL);
				}
				}
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==POUND || _la==IDENTIFIER) {
				{
				setState(120);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					{
					setState(103);
					constantAssign();
					setState(108);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(104);
						match(COMMA);
						setState(105);
						constantAssign();
						}
						}
						setState(110);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case 2:
					{
					setState(111);
					constantEnum();
					}
					break;
				case 3:
					{
					{
					setState(112);
					constantEnumName();
					setState(117);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(113);
						match(COMMA);
						setState(114);
						constantEnumName();
						}
						}
						setState(119);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				}
				}
				setState(124);
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
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(125);
				match(INDENT);
				}
				}
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(131);
			((ConstantAssignContext)_localctx).name = match(IDENTIFIER);
			setState(132);
			match(EQUAL);
			setState(133);
			((ConstantAssignContext)_localctx).exp = expression(0);
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(134);
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
				setState(139);
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
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(140);
				match(INDENT);
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(146);
			match(POUND);
			setState(147);
			((ConstantEnumContext)_localctx).start = expression(0);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(148);
				match(OPEN_BRACKET);
				setState(149);
				((ConstantEnumContext)_localctx).step = expression(0);
				setState(150);
				match(CLOSE_BRACKET);
				}
			}

			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(154);
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
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(160);
				match(COMMA);
				setState(161);
				constantEnumName();
				}
				}
				setState(166);
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
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(167);
				match(INDENT);
				}
				}
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(173);
			((ConstantEnumNameContext)_localctx).name = match(IDENTIFIER);
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(174);
				match(OPEN_BRACKET);
				setState(175);
				((ConstantEnumNameContext)_localctx).multiplier = expression(0);
				setState(176);
				match(CLOSE_BRACKET);
				}
			}

			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(180);
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
				setState(185);
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
			setState(187); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(186);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(189); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(191);
				match(NL);
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==IDENTIFIER) {
				{
				{
				setState(197);
				object();
				}
				}
				setState(202);
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
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(203);
				match(INDENT);
				}
				}
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(209);
			((ObjectContext)_localctx).name = match(IDENTIFIER);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(210);
				match(OPEN_BRACKET);
				setState(211);
				((ObjectContext)_localctx).count = expression(0);
				setState(212);
				match(CLOSE_BRACKET);
				}
				}
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(219);
			match(COLON);
			setState(220);
			((ObjectContext)_localctx).filename = match(STRING);
			setState(222); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(221);
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
				setState(224); 
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
			setState(227); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(226);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(229); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(234);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(231);
				match(NL);
				}
				}
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==TYPE || _la==IDENTIFIER) {
				{
				{
				setState(237);
				variable();
				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(238);
					match(COMMA);
					setState(239);
					variable();
					}
					}
					setState(244);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(249);
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
			setState(253);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(250);
				match(INDENT);
				}
				}
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(256);
				((VariableContext)_localctx).type = match(TYPE);
				}
			}

			setState(259);
			((VariableContext)_localctx).name = match(IDENTIFIER);
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(260);
				match(OPEN_BRACKET);
				setState(261);
				((VariableContext)_localctx).size = expression(0);
				setState(262);
				match(CLOSE_BRACKET);
				}
			}

			setState(267); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(266);
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
				setState(269); 
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
			setState(271);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(272);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(273);
			match(OPEN_PAREN);
			setState(275);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(274);
				parameters();
				}
			}

			setState(277);
			match(CLOSE_PAREN);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(278);
				match(COLON);
				setState(279);
				result();
				}
			}

			setState(284);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(282);
				match(BIN_OR);
				setState(283);
				localvars();
				}
			}

			setState(287); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(286);
				match(NL);
				}
				}
				setState(289); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(291);
			match(INDENT);
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(292);
				statement();
				}
				}
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(298);
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
			setState(300);
			match(IDENTIFIER);
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(301);
				match(COMMA);
				setState(302);
				match(IDENTIFIER);
				}
				}
				setState(307);
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
			setState(308);
			match(IDENTIFIER);
			setState(313);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(309);
				match(COMMA);
				setState(310);
				match(IDENTIFIER);
				}
				}
				setState(315);
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
			setState(316);
			localvar();
			setState(321);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(317);
				match(COMMA);
				setState(318);
				localvar();
				}
				}
				setState(323);
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
			setState(325);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(324);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(327);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(330);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(335);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(331);
				match(OPEN_BRACKET);
				setState(332);
				((LocalvarContext)_localctx).count = expression(0);
				setState(333);
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
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
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
			case BACKSLASH:
			case PLUS:
			case MINUS:
			case TILDE:
			case OPEN_PAREN:
			case ENCOD:
			case DECOD:
			case FUNCTIONS:
			case TYPE:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(337);
				spinExpression(0);
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
			case REPEAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(343);
				repeatLoop();
				}
				break;
			case IFNOT:
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(344);
				conditional();
				}
				break;
			case CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(345);
				caseConditional();
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

	public static class FunctionContext extends ParserRuleContext {
		public Token name;
		public Token obj;
		public SpinExpressionContext index;
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode BACKSLASH() { return getToken(Spin2Parser.BACKSLASH, 0); }
		public FunctionArgumentContext functionArgument() {
			return getRuleContext(FunctionArgumentContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Spin2Parser.DOT, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
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
		enterRule(_localctx, 30, RULE_function);
		int _la;
		try {
			setState(383);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(348);
					match(BACKSLASH);
					}
				}

				setState(351);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(352);
				match(OPEN_PAREN);
				setState(354);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
					{
					setState(353);
					functionArgument();
					}
				}

				setState(356);
				match(CLOSE_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(358);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(357);
					match(BACKSLASH);
					}
				}

				setState(360);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(361);
				match(DOT);
				setState(362);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(363);
				match(OPEN_PAREN);
				setState(365);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
					{
					setState(364);
					functionArgument();
					}
				}

				setState(367);
				match(CLOSE_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(369);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(368);
					match(BACKSLASH);
					}
				}

				setState(371);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(372);
				match(OPEN_BRACKET);
				setState(373);
				((FunctionContext)_localctx).index = spinExpression(0);
				setState(374);
				match(CLOSE_BRACKET);
				setState(375);
				match(DOT);
				setState(376);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(377);
				match(OPEN_PAREN);
				setState(379);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
					{
					setState(378);
					functionArgument();
					}
				}

				setState(381);
				match(CLOSE_PAREN);
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

	public static class FunctionArgumentContext extends ParserRuleContext {
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public FunctionArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterFunctionArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitFunctionArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitFunctionArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgumentContext functionArgument() throws RecognitionException {
		FunctionArgumentContext _localctx = new FunctionArgumentContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_functionArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			spinExpression(0);
			setState(390);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(386);
				match(COMMA);
				setState(387);
				spinExpression(0);
				}
				}
				setState(392);
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

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(Spin2Parser.AT, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode DOT() { return getToken(Spin2Parser.DOT, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_identifier);
		try {
			setState(418);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				match(AT);
				setState(394);
				match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(395);
				match(IDENTIFIER);
				setState(400);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(396);
					match(OPEN_BRACKET);
					setState(397);
					spinExpression(0);
					setState(398);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(402);
				match(IDENTIFIER);
				setState(403);
				match(DOT);
				setState(404);
				match(IDENTIFIER);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(405);
				match(IDENTIFIER);
				setState(406);
				match(DOT);
				setState(407);
				match(OPEN_BRACKET);
				setState(408);
				spinExpression(0);
				setState(409);
				match(CLOSE_BRACKET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(411);
				match(IDENTIFIER);
				setState(412);
				match(DOT);
				setState(413);
				match(IDENTIFIER);
				setState(414);
				match(OPEN_BRACKET);
				setState(415);
				spinExpression(0);
				setState(416);
				match(CLOSE_BRACKET);
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

	public static class RepeatLoopContext extends ParserRuleContext {
		public TerminalNode REPEAT() { return getToken(Spin2Parser.REPEAT, 0); }
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
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
		public TerminalNode WHILE() { return getToken(Spin2Parser.WHILE, 0); }
		public TerminalNode UNTIL() { return getToken(Spin2Parser.UNTIL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
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
		enterRule(_localctx, 36, RULE_repeatLoop);
		int _la;
		try {
			setState(541);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(420);
				match(REPEAT);
				setState(422);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
					{
					setState(421);
					spinExpression(0);
					}
				}

				setState(425); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(424);
					match(NL);
					}
					}
					setState(427); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(429);
				match(INDENT);
				setState(433);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(430);
					statement();
					}
					}
					setState(435);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(436);
				match(DEDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(437);
				match(REPEAT);
				setState(439); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(438);
					match(NL);
					}
					}
					setState(441); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(443);
				match(INDENT);
				setState(447);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(444);
					statement();
					}
					}
					setState(449);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(450);
				match(DEDENT);
				setState(451);
				match(WHILE);
				setState(452);
				spinExpression(0);
				setState(454); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(453);
					match(NL);
					}
					}
					setState(456); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(458);
				match(REPEAT);
				setState(460); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(459);
					match(NL);
					}
					}
					setState(462); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(464);
				match(INDENT);
				setState(468);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(465);
					statement();
					}
					}
					setState(470);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(471);
				match(DEDENT);
				setState(472);
				match(UNTIL);
				setState(473);
				spinExpression(0);
				setState(475); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(474);
					match(NL);
					}
					}
					setState(477); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(479);
				match(REPEAT);
				setState(480);
				match(IDENTIFIER);
				setState(481);
				match(FROM);
				setState(482);
				spinExpression(0);
				setState(489);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(483);
					match(TO);
					setState(484);
					spinExpression(0);
					setState(487);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(485);
						match(STEP);
						setState(486);
						spinExpression(0);
						}
					}

					}
				}

				setState(492); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(491);
					match(NL);
					}
					}
					setState(494); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(496);
				match(INDENT);
				setState(500);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(497);
					statement();
					}
					}
					setState(502);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(503);
				match(DEDENT);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(505);
				match(REPEAT);
				setState(506);
				match(WHILE);
				setState(507);
				spinExpression(0);
				setState(509); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(508);
					match(NL);
					}
					}
					setState(511); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(521);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(513);
					match(INDENT);
					setState(517);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(514);
						statement();
						}
						}
						setState(519);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(520);
					match(DEDENT);
					}
				}

				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(523);
				match(REPEAT);
				setState(524);
				match(UNTIL);
				setState(525);
				spinExpression(0);
				setState(527); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(526);
					match(NL);
					}
					}
					setState(529); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(539);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(531);
					match(INDENT);
					setState(535);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(532);
						statement();
						}
						}
						setState(537);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(538);
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

	public static class ConditionalContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(Spin2Parser.IF, 0); }
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		enterRule(_localctx, 38, RULE_conditional);
		int _la;
		try {
			setState(585);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(543);
				match(IF);
				setState(544);
				spinExpression(0);
				setState(546); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(545);
					match(NL);
					}
					}
					setState(548); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(550);
				match(INDENT);
				setState(554);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(551);
					statement();
					}
					}
					setState(556);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(557);
				match(DEDENT);
				setState(561);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(558);
					elseConditional();
					}
					}
					setState(563);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case IFNOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(564);
				match(IFNOT);
				setState(565);
				spinExpression(0);
				setState(567); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(566);
					match(NL);
					}
					}
					setState(569); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(571);
				match(INDENT);
				setState(575);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(572);
					statement();
					}
					}
					setState(577);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(578);
				match(DEDENT);
				setState(582);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(579);
					elseConditional();
					}
					}
					setState(584);
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
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
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
		enterRule(_localctx, 40, RULE_elseConditional);
		int _la;
		try {
			setState(633);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ELSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(587);
				match(ELSE);
				setState(589); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(588);
					match(NL);
					}
					}
					setState(591); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(593);
				match(INDENT);
				setState(597);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(594);
					statement();
					}
					}
					setState(599);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(600);
				match(DEDENT);
				}
				break;
			case ELSEIF:
				enterOuterAlt(_localctx, 2);
				{
				setState(601);
				match(ELSEIF);
				setState(602);
				spinExpression(0);
				setState(604); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(603);
					match(NL);
					}
					}
					setState(606); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(608);
				match(INDENT);
				setState(612);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(609);
					statement();
					}
					}
					setState(614);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(615);
				match(DEDENT);
				}
				break;
			case ELSEIFNOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(617);
				match(ELSEIFNOT);
				setState(618);
				spinExpression(0);
				setState(620); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(619);
					match(NL);
					}
					}
					setState(622); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(624);
				match(INDENT);
				setState(628);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(625);
					statement();
					}
					}
					setState(630);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(631);
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
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
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
		enterRule(_localctx, 42, RULE_caseConditional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(635);
			match(CASE);
			setState(636);
			spinExpression(0);
			setState(638); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(637);
				match(NL);
				}
				}
				setState(640); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(642);
			match(INDENT);
			setState(647);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (OTHER - 65)) | (1L << (ENCOD - 65)) | (1L << (DECOD - 65)) | (1L << (FUNCTIONS - 65)) | (1L << (TYPE - 65)) | (1L << (IDENTIFIER - 65)))) != 0)) {
				{
				setState(645);
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
				case BACKSLASH:
				case PLUS:
				case MINUS:
				case TILDE:
				case OPEN_PAREN:
				case ENCOD:
				case DECOD:
				case FUNCTIONS:
				case TYPE:
				case IDENTIFIER:
					{
					setState(643);
					caseConditionalMatch();
					}
					break;
				case OTHER:
					{
					setState(644);
					caseConditionalOther();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(650);
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

	public static class CaseConditionalMatchContext extends ParserRuleContext {
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
		}
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public TerminalNode ELLIPSIS() { return getToken(Spin2Parser.ELLIPSIS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
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
		enterRule(_localctx, 44, RULE_caseConditionalMatch);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(652);
			spinExpression(0);
			setState(655);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(653);
				match(ELLIPSIS);
				setState(654);
				expression(0);
				}
			}

			setState(657);
			match(COLON);
			setState(659);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
				{
				setState(658);
				spinExpression(0);
				}
			}

			setState(662); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(661);
				match(NL);
				}
				}
				setState(664); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(679);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INDENT) {
				{
				setState(666);
				match(INDENT);
				setState(675);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (FUNCTIONS - 69)) | (1L << (TYPE - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) {
					{
					{
					setState(667);
					spinExpression(0);
					setState(669); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(668);
						match(NL);
						}
						}
						setState(671); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==NL );
					}
					}
					setState(677);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(678);
				match(DEDENT);
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

	public static class CaseConditionalOtherContext extends ParserRuleContext {
		public TerminalNode OTHER() { return getToken(Spin2Parser.OTHER, 0); }
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
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
		enterRule(_localctx, 46, RULE_caseConditionalOther);
		int _la;
		try {
			setState(716);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(681);
				match(OTHER);
				setState(682);
				match(COLON);
				setState(683);
				spinExpression(0);
				setState(685); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(684);
					match(NL);
					}
					}
					setState(687); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(697);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(689);
					match(INDENT);
					setState(693);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(690);
						statement();
						}
						}
						setState(695);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(696);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(699);
				match(OTHER);
				setState(700);
				match(COLON);
				setState(702); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(701);
					match(NL);
					}
					}
					setState(704); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(714);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(706);
					match(INDENT);
					setState(710);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT) | (1L << IFNOT) | (1L << IF))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(707);
						statement();
						}
						}
						setState(712);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(713);
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

	public static class SpinExpressionContext extends ParserRuleContext {
		public SpinExpressionContext left;
		public Token operator;
		public SpinExpressionContext exp;
		public SpinExpressionContext right;
		public SpinExpressionContext middle;
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(Spin2Parser.ASSIGN, 0); }
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode PLUS() { return getToken(Spin2Parser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(Spin2Parser.MINUS, 0); }
		public TerminalNode TILDE() { return getToken(Spin2Parser.TILDE, 0); }
		public TerminalNode ENCOD() { return getToken(Spin2Parser.ENCOD, 0); }
		public TerminalNode DECOD() { return getToken(Spin2Parser.DECOD, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(Spin2Parser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(Spin2Parser.CLOSE_PAREN, 0); }
		public TerminalNode FUNCTIONS() { return getToken(Spin2Parser.FUNCTIONS, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode PLUS_PLUS() { return getToken(Spin2Parser.PLUS_PLUS, 0); }
		public TerminalNode MINUS_MINUS() { return getToken(Spin2Parser.MINUS_MINUS, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode LEFT_SHIFT() { return getToken(Spin2Parser.LEFT_SHIFT, 0); }
		public TerminalNode RIGHT_SHIFT() { return getToken(Spin2Parser.RIGHT_SHIFT, 0); }
		public TerminalNode BIN_AND() { return getToken(Spin2Parser.BIN_AND, 0); }
		public TerminalNode BIN_XOR() { return getToken(Spin2Parser.BIN_XOR, 0); }
		public TerminalNode BIN_OR() { return getToken(Spin2Parser.BIN_OR, 0); }
		public TerminalNode STAR() { return getToken(Spin2Parser.STAR, 0); }
		public TerminalNode DIV() { return getToken(Spin2Parser.DIV, 0); }
		public TerminalNode FRAC() { return getToken(Spin2Parser.FRAC, 0); }
		public TerminalNode ADDPINS() { return getToken(Spin2Parser.ADDPINS, 0); }
		public TerminalNode ADDBITS() { return getToken(Spin2Parser.ADDBITS, 0); }
		public TerminalNode EQUALS() { return getToken(Spin2Parser.EQUALS, 0); }
		public TerminalNode NOT_EQUALS() { return getToken(Spin2Parser.NOT_EQUALS, 0); }
		public TerminalNode AND() { return getToken(Spin2Parser.AND, 0); }
		public TerminalNode LOGICAL_AND() { return getToken(Spin2Parser.LOGICAL_AND, 0); }
		public TerminalNode XOR() { return getToken(Spin2Parser.XOR, 0); }
		public TerminalNode LOGICAL_XOR() { return getToken(Spin2Parser.LOGICAL_XOR, 0); }
		public TerminalNode OR() { return getToken(Spin2Parser.OR, 0); }
		public TerminalNode LOGICAL_OR() { return getToken(Spin2Parser.LOGICAL_OR, 0); }
		public TerminalNode QUESTION() { return getToken(Spin2Parser.QUESTION, 0); }
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public SpinExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spinExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterSpinExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitSpinExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitSpinExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpinExpressionContext spinExpression() throws RecognitionException {
		return spinExpression(0);
	}

	private SpinExpressionContext spinExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SpinExpressionContext _localctx = new SpinExpressionContext(_ctx, _parentState);
		SpinExpressionContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_spinExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(756);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,106,_ctx) ) {
			case 1:
				{
				setState(719);
				identifier();
				setState(724);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(720);
					match(COMMA);
					setState(721);
					match(IDENTIFIER);
					}
					}
					setState(726);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(727);
				match(ASSIGN);
				setState(728);
				spinExpression(23);
				}
				break;
			case 2:
				{
				setState(730);
				match(TYPE);
				setState(731);
				match(OPEN_BRACKET);
				setState(732);
				spinExpression(0);
				setState(733);
				match(CLOSE_BRACKET);
				setState(734);
				match(ASSIGN);
				setState(735);
				spinExpression(22);
				}
				break;
			case 3:
				{
				setState(737);
				((SpinExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (PLUS - 35)) | (1L << (MINUS - 35)) | (1L << (TILDE - 35)) | (1L << (ENCOD - 35)) | (1L << (DECOD - 35)))) != 0)) ) {
					((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(738);
				((SpinExpressionContext)_localctx).exp = spinExpression(21);
				}
				break;
			case 4:
				{
				setState(739);
				((SpinExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(740);
				match(OPEN_PAREN);
				setState(741);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(742);
				match(CLOSE_PAREN);
				}
				break;
			case 5:
				{
				setState(744);
				match(OPEN_PAREN);
				setState(745);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(746);
				match(CLOSE_PAREN);
				}
				break;
			case 6:
				{
				setState(748);
				atom();
				}
				break;
			case 7:
				{
				setState(749);
				identifier();
				}
				break;
			case 8:
				{
				setState(750);
				identifier();
				setState(751);
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
			case 9:
				{
				setState(753);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(754);
				identifier();
				}
				break;
			case 10:
				{
				setState(755);
				function();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(802);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,108,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(800);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
					case 1:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(758);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(759);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LEFT_SHIFT || _la==RIGHT_SHIFT) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(760);
						((SpinExpressionContext)_localctx).right = spinExpression(21);
						}
						break;
					case 2:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(761);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(762);
						((SpinExpressionContext)_localctx).operator = match(BIN_AND);
						setState(763);
						((SpinExpressionContext)_localctx).right = spinExpression(20);
						}
						break;
					case 3:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(764);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(765);
						((SpinExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(766);
						((SpinExpressionContext)_localctx).right = spinExpression(19);
						}
						break;
					case 4:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(767);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(768);
						((SpinExpressionContext)_localctx).operator = match(BIN_OR);
						setState(769);
						((SpinExpressionContext)_localctx).right = spinExpression(18);
						}
						break;
					case 5:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(770);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(771);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==STAR || _la==DIV) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(772);
						((SpinExpressionContext)_localctx).right = spinExpression(17);
						}
						break;
					case 6:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(773);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(774);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(775);
						((SpinExpressionContext)_localctx).right = spinExpression(16);
						}
						break;
					case 7:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(776);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(777);
						((SpinExpressionContext)_localctx).operator = match(FRAC);
						setState(778);
						((SpinExpressionContext)_localctx).right = spinExpression(15);
						}
						break;
					case 8:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(779);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(780);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADDPINS || _la==ADDBITS) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(781);
						((SpinExpressionContext)_localctx).right = spinExpression(14);
						}
						break;
					case 9:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(782);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(783);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUALS || _la==NOT_EQUALS) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(784);
						((SpinExpressionContext)_localctx).right = spinExpression(13);
						}
						break;
					case 10:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(785);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(786);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_AND || _la==AND) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(787);
						((SpinExpressionContext)_localctx).right = spinExpression(12);
						}
						break;
					case 11:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(788);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(789);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_XOR || _la==XOR) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(790);
						((SpinExpressionContext)_localctx).right = spinExpression(11);
						}
						break;
					case 12:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(791);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(792);
						((SpinExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_OR || _la==OR) ) {
							((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(793);
						((SpinExpressionContext)_localctx).right = spinExpression(10);
						}
						break;
					case 13:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(794);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(795);
						((SpinExpressionContext)_localctx).operator = match(QUESTION);
						setState(796);
						((SpinExpressionContext)_localctx).middle = spinExpression(0);
						setState(797);
						((SpinExpressionContext)_localctx).operator = match(COLON);
						setState(798);
						((SpinExpressionContext)_localctx).right = spinExpression(9);
						}
						break;
					}
					} 
				}
				setState(804);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,108,_ctx);
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

	public static class ExperssionAtomContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(Spin2Parser.NUMBER, 0); }
		public TerminalNode HEX() { return getToken(Spin2Parser.HEX, 0); }
		public TerminalNode BIN() { return getToken(Spin2Parser.BIN, 0); }
		public TerminalNode QUAD() { return getToken(Spin2Parser.QUAD, 0); }
		public TerminalNode STRING() { return getToken(Spin2Parser.STRING, 0); }
		public ExperssionAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_experssionAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterExperssionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitExperssionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitExperssionAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExperssionAtomContext experssionAtom() throws RecognitionException {
		ExperssionAtomContext _localctx = new ExperssionAtomContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_experssionAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(805);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER))) != 0)) ) {
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
		enterRule(_localctx, 52, RULE_data);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(808); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(807);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(810); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(815);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,110,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(812);
					match(NL);
					}
					} 
				}
				setState(817);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,110,_ctx);
			}
			setState(821);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,111,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(818);
					dataLine();
					}
					} 
				}
				setState(823);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,111,_ctx);
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
		public TerminalNode ORG() { return getToken(Spin2Parser.ORG, 0); }
		public TerminalNode ORGH() { return getToken(Spin2Parser.ORGH, 0); }
		public TerminalNode ALIGN() { return getToken(Spin2Parser.ALIGN, 0); }
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
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
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
		enterRule(_localctx, 54, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(1023);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,151,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(824);
				label();
				setState(826); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(825);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(828); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,112,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(833);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(830);
					match(INDENT);
					}
					}
					setState(835);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(836);
				((DataLineContext)_localctx).directive = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & ((1L << (ORG - 76)) | (1L << (ORGH - 76)) | (1L << (ALIGN - 76)))) != 0)) ) {
					((DataLineContext)_localctx).directive = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(842);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(837);
					expression(0);
					setState(840);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(838);
						match(COMMA);
						setState(839);
						expression(0);
						}
					}

					}
				}

				setState(845); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(844);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(847); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(852);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(849);
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
					setState(854);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(858);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(855);
						match(INDENT);
						}
						} 
					}
					setState(860);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
				}
				setState(862);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
				case 1:
					{
					setState(861);
					label();
					}
					break;
				}
				setState(864);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(865);
				dataValue();
				setState(870);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(866);
					match(COMMA);
					setState(867);
					dataValue();
					}
					}
					setState(872);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(874); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(873);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(876); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(881);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(878);
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
					setState(883);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(887);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(884);
						match(INDENT);
						}
						} 
					}
					setState(889);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
				}
				setState(891);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
				case 1:
					{
					setState(890);
					label();
					}
					break;
				}
				setState(893);
				((DataLineContext)_localctx).directive = match(RES);
				setState(894);
				dataValue();
				setState(896); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(895);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(898); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(903);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(900);
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
					setState(905);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(909);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(906);
						match(INDENT);
						}
						} 
					}
					setState(911);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				}
				setState(913);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
				case 1:
					{
					setState(912);
					label();
					}
					break;
				}
				setState(916);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
				case 1:
					{
					setState(915);
					condition();
					}
					break;
				}
				setState(918);
				opcode();
				setState(919);
				argument();
				setState(920);
				match(COMMA);
				setState(921);
				argument();
				setState(922);
				match(COMMA);
				setState(923);
				argument();
				setState(925);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(924);
					effect();
					}
				}

				setState(928); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(927);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(930); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(935);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,132,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(932);
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
					setState(937);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,132,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(941);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(938);
						match(INDENT);
						}
						} 
					}
					setState(943);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				}
				setState(945);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
				case 1:
					{
					setState(944);
					label();
					}
					break;
				}
				setState(948);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
				case 1:
					{
					setState(947);
					condition();
					}
					break;
				}
				setState(950);
				opcode();
				setState(951);
				argument();
				setState(952);
				match(COMMA);
				setState(953);
				argument();
				setState(955);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(954);
					effect();
					}
				}

				setState(958); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(957);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(960); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,137,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(965);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(962);
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
					setState(967);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(971);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(968);
						match(INDENT);
						}
						} 
					}
					setState(973);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				}
				setState(975);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,140,_ctx) ) {
				case 1:
					{
					setState(974);
					label();
					}
					break;
				}
				setState(978);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
				case 1:
					{
					setState(977);
					condition();
					}
					break;
				}
				setState(980);
				opcode();
				setState(981);
				argument();
				setState(983);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(982);
					effect();
					}
				}

				setState(986); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(985);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(988); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,143,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(993);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,144,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(990);
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
					setState(995);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,144,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(999);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(996);
						match(INDENT);
						}
						} 
					}
					setState(1001);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
				}
				setState(1003);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
				case 1:
					{
					setState(1002);
					label();
					}
					break;
				}
				setState(1006);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
				case 1:
					{
					setState(1005);
					condition();
					}
					break;
				}
				setState(1008);
				opcode();
				setState(1010);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1009);
					effect();
					}
				}

				setState(1013); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1012);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1015); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1020);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,150,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1017);
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
					setState(1022);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,150,_ctx);
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
		enterRule(_localctx, 56, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1025);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			{
			setState(1027);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(1026);
				match(DOT);
				}
			}

			setState(1029);
			match(IDENTIFIER);
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
		enterRule(_localctx, 58, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1031);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1032);
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
		public TerminalNode OR() { return getToken(Spin2Parser.OR, 0); }
		public TerminalNode AND() { return getToken(Spin2Parser.AND, 0); }
		public TerminalNode NOT() { return getToken(Spin2Parser.NOT, 0); }
		public TerminalNode XOR() { return getToken(Spin2Parser.XOR, 0); }
		public TerminalNode ENCOD() { return getToken(Spin2Parser.ENCOD, 0); }
		public TerminalNode DECOD() { return getToken(Spin2Parser.DECOD, 0); }
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
		enterRule(_localctx, 60, RULE_opcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1034);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1035);
			_la = _input.LA(1);
			if ( !(((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & ((1L << (ENCOD - 69)) | (1L << (DECOD - 69)) | (1L << (AND - 69)) | (1L << (NOT - 69)) | (1L << (XOR - 69)) | (1L << (OR - 69)) | (1L << (IDENTIFIER - 69)))) != 0)) ) {
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
		enterRule(_localctx, 62, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1038);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==POUND_POUND || _la==POUND) {
				{
				setState(1037);
				prefix();
				}
			}

			setState(1040);
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
		enterRule(_localctx, 64, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1042);
			_la = _input.LA(1);
			if ( !(_la==POUND_POUND || _la==POUND) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1044);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(1043);
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
		public TerminalNode MODIFIER() { return getToken(Spin2Parser.MODIFIER, 0); }
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
		enterRule(_localctx, 66, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1046);
			match(MODIFIER);
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
		enterRule(_localctx, 68, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1048);
			expression(0);
			setState(1053);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1049);
				match(OPEN_BRACKET);
				setState(1050);
				((DataValueContext)_localctx).count = expression(0);
				setState(1051);
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
		public TerminalNode EQUALS() { return getToken(Spin2Parser.EQUALS, 0); }
		public TerminalNode NOT_EQUALS() { return getToken(Spin2Parser.NOT_EQUALS, 0); }
		public TerminalNode AND() { return getToken(Spin2Parser.AND, 0); }
		public TerminalNode LOGICAL_AND() { return getToken(Spin2Parser.LOGICAL_AND, 0); }
		public TerminalNode XOR() { return getToken(Spin2Parser.XOR, 0); }
		public TerminalNode LOGICAL_XOR() { return getToken(Spin2Parser.LOGICAL_XOR, 0); }
		public TerminalNode OR() { return getToken(Spin2Parser.OR, 0); }
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
		int _startState = 70;
		enterRecursionRule(_localctx, 70, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1071);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1056);
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
				setState(1057);
				((ExpressionContext)_localctx).exp = expression(16);
				}
				break;
			case FUNCTIONS:
				{
				setState(1058);
				((ExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1059);
				match(OPEN_PAREN);
				setState(1060);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1061);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1063);
				match(OPEN_PAREN);
				setState(1064);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1065);
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
				setState(1068);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1067);
					match(AT);
					}
				}

				setState(1070);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1114);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1112);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1073);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1074);
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
						setState(1075);
						((ExpressionContext)_localctx).right = expression(16);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1076);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1077);
						((ExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1078);
						((ExpressionContext)_localctx).right = expression(15);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1079);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1080);
						((ExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1081);
						((ExpressionContext)_localctx).right = expression(14);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1082);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1083);
						((ExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1084);
						((ExpressionContext)_localctx).right = expression(13);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1085);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1086);
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
						setState(1087);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1088);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1089);
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
						setState(1090);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1091);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1092);
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
						setState(1093);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1094);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1095);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUALS || _la==NOT_EQUALS) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1096);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1097);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1098);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_AND || _la==AND) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1099);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1100);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1101);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_XOR || _la==XOR) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1102);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1103);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1104);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_OR || _la==OR) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1105);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 12:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1106);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1107);
						((ExpressionContext)_localctx).operator = match(QUESTION);
						setState(1108);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(1109);
						((ExpressionContext)_localctx).operator = match(COLON);
						setState(1110);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(1116);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
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
		enterRule(_localctx, 72, RULE_atom);
		int _la;
		try {
			setState(1158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1117);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1118);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1119);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1120);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1121);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1122);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1123);
				match(IDENTIFIER);
				setState(1128);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
				case 1:
					{
					setState(1124);
					match(OPEN_BRACKET);
					setState(1125);
					expression(0);
					setState(1126);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1130);
				match(IDENTIFIER);
				setState(1131);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1136);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
				case 1:
					{
					setState(1132);
					match(OPEN_BRACKET);
					setState(1133);
					expression(0);
					setState(1134);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1138);
				match(IDENTIFIER);
				setState(1143);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1139);
					match(OPEN_BRACKET);
					setState(1140);
					expression(0);
					setState(1141);
					match(CLOSE_BRACKET);
					}
				}

				setState(1145);
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
				setState(1146);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1147);
				match(IDENTIFIER);
				setState(1152);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
				case 1:
					{
					setState(1148);
					match(OPEN_BRACKET);
					setState(1149);
					expression(0);
					setState(1150);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1155);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1154);
					match(DOT);
					}
				}

				setState(1157);
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
		case 24:
			return spinExpression_sempred((SpinExpressionContext)_localctx, predIndex);
		case 28:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 29:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 30:
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 35:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean spinExpression_sempred(SpinExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 20);
		case 1:
			return precpred(_ctx, 19);
		case 2:
			return precpred(_ctx, 18);
		case 3:
			return precpred(_ctx, 17);
		case 4:
			return precpred(_ctx, 16);
		case 5:
			return precpred(_ctx, 15);
		case 6:
			return precpred(_ctx, 14);
		case 7:
			return precpred(_ctx, 13);
		case 8:
			return precpred(_ctx, 12);
		case 9:
			return precpred(_ctx, 11);
		case 10:
			return precpred(_ctx, 10);
		case 11:
			return precpred(_ctx, 9);
		case 12:
			return precpred(_ctx, 8);
		}
		return true;
	}
	private boolean label_sempred(LabelContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return _input.LT(1).getCharPositionInLine() == 0;
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean opcode_sempred(OpcodeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 16:
			return precpred(_ctx, 15);
		case 17:
			return precpred(_ctx, 14);
		case 18:
			return precpred(_ctx, 13);
		case 19:
			return precpred(_ctx, 12);
		case 20:
			return precpred(_ctx, 11);
		case 21:
			return precpred(_ctx, 10);
		case 22:
			return precpred(_ctx, 9);
		case 23:
			return precpred(_ctx, 8);
		case 24:
			return precpred(_ctx, 7);
		case 25:
			return precpred(_ctx, 6);
		case 26:
			return precpred(_ctx, 5);
		case 27:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3U\u048b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\7\2N\n\2\f\2\16\2Q\13\2\3\2\3"+
		"\2\3\2\3\2\3\2\7\2X\n\2\f\2\16\2[\13\2\3\2\3\2\3\3\6\3`\n\3\r\3\16\3a"+
		"\3\3\7\3e\n\3\f\3\16\3h\13\3\3\3\3\3\3\3\7\3m\n\3\f\3\16\3p\13\3\3\3\3"+
		"\3\3\3\3\3\7\3v\n\3\f\3\16\3y\13\3\7\3{\n\3\f\3\16\3~\13\3\3\4\7\4\u0081"+
		"\n\4\f\4\16\4\u0084\13\4\3\4\3\4\3\4\3\4\7\4\u008a\n\4\f\4\16\4\u008d"+
		"\13\4\3\5\7\5\u0090\n\5\f\5\16\5\u0093\13\5\3\5\3\5\3\5\3\5\3\5\3\5\5"+
		"\5\u009b\n\5\3\5\7\5\u009e\n\5\f\5\16\5\u00a1\13\5\3\5\3\5\7\5\u00a5\n"+
		"\5\f\5\16\5\u00a8\13\5\3\6\7\6\u00ab\n\6\f\6\16\6\u00ae\13\6\3\6\3\6\3"+
		"\6\3\6\3\6\5\6\u00b5\n\6\3\6\7\6\u00b8\n\6\f\6\16\6\u00bb\13\6\3\7\6\7"+
		"\u00be\n\7\r\7\16\7\u00bf\3\7\7\7\u00c3\n\7\f\7\16\7\u00c6\13\7\3\7\7"+
		"\7\u00c9\n\7\f\7\16\7\u00cc\13\7\3\b\7\b\u00cf\n\b\f\b\16\b\u00d2\13\b"+
		"\3\b\3\b\3\b\3\b\3\b\7\b\u00d9\n\b\f\b\16\b\u00dc\13\b\3\b\3\b\3\b\6\b"+
		"\u00e1\n\b\r\b\16\b\u00e2\3\t\6\t\u00e6\n\t\r\t\16\t\u00e7\3\t\7\t\u00eb"+
		"\n\t\f\t\16\t\u00ee\13\t\3\t\3\t\3\t\7\t\u00f3\n\t\f\t\16\t\u00f6\13\t"+
		"\7\t\u00f8\n\t\f\t\16\t\u00fb\13\t\3\n\7\n\u00fe\n\n\f\n\16\n\u0101\13"+
		"\n\3\n\5\n\u0104\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u010b\n\n\3\n\6\n\u010e\n"+
		"\n\r\n\16\n\u010f\3\13\3\13\3\13\3\13\5\13\u0116\n\13\3\13\3\13\3\13\5"+
		"\13\u011b\n\13\3\13\3\13\5\13\u011f\n\13\3\13\6\13\u0122\n\13\r\13\16"+
		"\13\u0123\3\13\3\13\7\13\u0128\n\13\f\13\16\13\u012b\13\13\3\13\3\13\3"+
		"\f\3\f\3\f\7\f\u0132\n\f\f\f\16\f\u0135\13\f\3\r\3\r\3\r\7\r\u013a\n\r"+
		"\f\r\16\r\u013d\13\r\3\16\3\16\3\16\7\16\u0142\n\16\f\16\16\16\u0145\13"+
		"\16\3\17\5\17\u0148\n\17\3\17\5\17\u014b\n\17\3\17\3\17\3\17\3\17\3\17"+
		"\5\17\u0152\n\17\3\20\3\20\6\20\u0156\n\20\r\20\16\20\u0157\3\20\3\20"+
		"\3\20\5\20\u015d\n\20\3\21\5\21\u0160\n\21\3\21\3\21\3\21\5\21\u0165\n"+
		"\21\3\21\3\21\5\21\u0169\n\21\3\21\3\21\3\21\3\21\3\21\5\21\u0170\n\21"+
		"\3\21\3\21\5\21\u0174\n\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21"+
		"\u017e\n\21\3\21\3\21\5\21\u0182\n\21\3\22\3\22\3\22\7\22\u0187\n\22\f"+
		"\22\16\22\u018a\13\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u0193\n"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\5\23\u01a5\n\23\3\24\3\24\5\24\u01a9\n\24\3\24\6\24\u01ac"+
		"\n\24\r\24\16\24\u01ad\3\24\3\24\7\24\u01b2\n\24\f\24\16\24\u01b5\13\24"+
		"\3\24\3\24\3\24\6\24\u01ba\n\24\r\24\16\24\u01bb\3\24\3\24\7\24\u01c0"+
		"\n\24\f\24\16\24\u01c3\13\24\3\24\3\24\3\24\3\24\6\24\u01c9\n\24\r\24"+
		"\16\24\u01ca\3\24\3\24\6\24\u01cf\n\24\r\24\16\24\u01d0\3\24\3\24\7\24"+
		"\u01d5\n\24\f\24\16\24\u01d8\13\24\3\24\3\24\3\24\3\24\6\24\u01de\n\24"+
		"\r\24\16\24\u01df\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u01ea\n"+
		"\24\5\24\u01ec\n\24\3\24\6\24\u01ef\n\24\r\24\16\24\u01f0\3\24\3\24\7"+
		"\24\u01f5\n\24\f\24\16\24\u01f8\13\24\3\24\3\24\3\24\3\24\3\24\3\24\6"+
		"\24\u0200\n\24\r\24\16\24\u0201\3\24\3\24\7\24\u0206\n\24\f\24\16\24\u0209"+
		"\13\24\3\24\5\24\u020c\n\24\3\24\3\24\3\24\3\24\6\24\u0212\n\24\r\24\16"+
		"\24\u0213\3\24\3\24\7\24\u0218\n\24\f\24\16\24\u021b\13\24\3\24\5\24\u021e"+
		"\n\24\5\24\u0220\n\24\3\25\3\25\3\25\6\25\u0225\n\25\r\25\16\25\u0226"+
		"\3\25\3\25\7\25\u022b\n\25\f\25\16\25\u022e\13\25\3\25\3\25\7\25\u0232"+
		"\n\25\f\25\16\25\u0235\13\25\3\25\3\25\3\25\6\25\u023a\n\25\r\25\16\25"+
		"\u023b\3\25\3\25\7\25\u0240\n\25\f\25\16\25\u0243\13\25\3\25\3\25\7\25"+
		"\u0247\n\25\f\25\16\25\u024a\13\25\5\25\u024c\n\25\3\26\3\26\6\26\u0250"+
		"\n\26\r\26\16\26\u0251\3\26\3\26\7\26\u0256\n\26\f\26\16\26\u0259\13\26"+
		"\3\26\3\26\3\26\3\26\6\26\u025f\n\26\r\26\16\26\u0260\3\26\3\26\7\26\u0265"+
		"\n\26\f\26\16\26\u0268\13\26\3\26\3\26\3\26\3\26\3\26\6\26\u026f\n\26"+
		"\r\26\16\26\u0270\3\26\3\26\7\26\u0275\n\26\f\26\16\26\u0278\13\26\3\26"+
		"\3\26\5\26\u027c\n\26\3\27\3\27\3\27\6\27\u0281\n\27\r\27\16\27\u0282"+
		"\3\27\3\27\3\27\7\27\u0288\n\27\f\27\16\27\u028b\13\27\3\27\3\27\3\30"+
		"\3\30\3\30\5\30\u0292\n\30\3\30\3\30\5\30\u0296\n\30\3\30\6\30\u0299\n"+
		"\30\r\30\16\30\u029a\3\30\3\30\3\30\6\30\u02a0\n\30\r\30\16\30\u02a1\7"+
		"\30\u02a4\n\30\f\30\16\30\u02a7\13\30\3\30\5\30\u02aa\n\30\3\31\3\31\3"+
		"\31\3\31\6\31\u02b0\n\31\r\31\16\31\u02b1\3\31\3\31\7\31\u02b6\n\31\f"+
		"\31\16\31\u02b9\13\31\3\31\5\31\u02bc\n\31\3\31\3\31\3\31\6\31\u02c1\n"+
		"\31\r\31\16\31\u02c2\3\31\3\31\7\31\u02c7\n\31\f\31\16\31\u02ca\13\31"+
		"\3\31\5\31\u02cd\n\31\5\31\u02cf\n\31\3\32\3\32\3\32\3\32\7\32\u02d5\n"+
		"\32\f\32\16\32\u02d8\13\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u02f7\n\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\7\32\u0323\n\32\f\32\16"+
		"\32\u0326\13\32\3\33\3\33\3\34\6\34\u032b\n\34\r\34\16\34\u032c\3\34\7"+
		"\34\u0330\n\34\f\34\16\34\u0333\13\34\3\34\7\34\u0336\n\34\f\34\16\34"+
		"\u0339\13\34\3\35\3\35\6\35\u033d\n\35\r\35\16\35\u033e\3\35\7\35\u0342"+
		"\n\35\f\35\16\35\u0345\13\35\3\35\3\35\3\35\3\35\5\35\u034b\n\35\5\35"+
		"\u034d\n\35\3\35\6\35\u0350\n\35\r\35\16\35\u0351\3\35\7\35\u0355\n\35"+
		"\f\35\16\35\u0358\13\35\3\35\7\35\u035b\n\35\f\35\16\35\u035e\13\35\3"+
		"\35\5\35\u0361\n\35\3\35\3\35\3\35\3\35\7\35\u0367\n\35\f\35\16\35\u036a"+
		"\13\35\3\35\6\35\u036d\n\35\r\35\16\35\u036e\3\35\7\35\u0372\n\35\f\35"+
		"\16\35\u0375\13\35\3\35\7\35\u0378\n\35\f\35\16\35\u037b\13\35\3\35\5"+
		"\35\u037e\n\35\3\35\3\35\3\35\6\35\u0383\n\35\r\35\16\35\u0384\3\35\7"+
		"\35\u0388\n\35\f\35\16\35\u038b\13\35\3\35\7\35\u038e\n\35\f\35\16\35"+
		"\u0391\13\35\3\35\5\35\u0394\n\35\3\35\5\35\u0397\n\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u03a0\n\35\3\35\6\35\u03a3\n\35\r\35\16\35\u03a4"+
		"\3\35\7\35\u03a8\n\35\f\35\16\35\u03ab\13\35\3\35\7\35\u03ae\n\35\f\35"+
		"\16\35\u03b1\13\35\3\35\5\35\u03b4\n\35\3\35\5\35\u03b7\n\35\3\35\3\35"+
		"\3\35\3\35\3\35\5\35\u03be\n\35\3\35\6\35\u03c1\n\35\r\35\16\35\u03c2"+
		"\3\35\7\35\u03c6\n\35\f\35\16\35\u03c9\13\35\3\35\7\35\u03cc\n\35\f\35"+
		"\16\35\u03cf\13\35\3\35\5\35\u03d2\n\35\3\35\5\35\u03d5\n\35\3\35\3\35"+
		"\3\35\5\35\u03da\n\35\3\35\6\35\u03dd\n\35\r\35\16\35\u03de\3\35\7\35"+
		"\u03e2\n\35\f\35\16\35\u03e5\13\35\3\35\7\35\u03e8\n\35\f\35\16\35\u03eb"+
		"\13\35\3\35\5\35\u03ee\n\35\3\35\5\35\u03f1\n\35\3\35\3\35\5\35\u03f5"+
		"\n\35\3\35\6\35\u03f8\n\35\r\35\16\35\u03f9\3\35\7\35\u03fd\n\35\f\35"+
		"\16\35\u0400\13\35\5\35\u0402\n\35\3\36\3\36\5\36\u0406\n\36\3\36\3\36"+
		"\3\37\3\37\3\37\3 \3 \3 \3!\5!\u0411\n!\3!\3!\3\"\3\"\5\"\u0417\n\"\3"+
		"#\3#\3$\3$\3$\3$\3$\5$\u0420\n$\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3"+
		"%\5%\u042f\n%\3%\5%\u0432\n%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3"+
		"%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3%\3"+
		"%\3%\3%\7%\u045b\n%\f%\16%\u045e\13%\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&"+
		"\5&\u046b\n&\3&\3&\3&\3&\3&\3&\5&\u0473\n&\3&\3&\3&\3&\3&\5&\u047a\n&"+
		"\3&\3&\3&\3&\3&\3&\3&\5&\u0483\n&\3&\5&\u0486\n&\3&\5&\u0489\n&\3&\2\4"+
		"\62H\'\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<"+
		">@BDFHJ\2\23\4\2\4\4\7\7\3\2\64\65\5\2%&++GH\3\2\24\25\3\2\22\23\3\2\'"+
		"(\3\2%&\3\2DE\3\2\30\31\4\2\16\16JJ\4\2\20\20LL\4\2\17\17MM\3\2\t\r\4"+
		"\2NOQQ\5\2GHJMUU\4\2\21\21\36\36\4\2%&++\2\u0548\2O\3\2\2\2\4_\3\2\2\2"+
		"\6\u0082\3\2\2\2\b\u0091\3\2\2\2\n\u00ac\3\2\2\2\f\u00bd\3\2\2\2\16\u00d0"+
		"\3\2\2\2\20\u00e5\3\2\2\2\22\u00ff\3\2\2\2\24\u0111\3\2\2\2\26\u012e\3"+
		"\2\2\2\30\u0136\3\2\2\2\32\u013e\3\2\2\2\34\u0147\3\2\2\2\36\u015c\3\2"+
		"\2\2 \u0181\3\2\2\2\"\u0183\3\2\2\2$\u01a4\3\2\2\2&\u021f\3\2\2\2(\u024b"+
		"\3\2\2\2*\u027b\3\2\2\2,\u027d\3\2\2\2.\u028e\3\2\2\2\60\u02ce\3\2\2\2"+
		"\62\u02f6\3\2\2\2\64\u0327\3\2\2\2\66\u032a\3\2\2\28\u0401\3\2\2\2:\u0403"+
		"\3\2\2\2<\u0409\3\2\2\2>\u040c\3\2\2\2@\u0410\3\2\2\2B\u0414\3\2\2\2D"+
		"\u0418\3\2\2\2F\u041a\3\2\2\2H\u0431\3\2\2\2J\u0488\3\2\2\2LN\7\7\2\2"+
		"ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2PY\3\2\2\2QO\3\2\2\2RX\5\4\3\2"+
		"SX\5\f\7\2TX\5\20\t\2UX\5\24\13\2VX\5\66\34\2WR\3\2\2\2WS\3\2\2\2WT\3"+
		"\2\2\2WU\3\2\2\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\\\3\2\2\2[Y"+
		"\3\2\2\2\\]\7\2\2\3]\3\3\2\2\2^`\7\61\2\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2"+
		"\2ab\3\2\2\2bf\3\2\2\2ce\7\7\2\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2"+
		"\2g|\3\2\2\2hf\3\2\2\2in\5\6\4\2jk\7#\2\2km\5\6\4\2lj\3\2\2\2mp\3\2\2"+
		"\2nl\3\2\2\2no\3\2\2\2o{\3\2\2\2pn\3\2\2\2q{\5\b\5\2rw\5\n\6\2st\7#\2"+
		"\2tv\5\n\6\2us\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x{\3\2\2\2yw\3\2\2"+
		"\2zi\3\2\2\2zq\3\2\2\2zr\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\5\3\2"+
		"\2\2~|\3\2\2\2\177\u0081\7\3\2\2\u0080\177\3\2\2\2\u0081\u0084\3\2\2\2"+
		"\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084\u0082"+
		"\3\2\2\2\u0085\u0086\7U\2\2\u0086\u0087\7!\2\2\u0087\u008b\5H%\2\u0088"+
		"\u008a\t\2\2\2\u0089\u0088\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2"+
		"\2\2\u008b\u008c\3\2\2\2\u008c\7\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u0090"+
		"\7\3\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091"+
		"\u0092\3\2\2\2\u0092\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7\36"+
		"\2\2\u0095\u009a\5H%\2\u0096\u0097\7-\2\2\u0097\u0098\5H%\2\u0098\u0099"+
		"\7.\2\2\u0099\u009b\3\2\2\2\u009a\u0096\3\2\2\2\u009a\u009b\3\2\2\2\u009b"+
		"\u009f\3\2\2\2\u009c\u009e\t\2\2\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2"+
		"\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a6\3\2\2\2\u00a1"+
		"\u009f\3\2\2\2\u00a2\u00a3\7#\2\2\u00a3\u00a5\5\n\6\2\u00a4\u00a2\3\2"+
		"\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7"+
		"\t\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\u00ab\7\3\2\2\u00aa\u00a9\3\2\2\2"+
		"\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00af"+
		"\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b4\7U\2\2\u00b0\u00b1\7-\2\2\u00b1"+
		"\u00b2\5H%\2\u00b2\u00b3\7.\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00b0\3\2\2"+
		"\2\u00b4\u00b5\3\2\2\2\u00b5\u00b9\3\2\2\2\u00b6\u00b8\t\2\2\2\u00b7\u00b6"+
		"\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba"+
		"\13\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bc\u00be\7\63\2\2\u00bd\u00bc\3\2\2"+
		"\2\u00be\u00bf\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c4"+
		"\3\2\2\2\u00c1\u00c3\7\7\2\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00ca\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c7\u00c9\5\16\b\2\u00c8\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca"+
		"\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\r\3\2\2\2\u00cc\u00ca\3\2\2\2"+
		"\u00cd\u00cf\7\3\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce"+
		"\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d3\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3"+
		"\u00da\7U\2\2\u00d4\u00d5\7-\2\2\u00d5\u00d6\5H%\2\u00d6\u00d7\7.\2\2"+
		"\u00d7\u00d9\3\2\2\2\u00d8\u00d4\3\2\2\2\u00d9\u00dc\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dd\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd"+
		"\u00de\7*\2\2\u00de\u00e0\7\t\2\2\u00df\u00e1\t\2\2\2\u00e0\u00df\3\2"+
		"\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3"+
		"\17\3\2\2\2\u00e4\u00e6\7\62\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e7\3\2\2"+
		"\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00ec\3\2\2\2\u00e9\u00eb"+
		"\7\7\2\2\u00ea\u00e9\3\2\2\2\u00eb\u00ee\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec"+
		"\u00ed\3\2\2\2\u00ed\u00f9\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ef\u00f4\5\22"+
		"\n\2\u00f0\u00f1\7#\2\2\u00f1\u00f3\5\22\n\2\u00f2\u00f0\3\2\2\2\u00f3"+
		"\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f8\3\2"+
		"\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00ef\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9"+
		"\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\21\3\2\2\2\u00fb\u00f9\3\2\2"+
		"\2\u00fc\u00fe\7\3\2\2\u00fd\u00fc\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u00fd"+
		"\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0102"+
		"\u0104\7R\2\2\u0103\u0102\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2"+
		"\2\2\u0105\u010a\7U\2\2\u0106\u0107\7-\2\2\u0107\u0108\5H%\2\u0108\u0109"+
		"\7.\2\2\u0109\u010b\3\2\2\2\u010a\u0106\3\2\2\2\u010a\u010b\3\2\2\2\u010b"+
		"\u010d\3\2\2\2\u010c\u010e\t\2\2\2\u010d\u010c\3\2\2\2\u010e\u010f\3\2"+
		"\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110\23\3\2\2\2\u0111\u0112"+
		"\t\3\2\2\u0112\u0113\7U\2\2\u0113\u0115\7/\2\2\u0114\u0116\5\26\f\2\u0115"+
		"\u0114\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u011a\7\60"+
		"\2\2\u0118\u0119\7*\2\2\u0119\u011b\5\30\r\2\u011a\u0118\3\2\2\2\u011a"+
		"\u011b\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011d\7\33\2\2\u011d\u011f\5"+
		"\32\16\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0121\3\2\2\2\u0120"+
		"\u0122\7\7\2\2\u0121\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0121\3\2"+
		"\2\2\u0123\u0124\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0129\7\3\2\2\u0126"+
		"\u0128\5\36\20\2\u0127\u0126\3\2\2\2\u0128\u012b\3\2\2\2\u0129\u0127\3"+
		"\2\2\2\u0129\u012a\3\2\2\2\u012a\u012c\3\2\2\2\u012b\u0129\3\2\2\2\u012c"+
		"\u012d\7\4\2\2\u012d\25\3\2\2\2\u012e\u0133\7U\2\2\u012f\u0130\7#\2\2"+
		"\u0130\u0132\7U\2\2\u0131\u012f\3\2\2\2\u0132\u0135\3\2\2\2\u0133\u0131"+
		"\3\2\2\2\u0133\u0134\3\2\2\2\u0134\27\3\2\2\2\u0135\u0133\3\2\2\2\u0136"+
		"\u013b\7U\2\2\u0137\u0138\7#\2\2\u0138\u013a\7U\2\2\u0139\u0137\3\2\2"+
		"\2\u013a\u013d\3\2\2\2\u013b\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\31"+
		"\3\2\2\2\u013d\u013b\3\2\2\2\u013e\u0143\5\34\17\2\u013f\u0140\7#\2\2"+
		"\u0140\u0142\5\34\17\2\u0141\u013f\3\2\2\2\u0142\u0145\3\2\2\2\u0143\u0141"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\33\3\2\2\2\u0145\u0143\3\2\2\2\u0146"+
		"\u0148\7Q\2\2\u0147\u0146\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u014a\3\2"+
		"\2\2\u0149\u014b\7R\2\2\u014a\u0149\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u0151\7U\2\2\u014d\u014e\7-\2\2\u014e\u014f\5H%\2"+
		"\u014f\u0150\7.\2\2\u0150\u0152\3\2\2\2\u0151\u014d\3\2\2\2\u0151\u0152"+
		"\3\2\2\2\u0152\35\3\2\2\2\u0153\u0155\5\62\32\2\u0154\u0156\7\7\2\2\u0155"+
		"\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2"+
		"\2\2\u0158\u015d\3\2\2\2\u0159\u015d\5&\24\2\u015a\u015d\5(\25\2\u015b"+
		"\u015d\5,\27\2\u015c\u0153\3\2\2\2\u015c\u0159\3\2\2\2\u015c\u015a\3\2"+
		"\2\2\u015c\u015b\3\2\2\2\u015d\37\3\2\2\2\u015e\u0160\7$\2\2\u015f\u015e"+
		"\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162\7U\2\2\u0162"+
		"\u0164\7/\2\2\u0163\u0165\5\"\22\2\u0164\u0163\3\2\2\2\u0164\u0165\3\2"+
		"\2\2\u0165\u0166\3\2\2\2\u0166\u0182\7\60\2\2\u0167\u0169\7$\2\2\u0168"+
		"\u0167\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016b\7U"+
		"\2\2\u016b\u016c\7\"\2\2\u016c\u016d\7U\2\2\u016d\u016f\7/\2\2\u016e\u0170"+
		"\5\"\22\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u0171\3\2\2\2"+
		"\u0171\u0182\7\60\2\2\u0172\u0174\7$\2\2\u0173\u0172\3\2\2\2\u0173\u0174"+
		"\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0176\7U\2\2\u0176\u0177\7-\2\2\u0177"+
		"\u0178\5\62\32\2\u0178\u0179\7.\2\2\u0179\u017a\7\"\2\2\u017a\u017b\7"+
		"U\2\2\u017b\u017d\7/\2\2\u017c\u017e\5\"\22\2\u017d\u017c\3\2\2\2\u017d"+
		"\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\7\60\2\2\u0180\u0182\3"+
		"\2\2\2\u0181\u015f\3\2\2\2\u0181\u0168\3\2\2\2\u0181\u0173\3\2\2\2\u0182"+
		"!\3\2\2\2\u0183\u0188\5\62\32\2\u0184\u0185\7#\2\2\u0185\u0187\5\62\32"+
		"\2\u0186\u0184\3\2\2\2\u0187\u018a\3\2\2\2\u0188\u0186\3\2\2\2\u0188\u0189"+
		"\3\2\2\2\u0189#\3\2\2\2\u018a\u0188\3\2\2\2\u018b\u018c\7\35\2\2\u018c"+
		"\u01a5\7U\2\2\u018d\u0192\7U\2\2\u018e\u018f\7-\2\2\u018f\u0190\5\62\32"+
		"\2\u0190\u0191\7.\2\2\u0191\u0193\3\2\2\2\u0192\u018e\3\2\2\2\u0192\u0193"+
		"\3\2\2\2\u0193\u01a5\3\2\2\2\u0194\u0195\7U\2\2\u0195\u0196\7\"\2\2\u0196"+
		"\u01a5\7U\2\2\u0197\u0198\7U\2\2\u0198\u0199\7\"\2\2\u0199\u019a\7-\2"+
		"\2\u019a\u019b\5\62\32\2\u019b\u019c\7.\2\2\u019c\u01a5\3\2\2\2\u019d"+
		"\u019e\7U\2\2\u019e\u019f\7\"\2\2\u019f\u01a0\7U\2\2\u01a0\u01a1\7-\2"+
		"\2\u01a1\u01a2\5\62\32\2\u01a2\u01a3\7.\2\2\u01a3\u01a5\3\2\2\2\u01a4"+
		"\u018b\3\2\2\2\u01a4\u018d\3\2\2\2\u01a4\u0194\3\2\2\2\u01a4\u0197\3\2"+
		"\2\2\u01a4\u019d\3\2\2\2\u01a5%\3\2\2\2\u01a6\u01a8\7\67\2\2\u01a7\u01a9"+
		"\5\62\32\2\u01a8\u01a7\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9\u01ab\3\2\2\2"+
		"\u01aa\u01ac\7\7\2\2\u01ab\u01aa\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01ab"+
		"\3\2\2\2\u01ad\u01ae\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01b3\7\3\2\2\u01b0"+
		"\u01b2\5\36\20\2\u01b1\u01b0\3\2\2\2\u01b2\u01b5\3\2\2\2\u01b3\u01b1\3"+
		"\2\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b6\3\2\2\2\u01b5\u01b3\3\2\2\2\u01b6"+
		"\u0220\7\4\2\2\u01b7\u01b9\7\67\2\2\u01b8\u01ba\7\7\2\2\u01b9\u01b8\3"+
		"\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bc"+
		"\u01bd\3\2\2\2\u01bd\u01c1\7\3\2\2\u01be\u01c0\5\36\20\2\u01bf\u01be\3"+
		"\2\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2"+
		"\u01c4\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01c5\7\4\2\2\u01c5\u01c6\7;"+
		"\2\2\u01c6\u01c8\5\62\32\2\u01c7\u01c9\7\7\2\2\u01c8\u01c7\3\2\2\2\u01c9"+
		"\u01ca\3\2\2\2\u01ca\u01c8\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb\u0220\3\2"+
		"\2\2\u01cc\u01ce\7\67\2\2\u01cd\u01cf\7\7\2\2\u01ce\u01cd\3\2\2\2\u01cf"+
		"\u01d0\3\2\2\2\u01d0\u01ce\3\2\2\2\u01d0\u01d1\3\2\2\2\u01d1\u01d2\3\2"+
		"\2\2\u01d2\u01d6\7\3\2\2\u01d3\u01d5\5\36\20\2\u01d4\u01d3\3\2\2\2\u01d5"+
		"\u01d8\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d6\u01d7\3\2\2\2\u01d7\u01d9\3\2"+
		"\2\2\u01d8\u01d6\3\2\2\2\u01d9\u01da\7\4\2\2\u01da\u01db\7<\2\2\u01db"+
		"\u01dd\5\62\32\2\u01dc\u01de\7\7\2\2\u01dd\u01dc\3\2\2\2\u01de\u01df\3"+
		"\2\2\2\u01df\u01dd\3\2\2\2\u01df\u01e0\3\2\2\2\u01e0\u0220\3\2\2\2\u01e1"+
		"\u01e2\7\67\2\2\u01e2\u01e3\7U\2\2\u01e3\u01e4\78\2\2\u01e4\u01eb\5\62"+
		"\32\2\u01e5\u01e6\79\2\2\u01e6\u01e9\5\62\32\2\u01e7\u01e8\7:\2\2\u01e8"+
		"\u01ea\5\62\32\2\u01e9\u01e7\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01ec\3"+
		"\2\2\2\u01eb\u01e5\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ee\3\2\2\2\u01ed"+
		"\u01ef\7\7\2\2\u01ee\u01ed\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0\u01ee\3\2"+
		"\2\2\u01f0\u01f1\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2\u01f6\7\3\2\2\u01f3"+
		"\u01f5\5\36\20\2\u01f4\u01f3\3\2\2\2\u01f5\u01f8\3\2\2\2\u01f6\u01f4\3"+
		"\2\2\2\u01f6\u01f7\3\2\2\2\u01f7\u01f9\3\2\2\2\u01f8\u01f6\3\2\2\2\u01f9"+
		"\u01fa\7\4\2\2\u01fa\u0220\3\2\2\2\u01fb\u01fc\7\67\2\2\u01fc\u01fd\7"+
		";\2\2\u01fd\u01ff\5\62\32\2\u01fe\u0200\7\7\2\2\u01ff\u01fe\3\2\2\2\u0200"+
		"\u0201\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u020b\3\2"+
		"\2\2\u0203\u0207\7\3\2\2\u0204\u0206\5\36\20\2\u0205\u0204\3\2\2\2\u0206"+
		"\u0209\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0208\3\2\2\2\u0208\u020a\3\2"+
		"\2\2\u0209\u0207\3\2\2\2\u020a\u020c\7\4\2\2\u020b\u0203\3\2\2\2\u020b"+
		"\u020c\3\2\2\2\u020c\u0220\3\2\2\2\u020d\u020e\7\67\2\2\u020e\u020f\7"+
		"<\2\2\u020f\u0211\5\62\32\2\u0210\u0212\7\7\2\2\u0211\u0210\3\2\2\2\u0212"+
		"\u0213\3\2\2\2\u0213\u0211\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u021d\3\2"+
		"\2\2\u0215\u0219\7\3\2\2\u0216\u0218\5\36\20\2\u0217\u0216\3\2\2\2\u0218"+
		"\u021b\3\2\2\2\u0219\u0217\3\2\2\2\u0219\u021a\3\2\2\2\u021a\u021c\3\2"+
		"\2\2\u021b\u0219\3\2\2\2\u021c\u021e\7\4\2\2\u021d\u0215\3\2\2\2\u021d"+
		"\u021e\3\2\2\2\u021e\u0220\3\2\2\2\u021f\u01a6\3\2\2\2\u021f\u01b7\3\2"+
		"\2\2\u021f\u01cc\3\2\2\2\u021f\u01e1\3\2\2\2\u021f\u01fb\3\2\2\2\u021f"+
		"\u020d\3\2\2\2\u0220\'\3\2\2\2\u0221\u0222\7A\2\2\u0222\u0224\5\62\32"+
		"\2\u0223\u0225\7\7\2\2\u0224\u0223\3\2\2\2\u0225\u0226\3\2\2\2\u0226\u0224"+
		"\3\2\2\2\u0226\u0227\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u022c\7\3\2\2\u0229"+
		"\u022b\5\36\20\2\u022a\u0229\3\2\2\2\u022b\u022e\3\2\2\2\u022c\u022a\3"+
		"\2\2\2\u022c\u022d\3\2\2\2\u022d\u022f\3\2\2\2\u022e\u022c\3\2\2\2\u022f"+
		"\u0233\7\4\2\2\u0230\u0232\5*\26\2\u0231\u0230\3\2\2\2\u0232\u0235\3\2"+
		"\2\2\u0233\u0231\3\2\2\2\u0233\u0234\3\2\2\2\u0234\u024c\3\2\2\2\u0235"+
		"\u0233\3\2\2\2\u0236\u0237\7@\2\2\u0237\u0239\5\62\32\2\u0238\u023a\7"+
		"\7\2\2\u0239\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u0239\3\2\2\2\u023b"+
		"\u023c\3\2\2\2\u023c\u023d\3\2\2\2\u023d\u0241\7\3\2\2\u023e\u0240\5\36"+
		"\20\2\u023f\u023e\3\2\2\2\u0240\u0243\3\2\2\2\u0241\u023f\3\2\2\2\u0241"+
		"\u0242\3\2\2\2\u0242\u0244\3\2\2\2\u0243\u0241\3\2\2\2\u0244\u0248\7\4"+
		"\2\2\u0245\u0247\5*\26\2\u0246\u0245\3\2\2\2\u0247\u024a\3\2\2\2\u0248"+
		"\u0246\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024c\3\2\2\2\u024a\u0248\3\2"+
		"\2\2\u024b\u0221\3\2\2\2\u024b\u0236\3\2\2\2\u024c)\3\2\2\2\u024d\u024f"+
		"\7?\2\2\u024e\u0250\7\7\2\2\u024f\u024e\3\2\2\2\u0250\u0251\3\2\2\2\u0251"+
		"\u024f\3\2\2\2\u0251\u0252\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0257\7\3"+
		"\2\2\u0254\u0256\5\36\20\2\u0255\u0254\3\2\2\2\u0256\u0259\3\2\2\2\u0257"+
		"\u0255\3\2\2\2\u0257\u0258\3\2\2\2\u0258\u025a\3\2\2\2\u0259\u0257\3\2"+
		"\2\2\u025a\u027c\7\4\2\2\u025b\u025c\7>\2\2\u025c\u025e\5\62\32\2\u025d"+
		"\u025f\7\7\2\2\u025e\u025d\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u025e\3\2"+
		"\2\2\u0260\u0261\3\2\2\2\u0261\u0262\3\2\2\2\u0262\u0266\7\3\2\2\u0263"+
		"\u0265\5\36\20\2\u0264\u0263\3\2\2\2\u0265\u0268\3\2\2\2\u0266\u0264\3"+
		"\2\2\2\u0266\u0267\3\2\2\2\u0267\u0269\3\2\2\2\u0268\u0266\3\2\2\2\u0269"+
		"\u026a\7\4\2\2\u026a\u027c\3\2\2\2\u026b\u026c\7=\2\2\u026c\u026e\5\62"+
		"\32\2\u026d\u026f\7\7\2\2\u026e\u026d\3\2\2\2\u026f\u0270\3\2\2\2\u0270"+
		"\u026e\3\2\2\2\u0270\u0271\3\2\2\2\u0271\u0272\3\2\2\2\u0272\u0276\7\3"+
		"\2\2\u0273\u0275\5\36\20\2\u0274\u0273\3\2\2\2\u0275\u0278\3\2\2\2\u0276"+
		"\u0274\3\2\2\2\u0276\u0277\3\2\2\2\u0277\u0279\3\2\2\2\u0278\u0276\3\2"+
		"\2\2\u0279\u027a\7\4\2\2\u027a\u027c\3\2\2\2\u027b\u024d\3\2\2\2\u027b"+
		"\u025b\3\2\2\2\u027b\u026b\3\2\2\2\u027c+\3\2\2\2\u027d\u027e\7B\2\2\u027e"+
		"\u0280\5\62\32\2\u027f\u0281\7\7\2\2\u0280\u027f\3\2\2\2\u0281\u0282\3"+
		"\2\2\2\u0282\u0280\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0284\3\2\2\2\u0284"+
		"\u0289\7\3\2\2\u0285\u0288\5.\30\2\u0286\u0288\5\60\31\2\u0287\u0285\3"+
		"\2\2\2\u0287\u0286\3\2\2\2\u0288\u028b\3\2\2\2\u0289\u0287\3\2\2\2\u0289"+
		"\u028a\3\2\2\2\u028a\u028c\3\2\2\2\u028b\u0289\3\2\2\2\u028c\u028d\7\4"+
		"\2\2\u028d-\3\2\2\2\u028e\u0291\5\62\32\2\u028f\u0290\7\27\2\2\u0290\u0292"+
		"\5H%\2\u0291\u028f\3\2\2\2\u0291\u0292\3\2\2\2\u0292\u0293\3\2\2\2\u0293"+
		"\u0295\7*\2\2\u0294\u0296\5\62\32\2\u0295\u0294\3\2\2\2\u0295\u0296\3"+
		"\2\2\2\u0296\u0298\3\2\2\2\u0297\u0299\7\7\2\2\u0298\u0297\3\2\2\2\u0299"+
		"\u029a\3\2\2\2\u029a\u0298\3\2\2\2\u029a\u029b\3\2\2\2\u029b\u02a9\3\2"+
		"\2\2\u029c\u02a5\7\3\2\2\u029d\u029f\5\62\32\2\u029e\u02a0\7\7\2\2\u029f"+
		"\u029e\3\2\2\2\u02a0\u02a1\3\2\2\2\u02a1\u029f\3\2\2\2\u02a1\u02a2\3\2"+
		"\2\2\u02a2\u02a4\3\2\2\2\u02a3\u029d\3\2\2\2\u02a4\u02a7\3\2\2\2\u02a5"+
		"\u02a3\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a8\3\2\2\2\u02a7\u02a5\3\2"+
		"\2\2\u02a8\u02aa\7\4\2\2\u02a9\u029c\3\2\2\2\u02a9\u02aa\3\2\2\2\u02aa"+
		"/\3\2\2\2\u02ab\u02ac\7C\2\2\u02ac\u02ad\7*\2\2\u02ad\u02af\5\62\32\2"+
		"\u02ae\u02b0\7\7\2\2\u02af\u02ae\3\2\2\2\u02b0\u02b1\3\2\2\2\u02b1\u02af"+
		"\3\2\2\2\u02b1\u02b2\3\2\2\2\u02b2\u02bb\3\2\2\2\u02b3\u02b7\7\3\2\2\u02b4"+
		"\u02b6\5\36\20\2\u02b5\u02b4\3\2\2\2\u02b6\u02b9\3\2\2\2\u02b7\u02b5\3"+
		"\2\2\2\u02b7\u02b8\3\2\2\2\u02b8\u02ba\3\2\2\2\u02b9\u02b7\3\2\2\2\u02ba"+
		"\u02bc\7\4\2\2\u02bb\u02b3\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02cf\3\2"+
		"\2\2\u02bd\u02be\7C\2\2\u02be\u02c0\7*\2\2\u02bf\u02c1\7\7\2\2\u02c0\u02bf"+
		"\3\2\2\2\u02c1\u02c2\3\2\2\2\u02c2\u02c0\3\2\2\2\u02c2\u02c3\3\2\2\2\u02c3"+
		"\u02cc\3\2\2\2\u02c4\u02c8\7\3\2\2\u02c5\u02c7\5\36\20\2\u02c6\u02c5\3"+
		"\2\2\2\u02c7\u02ca\3\2\2\2\u02c8\u02c6\3\2\2\2\u02c8\u02c9\3\2\2\2\u02c9"+
		"\u02cb\3\2\2\2\u02ca\u02c8\3\2\2\2\u02cb\u02cd\7\4\2\2\u02cc\u02c4\3\2"+
		"\2\2\u02cc\u02cd\3\2\2\2\u02cd\u02cf\3\2\2\2\u02ce\u02ab\3\2\2\2\u02ce"+
		"\u02bd\3\2\2\2\u02cf\61\3\2\2\2\u02d0\u02d1\b\32\1\2\u02d1\u02d6\5$\23"+
		"\2\u02d2\u02d3\7#\2\2\u02d3\u02d5\7U\2\2\u02d4\u02d2\3\2\2\2\u02d5\u02d8"+
		"\3\2\2\2\u02d6\u02d4\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02d9\3\2\2\2\u02d8"+
		"\u02d6\3\2\2\2\u02d9\u02da\7\26\2\2\u02da\u02db\5\62\32\31\u02db\u02f7"+
		"\3\2\2\2\u02dc\u02dd\7R\2\2\u02dd\u02de\7-\2\2\u02de\u02df\5\62\32\2\u02df"+
		"\u02e0\7.\2\2\u02e0\u02e1\7\26\2\2\u02e1\u02e2\5\62\32\30\u02e2\u02f7"+
		"\3\2\2\2\u02e3\u02e4\t\4\2\2\u02e4\u02f7\5\62\32\27\u02e5\u02e6\7I\2\2"+
		"\u02e6\u02e7\7/\2\2\u02e7\u02e8\5\62\32\2\u02e8\u02e9\7\60\2\2\u02e9\u02f7"+
		"\3\2\2\2\u02ea\u02eb\7/\2\2\u02eb\u02ec\5\62\32\2\u02ec\u02ed\7\60\2\2"+
		"\u02ed\u02f7\3\2\2\2\u02ee\u02f7\5J&\2\u02ef\u02f7\5$\23\2\u02f0\u02f1"+
		"\5$\23\2\u02f1\u02f2\t\5\2\2\u02f2\u02f7\3\2\2\2\u02f3\u02f4\t\5\2\2\u02f4"+
		"\u02f7\5$\23\2\u02f5\u02f7\5 \21\2\u02f6\u02d0\3\2\2\2\u02f6\u02dc\3\2"+
		"\2\2\u02f6\u02e3\3\2\2\2\u02f6\u02e5\3\2\2\2\u02f6\u02ea\3\2\2\2\u02f6"+
		"\u02ee\3\2\2\2\u02f6\u02ef\3\2\2\2\u02f6\u02f0\3\2\2\2\u02f6\u02f3\3\2"+
		"\2\2\u02f6\u02f5\3\2\2\2\u02f7\u0324\3\2\2\2\u02f8\u02f9\f\26\2\2\u02f9"+
		"\u02fa\t\6\2\2\u02fa\u0323\5\62\32\27\u02fb\u02fc\f\25\2\2\u02fc\u02fd"+
		"\7\32\2\2\u02fd\u0323\5\62\32\26\u02fe\u02ff\f\24\2\2\u02ff\u0300\7\34"+
		"\2\2\u0300\u0323\5\62\32\25\u0301\u0302\f\23\2\2\u0302\u0303\7\33\2\2"+
		"\u0303\u0323\5\62\32\24\u0304\u0305\f\22\2\2\u0305\u0306\t\7\2\2\u0306"+
		"\u0323\5\62\32\23\u0307\u0308\f\21\2\2\u0308\u0309\t\b\2\2\u0309\u0323"+
		"\5\62\32\22\u030a\u030b\f\20\2\2\u030b\u030c\7F\2\2\u030c\u0323\5\62\32"+
		"\21\u030d\u030e\f\17\2\2\u030e\u030f\t\t\2\2\u030f\u0323\5\62\32\20\u0310"+
		"\u0311\f\16\2\2\u0311\u0312\t\n\2\2\u0312\u0323\5\62\32\17\u0313\u0314"+
		"\f\r\2\2\u0314\u0315\t\13\2\2\u0315\u0323\5\62\32\16\u0316\u0317\f\f\2"+
		"\2\u0317\u0318\t\f\2\2\u0318\u0323\5\62\32\r\u0319\u031a\f\13\2\2\u031a"+
		"\u031b\t\r\2\2\u031b\u0323\5\62\32\f\u031c\u031d\f\n\2\2\u031d\u031e\7"+
		")\2\2\u031e\u031f\5\62\32\2\u031f\u0320\7*\2\2\u0320\u0321\5\62\32\13"+
		"\u0321\u0323\3\2\2\2\u0322\u02f8\3\2\2\2\u0322\u02fb\3\2\2\2\u0322\u02fe"+
		"\3\2\2\2\u0322\u0301\3\2\2\2\u0322\u0304\3\2\2\2\u0322\u0307\3\2\2\2\u0322"+
		"\u030a\3\2\2\2\u0322\u030d\3\2\2\2\u0322\u0310\3\2\2\2\u0322\u0313\3\2"+
		"\2\2\u0322\u0316\3\2\2\2\u0322\u0319\3\2\2\2\u0322\u031c\3\2\2\2\u0323"+
		"\u0326\3\2\2\2\u0324\u0322\3\2\2\2\u0324\u0325\3\2\2\2\u0325\63\3\2\2"+
		"\2\u0326\u0324\3\2\2\2\u0327\u0328\t\16\2\2\u0328\65\3\2\2\2\u0329\u032b"+
		"\7\66\2\2\u032a\u0329\3\2\2\2\u032b\u032c\3\2\2\2\u032c\u032a\3\2\2\2"+
		"\u032c\u032d\3\2\2\2\u032d\u0331\3\2\2\2\u032e\u0330\7\7\2\2\u032f\u032e"+
		"\3\2\2\2\u0330\u0333\3\2\2\2\u0331\u032f\3\2\2\2\u0331\u0332\3\2\2\2\u0332"+
		"\u0337\3\2\2\2\u0333\u0331\3\2\2\2\u0334\u0336\58\35\2\u0335\u0334\3\2"+
		"\2\2\u0336\u0339\3\2\2\2\u0337\u0335\3\2\2\2\u0337\u0338\3\2\2\2\u0338"+
		"\67\3\2\2\2\u0339\u0337\3\2\2\2\u033a\u033c\5:\36\2\u033b\u033d\7\7\2"+
		"\2\u033c\u033b\3\2\2\2\u033d\u033e\3\2\2\2\u033e\u033c\3\2\2\2\u033e\u033f"+
		"\3\2\2\2\u033f\u0402\3\2\2\2\u0340\u0342\7\3\2\2\u0341\u0340\3\2\2\2\u0342"+
		"\u0345\3\2\2\2\u0343\u0341\3\2\2\2\u0343\u0344\3\2\2\2\u0344\u0346\3\2"+
		"\2\2\u0345\u0343\3\2\2\2\u0346\u034c\t\17\2\2\u0347\u034a\5H%\2\u0348"+
		"\u0349\7#\2\2\u0349\u034b\5H%\2\u034a\u0348\3\2\2\2\u034a\u034b\3\2\2"+
		"\2\u034b\u034d\3\2\2\2\u034c\u0347\3\2\2\2\u034c\u034d\3\2\2\2\u034d\u034f"+
		"\3\2\2\2\u034e\u0350\7\7\2\2\u034f\u034e\3\2\2\2\u0350\u0351\3\2\2\2\u0351"+
		"\u034f\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0356\3\2\2\2\u0353\u0355\t\2"+
		"\2\2\u0354\u0353\3\2\2\2\u0355\u0358\3\2\2\2\u0356\u0354\3\2\2\2\u0356"+
		"\u0357\3\2\2\2\u0357\u0402\3\2\2\2\u0358\u0356\3\2\2\2\u0359\u035b\7\3"+
		"\2\2\u035a\u0359\3\2\2\2\u035b\u035e\3\2\2\2\u035c\u035a\3\2\2\2\u035c"+
		"\u035d\3\2\2\2\u035d\u0360\3\2\2\2\u035e\u035c\3\2\2\2\u035f\u0361\5:"+
		"\36\2\u0360\u035f\3\2\2\2\u0360\u0361\3\2\2\2\u0361\u0362\3\2\2\2\u0362"+
		"\u0363\7R\2\2\u0363\u0368\5F$\2\u0364\u0365\7#\2\2\u0365\u0367\5F$\2\u0366"+
		"\u0364\3\2\2\2\u0367\u036a\3\2\2\2\u0368\u0366\3\2\2\2\u0368\u0369\3\2"+
		"\2\2\u0369\u036c\3\2\2\2\u036a\u0368\3\2\2\2\u036b\u036d\7\7\2\2\u036c"+
		"\u036b\3\2\2\2\u036d\u036e\3\2\2\2\u036e\u036c\3\2\2\2\u036e\u036f\3\2"+
		"\2\2\u036f\u0373\3\2\2\2\u0370\u0372\t\2\2\2\u0371\u0370\3\2\2\2\u0372"+
		"\u0375\3\2\2\2\u0373\u0371\3\2\2\2\u0373\u0374\3\2\2\2\u0374\u0402\3\2"+
		"\2\2\u0375\u0373\3\2\2\2\u0376\u0378\7\3\2\2\u0377\u0376\3\2\2\2\u0378"+
		"\u037b\3\2\2\2\u0379\u0377\3\2\2\2\u0379\u037a\3\2\2\2\u037a\u037d\3\2"+
		"\2\2\u037b\u0379\3\2\2\2\u037c\u037e\5:\36\2\u037d\u037c\3\2\2\2\u037d"+
		"\u037e\3\2\2\2\u037e\u037f\3\2\2\2\u037f\u0380\7P\2\2\u0380\u0382\5F$"+
		"\2\u0381\u0383\7\7\2\2\u0382\u0381\3\2\2\2\u0383\u0384\3\2\2\2\u0384\u0382"+
		"\3\2\2\2\u0384\u0385\3\2\2\2\u0385\u0389\3\2\2\2\u0386\u0388\t\2\2\2\u0387"+
		"\u0386\3\2\2\2\u0388\u038b\3\2\2\2\u0389\u0387\3\2\2\2\u0389\u038a\3\2"+
		"\2\2\u038a\u0402\3\2\2\2\u038b\u0389\3\2\2\2\u038c\u038e\7\3\2\2\u038d"+
		"\u038c\3\2\2\2\u038e\u0391\3\2\2\2\u038f\u038d\3\2\2\2\u038f\u0390\3\2"+
		"\2\2\u0390\u0393\3\2\2\2\u0391\u038f\3\2\2\2\u0392\u0394\5:\36\2\u0393"+
		"\u0392\3\2\2\2\u0393\u0394\3\2\2\2\u0394\u0396\3\2\2\2\u0395\u0397\5<"+
		"\37\2\u0396\u0395\3\2\2\2\u0396\u0397\3\2\2\2\u0397\u0398\3\2\2\2\u0398"+
		"\u0399\5> \2\u0399\u039a\5@!\2\u039a\u039b\7#\2\2\u039b\u039c\5@!\2\u039c"+
		"\u039d\7#\2\2\u039d\u039f\5@!\2\u039e\u03a0\5D#\2\u039f\u039e\3\2\2\2"+
		"\u039f\u03a0\3\2\2\2\u03a0\u03a2\3\2\2\2\u03a1\u03a3\7\7\2\2\u03a2\u03a1"+
		"\3\2\2\2\u03a3\u03a4\3\2\2\2\u03a4\u03a2\3\2\2\2\u03a4\u03a5\3\2\2\2\u03a5"+
		"\u03a9\3\2\2\2\u03a6\u03a8\t\2\2\2\u03a7\u03a6\3\2\2\2\u03a8\u03ab\3\2"+
		"\2\2\u03a9\u03a7\3\2\2\2\u03a9\u03aa\3\2\2\2\u03aa\u0402\3\2\2\2\u03ab"+
		"\u03a9\3\2\2\2\u03ac\u03ae\7\3\2\2\u03ad\u03ac\3\2\2\2\u03ae\u03b1\3\2"+
		"\2\2\u03af\u03ad\3\2\2\2\u03af\u03b0\3\2\2\2\u03b0\u03b3\3\2\2\2\u03b1"+
		"\u03af\3\2\2\2\u03b2\u03b4\5:\36\2\u03b3\u03b2\3\2\2\2\u03b3\u03b4\3\2"+
		"\2\2\u03b4\u03b6\3\2\2\2\u03b5\u03b7\5<\37\2\u03b6\u03b5\3\2\2\2\u03b6"+
		"\u03b7\3\2\2\2\u03b7\u03b8\3\2\2\2\u03b8\u03b9\5> \2\u03b9\u03ba\5@!\2"+
		"\u03ba\u03bb\7#\2\2\u03bb\u03bd\5@!\2\u03bc\u03be\5D#\2\u03bd\u03bc\3"+
		"\2\2\2\u03bd\u03be\3\2\2\2\u03be\u03c0\3\2\2\2\u03bf\u03c1\7\7\2\2\u03c0"+
		"\u03bf\3\2\2\2\u03c1\u03c2\3\2\2\2\u03c2\u03c0\3\2\2\2\u03c2\u03c3\3\2"+
		"\2\2\u03c3\u03c7\3\2\2\2\u03c4\u03c6\t\2\2\2\u03c5\u03c4\3\2\2\2\u03c6"+
		"\u03c9\3\2\2\2\u03c7\u03c5\3\2\2\2\u03c7\u03c8\3\2\2\2\u03c8\u0402\3\2"+
		"\2\2\u03c9\u03c7\3\2\2\2\u03ca\u03cc\7\3\2\2\u03cb\u03ca\3\2\2\2\u03cc"+
		"\u03cf\3\2\2\2\u03cd\u03cb\3\2\2\2\u03cd\u03ce\3\2\2\2\u03ce\u03d1\3\2"+
		"\2\2\u03cf\u03cd\3\2\2\2\u03d0\u03d2\5:\36\2\u03d1\u03d0\3\2\2\2\u03d1"+
		"\u03d2\3\2\2\2\u03d2\u03d4\3\2\2\2\u03d3\u03d5\5<\37\2\u03d4\u03d3\3\2"+
		"\2\2\u03d4\u03d5\3\2\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d7\5> \2\u03d7\u03d9"+
		"\5@!\2\u03d8\u03da\5D#\2\u03d9\u03d8\3\2\2\2\u03d9\u03da\3\2\2\2\u03da"+
		"\u03dc\3\2\2\2\u03db\u03dd\7\7\2\2\u03dc\u03db\3\2\2\2\u03dd\u03de\3\2"+
		"\2\2\u03de\u03dc\3\2\2\2\u03de\u03df\3\2\2\2\u03df\u03e3\3\2\2\2\u03e0"+
		"\u03e2\t\2\2\2\u03e1\u03e0\3\2\2\2\u03e2\u03e5\3\2\2\2\u03e3\u03e1\3\2"+
		"\2\2\u03e3\u03e4\3\2\2\2\u03e4\u0402\3\2\2\2\u03e5\u03e3\3\2\2\2\u03e6"+
		"\u03e8\7\3\2\2\u03e7\u03e6\3\2\2\2\u03e8\u03eb\3\2\2\2\u03e9\u03e7\3\2"+
		"\2\2\u03e9\u03ea\3\2\2\2\u03ea\u03ed\3\2\2\2\u03eb\u03e9\3\2\2\2\u03ec"+
		"\u03ee\5:\36\2\u03ed\u03ec\3\2\2\2\u03ed\u03ee\3\2\2\2\u03ee\u03f0\3\2"+
		"\2\2\u03ef\u03f1\5<\37\2\u03f0\u03ef\3\2\2\2\u03f0\u03f1\3\2\2\2\u03f1"+
		"\u03f2\3\2\2\2\u03f2\u03f4\5> \2\u03f3\u03f5\5D#\2\u03f4\u03f3\3\2\2\2"+
		"\u03f4\u03f5\3\2\2\2\u03f5\u03f7\3\2\2\2\u03f6\u03f8\7\7\2\2\u03f7\u03f6"+
		"\3\2\2\2\u03f8\u03f9\3\2\2\2\u03f9\u03f7\3\2\2\2\u03f9\u03fa\3\2\2\2\u03fa"+
		"\u03fe\3\2\2\2\u03fb\u03fd\t\2\2\2\u03fc\u03fb\3\2\2\2\u03fd\u0400\3\2"+
		"\2\2\u03fe\u03fc\3\2\2\2\u03fe\u03ff\3\2\2\2\u03ff\u0402\3\2\2\2\u0400"+
		"\u03fe\3\2\2\2\u0401\u033a\3\2\2\2\u0401\u0343\3\2\2\2\u0401\u035c\3\2"+
		"\2\2\u0401\u0379\3\2\2\2\u0401\u038f\3\2\2\2\u0401\u03af\3\2\2\2\u0401"+
		"\u03cd\3\2\2\2\u0401\u03e9\3\2\2\2\u04029\3\2\2\2\u0403\u0405\6\36\17"+
		"\2\u0404\u0406\7\"\2\2\u0405\u0404\3\2\2\2\u0405\u0406\3\2\2\2\u0406\u0407"+
		"\3\2\2\2\u0407\u0408\7U\2\2\u0408;\3\2\2\2\u0409\u040a\6\37\20\2\u040a"+
		"\u040b\7S\2\2\u040b=\3\2\2\2\u040c\u040d\6 \21\2\u040d\u040e\t\20\2\2"+
		"\u040e?\3\2\2\2\u040f\u0411\5B\"\2\u0410\u040f\3\2\2\2\u0410\u0411\3\2"+
		"\2\2\u0411\u0412\3\2\2\2\u0412\u0413\5H%\2\u0413A\3\2\2\2\u0414\u0416"+
		"\t\21\2\2\u0415\u0417\7$\2\2\u0416\u0415\3\2\2\2\u0416\u0417\3\2\2\2\u0417"+
		"C\3\2\2\2\u0418\u0419\7T\2\2\u0419E\3\2\2\2\u041a\u041f\5H%\2\u041b\u041c"+
		"\7-\2\2\u041c\u041d\5H%\2\u041d\u041e\7.\2\2\u041e\u0420\3\2\2\2\u041f"+
		"\u041b\3\2\2\2\u041f\u0420\3\2\2\2\u0420G\3\2\2\2\u0421\u0422\b%\1\2\u0422"+
		"\u0423\t\22\2\2\u0423\u0432\5H%\22\u0424\u0425\7I\2\2\u0425\u0426\7/\2"+
		"\2\u0426\u0427\5H%\2\u0427\u0428\7\60\2\2\u0428\u0432\3\2\2\2\u0429\u042a"+
		"\7/\2\2\u042a\u042b\5H%\2\u042b\u042c\7\60\2\2\u042c\u0432\3\2\2\2\u042d"+
		"\u042f\7\35\2\2\u042e\u042d\3\2\2\2\u042e\u042f\3\2\2\2\u042f\u0430\3"+
		"\2\2\2\u0430\u0432\5J&\2\u0431\u0421\3\2\2\2\u0431\u0424\3\2\2\2\u0431"+
		"\u0429\3\2\2\2\u0431\u042e\3\2\2\2\u0432\u045c\3\2\2\2\u0433\u0434\f\21"+
		"\2\2\u0434\u0435\t\6\2\2\u0435\u045b\5H%\22\u0436\u0437\f\20\2\2\u0437"+
		"\u0438\7\32\2\2\u0438\u045b\5H%\21\u0439\u043a\f\17\2\2\u043a\u043b\7"+
		"\34\2\2\u043b\u045b\5H%\20\u043c\u043d\f\16\2\2\u043d\u043e\7\33\2\2\u043e"+
		"\u045b\5H%\17\u043f\u0440\f\r\2\2\u0440\u0441\t\7\2\2\u0441\u045b\5H%"+
		"\16\u0442\u0443\f\f\2\2\u0443\u0444\t\b\2\2\u0444\u045b\5H%\r\u0445\u0446"+
		"\f\13\2\2\u0446\u0447\t\t\2\2\u0447\u045b\5H%\f\u0448\u0449\f\n\2\2\u0449"+
		"\u044a\t\n\2\2\u044a\u045b\5H%\13\u044b\u044c\f\t\2\2\u044c\u044d\t\13"+
		"\2\2\u044d\u045b\5H%\n\u044e\u044f\f\b\2\2\u044f\u0450\t\f\2\2\u0450\u045b"+
		"\5H%\t\u0451\u0452\f\7\2\2\u0452\u0453\t\r\2\2\u0453\u045b\5H%\b\u0454"+
		"\u0455\f\6\2\2\u0455\u0456\7)\2\2\u0456\u0457\5H%\2\u0457\u0458\7*\2\2"+
		"\u0458\u0459\5H%\7\u0459\u045b\3\2\2\2\u045a\u0433\3\2\2\2\u045a\u0436"+
		"\3\2\2\2\u045a\u0439\3\2\2\2\u045a\u043c\3\2\2\2\u045a\u043f\3\2\2\2\u045a"+
		"\u0442\3\2\2\2\u045a\u0445\3\2\2\2\u045a\u0448\3\2\2\2\u045a\u044b\3\2"+
		"\2\2\u045a\u044e\3\2\2\2\u045a\u0451\3\2\2\2\u045a\u0454\3\2\2\2\u045b"+
		"\u045e\3\2\2\2\u045c\u045a\3\2\2\2\u045c\u045d\3\2\2\2\u045dI\3\2\2\2"+
		"\u045e\u045c\3\2\2\2\u045f\u0489\7\r\2\2\u0460\u0489\7\f\2\2\u0461\u0489"+
		"\7\13\2\2\u0462\u0489\7\n\2\2\u0463\u0489\7\t\2\2\u0464\u0489\7\37\2\2"+
		"\u0465\u046a\7U\2\2\u0466\u0467\7-\2\2\u0467\u0468\5H%\2\u0468\u0469\7"+
		".\2\2\u0469\u046b\3\2\2\2\u046a\u0466\3\2\2\2\u046a\u046b\3\2\2\2\u046b"+
		"\u0489\3\2\2\2\u046c\u046d\7U\2\2\u046d\u0472\t\5\2\2\u046e\u046f\7-\2"+
		"\2\u046f\u0470\5H%\2\u0470\u0471\7.\2\2\u0471\u0473\3\2\2\2\u0472\u046e"+
		"\3\2\2\2\u0472\u0473\3\2\2\2\u0473\u0489\3\2\2\2\u0474\u0479\7U\2\2\u0475"+
		"\u0476\7-\2\2\u0476\u0477\5H%\2\u0477\u0478\7.\2\2\u0478\u047a\3\2\2\2"+
		"\u0479\u0475\3\2\2\2\u0479\u047a\3\2\2\2\u047a\u047b\3\2\2\2\u047b\u0489"+
		"\t\5\2\2\u047c\u047d\t\5\2\2\u047d\u0482\7U\2\2\u047e\u047f\7-\2\2\u047f"+
		"\u0480\5H%\2\u0480\u0481\7.\2\2\u0481\u0483\3\2\2\2\u0482\u047e\3\2\2"+
		"\2\u0482\u0483\3\2\2\2\u0483\u0489\3\2\2\2\u0484\u0486\7\"\2\2\u0485\u0484"+
		"\3\2\2\2\u0485\u0486\3\2\2\2\u0486\u0487\3\2\2\2\u0487\u0489\7U\2\2\u0488"+
		"\u045f\3\2\2\2\u0488\u0460\3\2\2\2\u0488\u0461\3\2\2\2\u0488\u0462\3\2"+
		"\2\2\u0488\u0463\3\2\2\2\u0488\u0464\3\2\2\2\u0488\u0465\3\2\2\2\u0488"+
		"\u046c\3\2\2\2\u0488\u0474\3\2\2\2\u0488\u047c\3\2\2\2\u0488\u0485\3\2"+
		"\2\2\u0489K\3\2\2\2\u00a8OWYafnwz|\u0082\u008b\u0091\u009a\u009f\u00a6"+
		"\u00ac\u00b4\u00b9\u00bf\u00c4\u00ca\u00d0\u00da\u00e2\u00e7\u00ec\u00f4"+
		"\u00f9\u00ff\u0103\u010a\u010f\u0115\u011a\u011e\u0123\u0129\u0133\u013b"+
		"\u0143\u0147\u014a\u0151\u0157\u015c\u015f\u0164\u0168\u016f\u0173\u017d"+
		"\u0181\u0188\u0192\u01a4\u01a8\u01ad\u01b3\u01bb\u01c1\u01ca\u01d0\u01d6"+
		"\u01df\u01e9\u01eb\u01f0\u01f6\u0201\u0207\u020b\u0213\u0219\u021d\u021f"+
		"\u0226\u022c\u0233\u023b\u0241\u0248\u024b\u0251\u0257\u0260\u0266\u0270"+
		"\u0276\u027b\u0282\u0287\u0289\u0291\u0295\u029a\u02a1\u02a5\u02a9\u02b1"+
		"\u02b7\u02bb\u02c2\u02c8\u02cc\u02ce\u02d6\u02f6\u0322\u0324\u032c\u0331"+
		"\u0337\u033e\u0343\u034a\u034c\u0351\u0356\u035c\u0360\u0368\u036e\u0373"+
		"\u0379\u037d\u0384\u0389\u038f\u0393\u0396\u039f\u03a4\u03a9\u03af\u03b3"+
		"\u03b6\u03bd\u03c2\u03c7\u03cd\u03d1\u03d4\u03d9\u03de\u03e3\u03e9\u03ed"+
		"\u03f0\u03f4\u03f9\u03fe\u0401\u0405\u0410\u0416\u041f\u042e\u0431\u045a"+
		"\u045c\u046a\u0472\u0479\u0482\u0485\u0488";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}