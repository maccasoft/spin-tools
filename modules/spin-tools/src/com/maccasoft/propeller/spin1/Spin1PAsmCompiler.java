/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.instructions.FileInc;

public abstract class Spin1PAsmCompiler {

    public Spin1PAsmCompiler() {

    }

    public List<Spin1PAsmLine> compileDatBlock(Context scope, Node parent) {
        Context savedContext = scope;
        Map<Spin1PAsmLine, Context> pendingAlias = new HashMap<>();
        List<Spin1PAsmLine> source = new ArrayList<>();

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            if (node.getTokenCount() == 0) {
                continue;
            }
            if (node.label == null && node.instruction == null) {
                throw new CompilerException("syntax error", node);
            }

            String label = node.label != null ? node.label.getText() : null;
            String condition = node.condition != null ? node.condition.getText() : null;
            String mnemonic = node.instruction != null ? node.instruction.getText() : null;
            String modifier = node.modifier != null ? node.modifier.getText() : null;
            List<Spin1PAsmExpression> parameters = new ArrayList<Spin1PAsmExpression>();

            try {
                Spin1PAsmLine pasmLine = new Spin1PAsmLine(scope, label, condition, mnemonic, parameters, modifier);
                pasmLine.setData(node);

                for (DataLineNode.ParameterNode param : node.parameters) {
                    int index = 0;
                    String prefix = null;
                    Expression expression = null, count = null;

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
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(pasmLine.getScope(), param.getTokens().subList(index, param.getTokens().size()));
                            expression = builder.getExpression();
                            expression.setData(param);
                        } catch (Exception e) {
                            throw new CompilerException(e, param);
                        }
                    }

                    if (param.count != null) {
                        try {
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(pasmLine.getScope(), param.count.getTokens());
                            count = builder.getExpression();
                        } catch (Exception e) {
                            throw new CompilerException(e, param.count);
                        }
                    }
                    parameters.add(new Spin1PAsmExpression(prefix, expression, count));
                }

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
                } catch (RuntimeException e) {
                    throw new CompilerException(e, node);
                }

                if (pasmLine.getLabel() != null) {
                    try {
                        if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                            scope = savedContext;
                        }
                        String type = "LONG";
                        if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Word) {
                            type = "WORD";
                        }
                        else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Byte) {
                            type = "BYTE";
                        }
                        else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                            type = "BYTE";
                        }
                        scope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                        scope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                        scope.addSymbol("@@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));

                        if (pasmLine.getMnemonic() == null) {
                            if (!pasmLine.isLocalLabel()) {
                                pendingAlias.put(pasmLine, scope);
                            }
                        }
                        else if (pendingAlias.size() != 0) {
                            for (Entry<Spin1PAsmLine, Context> entry : pendingAlias.entrySet()) {
                                Spin1PAsmLine line = entry.getKey();
                                Context context = entry.getValue();
                                context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                                context.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                            }
                            pendingAlias.clear();
                        }

                        if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                            scope = pasmLine.getScope();
                        }
                    } catch (RuntimeException e) {
                        throw new CompilerException(e, node.label);
                    }
                }
                else if (pasmLine.getMnemonic() != null && pendingAlias.size() != 0) {
                    String type = "LONG";
                    if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Word) {
                        type = "WORD";
                    }
                    else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Byte) {
                        type = "BYTE";
                    }
                    else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                        type = "BYTE";
                    }

                    for (Entry<Spin1PAsmLine, Context> entry : pendingAlias.entrySet()) {
                        Spin1PAsmLine line = entry.getKey();
                        Context context = entry.getValue();
                        context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        context.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                    }
                    pendingAlias.clear();
                }

                source.addAll(pasmLine.expand());

                if ("INCLUDE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                    if (node.condition != null) {
                        throw new CompilerException("not allowed", node.condition);
                    }
                    if (node.modifier != null) {
                        throw new CompilerException("not allowed", node.modifier);
                    }
                    int index = 0;
                    for (Spin1PAsmExpression argument : pasmLine.getArguments()) {
                        String fileName = argument.getString();
                        Node includedNode = getParsedSource(fileName);
                        try {
                            if (includedNode == null) {
                                throw new RuntimeException("file \"" + fileName + "\" not found");
                            }
                            source.addAll(compileDatInclude(scope, includedNode));
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

        return source;
    }

    protected List<Spin1PAsmLine> compileDatInclude(Context scope, Node root) {
        List<Spin1PAsmLine> source = new ArrayList<>();

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                source.addAll(compileDatBlock(scope, node));
            }
        }

        return source;
    }

    protected abstract Node getParsedSource(String fileName);

    protected abstract byte[] getBinaryFile(String fileName);

    protected abstract void logMessage(CompilerException message);

}
