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
		T__515=516, T__516=517, T__517=518, T__518=519, T__519=520, T__520=521, 
		T__521=522, T__522=523, T__523=524, T__524=525, T__525=526, T__526=527, 
		T__527=528, T__528=529, T__529=530, T__530=531, T__531=532, T__532=533, 
		T__533=534, T__534=535, T__535=536, T__536=537, T__537=538, T__538=539, 
		T__539=540, T__540=541, T__541=542, T__542=543, T__543=544, T__544=545, 
		T__545=546, T__546=547, T__547=548, T__548=549, T__549=550, T__550=551, 
		T__551=552, T__552=553, T__553=554, T__554=555, T__555=556, T__556=557, 
		T__557=558, T__558=559, T__559=560, T__560=561, T__561=562, T__562=563, 
		T__563=564, T__564=565, T__565=566, T__566=567, T__567=568, T__568=569, 
		T__569=570, T__570=571, T__571=572, T__572=573, T__573=574, T__574=575, 
		T__575=576, T__576=577, T__577=578, T__578=579, T__579=580, T__580=581, 
		T__581=582, T__582=583, T__583=584, T__584=585, T__585=586, T__586=587, 
		T__587=588, T__588=589, T__589=590, T__590=591, T__591=592, T__592=593, 
		T__593=594, T__594=595, T__595=596, T__596=597, T__597=598, T__598=599, 
		T__599=600, T__600=601, T__601=602, T__602=603, T__603=604, T__604=605, 
		T__605=606, T__606=607, T__607=608, T__608=609, T__609=610, T__610=611, 
		T__611=612, T__612=613, T__613=614, T__614=615, T__615=616, T__616=617, 
		T__617=618, T__618=619, T__619=620, T__620=621, T__621=622, T__622=623, 
		T__623=624, T__624=625, T__625=626, T__626=627, T__627=628, T__628=629, 
		T__629=630, T__630=631, T__631=632, T__632=633, T__633=634, T__634=635, 
		T__635=636, T__636=637, T__637=638, T__638=639, T__639=640, T__640=641, 
		T__641=642, T__642=643, T__643=644, T__644=645, T__645=646, T__646=647, 
		T__647=648, T__648=649, T__649=650, T__650=651, T__651=652, T__652=653, 
		T__653=654, T__654=655, T__655=656, T__656=657, T__657=658, T__658=659, 
		T__659=660, T__660=661, T__661=662, T__662=663, T__663=664, T__664=665, 
		T__665=666, T__666=667, T__667=668, T__668=669, T__669=670, T__670=671, 
		T__671=672, T__672=673, T__673=674, T__674=675, T__675=676, T__676=677, 
		T__677=678, T__678=679, T__679=680, T__680=681, T__681=682, T__682=683, 
		T__683=684, T__684=685, T__685=686, T__686=687, T__687=688, T__688=689, 
		T__689=690, T__690=691, T__691=692, T__692=693, T__693=694, T__694=695, 
		T__695=696, T__696=697, T__697=698, T__698=699, T__699=700, T__700=701, 
		T__701=702, T__702=703, T__703=704, T__704=705, T__705=706, T__706=707, 
		T__707=708, T__708=709, T__709=710, T__710=711, T__711=712, T__712=713, 
		T__713=714, T__714=715, T__715=716, T__716=717, T__717=718, T__718=719, 
		T__719=720, T__720=721, T__721=722, T__722=723, T__723=724, T__724=725, 
		T__725=726, T__726=727, T__727=728, T__728=729, T__729=730, T__730=731, 
		T__731=732, T__732=733, T__733=734, T__734=735, T__735=736, T__736=737, 
		T__737=738, T__738=739, T__739=740, T__740=741, T__741=742, T__742=743, 
		T__743=744, T__744=745, T__745=746, T__746=747, T__747=748, T__748=749, 
		T__749=750, T__750=751, T__751=752, T__752=753, T__753=754, T__754=755, 
		T__755=756, T__756=757, T__757=758, T__758=759, T__759=760, T__760=761, 
		T__761=762, T__762=763, T__763=764, T__764=765, T__765=766, T__766=767, 
		T__767=768, T__768=769, T__769=770, T__770=771, T__771=772, T__772=773, 
		T__773=774, T__774=775, T__775=776, T__776=777, T__777=778, T__778=779, 
		T__779=780, T__780=781, T__781=782, T__782=783, T__783=784, T__784=785, 
		T__785=786, T__786=787, T__787=788, T__788=789, T__789=790, T__790=791, 
		T__791=792, T__792=793, T__793=794, T__794=795, T__795=796, T__796=797, 
		T__797=798, T__798=799, T__799=800, T__800=801, T__801=802, T__802=803, 
		T__803=804, T__804=805, T__805=806, T__806=807, T__807=808, T__808=809, 
		T__809=810, T__810=811, T__811=812, T__812=813, T__813=814, T__814=815, 
		T__815=816, T__816=817, T__817=818, T__818=819, T__819=820, T__820=821, 
		T__821=822, T__822=823, T__823=824, T__824=825, T__825=826, T__826=827, 
		T__827=828, T__828=829, T__829=830, T__830=831, T__831=832, T__832=833, 
		T__833=834, T__834=835, T__835=836, T__836=837, T__837=838, T__838=839, 
		T__839=840, T__840=841, T__841=842, T__842=843, T__843=844, T__844=845, 
		T__845=846, T__846=847, T__847=848, T__848=849, T__849=850, T__850=851, 
		T__851=852, T__852=853, T__853=854, T__854=855, T__855=856, T__856=857, 
		T__857=858, T__858=859, T__859=860, T__860=861, T__861=862, T__862=863, 
		T__863=864, T__864=865, T__865=866, T__866=867, T__867=868, T__868=869, 
		T__869=870, T__870=871, T__871=872, T__872=873, T__873=874, T__874=875, 
		T__875=876, T__876=877, T__877=878, T__878=879, T__879=880, T__880=881, 
		T__881=882, T__882=883, T__883=884, T__884=885, T__885=886, T__886=887, 
		T__887=888, T__888=889, T__889=890, T__890=891, T__891=892, T__892=893, 
		T__893=894, T__894=895, T__895=896, T__896=897, T__897=898, T__898=899, 
		T__899=900, T__900=901, T__901=902, T__902=903, T__903=904, T__904=905, 
		T__905=906, T__906=907, T__907=908, T__908=909, T__909=910, T__910=911, 
		T__911=912, T__912=913, T__913=914, T__914=915, T__915=916, T__916=917, 
		T__917=918, BLOCK_COMMENT=919, COMMENT=920, PTR=921, VARIABLE=922, STRING=923, 
		QUAD=924, BIN=925, HEX=926, NUMBER=927, NL=928, WS=929;
	public static final int
		RULE_prog = 0, RULE_constants = 1, RULE_constant = 2, RULE_objects = 3, 
		RULE_object = 4, RULE_reference = 5, RULE_filename = 6, RULE_variables = 7, 
		RULE_variable = 8, RULE_data = 9, RULE_dataLine = 10, RULE_typeValue = 11, 
		RULE_dataValue = 12, RULE_directive = 13, RULE_label = 14, RULE_opcode = 15, 
		RULE_condition = 16, RULE_effect = 17, RULE_argument = 18, RULE_prefix = 19, 
		RULE_type = 20, RULE_expression = 21, RULE_atom = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "constants", "constant", "objects", "object", "reference", "filename", 
			"variables", "variable", "data", "dataLine", "typeValue", "dataValue", 
			"directive", "label", "opcode", "condition", "effect", "argument", "prefix", 
			"type", "expression", "atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'CON'", "'con'", "','", "'='", "'['", "']'", "'#'", "'OBJ'", "'obj'", 
			"':'", "'VAR'", "'var'", "'DAT'", "'dat'", "'LONG'", "'long'", "'WORD'", 
			"'word'", "'BYTE'", "'byte'", "'RES'", "'res'", "'.'", "'ORG'", "'org'", 
			"'ORGH'", "'orgh'", "'ORGF'", "'orgf'", "'FIT'", "'fit'", "'NOP'", "'nop'", 
			"'ROR'", "'ror'", "'ROL'", "'rol'", "'SHR'", "'shr'", "'SHL'", "'shl'", 
			"'RCR'", "'rcr'", "'RCL'", "'rcl'", "'SAR'", "'sar'", "'SAL'", "'sal'", 
			"'ADD'", "'add'", "'ADDX'", "'addx'", "'ADDS'", "'adds'", "'ADDSX'", 
			"'addsx'", "'SUB'", "'sub'", "'SUBX'", "'subx'", "'SUBS'", "'subs'", 
			"'SUBSX'", "'subsx'", "'CMP'", "'cmp'", "'CMPX'", "'cmpx'", "'CMPS'", 
			"'cmps'", "'CMPSX'", "'cmpsx'", "'CMPR'", "'cmpr'", "'CMPM'", "'cmpm'", 
			"'SUBR'", "'subr'", "'CMPSUB'", "'cmpsub'", "'FGE'", "'fge'", "'FLE'", 
			"'fle'", "'FGES'", "'fges'", "'FLES'", "'fles'", "'SUMC'", "'sumc'", 
			"'SUMNC'", "'sumnc'", "'SUMZ'", "'sumz'", "'SUMNZ'", "'sumnz'", "'TESTB'", 
			"'testb'", "'TESTBN'", "'testbn'", "'BITL'", "'bitl'", "'BITH'", "'bith'", 
			"'BITC'", "'bitc'", "'BITNC'", "'bitnc'", "'BITZ'", "'bitz'", "'BITNZ'", 
			"'bitnz'", "'BITRND'", "'bitrnd'", "'BITNOT'", "'bitnot'", "'AND'", "'and'", 
			"'ANDN'", "'andn'", "'OR'", "'or'", "'XOR'", "'xor'", "'MUXC'", "'muxc'", 
			"'MUXNC'", "'muxnc'", "'MUXZ'", "'muxz'", "'MUXNZ'", "'muxnz'", "'MOV'", 
			"'mov'", "'NOT'", "'not'", "'ABS'", "'abs'", "'NEG'", "'neg'", "'NEGC'", 
			"'negc'", "'NEGNC'", "'negnc'", "'NEGZ'", "'negz'", "'NEGNZ'", "'negnz'", 
			"'INCMOD'", "'incmod'", "'DECMOD'", "'decmod'", "'ZEROX'", "'zerox'", 
			"'SIGNX'", "'signx'", "'ENCOD'", "'encod'", "'ONES'", "'ones'", "'TEST'", 
			"'test'", "'TESTN'", "'testn'", "'SETNIB'", "'setnib'", "'GETNIB'", "'getnib'", 
			"'ROLNIB'", "'rolnib'", "'SETBYTE'", "'setbyte'", "'GETBYTE'", "'getbyte'", 
			"'ROLBYTE'", "'rolbyte'", "'SETWORD'", "'setword'", "'GETWORD'", "'getword'", 
			"'ROLWORD'", "'rolword'", "'ALTSN'", "'altsn'", "'ALTGN'", "'altgn'", 
			"'ALTSB'", "'altsb'", "'ALTGB'", "'altgb'", "'ALTSW'", "'altsw'", "'ALTGW'", 
			"'altgw'", "'ALTR'", "'altr'", "'ALTD'", "'altd'", "'ALTS'", "'alts'", 
			"'ALTB'", "'altb'", "'ALTI'", "'alti'", "'SETR'", "'setr'", "'SETD'", 
			"'setd'", "'SETS'", "'sets'", "'DECOD'", "'decod'", "'BMASK'", "'bmask'", 
			"'CRCBIT'", "'crcbit'", "'CRCNIB'", "'crcnib'", "'MUXNITS'", "'muxnits'", 
			"'MUXNIBS'", "'muxnibs'", "'MUXQ'", "'muxq'", "'MOVBYTS'", "'movbyts'", 
			"'MUL'", "'mul'", "'MULS'", "'muls'", "'SCA'", "'sca'", "'SCAS'", "'scas'", 
			"'ADDPIX'", "'addpix'", "'MULPIX'", "'mulpix'", "'BLNPIX'", "'blnpix'", 
			"'MIXPIX'", "'mixpix'", "'ADDCT1'", "'addct1'", "'ADDCT2'", "'addct2'", 
			"'ADDCT3'", "'addct3'", "'WMLONG'", "'wmlong'", "'RQPIN'", "'rqpin'", 
			"'RDPIN'", "'rdpin'", "'RDLUT'", "'rdlut'", "'RDBYTE'", "'rdbyte'", "'RDWORD'", 
			"'rdword'", "'RDLONG'", "'rdlong'", "'POPA'", "'popa'", "'POPB'", "'popb'", 
			"'CALLD'", "'calld'", "'RESI3'", "'resi3'", "'RESI2'", "'resi2'", "'RESI1'", 
			"'resi1'", "'RESI0'", "'resi0'", "'RETI3'", "'reti3'", "'RETI2'", "'reti2'", 
			"'RETI1'", "'reti1'", "'RETI0'", "'reti0'", "'CALLPA'", "'callpa'", "'CALLPB'", 
			"'callpb'", "'DJZ'", "'djz'", "'DJNZ'", "'djnz'", "'DJF'", "'djf'", "'DJNF'", 
			"'djnf'", "'IJZ'", "'ijz'", "'IJNZ'", "'ijnz'", "'TJZ'", "'tjz'", "'TJNZ'", 
			"'tjnz'", "'TJF'", "'tjf'", "'TJNF'", "'tjnf'", "'TJS'", "'tjs'", "'TJNS'", 
			"'tjns'", "'TJV'", "'tjv'", "'JINT'", "'jint'", "'JCT1'", "'jct1'", "'JCT2'", 
			"'jct2'", "'JCT3'", "'jct3'", "'JSE1'", "'jse1'", "'JSE2'", "'jse2'", 
			"'JSE3'", "'jse3'", "'JSE4'", "'jse4'", "'JPAT'", "'jpat'", "'JFBW'", 
			"'jfbw'", "'JXMT'", "'jxmt'", "'JXFI'", "'jxfi'", "'JXRO'", "'jxro'", 
			"'JXRL'", "'jxrl'", "'JATN'", "'jatn'", "'JQMT'", "'jqmt'", "'JNINT'", 
			"'jnint'", "'JNCT1'", "'jnct1'", "'JNCT2'", "'jnct2'", "'JNCT3'", "'jnct3'", 
			"'JNSE1'", "'jnse1'", "'JNSE2'", "'jnse2'", "'JNSE3'", "'jnse3'", "'JNSE4'", 
			"'jnse4'", "'JNPAT'", "'jnpat'", "'JNFBW'", "'jnfbw'", "'JNXMT'", "'jnxmt'", 
			"'JNXFI'", "'jnxfi'", "'JNXRO'", "'jnxro'", "'JNXRL'", "'jnxrl'", "'JNATN'", 
			"'jnatn'", "'JNQMT'", "'jnqmt'", "'SETPAT'", "'setpat'", "'AKPIN'", "'akpin'", 
			"'WRPIN'", "'wrpin'", "'WXPIN'", "'wxpin'", "'WYPIN'", "'wypin'", "'WRLUT'", 
			"'wrlut'", "'WRBYTE'", "'wrbyte'", "'WRWORD'", "'wrword'", "'WRLONG'", 
			"'wrlong'", "'PUSHA'", "'pusha'", "'PUSHB'", "'pushb'", "'RDFAST'", "'rdfast'", 
			"'WRFAST'", "'wrfast'", "'FBLOCK'", "'fblock'", "'XINIT'", "'xinit'", 
			"'XSTOP'", "'xstop'", "'XZERO'", "'xzero'", "'XCONT'", "'xcont'", "'REP'", 
			"'rep'", "'COGINIT'", "'coginit'", "'QMUL'", "'qmul'", "'QDIV'", "'qdiv'", 
			"'QFRAC'", "'qfrac'", "'QSQRT'", "'qsqrt'", "'QROTATE'", "'qrotate'", 
			"'QVECTOR'", "'qvector'", "'HUBSET'", "'hubset'", "'COGID'", "'cogid'", 
			"'COGSTOP'", "'cogstop'", "'LOCKNEW'", "'locknew'", "'LOCKRET'", "'lockret'", 
			"'LOCKTRY'", "'locktry'", "'LOCKREL'", "'lockrel'", "'QLOG'", "'qlog'", 
			"'QEXP'", "'qexp'", "'RFBYTE'", "'rfbyte'", "'RFWORD'", "'rfword'", "'RFLONG'", 
			"'rflong'", "'RFVAR'", "'rfvar'", "'RFVARS'", "'rfvars'", "'WFBYTE'", 
			"'wfbyte'", "'WFWORD'", "'wfword'", "'WFLONG'", "'wflong'", "'GETQX'", 
			"'getqx'", "'GETQY'", "'getqy'", "'GETCT'", "'getct'", "'GETRND'", "'getrnd'", 
			"'SETDACS'", "'setdacs'", "'SETXFRQ'", "'setxfrq'", "'GETXACC'", "'getxacc'", 
			"'WAITX'", "'waitx'", "'SETSE1'", "'setse1'", "'SETSE2'", "'setse2'", 
			"'SETSE3'", "'setse3'", "'SETSE4'", "'setse4'", "'POLLINT'", "'pollint'", 
			"'POLLCT1'", "'pollct1'", "'POLLCT2'", "'pollct2'", "'POLLCT3'", "'pollct3'", 
			"'POLLSE1'", "'pollse1'", "'POLLSE2'", "'pollse2'", "'POLLSE3'", "'pollse3'", 
			"'POLLSE4'", "'pollse4'", "'POLLPAT'", "'pollpat'", "'POLLFBW'", "'pollfbw'", 
			"'POLLXMT'", "'pollxmt'", "'POLLXFI'", "'pollxfi'", "'POLLXRO'", "'pollxro'", 
			"'POLLXRL'", "'pollxrl'", "'POLLATN'", "'pollatn'", "'POLLQMT'", "'pollqmt'", 
			"'WAITINT'", "'waitint'", "'WAITCT1'", "'waitct1'", "'WAITCT2'", "'waitct2'", 
			"'WAITCT3'", "'waitct3'", "'WAITSE1'", "'waitse1'", "'WAITSE2'", "'waitse2'", 
			"'WAITSE3'", "'waitse3'", "'WAITSE4'", "'waitse4'", "'WAITPAT'", "'waitpat'", 
			"'WAITFBW'", "'waitfbw'", "'WAITXMT'", "'waitxmt'", "'WAITXFI'", "'waitxfi'", 
			"'WAITXRO'", "'waitxro'", "'WAITXRL'", "'waitxrl'", "'WAITATN'", "'waitatn'", 
			"'ALLOWI'", "'allowi'", "'STALLI'", "'stalli'", "'TRGINT1'", "'trgint1'", 
			"'TRGINT2'", "'trgint2'", "'TRGINT3'", "'trgint3'", "'NIXINT1'", "'nixint1'", 
			"'NIXINT2'", "'nixint2'", "'NIXINT3'", "'nixint3'", "'SETINT1'", "'setint1'", 
			"'SETINT2'", "'setint2'", "'SETINT3'", "'setint3'", "'SETQ'", "'setq'", 
			"'SETQ2'", "'setq2'", "'PUSH'", "'push'", "'POP'", "'pop'", "'JMP'", 
			"'jmp'", "'CALL'", "'call'", "'RET'", "'ret'", "'CALLA'", "'calla'", 
			"'RETA'", "'reta'", "'CALLB'", "'callb'", "'RETB'", "'retb'", "'JMPREL'", 
			"'jmprel'", "'SKIP'", "'skip'", "'SKIPF'", "'skipf'", "'EXECF'", "'execf'", 
			"'GETPTR'", "'getptr'", "'GETBRK'", "'getbrk'", "'COGBRK'", "'cogbrk'", 
			"'BRK'", "'brk'", "'SETLUTS'", "'setluts'", "'SETCY'", "'setcy'", "'SETCI'", 
			"'setci'", "'SETCQ'", "'setcq'", "'SETCFRQ'", "'setcfrq'", "'SETCMOD'", 
			"'setcmod'", "'SETPIV'", "'setpiv'", "'SETPIX'", "'setpix'", "'COGATN'", 
			"'cogatn'", "'TESTP'", "'testp'", "'TESTPN'", "'testpn'", "'DIRL'", "'dirl'", 
			"'DIRH'", "'dirh'", "'DIRC'", "'dirc'", "'DIRNC'", "'dirnc'", "'DIRZ'", 
			"'dirz'", "'DIRNZ'", "'dirnz'", "'DIRRND'", "'dirrnd'", "'DIRNOT'", "'dirnot'", 
			"'OUTL'", "'outl'", "'OUTH'", "'outh'", "'OUTC'", "'outc'", "'OUTNC'", 
			"'outnc'", "'OUTZ'", "'outz'", "'OUTNZ'", "'outnz'", "'OUTRND'", "'outrnd'", 
			"'OUTNOT'", "'outnot'", "'FLTL'", "'fltl'", "'FLTH'", "'flth'", "'FLTC'", 
			"'fltc'", "'FLTNC'", "'fltnc'", "'FLTZ'", "'fltz'", "'FLTNZ'", "'fltnz'", 
			"'FLTRND'", "'fltrnd'", "'FLTNOT'", "'fltnot'", "'DRVL'", "'drvl'", "'DRVH'", 
			"'drvh'", "'DRVC'", "'drvc'", "'DRVNC'", "'drvnc'", "'DRVZ'", "'drvz'", 
			"'DRVNZ'", "'drvnz'", "'DRVRND'", "'drvrnd'", "'DRVNOT'", "'drvnot'", 
			"'SPLITB'", "'splitb'", "'MERGEB'", "'mergeb'", "'SPLITW'", "'splitw'", 
			"'MERGEW'", "'mergew'", "'SEUSSF'", "'seussf'", "'SEUSSR'", "'seussr'", 
			"'RGBSQZ'", "'rgbsqz'", "'RGBEXP'", "'rgbexp'", "'XORO32'", "'xoro32'", 
			"'REV'", "'rev'", "'RCZR'", "'rczr'", "'RCZL'", "'rczl'", "'WRC'", "'wrc'", 
			"'WRNC'", "'wrnc'", "'WRZ'", "'wrz'", "'WRNZ'", "'wrnz'", "'MODCZ'", 
			"'modcz'", "'MODC'", "'modc'", "'MODZ'", "'modz'", "'SETSCP'", "'setscp'", 
			"'GETSCP'", "'getscp'", "'LOC'", "'loc'", "'AUGS'", "'augs'", "'AUGD'", 
			"'augd'", "'ASMCLK'", "'asmclk'", "'_RET_'", "'_ret_'", "'IF_NC_AND_NZ'", 
			"'if_nc_and_nz'", "'IF_NZ_AND_NC'", "'if_nz_and_nc'", "'IF_GT'", "'if_gt'", 
			"'IF_A'", "'if_a'", "'IF_00'", "'if_00'", "'IF_NC_AND_Z'", "'if_nc_and_z'", 
			"'IF_Z_AND_NC'", "'if_z_and_nc'", "'IF_01'", "'if_01'", "'IF_NC'", "'if_nc'", 
			"'IF_GE'", "'if_ge'", "'IF_AE'", "'if_ae'", "'IF_0X'", "'if_0x'", "'IF_C_AND_NZ'", 
			"'if_c_and_nz'", "'IF_NZ_AND_C'", "'if_nz_and_c'", "'IF_10'", "'if_10'", 
			"'IF_NZ'", "'if_nz'", "'IF_NE'", "'if_ne'", "'IF_X0'", "'if_x0'", "'IF_C_NE_Z'", 
			"'if_c_ne_z'", "'IF_Z_NE_C'", "'if_z_ne_c'", "'IF_DIFF'", "'if_diff'", 
			"'IF_NC_OR_NZ'", "'if_nc_or_nz'", "'IF_NZ_OR_NC'", "'if_nz_or_nc'", "'IF_NOT_11'", 
			"'if_not_11'", "'IF_C_AND_Z'", "'if_c_and_z'", "'IF_Z_AND_C'", "'if_z_and_c'", 
			"'IF_11'", "'if_11'", "'IF_C_EQ_Z'", "'if_c_eq_z'", "'IF_Z_EQ_C'", "'if_z_eq_c'", 
			"'IF_SAME'", "'if_same'", "'IF_Z'", "'if_z'", "'IF_E'", "'if_e'", "'IF_X1'", 
			"'if_x1'", "'IF_NC_OR_Z'", "'if_nc_or_z'", "'IF_Z_OR_NC'", "'if_z_or_nc'", 
			"'IF_NOT_10'", "'if_not_10'", "'IF_C'", "'if_c'", "'IF_LT'", "'if_lt'", 
			"'IF_B'", "'if_b'", "'IF_1X'", "'if_1x'", "'IF_C_OR_NZ'", "'if_c_or_nz'", 
			"'IF_NZ_OR_C'", "'if_nz_or_c'", "'IF_NOT_01'", "'if_not_01'", "'IF_C_OR_Z'", 
			"'if_c_or_z'", "'IF_Z_OR_C'", "'if_z_or_c'", "'IF_LE'", "'if_le'", "'IF_BE'", 
			"'if_be'", "'IF_NOT_00'", "'if_not_00'", "'WC'", "'WZ'", "'wc'", "'wz'", 
			"'WCZ'", "'wcz'", "'ANDC'", "'andc'", "'ANDZ'", "'andz'", "'ORC'", "'orc'", 
			"'ORZ'", "'orz'", "'XORC'", "'xorc'", "'XORZ'", "'xorz'", "'##'", "'\\'", 
			"'+'", "'-'", "'!!'", "'!'", "'~'", "'RMASK'", "'rmask'", "'SQRT'", "'sqrt'", 
			"'>>'", "'<<'", "'&'", "'^'", "'|'", "'*'", "'/'", "'+/'", "'//'", "'+//'", 
			"'frac'", "'#>'", "'<#'", "'ADDBITS'", "'addbits'", "'ADDPINS'", "'addpins'", 
			"'<'", "'+<'", "'<='", "'+<='", "'=='", "'<>'", "'>='", "'+>='", "'>'", 
			"'+>'", "'<=>'", "'&&'", "'^^'", "'||'", "'?'", "'FLOAT'", "'float'", 
			"'ROUND'", "'round'", "'TRUNC'", "'trunc'", "'('", "')'", "'@'", "'$'", 
			"'++'", "'--'"
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
			null, null, null, null, null, null, null, "BLOCK_COMMENT", "COMMENT", 
			"PTR", "VARIABLE", "STRING", "QUAD", "BIN", "HEX", "NUMBER", "NL", "WS"
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
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(46);
				match(NL);
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__7) | (1L << T__8) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) {
				{
				setState(56);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case T__1:
					{
					setState(52);
					constants();
					}
					break;
				case T__7:
				case T__8:
					{
					setState(53);
					objects();
					}
					break;
				case T__10:
				case T__11:
					{
					setState(54);
					variables();
					}
					break;
				case T__12:
				case T__13:
					{
					setState(55);
					data();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(60);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61);
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
			setState(64); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(63);
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
				setState(66); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2 || _la==T__6 || _la==VARIABLE) {
				{
				{
				setState(74);
				constant();
				}
				}
				setState(79);
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
			setState(122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(80);
					match(T__2);
					}
				}

				setState(83);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(84);
				match(T__3);
				setState(85);
				((ConstantContext)_localctx).exp = expression(0);
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
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(92);
					match(T__2);
					}
				}

				setState(95);
				((ConstantContext)_localctx).name = match(VARIABLE);
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(96);
					match(T__4);
					setState(97);
					((ConstantContext)_localctx).multiplier = expression(0);
					setState(98);
					match(T__5);
					}
				}

				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(102);
					match(NL);
					}
					}
					setState(107);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(108);
				match(T__6);
				setState(109);
				((ConstantContext)_localctx).start = expression(0);
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(110);
					match(T__4);
					setState(111);
					((ConstantContext)_localctx).step = expression(0);
					setState(112);
					match(T__5);
					}
				}

				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(116);
					match(NL);
					}
					}
					setState(121);
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
			setState(125); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(124);
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
				setState(127); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(129);
				match(NL);
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VARIABLE) {
				{
				{
				setState(135);
				object();
				}
				}
				setState(140);
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
			setState(141);
			reference();
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(142);
				match(T__4);
				setState(143);
				expression(0);
				setState(144);
				match(T__5);
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(151);
			match(T__9);
			setState(152);
			filename();
			setState(154); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(153);
				match(NL);
				}
				}
				setState(156); 
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
			setState(158);
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
			setState(160);
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
			setState(163); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(162);
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
				setState(165); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(167);
				match(NL);
				}
				}
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0) || _la==VARIABLE) {
				{
				{
				setState(173);
				variable();
				}
				}
				setState(178);
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
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(179);
				type();
				}
			}

			setState(182);
			match(VARIABLE);
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(183);
				match(T__4);
				setState(184);
				expression(0);
				setState(185);
				match(T__5);
				}
			}

			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(189);
				match(T__2);
				setState(190);
				match(VARIABLE);
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(191);
					match(T__4);
					setState(192);
					expression(0);
					setState(193);
					match(T__5);
					}
				}

				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(202);
				match(NL);
				}
				}
				setState(205); 
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
			setState(208); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(207);
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
				setState(210); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(215);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(212);
					match(NL);
					}
					} 
				}
				setState(217);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			}
			setState(222);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(220);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						setState(218);
						directive();
						}
						break;
					case 2:
						{
						setState(219);
						dataLine();
						}
						break;
					}
					} 
				}
				setState(224);
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
		enterRule(_localctx, 20, RULE_dataLine);
		int _la;
		try {
			int _alt;
			setState(317);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				label();
				setState(227); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(226);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(229); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(232);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(231);
					label();
					}
					break;
				}
				setState(235);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(234);
					condition();
					}
					break;
				}
				setState(237);
				opcode();
				setState(238);
				argument();
				setState(239);
				match(T__2);
				setState(240);
				argument();
				setState(241);
				match(T__2);
				setState(242);
				argument();
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 846)) & ~0x3f) == 0 && ((1L << (_la - 846)) & ((1L << (T__845 - 846)) | (1L << (T__846 - 846)) | (1L << (T__847 - 846)) | (1L << (T__848 - 846)) | (1L << (T__849 - 846)) | (1L << (T__850 - 846)) | (1L << (T__851 - 846)) | (1L << (T__852 - 846)) | (1L << (T__853 - 846)) | (1L << (T__854 - 846)) | (1L << (T__855 - 846)) | (1L << (T__856 - 846)) | (1L << (T__857 - 846)) | (1L << (T__858 - 846)) | (1L << (T__859 - 846)) | (1L << (T__860 - 846)) | (1L << (T__861 - 846)) | (1L << (T__862 - 846)))) != 0)) {
					{
					setState(243);
					effect();
					}
				}

				setState(247); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(246);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(249); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(252);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(251);
					label();
					}
					break;
				}
				setState(255);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(254);
					condition();
					}
					break;
				}
				setState(257);
				opcode();
				setState(258);
				argument();
				setState(259);
				match(T__2);
				setState(260);
				argument();
				setState(262);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 846)) & ~0x3f) == 0 && ((1L << (_la - 846)) & ((1L << (T__845 - 846)) | (1L << (T__846 - 846)) | (1L << (T__847 - 846)) | (1L << (T__848 - 846)) | (1L << (T__849 - 846)) | (1L << (T__850 - 846)) | (1L << (T__851 - 846)) | (1L << (T__852 - 846)) | (1L << (T__853 - 846)) | (1L << (T__854 - 846)) | (1L << (T__855 - 846)) | (1L << (T__856 - 846)) | (1L << (T__857 - 846)) | (1L << (T__858 - 846)) | (1L << (T__859 - 846)) | (1L << (T__860 - 846)) | (1L << (T__861 - 846)) | (1L << (T__862 - 846)))) != 0)) {
					{
					setState(261);
					effect();
					}
				}

				setState(265); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(264);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(267); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(270);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
				case 1:
					{
					setState(269);
					label();
					}
					break;
				}
				setState(273);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					setState(272);
					condition();
					}
					break;
				}
				setState(275);
				opcode();
				setState(276);
				argument();
				setState(278);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 846)) & ~0x3f) == 0 && ((1L << (_la - 846)) & ((1L << (T__845 - 846)) | (1L << (T__846 - 846)) | (1L << (T__847 - 846)) | (1L << (T__848 - 846)) | (1L << (T__849 - 846)) | (1L << (T__850 - 846)) | (1L << (T__851 - 846)) | (1L << (T__852 - 846)) | (1L << (T__853 - 846)) | (1L << (T__854 - 846)) | (1L << (T__855 - 846)) | (1L << (T__856 - 846)) | (1L << (T__857 - 846)) | (1L << (T__858 - 846)) | (1L << (T__859 - 846)) | (1L << (T__860 - 846)) | (1L << (T__861 - 846)) | (1L << (T__862 - 846)))) != 0)) {
					{
					setState(277);
					effect();
					}
				}

				setState(281); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(280);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(283); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(286);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
				case 1:
					{
					setState(285);
					label();
					}
					break;
				}
				setState(289);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(288);
					condition();
					}
					break;
				}
				setState(291);
				opcode();
				setState(293);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 846)) & ~0x3f) == 0 && ((1L << (_la - 846)) & ((1L << (T__845 - 846)) | (1L << (T__846 - 846)) | (1L << (T__847 - 846)) | (1L << (T__848 - 846)) | (1L << (T__849 - 846)) | (1L << (T__850 - 846)) | (1L << (T__851 - 846)) | (1L << (T__852 - 846)) | (1L << (T__853 - 846)) | (1L << (T__854 - 846)) | (1L << (T__855 - 846)) | (1L << (T__856 - 846)) | (1L << (T__857 - 846)) | (1L << (T__858 - 846)) | (1L << (T__859 - 846)) | (1L << (T__860 - 846)) | (1L << (T__861 - 846)) | (1L << (T__862 - 846)))) != 0)) {
					{
					setState(292);
					effect();
					}
				}

				setState(296); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(295);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(298); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(301);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
				case 1:
					{
					setState(300);
					label();
					}
					break;
				}
				setState(303);
				directive();
				setState(304);
				dataValue();
				setState(309);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(305);
					match(T__2);
					setState(306);
					dataValue();
					}
					}
					setState(311);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(313); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(312);
						match(NL);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(315); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
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
			setState(319);
			((TypeValueContext)_localctx).value = expression(0);
			setState(324);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(320);
				match(T__4);
				setState(321);
				((TypeValueContext)_localctx).count = expression(0);
				setState(322);
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
		enterRule(_localctx, 24, RULE_dataValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			expression(0);
			setState(331);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(327);
				match(T__4);
				setState(328);
				((DataValueContext)_localctx).count = expression(0);
				setState(329);
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
		enterRule(_localctx, 26, RULE_directive);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21))) != 0)) ) {
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
		enterRule(_localctx, 28, RULE_label);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			if (!(_input.LT(1).getCharPositionInLine() == 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() == 0");
			setState(337);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(336);
				match(T__22);
				}
			}

			setState(339);
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
		enterRule(_localctx, 30, RULE_opcode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << T__41) | (1L << T__42) | (1L << T__43) | (1L << T__44) | (1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49) | (1L << T__50) | (1L << T__51) | (1L << T__52) | (1L << T__53) | (1L << T__54) | (1L << T__55) | (1L << T__56) | (1L << T__57) | (1L << T__58) | (1L << T__59) | (1L << T__60) | (1L << T__61) | (1L << T__62))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (T__63 - 64)) | (1L << (T__64 - 64)) | (1L << (T__65 - 64)) | (1L << (T__66 - 64)) | (1L << (T__67 - 64)) | (1L << (T__68 - 64)) | (1L << (T__69 - 64)) | (1L << (T__70 - 64)) | (1L << (T__71 - 64)) | (1L << (T__72 - 64)) | (1L << (T__73 - 64)) | (1L << (T__74 - 64)) | (1L << (T__75 - 64)) | (1L << (T__76 - 64)) | (1L << (T__77 - 64)) | (1L << (T__78 - 64)) | (1L << (T__79 - 64)) | (1L << (T__80 - 64)) | (1L << (T__81 - 64)) | (1L << (T__82 - 64)) | (1L << (T__83 - 64)) | (1L << (T__84 - 64)) | (1L << (T__85 - 64)) | (1L << (T__86 - 64)) | (1L << (T__87 - 64)) | (1L << (T__88 - 64)) | (1L << (T__89 - 64)) | (1L << (T__90 - 64)) | (1L << (T__91 - 64)) | (1L << (T__92 - 64)) | (1L << (T__93 - 64)) | (1L << (T__94 - 64)) | (1L << (T__95 - 64)) | (1L << (T__96 - 64)) | (1L << (T__97 - 64)) | (1L << (T__98 - 64)) | (1L << (T__99 - 64)) | (1L << (T__100 - 64)) | (1L << (T__101 - 64)) | (1L << (T__102 - 64)) | (1L << (T__103 - 64)) | (1L << (T__104 - 64)) | (1L << (T__105 - 64)) | (1L << (T__106 - 64)) | (1L << (T__107 - 64)) | (1L << (T__108 - 64)) | (1L << (T__109 - 64)) | (1L << (T__110 - 64)) | (1L << (T__111 - 64)) | (1L << (T__112 - 64)) | (1L << (T__113 - 64)) | (1L << (T__114 - 64)) | (1L << (T__115 - 64)) | (1L << (T__116 - 64)) | (1L << (T__117 - 64)) | (1L << (T__118 - 64)) | (1L << (T__119 - 64)) | (1L << (T__120 - 64)) | (1L << (T__121 - 64)) | (1L << (T__122 - 64)) | (1L << (T__123 - 64)) | (1L << (T__124 - 64)) | (1L << (T__125 - 64)) | (1L << (T__126 - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (T__127 - 128)) | (1L << (T__128 - 128)) | (1L << (T__129 - 128)) | (1L << (T__130 - 128)) | (1L << (T__131 - 128)) | (1L << (T__132 - 128)) | (1L << (T__133 - 128)) | (1L << (T__134 - 128)) | (1L << (T__135 - 128)) | (1L << (T__136 - 128)) | (1L << (T__137 - 128)) | (1L << (T__138 - 128)) | (1L << (T__139 - 128)) | (1L << (T__140 - 128)) | (1L << (T__141 - 128)) | (1L << (T__142 - 128)) | (1L << (T__143 - 128)) | (1L << (T__144 - 128)) | (1L << (T__145 - 128)) | (1L << (T__146 - 128)) | (1L << (T__147 - 128)) | (1L << (T__148 - 128)) | (1L << (T__149 - 128)) | (1L << (T__150 - 128)) | (1L << (T__151 - 128)) | (1L << (T__152 - 128)) | (1L << (T__153 - 128)) | (1L << (T__154 - 128)) | (1L << (T__155 - 128)) | (1L << (T__156 - 128)) | (1L << (T__157 - 128)) | (1L << (T__158 - 128)) | (1L << (T__159 - 128)) | (1L << (T__160 - 128)) | (1L << (T__161 - 128)) | (1L << (T__162 - 128)) | (1L << (T__163 - 128)) | (1L << (T__164 - 128)) | (1L << (T__165 - 128)) | (1L << (T__166 - 128)) | (1L << (T__167 - 128)) | (1L << (T__168 - 128)) | (1L << (T__169 - 128)) | (1L << (T__170 - 128)) | (1L << (T__171 - 128)) | (1L << (T__172 - 128)) | (1L << (T__173 - 128)) | (1L << (T__174 - 128)) | (1L << (T__175 - 128)) | (1L << (T__176 - 128)) | (1L << (T__177 - 128)) | (1L << (T__178 - 128)) | (1L << (T__179 - 128)) | (1L << (T__180 - 128)) | (1L << (T__181 - 128)) | (1L << (T__182 - 128)) | (1L << (T__183 - 128)) | (1L << (T__184 - 128)) | (1L << (T__185 - 128)) | (1L << (T__186 - 128)) | (1L << (T__187 - 128)) | (1L << (T__188 - 128)) | (1L << (T__189 - 128)) | (1L << (T__190 - 128)))) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & ((1L << (T__191 - 192)) | (1L << (T__192 - 192)) | (1L << (T__193 - 192)) | (1L << (T__194 - 192)) | (1L << (T__195 - 192)) | (1L << (T__196 - 192)) | (1L << (T__197 - 192)) | (1L << (T__198 - 192)) | (1L << (T__199 - 192)) | (1L << (T__200 - 192)) | (1L << (T__201 - 192)) | (1L << (T__202 - 192)) | (1L << (T__203 - 192)) | (1L << (T__204 - 192)) | (1L << (T__205 - 192)) | (1L << (T__206 - 192)) | (1L << (T__207 - 192)) | (1L << (T__208 - 192)) | (1L << (T__209 - 192)) | (1L << (T__210 - 192)) | (1L << (T__211 - 192)) | (1L << (T__212 - 192)) | (1L << (T__213 - 192)) | (1L << (T__214 - 192)) | (1L << (T__215 - 192)) | (1L << (T__216 - 192)) | (1L << (T__217 - 192)) | (1L << (T__218 - 192)) | (1L << (T__219 - 192)) | (1L << (T__220 - 192)) | (1L << (T__221 - 192)) | (1L << (T__222 - 192)) | (1L << (T__223 - 192)) | (1L << (T__224 - 192)) | (1L << (T__225 - 192)) | (1L << (T__226 - 192)) | (1L << (T__227 - 192)) | (1L << (T__228 - 192)) | (1L << (T__229 - 192)) | (1L << (T__230 - 192)) | (1L << (T__231 - 192)) | (1L << (T__232 - 192)) | (1L << (T__233 - 192)) | (1L << (T__234 - 192)) | (1L << (T__235 - 192)) | (1L << (T__236 - 192)) | (1L << (T__237 - 192)) | (1L << (T__238 - 192)) | (1L << (T__239 - 192)) | (1L << (T__240 - 192)) | (1L << (T__241 - 192)) | (1L << (T__242 - 192)) | (1L << (T__243 - 192)) | (1L << (T__244 - 192)) | (1L << (T__245 - 192)) | (1L << (T__246 - 192)) | (1L << (T__247 - 192)) | (1L << (T__248 - 192)) | (1L << (T__249 - 192)) | (1L << (T__250 - 192)) | (1L << (T__251 - 192)) | (1L << (T__252 - 192)) | (1L << (T__253 - 192)) | (1L << (T__254 - 192)))) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & ((1L << (T__255 - 256)) | (1L << (T__256 - 256)) | (1L << (T__257 - 256)) | (1L << (T__258 - 256)) | (1L << (T__259 - 256)) | (1L << (T__260 - 256)) | (1L << (T__261 - 256)) | (1L << (T__262 - 256)) | (1L << (T__263 - 256)) | (1L << (T__264 - 256)) | (1L << (T__265 - 256)) | (1L << (T__266 - 256)) | (1L << (T__267 - 256)) | (1L << (T__268 - 256)) | (1L << (T__269 - 256)) | (1L << (T__270 - 256)) | (1L << (T__271 - 256)) | (1L << (T__272 - 256)) | (1L << (T__273 - 256)) | (1L << (T__274 - 256)) | (1L << (T__275 - 256)) | (1L << (T__276 - 256)) | (1L << (T__277 - 256)) | (1L << (T__278 - 256)) | (1L << (T__279 - 256)) | (1L << (T__280 - 256)) | (1L << (T__281 - 256)) | (1L << (T__282 - 256)) | (1L << (T__283 - 256)) | (1L << (T__284 - 256)) | (1L << (T__285 - 256)) | (1L << (T__286 - 256)) | (1L << (T__287 - 256)) | (1L << (T__288 - 256)) | (1L << (T__289 - 256)) | (1L << (T__290 - 256)) | (1L << (T__291 - 256)) | (1L << (T__292 - 256)) | (1L << (T__293 - 256)) | (1L << (T__294 - 256)) | (1L << (T__295 - 256)) | (1L << (T__296 - 256)) | (1L << (T__297 - 256)) | (1L << (T__298 - 256)) | (1L << (T__299 - 256)) | (1L << (T__300 - 256)) | (1L << (T__301 - 256)) | (1L << (T__302 - 256)) | (1L << (T__303 - 256)) | (1L << (T__304 - 256)) | (1L << (T__305 - 256)) | (1L << (T__306 - 256)) | (1L << (T__307 - 256)) | (1L << (T__308 - 256)) | (1L << (T__309 - 256)) | (1L << (T__310 - 256)) | (1L << (T__311 - 256)) | (1L << (T__312 - 256)) | (1L << (T__313 - 256)) | (1L << (T__314 - 256)) | (1L << (T__315 - 256)) | (1L << (T__316 - 256)) | (1L << (T__317 - 256)) | (1L << (T__318 - 256)))) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & ((1L << (T__319 - 320)) | (1L << (T__320 - 320)) | (1L << (T__321 - 320)) | (1L << (T__322 - 320)) | (1L << (T__323 - 320)) | (1L << (T__324 - 320)) | (1L << (T__325 - 320)) | (1L << (T__326 - 320)) | (1L << (T__327 - 320)) | (1L << (T__328 - 320)) | (1L << (T__329 - 320)) | (1L << (T__330 - 320)) | (1L << (T__331 - 320)) | (1L << (T__332 - 320)) | (1L << (T__333 - 320)) | (1L << (T__334 - 320)) | (1L << (T__335 - 320)) | (1L << (T__336 - 320)) | (1L << (T__337 - 320)) | (1L << (T__338 - 320)) | (1L << (T__339 - 320)) | (1L << (T__340 - 320)) | (1L << (T__341 - 320)) | (1L << (T__342 - 320)) | (1L << (T__343 - 320)) | (1L << (T__344 - 320)) | (1L << (T__345 - 320)) | (1L << (T__346 - 320)) | (1L << (T__347 - 320)) | (1L << (T__348 - 320)) | (1L << (T__349 - 320)) | (1L << (T__350 - 320)) | (1L << (T__351 - 320)) | (1L << (T__352 - 320)) | (1L << (T__353 - 320)) | (1L << (T__354 - 320)) | (1L << (T__355 - 320)) | (1L << (T__356 - 320)) | (1L << (T__357 - 320)) | (1L << (T__358 - 320)) | (1L << (T__359 - 320)) | (1L << (T__360 - 320)) | (1L << (T__361 - 320)) | (1L << (T__362 - 320)) | (1L << (T__363 - 320)) | (1L << (T__364 - 320)) | (1L << (T__365 - 320)) | (1L << (T__366 - 320)) | (1L << (T__367 - 320)) | (1L << (T__368 - 320)) | (1L << (T__369 - 320)) | (1L << (T__370 - 320)) | (1L << (T__371 - 320)) | (1L << (T__372 - 320)) | (1L << (T__373 - 320)) | (1L << (T__374 - 320)) | (1L << (T__375 - 320)) | (1L << (T__376 - 320)) | (1L << (T__377 - 320)) | (1L << (T__378 - 320)) | (1L << (T__379 - 320)) | (1L << (T__380 - 320)) | (1L << (T__381 - 320)) | (1L << (T__382 - 320)))) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & ((1L << (T__383 - 384)) | (1L << (T__384 - 384)) | (1L << (T__385 - 384)) | (1L << (T__386 - 384)) | (1L << (T__387 - 384)) | (1L << (T__388 - 384)) | (1L << (T__389 - 384)) | (1L << (T__390 - 384)) | (1L << (T__391 - 384)) | (1L << (T__392 - 384)) | (1L << (T__393 - 384)) | (1L << (T__394 - 384)) | (1L << (T__395 - 384)) | (1L << (T__396 - 384)) | (1L << (T__397 - 384)) | (1L << (T__398 - 384)) | (1L << (T__399 - 384)) | (1L << (T__400 - 384)) | (1L << (T__401 - 384)) | (1L << (T__402 - 384)) | (1L << (T__403 - 384)) | (1L << (T__404 - 384)) | (1L << (T__405 - 384)) | (1L << (T__406 - 384)) | (1L << (T__407 - 384)) | (1L << (T__408 - 384)) | (1L << (T__409 - 384)) | (1L << (T__410 - 384)) | (1L << (T__411 - 384)) | (1L << (T__412 - 384)) | (1L << (T__413 - 384)) | (1L << (T__414 - 384)) | (1L << (T__415 - 384)) | (1L << (T__416 - 384)) | (1L << (T__417 - 384)) | (1L << (T__418 - 384)) | (1L << (T__419 - 384)) | (1L << (T__420 - 384)) | (1L << (T__421 - 384)) | (1L << (T__422 - 384)) | (1L << (T__423 - 384)) | (1L << (T__424 - 384)) | (1L << (T__425 - 384)) | (1L << (T__426 - 384)) | (1L << (T__427 - 384)) | (1L << (T__428 - 384)) | (1L << (T__429 - 384)) | (1L << (T__430 - 384)) | (1L << (T__431 - 384)) | (1L << (T__432 - 384)) | (1L << (T__433 - 384)) | (1L << (T__434 - 384)) | (1L << (T__435 - 384)) | (1L << (T__436 - 384)) | (1L << (T__437 - 384)) | (1L << (T__438 - 384)) | (1L << (T__439 - 384)) | (1L << (T__440 - 384)) | (1L << (T__441 - 384)) | (1L << (T__442 - 384)) | (1L << (T__443 - 384)) | (1L << (T__444 - 384)) | (1L << (T__445 - 384)) | (1L << (T__446 - 384)))) != 0) || ((((_la - 448)) & ~0x3f) == 0 && ((1L << (_la - 448)) & ((1L << (T__447 - 448)) | (1L << (T__448 - 448)) | (1L << (T__449 - 448)) | (1L << (T__450 - 448)) | (1L << (T__451 - 448)) | (1L << (T__452 - 448)) | (1L << (T__453 - 448)) | (1L << (T__454 - 448)) | (1L << (T__455 - 448)) | (1L << (T__456 - 448)) | (1L << (T__457 - 448)) | (1L << (T__458 - 448)) | (1L << (T__459 - 448)) | (1L << (T__460 - 448)) | (1L << (T__461 - 448)) | (1L << (T__462 - 448)) | (1L << (T__463 - 448)) | (1L << (T__464 - 448)) | (1L << (T__465 - 448)) | (1L << (T__466 - 448)) | (1L << (T__467 - 448)) | (1L << (T__468 - 448)) | (1L << (T__469 - 448)) | (1L << (T__470 - 448)) | (1L << (T__471 - 448)) | (1L << (T__472 - 448)) | (1L << (T__473 - 448)) | (1L << (T__474 - 448)) | (1L << (T__475 - 448)) | (1L << (T__476 - 448)) | (1L << (T__477 - 448)) | (1L << (T__478 - 448)) | (1L << (T__479 - 448)) | (1L << (T__480 - 448)) | (1L << (T__481 - 448)) | (1L << (T__482 - 448)) | (1L << (T__483 - 448)) | (1L << (T__484 - 448)) | (1L << (T__485 - 448)) | (1L << (T__486 - 448)) | (1L << (T__487 - 448)) | (1L << (T__488 - 448)) | (1L << (T__489 - 448)) | (1L << (T__490 - 448)) | (1L << (T__491 - 448)) | (1L << (T__492 - 448)) | (1L << (T__493 - 448)) | (1L << (T__494 - 448)) | (1L << (T__495 - 448)) | (1L << (T__496 - 448)) | (1L << (T__497 - 448)) | (1L << (T__498 - 448)) | (1L << (T__499 - 448)) | (1L << (T__500 - 448)) | (1L << (T__501 - 448)) | (1L << (T__502 - 448)) | (1L << (T__503 - 448)) | (1L << (T__504 - 448)) | (1L << (T__505 - 448)) | (1L << (T__506 - 448)) | (1L << (T__507 - 448)) | (1L << (T__508 - 448)) | (1L << (T__509 - 448)) | (1L << (T__510 - 448)))) != 0) || ((((_la - 512)) & ~0x3f) == 0 && ((1L << (_la - 512)) & ((1L << (T__511 - 512)) | (1L << (T__512 - 512)) | (1L << (T__513 - 512)) | (1L << (T__514 - 512)) | (1L << (T__515 - 512)) | (1L << (T__516 - 512)) | (1L << (T__517 - 512)) | (1L << (T__518 - 512)) | (1L << (T__519 - 512)) | (1L << (T__520 - 512)) | (1L << (T__521 - 512)) | (1L << (T__522 - 512)) | (1L << (T__523 - 512)) | (1L << (T__524 - 512)) | (1L << (T__525 - 512)) | (1L << (T__526 - 512)) | (1L << (T__527 - 512)) | (1L << (T__528 - 512)) | (1L << (T__529 - 512)) | (1L << (T__530 - 512)) | (1L << (T__531 - 512)) | (1L << (T__532 - 512)) | (1L << (T__533 - 512)) | (1L << (T__534 - 512)) | (1L << (T__535 - 512)) | (1L << (T__536 - 512)) | (1L << (T__537 - 512)) | (1L << (T__538 - 512)) | (1L << (T__539 - 512)) | (1L << (T__540 - 512)) | (1L << (T__541 - 512)) | (1L << (T__542 - 512)) | (1L << (T__543 - 512)) | (1L << (T__544 - 512)) | (1L << (T__545 - 512)) | (1L << (T__546 - 512)) | (1L << (T__547 - 512)) | (1L << (T__548 - 512)) | (1L << (T__549 - 512)) | (1L << (T__550 - 512)) | (1L << (T__551 - 512)) | (1L << (T__552 - 512)) | (1L << (T__553 - 512)) | (1L << (T__554 - 512)) | (1L << (T__555 - 512)) | (1L << (T__556 - 512)) | (1L << (T__557 - 512)) | (1L << (T__558 - 512)) | (1L << (T__559 - 512)) | (1L << (T__560 - 512)) | (1L << (T__561 - 512)) | (1L << (T__562 - 512)) | (1L << (T__563 - 512)) | (1L << (T__564 - 512)) | (1L << (T__565 - 512)) | (1L << (T__566 - 512)) | (1L << (T__567 - 512)) | (1L << (T__568 - 512)) | (1L << (T__569 - 512)) | (1L << (T__570 - 512)) | (1L << (T__571 - 512)) | (1L << (T__572 - 512)) | (1L << (T__573 - 512)) | (1L << (T__574 - 512)))) != 0) || ((((_la - 576)) & ~0x3f) == 0 && ((1L << (_la - 576)) & ((1L << (T__575 - 576)) | (1L << (T__576 - 576)) | (1L << (T__577 - 576)) | (1L << (T__578 - 576)) | (1L << (T__579 - 576)) | (1L << (T__580 - 576)) | (1L << (T__581 - 576)) | (1L << (T__582 - 576)) | (1L << (T__583 - 576)) | (1L << (T__584 - 576)) | (1L << (T__585 - 576)) | (1L << (T__586 - 576)) | (1L << (T__587 - 576)) | (1L << (T__588 - 576)) | (1L << (T__589 - 576)) | (1L << (T__590 - 576)) | (1L << (T__591 - 576)) | (1L << (T__592 - 576)) | (1L << (T__593 - 576)) | (1L << (T__594 - 576)) | (1L << (T__595 - 576)) | (1L << (T__596 - 576)) | (1L << (T__597 - 576)) | (1L << (T__598 - 576)) | (1L << (T__599 - 576)) | (1L << (T__600 - 576)) | (1L << (T__601 - 576)) | (1L << (T__602 - 576)) | (1L << (T__603 - 576)) | (1L << (T__604 - 576)) | (1L << (T__605 - 576)) | (1L << (T__606 - 576)) | (1L << (T__607 - 576)) | (1L << (T__608 - 576)) | (1L << (T__609 - 576)) | (1L << (T__610 - 576)) | (1L << (T__611 - 576)) | (1L << (T__612 - 576)) | (1L << (T__613 - 576)) | (1L << (T__614 - 576)) | (1L << (T__615 - 576)) | (1L << (T__616 - 576)) | (1L << (T__617 - 576)) | (1L << (T__618 - 576)) | (1L << (T__619 - 576)) | (1L << (T__620 - 576)) | (1L << (T__621 - 576)) | (1L << (T__622 - 576)) | (1L << (T__623 - 576)) | (1L << (T__624 - 576)) | (1L << (T__625 - 576)) | (1L << (T__626 - 576)) | (1L << (T__627 - 576)) | (1L << (T__628 - 576)) | (1L << (T__629 - 576)) | (1L << (T__630 - 576)) | (1L << (T__631 - 576)) | (1L << (T__632 - 576)) | (1L << (T__633 - 576)) | (1L << (T__634 - 576)) | (1L << (T__635 - 576)) | (1L << (T__636 - 576)) | (1L << (T__637 - 576)) | (1L << (T__638 - 576)))) != 0) || ((((_la - 640)) & ~0x3f) == 0 && ((1L << (_la - 640)) & ((1L << (T__639 - 640)) | (1L << (T__640 - 640)) | (1L << (T__641 - 640)) | (1L << (T__642 - 640)) | (1L << (T__643 - 640)) | (1L << (T__644 - 640)) | (1L << (T__645 - 640)) | (1L << (T__646 - 640)) | (1L << (T__647 - 640)) | (1L << (T__648 - 640)) | (1L << (T__649 - 640)) | (1L << (T__650 - 640)) | (1L << (T__651 - 640)) | (1L << (T__652 - 640)) | (1L << (T__653 - 640)) | (1L << (T__654 - 640)) | (1L << (T__655 - 640)) | (1L << (T__656 - 640)) | (1L << (T__657 - 640)) | (1L << (T__658 - 640)) | (1L << (T__659 - 640)) | (1L << (T__660 - 640)) | (1L << (T__661 - 640)) | (1L << (T__662 - 640)) | (1L << (T__663 - 640)) | (1L << (T__664 - 640)) | (1L << (T__665 - 640)) | (1L << (T__666 - 640)) | (1L << (T__667 - 640)) | (1L << (T__668 - 640)) | (1L << (T__669 - 640)) | (1L << (T__670 - 640)) | (1L << (T__671 - 640)) | (1L << (T__672 - 640)) | (1L << (T__673 - 640)) | (1L << (T__674 - 640)) | (1L << (T__675 - 640)) | (1L << (T__676 - 640)) | (1L << (T__677 - 640)) | (1L << (T__678 - 640)) | (1L << (T__679 - 640)) | (1L << (T__680 - 640)) | (1L << (T__681 - 640)) | (1L << (T__682 - 640)) | (1L << (T__683 - 640)) | (1L << (T__684 - 640)) | (1L << (T__685 - 640)) | (1L << (T__686 - 640)) | (1L << (T__687 - 640)) | (1L << (T__688 - 640)) | (1L << (T__689 - 640)) | (1L << (T__690 - 640)) | (1L << (T__691 - 640)) | (1L << (T__692 - 640)) | (1L << (T__693 - 640)) | (1L << (T__694 - 640)) | (1L << (T__695 - 640)) | (1L << (T__696 - 640)) | (1L << (T__697 - 640)) | (1L << (T__698 - 640)) | (1L << (T__699 - 640)) | (1L << (T__700 - 640)) | (1L << (T__701 - 640)) | (1L << (T__702 - 640)))) != 0) || ((((_la - 704)) & ~0x3f) == 0 && ((1L << (_la - 704)) & ((1L << (T__703 - 704)) | (1L << (T__704 - 704)) | (1L << (T__705 - 704)) | (1L << (T__706 - 704)) | (1L << (T__707 - 704)) | (1L << (T__708 - 704)) | (1L << (T__709 - 704)) | (1L << (T__710 - 704)) | (1L << (T__711 - 704)) | (1L << (T__712 - 704)) | (1L << (T__713 - 704)) | (1L << (T__714 - 704)) | (1L << (T__715 - 704)) | (1L << (T__716 - 704)) | (1L << (T__717 - 704)) | (1L << (T__718 - 704)) | (1L << (T__719 - 704)) | (1L << (T__720 - 704)) | (1L << (T__721 - 704)) | (1L << (T__722 - 704)) | (1L << (T__723 - 704)) | (1L << (T__724 - 704)) | (1L << (T__725 - 704)) | (1L << (T__726 - 704)) | (1L << (T__727 - 704)) | (1L << (T__728 - 704)) | (1L << (T__729 - 704)) | (1L << (T__730 - 704)) | (1L << (T__731 - 704)) | (1L << (T__732 - 704)) | (1L << (T__733 - 704)) | (1L << (T__734 - 704)) | (1L << (T__735 - 704)) | (1L << (T__736 - 704)) | (1L << (T__737 - 704)) | (1L << (T__738 - 704)) | (1L << (T__739 - 704)) | (1L << (T__740 - 704)) | (1L << (T__741 - 704)) | (1L << (T__742 - 704)) | (1L << (T__743 - 704)) | (1L << (T__744 - 704)) | (1L << (T__745 - 704)) | (1L << (T__746 - 704)))) != 0)) ) {
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
		enterRule(_localctx, 32, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			if (!(_input.LT(1).getCharPositionInLine() != 0)) throw new FailedPredicateException(this, "_input.LT(1).getCharPositionInLine() != 0");
			setState(344);
			_la = _input.LA(1);
			if ( !(((((_la - 748)) & ~0x3f) == 0 && ((1L << (_la - 748)) & ((1L << (T__747 - 748)) | (1L << (T__748 - 748)) | (1L << (T__749 - 748)) | (1L << (T__750 - 748)) | (1L << (T__751 - 748)) | (1L << (T__752 - 748)) | (1L << (T__753 - 748)) | (1L << (T__754 - 748)) | (1L << (T__755 - 748)) | (1L << (T__756 - 748)) | (1L << (T__757 - 748)) | (1L << (T__758 - 748)) | (1L << (T__759 - 748)) | (1L << (T__760 - 748)) | (1L << (T__761 - 748)) | (1L << (T__762 - 748)) | (1L << (T__763 - 748)) | (1L << (T__764 - 748)) | (1L << (T__765 - 748)) | (1L << (T__766 - 748)) | (1L << (T__767 - 748)) | (1L << (T__768 - 748)) | (1L << (T__769 - 748)) | (1L << (T__770 - 748)) | (1L << (T__771 - 748)) | (1L << (T__772 - 748)) | (1L << (T__773 - 748)) | (1L << (T__774 - 748)) | (1L << (T__775 - 748)) | (1L << (T__776 - 748)) | (1L << (T__777 - 748)) | (1L << (T__778 - 748)) | (1L << (T__779 - 748)) | (1L << (T__780 - 748)) | (1L << (T__781 - 748)) | (1L << (T__782 - 748)) | (1L << (T__783 - 748)) | (1L << (T__784 - 748)) | (1L << (T__785 - 748)) | (1L << (T__786 - 748)) | (1L << (T__787 - 748)) | (1L << (T__788 - 748)) | (1L << (T__789 - 748)) | (1L << (T__790 - 748)) | (1L << (T__791 - 748)) | (1L << (T__792 - 748)) | (1L << (T__793 - 748)) | (1L << (T__794 - 748)) | (1L << (T__795 - 748)) | (1L << (T__796 - 748)) | (1L << (T__797 - 748)) | (1L << (T__798 - 748)) | (1L << (T__799 - 748)) | (1L << (T__800 - 748)) | (1L << (T__801 - 748)) | (1L << (T__802 - 748)) | (1L << (T__803 - 748)) | (1L << (T__804 - 748)) | (1L << (T__805 - 748)) | (1L << (T__806 - 748)) | (1L << (T__807 - 748)) | (1L << (T__808 - 748)) | (1L << (T__809 - 748)) | (1L << (T__810 - 748)))) != 0) || ((((_la - 812)) & ~0x3f) == 0 && ((1L << (_la - 812)) & ((1L << (T__811 - 812)) | (1L << (T__812 - 812)) | (1L << (T__813 - 812)) | (1L << (T__814 - 812)) | (1L << (T__815 - 812)) | (1L << (T__816 - 812)) | (1L << (T__817 - 812)) | (1L << (T__818 - 812)) | (1L << (T__819 - 812)) | (1L << (T__820 - 812)) | (1L << (T__821 - 812)) | (1L << (T__822 - 812)) | (1L << (T__823 - 812)) | (1L << (T__824 - 812)) | (1L << (T__825 - 812)) | (1L << (T__826 - 812)) | (1L << (T__827 - 812)) | (1L << (T__828 - 812)) | (1L << (T__829 - 812)) | (1L << (T__830 - 812)) | (1L << (T__831 - 812)) | (1L << (T__832 - 812)) | (1L << (T__833 - 812)) | (1L << (T__834 - 812)) | (1L << (T__835 - 812)) | (1L << (T__836 - 812)) | (1L << (T__837 - 812)) | (1L << (T__838 - 812)) | (1L << (T__839 - 812)) | (1L << (T__840 - 812)) | (1L << (T__841 - 812)) | (1L << (T__842 - 812)) | (1L << (T__843 - 812)) | (1L << (T__844 - 812)))) != 0)) ) {
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
		enterRule(_localctx, 34, RULE_effect);
		int _la;
		try {
			setState(380);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__845:
				enterOuterAlt(_localctx, 1);
				{
				setState(346);
				match(T__845);
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(347);
					match(T__2);
					setState(348);
					match(T__846);
					}
				}

				}
				break;
			case T__847:
				enterOuterAlt(_localctx, 2);
				{
				setState(351);
				match(T__847);
				setState(354);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(352);
					match(T__2);
					setState(353);
					match(T__848);
					}
				}

				}
				break;
			case T__846:
				enterOuterAlt(_localctx, 3);
				{
				setState(356);
				match(T__846);
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(357);
					match(T__2);
					setState(358);
					match(T__845);
					}
				}

				}
				break;
			case T__848:
				enterOuterAlt(_localctx, 4);
				{
				setState(361);
				match(T__848);
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(362);
					match(T__2);
					setState(363);
					match(T__847);
					}
				}

				}
				break;
			case T__849:
				enterOuterAlt(_localctx, 5);
				{
				setState(366);
				match(T__849);
				}
				break;
			case T__850:
				enterOuterAlt(_localctx, 6);
				{
				setState(367);
				match(T__850);
				}
				break;
			case T__851:
				enterOuterAlt(_localctx, 7);
				{
				setState(368);
				match(T__851);
				}
				break;
			case T__852:
				enterOuterAlt(_localctx, 8);
				{
				setState(369);
				match(T__852);
				}
				break;
			case T__853:
				enterOuterAlt(_localctx, 9);
				{
				setState(370);
				match(T__853);
				}
				break;
			case T__854:
				enterOuterAlt(_localctx, 10);
				{
				setState(371);
				match(T__854);
				}
				break;
			case T__855:
				enterOuterAlt(_localctx, 11);
				{
				setState(372);
				match(T__855);
				}
				break;
			case T__856:
				enterOuterAlt(_localctx, 12);
				{
				setState(373);
				match(T__856);
				}
				break;
			case T__857:
				enterOuterAlt(_localctx, 13);
				{
				setState(374);
				match(T__857);
				}
				break;
			case T__858:
				enterOuterAlt(_localctx, 14);
				{
				setState(375);
				match(T__858);
				}
				break;
			case T__859:
				enterOuterAlt(_localctx, 15);
				{
				setState(376);
				match(T__859);
				}
				break;
			case T__860:
				enterOuterAlt(_localctx, 16);
				{
				setState(377);
				match(T__860);
				}
				break;
			case T__861:
				enterOuterAlt(_localctx, 17);
				{
				setState(378);
				match(T__861);
				}
				break;
			case T__862:
				enterOuterAlt(_localctx, 18);
				{
				setState(379);
				match(T__862);
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
		enterRule(_localctx, 36, RULE_argument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6 || _la==T__863) {
				{
				setState(382);
				prefix();
				}
			}

			setState(385);
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
		enterRule(_localctx, 38, RULE_prefix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==T__863) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(389);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__864) {
				{
				setState(388);
				match(T__864);
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
		enterRule(_localctx, 40, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
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
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__137:
			case T__138:
			case T__157:
			case T__158:
			case T__159:
			case T__160:
			case T__211:
			case T__212:
			case T__445:
			case T__446:
			case T__447:
			case T__448:
			case T__865:
			case T__866:
			case T__867:
			case T__868:
			case T__869:
			case T__870:
			case T__871:
			case T__872:
			case T__873:
				{
				setState(394);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 138)) & ~0x3f) == 0 && ((1L << (_la - 138)) & ((1L << (T__137 - 138)) | (1L << (T__138 - 138)) | (1L << (T__157 - 138)) | (1L << (T__158 - 138)) | (1L << (T__159 - 138)) | (1L << (T__160 - 138)))) != 0) || _la==T__211 || _la==T__212 || ((((_la - 446)) & ~0x3f) == 0 && ((1L << (_la - 446)) & ((1L << (T__445 - 446)) | (1L << (T__446 - 446)) | (1L << (T__447 - 446)) | (1L << (T__448 - 446)))) != 0) || ((((_la - 866)) & ~0x3f) == 0 && ((1L << (_la - 866)) & ((1L << (T__865 - 866)) | (1L << (T__866 - 866)) | (1L << (T__867 - 866)) | (1L << (T__868 - 866)) | (1L << (T__869 - 866)) | (1L << (T__870 - 866)) | (1L << (T__871 - 866)) | (1L << (T__872 - 866)) | (1L << (T__873 - 866)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(395);
				((ExpressionContext)_localctx).exp = expression(12);
				}
				break;
			case T__906:
			case T__907:
			case T__908:
			case T__909:
			case T__910:
			case T__911:
				{
				setState(396);
				((ExpressionContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 907)) & ~0x3f) == 0 && ((1L << (_la - 907)) & ((1L << (T__906 - 907)) | (1L << (T__907 - 907)) | (1L << (T__908 - 907)) | (1L << (T__909 - 907)) | (1L << (T__910 - 907)) | (1L << (T__911 - 907)))) != 0)) ) {
					((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(397);
				match(T__912);
				setState(398);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(399);
				match(T__913);
				}
				break;
			case T__912:
				{
				setState(401);
				match(T__912);
				setState(402);
				((ExpressionContext)_localctx).exp = expression(0);
				setState(403);
				match(T__913);
				}
				break;
			case T__914:
			case T__915:
			case T__916:
			case T__917:
			case PTR:
			case VARIABLE:
			case STRING:
			case QUAD:
			case BIN:
			case HEX:
			case NUMBER:
				{
				setState(406);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__914) {
					{
					setState(405);
					match(T__914);
					}
				}

				setState(408);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(440);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(438);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(411);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(412);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__36) | (1L << T__46))) != 0) || _la==T__154 || _la==T__156 || _la==T__716 || _la==T__874 || _la==T__875) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(413);
						((ExpressionContext)_localctx).right = expression(12);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(414);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(415);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 877)) & ~0x3f) == 0 && ((1L << (_la - 877)) & ((1L << (T__876 - 877)) | (1L << (T__877 - 877)) | (1L << (T__878 - 877)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(416);
						((ExpressionContext)_localctx).right = expression(11);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(417);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(418);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__232 || _la==T__234 || ((((_la - 880)) & ~0x3f) == 0 && ((1L << (_la - 880)) & ((1L << (T__879 - 880)) | (1L << (T__880 - 880)) | (1L << (T__881 - 880)) | (1L << (T__882 - 880)) | (1L << (T__883 - 880)) | (1L << (T__884 - 880)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(419);
						((ExpressionContext)_localctx).right = expression(10);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(420);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(421);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 866)) & ~0x3f) == 0 && ((1L << (_la - 866)) & ((1L << (T__865 - 866)) | (1L << (T__866 - 866)) | (1L << (T__885 - 866)) | (1L << (T__886 - 866)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(422);
						((ExpressionContext)_localctx).right = expression(9);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(423);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(424);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 888)) & ~0x3f) == 0 && ((1L << (_la - 888)) & ((1L << (T__887 - 888)) | (1L << (T__888 - 888)) | (1L << (T__889 - 888)) | (1L << (T__890 - 888)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(425);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(426);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(427);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 892)) & ~0x3f) == 0 && ((1L << (_la - 892)) & ((1L << (T__891 - 892)) | (1L << (T__892 - 892)) | (1L << (T__893 - 892)) | (1L << (T__894 - 892)) | (1L << (T__895 - 892)) | (1L << (T__896 - 892)) | (1L << (T__897 - 892)) | (1L << (T__898 - 892)) | (1L << (T__899 - 892)) | (1L << (T__900 - 892)) | (1L << (T__901 - 892)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(428);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(429);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(430);
						((ExpressionContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 119)) & ~0x3f) == 0 && ((1L << (_la - 119)) & ((1L << (T__118 - 119)) | (1L << (T__122 - 119)) | (1L << (T__124 - 119)))) != 0) || ((((_la - 903)) & ~0x3f) == 0 && ((1L << (_la - 903)) & ((1L << (T__902 - 903)) | (1L << (T__903 - 903)) | (1L << (T__904 - 903)))) != 0)) ) {
							((ExpressionContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(431);
						((ExpressionContext)_localctx).right = expression(6);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(432);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(433);
						((ExpressionContext)_localctx).operator = match(T__905);
						setState(434);
						((ExpressionContext)_localctx).middle = expression(0);
						setState(435);
						((ExpressionContext)_localctx).operator = match(T__9);
						setState(436);
						((ExpressionContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(442);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
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
		enterRule(_localctx, 44, RULE_atom);
		int _la;
		try {
			setState(482);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(443);
				match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(444);
				match(HEX);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(445);
				match(BIN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(446);
				match(QUAD);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(447);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(448);
				match(T__915);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(449);
				match(PTR);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(450);
				match(PTR);
				setState(455);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(451);
					match(T__4);
					setState(452);
					expression(0);
					setState(453);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(457);
				match(PTR);
				setState(458);
				_la = _input.LA(1);
				if ( !(_la==T__916 || _la==T__917) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(463);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(459);
					match(T__4);
					setState(460);
					expression(0);
					setState(461);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(465);
				match(PTR);
				setState(470);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4) {
					{
					setState(466);
					match(T__4);
					setState(467);
					expression(0);
					setState(468);
					match(T__5);
					}
				}

				setState(472);
				_la = _input.LA(1);
				if ( !(_la==T__916 || _la==T__917) ) {
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
				setState(473);
				_la = _input.LA(1);
				if ( !(_la==T__916 || _la==T__917) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(474);
				match(PTR);
				setState(479);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
				case 1:
					{
					setState(475);
					match(T__4);
					setState(476);
					expression(0);
					setState(477);
					match(T__5);
					}
					break;
				}
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(481);
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
		case 14:
			return label_sempred((LabelContext)_localctx, predIndex);
		case 16:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 21:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u03a3\u01e7\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\7\2\62"+
		"\n\2\f\2\16\2\65\13\2\3\2\3\2\3\2\3\2\7\2;\n\2\f\2\16\2>\13\2\3\2\3\2"+
		"\3\3\6\3C\n\3\r\3\16\3D\3\3\7\3H\n\3\f\3\16\3K\13\3\3\3\7\3N\n\3\f\3\16"+
		"\3Q\13\3\3\4\5\4T\n\4\3\4\3\4\3\4\3\4\7\4Z\n\4\f\4\16\4]\13\4\3\4\5\4"+
		"`\n\4\3\4\3\4\3\4\3\4\3\4\5\4g\n\4\3\4\7\4j\n\4\f\4\16\4m\13\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\5\4u\n\4\3\4\7\4x\n\4\f\4\16\4{\13\4\5\4}\n\4\3\5\6\5"+
		"\u0080\n\5\r\5\16\5\u0081\3\5\7\5\u0085\n\5\f\5\16\5\u0088\13\5\3\5\7"+
		"\5\u008b\n\5\f\5\16\5\u008e\13\5\3\6\3\6\3\6\3\6\3\6\7\6\u0095\n\6\f\6"+
		"\16\6\u0098\13\6\3\6\3\6\3\6\6\6\u009d\n\6\r\6\16\6\u009e\3\7\3\7\3\b"+
		"\3\b\3\t\6\t\u00a6\n\t\r\t\16\t\u00a7\3\t\7\t\u00ab\n\t\f\t\16\t\u00ae"+
		"\13\t\3\t\7\t\u00b1\n\t\f\t\16\t\u00b4\13\t\3\n\5\n\u00b7\n\n\3\n\3\n"+
		"\3\n\3\n\3\n\5\n\u00be\n\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00c6\n\n\7\n\u00c8"+
		"\n\n\f\n\16\n\u00cb\13\n\3\n\6\n\u00ce\n\n\r\n\16\n\u00cf\3\13\6\13\u00d3"+
		"\n\13\r\13\16\13\u00d4\3\13\7\13\u00d8\n\13\f\13\16\13\u00db\13\13\3\13"+
		"\3\13\7\13\u00df\n\13\f\13\16\13\u00e2\13\13\3\f\3\f\6\f\u00e6\n\f\r\f"+
		"\16\f\u00e7\3\f\5\f\u00eb\n\f\3\f\5\f\u00ee\n\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\5\f\u00f7\n\f\3\f\6\f\u00fa\n\f\r\f\16\f\u00fb\3\f\5\f\u00ff\n"+
		"\f\3\f\5\f\u0102\n\f\3\f\3\f\3\f\3\f\3\f\5\f\u0109\n\f\3\f\6\f\u010c\n"+
		"\f\r\f\16\f\u010d\3\f\5\f\u0111\n\f\3\f\5\f\u0114\n\f\3\f\3\f\3\f\5\f"+
		"\u0119\n\f\3\f\6\f\u011c\n\f\r\f\16\f\u011d\3\f\5\f\u0121\n\f\3\f\5\f"+
		"\u0124\n\f\3\f\3\f\5\f\u0128\n\f\3\f\6\f\u012b\n\f\r\f\16\f\u012c\3\f"+
		"\5\f\u0130\n\f\3\f\3\f\3\f\3\f\7\f\u0136\n\f\f\f\16\f\u0139\13\f\3\f\6"+
		"\f\u013c\n\f\r\f\16\f\u013d\5\f\u0140\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u0147"+
		"\n\r\3\16\3\16\3\16\3\16\3\16\5\16\u014e\n\16\3\17\3\17\3\20\3\20\5\20"+
		"\u0154\n\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\5\23\u0160"+
		"\n\23\3\23\3\23\3\23\5\23\u0165\n\23\3\23\3\23\3\23\5\23\u016a\n\23\3"+
		"\23\3\23\3\23\5\23\u016f\n\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u017f\n\23\3\24\5\24\u0182\n\24\3"+
		"\24\3\24\3\25\3\25\5\25\u0188\n\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0199\n\27\3\27\5\27\u019c"+
		"\n\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\7\27\u01b9\n\27\f\27\16\27\u01bc\13\27\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u01ca\n\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\5\30\u01d2\n\30\3\30\3\30\3\30\3\30\3\30\5\30\u01d9\n\30\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\5\30\u01e2\n\30\3\30\5\30\u01e5\n\30\3\30"+
		"\2\3,\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\25\3\2\3\4"+
		"\3\2\n\13\3\2\r\16\3\2\17\20\3\2\21\30\3\2\32\u02ed\3\2\u02ee\u034f\4"+
		"\2\t\t\u0362\u0362\3\2\21\26\7\2\u008c\u008d\u00a0\u00a3\u00d6\u00d7\u01c0"+
		"\u01c3\u0364\u036c\3\2\u038d\u0392\t\2%%\'\'\61\61\u009d\u009d\u009f\u009f"+
		"\u02cf\u02cf\u036d\u036e\3\2\u036f\u0371\5\2\u00eb\u00eb\u00ed\u00ed\u0372"+
		"\u0377\4\2\u0364\u0365\u0378\u0379\3\2\u037a\u037d\3\2\u037e\u0388\6\2"+
		"yy}}\177\177\u0389\u038b\3\2\u0397\u0398\2\u023f\2\63\3\2\2\2\4B\3\2\2"+
		"\2\6|\3\2\2\2\b\177\3\2\2\2\n\u008f\3\2\2\2\f\u00a0\3\2\2\2\16\u00a2\3"+
		"\2\2\2\20\u00a5\3\2\2\2\22\u00b6\3\2\2\2\24\u00d2\3\2\2\2\26\u013f\3\2"+
		"\2\2\30\u0141\3\2\2\2\32\u0148\3\2\2\2\34\u014f\3\2\2\2\36\u0151\3\2\2"+
		"\2 \u0157\3\2\2\2\"\u0159\3\2\2\2$\u017e\3\2\2\2&\u0181\3\2\2\2(\u0185"+
		"\3\2\2\2*\u0189\3\2\2\2,\u019b\3\2\2\2.\u01e4\3\2\2\2\60\62\7\u03a2\2"+
		"\2\61\60\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64<\3\2\2\2"+
		"\65\63\3\2\2\2\66;\5\4\3\2\67;\5\b\5\28;\5\20\t\29;\5\24\13\2:\66\3\2"+
		"\2\2:\67\3\2\2\2:8\3\2\2\2:9\3\2\2\2;>\3\2\2\2<:\3\2\2\2<=\3\2\2\2=?\3"+
		"\2\2\2><\3\2\2\2?@\7\2\2\3@\3\3\2\2\2AC\t\2\2\2BA\3\2\2\2CD\3\2\2\2DB"+
		"\3\2\2\2DE\3\2\2\2EI\3\2\2\2FH\7\u03a2\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2"+
		"\2IJ\3\2\2\2JO\3\2\2\2KI\3\2\2\2LN\5\6\4\2ML\3\2\2\2NQ\3\2\2\2OM\3\2\2"+
		"\2OP\3\2\2\2P\5\3\2\2\2QO\3\2\2\2RT\7\5\2\2SR\3\2\2\2ST\3\2\2\2TU\3\2"+
		"\2\2UV\7\u039c\2\2VW\7\6\2\2W[\5,\27\2XZ\7\u03a2\2\2YX\3\2\2\2Z]\3\2\2"+
		"\2[Y\3\2\2\2[\\\3\2\2\2\\}\3\2\2\2][\3\2\2\2^`\7\5\2\2_^\3\2\2\2_`\3\2"+
		"\2\2`a\3\2\2\2af\7\u039c\2\2bc\7\7\2\2cd\5,\27\2de\7\b\2\2eg\3\2\2\2f"+
		"b\3\2\2\2fg\3\2\2\2gk\3\2\2\2hj\7\u03a2\2\2ih\3\2\2\2jm\3\2\2\2ki\3\2"+
		"\2\2kl\3\2\2\2l}\3\2\2\2mk\3\2\2\2no\7\t\2\2ot\5,\27\2pq\7\7\2\2qr\5,"+
		"\27\2rs\7\b\2\2su\3\2\2\2tp\3\2\2\2tu\3\2\2\2uy\3\2\2\2vx\7\u03a2\2\2"+
		"wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z}\3\2\2\2{y\3\2\2\2|S\3\2\2\2"+
		"|_\3\2\2\2|n\3\2\2\2}\7\3\2\2\2~\u0080\t\3\2\2\177~\3\2\2\2\u0080\u0081"+
		"\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0086\3\2\2\2\u0083"+
		"\u0085\7\u03a2\2\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084"+
		"\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u008c\3\2\2\2\u0088\u0086\3\2\2\2\u0089"+
		"\u008b\5\n\6\2\u008a\u0089\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2"+
		"\2\2\u008c\u008d\3\2\2\2\u008d\t\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0096"+
		"\5\f\7\2\u0090\u0091\7\7\2\2\u0091\u0092\5,\27\2\u0092\u0093\7\b\2\2\u0093"+
		"\u0095\3\2\2\2\u0094\u0090\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3\2"+
		"\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098\u0096\3\2\2\2\u0099"+
		"\u009a\7\f\2\2\u009a\u009c\5\16\b\2\u009b\u009d\7\u03a2\2\2\u009c\u009b"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\13\3\2\2\2\u00a0\u00a1\7\u039c\2\2\u00a1\r\3\2\2\2\u00a2\u00a3\7\u039d"+
		"\2\2\u00a3\17\3\2\2\2\u00a4\u00a6\t\4\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a7"+
		"\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00ac\3\2\2\2\u00a9"+
		"\u00ab\7\u03a2\2\2\u00aa\u00a9\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa"+
		"\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00b2\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af"+
		"\u00b1\5\22\n\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3"+
		"\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\21\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5"+
		"\u00b7\5*\26\2\u00b6\u00b5\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b8\3\2"+
		"\2\2\u00b8\u00bd\7\u039c\2\2\u00b9\u00ba\7\7\2\2\u00ba\u00bb\5,\27\2\u00bb"+
		"\u00bc\7\b\2\2\u00bc\u00be\3\2\2\2\u00bd\u00b9\3\2\2\2\u00bd\u00be\3\2"+
		"\2\2\u00be\u00c9\3\2\2\2\u00bf\u00c0\7\5\2\2\u00c0\u00c5\7\u039c\2\2\u00c1"+
		"\u00c2\7\7\2\2\u00c2\u00c3\5,\27\2\u00c3\u00c4\7\b\2\2\u00c4\u00c6\3\2"+
		"\2\2\u00c5\u00c1\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7"+
		"\u00bf\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2"+
		"\2\2\u00ca\u00cd\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cc\u00ce\7\u03a2\2\2\u00cd"+
		"\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\23\3\2\2\2\u00d1\u00d3\t\5\2\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4"+
		"\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d9\3\2\2\2\u00d6"+
		"\u00d8\7\u03a2\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7"+
		"\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00e0\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc"+
		"\u00df\5\34\17\2\u00dd\u00df\5\26\f\2\u00de\u00dc\3\2\2\2\u00de\u00dd"+
		"\3\2\2\2\u00df\u00e2\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\25\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3\u00e5\5\36\20\2\u00e4\u00e6\7\u03a2"+
		"\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7"+
		"\u00e8\3\2\2\2\u00e8\u0140\3\2\2\2\u00e9\u00eb\5\36\20\2\u00ea\u00e9\3"+
		"\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00ee\5\"\22\2\u00ed"+
		"\u00ec\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\5 "+
		"\21\2\u00f0\u00f1\5&\24\2\u00f1\u00f2\7\5\2\2\u00f2\u00f3\5&\24\2\u00f3"+
		"\u00f4\7\5\2\2\u00f4\u00f6\5&\24\2\u00f5\u00f7\5$\23\2\u00f6\u00f5\3\2"+
		"\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f9\3\2\2\2\u00f8\u00fa\7\u03a2\2\2\u00f9"+
		"\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2"+
		"\2\2\u00fc\u0140\3\2\2\2\u00fd\u00ff\5\36\20\2\u00fe\u00fd\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\u0101\3\2\2\2\u0100\u0102\5\"\22\2\u0101\u0100\3"+
		"\2\2\2\u0101\u0102\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0104\5 \21\2\u0104"+
		"\u0105\5&\24\2\u0105\u0106\7\5\2\2\u0106\u0108\5&\24\2\u0107\u0109\5$"+
		"\23\2\u0108\u0107\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010b\3\2\2\2\u010a"+
		"\u010c\7\u03a2\2\2\u010b\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010b"+
		"\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u0140\3\2\2\2\u010f\u0111\5\36\20\2"+
		"\u0110\u010f\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0113\3\2\2\2\u0112\u0114"+
		"\5\"\22\2\u0113\u0112\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\3\2\2\2"+
		"\u0115\u0116\5 \21\2\u0116\u0118\5&\24\2\u0117\u0119\5$\23\2\u0118\u0117"+
		"\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011b\3\2\2\2\u011a\u011c\7\u03a2\2"+
		"\2\u011b\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011b\3\2\2\2\u011d\u011e"+
		"\3\2\2\2\u011e\u0140\3\2\2\2\u011f\u0121\5\36\20\2\u0120\u011f\3\2\2\2"+
		"\u0120\u0121\3\2\2\2\u0121\u0123\3\2\2\2\u0122\u0124\5\"\22\2\u0123\u0122"+
		"\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0127\5 \21\2\u0126"+
		"\u0128\5$\23\2\u0127\u0126\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u012a\3\2"+
		"\2\2\u0129\u012b\7\u03a2\2\2\u012a\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c"+
		"\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u0140\3\2\2\2\u012e\u0130\5\36"+
		"\20\2\u012f\u012e\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0131\3\2\2\2\u0131"+
		"\u0132\5\34\17\2\u0132\u0137\5\32\16\2\u0133\u0134\7\5\2\2\u0134\u0136"+
		"\5\32\16\2\u0135\u0133\3\2\2\2\u0136\u0139\3\2\2\2\u0137\u0135\3\2\2\2"+
		"\u0137\u0138\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u013a\u013c"+
		"\7\u03a2\2\2\u013b\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013b\3\2\2"+
		"\2\u013d\u013e\3\2\2\2\u013e\u0140\3\2\2\2\u013f\u00e3\3\2\2\2\u013f\u00ea"+
		"\3\2\2\2\u013f\u00fe\3\2\2\2\u013f\u0110\3\2\2\2\u013f\u0120\3\2\2\2\u013f"+
		"\u012f\3\2\2\2\u0140\27\3\2\2\2\u0141\u0146\5,\27\2\u0142\u0143\7\7\2"+
		"\2\u0143\u0144\5,\27\2\u0144\u0145\7\b\2\2\u0145\u0147\3\2\2\2\u0146\u0142"+
		"\3\2\2\2\u0146\u0147\3\2\2\2\u0147\31\3\2\2\2\u0148\u014d\5,\27\2\u0149"+
		"\u014a\7\7\2\2\u014a\u014b\5,\27\2\u014b\u014c\7\b\2\2\u014c\u014e\3\2"+
		"\2\2\u014d\u0149\3\2\2\2\u014d\u014e\3\2\2\2\u014e\33\3\2\2\2\u014f\u0150"+
		"\t\6\2\2\u0150\35\3\2\2\2\u0151\u0153\6\20\2\2\u0152\u0154\7\31\2\2\u0153"+
		"\u0152\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0156\7\u039c"+
		"\2\2\u0156\37\3\2\2\2\u0157\u0158\t\7\2\2\u0158!\3\2\2\2\u0159\u015a\6"+
		"\22\3\2\u015a\u015b\t\b\2\2\u015b#\3\2\2\2\u015c\u015f\7\u0350\2\2\u015d"+
		"\u015e\7\5\2\2\u015e\u0160\7\u0351\2\2\u015f\u015d\3\2\2\2\u015f\u0160"+
		"\3\2\2\2\u0160\u017f\3\2\2\2\u0161\u0164\7\u0352\2\2\u0162\u0163\7\5\2"+
		"\2\u0163\u0165\7\u0353\2\2\u0164\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165"+
		"\u017f\3\2\2\2\u0166\u0169\7\u0351\2\2\u0167\u0168\7\5\2\2\u0168\u016a"+
		"\7\u0350\2\2\u0169\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u017f\3\2\2"+
		"\2\u016b\u016e\7\u0353\2\2\u016c\u016d\7\5\2\2\u016d\u016f\7\u0352\2\2"+
		"\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u017f\3\2\2\2\u0170\u017f"+
		"\7\u0354\2\2\u0171\u017f\7\u0355\2\2\u0172\u017f\7\u0356\2\2\u0173\u017f"+
		"\7\u0357\2\2\u0174\u017f\7\u0358\2\2\u0175\u017f\7\u0359\2\2\u0176\u017f"+
		"\7\u035a\2\2\u0177\u017f\7\u035b\2\2\u0178\u017f\7\u035c\2\2\u0179\u017f"+
		"\7\u035d\2\2\u017a\u017f\7\u035e\2\2\u017b\u017f\7\u035f\2\2\u017c\u017f"+
		"\7\u0360\2\2\u017d\u017f\7\u0361\2\2\u017e\u015c\3\2\2\2\u017e\u0161\3"+
		"\2\2\2\u017e\u0166\3\2\2\2\u017e\u016b\3\2\2\2\u017e\u0170\3\2\2\2\u017e"+
		"\u0171\3\2\2\2\u017e\u0172\3\2\2\2\u017e\u0173\3\2\2\2\u017e\u0174\3\2"+
		"\2\2\u017e\u0175\3\2\2\2\u017e\u0176\3\2\2\2\u017e\u0177\3\2\2\2\u017e"+
		"\u0178\3\2\2\2\u017e\u0179\3\2\2\2\u017e\u017a\3\2\2\2\u017e\u017b\3\2"+
		"\2\2\u017e\u017c\3\2\2\2\u017e\u017d\3\2\2\2\u017f%\3\2\2\2\u0180\u0182"+
		"\5(\25\2\u0181\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0183\3\2\2\2\u0183"+
		"\u0184\5,\27\2\u0184\'\3\2\2\2\u0185\u0187\t\t\2\2\u0186\u0188\7\u0363"+
		"\2\2\u0187\u0186\3\2\2\2\u0187\u0188\3\2\2\2\u0188)\3\2\2\2\u0189\u018a"+
		"\t\n\2\2\u018a+\3\2\2\2\u018b\u018c\b\27\1\2\u018c\u018d\t\13\2\2\u018d"+
		"\u019c\5,\27\16\u018e\u018f\t\f\2\2\u018f\u0190\7\u0393\2\2\u0190\u0191"+
		"\5,\27\2\u0191\u0192\7\u0394\2\2\u0192\u019c\3\2\2\2\u0193\u0194\7\u0393"+
		"\2\2\u0194\u0195\5,\27\2\u0195\u0196\7\u0394\2\2\u0196\u019c\3\2\2\2\u0197"+
		"\u0199\7\u0395\2\2\u0198\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a"+
		"\3\2\2\2\u019a\u019c\5.\30\2\u019b\u018b\3\2\2\2\u019b\u018e\3\2\2\2\u019b"+
		"\u0193\3\2\2\2\u019b\u0198\3\2\2\2\u019c\u01ba\3\2\2\2\u019d\u019e\f\r"+
		"\2\2\u019e\u019f\t\r\2\2\u019f\u01b9\5,\27\16\u01a0\u01a1\f\f\2\2\u01a1"+
		"\u01a2\t\16\2\2\u01a2\u01b9\5,\27\r\u01a3\u01a4\f\13\2\2\u01a4\u01a5\t"+
		"\17\2\2\u01a5\u01b9\5,\27\f\u01a6\u01a7\f\n\2\2\u01a7\u01a8\t\20\2\2\u01a8"+
		"\u01b9\5,\27\13\u01a9\u01aa\f\t\2\2\u01aa\u01ab\t\21\2\2\u01ab\u01b9\5"+
		",\27\n\u01ac\u01ad\f\b\2\2\u01ad\u01ae\t\22\2\2\u01ae\u01b9\5,\27\t\u01af"+
		"\u01b0\f\7\2\2\u01b0\u01b1\t\23\2\2\u01b1\u01b9\5,\27\b\u01b2\u01b3\f"+
		"\6\2\2\u01b3\u01b4\7\u038c\2\2\u01b4\u01b5\5,\27\2\u01b5\u01b6\7\f\2\2"+
		"\u01b6\u01b7\5,\27\7\u01b7\u01b9\3\2\2\2\u01b8\u019d\3\2\2\2\u01b8\u01a0"+
		"\3\2\2\2\u01b8\u01a3\3\2\2\2\u01b8\u01a6\3\2\2\2\u01b8\u01a9\3\2\2\2\u01b8"+
		"\u01ac\3\2\2\2\u01b8\u01af\3\2\2\2\u01b8\u01b2\3\2\2\2\u01b9\u01bc\3\2"+
		"\2\2\u01ba\u01b8\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb-\3\2\2\2\u01bc\u01ba"+
		"\3\2\2\2\u01bd\u01e5\7\u03a1\2\2\u01be\u01e5\7\u03a0\2\2\u01bf\u01e5\7"+
		"\u039f\2\2\u01c0\u01e5\7\u039e\2\2\u01c1\u01e5\7\u039d\2\2\u01c2\u01e5"+
		"\7\u0396\2\2\u01c3\u01e5\7\u039b\2\2\u01c4\u01c9\7\u039b\2\2\u01c5\u01c6"+
		"\7\7\2\2\u01c6\u01c7\5,\27\2\u01c7\u01c8\7\b\2\2\u01c8\u01ca\3\2\2\2\u01c9"+
		"\u01c5\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u01e5\3\2\2\2\u01cb\u01cc\7\u039b"+
		"\2\2\u01cc\u01d1\t\24\2\2\u01cd\u01ce\7\7\2\2\u01ce\u01cf\5,\27\2\u01cf"+
		"\u01d0\7\b\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01cd\3\2\2\2\u01d1\u01d2\3\2"+
		"\2\2\u01d2\u01e5\3\2\2\2\u01d3\u01d8\7\u039b\2\2\u01d4\u01d5\7\7\2\2\u01d5"+
		"\u01d6\5,\27\2\u01d6\u01d7\7\b\2\2\u01d7\u01d9\3\2\2\2\u01d8\u01d4\3\2"+
		"\2\2\u01d8\u01d9\3\2\2\2\u01d9\u01da\3\2\2\2\u01da\u01e5\t\24\2\2\u01db"+
		"\u01dc\t\24\2\2\u01dc\u01e1\7\u039b\2\2\u01dd\u01de\7\7\2\2\u01de\u01df"+
		"\5,\27\2\u01df\u01e0\7\b\2\2\u01e0\u01e2\3\2\2\2\u01e1\u01dd\3\2\2\2\u01e1"+
		"\u01e2\3\2\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e5\7\u039c\2\2\u01e4\u01bd"+
		"\3\2\2\2\u01e4\u01be\3\2\2\2\u01e4\u01bf\3\2\2\2\u01e4\u01c0\3\2\2\2\u01e4"+
		"\u01c1\3\2\2\2\u01e4\u01c2\3\2\2\2\u01e4\u01c3\3\2\2\2\u01e4\u01c4\3\2"+
		"\2\2\u01e4\u01cb\3\2\2\2\u01e4\u01d3\3\2\2\2\u01e4\u01db\3\2\2\2\u01e4"+
		"\u01e3\3\2\2\2\u01e5/\3\2\2\2I\63:<DIOS[_fkty|\u0081\u0086\u008c\u0096"+
		"\u009e\u00a7\u00ac\u00b2\u00b6\u00bd\u00c5\u00c9\u00cf\u00d4\u00d9\u00de"+
		"\u00e0\u00e7\u00ea\u00ed\u00f6\u00fb\u00fe\u0101\u0108\u010d\u0110\u0113"+
		"\u0118\u011d\u0120\u0123\u0127\u012c\u012f\u0137\u013d\u013f\u0146\u014d"+
		"\u0153\u015f\u0164\u0169\u016e\u017e\u0181\u0187\u0198\u019b\u01b8\u01ba"+
		"\u01c9\u01d1\u01d8\u01e1\u01e4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}