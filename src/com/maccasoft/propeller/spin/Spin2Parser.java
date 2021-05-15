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
		T__101=102, T__102=103, T__103=104, T__104=105, T__105=106, T__106=107, 
		T__107=108, T__108=109, T__109=110, T__110=111, T__111=112, T__112=113, 
		T__113=114, T__114=115, T__115=116, T__116=117, T__117=118, T__118=119, 
		T__119=120, T__120=121, T__121=122, T__122=123, T__123=124, T__124=125, 
		T__125=126, T__126=127, T__127=128, T__128=129, T__129=130, T__130=131, 
		T__131=132, T__132=133, T__133=134, T__134=135, T__135=136, T__136=137, 
		T__137=138, T__138=139, T__139=140, T__140=141, T__141=142, T__142=143, 
		T__143=144, T__144=145, T__145=146, T__146=147, T__147=148, T__148=149, 
		T__149=150, T__150=151, T__151=152, T__152=153, T__153=154, T__154=155, 
		T__155=156, T__156=157, T__157=158, T__158=159, T__159=160, T__160=161, 
		T__161=162, T__162=163, T__163=164, T__164=165, T__165=166, T__166=167, 
		T__167=168, T__168=169, T__169=170, T__170=171, T__171=172, T__172=173, 
		T__173=174, T__174=175, T__175=176, T__176=177, T__177=178, T__178=179, 
		T__179=180, T__180=181, T__181=182, T__182=183, T__183=184, T__184=185, 
		T__185=186, T__186=187, T__187=188, T__188=189, T__189=190, T__190=191, 
		T__191=192, T__192=193, T__193=194, T__194=195, T__195=196, T__196=197, 
		T__197=198, T__198=199, T__199=200, T__200=201, T__201=202, T__202=203, 
		T__203=204, T__204=205, T__205=206, T__206=207, T__207=208, T__208=209, 
		T__209=210, T__210=211, T__211=212, T__212=213, T__213=214, T__214=215, 
		T__215=216, T__216=217, T__217=218, T__218=219, T__219=220, T__220=221, 
		T__221=222, T__222=223, T__223=224, T__224=225, T__225=226, T__226=227, 
		T__227=228, T__228=229, T__229=230, T__230=231, T__231=232, T__232=233, 
		T__233=234, T__234=235, T__235=236, T__236=237, T__237=238, T__238=239, 
		T__239=240, T__240=241, T__241=242, T__242=243, T__243=244, T__244=245, 
		T__245=246, T__246=247, T__247=248, T__248=249, T__249=250, T__250=251, 
		T__251=252, T__252=253, T__253=254, T__254=255, T__255=256, T__256=257, 
		T__257=258, T__258=259, T__259=260, T__260=261, T__261=262, T__262=263, 
		T__263=264, T__264=265, T__265=266, T__266=267, T__267=268, T__268=269, 
		T__269=270, T__270=271, T__271=272, T__272=273, T__273=274, T__274=275, 
		T__275=276, T__276=277, T__277=278, T__278=279, T__279=280, T__280=281, 
		T__281=282, T__282=283, T__283=284, T__284=285, T__285=286, T__286=287, 
		T__287=288, T__288=289, T__289=290, T__290=291, T__291=292, T__292=293, 
		T__293=294, T__294=295, T__295=296, T__296=297, T__297=298, T__298=299, 
		T__299=300, T__300=301, T__301=302, T__302=303, T__303=304, T__304=305, 
		T__305=306, T__306=307, T__307=308, T__308=309, T__309=310, T__310=311, 
		T__311=312, T__312=313, T__313=314, T__314=315, T__315=316, T__316=317, 
		T__317=318, T__318=319, T__319=320, T__320=321, T__321=322, T__322=323, 
		T__323=324, T__324=325, T__325=326, T__326=327, T__327=328, T__328=329, 
		T__329=330, T__330=331, T__331=332, T__332=333, T__333=334, T__334=335, 
		T__335=336, T__336=337, T__337=338, T__338=339, T__339=340, T__340=341, 
		T__341=342, T__342=343, T__343=344, T__344=345, T__345=346, T__346=347, 
		T__347=348, T__348=349, T__349=350, T__350=351, T__351=352, T__352=353, 
		T__353=354, T__354=355, T__355=356, T__356=357, T__357=358, T__358=359, 
		T__359=360, T__360=361, T__361=362, T__362=363, T__363=364, T__364=365, 
		T__365=366, T__366=367, T__367=368, T__368=369, T__369=370, T__370=371, 
		T__371=372, T__372=373, T__373=374, T__374=375, T__375=376, T__376=377, 
		T__377=378, T__378=379, T__379=380, T__380=381, T__381=382, T__382=383, 
		T__383=384, T__384=385, T__385=386, T__386=387, T__387=388, T__388=389, 
		T__389=390, T__390=391, T__391=392, T__392=393, T__393=394, T__394=395, 
		T__395=396, T__396=397, T__397=398, T__398=399, T__399=400, T__400=401, 
		T__401=402, T__402=403, T__403=404, T__404=405, T__405=406, T__406=407, 
		T__407=408, T__408=409, T__409=410, T__410=411, T__411=412, T__412=413, 
		T__413=414, T__414=415, T__415=416, T__416=417, T__417=418, T__418=419, 
		T__419=420, T__420=421, T__421=422, T__422=423, T__423=424, T__424=425, 
		T__425=426, T__426=427, T__427=428, T__428=429, T__429=430, T__430=431, 
		T__431=432, T__432=433, T__433=434, T__434=435, T__435=436, T__436=437, 
		T__437=438, T__438=439, T__439=440, T__440=441, T__441=442, T__442=443, 
		T__443=444, T__444=445, T__445=446, T__446=447, T__447=448, T__448=449, 
		T__449=450, T__450=451, T__451=452, T__452=453, T__453=454, T__454=455, 
		T__455=456, T__456=457, T__457=458, T__458=459, T__459=460, T__460=461, 
		T__461=462, T__462=463, T__463=464, T__464=465, T__465=466, T__466=467, 
		T__467=468, T__468=469, T__469=470, T__470=471, T__471=472, T__472=473, 
		T__473=474, T__474=475, T__475=476, T__476=477, T__477=478, T__478=479, 
		T__479=480, T__480=481, T__481=482, T__482=483, T__483=484, T__484=485, 
		T__485=486, T__486=487, T__487=488, T__488=489, T__489=490, T__490=491, 
		T__491=492, T__492=493, T__493=494, T__494=495, T__495=496, T__496=497, 
		T__497=498, T__498=499, T__499=500, T__500=501, T__501=502, T__502=503, 
		T__503=504, T__504=505, T__505=506, T__506=507, T__507=508, T__508=509, 
		T__509=510, T__510=511, T__511=512, T__512=513, T__513=514, T__514=515, 
		BLOCK_COMMENT=516, COMMENT=517, VARIABLE=518, STRING=519, QUAD=520, BIN=521, 
		HEX=522, NUMBER=523, NL=524, WS=525;
	public static final int
		RULE_prog = 0, RULE_constants = 1, RULE_constant = 2, RULE_objects = 3, 
		RULE_object = 4, RULE_reference = 5, RULE_filename = 6, RULE_variables = 7, 
		RULE_variable = 8, RULE_data = 9, RULE_line = 10, RULE_typeValue = 11, 
		RULE_org = 12, RULE_orgh = 13, RULE_orgf = 14, RULE_fit = 15, RULE_longData = 16, 
		RULE_wordData = 17, RULE_byteData = 18, RULE_singleValue = 19, RULE_arrayValue = 20, 
		RULE_directive = 21, RULE_label = 22, RULE_opcode = 23, RULE_condition = 24, 
		RULE_effect = 25, RULE_dst = 26, RULE_src = 27, RULE_index = 28, RULE_type = 29, 
		RULE_expression = 30, RULE_atom = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constants", "constant", "objects", "object", "reference", "filename", 
			"variables", "variable", "data", "line", "typeValue", "org", "orgh", 
			"orgf", "fit", "longData", "wordData", "byteData", "singleValue", "arrayValue", 
			"directive", "label", "opcode", "condition", "effect", "dst", "src", 
			"index", "type", "expression", "atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'CON'", "'con'", "','", "'='", "'['", "']'", "'#'", "'OBJ'", "'obj'", 
			"':'", "'VAR'", "'var'", "'DAT'", "'dat'", "'ORG'", "'org'", "'ORGH'", 
			"'orgh'", "'ORGF'", "'orgf'", "'FIT'", "'fit'", "'LONG'", "'long'", "'WORD'", 
			"'word'", "'BYTE'", "'byte'", "'RES'", "'res'", "'.'", "'nop'", "'ror'", 
			"'rol'", "'shr'", "'shl'", "'rcr'", "'rcl'", "'sar'", "'sal'", "'ADD'", 
			"'add'", "'ADDX'", "'addx'", "'ADDS'", "'adds'", "'ADDSX'", "'addsx'", 
			"'sub'", "'subx'", "'subs'", "'subsx'", "'cmp'", "'cmpx'", "'cmps'", 
			"'cmpsx'", "'cmpr'", "'cmpm'", "'subr'", "'cmpsub'", "'fge'", "'fle'", 
			"'fges'", "'fles'", "'sumc'", "'sumnc'", "'sumz'", "'sumnz'", "'testb'", 
			"'testbn'", "'bitl'", "'bith'", "'bitc'", "'bitnc'", "'bitz'", "'bitnz'", 
			"'bitrnd'", "'bitnot'", "'and'", "'andn'", "'or'", "'xor'", "'muxc'", 
			"'muxnc'", "'muxz'", "'muxnz'", "'MOV'", "'mov'", "'NOT'", "'not'", "'ABS'", 
			"'abs'", "'neg'", "'negc'", "'negnc'", "'negz'", "'negnz'", "'incmod'", 
			"'decmod'", "'zerox'", "'signx'", "'encod'", "'ones'", "'test'", "'testn'", 
			"'setnib'", "'getnib'", "'rolnib'", "'setbyte'", "'getbyte'", "'rolbyte'", 
			"'setword'", "'getword'", "'rolword'", "'altsn'", "'altgn'", "'altsb'", 
			"'altgb'", "'altsw'", "'altgw'", "'altr'", "'altd'", "'alts'", "'altb'", 
			"'alti'", "'setr'", "'setd'", "'sets'", "'decod'", "'bmask'", "'crcbit'", 
			"'crcnib'", "'muxnits'", "'muxnibs'", "'muxq'", "'movbyts'", "'mul'", 
			"'muls'", "'sca'", "'scas'", "'addpix'", "'mulpix'", "'blnpix'", "'mixpix'", 
			"'addct1'", "'addct2'", "'addct3'", "'wmlong'", "'rqpin'", "'rdpin'", 
			"'rdlut'", "'rdbyte'", "'rdword'", "'rdlong'", "'popa'", "'popb'", "'calld'", 
			"'resi3'", "'resi2'", "'resi1'", "'resi0'", "'reti3'", "'reti2'", "'reti1'", 
			"'reti0'", "'callpa'", "'callpb'", "'djz'", "'djnz'", "'djf'", "'djnf'", 
			"'ijz'", "'ijnz'", "'tjz'", "'tjnz'", "'tjf'", "'tjnf'", "'tjs'", "'tjns'", 
			"'tjv'", "'jint'", "'jct1'", "'jct2'", "'jct3'", "'jse1'", "'jse2'", 
			"'jse3'", "'jse4'", "'jpat'", "'jfbw'", "'jxmt'", "'jxfi'", "'jxro'", 
			"'jxrl'", "'jatn'", "'jqmt'", "'jnint'", "'jnct1'", "'jnct2'", "'jnct3'", 
			"'jnse1'", "'jnse2'", "'jnse3'", "'jnse4'", "'jnpat'", "'jnfbw'", "'jnxmt'", 
			"'jnxfi'", "'jnxro'", "'jnxrl'", "'jnatn'", "'jnqmt'", "'setpat'", "'akpin'", 
			"'wrpin'", "'wxpin'", "'wypin'", "'wrlut'", "'wrbyte'", "'wrword'", "'wrlong'", 
			"'pusha'", "'pushb'", "'rdfast'", "'wrfast'", "'fblock'", "'xinit'", 
			"'xstop'", "'xzero'", "'xcont'", "'rep'", "'coginit'", "'qmul'", "'qdiv'", 
			"'qfrac'", "'qsqrt'", "'qrotate'", "'qvector'", "'hubset'", "'cogid'", 
			"'cogstop'", "'locknew'", "'lockret'", "'locktry'", "'lockrel'", "'qlog'", 
			"'qexp'", "'rfbyte'", "'rfword'", "'rflong'", "'rfvar'", "'rfvars'", 
			"'wfbyte'", "'wfword'", "'wflong'", "'getqx'", "'getqy'", "'getct'", 
			"'getrnd'", "'setdacs'", "'setxfrq'", "'getxacc'", "'waitx'", "'setse1'", 
			"'setse2'", "'setse3'", "'setse4'", "'pollint'", "'pollct1'", "'pollct2'", 
			"'pollct3'", "'pollse1'", "'pollse2'", "'pollse3'", "'pollse4'", "'pollpat'", 
			"'pollfbw'", "'pollxmt'", "'pollxfi'", "'pollxro'", "'pollxrl'", "'pollatn'", 
			"'pollqmt'", "'waitint'", "'waitct1'", "'waitct2'", "'waitct3'", "'waitse1'", 
			"'waitse2'", "'waitse3'", "'waitse4'", "'waitpat'", "'waitfbw'", "'waitxmt'", 
			"'waitxfi'", "'waitxro'", "'waitxrl'", "'waitatn'", "'allowi'", "'stalli'", 
			"'trgint1'", "'trgint2'", "'trgint3'", "'nixint1'", "'nixint2'", "'nixint3'", 
			"'setint1'", "'setint2'", "'setint3'", "'setq'", "'setq2'", "'push'", 
			"'pop'", "'JMP'", "'jmp'", "'call'", "'ret'", "'calla'", "'reta'", "'callb'", 
			"'retb'", "'jmprel'", "'skip'", "'skipf'", "'execf'", "'getptr'", "'getbrk'", 
			"'cogbrk'", "'brk'", "'setluts'", "'setcy'", "'setci'", "'setcq'", "'setcfrq'", 
			"'setcmod'", "'setpiv'", "'setpix'", "'cogatn'", "'testp'", "'testpn'", 
			"'dirl'", "'dirh'", "'dirc'", "'dirnc'", "'dirz'", "'dirnz'", "'dirrnd'", 
			"'dirnot'", "'outl'", "'outh'", "'outc'", "'outnc'", "'outz'", "'outnz'", 
			"'outrnd'", "'outnot'", "'fltl'", "'flth'", "'fltc'", "'fltnc'", "'fltz'", 
			"'fltnz'", "'fltrnd'", "'fltnot'", "'drvl'", "'drvh'", "'drvc'", "'drvnc'", 
			"'drvz'", "'drvnz'", "'drvrnd'", "'drvnot'", "'splitb'", "'mergeb'", 
			"'splitw'", "'mergew'", "'seussf'", "'seussr'", "'rgbsqz'", "'rgbexp'", 
			"'xoro32'", "'rev'", "'rczr'", "'rczl'", "'wrc'", "'wrnc'", "'wrz'", 
			"'wrnz'", "'modcz'", "'modc'", "'modz'", "'setscp'", "'getscp'", "'loc'", 
			"'augs'", "'augd'", "'asmclk'", "'_ret_'", "'if_nc_and_nz'", "'if_nz_and_nc'", 
			"'if_gt'", "'if_a'", "'if_00'", "'if_nc_and_z'", "'if_z_and_nc'", "'if_01'", 
			"'if_nc'", "'if_ge'", "'if_ae'", "'if_0x'", "'if_c_and_nz'", "'if_nz_and_c'", 
			"'if_10'", "'if_nz'", "'if_ne'", "'if_x0'", "'if_c_ne_z'", "'if_z_ne_c'", 
			"'if_diff'", "'if_nc_or_nz'", "'if_nz_or_nc'", "'if_not_11'", "'if_c_and_z'", 
			"'if_z_and_c'", "'if_11'", "'if_c_eq_z'", "'if_z_eq_c'", "'if_same'", 
			"'if_z'", "'if_e'", "'if_x1'", "'if_nc_or_z'", "'if_z_or_nc'", "'if_not_10'", 
			"'if_c'", "'if_lt'", "'if_b'", "'if_1x'", "'if_c_or_nz'", "'if_nz_or_c'", 
			"'if_not_01'", "'if_c_or_z'", "'if_z_or_c'", "'if_le'", "'if_be'", "'if_not_00'", 
			"'wc'", "'wz'", "'wcz'", "'andc'", "'andz'", "'orc'", "'orz'", "'xorc'", 
			"'xorz'", "'##'", "'\\'", "'+'", "'-'", "'!!'", "'!'", "'~'", "'ENCOD'", 
			"'DECOD'", "'RMASK'", "'rmask'", "'ONES'", "'SQRT'", "'sqrt'", "'QLOG'", 
			"'QEXP'", "'>>'", "'<<'", "'&'", "'^'", "'|'", "'*'", "'/'", "'+/'", 
			"'//'", "'+//'", "'frac'", "'#>'", "'<#'", "'ADDBITS'", "'addbits'", 
			"'ADDPINS'", "'addpins'", "'<'", "'+<'", "'<='", "'+<='", "'=='", "'<>'", 
			"'>='", "'+>='", "'>'", "'+>'", "'<=>'", "'&&'", "'^^'", "'||'", "'?'", 
			"'FLOAT'", "'float'", "'ROUND'", "'round'", "'TRUNC'", "'trunc'", "'('", 
			"')'", "'@'", "'$'", "'++'", "'--'"
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
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"BLOCK_COMMENT", "COMMENT", "VARIABLE", "STRING", "QUAD", "BIN", "HEX", 
			"NUMBER", "NL", "WS"
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
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(64);
				match(NL);
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__7) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) {
				{
				setState(74);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
					{
					setState(70);
					constants();
					}
					break;
				case T__7:
				case T__8:
					{
					setState(71);
					objects();
					}
					break;
				case T__10:
				case T__11:
					{
					setState(72);
					variables();
					}
					break;
				case T__12:
				case T__13:
					{
					setState(73);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(79);
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
			setState(82); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(81);
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
				setState(84); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(86);
				match(NL);
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2 || _la==T__6 || _la==VARIABLE) {
				{
				{
				setState(92);
				constant();
				}
				}
				setState(97);
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
			setState(140);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(98);
					match(T__2);
					}
				}

				setState(101);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(102);
				match(T__3);
				setState(103);
				((ConstantContext)_localctx).exp = expression(0);
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(104);
					match(NL);
					}
					}
					setState(109);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(110);
					match(T__2);
					}
				}

				setState(113);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(114);
					match(T__4);
					setState(115);
					((ConstantContext)_localctx).multiplier = expression(0);
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
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(126);
				match(T__6);
				setState(127);
				((ConstantContext)_localctx).start = expression(0);
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(128);
					match(T__4);
					setState(129);
					((ConstantContext)_localctx).step = expression(0);
					setState(130);
					match(T__5);
					}
				}

				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(134);
					match(NL);
					}
					}
					setState(139);
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
			setState(143); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(142);
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
				setState(145); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(147);
				match(NL);
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VARIABLE) {
				{
				{
				setState(153);
				object();
				}
				}
				setState(158);
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
			setState(159);
			reference();
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(160);
				match(T__4);
				setState(161);
				expression(0);
				setState(162);
				match(T__5);
				}
				}
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(169);
			match(T__9);
			setState(170);
			filename();
			setState(172); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(171);
				match(NL);
				}
				}
				setState(174); 
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
			setState(176);
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
			setState(178);
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
			setState(181); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(180);
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
				setState(183); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27))) != 0) || _la==VARIABLE) {
				{
				{
				setState(191);
				variable();
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
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) {
				{
				setState(197);
				type();
				}
			}

			setState(200);
			match(VARIABLE);
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(201);
				match(T__4);
				setState(202);
				expression(0);
				setState(203);
				match(T__5);
				}
			}

			setState(217);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(207);
				match(T__2);
				setState(208);
				match(VARIABLE);
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(209);
					match(T__4);
					setState(210);
					expression(0);
					setState(211);
					match(T__5);
					}
				}

				}
				}
				setState(219);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(221); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(220);
				match(NL);
				}
				}
				setState(223); 
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
		public List<OrgfContext> orgf() {
			return getRuleContexts(OrgfContext.class);
		}
		public OrgfContext orgf(int i) {
			return getRuleContext(OrgfContext.class,i);
		}
		public List<DirectiveContext> directive() {
			return getRuleContexts(DirectiveContext.class);
		}
		public DirectiveContext directive(int i) {
			return getRuleContext(DirectiveContext.class,i);
		}
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public List<LongDataContext> longData() {
			return getRuleContexts(LongDataContext.class);
		}
		public LongDataContext longData(int i) {
			return getRuleContext(LongDataContext.class,i);
		}
		public List<ByteDataContext> byteData() {
			return getRuleContexts(ByteDataContext.class);
		}
		public ByteDataContext byteData(int i) {
			return getRuleContext(ByteDataContext.class,i);
		}
		public List<WordDataContext> wordData() {
			return getRuleContexts(WordDataContext.class);
		}
		public WordDataContext wordData(int i) {
			return getRuleContext(WordDataContext.class,i);
		}
		public List<FitContext> fit() {
			return getRuleContexts(FitContext.class);
		}
		public FitContext fit(int i) {
			return getRuleContext(FitContext.class,i);
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
			setState(226); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(225);
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
				setState(228); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(233);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(230);
					match(NL);
					}
					} 
				}
				setState(235);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			}
			setState(247);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(245);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						setState(236);
						org();
						}
						break;
					case 2:
						{
						setState(237);
						orgh();
						}
						break;
					case 3:
						{
						setState(238);
						orgf();
						}
						break;
					case 4:
						{
						setState(239);
						directive();
						}
						break;
					case 5:
						{
						setState(240);
						line();
						}
						break;
					case 6:
						{
						setState(241);
						longData();
						}
						break;
					case 7:
						{
						setState(242);
						byteData();
						}
						break;
					case 8:
						{
						setState(243);
						wordData();
						}
						break;
					case 9:
						{
						setState(244);
						fit();
						}
						break;
					}
					} 
				}
				setState(249);
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

	public static class LineContext extends ParserRuleContext {
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
		public DstContext dst() {
			return getRuleContext(DstContext.class,0);
		}
		public SrcContext src() {
			return getRuleContext(SrcContext.class,0);
		}
		public IndexContext index() {
			return getRuleContext(IndexContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_line);
		int _la;
		try {
			int _alt;
			setState(325);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(250);
				label();
				setState(252); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(251);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(254); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(257);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(256);
					label();
					}
					break;
				}
				setState(260);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(259);
					condition();
					}
					break;
				}
				setState(262);
				opcode();
				setState(263);
				dst();
				setState(264);
				match(T__2);
				setState(265);
				src();
				setState(266);
				match(T__2);
				setState(267);
				index();
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 447)) & ~0x3f) == 0 && ((1L << (_la - 447)) & ((1L << (T__446 - 447)) | (1L << (T__447 - 447)) | (1L << (T__448 - 447)) | (1L << (T__449 - 447)) | (1L << (T__450 - 447)) | (1L << (T__451 - 447)) | (1L << (T__452 - 447)) | (1L << (T__453 - 447)) | (1L << (T__454 - 447)))) != 0)) {
					{
					setState(268);
					effect();
					}
				}

				setState(272); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(271);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(274); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(277);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(276);
					label();
					}
					break;
				}
				setState(280);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(279);
					condition();
					}
					break;
				}
				setState(282);
				opcode();
				setState(283);
				dst();
				setState(284);
				match(T__2);
				setState(285);
				src();
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 447)) & ~0x3f) == 0 && ((1L << (_la - 447)) & ((1L << (T__446 - 447)) | (1L << (T__447 - 447)) | (1L << (T__448 - 447)) | (1L << (T__449 - 447)) | (1L << (T__450 - 447)) | (1L << (T__451 - 447)) | (1L << (T__452 - 447)) | (1L << (T__453 - 447)) | (1L << (T__454 - 447)))) != 0)) {
					{
					setState(286);
					effect();
					}
				}

				setState(290); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(289);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(292); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(295);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
				case 1:
					{
					setState(294);
					label();
					}
					break;
				}
				setState(298);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					setState(297);
					condition();
					}
					break;
				}
				setState(300);
				opcode();
				setState(301);
				dst();
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 447)) & ~0x3f) == 0 && ((1L << (_la - 447)) & ((1L << (T__446 - 447)) | (1L << (T__447 - 447)) | (1L << (T__448 - 447)) | (1L << (T__449 - 447)) | (1L << (T__450 - 447)) | (1L << (T__451 - 447)) | (1L << (T__452 - 447)) | (1L << (T__453 - 447)) | (1L << (T__454 - 447)))) != 0)) {
					{
					setState(302);
					effect();
					}
				}

				setState(306); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(305);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(308); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(311);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
				case 1:
					{
					setState(310);
					label();
					}
					break;
				}
				setState(314);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(313);
					condition();
					}
					break;
				}
				setState(316);
				opcode();
				setState(318);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 447)) & ~0x3f) == 0 && ((1L << (_la - 447)) & ((1L << (T__446 - 447)) | (1L << (T__447 - 447)) | (1L << (T__448 - 447)) | (1L << (T__449 - 447)) | (1L << (T__450 - 447)) | (1L << (T__451 - 447)) | (1L << (T__452 - 447)) | (1L << (T__453 - 447)) | (1L << (T__454 - 447)))) != 0)) {
					{
					setState(317);
					effect();
					}
				}

				setState(321); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(320);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(323); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
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
		enterRule(_localctx, 22, RULE_typeValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			((TypeValueContext)_localctx).value = expression(0);
			setState(332);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(328);
				match(T__4);
				setState(329);
				((TypeValueContext)_localctx).count = expression(0);
				setState(330);
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

	public static class OrgContext extends ParserRuleContext {
		public ExpressionContext address;
		public ExpressionContext limit;
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
		enterRule(_localctx, 24, RULE_org);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			_la = _input.LA(1);
			if ( !(_la==T__14 || _la==T__15) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (T__90 - 91)) | (1L << (T__91 - 91)) | (1L << (T__101 - 91)) | (1L << (T__102 - 91)) | (1L << (T__128 - 91)))) != 0) || _la==T__245 || _la==T__246 || ((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__459 - 458)) | (1L << (T__460 - 458)) | (1L << (T__461 - 458)) | (1L << (T__462 - 458)) | (1L << (T__463 - 458)) | (1L << (T__464 - 458)) | (1L << (T__465 - 458)) | (1L << (T__466 - 458)) | (1L << (T__467 - 458)) | (1L << (T__468 - 458)) | (1L << (T__469 - 458)) | (1L << (T__470 - 458)) | (1L << (T__503 - 458)) | (1L << (T__504 - 458)) | (1L << (T__505 - 458)) | (1L << (T__506 - 458)) | (1L << (T__507 - 458)) | (1L << (T__508 - 458)) | (1L << (T__509 - 458)) | (1L << (T__511 - 458)) | (1L << (T__512 - 458)) | (1L << (VARIABLE - 458)) | (1L << (STRING - 458)) | (1L << (QUAD - 458)) | (1L << (BIN - 458)))) != 0) || _la==HEX || _la==NUMBER) {
				{
				setState(335);
				((OrgContext)_localctx).address = expression(0);
				setState(338);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(336);
					match(T__2);
					setState(337);
					((OrgContext)_localctx).limit = expression(0);
					}
				}

				}
			}

			setState(343); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(342);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(345); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
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

	public static class OrghContext extends ParserRuleContext {
		public ExpressionContext address;
		public ExpressionContext limit;
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
		enterRule(_localctx, 26, RULE_orgh);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(347);
			_la = _input.LA(1);
			if ( !(_la==T__16 || _la==T__17) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(353);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (T__90 - 91)) | (1L << (T__91 - 91)) | (1L << (T__101 - 91)) | (1L << (T__102 - 91)) | (1L << (T__128 - 91)))) != 0) || _la==T__245 || _la==T__246 || ((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__459 - 458)) | (1L << (T__460 - 458)) | (1L << (T__461 - 458)) | (1L << (T__462 - 458)) | (1L << (T__463 - 458)) | (1L << (T__464 - 458)) | (1L << (T__465 - 458)) | (1L << (T__466 - 458)) | (1L << (T__467 - 458)) | (1L << (T__468 - 458)) | (1L << (T__469 - 458)) | (1L << (T__470 - 458)) | (1L << (T__503 - 458)) | (1L << (T__504 - 458)) | (1L << (T__505 - 458)) | (1L << (T__506 - 458)) | (1L << (T__507 - 458)) | (1L << (T__508 - 458)) | (1L << (T__509 - 458)) | (1L << (T__511 - 458)) | (1L << (T__512 - 458)) | (1L << (VARIABLE - 458)) | (1L << (STRING - 458)) | (1L << (QUAD - 458)) | (1L << (BIN - 458)))) != 0) || _la==HEX || _la==NUMBER) {
				{
				setState(348);
				((OrghContext)_localctx).address = expression(0);
				setState(351);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(349);
					match(T__2);
					setState(350);
					((OrghContext)_localctx).limit = expression(0);
					}
				}

				}
			}

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
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
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

	public static class OrgfContext extends ParserRuleContext {
		public ExpressionContext address;
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public OrgfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orgf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterOrgf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitOrgf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitOrgf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrgfContext orgf() throws RecognitionException {
		OrgfContext _localctx = new OrgfContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_orgf);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			_la = _input.LA(1);
			if ( !(_la==T__18 || _la==T__19) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (T__90 - 91)) | (1L << (T__91 - 91)) | (1L << (T__101 - 91)) | (1L << (T__102 - 91)) | (1L << (T__128 - 91)))) != 0) || _la==T__245 || _la==T__246 || ((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__459 - 458)) | (1L << (T__460 - 458)) | (1L << (T__461 - 458)) | (1L << (T__462 - 458)) | (1L << (T__463 - 458)) | (1L << (T__464 - 458)) | (1L << (T__465 - 458)) | (1L << (T__466 - 458)) | (1L << (T__467 - 458)) | (1L << (T__468 - 458)) | (1L << (T__469 - 458)) | (1L << (T__470 - 458)) | (1L << (T__503 - 458)) | (1L << (T__504 - 458)) | (1L << (T__505 - 458)) | (1L << (T__506 - 458)) | (1L << (T__507 - 458)) | (1L << (T__508 - 458)) | (1L << (T__509 - 458)) | (1L << (T__511 - 458)) | (1L << (T__512 - 458)) | (1L << (VARIABLE - 458)) | (1L << (STRING - 458)) | (1L << (QUAD - 458)) | (1L << (BIN - 458)))) != 0) || _la==HEX || _la==NUMBER) {
				{
				setState(361);
				((OrgfContext)_localctx).address = expression(0);
				}
			}

			setState(365); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(364);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(367); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
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

	public static class FitContext extends ParserRuleContext {
		public ExpressionContext limit;
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public FitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterFit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitFit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitFit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FitContext fit() throws RecognitionException {
		FitContext _localctx = new FitContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_fit);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			_la = _input.LA(1);
			if ( !(_la==T__20 || _la==T__21) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (T__90 - 91)) | (1L << (T__91 - 91)) | (1L << (T__101 - 91)) | (1L << (T__102 - 91)) | (1L << (T__128 - 91)))) != 0) || _la==T__245 || _la==T__246 || ((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__459 - 458)) | (1L << (T__460 - 458)) | (1L << (T__461 - 458)) | (1L << (T__462 - 458)) | (1L << (T__463 - 458)) | (1L << (T__464 - 458)) | (1L << (T__465 - 458)) | (1L << (T__466 - 458)) | (1L << (T__467 - 458)) | (1L << (T__468 - 458)) | (1L << (T__469 - 458)) | (1L << (T__470 - 458)) | (1L << (T__503 - 458)) | (1L << (T__504 - 458)) | (1L << (T__505 - 458)) | (1L << (T__506 - 458)) | (1L << (T__507 - 458)) | (1L << (T__508 - 458)) | (1L << (T__509 - 458)) | (1L << (T__511 - 458)) | (1L << (T__512 - 458)) | (1L << (VARIABLE - 458)) | (1L << (STRING - 458)) | (1L << (QUAD - 458)) | (1L << (BIN - 458)))) != 0) || _la==HEX || _la==NUMBER) {
				{
				setState(370);
				((FitContext)_localctx).limit = expression(0);
				}
			}

			setState(374); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(373);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(376); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
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

	public static class LongDataContext extends ParserRuleContext {
		public List<SingleValueContext> singleValue() {
			return getRuleContexts(SingleValueContext.class);
		}
		public SingleValueContext singleValue(int i) {
			return getRuleContext(SingleValueContext.class,i);
		}
		public List<ArrayValueContext> arrayValue() {
			return getRuleContexts(ArrayValueContext.class);
		}
		public ArrayValueContext arrayValue(int i) {
			return getRuleContext(ArrayValueContext.class,i);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public LongDataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_longData; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterLongData(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitLongData(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitLongData(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LongDataContext longData() throws RecognitionException {
		LongDataContext _localctx = new LongDataContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_longData);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(378);
				label();
				}
				break;
			}
			setState(381);
			_la = _input.LA(1);
			if ( !(_la==T__22 || _la==T__23) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(384);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(382);
				singleValue();
				}
				break;
			case 2:
				{
				setState(383);
				arrayValue();
				}
				break;
			}
			setState(393);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(386);
				match(T__2);
				setState(389);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
				case 1:
					{
					setState(387);
					singleValue();
					}
					break;
				case 2:
					{
					setState(388);
					arrayValue();
					}
					break;
				}
				}
				}
				setState(395);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(397); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(396);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(399); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
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

	public static class WordDataContext extends ParserRuleContext {
		public List<SingleValueContext> singleValue() {
			return getRuleContexts(SingleValueContext.class);
		}
		public SingleValueContext singleValue(int i) {
			return getRuleContext(SingleValueContext.class,i);
		}
		public List<ArrayValueContext> arrayValue() {
			return getRuleContexts(ArrayValueContext.class);
		}
		public ArrayValueContext arrayValue(int i) {
			return getRuleContext(ArrayValueContext.class,i);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public WordDataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wordData; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterWordData(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitWordData(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitWordData(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WordDataContext wordData() throws RecognitionException {
		WordDataContext _localctx = new WordDataContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_wordData);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(401);
				label();
				}
				break;
			}
			setState(404);
			_la = _input.LA(1);
			if ( !(_la==T__24 || _la==T__25) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(407);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				{
				setState(405);
				singleValue();
				}
				break;
			case 2:
				{
				setState(406);
				arrayValue();
				}
				break;
			}
			setState(416);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(409);
				match(T__2);
				setState(412);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(410);
					singleValue();
					}
					break;
				case 2:
					{
					setState(411);
					arrayValue();
					}
					break;
				}
				}
				}
				setState(418);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(420); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(419);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(422); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
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

	public static class ByteDataContext extends ParserRuleContext {
		public List<SingleValueContext> singleValue() {
			return getRuleContexts(SingleValueContext.class);
		}
		public SingleValueContext singleValue(int i) {
			return getRuleContext(SingleValueContext.class,i);
		}
		public List<ArrayValueContext> arrayValue() {
			return getRuleContexts(ArrayValueContext.class);
		}
		public ArrayValueContext arrayValue(int i) {
			return getRuleContext(ArrayValueContext.class,i);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
		public ByteDataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_byteData; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterByteData(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitByteData(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitByteData(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ByteDataContext byteData() throws RecognitionException {
		ByteDataContext _localctx = new ByteDataContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_byteData);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(425);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(424);
				label();
				}
				break;
			}
			setState(427);
			_la = _input.LA(1);
			if ( !(_la==T__26 || _la==T__27) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(430);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(428);
				singleValue();
				}
				break;
			case 2:
				{
				setState(429);
				arrayValue();
				}
				break;
			}
			setState(439);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(432);
				match(T__2);
				setState(435);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
				case 1:
					{
					setState(433);
					singleValue();
					}
					break;
				case 2:
					{
					setState(434);
					arrayValue();
					}
					break;
				}
				}
				}
				setState(441);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(443); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(442);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(445); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
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

	public static class SingleValueContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SingleValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterSingleValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitSingleValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitSingleValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleValueContext singleValue() throws RecognitionException {
		SingleValueContext _localctx = new SingleValueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_singleValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
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

	public static class ArrayValueContext extends ParserRuleContext {
		public ExpressionContext count;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArrayValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterArrayValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitArrayValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitArrayValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayValueContext arrayValue() throws RecognitionException {
		ArrayValueContext _localctx = new ArrayValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_arrayValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(449);
			expression(0);
			setState(450);
			match(T__4);
			setState(451);
			((ArrayValueContext)_localctx).count = expression(0);
			setState(452);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
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
		public ExpressionContext count;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(Spin2Parser.NL); }
		public TerminalNode NL(int i) {
			return getToken(Spin2Parser.NL, i);
		}
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
		enterRule(_localctx, 42, RULE_directive);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				{
				setState(454);
				label();
				}
				break;
			}
			setState(457);
			((DirectiveContext)_localctx).name = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__28 || _la==T__29) ) {
				((DirectiveContext)_localctx).name = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(458);
			((DirectiveContext)_localctx).count = expression(0);
			setState(460); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(459);
					match(NL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(462); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
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
		enterRule(_localctx, 44, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(466);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__30) {
				{
				setState(465);
				match(T__30);
				}
			}

			setState(468);
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
		enterRule(_localctx, 46, RULE_opcode);
		int _la;
		try {
			setState(829);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(470);
				match(T__31);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(471);
				match(T__32);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(472);
				match(T__33);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(473);
				match(T__34);
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				{
				setState(474);
				match(T__35);
				}
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				{
				setState(475);
				match(T__36);
				}
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				{
				setState(476);
				match(T__37);
				}
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				{
				setState(477);
				match(T__38);
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				{
				setState(478);
				match(T__39);
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(479);
				_la = _input.LA(1);
				if ( !(_la==T__40 || _la==T__41) ) {
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
				setState(480);
				_la = _input.LA(1);
				if ( !(_la==T__42 || _la==T__43) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(481);
				_la = _input.LA(1);
				if ( !(_la==T__44 || _la==T__45) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(482);
				_la = _input.LA(1);
				if ( !(_la==T__46 || _la==T__47) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				{
				setState(483);
				match(T__48);
				}
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				{
				setState(484);
				match(T__49);
				}
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				{
				setState(485);
				match(T__50);
				}
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				{
				setState(486);
				match(T__51);
				}
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				{
				setState(487);
				match(T__52);
				}
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				{
				setState(488);
				match(T__53);
				}
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				{
				setState(489);
				match(T__54);
				}
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				{
				setState(490);
				match(T__55);
				}
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				{
				setState(491);
				match(T__56);
				}
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				{
				setState(492);
				match(T__57);
				}
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				{
				setState(493);
				match(T__58);
				}
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				{
				setState(494);
				match(T__59);
				}
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				{
				setState(495);
				match(T__60);
				}
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				{
				setState(496);
				match(T__61);
				}
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				{
				setState(497);
				match(T__62);
				}
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				{
				setState(498);
				match(T__63);
				}
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				{
				setState(499);
				match(T__64);
				}
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				{
				setState(500);
				match(T__65);
				}
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				{
				setState(501);
				match(T__66);
				}
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				{
				setState(502);
				match(T__67);
				}
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				{
				setState(503);
				match(T__68);
				}
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				{
				setState(504);
				match(T__69);
				}
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				{
				setState(505);
				match(T__70);
				}
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				{
				setState(506);
				match(T__71);
				}
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				{
				setState(507);
				match(T__72);
				}
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				{
				setState(508);
				match(T__73);
				}
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				{
				setState(509);
				match(T__74);
				}
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				{
				setState(510);
				match(T__75);
				}
				}
				break;
			case 42:
				enterOuterAlt(_localctx, 42);
				{
				{
				setState(511);
				match(T__76);
				}
				}
				break;
			case 43:
				enterOuterAlt(_localctx, 43);
				{
				{
				setState(512);
				match(T__77);
				}
				}
				break;
			case 44:
				enterOuterAlt(_localctx, 44);
				{
				{
				setState(513);
				match(T__78);
				}
				}
				break;
			case 45:
				enterOuterAlt(_localctx, 45);
				{
				{
				setState(514);
				match(T__79);
				}
				}
				break;
			case 46:
				enterOuterAlt(_localctx, 46);
				{
				{
				setState(515);
				match(T__80);
				}
				}
				break;
			case 47:
				enterOuterAlt(_localctx, 47);
				{
				{
				setState(516);
				match(T__81);
				}
				}
				break;
			case 48:
				enterOuterAlt(_localctx, 48);
				{
				{
				setState(517);
				match(T__82);
				}
				}
				break;
			case 49:
				enterOuterAlt(_localctx, 49);
				{
				{
				setState(518);
				match(T__83);
				}
				}
				break;
			case 50:
				enterOuterAlt(_localctx, 50);
				{
				{
				setState(519);
				match(T__84);
				}
				}
				break;
			case 51:
				enterOuterAlt(_localctx, 51);
				{
				{
				setState(520);
				match(T__85);
				}
				}
				break;
			case 52:
				enterOuterAlt(_localctx, 52);
				{
				setState(521);
				_la = _input.LA(1);
				if ( !(_la==T__86 || _la==T__87) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 53:
				enterOuterAlt(_localctx, 53);
				{
				setState(522);
				_la = _input.LA(1);
				if ( !(_la==T__88 || _la==T__89) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 54:
				enterOuterAlt(_localctx, 54);
				{
				setState(523);
				_la = _input.LA(1);
				if ( !(_la==T__90 || _la==T__91) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 55:
				enterOuterAlt(_localctx, 55);
				{
				{
				setState(524);
				match(T__92);
				}
				}
				break;
			case 56:
				enterOuterAlt(_localctx, 56);
				{
				{
				setState(525);
				match(T__93);
				}
				}
				break;
			case 57:
				enterOuterAlt(_localctx, 57);
				{
				{
				setState(526);
				match(T__94);
				}
				}
				break;
			case 58:
				enterOuterAlt(_localctx, 58);
				{
				{
				setState(527);
				match(T__95);
				}
				}
				break;
			case 59:
				enterOuterAlt(_localctx, 59);
				{
				{
				setState(528);
				match(T__96);
				}
				}
				break;
			case 60:
				enterOuterAlt(_localctx, 60);
				{
				{
				setState(529);
				match(T__97);
				}
				}
				break;
			case 61:
				enterOuterAlt(_localctx, 61);
				{
				{
				setState(530);
				match(T__98);
				}
				}
				break;
			case 62:
				enterOuterAlt(_localctx, 62);
				{
				{
				setState(531);
				match(T__99);
				}
				}
				break;
			case 63:
				enterOuterAlt(_localctx, 63);
				{
				{
				setState(532);
				match(T__100);
				}
				}
				break;
			case 64:
				enterOuterAlt(_localctx, 64);
				{
				{
				setState(533);
				match(T__101);
				}
				}
				break;
			case 65:
				enterOuterAlt(_localctx, 65);
				{
				{
				setState(534);
				match(T__102);
				}
				}
				break;
			case 66:
				enterOuterAlt(_localctx, 66);
				{
				{
				setState(535);
				match(T__103);
				}
				}
				break;
			case 67:
				enterOuterAlt(_localctx, 67);
				{
				{
				setState(536);
				match(T__104);
				}
				}
				break;
			case 68:
				enterOuterAlt(_localctx, 68);
				{
				{
				setState(537);
				match(T__105);
				}
				}
				break;
			case 69:
				enterOuterAlt(_localctx, 69);
				{
				{
				setState(538);
				match(T__106);
				}
				}
				break;
			case 70:
				enterOuterAlt(_localctx, 70);
				{
				{
				setState(539);
				match(T__107);
				}
				}
				break;
			case 71:
				enterOuterAlt(_localctx, 71);
				{
				{
				setState(540);
				match(T__108);
				}
				}
				break;
			case 72:
				enterOuterAlt(_localctx, 72);
				{
				{
				setState(541);
				match(T__109);
				}
				}
				break;
			case 73:
				enterOuterAlt(_localctx, 73);
				{
				{
				setState(542);
				match(T__110);
				}
				}
				break;
			case 74:
				enterOuterAlt(_localctx, 74);
				{
				{
				setState(543);
				match(T__111);
				}
				}
				break;
			case 75:
				enterOuterAlt(_localctx, 75);
				{
				{
				setState(544);
				match(T__112);
				}
				}
				break;
			case 76:
				enterOuterAlt(_localctx, 76);
				{
				{
				setState(545);
				match(T__113);
				}
				}
				break;
			case 77:
				enterOuterAlt(_localctx, 77);
				{
				{
				setState(546);
				match(T__114);
				}
				}
				break;
			case 78:
				enterOuterAlt(_localctx, 78);
				{
				{
				setState(547);
				match(T__115);
				}
				}
				break;
			case 79:
				enterOuterAlt(_localctx, 79);
				{
				{
				setState(548);
				match(T__116);
				}
				}
				break;
			case 80:
				enterOuterAlt(_localctx, 80);
				{
				{
				setState(549);
				match(T__117);
				}
				}
				break;
			case 81:
				enterOuterAlt(_localctx, 81);
				{
				{
				setState(550);
				match(T__118);
				}
				}
				break;
			case 82:
				enterOuterAlt(_localctx, 82);
				{
				{
				setState(551);
				match(T__119);
				}
				}
				break;
			case 83:
				enterOuterAlt(_localctx, 83);
				{
				{
				setState(552);
				match(T__120);
				}
				}
				break;
			case 84:
				enterOuterAlt(_localctx, 84);
				{
				{
				setState(553);
				match(T__121);
				}
				}
				break;
			case 85:
				enterOuterAlt(_localctx, 85);
				{
				{
				setState(554);
				match(T__122);
				}
				}
				break;
			case 86:
				enterOuterAlt(_localctx, 86);
				{
				{
				setState(555);
				match(T__123);
				}
				}
				break;
			case 87:
				enterOuterAlt(_localctx, 87);
				{
				{
				setState(556);
				match(T__124);
				}
				}
				break;
			case 88:
				enterOuterAlt(_localctx, 88);
				{
				{
				setState(557);
				match(T__125);
				}
				}
				break;
			case 89:
				enterOuterAlt(_localctx, 89);
				{
				{
				setState(558);
				match(T__126);
				}
				}
				break;
			case 90:
				enterOuterAlt(_localctx, 90);
				{
				{
				setState(559);
				match(T__127);
				}
				}
				break;
			case 91:
				enterOuterAlt(_localctx, 91);
				{
				{
				setState(560);
				match(T__128);
				}
				}
				break;
			case 92:
				enterOuterAlt(_localctx, 92);
				{
				{
				setState(561);
				match(T__129);
				}
				}
				break;
			case 93:
				enterOuterAlt(_localctx, 93);
				{
				{
				setState(562);
				match(T__130);
				}
				}
				break;
			case 94:
				enterOuterAlt(_localctx, 94);
				{
				{
				setState(563);
				match(T__131);
				}
				}
				break;
			case 95:
				enterOuterAlt(_localctx, 95);
				{
				{
				setState(564);
				match(T__132);
				}
				}
				break;
			case 96:
				enterOuterAlt(_localctx, 96);
				{
				{
				setState(565);
				match(T__133);
				}
				}
				break;
			case 97:
				enterOuterAlt(_localctx, 97);
				{
				{
				setState(566);
				match(T__134);
				}
				}
				break;
			case 98:
				enterOuterAlt(_localctx, 98);
				{
				{
				setState(567);
				match(T__135);
				}
				}
				break;
			case 99:
				enterOuterAlt(_localctx, 99);
				{
				{
				setState(568);
				match(T__136);
				}
				}
				break;
			case 100:
				enterOuterAlt(_localctx, 100);
				{
				{
				setState(569);
				match(T__137);
				}
				}
				break;
			case 101:
				enterOuterAlt(_localctx, 101);
				{
				{
				setState(570);
				match(T__138);
				}
				}
				break;
			case 102:
				enterOuterAlt(_localctx, 102);
				{
				{
				setState(571);
				match(T__139);
				}
				}
				break;
			case 103:
				enterOuterAlt(_localctx, 103);
				{
				{
				setState(572);
				match(T__140);
				}
				}
				break;
			case 104:
				enterOuterAlt(_localctx, 104);
				{
				{
				setState(573);
				match(T__141);
				}
				}
				break;
			case 105:
				enterOuterAlt(_localctx, 105);
				{
				{
				setState(574);
				match(T__142);
				}
				}
				break;
			case 106:
				enterOuterAlt(_localctx, 106);
				{
				{
				setState(575);
				match(T__143);
				}
				}
				break;
			case 107:
				enterOuterAlt(_localctx, 107);
				{
				{
				setState(576);
				match(T__144);
				}
				}
				break;
			case 108:
				enterOuterAlt(_localctx, 108);
				{
				{
				setState(577);
				match(T__145);
				}
				}
				break;
			case 109:
				enterOuterAlt(_localctx, 109);
				{
				{
				setState(578);
				match(T__146);
				}
				}
				break;
			case 110:
				enterOuterAlt(_localctx, 110);
				{
				{
				setState(579);
				match(T__147);
				}
				}
				break;
			case 111:
				enterOuterAlt(_localctx, 111);
				{
				{
				setState(580);
				match(T__148);
				}
				}
				break;
			case 112:
				enterOuterAlt(_localctx, 112);
				{
				{
				setState(581);
				match(T__149);
				}
				}
				break;
			case 113:
				enterOuterAlt(_localctx, 113);
				{
				{
				setState(582);
				match(T__150);
				}
				}
				break;
			case 114:
				enterOuterAlt(_localctx, 114);
				{
				{
				setState(583);
				match(T__151);
				}
				}
				break;
			case 115:
				enterOuterAlt(_localctx, 115);
				{
				{
				setState(584);
				match(T__152);
				}
				}
				break;
			case 116:
				enterOuterAlt(_localctx, 116);
				{
				{
				setState(585);
				match(T__153);
				}
				}
				break;
			case 117:
				enterOuterAlt(_localctx, 117);
				{
				{
				setState(586);
				match(T__154);
				}
				}
				break;
			case 118:
				enterOuterAlt(_localctx, 118);
				{
				{
				setState(587);
				match(T__155);
				}
				}
				break;
			case 119:
				enterOuterAlt(_localctx, 119);
				{
				{
				setState(588);
				match(T__156);
				}
				}
				break;
			case 120:
				enterOuterAlt(_localctx, 120);
				{
				{
				setState(589);
				match(T__157);
				}
				}
				break;
			case 121:
				enterOuterAlt(_localctx, 121);
				{
				{
				setState(590);
				match(T__158);
				}
				}
				break;
			case 122:
				enterOuterAlt(_localctx, 122);
				{
				{
				setState(591);
				match(T__159);
				}
				}
				break;
			case 123:
				enterOuterAlt(_localctx, 123);
				{
				{
				setState(592);
				match(T__160);
				}
				}
				break;
			case 124:
				enterOuterAlt(_localctx, 124);
				{
				{
				setState(593);
				match(T__161);
				}
				}
				break;
			case 125:
				enterOuterAlt(_localctx, 125);
				{
				{
				setState(594);
				match(T__162);
				}
				}
				break;
			case 126:
				enterOuterAlt(_localctx, 126);
				{
				{
				setState(595);
				match(T__163);
				}
				}
				break;
			case 127:
				enterOuterAlt(_localctx, 127);
				{
				{
				setState(596);
				match(T__164);
				}
				}
				break;
			case 128:
				enterOuterAlt(_localctx, 128);
				{
				{
				setState(597);
				match(T__165);
				}
				}
				break;
			case 129:
				enterOuterAlt(_localctx, 129);
				{
				{
				setState(598);
				match(T__166);
				}
				}
				break;
			case 130:
				enterOuterAlt(_localctx, 130);
				{
				{
				setState(599);
				match(T__167);
				}
				}
				break;
			case 131:
				enterOuterAlt(_localctx, 131);
				{
				{
				setState(600);
				match(T__168);
				}
				}
				break;
			case 132:
				enterOuterAlt(_localctx, 132);
				{
				{
				setState(601);
				match(T__169);
				}
				}
				break;
			case 133:
				enterOuterAlt(_localctx, 133);
				{
				{
				setState(602);
				match(T__170);
				}
				}
				break;
			case 134:
				enterOuterAlt(_localctx, 134);
				{
				{
				setState(603);
				match(T__171);
				}
				}
				break;
			case 135:
				enterOuterAlt(_localctx, 135);
				{
				{
				setState(604);
				match(T__172);
				}
				}
				break;
			case 136:
				enterOuterAlt(_localctx, 136);
				{
				{
				setState(605);
				match(T__173);
				}
				}
				break;
			case 137:
				enterOuterAlt(_localctx, 137);
				{
				{
				setState(606);
				match(T__174);
				}
				}
				break;
			case 138:
				enterOuterAlt(_localctx, 138);
				{
				{
				setState(607);
				match(T__175);
				}
				}
				break;
			case 139:
				enterOuterAlt(_localctx, 139);
				{
				{
				setState(608);
				match(T__176);
				}
				}
				break;
			case 140:
				enterOuterAlt(_localctx, 140);
				{
				{
				setState(609);
				match(T__177);
				}
				}
				break;
			case 141:
				enterOuterAlt(_localctx, 141);
				{
				{
				setState(610);
				match(T__178);
				}
				}
				break;
			case 142:
				enterOuterAlt(_localctx, 142);
				{
				{
				setState(611);
				match(T__179);
				}
				}
				break;
			case 143:
				enterOuterAlt(_localctx, 143);
				{
				{
				setState(612);
				match(T__180);
				}
				}
				break;
			case 144:
				enterOuterAlt(_localctx, 144);
				{
				{
				setState(613);
				match(T__181);
				}
				}
				break;
			case 145:
				enterOuterAlt(_localctx, 145);
				{
				{
				setState(614);
				match(T__182);
				}
				}
				break;
			case 146:
				enterOuterAlt(_localctx, 146);
				{
				{
				setState(615);
				match(T__183);
				}
				}
				break;
			case 147:
				enterOuterAlt(_localctx, 147);
				{
				{
				setState(616);
				match(T__184);
				}
				}
				break;
			case 148:
				enterOuterAlt(_localctx, 148);
				{
				{
				setState(617);
				match(T__185);
				}
				}
				break;
			case 149:
				enterOuterAlt(_localctx, 149);
				{
				{
				setState(618);
				match(T__186);
				}
				}
				break;
			case 150:
				enterOuterAlt(_localctx, 150);
				{
				{
				setState(619);
				match(T__187);
				}
				}
				break;
			case 151:
				enterOuterAlt(_localctx, 151);
				{
				{
				setState(620);
				match(T__188);
				}
				}
				break;
			case 152:
				enterOuterAlt(_localctx, 152);
				{
				{
				setState(621);
				match(T__189);
				}
				}
				break;
			case 153:
				enterOuterAlt(_localctx, 153);
				{
				{
				setState(622);
				match(T__190);
				}
				}
				break;
			case 154:
				enterOuterAlt(_localctx, 154);
				{
				{
				setState(623);
				match(T__191);
				}
				}
				break;
			case 155:
				enterOuterAlt(_localctx, 155);
				{
				{
				setState(624);
				match(T__192);
				}
				}
				break;
			case 156:
				enterOuterAlt(_localctx, 156);
				{
				{
				setState(625);
				match(T__193);
				}
				}
				break;
			case 157:
				enterOuterAlt(_localctx, 157);
				{
				{
				setState(626);
				match(T__194);
				}
				}
				break;
			case 158:
				enterOuterAlt(_localctx, 158);
				{
				{
				setState(627);
				match(T__195);
				}
				}
				break;
			case 159:
				enterOuterAlt(_localctx, 159);
				{
				{
				setState(628);
				match(T__196);
				}
				}
				break;
			case 160:
				enterOuterAlt(_localctx, 160);
				{
				{
				setState(629);
				match(T__197);
				}
				}
				break;
			case 161:
				enterOuterAlt(_localctx, 161);
				{
				{
				setState(630);
				match(T__198);
				}
				}
				break;
			case 162:
				enterOuterAlt(_localctx, 162);
				{
				{
				setState(631);
				match(T__199);
				}
				}
				break;
			case 163:
				enterOuterAlt(_localctx, 163);
				{
				{
				setState(632);
				match(T__200);
				}
				}
				break;
			case 164:
				enterOuterAlt(_localctx, 164);
				{
				{
				setState(633);
				match(T__201);
				}
				}
				break;
			case 165:
				enterOuterAlt(_localctx, 165);
				{
				{
				setState(634);
				match(T__202);
				}
				}
				break;
			case 166:
				enterOuterAlt(_localctx, 166);
				{
				{
				setState(635);
				match(T__203);
				}
				}
				break;
			case 167:
				enterOuterAlt(_localctx, 167);
				{
				{
				setState(636);
				match(T__204);
				}
				}
				break;
			case 168:
				enterOuterAlt(_localctx, 168);
				{
				{
				setState(637);
				match(T__205);
				}
				}
				break;
			case 169:
				enterOuterAlt(_localctx, 169);
				{
				{
				setState(638);
				match(T__206);
				}
				}
				break;
			case 170:
				enterOuterAlt(_localctx, 170);
				{
				{
				setState(639);
				match(T__207);
				}
				}
				break;
			case 171:
				enterOuterAlt(_localctx, 171);
				{
				{
				setState(640);
				match(T__208);
				}
				}
				break;
			case 172:
				enterOuterAlt(_localctx, 172);
				{
				{
				setState(641);
				match(T__209);
				}
				}
				break;
			case 173:
				enterOuterAlt(_localctx, 173);
				{
				{
				setState(642);
				match(T__210);
				}
				}
				break;
			case 174:
				enterOuterAlt(_localctx, 174);
				{
				{
				setState(643);
				match(T__211);
				}
				}
				break;
			case 175:
				enterOuterAlt(_localctx, 175);
				{
				{
				setState(644);
				match(T__212);
				}
				}
				break;
			case 176:
				enterOuterAlt(_localctx, 176);
				{
				{
				setState(645);
				match(T__213);
				}
				}
				break;
			case 177:
				enterOuterAlt(_localctx, 177);
				{
				{
				setState(646);
				match(T__214);
				}
				}
				break;
			case 178:
				enterOuterAlt(_localctx, 178);
				{
				{
				setState(647);
				match(T__215);
				}
				}
				break;
			case 179:
				enterOuterAlt(_localctx, 179);
				{
				{
				setState(648);
				match(T__216);
				}
				}
				break;
			case 180:
				enterOuterAlt(_localctx, 180);
				{
				{
				setState(649);
				match(T__217);
				}
				}
				break;
			case 181:
				enterOuterAlt(_localctx, 181);
				{
				{
				setState(650);
				match(T__218);
				}
				}
				break;
			case 182:
				enterOuterAlt(_localctx, 182);
				{
				{
				setState(651);
				match(T__219);
				}
				}
				break;
			case 183:
				enterOuterAlt(_localctx, 183);
				{
				{
				setState(652);
				match(T__220);
				}
				}
				break;
			case 184:
				enterOuterAlt(_localctx, 184);
				{
				{
				setState(653);
				match(T__221);
				}
				}
				break;
			case 185:
				enterOuterAlt(_localctx, 185);
				{
				{
				setState(654);
				match(T__222);
				}
				}
				break;
			case 186:
				enterOuterAlt(_localctx, 186);
				{
				{
				setState(655);
				match(T__223);
				}
				}
				break;
			case 187:
				enterOuterAlt(_localctx, 187);
				{
				{
				setState(656);
				match(T__224);
				}
				}
				break;
			case 188:
				enterOuterAlt(_localctx, 188);
				{
				{
				setState(657);
				match(T__225);
				}
				}
				break;
			case 189:
				enterOuterAlt(_localctx, 189);
				{
				{
				setState(658);
				match(T__226);
				}
				}
				break;
			case 190:
				enterOuterAlt(_localctx, 190);
				{
				{
				setState(659);
				match(T__227);
				}
				}
				break;
			case 191:
				enterOuterAlt(_localctx, 191);
				{
				{
				setState(660);
				match(T__228);
				}
				}
				break;
			case 192:
				enterOuterAlt(_localctx, 192);
				{
				{
				setState(661);
				match(T__229);
				}
				}
				break;
			case 193:
				enterOuterAlt(_localctx, 193);
				{
				{
				setState(662);
				match(T__230);
				}
				}
				break;
			case 194:
				enterOuterAlt(_localctx, 194);
				{
				{
				setState(663);
				match(T__231);
				}
				}
				break;
			case 195:
				enterOuterAlt(_localctx, 195);
				{
				{
				setState(664);
				match(T__232);
				}
				}
				break;
			case 196:
				enterOuterAlt(_localctx, 196);
				{
				{
				setState(665);
				match(T__233);
				}
				}
				break;
			case 197:
				enterOuterAlt(_localctx, 197);
				{
				{
				setState(666);
				match(T__234);
				}
				}
				break;
			case 198:
				enterOuterAlt(_localctx, 198);
				{
				{
				setState(667);
				match(T__235);
				}
				}
				break;
			case 199:
				enterOuterAlt(_localctx, 199);
				{
				{
				setState(668);
				match(T__236);
				}
				}
				break;
			case 200:
				enterOuterAlt(_localctx, 200);
				{
				{
				setState(669);
				match(T__237);
				}
				}
				break;
			case 201:
				enterOuterAlt(_localctx, 201);
				{
				{
				setState(670);
				match(T__238);
				}
				}
				break;
			case 202:
				enterOuterAlt(_localctx, 202);
				{
				{
				setState(671);
				match(T__239);
				}
				}
				break;
			case 203:
				enterOuterAlt(_localctx, 203);
				{
				{
				setState(672);
				match(T__240);
				}
				}
				break;
			case 204:
				enterOuterAlt(_localctx, 204);
				{
				{
				setState(673);
				match(T__241);
				}
				}
				break;
			case 205:
				enterOuterAlt(_localctx, 205);
				{
				{
				setState(674);
				match(T__242);
				}
				}
				break;
			case 206:
				enterOuterAlt(_localctx, 206);
				{
				{
				setState(675);
				match(T__243);
				}
				}
				break;
			case 207:
				enterOuterAlt(_localctx, 207);
				{
				{
				setState(676);
				match(T__244);
				}
				}
				break;
			case 208:
				enterOuterAlt(_localctx, 208);
				{
				{
				setState(677);
				match(T__245);
				}
				}
				break;
			case 209:
				enterOuterAlt(_localctx, 209);
				{
				{
				setState(678);
				match(T__246);
				}
				}
				break;
			case 210:
				enterOuterAlt(_localctx, 210);
				{
				{
				setState(679);
				match(T__247);
				}
				}
				break;
			case 211:
				enterOuterAlt(_localctx, 211);
				{
				{
				setState(680);
				match(T__248);
				}
				}
				break;
			case 212:
				enterOuterAlt(_localctx, 212);
				{
				{
				setState(681);
				match(T__249);
				}
				}
				break;
			case 213:
				enterOuterAlt(_localctx, 213);
				{
				{
				setState(682);
				match(T__250);
				}
				}
				break;
			case 214:
				enterOuterAlt(_localctx, 214);
				{
				{
				setState(683);
				match(T__251);
				}
				}
				break;
			case 215:
				enterOuterAlt(_localctx, 215);
				{
				{
				setState(684);
				match(T__252);
				}
				}
				break;
			case 216:
				enterOuterAlt(_localctx, 216);
				{
				{
				setState(685);
				match(T__253);
				}
				}
				break;
			case 217:
				enterOuterAlt(_localctx, 217);
				{
				{
				setState(686);
				match(T__254);
				}
				}
				break;
			case 218:
				enterOuterAlt(_localctx, 218);
				{
				{
				setState(687);
				match(T__255);
				}
				}
				break;
			case 219:
				enterOuterAlt(_localctx, 219);
				{
				{
				setState(688);
				match(T__256);
				}
				}
				break;
			case 220:
				enterOuterAlt(_localctx, 220);
				{
				{
				setState(689);
				match(T__257);
				}
				}
				break;
			case 221:
				enterOuterAlt(_localctx, 221);
				{
				{
				setState(690);
				match(T__258);
				}
				}
				break;
			case 222:
				enterOuterAlt(_localctx, 222);
				{
				{
				setState(691);
				match(T__259);
				}
				}
				break;
			case 223:
				enterOuterAlt(_localctx, 223);
				{
				{
				setState(692);
				match(T__260);
				}
				}
				break;
			case 224:
				enterOuterAlt(_localctx, 224);
				{
				{
				setState(693);
				match(T__261);
				}
				}
				break;
			case 225:
				enterOuterAlt(_localctx, 225);
				{
				{
				setState(694);
				match(T__262);
				}
				}
				break;
			case 226:
				enterOuterAlt(_localctx, 226);
				{
				{
				setState(695);
				match(T__263);
				}
				}
				break;
			case 227:
				enterOuterAlt(_localctx, 227);
				{
				{
				setState(696);
				match(T__264);
				}
				}
				break;
			case 228:
				enterOuterAlt(_localctx, 228);
				{
				{
				setState(697);
				match(T__265);
				}
				}
				break;
			case 229:
				enterOuterAlt(_localctx, 229);
				{
				{
				setState(698);
				match(T__266);
				}
				}
				break;
			case 230:
				enterOuterAlt(_localctx, 230);
				{
				{
				setState(699);
				match(T__267);
				}
				}
				break;
			case 231:
				enterOuterAlt(_localctx, 231);
				{
				{
				setState(700);
				match(T__268);
				}
				}
				break;
			case 232:
				enterOuterAlt(_localctx, 232);
				{
				{
				setState(701);
				match(T__269);
				}
				}
				break;
			case 233:
				enterOuterAlt(_localctx, 233);
				{
				{
				setState(702);
				match(T__270);
				}
				}
				break;
			case 234:
				enterOuterAlt(_localctx, 234);
				{
				{
				setState(703);
				match(T__271);
				}
				}
				break;
			case 235:
				enterOuterAlt(_localctx, 235);
				{
				{
				setState(704);
				match(T__272);
				}
				}
				break;
			case 236:
				enterOuterAlt(_localctx, 236);
				{
				{
				setState(705);
				match(T__273);
				}
				}
				break;
			case 237:
				enterOuterAlt(_localctx, 237);
				{
				{
				setState(706);
				match(T__274);
				}
				}
				break;
			case 238:
				enterOuterAlt(_localctx, 238);
				{
				{
				setState(707);
				match(T__275);
				}
				}
				break;
			case 239:
				enterOuterAlt(_localctx, 239);
				{
				{
				setState(708);
				match(T__276);
				}
				}
				break;
			case 240:
				enterOuterAlt(_localctx, 240);
				{
				{
				setState(709);
				match(T__277);
				}
				}
				break;
			case 241:
				enterOuterAlt(_localctx, 241);
				{
				{
				setState(710);
				match(T__278);
				}
				}
				break;
			case 242:
				enterOuterAlt(_localctx, 242);
				{
				{
				setState(711);
				match(T__279);
				}
				}
				break;
			case 243:
				enterOuterAlt(_localctx, 243);
				{
				{
				setState(712);
				match(T__280);
				}
				}
				break;
			case 244:
				enterOuterAlt(_localctx, 244);
				{
				{
				setState(713);
				match(T__281);
				}
				}
				break;
			case 245:
				enterOuterAlt(_localctx, 245);
				{
				{
				setState(714);
				match(T__282);
				}
				}
				break;
			case 246:
				enterOuterAlt(_localctx, 246);
				{
				{
				setState(715);
				match(T__283);
				}
				}
				break;
			case 247:
				enterOuterAlt(_localctx, 247);
				{
				{
				setState(716);
				match(T__284);
				}
				}
				break;
			case 248:
				enterOuterAlt(_localctx, 248);
				{
				{
				setState(717);
				match(T__285);
				}
				}
				break;
			case 249:
				enterOuterAlt(_localctx, 249);
				{
				{
				setState(718);
				match(T__286);
				}
				}
				break;
			case 250:
				enterOuterAlt(_localctx, 250);
				{
				{
				setState(719);
				match(T__287);
				}
				}
				break;
			case 251:
				enterOuterAlt(_localctx, 251);
				{
				{
				setState(720);
				match(T__288);
				}
				}
				break;
			case 252:
				enterOuterAlt(_localctx, 252);
				{
				{
				setState(721);
				match(T__289);
				}
				}
				break;
			case 253:
				enterOuterAlt(_localctx, 253);
				{
				{
				setState(722);
				match(T__290);
				}
				}
				break;
			case 254:
				enterOuterAlt(_localctx, 254);
				{
				{
				setState(723);
				match(T__291);
				}
				}
				break;
			case 255:
				enterOuterAlt(_localctx, 255);
				{
				{
				setState(724);
				match(T__292);
				}
				}
				break;
			case 256:
				enterOuterAlt(_localctx, 256);
				{
				{
				setState(725);
				match(T__293);
				}
				}
				break;
			case 257:
				enterOuterAlt(_localctx, 257);
				{
				{
				setState(726);
				match(T__294);
				}
				}
				break;
			case 258:
				enterOuterAlt(_localctx, 258);
				{
				{
				setState(727);
				match(T__295);
				}
				}
				break;
			case 259:
				enterOuterAlt(_localctx, 259);
				{
				{
				setState(728);
				match(T__296);
				}
				}
				break;
			case 260:
				enterOuterAlt(_localctx, 260);
				{
				{
				setState(729);
				match(T__297);
				}
				}
				break;
			case 261:
				enterOuterAlt(_localctx, 261);
				{
				{
				setState(730);
				match(T__298);
				}
				}
				break;
			case 262:
				enterOuterAlt(_localctx, 262);
				{
				{
				setState(731);
				match(T__299);
				}
				}
				break;
			case 263:
				enterOuterAlt(_localctx, 263);
				{
				{
				setState(732);
				match(T__300);
				}
				}
				break;
			case 264:
				enterOuterAlt(_localctx, 264);
				{
				{
				setState(733);
				match(T__301);
				}
				}
				break;
			case 265:
				enterOuterAlt(_localctx, 265);
				{
				{
				setState(734);
				match(T__302);
				}
				}
				break;
			case 266:
				enterOuterAlt(_localctx, 266);
				{
				{
				setState(735);
				match(T__303);
				}
				}
				break;
			case 267:
				enterOuterAlt(_localctx, 267);
				{
				{
				setState(736);
				match(T__304);
				}
				}
				break;
			case 268:
				enterOuterAlt(_localctx, 268);
				{
				{
				setState(737);
				match(T__305);
				}
				}
				break;
			case 269:
				enterOuterAlt(_localctx, 269);
				{
				{
				setState(738);
				match(T__306);
				}
				}
				break;
			case 270:
				enterOuterAlt(_localctx, 270);
				{
				{
				setState(739);
				match(T__307);
				}
				}
				break;
			case 271:
				enterOuterAlt(_localctx, 271);
				{
				{
				setState(740);
				match(T__308);
				}
				}
				break;
			case 272:
				enterOuterAlt(_localctx, 272);
				{
				{
				setState(741);
				match(T__309);
				}
				}
				break;
			case 273:
				enterOuterAlt(_localctx, 273);
				{
				{
				setState(742);
				match(T__310);
				}
				}
				break;
			case 274:
				enterOuterAlt(_localctx, 274);
				{
				{
				setState(743);
				match(T__311);
				}
				}
				break;
			case 275:
				enterOuterAlt(_localctx, 275);
				{
				{
				setState(744);
				match(T__312);
				}
				}
				break;
			case 276:
				enterOuterAlt(_localctx, 276);
				{
				setState(745);
				_la = _input.LA(1);
				if ( !(_la==T__313 || _la==T__314) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 277:
				enterOuterAlt(_localctx, 277);
				{
				{
				setState(746);
				match(T__315);
				}
				}
				break;
			case 278:
				enterOuterAlt(_localctx, 278);
				{
				{
				setState(747);
				match(T__316);
				}
				}
				break;
			case 279:
				enterOuterAlt(_localctx, 279);
				{
				{
				setState(748);
				match(T__317);
				}
				}
				break;
			case 280:
				enterOuterAlt(_localctx, 280);
				{
				{
				setState(749);
				match(T__318);
				}
				}
				break;
			case 281:
				enterOuterAlt(_localctx, 281);
				{
				{
				setState(750);
				match(T__319);
				}
				}
				break;
			case 282:
				enterOuterAlt(_localctx, 282);
				{
				{
				setState(751);
				match(T__320);
				}
				}
				break;
			case 283:
				enterOuterAlt(_localctx, 283);
				{
				{
				setState(752);
				match(T__321);
				}
				}
				break;
			case 284:
				enterOuterAlt(_localctx, 284);
				{
				{
				setState(753);
				match(T__322);
				}
				}
				break;
			case 285:
				enterOuterAlt(_localctx, 285);
				{
				{
				setState(754);
				match(T__323);
				}
				}
				break;
			case 286:
				enterOuterAlt(_localctx, 286);
				{
				{
				setState(755);
				match(T__324);
				}
				}
				break;
			case 287:
				enterOuterAlt(_localctx, 287);
				{
				{
				setState(756);
				match(T__325);
				}
				}
				break;
			case 288:
				enterOuterAlt(_localctx, 288);
				{
				{
				setState(757);
				match(T__326);
				}
				}
				break;
			case 289:
				enterOuterAlt(_localctx, 289);
				{
				{
				setState(758);
				match(T__327);
				}
				}
				break;
			case 290:
				enterOuterAlt(_localctx, 290);
				{
				{
				setState(759);
				match(T__328);
				}
				}
				break;
			case 291:
				enterOuterAlt(_localctx, 291);
				{
				{
				setState(760);
				match(T__329);
				}
				}
				break;
			case 292:
				enterOuterAlt(_localctx, 292);
				{
				{
				setState(761);
				match(T__330);
				}
				}
				break;
			case 293:
				enterOuterAlt(_localctx, 293);
				{
				{
				setState(762);
				match(T__331);
				}
				}
				break;
			case 294:
				enterOuterAlt(_localctx, 294);
				{
				{
				setState(763);
				match(T__332);
				}
				}
				break;
			case 295:
				enterOuterAlt(_localctx, 295);
				{
				{
				setState(764);
				match(T__333);
				}
				}
				break;
			case 296:
				enterOuterAlt(_localctx, 296);
				{
				{
				setState(765);
				match(T__334);
				}
				}
				break;
			case 297:
				enterOuterAlt(_localctx, 297);
				{
				{
				setState(766);
				match(T__335);
				}
				}
				break;
			case 298:
				enterOuterAlt(_localctx, 298);
				{
				{
				setState(767);
				match(T__336);
				}
				}
				break;
			case 299:
				enterOuterAlt(_localctx, 299);
				{
				{
				setState(768);
				match(T__337);
				}
				}
				break;
			case 300:
				enterOuterAlt(_localctx, 300);
				{
				{
				setState(769);
				match(T__338);
				}
				}
				break;
			case 301:
				enterOuterAlt(_localctx, 301);
				{
				{
				setState(770);
				match(T__339);
				}
				}
				break;
			case 302:
				enterOuterAlt(_localctx, 302);
				{
				{
				setState(771);
				match(T__340);
				}
				}
				break;
			case 303:
				enterOuterAlt(_localctx, 303);
				{
				{
				setState(772);
				match(T__341);
				}
				}
				break;
			case 304:
				enterOuterAlt(_localctx, 304);
				{
				{
				setState(773);
				match(T__342);
				}
				}
				break;
			case 305:
				enterOuterAlt(_localctx, 305);
				{
				{
				setState(774);
				match(T__343);
				}
				}
				break;
			case 306:
				enterOuterAlt(_localctx, 306);
				{
				{
				setState(775);
				match(T__344);
				}
				}
				break;
			case 307:
				enterOuterAlt(_localctx, 307);
				{
				{
				setState(776);
				match(T__345);
				}
				}
				break;
			case 308:
				enterOuterAlt(_localctx, 308);
				{
				{
				setState(777);
				match(T__346);
				}
				}
				break;
			case 309:
				enterOuterAlt(_localctx, 309);
				{
				{
				setState(778);
				match(T__347);
				}
				}
				break;
			case 310:
				enterOuterAlt(_localctx, 310);
				{
				{
				setState(779);
				match(T__348);
				}
				}
				break;
			case 311:
				enterOuterAlt(_localctx, 311);
				{
				{
				setState(780);
				match(T__349);
				}
				}
				break;
			case 312:
				enterOuterAlt(_localctx, 312);
				{
				{
				setState(781);
				match(T__350);
				}
				}
				break;
			case 313:
				enterOuterAlt(_localctx, 313);
				{
				{
				setState(782);
				match(T__351);
				}
				}
				break;
			case 314:
				enterOuterAlt(_localctx, 314);
				{
				{
				setState(783);
				match(T__352);
				}
				}
				break;
			case 315:
				enterOuterAlt(_localctx, 315);
				{
				{
				setState(784);
				match(T__353);
				}
				}
				break;
			case 316:
				enterOuterAlt(_localctx, 316);
				{
				{
				setState(785);
				match(T__354);
				}
				}
				break;
			case 317:
				enterOuterAlt(_localctx, 317);
				{
				{
				setState(786);
				match(T__355);
				}
				}
				break;
			case 318:
				enterOuterAlt(_localctx, 318);
				{
				{
				setState(787);
				match(T__356);
				}
				}
				break;
			case 319:
				enterOuterAlt(_localctx, 319);
				{
				{
				setState(788);
				match(T__357);
				}
				}
				break;
			case 320:
				enterOuterAlt(_localctx, 320);
				{
				{
				setState(789);
				match(T__358);
				}
				}
				break;
			case 321:
				enterOuterAlt(_localctx, 321);
				{
				{
				setState(790);
				match(T__359);
				}
				}
				break;
			case 322:
				enterOuterAlt(_localctx, 322);
				{
				{
				setState(791);
				match(T__360);
				}
				}
				break;
			case 323:
				enterOuterAlt(_localctx, 323);
				{
				{
				setState(792);
				match(T__361);
				}
				}
				break;
			case 324:
				enterOuterAlt(_localctx, 324);
				{
				{
				setState(793);
				match(T__362);
				}
				}
				break;
			case 325:
				enterOuterAlt(_localctx, 325);
				{
				{
				setState(794);
				match(T__363);
				}
				}
				break;
			case 326:
				enterOuterAlt(_localctx, 326);
				{
				{
				setState(795);
				match(T__364);
				}
				}
				break;
			case 327:
				enterOuterAlt(_localctx, 327);
				{
				{
				setState(796);
				match(T__365);
				}
				}
				break;
			case 328:
				enterOuterAlt(_localctx, 328);
				{
				{
				setState(797);
				match(T__366);
				}
				}
				break;
			case 329:
				enterOuterAlt(_localctx, 329);
				{
				{
				setState(798);
				match(T__367);
				}
				}
				break;
			case 330:
				enterOuterAlt(_localctx, 330);
				{
				{
				setState(799);
				match(T__368);
				}
				}
				break;
			case 331:
				enterOuterAlt(_localctx, 331);
				{
				{
				setState(800);
				match(T__369);
				}
				}
				break;
			case 332:
				enterOuterAlt(_localctx, 332);
				{
				{
				setState(801);
				match(T__370);
				}
				}
				break;
			case 333:
				enterOuterAlt(_localctx, 333);
				{
				{
				setState(802);
				match(T__371);
				}
				}
				break;
			case 334:
				enterOuterAlt(_localctx, 334);
				{
				{
				setState(803);
				match(T__372);
				}
				}
				break;
			case 335:
				enterOuterAlt(_localctx, 335);
				{
				{
				setState(804);
				match(T__373);
				}
				}
				break;
			case 336:
				enterOuterAlt(_localctx, 336);
				{
				{
				setState(805);
				match(T__374);
				}
				}
				break;
			case 337:
				enterOuterAlt(_localctx, 337);
				{
				{
				setState(806);
				match(T__375);
				}
				}
				break;
			case 338:
				enterOuterAlt(_localctx, 338);
				{
				{
				setState(807);
				match(T__376);
				}
				}
				break;
			case 339:
				enterOuterAlt(_localctx, 339);
				{
				{
				setState(808);
				match(T__377);
				}
				}
				break;
			case 340:
				enterOuterAlt(_localctx, 340);
				{
				{
				setState(809);
				match(T__378);
				}
				}
				break;
			case 341:
				enterOuterAlt(_localctx, 341);
				{
				{
				setState(810);
				match(T__379);
				}
				}
				break;
			case 342:
				enterOuterAlt(_localctx, 342);
				{
				{
				setState(811);
				match(T__380);
				}
				}
				break;
			case 343:
				enterOuterAlt(_localctx, 343);
				{
				{
				setState(812);
				match(T__381);
				}
				}
				break;
			case 344:
				enterOuterAlt(_localctx, 344);
				{
				{
				setState(813);
				match(T__382);
				}
				}
				break;
			case 345:
				enterOuterAlt(_localctx, 345);
				{
				{
				setState(814);
				match(T__383);
				}
				}
				break;
			case 346:
				enterOuterAlt(_localctx, 346);
				{
				{
				setState(815);
				match(T__384);
				}
				}
				break;
			case 347:
				enterOuterAlt(_localctx, 347);
				{
				{
				setState(816);
				match(T__385);
				}
				}
				break;
			case 348:
				enterOuterAlt(_localctx, 348);
				{
				{
				setState(817);
				match(T__386);
				}
				}
				break;
			case 349:
				enterOuterAlt(_localctx, 349);
				{
				{
				setState(818);
				match(T__387);
				}
				}
				break;
			case 350:
				enterOuterAlt(_localctx, 350);
				{
				{
				setState(819);
				match(T__388);
				}
				}
				break;
			case 351:
				enterOuterAlt(_localctx, 351);
				{
				{
				setState(820);
				match(T__389);
				}
				}
				break;
			case 352:
				enterOuterAlt(_localctx, 352);
				{
				{
				setState(821);
				match(T__390);
				}
				}
				break;
			case 353:
				enterOuterAlt(_localctx, 353);
				{
				{
				setState(822);
				match(T__391);
				}
				}
				break;
			case 354:
				enterOuterAlt(_localctx, 354);
				{
				{
				setState(823);
				match(T__392);
				}
				}
				break;
			case 355:
				enterOuterAlt(_localctx, 355);
				{
				{
				setState(824);
				match(T__156);
				}
				}
				break;
			case 356:
				enterOuterAlt(_localctx, 356);
				{
				{
				setState(825);
				match(T__393);
				}
				}
				break;
			case 357:
				enterOuterAlt(_localctx, 357);
				{
				{
				setState(826);
				match(T__394);
				}
				}
				break;
			case 358:
				enterOuterAlt(_localctx, 358);
				{
				{
				setState(827);
				match(T__395);
				}
				}
				break;
			case 359:
				enterOuterAlt(_localctx, 359);
				{
				{
				setState(828);
				match(T__396);
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

	public static class ConditionContext extends ParserRuleContext {
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
		enterRule(_localctx, 48, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(831);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(881);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__397:
				{
				{
				setState(832);
				match(T__397);
				}
				}
				break;
			case T__398:
				{
				{
				setState(833);
				match(T__398);
				}
				}
				break;
			case T__399:
				{
				{
				setState(834);
				match(T__399);
				}
				}
				break;
			case T__400:
				{
				{
				setState(835);
				match(T__400);
				}
				}
				break;
			case T__401:
				{
				{
				setState(836);
				match(T__401);
				}
				}
				break;
			case T__402:
				{
				{
				setState(837);
				match(T__402);
				}
				}
				break;
			case T__403:
				{
				{
				setState(838);
				match(T__403);
				}
				}
				break;
			case T__404:
				{
				{
				setState(839);
				match(T__404);
				}
				}
				break;
			case T__405:
				{
				{
				setState(840);
				match(T__405);
				}
				}
				break;
			case T__406:
				{
				{
				setState(841);
				match(T__406);
				}
				}
				break;
			case T__407:
				{
				{
				setState(842);
				match(T__407);
				}
				}
				break;
			case T__408:
				{
				{
				setState(843);
				match(T__408);
				}
				}
				break;
			case T__409:
				{
				{
				setState(844);
				match(T__409);
				}
				}
				break;
			case T__410:
				{
				{
				setState(845);
				match(T__410);
				}
				}
				break;
			case T__411:
				{
				{
				setState(846);
				match(T__411);
				}
				}
				break;
			case T__412:
				{
				{
				setState(847);
				match(T__412);
				}
				}
				break;
			case T__413:
				{
				{
				setState(848);
				match(T__413);
				}
				}
				break;
			case T__414:
				{
				{
				setState(849);
				match(T__414);
				}
				}
				break;
			case T__415:
				{
				{
				setState(850);
				match(T__415);
				}
				}
				break;
			case T__416:
				{
				{
				setState(851);
				match(T__416);
				}
				}
				break;
			case T__417:
				{
				{
				setState(852);
				match(T__417);
				}
				}
				break;
			case T__418:
				{
				{
				setState(853);
				match(T__418);
				}
				}
				break;
			case T__419:
				{
				{
				setState(854);
				match(T__419);
				}
				}
				break;
			case T__420:
				{
				{
				setState(855);
				match(T__420);
				}
				}
				break;
			case T__421:
				{
				{
				setState(856);
				match(T__421);
				}
				}
				break;
			case T__422:
				{
				{
				setState(857);
				match(T__422);
				}
				}
				break;
			case T__423:
				{
				{
				setState(858);
				match(T__423);
				}
				}
				break;
			case T__424:
				{
				{
				setState(859);
				match(T__424);
				}
				}
				break;
			case T__425:
				{
				{
				setState(860);
				match(T__425);
				}
				}
				break;
			case T__426:
				{
				{
				setState(861);
				match(T__426);
				}
				}
				break;
			case T__427:
				{
				{
				setState(862);
				match(T__427);
				}
				}
				break;
			case T__428:
				{
				{
				setState(863);
				match(T__428);
				}
				}
				break;
			case T__429:
				{
				{
				setState(864);
				match(T__429);
				}
				}
				break;
			case T__430:
				{
				{
				setState(865);
				match(T__430);
				}
				}
				break;
			case T__431:
				{
				{
				setState(866);
				match(T__431);
				}
				}
				break;
			case T__432:
				{
				{
				setState(867);
				match(T__432);
				}
				}
				break;
			case T__433:
				{
				{
				setState(868);
				match(T__433);
				}
				}
				break;
			case T__434:
				{
				{
				setState(869);
				match(T__434);
				}
				}
				break;
			case T__435:
				{
				{
				setState(870);
				match(T__435);
				}
				}
				break;
			case T__436:
				{
				{
				setState(871);
				match(T__436);
				}
				}
				break;
			case T__437:
				{
				{
				setState(872);
				match(T__437);
				}
				}
				break;
			case T__438:
				{
				{
				setState(873);
				match(T__438);
				}
				}
				break;
			case T__439:
				{
				{
				setState(874);
				match(T__439);
				}
				}
				break;
			case T__440:
				{
				{
				setState(875);
				match(T__440);
				}
				}
				break;
			case T__441:
				{
				{
				setState(876);
				match(T__441);
				}
				}
				break;
			case T__442:
				{
				{
				setState(877);
				match(T__442);
				}
				}
				break;
			case T__443:
				{
				{
				setState(878);
				match(T__443);
				}
				}
				break;
			case T__444:
				{
				{
				setState(879);
				match(T__444);
				}
				}
				break;
			case T__445:
				{
				{
				setState(880);
				match(T__445);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		enterRule(_localctx, 50, RULE_effect);
		int _la;
		try {
			setState(900);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__446:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(883);
				match(T__446);
				setState(886);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(884);
					match(T__2);
					setState(885);
					match(T__447);
					}
				}

				}
				}
				break;
			case T__447:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(888);
				match(T__447);
				setState(891);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(889);
					match(T__2);
					setState(890);
					match(T__446);
					}
				}

				}
				}
				break;
			case T__448:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(893);
				match(T__448);
				}
				}
				break;
			case T__449:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(894);
				match(T__449);
				}
				}
				break;
			case T__450:
				enterOuterAlt(_localctx, 5);
				{
				{
				setState(895);
				match(T__450);
				}
				}
				break;
			case T__451:
				enterOuterAlt(_localctx, 6);
				{
				{
				setState(896);
				match(T__451);
				}
				}
				break;
			case T__452:
				enterOuterAlt(_localctx, 7);
				{
				{
				setState(897);
				match(T__452);
				}
				}
				break;
			case T__453:
				enterOuterAlt(_localctx, 8);
				{
				{
				setState(898);
				match(T__453);
				}
				}
				break;
			case T__454:
				enterOuterAlt(_localctx, 9);
				{
				{
				setState(899);
				match(T__454);
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

	public static class DstContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterDst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitDst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitDst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DstContext dst() throws RecognitionException {
		DstContext _localctx = new DstContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_dst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(903);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6 || _la==T__455) {
				{
				setState(902);
				_la = _input.LA(1);
				if ( !(_la==T__6 || _la==T__455) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(906);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__456) {
				{
				setState(905);
				match(T__456);
				}
			}

			setState(908);
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

	public static class SrcContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SrcContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_src; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterSrc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitSrc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitSrc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcContext src() throws RecognitionException {
		SrcContext _localctx = new SrcContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_src);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(911);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6 || _la==T__455) {
				{
				setState(910);
				_la = _input.LA(1);
				if ( !(_la==T__6 || _la==T__455) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(914);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__456) {
				{
				setState(913);
				match(T__456);
				}
			}

			setState(916);
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

	public static class IndexContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).enterIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Spin2Listener ) ((Spin2Listener)listener).exitIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Spin2Visitor ) return ((Spin2Visitor<? extends T>)visitor).visitIndex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexContext index() throws RecognitionException {
		IndexContext _localctx = new IndexContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(918);
			match(T__6);
			setState(919);
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

	public static class TypeContext extends ParserRuleContext {
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
		enterRule(_localctx, 58, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(921);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
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
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(939);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__90:
			case T__91:
			case T__101:
			case T__102:
			case T__128:
			case T__245:
			case T__246:
			case T__457:
			case T__458:
			case T__459:
			case T__460:
			case T__461:
			case T__462:
			case T__463:
			case T__464:
			case T__465:
			case T__466:
			case T__467:
			case T__468:
			case T__469:
			case T__470:
				{
				setState(924);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (T__90 - 91)) | (1L << (T__91 - 91)) | (1L << (T__101 - 91)) | (1L << (T__102 - 91)) | (1L << (T__128 - 91)))) != 0) || _la==T__245 || _la==T__246 || ((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__459 - 458)) | (1L << (T__460 - 458)) | (1L << (T__461 - 458)) | (1L << (T__462 - 458)) | (1L << (T__463 - 458)) | (1L << (T__464 - 458)) | (1L << (T__465 - 458)) | (1L << (T__466 - 458)) | (1L << (T__467 - 458)) | (1L << (T__468 - 458)) | (1L << (T__469 - 458)) | (1L << (T__470 - 458)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(925);
				((ExpressionContext)_localctx).right = expression(12);
				}
				break;
			case T__503:
			case T__504:
			case T__505:
			case T__506:
			case T__507:
			case T__508:
				{
				setState(926);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 504)) & ~0x3f) == 0 && ((1L << (_la - 504)) & ((1L << (T__503 - 504)) | (1L << (T__504 - 504)) | (1L << (T__505 - 504)) | (1L << (T__506 - 504)) | (1L << (T__507 - 504)) | (1L << (T__508 - 504)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(927);
				match(T__509);
				setState(928);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(929);
				match(T__510);
				}
				break;
			case T__509:
				{
				setState(931);
				match(T__509);
				setState(932);
				expression(0);
				setState(933);
				match(T__510);
				}
				break;
			case T__511:
			case T__512:
			case VARIABLE:
			case STRING:
			case QUAD:
			case BIN:
			case HEX:
			case NUMBER:
				{
				setState(936);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__511) {
					{
					setState(935);
					match(T__511);
					}
				}

				setState(938);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(970);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(968);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(941);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(942);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__38))) != 0) || _la==T__99 || _la==T__100 || _la==T__381 || _la==T__471 || _la==T__472) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(943);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(944);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(945);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 474)) & ~0x3f) == 0 && ((1L << (_la - 474)) & ((1L << (T__473 - 474)) | (1L << (T__474 - 474)) | (1L << (T__475 - 474)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(946);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(947);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(948);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__138 || _la==T__139 || ((((_la - 477)) & ~0x3f) == 0 && ((1L << (_la - 477)) & ((1L << (T__476 - 477)) | (1L << (T__477 - 477)) | (1L << (T__478 - 477)) | (1L << (T__479 - 477)) | (1L << (T__480 - 477)) | (1L << (T__481 - 477)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(949);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(950);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(951);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 458)) & ~0x3f) == 0 && ((1L << (_la - 458)) & ((1L << (T__457 - 458)) | (1L << (T__458 - 458)) | (1L << (T__482 - 458)) | (1L << (T__483 - 458)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(952);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(953);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(954);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 485)) & ~0x3f) == 0 && ((1L << (_la - 485)) & ((1L << (T__484 - 485)) | (1L << (T__485 - 485)) | (1L << (T__486 - 485)) | (1L << (T__487 - 485)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(955);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(956);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(957);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 489)) & ~0x3f) == 0 && ((1L << (_la - 489)) & ((1L << (T__488 - 489)) | (1L << (T__489 - 489)) | (1L << (T__490 - 489)) | (1L << (T__491 - 489)) | (1L << (T__492 - 489)) | (1L << (T__493 - 489)) | (1L << (T__494 - 489)) | (1L << (T__495 - 489)) | (1L << (T__496 - 489)) | (1L << (T__497 - 489)) | (1L << (T__498 - 489)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(958);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(959);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(960);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 79)) & ~0x3f) == 0 && ((1L << (_la - 79)) & ((1L << (T__78 - 79)) | (1L << (T__80 - 79)) | (1L << (T__81 - 79)))) != 0) || ((((_la - 500)) & ~0x3f) == 0 && ((1L << (_la - 500)) & ((1L << (T__499 - 500)) | (1L << (T__500 - 500)) | (1L << (T__501 - 500)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(961);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(962);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(963);
						((ExpressionContext)_localctx).operator = match(T__502);
						setState(964);
						expression(0);
						setState(965);
						((ExpressionContext)_localctx).operator = match(T__9);
						setState(966);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(972);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
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
		enterRule(_localctx, 62, RULE_atom);
		int _la;
		try {
			setState(983);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(973);
				match(NUMBER);
				}
				break;
			case HEX:
				enterOuterAlt(_localctx, 2);
				{
				setState(974);
				match(HEX);
				}
				break;
			case BIN:
				enterOuterAlt(_localctx, 3);
				{
				setState(975);
				match(BIN);
				}
				break;
			case QUAD:
				enterOuterAlt(_localctx, 4);
				{
				setState(976);
				match(QUAD);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 5);
				{
				setState(977);
				match(STRING);
				}
				break;
			case T__512:
				enterOuterAlt(_localctx, 6);
				{
				setState(978);
				match(T__512);
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 7);
				{
				setState(979);
				match(VARIABLE);
				setState(981);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
				case 1:
					{
					setState(980);
					_la = _input.LA(1);
					if ( !(_la==T__513 || _la==T__514) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 22:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 24:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 30:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u020f\u03dc\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\7\2D\n\2\f\2\16\2G\13\2\3\2\3\2\3\2\3\2\7\2M\n\2\f\2\16\2P\13"+
		"\2\3\2\3\2\3\3\6\3U\n\3\r\3\16\3V\3\3\7\3Z\n\3\f\3\16\3]\13\3\3\3\7\3"+
		"`\n\3\f\3\16\3c\13\3\3\4\5\4f\n\4\3\4\3\4\3\4\3\4\7\4l\n\4\f\4\16\4o\13"+
		"\4\3\4\5\4r\n\4\3\4\3\4\3\4\3\4\3\4\5\4y\n\4\3\4\7\4|\n\4\f\4\16\4\177"+
		"\13\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0087\n\4\3\4\7\4\u008a\n\4\f\4\16\4"+
		"\u008d\13\4\5\4\u008f\n\4\3\5\6\5\u0092\n\5\r\5\16\5\u0093\3\5\7\5\u0097"+
		"\n\5\f\5\16\5\u009a\13\5\3\5\7\5\u009d\n\5\f\5\16\5\u00a0\13\5\3\6\3\6"+
		"\3\6\3\6\3\6\7\6\u00a7\n\6\f\6\16\6\u00aa\13\6\3\6\3\6\3\6\6\6\u00af\n"+
		"\6\r\6\16\6\u00b0\3\7\3\7\3\b\3\b\3\t\6\t\u00b8\n\t\r\t\16\t\u00b9\3\t"+
		"\7\t\u00bd\n\t\f\t\16\t\u00c0\13\t\3\t\7\t\u00c3\n\t\f\t\16\t\u00c6\13"+
		"\t\3\n\5\n\u00c9\n\n\3\n\3\n\3\n\3\n\3\n\5\n\u00d0\n\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\5\n\u00d8\n\n\7\n\u00da\n\n\f\n\16\n\u00dd\13\n\3\n\6\n\u00e0"+
		"\n\n\r\n\16\n\u00e1\3\13\6\13\u00e5\n\13\r\13\16\13\u00e6\3\13\7\13\u00ea"+
		"\n\13\f\13\16\13\u00ed\13\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\7\13\u00f8\n\13\f\13\16\13\u00fb\13\13\3\f\3\f\6\f\u00ff\n\f\r\f\16"+
		"\f\u0100\3\f\5\f\u0104\n\f\3\f\5\f\u0107\n\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\5\f\u0110\n\f\3\f\6\f\u0113\n\f\r\f\16\f\u0114\3\f\5\f\u0118\n\f\3"+
		"\f\5\f\u011b\n\f\3\f\3\f\3\f\3\f\3\f\5\f\u0122\n\f\3\f\6\f\u0125\n\f\r"+
		"\f\16\f\u0126\3\f\5\f\u012a\n\f\3\f\5\f\u012d\n\f\3\f\3\f\3\f\5\f\u0132"+
		"\n\f\3\f\6\f\u0135\n\f\r\f\16\f\u0136\3\f\5\f\u013a\n\f\3\f\5\f\u013d"+
		"\n\f\3\f\3\f\5\f\u0141\n\f\3\f\6\f\u0144\n\f\r\f\16\f\u0145\5\f\u0148"+
		"\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u014f\n\r\3\16\3\16\3\16\3\16\5\16\u0155"+
		"\n\16\5\16\u0157\n\16\3\16\6\16\u015a\n\16\r\16\16\16\u015b\3\17\3\17"+
		"\3\17\3\17\5\17\u0162\n\17\5\17\u0164\n\17\3\17\6\17\u0167\n\17\r\17\16"+
		"\17\u0168\3\20\3\20\5\20\u016d\n\20\3\20\6\20\u0170\n\20\r\20\16\20\u0171"+
		"\3\21\3\21\5\21\u0176\n\21\3\21\6\21\u0179\n\21\r\21\16\21\u017a\3\22"+
		"\5\22\u017e\n\22\3\22\3\22\3\22\5\22\u0183\n\22\3\22\3\22\3\22\5\22\u0188"+
		"\n\22\7\22\u018a\n\22\f\22\16\22\u018d\13\22\3\22\6\22\u0190\n\22\r\22"+
		"\16\22\u0191\3\23\5\23\u0195\n\23\3\23\3\23\3\23\5\23\u019a\n\23\3\23"+
		"\3\23\3\23\5\23\u019f\n\23\7\23\u01a1\n\23\f\23\16\23\u01a4\13\23\3\23"+
		"\6\23\u01a7\n\23\r\23\16\23\u01a8\3\24\5\24\u01ac\n\24\3\24\3\24\3\24"+
		"\5\24\u01b1\n\24\3\24\3\24\3\24\5\24\u01b6\n\24\7\24\u01b8\n\24\f\24\16"+
		"\24\u01bb\13\24\3\24\6\24\u01be\n\24\r\24\16\24\u01bf\3\25\3\25\3\26\3"+
		"\26\3\26\3\26\3\26\3\27\5\27\u01ca\n\27\3\27\3\27\3\27\6\27\u01cf\n\27"+
		"\r\27\16\27\u01d0\3\30\3\30\5\30\u01d5\n\30\3\30\3\30\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\31\3\31\3\31\5\31\u0340\n\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\5\32\u0374\n\32\3\33\3\33\3\33\5\33\u0379\n\33\3\33\3"+
		"\33\3\33\5\33\u037e\n\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u0387"+
		"\n\33\3\34\5\34\u038a\n\34\3\34\5\34\u038d\n\34\3\34\3\34\3\35\5\35\u0392"+
		"\n\35\3\35\5\35\u0395\n\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u03ab\n \3 \5 \u03ae\n \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \7 \u03cb\n \f \16 \u03ce\13 \3!\3!\3!\3!\3!\3!\3!\3!\5!\u03d8\n!\5!"+
		"\u03da\n!\3!\2\3>\"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@\2\"\3\2\3\4\3\2\n\13\3\2\r\16\3\2\17\20\3\2\21\22\3\2\23"+
		"\24\3\2\25\26\3\2\27\30\3\2\31\32\3\2\33\34\3\2\35\36\3\2\37 \3\2+,\3"+
		"\2-.\3\2/\60\3\2\61\62\3\2YZ\3\2[\\\3\2]^\3\2\u013c\u013d\4\2\t\t\u01ca"+
		"\u01ca\3\2\31\36\7\2]^hi\u0083\u0083\u00f8\u00f9\u01cc\u01d9\3\2\u01fa"+
		"\u01ff\7\2#$))fg\u0180\u0180\u01da\u01db\3\2\u01dc\u01de\4\2\u008d\u008e"+
		"\u01df\u01e4\4\2\u01cc\u01cd\u01e5\u01e6\3\2\u01e7\u01ea\3\2\u01eb\u01f5"+
		"\5\2QQST\u01f6\u01f8\3\2\u0204\u0205\2\u05cd\2E\3\2\2\2\4T\3\2\2\2\6\u008e"+
		"\3\2\2\2\b\u0091\3\2\2\2\n\u00a1\3\2\2\2\f\u00b2\3\2\2\2\16\u00b4\3\2"+
		"\2\2\20\u00b7\3\2\2\2\22\u00c8\3\2\2\2\24\u00e4\3\2\2\2\26\u0147\3\2\2"+
		"\2\30\u0149\3\2\2\2\32\u0150\3\2\2\2\34\u015d\3\2\2\2\36\u016a\3\2\2\2"+
		" \u0173\3\2\2\2\"\u017d\3\2\2\2$\u0194\3\2\2\2&\u01ab\3\2\2\2(\u01c1\3"+
		"\2\2\2*\u01c3\3\2\2\2,\u01c9\3\2\2\2.\u01d2\3\2\2\2\60\u033f\3\2\2\2\62"+
		"\u0341\3\2\2\2\64\u0386\3\2\2\2\66\u0389\3\2\2\28\u0391\3\2\2\2:\u0398"+
		"\3\2\2\2<\u039b\3\2\2\2>\u03ad\3\2\2\2@\u03d9\3\2\2\2BD\7\u020e\2\2CB"+
		"\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FN\3\2\2\2GE\3\2\2\2HM\5\4\3\2I"+
		"M\5\b\5\2JM\5\20\t\2KM\5\24\13\2LH\3\2\2\2LI\3\2\2\2LJ\3\2\2\2LK\3\2\2"+
		"\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2PN\3\2\2\2QR\7\2\2\3R\3\3\2"+
		"\2\2SU\t\2\2\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2W[\3\2\2\2XZ\7\u020e"+
		"\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\a\3\2\2\2][\3\2\2\2^`\5"+
		"\6\4\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2b\5\3\2\2\2ca\3\2\2\2df"+
		"\7\5\2\2ed\3\2\2\2ef\3\2\2\2fg\3\2\2\2gh\7\u0208\2\2hi\7\6\2\2im\5> \2"+
		"jl\7\u020e\2\2kj\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\u008f\3\2\2\2"+
		"om\3\2\2\2pr\7\5\2\2qp\3\2\2\2qr\3\2\2\2rs\3\2\2\2sx\7\u0208\2\2tu\7\7"+
		"\2\2uv\5> \2vw\7\b\2\2wy\3\2\2\2xt\3\2\2\2xy\3\2\2\2y}\3\2\2\2z|\7\u020e"+
		"\2\2{z\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\u008f\3\2\2\2\177}\3"+
		"\2\2\2\u0080\u0081\7\t\2\2\u0081\u0086\5> \2\u0082\u0083\7\7\2\2\u0083"+
		"\u0084\5> \2\u0084\u0085\7\b\2\2\u0085\u0087\3\2\2\2\u0086\u0082\3\2\2"+
		"\2\u0086\u0087\3\2\2\2\u0087\u008b\3\2\2\2\u0088\u008a\7\u020e\2\2\u0089"+
		"\u0088\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c\3\2"+
		"\2\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008ee\3\2\2\2\u008eq\3\2"+
		"\2\2\u008e\u0080\3\2\2\2\u008f\7\3\2\2\2\u0090\u0092\t\3\2\2\u0091\u0090"+
		"\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094"+
		"\u0098\3\2\2\2\u0095\u0097\7\u020e\2\2\u0096\u0095\3\2\2\2\u0097\u009a"+
		"\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009e\3\2\2\2\u009a"+
		"\u0098\3\2\2\2\u009b\u009d\5\n\6\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2"+
		"\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\t\3\2\2\2\u00a0\u009e"+
		"\3\2\2\2\u00a1\u00a8\5\f\7\2\u00a2\u00a3\7\7\2\2\u00a3\u00a4\5> \2\u00a4"+
		"\u00a5\7\b\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a2\3\2\2\2\u00a7\u00aa\3\2"+
		"\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00ab\3\2\2\2\u00aa"+
		"\u00a8\3\2\2\2\u00ab\u00ac\7\f\2\2\u00ac\u00ae\5\16\b\2\u00ad\u00af\7"+
		"\u020e\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00ae\3\2\2\2"+
		"\u00b0\u00b1\3\2\2\2\u00b1\13\3\2\2\2\u00b2\u00b3\7\u0208\2\2\u00b3\r"+
		"\3\2\2\2\u00b4\u00b5\7\u0209\2\2\u00b5\17\3\2\2\2\u00b6\u00b8\t\4\2\2"+
		"\u00b7\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba"+
		"\3\2\2\2\u00ba\u00be\3\2\2\2\u00bb\u00bd\7\u020e\2\2\u00bc\u00bb\3\2\2"+
		"\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c4"+
		"\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c3\5\22\n\2\u00c2\u00c1\3\2\2\2"+
		"\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\21"+
		"\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c9\5<\37\2\u00c8\u00c7\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cf\7\u0208\2\2\u00cb\u00cc"+
		"\7\7\2\2\u00cc\u00cd\5> \2\u00cd\u00ce\7\b\2\2\u00ce\u00d0\3\2\2\2\u00cf"+
		"\u00cb\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00db\3\2\2\2\u00d1\u00d2\7\5"+
		"\2\2\u00d2\u00d7\7\u0208\2\2\u00d3\u00d4\7\7\2\2\u00d4\u00d5\5> \2\u00d5"+
		"\u00d6\7\b\2\2\u00d6\u00d8\3\2\2\2\u00d7\u00d3\3\2\2\2\u00d7\u00d8\3\2"+
		"\2\2\u00d8\u00da\3\2\2\2\u00d9\u00d1\3\2\2\2\u00da\u00dd\3\2\2\2\u00db"+
		"\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00df\3\2\2\2\u00dd\u00db\3\2"+
		"\2\2\u00de\u00e0\7\u020e\2\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\23\3\2\2\2\u00e3\u00e5\t\5\2"+
		"\2\u00e4\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00eb\3\2\2\2\u00e8\u00ea\7\u020e\2\2\u00e9\u00e8\3\2\2"+
		"\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00f9"+
		"\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00f8\5\32\16\2\u00ef\u00f8\5\34\17"+
		"\2\u00f0\u00f8\5\36\20\2\u00f1\u00f8\5,\27\2\u00f2\u00f8\5\26\f\2\u00f3"+
		"\u00f8\5\"\22\2\u00f4\u00f8\5&\24\2\u00f5\u00f8\5$\23\2\u00f6\u00f8\5"+
		" \21\2\u00f7\u00ee\3\2\2\2\u00f7\u00ef\3\2\2\2\u00f7\u00f0\3\2\2\2\u00f7"+
		"\u00f1\3\2\2\2\u00f7\u00f2\3\2\2\2\u00f7\u00f3\3\2\2\2\u00f7\u00f4\3\2"+
		"\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f6\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9"+
		"\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\25\3\2\2\2\u00fb\u00f9\3\2\2"+
		"\2\u00fc\u00fe\5.\30\2\u00fd\u00ff\7\u020e\2\2\u00fe\u00fd\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0148\3\2"+
		"\2\2\u0102\u0104\5.\30\2\u0103\u0102\3\2\2\2\u0103\u0104\3\2\2\2\u0104"+
		"\u0106\3\2\2\2\u0105\u0107\5\62\32\2\u0106\u0105\3\2\2\2\u0106\u0107\3"+
		"\2\2\2\u0107\u0108\3\2\2\2\u0108\u0109\5\60\31\2\u0109\u010a\5\66\34\2"+
		"\u010a\u010b\7\5\2\2\u010b\u010c\58\35\2\u010c\u010d\7\5\2\2\u010d\u010f"+
		"\5:\36\2\u010e\u0110\5\64\33\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2"+
		"\u0110\u0112\3\2\2\2\u0111\u0113\7\u020e\2\2\u0112\u0111\3\2\2\2\u0113"+
		"\u0114\3\2\2\2\u0114\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0148\3\2"+
		"\2\2\u0116\u0118\5.\30\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118"+
		"\u011a\3\2\2\2\u0119\u011b\5\62\32\2\u011a\u0119\3\2\2\2\u011a\u011b\3"+
		"\2\2\2\u011b\u011c\3\2\2\2\u011c\u011d\5\60\31\2\u011d\u011e\5\66\34\2"+
		"\u011e\u011f\7\5\2\2\u011f\u0121\58\35\2\u0120\u0122\5\64\33\2\u0121\u0120"+
		"\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0124\3\2\2\2\u0123\u0125\7\u020e\2"+
		"\2\u0124\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127"+
		"\3\2\2\2\u0127\u0148\3\2\2\2\u0128\u012a\5.\30\2\u0129\u0128\3\2\2\2\u0129"+
		"\u012a\3\2\2\2\u012a\u012c\3\2\2\2\u012b\u012d\5\62\32\2\u012c\u012b\3"+
		"\2\2\2\u012c\u012d\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012f\5\60\31\2\u012f"+
		"\u0131\5\66\34\2\u0130\u0132\5\64\33\2\u0131\u0130\3\2\2\2\u0131\u0132"+
		"\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0135\7\u020e\2\2\u0134\u0133\3\2\2"+
		"\2\u0135\u0136\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0148"+
		"\3\2\2\2\u0138\u013a\5.\30\2\u0139\u0138\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013c\3\2\2\2\u013b\u013d\5\62\32\2\u013c\u013b\3\2\2\2\u013c\u013d\3"+
		"\2\2\2\u013d\u013e\3\2\2\2\u013e\u0140\5\60\31\2\u013f\u0141\5\64\33\2"+
		"\u0140\u013f\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0143\3\2\2\2\u0142\u0144"+
		"\7\u020e\2\2\u0143\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0143\3\2\2"+
		"\2\u0145\u0146\3\2\2\2\u0146\u0148\3\2\2\2\u0147\u00fc\3\2\2\2\u0147\u0103"+
		"\3\2\2\2\u0147\u0117\3\2\2\2\u0147\u0129\3\2\2\2\u0147\u0139\3\2\2\2\u0148"+
		"\27\3\2\2\2\u0149\u014e\5> \2\u014a\u014b\7\7\2\2\u014b\u014c\5> \2\u014c"+
		"\u014d\7\b\2\2\u014d\u014f\3\2\2\2\u014e\u014a\3\2\2\2\u014e\u014f\3\2"+
		"\2\2\u014f\31\3\2\2\2\u0150\u0156\t\6\2\2\u0151\u0154\5> \2\u0152\u0153"+
		"\7\5\2\2\u0153\u0155\5> \2\u0154\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155"+
		"\u0157\3\2\2\2\u0156\u0151\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0159\3\2"+
		"\2\2\u0158\u015a\7\u020e\2\2\u0159\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\33\3\2\2\2\u015d\u0163\t\7\2"+
		"\2\u015e\u0161\5> \2\u015f\u0160\7\5\2\2\u0160\u0162\5> \2\u0161\u015f"+
		"\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0164\3\2\2\2\u0163\u015e\3\2\2\2\u0163"+
		"\u0164\3\2\2\2\u0164\u0166\3\2\2\2\u0165\u0167\7\u020e\2\2\u0166\u0165"+
		"\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169"+
		"\35\3\2\2\2\u016a\u016c\t\b\2\2\u016b\u016d\5> \2\u016c\u016b\3\2\2\2"+
		"\u016c\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e\u0170\7\u020e\2\2\u016f"+
		"\u016e\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2"+
		"\2\2\u0172\37\3\2\2\2\u0173\u0175\t\t\2\2\u0174\u0176\5> \2\u0175\u0174"+
		"\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0178\3\2\2\2\u0177\u0179\7\u020e\2"+
		"\2\u0178\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b"+
		"\3\2\2\2\u017b!\3\2\2\2\u017c\u017e\5.\30\2\u017d\u017c\3\2\2\2\u017d"+
		"\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0182\t\n\2\2\u0180\u0183\5("+
		"\25\2\u0181\u0183\5*\26\2\u0182\u0180\3\2\2\2\u0182\u0181\3\2\2\2\u0183"+
		"\u018b\3\2\2\2\u0184\u0187\7\5\2\2\u0185\u0188\5(\25\2\u0186\u0188\5*"+
		"\26\2\u0187\u0185\3\2\2\2\u0187\u0186\3\2\2\2\u0188\u018a\3\2\2\2\u0189"+
		"\u0184\3\2\2\2\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018b\u018c\3\2"+
		"\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018e\u0190\7\u020e\2\2\u018f"+
		"\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u018f\3\2\2\2\u0191\u0192\3\2"+
		"\2\2\u0192#\3\2\2\2\u0193\u0195\5.\30\2\u0194\u0193\3\2\2\2\u0194\u0195"+
		"\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0199\t\13\2\2\u0197\u019a\5(\25\2"+
		"\u0198\u019a\5*\26\2\u0199\u0197\3\2\2\2\u0199\u0198\3\2\2\2\u019a\u01a2"+
		"\3\2\2\2\u019b\u019e\7\5\2\2\u019c\u019f\5(\25\2\u019d\u019f\5*\26\2\u019e"+
		"\u019c\3\2\2\2\u019e\u019d\3\2\2\2\u019f\u01a1\3\2\2\2\u01a0\u019b\3\2"+
		"\2\2\u01a1\u01a4\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3"+
		"\u01a6\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a5\u01a7\7\u020e\2\2\u01a6\u01a5"+
		"\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a6\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9"+
		"%\3\2\2\2\u01aa\u01ac\5.\30\2\u01ab\u01aa\3\2\2\2\u01ab\u01ac\3\2\2\2"+
		"\u01ac\u01ad\3\2\2\2\u01ad\u01b0\t\f\2\2\u01ae\u01b1\5(\25\2\u01af\u01b1"+
		"\5*\26\2\u01b0\u01ae\3\2\2\2\u01b0\u01af\3\2\2\2\u01b1\u01b9\3\2\2\2\u01b2"+
		"\u01b5\7\5\2\2\u01b3\u01b6\5(\25\2\u01b4\u01b6\5*\26\2\u01b5\u01b3\3\2"+
		"\2\2\u01b5\u01b4\3\2\2\2\u01b6\u01b8\3\2\2\2\u01b7\u01b2\3\2\2\2\u01b8"+
		"\u01bb\3\2\2\2\u01b9\u01b7\3\2\2\2\u01b9\u01ba\3\2\2\2\u01ba\u01bd\3\2"+
		"\2\2\u01bb\u01b9\3\2\2\2\u01bc\u01be\7\u020e\2\2\u01bd\u01bc\3\2\2\2\u01be"+
		"\u01bf\3\2\2\2\u01bf\u01bd\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0\'\3\2\2\2"+
		"\u01c1\u01c2\5> \2\u01c2)\3\2\2\2\u01c3\u01c4\5> \2\u01c4\u01c5\7\7\2"+
		"\2\u01c5\u01c6\5> \2\u01c6\u01c7\7\b\2\2\u01c7+\3\2\2\2\u01c8\u01ca\5"+
		".\30\2\u01c9\u01c8\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb"+
		"\u01cc\t\r\2\2\u01cc\u01ce\5> \2\u01cd\u01cf\7\u020e\2\2\u01ce\u01cd\3"+
		"\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01ce\3\2\2\2\u01d0\u01d1\3\2\2\2\u01d1"+
		"-\3\2\2\2\u01d2\u01d4\6\30\2\2\u01d3\u01d5\7!\2\2\u01d4\u01d3\3\2\2\2"+
		"\u01d4\u01d5\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d7\7\u0208\2\2\u01d7"+
		"/\3\2\2\2\u01d8\u0340\7\"\2\2\u01d9\u0340\7#\2\2\u01da\u0340\7$\2\2\u01db"+
		"\u0340\7%\2\2\u01dc\u0340\7&\2\2\u01dd\u0340\7\'\2\2\u01de\u0340\7(\2"+
		"\2\u01df\u0340\7)\2\2\u01e0\u0340\7*\2\2\u01e1\u0340\t\16\2\2\u01e2\u0340"+
		"\t\17\2\2\u01e3\u0340\t\20\2\2\u01e4\u0340\t\21\2\2\u01e5\u0340\7\63\2"+
		"\2\u01e6\u0340\7\64\2\2\u01e7\u0340\7\65\2\2\u01e8\u0340\7\66\2\2\u01e9"+
		"\u0340\7\67\2\2\u01ea\u0340\78\2\2\u01eb\u0340\79\2\2\u01ec\u0340\7:\2"+
		"\2\u01ed\u0340\7;\2\2\u01ee\u0340\7<\2\2\u01ef\u0340\7=\2\2\u01f0\u0340"+
		"\7>\2\2\u01f1\u0340\7?\2\2\u01f2\u0340\7@\2\2\u01f3\u0340\7A\2\2\u01f4"+
		"\u0340\7B\2\2\u01f5\u0340\7C\2\2\u01f6\u0340\7D\2\2\u01f7\u0340\7E\2\2"+
		"\u01f8\u0340\7F\2\2\u01f9\u0340\7G\2\2\u01fa\u0340\7H\2\2\u01fb\u0340"+
		"\7I\2\2\u01fc\u0340\7J\2\2\u01fd\u0340\7K\2\2\u01fe\u0340\7L\2\2\u01ff"+
		"\u0340\7M\2\2\u0200\u0340\7N\2\2\u0201\u0340\7O\2\2\u0202\u0340\7P\2\2"+
		"\u0203\u0340\7Q\2\2\u0204\u0340\7R\2\2\u0205\u0340\7S\2\2\u0206\u0340"+
		"\7T\2\2\u0207\u0340\7U\2\2\u0208\u0340\7V\2\2\u0209\u0340\7W\2\2\u020a"+
		"\u0340\7X\2\2\u020b\u0340\t\22\2\2\u020c\u0340\t\23\2\2\u020d\u0340\t"+
		"\24\2\2\u020e\u0340\7_\2\2\u020f\u0340\7`\2\2\u0210\u0340\7a\2\2\u0211"+
		"\u0340\7b\2\2\u0212\u0340\7c\2\2\u0213\u0340\7d\2\2\u0214\u0340\7e\2\2"+
		"\u0215\u0340\7f\2\2\u0216\u0340\7g\2\2\u0217\u0340\7h\2\2\u0218\u0340"+
		"\7i\2\2\u0219\u0340\7j\2\2\u021a\u0340\7k\2\2\u021b\u0340\7l\2\2\u021c"+
		"\u0340\7m\2\2\u021d\u0340\7n\2\2\u021e\u0340\7o\2\2\u021f\u0340\7p\2\2"+
		"\u0220\u0340\7q\2\2\u0221\u0340\7r\2\2\u0222\u0340\7s\2\2\u0223\u0340"+
		"\7t\2\2\u0224\u0340\7u\2\2\u0225\u0340\7v\2\2\u0226\u0340\7w\2\2\u0227"+
		"\u0340\7x\2\2\u0228\u0340\7y\2\2\u0229\u0340\7z\2\2\u022a\u0340\7{\2\2"+
		"\u022b\u0340\7|\2\2\u022c\u0340\7}\2\2\u022d\u0340\7~\2\2\u022e\u0340"+
		"\7\177\2\2\u022f\u0340\7\u0080\2\2\u0230\u0340\7\u0081\2\2\u0231\u0340"+
		"\7\u0082\2\2\u0232\u0340\7\u0083\2\2\u0233\u0340\7\u0084\2\2\u0234\u0340"+
		"\7\u0085\2\2\u0235\u0340\7\u0086\2\2\u0236\u0340\7\u0087\2\2\u0237\u0340"+
		"\7\u0088\2\2\u0238\u0340\7\u0089\2\2\u0239\u0340\7\u008a\2\2\u023a\u0340"+
		"\7\u008b\2\2\u023b\u0340\7\u008c\2\2\u023c\u0340\7\u008d\2\2\u023d\u0340"+
		"\7\u008e\2\2\u023e\u0340\7\u008f\2\2\u023f\u0340\7\u0090\2\2\u0240\u0340"+
		"\7\u0091\2\2\u0241\u0340\7\u0092\2\2\u0242\u0340\7\u0093\2\2\u0243\u0340"+
		"\7\u0094\2\2\u0244\u0340\7\u0095\2\2\u0245\u0340\7\u0096\2\2\u0246\u0340"+
		"\7\u0097\2\2\u0247\u0340\7\u0098\2\2\u0248\u0340\7\u0099\2\2\u0249\u0340"+
		"\7\u009a\2\2\u024a\u0340\7\u009b\2\2\u024b\u0340\7\u009c\2\2\u024c\u0340"+
		"\7\u009d\2\2\u024d\u0340\7\u009e\2\2\u024e\u0340\7\u009f\2\2\u024f\u0340"+
		"\7\u00a0\2\2\u0250\u0340\7\u00a1\2\2\u0251\u0340\7\u00a2\2\2\u0252\u0340"+
		"\7\u00a3\2\2\u0253\u0340\7\u00a4\2\2\u0254\u0340\7\u00a5\2\2\u0255\u0340"+
		"\7\u00a6\2\2\u0256\u0340\7\u00a7\2\2\u0257\u0340\7\u00a8\2\2\u0258\u0340"+
		"\7\u00a9\2\2\u0259\u0340\7\u00aa\2\2\u025a\u0340\7\u00ab\2\2\u025b\u0340"+
		"\7\u00ac\2\2\u025c\u0340\7\u00ad\2\2\u025d\u0340\7\u00ae\2\2\u025e\u0340"+
		"\7\u00af\2\2\u025f\u0340\7\u00b0\2\2\u0260\u0340\7\u00b1\2\2\u0261\u0340"+
		"\7\u00b2\2\2\u0262\u0340\7\u00b3\2\2\u0263\u0340\7\u00b4\2\2\u0264\u0340"+
		"\7\u00b5\2\2\u0265\u0340\7\u00b6\2\2\u0266\u0340\7\u00b7\2\2\u0267\u0340"+
		"\7\u00b8\2\2\u0268\u0340\7\u00b9\2\2\u0269\u0340\7\u00ba\2\2\u026a\u0340"+
		"\7\u00bb\2\2\u026b\u0340\7\u00bc\2\2\u026c\u0340\7\u00bd\2\2\u026d\u0340"+
		"\7\u00be\2\2\u026e\u0340\7\u00bf\2\2\u026f\u0340\7\u00c0\2\2\u0270\u0340"+
		"\7\u00c1\2\2\u0271\u0340\7\u00c2\2\2\u0272\u0340\7\u00c3\2\2\u0273\u0340"+
		"\7\u00c4\2\2\u0274\u0340\7\u00c5\2\2\u0275\u0340\7\u00c6\2\2\u0276\u0340"+
		"\7\u00c7\2\2\u0277\u0340\7\u00c8\2\2\u0278\u0340\7\u00c9\2\2\u0279\u0340"+
		"\7\u00ca\2\2\u027a\u0340\7\u00cb\2\2\u027b\u0340\7\u00cc\2\2\u027c\u0340"+
		"\7\u00cd\2\2\u027d\u0340\7\u00ce\2\2\u027e\u0340\7\u00cf\2\2\u027f\u0340"+
		"\7\u00d0\2\2\u0280\u0340\7\u00d1\2\2\u0281\u0340\7\u00d2\2\2\u0282\u0340"+
		"\7\u00d3\2\2\u0283\u0340\7\u00d4\2\2\u0284\u0340\7\u00d5\2\2\u0285\u0340"+
		"\7\u00d6\2\2\u0286\u0340\7\u00d7\2\2\u0287\u0340\7\u00d8\2\2\u0288\u0340"+
		"\7\u00d9\2\2\u0289\u0340\7\u00da\2\2\u028a\u0340\7\u00db\2\2\u028b\u0340"+
		"\7\u00dc\2\2\u028c\u0340\7\u00dd\2\2\u028d\u0340\7\u00de\2\2\u028e\u0340"+
		"\7\u00df\2\2\u028f\u0340\7\u00e0\2\2\u0290\u0340\7\u00e1\2\2\u0291\u0340"+
		"\7\u00e2\2\2\u0292\u0340\7\u00e3\2\2\u0293\u0340\7\u00e4\2\2\u0294\u0340"+
		"\7\u00e5\2\2\u0295\u0340\7\u00e6\2\2\u0296\u0340\7\u00e7\2\2\u0297\u0340"+
		"\7\u00e8\2\2\u0298\u0340\7\u00e9\2\2\u0299\u0340\7\u00ea\2\2\u029a\u0340"+
		"\7\u00eb\2\2\u029b\u0340\7\u00ec\2\2\u029c\u0340\7\u00ed\2\2\u029d\u0340"+
		"\7\u00ee\2\2\u029e\u0340\7\u00ef\2\2\u029f\u0340\7\u00f0\2\2\u02a0\u0340"+
		"\7\u00f1\2\2\u02a1\u0340\7\u00f2\2\2\u02a2\u0340\7\u00f3\2\2\u02a3\u0340"+
		"\7\u00f4\2\2\u02a4\u0340\7\u00f5\2\2\u02a5\u0340\7\u00f6\2\2\u02a6\u0340"+
		"\7\u00f7\2\2\u02a7\u0340\7\u00f8\2\2\u02a8\u0340\7\u00f9\2\2\u02a9\u0340"+
		"\7\u00fa\2\2\u02aa\u0340\7\u00fb\2\2\u02ab\u0340\7\u00fc\2\2\u02ac\u0340"+
		"\7\u00fd\2\2\u02ad\u0340\7\u00fe\2\2\u02ae\u0340\7\u00ff\2\2\u02af\u0340"+
		"\7\u0100\2\2\u02b0\u0340\7\u0101\2\2\u02b1\u0340\7\u0102\2\2\u02b2\u0340"+
		"\7\u0103\2\2\u02b3\u0340\7\u0104\2\2\u02b4\u0340\7\u0105\2\2\u02b5\u0340"+
		"\7\u0106\2\2\u02b6\u0340\7\u0107\2\2\u02b7\u0340\7\u0108\2\2\u02b8\u0340"+
		"\7\u0109\2\2\u02b9\u0340\7\u010a\2\2\u02ba\u0340\7\u010b\2\2\u02bb\u0340"+
		"\7\u010c\2\2\u02bc\u0340\7\u010d\2\2\u02bd\u0340\7\u010e\2\2\u02be\u0340"+
		"\7\u010f\2\2\u02bf\u0340\7\u0110\2\2\u02c0\u0340\7\u0111\2\2\u02c1\u0340"+
		"\7\u0112\2\2\u02c2\u0340\7\u0113\2\2\u02c3\u0340\7\u0114\2\2\u02c4\u0340"+
		"\7\u0115\2\2\u02c5\u0340\7\u0116\2\2\u02c6\u0340\7\u0117\2\2\u02c7\u0340"+
		"\7\u0118\2\2\u02c8\u0340\7\u0119\2\2\u02c9\u0340\7\u011a\2\2\u02ca\u0340"+
		"\7\u011b\2\2\u02cb\u0340\7\u011c\2\2\u02cc\u0340\7\u011d\2\2\u02cd\u0340"+
		"\7\u011e\2\2\u02ce\u0340\7\u011f\2\2\u02cf\u0340\7\u0120\2\2\u02d0\u0340"+
		"\7\u0121\2\2\u02d1\u0340\7\u0122\2\2\u02d2\u0340\7\u0123\2\2\u02d3\u0340"+
		"\7\u0124\2\2\u02d4\u0340\7\u0125\2\2\u02d5\u0340\7\u0126\2\2\u02d6\u0340"+
		"\7\u0127\2\2\u02d7\u0340\7\u0128\2\2\u02d8\u0340\7\u0129\2\2\u02d9\u0340"+
		"\7\u012a\2\2\u02da\u0340\7\u012b\2\2\u02db\u0340\7\u012c\2\2\u02dc\u0340"+
		"\7\u012d\2\2\u02dd\u0340\7\u012e\2\2\u02de\u0340\7\u012f\2\2\u02df\u0340"+
		"\7\u0130\2\2\u02e0\u0340\7\u0131\2\2\u02e1\u0340\7\u0132\2\2\u02e2\u0340"+
		"\7\u0133\2\2\u02e3\u0340\7\u0134\2\2\u02e4\u0340\7\u0135\2\2\u02e5\u0340"+
		"\7\u0136\2\2\u02e6\u0340\7\u0137\2\2\u02e7\u0340\7\u0138\2\2\u02e8\u0340"+
		"\7\u0139\2\2\u02e9\u0340\7\u013a\2\2\u02ea\u0340\7\u013b\2\2\u02eb\u0340"+
		"\t\25\2\2\u02ec\u0340\7\u013e\2\2\u02ed\u0340\7\u013f\2\2\u02ee\u0340"+
		"\7\u0140\2\2\u02ef\u0340\7\u0141\2\2\u02f0\u0340\7\u0142\2\2\u02f1\u0340"+
		"\7\u0143\2\2\u02f2\u0340\7\u0144\2\2\u02f3\u0340\7\u0145\2\2\u02f4\u0340"+
		"\7\u0146\2\2\u02f5\u0340\7\u0147\2\2\u02f6\u0340\7\u0148\2\2\u02f7\u0340"+
		"\7\u0149\2\2\u02f8\u0340\7\u014a\2\2\u02f9\u0340\7\u014b\2\2\u02fa\u0340"+
		"\7\u014c\2\2\u02fb\u0340\7\u014d\2\2\u02fc\u0340\7\u014e\2\2\u02fd\u0340"+
		"\7\u014f\2\2\u02fe\u0340\7\u0150\2\2\u02ff\u0340\7\u0151\2\2\u0300\u0340"+
		"\7\u0152\2\2\u0301\u0340\7\u0153\2\2\u0302\u0340\7\u0154\2\2\u0303\u0340"+
		"\7\u0155\2\2\u0304\u0340\7\u0156\2\2\u0305\u0340\7\u0157\2\2\u0306\u0340"+
		"\7\u0158\2\2\u0307\u0340\7\u0159\2\2\u0308\u0340\7\u015a\2\2\u0309\u0340"+
		"\7\u015b\2\2\u030a\u0340\7\u015c\2\2\u030b\u0340\7\u015d\2\2\u030c\u0340"+
		"\7\u015e\2\2\u030d\u0340\7\u015f\2\2\u030e\u0340\7\u0160\2\2\u030f\u0340"+
		"\7\u0161\2\2\u0310\u0340\7\u0162\2\2\u0311\u0340\7\u0163\2\2\u0312\u0340"+
		"\7\u0164\2\2\u0313\u0340\7\u0165\2\2\u0314\u0340\7\u0166\2\2\u0315\u0340"+
		"\7\u0167\2\2\u0316\u0340\7\u0168\2\2\u0317\u0340\7\u0169\2\2\u0318\u0340"+
		"\7\u016a\2\2\u0319\u0340\7\u016b\2\2\u031a\u0340\7\u016c\2\2\u031b\u0340"+
		"\7\u016d\2\2\u031c\u0340\7\u016e\2\2\u031d\u0340\7\u016f\2\2\u031e\u0340"+
		"\7\u0170\2\2\u031f\u0340\7\u0171\2\2\u0320\u0340\7\u0172\2\2\u0321\u0340"+
		"\7\u0173\2\2\u0322\u0340\7\u0174\2\2\u0323\u0340\7\u0175\2\2\u0324\u0340"+
		"\7\u0176\2\2\u0325\u0340\7\u0177\2\2\u0326\u0340\7\u0178\2\2\u0327\u0340"+
		"\7\u0179\2\2\u0328\u0340\7\u017a\2\2\u0329\u0340\7\u017b\2\2\u032a\u0340"+
		"\7\u017c\2\2\u032b\u0340\7\u017d\2\2\u032c\u0340\7\u017e\2\2\u032d\u0340"+
		"\7\u017f\2\2\u032e\u0340\7\u0180\2\2\u032f\u0340\7\u0181\2\2\u0330\u0340"+
		"\7\u0182\2\2\u0331\u0340\7\u0183\2\2\u0332\u0340\7\u0184\2\2\u0333\u0340"+
		"\7\u0185\2\2\u0334\u0340\7\u0186\2\2\u0335\u0340\7\u0187\2\2\u0336\u0340"+
		"\7\u0188\2\2\u0337\u0340\7\u0189\2\2\u0338\u0340\7\u018a\2\2\u0339\u0340"+
		"\7\u018b\2\2\u033a\u0340\7\u009f\2\2\u033b\u0340\7\u018c\2\2\u033c\u0340"+
		"\7\u018d\2\2\u033d\u0340\7\u018e\2\2\u033e\u0340\7\u018f\2\2\u033f\u01d8"+
		"\3\2\2\2\u033f\u01d9\3\2\2\2\u033f\u01da\3\2\2\2\u033f\u01db\3\2\2\2\u033f"+
		"\u01dc\3\2\2\2\u033f\u01dd\3\2\2\2\u033f\u01de\3\2\2\2\u033f\u01df\3\2"+
		"\2\2\u033f\u01e0\3\2\2\2\u033f\u01e1\3\2\2\2\u033f\u01e2\3\2\2\2\u033f"+
		"\u01e3\3\2\2\2\u033f\u01e4\3\2\2\2\u033f\u01e5\3\2\2\2\u033f\u01e6\3\2"+
		"\2\2\u033f\u01e7\3\2\2\2\u033f\u01e8\3\2\2\2\u033f\u01e9\3\2\2\2\u033f"+
		"\u01ea\3\2\2\2\u033f\u01eb\3\2\2\2\u033f\u01ec\3\2\2\2\u033f\u01ed\3\2"+
		"\2\2\u033f\u01ee\3\2\2\2\u033f\u01ef\3\2\2\2\u033f\u01f0\3\2\2\2\u033f"+
		"\u01f1\3\2\2\2\u033f\u01f2\3\2\2\2\u033f\u01f3\3\2\2\2\u033f\u01f4\3\2"+
		"\2\2\u033f\u01f5\3\2\2\2\u033f\u01f6\3\2\2\2\u033f\u01f7\3\2\2\2\u033f"+
		"\u01f8\3\2\2\2\u033f\u01f9\3\2\2\2\u033f\u01fa\3\2\2\2\u033f\u01fb\3\2"+
		"\2\2\u033f\u01fc\3\2\2\2\u033f\u01fd\3\2\2\2\u033f\u01fe\3\2\2\2\u033f"+
		"\u01ff\3\2\2\2\u033f\u0200\3\2\2\2\u033f\u0201\3\2\2\2\u033f\u0202\3\2"+
		"\2\2\u033f\u0203\3\2\2\2\u033f\u0204\3\2\2\2\u033f\u0205\3\2\2\2\u033f"+
		"\u0206\3\2\2\2\u033f\u0207\3\2\2\2\u033f\u0208\3\2\2\2\u033f\u0209\3\2"+
		"\2\2\u033f\u020a\3\2\2\2\u033f\u020b\3\2\2\2\u033f\u020c\3\2\2\2\u033f"+
		"\u020d\3\2\2\2\u033f\u020e\3\2\2\2\u033f\u020f\3\2\2\2\u033f\u0210\3\2"+
		"\2\2\u033f\u0211\3\2\2\2\u033f\u0212\3\2\2\2\u033f\u0213\3\2\2\2\u033f"+
		"\u0214\3\2\2\2\u033f\u0215\3\2\2\2\u033f\u0216\3\2\2\2\u033f\u0217\3\2"+
		"\2\2\u033f\u0218\3\2\2\2\u033f\u0219\3\2\2\2\u033f\u021a\3\2\2\2\u033f"+
		"\u021b\3\2\2\2\u033f\u021c\3\2\2\2\u033f\u021d\3\2\2\2\u033f\u021e\3\2"+
		"\2\2\u033f\u021f\3\2\2\2\u033f\u0220\3\2\2\2\u033f\u0221\3\2\2\2\u033f"+
		"\u0222\3\2\2\2\u033f\u0223\3\2\2\2\u033f\u0224\3\2\2\2\u033f\u0225\3\2"+
		"\2\2\u033f\u0226\3\2\2\2\u033f\u0227\3\2\2\2\u033f\u0228\3\2\2\2\u033f"+
		"\u0229\3\2\2\2\u033f\u022a\3\2\2\2\u033f\u022b\3\2\2\2\u033f\u022c\3\2"+
		"\2\2\u033f\u022d\3\2\2\2\u033f\u022e\3\2\2\2\u033f\u022f\3\2\2\2\u033f"+
		"\u0230\3\2\2\2\u033f\u0231\3\2\2\2\u033f\u0232\3\2\2\2\u033f\u0233\3\2"+
		"\2\2\u033f\u0234\3\2\2\2\u033f\u0235\3\2\2\2\u033f\u0236\3\2\2\2\u033f"+
		"\u0237\3\2\2\2\u033f\u0238\3\2\2\2\u033f\u0239\3\2\2\2\u033f\u023a\3\2"+
		"\2\2\u033f\u023b\3\2\2\2\u033f\u023c\3\2\2\2\u033f\u023d\3\2\2\2\u033f"+
		"\u023e\3\2\2\2\u033f\u023f\3\2\2\2\u033f\u0240\3\2\2\2\u033f\u0241\3\2"+
		"\2\2\u033f\u0242\3\2\2\2\u033f\u0243\3\2\2\2\u033f\u0244\3\2\2\2\u033f"+
		"\u0245\3\2\2\2\u033f\u0246\3\2\2\2\u033f\u0247\3\2\2\2\u033f\u0248\3\2"+
		"\2\2\u033f\u0249\3\2\2\2\u033f\u024a\3\2\2\2\u033f\u024b\3\2\2\2\u033f"+
		"\u024c\3\2\2\2\u033f\u024d\3\2\2\2\u033f\u024e\3\2\2\2\u033f\u024f\3\2"+
		"\2\2\u033f\u0250\3\2\2\2\u033f\u0251\3\2\2\2\u033f\u0252\3\2\2\2\u033f"+
		"\u0253\3\2\2\2\u033f\u0254\3\2\2\2\u033f\u0255\3\2\2\2\u033f\u0256\3\2"+
		"\2\2\u033f\u0257\3\2\2\2\u033f\u0258\3\2\2\2\u033f\u0259\3\2\2\2\u033f"+
		"\u025a\3\2\2\2\u033f\u025b\3\2\2\2\u033f\u025c\3\2\2\2\u033f\u025d\3\2"+
		"\2\2\u033f\u025e\3\2\2\2\u033f\u025f\3\2\2\2\u033f\u0260\3\2\2\2\u033f"+
		"\u0261\3\2\2\2\u033f\u0262\3\2\2\2\u033f\u0263\3\2\2\2\u033f\u0264\3\2"+
		"\2\2\u033f\u0265\3\2\2\2\u033f\u0266\3\2\2\2\u033f\u0267\3\2\2\2\u033f"+
		"\u0268\3\2\2\2\u033f\u0269\3\2\2\2\u033f\u026a\3\2\2\2\u033f\u026b\3\2"+
		"\2\2\u033f\u026c\3\2\2\2\u033f\u026d\3\2\2\2\u033f\u026e\3\2\2\2\u033f"+
		"\u026f\3\2\2\2\u033f\u0270\3\2\2\2\u033f\u0271\3\2\2\2\u033f\u0272\3\2"+
		"\2\2\u033f\u0273\3\2\2\2\u033f\u0274\3\2\2\2\u033f\u0275\3\2\2\2\u033f"+
		"\u0276\3\2\2\2\u033f\u0277\3\2\2\2\u033f\u0278\3\2\2\2\u033f\u0279\3\2"+
		"\2\2\u033f\u027a\3\2\2\2\u033f\u027b\3\2\2\2\u033f\u027c\3\2\2\2\u033f"+
		"\u027d\3\2\2\2\u033f\u027e\3\2\2\2\u033f\u027f\3\2\2\2\u033f\u0280\3\2"+
		"\2\2\u033f\u0281\3\2\2\2\u033f\u0282\3\2\2\2\u033f\u0283\3\2\2\2\u033f"+
		"\u0284\3\2\2\2\u033f\u0285\3\2\2\2\u033f\u0286\3\2\2\2\u033f\u0287\3\2"+
		"\2\2\u033f\u0288\3\2\2\2\u033f\u0289\3\2\2\2\u033f\u028a\3\2\2\2\u033f"+
		"\u028b\3\2\2\2\u033f\u028c\3\2\2\2\u033f\u028d\3\2\2\2\u033f\u028e\3\2"+
		"\2\2\u033f\u028f\3\2\2\2\u033f\u0290\3\2\2\2\u033f\u0291\3\2\2\2\u033f"+
		"\u0292\3\2\2\2\u033f\u0293\3\2\2\2\u033f\u0294\3\2\2\2\u033f\u0295\3\2"+
		"\2\2\u033f\u0296\3\2\2\2\u033f\u0297\3\2\2\2\u033f\u0298\3\2\2\2\u033f"+
		"\u0299\3\2\2\2\u033f\u029a\3\2\2\2\u033f\u029b\3\2\2\2\u033f\u029c\3\2"+
		"\2\2\u033f\u029d\3\2\2\2\u033f\u029e\3\2\2\2\u033f\u029f\3\2\2\2\u033f"+
		"\u02a0\3\2\2\2\u033f\u02a1\3\2\2\2\u033f\u02a2\3\2\2\2\u033f\u02a3\3\2"+
		"\2\2\u033f\u02a4\3\2\2\2\u033f\u02a5\3\2\2\2\u033f\u02a6\3\2\2\2\u033f"+
		"\u02a7\3\2\2\2\u033f\u02a8\3\2\2\2\u033f\u02a9\3\2\2\2\u033f\u02aa\3\2"+
		"\2\2\u033f\u02ab\3\2\2\2\u033f\u02ac\3\2\2\2\u033f\u02ad\3\2\2\2\u033f"+
		"\u02ae\3\2\2\2\u033f\u02af\3\2\2\2\u033f\u02b0\3\2\2\2\u033f\u02b1\3\2"+
		"\2\2\u033f\u02b2\3\2\2\2\u033f\u02b3\3\2\2\2\u033f\u02b4\3\2\2\2\u033f"+
		"\u02b5\3\2\2\2\u033f\u02b6\3\2\2\2\u033f\u02b7\3\2\2\2\u033f\u02b8\3\2"+
		"\2\2\u033f\u02b9\3\2\2\2\u033f\u02ba\3\2\2\2\u033f\u02bb\3\2\2\2\u033f"+
		"\u02bc\3\2\2\2\u033f\u02bd\3\2\2\2\u033f\u02be\3\2\2\2\u033f\u02bf\3\2"+
		"\2\2\u033f\u02c0\3\2\2\2\u033f\u02c1\3\2\2\2\u033f\u02c2\3\2\2\2\u033f"+
		"\u02c3\3\2\2\2\u033f\u02c4\3\2\2\2\u033f\u02c5\3\2\2\2\u033f\u02c6\3\2"+
		"\2\2\u033f\u02c7\3\2\2\2\u033f\u02c8\3\2\2\2\u033f\u02c9\3\2\2\2\u033f"+
		"\u02ca\3\2\2\2\u033f\u02cb\3\2\2\2\u033f\u02cc\3\2\2\2\u033f\u02cd\3\2"+
		"\2\2\u033f\u02ce\3\2\2\2\u033f\u02cf\3\2\2\2\u033f\u02d0\3\2\2\2\u033f"+
		"\u02d1\3\2\2\2\u033f\u02d2\3\2\2\2\u033f\u02d3\3\2\2\2\u033f\u02d4\3\2"+
		"\2\2\u033f\u02d5\3\2\2\2\u033f\u02d6\3\2\2\2\u033f\u02d7\3\2\2\2\u033f"+
		"\u02d8\3\2\2\2\u033f\u02d9\3\2\2\2\u033f\u02da\3\2\2\2\u033f\u02db\3\2"+
		"\2\2\u033f\u02dc\3\2\2\2\u033f\u02dd\3\2\2\2\u033f\u02de\3\2\2\2\u033f"+
		"\u02df\3\2\2\2\u033f\u02e0\3\2\2\2\u033f\u02e1\3\2\2\2\u033f\u02e2\3\2"+
		"\2\2\u033f\u02e3\3\2\2\2\u033f\u02e4\3\2\2\2\u033f\u02e5\3\2\2\2\u033f"+
		"\u02e6\3\2\2\2\u033f\u02e7\3\2\2\2\u033f\u02e8\3\2\2\2\u033f\u02e9\3\2"+
		"\2\2\u033f\u02ea\3\2\2\2\u033f\u02eb\3\2\2\2\u033f\u02ec\3\2\2\2\u033f"+
		"\u02ed\3\2\2\2\u033f\u02ee\3\2\2\2\u033f\u02ef\3\2\2\2\u033f\u02f0\3\2"+
		"\2\2\u033f\u02f1\3\2\2\2\u033f\u02f2\3\2\2\2\u033f\u02f3\3\2\2\2\u033f"+
		"\u02f4\3\2\2\2\u033f\u02f5\3\2\2\2\u033f\u02f6\3\2\2\2\u033f\u02f7\3\2"+
		"\2\2\u033f\u02f8\3\2\2\2\u033f\u02f9\3\2\2\2\u033f\u02fa\3\2\2\2\u033f"+
		"\u02fb\3\2\2\2\u033f\u02fc\3\2\2\2\u033f\u02fd\3\2\2\2\u033f\u02fe\3\2"+
		"\2\2\u033f\u02ff\3\2\2\2\u033f\u0300\3\2\2\2\u033f\u0301\3\2\2\2\u033f"+
		"\u0302\3\2\2\2\u033f\u0303\3\2\2\2\u033f\u0304\3\2\2\2\u033f\u0305\3\2"+
		"\2\2\u033f\u0306\3\2\2\2\u033f\u0307\3\2\2\2\u033f\u0308\3\2\2\2\u033f"+
		"\u0309\3\2\2\2\u033f\u030a\3\2\2\2\u033f\u030b\3\2\2\2\u033f\u030c\3\2"+
		"\2\2\u033f\u030d\3\2\2\2\u033f\u030e\3\2\2\2\u033f\u030f\3\2\2\2\u033f"+
		"\u0310\3\2\2\2\u033f\u0311\3\2\2\2\u033f\u0312\3\2\2\2\u033f\u0313\3\2"+
		"\2\2\u033f\u0314\3\2\2\2\u033f\u0315\3\2\2\2\u033f\u0316\3\2\2\2\u033f"+
		"\u0317\3\2\2\2\u033f\u0318\3\2\2\2\u033f\u0319\3\2\2\2\u033f\u031a\3\2"+
		"\2\2\u033f\u031b\3\2\2\2\u033f\u031c\3\2\2\2\u033f\u031d\3\2\2\2\u033f"+
		"\u031e\3\2\2\2\u033f\u031f\3\2\2\2\u033f\u0320\3\2\2\2\u033f\u0321\3\2"+
		"\2\2\u033f\u0322\3\2\2\2\u033f\u0323\3\2\2\2\u033f\u0324\3\2\2\2\u033f"+
		"\u0325\3\2\2\2\u033f\u0326\3\2\2\2\u033f\u0327\3\2\2\2\u033f\u0328\3\2"+
		"\2\2\u033f\u0329\3\2\2\2\u033f\u032a\3\2\2\2\u033f\u032b\3\2\2\2\u033f"+
		"\u032c\3\2\2\2\u033f\u032d\3\2\2\2\u033f\u032e\3\2\2\2\u033f\u032f\3\2"+
		"\2\2\u033f\u0330\3\2\2\2\u033f\u0331\3\2\2\2\u033f\u0332\3\2\2\2\u033f"+
		"\u0333\3\2\2\2\u033f\u0334\3\2\2\2\u033f\u0335\3\2\2\2\u033f\u0336\3\2"+
		"\2\2\u033f\u0337\3\2\2\2\u033f\u0338\3\2\2\2\u033f\u0339\3\2\2\2\u033f"+
		"\u033a\3\2\2\2\u033f\u033b\3\2\2\2\u033f\u033c\3\2\2\2\u033f\u033d\3\2"+
		"\2\2\u033f\u033e\3\2\2\2\u0340\61\3\2\2\2\u0341\u0373\6\32\3\2\u0342\u0374"+
		"\7\u0190\2\2\u0343\u0374\7\u0191\2\2\u0344\u0374\7\u0192\2\2\u0345\u0374"+
		"\7\u0193\2\2\u0346\u0374\7\u0194\2\2\u0347\u0374\7\u0195\2\2\u0348\u0374"+
		"\7\u0196\2\2\u0349\u0374\7\u0197\2\2\u034a\u0374\7\u0198\2\2\u034b\u0374"+
		"\7\u0199\2\2\u034c\u0374\7\u019a\2\2\u034d\u0374\7\u019b\2\2\u034e\u0374"+
		"\7\u019c\2\2\u034f\u0374\7\u019d\2\2\u0350\u0374\7\u019e\2\2\u0351\u0374"+
		"\7\u019f\2\2\u0352\u0374\7\u01a0\2\2\u0353\u0374\7\u01a1\2\2\u0354\u0374"+
		"\7\u01a2\2\2\u0355\u0374\7\u01a3\2\2\u0356\u0374\7\u01a4\2\2\u0357\u0374"+
		"\7\u01a5\2\2\u0358\u0374\7\u01a6\2\2\u0359\u0374\7\u01a7\2\2\u035a\u0374"+
		"\7\u01a8\2\2\u035b\u0374\7\u01a9\2\2\u035c\u0374\7\u01aa\2\2\u035d\u0374"+
		"\7\u01ab\2\2\u035e\u0374\7\u01ac\2\2\u035f\u0374\7\u01ad\2\2\u0360\u0374"+
		"\7\u01ae\2\2\u0361\u0374\7\u01af\2\2\u0362\u0374\7\u01b0\2\2\u0363\u0374"+
		"\7\u01b1\2\2\u0364\u0374\7\u01b2\2\2\u0365\u0374\7\u01b3\2\2\u0366\u0374"+
		"\7\u01b4\2\2\u0367\u0374\7\u01b5\2\2\u0368\u0374\7\u01b6\2\2\u0369\u0374"+
		"\7\u01b7\2\2\u036a\u0374\7\u01b8\2\2\u036b\u0374\7\u01b9\2\2\u036c\u0374"+
		"\7\u01ba\2\2\u036d\u0374\7\u01bb\2\2\u036e\u0374\7\u01bc\2\2\u036f\u0374"+
		"\7\u01bd\2\2\u0370\u0374\7\u01be\2\2\u0371\u0374\7\u01bf\2\2\u0372\u0374"+
		"\7\u01c0\2\2\u0373\u0342\3\2\2\2\u0373\u0343\3\2\2\2\u0373\u0344\3\2\2"+
		"\2\u0373\u0345\3\2\2\2\u0373\u0346\3\2\2\2\u0373\u0347\3\2\2\2\u0373\u0348"+
		"\3\2\2\2\u0373\u0349\3\2\2\2\u0373\u034a\3\2\2\2\u0373\u034b\3\2\2\2\u0373"+
		"\u034c\3\2\2\2\u0373\u034d\3\2\2\2\u0373\u034e\3\2\2\2\u0373\u034f\3\2"+
		"\2\2\u0373\u0350\3\2\2\2\u0373\u0351\3\2\2\2\u0373\u0352\3\2\2\2\u0373"+
		"\u0353\3\2\2\2\u0373\u0354\3\2\2\2\u0373\u0355\3\2\2\2\u0373\u0356\3\2"+
		"\2\2\u0373\u0357\3\2\2\2\u0373\u0358\3\2\2\2\u0373\u0359\3\2\2\2\u0373"+
		"\u035a\3\2\2\2\u0373\u035b\3\2\2\2\u0373\u035c\3\2\2\2\u0373\u035d\3\2"+
		"\2\2\u0373\u035e\3\2\2\2\u0373\u035f\3\2\2\2\u0373\u0360\3\2\2\2\u0373"+
		"\u0361\3\2\2\2\u0373\u0362\3\2\2\2\u0373\u0363\3\2\2\2\u0373\u0364\3\2"+
		"\2\2\u0373\u0365\3\2\2\2\u0373\u0366\3\2\2\2\u0373\u0367\3\2\2\2\u0373"+
		"\u0368\3\2\2\2\u0373\u0369\3\2\2\2\u0373\u036a\3\2\2\2\u0373\u036b\3\2"+
		"\2\2\u0373\u036c\3\2\2\2\u0373\u036d\3\2\2\2\u0373\u036e\3\2\2\2\u0373"+
		"\u036f\3\2\2\2\u0373\u0370\3\2\2\2\u0373\u0371\3\2\2\2\u0373\u0372\3\2"+
		"\2\2\u0374\63\3\2\2\2\u0375\u0378\7\u01c1\2\2\u0376\u0377\7\5\2\2\u0377"+
		"\u0379\7\u01c2\2\2\u0378\u0376\3\2\2\2\u0378\u0379\3\2\2\2\u0379\u0387"+
		"\3\2\2\2\u037a\u037d\7\u01c2\2\2\u037b\u037c\7\5\2\2\u037c\u037e\7\u01c1"+
		"\2\2\u037d\u037b\3\2\2\2\u037d\u037e\3\2\2\2\u037e\u0387\3\2\2\2\u037f"+
		"\u0387\7\u01c3\2\2\u0380\u0387\7\u01c4\2\2\u0381\u0387\7\u01c5\2\2\u0382"+
		"\u0387\7\u01c6\2\2\u0383\u0387\7\u01c7\2\2\u0384\u0387\7\u01c8\2\2\u0385"+
		"\u0387\7\u01c9\2\2\u0386\u0375\3\2\2\2\u0386\u037a\3\2\2\2\u0386\u037f"+
		"\3\2\2\2\u0386\u0380\3\2\2\2\u0386\u0381\3\2\2\2\u0386\u0382\3\2\2\2\u0386"+
		"\u0383\3\2\2\2\u0386\u0384\3\2\2\2\u0386\u0385\3\2\2\2\u0387\65\3\2\2"+
		"\2\u0388\u038a\t\26\2\2\u0389\u0388\3\2\2\2\u0389\u038a\3\2\2\2\u038a"+
		"\u038c\3\2\2\2\u038b\u038d\7\u01cb\2\2\u038c\u038b\3\2\2\2\u038c\u038d"+
		"\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u038f\5> \2\u038f\67\3\2\2\2\u0390"+
		"\u0392\t\26\2\2\u0391\u0390\3\2\2\2\u0391\u0392\3\2\2\2\u0392\u0394\3"+
		"\2\2\2\u0393\u0395\7\u01cb\2\2\u0394\u0393\3\2\2\2\u0394\u0395\3\2\2\2"+
		"\u0395\u0396\3\2\2\2\u0396\u0397\5> \2\u03979\3\2\2\2\u0398\u0399\7\t"+
		"\2\2\u0399\u039a\5> \2\u039a;\3\2\2\2\u039b\u039c\t\27\2\2\u039c=\3\2"+
		"\2\2\u039d\u039e\b \1\2\u039e\u039f\t\30\2\2\u039f\u03ae\5> \16\u03a0"+
		"\u03a1\t\31\2\2\u03a1\u03a2\7\u0200\2\2\u03a2\u03a3\5> \2\u03a3\u03a4"+
		"\7\u0201\2\2\u03a4\u03ae\3\2\2\2\u03a5\u03a6\7\u0200\2\2\u03a6\u03a7\5"+
		"> \2\u03a7\u03a8\7\u0201\2\2\u03a8\u03ae\3\2\2\2\u03a9\u03ab\7\u0202\2"+
		"\2\u03aa\u03a9\3\2\2\2\u03aa\u03ab\3\2\2\2\u03ab\u03ac\3\2\2\2\u03ac\u03ae"+
		"\5@!\2\u03ad\u039d\3\2\2\2\u03ad\u03a0\3\2\2\2\u03ad\u03a5\3\2\2\2\u03ad"+
		"\u03aa\3\2\2\2\u03ae\u03cc\3\2\2\2\u03af\u03b0\f\r\2\2\u03b0\u03b1\t\32"+
		"\2\2\u03b1\u03cb\5> \16\u03b2\u03b3\f\f\2\2\u03b3\u03b4\t\33\2\2\u03b4"+
		"\u03cb\5> \r\u03b5\u03b6\f\13\2\2\u03b6\u03b7\t\34\2\2\u03b7\u03cb\5>"+
		" \f\u03b8\u03b9\f\n\2\2\u03b9\u03ba\t\35\2\2\u03ba\u03cb\5> \13\u03bb"+
		"\u03bc\f\t\2\2\u03bc\u03bd\t\36\2\2\u03bd\u03cb\5> \n\u03be\u03bf\f\b"+
		"\2\2\u03bf\u03c0\t\37\2\2\u03c0\u03cb\5> \t\u03c1\u03c2\f\7\2\2\u03c2"+
		"\u03c3\t \2\2\u03c3\u03cb\5> \b\u03c4\u03c5\f\6\2\2\u03c5\u03c6\7\u01f9"+
		"\2\2\u03c6\u03c7\5> \2\u03c7\u03c8\7\f\2\2\u03c8\u03c9\5> \7\u03c9\u03cb"+
		"\3\2\2\2\u03ca\u03af\3\2\2\2\u03ca\u03b2\3\2\2\2\u03ca\u03b5\3\2\2\2\u03ca"+
		"\u03b8\3\2\2\2\u03ca\u03bb\3\2\2\2\u03ca\u03be\3\2\2\2\u03ca\u03c1\3\2"+
		"\2\2\u03ca\u03c4\3\2\2\2\u03cb\u03ce\3\2\2\2\u03cc\u03ca\3\2\2\2\u03cc"+
		"\u03cd\3\2\2\2\u03cd?\3\2\2\2\u03ce\u03cc\3\2\2\2\u03cf\u03da\7\u020d"+
		"\2\2\u03d0\u03da\7\u020c\2\2\u03d1\u03da\7\u020b\2\2\u03d2\u03da\7\u020a"+
		"\2\2\u03d3\u03da\7\u0209\2\2\u03d4\u03da\7\u0203\2\2\u03d5\u03d7\7\u0208"+
		"\2\2\u03d6\u03d8\t!\2\2\u03d7\u03d6\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8"+
		"\u03da\3\2\2\2\u03d9\u03cf\3\2\2\2\u03d9\u03d0\3\2\2\2\u03d9\u03d1\3\2"+
		"\2\2\u03d9\u03d2\3\2\2\2\u03d9\u03d3\3\2\2\2\u03d9\u03d4\3\2\2\2\u03d9"+
		"\u03d5\3\2\2\2\u03daA\3\2\2\2_ELNV[aemqx}\u0086\u008b\u008e\u0093\u0098"+
		"\u009e\u00a8\u00b0\u00b9\u00be\u00c4\u00c8\u00cf\u00d7\u00db\u00e1\u00e6"+
		"\u00eb\u00f7\u00f9\u0100\u0103\u0106\u010f\u0114\u0117\u011a\u0121\u0126"+
		"\u0129\u012c\u0131\u0136\u0139\u013c\u0140\u0145\u0147\u014e\u0154\u0156"+
		"\u015b\u0161\u0163\u0168\u016c\u0171\u0175\u017a\u017d\u0182\u0187\u018b"+
		"\u0191\u0194\u0199\u019e\u01a2\u01a8\u01ab\u01b0\u01b5\u01b9\u01bf\u01c9"+
		"\u01d0\u01d4\u033f\u0373\u0378\u037d\u0386\u0389\u038c\u0391\u0394\u03aa"+
		"\u03ad\u03ca\u03cc\u03d7\u03d9";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}