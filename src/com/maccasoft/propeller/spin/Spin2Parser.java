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
		RULE_prog = 0, RULE_constants = 1, RULE_constant = 2, RULE_objects = 3, 
		RULE_object = 4, RULE_reference = 5, RULE_filename = 6, RULE_variables = 7, 
		RULE_variable = 8, RULE_method = 9, RULE_parameters = 10, RULE_result = 11, 
		RULE_localvars = 12, RULE_localvar = 13, RULE_statement = 14, RULE_assignment = 15, 
		RULE_function = 16, RULE_data = 17, RULE_dataLine = 18, RULE_label = 19, 
		RULE_condition = 20, RULE_opcode = 21, RULE_argument = 22, RULE_prefix = 23, 
		RULE_effect = 24, RULE_dataValue = 25, RULE_expression = 26, RULE_atom = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constants", "constant", "objects", "object", "reference", "filename", 
			"variables", "variable", "method", "parameters", "result", "localvars", 
			"localvar", "statement", "assignment", "function", "data", "dataLine", 
			"label", "condition", "opcode", "argument", "prefix", "effect", "dataValue", 
			"expression", "atom"
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
		public List<ConstantsContext> constants() {
			return getRuleContexts(ConstantsContext.class);
		}
		public ConstantsContext constants(int i) {
			return getRuleContext(ConstantsContext.class,i);
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
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(56);
				match(NL);
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CON_START) | (1L << VAR_START) | (1L << OBJ_START) | (1L << PUB_START) | (1L << PRI_START) | (1L << DAT_START))) != 0)) {
				{
				setState(67);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CON_START:
					{
					setState(62);
					constants();
					}
					break;
				case OBJ_START:
					{
					setState(63);
					objects();
					}
					break;
				case VAR_START:
					{
					setState(64);
					variables();
					}
					break;
				case PUB_START:
				case PRI_START:
					{
					setState(65);
					method();
					}
					break;
				case DAT_START:
					{
					setState(66);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(72);
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

	public static class ConstantsContext extends ParserRuleContext {
		public List<TerminalNode> CON_START() { return getTokens(Spin2Parser.CON_START); }
		public TerminalNode CON_START(int i) {
			return getToken(Spin2Parser.CON_START, i);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<ConstantContext> constant() {
			return getRuleContexts(ConstantContext.class);
		}
		public ConstantContext constant(int i) {
			return getRuleContext(ConstantContext.class,i);
		}
		public ConstantsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constants; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstants(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstants(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstants(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantsContext constants() throws RecognitionException {
		ConstantsContext _localctx = new ConstantsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_constants);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(75); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(74);
					match(CON_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(77); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(79);
				match(NL);
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INDENT) | (1L << POUND) | (1L << COMMA) | (1L << IDENTIFIER))) != 0)) {
				{
				{
				setState(85);
				constant();
				}
				}
				setState(90);
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

	public static class ConstantContext extends ParserRuleContext {
		public Token name;
		public ExpressionContext exp;
		public ExpressionContext multiplier;
		public ExpressionContext start;
		public ExpressionContext step;
		public TerminalNode EQUAL() { return getToken(Spin2Parser.EQUAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
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
		public TerminalNode COMMA() { return getToken(Spin2Parser.COMMA, 0); }
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<TerminalNode> DEDENT() { return getTokens(Spin2Parser.DEDENT); }
		public TerminalNode DEDENT(int i) {
			return getToken(Spin2Parser.DEDENT, i);
		}
		public TerminalNode OPEN_BRACKET() { return getToken(Spin2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(Spin2Parser.CLOSE_BRACKET, 0); }
		public TerminalNode POUND() { return getToken(Spin2Parser.POUND, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2ParserListener ) ((Spin2ParserListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2ParserVisitor ) return ((Spin2ParserVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constant);
		int _la;
		try {
			setState(151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(91);
					match(INDENT);
					}
					}
					setState(96);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(97);
					match(COMMA);
					}
				}

				setState(100);
				((ConstantContext)_localctx).name = match(IDENTIFIER);
				setState(101);
				match(EQUAL);
				setState(102);
				((ConstantContext)_localctx).exp = expression(0);
				setState(106);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DEDENT || _la==NL) {
					{
					{
					setState(103);
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
					setState(108);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(109);
					match(INDENT);
					}
					}
					setState(114);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(115);
					match(COMMA);
					}
				}

				setState(118);
				((ConstantContext)_localctx).name = match(IDENTIFIER);
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(119);
					match(OPEN_BRACKET);
					setState(120);
					((ConstantContext)_localctx).multiplier = expression(0);
					setState(121);
					match(CLOSE_BRACKET);
					}
				}

				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DEDENT || _la==NL) {
					{
					{
					setState(125);
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
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(131);
					match(INDENT);
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(137);
				match(POUND);
				setState(138);
				((ConstantContext)_localctx).start = expression(0);
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(139);
					match(OPEN_BRACKET);
					setState(140);
					((ConstantContext)_localctx).step = expression(0);
					setState(141);
					match(CLOSE_BRACKET);
					}
				}

				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DEDENT || _la==NL) {
					{
					{
					setState(145);
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
					setState(150);
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
		enterRule(_localctx, 6, RULE_objects);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(154); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(153);
					match(OBJ_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(156); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(158);
				match(NL);
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER) {
				{
				{
				setState(164);
				object();
				}
				}
				setState(169);
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
		enterRule(_localctx, 8, RULE_object);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			reference();
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(171);
				match(OPEN_BRACKET);
				setState(172);
				expression(0);
				setState(173);
				match(CLOSE_BRACKET);
				}
				}
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(180);
			match(COLON);
			setState(181);
			filename();
			setState(183); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(182);
				match(NL);
				}
				}
				setState(185); 
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
		enterRule(_localctx, 10, RULE_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
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
		enterRule(_localctx, 12, RULE_filename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
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
		enterRule(_localctx, 14, RULE_variables);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(192); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(191);
					match(VAR_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(194); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(196);
				match(NL);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(202);
				variable();
				}
				}
				setState(207);
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
		enterRule(_localctx, 16, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			((VariableContext)_localctx).type = match(TYPE);
			setState(209);
			match(IDENTIFIER);
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(210);
				match(OPEN_BRACKET);
				setState(211);
				expression(0);
				setState(212);
				match(CLOSE_BRACKET);
				}
			}

			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(216);
				match(COMMA);
				setState(217);
				match(IDENTIFIER);
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(218);
					match(OPEN_BRACKET);
					setState(219);
					expression(0);
					setState(220);
					match(CLOSE_BRACKET);
					}
				}

				}
				}
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(230); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(229);
				match(NL);
				}
				}
				setState(232); 
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
		enterRule(_localctx, 18, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			_la = _input.LA(1);
			if ( !(_la==PUB_START || _la==PRI_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(235);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(236);
			match(OPEN_PAREN);
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(237);
				parameters();
				}
			}

			setState(240);
			match(CLOSE_PAREN);
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(241);
				match(COLON);
				setState(242);
				result();
				}
			}

			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BIN_OR) {
				{
				setState(245);
				match(BIN_OR);
				setState(246);
				localvars();
				}
			}

			setState(250); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(249);
				match(NL);
				}
				}
				setState(252); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==REPEAT || _la==IDENTIFIER) {
				{
				{
				setState(254);
				statement();
				}
				}
				setState(259);
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
		enterRule(_localctx, 20, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			match(IDENTIFIER);
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(261);
				match(COMMA);
				setState(262);
				match(IDENTIFIER);
				}
				}
				setState(267);
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
			setState(268);
			match(IDENTIFIER);
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(269);
				match(COMMA);
				setState(270);
				match(IDENTIFIER);
				}
				}
				setState(275);
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
			setState(276);
			localvar();
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(277);
				match(COMMA);
				setState(278);
				localvar();
				}
				}
				setState(283);
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
			setState(285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ALIGN) {
				{
				setState(284);
				((LocalvarContext)_localctx).align = match(ALIGN);
				}
			}

			setState(288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(287);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(290);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(291);
				match(OPEN_BRACKET);
				setState(292);
				((LocalvarContext)_localctx).count = expression(0);
				setState(293);
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
		enterRule(_localctx, 28, RULE_statement);
		int _la;
		try {
			setState(305);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(297);
				match(REPEAT);
				setState(299); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(298);
					match(NL);
					}
					}
					setState(301); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==NL );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(303);
				assignment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(304);
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
		enterRule(_localctx, 30, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			match(IDENTIFIER);
			setState(308);
			_la = _input.LA(1);
			if ( !(_la==ASSIGN || _la==ADD_ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(312);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(309);
				function();
				}
				break;
			case 2:
				{
				setState(310);
				match(IDENTIFIER);
				}
				break;
			case 3:
				{
				setState(311);
				expression(0);
				}
				break;
			}
			setState(315); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(314);
				match(NL);
				}
				}
				setState(317); 
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
		enterRule(_localctx, 32, RULE_function);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(IDENTIFIER);
			setState(320);
			match(OPEN_PAREN);
			setState(329);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(321);
				match(IDENTIFIER);
				setState(326);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(322);
					match(COMMA);
					setState(323);
					match(IDENTIFIER);
					}
					}
					setState(328);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(331);
			match(CLOSE_PAREN);
			setState(333); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(332);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(335); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
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
		enterRule(_localctx, 34, RULE_data);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(338); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(337);
					match(DAT_START);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(340); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(345);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(342);
					match(NL);
					}
					} 
				}
				setState(347);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			}
			setState(351);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(348);
					dataLine();
					}
					} 
				}
				setState(353);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
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
		enterRule(_localctx, 36, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(559);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				label();
				setState(356); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(355);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(358); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(363);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(360);
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
					setState(365);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(369);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INDENT) {
					{
					{
					setState(366);
					match(INDENT);
					}
					}
					setState(371);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(372);
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
				setState(378);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << QUAD) | (1L << BIN) | (1L << HEX) | (1L << NUMBER) | (1L << PLUS_PLUS) | (1L << MINUS_MINUS) | (1L << AT) | (1L << DOLLAR) | (1L << DOT) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << OPEN_PAREN) | (1L << FLOAT) | (1L << ROUND) | (1L << TRUNC) | (1L << PTR) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(373);
					expression(0);
					setState(376);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(374);
						match(COMMA);
						setState(375);
						expression(0);
						}
					}

					}
				}

				setState(381); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(380);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(383); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(388);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(385);
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
					setState(390);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(394);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(391);
						match(INDENT);
						}
						} 
					}
					setState(396);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				}
				setState(398);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(397);
					label();
					}
					break;
				}
				setState(400);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(401);
				dataValue();
				setState(406);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(402);
					match(COMMA);
					setState(403);
					dataValue();
					}
					}
					setState(408);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(410); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(409);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(412); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(417);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(414);
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
					setState(419);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(423);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(420);
						match(INDENT);
						}
						} 
					}
					setState(425);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				}
				setState(427);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
				case 1:
					{
					setState(426);
					label();
					}
					break;
				}
				setState(429);
				((DataLineContext)_localctx).directive = match(RES);
				setState(430);
				dataValue();
				setState(432); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(431);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(434); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(439);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(436);
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
					setState(441);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(445);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(442);
						match(INDENT);
						}
						} 
					}
					setState(447);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
				}
				setState(449);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(448);
					label();
					}
					break;
				}
				setState(452);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
				case 1:
					{
					setState(451);
					condition();
					}
					break;
				}
				setState(454);
				opcode();
				setState(455);
				argument();
				setState(456);
				match(COMMA);
				setState(457);
				argument();
				setState(458);
				match(COMMA);
				setState(459);
				argument();
				setState(461);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
				case 1:
					{
					setState(460);
					effect();
					}
					break;
				}
				setState(464); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(463);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(466); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(471);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(468);
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
					setState(473);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(477);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(474);
						match(INDENT);
						}
						} 
					}
					setState(479);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
				}
				setState(481);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
				case 1:
					{
					setState(480);
					label();
					}
					break;
				}
				setState(484);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
				case 1:
					{
					setState(483);
					condition();
					}
					break;
				}
				setState(486);
				opcode();
				setState(487);
				argument();
				setState(488);
				match(COMMA);
				setState(489);
				argument();
				setState(491);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					setState(490);
					effect();
					}
					break;
				}
				setState(494); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(493);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(496); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(501);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(498);
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
					setState(503);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(507);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(504);
						match(INDENT);
						}
						} 
					}
					setState(509);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
				}
				setState(511);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
				case 1:
					{
					setState(510);
					label();
					}
					break;
				}
				setState(514);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
				case 1:
					{
					setState(513);
					condition();
					}
					break;
				}
				setState(516);
				opcode();
				setState(517);
				argument();
				setState(519);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
				case 1:
					{
					setState(518);
					effect();
					}
					break;
				}
				setState(522); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(521);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(524); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(529);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(526);
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
					setState(531);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(535);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(532);
						match(INDENT);
						}
						} 
					}
					setState(537);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,84,_ctx);
				}
				setState(539);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
				case 1:
					{
					setState(538);
					label();
					}
					break;
				}
				setState(542);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(541);
					condition();
					}
					break;
				}
				setState(544);
				opcode();
				setState(546);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(545);
					effect();
					}
					break;
				}
				setState(549); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(548);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(551); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,88,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(556);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(553);
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
					setState(558);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
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
		enterRule(_localctx, 38, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(563);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(562);
				match(DOT);
				}
			}

			setState(565);
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
		enterRule(_localctx, 40, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(567);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(568);
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
		enterRule(_localctx, 42, RULE_opcode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(571);
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
		enterRule(_localctx, 44, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==POUND_POUND || _la==POUND) {
				{
				setState(573);
				prefix();
				}
			}

			setState(576);
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
		enterRule(_localctx, 46, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(578);
			_la = _input.LA(1);
			if ( !(_la==POUND_POUND || _la==POUND) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(580);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BACKSLASH) {
				{
				setState(579);
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
		enterRule(_localctx, 48, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(582);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(583);
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
		enterRule(_localctx, 50, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			expression(0);
			setState(590);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_BRACKET) {
				{
				setState(586);
				match(OPEN_BRACKET);
				setState(587);
				((DataValueContext)_localctx).count = expression(0);
				setState(588);
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
		int _startState = 52;
		enterRecursionRule(_localctx, 52, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(608);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case TILDE:
				{
				setState(593);
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
				setState(594);
				((ExpressionContext)_localctx).exp = expression(11);
				}
				break;
			case FLOAT:
			case ROUND:
			case TRUNC:
				{
				setState(595);
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
				setState(596);
				match(OPEN_PAREN);
				setState(597);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(598);
				match(CLOSE_PAREN);
				}
				break;
			case OPEN_PAREN:
				{
				setState(600);
				match(OPEN_PAREN);
				setState(601);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(602);
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
				setState(605);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(604);
					match(AT);
					}
				}

				setState(607);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(636);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,98,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(634);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(610);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(611);
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
						setState(612);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(613);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(614);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BIN_AND) | (1L << BIN_OR) | (1L << BIN_XOR))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(615);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(616);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(617);
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
						setState(618);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(619);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(620);
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
						setState(621);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(622);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(623);
						((ExpressionContext)_localctx).operator = match(ADDPINS);
						setState(624);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(625);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(626);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LOGICAL_AND) | (1L << LOGICAL_OR) | (1L << LOGICAL_XOR))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(627);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(628);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(629);
						((ExpressionContext)_localctx).operator = match(QUESTION);
						setState(630);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(631);
						((ExpressionContext)_localctx).operator = match(COLON);
						setState(632);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(638);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,98,_ctx);
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
		enterRule(_localctx, 54, RULE_atom);
		int _la;
		try {
			setState(681);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(639);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(640);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(641);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(642);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(643);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(644);
				match(DOLLAR);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(645);
				match(PTR);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(646);
				match(PTR);
				setState(651);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
				case 1:
					{
					setState(647);
					match(OPEN_BRACKET);
					setState(648);
					expression(0);
					setState(649);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(653);
				match(PTR);
				setState(654);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(659);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
				case 1:
					{
					setState(655);
					match(OPEN_BRACKET);
					setState(656);
					expression(0);
					setState(657);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(661);
				match(PTR);
				setState(666);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_BRACKET) {
					{
					setState(662);
					match(OPEN_BRACKET);
					setState(663);
					expression(0);
					setState(664);
					match(CLOSE_BRACKET);
					}
				}

				setState(668);
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
				setState(669);
				_la = _input.LA(1);
				if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(670);
				match(PTR);
				setState(675);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
				case 1:
					{
					setState(671);
					match(OPEN_BRACKET);
					setState(672);
					expression(0);
					setState(673);
					match(CLOSE_BRACKET);
					}
					break;
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(678);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(677);
					match(DOT);
					}
				}

				setState(680);
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
		case 19:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 20:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 21:
			return opcode_sempred((OpcodeContext)_localctx, predIndex);
		case 24:
			return effect_sempred((EffectContext)_localctx, predIndex);
		case 26:
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
			return precpred(_ctx, 10);
		case 5:
			return precpred(_ctx, 9);
		case 6:
			return precpred(_ctx, 8);
		case 7:
			return precpred(_ctx, 7);
		case 8:
			return precpred(_ctx, 6);
		case 9:
			return precpred(_ctx, 5);
		case 10:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3A\u02ae\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\7\2<\n\2\f\2\16\2?\13\2\3"+
		"\2\3\2\3\2\3\2\3\2\7\2F\n\2\f\2\16\2I\13\2\3\2\3\2\3\3\6\3N\n\3\r\3\16"+
		"\3O\3\3\7\3S\n\3\f\3\16\3V\13\3\3\3\7\3Y\n\3\f\3\16\3\\\13\3\3\4\7\4_"+
		"\n\4\f\4\16\4b\13\4\3\4\5\4e\n\4\3\4\3\4\3\4\3\4\7\4k\n\4\f\4\16\4n\13"+
		"\4\3\4\7\4q\n\4\f\4\16\4t\13\4\3\4\5\4w\n\4\3\4\3\4\3\4\3\4\3\4\5\4~\n"+
		"\4\3\4\7\4\u0081\n\4\f\4\16\4\u0084\13\4\3\4\7\4\u0087\n\4\f\4\16\4\u008a"+
		"\13\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0092\n\4\3\4\7\4\u0095\n\4\f\4\16\4"+
		"\u0098\13\4\5\4\u009a\n\4\3\5\6\5\u009d\n\5\r\5\16\5\u009e\3\5\7\5\u00a2"+
		"\n\5\f\5\16\5\u00a5\13\5\3\5\7\5\u00a8\n\5\f\5\16\5\u00ab\13\5\3\6\3\6"+
		"\3\6\3\6\3\6\7\6\u00b2\n\6\f\6\16\6\u00b5\13\6\3\6\3\6\3\6\6\6\u00ba\n"+
		"\6\r\6\16\6\u00bb\3\7\3\7\3\b\3\b\3\t\6\t\u00c3\n\t\r\t\16\t\u00c4\3\t"+
		"\7\t\u00c8\n\t\f\t\16\t\u00cb\13\t\3\t\7\t\u00ce\n\t\f\t\16\t\u00d1\13"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00d9\n\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00e1"+
		"\n\n\7\n\u00e3\n\n\f\n\16\n\u00e6\13\n\3\n\6\n\u00e9\n\n\r\n\16\n\u00ea"+
		"\3\13\3\13\3\13\3\13\5\13\u00f1\n\13\3\13\3\13\3\13\5\13\u00f6\n\13\3"+
		"\13\3\13\5\13\u00fa\n\13\3\13\6\13\u00fd\n\13\r\13\16\13\u00fe\3\13\7"+
		"\13\u0102\n\13\f\13\16\13\u0105\13\13\3\f\3\f\3\f\7\f\u010a\n\f\f\f\16"+
		"\f\u010d\13\f\3\r\3\r\3\r\7\r\u0112\n\r\f\r\16\r\u0115\13\r\3\16\3\16"+
		"\3\16\7\16\u011a\n\16\f\16\16\16\u011d\13\16\3\17\5\17\u0120\n\17\3\17"+
		"\5\17\u0123\n\17\3\17\3\17\3\17\3\17\3\17\5\17\u012a\n\17\3\20\3\20\6"+
		"\20\u012e\n\20\r\20\16\20\u012f\3\20\3\20\5\20\u0134\n\20\3\21\3\21\3"+
		"\21\3\21\3\21\5\21\u013b\n\21\3\21\6\21\u013e\n\21\r\21\16\21\u013f\3"+
		"\22\3\22\3\22\3\22\3\22\7\22\u0147\n\22\f\22\16\22\u014a\13\22\5\22\u014c"+
		"\n\22\3\22\3\22\6\22\u0150\n\22\r\22\16\22\u0151\3\23\6\23\u0155\n\23"+
		"\r\23\16\23\u0156\3\23\7\23\u015a\n\23\f\23\16\23\u015d\13\23\3\23\7\23"+
		"\u0160\n\23\f\23\16\23\u0163\13\23\3\24\3\24\6\24\u0167\n\24\r\24\16\24"+
		"\u0168\3\24\7\24\u016c\n\24\f\24\16\24\u016f\13\24\3\24\7\24\u0172\n\24"+
		"\f\24\16\24\u0175\13\24\3\24\3\24\3\24\3\24\5\24\u017b\n\24\5\24\u017d"+
		"\n\24\3\24\6\24\u0180\n\24\r\24\16\24\u0181\3\24\7\24\u0185\n\24\f\24"+
		"\16\24\u0188\13\24\3\24\7\24\u018b\n\24\f\24\16\24\u018e\13\24\3\24\5"+
		"\24\u0191\n\24\3\24\3\24\3\24\3\24\7\24\u0197\n\24\f\24\16\24\u019a\13"+
		"\24\3\24\6\24\u019d\n\24\r\24\16\24\u019e\3\24\7\24\u01a2\n\24\f\24\16"+
		"\24\u01a5\13\24\3\24\7\24\u01a8\n\24\f\24\16\24\u01ab\13\24\3\24\5\24"+
		"\u01ae\n\24\3\24\3\24\3\24\6\24\u01b3\n\24\r\24\16\24\u01b4\3\24\7\24"+
		"\u01b8\n\24\f\24\16\24\u01bb\13\24\3\24\7\24\u01be\n\24\f\24\16\24\u01c1"+
		"\13\24\3\24\5\24\u01c4\n\24\3\24\5\24\u01c7\n\24\3\24\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\5\24\u01d0\n\24\3\24\6\24\u01d3\n\24\r\24\16\24\u01d4\3"+
		"\24\7\24\u01d8\n\24\f\24\16\24\u01db\13\24\3\24\7\24\u01de\n\24\f\24\16"+
		"\24\u01e1\13\24\3\24\5\24\u01e4\n\24\3\24\5\24\u01e7\n\24\3\24\3\24\3"+
		"\24\3\24\3\24\5\24\u01ee\n\24\3\24\6\24\u01f1\n\24\r\24\16\24\u01f2\3"+
		"\24\7\24\u01f6\n\24\f\24\16\24\u01f9\13\24\3\24\7\24\u01fc\n\24\f\24\16"+
		"\24\u01ff\13\24\3\24\5\24\u0202\n\24\3\24\5\24\u0205\n\24\3\24\3\24\3"+
		"\24\5\24\u020a\n\24\3\24\6\24\u020d\n\24\r\24\16\24\u020e\3\24\7\24\u0212"+
		"\n\24\f\24\16\24\u0215\13\24\3\24\7\24\u0218\n\24\f\24\16\24\u021b\13"+
		"\24\3\24\5\24\u021e\n\24\3\24\5\24\u0221\n\24\3\24\3\24\5\24\u0225\n\24"+
		"\3\24\6\24\u0228\n\24\r\24\16\24\u0229\3\24\7\24\u022d\n\24\f\24\16\24"+
		"\u0230\13\24\5\24\u0232\n\24\3\25\3\25\5\25\u0236\n\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\30\5\30\u0241\n\30\3\30\3\30\3\31\3\31\5\31"+
		"\u0247\n\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\5\33\u0251\n\33\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u0260"+
		"\n\34\3\34\5\34\u0263\n\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\7\34\u027d\n\34\f\34\16\34\u0280\13\34\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u028e\n\35\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\5\35\u0296\n\35\3\35\3\35\3\35\3\35\3\35\5\35\u029d\n\35\3"+
		"\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u02a6\n\35\3\35\5\35\u02a9\n\35"+
		"\3\35\5\35\u02ac\n\35\3\35\2\3\66\36\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668\2\17\4\2\4\4\7\7\3\2\62\63\3\2\26\27\3\2"+
		":;\4\2\21\21\34\34\4\2#$))\3\2\679\3\2\22\23\3\2\30\32\3\2%&\3\2#$\3\2"+
		"\16\20\3\2\24\25\2\u0317\2=\3\2\2\2\4M\3\2\2\2\6\u0099\3\2\2\2\b\u009c"+
		"\3\2\2\2\n\u00ac\3\2\2\2\f\u00bd\3\2\2\2\16\u00bf\3\2\2\2\20\u00c2\3\2"+
		"\2\2\22\u00d2\3\2\2\2\24\u00ec\3\2\2\2\26\u0106\3\2\2\2\30\u010e\3\2\2"+
		"\2\32\u0116\3\2\2\2\34\u011f\3\2\2\2\36\u0133\3\2\2\2 \u0135\3\2\2\2\""+
		"\u0141\3\2\2\2$\u0154\3\2\2\2&\u0231\3\2\2\2(\u0233\3\2\2\2*\u0239\3\2"+
		"\2\2,\u023c\3\2\2\2.\u0240\3\2\2\2\60\u0244\3\2\2\2\62\u0248\3\2\2\2\64"+
		"\u024b\3\2\2\2\66\u0262\3\2\2\28\u02ab\3\2\2\2:<\7\7\2\2;:\3\2\2\2<?\3"+
		"\2\2\2=;\3\2\2\2=>\3\2\2\2>G\3\2\2\2?=\3\2\2\2@F\5\4\3\2AF\5\b\5\2BF\5"+
		"\20\t\2CF\5\24\13\2DF\5$\23\2E@\3\2\2\2EA\3\2\2\2EB\3\2\2\2EC\3\2\2\2"+
		"ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3\2\2\2IG\3\2\2\2JK\7\2\2\3"+
		"K\3\3\2\2\2LN\7/\2\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2PT\3\2\2\2"+
		"QS\7\7\2\2RQ\3\2\2\2SV\3\2\2\2TR\3\2\2\2TU\3\2\2\2UZ\3\2\2\2VT\3\2\2\2"+
		"WY\5\6\4\2XW\3\2\2\2Y\\\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\5\3\2\2\2\\Z\3\2"+
		"\2\2]_\7\3\2\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ad\3\2\2\2b`\3\2"+
		"\2\2ce\7!\2\2dc\3\2\2\2de\3\2\2\2ef\3\2\2\2fg\7A\2\2gh\7\37\2\2hl\5\66"+
		"\34\2ik\t\2\2\2ji\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\u009a\3\2\2\2"+
		"nl\3\2\2\2oq\7\3\2\2po\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2sv\3\2\2\2"+
		"tr\3\2\2\2uw\7!\2\2vu\3\2\2\2vw\3\2\2\2wx\3\2\2\2x}\7A\2\2yz\7+\2\2z{"+
		"\5\66\34\2{|\7,\2\2|~\3\2\2\2}y\3\2\2\2}~\3\2\2\2~\u0082\3\2\2\2\177\u0081"+
		"\t\2\2\2\u0080\177\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082"+
		"\u0083\3\2\2\2\u0083\u009a\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0087\7\3"+
		"\2\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088"+
		"\u0089\3\2\2\2\u0089\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c\7\34"+
		"\2\2\u008c\u0091\5\66\34\2\u008d\u008e\7+\2\2\u008e\u008f\5\66\34\2\u008f"+
		"\u0090\7,\2\2\u0090\u0092\3\2\2\2\u0091\u008d\3\2\2\2\u0091\u0092\3\2"+
		"\2\2\u0092\u0096\3\2\2\2\u0093\u0095\t\2\2\2\u0094\u0093\3\2\2\2\u0095"+
		"\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u009a\3\2"+
		"\2\2\u0098\u0096\3\2\2\2\u0099`\3\2\2\2\u0099r\3\2\2\2\u0099\u0088\3\2"+
		"\2\2\u009a\7\3\2\2\2\u009b\u009d\7\61\2\2\u009c\u009b\3\2\2\2\u009d\u009e"+
		"\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a3\3\2\2\2\u00a0"+
		"\u00a2\7\7\2\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2"+
		"\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a9\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6"+
		"\u00a8\5\n\6\2\u00a7\u00a6\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\t\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00b3"+
		"\5\f\7\2\u00ad\u00ae\7+\2\2\u00ae\u00af\5\66\34\2\u00af\u00b0\7,\2\2\u00b0"+
		"\u00b2\3\2\2\2\u00b1\u00ad\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b6\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\u00b7\7(\2\2\u00b7\u00b9\5\16\b\2\u00b8\u00ba\7\7\2\2\u00b9\u00b8\3\2"+
		"\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
		"\13\3\2\2\2\u00bd\u00be\7A\2\2\u00be\r\3\2\2\2\u00bf\u00c0\7\t\2\2\u00c0"+
		"\17\3\2\2\2\u00c1\u00c3\7\60\2\2\u00c2\u00c1\3\2\2\2\u00c3\u00c4\3\2\2"+
		"\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c9\3\2\2\2\u00c6\u00c8"+
		"\7\7\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9"+
		"\u00ca\3\2\2\2\u00ca\u00cf\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cc\u00ce\5\22"+
		"\n\2\u00cd\u00cc\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf"+
		"\u00d0\3\2\2\2\u00d0\21\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d2\u00d3\7>\2\2"+
		"\u00d3\u00d8\7A\2\2\u00d4\u00d5\7+\2\2\u00d5\u00d6\5\66\34\2\u00d6\u00d7"+
		"\7,\2\2\u00d7\u00d9\3\2\2\2\u00d8\u00d4\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9"+
		"\u00e4\3\2\2\2\u00da\u00db\7!\2\2\u00db\u00e0\7A\2\2\u00dc\u00dd\7+\2"+
		"\2\u00dd\u00de\5\66\34\2\u00de\u00df\7,\2\2\u00df\u00e1\3\2\2\2\u00e0"+
		"\u00dc\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00da\3\2"+
		"\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5"+
		"\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00e9\7\7\2\2\u00e8\u00e7\3\2"+
		"\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb"+
		"\23\3\2\2\2\u00ec\u00ed\t\3\2\2\u00ed\u00ee\7A\2\2\u00ee\u00f0\7-\2\2"+
		"\u00ef\u00f1\5\26\f\2\u00f0\u00ef\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2"+
		"\3\2\2\2\u00f2\u00f5\7.\2\2\u00f3\u00f4\7(\2\2\u00f4\u00f6\5\30\r\2\u00f5"+
		"\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f8\7\31"+
		"\2\2\u00f8\u00fa\5\32\16\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa"+
		"\u00fc\3\2\2\2\u00fb\u00fd\7\7\2\2\u00fc\u00fb\3\2\2\2\u00fd\u00fe\3\2"+
		"\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0103\3\2\2\2\u0100"+
		"\u0102\5\36\20\2\u0101\u0100\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3"+
		"\2\2\2\u0103\u0104\3\2\2\2\u0104\25\3\2\2\2\u0105\u0103\3\2\2\2\u0106"+
		"\u010b\7A\2\2\u0107\u0108\7!\2\2\u0108\u010a\7A\2\2\u0109\u0107\3\2\2"+
		"\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c\3\2\2\2\u010c\27"+
		"\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u0113\7A\2\2\u010f\u0110\7!\2\2\u0110"+
		"\u0112\7A\2\2\u0111\u010f\3\2\2\2\u0112\u0115\3\2\2\2\u0113\u0111\3\2"+
		"\2\2\u0113\u0114\3\2\2\2\u0114\31\3\2\2\2\u0115\u0113\3\2\2\2\u0116\u011b"+
		"\5\34\17\2\u0117\u0118\7!\2\2\u0118\u011a\5\34\17\2\u0119\u0117\3\2\2"+
		"\2\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\33"+
		"\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u0120\7=\2\2\u011f\u011e\3\2\2\2\u011f"+
		"\u0120\3\2\2\2\u0120\u0122\3\2\2\2\u0121\u0123\7>\2\2\u0122\u0121\3\2"+
		"\2\2\u0122\u0123\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0129\7A\2\2\u0125"+
		"\u0126\7+\2\2\u0126\u0127\5\66\34\2\u0127\u0128\7,\2\2\u0128\u012a\3\2"+
		"\2\2\u0129\u0125\3\2\2\2\u0129\u012a\3\2\2\2\u012a\35\3\2\2\2\u012b\u012d"+
		"\7\65\2\2\u012c\u012e\7\7\2\2\u012d\u012c\3\2\2\2\u012e\u012f\3\2\2\2"+
		"\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0134\3\2\2\2\u0131\u0134"+
		"\5 \21\2\u0132\u0134\5\"\22\2\u0133\u012b\3\2\2\2\u0133\u0131\3\2\2\2"+
		"\u0133\u0132\3\2\2\2\u0134\37\3\2\2\2\u0135\u0136\7A\2\2\u0136\u013a\t"+
		"\4\2\2\u0137\u013b\5\"\22\2\u0138\u013b\7A\2\2\u0139\u013b\5\66\34\2\u013a"+
		"\u0137\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013b\u013d\3\2"+
		"\2\2\u013c\u013e\7\7\2\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f"+
		"\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140!\3\2\2\2\u0141\u0142\7A\2\2\u0142"+
		"\u014b\7-\2\2\u0143\u0148\7A\2\2\u0144\u0145\7!\2\2\u0145\u0147\7A\2\2"+
		"\u0146\u0144\3\2\2\2\u0147\u014a\3\2\2\2\u0148\u0146\3\2\2\2\u0148\u0149"+
		"\3\2\2\2\u0149\u014c\3\2\2\2\u014a\u0148\3\2\2\2\u014b\u0143\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014f\7.\2\2\u014e\u0150\7\7"+
		"\2\2\u014f\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u014f\3\2\2\2\u0151"+
		"\u0152\3\2\2\2\u0152#\3\2\2\2\u0153\u0155\7\64\2\2\u0154\u0153\3\2\2\2"+
		"\u0155\u0156\3\2\2\2\u0156\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u015b"+
		"\3\2\2\2\u0158\u015a\7\7\2\2\u0159\u0158\3\2\2\2\u015a\u015d\3\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u0161\3\2\2\2\u015d\u015b\3\2"+
		"\2\2\u015e\u0160\5&\24\2\u015f\u015e\3\2\2\2\u0160\u0163\3\2\2\2\u0161"+
		"\u015f\3\2\2\2\u0161\u0162\3\2\2\2\u0162%\3\2\2\2\u0163\u0161\3\2\2\2"+
		"\u0164\u0166\5(\25\2\u0165\u0167\7\7\2\2\u0166\u0165\3\2\2\2\u0167\u0168"+
		"\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016d\3\2\2\2\u016a"+
		"\u016c\t\2\2\2\u016b\u016a\3\2\2\2\u016c\u016f\3\2\2\2\u016d\u016b\3\2"+
		"\2\2\u016d\u016e\3\2\2\2\u016e\u0232\3\2\2\2\u016f\u016d\3\2\2\2\u0170"+
		"\u0172\7\3\2\2\u0171\u0170\3\2\2\2\u0172\u0175\3\2\2\2\u0173\u0171\3\2"+
		"\2\2\u0173\u0174\3\2\2\2\u0174\u0176\3\2\2\2\u0175\u0173\3\2\2\2\u0176"+
		"\u017c\t\5\2\2\u0177\u017a\5\66\34\2\u0178\u0179\7!\2\2\u0179\u017b\5"+
		"\66\34\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u017d\3\2\2\2\u017c"+
		"\u0177\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017f\3\2\2\2\u017e\u0180\7\7"+
		"\2\2\u017f\u017e\3\2\2\2\u0180\u0181\3\2\2\2\u0181\u017f\3\2\2\2\u0181"+
		"\u0182\3\2\2\2\u0182\u0186\3\2\2\2\u0183\u0185\t\2\2\2\u0184\u0183\3\2"+
		"\2\2\u0185\u0188\3\2\2\2\u0186\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187"+
		"\u0232\3\2\2\2\u0188\u0186\3\2\2\2\u0189\u018b\7\3\2\2\u018a\u0189\3\2"+
		"\2\2\u018b\u018e\3\2\2\2\u018c\u018a\3\2\2\2\u018c\u018d\3\2\2\2\u018d"+
		"\u0190\3\2\2\2\u018e\u018c\3\2\2\2\u018f\u0191\5(\25\2\u0190\u018f\3\2"+
		"\2\2\u0190\u0191\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0193\7>\2\2\u0193"+
		"\u0198\5\64\33\2\u0194\u0195\7!\2\2\u0195\u0197\5\64\33\2\u0196\u0194"+
		"\3\2\2\2\u0197\u019a\3\2\2\2\u0198\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199"+
		"\u019c\3\2\2\2\u019a\u0198\3\2\2\2\u019b\u019d\7\7\2\2\u019c\u019b\3\2"+
		"\2\2\u019d\u019e\3\2\2\2\u019e\u019c\3\2\2\2\u019e\u019f\3\2\2\2\u019f"+
		"\u01a3\3\2\2\2\u01a0\u01a2\t\2\2\2\u01a1\u01a0\3\2\2\2\u01a2\u01a5\3\2"+
		"\2\2\u01a3\u01a1\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4\u0232\3\2\2\2\u01a5"+
		"\u01a3\3\2\2\2\u01a6\u01a8\7\3\2\2\u01a7\u01a6\3\2\2\2\u01a8\u01ab\3\2"+
		"\2\2\u01a9\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01ad\3\2\2\2\u01ab"+
		"\u01a9\3\2\2\2\u01ac\u01ae\5(\25\2\u01ad\u01ac\3\2\2\2\u01ad\u01ae\3\2"+
		"\2\2\u01ae\u01af\3\2\2\2\u01af\u01b0\7<\2\2\u01b0\u01b2\5\64\33\2\u01b1"+
		"\u01b3\7\7\2\2\u01b2\u01b1\3\2\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b2\3\2"+
		"\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b9\3\2\2\2\u01b6\u01b8\t\2\2\2\u01b7"+
		"\u01b6\3\2\2\2\u01b8\u01bb\3\2\2\2\u01b9\u01b7\3\2\2\2\u01b9\u01ba\3\2"+
		"\2\2\u01ba\u0232\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bc\u01be\7\3\2\2\u01bd"+
		"\u01bc\3\2\2\2\u01be\u01c1\3\2\2\2\u01bf\u01bd\3\2\2\2\u01bf\u01c0\3\2"+
		"\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c2\u01c4\5(\25\2\u01c3"+
		"\u01c2\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c6\3\2\2\2\u01c5\u01c7\5*"+
		"\26\2\u01c6\u01c5\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8"+
		"\u01c9\5,\27\2\u01c9\u01ca\5.\30\2\u01ca\u01cb\7!\2\2\u01cb\u01cc\5.\30"+
		"\2\u01cc\u01cd\7!\2\2\u01cd\u01cf\5.\30\2\u01ce\u01d0\5\62\32\2\u01cf"+
		"\u01ce\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01d3\7\7"+
		"\2\2\u01d2\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u01d2\3\2\2\2\u01d4"+
		"\u01d5\3\2\2\2\u01d5\u01d9\3\2\2\2\u01d6\u01d8\t\2\2\2\u01d7\u01d6\3\2"+
		"\2\2\u01d8\u01db\3\2\2\2\u01d9\u01d7\3\2\2\2\u01d9\u01da\3\2\2\2\u01da"+
		"\u0232\3\2\2\2\u01db\u01d9\3\2\2\2\u01dc\u01de\7\3\2\2\u01dd\u01dc\3\2"+
		"\2\2\u01de\u01e1\3\2\2\2\u01df\u01dd\3\2\2\2\u01df\u01e0\3\2\2\2\u01e0"+
		"\u01e3\3\2\2\2\u01e1\u01df\3\2\2\2\u01e2\u01e4\5(\25\2\u01e3\u01e2\3\2"+
		"\2\2\u01e3\u01e4\3\2\2\2\u01e4\u01e6\3\2\2\2\u01e5\u01e7\5*\26\2\u01e6"+
		"\u01e5\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8\u01e9\5,"+
		"\27\2\u01e9\u01ea\5.\30\2\u01ea\u01eb\7!\2\2\u01eb\u01ed\5.\30\2\u01ec"+
		"\u01ee\5\62\32\2\u01ed\u01ec\3\2\2\2\u01ed\u01ee\3\2\2\2\u01ee\u01f0\3"+
		"\2\2\2\u01ef\u01f1\7\7\2\2\u01f0\u01ef\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2"+
		"\u01f0\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f7\3\2\2\2\u01f4\u01f6\t\2"+
		"\2\2\u01f5\u01f4\3\2\2\2\u01f6\u01f9\3\2\2\2\u01f7\u01f5\3\2\2\2\u01f7"+
		"\u01f8\3\2\2\2\u01f8\u0232\3\2\2\2\u01f9\u01f7\3\2\2\2\u01fa\u01fc\7\3"+
		"\2\2\u01fb\u01fa\3\2\2\2\u01fc\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd"+
		"\u01fe\3\2\2\2\u01fe\u0201\3\2\2\2\u01ff\u01fd\3\2\2\2\u0200\u0202\5("+
		"\25\2\u0201\u0200\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0204\3\2\2\2\u0203"+
		"\u0205\5*\26\2\u0204\u0203\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u0206\3\2"+
		"\2\2\u0206\u0207\5,\27\2\u0207\u0209\5.\30\2\u0208\u020a\5\62\32\2\u0209"+
		"\u0208\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u020c\3\2\2\2\u020b\u020d\7\7"+
		"\2\2\u020c\u020b\3\2\2\2\u020d\u020e\3\2\2\2\u020e\u020c\3\2\2\2\u020e"+
		"\u020f\3\2\2\2\u020f\u0213\3\2\2\2\u0210\u0212\t\2\2\2\u0211\u0210\3\2"+
		"\2\2\u0212\u0215\3\2\2\2\u0213\u0211\3\2\2\2\u0213\u0214\3\2\2\2\u0214"+
		"\u0232\3\2\2\2\u0215\u0213\3\2\2\2\u0216\u0218\7\3\2\2\u0217\u0216\3\2"+
		"\2\2\u0218\u021b\3\2\2\2\u0219\u0217\3\2\2\2\u0219\u021a\3\2\2\2\u021a"+
		"\u021d\3\2\2\2\u021b\u0219\3\2\2\2\u021c\u021e\5(\25\2\u021d\u021c\3\2"+
		"\2\2\u021d\u021e\3\2\2\2\u021e\u0220\3\2\2\2\u021f\u0221\5*\26\2\u0220"+
		"\u021f\3\2\2\2\u0220\u0221\3\2\2\2\u0221\u0222\3\2\2\2\u0222\u0224\5,"+
		"\27\2\u0223\u0225\5\62\32\2\u0224\u0223\3\2\2\2\u0224\u0225\3\2\2\2\u0225"+
		"\u0227\3\2\2\2\u0226\u0228\7\7\2\2\u0227\u0226\3\2\2\2\u0228\u0229\3\2"+
		"\2\2\u0229\u0227\3\2\2\2\u0229\u022a\3\2\2\2\u022a\u022e\3\2\2\2\u022b"+
		"\u022d\t\2\2\2\u022c\u022b\3\2\2\2\u022d\u0230\3\2\2\2\u022e\u022c\3\2"+
		"\2\2\u022e\u022f\3\2\2\2\u022f\u0232\3\2\2\2\u0230\u022e\3\2\2\2\u0231"+
		"\u0164\3\2\2\2\u0231\u0173\3\2\2\2\u0231\u018c\3\2\2\2\u0231\u01a9\3\2"+
		"\2\2\u0231\u01bf\3\2\2\2\u0231\u01df\3\2\2\2\u0231\u01fd\3\2\2\2\u0231"+
		"\u0219\3\2\2\2\u0232\'\3\2\2\2\u0233\u0235\6\25\2\2\u0234\u0236\7 \2\2"+
		"\u0235\u0234\3\2\2\2\u0235\u0236\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0238"+
		"\7A\2\2\u0238)\3\2\2\2\u0239\u023a\6\26\3\2\u023a\u023b\7@\2\2\u023b+"+
		"\3\2\2\2\u023c\u023d\6\27\4\2\u023d\u023e\7A\2\2\u023e-\3\2\2\2\u023f"+
		"\u0241\5\60\31\2\u0240\u023f\3\2\2\2\u0240\u0241\3\2\2\2\u0241\u0242\3"+
		"\2\2\2\u0242\u0243\5\66\34\2\u0243/\3\2\2\2\u0244\u0246\t\6\2\2\u0245"+
		"\u0247\7\"\2\2\u0246\u0245\3\2\2\2\u0246\u0247\3\2\2\2\u0247\61\3\2\2"+
		"\2\u0248\u0249\6\32\5\2\u0249\u024a\7A\2\2\u024a\63\3\2\2\2\u024b\u0250"+
		"\5\66\34\2\u024c\u024d\7+\2\2\u024d\u024e\5\66\34\2\u024e\u024f\7,\2\2"+
		"\u024f\u0251\3\2\2\2\u0250\u024c\3\2\2\2\u0250\u0251\3\2\2\2\u0251\65"+
		"\3\2\2\2\u0252\u0253\b\34\1\2\u0253\u0254\t\7\2\2\u0254\u0263\5\66\34"+
		"\r\u0255\u0256\t\b\2\2\u0256\u0257\7-\2\2\u0257\u0258\5\66\34\2\u0258"+
		"\u0259\7.\2\2\u0259\u0263\3\2\2\2\u025a\u025b\7-\2\2\u025b\u025c\5\66"+
		"\34\2\u025c\u025d\7.\2\2\u025d\u0263\3\2\2\2\u025e\u0260\7\33\2\2\u025f"+
		"\u025e\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0263\58"+
		"\35\2\u0262\u0252\3\2\2\2\u0262\u0255\3\2\2\2\u0262\u025a\3\2\2\2\u0262"+
		"\u025f\3\2\2\2\u0263\u027e\3\2\2\2\u0264\u0265\f\f\2\2\u0265\u0266\t\t"+
		"\2\2\u0266\u027d\5\66\34\r\u0267\u0268\f\13\2\2\u0268\u0269\t\n\2\2\u0269"+
		"\u027d\5\66\34\f\u026a\u026b\f\n\2\2\u026b\u026c\t\13\2\2\u026c\u027d"+
		"\5\66\34\13\u026d\u026e\f\t\2\2\u026e\u026f\t\f\2\2\u026f\u027d\5\66\34"+
		"\n\u0270\u0271\f\b\2\2\u0271\u0272\7\66\2\2\u0272\u027d\5\66\34\t\u0273"+
		"\u0274\f\7\2\2\u0274\u0275\t\r\2\2\u0275\u027d\5\66\34\b\u0276\u0277\f"+
		"\6\2\2\u0277\u0278\7\'\2\2\u0278\u0279\5\66\34\2\u0279\u027a\7(\2\2\u027a"+
		"\u027b\5\66\34\7\u027b\u027d\3\2\2\2\u027c\u0264\3\2\2\2\u027c\u0267\3"+
		"\2\2\2\u027c\u026a\3\2\2\2\u027c\u026d\3\2\2\2\u027c\u0270\3\2\2\2\u027c"+
		"\u0273\3\2\2\2\u027c\u0276\3\2\2\2\u027d\u0280\3\2\2\2\u027e\u027c\3\2"+
		"\2\2\u027e\u027f\3\2\2\2\u027f\67\3\2\2\2\u0280\u027e\3\2\2\2\u0281\u02ac"+
		"\7\r\2\2\u0282\u02ac\7\f\2\2\u0283\u02ac\7\13\2\2\u0284\u02ac\7\n\2\2"+
		"\u0285\u02ac\7\t\2\2\u0286\u02ac\7\35\2\2\u0287\u02ac\7?\2\2\u0288\u028d"+
		"\7?\2\2\u0289\u028a\7+\2\2\u028a\u028b\5\66\34\2\u028b\u028c\7,\2\2\u028c"+
		"\u028e\3\2\2\2\u028d\u0289\3\2\2\2\u028d\u028e\3\2\2\2\u028e\u02ac\3\2"+
		"\2\2\u028f\u0290\7?\2\2\u0290\u0295\t\16\2\2\u0291\u0292\7+\2\2\u0292"+
		"\u0293\5\66\34\2\u0293\u0294\7,\2\2\u0294\u0296\3\2\2\2\u0295\u0291\3"+
		"\2\2\2\u0295\u0296\3\2\2\2\u0296\u02ac\3\2\2\2\u0297\u029c\7?\2\2\u0298"+
		"\u0299\7+\2\2\u0299\u029a\5\66\34\2\u029a\u029b\7,\2\2\u029b\u029d\3\2"+
		"\2\2\u029c\u0298\3\2\2\2\u029c\u029d\3\2\2\2\u029d\u029e\3\2\2\2\u029e"+
		"\u02ac\t\16\2\2\u029f\u02a0\t\16\2\2\u02a0\u02a5\7?\2\2\u02a1\u02a2\7"+
		"+\2\2\u02a2\u02a3\5\66\34\2\u02a3\u02a4\7,\2\2\u02a4\u02a6\3\2\2\2\u02a5"+
		"\u02a1\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02ac\3\2\2\2\u02a7\u02a9\7 "+
		"\2\2\u02a8\u02a7\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9\u02aa\3\2\2\2\u02aa"+
		"\u02ac\7A\2\2\u02ab\u0281\3\2\2\2\u02ab\u0282\3\2\2\2\u02ab\u0283\3\2"+
		"\2\2\u02ab\u0284\3\2\2\2\u02ab\u0285\3\2\2\2\u02ab\u0286\3\2\2\2\u02ab"+
		"\u0287\3\2\2\2\u02ab\u0288\3\2\2\2\u02ab\u028f\3\2\2\2\u02ab\u0297\3\2"+
		"\2\2\u02ab\u029f\3\2\2\2\u02ab\u02a8\3\2\2\2\u02ac9\3\2\2\2k=EGOTZ`dl"+
		"rv}\u0082\u0088\u0091\u0096\u0099\u009e\u00a3\u00a9\u00b3\u00bb\u00c4"+
		"\u00c9\u00cf\u00d8\u00e0\u00e4\u00ea\u00f0\u00f5\u00f9\u00fe\u0103\u010b"+
		"\u0113\u011b\u011f\u0122\u0129\u012f\u0133\u013a\u013f\u0148\u014b\u0151"+
		"\u0156\u015b\u0161\u0168\u016d\u0173\u017a\u017c\u0181\u0186\u018c\u0190"+
		"\u0198\u019e\u01a3\u01a9\u01ad\u01b4\u01b9\u01bf\u01c3\u01c6\u01cf\u01d4"+
		"\u01d9\u01df\u01e3\u01e6\u01ed\u01f2\u01f7\u01fd\u0201\u0204\u0209\u020e"+
		"\u0213\u0219\u021d\u0220\u0224\u0229\u022e\u0231\u0235\u0240\u0246\u0250"+
		"\u025f\u0262\u027c\u027e\u028d\u0295\u029c\u02a5\u02a8\u02ab";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}