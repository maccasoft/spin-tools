/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.instructions.FileInc;

public abstract class Spin2PasmCompiler {

    Spin2Context scope;
    boolean debugEnabled;
    List<Object> debugStatements;

    int nested;
    Map<Spin2PAsmLine, Spin2Context> pendingAlias = new HashMap<Spin2PAsmLine, Spin2Context>();

    Spin2PasmCompiler() {
        this.debugStatements = new ArrayList<Object>();
    }

    public Spin2PasmCompiler(Spin2Context scope, boolean debugEnabled, List<Object> debugStatements) {
        this.scope = scope;
        this.debugEnabled = debugEnabled;
        this.debugStatements = debugStatements;
    }

    public List<Spin2PAsmLine> compileDat(DataNode root) {
        List<Spin2PAsmLine> source = new ArrayList<>();

        Spin2Context savedContext = scope;
        nested = 0;

        for (Node child : root.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            if (!debugEnabled && node.instruction != null && "DEBUG".equalsIgnoreCase(node.instruction.getText())) {
                if (node.label == null) {
                    continue;
                }
            }
            try {
                Spin2PAsmLine pasmLine = compileDataLine(node);
                pasmLine.setData(node);
                source.addAll(pasmLine.expand());

                if ("INCLUDE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                    if (node.condition != null) {
                        throw new CompilerException("not allowed", node.condition);
                    }
                    if (node.modifier != null) {
                        throw new CompilerException("not allowed", node.modifier);
                    }
                    int index = 0;
                    for (Spin2PAsmExpression argument : pasmLine.getArguments()) {
                        String fileName = argument.getString();
                        Node includedNode = getParsedSource(fileName);
                        try {
                            if (includedNode == null) {
                                throw new RuntimeException("file \"" + fileName + "\" not found");
                            }
                            source.addAll(compileDatInclude(scope, includedNode));
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, node.parameters.get(index)));
                        }
                        index++;
                    }
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node.instruction));
            }
        }

        pendingAlias.clear();
        scope = savedContext;

        return source;
    }

    public Spin2PAsmLine compileDataLine(Spin2Context scope, DataLineNode node) {
        Spin2Context savedContext = this.scope;
        this.scope = scope;
        try {
            return compileDataLine(node);
        } finally {
            this.scope = savedContext;
        }
    }

    public Spin2PAsmLine compileDataLine(DataLineNode node) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

        Spin2Context rootScope = scope;
        if (label != null && !label.startsWith(".")) {
            if (nested > 0) {
                scope = rootScope = scope.getParent();
                nested--;
            }
            scope = new Spin2Context(scope);
            nested++;
        }

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Spin2Context localScope = new Spin2Context(scope);

        for (DataLineNode.ParameterNode param : node.parameters) {
            int index = 0;
            String prefix = null;
            Expression expression = null, count = null;

            if (parameters.size() == 1 && Spin2InstructionObject.ptrInstructions.contains(mnemonic.toLowerCase()) && isPtrExpression(param.getTokens())) {
                expression = new Identifier(param.getText(), scope);
                expression.setData(param);
            }
            else {
                Token token;
                if (index < param.getTokens().size()) {
                    token = param.getToken(index);
                    if (token.getText().startsWith("#")) {
                        prefix = (prefix == null ? "" : prefix) + token.getText();
                        index++;
                    }
                }
                if (index < param.getTokens().size()) {
                    token = param.getToken(index);
                    if ("\\".equals(token.getText())) {
                        prefix = (prefix == null ? "" : prefix) + token.getText();
                        index++;
                    }
                }
                if (index < param.getTokens().size()) {
                    try {
                        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(localScope, param.getTokens().subList(index, param.getTokens().size()));
                        expression = expressionBuilder.getExpression();
                        expression.setData(param);
                    } catch (CompilerException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new CompilerException(e, param);
                    }
                }
            }
            if (param.count != null) {
                try {
                    Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(localScope, param.count.getTokens());
                    count = expressionBuilder.getExpression();
                    count.setData(param.count);
                } catch (CompilerException e) {
                    throw e;
                } catch (Exception e) {
                    throw new CompilerException(e, param.count);
                }
            }
            if (expression != null) {
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }
        }

        if (!debugEnabled && mnemonic != null && "DEBUG".equalsIgnoreCase(mnemonic)) {
            condition = mnemonic = null;
            parameters.clear();
            modifier = null;
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);

        try {
            if ("FILE".equalsIgnoreCase(mnemonic)) {
                if (node.condition != null) {
                    throw new CompilerException("not allowed", node.condition);
                }
                if (node.modifier != null) {
                    throw new CompilerException("not allowed", node.modifier);
                }
                String fileName = parameters.get(0).getString();
                byte[] data = getBinaryFile(fileName);
                if (data == null) {
                    throw new CompilerException("file \"" + fileName + "\" not found", node.parameters.get(0));
                }
                pasmLine.setInstructionObject(new FileInc(pasmLine.getScope(), data));
            }
            if ("DEBUG".equalsIgnoreCase(mnemonic)) {
                if (node.condition != null) {
                    throw new CompilerException("not allowed", node.condition);
                }
                if (node.modifier != null) {
                    throw new CompilerException("not allowed", node.modifier);
                }
                int debugIndex = debugStatements.size() + 1;
                if (debugIndex >= 255) {
                    throw new CompilerException("too much debug statements", node);
                }
                parameters.add(new Spin2PAsmExpression("#", new NumberLiteral(debugIndex), null));

                List<Token> tokens = new ArrayList<>(node.getTokens());
                if (node.label != null) {
                    tokens.remove(node.label);
                }
                Spin2PAsmDebugLine debugLine = Spin2PAsmDebugLine.buildFrom(pasmLine.getScope(), tokens);
                debugStatements.add(debugLine);
            }
        } catch (CompilerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CompilerException(e, node);
        }

        if (pasmLine.getLabel() != null) {
            try {
                String type = "LONG";
                if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Word) {
                    type = "WORD";
                }
                else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) {
                    type = "BYTE";
                }
                else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                    type = "BYTE";
                }
                rootScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                rootScope.addSymbol("#" + pasmLine.getLabel(), new RegisterAddress(pasmLine.getScope(), type));
                rootScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                rootScope.addSymbol("@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope(), type));

                if (pasmLine.getMnemonic() == null) {
                    if (!pasmLine.isLocalLabel()) {
                        pendingAlias.put(pasmLine, rootScope);
                    }
                }
                else if (pendingAlias.size() != 0) {
                    for (Entry<Spin2PAsmLine, Spin2Context> entry : pendingAlias.entrySet()) {
                        Spin2PAsmLine line = entry.getKey();
                        Spin2Context context = entry.getValue();
                        context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        context.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                        context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
                    }
                    pendingAlias.clear();
                }
            } catch (RuntimeException e) {
                throw new CompilerException(e, node.label);
            }
        }
        else if (pasmLine.getMnemonic() != null && pendingAlias.size() != 0) {
            String type = "LONG";
            if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Word) {
                type = "WORD";
            }
            else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) {
                type = "BYTE";
            }
            else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                type = "BYTE";
            }

            for (Entry<Spin2PAsmLine, Spin2Context> entry : pendingAlias.entrySet()) {
                Spin2PAsmLine line = entry.getKey();
                Spin2Context context = entry.getValue();
                context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                context.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
            }
            pendingAlias.clear();
        }

        return pasmLine;
    }

    boolean isPtrExpression(List<Token> tokens) {
        int index = 0;

        if (index >= tokens.size()) {
            return false;
        }
        Token token = tokens.get(index++);
        if ("++".equals(token.getText()) || "--".equals(token.getText())) {
            if (index >= tokens.size()) {
                return false;
            }
            token = tokens.get(index++);
        }
        if (!("PTRA".equalsIgnoreCase(token.getText()) || "PTRB".equalsIgnoreCase(token.getText()))) {
            return false;
        }
        if (index >= tokens.size()) {
            return true;
        }
        token = tokens.get(index++);
        if (!("++".equals(token.getText()) || "--".equals(token.getText()) || "[".equals(token.getText()))) {
            return false;
        }
        return index >= tokens.size();
    }

    protected List<Spin2PAsmLine> compileDatInclude(Spin2Context scope, Node root) {
        List<Spin2PAsmLine> source = new ArrayList<>();

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                List<Spin2PAsmLine> list = compileDat((DataNode) node);
                source.addAll(list);
            }
        }

        return source;
    }

    protected abstract Node getParsedSource(String fileName);

    protected abstract byte[] getBinaryFile(String fileName);

    protected abstract void logMessage(CompilerException message);

}