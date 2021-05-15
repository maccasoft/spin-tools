// Generated from Spin2.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Spin2Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Spin2Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(Spin2Parser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constants}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstants(Spin2Parser.ConstantsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(Spin2Parser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#objects}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjects(Spin2Parser.ObjectsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(Spin2Parser.ObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#reference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference(Spin2Parser.ReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#filename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename(Spin2Parser.FilenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariables(Spin2Parser.VariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(Spin2Parser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData(Spin2Parser.DataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(Spin2Parser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#typeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeValue(Spin2Parser.TypeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#org}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrg(Spin2Parser.OrgContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#orgh}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrgh(Spin2Parser.OrghContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#orgf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrgf(Spin2Parser.OrgfContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#fit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFit(Spin2Parser.FitContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#longData}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLongData(Spin2Parser.LongDataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#wordData}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWordData(Spin2Parser.WordDataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#byteData}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByteData(Spin2Parser.ByteDataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#singleValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleValue(Spin2Parser.SingleValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#arrayValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayValue(Spin2Parser.ArrayValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirective(Spin2Parser.DirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(Spin2Parser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#opcode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpcode(Spin2Parser.OpcodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(Spin2Parser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(Spin2Parser.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#dst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDst(Spin2Parser.DstContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#src}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrc(Spin2Parser.SrcContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#index}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndex(Spin2Parser.IndexContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(Spin2Parser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(Spin2Parser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(Spin2Parser.AtomContext ctx);
}