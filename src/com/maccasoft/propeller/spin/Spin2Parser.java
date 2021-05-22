// Generated from Spin2.g4 by ANTLR 4.9.2
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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, T__64=65, T__65=66, 
		T__66=67, T__67=68, T__68=69, T__69=70, T__70=71, T__71=72, T__72=73, 
		T__73=74, T__74=75, T__75=76, T__76=77, T__77=78, T__78=79, T__79=80, 
		BLOCK_COMMENT=81, COMMENT=82, TYPE=83, PTR=84, CONDITION=85, IDENTIFIER=86, 
		STRING=87, QUAD=88, BIN=89, HEX=90, NUMBER=91, NL=92, WS=93;
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
			null, "'CON'", "'con'", "','", "'='", "'['", "']'", "'#'", "'OBJ'", "'obj'", 
			"':'", "'VAR'", "'var'", "'PUB'", "'pub'", "'PRI'", "'pri'", "'('", "')'", 
			"'|'", "'ALIGNW'", "'ALIGNL'", "'repeat'", "':='", "'+='", "'DAT'", "'dat'", 
			"'ORG'", "'org'", "'ORGH'", "'orgh'", "'RES'", "'res'", "'.'", "'##'", 
			"'\\'", "'+'", "'-'", "'!!'", "'!'", "'~'", "'>>'", "'<<'", "'&'", "'^'", 
			"'*'", "'/'", "'+/'", "'//'", "'+//'", "'#>'", "'<#'", "'ADDBITS'", "'addbits'", 
			"'ADDPINS'", "'addpins'", "'<'", "'+<'", "'<='", "'+<='", "'=='", "'<>'", 
			"'>='", "'+>='", "'>'", "'+>'", "'<=>'", "'&&'", "'^^'", "'||'", "'?'", 
			"'FLOAT'", "'float'", "'ROUND'", "'round'", "'TRUNC'", "'trunc'", "'@'", 
			"'$'", "'++'", "'--'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "BLOCK_COMMENT", 
			"COMMENT", "TYPE", "PTR", "CONDITION", "IDENTIFIER", "STRING", "QUAD", 
			"BIN", "HEX", "NUMBER", "NL", "WS"
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
	public String getGrammarFileName() { return "Spin2.g4"; }

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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitProg(this);
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__7) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__24) | (1L << T__25))) != 0)) {
				{
				setState(67);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
					{
					setState(62);
					constants();
					}
					break;
				case T__7:
				case T__8:
					{
					setState(63);
					objects();
					}
					break;
				case T__10:
				case T__11:
					{
					setState(64);
					variables();
					}
					break;
				case T__12:
				case T__13:
				case T__14:
				case T__15:
					{
					setState(65);
					method();
					}
					break;
				case T__24:
				case T__25:
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterConstants(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitConstants(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitConstants(this);
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
					_la = _input.LA(1);
					if ( !(_la==T__0 || _la==T__1) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
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
			while (_la==T__2 || _la==T__6 || _la==IDENTIFIER) {
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
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
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
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constant);
		int _la;
		try {
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(91);
					match(T__2);
					}
				}

				setState(94);
				((ConstantContext)_localctx).name = match(IDENTIFIER);
				setState(95);
				match(T__3);
				setState(96);
				((ConstantContext)_localctx).exp = expression(0);
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
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(103);
					match(T__2);
					}
				}

				setState(106);
				((ConstantContext)_localctx).name = match(IDENTIFIER);
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(107);
					match(T__4);
					setState(108);
					((ConstantContext)_localctx).multiplier = expression(0);
					setState(109);
					match(T__5);
					}
				}

				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(113);
					match(NL);
					}
					}
					setState(118);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(119);
				match(T__6);
				setState(120);
				((ConstantContext)_localctx).start = expression(0);
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(121);
					match(T__4);
					setState(122);
					((ConstantContext)_localctx).step = expression(0);
					setState(123);
					match(T__5);
					}
				}

				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(127);
					match(NL);
					}
					}
					setState(132);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterObjects(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitObjects(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitObjects(this);
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
			setState(136); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(135);
					_la = _input.LA(1);
					if ( !(_la==T__7 || _la==T__8) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(138); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(140);
				match(NL);
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER) {
				{
				{
				setState(146);
				object();
				}
				}
				setState(151);
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
		public FilenameContext filename() {
			return getRuleContext(FilenameContext.class,0);
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
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitObject(this);
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
			setState(152);
			reference();
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(153);
				match(T__4);
				setState(154);
				expression(0);
				setState(155);
				match(T__5);
				}
				}
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(162);
			match(T__9);
			setState(163);
			filename();
			setState(165); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(164);
				match(NL);
				}
				}
				setState(167); 
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReferenceContext reference() throws RecognitionException {
		ReferenceContext _localctx = new ReferenceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_reference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterFilename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitFilename(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitFilename(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilenameContext filename() throws RecognitionException {
		FilenameContext _localctx = new FilenameContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_filename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitVariables(this);
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
			setState(174); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(173);
					_la = _input.LA(1);
					if ( !(_la==T__10 || _la==T__11) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(176); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(178);
				match(NL);
				}
				}
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(184);
				variable();
				}
				}
				setState(189);
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
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitVariable(this);
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
			setState(190);
			((VariableContext)_localctx).type = match(TYPE);
			setState(191);
			match(IDENTIFIER);
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(192);
				match(T__4);
				setState(193);
				expression(0);
				setState(194);
				match(T__5);
				}
			}

			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(198);
				match(T__2);
				setState(199);
				match(IDENTIFIER);
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(200);
					match(T__4);
					setState(201);
					expression(0);
					setState(202);
					match(T__5);
					}
				}

				}
				}
				setState(210);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(212); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(211);
				match(NL);
				}
				}
				setState(214); 
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
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitMethod(this);
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
			setState(216);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(217);
			((MethodContext)_localctx).name = match(IDENTIFIER);
			setState(218);
			match(T__16);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(219);
				parameters();
				}
			}

			setState(222);
			match(T__17);
			setState(225);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(223);
				match(T__9);
				setState(224);
				result();
				}
			}

			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__18) {
				{
				setState(227);
				match(T__18);
				setState(228);
				localvars();
				}
			}

			setState(232); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(231);
				match(NL);
				}
				}
				setState(234); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(239);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__21 || _la==IDENTIFIER) {
				{
				{
				setState(236);
				statement();
				}
				}
				setState(241);
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
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitParameters(this);
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
			setState(242);
			match(IDENTIFIER);
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(243);
				match(T__2);
				setState(244);
				match(IDENTIFIER);
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

	public static class ResultContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(Spin2Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(Spin2Parser.IDENTIFIER, i);
		}
		public ResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitResult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitResult(this);
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
			setState(250);
			match(IDENTIFIER);
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(251);
				match(T__2);
				setState(252);
				match(IDENTIFIER);
				}
				}
				setState(257);
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
		public LocalvarsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localvars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterLocalvars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitLocalvars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitLocalvars(this);
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
			setState(258);
			localvar();
			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(259);
				match(T__2);
				setState(260);
				localvar();
				}
				}
				setState(265);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterLocalvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitLocalvar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitLocalvar(this);
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
			setState(267);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__19 || _la==T__20) {
				{
				setState(266);
				((LocalvarContext)_localctx).align = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__19 || _la==T__20) ) {
					((LocalvarContext)_localctx).align = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(270);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(269);
				((LocalvarContext)_localctx).vartype = match(TYPE);
				}
			}

			setState(272);
			((LocalvarContext)_localctx).name = match(IDENTIFIER);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(273);
				match(T__4);
				setState(274);
				((LocalvarContext)_localctx).count = expression(0);
				setState(275);
				match(T__5);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_statement);
		int _la;
		try {
			setState(287);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(279);
				match(T__21);
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
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
				assignment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(286);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitAssignment(this);
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
			setState(289);
			match(IDENTIFIER);
			setState(290);
			_la = _input.LA(1);
			if ( !(_la==T__22 || _la==T__23) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(291);
				function();
				}
				break;
			case 2:
				{
				setState(292);
				match(IDENTIFIER);
				}
				break;
			case 3:
				{
				setState(293);
				expression(0);
				}
				break;
			}
			setState(297); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(296);
				match(NL);
				}
				}
				setState(299); 
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
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitFunction(this);
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
			setState(301);
			match(IDENTIFIER);
			setState(302);
			match(T__16);
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(303);
				match(IDENTIFIER);
				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(304);
					match(T__2);
					setState(305);
					match(IDENTIFIER);
					}
					}
					setState(310);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(313);
			match(T__17);
			setState(315); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(314);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(317); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterData(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitData(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitData(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataContext data() throws RecognitionException {
		DataContext _localctx = new DataContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_data);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(320); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(319);
					_la = _input.LA(1);
					if ( !(_la==T__24 || _la==T__25) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(322); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(327);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(324);
					match(NL);
					}
					} 
				}
				setState(329);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
			}
			setState(333);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(330);
					dataLine();
					}
					} 
				}
				setState(335);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<DataValueContext> dataValue() {
			return getRuleContexts(DataValueContext.class);
		}
		public DataValueContext dataValue(int i) {
			return getRuleContext(DataValueContext.class,i);
		}
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterDataLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitDataLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitDataLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataLineContext dataLine() throws RecognitionException {
		DataLineContext _localctx = new DataLineContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(454);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(336);
				label();
				setState(338); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(337);
						match(NL);
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
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(343);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
				case 1:
					{
					setState(342);
					label();
					}
					break;
				}
				setState(345);
				((DataLineContext)_localctx).directive = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29))) != 0)) ) {
					((DataLineContext)_localctx).directive = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(351);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__32) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (T__70 - 71)) | (1L << (T__71 - 71)) | (1L << (T__72 - 71)) | (1L << (T__73 - 71)) | (1L << (T__74 - 71)) | (1L << (T__75 - 71)) | (1L << (T__76 - 71)) | (1L << (T__77 - 71)) | (1L << (T__78 - 71)) | (1L << (T__79 - 71)) | (1L << (PTR - 71)) | (1L << (IDENTIFIER - 71)) | (1L << (STRING - 71)) | (1L << (QUAD - 71)) | (1L << (BIN - 71)) | (1L << (HEX - 71)) | (1L << (NUMBER - 71)))) != 0)) {
					{
					setState(346);
					expression(0);
					setState(349);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__2) {
						{
						setState(347);
						match(T__2);
						setState(348);
						expression(0);
						}
					}

					}
				}

				setState(354); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(353);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(356); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,51,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(359);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(358);
					label();
					}
					break;
				}
				setState(361);
				((DataLineContext)_localctx).directive = match(TYPE);
				setState(362);
				dataValue();
				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(363);
					match(T__2);
					setState(364);
					dataValue();
					}
					}
					setState(369);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(371); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(370);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(373); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(376);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(375);
					label();
					}
					break;
				}
				setState(378);
				((DataLineContext)_localctx).directive = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__30 || _la==T__31) ) {
					((DataLineContext)_localctx).directive = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(379);
				dataValue();
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
					_alt = getInterpreter().adaptivePredict(_input,56,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(386);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					setState(385);
					label();
					}
					break;
				}
				setState(389);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(388);
					condition();
					}
					break;
				}
				setState(391);
				opcode();
				setState(392);
				argument();
				setState(393);
				match(T__2);
				setState(394);
				argument();
				setState(395);
				match(T__2);
				setState(396);
				argument();
				setState(398);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(397);
					effect();
					}
					break;
				}
				setState(401); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(400);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(403); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(406);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
				case 1:
					{
					setState(405);
					label();
					}
					break;
				}
				setState(409);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
				case 1:
					{
					setState(408);
					condition();
					}
					break;
				}
				setState(411);
				opcode();
				setState(412);
				argument();
				setState(413);
				match(T__2);
				setState(414);
				argument();
				setState(416);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
				case 1:
					{
					setState(415);
					effect();
					}
					break;
				}
				setState(419); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(418);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(421); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(424);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
				case 1:
					{
					setState(423);
					label();
					}
					break;
				}
				setState(427);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(426);
					condition();
					}
					break;
				}
				setState(429);
				opcode();
				setState(430);
				argument();
				setState(432);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(431);
					effect();
					}
					break;
				}
				setState(435); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(434);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(437); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(440);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
				case 1:
					{
					setState(439);
					label();
					}
					break;
				}
				setState(443);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
				case 1:
					{
					setState(442);
					condition();
					}
					break;
				}
				setState(445);
				opcode();
				setState(447);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
				case 1:
					{
					setState(446);
					effect();
					}
					break;
				}
				setState(450); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(449);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(452); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,72,_ctx);
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

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitLabel(this);
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
			setState(456);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(458);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__32) {
				{
				setState(457);
				match(T__32);
				}
			}

			setState(460);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(463);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterOpcode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitOpcode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitOpcode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpcodeContext opcode() throws RecognitionException {
		OpcodeContext _localctx = new OpcodeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_opcode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(466);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitArgument(this);
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
			setState(469);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6 || _la==T__33) {
				{
				setState(468);
				prefix();
				}
			}

			setState(471);
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
		public PrefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitPrefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitPrefix(this);
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
			setState(473);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==T__33) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(475);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__34) {
				{
				setState(474);
				match(T__34);
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
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_effect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(479);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__32) {
				{
				setState(478);
				match(T__32);
				}
			}

			setState(481);
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
		public DataValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterDataValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitDataValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitDataValue(this);
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
			setState(483);
			expression(0);
			setState(488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(484);
				match(T__4);
				setState(485);
				((DataValueContext)_localctx).count = expression(0);
				setState(486);
				match(T__5);
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
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitExpression(this);
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
			setState(506);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__35:
			case T__36:
			case T__37:
			case T__38:
			case T__39:
				{
				setState(491);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(492);
				((ExpressionContext)_localctx).exp = expression(12);
				}
				break;
			case T__70:
			case T__71:
			case T__72:
			case T__73:
			case T__74:
			case T__75:
				{
				setState(493);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (T__70 - 71)) | (1L << (T__71 - 71)) | (1L << (T__72 - 71)) | (1L << (T__73 - 71)) | (1L << (T__74 - 71)) | (1L << (T__75 - 71)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(494);
				match(T__16);
				setState(495);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(496);
				match(T__17);
				}
				break;
			case T__16:
				{
				setState(498);
				match(T__16);
				setState(499);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(500);
				match(T__17);
				}
				break;
			case T__32:
			case T__76:
			case T__77:
			case T__78:
			case T__79:
			case PTR:
			case IDENTIFIER:
			case STRING:
			case QUAD:
			case BIN:
			case HEX:
			case NUMBER:
				{
				setState(503);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__76) {
					{
					setState(502);
					match(T__76);
					}
				}

				setState(505);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(537);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(535);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(508);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(509);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__40 || _la==T__41) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(510);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(511);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(512);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__18) | (1L << T__42) | (1L << T__43))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(513);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(514);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(515);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(516);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(517);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(518);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__23) | (1L << T__35) | (1L << T__36) | (1L << T__49) | (1L << T__50))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(519);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(520);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(521);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__51) | (1L << T__52) | (1L << T__53) | (1L << T__54))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(522);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(523);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(524);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 56)) & ~0x3f) == 0 && ((1L << (_la - 56)) & ((1L << (T__55 - 56)) | (1L << (T__56 - 56)) | (1L << (T__57 - 56)) | (1L << (T__58 - 56)) | (1L << (T__59 - 56)) | (1L << (T__60 - 56)) | (1L << (T__61 - 56)) | (1L << (T__62 - 56)) | (1L << (T__63 - 56)) | (1L << (T__64 - 56)) | (1L << (T__65 - 56)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(525);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(526);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(527);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (T__66 - 67)) | (1L << (T__67 - 67)) | (1L << (T__68 - 67)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(528);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(529);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(530);
						((ExpressionContext)_localctx).operator = match(T__69);
						setState(531);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(532);
						((ExpressionContext)_localctx).operator = match(T__9);
						setState(533);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(539);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
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
		public TerminalNode PTR() { return getToken(Spin2Parser.PTR, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(Spin2Parser.IDENTIFIER, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_atom);
		int _la;
		try {
			setState(582);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(540);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(541);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(542);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(543);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(544);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(545);
				match(T__77);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(546);
				match(PTR);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(547);
				match(PTR);
				setState(552);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
				case 1:
					{
					setState(548);
					match(T__4);
					setState(549);
					expression(0);
					setState(550);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(554);
				match(PTR);
				setState(555);
				_la = _input.LA(1);
				if ( !(_la==T__78 || _la==T__79) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(560);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
				case 1:
					{
					setState(556);
					match(T__4);
					setState(557);
					expression(0);
					setState(558);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(562);
				match(PTR);
				setState(567);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(563);
					match(T__4);
					setState(564);
					expression(0);
					setState(565);
					match(T__5);
					}
				}

				setState(569);
				_la = _input.LA(1);
				if ( !(_la==T__78 || _la==T__79) ) {
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
				setState(570);
				_la = _input.LA(1);
				if ( !(_la==T__78 || _la==T__79) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(571);
				match(PTR);
				setState(576);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
				case 1:
					{
					setState(572);
					match(T__4);
					setState(573);
					expression(0);
					setState(574);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(579);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__32) {
					{
					setState(578);
					match(T__32);
					}
				}

				setState(581);
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
			return precpred(_ctx, 11);
		case 5:
			return precpred(_ctx, 10);
		case 6:
			return precpred(_ctx, 9);
		case 7:
			return precpred(_ctx, 8);
		case 8:
			return precpred(_ctx, 7);
		case 9:
			return precpred(_ctx, 6);
		case 10:
			return precpred(_ctx, 5);
		case 11:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3_\u024b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\7\2<\n\2\f\2\16\2?\13\2\3"+
		"\2\3\2\3\2\3\2\3\2\7\2F\n\2\f\2\16\2I\13\2\3\2\3\2\3\3\6\3N\n\3\r\3\16"+
		"\3O\3\3\7\3S\n\3\f\3\16\3V\13\3\3\3\7\3Y\n\3\f\3\16\3\\\13\3\3\4\5\4_"+
		"\n\4\3\4\3\4\3\4\3\4\7\4e\n\4\f\4\16\4h\13\4\3\4\5\4k\n\4\3\4\3\4\3\4"+
		"\3\4\3\4\5\4r\n\4\3\4\7\4u\n\4\f\4\16\4x\13\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\5\4\u0080\n\4\3\4\7\4\u0083\n\4\f\4\16\4\u0086\13\4\5\4\u0088\n\4\3\5"+
		"\6\5\u008b\n\5\r\5\16\5\u008c\3\5\7\5\u0090\n\5\f\5\16\5\u0093\13\5\3"+
		"\5\7\5\u0096\n\5\f\5\16\5\u0099\13\5\3\6\3\6\3\6\3\6\3\6\7\6\u00a0\n\6"+
		"\f\6\16\6\u00a3\13\6\3\6\3\6\3\6\6\6\u00a8\n\6\r\6\16\6\u00a9\3\7\3\7"+
		"\3\b\3\b\3\t\6\t\u00b1\n\t\r\t\16\t\u00b2\3\t\7\t\u00b6\n\t\f\t\16\t\u00b9"+
		"\13\t\3\t\7\t\u00bc\n\t\f\t\16\t\u00bf\13\t\3\n\3\n\3\n\3\n\3\n\3\n\5"+
		"\n\u00c7\n\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00cf\n\n\7\n\u00d1\n\n\f\n\16"+
		"\n\u00d4\13\n\3\n\6\n\u00d7\n\n\r\n\16\n\u00d8\3\13\3\13\3\13\3\13\5\13"+
		"\u00df\n\13\3\13\3\13\3\13\5\13\u00e4\n\13\3\13\3\13\5\13\u00e8\n\13\3"+
		"\13\6\13\u00eb\n\13\r\13\16\13\u00ec\3\13\7\13\u00f0\n\13\f\13\16\13\u00f3"+
		"\13\13\3\f\3\f\3\f\7\f\u00f8\n\f\f\f\16\f\u00fb\13\f\3\r\3\r\3\r\7\r\u0100"+
		"\n\r\f\r\16\r\u0103\13\r\3\16\3\16\3\16\7\16\u0108\n\16\f\16\16\16\u010b"+
		"\13\16\3\17\5\17\u010e\n\17\3\17\5\17\u0111\n\17\3\17\3\17\3\17\3\17\3"+
		"\17\5\17\u0118\n\17\3\20\3\20\6\20\u011c\n\20\r\20\16\20\u011d\3\20\3"+
		"\20\5\20\u0122\n\20\3\21\3\21\3\21\3\21\3\21\5\21\u0129\n\21\3\21\6\21"+
		"\u012c\n\21\r\21\16\21\u012d\3\22\3\22\3\22\3\22\3\22\7\22\u0135\n\22"+
		"\f\22\16\22\u0138\13\22\5\22\u013a\n\22\3\22\3\22\6\22\u013e\n\22\r\22"+
		"\16\22\u013f\3\23\6\23\u0143\n\23\r\23\16\23\u0144\3\23\7\23\u0148\n\23"+
		"\f\23\16\23\u014b\13\23\3\23\7\23\u014e\n\23\f\23\16\23\u0151\13\23\3"+
		"\24\3\24\6\24\u0155\n\24\r\24\16\24\u0156\3\24\5\24\u015a\n\24\3\24\3"+
		"\24\3\24\3\24\5\24\u0160\n\24\5\24\u0162\n\24\3\24\6\24\u0165\n\24\r\24"+
		"\16\24\u0166\3\24\5\24\u016a\n\24\3\24\3\24\3\24\3\24\7\24\u0170\n\24"+
		"\f\24\16\24\u0173\13\24\3\24\6\24\u0176\n\24\r\24\16\24\u0177\3\24\5\24"+
		"\u017b\n\24\3\24\3\24\3\24\6\24\u0180\n\24\r\24\16\24\u0181\3\24\5\24"+
		"\u0185\n\24\3\24\5\24\u0188\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5"+
		"\24\u0191\n\24\3\24\6\24\u0194\n\24\r\24\16\24\u0195\3\24\5\24\u0199\n"+
		"\24\3\24\5\24\u019c\n\24\3\24\3\24\3\24\3\24\3\24\5\24\u01a3\n\24\3\24"+
		"\6\24\u01a6\n\24\r\24\16\24\u01a7\3\24\5\24\u01ab\n\24\3\24\5\24\u01ae"+
		"\n\24\3\24\3\24\3\24\5\24\u01b3\n\24\3\24\6\24\u01b6\n\24\r\24\16\24\u01b7"+
		"\3\24\5\24\u01bb\n\24\3\24\5\24\u01be\n\24\3\24\3\24\5\24\u01c2\n\24\3"+
		"\24\6\24\u01c5\n\24\r\24\16\24\u01c6\5\24\u01c9\n\24\3\25\3\25\5\25\u01cd"+
		"\n\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\5\30\u01d8\n\30\3\30"+
		"\3\30\3\31\3\31\5\31\u01de\n\31\3\32\3\32\5\32\u01e2\n\32\3\32\3\32\3"+
		"\33\3\33\3\33\3\33\3\33\5\33\u01eb\n\33\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u01fa\n\34\3\34\5\34\u01fd\n"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\7"+
		"\34\u021a\n\34\f\34\16\34\u021d\13\34\3\35\3\35\3\35\3\35\3\35\3\35\3"+
		"\35\3\35\3\35\3\35\3\35\3\35\5\35\u022b\n\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\35\5\35\u0233\n\35\3\35\3\35\3\35\3\35\3\35\5\35\u023a\n\35\3\35\3"+
		"\35\3\35\3\35\3\35\3\35\3\35\5\35\u0243\n\35\3\35\5\35\u0246\n\35\3\35"+
		"\5\35\u0249\n\35\3\35\2\3\66\36\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		" \"$&(*,.\60\62\64\668\2\26\3\2\3\4\3\2\n\13\3\2\r\16\3\2\17\22\3\2\26"+
		"\27\3\2\31\32\3\2\33\34\3\2\35 \3\2!\"\4\2\t\t$$\3\2&*\3\2IN\3\2+,\4\2"+
		"\25\25-.\3\2/\63\5\2\32\32&\'\64\65\3\2\669\3\2:D\3\2EG\3\2QR\2\u02a5"+
		"\2=\3\2\2\2\4M\3\2\2\2\6\u0087\3\2\2\2\b\u008a\3\2\2\2\n\u009a\3\2\2\2"+
		"\f\u00ab\3\2\2\2\16\u00ad\3\2\2\2\20\u00b0\3\2\2\2\22\u00c0\3\2\2\2\24"+
		"\u00da\3\2\2\2\26\u00f4\3\2\2\2\30\u00fc\3\2\2\2\32\u0104\3\2\2\2\34\u010d"+
		"\3\2\2\2\36\u0121\3\2\2\2 \u0123\3\2\2\2\"\u012f\3\2\2\2$\u0142\3\2\2"+
		"\2&\u01c8\3\2\2\2(\u01ca\3\2\2\2*\u01d0\3\2\2\2,\u01d3\3\2\2\2.\u01d7"+
		"\3\2\2\2\60\u01db\3\2\2\2\62\u01df\3\2\2\2\64\u01e5\3\2\2\2\66\u01fc\3"+
		"\2\2\28\u0248\3\2\2\2:<\7^\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2"+
		">G\3\2\2\2?=\3\2\2\2@F\5\4\3\2AF\5\b\5\2BF\5\20\t\2CF\5\24\13\2DF\5$\23"+
		"\2E@\3\2\2\2EA\3\2\2\2EB\3\2\2\2EC\3\2\2\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2"+
		"\2GH\3\2\2\2HJ\3\2\2\2IG\3\2\2\2JK\7\2\2\3K\3\3\2\2\2LN\t\2\2\2ML\3\2"+
		"\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2PT\3\2\2\2QS\7^\2\2RQ\3\2\2\2SV\3\2"+
		"\2\2TR\3\2\2\2TU\3\2\2\2UZ\3\2\2\2VT\3\2\2\2WY\5\6\4\2XW\3\2\2\2Y\\\3"+
		"\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\5\3\2\2\2\\Z\3\2\2\2]_\7\5\2\2^]\3\2\2\2^"+
		"_\3\2\2\2_`\3\2\2\2`a\7X\2\2ab\7\6\2\2bf\5\66\34\2ce\7^\2\2dc\3\2\2\2"+
		"eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2g\u0088\3\2\2\2hf\3\2\2\2ik\7\5\2\2ji\3"+
		"\2\2\2jk\3\2\2\2kl\3\2\2\2lq\7X\2\2mn\7\7\2\2no\5\66\34\2op\7\b\2\2pr"+
		"\3\2\2\2qm\3\2\2\2qr\3\2\2\2rv\3\2\2\2su\7^\2\2ts\3\2\2\2ux\3\2\2\2vt"+
		"\3\2\2\2vw\3\2\2\2w\u0088\3\2\2\2xv\3\2\2\2yz\7\t\2\2z\177\5\66\34\2{"+
		"|\7\7\2\2|}\5\66\34\2}~\7\b\2\2~\u0080\3\2\2\2\177{\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\u0084\3\2\2\2\u0081\u0083\7^\2\2\u0082\u0081\3\2\2\2\u0083"+
		"\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0088\3\2"+
		"\2\2\u0086\u0084\3\2\2\2\u0087^\3\2\2\2\u0087j\3\2\2\2\u0087y\3\2\2\2"+
		"\u0088\7\3\2\2\2\u0089\u008b\t\3\2\2\u008a\u0089\3\2\2\2\u008b\u008c\3"+
		"\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u0091\3\2\2\2\u008e"+
		"\u0090\7^\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2"+
		"\2\2\u0091\u0092\3\2\2\2\u0092\u0097\3\2\2\2\u0093\u0091\3\2\2\2\u0094"+
		"\u0096\5\n\6\2\u0095\u0094\3\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2"+
		"\2\2\u0097\u0098\3\2\2\2\u0098\t\3\2\2\2\u0099\u0097\3\2\2\2\u009a\u00a1"+
		"\5\f\7\2\u009b\u009c\7\7\2\2\u009c\u009d\5\66\34\2\u009d\u009e\7\b\2\2"+
		"\u009e\u00a0\3\2\2\2\u009f\u009b\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f"+
		"\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4"+
		"\u00a5\7\f\2\2\u00a5\u00a7\5\16\b\2\u00a6\u00a8\7^\2\2\u00a7\u00a6\3\2"+
		"\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa"+
		"\13\3\2\2\2\u00ab\u00ac\7X\2\2\u00ac\r\3\2\2\2\u00ad\u00ae\7Y\2\2\u00ae"+
		"\17\3\2\2\2\u00af\u00b1\t\4\2\2\u00b0\u00af\3\2\2\2\u00b1\u00b2\3\2\2"+
		"\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b7\3\2\2\2\u00b4\u00b6"+
		"\7^\2\2\u00b5\u00b4\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7"+
		"\u00b8\3\2\2\2\u00b8\u00bd\3\2\2\2\u00b9\u00b7\3\2\2\2\u00ba\u00bc\5\22"+
		"\n\2\u00bb\u00ba\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\21\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c1\7U\2\2"+
		"\u00c1\u00c6\7X\2\2\u00c2\u00c3\7\7\2\2\u00c3\u00c4\5\66\34\2\u00c4\u00c5"+
		"\7\b\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c2\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7"+
		"\u00d2\3\2\2\2\u00c8\u00c9\7\5\2\2\u00c9\u00ce\7X\2\2\u00ca\u00cb\7\7"+
		"\2\2\u00cb\u00cc\5\66\34\2\u00cc\u00cd\7\b\2\2\u00cd\u00cf\3\2\2\2\u00ce"+
		"\u00ca\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d1\3\2\2\2\u00d0\u00c8\3\2"+
		"\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3"+
		"\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5\u00d7\7^\2\2\u00d6\u00d5\3\2"+
		"\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9"+
		"\23\3\2\2\2\u00da\u00db\t\5\2\2\u00db\u00dc\7X\2\2\u00dc\u00de\7\23\2"+
		"\2\u00dd\u00df\5\26\f\2\u00de\u00dd\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"\u00e0\3\2\2\2\u00e0\u00e3\7\24\2\2\u00e1\u00e2\7\f\2\2\u00e2\u00e4\5"+
		"\30\r\2\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5"+
		"\u00e6\7\25\2\2\u00e6\u00e8\5\32\16\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8"+
		"\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00eb\7^\2\2\u00ea\u00e9\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00f1\3\2"+
		"\2\2\u00ee\u00f0\5\36\20\2\u00ef\u00ee\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\25\3\2\2\2\u00f3\u00f1\3\2\2"+
		"\2\u00f4\u00f9\7X\2\2\u00f5\u00f6\7\5\2\2\u00f6\u00f8\7X\2\2\u00f7\u00f5"+
		"\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa"+
		"\27\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc\u0101\7X\2\2\u00fd\u00fe\7\5\2\2"+
		"\u00fe\u0100\7X\2\2\u00ff\u00fd\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff"+
		"\3\2\2\2\u0101\u0102\3\2\2\2\u0102\31\3\2\2\2\u0103\u0101\3\2\2\2\u0104"+
		"\u0109\5\34\17\2\u0105\u0106\7\5\2\2\u0106\u0108\5\34\17\2\u0107\u0105"+
		"\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
		"\33\3\2\2\2\u010b\u0109\3\2\2\2\u010c\u010e\t\6\2\2\u010d\u010c\3\2\2"+
		"\2\u010d\u010e\3\2\2\2\u010e\u0110\3\2\2\2\u010f\u0111\7U\2\2\u0110\u010f"+
		"\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0117\7X\2\2\u0113"+
		"\u0114\7\7\2\2\u0114\u0115\5\66\34\2\u0115\u0116\7\b\2\2\u0116\u0118\3"+
		"\2\2\2\u0117\u0113\3\2\2\2\u0117\u0118\3\2\2\2\u0118\35\3\2\2\2\u0119"+
		"\u011b\7\30\2\2\u011a\u011c\7^\2\2\u011b\u011a\3\2\2\2\u011c\u011d\3\2"+
		"\2\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0122\3\2\2\2\u011f"+
		"\u0122\5 \21\2\u0120\u0122\5\"\22\2\u0121\u0119\3\2\2\2\u0121\u011f\3"+
		"\2\2\2\u0121\u0120\3\2\2\2\u0122\37\3\2\2\2\u0123\u0124\7X\2\2\u0124\u0128"+
		"\t\7\2\2\u0125\u0129\5\"\22\2\u0126\u0129\7X\2\2\u0127\u0129\5\66\34\2"+
		"\u0128\u0125\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0127\3\2\2\2\u0129\u012b"+
		"\3\2\2\2\u012a\u012c\7^\2\2\u012b\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d"+
		"\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e!\3\2\2\2\u012f\u0130\7X\2\2\u0130"+
		"\u0139\7\23\2\2\u0131\u0136\7X\2\2\u0132\u0133\7\5\2\2\u0133\u0135\7X"+
		"\2\2\u0134\u0132\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0134\3\2\2\2\u0136"+
		"\u0137\3\2\2\2\u0137\u013a\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u0131\3\2"+
		"\2\2\u0139\u013a\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013d\7\24\2\2\u013c"+
		"\u013e\7^\2\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u013d\3\2"+
		"\2\2\u013f\u0140\3\2\2\2\u0140#\3\2\2\2\u0141\u0143\t\b\2\2\u0142\u0141"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145"+
		"\u0149\3\2\2\2\u0146\u0148\7^\2\2\u0147\u0146\3\2\2\2\u0148\u014b\3\2"+
		"\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014f\3\2\2\2\u014b"+
		"\u0149\3\2\2\2\u014c\u014e\5&\24\2\u014d\u014c\3\2\2\2\u014e\u0151\3\2"+
		"\2\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2\2\2\u0150%\3\2\2\2\u0151\u014f"+
		"\3\2\2\2\u0152\u0154\5(\25\2\u0153\u0155\7^\2\2\u0154\u0153\3\2\2\2\u0155"+
		"\u0156\3\2\2\2\u0156\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u01c9\3\2"+
		"\2\2\u0158\u015a\5(\25\2\u0159\u0158\3\2\2\2\u0159\u015a\3\2\2\2\u015a"+
		"\u015b\3\2\2\2\u015b\u0161\t\t\2\2\u015c\u015f\5\66\34\2\u015d\u015e\7"+
		"\5\2\2\u015e\u0160\5\66\34\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160"+
		"\u0162\3\2\2\2\u0161\u015c\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0164\3\2"+
		"\2\2\u0163\u0165\7^\2\2\u0164\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166"+
		"\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u01c9\3\2\2\2\u0168\u016a\5("+
		"\25\2\u0169\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016b\3\2\2\2\u016b"+
		"\u016c\7U\2\2\u016c\u0171\5\64\33\2\u016d\u016e\7\5\2\2\u016e\u0170\5"+
		"\64\33\2\u016f\u016d\3\2\2\2\u0170\u0173\3\2\2\2\u0171\u016f\3\2\2\2\u0171"+
		"\u0172\3\2\2\2\u0172\u0175\3\2\2\2\u0173\u0171\3\2\2\2\u0174\u0176\7^"+
		"\2\2\u0175\u0174\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0175\3\2\2\2\u0177"+
		"\u0178\3\2\2\2\u0178\u01c9\3\2\2\2\u0179\u017b\5(\25\2\u017a\u0179\3\2"+
		"\2\2\u017a\u017b\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u017d\t\n\2\2\u017d"+
		"\u017f\5\64\33\2\u017e\u0180\7^\2\2\u017f\u017e\3\2\2\2\u0180\u0181\3"+
		"\2\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u01c9\3\2\2\2\u0183"+
		"\u0185\5(\25\2\u0184\u0183\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0187\3\2"+
		"\2\2\u0186\u0188\5*\26\2\u0187\u0186\3\2\2\2\u0187\u0188\3\2\2\2\u0188"+
		"\u0189\3\2\2\2\u0189\u018a\5,\27\2\u018a\u018b\5.\30\2\u018b\u018c\7\5"+
		"\2\2\u018c\u018d\5.\30\2\u018d\u018e\7\5\2\2\u018e\u0190\5.\30\2\u018f"+
		"\u0191\5\62\32\2\u0190\u018f\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0193\3"+
		"\2\2\2\u0192\u0194\7^\2\2\u0193\u0192\3\2\2\2\u0194\u0195\3\2\2\2\u0195"+
		"\u0193\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u01c9\3\2\2\2\u0197\u0199\5("+
		"\25\2\u0198\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019b\3\2\2\2\u019a"+
		"\u019c\5*\26\2\u019b\u019a\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019d\3\2"+
		"\2\2\u019d\u019e\5,\27\2\u019e\u019f\5.\30\2\u019f\u01a0\7\5\2\2\u01a0"+
		"\u01a2\5.\30\2\u01a1\u01a3\5\62\32\2\u01a2\u01a1\3\2\2\2\u01a2\u01a3\3"+
		"\2\2\2\u01a3\u01a5\3\2\2\2\u01a4\u01a6\7^\2\2\u01a5\u01a4\3\2\2\2\u01a6"+
		"\u01a7\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01c9\3\2"+
		"\2\2\u01a9\u01ab\5(\25\2\u01aa\u01a9\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab"+
		"\u01ad\3\2\2\2\u01ac\u01ae\5*\26\2\u01ad\u01ac\3\2\2\2\u01ad\u01ae\3\2"+
		"\2\2\u01ae\u01af\3\2\2\2\u01af\u01b0\5,\27\2\u01b0\u01b2\5.\30\2\u01b1"+
		"\u01b3\5\62\32\2\u01b2\u01b1\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b5\3"+
		"\2\2\2\u01b4\u01b6\7^\2\2\u01b5\u01b4\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7"+
		"\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01c9\3\2\2\2\u01b9\u01bb\5("+
		"\25\2\u01ba\u01b9\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01bd\3\2\2\2\u01bc"+
		"\u01be\5*\26\2\u01bd\u01bc\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01bf\3\2"+
		"\2\2\u01bf\u01c1\5,\27\2\u01c0\u01c2\5\62\32\2\u01c1\u01c0\3\2\2\2\u01c1"+
		"\u01c2\3\2\2\2\u01c2\u01c4\3\2\2\2\u01c3\u01c5\7^\2\2\u01c4\u01c3\3\2"+
		"\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7"+
		"\u01c9\3\2\2\2\u01c8\u0152\3\2\2\2\u01c8\u0159\3\2\2\2\u01c8\u0169\3\2"+
		"\2\2\u01c8\u017a\3\2\2\2\u01c8\u0184\3\2\2\2\u01c8\u0198\3\2\2\2\u01c8"+
		"\u01aa\3\2\2\2\u01c8\u01ba\3\2\2\2\u01c9\'\3\2\2\2\u01ca\u01cc\6\25\2"+
		"\2\u01cb\u01cd\7#\2\2\u01cc\u01cb\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u01ce"+
		"\3\2\2\2\u01ce\u01cf\7X\2\2\u01cf)\3\2\2\2\u01d0\u01d1\6\26\3\2\u01d1"+
		"\u01d2\7W\2\2\u01d2+\3\2\2\2\u01d3\u01d4\6\27\4\2\u01d4\u01d5\7X\2\2\u01d5"+
		"-\3\2\2\2\u01d6\u01d8\5\60\31\2\u01d7\u01d6\3\2\2\2\u01d7\u01d8\3\2\2"+
		"\2\u01d8\u01d9\3\2\2\2\u01d9\u01da\5\66\34\2\u01da/\3\2\2\2\u01db\u01dd"+
		"\t\13\2\2\u01dc\u01de\7%\2\2\u01dd\u01dc\3\2\2\2\u01dd\u01de\3\2\2\2\u01de"+
		"\61\3\2\2\2\u01df\u01e1\6\32\5\2\u01e0\u01e2\7#\2\2\u01e1\u01e0\3\2\2"+
		"\2\u01e1\u01e2\3\2\2\2\u01e2\u01e3\3\2\2\2\u01e3\u01e4\7X\2\2\u01e4\63"+
		"\3\2\2\2\u01e5\u01ea\5\66\34\2\u01e6\u01e7\7\7\2\2\u01e7\u01e8\5\66\34"+
		"\2\u01e8\u01e9\7\b\2\2\u01e9\u01eb\3\2\2\2\u01ea\u01e6\3\2\2\2\u01ea\u01eb"+
		"\3\2\2\2\u01eb\65\3\2\2\2\u01ec\u01ed\b\34\1\2\u01ed\u01ee\t\f\2\2\u01ee"+
		"\u01fd\5\66\34\16\u01ef\u01f0\t\r\2\2\u01f0\u01f1\7\23\2\2\u01f1\u01f2"+
		"\5\66\34\2\u01f2\u01f3\7\24\2\2\u01f3\u01fd\3\2\2\2\u01f4\u01f5\7\23\2"+
		"\2\u01f5\u01f6\5\66\34\2\u01f6\u01f7\7\24\2\2\u01f7\u01fd\3\2\2\2\u01f8"+
		"\u01fa\7O\2\2\u01f9\u01f8\3\2\2\2\u01f9\u01fa\3\2\2\2\u01fa\u01fb\3\2"+
		"\2\2\u01fb\u01fd\58\35\2\u01fc\u01ec\3\2\2\2\u01fc\u01ef\3\2\2\2\u01fc"+
		"\u01f4\3\2\2\2\u01fc\u01f9\3\2\2\2\u01fd\u021b\3\2\2\2\u01fe\u01ff\f\r"+
		"\2\2\u01ff\u0200\t\16\2\2\u0200\u021a\5\66\34\16\u0201\u0202\f\f\2\2\u0202"+
		"\u0203\t\17\2\2\u0203\u021a\5\66\34\r\u0204\u0205\f\13\2\2\u0205\u0206"+
		"\t\20\2\2\u0206\u021a\5\66\34\f\u0207\u0208\f\n\2\2\u0208\u0209\t\21\2"+
		"\2\u0209\u021a\5\66\34\13\u020a\u020b\f\t\2\2\u020b\u020c\t\22\2\2\u020c"+
		"\u021a\5\66\34\n\u020d\u020e\f\b\2\2\u020e\u020f\t\23\2\2\u020f\u021a"+
		"\5\66\34\t\u0210\u0211\f\7\2\2\u0211\u0212\t\24\2\2\u0212\u021a\5\66\34"+
		"\b\u0213\u0214\f\6\2\2\u0214\u0215\7H\2\2\u0215\u0216\5\66\34\2\u0216"+
		"\u0217\7\f\2\2\u0217\u0218\5\66\34\7\u0218\u021a\3\2\2\2\u0219\u01fe\3"+
		"\2\2\2\u0219\u0201\3\2\2\2\u0219\u0204\3\2\2\2\u0219\u0207\3\2\2\2\u0219"+
		"\u020a\3\2\2\2\u0219\u020d\3\2\2\2\u0219\u0210\3\2\2\2\u0219\u0213\3\2"+
		"\2\2\u021a\u021d\3\2\2\2\u021b\u0219\3\2\2\2\u021b\u021c\3\2\2\2\u021c"+
		"\67\3\2\2\2\u021d\u021b\3\2\2\2\u021e\u0249\7]\2\2\u021f\u0249\7\\\2\2"+
		"\u0220\u0249\7[\2\2\u0221\u0249\7Z\2\2\u0222\u0249\7Y\2\2\u0223\u0249"+
		"\7P\2\2\u0224\u0249\7V\2\2\u0225\u022a\7V\2\2\u0226\u0227\7\7\2\2\u0227"+
		"\u0228\5\66\34\2\u0228\u0229\7\b\2\2\u0229\u022b\3\2\2\2\u022a\u0226\3"+
		"\2\2\2\u022a\u022b\3\2\2\2\u022b\u0249\3\2\2\2\u022c\u022d\7V\2\2\u022d"+
		"\u0232\t\25\2\2\u022e\u022f\7\7\2\2\u022f\u0230\5\66\34\2\u0230\u0231"+
		"\7\b\2\2\u0231\u0233\3\2\2\2\u0232\u022e\3\2\2\2\u0232\u0233\3\2\2\2\u0233"+
		"\u0249\3\2\2\2\u0234\u0239\7V\2\2\u0235\u0236\7\7\2\2\u0236\u0237\5\66"+
		"\34\2\u0237\u0238\7\b\2\2\u0238\u023a\3\2\2\2\u0239\u0235\3\2\2\2\u0239"+
		"\u023a\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u0249\t\25\2\2\u023c\u023d\t"+
		"\25\2\2\u023d\u0242\7V\2\2\u023e\u023f\7\7\2\2\u023f\u0240\5\66\34\2\u0240"+
		"\u0241\7\b\2\2\u0241\u0243\3\2\2\2\u0242\u023e\3\2\2\2\u0242\u0243\3\2"+
		"\2\2\u0243\u0249\3\2\2\2\u0244\u0246\7#\2\2\u0245\u0244\3\2\2\2\u0245"+
		"\u0246\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0249\7X\2\2\u0248\u021e\3\2"+
		"\2\2\u0248\u021f\3\2\2\2\u0248\u0220\3\2\2\2\u0248\u0221\3\2\2\2\u0248"+
		"\u0222\3\2\2\2\u0248\u0223\3\2\2\2\u0248\u0224\3\2\2\2\u0248\u0225\3\2"+
		"\2\2\u0248\u022c\3\2\2\2\u0248\u0234\3\2\2\2\u0248\u023c\3\2\2\2\u0248"+
		"\u0245\3\2\2\2\u02499\3\2\2\2[=EGOTZ^fjqv\177\u0084\u0087\u008c\u0091"+
		"\u0097\u00a1\u00a9\u00b2\u00b7\u00bd\u00c6\u00ce\u00d2\u00d8\u00de\u00e3"+
		"\u00e7\u00ec\u00f1\u00f9\u0101\u0109\u010d\u0110\u0117\u011d\u0121\u0128"+
		"\u012d\u0136\u0139\u013f\u0144\u0149\u014f\u0156\u0159\u015f\u0161\u0166"+
		"\u0169\u0171\u0177\u017a\u0181\u0184\u0187\u0190\u0195\u0198\u019b\u01a2"+
		"\u01a7\u01aa\u01ad\u01b2\u01b7\u01ba\u01bd\u01c1\u01c6\u01c8\u01cc\u01d7"+
		"\u01dd\u01e1\u01ea\u01f9\u01fc\u0219\u021b\u022a\u0232\u0239\u0242\u0245"+
		"\u0248";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}