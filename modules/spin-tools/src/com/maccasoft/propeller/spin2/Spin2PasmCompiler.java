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
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.instructions.FileInc;

public abstract class Spin2PasmCompiler extends ObjectCompiler {

    protected Spin2Compiler compiler;

    private Map<Spin2PAsmLine, Context> pendingAlias = new HashMap<Spin2PAsmLine, Context>();

    protected List<Spin2PAsmLine> source = new ArrayList<>();
    protected List<Spin2PAsmDebugLine> debugSource = new ArrayList<>();

    protected List<Node> excludedNodes = new ArrayList<>();

    public Spin2PasmCompiler(Context scope, Spin2Compiler compiler, File file) {
        this(scope, compiler, null, file);
    }

    public Spin2PasmCompiler(Context scope, Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(parent, file, scope);
        this.compiler = compiler;
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
                    if (!isDebugEnabled() && node.instruction != null && "DEBUG".equalsIgnoreCase(node.instruction.getText())) {
                        if (node.label == null) {
                            continue;
                        }
                    }

                    if (node.label != null && !node.label.getText().startsWith(".")) {
                        lineScope = scope;
                    }

                    Spin2PAsmLine pasmLine = compileDataLine(lineScope, node);
                    pasmLine.setData(node);
                    source.addAll(pasmLine.expand());

                    if (node.label != null && !node.label.getText().startsWith(".")) {
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
                        for (Spin2PAsmExpression argument : pasmLine.getArguments()) {
                            String fileName = argument.getString();
                            File includeFile = compiler.getFile(fileName, ".spin2");
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

    protected Spin2PAsmLine compileInlineDataLine(Context scope, Context lineScope, DataLineNode node) {
        Context savedContext = this.scope;
        this.scope = scope;
        try {
            return compileDataLine(lineScope, node);
        } finally {
            this.scope = savedContext;
        }
    }

    protected Spin2PAsmLine compileDataLine(Context lineScope, DataLineNode node) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Context localScope = new Context(lineScope);

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
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, param));
                    }
                }
            }
            if (param.count != null) {
                try {
                    Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(localScope, param.count.getTokens());
                    count = expressionBuilder.getExpression();
                    count.setData(param.count);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, param));
                }
            }
            if (expression != null) {
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }
        }

        if (!isDebugEnabled() && mnemonic != null && "DEBUG".equalsIgnoreCase(mnemonic)) {
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

                List<Token> tokens = new ArrayList<>(node.getTokens());
                if (node.label != null) {
                    tokens.remove(node.label);
                }
                Spin2PAsmDebugLine debugLine = Spin2PAsmDebugLine.buildFrom(pasmLine.getScope(), tokens);
                debugSource.add(debugLine);
                compiler.debugStatements.add(debugLine);

                parameters.add(new Spin2PAsmExpression("#", new NumberLiteral(0) {

                    @Override
                    public Number getNumber() {
                        int index = compiler.debugStatements.indexOf(debugLine) + 1;
                        if (index >= 255) {
                            throw new CompilerException("too much debug statements", node);
                        }
                        return Long.valueOf(index);
                    }

                }, null));
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

                Context labelScope = pasmLine.isLocalLabel() ? lineScope : scope;
                labelScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                labelScope.addSymbol("#" + pasmLine.getLabel(), new RegisterAddress(pasmLine.getScope(), type));
                labelScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                labelScope.addSymbol("@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope(), type));

                if (pasmLine.getMnemonic() == null) {
                    pendingAlias.put(pasmLine, labelScope);
                }
                else if (pendingAlias.size() != 0) {
                    for (Entry<Spin2PAsmLine, Context> entry : pendingAlias.entrySet()) {
                        Spin2PAsmLine line = entry.getKey();
                        Context aliasScope = entry.getValue();
                        aliasScope.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        aliasScope.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                        aliasScope.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        aliasScope.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
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

            for (Entry<Spin2PAsmLine, Context> entry : pendingAlias.entrySet()) {
                Spin2PAsmLine line = entry.getKey();
                Context aliasScope = entry.getValue();
                aliasScope.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                aliasScope.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                aliasScope.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                aliasScope.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
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

    protected boolean isDebugEnabled() {
        return compiler.isDebugEnabled() && !scope.isDefined("DEBUG_DISABLED");
    }

    protected abstract void compileDatInclude(Node root);

    protected byte[] getBinaryFile(String fileName) {
        return compiler.getBinaryFile(fileName);
    }

}
