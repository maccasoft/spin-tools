// Generated from Spin2.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Spin2Parser}.
 */
public interface Spin2Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(Spin2Parser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(Spin2Parser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constants}.
	 * @param ctx the parse tree
	 */
	void enterConstants(Spin2Parser.ConstantsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constants}.
	 * @param ctx the parse tree
	 */
	void exitConstants(Spin2Parser.ConstantsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(Spin2Parser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(Spin2Parser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#objects}.
	 * @param ctx the parse tree
	 */
	void enterObjects(Spin2Parser.ObjectsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#objects}.
	 * @param ctx the parse tree
	 */
	void exitObjects(Spin2Parser.ObjectsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(Spin2Parser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(Spin2Parser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(Spin2Parser.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(Spin2Parser.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#filename}.
	 * @param ctx the parse tree
	 */
	void enterFilename(Spin2Parser.FilenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#filename}.
	 * @param ctx the parse tree
	 */
	void exitFilename(Spin2Parser.FilenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariables(Spin2Parser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariables(Spin2Parser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(Spin2Parser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(Spin2Parser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#data}.
	 * @param ctx the parse tree
	 */
	void enterData(Spin2Parser.DataContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#data}.
	 * @param ctx the parse tree
	 */
	void exitData(Spin2Parser.DataContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(Spin2Parser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(Spin2Parser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#typeValue}.
	 * @param ctx the parse tree
	 */
	void enterTypeValue(Spin2Parser.TypeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#typeValue}.
	 * @param ctx the parse tree
	 */
	void exitTypeValue(Spin2Parser.TypeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#org}.
	 * @param ctx the parse tree
	 */
	void enterOrg(Spin2Parser.OrgContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#org}.
	 * @param ctx the parse tree
	 */
	void exitOrg(Spin2Parser.OrgContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#orgh}.
	 * @param ctx the parse tree
	 */
	void enterOrgh(Spin2Parser.OrghContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#orgh}.
	 * @param ctx the parse tree
	 */
	void exitOrgh(Spin2Parser.OrghContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#orgf}.
	 * @param ctx the parse tree
	 */
	void enterOrgf(Spin2Parser.OrgfContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#orgf}.
	 * @param ctx the parse tree
	 */
	void exitOrgf(Spin2Parser.OrgfContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#fit}.
	 * @param ctx the parse tree
	 */
	void enterFit(Spin2Parser.FitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#fit}.
	 * @param ctx the parse tree
	 */
	void exitFit(Spin2Parser.FitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#longData}.
	 * @param ctx the parse tree
	 */
	void enterLongData(Spin2Parser.LongDataContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#longData}.
	 * @param ctx the parse tree
	 */
	void exitLongData(Spin2Parser.LongDataContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#wordData}.
	 * @param ctx the parse tree
	 */
	void enterWordData(Spin2Parser.WordDataContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#wordData}.
	 * @param ctx the parse tree
	 */
	void exitWordData(Spin2Parser.WordDataContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#byteData}.
	 * @param ctx the parse tree
	 */
	void enterByteData(Spin2Parser.ByteDataContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#byteData}.
	 * @param ctx the parse tree
	 */
	void exitByteData(Spin2Parser.ByteDataContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#singleValue}.
	 * @param ctx the parse tree
	 */
	void enterSingleValue(Spin2Parser.SingleValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#singleValue}.
	 * @param ctx the parse tree
	 */
	void exitSingleValue(Spin2Parser.SingleValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#arrayValue}.
	 * @param ctx the parse tree
	 */
	void enterArrayValue(Spin2Parser.ArrayValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#arrayValue}.
	 * @param ctx the parse tree
	 */
	void exitArrayValue(Spin2Parser.ArrayValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirective(Spin2Parser.DirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirective(Spin2Parser.DirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(Spin2Parser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(Spin2Parser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#opcode}.
	 * @param ctx the parse tree
	 */
	void enterOpcode(Spin2Parser.OpcodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#opcode}.
	 * @param ctx the parse tree
	 */
	void exitOpcode(Spin2Parser.OpcodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(Spin2Parser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(Spin2Parser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#effect}.
	 * @param ctx the parse tree
	 */
	void enterEffect(Spin2Parser.EffectContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#effect}.
	 * @param ctx the parse tree
	 */
	void exitEffect(Spin2Parser.EffectContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#dst}.
	 * @param ctx the parse tree
	 */
	void enterDst(Spin2Parser.DstContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#dst}.
	 * @param ctx the parse tree
	 */
	void exitDst(Spin2Parser.DstContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#src}.
	 * @param ctx the parse tree
	 */
	void enterSrc(Spin2Parser.SrcContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#src}.
	 * @param ctx the parse tree
	 */
	void exitSrc(Spin2Parser.SrcContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#index}.
	 * @param ctx the parse tree
	 */
	void enterIndex(Spin2Parser.IndexContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#index}.
	 * @param ctx the parse tree
	 */
	void exitIndex(Spin2Parser.IndexContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(Spin2Parser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(Spin2Parser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(Spin2Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(Spin2Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(Spin2Parser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(Spin2Parser.AtomContext ctx);
}