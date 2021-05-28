// Generated from Spin2Parser.g4 by ANTLR 4.9.2
package com.maccasoft.propeller.spin;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Spin2Parser}.
 */
public interface Spin2ParserListener extends ParseTreeListener {
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
	 * Enter a parse tree produced by {@link Spin2Parser#constantsSection}.
	 * @param ctx the parse tree
	 */
	void enterConstantsSection(Spin2Parser.ConstantsSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constantsSection}.
	 * @param ctx the parse tree
	 */
	void exitConstantsSection(Spin2Parser.ConstantsSectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constantAssign}.
	 * @param ctx the parse tree
	 */
	void enterConstantAssign(Spin2Parser.ConstantAssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constantAssign}.
	 * @param ctx the parse tree
	 */
	void exitConstantAssign(Spin2Parser.ConstantAssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constantEnum}.
	 * @param ctx the parse tree
	 */
	void enterConstantEnum(Spin2Parser.ConstantEnumContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constantEnum}.
	 * @param ctx the parse tree
	 */
	void exitConstantEnum(Spin2Parser.ConstantEnumContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constantEnumName}.
	 * @param ctx the parse tree
	 */
	void enterConstantEnumName(Spin2Parser.ConstantEnumNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constantEnumName}.
	 * @param ctx the parse tree
	 */
	void exitConstantEnumName(Spin2Parser.ConstantEnumNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#objectsSection}.
	 * @param ctx the parse tree
	 */
	void enterObjectsSection(Spin2Parser.ObjectsSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#objectsSection}.
	 * @param ctx the parse tree
	 */
	void exitObjectsSection(Spin2Parser.ObjectsSectionContext ctx);
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
	 * Enter a parse tree produced by {@link Spin2Parser#variablesSection}.
	 * @param ctx the parse tree
	 */
	void enterVariablesSection(Spin2Parser.VariablesSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#variablesSection}.
	 * @param ctx the parse tree
	 */
	void exitVariablesSection(Spin2Parser.VariablesSectionContext ctx);
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
	 * Enter a parse tree produced by {@link Spin2Parser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(Spin2Parser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(Spin2Parser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(Spin2Parser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(Spin2Parser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#result}.
	 * @param ctx the parse tree
	 */
	void enterResult(Spin2Parser.ResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#result}.
	 * @param ctx the parse tree
	 */
	void exitResult(Spin2Parser.ResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#localvars}.
	 * @param ctx the parse tree
	 */
	void enterLocalvars(Spin2Parser.LocalvarsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#localvars}.
	 * @param ctx the parse tree
	 */
	void exitLocalvars(Spin2Parser.LocalvarsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#localvar}.
	 * @param ctx the parse tree
	 */
	void enterLocalvar(Spin2Parser.LocalvarContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#localvar}.
	 * @param ctx the parse tree
	 */
	void exitLocalvar(Spin2Parser.LocalvarContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(Spin2Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(Spin2Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(Spin2Parser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(Spin2Parser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#functionArgument}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArgument(Spin2Parser.FunctionArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#functionArgument}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArgument(Spin2Parser.FunctionArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(Spin2Parser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(Spin2Parser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#repeatLoop}.
	 * @param ctx the parse tree
	 */
	void enterRepeatLoop(Spin2Parser.RepeatLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#repeatLoop}.
	 * @param ctx the parse tree
	 */
	void exitRepeatLoop(Spin2Parser.RepeatLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterConditional(Spin2Parser.ConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitConditional(Spin2Parser.ConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#elseConditional}.
	 * @param ctx the parse tree
	 */
	void enterElseConditional(Spin2Parser.ElseConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#elseConditional}.
	 * @param ctx the parse tree
	 */
	void exitElseConditional(Spin2Parser.ElseConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#caseConditional}.
	 * @param ctx the parse tree
	 */
	void enterCaseConditional(Spin2Parser.CaseConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#caseConditional}.
	 * @param ctx the parse tree
	 */
	void exitCaseConditional(Spin2Parser.CaseConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#caseConditionalMatch}.
	 * @param ctx the parse tree
	 */
	void enterCaseConditionalMatch(Spin2Parser.CaseConditionalMatchContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#caseConditionalMatch}.
	 * @param ctx the parse tree
	 */
	void exitCaseConditionalMatch(Spin2Parser.CaseConditionalMatchContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#caseConditionalOther}.
	 * @param ctx the parse tree
	 */
	void enterCaseConditionalOther(Spin2Parser.CaseConditionalOtherContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#caseConditionalOther}.
	 * @param ctx the parse tree
	 */
	void exitCaseConditionalOther(Spin2Parser.CaseConditionalOtherContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#spinExpression}.
	 * @param ctx the parse tree
	 */
	void enterSpinExpression(Spin2Parser.SpinExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#spinExpression}.
	 * @param ctx the parse tree
	 */
	void exitSpinExpression(Spin2Parser.SpinExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAtom(Spin2Parser.ExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#expressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAtom(Spin2Parser.ExpressionAtomContext ctx);
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
	 * Enter a parse tree produced by {@link Spin2Parser#dataLine}.
	 * @param ctx the parse tree
	 */
	void enterDataLine(Spin2Parser.DataLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#dataLine}.
	 * @param ctx the parse tree
	 */
	void exitDataLine(Spin2Parser.DataLineContext ctx);
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
	 * Enter a parse tree produced by {@link Spin2Parser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(Spin2Parser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(Spin2Parser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#dataValue}.
	 * @param ctx the parse tree
	 */
	void enterDataValue(Spin2Parser.DataValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#dataValue}.
	 * @param ctx the parse tree
	 */
	void exitDataValue(Spin2Parser.DataValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Spin2Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(Spin2Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Spin2Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(Spin2Parser.ConstantExpressionContext ctx);
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