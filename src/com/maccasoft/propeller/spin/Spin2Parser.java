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
			setState(534);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
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
				setState(422);
				match(INDENT);
				setState(426);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(423);
					statement();
					}
					}
					setState(428);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(429);
				match(DEDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				match(REPEAT);
				setState(432); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(431);
					match(NL);
					}
					}
					setState(434); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(436);
				match(INDENT);
				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(437);
					statement();
					}
					}
					setState(442);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(443);
				match(DEDENT);
				setState(444);
				match(WHILE);
				setState(445);
				spinExpression(0);
				setState(447); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(446);
					match(NL);
					}
					}
					setState(449); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(451);
				match(REPEAT);
				setState(453); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(452);
					match(NL);
					}
					}
					setState(455); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(457);
				match(INDENT);
				setState(461);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(458);
					statement();
					}
					}
					setState(463);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(464);
				match(DEDENT);
				setState(465);
				match(UNTIL);
				setState(466);
				spinExpression(0);
				setState(468); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(467);
					match(NL);
					}
					}
					setState(470); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(472);
				match(REPEAT);
				setState(473);
				match(IDENTIFIER);
				setState(474);
				match(FROM);
				setState(475);
				spinExpression(0);
				setState(482);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(476);
					match(TO);
					setState(477);
					spinExpression(0);
					setState(480);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(478);
						match(STEP);
						setState(479);
						spinExpression(0);
						}
					}

					}
				}

				setState(485); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(484);
					match(NL);
					}
					}
					setState(487); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(489);
				match(INDENT);
				setState(493);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(490);
					statement();
					}
					}
					setState(495);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(496);
				match(DEDENT);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(498);
				match(REPEAT);
				setState(499);
				match(WHILE);
				setState(500);
				spinExpression(0);
				setState(502); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(501);
					match(NL);
					}
					}
					setState(504); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(514);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(506);
					match(INDENT);
					setState(510);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(507);
						statement();
						}
						}
						setState(512);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(513);
					match(DEDENT);
					}
				}

				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(516);
				match(REPEAT);
				setState(517);
				match(UNTIL);
				setState(518);
				spinExpression(0);
				setState(520); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(519);
					match(NL);
					}
					}
					setState(522); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(532);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(524);
					match(INDENT);
					setState(528);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(525);
						statement();
						}
						}
						setState(530);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(531);
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
			setState(600);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(536);
				match(IF);
				setState(537);
				match(NOT);
				setState(538);
				spinExpression(0);
				setState(540); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(539);
					match(NL);
					}
					}
					setState(542); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(544);
				match(INDENT);
				setState(548);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(545);
					statement();
					}
					}
					setState(550);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(551);
				match(DEDENT);
				setState(555);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(552);
					elseConditional();
					}
					}
					setState(557);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(558);
				match(IFNOT);
				setState(559);
				spinExpression(0);
				setState(561); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(560);
					match(NL);
					}
					}
					setState(563); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(565);
				match(INDENT);
				setState(569);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(566);
					statement();
					}
					}
					setState(571);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(572);
				match(DEDENT);
				setState(576);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(573);
					elseConditional();
					}
					}
					setState(578);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(579);
				match(IF);
				setState(580);
				spinExpression(0);
				setState(582); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(581);
					match(NL);
					}
					}
					setState(584); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(586);
				match(INDENT);
				setState(590);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(587);
					statement();
					}
					}
					setState(592);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(593);
				match(DEDENT);
				setState(597);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(594);
					elseConditional();
					}
					}
					setState(599);
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
			setState(665);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(602);
				match(ELSE);
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
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
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
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(616);
				match(ELSEIF);
				setState(617);
				match(NOT);
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
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
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
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(633);
				match(ELSEIF);
				setState(634);
				spinExpression(0);
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
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
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
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(649);
				match(ELSEIFNOT);
				setState(650);
				spinExpression(0);
				setState(652); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(651);
					match(NL);
					}
					}
					setState(654); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(656);
				match(INDENT);
				setState(660);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(657);
					statement();
					}
					}
					setState(662);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(663);
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
			setState(667);
			match(CASE);
			setState(668);
			spinExpression(0);
			setState(670); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(669);
				match(NL);
				}
				}
				setState(672); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(674);
			match(INDENT);
			setState(679);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (OTHER - 67)) | (1L << (ENCOD - 67)) | (1L << (DECOD - 67)) | (1L << (FUNCTIONS - 67)) | (1L << (TYPE - 67)) | (1L << (IDENTIFIER - 67)))) != 0)) {
				{
				setState(677);
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
					setState(675);
					caseConditionalMatch();
					}
					break;
				case OTHER:
					{
					setState(676);
					caseConditionalOther();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(681);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(682);
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
			setState(684);
			spinExpression(0);
			setState(687);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(685);
				match(ELLIPSIS);
				setState(686);
				spinExpression(0);
				}
			}

			setState(689);
			match(COLON);
			setState(691);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
				{
				setState(690);
				spinExpression(0);
				}
			}

			setState(694); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(693);
				match(NL);
				}
				}
				setState(696); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(711);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INDENT) {
				{
				setState(698);
				match(INDENT);
				setState(707);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					{
					setState(699);
					spinExpression(0);
					setState(701); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(700);
						match(NL);
						}
						}
						setState(703); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==NL );
					}
					}
					setState(709);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(710);
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
			setState(748);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(713);
				match(OTHER);
				setState(714);
				match(COLON);
				setState(715);
				spinExpression(0);
				setState(717); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(716);
					match(NL);
					}
					}
					setState(719); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(729);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(721);
					match(INDENT);
					setState(725);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(722);
						statement();
						}
						}
						setState(727);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(728);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(731);
				match(OTHER);
				setState(732);
				match(COLON);
				setState(734); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(733);
					match(NL);
					}
					}
					setState(736); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(746);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(738);
					match(INDENT);
					setState(742);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(739);
						statement();
						}
						}
						setState(744);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(745);
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
			setState(781);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				{
				setState(751);
				identifier();
				setState(756);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(752);
					match(COMMA);
					setState(753);
					match(IDENTIFIER);
					}
					}
					setState(758);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(759);
				match(ASSIGN);
				setState(760);
				spinExpression(19);
				}
				break;
			case 2:
				{
				setState(762);
				match(TYPE);
				setState(763);
				match(OPEN_BRACKET);
				setState(764);
				spinExpression(0);
				setState(765);
				match(CLOSE_BRACKET);
				setState(766);
				match(ASSIGN);
				setState(767);
				spinExpression(18);
				}
				break;
			case 3:
				{
				setState(769);
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
				setState(770);
				((SpinExpressionContext)_localctx).exp = spinExpression(17);
				}
				break;
			case 4:
				{
				setState(771);
				((SpinExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(772);
				match(OPEN_PAREN);
				setState(773);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(774);
				match(CLOSE_PAREN);
				}
				break;
			case 5:
				{
				setState(776);
				match(OPEN_PAREN);
				setState(777);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(778);
				match(CLOSE_PAREN);
				}
				break;
			case 6:
				{
				setState(780);
				expressionAtom();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(827);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,112,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(825);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
					case 1:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(783);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(784);
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
						setState(785);
						((SpinExpressionContext)_localctx).right = spinExpression(17);
						}
						break;
					case 2:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(786);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(787);
						((SpinExpressionContext)_localctx).operator = match(BIN_AND);
						setState(788);
						((SpinExpressionContext)_localctx).right = spinExpression(16);
						}
						break;
					case 3:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(789);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(790);
						((SpinExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(791);
						((SpinExpressionContext)_localctx).right = spinExpression(15);
						}
						break;
					case 4:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(792);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(793);
						((SpinExpressionContext)_localctx).operator = match(BIN_OR);
						setState(794);
						((SpinExpressionContext)_localctx).right = spinExpression(14);
						}
						break;
					case 5:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(795);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(796);
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
						setState(797);
						((SpinExpressionContext)_localctx).right = spinExpression(13);
						}
						break;
					case 6:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(798);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(799);
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
						setState(800);
						((SpinExpressionContext)_localctx).right = spinExpression(12);
						}
						break;
					case 7:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(801);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(802);
						((SpinExpressionContext)_localctx).operator = match(FRAC);
						setState(803);
						((SpinExpressionContext)_localctx).right = spinExpression(11);
						}
						break;
					case 8:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(804);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(805);
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
						setState(806);
						((SpinExpressionContext)_localctx).right = spinExpression(10);
						}
						break;
					case 9:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(807);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(808);
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
						setState(809);
						((SpinExpressionContext)_localctx).right = spinExpression(9);
						}
						break;
					case 10:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(810);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(811);
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
						setState(812);
						((SpinExpressionContext)_localctx).right = spinExpression(8);
						}
						break;
					case 11:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(813);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(814);
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
						setState(815);
						((SpinExpressionContext)_localctx).right = spinExpression(7);
						}
						break;
					case 12:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(816);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(817);
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
						setState(818);
						((SpinExpressionContext)_localctx).right = spinExpression(6);
						}
						break;
					case 13:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(819);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(820);
						((SpinExpressionContext)_localctx).operator = match(QUESTION);
						setState(821);
						((SpinExpressionContext)_localctx).middle = spinExpression(0);
						setState(822);
						((SpinExpressionContext)_localctx).operator = match(COLON);
						setState(823);
						((SpinExpressionContext)_localctx).right = spinExpression(5);
						}
						break;
					}
					} 
				}
				setState(829);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,112,_ctx);
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
			setState(842);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(830);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(831);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(832);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(833);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(834);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(835);
				identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(836);
				identifier();
				setState(837);
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
				setState(839);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(840);
				identifier();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(841);
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
			setState(845); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(844);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(847); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,114,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(852);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(849);
					match(NL);
					}
					} 
				}
				setState(854);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			}
			setState(858);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(855);
					dataLine();
					}
					} 
				}
				setState(860);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
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
			setState(1084);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(861);
				label();
				setState(863); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(862);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(865); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(870);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(867);
					match(INDENT);
					}
					}
					setState(872);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(873);
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
				setState(879);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(874);
					constantExpression(0);
					setState(877);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(875);
						match(COMMA);
						setState(876);
						constantExpression(0);
						}
					}

					}
				}

				setState(882); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(881);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(884); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(889);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(886);
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
					setState(891);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(895);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(892);
						match(INDENT);
						}
						} 
					}
					setState(897);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
				}
				setState(899);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
				case 1:
					{
					setState(898);
					label();
					}
					break;
				}
				setState(901);
				((DataLineContext)_localctx).directive = match(FIT);
				setState(903);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(902);
					argument();
					}
				}

				setState(906); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(905);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(908); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(913);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(910);
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
					setState(915);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(919);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(916);
						match(INDENT);
						}
						} 
					}
					setState(921);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
				}
				setState(923);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
				case 1:
					{
					setState(922);
					label();
					}
					break;
				}
				setState(925);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(926);
				dataValue();
				setState(931);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(927);
					match(COMMA);
					setState(928);
					dataValue();
					}
					}
					setState(933);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(935); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(934);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(937); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(942);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,132,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(939);
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
					setState(944);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,132,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(948);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(945);
						match(INDENT);
						}
						} 
					}
					setState(950);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				}
				setState(952);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
				case 1:
					{
					setState(951);
					label();
					}
					break;
				}
				setState(954);
				((DataLineContext)_localctx).directive = match(RES);
				setState(955);
				constantExpression(0);
				setState(957); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(956);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(959); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(964);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(961);
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
					setState(966);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,136,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(970);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,137,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(967);
						match(INDENT);
						}
						} 
					}
					setState(972);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,137,_ctx);
				}
				setState(974);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
				case 1:
					{
					setState(973);
					label();
					}
					break;
				}
				setState(977);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,139,_ctx) ) {
				case 1:
					{
					setState(976);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(979);
				opcode();
				setState(980);
				argument();
				setState(981);
				match(COMMA);
				setState(982);
				argument();
				setState(983);
				match(COMMA);
				setState(984);
				argument();
				setState(986);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(985);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(989); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(988);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(991); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(996);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(993);
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
					setState(998);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,142,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1002);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,143,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(999);
						match(INDENT);
						}
						} 
					}
					setState(1004);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,143,_ctx);
				}
				setState(1006);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
				case 1:
					{
					setState(1005);
					label();
					}
					break;
				}
				setState(1009);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,145,_ctx) ) {
				case 1:
					{
					setState(1008);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1011);
				opcode();
				setState(1012);
				argument();
				setState(1013);
				match(COMMA);
				setState(1014);
				argument();
				setState(1016);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1015);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1019); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1018);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1021); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,147,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1026);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1023);
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
					setState(1028);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,148,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1032);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1029);
						match(INDENT);
						}
						} 
					}
					setState(1034);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,149,_ctx);
				}
				setState(1036);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
				case 1:
					{
					setState(1035);
					label();
					}
					break;
				}
				setState(1039);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,151,_ctx) ) {
				case 1:
					{
					setState(1038);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1041);
				opcode();
				setState(1042);
				argument();
				setState(1044);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1043);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1047); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1046);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1049); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1054);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1051);
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
					setState(1056);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1060);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1057);
						match(INDENT);
						}
						} 
					}
					setState(1062);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
				}
				setState(1064);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
				case 1:
					{
					setState(1063);
					label();
					}
					break;
				}
				setState(1067);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
				case 1:
					{
					setState(1066);
					((DataLineContext)_localctx).condition = match(CONDITION);
					}
					break;
				}
				setState(1069);
				opcode();
				setState(1071);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1070);
					((DataLineContext)_localctx).modifier = match(MODIFIER);
					}
				}

				setState(1074); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1073);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1076); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,159,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1081);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1078);
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
					setState(1083);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,160,_ctx);
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
			setState(1086);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			{
			setState(1088);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(1087);
				match(DOT);
				}
			}

			setState(1090);
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
			setState(1092);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1093);
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
			setState(1096);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL))) != 0)) {
				{
				setState(1095);
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

			setState(1098);
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
			setState(1100);
			constantExpression(0);
			setState(1105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1101);
				match(OPEN_BRACKET);
				setState(1102);
				((DataValueContext)_localctx).count = constantExpression(0);
				setState(1103);
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
			setState(1123);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1108);
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
				setState(1109);
				((ConstantExpressionContext)_localctx).exp = constantExpression(16);
				}
				break;
			case FUNCTIONS:
				{
				setState(1110);
				((ConstantExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1111);
				match(OPEN_PAREN);
				setState(1112);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1113);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1115);
				match(OPEN_PAREN);
				setState(1116);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1117);
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
				setState(1120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1119);
					match(AT);
					}
				}

				setState(1122);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1166);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,168,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1164);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
					case 1:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1125);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1126);
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
						setState(1127);
						((ConstantExpressionContext)_localctx).right = constantExpression(16);
						}
						break;
					case 2:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1128);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1129);
						((ConstantExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1130);
						((ConstantExpressionContext)_localctx).right = constantExpression(15);
						}
						break;
					case 3:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1131);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1132);
						((ConstantExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1133);
						((ConstantExpressionContext)_localctx).right = constantExpression(14);
						}
						break;
					case 4:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1134);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1135);
						((ConstantExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1136);
						((ConstantExpressionContext)_localctx).right = constantExpression(13);
						}
						break;
					case 5:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1137);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1138);
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
						setState(1139);
						((ConstantExpressionContext)_localctx).right = constantExpression(12);
						}
						break;
					case 6:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1140);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1141);
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
						setState(1142);
						((ConstantExpressionContext)_localctx).right = constantExpression(11);
						}
						break;
					case 7:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1143);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1144);
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
						setState(1145);
						((ConstantExpressionContext)_localctx).right = constantExpression(10);
						}
						break;
					case 8:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1146);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1147);
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
						setState(1148);
						((ConstantExpressionContext)_localctx).right = constantExpression(9);
						}
						break;
					case 9:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1149);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1150);
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
						setState(1151);
						((ConstantExpressionContext)_localctx).right = constantExpression(8);
						}
						break;
					case 10:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1152);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1153);
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
						setState(1154);
						((ConstantExpressionContext)_localctx).right = constantExpression(7);
						}
						break;
					case 11:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1155);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1156);
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
						setState(1157);
						((ConstantExpressionContext)_localctx).right = constantExpression(6);
						}
						break;
					case 12:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1158);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1159);
						((ConstantExpressionContext)_localctx).operator = match(QUESTION);
						setState(1160);
						((ConstantExpressionContext)_localctx).middle = constantExpression(0);
						setState(1161);
						((ConstantExpressionContext)_localctx).operator = match(COLON);
						setState(1162);
						((ConstantExpressionContext)_localctx).right = constantExpression(5);
						}
						break;
					}
					} 
				}
				setState(1168);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,168,_ctx);
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
			setState(1210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1169);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1170);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1171);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1172);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1173);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1174);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1175);
				match(IDENTIFIER);
				setState(1180);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
				case 1:
					{
					setState(1176);
					match(OPEN_BRACKET);
					setState(1177);
					constantExpression(0);
					setState(1178);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1182);
				match(IDENTIFIER);
				setState(1183);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1188);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,170,_ctx) ) {
				case 1:
					{
					setState(1184);
					match(OPEN_BRACKET);
					setState(1185);
					constantExpression(0);
					setState(1186);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1190);
				match(IDENTIFIER);
				setState(1195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1191);
					match(OPEN_BRACKET);
					setState(1192);
					constantExpression(0);
					setState(1193);
					match(CLOSE_BRACKET);
					}
				}

				setState(1197);
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
				setState(1198);
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
				match(IDENTIFIER);
				setState(1204);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
				case 1:
					{
					setState(1200);
					match(OPEN_BRACKET);
					setState(1201);
					constantExpression(0);
					setState(1202);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1206);
					match(DOT);
					}
				}

				setState(1209);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3Y\u04bf\4\2\t\2\4"+
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
		"\u01ab\n\24\f\24\16\24\u01ae\13\24\3\24\3\24\3\24\6\24\u01b3\n\24\r\24"+
		"\16\24\u01b4\3\24\3\24\7\24\u01b9\n\24\f\24\16\24\u01bc\13\24\3\24\3\24"+
		"\3\24\3\24\6\24\u01c2\n\24\r\24\16\24\u01c3\3\24\3\24\6\24\u01c8\n\24"+
		"\r\24\16\24\u01c9\3\24\3\24\7\24\u01ce\n\24\f\24\16\24\u01d1\13\24\3\24"+
		"\3\24\3\24\3\24\6\24\u01d7\n\24\r\24\16\24\u01d8\3\24\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\3\24\5\24\u01e3\n\24\5\24\u01e5\n\24\3\24\6\24\u01e8\n\24"+
		"\r\24\16\24\u01e9\3\24\3\24\7\24\u01ee\n\24\f\24\16\24\u01f1\13\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\6\24\u01f9\n\24\r\24\16\24\u01fa\3\24\3\24\7"+
		"\24\u01ff\n\24\f\24\16\24\u0202\13\24\3\24\5\24\u0205\n\24\3\24\3\24\3"+
		"\24\3\24\6\24\u020b\n\24\r\24\16\24\u020c\3\24\3\24\7\24\u0211\n\24\f"+
		"\24\16\24\u0214\13\24\3\24\5\24\u0217\n\24\5\24\u0219\n\24\3\25\3\25\3"+
		"\25\3\25\6\25\u021f\n\25\r\25\16\25\u0220\3\25\3\25\7\25\u0225\n\25\f"+
		"\25\16\25\u0228\13\25\3\25\3\25\7\25\u022c\n\25\f\25\16\25\u022f\13\25"+
		"\3\25\3\25\3\25\6\25\u0234\n\25\r\25\16\25\u0235\3\25\3\25\7\25\u023a"+
		"\n\25\f\25\16\25\u023d\13\25\3\25\3\25\7\25\u0241\n\25\f\25\16\25\u0244"+
		"\13\25\3\25\3\25\3\25\6\25\u0249\n\25\r\25\16\25\u024a\3\25\3\25\7\25"+
		"\u024f\n\25\f\25\16\25\u0252\13\25\3\25\3\25\7\25\u0256\n\25\f\25\16\25"+
		"\u0259\13\25\5\25\u025b\n\25\3\26\3\26\6\26\u025f\n\26\r\26\16\26\u0260"+
		"\3\26\3\26\7\26\u0265\n\26\f\26\16\26\u0268\13\26\3\26\3\26\3\26\3\26"+
		"\3\26\6\26\u026f\n\26\r\26\16\26\u0270\3\26\3\26\7\26\u0275\n\26\f\26"+
		"\16\26\u0278\13\26\3\26\3\26\3\26\3\26\3\26\6\26\u027f\n\26\r\26\16\26"+
		"\u0280\3\26\3\26\7\26\u0285\n\26\f\26\16\26\u0288\13\26\3\26\3\26\3\26"+
		"\3\26\3\26\6\26\u028f\n\26\r\26\16\26\u0290\3\26\3\26\7\26\u0295\n\26"+
		"\f\26\16\26\u0298\13\26\3\26\3\26\5\26\u029c\n\26\3\27\3\27\3\27\6\27"+
		"\u02a1\n\27\r\27\16\27\u02a2\3\27\3\27\3\27\7\27\u02a8\n\27\f\27\16\27"+
		"\u02ab\13\27\3\27\3\27\3\30\3\30\3\30\5\30\u02b2\n\30\3\30\3\30\5\30\u02b6"+
		"\n\30\3\30\6\30\u02b9\n\30\r\30\16\30\u02ba\3\30\3\30\3\30\6\30\u02c0"+
		"\n\30\r\30\16\30\u02c1\7\30\u02c4\n\30\f\30\16\30\u02c7\13\30\3\30\5\30"+
		"\u02ca\n\30\3\31\3\31\3\31\3\31\6\31\u02d0\n\31\r\31\16\31\u02d1\3\31"+
		"\3\31\7\31\u02d6\n\31\f\31\16\31\u02d9\13\31\3\31\5\31\u02dc\n\31\3\31"+
		"\3\31\3\31\6\31\u02e1\n\31\r\31\16\31\u02e2\3\31\3\31\7\31\u02e7\n\31"+
		"\f\31\16\31\u02ea\13\31\3\31\5\31\u02ed\n\31\5\31\u02ef\n\31\3\32\3\32"+
		"\3\32\3\32\7\32\u02f5\n\32\f\32\16\32\u02f8\13\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\5\32\u0310\n\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\7\32\u033c\n\32\f\32\16\32\u033f\13"+
		"\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u034d"+
		"\n\33\3\34\6\34\u0350\n\34\r\34\16\34\u0351\3\34\7\34\u0355\n\34\f\34"+
		"\16\34\u0358\13\34\3\34\7\34\u035b\n\34\f\34\16\34\u035e\13\34\3\35\3"+
		"\35\6\35\u0362\n\35\r\35\16\35\u0363\3\35\7\35\u0367\n\35\f\35\16\35\u036a"+
		"\13\35\3\35\3\35\3\35\3\35\5\35\u0370\n\35\5\35\u0372\n\35\3\35\6\35\u0375"+
		"\n\35\r\35\16\35\u0376\3\35\7\35\u037a\n\35\f\35\16\35\u037d\13\35\3\35"+
		"\7\35\u0380\n\35\f\35\16\35\u0383\13\35\3\35\5\35\u0386\n\35\3\35\3\35"+
		"\5\35\u038a\n\35\3\35\6\35\u038d\n\35\r\35\16\35\u038e\3\35\7\35\u0392"+
		"\n\35\f\35\16\35\u0395\13\35\3\35\7\35\u0398\n\35\f\35\16\35\u039b\13"+
		"\35\3\35\5\35\u039e\n\35\3\35\3\35\3\35\3\35\7\35\u03a4\n\35\f\35\16\35"+
		"\u03a7\13\35\3\35\6\35\u03aa\n\35\r\35\16\35\u03ab\3\35\7\35\u03af\n\35"+
		"\f\35\16\35\u03b2\13\35\3\35\7\35\u03b5\n\35\f\35\16\35\u03b8\13\35\3"+
		"\35\5\35\u03bb\n\35\3\35\3\35\3\35\6\35\u03c0\n\35\r\35\16\35\u03c1\3"+
		"\35\7\35\u03c5\n\35\f\35\16\35\u03c8\13\35\3\35\7\35\u03cb\n\35\f\35\16"+
		"\35\u03ce\13\35\3\35\5\35\u03d1\n\35\3\35\5\35\u03d4\n\35\3\35\3\35\3"+
		"\35\3\35\3\35\3\35\3\35\5\35\u03dd\n\35\3\35\6\35\u03e0\n\35\r\35\16\35"+
		"\u03e1\3\35\7\35\u03e5\n\35\f\35\16\35\u03e8\13\35\3\35\7\35\u03eb\n\35"+
		"\f\35\16\35\u03ee\13\35\3\35\5\35\u03f1\n\35\3\35\5\35\u03f4\n\35\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u03fb\n\35\3\35\6\35\u03fe\n\35\r\35\16\35\u03ff"+
		"\3\35\7\35\u0403\n\35\f\35\16\35\u0406\13\35\3\35\7\35\u0409\n\35\f\35"+
		"\16\35\u040c\13\35\3\35\5\35\u040f\n\35\3\35\5\35\u0412\n\35\3\35\3\35"+
		"\3\35\5\35\u0417\n\35\3\35\6\35\u041a\n\35\r\35\16\35\u041b\3\35\7\35"+
		"\u041f\n\35\f\35\16\35\u0422\13\35\3\35\7\35\u0425\n\35\f\35\16\35\u0428"+
		"\13\35\3\35\5\35\u042b\n\35\3\35\5\35\u042e\n\35\3\35\3\35\5\35\u0432"+
		"\n\35\3\35\6\35\u0435\n\35\r\35\16\35\u0436\3\35\7\35\u043a\n\35\f\35"+
		"\16\35\u043d\13\35\5\35\u043f\n\35\3\36\3\36\5\36\u0443\n\36\3\36\3\36"+
		"\3\37\3\37\3\37\3 \5 \u044b\n \3 \3 \3!\3!\3!\3!\3!\5!\u0454\n!\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u0463\n\"\3\"\5\"\u0466"+
		"\n\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\""+
		"\3\"\3\"\3\"\3\"\3\"\7\"\u048f\n\"\f\"\16\"\u0492\13\"\3#\3#\3#\3#\3#"+
		"\3#\3#\3#\3#\3#\3#\5#\u049f\n#\3#\3#\3#\3#\3#\3#\5#\u04a7\n#\3#\3#\3#"+
		"\3#\3#\5#\u04ae\n#\3#\3#\3#\3#\3#\3#\3#\5#\u04b7\n#\3#\5#\u04ba\n#\3#"+
		"\5#\u04bd\n#\3#\2\4\62B$\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&("+
		"*,.\60\62\64\668:<>@BD\2\22\4\2\4\4\7\7\3\2\66\67\5\2\'(--IJ\3\2\25\26"+
		"\3\2)*\3\2\'(\3\2FG\3\2\33\34\4\2\22\22LL\4\2\24\24NN\4\2\23\23OO\3\2"+
		"\27\30\4\2PRUU\5\2IJLOYY\3\2\16\21\4\2\'(--\2\u058f\2I\3\2\2\2\4Y\3\2"+
		"\2\2\6|\3\2\2\2\b\u008b\3\2\2\2\n\u00a6\3\2\2\2\f\u00b7\3\2\2\2\16\u00ca"+
		"\3\2\2\2\20\u00df\3\2\2\2\22\u00f9\3\2\2\2\24\u010b\3\2\2\2\26\u0128\3"+
		"\2\2\2\30\u0130\3\2\2\2\32\u0138\3\2\2\2\34\u0141\3\2\2\2\36\u0156\3\2"+
		"\2\2 \u017b\3\2\2\2\"\u017d\3\2\2\2$\u019d\3\2\2\2&\u0218\3\2\2\2(\u025a"+
		"\3\2\2\2*\u029b\3\2\2\2,\u029d\3\2\2\2.\u02ae\3\2\2\2\60\u02ee\3\2\2\2"+
		"\62\u030f\3\2\2\2\64\u034c\3\2\2\2\66\u034f\3\2\2\28\u043e\3\2\2\2:\u0440"+
		"\3\2\2\2<\u0446\3\2\2\2>\u044a\3\2\2\2@\u044e\3\2\2\2B\u0465\3\2\2\2D"+
		"\u04bc\3\2\2\2FH\7\7\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JS\3\2"+
		"\2\2KI\3\2\2\2LR\5\4\3\2MR\5\f\7\2NR\5\20\t\2OR\5\24\13\2PR\5\66\34\2"+
		"QL\3\2\2\2QM\3\2\2\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2"+
		"ST\3\2\2\2TV\3\2\2\2US\3\2\2\2VW\7\2\2\3W\3\3\2\2\2XZ\7\63\2\2YX\3\2\2"+
		"\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\`\3\2\2\2]_\7\7\2\2^]\3\2\2\2_b\3\2"+
		"\2\2`^\3\2\2\2`a\3\2\2\2av\3\2\2\2b`\3\2\2\2ch\5\6\4\2de\7%\2\2eg\5\6"+
		"\4\2fd\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2iu\3\2\2\2jh\3\2\2\2ku\5\b"+
		"\5\2lq\5\n\6\2mn\7%\2\2np\5\n\6\2om\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2"+
		"\2\2ru\3\2\2\2sq\3\2\2\2tc\3\2\2\2tk\3\2\2\2tl\3\2\2\2ux\3\2\2\2vt\3\2"+
		"\2\2vw\3\2\2\2w\5\3\2\2\2xv\3\2\2\2y{\7\3\2\2zy\3\2\2\2{~\3\2\2\2|z\3"+
		"\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7Y\2\2\u0080\u0081\7"+
		"#\2\2\u0081\u0085\5B\"\2\u0082\u0084\t\2\2\2\u0083\u0082\3\2\2\2\u0084"+
		"\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\7\3\2\2\2"+
		"\u0087\u0085\3\2\2\2\u0088\u008a\7\3\2\2\u0089\u0088\3\2\2\2\u008a\u008d"+
		"\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008e\3\2\2\2\u008d"+
		"\u008b\3\2\2\2\u008e\u008f\7\21\2\2\u008f\u0094\5B\"\2\u0090\u0091\7/"+
		"\2\2\u0091\u0092\5B\"\2\u0092\u0093\7\60\2\2\u0093\u0095\3\2\2\2\u0094"+
		"\u0090\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0099\3\2\2\2\u0096\u0098\t\2"+
		"\2\2\u0097\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099"+
		"\u009a\3\2\2\2\u009a\u00a0\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\7%"+
		"\2\2\u009d\u009f\5\n\6\2\u009e\u009c\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0"+
		"\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\t\3\2\2\2\u00a2\u00a0\3\2\2\2"+
		"\u00a3\u00a5\7\3\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4"+
		"\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9"+
		"\u00ae\7Y\2\2\u00aa\u00ab\7/\2\2\u00ab\u00ac\5B\"\2\u00ac\u00ad\7\60\2"+
		"\2\u00ad\u00af\3\2\2\2\u00ae\u00aa\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b3"+
		"\3\2\2\2\u00b0\u00b2\t\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3"+
		"\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\13\3\2\2\2\u00b5\u00b3\3\2\2"+
		"\2\u00b6\u00b8\7\65\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00be\3\2\2\2\u00bb\u00bd\7\7"+
		"\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be"+
		"\u00bf\3\2\2\2\u00bf\u00c4\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c3\5\16"+
		"\b\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4"+
		"\u00c5\3\2\2\2\u00c5\r\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c9\7\3\2\2"+
		"\u00c8\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb"+
		"\3\2\2\2\u00cb\u00cd\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00d4\7Y\2\2\u00ce"+
		"\u00cf\7/\2\2\u00cf\u00d0\5B\"\2\u00d0\u00d1\7\60\2\2\u00d1\u00d3\3\2"+
		"\2\2\u00d2\u00ce\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4"+
		"\u00d5\3\2\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00d8\7,"+
		"\2\2\u00d8\u00da\7\t\2\2\u00d9\u00db\t\2\2\2\u00da\u00d9\3\2\2\2\u00db"+
		"\u00dc\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\17\3\2\2"+
		"\2\u00de\u00e0\7\64\2\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e6\3\2\2\2\u00e3\u00e5\7\7"+
		"\2\2\u00e4\u00e3\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6"+
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
		"\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01ac\7\3\2\2\u01a9"+
		"\u01ab\5\36\20\2\u01aa\u01a9\3\2\2\2\u01ab\u01ae\3\2\2\2\u01ac\u01aa\3"+
		"\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01af\3\2\2\2\u01ae\u01ac\3\2\2\2\u01af"+
		"\u0219\7\4\2\2\u01b0\u01b2\79\2\2\u01b1\u01b3\7\7\2\2\u01b2\u01b1\3\2"+
		"\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b2\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5"+
		"\u01b6\3\2\2\2\u01b6\u01ba\7\3\2\2\u01b7\u01b9\5\36\20\2\u01b8\u01b7\3"+
		"\2\2\2\u01b9\u01bc\3\2\2\2\u01ba\u01b8\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb"+
		"\u01bd\3\2\2\2\u01bc\u01ba\3\2\2\2\u01bd\u01be\7\4\2\2\u01be\u01bf\7="+
		"\2\2\u01bf\u01c1\5\62\32\2\u01c0\u01c2\7\7\2\2\u01c1\u01c0\3\2\2\2\u01c2"+
		"\u01c3\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u0219\3\2"+
		"\2\2\u01c5\u01c7\79\2\2\u01c6\u01c8\7\7\2\2\u01c7\u01c6\3\2\2\2\u01c8"+
		"\u01c9\3\2\2\2\u01c9\u01c7\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u01cb\3\2"+
		"\2\2\u01cb\u01cf\7\3\2\2\u01cc\u01ce\5\36\20\2\u01cd\u01cc\3\2\2\2\u01ce"+
		"\u01d1\3\2\2\2\u01cf\u01cd\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d2\3\2"+
		"\2\2\u01d1\u01cf\3\2\2\2\u01d2\u01d3\7\4\2\2\u01d3\u01d4\7>\2\2\u01d4"+
		"\u01d6\5\62\32\2\u01d5\u01d7\7\7\2\2\u01d6\u01d5\3\2\2\2\u01d7\u01d8\3"+
		"\2\2\2\u01d8\u01d6\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9\u0219\3\2\2\2\u01da"+
		"\u01db\79\2\2\u01db\u01dc\7Y\2\2\u01dc\u01dd\7:\2\2\u01dd\u01e4\5\62\32"+
		"\2\u01de\u01df\7;\2\2\u01df\u01e2\5\62\32\2\u01e0\u01e1\7<\2\2\u01e1\u01e3"+
		"\5\62\32\2\u01e2\u01e0\3\2\2\2\u01e2\u01e3\3\2\2\2\u01e3\u01e5\3\2\2\2"+
		"\u01e4\u01de\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e7\3\2\2\2\u01e6\u01e8"+
		"\7\7\2\2\u01e7\u01e6\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9\u01e7\3\2\2\2\u01e9"+
		"\u01ea\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb\u01ef\7\3\2\2\u01ec\u01ee\5\36"+
		"\20\2\u01ed\u01ec\3\2\2\2\u01ee\u01f1\3\2\2\2\u01ef\u01ed\3\2\2\2\u01ef"+
		"\u01f0\3\2\2\2\u01f0\u01f2\3\2\2\2\u01f1\u01ef\3\2\2\2\u01f2\u01f3\7\4"+
		"\2\2\u01f3\u0219\3\2\2\2\u01f4\u01f5\79\2\2\u01f5\u01f6\7=\2\2\u01f6\u01f8"+
		"\5\62\32\2\u01f7\u01f9\7\7\2\2\u01f8\u01f7\3\2\2\2\u01f9\u01fa\3\2\2\2"+
		"\u01fa\u01f8\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u0204\3\2\2\2\u01fc\u0200"+
		"\7\3\2\2\u01fd\u01ff\5\36\20\2\u01fe\u01fd\3\2\2\2\u01ff\u0202\3\2\2\2"+
		"\u0200\u01fe\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u0203\3\2\2\2\u0202\u0200"+
		"\3\2\2\2\u0203\u0205\7\4\2\2\u0204\u01fc\3\2\2\2\u0204\u0205\3\2\2\2\u0205"+
		"\u0219\3\2\2\2\u0206\u0207\79\2\2\u0207\u0208\7>\2\2\u0208\u020a\5\62"+
		"\32\2\u0209\u020b\7\7\2\2\u020a\u0209\3\2\2\2\u020b\u020c\3\2\2\2\u020c"+
		"\u020a\3\2\2\2\u020c\u020d\3\2\2\2\u020d\u0216\3\2\2\2\u020e\u0212\7\3"+
		"\2\2\u020f\u0211\5\36\20\2\u0210\u020f\3\2\2\2\u0211\u0214\3\2\2\2\u0212"+
		"\u0210\3\2\2\2\u0212\u0213\3\2\2\2\u0213\u0215\3\2\2\2\u0214\u0212\3\2"+
		"\2\2\u0215\u0217\7\4\2\2\u0216\u020e\3\2\2\2\u0216\u0217\3\2\2\2\u0217"+
		"\u0219\3\2\2\2\u0218\u019f\3\2\2\2\u0218\u01b0\3\2\2\2\u0218\u01c5\3\2"+
		"\2\2\u0218\u01da\3\2\2\2\u0218\u01f4\3\2\2\2\u0218\u0206\3\2\2\2\u0219"+
		"\'\3\2\2\2\u021a\u021b\7C\2\2\u021b\u021c\7M\2\2\u021c\u021e\5\62\32\2"+
		"\u021d\u021f\7\7\2\2\u021e\u021d\3\2\2\2\u021f\u0220\3\2\2\2\u0220\u021e"+
		"\3\2\2\2\u0220\u0221\3\2\2\2\u0221\u0222\3\2\2\2\u0222\u0226\7\3\2\2\u0223"+
		"\u0225\5\36\20\2\u0224\u0223\3\2\2\2\u0225\u0228\3\2\2\2\u0226\u0224\3"+
		"\2\2\2\u0226\u0227\3\2\2\2\u0227\u0229\3\2\2\2\u0228\u0226\3\2\2\2\u0229"+
		"\u022d\7\4\2\2\u022a\u022c\5*\26\2\u022b\u022a\3\2\2\2\u022c\u022f\3\2"+
		"\2\2\u022d\u022b\3\2\2\2\u022d\u022e\3\2\2\2\u022e\u025b\3\2\2\2\u022f"+
		"\u022d\3\2\2\2\u0230\u0231\7B\2\2\u0231\u0233\5\62\32\2\u0232\u0234\7"+
		"\7\2\2\u0233\u0232\3\2\2\2\u0234\u0235\3\2\2\2\u0235\u0233\3\2\2\2\u0235"+
		"\u0236\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u023b\7\3\2\2\u0238\u023a\5\36"+
		"\20\2\u0239\u0238\3\2\2\2\u023a\u023d\3\2\2\2\u023b\u0239\3\2\2\2\u023b"+
		"\u023c\3\2\2\2\u023c\u023e\3\2\2\2\u023d\u023b\3\2\2\2\u023e\u0242\7\4"+
		"\2\2\u023f\u0241\5*\26\2\u0240\u023f\3\2\2\2\u0241\u0244\3\2\2\2\u0242"+
		"\u0240\3\2\2\2\u0242\u0243\3\2\2\2\u0243\u025b\3\2\2\2\u0244\u0242\3\2"+
		"\2\2\u0245\u0246\7C\2\2\u0246\u0248\5\62\32\2\u0247\u0249\7\7\2\2\u0248"+
		"\u0247\3\2\2\2\u0249\u024a\3\2\2\2\u024a\u0248\3\2\2\2\u024a\u024b\3\2"+
		"\2\2\u024b\u024c\3\2\2\2\u024c\u0250\7\3\2\2\u024d\u024f\5\36\20\2\u024e"+
		"\u024d\3\2\2\2\u024f\u0252\3\2\2\2\u0250\u024e\3\2\2\2\u0250\u0251\3\2"+
		"\2\2\u0251\u0253\3\2\2\2\u0252\u0250\3\2\2\2\u0253\u0257\7\4\2\2\u0254"+
		"\u0256\5*\26\2\u0255\u0254\3\2\2\2\u0256\u0259\3\2\2\2\u0257\u0255\3\2"+
		"\2\2\u0257\u0258\3\2\2\2\u0258\u025b\3\2\2\2\u0259\u0257\3\2\2\2\u025a"+
		"\u021a\3\2\2\2\u025a\u0230\3\2\2\2\u025a\u0245\3\2\2\2\u025b)\3\2\2\2"+
		"\u025c\u025e\7A\2\2\u025d\u025f\7\7\2\2\u025e\u025d\3\2\2\2\u025f\u0260"+
		"\3\2\2\2\u0260\u025e\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0262\3\2\2\2\u0262"+
		"\u0266\7\3\2\2\u0263\u0265\5\36\20\2\u0264\u0263\3\2\2\2\u0265\u0268\3"+
		"\2\2\2\u0266\u0264\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0269\3\2\2\2\u0268"+
		"\u0266\3\2\2\2\u0269\u029c\7\4\2\2\u026a\u026b\7@\2\2\u026b\u026c\7M\2"+
		"\2\u026c\u026e\5\62\32\2\u026d\u026f\7\7\2\2\u026e\u026d\3\2\2\2\u026f"+
		"\u0270\3\2\2\2\u0270\u026e\3\2\2\2\u0270\u0271\3\2\2\2\u0271\u0272\3\2"+
		"\2\2\u0272\u0276\7\3\2\2\u0273\u0275\5\36\20\2\u0274\u0273\3\2\2\2\u0275"+
		"\u0278\3\2\2\2\u0276\u0274\3\2\2\2\u0276\u0277\3\2\2\2\u0277\u0279\3\2"+
		"\2\2\u0278\u0276\3\2\2\2\u0279\u027a\7\4\2\2\u027a\u029c\3\2\2\2\u027b"+
		"\u027c\7@\2\2\u027c\u027e\5\62\32\2\u027d\u027f\7\7\2\2\u027e\u027d\3"+
		"\2\2\2\u027f\u0280\3\2\2\2\u0280\u027e\3\2\2\2\u0280\u0281\3\2\2\2\u0281"+
		"\u0282\3\2\2\2\u0282\u0286\7\3\2\2\u0283\u0285\5\36\20\2\u0284\u0283\3"+
		"\2\2\2\u0285\u0288\3\2\2\2\u0286\u0284\3\2\2\2\u0286\u0287\3\2\2\2\u0287"+
		"\u0289\3\2\2\2\u0288\u0286\3\2\2\2\u0289\u028a\7\4\2\2\u028a\u029c\3\2"+
		"\2\2\u028b\u028c\7?\2\2\u028c\u028e\5\62\32\2\u028d\u028f\7\7\2\2\u028e"+
		"\u028d\3\2\2\2\u028f\u0290\3\2\2\2\u0290\u028e\3\2\2\2\u0290\u0291\3\2"+
		"\2\2\u0291\u0292\3\2\2\2\u0292\u0296\7\3\2\2\u0293\u0295\5\36\20\2\u0294"+
		"\u0293\3\2\2\2\u0295\u0298\3\2\2\2\u0296\u0294\3\2\2\2\u0296\u0297\3\2"+
		"\2\2\u0297\u0299\3\2\2\2\u0298\u0296\3\2\2\2\u0299\u029a\7\4\2\2\u029a"+
		"\u029c\3\2\2\2\u029b\u025c\3\2\2\2\u029b\u026a\3\2\2\2\u029b\u027b\3\2"+
		"\2\2\u029b\u028b\3\2\2\2\u029c+\3\2\2\2\u029d\u029e\7D\2\2\u029e\u02a0"+
		"\5\62\32\2\u029f\u02a1\7\7\2\2\u02a0\u029f\3\2\2\2\u02a1\u02a2\3\2\2\2"+
		"\u02a2\u02a0\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a3\u02a4\3\2\2\2\u02a4\u02a9"+
		"\7\3\2\2\u02a5\u02a8\5.\30\2\u02a6\u02a8\5\60\31\2\u02a7\u02a5\3\2\2\2"+
		"\u02a7\u02a6\3\2\2\2\u02a8\u02ab\3\2\2\2\u02a9\u02a7\3\2\2\2\u02a9\u02aa"+
		"\3\2\2\2\u02aa\u02ac\3\2\2\2\u02ab\u02a9\3\2\2\2\u02ac\u02ad\7\4\2\2\u02ad"+
		"-\3\2\2\2\u02ae\u02b1\5\62\32\2\u02af\u02b0\7\32\2\2\u02b0\u02b2\5\62"+
		"\32\2\u02b1\u02af\3\2\2\2\u02b1\u02b2\3\2\2\2\u02b2\u02b3\3\2\2\2\u02b3"+
		"\u02b5\7,\2\2\u02b4\u02b6\5\62\32\2\u02b5\u02b4\3\2\2\2\u02b5\u02b6\3"+
		"\2\2\2\u02b6\u02b8\3\2\2\2\u02b7\u02b9\7\7\2\2\u02b8\u02b7\3\2\2\2\u02b9"+
		"\u02ba\3\2\2\2\u02ba\u02b8\3\2\2\2\u02ba\u02bb\3\2\2\2\u02bb\u02c9\3\2"+
		"\2\2\u02bc\u02c5\7\3\2\2\u02bd\u02bf\5\62\32\2\u02be\u02c0\7\7\2\2\u02bf"+
		"\u02be\3\2\2\2\u02c0\u02c1\3\2\2\2\u02c1\u02bf\3\2\2\2\u02c1\u02c2\3\2"+
		"\2\2\u02c2\u02c4\3\2\2\2\u02c3\u02bd\3\2\2\2\u02c4\u02c7\3\2\2\2\u02c5"+
		"\u02c3\3\2\2\2\u02c5\u02c6\3\2\2\2\u02c6\u02c8\3\2\2\2\u02c7\u02c5\3\2"+
		"\2\2\u02c8\u02ca\7\4\2\2\u02c9\u02bc\3\2\2\2\u02c9\u02ca\3\2\2\2\u02ca"+
		"/\3\2\2\2\u02cb\u02cc\7E\2\2\u02cc\u02cd\7,\2\2\u02cd\u02cf\5\62\32\2"+
		"\u02ce\u02d0\7\7\2\2\u02cf\u02ce\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02cf"+
		"\3\2\2\2\u02d1\u02d2\3\2\2\2\u02d2\u02db\3\2\2\2\u02d3\u02d7\7\3\2\2\u02d4"+
		"\u02d6\5\36\20\2\u02d5\u02d4\3\2\2\2\u02d6\u02d9\3\2\2\2\u02d7\u02d5\3"+
		"\2\2\2\u02d7\u02d8\3\2\2\2\u02d8\u02da\3\2\2\2\u02d9\u02d7\3\2\2\2\u02da"+
		"\u02dc\7\4\2\2\u02db\u02d3\3\2\2\2\u02db\u02dc\3\2\2\2\u02dc\u02ef\3\2"+
		"\2\2\u02dd\u02de\7E\2\2\u02de\u02e0\7,\2\2\u02df\u02e1\7\7\2\2\u02e0\u02df"+
		"\3\2\2\2\u02e1\u02e2\3\2\2\2\u02e2\u02e0\3\2\2\2\u02e2\u02e3\3\2\2\2\u02e3"+
		"\u02ec\3\2\2\2\u02e4\u02e8\7\3\2\2\u02e5\u02e7\5\36\20\2\u02e6\u02e5\3"+
		"\2\2\2\u02e7\u02ea\3\2\2\2\u02e8\u02e6\3\2\2\2\u02e8\u02e9\3\2\2\2\u02e9"+
		"\u02eb\3\2\2\2\u02ea\u02e8\3\2\2\2\u02eb\u02ed\7\4\2\2\u02ec\u02e4\3\2"+
		"\2\2\u02ec\u02ed\3\2\2\2\u02ed\u02ef\3\2\2\2\u02ee\u02cb\3\2\2\2\u02ee"+
		"\u02dd\3\2\2\2\u02ef\61\3\2\2\2\u02f0\u02f1\b\32\1\2\u02f1\u02f6\5$\23"+
		"\2\u02f2\u02f3\7%\2\2\u02f3\u02f5\7Y\2\2\u02f4\u02f2\3\2\2\2\u02f5\u02f8"+
		"\3\2\2\2\u02f6\u02f4\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7\u02f9\3\2\2\2\u02f8"+
		"\u02f6\3\2\2\2\u02f9\u02fa\7\31\2\2\u02fa\u02fb\5\62\32\25\u02fb\u0310"+
		"\3\2\2\2\u02fc\u02fd\7V\2\2\u02fd\u02fe\7/\2\2\u02fe\u02ff\5\62\32\2\u02ff"+
		"\u0300\7\60\2\2\u0300\u0301\7\31\2\2\u0301\u0302\5\62\32\24\u0302\u0310"+
		"\3\2\2\2\u0303\u0304\t\4\2\2\u0304\u0310\5\62\32\23\u0305\u0306\7K\2\2"+
		"\u0306\u0307\7\61\2\2\u0307\u0308\5\62\32\2\u0308\u0309\7\62\2\2\u0309"+
		"\u0310\3\2\2\2\u030a\u030b\7\61\2\2\u030b\u030c\5\62\32\2\u030c\u030d"+
		"\7\62\2\2\u030d\u0310\3\2\2\2\u030e\u0310\5\64\33\2\u030f\u02f0\3\2\2"+
		"\2\u030f\u02fc\3\2\2\2\u030f\u0303\3\2\2\2\u030f\u0305\3\2\2\2\u030f\u030a"+
		"\3\2\2\2\u030f\u030e\3\2\2\2\u0310\u033d\3\2\2\2\u0311\u0312\f\22\2\2"+
		"\u0312\u0313\t\5\2\2\u0313\u033c\5\62\32\23\u0314\u0315\f\21\2\2\u0315"+
		"\u0316\7\35\2\2\u0316\u033c\5\62\32\22\u0317\u0318\f\20\2\2\u0318\u0319"+
		"\7\37\2\2\u0319\u033c\5\62\32\21\u031a\u031b\f\17\2\2\u031b\u031c\7\36"+
		"\2\2\u031c\u033c\5\62\32\20\u031d\u031e\f\16\2\2\u031e\u031f\t\6\2\2\u031f"+
		"\u033c\5\62\32\17\u0320\u0321\f\r\2\2\u0321\u0322\t\7\2\2\u0322\u033c"+
		"\5\62\32\16\u0323\u0324\f\f\2\2\u0324\u0325\7H\2\2\u0325\u033c\5\62\32"+
		"\r\u0326\u0327\f\13\2\2\u0327\u0328\t\b\2\2\u0328\u033c\5\62\32\f\u0329"+
		"\u032a\f\n\2\2\u032a\u032b\t\t\2\2\u032b\u033c\5\62\32\13\u032c\u032d"+
		"\f\t\2\2\u032d\u032e\t\n\2\2\u032e\u033c\5\62\32\n\u032f\u0330\f\b\2\2"+
		"\u0330\u0331\t\13\2\2\u0331\u033c\5\62\32\t\u0332\u0333\f\7\2\2\u0333"+
		"\u0334\t\f\2\2\u0334\u033c\5\62\32\b\u0335\u0336\f\6\2\2\u0336\u0337\7"+
		"+\2\2\u0337\u0338\5\62\32\2\u0338\u0339\7,\2\2\u0339\u033a\5\62\32\7\u033a"+
		"\u033c\3\2\2\2\u033b\u0311\3\2\2\2\u033b\u0314\3\2\2\2\u033b\u0317\3\2"+
		"\2\2\u033b\u031a\3\2\2\2\u033b\u031d\3\2\2\2\u033b\u0320\3\2\2\2\u033b"+
		"\u0323\3\2\2\2\u033b\u0326\3\2\2\2\u033b\u0329\3\2\2\2\u033b\u032c\3\2"+
		"\2\2\u033b\u032f\3\2\2\2\u033b\u0332\3\2\2\2\u033b\u0335\3\2\2\2\u033c"+
		"\u033f\3\2\2\2\u033d\u033b\3\2\2\2\u033d\u033e\3\2\2\2\u033e\63\3\2\2"+
		"\2\u033f\u033d\3\2\2\2\u0340\u034d\7\r\2\2\u0341\u034d\7\f\2\2\u0342\u034d"+
		"\7\13\2\2\u0343\u034d\7\n\2\2\u0344\u034d\7\t\2\2\u0345\u034d\5$\23\2"+
		"\u0346\u0347\5$\23\2\u0347\u0348\t\r\2\2\u0348\u034d\3\2\2\2\u0349\u034a"+
		"\t\r\2\2\u034a\u034d\5$\23\2\u034b\u034d\5 \21\2\u034c\u0340\3\2\2\2\u034c"+
		"\u0341\3\2\2\2\u034c\u0342\3\2\2\2\u034c\u0343\3\2\2\2\u034c\u0344\3\2"+
		"\2\2\u034c\u0345\3\2\2\2\u034c\u0346\3\2\2\2\u034c\u0349\3\2\2\2\u034c"+
		"\u034b\3\2\2\2\u034d\65\3\2\2\2\u034e\u0350\78\2\2\u034f\u034e\3\2\2\2"+
		"\u0350\u0351\3\2\2\2\u0351\u034f\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0356"+
		"\3\2\2\2\u0353\u0355\7\7\2\2\u0354\u0353\3\2\2\2\u0355\u0358\3\2\2\2\u0356"+
		"\u0354\3\2\2\2\u0356\u0357\3\2\2\2\u0357\u035c\3\2\2\2\u0358\u0356\3\2"+
		"\2\2\u0359\u035b\58\35\2\u035a\u0359\3\2\2\2\u035b\u035e\3\2\2\2\u035c"+
		"\u035a\3\2\2\2\u035c\u035d\3\2\2\2\u035d\67\3\2\2\2\u035e\u035c\3\2\2"+
		"\2\u035f\u0361\5:\36\2\u0360\u0362\7\7\2\2\u0361\u0360\3\2\2\2\u0362\u0363"+
		"\3\2\2\2\u0363\u0361\3\2\2\2\u0363\u0364\3\2\2\2\u0364\u043f\3\2\2\2\u0365"+
		"\u0367\7\3\2\2\u0366\u0365\3\2\2\2\u0367\u036a\3\2\2\2\u0368\u0366\3\2"+
		"\2\2\u0368\u0369\3\2\2\2\u0369\u036b\3\2\2\2\u036a\u0368\3\2\2\2\u036b"+
		"\u0371\t\16\2\2\u036c\u036f\5B\"\2\u036d\u036e\7%\2\2\u036e\u0370\5B\""+
		"\2\u036f\u036d\3\2\2\2\u036f\u0370\3\2\2\2\u0370\u0372\3\2\2\2\u0371\u036c"+
		"\3\2\2\2\u0371\u0372\3\2\2\2\u0372\u0374\3\2\2\2\u0373\u0375\7\7\2\2\u0374"+
		"\u0373\3\2\2\2\u0375\u0376\3\2\2\2\u0376\u0374\3\2\2\2\u0376\u0377\3\2"+
		"\2\2\u0377\u037b\3\2\2\2\u0378\u037a\t\2\2\2\u0379\u0378\3\2\2\2\u037a"+
		"\u037d\3\2\2\2\u037b\u0379\3\2\2\2\u037b\u037c\3\2\2\2\u037c\u043f\3\2"+
		"\2\2\u037d\u037b\3\2\2\2\u037e\u0380\7\3\2\2\u037f\u037e\3\2\2\2\u0380"+
		"\u0383\3\2\2\2\u0381\u037f\3\2\2\2\u0381\u0382\3\2\2\2\u0382\u0385\3\2"+
		"\2\2\u0383\u0381\3\2\2\2\u0384\u0386\5:\36\2\u0385\u0384\3\2\2\2\u0385"+
		"\u0386\3\2\2\2\u0386\u0387\3\2\2\2\u0387\u0389\7S\2\2\u0388\u038a\5> "+
		"\2\u0389\u0388\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u038c\3\2\2\2\u038b\u038d"+
		"\7\7\2\2\u038c\u038b\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u038c\3\2\2\2\u038e"+
		"\u038f\3\2\2\2\u038f\u0393\3\2\2\2\u0390\u0392\t\2\2\2\u0391\u0390\3\2"+
		"\2\2\u0392\u0395\3\2\2\2\u0393\u0391\3\2\2\2\u0393\u0394\3\2\2\2\u0394"+
		"\u043f\3\2\2\2\u0395\u0393\3\2\2\2\u0396\u0398\7\3\2\2\u0397\u0396\3\2"+
		"\2\2\u0398\u039b\3\2\2\2\u0399\u0397\3\2\2\2\u0399\u039a\3\2\2\2\u039a"+
		"\u039d\3\2\2\2\u039b\u0399\3\2\2\2\u039c\u039e\5:\36\2\u039d\u039c\3\2"+
		"\2\2\u039d\u039e\3\2\2\2\u039e\u039f\3\2\2\2\u039f\u03a0\7V\2\2\u03a0"+
		"\u03a5\5@!\2\u03a1\u03a2\7%\2\2\u03a2\u03a4\5@!\2\u03a3\u03a1\3\2\2\2"+
		"\u03a4\u03a7\3\2\2\2\u03a5\u03a3\3\2\2\2\u03a5\u03a6\3\2\2\2\u03a6\u03a9"+
		"\3\2\2\2\u03a7\u03a5\3\2\2\2\u03a8\u03aa\7\7\2\2\u03a9\u03a8\3\2\2\2\u03aa"+
		"\u03ab\3\2\2\2\u03ab\u03a9\3\2\2\2\u03ab\u03ac\3\2\2\2\u03ac\u03b0\3\2"+
		"\2\2\u03ad\u03af\t\2\2\2\u03ae\u03ad\3\2\2\2\u03af\u03b2\3\2\2\2\u03b0"+
		"\u03ae\3\2\2\2\u03b0\u03b1\3\2\2\2\u03b1\u043f\3\2\2\2\u03b2\u03b0\3\2"+
		"\2\2\u03b3\u03b5\7\3\2\2\u03b4\u03b3\3\2\2\2\u03b5\u03b8\3\2\2\2\u03b6"+
		"\u03b4\3\2\2\2\u03b6\u03b7\3\2\2\2\u03b7\u03ba\3\2\2\2\u03b8\u03b6\3\2"+
		"\2\2\u03b9\u03bb\5:\36\2\u03ba\u03b9\3\2\2\2\u03ba\u03bb\3\2\2\2\u03bb"+
		"\u03bc\3\2\2\2\u03bc\u03bd\7T\2\2\u03bd\u03bf\5B\"\2\u03be\u03c0\7\7\2"+
		"\2\u03bf\u03be\3\2\2\2\u03c0\u03c1\3\2\2\2\u03c1\u03bf\3\2\2\2\u03c1\u03c2"+
		"\3\2\2\2\u03c2\u03c6\3\2\2\2\u03c3\u03c5\t\2\2\2\u03c4\u03c3\3\2\2\2\u03c5"+
		"\u03c8\3\2\2\2\u03c6\u03c4\3\2\2\2\u03c6\u03c7\3\2\2\2\u03c7\u043f\3\2"+
		"\2\2\u03c8\u03c6\3\2\2\2\u03c9\u03cb\7\3\2\2\u03ca\u03c9\3\2\2\2\u03cb"+
		"\u03ce\3\2\2\2\u03cc\u03ca\3\2\2\2\u03cc\u03cd\3\2\2\2\u03cd\u03d0\3\2"+
		"\2\2\u03ce\u03cc\3\2\2\2\u03cf\u03d1\5:\36\2\u03d0\u03cf\3\2\2\2\u03d0"+
		"\u03d1\3\2\2\2\u03d1\u03d3\3\2\2\2\u03d2\u03d4\7W\2\2\u03d3\u03d2\3\2"+
		"\2\2\u03d3\u03d4\3\2\2\2\u03d4\u03d5\3\2\2\2\u03d5\u03d6\5<\37\2\u03d6"+
		"\u03d7\5> \2\u03d7\u03d8\7%\2\2\u03d8\u03d9\5> \2\u03d9\u03da\7%\2\2\u03da"+
		"\u03dc\5> \2\u03db\u03dd\7X\2\2\u03dc\u03db\3\2\2\2\u03dc\u03dd\3\2\2"+
		"\2\u03dd\u03df\3\2\2\2\u03de\u03e0\7\7\2\2\u03df\u03de\3\2\2\2\u03e0\u03e1"+
		"\3\2\2\2\u03e1\u03df\3\2\2\2\u03e1\u03e2\3\2\2\2\u03e2\u03e6\3\2\2\2\u03e3"+
		"\u03e5\t\2\2\2\u03e4\u03e3\3\2\2\2\u03e5\u03e8\3\2\2\2\u03e6\u03e4\3\2"+
		"\2\2\u03e6\u03e7\3\2\2\2\u03e7\u043f\3\2\2\2\u03e8\u03e6\3\2\2\2\u03e9"+
		"\u03eb\7\3\2\2\u03ea\u03e9\3\2\2\2\u03eb\u03ee\3\2\2\2\u03ec\u03ea\3\2"+
		"\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03f0\3\2\2\2\u03ee\u03ec\3\2\2\2\u03ef"+
		"\u03f1\5:\36\2\u03f0\u03ef\3\2\2\2\u03f0\u03f1\3\2\2\2\u03f1\u03f3\3\2"+
		"\2\2\u03f2\u03f4\7W\2\2\u03f3\u03f2\3\2\2\2\u03f3\u03f4\3\2\2\2\u03f4"+
		"\u03f5\3\2\2\2\u03f5\u03f6\5<\37\2\u03f6\u03f7\5> \2\u03f7\u03f8\7%\2"+
		"\2\u03f8\u03fa\5> \2\u03f9\u03fb\7X\2\2\u03fa\u03f9\3\2\2\2\u03fa\u03fb"+
		"\3\2\2\2\u03fb\u03fd\3\2\2\2\u03fc\u03fe\7\7\2\2\u03fd\u03fc\3\2\2\2\u03fe"+
		"\u03ff\3\2\2\2\u03ff\u03fd\3\2\2\2\u03ff\u0400\3\2\2\2\u0400\u0404\3\2"+
		"\2\2\u0401\u0403\t\2\2\2\u0402\u0401\3\2\2\2\u0403\u0406\3\2\2\2\u0404"+
		"\u0402\3\2\2\2\u0404\u0405\3\2\2\2\u0405\u043f\3\2\2\2\u0406\u0404\3\2"+
		"\2\2\u0407\u0409\7\3\2\2\u0408\u0407\3\2\2\2\u0409\u040c\3\2\2\2\u040a"+
		"\u0408\3\2\2\2\u040a\u040b\3\2\2\2\u040b\u040e\3\2\2\2\u040c\u040a\3\2"+
		"\2\2\u040d\u040f\5:\36\2\u040e\u040d\3\2\2\2\u040e\u040f\3\2\2\2\u040f"+
		"\u0411\3\2\2\2\u0410\u0412\7W\2\2\u0411\u0410\3\2\2\2\u0411\u0412\3\2"+
		"\2\2\u0412\u0413\3\2\2\2\u0413\u0414\5<\37\2\u0414\u0416\5> \2\u0415\u0417"+
		"\7X\2\2\u0416\u0415\3\2\2\2\u0416\u0417\3\2\2\2\u0417\u0419\3\2\2\2\u0418"+
		"\u041a\7\7\2\2\u0419\u0418\3\2\2\2\u041a\u041b\3\2\2\2\u041b\u0419\3\2"+
		"\2\2\u041b\u041c\3\2\2\2\u041c\u0420\3\2\2\2\u041d\u041f\t\2\2\2\u041e"+
		"\u041d\3\2\2\2\u041f\u0422\3\2\2\2\u0420\u041e\3\2\2\2\u0420\u0421\3\2"+
		"\2\2\u0421\u043f\3\2\2\2\u0422\u0420\3\2\2\2\u0423\u0425\7\3\2\2\u0424"+
		"\u0423\3\2\2\2\u0425\u0428\3\2\2\2\u0426\u0424\3\2\2\2\u0426\u0427\3\2"+
		"\2\2\u0427\u042a\3\2\2\2\u0428\u0426\3\2\2\2\u0429\u042b\5:\36\2\u042a"+
		"\u0429\3\2\2\2\u042a\u042b\3\2\2\2\u042b\u042d\3\2\2\2\u042c\u042e\7W"+
		"\2\2\u042d\u042c\3\2\2\2\u042d\u042e\3\2\2\2\u042e\u042f\3\2\2\2\u042f"+
		"\u0431\5<\37\2\u0430\u0432\7X\2\2\u0431\u0430\3\2\2\2\u0431\u0432\3\2"+
		"\2\2\u0432\u0434\3\2\2\2\u0433\u0435\7\7\2\2\u0434\u0433\3\2\2\2\u0435"+
		"\u0436\3\2\2\2\u0436\u0434\3\2\2\2\u0436\u0437\3\2\2\2\u0437\u043b\3\2"+
		"\2\2\u0438\u043a\t\2\2\2\u0439\u0438\3\2\2\2\u043a\u043d\3\2\2\2\u043b"+
		"\u0439\3\2\2\2\u043b\u043c\3\2\2\2\u043c\u043f\3\2\2\2\u043d\u043b\3\2"+
		"\2\2\u043e\u035f\3\2\2\2\u043e\u0368\3\2\2\2\u043e\u0381\3\2\2\2\u043e"+
		"\u0399\3\2\2\2\u043e\u03b6\3\2\2\2\u043e\u03cc\3\2\2\2\u043e\u03ec\3\2"+
		"\2\2\u043e\u040a\3\2\2\2\u043e\u0426\3\2\2\2\u043f9\3\2\2\2\u0440\u0442"+
		"\6\36\17\2\u0441\u0443\7$\2\2\u0442\u0441\3\2\2\2\u0442\u0443\3\2\2\2"+
		"\u0443\u0444\3\2\2\2\u0444\u0445\7Y\2\2\u0445;\3\2\2\2\u0446\u0447\6\37"+
		"\20\2\u0447\u0448\t\17\2\2\u0448=\3\2\2\2\u0449\u044b\t\20\2\2\u044a\u0449"+
		"\3\2\2\2\u044a\u044b\3\2\2\2\u044b\u044c\3\2\2\2\u044c\u044d\5B\"\2\u044d"+
		"?\3\2\2\2\u044e\u0453\5B\"\2\u044f\u0450\7/\2\2\u0450\u0451\5B\"\2\u0451"+
		"\u0452\7\60\2\2\u0452\u0454\3\2\2\2\u0453\u044f\3\2\2\2\u0453\u0454\3"+
		"\2\2\2\u0454A\3\2\2\2\u0455\u0456\b\"\1\2\u0456\u0457\t\21\2\2\u0457\u0466"+
		"\5B\"\22\u0458\u0459\7K\2\2\u0459\u045a\7\61\2\2\u045a\u045b\5B\"\2\u045b"+
		"\u045c\7\62\2\2\u045c\u0466\3\2\2\2\u045d\u045e\7\61\2\2\u045e\u045f\5"+
		"B\"\2\u045f\u0460\7\62\2\2\u0460\u0466\3\2\2\2\u0461\u0463\7 \2\2\u0462"+
		"\u0461\3\2\2\2\u0462\u0463\3\2\2\2\u0463\u0464\3\2\2\2\u0464\u0466\5D"+
		"#\2\u0465\u0455\3\2\2\2\u0465\u0458\3\2\2\2\u0465\u045d\3\2\2\2\u0465"+
		"\u0462\3\2\2\2\u0466\u0490\3\2\2\2\u0467\u0468\f\21\2\2\u0468\u0469\t"+
		"\5\2\2\u0469\u048f\5B\"\22\u046a\u046b\f\20\2\2\u046b\u046c\7\35\2\2\u046c"+
		"\u048f\5B\"\21\u046d\u046e\f\17\2\2\u046e\u046f\7\37\2\2\u046f\u048f\5"+
		"B\"\20\u0470\u0471\f\16\2\2\u0471\u0472\7\36\2\2\u0472\u048f\5B\"\17\u0473"+
		"\u0474\f\r\2\2\u0474\u0475\t\6\2\2\u0475\u048f\5B\"\16\u0476\u0477\f\f"+
		"\2\2\u0477\u0478\t\7\2\2\u0478\u048f\5B\"\r\u0479\u047a\f\13\2\2\u047a"+
		"\u047b\t\b\2\2\u047b\u048f\5B\"\f\u047c\u047d\f\n\2\2\u047d\u047e\t\t"+
		"\2\2\u047e\u048f\5B\"\13\u047f\u0480\f\t\2\2\u0480\u0481\t\n\2\2\u0481"+
		"\u048f\5B\"\n\u0482\u0483\f\b\2\2\u0483\u0484\t\13\2\2\u0484\u048f\5B"+
		"\"\t\u0485\u0486\f\7\2\2\u0486\u0487\t\f\2\2\u0487\u048f\5B\"\b\u0488"+
		"\u0489\f\6\2\2\u0489\u048a\7+\2\2\u048a\u048b\5B\"\2\u048b\u048c\7,\2"+
		"\2\u048c\u048d\5B\"\7\u048d\u048f\3\2\2\2\u048e\u0467\3\2\2\2\u048e\u046a"+
		"\3\2\2\2\u048e\u046d\3\2\2\2\u048e\u0470\3\2\2\2\u048e\u0473\3\2\2\2\u048e"+
		"\u0476\3\2\2\2\u048e\u0479\3\2\2\2\u048e\u047c\3\2\2\2\u048e\u047f\3\2"+
		"\2\2\u048e\u0482\3\2\2\2\u048e\u0485\3\2\2\2\u048e\u0488\3\2\2\2\u048f"+
		"\u0492\3\2\2\2\u0490\u048e\3\2\2\2\u0490\u0491\3\2\2\2\u0491C\3\2\2\2"+
		"\u0492\u0490\3\2\2\2\u0493\u04bd\7\r\2\2\u0494\u04bd\7\f\2\2\u0495\u04bd"+
		"\7\13\2\2\u0496\u04bd\7\n\2\2\u0497\u04bd\7\t\2\2\u0498\u04bd\7!\2\2\u0499"+
		"\u049e\7Y\2\2\u049a\u049b\7/\2\2\u049b\u049c\5B\"\2\u049c\u049d\7\60\2"+
		"\2\u049d\u049f\3\2\2\2\u049e\u049a\3\2\2\2\u049e\u049f\3\2\2\2\u049f\u04bd"+
		"\3\2\2\2\u04a0\u04a1\7Y\2\2\u04a1\u04a6\t\r\2\2\u04a2\u04a3\7/\2\2\u04a3"+
		"\u04a4\5B\"\2\u04a4\u04a5\7\60\2\2\u04a5\u04a7\3\2\2\2\u04a6\u04a2\3\2"+
		"\2\2\u04a6\u04a7\3\2\2\2\u04a7\u04bd\3\2\2\2\u04a8\u04ad\7Y\2\2\u04a9"+
		"\u04aa\7/\2\2\u04aa\u04ab\5B\"\2\u04ab\u04ac\7\60\2\2\u04ac\u04ae\3\2"+
		"\2\2\u04ad\u04a9\3\2\2\2\u04ad\u04ae\3\2\2\2\u04ae\u04af\3\2\2\2\u04af"+
		"\u04bd\t\r\2\2\u04b0\u04b1\t\r\2\2\u04b1\u04b6\7Y\2\2\u04b2\u04b3\7/\2"+
		"\2\u04b3\u04b4\5B\"\2\u04b4\u04b5\7\60\2\2\u04b5\u04b7\3\2\2\2\u04b6\u04b2"+
		"\3\2\2\2\u04b6\u04b7\3\2\2\2\u04b7\u04bd\3\2\2\2\u04b8\u04ba\7$\2\2\u04b9"+
		"\u04b8\3\2\2\2\u04b9\u04ba\3\2\2\2\u04ba\u04bb\3\2\2\2\u04bb\u04bd\7Y"+
		"\2\2\u04bc\u0493\3\2\2\2\u04bc\u0494\3\2\2\2\u04bc\u0495\3\2\2\2\u04bc"+
		"\u0496\3\2\2\2\u04bc\u0497\3\2\2\2\u04bc\u0498\3\2\2\2\u04bc\u0499\3\2"+
		"\2\2\u04bc\u04a0\3\2\2\2\u04bc\u04a8\3\2\2\2\u04bc\u04b0\3\2\2\2\u04bc"+
		"\u04b9\3\2\2\2\u04bdE\3\2\2\2\u00b1IQS[`hqtv|\u0085\u008b\u0094\u0099"+
		"\u00a0\u00a6\u00ae\u00b3\u00b9\u00be\u00c4\u00ca\u00d4\u00dc\u00e1\u00e6"+
		"\u00ee\u00f3\u00f9\u00fd\u0104\u0109\u010f\u0114\u0118\u011d\u0123\u012d"+
		"\u0135\u013d\u0141\u0144\u014b\u0151\u0156\u0159\u015e\u0162\u0169\u016d"+
		"\u0177\u017b\u0182\u019d\u01a1\u01a6\u01ac\u01b4\u01ba\u01c3\u01c9\u01cf"+
		"\u01d8\u01e2\u01e4\u01e9\u01ef\u01fa\u0200\u0204\u020c\u0212\u0216\u0218"+
		"\u0220\u0226\u022d\u0235\u023b\u0242\u024a\u0250\u0257\u025a\u0260\u0266"+
		"\u0270\u0276\u0280\u0286\u0290\u0296\u029b\u02a2\u02a7\u02a9\u02b1\u02b5"+
		"\u02ba\u02c1\u02c5\u02c9\u02d1\u02d7\u02db\u02e2\u02e8\u02ec\u02ee\u02f6"+
		"\u030f\u033b\u033d\u034c\u0351\u0356\u035c\u0363\u0368\u036f\u0371\u0376"+
		"\u037b\u0381\u0385\u0389\u038e\u0393\u0399\u039d\u03a5\u03ab\u03b0\u03b6"+
		"\u03ba\u03c1\u03c6\u03cc\u03d0\u03d3\u03dc\u03e1\u03e6\u03ec\u03f0\u03f3"+
		"\u03fa\u03ff\u0404\u040a\u040e\u0411\u0416\u041b\u0420\u0426\u042a\u042d"+
		"\u0431\u0436\u043b\u043e\u0442\u044a\u0453\u0462\u0465\u048e\u0490\u049e"+
		"\u04a6\u04ad\u04b6\u04b9\u04bc";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}