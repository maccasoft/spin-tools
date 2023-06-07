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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.instructions.FileInc;

public abstract class Spin1PAsmCompiler extends ObjectCompiler {

    protected Context scope;
    protected Spin1Compiler compiler;

    private Map<Spin1PAsmLine, Context> pendingAlias = new HashMap<Spin1PAsmLine, Context>();

    protected List<Spin1PAsmLine> source = new ArrayList<>();

    protected List<Node> excludedNodes = new ArrayList<>();

    public Spin1PAsmCompiler(Context scope, Spin1Compiler compiler, File file) {
        this(scope, compiler, null, file);
    }

    public Spin1PAsmCompiler(Context scope, Spin1Compiler compiler, ObjectCompiler parent, File file) {
        super(parent, file);
        this.scope = scope;
        this.compiler = compiler;
    }

    public Context getScope() {
        return scope;
    }

    protected void compileDatBlock(Node parent) {
        Context lineScope = scope;

        for (Node child : parent.getChilds()) {
            try {
                if (skipNode(child)) {
                    continue;
                }
                if (child instanceof DataLineNode) {
                    DataLineNode node = (DataLineNode) child;

                    if (node.label != null && !node.label.getText().startsWith(":")) {
                        lineScope = scope;
                    }

                    Spin1PAsmLine pasmLine = compileDataLine(lineScope, node);
                    pasmLine.setData(node);
                    source.addAll(pasmLine.expand());

                    if (node.label != null && !node.label.getText().startsWith(":")) {
                        lineScope = pasmLine.getScope();
                    }

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
                            File includeFile = compiler.getFile(fileName, ".spin");
                            Node includedNode = compiler.getParsedSource(includeFile);
                            try {
                                if (includedNode == null) {
                                    throw new RuntimeException("file \"" + fileName + "\" not found");
                                }
                                compileDatInclude(includedNode);
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, node.parameters.get(index)));
                            }
                            index++;
                        }
                    }
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child));
            }
        }

        pendingAlias.clear();
    }

    protected boolean skipNode(Node node) {
        return excludedNodes.contains(node);
    }

    protected Spin1PAsmLine compileDataLine(Context lineScope, DataLineNode node) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin1PAsmExpression> parameters = new ArrayList<Spin1PAsmExpression>();

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Context localScope = new Context(lineScope);

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
                    Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(localScope, param.getTokens().subList(index, param.getTokens().size()));
                    expression = builder.getExpression();
                    expression.setData(param);
                } catch (Exception e) {
                    throw new CompilerException(e, param);
                }
            }

            if (param.count != null) {
                try {
                    Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(localScope, param.count.getTokens());
                    count = builder.getExpression();
                } catch (Exception e) {
                    throw new CompilerException(e, param.count);
                }
            }
            parameters.add(new Spin1PAsmExpression(prefix, expression, count));
        }

        Spin1PAsmLine pasmLine = new Spin1PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);
        pasmLine.setData(node);

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

                Context labelScope = pasmLine.isLocalLabel() ? lineScope : scope;
                labelScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                labelScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                labelScope.addSymbol("@@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));

                if (pasmLine.getMnemonic() == null) {
                    pendingAlias.put(pasmLine, labelScope);
                }
                else if (pendingAlias.size() != 0) {
                    for (Entry<Spin1PAsmLine, Context> entry : pendingAlias.entrySet()) {
                        Spin1PAsmLine line = entry.getKey();
                        Context aliasScope = entry.getValue();
                        aliasScope.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        aliasScope.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        aliasScope.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                    }
                    pendingAlias.clear();
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
                Context aliasScope = entry.getValue();
                aliasScope.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                aliasScope.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                aliasScope.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
            }
            pendingAlias.clear();
        }

        return pasmLine;
    }

    protected abstract void compileDatInclude(Node root);

    protected byte[] getBinaryFile(String fileName) {
        return compiler.getBinaryFile(fileName);
    }

}
