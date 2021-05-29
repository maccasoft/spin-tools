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
		QUAD=8, BIN=9, HEX=10, NUMBER=11, LONG_LITERAL=12, LONG_LITERAL_ABS=13, 
		LITERAL_ABS=14, LITERAL=15, LOGICAL_AND=16, LOGICAL_OR=17, LOGICAL_XOR=18, 
		LEFT_SHIFT=19, RIGHT_SHIFT=20, PLUS_PLUS=21, MINUS_MINUS=22, ASSIGN=23, 
		ELLIPSIS=24, EQUALS=25, NOT_EQUALS=26, BIN_AND=27, BIN_OR=28, BIN_XOR=29, 
		AT=30, DOLLAR=31, PERCENT=32, EQUAL=33, DOT=34, COMMA=35, BACKSLASH=36, 
		PLUS=37, MINUS=38, STAR=39, DIV=40, QUESTION=41, COLON=42, TILDE=43, UNDERSCORE=44, 
		OPEN_BRACKET=45, CLOSE_BRACKET=46, OPEN_PAREN=47, CLOSE_PAREN=48, CON_START=49, 
		VAR_START=50, OBJ_START=51, PUB_START=52, PRI_START=53, DAT_START=54, 
		REPEAT=55, FROM=56, TO=57, STEP=58, WHILE=59, UNTIL=60, ELSEIFNOT=61, 
		ELSEIF=62, ELSE=63, IFNOT=64, IF=65, CASE=66, OTHER=67, ADDPINS=68, ADDBITS=69, 
		FRAC=70, ENCOD=71, DECOD=72, FUNCTIONS=73, AND=74, NOT=75, XOR=76, OR=77, 
		ORG=78, ORGH=79, ORGF=80, FIT=81, RES=82, ALIGN=83, TYPE=84, CONDITION=85, 
		MODIFIER=86, IDENTIFIER=87;
	public static final int
		RULE_prog = 0, RULE_constantsSection = 1, RULE_constantAssign = 2, RULE_constantEnum = 3, 
		RULE_constantEnumName = 4, RULE_objectsSection = 5, RULE_object = 6, RULE_variablesSection = 7, 
		RULE_variable = 8, RULE_method = 9, RULE_parameters = 10, RULE_result = 11, 
		RULE_localvars = 12, RULE_localvar = 13, RULE_statement = 14, RULE_function = 15, 
		RULE_functionArgument = 16, RULE_identifier = 17, RULE_repeatLoop = 18, 
		RULE_conditional = 19, RULE_elseConditional = 20, RULE_caseConditional = 21, 
		RULE_caseConditionalMatch = 22, RULE_caseConditionalOther = 23, RULE_spinExpression = 24, 
		RULE_expressionAtom = 25, RULE_data = 26, RULE_dataLine = 27, RULE_label = 28, 
		RULE_opcode = 29, RULE_argument = 30, RULE_dataValue = 31, RULE_constantExpression = 32, 
		RULE_atom = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objectsSection", "object", "variablesSection", "variable", "method", 
			"parameters", "result", "localvars", "localvar", "statement", "function", 
			"functionArgument", "identifier", "repeatLoop", "conditional", "elseConditional", 
			"caseConditional", "caseConditionalMatch", "caseConditionalOther", "spinExpression", 
			"expressionAtom", "data", "dataLine", "label", "opcode", "argument", 
			"dataValue", "constantExpression", "atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'##'", "'##\\'", "'#\\'", "'#'", "'&&'", "'||'", "'^^'", "'<<'", "'>>'", 
			"'++'", "'--'", null, "'..'", "'=='", "'<>'", "'&'", "'|'", "'^'", "'@'", 
			"'$'", "'%'", "'='", "'.'", "','", "'\\'", "'+'", "'-'", "'*'", "'/'", 
			"'?'", "':'", "'~'", "'_'", "'['", "']'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LONG_LITERAL", "LONG_LITERAL_ABS", "LITERAL_ABS", 
			"LITERAL", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", "LEFT_SHIFT", 
			"RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", "ASSIGN", "ELLIPSIS", "EQUALS", 
			"NOT_EQUALS", "BIN_AND", "BIN_OR", "BIN_XOR", "AT", "DOLLAR", "PERCENT", 
			"EQUAL", "DOT", "COMMA", "BACKSLASH", "PLUS", "MINUS", "STAR", "DIV", 
			"QUESTION", "COLON", "TILDE", "UNDERSCORE", "OPEN_BRACKET", "CLOSE_BRACKET", 
			"OPEN_PAREN", "CLOSE_PAREN", "CON_START", "VAR_START", "OBJ_START", "PUB_START", 
			"PRI_START", "DAT_START", "REPEAT", "FROM", "TO", "STEP", "WHILE", "UNTIL", 
			"ELSEIFNOT", "ELSEIF", "ELSE", "IFNOT", "IF", "CASE", "OTHER", "ADDPINS", 
			"ADDBITS", "FRAC", "ENCOD", "DECOD", "FUNCTIONS", "AND", "NOT", "XOR", 
			"OR", "ORG", "ORGH", "ORGF", "FIT", "RES", "ALIGN", "TYPE", "CONDITION", 
			"MODIFIER", "IDENTIFIER"
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
			while (_la==INDENT || _la==LITERAL || _la==IDENTIFIER) {
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
		public ConstantExpressionContext exp;
		public TerminalNode EQUAL() { return getToken(Spin2Parser.EQUAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
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
			((ConstantAssignContext)_localctx).exp = constantExpression(0);
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
		public ConstantExpressionContext start;
		public ConstantExpressionContext step;
		public TerminalNode LITERAL() { return getToken(Spin2Parser.LITERAL, 0); }
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
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
			match(LITERAL);
			setState(141);
			((ConstantEnumContext)_localctx).start = constantExpression(0);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(142);
				match(OPEN_BRACKET);
				setState(143);
				((ConstantEnumContext)_localctx).step = constantExpression(0);
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
		public ConstantExpressionContext multiplier;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
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
				((ConstantEnumNameContext)_localctx).multiplier = constantExpression(0);
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
		public ConstantExpressionContext count;
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
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
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
				((ObjectContext)_localctx).count = constantExpression(0);
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
		public ConstantExpressionContext size;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
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
				((VariableContext)_localctx).size = constantExpression(0);
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
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
		public ConstantExpressionContext count;
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode ALIGN() { return getToken(Spin2Parser.ALIGN, 0); }
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
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
				((LocalvarContext)_localctx).count = constantExpression(0);
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
			setState(340);
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
				setState(331);
				spinExpression(0);
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
			case REPEAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(337);
				repeatLoop();
				}
				break;
			case IFNOT:
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(338);
				conditional();
				}
				break;
			case CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(339);
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
			setState(377);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(343);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(342);
					match(BACKSLASH);
					}
				}

				setState(345);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(346);
				match(OPEN_PAREN);
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(347);
					functionArgument();
					}
				}

				setState(350);
				match(CLOSE_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(352);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(351);
					match(BACKSLASH);
					}
				}

				setState(354);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(355);
				match(DOT);
				setState(356);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(357);
				match(OPEN_PAREN);
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(358);
					functionArgument();
					}
				}

				setState(361);
				match(CLOSE_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(362);
					match(BACKSLASH);
					}
				}

				setState(365);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(366);
				match(OPEN_BRACKET);
				setState(367);
				((FunctionContext)_localctx).index = spinExpression(0);
				setState(368);
				match(CLOSE_BRACKET);
				setState(369);
				match(DOT);
				setState(370);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(371);
				match(OPEN_PAREN);
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(372);
					functionArgument();
					}
				}

				setState(375);
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
			setState(379);
			spinExpression(0);
			setState(384);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(380);
				match(COMMA);
				setState(381);
				spinExpression(0);
				}
				}
				setState(386);
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
		public Token name;
		public TerminalNode AT() { return getToken(Spin2Parser.AT, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public TerminalNode DOT() { return getToken(Spin2Parser.DOT, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
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
			setState(411);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(387);
				match(AT);
				setState(388);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(389);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(390);
				match(DOT);
				setState(391);
				match(IDENTIFIER);
				setState(392);
				match(OPEN_BRACKET);
				setState(393);
				spinExpression(0);
				setState(394);
				match(CLOSE_BRACKET);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(396);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(397);
				match(DOT);
				setState(398);
				match(OPEN_BRACKET);
				setState(399);
				spinExpression(0);
				setState(400);
				match(CLOSE_BRACKET);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(402);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(403);
				match(DOT);
				setState(404);
				match(IDENTIFIER);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(405);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(406);
				match(OPEN_BRACKET);
				setState(407);
				spinExpression(0);
				setState(408);
				match(CLOSE_BRACKET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(410);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
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
		public TerminalNode INDENT() { return getToken(Spin2Parser.INDENT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode DEDENT() { return getToken(Spin2Parser.DEDENT, 0); }
		public TerminalNode WHILE() { return getToken(Spin2Parser.WHILE, 0); }
		public TerminalNode UNTIL() { return getToken(Spin2Parser.UNTIL, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
			int _alt;
			setState(545);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(413);
				match(REPEAT);
				setState(415);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(414);
					spinExpression(0);
					}
				}

				setState(418); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(417);
					match(NL);
					}
					}
					setState(420); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(432);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(422);
					match(INDENT);
					setState(426);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(423);
							statement();
							}
							} 
						}
						setState(428);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
					}
					setState(430);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
					case 1:
						{
						setState(429);
						match(DEDENT);
						}
						break;
					}
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(434);
				match(REPEAT);
				setState(436); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(435);
					match(NL);
					}
					}
					setState(438); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(440);
				match(INDENT);
				setState(444);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(441);
					statement();
					}
					}
					setState(446);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(447);
				match(DEDENT);
				setState(448);
				match(WHILE);
				setState(449);
				spinExpression(0);
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
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(455);
				match(REPEAT);
				setState(457); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(456);
					match(NL);
					}
					}
					setState(459); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(461);
				match(INDENT);
				setState(465);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(462);
					statement();
					}
					}
					setState(467);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(468);
				match(DEDENT);
				setState(469);
				match(UNTIL);
				setState(470);
				spinExpression(0);
				setState(472); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(471);
					match(NL);
					}
					}
					setState(474); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(476);
				match(REPEAT);
				setState(477);
				identifier();
				setState(478);
				match(FROM);
				setState(479);
				spinExpression(0);
				setState(486);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(480);
					match(TO);
					setState(481);
					spinExpression(0);
					setState(484);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(482);
						match(STEP);
						setState(483);
						spinExpression(0);
						}
					}

					}
				}

				setState(489); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(488);
					match(NL);
					}
					}
					setState(491); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(503);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(493);
					match(INDENT);
					setState(497);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(494);
							statement();
							}
							} 
						}
						setState(499);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
					}
					setState(501);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
					case 1:
						{
						setState(500);
						match(DEDENT);
						}
						break;
					}
					}
				}

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
				setState(523);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(513);
					match(INDENT);
					setState(517);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(514);
							statement();
							}
							} 
						}
						setState(519);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
					}
					setState(521);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
					case 1:
						{
						setState(520);
						match(DEDENT);
						}
						break;
					}
					}
				}

				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(525);
				match(REPEAT);
				setState(526);
				match(UNTIL);
				setState(527);
				spinExpression(0);
				setState(529); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(528);
					match(NL);
					}
					}
					setState(531); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(543);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(533);
					match(INDENT);
					setState(537);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(534);
							statement();
							}
							} 
						}
						setState(539);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
					}
					setState(541);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
					case 1:
						{
						setState(540);
						match(DEDENT);
						}
						break;
					}
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
		public TerminalNode NOT() { return getToken(Spin2Parser.NOT, 0); }
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
			setState(611);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(547);
				match(IF);
				setState(548);
				match(NOT);
				setState(549);
				spinExpression(0);
				setState(551); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(550);
					match(NL);
					}
					}
					setState(553); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(555);
				match(INDENT);
				setState(559);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(556);
					statement();
					}
					}
					setState(561);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(562);
				match(DEDENT);
				setState(566);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(563);
					elseConditional();
					}
					}
					setState(568);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(569);
				match(IFNOT);
				setState(570);
				spinExpression(0);
				setState(572); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(571);
					match(NL);
					}
					}
					setState(574); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(576);
				match(INDENT);
				setState(580);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(577);
					statement();
					}
					}
					setState(582);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(583);
				match(DEDENT);
				setState(587);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(584);
					elseConditional();
					}
					}
					setState(589);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(590);
				match(IF);
				setState(591);
				spinExpression(0);
				setState(593); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(592);
					match(NL);
					}
					}
					setState(595); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(597);
				match(INDENT);
				setState(601);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(598);
					statement();
					}
					}
					setState(603);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(604);
				match(DEDENT);
				setState(608);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(605);
					elseConditional();
					}
					}
					setState(610);
					_errHandler.sync(this);
					_la = _input.LA(1);
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
		public TerminalNode NOT() { return getToken(Spin2Parser.NOT, 0); }
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
			setState(676);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(613);
				match(ELSE);
				setState(615); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(614);
					match(NL);
					}
					}
					setState(617); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(619);
				match(INDENT);
				setState(623);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(620);
					statement();
					}
					}
					setState(625);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(626);
				match(DEDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(627);
				match(ELSEIF);
				setState(628);
				match(NOT);
				setState(629);
				spinExpression(0);
				setState(631); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(630);
					match(NL);
					}
					}
					setState(633); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(635);
				match(INDENT);
				setState(639);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(636);
					statement();
					}
					}
					setState(641);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(642);
				match(DEDENT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(644);
				match(ELSEIF);
				setState(645);
				spinExpression(0);
				setState(647); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(646);
					match(NL);
					}
					}
					setState(649); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(651);
				match(INDENT);
				setState(655);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(652);
					statement();
					}
					}
					setState(657);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(658);
				match(DEDENT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(660);
				match(ELSEIFNOT);
				setState(661);
				spinExpression(0);
				setState(663); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(662);
					match(NL);
					}
					}
					setState(665); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(667);
				match(INDENT);
				setState(671);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(668);
					statement();
					}
					}
					setState(673);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(674);
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
			setState(678);
			match(CASE);
			setState(679);
			spinExpression(0);
			setState(681); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(680);
				match(NL);
				}
				}
				setState(683); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(685);
			match(INDENT);
			setState(690);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (OTHER - 67)) | (1L << (ENCOD - 67)) | (1L << (DECOD - 67)) | (1L << (FUNCTIONS - 67)) | (1L << (TYPE - 67)) | (1L << (IDENTIFIER - 67)))) != 0)) {
				{
				setState(688);
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
					setState(686);
					caseConditionalMatch();
					}
					break;
				case OTHER:
					{
					setState(687);
					caseConditionalOther();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(692);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(693);
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
			setState(695);
			spinExpression(0);
			setState(698);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(696);
				match(ELLIPSIS);
				setState(697);
				spinExpression(0);
				}
			}

			setState(700);
			match(COLON);
			setState(702);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
				{
				setState(701);
				spinExpression(0);
				}
			}

			setState(705); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(704);
				match(NL);
				}
				}
				setState(707); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(722);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INDENT) {
				{
				setState(709);
				match(INDENT);
				setState(718);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					{
					setState(710);
					spinExpression(0);
					setState(712); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(711);
						match(NL);
						}
						}
						setState(714); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==NL );
					}
					}
					setState(720);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(721);
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
			setState(759);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(724);
				match(OTHER);
				setState(725);
				match(COLON);
				setState(726);
				spinExpression(0);
				setState(728); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(727);
					match(NL);
					}
					}
					setState(730); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(740);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(732);
					match(INDENT);
					setState(736);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(733);
						statement();
						}
						}
						setState(738);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(739);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(742);
				match(OTHER);
				setState(743);
				match(COLON);
				setState(745); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(744);
					match(NL);
					}
					}
					setState(747); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(757);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(749);
					match(INDENT);
					setState(753);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(750);
						statement();
						}
						}
						setState(755);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(756);
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
		public ExpressionAtomContext expressionAtom() {
			return getRuleContext(ExpressionAtomContext.class,0);
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
			setState(792);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
			case 1:
				{
				setState(762);
				identifier();
				setState(767);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(763);
					match(COMMA);
					setState(764);
					match(IDENTIFIER);
					}
					}
					setState(769);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(770);
				match(ASSIGN);
				setState(771);
				spinExpression(19);
				}
				break;
			case 2:
				{
				setState(773);
				match(TYPE);
				setState(774);
				match(OPEN_BRACKET);
				setState(775);
				spinExpression(0);
				setState(776);
				match(CLOSE_BRACKET);
				setState(777);
				match(ASSIGN);
				setState(778);
				spinExpression(18);
				}
				break;
			case 3:
				{
				setState(780);
				((SpinExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 37)) & ~0x3f) == 0 && ((1L << (_la - 37)) & ((1L << (PLUS - 37)) | (1L << (MINUS - 37)) | (1L << (TILDE - 37)) | (1L << (ENCOD - 37)) | (1L << (DECOD - 37)))) != 0)) ) {
					((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(781);
				((SpinExpressionContext)_localctx).exp = spinExpression(17);
				}
				break;
			case 4:
				{
				setState(782);
				((SpinExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(783);
				match(OPEN_PAREN);
				setState(784);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(785);
				match(CLOSE_PAREN);
				}
				break;
			case 5:
				{
				setState(787);
				match(OPEN_PAREN);
				setState(788);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(789);
				match(CLOSE_PAREN);
				}
				break;
			case 6:
				{
				setState(791);
				expressionAtom();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(838);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(836);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
					case 1:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(794);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(795);
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
						setState(796);
						((SpinExpressionContext)_localctx).right = spinExpression(17);
						}
						break;
					case 2:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(797);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(798);
						((SpinExpressionContext)_localctx).operator = match(BIN_AND);
						setState(799);
						((SpinExpressionContext)_localctx).right = spinExpression(16);
						}
						break;
					case 3:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(800);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(801);
						((SpinExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(802);
						((SpinExpressionContext)_localctx).right = spinExpression(15);
						}
						break;
					case 4:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(803);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(804);
						((SpinExpressionContext)_localctx).operator = match(BIN_OR);
						setState(805);
						((SpinExpressionContext)_localctx).right = spinExpression(14);
						}
						break;
					case 5:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(806);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(807);
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
						setState(808);
						((SpinExpressionContext)_localctx).right = spinExpression(13);
						}
						break;
					case 6:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(809);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(810);
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
						setState(811);
						((SpinExpressionContext)_localctx).right = spinExpression(12);
						}
						break;
					case 7:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(812);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(813);
						((SpinExpressionContext)_localctx).operator = match(FRAC);
						setState(814);
						((SpinExpressionContext)_localctx).right = spinExpression(11);
						}
						break;
					case 8:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(815);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(816);
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
						setState(817);
						((SpinExpressionContext)_localctx).right = spinExpression(10);
						}
						break;
					case 9:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(818);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(819);
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
						setState(820);
						((SpinExpressionContext)_localctx).right = spinExpression(9);
						}
						break;
					case 10:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(821);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(822);
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
						setState(823);
						((SpinExpressionContext)_localctx).right = spinExpression(8);
						}
						break;
					case 11:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(824);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(825);
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
						setState(826);
						((SpinExpressionContext)_localctx).right = spinExpression(7);
						}
						break;
					case 12:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(827);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(828);
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
						setState(829);
						((SpinExpressionContext)_localctx).right = spinExpression(6);
						}
						break;
					case 13:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(830);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(831);
						((SpinExpressionContext)_localctx).operator = match(QUESTION);
						setState(832);
						((SpinExpressionContext)_localctx).middle = spinExpression(0);
						setState(833);
						((SpinExpressionContext)_localctx).operator = match(COLON);
						setState(834);
						((SpinExpressionContext)_localctx).right = spinExpression(5);
						}
						break;
					}
					} 
				}
				setState(840);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
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

	public static class ExpressionAtomContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(Spin2Parser.NUMBER, 0); }
		public TerminalNode HEX() { return getToken(Spin2Parser.HEX, 0); }
		public TerminalNode BIN() { return getToken(Spin2Parser.BIN, 0); }
		public TerminalNode QUAD() { return getToken(Spin2Parser.QUAD, 0); }
		public TerminalNode STRING() { return getToken(Spin2Parser.STRING, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode PLUS_PLUS() { return getToken(Spin2Parser.PLUS_PLUS, 0); }
		public TerminalNode MINUS_MINUS() { return getToken(Spin2Parser.MINUS_MINUS, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExpressionAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitExpressionAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitExpressionAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionAtomContext expressionAtom() throws RecognitionException {
		ExpressionAtomContext _localctx = new ExpressionAtomContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_expressionAtom);
		int _la;
		try {
			setState(853);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(841);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(842);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(843);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(844);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(845);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(846);
				identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(847);
				identifier();
				setState(848);
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
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(850);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(851);
				identifier();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(852);
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
			setState(856); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(855);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(858); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(863);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(860);
					match(NL);
					}
					} 
				}
				setState(865);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			}
			setState(869);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(866);
					dataLine();
					}
					} 
				}
				setState(871);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
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
		public Token condition;
		public Token modifier;
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public TerminalNode ORG() { return getToken(Spin2Parser.ORG, 0); }
		public TerminalNode ORGH() { return getToken(Spin2Parser.ORGH, 0); }
		public TerminalNode ORGF() { return getToken(Spin2Parser.ORGF, 0); }
		public TerminalNode ALIGN() { return getToken(Spin2Parser.ALIGN, 0); }
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public TerminalNode FIT() { return getToken(Spin2Parser.FIT, 0); }
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
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
		public TerminalNode CONDITION() { return getToken(Spin2Parser.CONDITION, 0); }
		public TerminalNode MODIFIER() { return getToken(Spin2Parser.MODIFIER, 0); }
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
			setState(1095);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(872);
				label();
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
					_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(881);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(878);
					match(INDENT);
					}
					}
					setState(883);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(884);
				((DataLineContext)_localctx).directive = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (ORG - 78)) | (1L << (ORGH - 78)) | (1L << (ORGF - 78)) | (1L << (ALIGN - 78)))) != 0)) ) {
					((DataLineContext)_localctx).directive = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(890);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(885);
					constantExpression(0);
					setState(888);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(886);
						match(COMMA);
						setState(887);
						constantExpression(0);
						}
					}

					}
				}

				setState(893); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(892);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(895); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(900);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(897);
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
					setState(902);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(906);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(903);
						match(INDENT);
						}
						} 
					}
					setState(908);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				}
				setState(910);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,130,_ctx) ) {
				case 1:
					{
					setState(909);
					label();
					}
					break;
				}
				setState(912);
				((DataLineContext)_localctx).directive = match(FIT);
				setState(914);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(913);
					argument();
					}
				}

				setState(917); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(916);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(919); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,132,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(924);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(921);
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
					setState(926);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(930);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(927);
						match(INDENT);
						}
						} 
					}
					setState(932);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
				}
				setState(934);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
				case 1:
					{
					setState(933);
					label();
					}
					break;
				}
				setState(936);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(937);
				dataValue();
				setState(942);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(938);
					match(COMMA);
					setState(939);
					dataValue();
					}
					}
					setState(944);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(946); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(945);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(948); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,137,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(953);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(950);
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
					setState(955);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(959);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(956);
						match(INDENT);
						}
						} 
					}
					setState(961);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				}
				setState(963);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,140,_ctx) ) {
				case 1:
					{
					setState(962);
					label();
					}
					break;
				}
				setState(965);
				((DataLineContext)_localctx).directive = match(RES);
				setState(966);
				constantExpression(0);
				setState(968); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(967);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(970); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(975);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(972);
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
					setState(977);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(981);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,143,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(978);
						match(INDENT);
						}
						} 
					}
					setState(983);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,143,_ctx);
				}
				setState(985);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
				case 1:
					{
					setState(984);
					label();
					}
					break;
				}
				setState(988);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,145,_ctx) ) {
				case 1:
					{
					setState(987);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(990);
				opcode();
				setState(991);
				argument();
				setState(992);
				match(COMMA);
				setState(993);
				argument();
				setState(994);
				match(COMMA);
				setState(995);
				argument();
				setState(997);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(996);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1000); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(999);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1002); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,147,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1007);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1004);
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
					setState(1009);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1013);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1010);
						match(INDENT);
						}
						} 
					}
					setState(1015);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
				}
				setState(1017);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
				case 1:
					{
					setState(1016);
					label();
					}
					break;
				}
				setState(1020);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,151,_ctx) ) {
				case 1:
					{
					setState(1019);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1022);
				opcode();
				setState(1023);
				argument();
				setState(1024);
				match(COMMA);
				setState(1025);
				argument();
				setState(1027);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1026);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1030); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1029);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1032); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1037);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1034);
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
					setState(1039);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1043);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1040);
						match(INDENT);
						}
						} 
					}
					setState(1045);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
				}
				setState(1047);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
				case 1:
					{
					setState(1046);
					label();
					}
					break;
				}
				setState(1050);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
				case 1:
					{
					setState(1049);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1052);
				opcode();
				setState(1053);
				argument();
				setState(1055);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1054);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1058); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1057);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1060); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1065);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1062);
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
					setState(1067);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1071);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1068);
						match(INDENT);
						}
						} 
					}
					setState(1073);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
				}
				setState(1075);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
				case 1:
					{
					setState(1074);
					label();
					}
					break;
				}
				setState(1078);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
				case 1:
					{
					setState(1077);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1080);
				opcode();
				setState(1082);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1081);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1085); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1084);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1087); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,165,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1092);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,166,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1089);
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
					setState(1094);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,166,_ctx);
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
			setState(1097);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			{
			setState(1099);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(1098);
				match(DOT);
				}
			}

			setState(1101);
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
		enterRule(_localctx, 58, RULE_opcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1103);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1104);
			_la = _input.LA(1);
			if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
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
		public Token prefix;
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
		}
		public TerminalNode LITERAL() { return getToken(Spin2Parser.LITERAL, 0); }
		public TerminalNode LITERAL_ABS() { return getToken(Spin2Parser.LITERAL_ABS, 0); }
		public TerminalNode LONG_LITERAL() { return getToken(Spin2Parser.LONG_LITERAL, 0); }
		public TerminalNode LONG_LITERAL_ABS() { return getToken(Spin2Parser.LONG_LITERAL_ABS, 0); }
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
		enterRule(_localctx, 60, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL))) != 0)) {
				{
				setState(1106);
				((ArgumentContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL))) != 0)) ) {
					((ArgumentContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(1109);
			constantExpression(0);
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
		public ConstantExpressionContext count;
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
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
			setState(1111);
			constantExpression(0);
			setState(1116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1112);
				match(OPEN_BRACKET);
				setState(1113);
				((DataValueContext)_localctx).count = constantExpression(0);
				setState(1114);
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

	public static class ConstantExpressionContext extends ParserRuleContext {
		public ConstantExpressionContext left;
		public Token operator;
		public ConstantExpressionContext exp;
		public ConstantExpressionContext right;
		public ConstantExpressionContext middle;
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
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
		public ConstantExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstantExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstantExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstantExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantExpressionContext constantExpression() throws RecognitionException {
		return constantExpression(0);
	}

	private ConstantExpressionContext constantExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConstantExpressionContext _localctx = new ConstantExpressionContext(_ctx, _parentState);
		ConstantExpressionContext _prevctx = _localctx;
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_constantExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1134);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1119);
				((ConstantExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TILDE))) != 0)) ) {
					((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1120);
				((ConstantExpressionContext)_localctx).exp = constantExpression(16);
				}
				break;
			case FUNCTIONS:
				{
				setState(1121);
				((ConstantExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1122);
				match(OPEN_PAREN);
				setState(1123);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1124);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1126);
				match(OPEN_PAREN);
				setState(1127);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1128);
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
				setState(1131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1130);
					match(AT);
					}
				}

				setState(1133);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1177);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,174,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1175);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
					case 1:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1136);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1137);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LEFT_SHIFT || _la==RIGHT_SHIFT) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1138);
						((ConstantExpressionContext)_localctx).right = constantExpression(16);
						}
						break;
					case 2:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1139);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1140);
						((ConstantExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1141);
						((ConstantExpressionContext)_localctx).right = constantExpression(15);
						}
						break;
					case 3:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1142);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1143);
						((ConstantExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1144);
						((ConstantExpressionContext)_localctx).right = constantExpression(14);
						}
						break;
					case 4:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1145);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1146);
						((ConstantExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1147);
						((ConstantExpressionContext)_localctx).right = constantExpression(13);
						}
						break;
					case 5:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1148);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1149);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==STAR || _la==DIV) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1150);
						((ConstantExpressionContext)_localctx).right = constantExpression(12);
						}
						break;
					case 6:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1151);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1152);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1153);
						((ConstantExpressionContext)_localctx).right = constantExpression(11);
						}
						break;
					case 7:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1154);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1155);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADDPINS || _la==ADDBITS) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1156);
						((ConstantExpressionContext)_localctx).right = constantExpression(10);
						}
						break;
					case 8:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1157);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1158);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUALS || _la==NOT_EQUALS) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1159);
						((ConstantExpressionContext)_localctx).right = constantExpression(9);
						}
						break;
					case 9:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1160);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1161);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_AND || _la==AND) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1162);
						((ConstantExpressionContext)_localctx).right = constantExpression(8);
						}
						break;
					case 10:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1163);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1164);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_XOR || _la==XOR) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1165);
						((ConstantExpressionContext)_localctx).right = constantExpression(7);
						}
						break;
					case 11:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1166);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1167);
						((ConstantExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_OR || _la==OR) ) {
							((ConstantExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1168);
						((ConstantExpressionContext)_localctx).right = constantExpression(6);
						}
						break;
					case 12:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1169);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1170);
						((ConstantExpressionContext)_localctx).operator = match(QUESTION);
						setState(1171);
						((ConstantExpressionContext)_localctx).middle = constantExpression(0);
						setState(1172);
						((ConstantExpressionContext)_localctx).operator = match(COLON);
						setState(1173);
						((ConstantExpressionContext)_localctx).right = constantExpression(5);
						}
						break;
					}
					} 
				}
				setState(1179);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,174,_ctx);
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
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
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
			setState(1221);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,180,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1180);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1181);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1182);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1183);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1184);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1185);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1186);
				match(IDENTIFIER);
				setState(1191);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,175,_ctx) ) {
				case 1:
					{
					setState(1187);
					match(OPEN_BRACKET);
					setState(1188);
					constantExpression(0);
					setState(1189);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1193);
				match(IDENTIFIER);
				setState(1194);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1199);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,176,_ctx) ) {
				case 1:
					{
					setState(1195);
					match(OPEN_BRACKET);
					setState(1196);
					constantExpression(0);
					setState(1197);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1201);
				match(IDENTIFIER);
				setState(1206);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1202);
					match(OPEN_BRACKET);
					setState(1203);
					constantExpression(0);
					setState(1204);
					match(CLOSE_BRACKET);
					}
				}

				setState(1208);
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
				setState(1209);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1210);
				match(IDENTIFIER);
				setState(1215);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,178,_ctx) ) {
				case 1:
					{
					setState(1211);
					match(OPEN_BRACKET);
					setState(1212);
					constantExpression(0);
					setState(1213);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1217);
					match(DOT);
					}
				}

				setState(1220);
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
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 32:
			return constantExpression_sempred((ConstantExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean spinExpression_sempred(SpinExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 16);
		case 1:
			return precpred(_ctx, 15);
		case 2:
			return precpred(_ctx, 14);
		case 3:
			return precpred(_ctx, 13);
		case 4:
			return precpred(_ctx, 12);
		case 5:
			return precpred(_ctx, 11);
		case 6:
			return precpred(_ctx, 10);
		case 7:
			return precpred(_ctx, 9);
		case 8:
			return precpred(_ctx, 8);
		case 9:
			return precpred(_ctx, 7);
		case 10:
			return precpred(_ctx, 6);
		case 11:
			return precpred(_ctx, 5);
		case 12:
			return precpred(_ctx, 4);
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
	private boolean opcode_sempred(OpcodeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean constantExpression_sempred(ConstantExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return precpred(_ctx, 15);
		case 16:
			return precpred(_ctx, 14);
		case 17:
			return precpred(_ctx, 13);
		case 18:
			return precpred(_ctx, 12);
		case 19:
			return precpred(_ctx, 11);
		case 20:
			return precpred(_ctx, 10);
		case 21:
			return precpred(_ctx, 9);
		case 22:
			return precpred(_ctx, 8);
		case 23:
			return precpred(_ctx, 7);
		case 24:
			return precpred(_ctx, 6);
		case 25:
			return precpred(_ctx, 5);
		case 26:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3Y\u04ca\4\2\t\2\4"+
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
		"\20\u0150\n\20\r\20\16\20\u0151\3\20\3\20\3\20\5\20\u0157\n\20\3\21\5"+
		"\21\u015a\n\21\3\21\3\21\3\21\5\21\u015f\n\21\3\21\3\21\5\21\u0163\n\21"+
		"\3\21\3\21\3\21\3\21\3\21\5\21\u016a\n\21\3\21\3\21\5\21\u016e\n\21\3"+
		"\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0178\n\21\3\21\3\21\5\21"+
		"\u017c\n\21\3\22\3\22\3\22\7\22\u0181\n\22\f\22\16\22\u0184\13\22\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u019e\n\23\3\24\3\24"+
		"\5\24\u01a2\n\24\3\24\6\24\u01a5\n\24\r\24\16\24\u01a6\3\24\3\24\7\24"+
		"\u01ab\n\24\f\24\16\24\u01ae\13\24\3\24\5\24\u01b1\n\24\5\24\u01b3\n\24"+
		"\3\24\3\24\6\24\u01b7\n\24\r\24\16\24\u01b8\3\24\3\24\7\24\u01bd\n\24"+
		"\f\24\16\24\u01c0\13\24\3\24\3\24\3\24\3\24\6\24\u01c6\n\24\r\24\16\24"+
		"\u01c7\3\24\3\24\6\24\u01cc\n\24\r\24\16\24\u01cd\3\24\3\24\7\24\u01d2"+
		"\n\24\f\24\16\24\u01d5\13\24\3\24\3\24\3\24\3\24\6\24\u01db\n\24\r\24"+
		"\16\24\u01dc\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u01e7\n\24\5"+
		"\24\u01e9\n\24\3\24\6\24\u01ec\n\24\r\24\16\24\u01ed\3\24\3\24\7\24\u01f2"+
		"\n\24\f\24\16\24\u01f5\13\24\3\24\5\24\u01f8\n\24\5\24\u01fa\n\24\3\24"+
		"\3\24\3\24\3\24\6\24\u0200\n\24\r\24\16\24\u0201\3\24\3\24\7\24\u0206"+
		"\n\24\f\24\16\24\u0209\13\24\3\24\5\24\u020c\n\24\5\24\u020e\n\24\3\24"+
		"\3\24\3\24\3\24\6\24\u0214\n\24\r\24\16\24\u0215\3\24\3\24\7\24\u021a"+
		"\n\24\f\24\16\24\u021d\13\24\3\24\5\24\u0220\n\24\5\24\u0222\n\24\5\24"+
		"\u0224\n\24\3\25\3\25\3\25\3\25\6\25\u022a\n\25\r\25\16\25\u022b\3\25"+
		"\3\25\7\25\u0230\n\25\f\25\16\25\u0233\13\25\3\25\3\25\7\25\u0237\n\25"+
		"\f\25\16\25\u023a\13\25\3\25\3\25\3\25\6\25\u023f\n\25\r\25\16\25\u0240"+
		"\3\25\3\25\7\25\u0245\n\25\f\25\16\25\u0248\13\25\3\25\3\25\7\25\u024c"+
		"\n\25\f\25\16\25\u024f\13\25\3\25\3\25\3\25\6\25\u0254\n\25\r\25\16\25"+
		"\u0255\3\25\3\25\7\25\u025a\n\25\f\25\16\25\u025d\13\25\3\25\3\25\7\25"+
		"\u0261\n\25\f\25\16\25\u0264\13\25\5\25\u0266\n\25\3\26\3\26\6\26\u026a"+
		"\n\26\r\26\16\26\u026b\3\26\3\26\7\26\u0270\n\26\f\26\16\26\u0273\13\26"+
		"\3\26\3\26\3\26\3\26\3\26\6\26\u027a\n\26\r\26\16\26\u027b\3\26\3\26\7"+
		"\26\u0280\n\26\f\26\16\26\u0283\13\26\3\26\3\26\3\26\3\26\3\26\6\26\u028a"+
		"\n\26\r\26\16\26\u028b\3\26\3\26\7\26\u0290\n\26\f\26\16\26\u0293\13\26"+
		"\3\26\3\26\3\26\3\26\3\26\6\26\u029a\n\26\r\26\16\26\u029b\3\26\3\26\7"+
		"\26\u02a0\n\26\f\26\16\26\u02a3\13\26\3\26\3\26\5\26\u02a7\n\26\3\27\3"+
		"\27\3\27\6\27\u02ac\n\27\r\27\16\27\u02ad\3\27\3\27\3\27\7\27\u02b3\n"+
		"\27\f\27\16\27\u02b6\13\27\3\27\3\27\3\30\3\30\3\30\5\30\u02bd\n\30\3"+
		"\30\3\30\5\30\u02c1\n\30\3\30\6\30\u02c4\n\30\r\30\16\30\u02c5\3\30\3"+
		"\30\3\30\6\30\u02cb\n\30\r\30\16\30\u02cc\7\30\u02cf\n\30\f\30\16\30\u02d2"+
		"\13\30\3\30\5\30\u02d5\n\30\3\31\3\31\3\31\3\31\6\31\u02db\n\31\r\31\16"+
		"\31\u02dc\3\31\3\31\7\31\u02e1\n\31\f\31\16\31\u02e4\13\31\3\31\5\31\u02e7"+
		"\n\31\3\31\3\31\3\31\6\31\u02ec\n\31\r\31\16\31\u02ed\3\31\3\31\7\31\u02f2"+
		"\n\31\f\31\16\31\u02f5\13\31\3\31\5\31\u02f8\n\31\5\31\u02fa\n\31\3\32"+
		"\3\32\3\32\3\32\7\32\u0300\n\32\f\32\16\32\u0303\13\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u031b\n\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\7\32\u0347\n\32\f\32\16\32\u034a"+
		"\13\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33"+
		"\u0358\n\33\3\34\6\34\u035b\n\34\r\34\16\34\u035c\3\34\7\34\u0360\n\34"+
		"\f\34\16\34\u0363\13\34\3\34\7\34\u0366\n\34\f\34\16\34\u0369\13\34\3"+
		"\35\3\35\6\35\u036d\n\35\r\35\16\35\u036e\3\35\7\35\u0372\n\35\f\35\16"+
		"\35\u0375\13\35\3\35\3\35\3\35\3\35\5\35\u037b\n\35\5\35\u037d\n\35\3"+
		"\35\6\35\u0380\n\35\r\35\16\35\u0381\3\35\7\35\u0385\n\35\f\35\16\35\u0388"+
		"\13\35\3\35\7\35\u038b\n\35\f\35\16\35\u038e\13\35\3\35\5\35\u0391\n\35"+
		"\3\35\3\35\5\35\u0395\n\35\3\35\6\35\u0398\n\35\r\35\16\35\u0399\3\35"+
		"\7\35\u039d\n\35\f\35\16\35\u03a0\13\35\3\35\7\35\u03a3\n\35\f\35\16\35"+
		"\u03a6\13\35\3\35\5\35\u03a9\n\35\3\35\3\35\3\35\3\35\7\35\u03af\n\35"+
		"\f\35\16\35\u03b2\13\35\3\35\6\35\u03b5\n\35\r\35\16\35\u03b6\3\35\7\35"+
		"\u03ba\n\35\f\35\16\35\u03bd\13\35\3\35\7\35\u03c0\n\35\f\35\16\35\u03c3"+
		"\13\35\3\35\5\35\u03c6\n\35\3\35\3\35\3\35\6\35\u03cb\n\35\r\35\16\35"+
		"\u03cc\3\35\7\35\u03d0\n\35\f\35\16\35\u03d3\13\35\3\35\7\35\u03d6\n\35"+
		"\f\35\16\35\u03d9\13\35\3\35\5\35\u03dc\n\35\3\35\5\35\u03df\n\35\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u03e8\n\35\3\35\6\35\u03eb\n\35\r"+
		"\35\16\35\u03ec\3\35\7\35\u03f0\n\35\f\35\16\35\u03f3\13\35\3\35\7\35"+
		"\u03f6\n\35\f\35\16\35\u03f9\13\35\3\35\5\35\u03fc\n\35\3\35\5\35\u03ff"+
		"\n\35\3\35\3\35\3\35\3\35\3\35\5\35\u0406\n\35\3\35\6\35\u0409\n\35\r"+
		"\35\16\35\u040a\3\35\7\35\u040e\n\35\f\35\16\35\u0411\13\35\3\35\7\35"+
		"\u0414\n\35\f\35\16\35\u0417\13\35\3\35\5\35\u041a\n\35\3\35\5\35\u041d"+
		"\n\35\3\35\3\35\3\35\5\35\u0422\n\35\3\35\6\35\u0425\n\35\r\35\16\35\u0426"+
		"\3\35\7\35\u042a\n\35\f\35\16\35\u042d\13\35\3\35\7\35\u0430\n\35\f\35"+
		"\16\35\u0433\13\35\3\35\5\35\u0436\n\35\3\35\5\35\u0439\n\35\3\35\3\35"+
		"\5\35\u043d\n\35\3\35\6\35\u0440\n\35\r\35\16\35\u0441\3\35\7\35\u0445"+
		"\n\35\f\35\16\35\u0448\13\35\5\35\u044a\n\35\3\36\3\36\5\36\u044e\n\36"+
		"\3\36\3\36\3\37\3\37\3\37\3 \5 \u0456\n \3 \3 \3!\3!\3!\3!\3!\5!\u045f"+
		"\n!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u046e\n\""+
		"\3\"\5\"\u0471\n\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\""+
		"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\7\"\u049a\n\"\f\"\16\"\u049d\13\"\3"+
		"#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\5#\u04aa\n#\3#\3#\3#\3#\3#\3#\5#\u04b2"+
		"\n#\3#\3#\3#\3#\3#\5#\u04b9\n#\3#\3#\3#\3#\3#\3#\3#\5#\u04c2\n#\3#\5#"+
		"\u04c5\n#\3#\5#\u04c8\n#\3#\2\4\62B$\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668:<>@BD\2\22\4\2\4\4\7\7\3\2\66\67\5\2\'(-"+
		"-IJ\3\2\25\26\3\2)*\3\2\'(\3\2FG\3\2\33\34\4\2\22\22LL\4\2\24\24NN\4\2"+
		"\23\23OO\3\2\27\30\4\2PRUU\5\2IJLOYY\3\2\16\21\4\2\'(--\2\u05a0\2I\3\2"+
		"\2\2\4Y\3\2\2\2\6|\3\2\2\2\b\u008b\3\2\2\2\n\u00a6\3\2\2\2\f\u00b7\3\2"+
		"\2\2\16\u00ca\3\2\2\2\20\u00df\3\2\2\2\22\u00f9\3\2\2\2\24\u010b\3\2\2"+
		"\2\26\u0128\3\2\2\2\30\u0130\3\2\2\2\32\u0138\3\2\2\2\34\u0141\3\2\2\2"+
		"\36\u0156\3\2\2\2 \u017b\3\2\2\2\"\u017d\3\2\2\2$\u019d\3\2\2\2&\u0223"+
		"\3\2\2\2(\u0265\3\2\2\2*\u02a6\3\2\2\2,\u02a8\3\2\2\2.\u02b9\3\2\2\2\60"+
		"\u02f9\3\2\2\2\62\u031a\3\2\2\2\64\u0357\3\2\2\2\66\u035a\3\2\2\28\u0449"+
		"\3\2\2\2:\u044b\3\2\2\2<\u0451\3\2\2\2>\u0455\3\2\2\2@\u0459\3\2\2\2B"+
		"\u0470\3\2\2\2D\u04c7\3\2\2\2FH\7\7\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2"+
		"IJ\3\2\2\2JS\3\2\2\2KI\3\2\2\2LR\5\4\3\2MR\5\f\7\2NR\5\20\t\2OR\5\24\13"+
		"\2PR\5\66\34\2QL\3\2\2\2QM\3\2\2\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2RU\3\2"+
		"\2\2SQ\3\2\2\2ST\3\2\2\2TV\3\2\2\2US\3\2\2\2VW\7\2\2\3W\3\3\2\2\2XZ\7"+
		"\63\2\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\`\3\2\2\2]_\7\7\2\2"+
		"^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2av\3\2\2\2b`\3\2\2\2ch\5\6\4\2"+
		"de\7%\2\2eg\5\6\4\2fd\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2iu\3\2\2\2"+
		"jh\3\2\2\2ku\5\b\5\2lq\5\n\6\2mn\7%\2\2np\5\n\6\2om\3\2\2\2ps\3\2\2\2"+
		"qo\3\2\2\2qr\3\2\2\2ru\3\2\2\2sq\3\2\2\2tc\3\2\2\2tk\3\2\2\2tl\3\2\2\2"+
		"ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\5\3\2\2\2xv\3\2\2\2y{\7\3\2\2zy\3\2\2"+
		"\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7Y\2"+
		"\2\u0080\u0081\7#\2\2\u0081\u0085\5B\"\2\u0082\u0084\t\2\2\2\u0083\u0082"+
		"\3\2\2\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086"+
		"\7\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u008a\7\3\2\2\u0089\u0088\3\2\2\2"+
		"\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008e"+
		"\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u008f\7\21\2\2\u008f\u0094\5B\"\2\u0090"+
		"\u0091\7/\2\2\u0091\u0092\5B\"\2\u0092\u0093\7\60\2\2\u0093\u0095\3\2"+
		"\2\2\u0094\u0090\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0099\3\2\2\2\u0096"+
		"\u0098\t\2\2\2\u0097\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u0099\u009a\3\2\2\2\u009a\u00a0\3\2\2\2\u009b\u0099\3\2\2\2\u009c"+
		"\u009d\7%\2\2\u009d\u009f\5\n\6\2\u009e\u009c\3\2\2\2\u009f\u00a2\3\2"+
		"\2\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\t\3\2\2\2\u00a2\u00a0"+
		"\3\2\2\2\u00a3\u00a5\7\3\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00a6\3\2"+
		"\2\2\u00a9\u00ae\7Y\2\2\u00aa\u00ab\7/\2\2\u00ab\u00ac\5B\"\2\u00ac\u00ad"+
		"\7\60\2\2\u00ad\u00af\3\2\2\2\u00ae\u00aa\3\2\2\2\u00ae\u00af\3\2\2\2"+
		"\u00af\u00b3\3\2\2\2\u00b0\u00b2\t\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5"+
		"\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\13\3\2\2\2\u00b5"+
		"\u00b3\3\2\2\2\u00b6\u00b8\7\65\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00b9\3"+
		"\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00be\3\2\2\2\u00bb"+
		"\u00bd\7\7\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2"+
		"\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c4\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1"+
		"\u00c3\5\16\b\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3"+
		"\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\r\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c9"+
		"\7\3\2\2\u00c8\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca"+
		"\u00cb\3\2\2\2\u00cb\u00cd\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00d4\7Y"+
		"\2\2\u00ce\u00cf\7/\2\2\u00cf\u00d0\5B\"\2\u00d0\u00d1\7\60\2\2\u00d1"+
		"\u00d3\3\2\2\2\u00d2\u00ce\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2"+
		"\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7"+
		"\u00d8\7,\2\2\u00d8\u00da\7\t\2\2\u00d9\u00db\t\2\2\2\u00da\u00d9\3\2"+
		"\2\2\u00db\u00dc\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd"+
		"\17\3\2\2\2\u00de\u00e0\7\64\2\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2"+
		"\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e6\3\2\2\2\u00e3\u00e5"+
		"\7\7\2\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6"+
		"\u00e7\3\2\2\2\u00e7\u00f3\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00ee\5\22"+
		"\n\2\u00ea\u00eb\7%\2\2\u00eb\u00ed\5\22\n\2\u00ec\u00ea\3\2\2\2\u00ed"+
		"\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f2\3\2"+
		"\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00e9\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3"+
		"\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\21\3\2\2\2\u00f5\u00f3\3\2\2"+
		"\2\u00f6\u00f8\7\3\2\2\u00f7\u00f6\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7"+
		"\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc"+
		"\u00fe\7V\2\2\u00fd\u00fc\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff\u0104\7Y\2\2\u0100\u0101\7/\2\2\u0101\u0102\5B\"\2\u0102\u0103"+
		"\7\60\2\2\u0103\u0105\3\2\2\2\u0104\u0100\3\2\2\2\u0104\u0105\3\2\2\2"+
		"\u0105\u0107\3\2\2\2\u0106\u0108\t\2\2\2\u0107\u0106\3\2\2\2\u0108\u0109"+
		"\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\23\3\2\2\2\u010b"+
		"\u010c\t\3\2\2\u010c\u010d\7Y\2\2\u010d\u010f\7\61\2\2\u010e\u0110\5\26"+
		"\f\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0111\3\2\2\2\u0111"+
		"\u0114\7\62\2\2\u0112\u0113\7,\2\2\u0113\u0115\5\30\r\2\u0114\u0112\3"+
		"\2\2\2\u0114\u0115\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0117\7\36\2\2\u0117"+
		"\u0119\5\32\16\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011b\3"+
		"\2\2\2\u011a\u011c\7\7\2\2\u011b\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d"+
		"\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0123\7\3"+
		"\2\2\u0120\u0122\5\36\20\2\u0121\u0120\3\2\2\2\u0122\u0125\3\2\2\2\u0123"+
		"\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0126\3\2\2\2\u0125\u0123\3\2"+
		"\2\2\u0126\u0127\7\4\2\2\u0127\25\3\2\2\2\u0128\u012d\7Y\2\2\u0129\u012a"+
		"\7%\2\2\u012a\u012c\7Y\2\2\u012b\u0129\3\2\2\2\u012c\u012f\3\2\2\2\u012d"+
		"\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\27\3\2\2\2\u012f\u012d\3\2\2"+
		"\2\u0130\u0135\7Y\2\2\u0131\u0132\7%\2\2\u0132\u0134\7Y\2\2\u0133\u0131"+
		"\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136"+
		"\31\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u013d\5\34\17\2\u0139\u013a\7%\2"+
		"\2\u013a\u013c\5\34\17\2\u013b\u0139\3\2\2\2\u013c\u013f\3\2\2\2\u013d"+
		"\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\33\3\2\2\2\u013f\u013d\3\2\2"+
		"\2\u0140\u0142\7U\2\2\u0141\u0140\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0144"+
		"\3\2\2\2\u0143\u0145\7V\2\2\u0144\u0143\3\2\2\2\u0144\u0145\3\2\2\2\u0145"+
		"\u0146\3\2\2\2\u0146\u014b\7Y\2\2\u0147\u0148\7/\2\2\u0148\u0149\5B\""+
		"\2\u0149\u014a\7\60\2\2\u014a\u014c\3\2\2\2\u014b\u0147\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\35\3\2\2\2\u014d\u014f\5\62\32\2\u014e\u0150\7\7"+
		"\2\2\u014f\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u014f\3\2\2\2\u0151"+
		"\u0152\3\2\2\2\u0152\u0157\3\2\2\2\u0153\u0157\5&\24\2\u0154\u0157\5("+
		"\25\2\u0155\u0157\5,\27\2\u0156\u014d\3\2\2\2\u0156\u0153\3\2\2\2\u0156"+
		"\u0154\3\2\2\2\u0156\u0155\3\2\2\2\u0157\37\3\2\2\2\u0158\u015a\7&\2\2"+
		"\u0159\u0158\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015c"+
		"\7Y\2\2\u015c\u015e\7\61\2\2\u015d\u015f\5\"\22\2\u015e\u015d\3\2\2\2"+
		"\u015e\u015f\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u017c\7\62\2\2\u0161\u0163"+
		"\7&\2\2\u0162\u0161\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"\u0165\7Y\2\2\u0165\u0166\7$\2\2\u0166\u0167\7Y\2\2\u0167\u0169\7\61\2"+
		"\2\u0168\u016a\5\"\22\2\u0169\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a"+
		"\u016b\3\2\2\2\u016b\u017c\7\62\2\2\u016c\u016e\7&\2\2\u016d\u016c\3\2"+
		"\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\7Y\2\2\u0170"+
		"\u0171\7/\2\2\u0171\u0172\5\62\32\2\u0172\u0173\7\60\2\2\u0173\u0174\7"+
		"$\2\2\u0174\u0175\7Y\2\2\u0175\u0177\7\61\2\2\u0176\u0178\5\"\22\2\u0177"+
		"\u0176\3\2\2\2\u0177\u0178\3\2\2\2\u0178\u0179\3\2\2\2\u0179\u017a\7\62"+
		"\2\2\u017a\u017c\3\2\2\2\u017b\u0159\3\2\2\2\u017b\u0162\3\2\2\2\u017b"+
		"\u016d\3\2\2\2\u017c!\3\2\2\2\u017d\u0182\5\62\32\2\u017e\u017f\7%\2\2"+
		"\u017f\u0181\5\62\32\2\u0180\u017e\3\2\2\2\u0181\u0184\3\2\2\2\u0182\u0180"+
		"\3\2\2\2\u0182\u0183\3\2\2\2\u0183#\3\2\2\2\u0184\u0182\3\2\2\2\u0185"+
		"\u0186\7 \2\2\u0186\u019e\7Y\2\2\u0187\u0188\7Y\2\2\u0188\u0189\7$\2\2"+
		"\u0189\u018a\7Y\2\2\u018a\u018b\7/\2\2\u018b\u018c\5\62\32\2\u018c\u018d"+
		"\7\60\2\2\u018d\u019e\3\2\2\2\u018e\u018f\7Y\2\2\u018f\u0190\7$\2\2\u0190"+
		"\u0191\7/\2\2\u0191\u0192\5\62\32\2\u0192\u0193\7\60\2\2\u0193\u019e\3"+
		"\2\2\2\u0194\u0195\7Y\2\2\u0195\u0196\7$\2\2\u0196\u019e\7Y\2\2\u0197"+
		"\u0198\7Y\2\2\u0198\u0199\7/\2\2\u0199\u019a\5\62\32\2\u019a\u019b\7\60"+
		"\2\2\u019b\u019e\3\2\2\2\u019c\u019e\7Y\2\2\u019d\u0185\3\2\2\2\u019d"+
		"\u0187\3\2\2\2\u019d\u018e\3\2\2\2\u019d\u0194\3\2\2\2\u019d\u0197\3\2"+
		"\2\2\u019d\u019c\3\2\2\2\u019e%\3\2\2\2\u019f\u01a1\79\2\2\u01a0\u01a2"+
		"\5\62\32\2\u01a1\u01a0\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a4\3\2\2\2"+
		"\u01a3\u01a5\7\7\2\2\u01a4\u01a3\3\2\2\2\u01a5\u01a6\3\2\2\2\u01a6\u01a4"+
		"\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01b2\3\2\2\2\u01a8\u01ac\7\3\2\2\u01a9"+
		"\u01ab\5\36\20\2\u01aa\u01a9\3\2\2\2\u01ab\u01ae\3\2\2\2\u01ac\u01aa\3"+
		"\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01b0\3\2\2\2\u01ae\u01ac\3\2\2\2\u01af"+
		"\u01b1\7\4\2\2\u01b0\u01af\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b3\3\2"+
		"\2\2\u01b2\u01a8\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u0224\3\2\2\2\u01b4"+
		"\u01b6\79\2\2\u01b5\u01b7\7\7\2\2\u01b6\u01b5\3\2\2\2\u01b7\u01b8\3\2"+
		"\2\2\u01b8\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba"+
		"\u01be\7\3\2\2\u01bb\u01bd\5\36\20\2\u01bc\u01bb\3\2\2\2\u01bd\u01c0\3"+
		"\2\2\2\u01be\u01bc\3\2\2\2\u01be\u01bf\3\2\2\2\u01bf\u01c1\3\2\2\2\u01c0"+
		"\u01be\3\2\2\2\u01c1\u01c2\7\4\2\2\u01c2\u01c3\7=\2\2\u01c3\u01c5\5\62"+
		"\32\2\u01c4\u01c6\7\7\2\2\u01c5\u01c4\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7"+
		"\u01c5\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u0224\3\2\2\2\u01c9\u01cb\79"+
		"\2\2\u01ca\u01cc\7\7\2\2\u01cb\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd"+
		"\u01cb\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d3\7\3"+
		"\2\2\u01d0\u01d2\5\36\20\2\u01d1\u01d0\3\2\2\2\u01d2\u01d5\3\2\2\2\u01d3"+
		"\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u01d6\3\2\2\2\u01d5\u01d3\3\2"+
		"\2\2\u01d6\u01d7\7\4\2\2\u01d7\u01d8\7>\2\2\u01d8\u01da\5\62\32\2\u01d9"+
		"\u01db\7\7\2\2\u01da\u01d9\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc\u01da\3\2"+
		"\2\2\u01dc\u01dd\3\2\2\2\u01dd\u0224\3\2\2\2\u01de\u01df\79\2\2\u01df"+
		"\u01e0\5$\23\2\u01e0\u01e1\7:\2\2\u01e1\u01e8\5\62\32\2\u01e2\u01e3\7"+
		";\2\2\u01e3\u01e6\5\62\32\2\u01e4\u01e5\7<\2\2\u01e5\u01e7\5\62\32\2\u01e6"+
		"\u01e4\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7\u01e9\3\2\2\2\u01e8\u01e2\3\2"+
		"\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01eb\3\2\2\2\u01ea\u01ec\7\7\2\2\u01eb"+
		"\u01ea\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01eb\3\2\2\2\u01ed\u01ee\3\2"+
		"\2\2\u01ee\u01f9\3\2\2\2\u01ef\u01f3\7\3\2\2\u01f0\u01f2\5\36\20\2\u01f1"+
		"\u01f0\3\2\2\2\u01f2\u01f5\3\2\2\2\u01f3\u01f1\3\2\2\2\u01f3\u01f4\3\2"+
		"\2\2\u01f4\u01f7\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f6\u01f8\7\4\2\2\u01f7"+
		"\u01f6\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01fa\3\2\2\2\u01f9\u01ef\3\2"+
		"\2\2\u01f9\u01fa\3\2\2\2\u01fa\u0224\3\2\2\2\u01fb\u01fc\79\2\2\u01fc"+
		"\u01fd\7=\2\2\u01fd\u01ff\5\62\32\2\u01fe\u0200\7\7\2\2\u01ff\u01fe\3"+
		"\2\2\2\u0200\u0201\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2\2\2\u0202"+
		"\u020d\3\2\2\2\u0203\u0207\7\3\2\2\u0204\u0206\5\36\20\2\u0205\u0204\3"+
		"\2\2\2\u0206\u0209\3\2\2\2\u0207\u0205\3\2\2\2\u0207\u0208\3\2\2\2\u0208"+
		"\u020b\3\2\2\2\u0209\u0207\3\2\2\2\u020a\u020c\7\4\2\2\u020b\u020a\3\2"+
		"\2\2\u020b\u020c\3\2\2\2\u020c\u020e\3\2\2\2\u020d\u0203\3\2\2\2\u020d"+
		"\u020e\3\2\2\2\u020e\u0224\3\2\2\2\u020f\u0210\79\2\2\u0210\u0211\7>\2"+
		"\2\u0211\u0213\5\62\32\2\u0212\u0214\7\7\2\2\u0213\u0212\3\2\2\2\u0214"+
		"\u0215\3\2\2\2\u0215\u0213\3\2\2\2\u0215\u0216\3\2\2\2\u0216\u0221\3\2"+
		"\2\2\u0217\u021b\7\3\2\2\u0218\u021a\5\36\20\2\u0219\u0218\3\2\2\2\u021a"+
		"\u021d\3\2\2\2\u021b\u0219\3\2\2\2\u021b\u021c\3\2\2\2\u021c\u021f\3\2"+
		"\2\2\u021d\u021b\3\2\2\2\u021e\u0220\7\4\2\2\u021f\u021e\3\2\2\2\u021f"+
		"\u0220\3\2\2\2\u0220\u0222\3\2\2\2\u0221\u0217\3\2\2\2\u0221\u0222\3\2"+
		"\2\2\u0222\u0224\3\2\2\2\u0223\u019f\3\2\2\2\u0223\u01b4\3\2\2\2\u0223"+
		"\u01c9\3\2\2\2\u0223\u01de\3\2\2\2\u0223\u01fb\3\2\2\2\u0223\u020f\3\2"+
		"\2\2\u0224\'\3\2\2\2\u0225\u0226\7C\2\2\u0226\u0227\7M\2\2\u0227\u0229"+
		"\5\62\32\2\u0228\u022a\7\7\2\2\u0229\u0228\3\2\2\2\u022a\u022b\3\2\2\2"+
		"\u022b\u0229\3\2\2\2\u022b\u022c\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u0231"+
		"\7\3\2\2\u022e\u0230\5\36\20\2\u022f\u022e\3\2\2\2\u0230\u0233\3\2\2\2"+
		"\u0231\u022f\3\2\2\2\u0231\u0232\3\2\2\2\u0232\u0234\3\2\2\2\u0233\u0231"+
		"\3\2\2\2\u0234\u0238\7\4\2\2\u0235\u0237\5*\26\2\u0236\u0235\3\2\2\2\u0237"+
		"\u023a\3\2\2\2\u0238\u0236\3\2\2\2\u0238\u0239\3\2\2\2\u0239\u0266\3\2"+
		"\2\2\u023a\u0238\3\2\2\2\u023b\u023c\7B\2\2\u023c\u023e\5\62\32\2\u023d"+
		"\u023f\7\7\2\2\u023e\u023d\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u023e\3\2"+
		"\2\2\u0240\u0241\3\2\2\2\u0241\u0242\3\2\2\2\u0242\u0246\7\3\2\2\u0243"+
		"\u0245\5\36\20\2\u0244\u0243\3\2\2\2\u0245\u0248\3\2\2\2\u0246\u0244\3"+
		"\2\2\2\u0246\u0247\3\2\2\2\u0247\u0249\3\2\2\2\u0248\u0246\3\2\2\2\u0249"+
		"\u024d\7\4\2\2\u024a\u024c\5*\26\2\u024b\u024a\3\2\2\2\u024c\u024f\3\2"+
		"\2\2\u024d\u024b\3\2\2\2\u024d\u024e\3\2\2\2\u024e\u0266\3\2\2\2\u024f"+
		"\u024d\3\2\2\2\u0250\u0251\7C\2\2\u0251\u0253\5\62\32\2\u0252\u0254\7"+
		"\7\2\2\u0253\u0252\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0253\3\2\2\2\u0255"+
		"\u0256\3\2\2\2\u0256\u0257\3\2\2\2\u0257\u025b\7\3\2\2\u0258\u025a\5\36"+
		"\20\2\u0259\u0258\3\2\2\2\u025a\u025d\3\2\2\2\u025b\u0259\3\2\2\2\u025b"+
		"\u025c\3\2\2\2\u025c\u025e\3\2\2\2\u025d\u025b\3\2\2\2\u025e\u0262\7\4"+
		"\2\2\u025f\u0261\5*\26\2\u0260\u025f\3\2\2\2\u0261\u0264\3\2\2\2\u0262"+
		"\u0260\3\2\2\2\u0262\u0263\3\2\2\2\u0263\u0266\3\2\2\2\u0264\u0262\3\2"+
		"\2\2\u0265\u0225\3\2\2\2\u0265\u023b\3\2\2\2\u0265\u0250\3\2\2\2\u0266"+
		")\3\2\2\2\u0267\u0269\7A\2\2\u0268\u026a\7\7\2\2\u0269\u0268\3\2\2\2\u026a"+
		"\u026b\3\2\2\2\u026b\u0269\3\2\2\2\u026b\u026c\3\2\2\2\u026c\u026d\3\2"+
		"\2\2\u026d\u0271\7\3\2\2\u026e\u0270\5\36\20\2\u026f\u026e\3\2\2\2\u0270"+
		"\u0273\3\2\2\2\u0271\u026f\3\2\2\2\u0271\u0272\3\2\2\2\u0272\u0274\3\2"+
		"\2\2\u0273\u0271\3\2\2\2\u0274\u02a7\7\4\2\2\u0275\u0276\7@\2\2\u0276"+
		"\u0277\7M\2\2\u0277\u0279\5\62\32\2\u0278\u027a\7\7\2\2\u0279\u0278\3"+
		"\2\2\2\u027a\u027b\3\2\2\2\u027b\u0279\3\2\2\2\u027b\u027c\3\2\2\2\u027c"+
		"\u027d\3\2\2\2\u027d\u0281\7\3\2\2\u027e\u0280\5\36\20\2\u027f\u027e\3"+
		"\2\2\2\u0280\u0283\3\2\2\2\u0281\u027f\3\2\2\2\u0281\u0282\3\2\2\2\u0282"+
		"\u0284\3\2\2\2\u0283\u0281\3\2\2\2\u0284\u0285\7\4\2\2\u0285\u02a7\3\2"+
		"\2\2\u0286\u0287\7@\2\2\u0287\u0289\5\62\32\2\u0288\u028a\7\7\2\2\u0289"+
		"\u0288\3\2\2\2\u028a\u028b\3\2\2\2\u028b\u0289\3\2\2\2\u028b\u028c\3\2"+
		"\2\2\u028c\u028d\3\2\2\2\u028d\u0291\7\3\2\2\u028e\u0290\5\36\20\2\u028f"+
		"\u028e\3\2\2\2\u0290\u0293\3\2\2\2\u0291\u028f\3\2\2\2\u0291\u0292\3\2"+
		"\2\2\u0292\u0294\3\2\2\2\u0293\u0291\3\2\2\2\u0294\u0295\7\4\2\2\u0295"+
		"\u02a7\3\2\2\2\u0296\u0297\7?\2\2\u0297\u0299\5\62\32\2\u0298\u029a\7"+
		"\7\2\2\u0299\u0298\3\2\2\2\u029a\u029b\3\2\2\2\u029b\u0299\3\2\2\2\u029b"+
		"\u029c\3\2\2\2\u029c\u029d\3\2\2\2\u029d\u02a1\7\3\2\2\u029e\u02a0\5\36"+
		"\20\2\u029f\u029e\3\2\2\2\u02a0\u02a3\3\2\2\2\u02a1\u029f\3\2\2\2\u02a1"+
		"\u02a2\3\2\2\2\u02a2\u02a4\3\2\2\2\u02a3\u02a1\3\2\2\2\u02a4\u02a5\7\4"+
		"\2\2\u02a5\u02a7\3\2\2\2\u02a6\u0267\3\2\2\2\u02a6\u0275\3\2\2\2\u02a6"+
		"\u0286\3\2\2\2\u02a6\u0296\3\2\2\2\u02a7+\3\2\2\2\u02a8\u02a9\7D\2\2\u02a9"+
		"\u02ab\5\62\32\2\u02aa\u02ac\7\7\2\2\u02ab\u02aa\3\2\2\2\u02ac\u02ad\3"+
		"\2\2\2\u02ad\u02ab\3\2\2\2\u02ad\u02ae\3\2\2\2\u02ae\u02af\3\2\2\2\u02af"+
		"\u02b4\7\3\2\2\u02b0\u02b3\5.\30\2\u02b1\u02b3\5\60\31\2\u02b2\u02b0\3"+
		"\2\2\2\u02b2\u02b1\3\2\2\2\u02b3\u02b6\3\2\2\2\u02b4\u02b2\3\2\2\2\u02b4"+
		"\u02b5\3\2\2\2\u02b5\u02b7\3\2\2\2\u02b6\u02b4\3\2\2\2\u02b7\u02b8\7\4"+
		"\2\2\u02b8-\3\2\2\2\u02b9\u02bc\5\62\32\2\u02ba\u02bb\7\32\2\2\u02bb\u02bd"+
		"\5\62\32\2\u02bc\u02ba\3\2\2\2\u02bc\u02bd\3\2\2\2\u02bd\u02be\3\2\2\2"+
		"\u02be\u02c0\7,\2\2\u02bf\u02c1\5\62\32\2\u02c0\u02bf\3\2\2\2\u02c0\u02c1"+
		"\3\2\2\2\u02c1\u02c3\3\2\2\2\u02c2\u02c4\7\7\2\2\u02c3\u02c2\3\2\2\2\u02c4"+
		"\u02c5\3\2\2\2\u02c5\u02c3\3\2\2\2\u02c5\u02c6\3\2\2\2\u02c6\u02d4\3\2"+
		"\2\2\u02c7\u02d0\7\3\2\2\u02c8\u02ca\5\62\32\2\u02c9\u02cb\7\7\2\2\u02ca"+
		"\u02c9\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc\u02ca\3\2\2\2\u02cc\u02cd\3\2"+
		"\2\2\u02cd\u02cf\3\2\2\2\u02ce\u02c8\3\2\2\2\u02cf\u02d2\3\2\2\2\u02d0"+
		"\u02ce\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02d3\3\2\2\2\u02d2\u02d0\3\2"+
		"\2\2\u02d3\u02d5\7\4\2\2\u02d4\u02c7\3\2\2\2\u02d4\u02d5\3\2\2\2\u02d5"+
		"/\3\2\2\2\u02d6\u02d7\7E\2\2\u02d7\u02d8\7,\2\2\u02d8\u02da\5\62\32\2"+
		"\u02d9\u02db\7\7\2\2\u02da\u02d9\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc\u02da"+
		"\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02e6\3\2\2\2\u02de\u02e2\7\3\2\2\u02df"+
		"\u02e1\5\36\20\2\u02e0\u02df\3\2\2\2\u02e1\u02e4\3\2\2\2\u02e2\u02e0\3"+
		"\2\2\2\u02e2\u02e3\3\2\2\2\u02e3\u02e5\3\2\2\2\u02e4\u02e2\3\2\2\2\u02e5"+
		"\u02e7\7\4\2\2\u02e6\u02de\3\2\2\2\u02e6\u02e7\3\2\2\2\u02e7\u02fa\3\2"+
		"\2\2\u02e8\u02e9\7E\2\2\u02e9\u02eb\7,\2\2\u02ea\u02ec\7\7\2\2\u02eb\u02ea"+
		"\3\2\2\2\u02ec\u02ed\3\2\2\2\u02ed\u02eb\3\2\2\2\u02ed\u02ee\3\2\2\2\u02ee"+
		"\u02f7\3\2\2\2\u02ef\u02f3\7\3\2\2\u02f0\u02f2\5\36\20\2\u02f1\u02f0\3"+
		"\2\2\2\u02f2\u02f5\3\2\2\2\u02f3\u02f1\3\2\2\2\u02f3\u02f4\3\2\2\2\u02f4"+
		"\u02f6\3\2\2\2\u02f5\u02f3\3\2\2\2\u02f6\u02f8\7\4\2\2\u02f7\u02ef\3\2"+
		"\2\2\u02f7\u02f8\3\2\2\2\u02f8\u02fa\3\2\2\2\u02f9\u02d6\3\2\2\2\u02f9"+
		"\u02e8\3\2\2\2\u02fa\61\3\2\2\2\u02fb\u02fc\b\32\1\2\u02fc\u0301\5$\23"+
		"\2\u02fd\u02fe\7%\2\2\u02fe\u0300\7Y\2\2\u02ff\u02fd\3\2\2\2\u0300\u0303"+
		"\3\2\2\2\u0301\u02ff\3\2\2\2\u0301\u0302\3\2\2\2\u0302\u0304\3\2\2\2\u0303"+
		"\u0301\3\2\2\2\u0304\u0305\7\31\2\2\u0305\u0306\5\62\32\25\u0306\u031b"+
		"\3\2\2\2\u0307\u0308\7V\2\2\u0308\u0309\7/\2\2\u0309\u030a\5\62\32\2\u030a"+
		"\u030b\7\60\2\2\u030b\u030c\7\31\2\2\u030c\u030d\5\62\32\24\u030d\u031b"+
		"\3\2\2\2\u030e\u030f\t\4\2\2\u030f\u031b\5\62\32\23\u0310\u0311\7K\2\2"+
		"\u0311\u0312\7\61\2\2\u0312\u0313\5\62\32\2\u0313\u0314\7\62\2\2\u0314"+
		"\u031b\3\2\2\2\u0315\u0316\7\61\2\2\u0316\u0317\5\62\32\2\u0317\u0318"+
		"\7\62\2\2\u0318\u031b\3\2\2\2\u0319\u031b\5\64\33\2\u031a\u02fb\3\2\2"+
		"\2\u031a\u0307\3\2\2\2\u031a\u030e\3\2\2\2\u031a\u0310\3\2\2\2\u031a\u0315"+
		"\3\2\2\2\u031a\u0319\3\2\2\2\u031b\u0348\3\2\2\2\u031c\u031d\f\22\2\2"+
		"\u031d\u031e\t\5\2\2\u031e\u0347\5\62\32\23\u031f\u0320\f\21\2\2\u0320"+
		"\u0321\7\35\2\2\u0321\u0347\5\62\32\22\u0322\u0323\f\20\2\2\u0323\u0324"+
		"\7\37\2\2\u0324\u0347\5\62\32\21\u0325\u0326\f\17\2\2\u0326\u0327\7\36"+
		"\2\2\u0327\u0347\5\62\32\20\u0328\u0329\f\16\2\2\u0329\u032a\t\6\2\2\u032a"+
		"\u0347\5\62\32\17\u032b\u032c\f\r\2\2\u032c\u032d\t\7\2\2\u032d\u0347"+
		"\5\62\32\16\u032e\u032f\f\f\2\2\u032f\u0330\7H\2\2\u0330\u0347\5\62\32"+
		"\r\u0331\u0332\f\13\2\2\u0332\u0333\t\b\2\2\u0333\u0347\5\62\32\f\u0334"+
		"\u0335\f\n\2\2\u0335\u0336\t\t\2\2\u0336\u0347\5\62\32\13\u0337\u0338"+
		"\f\t\2\2\u0338\u0339\t\n\2\2\u0339\u0347\5\62\32\n\u033a\u033b\f\b\2\2"+
		"\u033b\u033c\t\13\2\2\u033c\u0347\5\62\32\t\u033d\u033e\f\7\2\2\u033e"+
		"\u033f\t\f\2\2\u033f\u0347\5\62\32\b\u0340\u0341\f\6\2\2\u0341\u0342\7"+
		"+\2\2\u0342\u0343\5\62\32\2\u0343\u0344\7,\2\2\u0344\u0345\5\62\32\7\u0345"+
		"\u0347\3\2\2\2\u0346\u031c\3\2\2\2\u0346\u031f\3\2\2\2\u0346\u0322\3\2"+
		"\2\2\u0346\u0325\3\2\2\2\u0346\u0328\3\2\2\2\u0346\u032b\3\2\2\2\u0346"+
		"\u032e\3\2\2\2\u0346\u0331\3\2\2\2\u0346\u0334\3\2\2\2\u0346\u0337\3\2"+
		"\2\2\u0346\u033a\3\2\2\2\u0346\u033d\3\2\2\2\u0346\u0340\3\2\2\2\u0347"+
		"\u034a\3\2\2\2\u0348\u0346\3\2\2\2\u0348\u0349\3\2\2\2\u0349\63\3\2\2"+
		"\2\u034a\u0348\3\2\2\2\u034b\u0358\7\r\2\2\u034c\u0358\7\f\2\2\u034d\u0358"+
		"\7\13\2\2\u034e\u0358\7\n\2\2\u034f\u0358\7\t\2\2\u0350\u0358\5$\23\2"+
		"\u0351\u0352\5$\23\2\u0352\u0353\t\r\2\2\u0353\u0358\3\2\2\2\u0354\u0355"+
		"\t\r\2\2\u0355\u0358\5$\23\2\u0356\u0358\5 \21\2\u0357\u034b\3\2\2\2\u0357"+
		"\u034c\3\2\2\2\u0357\u034d\3\2\2\2\u0357\u034e\3\2\2\2\u0357\u034f\3\2"+
		"\2\2\u0357\u0350\3\2\2\2\u0357\u0351\3\2\2\2\u0357\u0354\3\2\2\2\u0357"+
		"\u0356\3\2\2\2\u0358\65\3\2\2\2\u0359\u035b\78\2\2\u035a\u0359\3\2\2\2"+
		"\u035b\u035c\3\2\2\2\u035c\u035a\3\2\2\2\u035c\u035d\3\2\2\2\u035d\u0361"+
		"\3\2\2\2\u035e\u0360\7\7\2\2\u035f\u035e\3\2\2\2\u0360\u0363\3\2\2\2\u0361"+
		"\u035f\3\2\2\2\u0361\u0362\3\2\2\2\u0362\u0367\3\2\2\2\u0363\u0361\3\2"+
		"\2\2\u0364\u0366\58\35\2\u0365\u0364\3\2\2\2\u0366\u0369\3\2\2\2\u0367"+
		"\u0365\3\2\2\2\u0367\u0368\3\2\2\2\u0368\67\3\2\2\2\u0369\u0367\3\2\2"+
		"\2\u036a\u036c\5:\36\2\u036b\u036d\7\7\2\2\u036c\u036b\3\2\2\2\u036d\u036e"+
		"\3\2\2\2\u036e\u036c\3\2\2\2\u036e\u036f\3\2\2\2\u036f\u044a\3\2\2\2\u0370"+
		"\u0372\7\3\2\2\u0371\u0370\3\2\2\2\u0372\u0375\3\2\2\2\u0373\u0371\3\2"+
		"\2\2\u0373\u0374\3\2\2\2\u0374\u0376\3\2\2\2\u0375\u0373\3\2\2\2\u0376"+
		"\u037c\t\16\2\2\u0377\u037a\5B\"\2\u0378\u0379\7%\2\2\u0379\u037b\5B\""+
		"\2\u037a\u0378\3\2\2\2\u037a\u037b\3\2\2\2\u037b\u037d\3\2\2\2\u037c\u0377"+
		"\3\2\2\2\u037c\u037d\3\2\2\2\u037d\u037f\3\2\2\2\u037e\u0380\7\7\2\2\u037f"+
		"\u037e\3\2\2\2\u0380\u0381\3\2\2\2\u0381\u037f\3\2\2\2\u0381\u0382\3\2"+
		"\2\2\u0382\u0386\3\2\2\2\u0383\u0385\t\2\2\2\u0384\u0383\3\2\2\2\u0385"+
		"\u0388\3\2\2\2\u0386\u0384\3\2\2\2\u0386\u0387\3\2\2\2\u0387\u044a\3\2"+
		"\2\2\u0388\u0386\3\2\2\2\u0389\u038b\7\3\2\2\u038a\u0389\3\2\2\2\u038b"+
		"\u038e\3\2\2\2\u038c\u038a\3\2\2\2\u038c\u038d\3\2\2\2\u038d\u0390\3\2"+
		"\2\2\u038e\u038c\3\2\2\2\u038f\u0391\5:\36\2\u0390\u038f\3\2\2\2\u0390"+
		"\u0391\3\2\2\2\u0391\u0392\3\2\2\2\u0392\u0394\7S\2\2\u0393\u0395\5> "+
		"\2\u0394\u0393\3\2\2\2\u0394\u0395\3\2\2\2\u0395\u0397\3\2\2\2\u0396\u0398"+
		"\7\7\2\2\u0397\u0396\3\2\2\2\u0398\u0399\3\2\2\2\u0399\u0397\3\2\2\2\u0399"+
		"\u039a\3\2\2\2\u039a\u039e\3\2\2\2\u039b\u039d\t\2\2\2\u039c\u039b\3\2"+
		"\2\2\u039d\u03a0\3\2\2\2\u039e\u039c\3\2\2\2\u039e\u039f\3\2\2\2\u039f"+
		"\u044a\3\2\2\2\u03a0\u039e\3\2\2\2\u03a1\u03a3\7\3\2\2\u03a2\u03a1\3\2"+
		"\2\2\u03a3\u03a6\3\2\2\2\u03a4\u03a2\3\2\2\2\u03a4\u03a5\3\2\2\2\u03a5"+
		"\u03a8\3\2\2\2\u03a6\u03a4\3\2\2\2\u03a7\u03a9\5:\36\2\u03a8\u03a7\3\2"+
		"\2\2\u03a8\u03a9\3\2\2\2\u03a9\u03aa\3\2\2\2\u03aa\u03ab\7V\2\2\u03ab"+
		"\u03b0\5@!\2\u03ac\u03ad\7%\2\2\u03ad\u03af\5@!\2\u03ae\u03ac\3\2\2\2"+
		"\u03af\u03b2\3\2\2\2\u03b0\u03ae\3\2\2\2\u03b0\u03b1\3\2\2\2\u03b1\u03b4"+
		"\3\2\2\2\u03b2\u03b0\3\2\2\2\u03b3\u03b5\7\7\2\2\u03b4\u03b3\3\2\2\2\u03b5"+
		"\u03b6\3\2\2\2\u03b6\u03b4\3\2\2\2\u03b6\u03b7\3\2\2\2\u03b7\u03bb\3\2"+
		"\2\2\u03b8\u03ba\t\2\2\2\u03b9\u03b8\3\2\2\2\u03ba\u03bd\3\2\2\2\u03bb"+
		"\u03b9\3\2\2\2\u03bb\u03bc\3\2\2\2\u03bc\u044a\3\2\2\2\u03bd\u03bb\3\2"+
		"\2\2\u03be\u03c0\7\3\2\2\u03bf\u03be\3\2\2\2\u03c0\u03c3\3\2\2\2\u03c1"+
		"\u03bf\3\2\2\2\u03c1\u03c2\3\2\2\2\u03c2\u03c5\3\2\2\2\u03c3\u03c1\3\2"+
		"\2\2\u03c4\u03c6\5:\36\2\u03c5\u03c4\3\2\2\2\u03c5\u03c6\3\2\2\2\u03c6"+
		"\u03c7\3\2\2\2\u03c7\u03c8\7T\2\2\u03c8\u03ca\5B\"\2\u03c9\u03cb\7\7\2"+
		"\2\u03ca\u03c9\3\2\2\2\u03cb\u03cc\3\2\2\2\u03cc\u03ca\3\2\2\2\u03cc\u03cd"+
		"\3\2\2\2\u03cd\u03d1\3\2\2\2\u03ce\u03d0\t\2\2\2\u03cf\u03ce\3\2\2\2\u03d0"+
		"\u03d3\3\2\2\2\u03d1\u03cf\3\2\2\2\u03d1\u03d2\3\2\2\2\u03d2\u044a\3\2"+
		"\2\2\u03d3\u03d1\3\2\2\2\u03d4\u03d6\7\3\2\2\u03d5\u03d4\3\2\2\2\u03d6"+
		"\u03d9\3\2\2\2\u03d7\u03d5\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8\u03db\3\2"+
		"\2\2\u03d9\u03d7\3\2\2\2\u03da\u03dc\5:\36\2\u03db\u03da\3\2\2\2\u03db"+
		"\u03dc\3\2\2\2\u03dc\u03de\3\2\2\2\u03dd\u03df\7W\2\2\u03de\u03dd\3\2"+
		"\2\2\u03de\u03df\3\2\2\2\u03df\u03e0\3\2\2\2\u03e0\u03e1\5<\37\2\u03e1"+
		"\u03e2\5> \2\u03e2\u03e3\7%\2\2\u03e3\u03e4\5> \2\u03e4\u03e5\7%\2\2\u03e5"+
		"\u03e7\5> \2\u03e6\u03e8\7X\2\2\u03e7\u03e6\3\2\2\2\u03e7\u03e8\3\2\2"+
		"\2\u03e8\u03ea\3\2\2\2\u03e9\u03eb\7\7\2\2\u03ea\u03e9\3\2\2\2\u03eb\u03ec"+
		"\3\2\2\2\u03ec\u03ea\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03f1\3\2\2\2\u03ee"+
		"\u03f0\t\2\2\2\u03ef\u03ee\3\2\2\2\u03f0\u03f3\3\2\2\2\u03f1\u03ef\3\2"+
		"\2\2\u03f1\u03f2\3\2\2\2\u03f2\u044a\3\2\2\2\u03f3\u03f1\3\2\2\2\u03f4"+
		"\u03f6\7\3\2\2\u03f5\u03f4\3\2\2\2\u03f6\u03f9\3\2\2\2\u03f7\u03f5\3\2"+
		"\2\2\u03f7\u03f8\3\2\2\2\u03f8\u03fb\3\2\2\2\u03f9\u03f7\3\2\2\2\u03fa"+
		"\u03fc\5:\36\2\u03fb\u03fa\3\2\2\2\u03fb\u03fc\3\2\2\2\u03fc\u03fe\3\2"+
		"\2\2\u03fd\u03ff\7W\2\2\u03fe\u03fd\3\2\2\2\u03fe\u03ff\3\2\2\2\u03ff"+
		"\u0400\3\2\2\2\u0400\u0401\5<\37\2\u0401\u0402\5> \2\u0402\u0403\7%\2"+
		"\2\u0403\u0405\5> \2\u0404\u0406\7X\2\2\u0405\u0404\3\2\2\2\u0405\u0406"+
		"\3\2\2\2\u0406\u0408\3\2\2\2\u0407\u0409\7\7\2\2\u0408\u0407\3\2\2\2\u0409"+
		"\u040a\3\2\2\2\u040a\u0408\3\2\2\2\u040a\u040b\3\2\2\2\u040b\u040f\3\2"+
		"\2\2\u040c\u040e\t\2\2\2\u040d\u040c\3\2\2\2\u040e\u0411\3\2\2\2\u040f"+
		"\u040d\3\2\2\2\u040f\u0410\3\2\2\2\u0410\u044a\3\2\2\2\u0411\u040f\3\2"+
		"\2\2\u0412\u0414\7\3\2\2\u0413\u0412\3\2\2\2\u0414\u0417\3\2\2\2\u0415"+
		"\u0413\3\2\2\2\u0415\u0416\3\2\2\2\u0416\u0419\3\2\2\2\u0417\u0415\3\2"+
		"\2\2\u0418\u041a\5:\36\2\u0419\u0418\3\2\2\2\u0419\u041a\3\2\2\2\u041a"+
		"\u041c\3\2\2\2\u041b\u041d\7W\2\2\u041c\u041b\3\2\2\2\u041c\u041d\3\2"+
		"\2\2\u041d\u041e\3\2\2\2\u041e\u041f\5<\37\2\u041f\u0421\5> \2\u0420\u0422"+
		"\7X\2\2\u0421\u0420\3\2\2\2\u0421\u0422\3\2\2\2\u0422\u0424\3\2\2\2\u0423"+
		"\u0425\7\7\2\2\u0424\u0423\3\2\2\2\u0425\u0426\3\2\2\2\u0426\u0424\3\2"+
		"\2\2\u0426\u0427\3\2\2\2\u0427\u042b\3\2\2\2\u0428\u042a\t\2\2\2\u0429"+
		"\u0428\3\2\2\2\u042a\u042d\3\2\2\2\u042b\u0429\3\2\2\2\u042b\u042c\3\2"+
		"\2\2\u042c\u044a\3\2\2\2\u042d\u042b\3\2\2\2\u042e\u0430\7\3\2\2\u042f"+
		"\u042e\3\2\2\2\u0430\u0433\3\2\2\2\u0431\u042f\3\2\2\2\u0431\u0432\3\2"+
		"\2\2\u0432\u0435\3\2\2\2\u0433\u0431\3\2\2\2\u0434\u0436\5:\36\2\u0435"+
		"\u0434\3\2\2\2\u0435\u0436\3\2\2\2\u0436\u0438\3\2\2\2\u0437\u0439\7W"+
		"\2\2\u0438\u0437\3\2\2\2\u0438\u0439\3\2\2\2\u0439\u043a\3\2\2\2\u043a"+
		"\u043c\5<\37\2\u043b\u043d\7X\2\2\u043c\u043b\3\2\2\2\u043c\u043d\3\2"+
		"\2\2\u043d\u043f\3\2\2\2\u043e\u0440\7\7\2\2\u043f\u043e\3\2\2\2\u0440"+
		"\u0441\3\2\2\2\u0441\u043f\3\2\2\2\u0441\u0442\3\2\2\2\u0442\u0446\3\2"+
		"\2\2\u0443\u0445\t\2\2\2\u0444\u0443\3\2\2\2\u0445\u0448\3\2\2\2\u0446"+
		"\u0444\3\2\2\2\u0446\u0447\3\2\2\2\u0447\u044a\3\2\2\2\u0448\u0446\3\2"+
		"\2\2\u0449\u036a\3\2\2\2\u0449\u0373\3\2\2\2\u0449\u038c\3\2\2\2\u0449"+
		"\u03a4\3\2\2\2\u0449\u03c1\3\2\2\2\u0449\u03d7\3\2\2\2\u0449\u03f7\3\2"+
		"\2\2\u0449\u0415\3\2\2\2\u0449\u0431\3\2\2\2\u044a9\3\2\2\2\u044b\u044d"+
		"\6\36\17\2\u044c\u044e\7$\2\2\u044d\u044c\3\2\2\2\u044d\u044e\3\2\2\2"+
		"\u044e\u044f\3\2\2\2\u044f\u0450\7Y\2\2\u0450;\3\2\2\2\u0451\u0452\6\37"+
		"\20\2\u0452\u0453\t\17\2\2\u0453=\3\2\2\2\u0454\u0456\t\20\2\2\u0455\u0454"+
		"\3\2\2\2\u0455\u0456\3\2\2\2\u0456\u0457\3\2\2\2\u0457\u0458\5B\"\2\u0458"+
		"?\3\2\2\2\u0459\u045e\5B\"\2\u045a\u045b\7/\2\2\u045b\u045c\5B\"\2\u045c"+
		"\u045d\7\60\2\2\u045d\u045f\3\2\2\2\u045e\u045a\3\2\2\2\u045e\u045f\3"+
		"\2\2\2\u045fA\3\2\2\2\u0460\u0461\b\"\1\2\u0461\u0462\t\21\2\2\u0462\u0471"+
		"\5B\"\22\u0463\u0464\7K\2\2\u0464\u0465\7\61\2\2\u0465\u0466\5B\"\2\u0466"+
		"\u0467\7\62\2\2\u0467\u0471\3\2\2\2\u0468\u0469\7\61\2\2\u0469\u046a\5"+
		"B\"\2\u046a\u046b\7\62\2\2\u046b\u0471\3\2\2\2\u046c\u046e\7 \2\2\u046d"+
		"\u046c\3\2\2\2\u046d\u046e\3\2\2\2\u046e\u046f\3\2\2\2\u046f\u0471\5D"+
		"#\2\u0470\u0460\3\2\2\2\u0470\u0463\3\2\2\2\u0470\u0468\3\2\2\2\u0470"+
		"\u046d\3\2\2\2\u0471\u049b\3\2\2\2\u0472\u0473\f\21\2\2\u0473\u0474\t"+
		"\5\2\2\u0474\u049a\5B\"\22\u0475\u0476\f\20\2\2\u0476\u0477\7\35\2\2\u0477"+
		"\u049a\5B\"\21\u0478\u0479\f\17\2\2\u0479\u047a\7\37\2\2\u047a\u049a\5"+
		"B\"\20\u047b\u047c\f\16\2\2\u047c\u047d\7\36\2\2\u047d\u049a\5B\"\17\u047e"+
		"\u047f\f\r\2\2\u047f\u0480\t\6\2\2\u0480\u049a\5B\"\16\u0481\u0482\f\f"+
		"\2\2\u0482\u0483\t\7\2\2\u0483\u049a\5B\"\r\u0484\u0485\f\13\2\2\u0485"+
		"\u0486\t\b\2\2\u0486\u049a\5B\"\f\u0487\u0488\f\n\2\2\u0488\u0489\t\t"+
		"\2\2\u0489\u049a\5B\"\13\u048a\u048b\f\t\2\2\u048b\u048c\t\n\2\2\u048c"+
		"\u049a\5B\"\n\u048d\u048e\f\b\2\2\u048e\u048f\t\13\2\2\u048f\u049a\5B"+
		"\"\t\u0490\u0491\f\7\2\2\u0491\u0492\t\f\2\2\u0492\u049a\5B\"\b\u0493"+
		"\u0494\f\6\2\2\u0494\u0495\7+\2\2\u0495\u0496\5B\"\2\u0496\u0497\7,\2"+
		"\2\u0497\u0498\5B\"\7\u0498\u049a\3\2\2\2\u0499\u0472\3\2\2\2\u0499\u0475"+
		"\3\2\2\2\u0499\u0478\3\2\2\2\u0499\u047b\3\2\2\2\u0499\u047e\3\2\2\2\u0499"+
		"\u0481\3\2\2\2\u0499\u0484\3\2\2\2\u0499\u0487\3\2\2\2\u0499\u048a\3\2"+
		"\2\2\u0499\u048d\3\2\2\2\u0499\u0490\3\2\2\2\u0499\u0493\3\2\2\2\u049a"+
		"\u049d\3\2\2\2\u049b\u0499\3\2\2\2\u049b\u049c\3\2\2\2\u049cC\3\2\2\2"+
		"\u049d\u049b\3\2\2\2\u049e\u04c8\7\r\2\2\u049f\u04c8\7\f\2\2\u04a0\u04c8"+
		"\7\13\2\2\u04a1\u04c8\7\n\2\2\u04a2\u04c8\7\t\2\2\u04a3\u04c8\7!\2\2\u04a4"+
		"\u04a9\7Y\2\2\u04a5\u04a6\7/\2\2\u04a6\u04a7\5B\"\2\u04a7\u04a8\7\60\2"+
		"\2\u04a8\u04aa\3\2\2\2\u04a9\u04a5\3\2\2\2\u04a9\u04aa\3\2\2\2\u04aa\u04c8"+
		"\3\2\2\2\u04ab\u04ac\7Y\2\2\u04ac\u04b1\t\r\2\2\u04ad\u04ae\7/\2\2\u04ae"+
		"\u04af\5B\"\2\u04af\u04b0\7\60\2\2\u04b0\u04b2\3\2\2\2\u04b1\u04ad\3\2"+
		"\2\2\u04b1\u04b2\3\2\2\2\u04b2\u04c8\3\2\2\2\u04b3\u04b8\7Y\2\2\u04b4"+
		"\u04b5\7/\2\2\u04b5\u04b6\5B\"\2\u04b6\u04b7\7\60\2\2\u04b7\u04b9\3\2"+
		"\2\2\u04b8\u04b4\3\2\2\2\u04b8\u04b9\3\2\2\2\u04b9\u04ba\3\2\2\2\u04ba"+
		"\u04c8\t\r\2\2\u04bb\u04bc\t\r\2\2\u04bc\u04c1\7Y\2\2\u04bd\u04be\7/\2"+
		"\2\u04be\u04bf\5B\"\2\u04bf\u04c0\7\60\2\2\u04c0\u04c2\3\2\2\2\u04c1\u04bd"+
		"\3\2\2\2\u04c1\u04c2\3\2\2\2\u04c2\u04c8\3\2\2\2\u04c3\u04c5\7$\2\2\u04c4"+
		"\u04c3\3\2\2\2\u04c4\u04c5\3\2\2\2\u04c5\u04c6\3\2\2\2\u04c6\u04c8\7Y"+
		"\2\2\u04c7\u049e\3\2\2\2\u04c7\u049f\3\2\2\2\u04c7\u04a0\3\2\2\2\u04c7"+
		"\u04a1\3\2\2\2\u04c7\u04a2\3\2\2\2\u04c7\u04a3\3\2\2\2\u04c7\u04a4\3\2"+
		"\2\2\u04c7\u04ab\3\2\2\2\u04c7\u04b3\3\2\2\2\u04c7\u04bb\3\2\2\2\u04c7"+
		"\u04c4\3\2\2\2\u04c8E\3\2\2\2\u00b7IQS[`hqtv|\u0085\u008b\u0094\u0099"+
		"\u00a0\u00a6\u00ae\u00b3\u00b9\u00be\u00c4\u00ca\u00d4\u00dc\u00e1\u00e6"+
		"\u00ee\u00f3\u00f9\u00fd\u0104\u0109\u010f\u0114\u0118\u011d\u0123\u012d"+
		"\u0135\u013d\u0141\u0144\u014b\u0151\u0156\u0159\u015e\u0162\u0169\u016d"+
		"\u0177\u017b\u0182\u019d\u01a1\u01a6\u01ac\u01b0\u01b2\u01b8\u01be\u01c7"+
		"\u01cd\u01d3\u01dc\u01e6\u01e8\u01ed\u01f3\u01f7\u01f9\u0201\u0207\u020b"+
		"\u020d\u0215\u021b\u021f\u0221\u0223\u022b\u0231\u0238\u0240\u0246\u024d"+
		"\u0255\u025b\u0262\u0265\u026b\u0271\u027b\u0281\u028b\u0291\u029b\u02a1"+
		"\u02a6\u02ad\u02b2\u02b4\u02bc\u02c0\u02c5\u02cc\u02d0\u02d4\u02dc\u02e2"+
		"\u02e6\u02ed\u02f3\u02f7\u02f9\u0301\u031a\u0346\u0348\u0357\u035c\u0361"+
		"\u0367\u036e\u0373\u037a\u037c\u0381\u0386\u038c\u0390\u0394\u0399\u039e"+
		"\u03a4\u03a8\u03b0\u03b6\u03bb\u03c1\u03c5\u03cc\u03d1\u03d7\u03db\u03de"+
		"\u03e7\u03ec\u03f1\u03f7\u03fb\u03fe\u0405\u040a\u040f\u0415\u0419\u041c"+
		"\u0421\u0426\u042b\u0431\u0435\u0438\u043c\u0441\u0446\u0449\u044d\u0455"+
		"\u045e\u046d\u0470\u0499\u049b\u04a9\u04b1\u04b8\u04c1\u04c4\u04c7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}