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
	 * Visit a parse tree produced by {@link Spin2Parser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(Spin2Parser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(Spin2Parser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#result}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResult(Spin2Parser.ResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#localvars}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalvars(Spin2Parser.LocalvarsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#localvar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalvar(Spin2Parser.LocalvarContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData(Spin2Parser.DataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#dataLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataLine(Spin2Parser.DataLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(Spin2Parser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(Spin2Parser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#opcode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpcode(Spin2Parser.OpcodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(Spin2Parser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefix(Spin2Parser.PrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#effect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(Spin2Parser.EffectContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#dataValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataValue(Spin2Parser.DataValueContext ctx);
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