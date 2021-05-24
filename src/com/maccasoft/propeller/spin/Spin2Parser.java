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
		ASSIGN=20, ADD_ASSIGN=21, ELLIPSIS=22, EQUALS=23, NOT_EQUALS=24, BIN_AND=25, 
		BIN_OR=26, BIN_XOR=27, AT=28, POUND=29, DOLLAR=30, PERCENT=31, EQUAL=32, 
		DOT=33, COMMA=34, BACKSLASH=35, PLUS=36, MINUS=37, STAR=38, DIV=39, QUESTION=40, 
		COLON=41, TILDE=42, UNDERSCORE=43, OPEN_BRACKET=44, CLOSE_BRACKET=45, 
		OPEN_PAREN=46, CLOSE_PAREN=47, CON_START=48, VAR_START=49, OBJ_START=50, 
		PUB_START=51, PRI_START=52, DAT_START=53, REPEAT=54, FROM=55, TO=56, STEP=57, 
		WHILE=58, UNTIL=59, ELSEIFNOT=60, ELSEIF=61, ELSE=62, IFNOT=63, IF=64, 
		CASE=65, OTHER=66, ADDPINS=67, ADDBITS=68, FUNCTIONS=69, AND=70, NOT=71, 
		XOR=72, OR=73, ORG=74, ORGH=75, RES=76, ALIGN=77, TYPE=78, CONDITION=79, 
		IDENTIFIER=80;
	public static final int
		RULE_prog = 0, RULE_constantsSection = 1, RULE_constantAssign = 2, RULE_constantEnum = 3, 
		RULE_constantEnumName = 4, RULE_objectsSection = 5, RULE_object = 6, RULE_variablesSection = 7, 
		RULE_variable = 8, RULE_method = 9, RULE_parameters = 10, RULE_result = 11, 
		RULE_localvars = 12, RULE_localvar = 13, RULE_statement = 14, RULE_assignment = 15, 
		RULE_function = 16, RULE_functionArgument = 17, RULE_identifier = 18, 
		RULE_repeatLoop = 19, RULE_conditional = 20, RULE_elseConditional = 21, 
		RULE_caseConditional = 22, RULE_caseConditionalMatch = 23, RULE_caseConditionalOther = 24, 
		RULE_spinExpression = 25, RULE_experssionAtom = 26, RULE_data = 27, RULE_dataLine = 28, 
		RULE_label = 29, RULE_condition = 30, RULE_opcode = 31, RULE_argument = 32, 
		RULE_prefix = 33, RULE_effect = 34, RULE_dataValue = 35, RULE_expression = 36, 
		RULE_atom = 37;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objectsSection", "object", "variablesSection", "variable", "method", 
			"parameters", "result", "localvars", "localvar", "statement", "assignment", 
			"function", "functionArgument", "identifier", "repeatLoop", "conditional", 
			"elseConditional", "caseConditional", "caseConditionalMatch", "caseConditionalOther", 
			"spinExpression", "experssionAtom", "data", "dataLine", "label", "condition", 
			"opcode", "argument", "prefix", "effect", "dataValue", "expression", 
			"atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'&&'", "'||'", "'^^'", "'##'", "'<<'", "'>>'", "'++'", "'--'", "':='", 
			"'+='", "'..'", "'=='", "'<>'", "'&'", "'|'", "'^'", "'@'", "'#'", "'$'", 
			"'%'", "'='", "'.'", "','", "'\\'", "'+'", "'-'", "'*'", "'/'", "'?'", 
			"':'", "'~'", "'_'", "'['", "']'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INDENT", "DEDENT", "BLOCK_COMMENT", "COMMENT", "NL", "WS", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "LOGICAL_AND", "LOGICAL_OR", "LOGICAL_XOR", 
			"POUND_POUND", "LEFT_SHIFT", "RIGHT_SHIFT", "PLUS_PLUS", "MINUS_MINUS", 
			"ASSIGN", "ADD_ASSIGN", "ELLIPSIS", "EQUALS", "NOT_EQUALS", "BIN_AND", 
			"BIN_OR", "BIN_XOR", "AT", "POUND", "DOLLAR", "PERCENT", "EQUAL", "DOT", 
			"COMMA", "BACKSLASH", "PLUS", "MINUS", "STAR", "DIV", "QUESTION", "COLON", 
			"TILDE", "UNDERSCORE", "OPEN_BRACKET", "CLOSE_BRACKET", "OPEN_PAREN", 
			"CLOSE_PAREN", "CON_START", "VAR_START", "OBJ_START", "PUB_START", "PRI_START", 
			"DAT_START", "REPEAT", "FROM", "TO", "STEP", "WHILE", "UNTIL", "ELSEIFNOT", 
			"ELSEIF", "ELSE", "IFNOT", "IF", "CASE", "OTHER", "ADDPINS", "ADDBITS", 
			"FUNCTIONS", "AND", "NOT", "XOR", "OR", "ORG", "ORGH", "RES", "ALIGN", 
			"TYPE", "CONDITION", "IDENTIFIER"
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
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(76);
				match(NL);
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(87);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(82);
					constantsSection();
					}
					break;
				case OBJ_START:
					{
					setState(83);
					objectsSection();
					}
					break;
				case VAR_START:
					{
					setState(84);
					variablesSection();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(85);
					method();
					}
					break;
				case DAT_START:
					{
					setState(86);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(92);
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
			setState(95); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(94);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(97); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(99);
				match(NL);
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==POUND || _la==IDENTIFIER) {
				{
				setState(122);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					{
					setState(105);
					constantAssign();
					setState(110);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(106);
						match(COMMA);
						setState(107);
						constantAssign();
						}
						}
						setState(112);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case 2:
					{
					setState(113);
					constantEnum();
					}
					break;
				case 3:
					{
					{
					setState(114);
					constantEnumName();
					setState(119);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(115);
						match(COMMA);
						setState(116);
						constantEnumName();
						}
						}
						setState(121);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				}
				}
				setState(126);
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
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(127);
				match(INDENT);
				}
				}
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(133);
			((ConstantAssignContext)_localctx).name = match(IDENTIFIER);
			setState(134);
			match(EQUAL);
			setState(135);
			((ConstantAssignContext)_localctx).exp = expression(0);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(136);
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
				setState(141);
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
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(142);
				match(INDENT);
				}
				}
				setState(147);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(148);
			match(POUND);
			setState(149);
			((ConstantEnumContext)_localctx).start = expression(0);
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(150);
				match(OPEN_BRACKET);
				setState(151);
				((ConstantEnumContext)_localctx).step = expression(0);
				setState(152);
				match(CLOSE_BRACKET);
				}
			}

			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(156);
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
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(162);
				match(COMMA);
				setState(163);
				constantEnumName();
				}
				}
				setState(168);
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
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(169);
				match(INDENT);
				}
				}
				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(175);
			((ConstantEnumNameContext)_localctx).name = match(IDENTIFIER);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(176);
				match(OPEN_BRACKET);
				setState(177);
				((ConstantEnumNameContext)_localctx).multiplier = expression(0);
				setState(178);
				match(CLOSE_BRACKET);
				}
			}

			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(182);
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
				setState(187);
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
			setState(189); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(188);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(191); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(193);
				match(NL);
				}
				}
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==IDENTIFIER) {
				{
				{
				setState(199);
				object();
				}
				}
				setState(204);
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
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(205);
				match(INDENT);
				}
				}
				setState(210);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(211);
			((ObjectContext)_localctx).name = match(IDENTIFIER);
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(212);
				match(OPEN_BRACKET);
				setState(213);
				((ObjectContext)_localctx).count = expression(0);
				setState(214);
				match(CLOSE_BRACKET);
				}
				}
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(221);
			match(COLON);
			setState(222);
			((ObjectContext)_localctx).filename = match(STRING);
			setState(224); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(223);
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
				setState(226); 
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
			setState(229); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(228);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(231); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(233);
				match(NL);
				}
				}
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==TYPE || _la==IDENTIFIER) {
				{
				{
				setState(239);
				variable();
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(240);
					match(COMMA);
					setState(241);
					variable();
					}
					}
					setState(246);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(251);
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
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(252);
				match(INDENT);
				}
				}
				setState(257);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(259);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(258);
				((VariableContext)_localctx).type = match(TYPE);
				}
			}

			setState(261);
			((VariableContext)_localctx).name = match(IDENTIFIER);
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(262);
				match(OPEN_BRACKET);
				setState(263);
				((VariableContext)_localctx).size = expression(0);
				setState(264);
				match(CLOSE_BRACKET);
				}
			}

			setState(269); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(268);
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
				setState(271); 
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
			setState(273);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(274);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(275);
			match(OPEN_PAREN);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(276);
				parameters();
				}
			}

			setState(279);
			match(CLOSE_PAREN);
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(280);
				match(COLON);
				setState(281);
				result();
				}
			}

			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(284);
				match(BIN_OR);
				setState(285);
				localvars();
				}
			}

			setState(289); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(288);
				match(NL);
				}
				}
				setState(291); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(293);
			match(INDENT);
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
				{
				{
				setState(294);
				statement();
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(300);
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
			setState(310);
			match(IDENTIFIER);
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(311);
				match(COMMA);
				setState(312);
				match(IDENTIFIER);
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
			setState(318);
			localvar();
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(319);
				match(COMMA);
				setState(320);
				localvar();
				}
				}
				setState(325);
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
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(326);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(330);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(329);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(332);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(337);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(333);
				match(OPEN_BRACKET);
				setState(334);
				((LocalvarContext)_localctx).count = expression(0);
				setState(335);
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
			setState(354);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(339);
				assignment();
				setState(341); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(340);
					match(NL);
					}
					}
					setState(343); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(345);
				function();
				setState(347); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(346);
					match(NL);
					}
					}
					setState(349); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(351);
				repeatLoop();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(352);
				conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(353);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
		}
		public List<TerminalNode> ASSIGN() { return getTokens(Spin2Parser.ASSIGN); }
		public TerminalNode ASSIGN(int i) {
			return getToken(Spin2Parser.ASSIGN, i);
		}
		public List<TerminalNode> ADD_ASSIGN() { return getTokens(Spin2Parser.ADD_ASSIGN); }
		public TerminalNode ADD_ASSIGN(int i) {
			return getToken(Spin2Parser.ADD_ASSIGN, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
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
			setState(376);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(356);
				identifier();
				setState(357);
				_la = _input.LA(1);
				if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(358);
				spinExpression(0);
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==ASSIGN || _la==ADD_ASSIGN) {
					{
					{
					setState(359);
					_la = _input.LA(1);
					if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(360);
					spinExpression(0);
					}
					}
					setState(365);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(366);
				identifier();
				setState(369); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(367);
					match(COMMA);
					setState(368);
					match(IDENTIFIER);
					}
					}
					setState(371); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==COMMA );
				setState(373);
				_la = _input.LA(1);
				if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(374);
				spinExpression(0);
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
		enterRule(_localctx, 32, RULE_function);
		int _la;
		try {
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(379);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(378);
					match(BACKSLASH);
					}
				}

				setState(381);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(382);
				match(OPEN_PAREN);
				setState(384);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(383);
					functionArgument();
					}
				}

				setState(386);
				match(CLOSE_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(388);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(387);
					match(BACKSLASH);
					}
				}

				setState(390);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(391);
				match(DOT);
				setState(392);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(393);
				match(OPEN_PAREN);
				setState(395);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(394);
					functionArgument();
					}
				}

				setState(397);
				match(CLOSE_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(399);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(398);
					match(BACKSLASH);
					}
				}

				setState(401);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(402);
				match(OPEN_BRACKET);
				setState(403);
				((FunctionContext)_localctx).index = spinExpression(0);
				setState(404);
				match(CLOSE_BRACKET);
				setState(405);
				match(DOT);
				setState(406);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(407);
				match(OPEN_PAREN);
				setState(409);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(408);
					functionArgument();
					}
				}

				setState(411);
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
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
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
		enterRule(_localctx, 34, RULE_functionArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(415);
				assignment();
				}
				break;
			case 2:
				{
				setState(416);
				spinExpression(0);
				}
				break;
			}
			setState(426);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(419);
				match(COMMA);
				setState(422);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					setState(420);
					assignment();
					}
					break;
				case 2:
					{
					setState(421);
					spinExpression(0);
					}
					break;
				}
				}
				}
				setState(428);
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
		enterRule(_localctx, 36, RULE_identifier);
		try {
			setState(452);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(429);
				match(IDENTIFIER);
				setState(434);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(430);
					match(OPEN_BRACKET);
					setState(431);
					spinExpression(0);
					setState(432);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(436);
				match(IDENTIFIER);
				setState(437);
				match(DOT);
				setState(438);
				match(IDENTIFIER);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(439);
				match(IDENTIFIER);
				setState(440);
				match(DOT);
				setState(441);
				match(OPEN_BRACKET);
				setState(442);
				spinExpression(0);
				setState(443);
				match(CLOSE_BRACKET);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(445);
				match(IDENTIFIER);
				setState(446);
				match(DOT);
				setState(447);
				match(IDENTIFIER);
				setState(448);
				match(OPEN_BRACKET);
				setState(449);
				spinExpression(0);
				setState(450);
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
		enterRule(_localctx, 38, RULE_repeatLoop);
		int _la;
		try {
			setState(574);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(454);
				match(REPEAT);
				setState(456);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(455);
					spinExpression(0);
					}
				}

				setState(459); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(458);
					match(NL);
					}
					}
					setState(461); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(463);
				match(INDENT);
				setState(467);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(464);
					statement();
					}
					}
					setState(469);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(470);
				match(DEDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(471);
				match(REPEAT);
				setState(473); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(472);
					match(NL);
					}
					}
					setState(475); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(477);
				match(INDENT);
				setState(481);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(478);
					statement();
					}
					}
					setState(483);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(484);
				match(DEDENT);
				setState(485);
				match(WHILE);
				setState(486);
				spinExpression(0);
				setState(488); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(487);
					match(NL);
					}
					}
					setState(490); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(492);
				match(REPEAT);
				setState(494); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(493);
					match(NL);
					}
					}
					setState(496); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(498);
				match(INDENT);
				setState(502);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(499);
					statement();
					}
					}
					setState(504);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(505);
				match(DEDENT);
				setState(506);
				match(UNTIL);
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
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(513);
				match(REPEAT);
				setState(514);
				match(FROM);
				setState(515);
				spinExpression(0);
				setState(522);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(516);
					match(TO);
					setState(517);
					spinExpression(0);
					setState(520);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(518);
						match(STEP);
						setState(519);
						spinExpression(0);
						}
					}

					}
				}

				setState(525); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(524);
					match(NL);
					}
					}
					setState(527); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(529);
				match(INDENT);
				setState(533);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(530);
					statement();
					}
					}
					setState(535);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(536);
				match(DEDENT);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(538);
				match(REPEAT);
				setState(539);
				match(WHILE);
				setState(540);
				spinExpression(0);
				setState(542); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(541);
					match(NL);
					}
					}
					setState(544); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(554);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(546);
					match(INDENT);
					setState(550);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
						{
						{
						setState(547);
						statement();
						}
						}
						setState(552);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(553);
					match(DEDENT);
					}
				}

				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(556);
				match(REPEAT);
				setState(557);
				match(UNTIL);
				setState(558);
				spinExpression(0);
				setState(560); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(559);
					match(NL);
					}
					}
					setState(562); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(572);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(564);
					match(INDENT);
					setState(568);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
						{
						{
						setState(565);
						statement();
						}
						}
						setState(570);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(571);
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
		enterRule(_localctx, 40, RULE_conditional);
		int _la;
		try {
			setState(618);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(576);
				match(IF);
				setState(577);
				spinExpression(0);
				setState(579); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(578);
					match(NL);
					}
					}
					setState(581); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(583);
				match(INDENT);
				setState(587);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(584);
					statement();
					}
					}
					setState(589);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(590);
				match(DEDENT);
				setState(594);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(591);
					elseConditional();
					}
					}
					setState(596);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case IFNOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(597);
				match(IFNOT);
				setState(598);
				spinExpression(0);
				setState(600); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(599);
					match(NL);
					}
					}
					setState(602); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(604);
				match(INDENT);
				setState(608);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(605);
					statement();
					}
					}
					setState(610);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(611);
				match(DEDENT);
				setState(615);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(612);
					elseConditional();
					}
					}
					setState(617);
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
		enterRule(_localctx, 42, RULE_elseConditional);
		int _la;
		try {
			setState(666);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ELSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(620);
				match(ELSE);
				setState(622); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(621);
					match(NL);
					}
					}
					setState(624); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(626);
				match(INDENT);
				setState(630);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(627);
					statement();
					}
					}
					setState(632);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(633);
				match(DEDENT);
				}
				break;
			case ELSEIF:
				enterOuterAlt(_localctx, 2);
				{
				setState(634);
				match(ELSEIF);
				setState(635);
				spinExpression(0);
				setState(637); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(636);
					match(NL);
					}
					}
					setState(639); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(641);
				match(INDENT);
				setState(645);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(642);
					statement();
					}
					}
					setState(647);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(648);
				match(DEDENT);
				}
				break;
			case ELSEIFNOT:
				enterOuterAlt(_localctx, 3);
				{
				setState(650);
				match(ELSEIFNOT);
				setState(651);
				spinExpression(0);
				setState(653); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(652);
					match(NL);
					}
					}
					setState(655); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(657);
				match(INDENT);
				setState(661);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
					{
					{
					setState(658);
					statement();
					}
					}
					setState(663);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(664);
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
		enterRule(_localctx, 44, RULE_caseConditional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(668);
			match(CASE);
			setState(669);
			spinExpression(0);
			setState(671); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(670);
				match(NL);
				}
				}
				setState(673); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(675);
			match(INDENT);
			setState(680);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << DOLLAR) | (1L << DOT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & ((1L << (OTHER - 66)) | (1L << (FUNCTIONS - 66)) | (1L << (IDENTIFIER - 66)))) != 0)) {
				{
				setState(678);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STRING:
				case QUAD:
				case BIN:
				case HEX:
				case NUMBER:
				case PLUS_PLUS:
				case MINUS_MINUS:
				case DOLLAR:
				case DOT:
				case BACKSLASH:
				case PLUS:
				case MINUS:
				case TILDE:
				case OPEN_PAREN:
				case FUNCTIONS:
				case IDENTIFIER:
					{
					setState(676);
					caseConditionalMatch();
					}
					break;
				case OTHER:
					{
					setState(677);
					caseConditionalOther();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(682);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(683);
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
		public SpinExpressionContext spinExpression() {
			return getRuleContext(SpinExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Spin2Parser.COLON, 0); }
		public TerminalNode ELLIPSIS() { return getToken(Spin2Parser.ELLIPSIS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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
		enterRule(_localctx, 46, RULE_caseConditionalMatch);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(685);
			spinExpression(0);
			setState(688);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(686);
				match(ELLIPSIS);
				setState(687);
				expression(0);
				}
			}

			setState(690);
			match(COLON);
			setState(693);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				{
				setState(691);
				assignment();
				}
				break;
			case 2:
				{
				setState(692);
				function();
				}
				break;
			}
			setState(696); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(695);
				match(NL);
				}
				}
				setState(698); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(716);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INDENT) {
				{
				setState(700);
				match(INDENT);
				setState(712);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BACKSLASH || _la==IDENTIFIER) {
					{
					{
					setState(703);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
					case 1:
						{
						setState(701);
						assignment();
						}
						break;
					case 2:
						{
						setState(702);
						function();
						}
						break;
					}
					setState(706); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(705);
						match(NL);
						}
						}
						setState(708); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==NL );
					}
					}
					setState(714);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(715);
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
		enterRule(_localctx, 48, RULE_caseConditionalOther);
		int _la;
		try {
			setState(756);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(718);
				match(OTHER);
				setState(719);
				match(COLON);
				setState(722);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
				case 1:
					{
					setState(720);
					assignment();
					}
					break;
				case 2:
					{
					setState(721);
					function();
					}
					break;
				}
				setState(725); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(724);
					match(NL);
					}
					}
					setState(727); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(737);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(729);
					match(INDENT);
					setState(733);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
						{
						{
						setState(730);
						statement();
						}
						}
						setState(735);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(736);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(739);
				match(OTHER);
				setState(740);
				match(COLON);
				setState(742); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(741);
					match(NL);
					}
					}
					setState(744); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(754);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(746);
					match(INDENT);
					setState(750);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 35)) & ~0x3f) == 0 && ((1L << (_la - 35)) & ((1L << (BACKSLASH - 35)) | (1L << (REPEAT - 35)) | (1L << (IFNOT - 35)) | (1L << (IF - 35)) | (1L << (CASE - 35)) | (1L << (IDENTIFIER - 35)))) != 0)) {
						{
						{
						setState(747);
						statement();
						}
						}
						setState(752);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(753);
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
		public List<SpinExpressionContext> spinExpression() {
			return getRuleContexts(SpinExpressionContext.class);
		}
		public SpinExpressionContext spinExpression(int i) {
			return getRuleContext(SpinExpressionContext.class,i);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		int _startState = 50;
		enterRecursionRule(_localctx, 50, RULE_spinExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(773);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,113,_ctx) ) {
			case 1:
				{
				setState(759);
				((SpinExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TILDE))) != 0)) ) {
					((SpinExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(760);
				((SpinExpressionContext)_localctx).exp = spinExpression(18);
				}
				break;
			case 2:
				{
				setState(761);
				((SpinExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(762);
				match(OPEN_PAREN);
				setState(763);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(764);
				match(CLOSE_PAREN);
				}
				break;
			case 3:
				{
				setState(766);
				match(OPEN_PAREN);
				setState(767);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(768);
				match(CLOSE_PAREN);
				}
				break;
			case 4:
				{
				setState(770);
				atom();
				}
				break;
			case 5:
				{
				setState(771);
				identifier();
				}
				break;
			case 6:
				{
				setState(772);
				function();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(816);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(814);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
					case 1:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(775);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(776);
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
						setState(777);
						((SpinExpressionContext)_localctx).right = spinExpression(18);
						}
						break;
					case 2:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(778);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(779);
						((SpinExpressionContext)_localctx).operator = match(BIN_AND);
						setState(780);
						((SpinExpressionContext)_localctx).right = spinExpression(17);
						}
						break;
					case 3:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(781);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(782);
						((SpinExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(783);
						((SpinExpressionContext)_localctx).right = spinExpression(16);
						}
						break;
					case 4:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(784);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(785);
						((SpinExpressionContext)_localctx).operator = match(BIN_OR);
						setState(786);
						((SpinExpressionContext)_localctx).right = spinExpression(15);
						}
						break;
					case 5:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(787);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(788);
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
						setState(789);
						((SpinExpressionContext)_localctx).right = spinExpression(14);
						}
						break;
					case 6:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(790);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(791);
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
						setState(792);
						((SpinExpressionContext)_localctx).right = spinExpression(13);
						}
						break;
					case 7:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(793);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(794);
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
						setState(795);
						((SpinExpressionContext)_localctx).right = spinExpression(12);
						}
						break;
					case 8:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(796);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(797);
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
						setState(798);
						((SpinExpressionContext)_localctx).right = spinExpression(11);
						}
						break;
					case 9:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(799);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(800);
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
						setState(801);
						((SpinExpressionContext)_localctx).right = spinExpression(10);
						}
						break;
					case 10:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(802);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(803);
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
						setState(804);
						((SpinExpressionContext)_localctx).right = spinExpression(9);
						}
						break;
					case 11:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(805);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(806);
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
						setState(807);
						((SpinExpressionContext)_localctx).right = spinExpression(8);
						}
						break;
					case 12:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(808);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(809);
						((SpinExpressionContext)_localctx).operator = match(QUESTION);
						setState(810);
						((SpinExpressionContext)_localctx).middle = spinExpression(0);
						setState(811);
						((SpinExpressionContext)_localctx).operator = match(COLON);
						setState(812);
						((SpinExpressionContext)_localctx).right = spinExpression(7);
						}
						break;
					}
					} 
				}
				setState(818);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
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
		enterRule(_localctx, 52, RULE_experssionAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
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
		enterRule(_localctx, 54, RULE_data);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(822); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(821);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(824); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,116,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(829);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(826);
					match(NL);
					}
					} 
				}
				setState(831);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,117,_ctx);
			}
			setState(835);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(832);
					dataLine();
					}
					} 
				}
				setState(837);
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
		enterRule(_localctx, 56, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(1043);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,159,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(838);
				label();
				setState(840); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(839);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(842); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,119,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(847);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(844);
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
					setState(849);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(853);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(850);
					match(INDENT);
					}
					}
					setState(855);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(856);
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
				setState(862);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(857);
					expression(0);
					setState(860);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(858);
						match(COMMA);
						setState(859);
						expression(0);
						}
					}

					}
				}

				setState(865); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(864);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(867); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,124,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(872);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(869);
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
					setState(874);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(878);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(875);
						match(INDENT);
						}
						} 
					}
					setState(880);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				}
				setState(882);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
				case 1:
					{
					setState(881);
					label();
					}
					break;
				}
				setState(884);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(885);
				dataValue();
				setState(890);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(886);
					match(COMMA);
					setState(887);
					dataValue();
					}
					}
					setState(892);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(894); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(893);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(896); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(901);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(898);
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
					setState(903);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,130,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(907);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(904);
						match(INDENT);
						}
						} 
					}
					setState(909);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
				}
				setState(911);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
				case 1:
					{
					setState(910);
					label();
					}
					break;
				}
				setState(913);
				((DataLineContext)_localctx).directive = match(RES);
				setState(914);
				dataValue();
				setState(916); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(915);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(918); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(923);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(920);
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
					setState(925);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,134,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(929);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(926);
						match(INDENT);
						}
						} 
					}
					setState(931);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,135,_ctx);
				}
				setState(933);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,136,_ctx) ) {
				case 1:
					{
					setState(932);
					label();
					}
					break;
				}
				setState(936);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
				case 1:
					{
					setState(935);
					condition();
					}
					break;
				}
				setState(938);
				opcode();
				setState(939);
				argument();
				setState(940);
				match(COMMA);
				setState(941);
				argument();
				setState(942);
				match(COMMA);
				setState(943);
				argument();
				setState(945);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
				case 1:
					{
					setState(944);
					effect();
					}
					break;
				}
				setState(948); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(947);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(950); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,139,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(955);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(952);
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
					setState(957);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(961);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(958);
						match(INDENT);
						}
						} 
					}
					setState(963);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
				}
				setState(965);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
				case 1:
					{
					setState(964);
					label();
					}
					break;
				}
				setState(968);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,143,_ctx) ) {
				case 1:
					{
					setState(967);
					condition();
					}
					break;
				}
				setState(970);
				opcode();
				setState(971);
				argument();
				setState(972);
				match(COMMA);
				setState(973);
				argument();
				setState(975);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
				case 1:
					{
					setState(974);
					effect();
					}
					break;
				}
				setState(978); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(977);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(980); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,145,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(985);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,146,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(982);
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
					setState(987);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,146,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(991);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,147,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(988);
						match(INDENT);
						}
						} 
					}
					setState(993);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,147,_ctx);
				}
				setState(995);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,148,_ctx) ) {
				case 1:
					{
					setState(994);
					label();
					}
					break;
				}
				setState(998);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
				case 1:
					{
					setState(997);
					condition();
					}
					break;
				}
				setState(1000);
				opcode();
				setState(1001);
				argument();
				setState(1003);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
				case 1:
					{
					setState(1002);
					effect();
					}
					break;
				}
				setState(1006); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1005);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1008); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,151,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1013);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,152,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1010);
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
					setState(1015);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,152,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1019);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1016);
						match(INDENT);
						}
						} 
					}
					setState(1021);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,153,_ctx);
				}
				setState(1023);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,154,_ctx) ) {
				case 1:
					{
					setState(1022);
					label();
					}
					break;
				}
				setState(1026);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,155,_ctx) ) {
				case 1:
					{
					setState(1025);
					condition();
					}
					break;
				}
				setState(1028);
				opcode();
				setState(1030);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
				case 1:
					{
					setState(1029);
					effect();
					}
					break;
				}
				setState(1033); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(1032);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1035); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,157,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1040);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1037);
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
					setState(1042);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
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
		enterRule(_localctx, 58, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1045);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(1047);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(1046);
				match(DOT);
				}
			}

			setState(1049);
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
		enterRule(_localctx, 60, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1051);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1052);
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
		public TerminalNode XOR() { return getToken(Spin2Parser.XOR, 0); }
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
		enterRule(_localctx, 62, RULE_opcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1054);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1055);
			_la = _input.LA(1);
			if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (AND - 70)) | (1L << (XOR - 70)) | (1L << (OR - 70)) | (1L << (IDENTIFIER - 70)))) != 0)) ) {
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
		enterRule(_localctx, 64, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1058);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==POUND_POUND || _la==POUND) {
				{
				setState(1057);
				prefix();
				}
			}

			setState(1060);
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
		enterRule(_localctx, 66, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1062);
			_la = _input.LA(1);
			if ( !(_la==POUND_POUND || _la==POUND) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(1064);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(1063);
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
		enterRule(_localctx, 68, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1066);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(1067);
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
		enterRule(_localctx, 70, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1069);
			expression(0);
			setState(1074);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1070);
				match(OPEN_BRACKET);
				setState(1071);
				((DataValueContext)_localctx).count = expression(0);
				setState(1072);
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1092);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1077);
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
				setState(1078);
				((ExpressionContext)_localctx).exp = expression(16);
				}
				break;
			case FUNCTIONS:
				{
				setState(1079);
				((ExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1080);
				match(OPEN_PAREN);
				setState(1081);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1082);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1084);
				match(OPEN_PAREN);
				setState(1085);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(1086);
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
				setState(1089);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1088);
					match(AT);
					}
				}

				setState(1091);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1135);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,167,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1133);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1094);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1095);
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
						setState(1096);
						((ExpressionContext)_localctx).right = expression(16);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1097);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1098);
						((ExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1099);
						((ExpressionContext)_localctx).right = expression(15);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1100);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1101);
						((ExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1102);
						((ExpressionContext)_localctx).right = expression(14);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1103);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1104);
						((ExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1105);
						((ExpressionContext)_localctx).right = expression(13);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1106);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1107);
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
						setState(1108);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1109);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1110);
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
						setState(1111);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1112);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1113);
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
						setState(1114);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1115);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1116);
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
						setState(1117);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1118);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1119);
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
						setState(1120);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1121);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1122);
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
						setState(1123);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1124);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1125);
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
						setState(1126);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 12:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(1127);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1128);
						((ExpressionContext)_localctx).operator = match(QUESTION);
						setState(1129);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(1130);
						((ExpressionContext)_localctx).operator = match(COLON);
						setState(1131);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(1137);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,167,_ctx);
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
		enterRule(_localctx, 74, RULE_atom);
		int _la;
		try {
			setState(1179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1138);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1139);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1140);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1141);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1142);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1143);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1144);
				match(IDENTIFIER);
				setState(1149);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,168,_ctx) ) {
				case 1:
					{
					setState(1145);
					match(OPEN_BRACKET);
					setState(1146);
					expression(0);
					setState(1147);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1151);
				match(IDENTIFIER);
				setState(1152);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1157);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
				case 1:
					{
					setState(1153);
					match(OPEN_BRACKET);
					setState(1154);
					expression(0);
					setState(1155);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1159);
				match(IDENTIFIER);
				setState(1164);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1160);
					match(OPEN_BRACKET);
					setState(1161);
					expression(0);
					setState(1162);
					match(CLOSE_BRACKET);
					}
				}

				setState(1166);
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
				setState(1167);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1168);
				match(IDENTIFIER);
				setState(1173);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
				case 1:
					{
					setState(1169);
					match(OPEN_BRACKET);
					setState(1170);
					expression(0);
					setState(1171);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1175);
					match(DOT);
					}
				}

				setState(1178);
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
			return spinExpression_sempred((SpinExpressionContext)_localctx, predIndex);
		case 29:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 30:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 31:
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 34:
			return effect_sempred((EffectContext)_localctx, predIndex);
		case 36:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean spinExpression_sempred(SpinExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 17);
		case 1:
			return precpred(_ctx, 16);
		case 2:
			return precpred(_ctx, 15);
		case 3:
			return precpred(_ctx, 14);
		case 4:
			return precpred(_ctx, 13);
		case 5:
			return precpred(_ctx, 12);
		case 6:
			return precpred(_ctx, 11);
		case 7:
			return precpred(_ctx, 10);
		case 8:
			return precpred(_ctx, 9);
		case 9:
			return precpred(_ctx, 8);
		case 10:
			return precpred(_ctx, 7);
		case 11:
			return precpred(_ctx, 6);
		}
		return true;
	}
	private boolean label_sempred(LabelContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return _input.LT(1).getCharPositionInLine() == 0;
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return _input.LT(1).getCharPositionInLine() != 0;
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
	private boolean effect_sempred(EffectContext _localctx, int predIndex) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3R\u04a0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\3\2\7\2P\n\2\f\2\16\2S\13"+
		"\2\3\2\3\2\3\2\3\2\3\2\7\2Z\n\2\f\2\16\2]\13\2\3\2\3\2\3\3\6\3b\n\3\r"+
		"\3\16\3c\3\3\7\3g\n\3\f\3\16\3j\13\3\3\3\3\3\3\3\7\3o\n\3\f\3\16\3r\13"+
		"\3\3\3\3\3\3\3\3\3\7\3x\n\3\f\3\16\3{\13\3\7\3}\n\3\f\3\16\3\u0080\13"+
		"\3\3\4\7\4\u0083\n\4\f\4\16\4\u0086\13\4\3\4\3\4\3\4\3\4\7\4\u008c\n\4"+
		"\f\4\16\4\u008f\13\4\3\5\7\5\u0092\n\5\f\5\16\5\u0095\13\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\5\5\u009d\n\5\3\5\7\5\u00a0\n\5\f\5\16\5\u00a3\13\5\3\5\3"+
		"\5\7\5\u00a7\n\5\f\5\16\5\u00aa\13\5\3\6\7\6\u00ad\n\6\f\6\16\6\u00b0"+
		"\13\6\3\6\3\6\3\6\3\6\3\6\5\6\u00b7\n\6\3\6\7\6\u00ba\n\6\f\6\16\6\u00bd"+
		"\13\6\3\7\6\7\u00c0\n\7\r\7\16\7\u00c1\3\7\7\7\u00c5\n\7\f\7\16\7\u00c8"+
		"\13\7\3\7\7\7\u00cb\n\7\f\7\16\7\u00ce\13\7\3\b\7\b\u00d1\n\b\f\b\16\b"+
		"\u00d4\13\b\3\b\3\b\3\b\3\b\3\b\7\b\u00db\n\b\f\b\16\b\u00de\13\b\3\b"+
		"\3\b\3\b\6\b\u00e3\n\b\r\b\16\b\u00e4\3\t\6\t\u00e8\n\t\r\t\16\t\u00e9"+
		"\3\t\7\t\u00ed\n\t\f\t\16\t\u00f0\13\t\3\t\3\t\3\t\7\t\u00f5\n\t\f\t\16"+
		"\t\u00f8\13\t\7\t\u00fa\n\t\f\t\16\t\u00fd\13\t\3\n\7\n\u0100\n\n\f\n"+
		"\16\n\u0103\13\n\3\n\5\n\u0106\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u010d\n\n\3"+
		"\n\6\n\u0110\n\n\r\n\16\n\u0111\3\13\3\13\3\13\3\13\5\13\u0118\n\13\3"+
		"\13\3\13\3\13\5\13\u011d\n\13\3\13\3\13\5\13\u0121\n\13\3\13\6\13\u0124"+
		"\n\13\r\13\16\13\u0125\3\13\3\13\7\13\u012a\n\13\f\13\16\13\u012d\13\13"+
		"\3\13\3\13\3\f\3\f\3\f\7\f\u0134\n\f\f\f\16\f\u0137\13\f\3\r\3\r\3\r\7"+
		"\r\u013c\n\r\f\r\16\r\u013f\13\r\3\16\3\16\3\16\7\16\u0144\n\16\f\16\16"+
		"\16\u0147\13\16\3\17\5\17\u014a\n\17\3\17\5\17\u014d\n\17\3\17\3\17\3"+
		"\17\3\17\3\17\5\17\u0154\n\17\3\20\3\20\6\20\u0158\n\20\r\20\16\20\u0159"+
		"\3\20\3\20\6\20\u015e\n\20\r\20\16\20\u015f\3\20\3\20\3\20\5\20\u0165"+
		"\n\20\3\21\3\21\3\21\3\21\3\21\7\21\u016c\n\21\f\21\16\21\u016f\13\21"+
		"\3\21\3\21\3\21\6\21\u0174\n\21\r\21\16\21\u0175\3\21\3\21\3\21\5\21\u017b"+
		"\n\21\3\22\5\22\u017e\n\22\3\22\3\22\3\22\5\22\u0183\n\22\3\22\3\22\5"+
		"\22\u0187\n\22\3\22\3\22\3\22\3\22\3\22\5\22\u018e\n\22\3\22\3\22\5\22"+
		"\u0192\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u019c\n\22\3"+
		"\22\3\22\5\22\u01a0\n\22\3\23\3\23\5\23\u01a4\n\23\3\23\3\23\3\23\5\23"+
		"\u01a9\n\23\7\23\u01ab\n\23\f\23\16\23\u01ae\13\23\3\24\3\24\3\24\3\24"+
		"\3\24\5\24\u01b5\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u01c7\n\24\3\25\3\25\5\25\u01cb\n"+
		"\25\3\25\6\25\u01ce\n\25\r\25\16\25\u01cf\3\25\3\25\7\25\u01d4\n\25\f"+
		"\25\16\25\u01d7\13\25\3\25\3\25\3\25\6\25\u01dc\n\25\r\25\16\25\u01dd"+
		"\3\25\3\25\7\25\u01e2\n\25\f\25\16\25\u01e5\13\25\3\25\3\25\3\25\3\25"+
		"\6\25\u01eb\n\25\r\25\16\25\u01ec\3\25\3\25\6\25\u01f1\n\25\r\25\16\25"+
		"\u01f2\3\25\3\25\7\25\u01f7\n\25\f\25\16\25\u01fa\13\25\3\25\3\25\3\25"+
		"\3\25\6\25\u0200\n\25\r\25\16\25\u0201\3\25\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\5\25\u020b\n\25\5\25\u020d\n\25\3\25\6\25\u0210\n\25\r\25\16\25\u0211"+
		"\3\25\3\25\7\25\u0216\n\25\f\25\16\25\u0219\13\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\6\25\u0221\n\25\r\25\16\25\u0222\3\25\3\25\7\25\u0227\n\25"+
		"\f\25\16\25\u022a\13\25\3\25\5\25\u022d\n\25\3\25\3\25\3\25\3\25\6\25"+
		"\u0233\n\25\r\25\16\25\u0234\3\25\3\25\7\25\u0239\n\25\f\25\16\25\u023c"+
		"\13\25\3\25\5\25\u023f\n\25\5\25\u0241\n\25\3\26\3\26\3\26\6\26\u0246"+
		"\n\26\r\26\16\26\u0247\3\26\3\26\7\26\u024c\n\26\f\26\16\26\u024f\13\26"+
		"\3\26\3\26\7\26\u0253\n\26\f\26\16\26\u0256\13\26\3\26\3\26\3\26\6\26"+
		"\u025b\n\26\r\26\16\26\u025c\3\26\3\26\7\26\u0261\n\26\f\26\16\26\u0264"+
		"\13\26\3\26\3\26\7\26\u0268\n\26\f\26\16\26\u026b\13\26\5\26\u026d\n\26"+
		"\3\27\3\27\6\27\u0271\n\27\r\27\16\27\u0272\3\27\3\27\7\27\u0277\n\27"+
		"\f\27\16\27\u027a\13\27\3\27\3\27\3\27\3\27\6\27\u0280\n\27\r\27\16\27"+
		"\u0281\3\27\3\27\7\27\u0286\n\27\f\27\16\27\u0289\13\27\3\27\3\27\3\27"+
		"\3\27\3\27\6\27\u0290\n\27\r\27\16\27\u0291\3\27\3\27\7\27\u0296\n\27"+
		"\f\27\16\27\u0299\13\27\3\27\3\27\5\27\u029d\n\27\3\30\3\30\3\30\6\30"+
		"\u02a2\n\30\r\30\16\30\u02a3\3\30\3\30\3\30\7\30\u02a9\n\30\f\30\16\30"+
		"\u02ac\13\30\3\30\3\30\3\31\3\31\3\31\5\31\u02b3\n\31\3\31\3\31\3\31\5"+
		"\31\u02b8\n\31\3\31\6\31\u02bb\n\31\r\31\16\31\u02bc\3\31\3\31\3\31\5"+
		"\31\u02c2\n\31\3\31\6\31\u02c5\n\31\r\31\16\31\u02c6\7\31\u02c9\n\31\f"+
		"\31\16\31\u02cc\13\31\3\31\5\31\u02cf\n\31\3\32\3\32\3\32\3\32\5\32\u02d5"+
		"\n\32\3\32\6\32\u02d8\n\32\r\32\16\32\u02d9\3\32\3\32\7\32\u02de\n\32"+
		"\f\32\16\32\u02e1\13\32\3\32\5\32\u02e4\n\32\3\32\3\32\3\32\6\32\u02e9"+
		"\n\32\r\32\16\32\u02ea\3\32\3\32\7\32\u02ef\n\32\f\32\16\32\u02f2\13\32"+
		"\3\32\5\32\u02f5\n\32\5\32\u02f7\n\32\3\33\3\33\3\33\3\33\3\33\3\33\3"+
		"\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u0308\n\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\7\33\u0331\n\33\f\33\16"+
		"\33\u0334\13\33\3\34\3\34\3\35\6\35\u0339\n\35\r\35\16\35\u033a\3\35\7"+
		"\35\u033e\n\35\f\35\16\35\u0341\13\35\3\35\7\35\u0344\n\35\f\35\16\35"+
		"\u0347\13\35\3\36\3\36\6\36\u034b\n\36\r\36\16\36\u034c\3\36\7\36\u0350"+
		"\n\36\f\36\16\36\u0353\13\36\3\36\7\36\u0356\n\36\f\36\16\36\u0359\13"+
		"\36\3\36\3\36\3\36\3\36\5\36\u035f\n\36\5\36\u0361\n\36\3\36\6\36\u0364"+
		"\n\36\r\36\16\36\u0365\3\36\7\36\u0369\n\36\f\36\16\36\u036c\13\36\3\36"+
		"\7\36\u036f\n\36\f\36\16\36\u0372\13\36\3\36\5\36\u0375\n\36\3\36\3\36"+
		"\3\36\3\36\7\36\u037b\n\36\f\36\16\36\u037e\13\36\3\36\6\36\u0381\n\36"+
		"\r\36\16\36\u0382\3\36\7\36\u0386\n\36\f\36\16\36\u0389\13\36\3\36\7\36"+
		"\u038c\n\36\f\36\16\36\u038f\13\36\3\36\5\36\u0392\n\36\3\36\3\36\3\36"+
		"\6\36\u0397\n\36\r\36\16\36\u0398\3\36\7\36\u039c\n\36\f\36\16\36\u039f"+
		"\13\36\3\36\7\36\u03a2\n\36\f\36\16\36\u03a5\13\36\3\36\5\36\u03a8\n\36"+
		"\3\36\5\36\u03ab\n\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\5\36\u03b4\n"+
		"\36\3\36\6\36\u03b7\n\36\r\36\16\36\u03b8\3\36\7\36\u03bc\n\36\f\36\16"+
		"\36\u03bf\13\36\3\36\7\36\u03c2\n\36\f\36\16\36\u03c5\13\36\3\36\5\36"+
		"\u03c8\n\36\3\36\5\36\u03cb\n\36\3\36\3\36\3\36\3\36\3\36\5\36\u03d2\n"+
		"\36\3\36\6\36\u03d5\n\36\r\36\16\36\u03d6\3\36\7\36\u03da\n\36\f\36\16"+
		"\36\u03dd\13\36\3\36\7\36\u03e0\n\36\f\36\16\36\u03e3\13\36\3\36\5\36"+
		"\u03e6\n\36\3\36\5\36\u03e9\n\36\3\36\3\36\3\36\5\36\u03ee\n\36\3\36\6"+
		"\36\u03f1\n\36\r\36\16\36\u03f2\3\36\7\36\u03f6\n\36\f\36\16\36\u03f9"+
		"\13\36\3\36\7\36\u03fc\n\36\f\36\16\36\u03ff\13\36\3\36\5\36\u0402\n\36"+
		"\3\36\5\36\u0405\n\36\3\36\3\36\5\36\u0409\n\36\3\36\6\36\u040c\n\36\r"+
		"\36\16\36\u040d\3\36\7\36\u0411\n\36\f\36\16\36\u0414\13\36\5\36\u0416"+
		"\n\36\3\37\3\37\5\37\u041a\n\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\5\"\u0425"+
		"\n\"\3\"\3\"\3#\3#\5#\u042b\n#\3$\3$\3$\3%\3%\3%\3%\3%\5%\u0435\n%\3&"+
		"\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\5&\u0444\n&\3&\5&\u0447\n&\3&\3&"+
		"\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&"+
		"\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\7&\u0470\n&\f&\16&\u0473\13"+
		"&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0480\n\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\5\'\u0488\n\'\3\'\3\'\3\'\3\'\3\'\5\'\u048f\n\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\5\'\u0498\n\'\3\'\5\'\u049b\n\'\3\'\5\'\u049e\n\'"+
		"\3\'\2\4\64J(\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\668:<>@BDFHJL\2\23\4\2\4\4\7\7\3\2\65\66\3\2\26\27\4\2&\',,\3\2\22\23"+
		"\3\2()\3\2&\'\3\2EF\3\2\31\32\4\2\16\16HH\4\2\20\20JJ\4\2\17\17KK\3\2"+
		"\t\r\3\2LM\5\2HHJKRR\4\2\21\21\37\37\3\2\24\25\2\u0560\2Q\3\2\2\2\4a\3"+
		"\2\2\2\6\u0084\3\2\2\2\b\u0093\3\2\2\2\n\u00ae\3\2\2\2\f\u00bf\3\2\2\2"+
		"\16\u00d2\3\2\2\2\20\u00e7\3\2\2\2\22\u0101\3\2\2\2\24\u0113\3\2\2\2\26"+
		"\u0130\3\2\2\2\30\u0138\3\2\2\2\32\u0140\3\2\2\2\34\u0149\3\2\2\2\36\u0164"+
		"\3\2\2\2 \u017a\3\2\2\2\"\u019f\3\2\2\2$\u01a3\3\2\2\2&\u01c6\3\2\2\2"+
		"(\u0240\3\2\2\2*\u026c\3\2\2\2,\u029c\3\2\2\2.\u029e\3\2\2\2\60\u02af"+
		"\3\2\2\2\62\u02f6\3\2\2\2\64\u0307\3\2\2\2\66\u0335\3\2\2\28\u0338\3\2"+
		"\2\2:\u0415\3\2\2\2<\u0417\3\2\2\2>\u041d\3\2\2\2@\u0420\3\2\2\2B\u0424"+
		"\3\2\2\2D\u0428\3\2\2\2F\u042c\3\2\2\2H\u042f\3\2\2\2J\u0446\3\2\2\2L"+
		"\u049d\3\2\2\2NP\7\7\2\2ON\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR\3\2\2\2R[\3\2"+
		"\2\2SQ\3\2\2\2TZ\5\4\3\2UZ\5\f\7\2VZ\5\20\t\2WZ\5\24\13\2XZ\58\35\2YT"+
		"\3\2\2\2YU\3\2\2\2YV\3\2\2\2YW\3\2\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2["+
		"\\\3\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7\2\2\3_\3\3\2\2\2`b\7\62\2\2a`\3\2"+
		"\2\2bc\3\2\2\2ca\3\2\2\2cd\3\2\2\2dh\3\2\2\2eg\7\7\2\2fe\3\2\2\2gj\3\2"+
		"\2\2hf\3\2\2\2hi\3\2\2\2i~\3\2\2\2jh\3\2\2\2kp\5\6\4\2lm\7$\2\2mo\5\6"+
		"\4\2nl\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q}\3\2\2\2rp\3\2\2\2s}\5\b"+
		"\5\2ty\5\n\6\2uv\7$\2\2vx\5\n\6\2wu\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2"+
		"\2\2z}\3\2\2\2{y\3\2\2\2|k\3\2\2\2|s\3\2\2\2|t\3\2\2\2}\u0080\3\2\2\2"+
		"~|\3\2\2\2~\177\3\2\2\2\177\5\3\2\2\2\u0080~\3\2\2\2\u0081\u0083\7\3\2"+
		"\2\u0082\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085"+
		"\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0084\3\2\2\2\u0087\u0088\7R\2\2\u0088"+
		"\u0089\7\"\2\2\u0089\u008d\5J&\2\u008a\u008c\t\2\2\2\u008b\u008a\3\2\2"+
		"\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\7"+
		"\3\2\2\2\u008f\u008d\3\2\2\2\u0090\u0092\7\3\2\2\u0091\u0090\3\2\2\2\u0092"+
		"\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0096\3\2"+
		"\2\2\u0095\u0093\3\2\2\2\u0096\u0097\7\37\2\2\u0097\u009c\5J&\2\u0098"+
		"\u0099\7.\2\2\u0099\u009a\5J&\2\u009a\u009b\7/\2\2\u009b\u009d\3\2\2\2"+
		"\u009c\u0098\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u00a1\3\2\2\2\u009e\u00a0"+
		"\t\2\2\2\u009f\u009e\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1"+
		"\u00a2\3\2\2\2\u00a2\u00a8\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7$"+
		"\2\2\u00a5\u00a7\5\n\6\2\u00a6\u00a4\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8"+
		"\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\t\3\2\2\2\u00aa\u00a8\3\2\2\2"+
		"\u00ab\u00ad\7\3\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac"+
		"\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1"+
		"\u00b6\7R\2\2\u00b2\u00b3\7.\2\2\u00b3\u00b4\5J&\2\u00b4\u00b5\7/\2\2"+
		"\u00b5\u00b7\3\2\2\2\u00b6\u00b2\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00bb"+
		"\3\2\2\2\u00b8\u00ba\t\2\2\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb"+
		"\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\13\3\2\2\2\u00bd\u00bb\3\2\2"+
		"\2\u00be\u00c0\7\64\2\2\u00bf\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c6\3\2\2\2\u00c3\u00c5\7\7"+
		"\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6"+
		"\u00c7\3\2\2\2\u00c7\u00cc\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c9\u00cb\5\16"+
		"\b\2\u00ca\u00c9\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc"+
		"\u00cd\3\2\2\2\u00cd\r\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d1\7\3\2\2"+
		"\u00d0\u00cf\3\2\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3"+
		"\3\2\2\2\u00d3\u00d5\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5\u00dc\7R\2\2\u00d6"+
		"\u00d7\7.\2\2\u00d7\u00d8\5J&\2\u00d8\u00d9\7/\2\2\u00d9\u00db\3\2\2\2"+
		"\u00da\u00d6\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd"+
		"\3\2\2\2\u00dd\u00df\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e0\7+\2\2\u00e0"+
		"\u00e2\7\t\2\2\u00e1\u00e3\t\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e4\3\2"+
		"\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\17\3\2\2\2\u00e6\u00e8"+
		"\7\63\2\2\u00e7\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00e7\3\2\2\2"+
		"\u00e9\u00ea\3\2\2\2\u00ea\u00ee\3\2\2\2\u00eb\u00ed\7\7\2\2\u00ec\u00eb"+
		"\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef"+
		"\u00fb\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f6\5\22\n\2\u00f2\u00f3\7"+
		"$\2\2\u00f3\u00f5\5\22\n\2\u00f4\u00f2\3\2\2\2\u00f5\u00f8\3\2\2\2\u00f6"+
		"\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00fa\3\2\2\2\u00f8\u00f6\3\2"+
		"\2\2\u00f9\u00f1\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb"+
		"\u00fc\3\2\2\2\u00fc\21\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u0100\7\3\2"+
		"\2\u00ff\u00fe\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0102"+
		"\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u0106\7P\2\2\u0105"+
		"\u0104\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u010c\7R"+
		"\2\2\u0108\u0109\7.\2\2\u0109\u010a\5J&\2\u010a\u010b\7/\2\2\u010b\u010d"+
		"\3\2\2\2\u010c\u0108\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010f\3\2\2\2\u010e"+
		"\u0110\t\2\2\2\u010f\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2"+
		"\2\2\u0111\u0112\3\2\2\2\u0112\23\3\2\2\2\u0113\u0114\t\3\2\2\u0114\u0115"+
		"\7R\2\2\u0115\u0117\7\60\2\2\u0116\u0118\5\26\f\2\u0117\u0116\3\2\2\2"+
		"\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011c\7\61\2\2\u011a\u011b"+
		"\7+\2\2\u011b\u011d\5\30\r\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d"+
		"\u0120\3\2\2\2\u011e\u011f\7\34\2\2\u011f\u0121\5\32\16\2\u0120\u011e"+
		"\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0123\3\2\2\2\u0122\u0124\7\7\2\2\u0123"+
		"\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2"+
		"\2\2\u0126\u0127\3\2\2\2\u0127\u012b\7\3\2\2\u0128\u012a\5\36\20\2\u0129"+
		"\u0128\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b\u012c\3\2"+
		"\2\2\u012c\u012e\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u012f\7\4\2\2\u012f"+
		"\25\3\2\2\2\u0130\u0135\7R\2\2\u0131\u0132\7$\2\2\u0132\u0134\7R\2\2\u0133"+
		"\u0131\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2"+
		"\2\2\u0136\27\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u013d\7R\2\2\u0139\u013a"+
		"\7$\2\2\u013a\u013c\7R\2\2\u013b\u0139\3\2\2\2\u013c\u013f\3\2\2\2\u013d"+
		"\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\31\3\2\2\2\u013f\u013d\3\2\2"+
		"\2\u0140\u0145\5\34\17\2\u0141\u0142\7$\2\2\u0142\u0144\5\34\17\2\u0143"+
		"\u0141\3\2\2\2\u0144\u0147\3\2\2\2\u0145\u0143\3\2\2\2\u0145\u0146\3\2"+
		"\2\2\u0146\33\3\2\2\2\u0147\u0145\3\2\2\2\u0148\u014a\7O\2\2\u0149\u0148"+
		"\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014c\3\2\2\2\u014b\u014d\7P\2\2\u014c"+
		"\u014b\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u0153\7R"+
		"\2\2\u014f\u0150\7.\2\2\u0150\u0151\5J&\2\u0151\u0152\7/\2\2\u0152\u0154"+
		"\3\2\2\2\u0153\u014f\3\2\2\2\u0153\u0154\3\2\2\2\u0154\35\3\2\2\2\u0155"+
		"\u0157\5 \21\2\u0156\u0158\7\7\2\2\u0157\u0156\3\2\2\2\u0158\u0159\3\2"+
		"\2\2\u0159\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u0165\3\2\2\2\u015b"+
		"\u015d\5\"\22\2\u015c\u015e\7\7\2\2\u015d\u015c\3\2\2\2\u015e\u015f\3"+
		"\2\2\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0165\3\2\2\2\u0161"+
		"\u0165\5(\25\2\u0162\u0165\5*\26\2\u0163\u0165\5.\30\2\u0164\u0155\3\2"+
		"\2\2\u0164\u015b\3\2\2\2\u0164\u0161\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0163\3\2\2\2\u0165\37\3\2\2\2\u0166\u0167\5&\24\2\u0167\u0168\t\4\2"+
		"\2\u0168\u016d\5\64\33\2\u0169\u016a\t\4\2\2\u016a\u016c\5\64\33\2\u016b"+
		"\u0169\3\2\2\2\u016c\u016f\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2"+
		"\2\2\u016e\u017b\3\2\2\2\u016f\u016d\3\2\2\2\u0170\u0173\5&\24\2\u0171"+
		"\u0172\7$\2\2\u0172\u0174\7R\2\2\u0173\u0171\3\2\2\2\u0174\u0175\3\2\2"+
		"\2\u0175\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0178"+
		"\t\4\2\2\u0178\u0179\5\64\33\2\u0179\u017b\3\2\2\2\u017a\u0166\3\2\2\2"+
		"\u017a\u0170\3\2\2\2\u017b!\3\2\2\2\u017c\u017e\7%\2\2\u017d\u017c\3\2"+
		"\2\2\u017d\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\7R\2\2\u0180"+
		"\u0182\7\60\2\2\u0181\u0183\5$\23\2\u0182\u0181\3\2\2\2\u0182\u0183\3"+
		"\2\2\2\u0183\u0184\3\2\2\2\u0184\u01a0\7\61\2\2\u0185\u0187\7%\2\2\u0186"+
		"\u0185\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0189\7R"+
		"\2\2\u0189\u018a\7#\2\2\u018a\u018b\7R\2\2\u018b\u018d\7\60\2\2\u018c"+
		"\u018e\5$\23\2\u018d\u018c\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018f\3\2"+
		"\2\2\u018f\u01a0\7\61\2\2\u0190\u0192\7%\2\2\u0191\u0190\3\2\2\2\u0191"+
		"\u0192\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0194\7R\2\2\u0194\u0195\7.\2"+
		"\2\u0195\u0196\5\64\33\2\u0196\u0197\7/\2\2\u0197\u0198\7#\2\2\u0198\u0199"+
		"\7R\2\2\u0199\u019b\7\60\2\2\u019a\u019c\5$\23\2\u019b\u019a\3\2\2\2\u019b"+
		"\u019c\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019e\7\61\2\2\u019e\u01a0\3"+
		"\2\2\2\u019f\u017d\3\2\2\2\u019f\u0186\3\2\2\2\u019f\u0191\3\2\2\2\u01a0"+
		"#\3\2\2\2\u01a1\u01a4\5 \21\2\u01a2\u01a4\5\64\33\2\u01a3\u01a1\3\2\2"+
		"\2\u01a3\u01a2\3\2\2\2\u01a4\u01ac\3\2\2\2\u01a5\u01a8\7$\2\2\u01a6\u01a9"+
		"\5 \21\2\u01a7\u01a9\5\64\33\2\u01a8\u01a6\3\2\2\2\u01a8\u01a7\3\2\2\2"+
		"\u01a9\u01ab\3\2\2\2\u01aa\u01a5\3\2\2\2\u01ab\u01ae\3\2\2\2\u01ac\u01aa"+
		"\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad%\3\2\2\2\u01ae\u01ac\3\2\2\2\u01af"+
		"\u01b4\7R\2\2\u01b0\u01b1\7.\2\2\u01b1\u01b2\5\64\33\2\u01b2\u01b3\7/"+
		"\2\2\u01b3\u01b5\3\2\2\2\u01b4\u01b0\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5"+
		"\u01c7\3\2\2\2\u01b6\u01b7\7R\2\2\u01b7\u01b8\7#\2\2\u01b8\u01c7\7R\2"+
		"\2\u01b9\u01ba\7R\2\2\u01ba\u01bb\7#\2\2\u01bb\u01bc\7.\2\2\u01bc\u01bd"+
		"\5\64\33\2\u01bd\u01be\7/\2\2\u01be\u01c7\3\2\2\2\u01bf\u01c0\7R\2\2\u01c0"+
		"\u01c1\7#\2\2\u01c1\u01c2\7R\2\2\u01c2\u01c3\7.\2\2\u01c3\u01c4\5\64\33"+
		"\2\u01c4\u01c5\7/\2\2\u01c5\u01c7\3\2\2\2\u01c6\u01af\3\2\2\2\u01c6\u01b6"+
		"\3\2\2\2\u01c6\u01b9\3\2\2\2\u01c6\u01bf\3\2\2\2\u01c7\'\3\2\2\2\u01c8"+
		"\u01ca\78\2\2\u01c9\u01cb\5\64\33\2\u01ca\u01c9\3\2\2\2\u01ca\u01cb\3"+
		"\2\2\2\u01cb\u01cd\3\2\2\2\u01cc\u01ce\7\7\2\2\u01cd\u01cc\3\2\2\2\u01ce"+
		"\u01cf\3\2\2\2\u01cf\u01cd\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d1\3\2"+
		"\2\2\u01d1\u01d5\7\3\2\2\u01d2\u01d4\5\36\20\2\u01d3\u01d2\3\2\2\2\u01d4"+
		"\u01d7\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d8\3\2"+
		"\2\2\u01d7\u01d5\3\2\2\2\u01d8\u0241\7\4\2\2\u01d9\u01db\78\2\2\u01da"+
		"\u01dc\7\7\2\2\u01db\u01da\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd\u01db\3\2"+
		"\2\2\u01dd\u01de\3\2\2\2\u01de\u01df\3\2\2\2\u01df\u01e3\7\3\2\2\u01e0"+
		"\u01e2\5\36\20\2\u01e1\u01e0\3\2\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e1\3"+
		"\2\2\2\u01e3\u01e4\3\2\2\2\u01e4\u01e6\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e6"+
		"\u01e7\7\4\2\2\u01e7\u01e8\7<\2\2\u01e8\u01ea\5\64\33\2\u01e9\u01eb\7"+
		"\7\2\2\u01ea\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ea\3\2\2\2\u01ec"+
		"\u01ed\3\2\2\2\u01ed\u0241\3\2\2\2\u01ee\u01f0\78\2\2\u01ef\u01f1\7\7"+
		"\2\2\u01f0\u01ef\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2\u01f0\3\2\2\2\u01f2"+
		"\u01f3\3\2\2\2\u01f3\u01f4\3\2\2\2\u01f4\u01f8\7\3\2\2\u01f5\u01f7\5\36"+
		"\20\2\u01f6\u01f5\3\2\2\2\u01f7\u01fa\3\2\2\2\u01f8\u01f6\3\2\2\2\u01f8"+
		"\u01f9\3\2\2\2\u01f9\u01fb\3\2\2\2\u01fa\u01f8\3\2\2\2\u01fb\u01fc\7\4"+
		"\2\2\u01fc\u01fd\7=\2\2\u01fd\u01ff\5\64\33\2\u01fe\u0200\7\7\2\2\u01ff"+
		"\u01fe\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2"+
		"\2\2\u0202\u0241\3\2\2\2\u0203\u0204\78\2\2\u0204\u0205\79\2\2\u0205\u020c"+
		"\5\64\33\2\u0206\u0207\7:\2\2\u0207\u020a\5\64\33\2\u0208\u0209\7;\2\2"+
		"\u0209\u020b\5\64\33\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b\u020d"+
		"\3\2\2\2\u020c\u0206\3\2\2\2\u020c\u020d\3\2\2\2\u020d\u020f\3\2\2\2\u020e"+
		"\u0210\7\7\2\2\u020f\u020e\3\2\2\2\u0210\u0211\3\2\2\2\u0211\u020f\3\2"+
		"\2\2\u0211\u0212\3\2\2\2\u0212\u0213\3\2\2\2\u0213\u0217\7\3\2\2\u0214"+
		"\u0216\5\36\20\2\u0215\u0214\3\2\2\2\u0216\u0219\3\2\2\2\u0217\u0215\3"+
		"\2\2\2\u0217\u0218\3\2\2\2\u0218\u021a\3\2\2\2\u0219\u0217\3\2\2\2\u021a"+
		"\u021b\7\4\2\2\u021b\u0241\3\2\2\2\u021c\u021d\78\2\2\u021d\u021e\7<\2"+
		"\2\u021e\u0220\5\64\33\2\u021f\u0221\7\7\2\2\u0220\u021f\3\2\2\2\u0221"+
		"\u0222\3\2\2\2\u0222\u0220\3\2\2\2\u0222\u0223\3\2\2\2\u0223\u022c\3\2"+
		"\2\2\u0224\u0228\7\3\2\2\u0225\u0227\5\36\20\2\u0226\u0225\3\2\2\2\u0227"+
		"\u022a\3\2\2\2\u0228\u0226\3\2\2\2\u0228\u0229\3\2\2\2\u0229\u022b\3\2"+
		"\2\2\u022a\u0228\3\2\2\2\u022b\u022d\7\4\2\2\u022c\u0224\3\2\2\2\u022c"+
		"\u022d\3\2\2\2\u022d\u0241\3\2\2\2\u022e\u022f\78\2\2\u022f\u0230\7=\2"+
		"\2\u0230\u0232\5\64\33\2\u0231\u0233\7\7\2\2\u0232\u0231\3\2\2\2\u0233"+
		"\u0234\3\2\2\2\u0234\u0232\3\2\2\2\u0234\u0235\3\2\2\2\u0235\u023e\3\2"+
		"\2\2\u0236\u023a\7\3\2\2\u0237\u0239\5\36\20\2\u0238\u0237\3\2\2\2\u0239"+
		"\u023c\3\2\2\2\u023a\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u023d\3\2"+
		"\2\2\u023c\u023a\3\2\2\2\u023d\u023f\7\4\2\2\u023e\u0236\3\2\2\2\u023e"+
		"\u023f\3\2\2\2\u023f\u0241\3\2\2\2\u0240\u01c8\3\2\2\2\u0240\u01d9\3\2"+
		"\2\2\u0240\u01ee\3\2\2\2\u0240\u0203\3\2\2\2\u0240\u021c\3\2\2\2\u0240"+
		"\u022e\3\2\2\2\u0241)\3\2\2\2\u0242\u0243\7B\2\2\u0243\u0245\5\64\33\2"+
		"\u0244\u0246\7\7\2\2\u0245\u0244\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0245"+
		"\3\2\2\2\u0247\u0248\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024d\7\3\2\2\u024a"+
		"\u024c\5\36\20\2\u024b\u024a\3\2\2\2\u024c\u024f\3\2\2\2\u024d\u024b\3"+
		"\2\2\2\u024d\u024e\3\2\2\2\u024e\u0250\3\2\2\2\u024f\u024d\3\2\2\2\u0250"+
		"\u0254\7\4\2\2\u0251\u0253\5,\27\2\u0252\u0251\3\2\2\2\u0253\u0256\3\2"+
		"\2\2\u0254\u0252\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u026d\3\2\2\2\u0256"+
		"\u0254\3\2\2\2\u0257\u0258\7A\2\2\u0258\u025a\5\64\33\2\u0259\u025b\7"+
		"\7\2\2\u025a\u0259\3\2\2\2\u025b\u025c\3\2\2\2\u025c\u025a\3\2\2\2\u025c"+
		"\u025d\3\2\2\2\u025d\u025e\3\2\2\2\u025e\u0262\7\3\2\2\u025f\u0261\5\36"+
		"\20\2\u0260\u025f\3\2\2\2\u0261\u0264\3\2\2\2\u0262\u0260\3\2\2\2\u0262"+
		"\u0263\3\2\2\2\u0263\u0265\3\2\2\2\u0264\u0262\3\2\2\2\u0265\u0269\7\4"+
		"\2\2\u0266\u0268\5,\27\2\u0267\u0266\3\2\2\2\u0268\u026b\3\2\2\2\u0269"+
		"\u0267\3\2\2\2\u0269\u026a\3\2\2\2\u026a\u026d\3\2\2\2\u026b\u0269\3\2"+
		"\2\2\u026c\u0242\3\2\2\2\u026c\u0257\3\2\2\2\u026d+\3\2\2\2\u026e\u0270"+
		"\7@\2\2\u026f\u0271\7\7\2\2\u0270\u026f\3\2\2\2\u0271\u0272\3\2\2\2\u0272"+
		"\u0270\3\2\2\2\u0272\u0273\3\2\2\2\u0273\u0274\3\2\2\2\u0274\u0278\7\3"+
		"\2\2\u0275\u0277\5\36\20\2\u0276\u0275\3\2\2\2\u0277\u027a\3\2\2\2\u0278"+
		"\u0276\3\2\2\2\u0278\u0279\3\2\2\2\u0279\u027b\3\2\2\2\u027a\u0278\3\2"+
		"\2\2\u027b\u029d\7\4\2\2\u027c\u027d\7?\2\2\u027d\u027f\5\64\33\2\u027e"+
		"\u0280\7\7\2\2\u027f\u027e\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u027f\3\2"+
		"\2\2\u0281\u0282\3\2\2\2\u0282\u0283\3\2\2\2\u0283\u0287\7\3\2\2\u0284"+
		"\u0286\5\36\20\2\u0285\u0284\3\2\2\2\u0286\u0289\3\2\2\2\u0287\u0285\3"+
		"\2\2\2\u0287\u0288\3\2\2\2\u0288\u028a\3\2\2\2\u0289\u0287\3\2\2\2\u028a"+
		"\u028b\7\4\2\2\u028b\u029d\3\2\2\2\u028c\u028d\7>\2\2\u028d\u028f\5\64"+
		"\33\2\u028e\u0290\7\7\2\2\u028f\u028e\3\2\2\2\u0290\u0291\3\2\2\2\u0291"+
		"\u028f\3\2\2\2\u0291\u0292\3\2\2\2\u0292\u0293\3\2\2\2\u0293\u0297\7\3"+
		"\2\2\u0294\u0296\5\36\20\2\u0295\u0294\3\2\2\2\u0296\u0299\3\2\2\2\u0297"+
		"\u0295\3\2\2\2\u0297\u0298\3\2\2\2\u0298\u029a\3\2\2\2\u0299\u0297\3\2"+
		"\2\2\u029a\u029b\7\4\2\2\u029b\u029d\3\2\2\2\u029c\u026e\3\2\2\2\u029c"+
		"\u027c\3\2\2\2\u029c\u028c\3\2\2\2\u029d-\3\2\2\2\u029e\u029f\7C\2\2\u029f"+
		"\u02a1\5\64\33\2\u02a0\u02a2\7\7\2\2\u02a1\u02a0\3\2\2\2\u02a2\u02a3\3"+
		"\2\2\2\u02a3\u02a1\3\2\2\2\u02a3\u02a4\3\2\2\2\u02a4\u02a5\3\2\2\2\u02a5"+
		"\u02aa\7\3\2\2\u02a6\u02a9\5\60\31\2\u02a7\u02a9\5\62\32\2\u02a8\u02a6"+
		"\3\2\2\2\u02a8\u02a7\3\2\2\2\u02a9\u02ac\3\2\2\2\u02aa\u02a8\3\2\2\2\u02aa"+
		"\u02ab\3\2\2\2\u02ab\u02ad\3\2\2\2\u02ac\u02aa\3\2\2\2\u02ad\u02ae\7\4"+
		"\2\2\u02ae/\3\2\2\2\u02af\u02b2\5\64\33\2\u02b0\u02b1\7\30\2\2\u02b1\u02b3"+
		"\5J&\2\u02b2\u02b0\3\2\2\2\u02b2\u02b3\3\2\2\2\u02b3\u02b4\3\2\2\2\u02b4"+
		"\u02b7\7+\2\2\u02b5\u02b8\5 \21\2\u02b6\u02b8\5\"\22\2\u02b7\u02b5\3\2"+
		"\2\2\u02b7\u02b6\3\2\2\2\u02b7\u02b8\3\2\2\2\u02b8\u02ba\3\2\2\2\u02b9"+
		"\u02bb\7\7\2\2\u02ba\u02b9\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02ba\3\2"+
		"\2\2\u02bc\u02bd\3\2\2\2\u02bd\u02ce\3\2\2\2\u02be\u02ca\7\3\2\2\u02bf"+
		"\u02c2\5 \21\2\u02c0\u02c2\5\"\22\2\u02c1\u02bf\3\2\2\2\u02c1\u02c0\3"+
		"\2\2\2\u02c2\u02c4\3\2\2\2\u02c3\u02c5\7\7\2\2\u02c4\u02c3\3\2\2\2\u02c5"+
		"\u02c6\3\2\2\2\u02c6\u02c4\3\2\2\2\u02c6\u02c7\3\2\2\2\u02c7\u02c9\3\2"+
		"\2\2\u02c8\u02c1\3\2\2\2\u02c9\u02cc\3\2\2\2\u02ca\u02c8\3\2\2\2\u02ca"+
		"\u02cb\3\2\2\2\u02cb\u02cd\3\2\2\2\u02cc\u02ca\3\2\2\2\u02cd\u02cf\7\4"+
		"\2\2\u02ce\u02be\3\2\2\2\u02ce\u02cf\3\2\2\2\u02cf\61\3\2\2\2\u02d0\u02d1"+
		"\7D\2\2\u02d1\u02d4\7+\2\2\u02d2\u02d5\5 \21\2\u02d3\u02d5\5\"\22\2\u02d4"+
		"\u02d2\3\2\2\2\u02d4\u02d3\3\2\2\2\u02d5\u02d7\3\2\2\2\u02d6\u02d8\7\7"+
		"\2\2\u02d7\u02d6\3\2\2\2\u02d8\u02d9\3\2\2\2\u02d9\u02d7\3\2\2\2\u02d9"+
		"\u02da\3\2\2\2\u02da\u02e3\3\2\2\2\u02db\u02df\7\3\2\2\u02dc\u02de\5\36"+
		"\20\2\u02dd\u02dc\3\2\2\2\u02de\u02e1\3\2\2\2\u02df\u02dd\3\2\2\2\u02df"+
		"\u02e0\3\2\2\2\u02e0\u02e2\3\2\2\2\u02e1\u02df\3\2\2\2\u02e2\u02e4\7\4"+
		"\2\2\u02e3\u02db\3\2\2\2\u02e3\u02e4\3\2\2\2\u02e4\u02f7\3\2\2\2\u02e5"+
		"\u02e6\7D\2\2\u02e6\u02e8\7+\2\2\u02e7\u02e9\7\7\2\2\u02e8\u02e7\3\2\2"+
		"\2\u02e9\u02ea\3\2\2\2\u02ea\u02e8\3\2\2\2\u02ea\u02eb\3\2\2\2\u02eb\u02f4"+
		"\3\2\2\2\u02ec\u02f0\7\3\2\2\u02ed\u02ef\5\36\20\2\u02ee\u02ed\3\2\2\2"+
		"\u02ef\u02f2\3\2\2\2\u02f0\u02ee\3\2\2\2\u02f0\u02f1\3\2\2\2\u02f1\u02f3"+
		"\3\2\2\2\u02f2\u02f0\3\2\2\2\u02f3\u02f5\7\4\2\2\u02f4\u02ec\3\2\2\2\u02f4"+
		"\u02f5\3\2\2\2\u02f5\u02f7\3\2\2\2\u02f6\u02d0\3\2\2\2\u02f6\u02e5\3\2"+
		"\2\2\u02f7\63\3\2\2\2\u02f8\u02f9\b\33\1\2\u02f9\u02fa\t\5\2\2\u02fa\u0308"+
		"\5\64\33\24\u02fb\u02fc\7G\2\2\u02fc\u02fd\7\60\2\2\u02fd\u02fe\5\64\33"+
		"\2\u02fe\u02ff\7\61\2\2\u02ff\u0308\3\2\2\2\u0300\u0301\7\60\2\2\u0301"+
		"\u0302\5\64\33\2\u0302\u0303\7\61\2\2\u0303\u0308\3\2\2\2\u0304\u0308"+
		"\5L\'\2\u0305\u0308\5&\24\2\u0306\u0308\5\"\22\2\u0307\u02f8\3\2\2\2\u0307"+
		"\u02fb\3\2\2\2\u0307\u0300\3\2\2\2\u0307\u0304\3\2\2\2\u0307\u0305\3\2"+
		"\2\2\u0307\u0306\3\2\2\2\u0308\u0332\3\2\2\2\u0309\u030a\f\23\2\2\u030a"+
		"\u030b\t\6\2\2\u030b\u0331\5\64\33\24\u030c\u030d\f\22\2\2\u030d\u030e"+
		"\7\33\2\2\u030e\u0331\5\64\33\23\u030f\u0310\f\21\2\2\u0310\u0311\7\35"+
		"\2\2\u0311\u0331\5\64\33\22\u0312\u0313\f\20\2\2\u0313\u0314\7\34\2\2"+
		"\u0314\u0331\5\64\33\21\u0315\u0316\f\17\2\2\u0316\u0317\t\7\2\2\u0317"+
		"\u0331\5\64\33\20\u0318\u0319\f\16\2\2\u0319\u031a\t\b\2\2\u031a\u0331"+
		"\5\64\33\17\u031b\u031c\f\r\2\2\u031c\u031d\t\t\2\2\u031d\u0331\5\64\33"+
		"\16\u031e\u031f\f\f\2\2\u031f\u0320\t\n\2\2\u0320\u0331\5\64\33\r\u0321"+
		"\u0322\f\13\2\2\u0322\u0323\t\13\2\2\u0323\u0331\5\64\33\f\u0324\u0325"+
		"\f\n\2\2\u0325\u0326\t\f\2\2\u0326\u0331\5\64\33\13\u0327\u0328\f\t\2"+
		"\2\u0328\u0329\t\r\2\2\u0329\u0331\5\64\33\n\u032a\u032b\f\b\2\2\u032b"+
		"\u032c\7*\2\2\u032c\u032d\5\64\33\2\u032d\u032e\7+\2\2\u032e\u032f\5\64"+
		"\33\t\u032f\u0331\3\2\2\2\u0330\u0309\3\2\2\2\u0330\u030c\3\2\2\2\u0330"+
		"\u030f\3\2\2\2\u0330\u0312\3\2\2\2\u0330\u0315\3\2\2\2\u0330\u0318\3\2"+
		"\2\2\u0330\u031b\3\2\2\2\u0330\u031e\3\2\2\2\u0330\u0321\3\2\2\2\u0330"+
		"\u0324\3\2\2\2\u0330\u0327\3\2\2\2\u0330\u032a\3\2\2\2\u0331\u0334\3\2"+
		"\2\2\u0332\u0330\3\2\2\2\u0332\u0333\3\2\2\2\u0333\65\3\2\2\2\u0334\u0332"+
		"\3\2\2\2\u0335\u0336\t\16\2\2\u0336\67\3\2\2\2\u0337\u0339\7\67\2\2\u0338"+
		"\u0337\3\2\2\2\u0339\u033a\3\2\2\2\u033a\u0338\3\2\2\2\u033a\u033b\3\2"+
		"\2\2\u033b\u033f\3\2\2\2\u033c\u033e\7\7\2\2\u033d\u033c\3\2\2\2\u033e"+
		"\u0341\3\2\2\2\u033f\u033d\3\2\2\2\u033f\u0340\3\2\2\2\u0340\u0345\3\2"+
		"\2\2\u0341\u033f\3\2\2\2\u0342\u0344\5:\36\2\u0343\u0342\3\2\2\2\u0344"+
		"\u0347\3\2\2\2\u0345\u0343\3\2\2\2\u0345\u0346\3\2\2\2\u03469\3\2\2\2"+
		"\u0347\u0345\3\2\2\2\u0348\u034a\5<\37\2\u0349\u034b\7\7\2\2\u034a\u0349"+
		"\3\2\2\2\u034b\u034c\3\2\2\2\u034c\u034a\3\2\2\2\u034c\u034d\3\2\2\2\u034d"+
		"\u0351\3\2\2\2\u034e\u0350\t\2\2\2\u034f\u034e\3\2\2\2\u0350\u0353\3\2"+
		"\2\2\u0351\u034f\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0416\3\2\2\2\u0353"+
		"\u0351\3\2\2\2\u0354\u0356\7\3\2\2\u0355\u0354\3\2\2\2\u0356\u0359\3\2"+
		"\2\2\u0357\u0355\3\2\2\2\u0357\u0358\3\2\2\2\u0358\u035a\3\2\2\2\u0359"+
		"\u0357\3\2\2\2\u035a\u0360\t\17\2\2\u035b\u035e\5J&\2\u035c\u035d\7$\2"+
		"\2\u035d\u035f\5J&\2\u035e\u035c\3\2\2\2\u035e\u035f\3\2\2\2\u035f\u0361"+
		"\3\2\2\2\u0360\u035b\3\2\2\2\u0360\u0361\3\2\2\2\u0361\u0363\3\2\2\2\u0362"+
		"\u0364\7\7\2\2\u0363\u0362\3\2\2\2\u0364\u0365\3\2\2\2\u0365\u0363\3\2"+
		"\2\2\u0365\u0366\3\2\2\2\u0366\u036a\3\2\2\2\u0367\u0369\t\2\2\2\u0368"+
		"\u0367\3\2\2\2\u0369\u036c\3\2\2\2\u036a\u0368\3\2\2\2\u036a\u036b\3\2"+
		"\2\2\u036b\u0416\3\2\2\2\u036c\u036a\3\2\2\2\u036d\u036f\7\3\2\2\u036e"+
		"\u036d\3\2\2\2\u036f\u0372\3\2\2\2\u0370\u036e\3\2\2\2\u0370\u0371\3\2"+
		"\2\2\u0371\u0374\3\2\2\2\u0372\u0370\3\2\2\2\u0373\u0375\5<\37\2\u0374"+
		"\u0373\3\2\2\2\u0374\u0375\3\2\2\2\u0375\u0376\3\2\2\2\u0376\u0377\7P"+
		"\2\2\u0377\u037c\5H%\2\u0378\u0379\7$\2\2\u0379\u037b\5H%\2\u037a\u0378"+
		"\3\2\2\2\u037b\u037e\3\2\2\2\u037c\u037a\3\2\2\2\u037c\u037d\3\2\2\2\u037d"+
		"\u0380\3\2\2\2\u037e\u037c\3\2\2\2\u037f\u0381\7\7\2\2\u0380\u037f\3\2"+
		"\2\2\u0381\u0382\3\2\2\2\u0382\u0380\3\2\2\2\u0382\u0383\3\2\2\2\u0383"+
		"\u0387\3\2\2\2\u0384\u0386\t\2\2\2\u0385\u0384\3\2\2\2\u0386\u0389\3\2"+
		"\2\2\u0387\u0385\3\2\2\2\u0387\u0388\3\2\2\2\u0388\u0416\3\2\2\2\u0389"+
		"\u0387\3\2\2\2\u038a\u038c\7\3\2\2\u038b\u038a\3\2\2\2\u038c\u038f\3\2"+
		"\2\2\u038d\u038b\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u0391\3\2\2\2\u038f"+
		"\u038d\3\2\2\2\u0390\u0392\5<\37\2\u0391\u0390\3\2\2\2\u0391\u0392\3\2"+
		"\2\2\u0392\u0393\3\2\2\2\u0393\u0394\7N\2\2\u0394\u0396\5H%\2\u0395\u0397"+
		"\7\7\2\2\u0396\u0395\3\2\2\2\u0397\u0398\3\2\2\2\u0398\u0396\3\2\2\2\u0398"+
		"\u0399\3\2\2\2\u0399\u039d\3\2\2\2\u039a\u039c\t\2\2\2\u039b\u039a\3\2"+
		"\2\2\u039c\u039f\3\2\2\2\u039d\u039b\3\2\2\2\u039d\u039e\3\2\2\2\u039e"+
		"\u0416\3\2\2\2\u039f\u039d\3\2\2\2\u03a0\u03a2\7\3\2\2\u03a1\u03a0\3\2"+
		"\2\2\u03a2\u03a5\3\2\2\2\u03a3\u03a1\3\2\2\2\u03a3\u03a4\3\2\2\2\u03a4"+
		"\u03a7\3\2\2\2\u03a5\u03a3\3\2\2\2\u03a6\u03a8\5<\37\2\u03a7\u03a6\3\2"+
		"\2\2\u03a7\u03a8\3\2\2\2\u03a8\u03aa\3\2\2\2\u03a9\u03ab\5> \2\u03aa\u03a9"+
		"\3\2\2\2\u03aa\u03ab\3\2\2\2\u03ab\u03ac\3\2\2\2\u03ac\u03ad\5@!\2\u03ad"+
		"\u03ae\5B\"\2\u03ae\u03af\7$\2\2\u03af\u03b0\5B\"\2\u03b0\u03b1\7$\2\2"+
		"\u03b1\u03b3\5B\"\2\u03b2\u03b4\5F$\2\u03b3\u03b2\3\2\2\2\u03b3\u03b4"+
		"\3\2\2\2\u03b4\u03b6\3\2\2\2\u03b5\u03b7\7\7\2\2\u03b6\u03b5\3\2\2\2\u03b7"+
		"\u03b8\3\2\2\2\u03b8\u03b6\3\2\2\2\u03b8\u03b9\3\2\2\2\u03b9\u03bd\3\2"+
		"\2\2\u03ba\u03bc\t\2\2\2\u03bb\u03ba\3\2\2\2\u03bc\u03bf\3\2\2\2\u03bd"+
		"\u03bb\3\2\2\2\u03bd\u03be\3\2\2\2\u03be\u0416\3\2\2\2\u03bf\u03bd\3\2"+
		"\2\2\u03c0\u03c2\7\3\2\2\u03c1\u03c0\3\2\2\2\u03c2\u03c5\3\2\2\2\u03c3"+
		"\u03c1\3\2\2\2\u03c3\u03c4\3\2\2\2\u03c4\u03c7\3\2\2\2\u03c5\u03c3\3\2"+
		"\2\2\u03c6\u03c8\5<\37\2\u03c7\u03c6\3\2\2\2\u03c7\u03c8\3\2\2\2\u03c8"+
		"\u03ca\3\2\2\2\u03c9\u03cb\5> \2\u03ca\u03c9\3\2\2\2\u03ca\u03cb\3\2\2"+
		"\2\u03cb\u03cc\3\2\2\2\u03cc\u03cd\5@!\2\u03cd\u03ce\5B\"\2\u03ce\u03cf"+
		"\7$\2\2\u03cf\u03d1\5B\"\2\u03d0\u03d2\5F$\2\u03d1\u03d0\3\2\2\2\u03d1"+
		"\u03d2\3\2\2\2\u03d2\u03d4\3\2\2\2\u03d3\u03d5\7\7\2\2\u03d4\u03d3\3\2"+
		"\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d4\3\2\2\2\u03d6\u03d7\3\2\2\2\u03d7"+
		"\u03db\3\2\2\2\u03d8\u03da\t\2\2\2\u03d9\u03d8\3\2\2\2\u03da\u03dd\3\2"+
		"\2\2\u03db\u03d9\3\2\2\2\u03db\u03dc\3\2\2\2\u03dc\u0416\3\2\2\2\u03dd"+
		"\u03db\3\2\2\2\u03de\u03e0\7\3\2\2\u03df\u03de\3\2\2\2\u03e0\u03e3\3\2"+
		"\2\2\u03e1\u03df\3\2\2\2\u03e1\u03e2\3\2\2\2\u03e2\u03e5\3\2\2\2\u03e3"+
		"\u03e1\3\2\2\2\u03e4\u03e6\5<\37\2\u03e5\u03e4\3\2\2\2\u03e5\u03e6\3\2"+
		"\2\2\u03e6\u03e8\3\2\2\2\u03e7\u03e9\5> \2\u03e8\u03e7\3\2\2\2\u03e8\u03e9"+
		"\3\2\2\2\u03e9\u03ea\3\2\2\2\u03ea\u03eb\5@!\2\u03eb\u03ed\5B\"\2\u03ec"+
		"\u03ee\5F$\2\u03ed\u03ec\3\2\2\2\u03ed\u03ee\3\2\2\2\u03ee\u03f0\3\2\2"+
		"\2\u03ef\u03f1\7\7\2\2\u03f0\u03ef\3\2\2\2\u03f1\u03f2\3\2\2\2\u03f2\u03f0"+
		"\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3\u03f7\3\2\2\2\u03f4\u03f6\t\2\2\2\u03f5"+
		"\u03f4\3\2\2\2\u03f6\u03f9\3\2\2\2\u03f7\u03f5\3\2\2\2\u03f7\u03f8\3\2"+
		"\2\2\u03f8\u0416\3\2\2\2\u03f9\u03f7\3\2\2\2\u03fa\u03fc\7\3\2\2\u03fb"+
		"\u03fa\3\2\2\2\u03fc\u03ff\3\2\2\2\u03fd\u03fb\3\2\2\2\u03fd\u03fe\3\2"+
		"\2\2\u03fe\u0401\3\2\2\2\u03ff\u03fd\3\2\2\2\u0400\u0402\5<\37\2\u0401"+
		"\u0400\3\2\2\2\u0401\u0402\3\2\2\2\u0402\u0404\3\2\2\2\u0403\u0405\5>"+
		" \2\u0404\u0403\3\2\2\2\u0404\u0405\3\2\2\2\u0405\u0406\3\2\2\2\u0406"+
		"\u0408\5@!\2\u0407\u0409\5F$\2\u0408\u0407\3\2\2\2\u0408\u0409\3\2\2\2"+
		"\u0409\u040b\3\2\2\2\u040a\u040c\7\7\2\2\u040b\u040a\3\2\2\2\u040c\u040d"+
		"\3\2\2\2\u040d\u040b\3\2\2\2\u040d\u040e\3\2\2\2\u040e\u0412\3\2\2\2\u040f"+
		"\u0411\t\2\2\2\u0410\u040f\3\2\2\2\u0411\u0414\3\2\2\2\u0412\u0410\3\2"+
		"\2\2\u0412\u0413\3\2\2\2\u0413\u0416\3\2\2\2\u0414\u0412\3\2\2\2\u0415"+
		"\u0348\3\2\2\2\u0415\u0357\3\2\2\2\u0415\u0370\3\2\2\2\u0415\u038d\3\2"+
		"\2\2\u0415\u03a3\3\2\2\2\u0415\u03c3\3\2\2\2\u0415\u03e1\3\2\2\2\u0415"+
		"\u03fd\3\2\2\2\u0416;\3\2\2\2\u0417\u0419\6\37\16\2\u0418\u041a\7#\2\2"+
		"\u0419\u0418\3\2\2\2\u0419\u041a\3\2\2\2\u041a\u041b\3\2\2\2\u041b\u041c"+
		"\7R\2\2\u041c=\3\2\2\2\u041d\u041e\6 \17\2\u041e\u041f\7Q\2\2\u041f?\3"+
		"\2\2\2\u0420\u0421\6!\20\2\u0421\u0422\t\20\2\2\u0422A\3\2\2\2\u0423\u0425"+
		"\5D#\2\u0424\u0423\3\2\2\2\u0424\u0425\3\2\2\2\u0425\u0426\3\2\2\2\u0426"+
		"\u0427\5J&\2\u0427C\3\2\2\2\u0428\u042a\t\21\2\2\u0429\u042b\7%\2\2\u042a"+
		"\u0429\3\2\2\2\u042a\u042b\3\2\2\2\u042bE\3\2\2\2\u042c\u042d\6$\21\2"+
		"\u042d\u042e\7R\2\2\u042eG\3\2\2\2\u042f\u0434\5J&\2\u0430\u0431\7.\2"+
		"\2\u0431\u0432\5J&\2\u0432\u0433\7/\2\2\u0433\u0435\3\2\2\2\u0434\u0430"+
		"\3\2\2\2\u0434\u0435\3\2\2\2\u0435I\3\2\2\2\u0436\u0437\b&\1\2\u0437\u0438"+
		"\t\5\2\2\u0438\u0447\5J&\22\u0439\u043a\7G\2\2\u043a\u043b\7\60\2\2\u043b"+
		"\u043c\5J&\2\u043c\u043d\7\61\2\2\u043d\u0447\3\2\2\2\u043e\u043f\7\60"+
		"\2\2\u043f\u0440\5J&\2\u0440\u0441\7\61\2\2\u0441\u0447\3\2\2\2\u0442"+
		"\u0444\7\36\2\2\u0443\u0442\3\2\2\2\u0443\u0444\3\2\2\2\u0444\u0445\3"+
		"\2\2\2\u0445\u0447\5L\'\2\u0446\u0436\3\2\2\2\u0446\u0439\3\2\2\2\u0446"+
		"\u043e\3\2\2\2\u0446\u0443\3\2\2\2\u0447\u0471\3\2\2\2\u0448\u0449\f\21"+
		"\2\2\u0449\u044a\t\6\2\2\u044a\u0470\5J&\22\u044b\u044c\f\20\2\2\u044c"+
		"\u044d\7\33\2\2\u044d\u0470\5J&\21\u044e\u044f\f\17\2\2\u044f\u0450\7"+
		"\35\2\2\u0450\u0470\5J&\20\u0451\u0452\f\16\2\2\u0452\u0453\7\34\2\2\u0453"+
		"\u0470\5J&\17\u0454\u0455\f\r\2\2\u0455\u0456\t\7\2\2\u0456\u0470\5J&"+
		"\16\u0457\u0458\f\f\2\2\u0458\u0459\t\b\2\2\u0459\u0470\5J&\r\u045a\u045b"+
		"\f\13\2\2\u045b\u045c\t\t\2\2\u045c\u0470\5J&\f\u045d\u045e\f\n\2\2\u045e"+
		"\u045f\t\n\2\2\u045f\u0470\5J&\13\u0460\u0461\f\t\2\2\u0461\u0462\t\13"+
		"\2\2\u0462\u0470\5J&\n\u0463\u0464\f\b\2\2\u0464\u0465\t\f\2\2\u0465\u0470"+
		"\5J&\t\u0466\u0467\f\7\2\2\u0467\u0468\t\r\2\2\u0468\u0470\5J&\b\u0469"+
		"\u046a\f\6\2\2\u046a\u046b\7*\2\2\u046b\u046c\5J&\2\u046c\u046d\7+\2\2"+
		"\u046d\u046e\5J&\7\u046e\u0470\3\2\2\2\u046f\u0448\3\2\2\2\u046f\u044b"+
		"\3\2\2\2\u046f\u044e\3\2\2\2\u046f\u0451\3\2\2\2\u046f\u0454\3\2\2\2\u046f"+
		"\u0457\3\2\2\2\u046f\u045a\3\2\2\2\u046f\u045d\3\2\2\2\u046f\u0460\3\2"+
		"\2\2\u046f\u0463\3\2\2\2\u046f\u0466\3\2\2\2\u046f\u0469\3\2\2\2\u0470"+
		"\u0473\3\2\2\2\u0471\u046f\3\2\2\2\u0471\u0472\3\2\2\2\u0472K\3\2\2\2"+
		"\u0473\u0471\3\2\2\2\u0474\u049e\7\r\2\2\u0475\u049e\7\f\2\2\u0476\u049e"+
		"\7\13\2\2\u0477\u049e\7\n\2\2\u0478\u049e\7\t\2\2\u0479\u049e\7 \2\2\u047a"+
		"\u047f\7R\2\2\u047b\u047c\7.\2\2\u047c\u047d\5J&\2\u047d\u047e\7/\2\2"+
		"\u047e\u0480\3\2\2\2\u047f\u047b\3\2\2\2\u047f\u0480\3\2\2\2\u0480\u049e"+
		"\3\2\2\2\u0481\u0482\7R\2\2\u0482\u0487\t\22\2\2\u0483\u0484\7.\2\2\u0484"+
		"\u0485\5J&\2\u0485\u0486\7/\2\2\u0486\u0488\3\2\2\2\u0487\u0483\3\2\2"+
		"\2\u0487\u0488\3\2\2\2\u0488\u049e\3\2\2\2\u0489\u048e\7R\2\2\u048a\u048b"+
		"\7.\2\2\u048b\u048c\5J&\2\u048c\u048d\7/\2\2\u048d\u048f\3\2\2\2\u048e"+
		"\u048a\3\2\2\2\u048e\u048f\3\2\2\2\u048f\u0490\3\2\2\2\u0490\u049e\t\22"+
		"\2\2\u0491\u0492\t\22\2\2\u0492\u0497\7R\2\2\u0493\u0494\7.\2\2\u0494"+
		"\u0495\5J&\2\u0495\u0496\7/\2\2\u0496\u0498\3\2\2\2\u0497\u0493\3\2\2"+
		"\2\u0497\u0498\3\2\2\2\u0498\u049e\3\2\2\2\u0499\u049b\7#\2\2\u049a\u0499"+
		"\3\2\2\2\u049a\u049b\3\2\2\2\u049b\u049c\3\2\2\2\u049c\u049e\7R\2\2\u049d"+
		"\u0474\3\2\2\2\u049d\u0475\3\2\2\2\u049d\u0476\3\2\2\2\u049d\u0477\3\2"+
		"\2\2\u049d\u0478\3\2\2\2\u049d\u0479\3\2\2\2\u049d\u047a\3\2\2\2\u049d"+
		"\u0481\3\2\2\2\u049d\u0489\3\2\2\2\u049d\u0491\3\2\2\2\u049d\u049a\3\2"+
		"\2\2\u049eM\3\2\2\2\u00b0QY[chpy|~\u0084\u008d\u0093\u009c\u00a1\u00a8"+
		"\u00ae\u00b6\u00bb\u00c1\u00c6\u00cc\u00d2\u00dc\u00e4\u00e9\u00ee\u00f6"+
		"\u00fb\u0101\u0105\u010c\u0111\u0117\u011c\u0120\u0125\u012b\u0135\u013d"+
		"\u0145\u0149\u014c\u0153\u0159\u015f\u0164\u016d\u0175\u017a\u017d\u0182"+
		"\u0186\u018d\u0191\u019b\u019f\u01a3\u01a8\u01ac\u01b4\u01c6\u01ca\u01cf"+
		"\u01d5\u01dd\u01e3\u01ec\u01f2\u01f8\u0201\u020a\u020c\u0211\u0217\u0222"+
		"\u0228\u022c\u0234\u023a\u023e\u0240\u0247\u024d\u0254\u025c\u0262\u0269"+
		"\u026c\u0272\u0278\u0281\u0287\u0291\u0297\u029c\u02a3\u02a8\u02aa\u02b2"+
		"\u02b7\u02bc\u02c1\u02c6\u02ca\u02ce\u02d4\u02d9\u02df\u02e3\u02ea\u02f0"+
		"\u02f4\u02f6\u0307\u0330\u0332\u033a\u033f\u0345\u034c\u0351\u0357\u035e"+
		"\u0360\u0365\u036a\u0370\u0374\u037c\u0382\u0387\u038d\u0391\u0398\u039d"+
		"\u03a3\u03a7\u03aa\u03b3\u03b8\u03bd\u03c3\u03c7\u03ca\u03d1\u03d6\u03db"+
		"\u03e1\u03e5\u03e8\u03ed\u03f2\u03f7\u03fd\u0401\u0404\u0408\u040d\u0412"+
		"\u0415\u0419\u0424\u042a\u0434\u0443\u0446\u046f\u0471\u047f\u0487\u048e"+
		"\u0497\u049a\u049d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}