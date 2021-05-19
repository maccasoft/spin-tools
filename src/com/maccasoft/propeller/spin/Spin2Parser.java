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
		T__80=81, T__81=82, T__82=83, T__83=84, T__84=85, T__85=86, T__86=87, 
		T__87=88, T__88=89, T__89=90, T__90=91, T__91=92, T__92=93, T__93=94, 
		T__94=95, T__95=96, T__96=97, T__97=98, T__98=99, T__99=100, T__100=101, 
		T__101=102, T__102=103, BLOCK_COMMENT=104, COMMENT=105, TYPE=106, CONDITION=107, 
		OPCODE=108, EFFECT=109, PTR=110, VARIABLE=111, STRING=112, QUAD=113, BIN=114, 
		HEX=115, NUMBER=116, NL=117, WS=118;
	public static final int
		RULE_prog = 0, RULE_constants = 1, RULE_constant = 2, RULE_objects = 3, 
		RULE_object = 4, RULE_reference = 5, RULE_filename = 6, RULE_variables = 7, 
		RULE_variable = 8, RULE_data = 9, RULE_org = 10, RULE_orgh = 11, RULE_dataLine = 12, 
		RULE_typeValue = 13, RULE_dataValue = 14, RULE_directive = 15, RULE_label = 16, 
		RULE_opcode = 17, RULE_condition = 18, RULE_effect = 19, RULE_argument = 20, 
		RULE_prefix = 21, RULE_type = 22, RULE_expression = 23, RULE_atom = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constants", "constant", "objects", "object", "reference", "filename", 
			"variables", "variable", "data", "org", "orgh", "dataLine", "typeValue", 
			"dataValue", "directive", "label", "opcode", "condition", "effect", "argument", 
			"prefix", "type", "expression", "atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'CON'", "'con'", "','", "'='", "'['", "']'", "'#'", "'OBJ'", "'obj'", 
			"':'", "'VAR'", "'var'", "'DAT'", "'dat'", "'ORG'", "'org'", "'ORGH'", 
			"'orgh'", "'RES'", "'res'", "'.'", "'ORGF'", "'orgf'", "'FIT'", "'fit'", 
			"'##'", "'\\'", "'+'", "'-'", "'!!'", "'!'", "'~'", "'ABS'", "'abs'", 
			"'ENCOD'", "'encod'", "'DECOD'", "'decod'", "'RMASK'", "'rmask'", "'ONES'", 
			"'ones'", "'SQRT'", "'sqrt'", "'QLOG'", "'qlog'", "'QEXP'", "'qexp'", 
			"'>>'", "'<<'", "'sar'", "'ror'", "'rol'", "'rev'", "'zerox'", "'signx'", 
			"'&'", "'^'", "'|'", "'*'", "'/'", "'+/'", "'//'", "'+//'", "'sca'", 
			"'scas'", "'frac'", "'#>'", "'<#'", "'ADDBITS'", "'addbits'", "'ADDPINS'", 
			"'addpins'", "'<'", "'+<'", "'<='", "'+<='", "'=='", "'<>'", "'>='", 
			"'+>='", "'>'", "'+>'", "'<=>'", "'&&'", "'and'", "'^^'", "'xor'", "'||'", 
			"'or'", "'?'", "'FLOAT'", "'float'", "'ROUND'", "'round'", "'TRUNC'", 
			"'trunc'", "'('", "')'", "'@'", "'$'", "'++'", "'--'"
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
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "BLOCK_COMMENT", "COMMENT", 
			"TYPE", "CONDITION", "OPCODE", "EFFECT", "PTR", "VARIABLE", "STRING", 
			"QUAD", "BIN", "HEX", "NUMBER", "NL", "WS"
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
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(50);
				match(NL);
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__7) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) {
				{
				setState(60);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
					{
					setState(56);
					constants();
					}
					break;
				case T__7:
				case T__8:
					{
					setState(57);
					objects();
					}
					break;
				case T__10:
				case T__11:
					{
					setState(58);
					variables();
					}
					break;
				case T__12:
				case T__13:
					{
					setState(59);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65);
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
			setState(68); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(67);
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
				setState(70); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2 || _la==T__6 || _la==VARIABLE) {
				{
				{
				setState(78);
				constant();
				}
				}
				setState(83);
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
		public TerminalNode VARIABLE() { return getToken(Spin2Parser.VARIABLE, 0); }
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
			setState(126);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(84);
					match(T__2);
					}
				}

				setState(87);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(88);
				match(T__3);
				setState(89);
				((ConstantContext)_localctx).exp = expression(0);
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(90);
					match(NL);
					}
					}
					setState(95);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(96);
					match(T__2);
					}
				}

				setState(99);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(100);
					match(T__4);
					setState(101);
					((ConstantContext)_localctx).multiplier = expression(0);
					setState(102);
					match(T__5);
					}
				}

				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(106);
					match(NL);
					}
					}
					setState(111);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(112);
				match(T__6);
				setState(113);
				((ConstantContext)_localctx).start = expression(0);
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(114);
					match(T__4);
					setState(115);
					((ConstantContext)_localctx).step = expression(0);
					setState(116);
					match(T__5);
					}
				}

				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(120);
					match(NL);
					}
					}
					setState(125);
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
			setState(129); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(128);
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
				setState(131); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(133);
				match(NL);
				}
				}
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VARIABLE) {
				{
				{
				setState(139);
				object();
				}
				}
				setState(144);
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
			setState(145);
			reference();
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(146);
				match(T__4);
				setState(147);
				expression(0);
				setState(148);
				match(T__5);
				}
				}
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
			match(T__9);
			setState(156);
			filename();
			setState(158); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(157);
				match(NL);
				}
				}
				setState(160); 
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
		public TerminalNode VARIABLE() { return getToken(Spin2Parser.VARIABLE, 0); }
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
			setState(162);
			match(VARIABLE);
			}
		}
		catch (RecognitionException re) {
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
			setState(164);
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
			setState(167); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(166);
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
				setState(169); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(171);
				match(NL);
				}
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE || _la==VARIABLE) {
				{
				{
				setState(177);
				variable();
				}
				}
				setState(182);
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
		public List<TerminalNode> VARIABLE() { return getTokens(Spin2Parser.VARIABLE); }
		public TerminalNode VARIABLE(int i) {
			return getToken(Spin2Parser.VARIABLE, i);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
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
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TYPE) {
				{
				setState(183);
				type();
				}
			}

			setState(186);
			match(VARIABLE);
			setState(191);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(187);
				match(T__4);
				setState(188);
				expression(0);
				setState(189);
				match(T__5);
				}
			}

			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(193);
				match(T__2);
				setState(194);
				match(VARIABLE);
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(195);
					match(T__4);
					setState(196);
					expression(0);
					setState(197);
					match(T__5);
					}
				}

				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(207); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(206);
				match(NL);
				}
				}
				setState(209); 
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

	public static class DataContext extends ParserRuleContext {
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<OrgContext> org() {
			return getRuleContexts(OrgContext.class);
		}
		public OrgContext org(int i) {
			return getRuleContext(OrgContext.class,i);
		}
		public List<OrghContext> orgh() {
			return getRuleContexts(OrghContext.class);
		}
		public OrghContext orgh(int i) {
			return getRuleContext(OrghContext.class,i);
		}
		public List<DirectiveContext> directive() {
			return getRuleContexts(DirectiveContext.class);
		}
		public DirectiveContext directive(int i) {
			return getRuleContext(DirectiveContext.class,i);
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
		enterRule(_localctx, 18, RULE_data);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(212); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(211);
					_la = _input.LA(1);
					if ( !(_la==T__12 || _la==T__13) ) {
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
				setState(214); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(219);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(216);
					match(NL);
					}
					} 
				}
				setState(221);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			}
			setState(228);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(226);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						setState(222);
						org();
						}
						break;
					case 2:
						{
						setState(223);
						orgh();
						}
						break;
					case 3:
						{
						setState(224);
						directive();
						}
						break;
					case 4:
						{
						setState(225);
						dataLine();
						}
						break;
					}
					} 
				}
				setState(230);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
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

	public static class OrgContext extends ParserRuleContext {
		public ExpressionContext address;
		public ExpressionContext limit;
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<DirectiveContext> directive() {
			return getRuleContexts(DirectiveContext.class);
		}
		public DirectiveContext directive(int i) {
			return getRuleContext(DirectiveContext.class,i);
		}
		public List<DataLineContext> dataLine() {
			return getRuleContexts(DataLineContext.class);
		}
		public DataLineContext dataLine(int i) {
			return getRuleContext(DataLineContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public OrgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_org; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterOrg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitOrg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitOrg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrgContext org() throws RecognitionException {
		OrgContext _localctx = new OrgContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_org);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			_la = _input.LA(1);
			if ( !(_la==T__14 || _la==T__15) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42) | (1L << T__43) | (1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47))) != 0) || ((((_la - 92)) & ~0x3f) == 0 && ((1L << (_la - 92)) & ((1L << (T__91 - 92)) | (1L << (T__92 - 92)) | (1L << (T__93 - 92)) | (1L << (T__94 - 92)) | (1L << (T__95 - 92)) | (1L << (T__96 - 92)) | (1L << (T__97 - 92)) | (1L << (T__99 - 92)) | (1L << (T__100 - 92)) | (1L << (T__101 - 92)) | (1L << (T__102 - 92)) | (1L << (PTR - 92)) | (1L << (VARIABLE - 92)) | (1L << (STRING - 92)) | (1L << (QUAD - 92)) | (1L << (BIN - 92)) | (1L << (HEX - 92)) | (1L << (NUMBER - 92)))) != 0)) {
				{
				setState(232);
				((OrgContext)_localctx).address = expression(0);
				setState(235);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(233);
					match(T__2);
					setState(234);
					((OrgContext)_localctx).limit = expression(0);
					}
				}

				}
			}

			setState(240); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(239);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(242); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(248);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(246);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
					case 1:
						{
						setState(244);
						directive();
						}
						break;
					case 2:
						{
						setState(245);
						dataLine();
						}
						break;
					}
					} 
				}
				setState(250);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
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

	public static class OrghContext extends ParserRuleContext {
		public ExpressionContext address;
		public ExpressionContext limit;
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public List<DirectiveContext> directive() {
			return getRuleContexts(DirectiveContext.class);
		}
		public DirectiveContext directive(int i) {
			return getRuleContext(DirectiveContext.class,i);
		}
		public List<DataLineContext> dataLine() {
			return getRuleContexts(DataLineContext.class);
		}
		public DataLineContext dataLine(int i) {
			return getRuleContext(DataLineContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public OrghContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orgh; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterOrgh(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitOrgh(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitOrgh(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrghContext orgh() throws RecognitionException {
		OrghContext _localctx = new OrghContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_orgh);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			_la = _input.LA(1);
			if ( !(_la==T__16 || _la==T__17) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42) | (1L << T__43) | (1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47))) != 0) || ((((_la - 92)) & ~0x3f) == 0 && ((1L << (_la - 92)) & ((1L << (T__91 - 92)) | (1L << (T__92 - 92)) | (1L << (T__93 - 92)) | (1L << (T__94 - 92)) | (1L << (T__95 - 92)) | (1L << (T__96 - 92)) | (1L << (T__97 - 92)) | (1L << (T__99 - 92)) | (1L << (T__100 - 92)) | (1L << (T__101 - 92)) | (1L << (T__102 - 92)) | (1L << (PTR - 92)) | (1L << (VARIABLE - 92)) | (1L << (STRING - 92)) | (1L << (QUAD - 92)) | (1L << (BIN - 92)) | (1L << (HEX - 92)) | (1L << (NUMBER - 92)))) != 0)) {
				{
				setState(252);
				((OrghContext)_localctx).address = expression(0);
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(253);
					match(T__2);
					setState(254);
					((OrghContext)_localctx).limit = expression(0);
					}
				}

				}
			}

			setState(260); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(259);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(262); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(268);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(266);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
					case 1:
						{
						setState(264);
						directive();
						}
						break;
					case 2:
						{
						setState(265);
						dataLine();
						}
						break;
					}
					} 
				}
				setState(270);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
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
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public List<DataValueContext> dataValue() {
			return getRuleContexts(DataValueContext.class);
		}
		public DataValueContext dataValue(int i) {
			return getRuleContext(DataValueContext.class,i);
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
		enterRule(_localctx, 24, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(363);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(271);
				label();
				setState(273); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(272);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(275); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
				case 1:
					{
					setState(277);
					label();
					}
					break;
				}
				setState(281);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
				case 1:
					{
					setState(280);
					condition();
					}
					break;
				}
				setState(283);
				opcode();
				setState(284);
				argument();
				setState(285);
				match(T__2);
				setState(286);
				argument();
				setState(287);
				match(T__2);
				setState(288);
				argument();
				setState(290);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EFFECT) {
					{
					setState(289);
					effect();
					}
				}

				setState(293); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(292);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(295); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(298);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
				case 1:
					{
					setState(297);
					label();
					}
					break;
				}
				setState(301);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
				case 1:
					{
					setState(300);
					condition();
					}
					break;
				}
				setState(303);
				opcode();
				setState(304);
				argument();
				setState(305);
				match(T__2);
				setState(306);
				argument();
				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EFFECT) {
					{
					setState(307);
					effect();
					}
				}

				setState(311); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(310);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(313); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(316);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
				case 1:
					{
					setState(315);
					label();
					}
					break;
				}
				setState(319);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(318);
					condition();
					}
					break;
				}
				setState(321);
				opcode();
				setState(322);
				argument();
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EFFECT) {
					{
					setState(323);
					effect();
					}
				}

				setState(327); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(326);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(329); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(332);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
				case 1:
					{
					setState(331);
					label();
					}
					break;
				}
				setState(335);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(334);
					condition();
					}
					break;
				}
				setState(337);
				opcode();
				setState(339);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EFFECT) {
					{
					setState(338);
					effect();
					}
				}

				setState(342); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(341);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(344); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(347);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(346);
					label();
					}
					break;
				}
				setState(349);
				directive();
				setState(350);
				dataValue();
				setState(355);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(351);
					match(T__2);
					setState(352);
					dataValue();
					}
					}
					setState(357);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(359); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(358);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(361); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
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

	public static class TypeValueContext extends ParserRuleContext {
		public ExpressionContext value;
		public ExpressionContext count;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TypeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterTypeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitTypeValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitTypeValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeValueContext typeValue() throws RecognitionException {
		TypeValueContext _localctx = new TypeValueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typeValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			((TypeValueContext)_localctx).value = expression(0);
			setState(370);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(366);
				match(T__4);
				setState(367);
				((TypeValueContext)_localctx).count = expression(0);
				setState(368);
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
		enterRule(_localctx, 28, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			expression(0);
			setState(377);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(373);
				match(T__4);
				setState(374);
				((DataValueContext)_localctx).count = expression(0);
				setState(375);
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

	public static class DirectiveContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_directive);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			_la = _input.LA(1);
			if ( !(_la==T__18 || _la==T__19 || _la==TYPE) ) {
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

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode VARIABLE() { return getToken(Spin2Parser.VARIABLE, 0); }
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
		enterRule(_localctx, 32, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(381);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(383);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(382);
				match(T__20);
				}
			}

			setState(385);
			match(VARIABLE);
			}
		}
		catch (RecognitionException re) {
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
		public TerminalNode OPCODE() { return getToken(Spin2Parser.OPCODE, 0); }
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
		enterRule(_localctx, 34, RULE_opcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24))) != 0) || _la==OPCODE) ) {
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
		enterRule(_localctx, 36, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(390);
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

	public static class EffectContext extends ParserRuleContext {
		public TerminalNode EFFECT() { return getToken(Spin2Parser.EFFECT, 0); }
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
		enterRule(_localctx, 38, RULE_effect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(EFFECT);
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 40, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6 || _la==T__25) {
				{
				setState(394);
				prefix();
				}
			}

			setState(397);
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
		enterRule(_localctx, 42, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==T__25) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(401);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(400);
				match(T__26);
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

	public static class TypeContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(Spin2Parser.TYPE, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			match(TYPE);
			}
		}
		catch (RecognitionException re) {
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
		public ExpressionContext right;
		public ExpressionContext exp;
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
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__27:
			case T__28:
			case T__29:
			case T__30:
			case T__31:
			case T__32:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
			case T__37:
			case T__38:
			case T__39:
			case T__40:
			case T__41:
			case T__42:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
				{
				setState(406);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42) | (1L << T__43) | (1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(407);
				((ExpressionContext)_localctx).right = expression(12);
				}
				break;
			case T__91:
			case T__92:
			case T__93:
			case T__94:
			case T__95:
			case T__96:
				{
				setState(408);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 92)) & ~0x3f) == 0 && ((1L << (_la - 92)) & ((1L << (T__91 - 92)) | (1L << (T__92 - 92)) | (1L << (T__93 - 92)) | (1L << (T__94 - 92)) | (1L << (T__95 - 92)) | (1L << (T__96 - 92)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(409);
				match(T__97);
				setState(410);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(411);
				match(T__98);
				}
				break;
			case T__97:
				{
				setState(413);
				match(T__97);
				setState(414);
				expression(0);
				setState(415);
				match(T__98);
				}
				break;
			case T__99:
			case T__100:
			case T__101:
			case T__102:
			case PTR:
			case VARIABLE:
			case STRING:
			case QUAD:
			case BIN:
			case HEX:
			case NUMBER:
				{
				setState(418);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__99) {
					{
					setState(417);
					match(T__99);
					}
				}

				setState(420);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(452);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(450);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(423);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(424);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__48) | (1L << T__49) | (1L << T__50) | (1L << T__51) | (1L << T__52) | (1L << T__53) | (1L << T__54) | (1L << T__55))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(425);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(426);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(427);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__56) | (1L << T__57) | (1L << T__58))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(428);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(429);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(430);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (T__59 - 60)) | (1L << (T__60 - 60)) | (1L << (T__61 - 60)) | (1L << (T__62 - 60)) | (1L << (T__63 - 60)) | (1L << (T__64 - 60)) | (1L << (T__65 - 60)) | (1L << (T__66 - 60)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(431);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(432);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(433);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 28)) & ~0x3f) == 0 && ((1L << (_la - 28)) & ((1L << (T__27 - 28)) | (1L << (T__28 - 28)) | (1L << (T__67 - 28)) | (1L << (T__68 - 28)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(434);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(435);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(436);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (T__69 - 70)) | (1L << (T__70 - 70)) | (1L << (T__71 - 70)) | (1L << (T__72 - 70)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(437);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(438);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(439);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (T__73 - 74)) | (1L << (T__74 - 74)) | (1L << (T__75 - 74)) | (1L << (T__76 - 74)) | (1L << (T__77 - 74)) | (1L << (T__78 - 74)) | (1L << (T__79 - 74)) | (1L << (T__80 - 74)) | (1L << (T__81 - 74)) | (1L << (T__82 - 74)) | (1L << (T__83 - 74)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(440);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(441);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(442);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 85)) & ~0x3f) == 0 && ((1L << (_la - 85)) & ((1L << (T__84 - 85)) | (1L << (T__85 - 85)) | (1L << (T__86 - 85)) | (1L << (T__87 - 85)) | (1L << (T__88 - 85)) | (1L << (T__89 - 85)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(443);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(444);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(445);
						((ExpressionContext)_localctx).operator = match(T__90);
						setState(446);
						expression(0);
						setState(447);
						((ExpressionContext)_localctx).operator = match(T__9);
						setState(448);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(454);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
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
		public TerminalNode VARIABLE() { return getToken(Spin2Parser.VARIABLE, 0); }
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
		enterRule(_localctx, 48, RULE_atom);
		int _la;
		try {
			setState(494);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(455);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(456);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(457);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(458);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(459);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(460);
				match(T__100);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(461);
				match(PTR);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(462);
				match(PTR);
				setState(467);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
				case 1:
					{
					setState(463);
					match(T__4);
					setState(464);
					expression(0);
					setState(465);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(469);
				match(PTR);
				setState(470);
				_la = _input.LA(1);
				if ( !(_la==T__101 || _la==T__102) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(475);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
				case 1:
					{
					setState(471);
					match(T__4);
					setState(472);
					expression(0);
					setState(473);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(477);
				match(PTR);
				setState(482);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(478);
					match(T__4);
					setState(479);
					expression(0);
					setState(480);
					match(T__5);
					}
				}

				setState(484);
				_la = _input.LA(1);
				if ( !(_la==T__101 || _la==T__102) ) {
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
				setState(485);
				_la = _input.LA(1);
				if ( !(_la==T__101 || _la==T__102) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(486);
				match(PTR);
				setState(491);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
				case 1:
					{
					setState(487);
					match(T__4);
					setState(488);
					expression(0);
					setState(489);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(493);
				match(VARIABLE);
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
		case 16:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 18:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 23:
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
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 11);
		case 3:
			return precpred(_ctx, 10);
		case 4:
			return precpred(_ctx, 9);
		case 5:
			return precpred(_ctx, 8);
		case 6:
			return precpred(_ctx, 7);
		case 7:
			return precpred(_ctx, 6);
		case 8:
			return precpred(_ctx, 5);
		case 9:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3x\u01f3\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\7\2\66\n\2\f\2\16\29\13\2\3\2\3\2\3\2\3\2\7\2?\n\2\f\2"+
		"\16\2B\13\2\3\2\3\2\3\3\6\3G\n\3\r\3\16\3H\3\3\7\3L\n\3\f\3\16\3O\13\3"+
		"\3\3\7\3R\n\3\f\3\16\3U\13\3\3\4\5\4X\n\4\3\4\3\4\3\4\3\4\7\4^\n\4\f\4"+
		"\16\4a\13\4\3\4\5\4d\n\4\3\4\3\4\3\4\3\4\3\4\5\4k\n\4\3\4\7\4n\n\4\f\4"+
		"\16\4q\13\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4y\n\4\3\4\7\4|\n\4\f\4\16\4\177"+
		"\13\4\5\4\u0081\n\4\3\5\6\5\u0084\n\5\r\5\16\5\u0085\3\5\7\5\u0089\n\5"+
		"\f\5\16\5\u008c\13\5\3\5\7\5\u008f\n\5\f\5\16\5\u0092\13\5\3\6\3\6\3\6"+
		"\3\6\3\6\7\6\u0099\n\6\f\6\16\6\u009c\13\6\3\6\3\6\3\6\6\6\u00a1\n\6\r"+
		"\6\16\6\u00a2\3\7\3\7\3\b\3\b\3\t\6\t\u00aa\n\t\r\t\16\t\u00ab\3\t\7\t"+
		"\u00af\n\t\f\t\16\t\u00b2\13\t\3\t\7\t\u00b5\n\t\f\t\16\t\u00b8\13\t\3"+
		"\n\5\n\u00bb\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u00c2\n\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\5\n\u00ca\n\n\7\n\u00cc\n\n\f\n\16\n\u00cf\13\n\3\n\6\n\u00d2\n\n"+
		"\r\n\16\n\u00d3\3\13\6\13\u00d7\n\13\r\13\16\13\u00d8\3\13\7\13\u00dc"+
		"\n\13\f\13\16\13\u00df\13\13\3\13\3\13\3\13\3\13\7\13\u00e5\n\13\f\13"+
		"\16\13\u00e8\13\13\3\f\3\f\3\f\3\f\5\f\u00ee\n\f\5\f\u00f0\n\f\3\f\6\f"+
		"\u00f3\n\f\r\f\16\f\u00f4\3\f\3\f\7\f\u00f9\n\f\f\f\16\f\u00fc\13\f\3"+
		"\r\3\r\3\r\3\r\5\r\u0102\n\r\5\r\u0104\n\r\3\r\6\r\u0107\n\r\r\r\16\r"+
		"\u0108\3\r\3\r\7\r\u010d\n\r\f\r\16\r\u0110\13\r\3\16\3\16\6\16\u0114"+
		"\n\16\r\16\16\16\u0115\3\16\5\16\u0119\n\16\3\16\5\16\u011c\n\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0125\n\16\3\16\6\16\u0128\n\16\r"+
		"\16\16\16\u0129\3\16\5\16\u012d\n\16\3\16\5\16\u0130\n\16\3\16\3\16\3"+
		"\16\3\16\3\16\5\16\u0137\n\16\3\16\6\16\u013a\n\16\r\16\16\16\u013b\3"+
		"\16\5\16\u013f\n\16\3\16\5\16\u0142\n\16\3\16\3\16\3\16\5\16\u0147\n\16"+
		"\3\16\6\16\u014a\n\16\r\16\16\16\u014b\3\16\5\16\u014f\n\16\3\16\5\16"+
		"\u0152\n\16\3\16\3\16\5\16\u0156\n\16\3\16\6\16\u0159\n\16\r\16\16\16"+
		"\u015a\3\16\5\16\u015e\n\16\3\16\3\16\3\16\3\16\7\16\u0164\n\16\f\16\16"+
		"\16\u0167\13\16\3\16\6\16\u016a\n\16\r\16\16\16\u016b\5\16\u016e\n\16"+
		"\3\17\3\17\3\17\3\17\3\17\5\17\u0175\n\17\3\20\3\20\3\20\3\20\3\20\5\20"+
		"\u017c\n\20\3\21\3\21\3\22\3\22\5\22\u0182\n\22\3\22\3\22\3\23\3\23\3"+
		"\24\3\24\3\24\3\25\3\25\3\26\5\26\u018e\n\26\3\26\3\26\3\27\3\27\5\27"+
		"\u0194\n\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\5\31\u01a5\n\31\3\31\5\31\u01a8\n\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u01c5\n\31\f\31"+
		"\16\31\u01c8\13\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3"+
		"\32\3\32\5\32\u01d6\n\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u01de\n\32"+
		"\3\32\3\32\3\32\3\32\3\32\5\32\u01e5\n\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\5\32\u01ee\n\32\3\32\5\32\u01f1\n\32\3\32\2\3\60\33\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\25\3\2\3\4\3\2\n\13\3\2\r"+
		"\16\3\2\17\20\3\2\21\22\3\2\23\24\4\2\25\26ll\4\2\30\33nn\4\2\t\t\34\34"+
		"\3\2\36\62\3\2^c\3\2\63:\3\2;=\3\2>E\4\2\36\37FG\3\2HK\3\2LV\3\2W\\\3"+
		"\2hi\2\u0240\2\67\3\2\2\2\4F\3\2\2\2\6\u0080\3\2\2\2\b\u0083\3\2\2\2\n"+
		"\u0093\3\2\2\2\f\u00a4\3\2\2\2\16\u00a6\3\2\2\2\20\u00a9\3\2\2\2\22\u00ba"+
		"\3\2\2\2\24\u00d6\3\2\2\2\26\u00e9\3\2\2\2\30\u00fd\3\2\2\2\32\u016d\3"+
		"\2\2\2\34\u016f\3\2\2\2\36\u0176\3\2\2\2 \u017d\3\2\2\2\"\u017f\3\2\2"+
		"\2$\u0185\3\2\2\2&\u0187\3\2\2\2(\u018a\3\2\2\2*\u018d\3\2\2\2,\u0191"+
		"\3\2\2\2.\u0195\3\2\2\2\60\u01a7\3\2\2\2\62\u01f0\3\2\2\2\64\66\7w\2\2"+
		"\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28@\3\2\2\29\67\3\2"+
		"\2\2:?\5\4\3\2;?\5\b\5\2<?\5\20\t\2=?\5\24\13\2>:\3\2\2\2>;\3\2\2\2><"+
		"\3\2\2\2>=\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2AC\3\2\2\2B@\3\2\2\2C"+
		"D\7\2\2\3D\3\3\2\2\2EG\t\2\2\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2"+
		"IM\3\2\2\2JL\7w\2\2KJ\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NS\3\2\2\2"+
		"OM\3\2\2\2PR\5\6\4\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\5\3\2\2"+
		"\2US\3\2\2\2VX\7\5\2\2WV\3\2\2\2WX\3\2\2\2XY\3\2\2\2YZ\7q\2\2Z[\7\6\2"+
		"\2[_\5\60\31\2\\^\7w\2\2]\\\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`\u0081"+
		"\3\2\2\2a_\3\2\2\2bd\7\5\2\2cb\3\2\2\2cd\3\2\2\2de\3\2\2\2ej\7q\2\2fg"+
		"\7\7\2\2gh\5\60\31\2hi\7\b\2\2ik\3\2\2\2jf\3\2\2\2jk\3\2\2\2ko\3\2\2\2"+
		"ln\7w\2\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2p\u0081\3\2\2\2qo\3\2"+
		"\2\2rs\7\t\2\2sx\5\60\31\2tu\7\7\2\2uv\5\60\31\2vw\7\b\2\2wy\3\2\2\2x"+
		"t\3\2\2\2xy\3\2\2\2y}\3\2\2\2z|\7w\2\2{z\3\2\2\2|\177\3\2\2\2}{\3\2\2"+
		"\2}~\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\u0080W\3\2\2\2\u0080c\3\2\2\2"+
		"\u0080r\3\2\2\2\u0081\7\3\2\2\2\u0082\u0084\t\3\2\2\u0083\u0082\3\2\2"+
		"\2\u0084\u0085\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u008a"+
		"\3\2\2\2\u0087\u0089\7w\2\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0090\3\2\2\2\u008c\u008a\3\2"+
		"\2\2\u008d\u008f\5\n\6\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\t\3\2\2\2\u0092\u0090\3\2\2\2"+
		"\u0093\u009a\5\f\7\2\u0094\u0095\7\7\2\2\u0095\u0096\5\60\31\2\u0096\u0097"+
		"\7\b\2\2\u0097\u0099\3\2\2\2\u0098\u0094\3\2\2\2\u0099\u009c\3\2\2\2\u009a"+
		"\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009a\3\2"+
		"\2\2\u009d\u009e\7\f\2\2\u009e\u00a0\5\16\b\2\u009f\u00a1\7w\2\2\u00a0"+
		"\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2"+
		"\2\2\u00a3\13\3\2\2\2\u00a4\u00a5\7q\2\2\u00a5\r\3\2\2\2\u00a6\u00a7\7"+
		"r\2\2\u00a7\17\3\2\2\2\u00a8\u00aa\t\4\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ab"+
		"\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00b0\3\2\2\2\u00ad"+
		"\u00af\7w\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0\u00ae\3\2"+
		"\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b6\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3"+
		"\u00b5\5\22\n\2\u00b4\u00b3\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3"+
		"\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\21\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9"+
		"\u00bb\5.\30\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\3\2"+
		"\2\2\u00bc\u00c1\7q\2\2\u00bd\u00be\7\7\2\2\u00be\u00bf\5\60\31\2\u00bf"+
		"\u00c0\7\b\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bd\3\2\2\2\u00c1\u00c2\3\2"+
		"\2\2\u00c2\u00cd\3\2\2\2\u00c3\u00c4\7\5\2\2\u00c4\u00c9\7q\2\2\u00c5"+
		"\u00c6\7\7\2\2\u00c6\u00c7\5\60\31\2\u00c7\u00c8\7\b\2\2\u00c8\u00ca\3"+
		"\2\2\2\u00c9\u00c5\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c3\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2"+
		"\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00d2\7w\2\2\u00d1"+
		"\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2"+
		"\2\2\u00d4\23\3\2\2\2\u00d5\u00d7\t\5\2\2\u00d6\u00d5\3\2\2\2\u00d7\u00d8"+
		"\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00dd\3\2\2\2\u00da"+
		"\u00dc\7w\2\2\u00db\u00da\3\2\2\2\u00dc\u00df\3\2\2\2\u00dd\u00db\3\2"+
		"\2\2\u00dd\u00de\3\2\2\2\u00de\u00e6\3\2\2\2\u00df\u00dd\3\2\2\2\u00e0"+
		"\u00e5\5\26\f\2\u00e1\u00e5\5\30\r\2\u00e2\u00e5\5 \21\2\u00e3\u00e5\5"+
		"\32\16\2\u00e4\u00e0\3\2\2\2\u00e4\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4"+
		"\u00e3\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7\3\2"+
		"\2\2\u00e7\25\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00ef\t\6\2\2\u00ea\u00ed"+
		"\5\60\31\2\u00eb\u00ec\7\5\2\2\u00ec\u00ee\5\60\31\2\u00ed\u00eb\3\2\2"+
		"\2\u00ed\u00ee\3\2\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00ea\3\2\2\2\u00ef\u00f0"+
		"\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1\u00f3\7w\2\2\u00f2\u00f1\3\2\2\2\u00f3"+
		"\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00fa\3\2"+
		"\2\2\u00f6\u00f9\5 \21\2\u00f7\u00f9\5\32\16\2\u00f8\u00f6\3\2\2\2\u00f8"+
		"\u00f7\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00fb\3\2"+
		"\2\2\u00fb\27\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fd\u0103\t\7\2\2\u00fe\u0101"+
		"\5\60\31\2\u00ff\u0100\7\5\2\2\u0100\u0102\5\60\31\2\u0101\u00ff\3\2\2"+
		"\2\u0101\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103\u00fe\3\2\2\2\u0103\u0104"+
		"\3\2\2\2\u0104\u0106\3\2\2\2\u0105\u0107\7w\2\2\u0106\u0105\3\2\2\2\u0107"+
		"\u0108\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010e\3\2"+
		"\2\2\u010a\u010d\5 \21\2\u010b\u010d\5\32\16\2\u010c\u010a\3\2\2\2\u010c"+
		"\u010b\3\2\2\2\u010d\u0110\3\2\2\2\u010e\u010c\3\2\2\2\u010e\u010f\3\2"+
		"\2\2\u010f\31\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0113\5\"\22\2\u0112\u0114"+
		"\7w\2\2\u0113\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0113\3\2\2\2\u0115"+
		"\u0116\3\2\2\2\u0116\u016e\3\2\2\2\u0117\u0119\5\"\22\2\u0118\u0117\3"+
		"\2\2\2\u0118\u0119\3\2\2\2\u0119\u011b\3\2\2\2\u011a\u011c\5&\24\2\u011b"+
		"\u011a\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011e\5$"+
		"\23\2\u011e\u011f\5*\26\2\u011f\u0120\7\5\2\2\u0120\u0121\5*\26\2\u0121"+
		"\u0122\7\5\2\2\u0122\u0124\5*\26\2\u0123\u0125\5(\25\2\u0124\u0123\3\2"+
		"\2\2\u0124\u0125\3\2\2\2\u0125\u0127\3\2\2\2\u0126\u0128\7w\2\2\u0127"+
		"\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2"+
		"\2\2\u012a\u016e\3\2\2\2\u012b\u012d\5\"\22\2\u012c\u012b\3\2\2\2\u012c"+
		"\u012d\3\2\2\2\u012d\u012f\3\2\2\2\u012e\u0130\5&\24\2\u012f\u012e\3\2"+
		"\2\2\u012f\u0130\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132\5$\23\2\u0132"+
		"\u0133\5*\26\2\u0133\u0134\7\5\2\2\u0134\u0136\5*\26\2\u0135\u0137\5("+
		"\25\2\u0136\u0135\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0139\3\2\2\2\u0138"+
		"\u013a\7w\2\2\u0139\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u0139\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c\u016e\3\2\2\2\u013d\u013f\5\"\22\2\u013e"+
		"\u013d\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0141\3\2\2\2\u0140\u0142\5&"+
		"\24\2\u0141\u0140\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\3\2\2\2\u0143"+
		"\u0144\5$\23\2\u0144\u0146\5*\26\2\u0145\u0147\5(\25\2\u0146\u0145\3\2"+
		"\2\2\u0146\u0147\3\2\2\2\u0147\u0149\3\2\2\2\u0148\u014a\7w\2\2\u0149"+
		"\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2\2\2\u014b\u014c\3\2"+
		"\2\2\u014c\u016e\3\2\2\2\u014d\u014f\5\"\22\2\u014e\u014d\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0151\3\2\2\2\u0150\u0152\5&\24\2\u0151\u0150\3\2"+
		"\2\2\u0151\u0152\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0155\5$\23\2\u0154"+
		"\u0156\5(\25\2\u0155\u0154\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0158\3\2"+
		"\2\2\u0157\u0159\7w\2\2\u0158\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a"+
		"\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u016e\3\2\2\2\u015c\u015e\5\""+
		"\22\2\u015d\u015c\3\2\2\2\u015d\u015e\3\2\2\2\u015e\u015f\3\2\2\2\u015f"+
		"\u0160\5 \21\2\u0160\u0165\5\36\20\2\u0161\u0162\7\5\2\2\u0162\u0164\5"+
		"\36\20\2\u0163\u0161\3\2\2\2\u0164\u0167\3\2\2\2\u0165\u0163\3\2\2\2\u0165"+
		"\u0166\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0165\3\2\2\2\u0168\u016a\7w"+
		"\2\2\u0169\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u0169\3\2\2\2\u016b"+
		"\u016c\3\2\2\2\u016c\u016e\3\2\2\2\u016d\u0111\3\2\2\2\u016d\u0118\3\2"+
		"\2\2\u016d\u012c\3\2\2\2\u016d\u013e\3\2\2\2\u016d\u014e\3\2\2\2\u016d"+
		"\u015d\3\2\2\2\u016e\33\3\2\2\2\u016f\u0174\5\60\31\2\u0170\u0171\7\7"+
		"\2\2\u0171\u0172\5\60\31\2\u0172\u0173\7\b\2\2\u0173\u0175\3\2\2\2\u0174"+
		"\u0170\3\2\2\2\u0174\u0175\3\2\2\2\u0175\35\3\2\2\2\u0176\u017b\5\60\31"+
		"\2\u0177\u0178\7\7\2\2\u0178\u0179\5\60\31\2\u0179\u017a\7\b\2\2\u017a"+
		"\u017c\3\2\2\2\u017b\u0177\3\2\2\2\u017b\u017c\3\2\2\2\u017c\37\3\2\2"+
		"\2\u017d\u017e\t\b\2\2\u017e!\3\2\2\2\u017f\u0181\6\22\2\2\u0180\u0182"+
		"\7\27\2\2\u0181\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0183\3\2\2\2"+
		"\u0183\u0184\7q\2\2\u0184#\3\2\2\2\u0185\u0186\t\t\2\2\u0186%\3\2\2\2"+
		"\u0187\u0188\6\24\3\2\u0188\u0189\7m\2\2\u0189\'\3\2\2\2\u018a\u018b\7"+
		"o\2\2\u018b)\3\2\2\2\u018c\u018e\5,\27\2\u018d\u018c\3\2\2\2\u018d\u018e"+
		"\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u0190\5\60\31\2\u0190+\3\2\2\2\u0191"+
		"\u0193\t\n\2\2\u0192\u0194\7\35\2\2\u0193\u0192\3\2\2\2\u0193\u0194\3"+
		"\2\2\2\u0194-\3\2\2\2\u0195\u0196\7l\2\2\u0196/\3\2\2\2\u0197\u0198\b"+
		"\31\1\2\u0198\u0199\t\13\2\2\u0199\u01a8\5\60\31\16\u019a\u019b\t\f\2"+
		"\2\u019b\u019c\7d\2\2\u019c\u019d\5\60\31\2\u019d\u019e\7e\2\2\u019e\u01a8"+
		"\3\2\2\2\u019f\u01a0\7d\2\2\u01a0\u01a1\5\60\31\2\u01a1\u01a2\7e\2\2\u01a2"+
		"\u01a8\3\2\2\2\u01a3\u01a5\7f\2\2\u01a4\u01a3\3\2\2\2\u01a4\u01a5\3\2"+
		"\2\2\u01a5\u01a6\3\2\2\2\u01a6\u01a8\5\62\32\2\u01a7\u0197\3\2\2\2\u01a7"+
		"\u019a\3\2\2\2\u01a7\u019f\3\2\2\2\u01a7\u01a4\3\2\2\2\u01a8\u01c6\3\2"+
		"\2\2\u01a9\u01aa\f\r\2\2\u01aa\u01ab\t\r\2\2\u01ab\u01c5\5\60\31\16\u01ac"+
		"\u01ad\f\f\2\2\u01ad\u01ae\t\16\2\2\u01ae\u01c5\5\60\31\r\u01af\u01b0"+
		"\f\13\2\2\u01b0\u01b1\t\17\2\2\u01b1\u01c5\5\60\31\f\u01b2\u01b3\f\n\2"+
		"\2\u01b3\u01b4\t\20\2\2\u01b4\u01c5\5\60\31\13\u01b5\u01b6\f\t\2\2\u01b6"+
		"\u01b7\t\21\2\2\u01b7\u01c5\5\60\31\n\u01b8\u01b9\f\b\2\2\u01b9\u01ba"+
		"\t\22\2\2\u01ba\u01c5\5\60\31\t\u01bb\u01bc\f\7\2\2\u01bc\u01bd\t\23\2"+
		"\2\u01bd\u01c5\5\60\31\b\u01be\u01bf\f\6\2\2\u01bf\u01c0\7]\2\2\u01c0"+
		"\u01c1\5\60\31\2\u01c1\u01c2\7\f\2\2\u01c2\u01c3\5\60\31\7\u01c3\u01c5"+
		"\3\2\2\2\u01c4\u01a9\3\2\2\2\u01c4\u01ac\3\2\2\2\u01c4\u01af\3\2\2\2\u01c4"+
		"\u01b2\3\2\2\2\u01c4\u01b5\3\2\2\2\u01c4\u01b8\3\2\2\2\u01c4\u01bb\3\2"+
		"\2\2\u01c4\u01be\3\2\2\2\u01c5\u01c8\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c6"+
		"\u01c7\3\2\2\2\u01c7\61\3\2\2\2\u01c8\u01c6\3\2\2\2\u01c9\u01f1\7v\2\2"+
		"\u01ca\u01f1\7u\2\2\u01cb\u01f1\7t\2\2\u01cc\u01f1\7s\2\2\u01cd\u01f1"+
		"\7r\2\2\u01ce\u01f1\7g\2\2\u01cf\u01f1\7p\2\2\u01d0\u01d5\7p\2\2\u01d1"+
		"\u01d2\7\7\2\2\u01d2\u01d3\5\60\31\2\u01d3\u01d4\7\b\2\2\u01d4\u01d6\3"+
		"\2\2\2\u01d5\u01d1\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01f1\3\2\2\2\u01d7"+
		"\u01d8\7p\2\2\u01d8\u01dd\t\24\2\2\u01d9\u01da\7\7\2\2\u01da\u01db\5\60"+
		"\31\2\u01db\u01dc\7\b\2\2\u01dc\u01de\3\2\2\2\u01dd\u01d9\3\2\2\2\u01dd"+
		"\u01de\3\2\2\2\u01de\u01f1\3\2\2\2\u01df\u01e4\7p\2\2\u01e0\u01e1\7\7"+
		"\2\2\u01e1\u01e2\5\60\31\2\u01e2\u01e3\7\b\2\2\u01e3\u01e5\3\2\2\2\u01e4"+
		"\u01e0\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6\u01f1\t\24"+
		"\2\2\u01e7\u01e8\t\24\2\2\u01e8\u01ed\7p\2\2\u01e9\u01ea\7\7\2\2\u01ea"+
		"\u01eb\5\60\31\2\u01eb\u01ec\7\b\2\2\u01ec\u01ee\3\2\2\2\u01ed\u01e9\3"+
		"\2\2\2\u01ed\u01ee\3\2\2\2\u01ee\u01f1\3\2\2\2\u01ef\u01f1\7q\2\2\u01f0"+
		"\u01c9\3\2\2\2\u01f0\u01ca\3\2\2\2\u01f0\u01cb\3\2\2\2\u01f0\u01cc\3\2"+
		"\2\2\u01f0\u01cd\3\2\2\2\u01f0\u01ce\3\2\2\2\u01f0\u01cf\3\2\2\2\u01f0"+
		"\u01d0\3\2\2\2\u01f0\u01d7\3\2\2\2\u01f0\u01df\3\2\2\2\u01f0\u01e7\3\2"+
		"\2\2\u01f0\u01ef\3\2\2\2\u01f1\63\3\2\2\2N\67>@HMSW_cjox}\u0080\u0085"+
		"\u008a\u0090\u009a\u00a2\u00ab\u00b0\u00b6\u00ba\u00c1\u00c9\u00cd\u00d3"+
		"\u00d8\u00dd\u00e4\u00e6\u00ed\u00ef\u00f4\u00f8\u00fa\u0101\u0103\u0108"+
		"\u010c\u010e\u0115\u0118\u011b\u0124\u0129\u012c\u012f\u0136\u013b\u013e"+
		"\u0141\u0146\u014b\u014e\u0151\u0155\u015a\u015d\u0165\u016b\u016d\u0174"+
		"\u017b\u0181\u018d\u0193\u01a4\u01a7\u01c4\u01c6\u01d5\u01dd\u01e4\u01ed"+
		"\u01f0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}