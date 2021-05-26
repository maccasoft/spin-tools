// Generated from Spin2Parser.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Spin2Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Spin2ParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(Spin2Parser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constantsSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantsSection(Spin2Parser.ConstantsSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constantAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantAssign(Spin2Parser.ConstantAssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constantEnum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantEnum(Spin2Parser.ConstantEnumContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#constantEnumName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantEnumName(Spin2Parser.ConstantEnumNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#objectsSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectsSection(Spin2Parser.ObjectsSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(Spin2Parser.ObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#variablesSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariablesSection(Spin2Parser.VariablesSectionContext ctx);
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
	 * Visit a parse tree produced by {@link Spin2Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(Spin2Parser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(Spin2Parser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#functionArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArgument(Spin2Parser.FunctionArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(Spin2Parser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#repeatLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatLoop(Spin2Parser.RepeatLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#conditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(Spin2Parser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#elseConditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseConditional(Spin2Parser.ElseConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#caseConditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseConditional(Spin2Parser.CaseConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#caseConditionalMatch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseConditionalMatch(Spin2Parser.CaseConditionalMatchContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#caseConditionalOther}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseConditionalOther(Spin2Parser.CaseConditionalOtherContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#spinExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpinExpression(Spin2Parser.SpinExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Spin2Parser#experssionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExperssionAtom(Spin2Parser.ExperssionAtomContext ctx);
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