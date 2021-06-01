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
		RULE_expressionAtom = 25, RULE_dataSection = 26, RULE_dataLine = 27, RULE_directive = 28, 
		RULE_assembler = 29, RULE_data = 30, RULE_label = 31, RULE_argument = 32, 
		RULE_dataValue = 33, RULE_constantExpression = 34, RULE_atom = 35;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constantsSection", "constantAssign", "constantEnum", "constantEnumName", 
			"objectsSection", "object", "variablesSection", "variable", "method", 
			"parameters", "result", "localvars", "localvar", "statement", "function", 
			"functionArgument", "identifier", "repeatLoop", "conditional", "elseConditional", 
			"caseConditional", "caseConditionalMatch", "caseConditionalOther", "spinExpression", 
			"expressionAtom", "dataSection", "dataLine", "directive", "assembler", 
			"data", "label", "argument", "dataValue", "constantExpression", "atom"
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
		public List<DataSectionContext> dataSection() {
			return getRuleContexts(DataSectionContext.class);
		}
		public DataSectionContext dataSection(int i) {
			return getRuleContext(DataSectionContext.class,i);
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
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(72);
				match(NL);
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(83);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(78);
					constantsSection();
					}
					break;
				case OBJ_START:
					{
					setState(79);
					objectsSection();
					}
					break;
				case VAR_START:
					{
					setState(80);
					variablesSection();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(81);
					method();
					}
					break;
				case DAT_START:
					{
					setState(82);
					dataSection();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
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
			setState(91); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(90);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(93); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(95);
				match(NL);
				}
				}
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==LITERAL || _la==IDENTIFIER) {
				{
				setState(118);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					{
					setState(101);
					constantAssign();
					setState(106);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(102);
						match(COMMA);
						setState(103);
						constantAssign();
						}
						}
						setState(108);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case 2:
					{
					setState(109);
					constantEnum();
					}
					break;
				case 3:
					{
					{
					setState(110);
					constantEnumName();
					setState(115);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(111);
						match(COMMA);
						setState(112);
						constantEnumName();
						}
						}
						setState(117);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				}
				}
				setState(122);
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
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(123);
				match(INDENT);
				}
				}
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(129);
			((ConstantAssignContext)_localctx).name = match(IDENTIFIER);
			setState(130);
			match(EQUAL);
			setState(131);
			((ConstantAssignContext)_localctx).exp = constantExpression(0);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(132);
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
				setState(137);
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
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(138);
				match(INDENT);
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(144);
			match(LITERAL);
			setState(145);
			((ConstantEnumContext)_localctx).start = constantExpression(0);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(146);
				match(OPEN_BRACKET);
				setState(147);
				((ConstantEnumContext)_localctx).step = constantExpression(0);
				setState(148);
				match(CLOSE_BRACKET);
				}
			}

			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(152);
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
				setState(157);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(158);
				match(COMMA);
				setState(159);
				constantEnumName();
				}
				}
				setState(164);
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
			setState(168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(165);
				match(INDENT);
				}
				}
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(171);
			((ConstantEnumNameContext)_localctx).name = match(IDENTIFIER);
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(172);
				match(OPEN_BRACKET);
				setState(173);
				((ConstantEnumNameContext)_localctx).multiplier = constantExpression(0);
				setState(174);
				match(CLOSE_BRACKET);
				}
			}

			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEDENT || _la==NL) {
				{
				{
				setState(178);
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
				setState(183);
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
			setState(185); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(184);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(187); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(189);
				match(NL);
				}
				}
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==IDENTIFIER) {
				{
				{
				setState(195);
				object();
				}
				}
				setState(200);
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
			setState(204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(201);
				match(INDENT);
				}
				}
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(207);
			((ObjectContext)_localctx).name = match(IDENTIFIER);
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(208);
				match(OPEN_BRACKET);
				setState(209);
				((ObjectContext)_localctx).count = constantExpression(0);
				setState(210);
				match(CLOSE_BRACKET);
				}
				}
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217);
			match(COLON);
			setState(218);
			((ObjectContext)_localctx).filename = match(STRING);
			setState(220); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(219);
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
				setState(222); 
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
			setState(225); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(224);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(227); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(229);
				match(NL);
				}
				}
				setState(234);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT || _la==TYPE || _la==IDENTIFIER) {
				{
				{
				setState(235);
				variable();
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(236);
					match(COMMA);
					setState(237);
					variable();
					}
					}
					setState(242);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(247);
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
			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INDENT) {
				{
				{
				setState(248);
				match(INDENT);
				}
				}
				setState(253);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(254);
				((VariableContext)_localctx).type = match(TYPE);
				}
			}

			setState(257);
			((VariableContext)_localctx).name = match(IDENTIFIER);
			setState(262);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(258);
				match(OPEN_BRACKET);
				setState(259);
				((VariableContext)_localctx).size = constantExpression(0);
				setState(260);
				match(CLOSE_BRACKET);
				}
			}

			setState(265); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(264);
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
				setState(267); 
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
			setState(269);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(270);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(271);
			match(OPEN_PAREN);
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(272);
				parameters();
				}
			}

			setState(275);
			match(CLOSE_PAREN);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(276);
				match(COLON);
				setState(277);
				result();
				}
			}

			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(280);
				match(BIN_OR);
				setState(281);
				localvars();
				}
			}

			setState(285); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(284);
				match(NL);
				}
				}
				setState(287); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(289);
			match(INDENT);
			setState(293);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
				{
				{
				setState(290);
				statement();
				}
				}
				setState(295);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(296);
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
			setState(298);
			match(IDENTIFIER);
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(299);
				match(COMMA);
				setState(300);
				match(IDENTIFIER);
				}
				}
				setState(305);
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
			setState(306);
			match(IDENTIFIER);
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(307);
				match(COMMA);
				setState(308);
				match(IDENTIFIER);
				}
				}
				setState(313);
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
			setState(314);
			localvar();
			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(315);
				match(COMMA);
				setState(316);
				localvar();
				}
				}
				setState(321);
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
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(322);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(325);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(328);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(329);
				match(OPEN_BRACKET);
				setState(330);
				((LocalvarContext)_localctx).count = constantExpression(0);
				setState(331);
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
			setState(344);
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
				setState(335);
				spinExpression(0);
				setState(337); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(336);
					match(NL);
					}
					}
					setState(339); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case REPEAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(341);
				repeatLoop();
				}
				break;
			case IFNOT:
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(342);
				conditional();
				}
				break;
			case CASE:
				enterOuterAlt(_localctx, 4);
				{
				setState(343);
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
			setState(381);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(346);
					match(BACKSLASH);
					}
				}

				setState(349);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(350);
				match(OPEN_PAREN);
				setState(352);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(351);
					functionArgument();
					}
				}

				setState(354);
				match(CLOSE_PAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(356);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(355);
					match(BACKSLASH);
					}
				}

				setState(358);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(359);
				match(DOT);
				setState(360);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(361);
				match(OPEN_PAREN);
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(362);
					functionArgument();
					}
				}

				setState(365);
				match(CLOSE_PAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==BACKSLASH) {
					{
					setState(366);
					match(BACKSLASH);
					}
				}

				setState(369);
				((FunctionContext)_localctx).obj = match(IDENTIFIER);
				setState(370);
				match(OPEN_BRACKET);
				setState(371);
				((FunctionContext)_localctx).index = spinExpression(0);
				setState(372);
				match(CLOSE_BRACKET);
				setState(373);
				match(DOT);
				setState(374);
				((FunctionContext)_localctx).name = match(IDENTIFIER);
				setState(375);
				match(OPEN_PAREN);
				setState(377);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(376);
					functionArgument();
					}
				}

				setState(379);
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
			setState(383);
			spinExpression(0);
			setState(388);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(384);
				match(COMMA);
				setState(385);
				spinExpression(0);
				}
				}
				setState(390);
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
			setState(415);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(391);
				match(AT);
				setState(392);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(393);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(394);
				match(DOT);
				setState(395);
				match(IDENTIFIER);
				setState(396);
				match(OPEN_BRACKET);
				setState(397);
				spinExpression(0);
				setState(398);
				match(CLOSE_BRACKET);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(400);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(401);
				match(DOT);
				setState(402);
				match(OPEN_BRACKET);
				setState(403);
				spinExpression(0);
				setState(404);
				match(CLOSE_BRACKET);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(406);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(407);
				match(DOT);
				setState(408);
				match(IDENTIFIER);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(409);
				((IdentifierContext)_localctx).name = match(IDENTIFIER);
				setState(410);
				match(OPEN_BRACKET);
				setState(411);
				spinExpression(0);
				setState(412);
				match(CLOSE_BRACKET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(414);
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
			setState(549);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				match(REPEAT);
				setState(419);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					setState(418);
					spinExpression(0);
					}
				}

				setState(422); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(421);
					match(NL);
					}
					}
					setState(424); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(436);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(426);
					match(INDENT);
					setState(430);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(427);
							statement();
							}
							} 
						}
						setState(432);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
					}
					setState(434);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
					case 1:
						{
						setState(433);
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
				setState(438);
				match(REPEAT);
				setState(440); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(439);
					match(NL);
					}
					}
					setState(442); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(444);
				match(INDENT);
				setState(448);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(445);
					statement();
					}
					}
					setState(450);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(451);
				match(DEDENT);
				setState(452);
				match(WHILE);
				setState(453);
				spinExpression(0);
				setState(455); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(454);
					match(NL);
					}
					}
					setState(457); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(459);
				match(REPEAT);
				setState(461); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(460);
					match(NL);
					}
					}
					setState(463); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(465);
				match(INDENT);
				setState(469);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(466);
					statement();
					}
					}
					setState(471);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(472);
				match(DEDENT);
				setState(473);
				match(UNTIL);
				setState(474);
				spinExpression(0);
				setState(476); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(475);
					match(NL);
					}
					}
					setState(478); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(480);
				match(REPEAT);
				setState(481);
				identifier();
				setState(482);
				match(FROM);
				setState(483);
				spinExpression(0);
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(484);
					match(TO);
					setState(485);
					spinExpression(0);
					setState(488);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==STEP) {
						{
						setState(486);
						match(STEP);
						setState(487);
						spinExpression(0);
						}
					}

					}
				}

				setState(493); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(492);
					match(NL);
					}
					}
					setState(495); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(507);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(497);
					match(INDENT);
					setState(501);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(498);
							statement();
							}
							} 
						}
						setState(503);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
					}
					setState(505);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
					case 1:
						{
						setState(504);
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
				setState(509);
				match(REPEAT);
				setState(510);
				match(WHILE);
				setState(511);
				spinExpression(0);
				setState(513); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(512);
					match(NL);
					}
					}
					setState(515); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(527);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(517);
					match(INDENT);
					setState(521);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(518);
							statement();
							}
							} 
						}
						setState(523);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
					}
					setState(525);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
					case 1:
						{
						setState(524);
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
				setState(529);
				match(REPEAT);
				setState(530);
				match(UNTIL);
				setState(531);
				spinExpression(0);
				setState(533); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(532);
					match(NL);
					}
					}
					setState(535); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(547);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(537);
					match(INDENT);
					setState(541);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(538);
							statement();
							}
							} 
						}
						setState(543);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
					}
					setState(545);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
					case 1:
						{
						setState(544);
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
			setState(615);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(551);
				match(IF);
				setState(552);
				match(NOT);
				setState(553);
				spinExpression(0);
				setState(555); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(554);
					match(NL);
					}
					}
					setState(557); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(559);
				match(INDENT);
				setState(563);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(560);
					statement();
					}
					}
					setState(565);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(566);
				match(DEDENT);
				setState(570);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(567);
					elseConditional();
					}
					}
					setState(572);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(573);
				match(IFNOT);
				setState(574);
				spinExpression(0);
				setState(576); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(575);
					match(NL);
					}
					}
					setState(578); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(580);
				match(INDENT);
				setState(584);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(581);
					statement();
					}
					}
					setState(586);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(587);
				match(DEDENT);
				setState(591);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(588);
					elseConditional();
					}
					}
					setState(593);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(594);
				match(IF);
				setState(595);
				spinExpression(0);
				setState(597); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(596);
					match(NL);
					}
					}
					setState(599); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(601);
				match(INDENT);
				setState(605);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(602);
					statement();
					}
					}
					setState(607);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(608);
				match(DEDENT);
				setState(612);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ELSEIFNOT) | (1L << ELSEIF) | (1L << ELSE))) != 0)) {
					{
					{
					setState(609);
					elseConditional();
					}
					}
					setState(614);
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
			setState(680);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				match(ELSE);
				setState(619); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(618);
					match(NL);
					}
					}
					setState(621); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(623);
				match(INDENT);
				setState(627);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(624);
					statement();
					}
					}
					setState(629);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(630);
				match(DEDENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(631);
				match(ELSEIF);
				setState(632);
				match(NOT);
				setState(633);
				spinExpression(0);
				setState(635); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(634);
					match(NL);
					}
					}
					setState(637); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(639);
				match(INDENT);
				setState(643);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(640);
					statement();
					}
					}
					setState(645);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(646);
				match(DEDENT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(648);
				match(ELSEIF);
				setState(649);
				spinExpression(0);
				setState(651); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(650);
					match(NL);
					}
					}
					setState(653); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(655);
				match(INDENT);
				setState(659);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(656);
					statement();
					}
					}
					setState(661);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(662);
				match(DEDENT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(664);
				match(ELSEIFNOT);
				setState(665);
				spinExpression(0);
				setState(667); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(666);
					match(NL);
					}
					}
					setState(669); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(671);
				match(INDENT);
				setState(675);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
					{
					{
					setState(672);
					statement();
					}
					}
					setState(677);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(678);
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
			setState(682);
			match(CASE);
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
			setState(689);
			match(INDENT);
			setState(694);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (OTHER - 67)) | (1L << (ENCOD - 67)) | (1L << (DECOD - 67)) | (1L << (FUNCTIONS - 67)) | (1L << (TYPE - 67)) | (1L << (IDENTIFIER - 67)))) != 0)) {
				{
				setState(692);
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
					setState(690);
					caseConditionalMatch();
					}
					break;
				case OTHER:
					{
					setState(691);
					caseConditionalOther();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(696);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(697);
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
			setState(699);
			spinExpression(0);
			setState(702);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(700);
				match(ELLIPSIS);
				setState(701);
				spinExpression(0);
				}
			}

			setState(704);
			match(COLON);
			setState(706);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
				{
				setState(705);
				spinExpression(0);
				}
			}

			setState(709); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(708);
				match(NL);
				}
				}
				setState(711); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(726);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INDENT) {
				{
				setState(713);
				match(INDENT);
				setState(722);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (FUNCTIONS - 71)) | (1L << (TYPE - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) {
					{
					{
					setState(714);
					spinExpression(0);
					setState(716); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(715);
						match(NL);
						}
						}
						setState(718); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==NL );
					}
					}
					setState(724);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(725);
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
			setState(763);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(728);
				match(OTHER);
				setState(729);
				match(COLON);
				setState(730);
				spinExpression(0);
				setState(732); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(731);
					match(NL);
					}
					}
					setState(734); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(744);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(736);
					match(INDENT);
					setState(740);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(737);
						statement();
						}
						}
						setState(742);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(743);
					match(DEDENT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(746);
				match(OTHER);
				setState(747);
				match(COLON);
				setState(749); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(748);
					match(NL);
					}
					}
					setState(751); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				setState(761);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INDENT) {
					{
					setState(753);
					match(INDENT);
					setState(757);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << BACKSLASH) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << REPEAT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (IFNOT - 64)) | (1L << (IF - 64)) | (1L << (CASE - 64)) | (1L << (ENCOD - 64)) | (1L << (DECOD - 64)) | (1L << (FUNCTIONS - 64)) | (1L << (TYPE - 64)) | (1L << (IDENTIFIER - 64)))) != 0)) {
						{
						{
						setState(754);
						statement();
						}
						}
						setState(759);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(760);
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
			setState(796);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
			case 1:
				{
				setState(766);
				identifier();
				setState(771);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(767);
					match(COMMA);
					setState(768);
					match(IDENTIFIER);
					}
					}
					setState(773);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(774);
				match(ASSIGN);
				setState(775);
				spinExpression(19);
				}
				break;
			case 2:
				{
				setState(777);
				match(TYPE);
				setState(778);
				match(OPEN_BRACKET);
				setState(779);
				spinExpression(0);
				setState(780);
				match(CLOSE_BRACKET);
				setState(781);
				match(ASSIGN);
				setState(782);
				spinExpression(18);
				}
				break;
			case 3:
				{
				setState(784);
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
				setState(785);
				((SpinExpressionContext)_localctx).exp = spinExpression(17);
				}
				break;
			case 4:
				{
				setState(786);
				((SpinExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(787);
				match(OPEN_PAREN);
				setState(788);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(789);
				match(CLOSE_PAREN);
				}
				break;
			case 5:
				{
				setState(791);
				match(OPEN_PAREN);
				setState(792);
				((SpinExpressionContext)_localctx).exp = spinExpression(0);
				setState(793);
				match(CLOSE_PAREN);
				}
				break;
			case 6:
				{
				setState(795);
				expressionAtom();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(842);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(840);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
					case 1:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(798);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(799);
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
						setState(800);
						((SpinExpressionContext)_localctx).right = spinExpression(17);
						}
						break;
					case 2:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(801);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(802);
						((SpinExpressionContext)_localctx).operator = match(BIN_AND);
						setState(803);
						((SpinExpressionContext)_localctx).right = spinExpression(16);
						}
						break;
					case 3:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(804);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(805);
						((SpinExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(806);
						((SpinExpressionContext)_localctx).right = spinExpression(15);
						}
						break;
					case 4:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(807);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(808);
						((SpinExpressionContext)_localctx).operator = match(BIN_OR);
						setState(809);
						((SpinExpressionContext)_localctx).right = spinExpression(14);
						}
						break;
					case 5:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(810);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(811);
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
						setState(812);
						((SpinExpressionContext)_localctx).right = spinExpression(13);
						}
						break;
					case 6:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(813);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(814);
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
						setState(815);
						((SpinExpressionContext)_localctx).right = spinExpression(12);
						}
						break;
					case 7:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(816);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(817);
						((SpinExpressionContext)_localctx).operator = match(FRAC);
						setState(818);
						((SpinExpressionContext)_localctx).right = spinExpression(11);
						}
						break;
					case 8:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(819);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(820);
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
						setState(821);
						((SpinExpressionContext)_localctx).right = spinExpression(10);
						}
						break;
					case 9:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(822);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(823);
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
						setState(824);
						((SpinExpressionContext)_localctx).right = spinExpression(9);
						}
						break;
					case 10:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(825);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(826);
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
						setState(827);
						((SpinExpressionContext)_localctx).right = spinExpression(8);
						}
						break;
					case 11:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(828);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(829);
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
						setState(830);
						((SpinExpressionContext)_localctx).right = spinExpression(7);
						}
						break;
					case 12:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(831);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(832);
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
						setState(833);
						((SpinExpressionContext)_localctx).right = spinExpression(6);
						}
						break;
					case 13:
						{
						_localctx = new SpinExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_spinExpression);
						setState(834);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(835);
						((SpinExpressionContext)_localctx).operator = match(QUESTION);
						setState(836);
						((SpinExpressionContext)_localctx).middle = spinExpression(0);
						setState(837);
						((SpinExpressionContext)_localctx).operator = match(COLON);
						setState(838);
						((SpinExpressionContext)_localctx).right = spinExpression(5);
						}
						break;
					}
					} 
				}
				setState(844);
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
			setState(857);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(845);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(846);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(847);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(848);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(849);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(850);
				identifier();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(851);
				identifier();
				setState(852);
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
				setState(854);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(855);
				identifier();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(856);
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

	public static class DataSectionContext extends ParserRuleContext {
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
		public List<TerminalNode> INDENT() { return getTokens(Spin2Parser.INDENT); }
		public TerminalNode INDENT(int i) {
			return getToken(Spin2Parser.INDENT, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public DataSectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataSection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterDataSection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitDataSection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitDataSection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataSectionContext dataSection() throws RecognitionException {
		DataSectionContext _localctx = new DataSectionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_dataSection);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(860); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(859);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(862); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,120,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(867);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(864);
					match(NL);
					}
					} 
				}
				setState(869);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			}
			setState(885);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,124,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(873);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(870);
							match(INDENT);
							}
							} 
						}
						setState(875);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,122,_ctx);
					}
					setState(876);
					dataLine();
					setState(880);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(877);
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
						setState(882);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
					}
					}
					} 
				}
				setState(887);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,124,_ctx);
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
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public AssemblerContext assembler() {
			return getRuleContext(AssemblerContext.class,0);
		}
		public DataContext data() {
			return getRuleContext(DataContext.class,0);
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
		try {
			int _alt;
			setState(912);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(888);
				label();
				setState(890); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(889);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(892); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(894);
				directive();
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
					_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(900);
				assembler();
				setState(902); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(901);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(904); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,127,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(906);
				data();
				setState(908); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(907);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(910); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,128,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class DirectiveContext extends ParserRuleContext {
		public Token name;
		public TerminalNode ORG() { return getToken(Spin2Parser.ORG, 0); }
		public TerminalNode ORGH() { return getToken(Spin2Parser.ORGH, 0); }
		public TerminalNode ORGF() { return getToken(Spin2Parser.ORGF, 0); }
		public TerminalNode ALIGN() { return getToken(Spin2Parser.ALIGN, 0); }
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(Spin2Parser.COMMA, 0); }
		public TerminalNode FIT() { return getToken(Spin2Parser.FIT, 0); }
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public TerminalNode RES() { return getToken(Spin2Parser.RES, 0); }
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_directive);
		int _la;
		try {
			setState(935);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(914);
				if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
				setState(915);
				((DirectiveContext)_localctx).name = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (ORG - 78)) | (1L << (ORGH - 78)) | (1L << (ORGF - 78)) | (1L << (ALIGN - 78)))) != 0)) ) {
					((DirectiveContext)_localctx).name = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(921);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(916);
					constantExpression(0);
					setState(919);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(917);
						match(COMMA);
						setState(918);
						constantExpression(0);
						}
					}

					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(924);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
				case 1:
					{
					setState(923);
					label();
					}
					break;
				}
				setState(926);
				((DirectiveContext)_localctx).name = match(FIT);
				setState(928);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN))) != 0) || _la==FUNCTIONS || _la==IDENTIFIER) {
					{
					setState(927);
					constantExpression(0);
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(931);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
				case 1:
					{
					setState(930);
					label();
					}
					break;
				}
				setState(933);
				((DirectiveContext)_localctx).name = match(RES);
				setState(934);
				constantExpression(0);
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

	public static class AssemblerContext extends ParserRuleContext {
		public Token condition;
		public Token instruction;
		public Token modifier;
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
		}
		public TerminalNode OR() { return getToken(Spin2Parser.OR, 0); }
		public TerminalNode AND() { return getToken(Spin2Parser.AND, 0); }
		public TerminalNode NOT() { return getToken(Spin2Parser.NOT, 0); }
		public TerminalNode XOR() { return getToken(Spin2Parser.XOR, 0); }
		public TerminalNode ENCOD() { return getToken(Spin2Parser.ENCOD, 0); }
		public TerminalNode DECOD() { return getToken(Spin2Parser.DECOD, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public TerminalNode CONDITION() { return getToken(Spin2Parser.CONDITION, 0); }
		public TerminalNode MODIFIER() { return getToken(Spin2Parser.MODIFIER, 0); }
		public AssemblerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assembler; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterAssembler(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitAssembler(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitAssembler(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssemblerContext assembler() throws RecognitionException {
		AssemblerContext _localctx = new AssemblerContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_assembler);
		int _la;
		try {
			setState(1015);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,152,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(937);
				label();
				setState(939);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(938);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(941);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(942);
				argument();
				setState(943);
				match(COMMA);
				setState(944);
				argument();
				setState(945);
				match(COMMA);
				setState(946);
				argument();
				setState(948);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(947);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(950);
				label();
				setState(952);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(951);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(954);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(955);
				argument();
				setState(956);
				match(COMMA);
				setState(957);
				argument();
				setState(959);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(958);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(961);
				label();
				setState(963);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(962);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(965);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(966);
				argument();
				setState(968);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(967);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(970);
				label();
				setState(972);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(971);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(974);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(976);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(975);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(979);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(978);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(981);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(982);
				argument();
				setState(983);
				match(COMMA);
				setState(984);
				argument();
				setState(985);
				match(COMMA);
				setState(986);
				argument();
				setState(988);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(987);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(991);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(990);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(993);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(994);
				argument();
				setState(995);
				match(COMMA);
				setState(996);
				argument();
				setState(998);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(997);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1001);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(1000);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(1003);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1004);
				argument();
				setState(1006);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1005);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
					}
				}

				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1009);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONDITION) {
					{
					setState(1008);
					((AssemblerContext)_localctx).condition = match(CONDITION);
					}
				}

				setState(1011);
				((AssemblerContext)_localctx).instruction = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (ENCOD - 71)) | (1L << (DECOD - 71)) | (1L << (AND - 71)) | (1L << (NOT - 71)) | (1L << (XOR - 71)) | (1L << (OR - 71)) | (1L << (IDENTIFIER - 71)))) != 0)) ) {
					((AssemblerContext)_localctx).instruction = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1013);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MODIFIER) {
					{
					setState(1012);
					((AssemblerContext)_localctx).modifier = match(MODIFIER);
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
		public Token type;
		public List<DataValueContext> dataValue() {
			return getRuleContexts(DataValueContext.class);
		}
		public DataValueContext dataValue(int i) {
			return getRuleContext(DataValueContext.class,i);
		}
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(Spin2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Spin2Parser.COMMA, i);
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
		enterRule(_localctx, 60, RULE_data);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1018);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
			case 1:
				{
				setState(1017);
				label();
				}
				break;
			}
			setState(1020);
			((DataContext)_localctx).type = match(TYPE);
			setState(1021);
			dataValue();
			setState(1026);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1022);
				match(COMMA);
				setState(1023);
				dataValue();
				}
				}
				setState(1028);
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
		enterRule(_localctx, 62, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1029);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			{
			setState(1031);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(1030);
				match(DOT);
				}
			}

			setState(1033);
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
		enterRule(_localctx, 64, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1036);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LONG_LITERAL) | (1L << LONG_LITERAL_ABS) | (1L << LITERAL_ABS) | (1L << LITERAL))) != 0)) {
				{
				setState(1035);
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

			setState(1038);
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
		enterRule(_localctx, 66, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040);
			constantExpression(0);
			setState(1045);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(1041);
				match(OPEN_BRACKET);
				setState(1042);
				((DataValueContext)_localctx).count = constantExpression(0);
				setState(1043);
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
		int _startState = 68;
		enterRecursionRule(_localctx, 68, RULE_constantExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1063);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(1048);
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
				setState(1049);
				((ConstantExpressionContext)_localctx).exp = constantExpression(16);
				}
				break;
			case FUNCTIONS:
				{
				setState(1050);
				((ConstantExpressionContext)_localctx).operator = match(FUNCTIONS);
				setState(1051);
				match(OPEN_PAREN);
				setState(1052);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1053);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(1055);
				match(OPEN_PAREN);
				setState(1056);
				((ConstantExpressionContext)_localctx).exp = constantExpression(0);
				setState(1057);
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
				setState(1060);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(1059);
					match(AT);
					}
				}

				setState(1062);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1104);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
					case 1:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1065);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1066);
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
						setState(1067);
						((ConstantExpressionContext)_localctx).right = constantExpression(16);
						}
						break;
					case 2:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1068);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1069);
						((ConstantExpressionContext)_localctx).operator = match(BIN_AND);
						setState(1070);
						((ConstantExpressionContext)_localctx).right = constantExpression(15);
						}
						break;
					case 3:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1071);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1072);
						((ConstantExpressionContext)_localctx).operator = match(BIN_XOR);
						setState(1073);
						((ConstantExpressionContext)_localctx).right = constantExpression(14);
						}
						break;
					case 4:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1074);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1075);
						((ConstantExpressionContext)_localctx).operator = match(BIN_OR);
						setState(1076);
						((ConstantExpressionContext)_localctx).right = constantExpression(13);
						}
						break;
					case 5:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1077);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1078);
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
						setState(1079);
						((ConstantExpressionContext)_localctx).right = constantExpression(12);
						}
						break;
					case 6:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1080);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1081);
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
						setState(1082);
						((ConstantExpressionContext)_localctx).right = constantExpression(11);
						}
						break;
					case 7:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1083);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1084);
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
						setState(1085);
						((ConstantExpressionContext)_localctx).right = constantExpression(10);
						}
						break;
					case 8:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1086);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1087);
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
						setState(1088);
						((ConstantExpressionContext)_localctx).right = constantExpression(9);
						}
						break;
					case 9:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1089);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1090);
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
						setState(1091);
						((ConstantExpressionContext)_localctx).right = constantExpression(8);
						}
						break;
					case 10:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1092);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1093);
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
						setState(1094);
						((ConstantExpressionContext)_localctx).right = constantExpression(7);
						}
						break;
					case 11:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1095);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1096);
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
						setState(1097);
						((ConstantExpressionContext)_localctx).right = constantExpression(6);
						}
						break;
					case 12:
						{
						_localctx = new ConstantExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_constantExpression);
						setState(1098);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1099);
						((ConstantExpressionContext)_localctx).operator = match(QUESTION);
						setState(1100);
						((ConstantExpressionContext)_localctx).middle = constantExpression(0);
						setState(1101);
						((ConstantExpressionContext)_localctx).operator = match(COLON);
						setState(1102);
						((ConstantExpressionContext)_localctx).right = constantExpression(5);
						}
						break;
					}
					} 
				}
				setState(1108);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,161,_ctx);
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
		enterRule(_localctx, 70, RULE_atom);
		int _la;
		try {
			setState(1150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1109);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1110);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1111);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1112);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1113);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1114);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1115);
				match(IDENTIFIER);
				setState(1120);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
				case 1:
					{
					setState(1116);
					match(OPEN_BRACKET);
					setState(1117);
					constantExpression(0);
					setState(1118);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1122);
				match(IDENTIFIER);
				setState(1123);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1128);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
				case 1:
					{
					setState(1124);
					match(OPEN_BRACKET);
					setState(1125);
					constantExpression(0);
					setState(1126);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1130);
				match(IDENTIFIER);
				setState(1135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(1131);
					match(OPEN_BRACKET);
					setState(1132);
					constantExpression(0);
					setState(1133);
					match(CLOSE_BRACKET);
					}
				}

				setState(1137);
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
				setState(1138);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1139);
				match(IDENTIFIER);
				setState(1144);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
				case 1:
					{
					setState(1140);
					match(OPEN_BRACKET);
					setState(1141);
					constantExpression(0);
					setState(1142);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1147);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(1146);
					match(DOT);
					}
				}

				setState(1149);
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
			return directive_sempred((DirectiveContext)_localctx, predIndex);
		case 31:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 34:
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
	private boolean directive_sempred(DirectiveContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return _input.LT(1).getCharPositionInLine() != 0;
		}
		return true;
	}
	private boolean label_sempred(LabelContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return _input.LT(1).getCharPositionInLine() == 0;
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3Y\u0483\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\3\2\7\2L\n\2\f\2\16\2O\13\2\3\2\3\2\3\2"+
		"\3\2\3\2\7\2V\n\2\f\2\16\2Y\13\2\3\2\3\2\3\3\6\3^\n\3\r\3\16\3_\3\3\7"+
		"\3c\n\3\f\3\16\3f\13\3\3\3\3\3\3\3\7\3k\n\3\f\3\16\3n\13\3\3\3\3\3\3\3"+
		"\3\3\7\3t\n\3\f\3\16\3w\13\3\7\3y\n\3\f\3\16\3|\13\3\3\4\7\4\177\n\4\f"+
		"\4\16\4\u0082\13\4\3\4\3\4\3\4\3\4\7\4\u0088\n\4\f\4\16\4\u008b\13\4\3"+
		"\5\7\5\u008e\n\5\f\5\16\5\u0091\13\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0099"+
		"\n\5\3\5\7\5\u009c\n\5\f\5\16\5\u009f\13\5\3\5\3\5\7\5\u00a3\n\5\f\5\16"+
		"\5\u00a6\13\5\3\6\7\6\u00a9\n\6\f\6\16\6\u00ac\13\6\3\6\3\6\3\6\3\6\3"+
		"\6\5\6\u00b3\n\6\3\6\7\6\u00b6\n\6\f\6\16\6\u00b9\13\6\3\7\6\7\u00bc\n"+
		"\7\r\7\16\7\u00bd\3\7\7\7\u00c1\n\7\f\7\16\7\u00c4\13\7\3\7\7\7\u00c7"+
		"\n\7\f\7\16\7\u00ca\13\7\3\b\7\b\u00cd\n\b\f\b\16\b\u00d0\13\b\3\b\3\b"+
		"\3\b\3\b\3\b\7\b\u00d7\n\b\f\b\16\b\u00da\13\b\3\b\3\b\3\b\6\b\u00df\n"+
		"\b\r\b\16\b\u00e0\3\t\6\t\u00e4\n\t\r\t\16\t\u00e5\3\t\7\t\u00e9\n\t\f"+
		"\t\16\t\u00ec\13\t\3\t\3\t\3\t\7\t\u00f1\n\t\f\t\16\t\u00f4\13\t\7\t\u00f6"+
		"\n\t\f\t\16\t\u00f9\13\t\3\n\7\n\u00fc\n\n\f\n\16\n\u00ff\13\n\3\n\5\n"+
		"\u0102\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u0109\n\n\3\n\6\n\u010c\n\n\r\n\16"+
		"\n\u010d\3\13\3\13\3\13\3\13\5\13\u0114\n\13\3\13\3\13\3\13\5\13\u0119"+
		"\n\13\3\13\3\13\5\13\u011d\n\13\3\13\6\13\u0120\n\13\r\13\16\13\u0121"+
		"\3\13\3\13\7\13\u0126\n\13\f\13\16\13\u0129\13\13\3\13\3\13\3\f\3\f\3"+
		"\f\7\f\u0130\n\f\f\f\16\f\u0133\13\f\3\r\3\r\3\r\7\r\u0138\n\r\f\r\16"+
		"\r\u013b\13\r\3\16\3\16\3\16\7\16\u0140\n\16\f\16\16\16\u0143\13\16\3"+
		"\17\5\17\u0146\n\17\3\17\5\17\u0149\n\17\3\17\3\17\3\17\3\17\3\17\5\17"+
		"\u0150\n\17\3\20\3\20\6\20\u0154\n\20\r\20\16\20\u0155\3\20\3\20\3\20"+
		"\5\20\u015b\n\20\3\21\5\21\u015e\n\21\3\21\3\21\3\21\5\21\u0163\n\21\3"+
		"\21\3\21\5\21\u0167\n\21\3\21\3\21\3\21\3\21\3\21\5\21\u016e\n\21\3\21"+
		"\3\21\5\21\u0172\n\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u017c"+
		"\n\21\3\21\3\21\5\21\u0180\n\21\3\22\3\22\3\22\7\22\u0185\n\22\f\22\16"+
		"\22\u0188\13\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u01a2\n\23\3\24\3\24\5\24\u01a6\n\24\3\24\6\24\u01a9\n\24\r\24\16\24"+
		"\u01aa\3\24\3\24\7\24\u01af\n\24\f\24\16\24\u01b2\13\24\3\24\5\24\u01b5"+
		"\n\24\5\24\u01b7\n\24\3\24\3\24\6\24\u01bb\n\24\r\24\16\24\u01bc\3\24"+
		"\3\24\7\24\u01c1\n\24\f\24\16\24\u01c4\13\24\3\24\3\24\3\24\3\24\6\24"+
		"\u01ca\n\24\r\24\16\24\u01cb\3\24\3\24\6\24\u01d0\n\24\r\24\16\24\u01d1"+
		"\3\24\3\24\7\24\u01d6\n\24\f\24\16\24\u01d9\13\24\3\24\3\24\3\24\3\24"+
		"\6\24\u01df\n\24\r\24\16\24\u01e0\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\5\24\u01eb\n\24\5\24\u01ed\n\24\3\24\6\24\u01f0\n\24\r\24\16\24\u01f1"+
		"\3\24\3\24\7\24\u01f6\n\24\f\24\16\24\u01f9\13\24\3\24\5\24\u01fc\n\24"+
		"\5\24\u01fe\n\24\3\24\3\24\3\24\3\24\6\24\u0204\n\24\r\24\16\24\u0205"+
		"\3\24\3\24\7\24\u020a\n\24\f\24\16\24\u020d\13\24\3\24\5\24\u0210\n\24"+
		"\5\24\u0212\n\24\3\24\3\24\3\24\3\24\6\24\u0218\n\24\r\24\16\24\u0219"+
		"\3\24\3\24\7\24\u021e\n\24\f\24\16\24\u0221\13\24\3\24\5\24\u0224\n\24"+
		"\5\24\u0226\n\24\5\24\u0228\n\24\3\25\3\25\3\25\3\25\6\25\u022e\n\25\r"+
		"\25\16\25\u022f\3\25\3\25\7\25\u0234\n\25\f\25\16\25\u0237\13\25\3\25"+
		"\3\25\7\25\u023b\n\25\f\25\16\25\u023e\13\25\3\25\3\25\3\25\6\25\u0243"+
		"\n\25\r\25\16\25\u0244\3\25\3\25\7\25\u0249\n\25\f\25\16\25\u024c\13\25"+
		"\3\25\3\25\7\25\u0250\n\25\f\25\16\25\u0253\13\25\3\25\3\25\3\25\6\25"+
		"\u0258\n\25\r\25\16\25\u0259\3\25\3\25\7\25\u025e\n\25\f\25\16\25\u0261"+
		"\13\25\3\25\3\25\7\25\u0265\n\25\f\25\16\25\u0268\13\25\5\25\u026a\n\25"+
		"\3\26\3\26\6\26\u026e\n\26\r\26\16\26\u026f\3\26\3\26\7\26\u0274\n\26"+
		"\f\26\16\26\u0277\13\26\3\26\3\26\3\26\3\26\3\26\6\26\u027e\n\26\r\26"+
		"\16\26\u027f\3\26\3\26\7\26\u0284\n\26\f\26\16\26\u0287\13\26\3\26\3\26"+
		"\3\26\3\26\3\26\6\26\u028e\n\26\r\26\16\26\u028f\3\26\3\26\7\26\u0294"+
		"\n\26\f\26\16\26\u0297\13\26\3\26\3\26\3\26\3\26\3\26\6\26\u029e\n\26"+
		"\r\26\16\26\u029f\3\26\3\26\7\26\u02a4\n\26\f\26\16\26\u02a7\13\26\3\26"+
		"\3\26\5\26\u02ab\n\26\3\27\3\27\3\27\6\27\u02b0\n\27\r\27\16\27\u02b1"+
		"\3\27\3\27\3\27\7\27\u02b7\n\27\f\27\16\27\u02ba\13\27\3\27\3\27\3\30"+
		"\3\30\3\30\5\30\u02c1\n\30\3\30\3\30\5\30\u02c5\n\30\3\30\6\30\u02c8\n"+
		"\30\r\30\16\30\u02c9\3\30\3\30\3\30\6\30\u02cf\n\30\r\30\16\30\u02d0\7"+
		"\30\u02d3\n\30\f\30\16\30\u02d6\13\30\3\30\5\30\u02d9\n\30\3\31\3\31\3"+
		"\31\3\31\6\31\u02df\n\31\r\31\16\31\u02e0\3\31\3\31\7\31\u02e5\n\31\f"+
		"\31\16\31\u02e8\13\31\3\31\5\31\u02eb\n\31\3\31\3\31\3\31\6\31\u02f0\n"+
		"\31\r\31\16\31\u02f1\3\31\3\31\7\31\u02f6\n\31\f\31\16\31\u02f9\13\31"+
		"\3\31\5\31\u02fc\n\31\5\31\u02fe\n\31\3\32\3\32\3\32\3\32\7\32\u0304\n"+
		"\32\f\32\16\32\u0307\13\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32"+
		"\u031f\n\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\7\32\u034b\n\32\f\32\16\32\u034e\13\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u035c\n\33\3\34\6\34\u035f"+
		"\n\34\r\34\16\34\u0360\3\34\7\34\u0364\n\34\f\34\16\34\u0367\13\34\3\34"+
		"\7\34\u036a\n\34\f\34\16\34\u036d\13\34\3\34\3\34\7\34\u0371\n\34\f\34"+
		"\16\34\u0374\13\34\7\34\u0376\n\34\f\34\16\34\u0379\13\34\3\35\3\35\6"+
		"\35\u037d\n\35\r\35\16\35\u037e\3\35\3\35\6\35\u0383\n\35\r\35\16\35\u0384"+
		"\3\35\3\35\6\35\u0389\n\35\r\35\16\35\u038a\3\35\3\35\6\35\u038f\n\35"+
		"\r\35\16\35\u0390\5\35\u0393\n\35\3\36\3\36\3\36\3\36\3\36\5\36\u039a"+
		"\n\36\5\36\u039c\n\36\3\36\5\36\u039f\n\36\3\36\3\36\5\36\u03a3\n\36\3"+
		"\36\5\36\u03a6\n\36\3\36\3\36\5\36\u03aa\n\36\3\37\3\37\5\37\u03ae\n\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u03b7\n\37\3\37\3\37\5\37\u03bb"+
		"\n\37\3\37\3\37\3\37\3\37\3\37\5\37\u03c2\n\37\3\37\3\37\5\37\u03c6\n"+
		"\37\3\37\3\37\3\37\5\37\u03cb\n\37\3\37\3\37\5\37\u03cf\n\37\3\37\3\37"+
		"\5\37\u03d3\n\37\3\37\5\37\u03d6\n\37\3\37\3\37\3\37\3\37\3\37\3\37\3"+
		"\37\5\37\u03df\n\37\3\37\5\37\u03e2\n\37\3\37\3\37\3\37\3\37\3\37\5\37"+
		"\u03e9\n\37\3\37\5\37\u03ec\n\37\3\37\3\37\3\37\5\37\u03f1\n\37\3\37\5"+
		"\37\u03f4\n\37\3\37\3\37\5\37\u03f8\n\37\5\37\u03fa\n\37\3 \5 \u03fd\n"+
		" \3 \3 \3 \3 \7 \u0403\n \f \16 \u0406\13 \3!\3!\5!\u040a\n!\3!\3!\3\""+
		"\5\"\u040f\n\"\3\"\3\"\3#\3#\3#\3#\3#\5#\u0418\n#\3$\3$\3$\3$\3$\3$\3"+
		"$\3$\3$\3$\3$\3$\3$\5$\u0427\n$\3$\5$\u042a\n$\3$\3$\3$\3$\3$\3$\3$\3"+
		"$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3"+
		"$\3$\3$\3$\3$\3$\3$\3$\3$\7$\u0453\n$\f$\16$\u0456\13$\3%\3%\3%\3%\3%"+
		"\3%\3%\3%\3%\3%\3%\5%\u0463\n%\3%\3%\3%\3%\3%\3%\5%\u046b\n%\3%\3%\3%"+
		"\3%\3%\5%\u0472\n%\3%\3%\3%\3%\3%\3%\3%\5%\u047b\n%\3%\5%\u047e\n%\3%"+
		"\5%\u0481\n%\3%\2\4\62F&\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&("+
		"*,.\60\62\64\668:<>@BDFH\2\22\4\2\4\4\7\7\3\2\66\67\5\2\'(--IJ\3\2\25"+
		"\26\3\2)*\3\2\'(\3\2FG\3\2\33\34\4\2\22\22LL\4\2\24\24NN\4\2\23\23OO\3"+
		"\2\27\30\4\2PRUU\5\2IJLOYY\3\2\16\21\4\2\'(--\2\u054c\2M\3\2\2\2\4]\3"+
		"\2\2\2\6\u0080\3\2\2\2\b\u008f\3\2\2\2\n\u00aa\3\2\2\2\f\u00bb\3\2\2\2"+
		"\16\u00ce\3\2\2\2\20\u00e3\3\2\2\2\22\u00fd\3\2\2\2\24\u010f\3\2\2\2\26"+
		"\u012c\3\2\2\2\30\u0134\3\2\2\2\32\u013c\3\2\2\2\34\u0145\3\2\2\2\36\u015a"+
		"\3\2\2\2 \u017f\3\2\2\2\"\u0181\3\2\2\2$\u01a1\3\2\2\2&\u0227\3\2\2\2"+
		"(\u0269\3\2\2\2*\u02aa\3\2\2\2,\u02ac\3\2\2\2.\u02bd\3\2\2\2\60\u02fd"+
		"\3\2\2\2\62\u031e\3\2\2\2\64\u035b\3\2\2\2\66\u035e\3\2\2\28\u0392\3\2"+
		"\2\2:\u03a9\3\2\2\2<\u03f9\3\2\2\2>\u03fc\3\2\2\2@\u0407\3\2\2\2B\u040e"+
		"\3\2\2\2D\u0412\3\2\2\2F\u0429\3\2\2\2H\u0480\3\2\2\2JL\7\7\2\2KJ\3\2"+
		"\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NW\3\2\2\2OM\3\2\2\2PV\5\4\3\2QV\5\f"+
		"\7\2RV\5\20\t\2SV\5\24\13\2TV\5\66\34\2UP\3\2\2\2UQ\3\2\2\2UR\3\2\2\2"+
		"US\3\2\2\2UT\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2\2\2YW\3\2\2\2"+
		"Z[\7\2\2\3[\3\3\2\2\2\\^\7\63\2\2]\\\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2"+
		"\2\2`d\3\2\2\2ac\7\7\2\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2ez\3\2"+
		"\2\2fd\3\2\2\2gl\5\6\4\2hi\7%\2\2ik\5\6\4\2jh\3\2\2\2kn\3\2\2\2lj\3\2"+
		"\2\2lm\3\2\2\2my\3\2\2\2nl\3\2\2\2oy\5\b\5\2pu\5\n\6\2qr\7%\2\2rt\5\n"+
		"\6\2sq\3\2\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2vy\3\2\2\2wu\3\2\2\2xg\3\2"+
		"\2\2xo\3\2\2\2xp\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{\5\3\2\2\2|z\3"+
		"\2\2\2}\177\7\3\2\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080\u0081"+
		"\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0084\7Y\2\2\u0084"+
		"\u0085\7#\2\2\u0085\u0089\5F$\2\u0086\u0088\t\2\2\2\u0087\u0086\3\2\2"+
		"\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\7"+
		"\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008e\7\3\2\2\u008d\u008c\3\2\2\2\u008e"+
		"\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2"+
		"\2\2\u0091\u008f\3\2\2\2\u0092\u0093\7\21\2\2\u0093\u0098\5F$\2\u0094"+
		"\u0095\7/\2\2\u0095\u0096\5F$\2\u0096\u0097\7\60\2\2\u0097\u0099\3\2\2"+
		"\2\u0098\u0094\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009d\3\2\2\2\u009a\u009c"+
		"\t\2\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\u00a4\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a1\7%"+
		"\2\2\u00a1\u00a3\5\n\6\2\u00a2\u00a0\3\2\2\2\u00a3\u00a6\3\2\2\2\u00a4"+
		"\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\t\3\2\2\2\u00a6\u00a4\3\2\2\2"+
		"\u00a7\u00a9\7\3\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00a8"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad"+
		"\u00b2\7Y\2\2\u00ae\u00af\7/\2\2\u00af\u00b0\5F$\2\u00b0\u00b1\7\60\2"+
		"\2\u00b1\u00b3\3\2\2\2\u00b2\u00ae\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b7"+
		"\3\2\2\2\u00b4\u00b6\t\2\2\2\u00b5\u00b4\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7"+
		"\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\13\3\2\2\2\u00b9\u00b7\3\2\2"+
		"\2\u00ba\u00bc\7\65\2\2\u00bb\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00c2\3\2\2\2\u00bf\u00c1\7\7"+
		"\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\u00c8\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c7\5\16"+
		"\b\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\r\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cd\7\3\2\2"+
		"\u00cc\u00cb\3\2\2\2\u00cd\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf"+
		"\3\2\2\2\u00cf\u00d1\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d1\u00d8\7Y\2\2\u00d2"+
		"\u00d3\7/\2\2\u00d3\u00d4\5F$\2\u00d4\u00d5\7\60\2\2\u00d5\u00d7\3\2\2"+
		"\2\u00d6\u00d2\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9"+
		"\3\2\2\2\u00d9\u00db\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00dc\7,\2\2\u00dc"+
		"\u00de\7\t\2\2\u00dd\u00df\t\2\2\2\u00de\u00dd\3\2\2\2\u00df\u00e0\3\2"+
		"\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\17\3\2\2\2\u00e2\u00e4"+
		"\7\64\2\2\u00e3\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e3\3\2\2\2"+
		"\u00e5\u00e6\3\2\2\2\u00e6\u00ea\3\2\2\2\u00e7\u00e9\7\7\2\2\u00e8\u00e7"+
		"\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb"+
		"\u00f7\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ed\u00f2\5\22\n\2\u00ee\u00ef\7"+
		"%\2\2\u00ef\u00f1\5\22\n\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2"+
		"\u00f0\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2"+
		"\2\2\u00f5\u00ed\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7"+
		"\u00f8\3\2\2\2\u00f8\21\3\2\2\2\u00f9\u00f7\3\2\2\2\u00fa\u00fc\7\3\2"+
		"\2\u00fb\u00fa\3\2\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe"+
		"\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0102\7V\2\2\u0101"+
		"\u0100\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0108\7Y"+
		"\2\2\u0104\u0105\7/\2\2\u0105\u0106\5F$\2\u0106\u0107\7\60\2\2\u0107\u0109"+
		"\3\2\2\2\u0108\u0104\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010b\3\2\2\2\u010a"+
		"\u010c\t\2\2\2\u010b\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010b\3\2"+
		"\2\2\u010d\u010e\3\2\2\2\u010e\23\3\2\2\2\u010f\u0110\t\3\2\2\u0110\u0111"+
		"\7Y\2\2\u0111\u0113\7\61\2\2\u0112\u0114\5\26\f\2\u0113\u0112\3\2\2\2"+
		"\u0113\u0114\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0118\7\62\2\2\u0116\u0117"+
		"\7,\2\2\u0117\u0119\5\30\r\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119"+
		"\u011c\3\2\2\2\u011a\u011b\7\36\2\2\u011b\u011d\5\32\16\2\u011c\u011a"+
		"\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\3\2\2\2\u011e\u0120\7\7\2\2\u011f"+
		"\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122\3\2"+
		"\2\2\u0122\u0123\3\2\2\2\u0123\u0127\7\3\2\2\u0124\u0126\5\36\20\2\u0125"+
		"\u0124\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0128\3\2"+
		"\2\2\u0128\u012a\3\2\2\2\u0129\u0127\3\2\2\2\u012a\u012b\7\4\2\2\u012b"+
		"\25\3\2\2\2\u012c\u0131\7Y\2\2\u012d\u012e\7%\2\2\u012e\u0130\7Y\2\2\u012f"+
		"\u012d\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2"+
		"\2\2\u0132\27\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u0139\7Y\2\2\u0135\u0136"+
		"\7%\2\2\u0136\u0138\7Y\2\2\u0137\u0135\3\2\2\2\u0138\u013b\3\2\2\2\u0139"+
		"\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\31\3\2\2\2\u013b\u0139\3\2\2"+
		"\2\u013c\u0141\5\34\17\2\u013d\u013e\7%\2\2\u013e\u0140\5\34\17\2\u013f"+
		"\u013d\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142\33\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0146\7U\2\2\u0145\u0144"+
		"\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0148\3\2\2\2\u0147\u0149\7V\2\2\u0148"+
		"\u0147\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014f\7Y"+
		"\2\2\u014b\u014c\7/\2\2\u014c\u014d\5F$\2\u014d\u014e\7\60\2\2\u014e\u0150"+
		"\3\2\2\2\u014f\u014b\3\2\2\2\u014f\u0150\3\2\2\2\u0150\35\3\2\2\2\u0151"+
		"\u0153\5\62\32\2\u0152\u0154\7\7\2\2\u0153\u0152\3\2\2\2\u0154\u0155\3"+
		"\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u015b\3\2\2\2\u0157"+
		"\u015b\5&\24\2\u0158\u015b\5(\25\2\u0159\u015b\5,\27\2\u015a\u0151\3\2"+
		"\2\2\u015a\u0157\3\2\2\2\u015a\u0158\3\2\2\2\u015a\u0159\3\2\2\2\u015b"+
		"\37\3\2\2\2\u015c\u015e\7&\2\2\u015d\u015c\3\2\2\2\u015d\u015e\3\2\2\2"+
		"\u015e\u015f\3\2\2\2\u015f\u0160\7Y\2\2\u0160\u0162\7\61\2\2\u0161\u0163"+
		"\5\"\22\2\u0162\u0161\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164\3\2\2\2"+
		"\u0164\u0180\7\62\2\2\u0165\u0167\7&\2\2\u0166\u0165\3\2\2\2\u0166\u0167"+
		"\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0169\7Y\2\2\u0169\u016a\7$\2\2\u016a"+
		"\u016b\7Y\2\2\u016b\u016d\7\61\2\2\u016c\u016e\5\"\22\2\u016d\u016c\3"+
		"\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0180\7\62\2\2\u0170"+
		"\u0172\7&\2\2\u0171\u0170\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0173\3\2"+
		"\2\2\u0173\u0174\7Y\2\2\u0174\u0175\7/\2\2\u0175\u0176\5\62\32\2\u0176"+
		"\u0177\7\60\2\2\u0177\u0178\7$\2\2\u0178\u0179\7Y\2\2\u0179\u017b\7\61"+
		"\2\2\u017a\u017c\5\"\22\2\u017b\u017a\3\2\2\2\u017b\u017c\3\2\2\2\u017c"+
		"\u017d\3\2\2\2\u017d\u017e\7\62\2\2\u017e\u0180\3\2\2\2\u017f\u015d\3"+
		"\2\2\2\u017f\u0166\3\2\2\2\u017f\u0171\3\2\2\2\u0180!\3\2\2\2\u0181\u0186"+
		"\5\62\32\2\u0182\u0183\7%\2\2\u0183\u0185\5\62\32\2\u0184\u0182\3\2\2"+
		"\2\u0185\u0188\3\2\2\2\u0186\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187#"+
		"\3\2\2\2\u0188\u0186\3\2\2\2\u0189\u018a\7 \2\2\u018a\u01a2\7Y\2\2\u018b"+
		"\u018c\7Y\2\2\u018c\u018d\7$\2\2\u018d\u018e\7Y\2\2\u018e\u018f\7/\2\2"+
		"\u018f\u0190\5\62\32\2\u0190\u0191\7\60\2\2\u0191\u01a2\3\2\2\2\u0192"+
		"\u0193\7Y\2\2\u0193\u0194\7$\2\2\u0194\u0195\7/\2\2\u0195\u0196\5\62\32"+
		"\2\u0196\u0197\7\60\2\2\u0197\u01a2\3\2\2\2\u0198\u0199\7Y\2\2\u0199\u019a"+
		"\7$\2\2\u019a\u01a2\7Y\2\2\u019b\u019c\7Y\2\2\u019c\u019d\7/\2\2\u019d"+
		"\u019e\5\62\32\2\u019e\u019f\7\60\2\2\u019f\u01a2\3\2\2\2\u01a0\u01a2"+
		"\7Y\2\2\u01a1\u0189\3\2\2\2\u01a1\u018b\3\2\2\2\u01a1\u0192\3\2\2\2\u01a1"+
		"\u0198\3\2\2\2\u01a1\u019b\3\2\2\2\u01a1\u01a0\3\2\2\2\u01a2%\3\2\2\2"+
		"\u01a3\u01a5\79\2\2\u01a4\u01a6\5\62\32\2\u01a5\u01a4\3\2\2\2\u01a5\u01a6"+
		"\3\2\2\2\u01a6\u01a8\3\2\2\2\u01a7\u01a9\7\7\2\2\u01a8\u01a7\3\2\2\2\u01a9"+
		"\u01aa\3\2\2\2\u01aa\u01a8\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01b6\3\2"+
		"\2\2\u01ac\u01b0\7\3\2\2\u01ad\u01af\5\36\20\2\u01ae\u01ad\3\2\2\2\u01af"+
		"\u01b2\3\2\2\2\u01b0\u01ae\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b4\3\2"+
		"\2\2\u01b2\u01b0\3\2\2\2\u01b3\u01b5\7\4\2\2\u01b4\u01b3\3\2\2\2\u01b4"+
		"\u01b5\3\2\2\2\u01b5\u01b7\3\2\2\2\u01b6\u01ac\3\2\2\2\u01b6\u01b7\3\2"+
		"\2\2\u01b7\u0228\3\2\2\2\u01b8\u01ba\79\2\2\u01b9\u01bb\7\7\2\2\u01ba"+
		"\u01b9\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bc\u01ba\3\2\2\2\u01bc\u01bd\3\2"+
		"\2\2\u01bd\u01be\3\2\2\2\u01be\u01c2\7\3\2\2\u01bf\u01c1\5\36\20\2\u01c0"+
		"\u01bf\3\2\2\2\u01c1\u01c4\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2\u01c3\3\2"+
		"\2\2\u01c3\u01c5\3\2\2\2\u01c4\u01c2\3\2\2\2\u01c5\u01c6\7\4\2\2\u01c6"+
		"\u01c7\7=\2\2\u01c7\u01c9\5\62\32\2\u01c8\u01ca\7\7\2\2\u01c9\u01c8\3"+
		"\2\2\2\u01ca\u01cb\3\2\2\2\u01cb\u01c9\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc"+
		"\u0228\3\2\2\2\u01cd\u01cf\79\2\2\u01ce\u01d0\7\7\2\2\u01cf\u01ce\3\2"+
		"\2\2\u01d0\u01d1\3\2\2\2\u01d1\u01cf\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2"+
		"\u01d3\3\2\2\2\u01d3\u01d7\7\3\2\2\u01d4\u01d6\5\36\20\2\u01d5\u01d4\3"+
		"\2\2\2\u01d6\u01d9\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8"+
		"\u01da\3\2\2\2\u01d9\u01d7\3\2\2\2\u01da\u01db\7\4\2\2\u01db\u01dc\7>"+
		"\2\2\u01dc\u01de\5\62\32\2\u01dd\u01df\7\7\2\2\u01de\u01dd\3\2\2\2\u01df"+
		"\u01e0\3\2\2\2\u01e0\u01de\3\2\2\2\u01e0\u01e1\3\2\2\2\u01e1\u0228\3\2"+
		"\2\2\u01e2\u01e3\79\2\2\u01e3\u01e4\5$\23\2\u01e4\u01e5\7:\2\2\u01e5\u01ec"+
		"\5\62\32\2\u01e6\u01e7\7;\2\2\u01e7\u01ea\5\62\32\2\u01e8\u01e9\7<\2\2"+
		"\u01e9\u01eb\5\62\32\2\u01ea\u01e8\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb\u01ed"+
		"\3\2\2\2\u01ec\u01e6\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01ef\3\2\2\2\u01ee"+
		"\u01f0\7\7\2\2\u01ef\u01ee\3\2\2\2\u01f0\u01f1\3\2\2\2\u01f1\u01ef\3\2"+
		"\2\2\u01f1\u01f2\3\2\2\2\u01f2\u01fd\3\2\2\2\u01f3\u01f7\7\3\2\2\u01f4"+
		"\u01f6\5\36\20\2\u01f5\u01f4\3\2\2\2\u01f6\u01f9\3\2\2\2\u01f7\u01f5\3"+
		"\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01fb\3\2\2\2\u01f9\u01f7\3\2\2\2\u01fa"+
		"\u01fc\7\4\2\2\u01fb\u01fa\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fe\3\2"+
		"\2\2\u01fd\u01f3\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0228\3\2\2\2\u01ff"+
		"\u0200\79\2\2\u0200\u0201\7=\2\2\u0201\u0203\5\62\32\2\u0202\u0204\7\7"+
		"\2\2\u0203\u0202\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u0203\3\2\2\2\u0205"+
		"\u0206\3\2\2\2\u0206\u0211\3\2\2\2\u0207\u020b\7\3\2\2\u0208\u020a\5\36"+
		"\20\2\u0209\u0208\3\2\2\2\u020a\u020d\3\2\2\2\u020b\u0209\3\2\2\2\u020b"+
		"\u020c\3\2\2\2\u020c\u020f\3\2\2\2\u020d\u020b\3\2\2\2\u020e\u0210\7\4"+
		"\2\2\u020f\u020e\3\2\2\2\u020f\u0210\3\2\2\2\u0210\u0212\3\2\2\2\u0211"+
		"\u0207\3\2\2\2\u0211\u0212\3\2\2\2\u0212\u0228\3\2\2\2\u0213\u0214\79"+
		"\2\2\u0214\u0215\7>\2\2\u0215\u0217\5\62\32\2\u0216\u0218\7\7\2\2\u0217"+
		"\u0216\3\2\2\2\u0218\u0219\3\2\2\2\u0219\u0217\3\2\2\2\u0219\u021a\3\2"+
		"\2\2\u021a\u0225\3\2\2\2\u021b\u021f\7\3\2\2\u021c\u021e\5\36\20\2\u021d"+
		"\u021c\3\2\2\2\u021e\u0221\3\2\2\2\u021f\u021d\3\2\2\2\u021f\u0220\3\2"+
		"\2\2\u0220\u0223\3\2\2\2\u0221\u021f\3\2\2\2\u0222\u0224\7\4\2\2\u0223"+
		"\u0222\3\2\2\2\u0223\u0224\3\2\2\2\u0224\u0226\3\2\2\2\u0225\u021b\3\2"+
		"\2\2\u0225\u0226\3\2\2\2\u0226\u0228\3\2\2\2\u0227\u01a3\3\2\2\2\u0227"+
		"\u01b8\3\2\2\2\u0227\u01cd\3\2\2\2\u0227\u01e2\3\2\2\2\u0227\u01ff\3\2"+
		"\2\2\u0227\u0213\3\2\2\2\u0228\'\3\2\2\2\u0229\u022a\7C\2\2\u022a\u022b"+
		"\7M\2\2\u022b\u022d\5\62\32\2\u022c\u022e\7\7\2\2\u022d\u022c\3\2\2\2"+
		"\u022e\u022f\3\2\2\2\u022f\u022d\3\2\2\2\u022f\u0230\3\2\2\2\u0230\u0231"+
		"\3\2\2\2\u0231\u0235\7\3\2\2\u0232\u0234\5\36\20\2\u0233\u0232\3\2\2\2"+
		"\u0234\u0237\3\2\2\2\u0235\u0233\3\2\2\2\u0235\u0236\3\2\2\2\u0236\u0238"+
		"\3\2\2\2\u0237\u0235\3\2\2\2\u0238\u023c\7\4\2\2\u0239\u023b\5*\26\2\u023a"+
		"\u0239\3\2\2\2\u023b\u023e\3\2\2\2\u023c\u023a\3\2\2\2\u023c\u023d\3\2"+
		"\2\2\u023d\u026a\3\2\2\2\u023e\u023c\3\2\2\2\u023f\u0240\7B\2\2\u0240"+
		"\u0242\5\62\32\2\u0241\u0243\7\7\2\2\u0242\u0241\3\2\2\2\u0243\u0244\3"+
		"\2\2\2\u0244\u0242\3\2\2\2\u0244\u0245\3\2\2\2\u0245\u0246\3\2\2\2\u0246"+
		"\u024a\7\3\2\2\u0247\u0249\5\36\20\2\u0248\u0247\3\2\2\2\u0249\u024c\3"+
		"\2\2\2\u024a\u0248\3\2\2\2\u024a\u024b\3\2\2\2\u024b\u024d\3\2\2\2\u024c"+
		"\u024a\3\2\2\2\u024d\u0251\7\4\2\2\u024e\u0250\5*\26\2\u024f\u024e\3\2"+
		"\2\2\u0250\u0253\3\2\2\2\u0251\u024f\3\2\2\2\u0251\u0252\3\2\2\2\u0252"+
		"\u026a\3\2\2\2\u0253\u0251\3\2\2\2\u0254\u0255\7C\2\2\u0255\u0257\5\62"+
		"\32\2\u0256\u0258\7\7\2\2\u0257\u0256\3\2\2\2\u0258\u0259\3\2\2\2\u0259"+
		"\u0257\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025f\7\3"+
		"\2\2\u025c\u025e\5\36\20\2\u025d\u025c\3\2\2\2\u025e\u0261\3\2\2\2\u025f"+
		"\u025d\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u0262\3\2\2\2\u0261\u025f\3\2"+
		"\2\2\u0262\u0266\7\4\2\2\u0263\u0265\5*\26\2\u0264\u0263\3\2\2\2\u0265"+
		"\u0268\3\2\2\2\u0266\u0264\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u026a\3\2"+
		"\2\2\u0268\u0266\3\2\2\2\u0269\u0229\3\2\2\2\u0269\u023f\3\2\2\2\u0269"+
		"\u0254\3\2\2\2\u026a)\3\2\2\2\u026b\u026d\7A\2\2\u026c\u026e\7\7\2\2\u026d"+
		"\u026c\3\2\2\2\u026e\u026f\3\2\2\2\u026f\u026d\3\2\2\2\u026f\u0270\3\2"+
		"\2\2\u0270\u0271\3\2\2\2\u0271\u0275\7\3\2\2\u0272\u0274\5\36\20\2\u0273"+
		"\u0272\3\2\2\2\u0274\u0277\3\2\2\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2"+
		"\2\2\u0276\u0278\3\2\2\2\u0277\u0275\3\2\2\2\u0278\u02ab\7\4\2\2\u0279"+
		"\u027a\7@\2\2\u027a\u027b\7M\2\2\u027b\u027d\5\62\32\2\u027c\u027e\7\7"+
		"\2\2\u027d\u027c\3\2\2\2\u027e\u027f\3\2\2\2\u027f\u027d\3\2\2\2\u027f"+
		"\u0280\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u0285\7\3\2\2\u0282\u0284\5\36"+
		"\20\2\u0283\u0282\3\2\2\2\u0284\u0287\3\2\2\2\u0285\u0283\3\2\2\2\u0285"+
		"\u0286\3\2\2\2\u0286\u0288\3\2\2\2\u0287\u0285\3\2\2\2\u0288\u0289\7\4"+
		"\2\2\u0289\u02ab\3\2\2\2\u028a\u028b\7@\2\2\u028b\u028d\5\62\32\2\u028c"+
		"\u028e\7\7\2\2\u028d\u028c\3\2\2\2\u028e\u028f\3\2\2\2\u028f\u028d\3\2"+
		"\2\2\u028f\u0290\3\2\2\2\u0290\u0291\3\2\2\2\u0291\u0295\7\3\2\2\u0292"+
		"\u0294\5\36\20\2\u0293\u0292\3\2\2\2\u0294\u0297\3\2\2\2\u0295\u0293\3"+
		"\2\2\2\u0295\u0296\3\2\2\2\u0296\u0298\3\2\2\2\u0297\u0295\3\2\2\2\u0298"+
		"\u0299\7\4\2\2\u0299\u02ab\3\2\2\2\u029a\u029b\7?\2\2\u029b\u029d\5\62"+
		"\32\2\u029c\u029e\7\7\2\2\u029d\u029c\3\2\2\2\u029e\u029f\3\2\2\2\u029f"+
		"\u029d\3\2\2\2\u029f\u02a0\3\2\2\2\u02a0\u02a1\3\2\2\2\u02a1\u02a5\7\3"+
		"\2\2\u02a2\u02a4\5\36\20\2\u02a3\u02a2\3\2\2\2\u02a4\u02a7\3\2\2\2\u02a5"+
		"\u02a3\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a8\3\2\2\2\u02a7\u02a5\3\2"+
		"\2\2\u02a8\u02a9\7\4\2\2\u02a9\u02ab\3\2\2\2\u02aa\u026b\3\2\2\2\u02aa"+
		"\u0279\3\2\2\2\u02aa\u028a\3\2\2\2\u02aa\u029a\3\2\2\2\u02ab+\3\2\2\2"+
		"\u02ac\u02ad\7D\2\2\u02ad\u02af\5\62\32\2\u02ae\u02b0\7\7\2\2\u02af\u02ae"+
		"\3\2\2\2\u02b0\u02b1\3\2\2\2\u02b1\u02af\3\2\2\2\u02b1\u02b2\3\2\2\2\u02b2"+
		"\u02b3\3\2\2\2\u02b3\u02b8\7\3\2\2\u02b4\u02b7\5.\30\2\u02b5\u02b7\5\60"+
		"\31\2\u02b6\u02b4\3\2\2\2\u02b6\u02b5\3\2\2\2\u02b7\u02ba\3\2\2\2\u02b8"+
		"\u02b6\3\2\2\2\u02b8\u02b9\3\2\2\2\u02b9\u02bb\3\2\2\2\u02ba\u02b8\3\2"+
		"\2\2\u02bb\u02bc\7\4\2\2\u02bc-\3\2\2\2\u02bd\u02c0\5\62\32\2\u02be\u02bf"+
		"\7\32\2\2\u02bf\u02c1\5\62\32\2\u02c0\u02be\3\2\2\2\u02c0\u02c1\3\2\2"+
		"\2\u02c1\u02c2\3\2\2\2\u02c2\u02c4\7,\2\2\u02c3\u02c5\5\62\32\2\u02c4"+
		"\u02c3\3\2\2\2\u02c4\u02c5\3\2\2\2\u02c5\u02c7\3\2\2\2\u02c6\u02c8\7\7"+
		"\2\2\u02c7\u02c6\3\2\2\2\u02c8\u02c9\3\2\2\2\u02c9\u02c7\3\2\2\2\u02c9"+
		"\u02ca\3\2\2\2\u02ca\u02d8\3\2\2\2\u02cb\u02d4\7\3\2\2\u02cc\u02ce\5\62"+
		"\32\2\u02cd\u02cf\7\7\2\2\u02ce\u02cd\3\2\2\2\u02cf\u02d0\3\2\2\2\u02d0"+
		"\u02ce\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02d3\3\2\2\2\u02d2\u02cc\3\2"+
		"\2\2\u02d3\u02d6\3\2\2\2\u02d4\u02d2\3\2\2\2\u02d4\u02d5\3\2\2\2\u02d5"+
		"\u02d7\3\2\2\2\u02d6\u02d4\3\2\2\2\u02d7\u02d9\7\4\2\2\u02d8\u02cb\3\2"+
		"\2\2\u02d8\u02d9\3\2\2\2\u02d9/\3\2\2\2\u02da\u02db\7E\2\2\u02db\u02dc"+
		"\7,\2\2\u02dc\u02de\5\62\32\2\u02dd\u02df\7\7\2\2\u02de\u02dd\3\2\2\2"+
		"\u02df\u02e0\3\2\2\2\u02e0\u02de\3\2\2\2\u02e0\u02e1\3\2\2\2\u02e1\u02ea"+
		"\3\2\2\2\u02e2\u02e6\7\3\2\2\u02e3\u02e5\5\36\20\2\u02e4\u02e3\3\2\2\2"+
		"\u02e5\u02e8\3\2\2\2\u02e6\u02e4\3\2\2\2\u02e6\u02e7\3\2\2\2\u02e7\u02e9"+
		"\3\2\2\2\u02e8\u02e6\3\2\2\2\u02e9\u02eb\7\4\2\2\u02ea\u02e2\3\2\2\2\u02ea"+
		"\u02eb\3\2\2\2\u02eb\u02fe\3\2\2\2\u02ec\u02ed\7E\2\2\u02ed\u02ef\7,\2"+
		"\2\u02ee\u02f0\7\7\2\2\u02ef\u02ee\3\2\2\2\u02f0\u02f1\3\2\2\2\u02f1\u02ef"+
		"\3\2\2\2\u02f1\u02f2\3\2\2\2\u02f2\u02fb\3\2\2\2\u02f3\u02f7\7\3\2\2\u02f4"+
		"\u02f6\5\36\20\2\u02f5\u02f4\3\2\2\2\u02f6\u02f9\3\2\2\2\u02f7\u02f5\3"+
		"\2\2\2\u02f7\u02f8\3\2\2\2\u02f8\u02fa\3\2\2\2\u02f9\u02f7\3\2\2\2\u02fa"+
		"\u02fc\7\4\2\2\u02fb\u02f3\3\2\2\2\u02fb\u02fc\3\2\2\2\u02fc\u02fe\3\2"+
		"\2\2\u02fd\u02da\3\2\2\2\u02fd\u02ec\3\2\2\2\u02fe\61\3\2\2\2\u02ff\u0300"+
		"\b\32\1\2\u0300\u0305\5$\23\2\u0301\u0302\7%\2\2\u0302\u0304\7Y\2\2\u0303"+
		"\u0301\3\2\2\2\u0304\u0307\3\2\2\2\u0305\u0303\3\2\2\2\u0305\u0306\3\2"+
		"\2\2\u0306\u0308\3\2\2\2\u0307\u0305\3\2\2\2\u0308\u0309\7\31\2\2\u0309"+
		"\u030a\5\62\32\25\u030a\u031f\3\2\2\2\u030b\u030c\7V\2\2\u030c\u030d\7"+
		"/\2\2\u030d\u030e\5\62\32\2\u030e\u030f\7\60\2\2\u030f\u0310\7\31\2\2"+
		"\u0310\u0311\5\62\32\24\u0311\u031f\3\2\2\2\u0312\u0313\t\4\2\2\u0313"+
		"\u031f\5\62\32\23\u0314\u0315\7K\2\2\u0315\u0316\7\61\2\2\u0316\u0317"+
		"\5\62\32\2\u0317\u0318\7\62\2\2\u0318\u031f\3\2\2\2\u0319\u031a\7\61\2"+
		"\2\u031a\u031b\5\62\32\2\u031b\u031c\7\62\2\2\u031c\u031f\3\2\2\2\u031d"+
		"\u031f\5\64\33\2\u031e\u02ff\3\2\2\2\u031e\u030b\3\2\2\2\u031e\u0312\3"+
		"\2\2\2\u031e\u0314\3\2\2\2\u031e\u0319\3\2\2\2\u031e\u031d\3\2\2\2\u031f"+
		"\u034c\3\2\2\2\u0320\u0321\f\22\2\2\u0321\u0322\t\5\2\2\u0322\u034b\5"+
		"\62\32\23\u0323\u0324\f\21\2\2\u0324\u0325\7\35\2\2\u0325\u034b\5\62\32"+
		"\22\u0326\u0327\f\20\2\2\u0327\u0328\7\37\2\2\u0328\u034b\5\62\32\21\u0329"+
		"\u032a\f\17\2\2\u032a\u032b\7\36\2\2\u032b\u034b\5\62\32\20\u032c\u032d"+
		"\f\16\2\2\u032d\u032e\t\6\2\2\u032e\u034b\5\62\32\17\u032f\u0330\f\r\2"+
		"\2\u0330\u0331\t\7\2\2\u0331\u034b\5\62\32\16\u0332\u0333\f\f\2\2\u0333"+
		"\u0334\7H\2\2\u0334\u034b\5\62\32\r\u0335\u0336\f\13\2\2\u0336\u0337\t"+
		"\b\2\2\u0337\u034b\5\62\32\f\u0338\u0339\f\n\2\2\u0339\u033a\t\t\2\2\u033a"+
		"\u034b\5\62\32\13\u033b\u033c\f\t\2\2\u033c\u033d\t\n\2\2\u033d\u034b"+
		"\5\62\32\n\u033e\u033f\f\b\2\2\u033f\u0340\t\13\2\2\u0340\u034b\5\62\32"+
		"\t\u0341\u0342\f\7\2\2\u0342\u0343\t\f\2\2\u0343\u034b\5\62\32\b\u0344"+
		"\u0345\f\6\2\2\u0345\u0346\7+\2\2\u0346\u0347\5\62\32\2\u0347\u0348\7"+
		",\2\2\u0348\u0349\5\62\32\7\u0349\u034b\3\2\2\2\u034a\u0320\3\2\2\2\u034a"+
		"\u0323\3\2\2\2\u034a\u0326\3\2\2\2\u034a\u0329\3\2\2\2\u034a\u032c\3\2"+
		"\2\2\u034a\u032f\3\2\2\2\u034a\u0332\3\2\2\2\u034a\u0335\3\2\2\2\u034a"+
		"\u0338\3\2\2\2\u034a\u033b\3\2\2\2\u034a\u033e\3\2\2\2\u034a\u0341\3\2"+
		"\2\2\u034a\u0344\3\2\2\2\u034b\u034e\3\2\2\2\u034c\u034a\3\2\2\2\u034c"+
		"\u034d\3\2\2\2\u034d\63\3\2\2\2\u034e\u034c\3\2\2\2\u034f\u035c\7\r\2"+
		"\2\u0350\u035c\7\f\2\2\u0351\u035c\7\13\2\2\u0352\u035c\7\n\2\2\u0353"+
		"\u035c\7\t\2\2\u0354\u035c\5$\23\2\u0355\u0356\5$\23\2\u0356\u0357\t\r"+
		"\2\2\u0357\u035c\3\2\2\2\u0358\u0359\t\r\2\2\u0359\u035c\5$\23\2\u035a"+
		"\u035c\5 \21\2\u035b\u034f\3\2\2\2\u035b\u0350\3\2\2\2\u035b\u0351\3\2"+
		"\2\2\u035b\u0352\3\2\2\2\u035b\u0353\3\2\2\2\u035b\u0354\3\2\2\2\u035b"+
		"\u0355\3\2\2\2\u035b\u0358\3\2\2\2\u035b\u035a\3\2\2\2\u035c\65\3\2\2"+
		"\2\u035d\u035f\78\2\2\u035e\u035d\3\2\2\2\u035f\u0360\3\2\2\2\u0360\u035e"+
		"\3\2\2\2\u0360\u0361\3\2\2\2\u0361\u0365\3\2\2\2\u0362\u0364\7\7\2\2\u0363"+
		"\u0362\3\2\2\2\u0364\u0367\3\2\2\2\u0365\u0363\3\2\2\2\u0365\u0366\3\2"+
		"\2\2\u0366\u0377\3\2\2\2\u0367\u0365\3\2\2\2\u0368\u036a\7\3\2\2\u0369"+
		"\u0368\3\2\2\2\u036a\u036d\3\2\2\2\u036b\u0369\3\2\2\2\u036b\u036c\3\2"+
		"\2\2\u036c\u036e\3\2\2\2\u036d\u036b\3\2\2\2\u036e\u0372\58\35\2\u036f"+
		"\u0371\t\2\2\2\u0370\u036f\3\2\2\2\u0371\u0374\3\2\2\2\u0372\u0370\3\2"+
		"\2\2\u0372\u0373\3\2\2\2\u0373\u0376\3\2\2\2\u0374\u0372\3\2\2\2\u0375"+
		"\u036b\3\2\2\2\u0376\u0379\3\2\2\2\u0377\u0375\3\2\2\2\u0377\u0378\3\2"+
		"\2\2\u0378\67\3\2\2\2\u0379\u0377\3\2\2\2\u037a\u037c\5@!\2\u037b\u037d"+
		"\7\7\2\2\u037c\u037b\3\2\2\2\u037d\u037e\3\2\2\2\u037e\u037c\3\2\2\2\u037e"+
		"\u037f\3\2\2\2\u037f\u0393\3\2\2\2\u0380\u0382\5:\36\2\u0381\u0383\7\7"+
		"\2\2\u0382\u0381\3\2\2\2\u0383\u0384\3\2\2\2\u0384\u0382\3\2\2\2\u0384"+
		"\u0385\3\2\2\2\u0385\u0393\3\2\2\2\u0386\u0388\5<\37\2\u0387\u0389\7\7"+
		"\2\2\u0388\u0387\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u0388\3\2\2\2\u038a"+
		"\u038b\3\2\2\2\u038b\u0393\3\2\2\2\u038c\u038e\5> \2\u038d\u038f\7\7\2"+
		"\2\u038e\u038d\3\2\2\2\u038f\u0390\3\2\2\2\u0390\u038e\3\2\2\2\u0390\u0391"+
		"\3\2\2\2\u0391\u0393\3\2\2\2\u0392\u037a\3\2\2\2\u0392\u0380\3\2\2\2\u0392"+
		"\u0386\3\2\2\2\u0392\u038c\3\2\2\2\u03939\3\2\2\2\u0394\u0395\6\36\17"+
		"\2\u0395\u039b\t\16\2\2\u0396\u0399\5F$\2\u0397\u0398\7%\2\2\u0398\u039a"+
		"\5F$\2\u0399\u0397\3\2\2\2\u0399\u039a\3\2\2\2\u039a\u039c\3\2\2\2\u039b"+
		"\u0396\3\2\2\2\u039b\u039c\3\2\2\2\u039c\u03aa\3\2\2\2\u039d\u039f\5@"+
		"!\2\u039e\u039d\3\2\2\2\u039e\u039f\3\2\2\2\u039f\u03a0\3\2\2\2\u03a0"+
		"\u03a2\7S\2\2\u03a1\u03a3\5F$\2\u03a2\u03a1\3\2\2\2\u03a2\u03a3\3\2\2"+
		"\2\u03a3\u03aa\3\2\2\2\u03a4\u03a6\5@!\2\u03a5\u03a4\3\2\2\2\u03a5\u03a6"+
		"\3\2\2\2\u03a6\u03a7\3\2\2\2\u03a7\u03a8\7T\2\2\u03a8\u03aa\5F$\2\u03a9"+
		"\u0394\3\2\2\2\u03a9\u039e\3\2\2\2\u03a9\u03a5\3\2\2\2\u03aa;\3\2\2\2"+
		"\u03ab\u03ad\5@!\2\u03ac\u03ae\7W\2\2\u03ad\u03ac\3\2\2\2\u03ad\u03ae"+
		"\3\2\2\2\u03ae\u03af\3\2\2\2\u03af\u03b0\t\17\2\2\u03b0\u03b1\5B\"\2\u03b1"+
		"\u03b2\7%\2\2\u03b2\u03b3\5B\"\2\u03b3\u03b4\7%\2\2\u03b4\u03b6\5B\"\2"+
		"\u03b5\u03b7\7X\2\2\u03b6\u03b5\3\2\2\2\u03b6\u03b7\3\2\2\2\u03b7\u03fa"+
		"\3\2\2\2\u03b8\u03ba\5@!\2\u03b9\u03bb\7W\2\2\u03ba\u03b9\3\2\2\2\u03ba"+
		"\u03bb\3\2\2\2\u03bb\u03bc\3\2\2\2\u03bc\u03bd\t\17\2\2\u03bd\u03be\5"+
		"B\"\2\u03be\u03bf\7%\2\2\u03bf\u03c1\5B\"\2\u03c0\u03c2\7X\2\2\u03c1\u03c0"+
		"\3\2\2\2\u03c1\u03c2\3\2\2\2\u03c2\u03fa\3\2\2\2\u03c3\u03c5\5@!\2\u03c4"+
		"\u03c6\7W\2\2\u03c5\u03c4\3\2\2\2\u03c5\u03c6\3\2\2\2\u03c6\u03c7\3\2"+
		"\2\2\u03c7\u03c8\t\17\2\2\u03c8\u03ca\5B\"\2\u03c9\u03cb\7X\2\2\u03ca"+
		"\u03c9\3\2\2\2\u03ca\u03cb\3\2\2\2\u03cb\u03fa\3\2\2\2\u03cc\u03ce\5@"+
		"!\2\u03cd\u03cf\7W\2\2\u03ce\u03cd\3\2\2\2\u03ce\u03cf\3\2\2\2\u03cf\u03d0"+
		"\3\2\2\2\u03d0\u03d2\t\17\2\2\u03d1\u03d3\7X\2\2\u03d2\u03d1\3\2\2\2\u03d2"+
		"\u03d3\3\2\2\2\u03d3\u03fa\3\2\2\2\u03d4\u03d6\7W\2\2\u03d5\u03d4\3\2"+
		"\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d7\3\2\2\2\u03d7\u03d8\t\17\2\2\u03d8"+
		"\u03d9\5B\"\2\u03d9\u03da\7%\2\2\u03da\u03db\5B\"\2\u03db\u03dc\7%\2\2"+
		"\u03dc\u03de\5B\"\2\u03dd\u03df\7X\2\2\u03de\u03dd\3\2\2\2\u03de\u03df"+
		"\3\2\2\2\u03df\u03fa\3\2\2\2\u03e0\u03e2\7W\2\2\u03e1\u03e0\3\2\2\2\u03e1"+
		"\u03e2\3\2\2\2\u03e2\u03e3\3\2\2\2\u03e3\u03e4\t\17\2\2\u03e4\u03e5\5"+
		"B\"\2\u03e5\u03e6\7%\2\2\u03e6\u03e8\5B\"\2\u03e7\u03e9\7X\2\2\u03e8\u03e7"+
		"\3\2\2\2\u03e8\u03e9\3\2\2\2\u03e9\u03fa\3\2\2\2\u03ea\u03ec\7W\2\2\u03eb"+
		"\u03ea\3\2\2\2\u03eb\u03ec\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03ee\t\17"+
		"\2\2\u03ee\u03f0\5B\"\2\u03ef\u03f1\7X\2\2\u03f0\u03ef\3\2\2\2\u03f0\u03f1"+
		"\3\2\2\2\u03f1\u03fa\3\2\2\2\u03f2\u03f4\7W\2\2\u03f3\u03f2\3\2\2\2\u03f3"+
		"\u03f4\3\2\2\2\u03f4\u03f5\3\2\2\2\u03f5\u03f7\t\17\2\2\u03f6\u03f8\7"+
		"X\2\2\u03f7\u03f6\3\2\2\2\u03f7\u03f8\3\2\2\2\u03f8\u03fa\3\2\2\2\u03f9"+
		"\u03ab\3\2\2\2\u03f9\u03b8\3\2\2\2\u03f9\u03c3\3\2\2\2\u03f9\u03cc\3\2"+
		"\2\2\u03f9\u03d5\3\2\2\2\u03f9\u03e1\3\2\2\2\u03f9\u03eb\3\2\2\2\u03f9"+
		"\u03f3\3\2\2\2\u03fa=\3\2\2\2\u03fb\u03fd\5@!\2\u03fc\u03fb\3\2\2\2\u03fc"+
		"\u03fd\3\2\2\2\u03fd\u03fe\3\2\2\2\u03fe\u03ff\7V\2\2\u03ff\u0404\5D#"+
		"\2\u0400\u0401\7%\2\2\u0401\u0403\5D#\2\u0402\u0400\3\2\2\2\u0403\u0406"+
		"\3\2\2\2\u0404\u0402\3\2\2\2\u0404\u0405\3\2\2\2\u0405?\3\2\2\2\u0406"+
		"\u0404\3\2\2\2\u0407\u0409\6!\20\2\u0408\u040a\7$\2\2\u0409\u0408\3\2"+
		"\2\2\u0409\u040a\3\2\2\2\u040a\u040b\3\2\2\2\u040b\u040c\7Y\2\2\u040c"+
		"A\3\2\2\2\u040d\u040f\t\20\2\2\u040e\u040d\3\2\2\2\u040e\u040f\3\2\2\2"+
		"\u040f\u0410\3\2\2\2\u0410\u0411\5F$\2\u0411C\3\2\2\2\u0412\u0417\5F$"+
		"\2\u0413\u0414\7/\2\2\u0414\u0415\5F$\2\u0415\u0416\7\60\2\2\u0416\u0418"+
		"\3\2\2\2\u0417\u0413\3\2\2\2\u0417\u0418\3\2\2\2\u0418E\3\2\2\2\u0419"+
		"\u041a\b$\1\2\u041a\u041b\t\21\2\2\u041b\u042a\5F$\22\u041c\u041d\7K\2"+
		"\2\u041d\u041e\7\61\2\2\u041e\u041f\5F$\2\u041f\u0420\7\62\2\2\u0420\u042a"+
		"\3\2\2\2\u0421\u0422\7\61\2\2\u0422\u0423\5F$\2\u0423\u0424\7\62\2\2\u0424"+
		"\u042a\3\2\2\2\u0425\u0427\7 \2\2\u0426\u0425\3\2\2\2\u0426\u0427\3\2"+
		"\2\2\u0427\u0428\3\2\2\2\u0428\u042a\5H%\2\u0429\u0419\3\2\2\2\u0429\u041c"+
		"\3\2\2\2\u0429\u0421\3\2\2\2\u0429\u0426\3\2\2\2\u042a\u0454\3\2\2\2\u042b"+
		"\u042c\f\21\2\2\u042c\u042d\t\5\2\2\u042d\u0453\5F$\22\u042e\u042f\f\20"+
		"\2\2\u042f\u0430\7\35\2\2\u0430\u0453\5F$\21\u0431\u0432\f\17\2\2\u0432"+
		"\u0433\7\37\2\2\u0433\u0453\5F$\20\u0434\u0435\f\16\2\2\u0435\u0436\7"+
		"\36\2\2\u0436\u0453\5F$\17\u0437\u0438\f\r\2\2\u0438\u0439\t\6\2\2\u0439"+
		"\u0453\5F$\16\u043a\u043b\f\f\2\2\u043b\u043c\t\7\2\2\u043c\u0453\5F$"+
		"\r\u043d\u043e\f\13\2\2\u043e\u043f\t\b\2\2\u043f\u0453\5F$\f\u0440\u0441"+
		"\f\n\2\2\u0441\u0442\t\t\2\2\u0442\u0453\5F$\13\u0443\u0444\f\t\2\2\u0444"+
		"\u0445\t\n\2\2\u0445\u0453\5F$\n\u0446\u0447\f\b\2\2\u0447\u0448\t\13"+
		"\2\2\u0448\u0453\5F$\t\u0449\u044a\f\7\2\2\u044a\u044b\t\f\2\2\u044b\u0453"+
		"\5F$\b\u044c\u044d\f\6\2\2\u044d\u044e\7+\2\2\u044e\u044f\5F$\2\u044f"+
		"\u0450\7,\2\2\u0450\u0451\5F$\7\u0451\u0453\3\2\2\2\u0452\u042b\3\2\2"+
		"\2\u0452\u042e\3\2\2\2\u0452\u0431\3\2\2\2\u0452\u0434\3\2\2\2\u0452\u0437"+
		"\3\2\2\2\u0452\u043a\3\2\2\2\u0452\u043d\3\2\2\2\u0452\u0440\3\2\2\2\u0452"+
		"\u0443\3\2\2\2\u0452\u0446\3\2\2\2\u0452\u0449\3\2\2\2\u0452\u044c\3\2"+
		"\2\2\u0453\u0456\3\2\2\2\u0454\u0452\3\2\2\2\u0454\u0455\3\2\2\2\u0455"+
		"G\3\2\2\2\u0456\u0454\3\2\2\2\u0457\u0481\7\r\2\2\u0458\u0481\7\f\2\2"+
		"\u0459\u0481\7\13\2\2\u045a\u0481\7\n\2\2\u045b\u0481\7\t\2\2\u045c\u0481"+
		"\7!\2\2\u045d\u0462\7Y\2\2\u045e\u045f\7/\2\2\u045f\u0460\5F$\2\u0460"+
		"\u0461\7\60\2\2\u0461\u0463\3\2\2\2\u0462\u045e\3\2\2\2\u0462\u0463\3"+
		"\2\2\2\u0463\u0481\3\2\2\2\u0464\u0465\7Y\2\2\u0465\u046a\t\r\2\2\u0466"+
		"\u0467\7/\2\2\u0467\u0468\5F$\2\u0468\u0469\7\60\2\2\u0469\u046b\3\2\2"+
		"\2\u046a\u0466\3\2\2\2\u046a\u046b\3\2\2\2\u046b\u0481\3\2\2\2\u046c\u0471"+
		"\7Y\2\2\u046d\u046e\7/\2\2\u046e\u046f\5F$\2\u046f\u0470\7\60\2\2\u0470"+
		"\u0472\3\2\2\2\u0471\u046d\3\2\2\2\u0471\u0472\3\2\2\2\u0472\u0473\3\2"+
		"\2\2\u0473\u0481\t\r\2\2\u0474\u0475\t\r\2\2\u0475\u047a\7Y\2\2\u0476"+
		"\u0477\7/\2\2\u0477\u0478\5F$\2\u0478\u0479\7\60\2\2\u0479\u047b\3\2\2"+
		"\2\u047a\u0476\3\2\2\2\u047a\u047b\3\2\2\2\u047b\u0481\3\2\2\2\u047c\u047e"+
		"\7$\2\2\u047d\u047c\3\2\2\2\u047d\u047e\3\2\2\2\u047e\u047f\3\2\2\2\u047f"+
		"\u0481\7Y\2\2\u0480\u0457\3\2\2\2\u0480\u0458\3\2\2\2\u0480\u0459\3\2"+
		"\2\2\u0480\u045a\3\2\2\2\u0480\u045b\3\2\2\2\u0480\u045c\3\2\2\2\u0480"+
		"\u045d\3\2\2\2\u0480\u0464\3\2\2\2\u0480\u046c\3\2\2\2\u0480\u0474\3\2"+
		"\2\2\u0480\u047d\3\2\2\2\u0481I\3\2\2\2\u00aaMUW_dluxz\u0080\u0089\u008f"+
		"\u0098\u009d\u00a4\u00aa\u00b2\u00b7\u00bd\u00c2\u00c8\u00ce\u00d8\u00e0"+
		"\u00e5\u00ea\u00f2\u00f7\u00fd\u0101\u0108\u010d\u0113\u0118\u011c\u0121"+
		"\u0127\u0131\u0139\u0141\u0145\u0148\u014f\u0155\u015a\u015d\u0162\u0166"+
		"\u016d\u0171\u017b\u017f\u0186\u01a1\u01a5\u01aa\u01b0\u01b4\u01b6\u01bc"+
		"\u01c2\u01cb\u01d1\u01d7\u01e0\u01ea\u01ec\u01f1\u01f7\u01fb\u01fd\u0205"+
		"\u020b\u020f\u0211\u0219\u021f\u0223\u0225\u0227\u022f\u0235\u023c\u0244"+
		"\u024a\u0251\u0259\u025f\u0266\u0269\u026f\u0275\u027f\u0285\u028f\u0295"+
		"\u029f\u02a5\u02aa\u02b1\u02b6\u02b8\u02c0\u02c4\u02c9\u02d0\u02d4\u02d8"+
		"\u02e0\u02e6\u02ea\u02f1\u02f7\u02fb\u02fd\u0305\u031e\u034a\u034c\u035b"+
		"\u0360\u0365\u036b\u0372\u0377\u037e\u0384\u038a\u0390\u0392\u0399\u039b"+
		"\u039e\u03a2\u03a5\u03a9\u03ad\u03b6\u03ba\u03c1\u03c5\u03ca\u03ce\u03d2"+
		"\u03d5\u03de\u03e1\u03e8\u03eb\u03f0\u03f3\u03f7\u03f9\u03fc\u0404\u0409"+
		"\u040e\u0417\u0426\u0429\u0452\u0454\u0462\u046a\u0471\u047a\u047d\u0480";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}