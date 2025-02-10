/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression.PtrExpression;
import com.maccasoft.propeller.spin2.instructions.FileInc;

public abstract class Spin2PasmCompiler extends ObjectCompiler {

    protected Spin2Compiler compiler;
    protected Spin2Debug debug = new Spin2Debug();

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

    protected void compileDatBlock(DataNode parent) {
        Context datScope = new Context(scope);
        Context localScope = datScope;
        String namespace = "";

        TokenIterator iter = parent.tokenIterator();
        if (iter.hasNext()) {
            Token section = iter.peekNext();
            if ("DAT".equalsIgnoreCase(section.getText())) {
                iter.next();
            }
        }
        if (iter.hasNext()) {
            namespace = iter.next().getText() + ".";
            if (iter.hasNext()) {
                logMessage(new CompilerException("syntax error", iter.next()));
            }
        }

        for (Node child : parent.getChilds()) {
            try {
                if (skipNode(child)) {
                    continue;
                }
                if (child instanceof DataLineNode) {
                    DataLineNode node = (DataLineNode) child;

                    if (node.label != null && !node.label.getText().startsWith(".")) {
                        localScope = datScope;
                    }

                    Spin2PAsmLine pasmLine = compileDataLine(scope, datScope, localScope, node, namespace);
                    pasmLine.setData(node);
                    source.addAll(pasmLine.expand());

                    if (node.label != null && !node.label.getText().startsWith(".")) {
                        localScope = pasmLine.getScope();
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

        processAliases(scope, "LONG", namespace);
    }

    protected boolean skipNode(Node node) {
        return excludedNodes.contains(node);
    }

    protected Spin2PAsmLine compileDataLine(Context datScope, Context lineScope, DataLineNode node) {
        return compileDataLine(null, datScope, lineScope, node, "");
    }

    protected Spin2PAsmLine compileDataLine(Context scope, Context datScope, Context localScope, DataLineNode node, String namespace) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Context lineScope = new Context(localScope);

        for (DataLineNode.ParameterNode param : node.parameters) {
            int index = 0;
            String prefix = null;
            Expression expression = null, count = null;
            Token token;

            if (parameters.size() == 1 && Spin2InstructionObject.ptrInstructions.contains(mnemonic.toLowerCase())) {
                try {
                    expression = getPtrExpression(lineScope, param);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, param));
                }
            }

            if (expression == null) {
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
                        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(lineScope, param.getTokens().subList(index, param.getTokens().size()));
                        expression = expressionBuilder.getExpression();
                        expression.setData(param);
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, param));
                    }
                }
                if (param.count != null) {
                    try {
                        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(lineScope, param.count.getTokens());
                        count = expressionBuilder.getExpression();
                        count.setData(param.count);
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, param));
                    }
                }
            }
            if (expression != null) {
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(lineScope, label, condition, mnemonic, parameters, modifier);

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
                if (debugLine != null) {
                    debugSource.add(debugLine);
                    compiler.debugStatements.add(debugLine);
                    pasmLine.setData("debug", debugLine);

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
                else {
                    parameters.add(new Spin2PAsmExpression("#", new NumberLiteral(0), null));
                }
            }
        } catch (CompilerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CompilerException(e, node);
        }

        if (pasmLine.getLabel() != null) {
            try {
                Context labelScope = pasmLine.isLocalLabel() ? localScope : datScope;
                if (pasmLine.getMnemonic() == null) {
                    pendingAlias.put(pasmLine, labelScope);
                }
                else {
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

                    labelScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                    labelScope.addSymbol("#" + pasmLine.getLabel(), new RegisterAddress(pasmLine.getScope(), type));
                    labelScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                    labelScope.addSymbol("@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope(), type));

                    if (!pasmLine.isLocalLabel() && scope != null) {
                        String qualifiedName = namespace + pasmLine.getLabel();
                        scope.addSymbol(qualifiedName, new DataVariable(pasmLine.getScope(), type));
                        scope.addSymbol("#" + qualifiedName, new RegisterAddress(pasmLine.getScope(), type));
                        scope.addSymbol("@" + qualifiedName, new ObjectContextLiteral(pasmLine.getScope(), type));
                        scope.addSymbol("@@" + qualifiedName, new MemoryContextLiteral(pasmLine.getScope(), type));
                    }

                    processAliases(scope, type, namespace);
                }
            } catch (RuntimeException e) {
                logMessage(new CompilerException(e, node.label));
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
            processAliases(scope, type, namespace);
        }

        return pasmLine;
    }

    protected void processAliases() {
        processAliases(null, "LONG", "");
    }

    protected void processAliases(Context scope) {
        processAliases(scope, "LONG", "");
    }

    protected void processAliases(Context scope, String type, String namespace) {
        for (Entry<Spin2PAsmLine, Context> entry : pendingAlias.entrySet()) {
            Spin2PAsmLine line = entry.getKey();

            Context aliasScope = entry.getValue();
            aliasScope.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
            aliasScope.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
            aliasScope.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
            aliasScope.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));

            if (!line.isLocalLabel() && scope != null) {
                String qualifiedName = namespace + line.getLabel();
                scope.addOrUpdateSymbol(qualifiedName, new DataVariable(line.getScope(), type));
                scope.addOrUpdateSymbol("#" + qualifiedName, new RegisterAddress(line.getScope(), type));
                scope.addOrUpdateSymbol("@" + qualifiedName, new ObjectContextLiteral(line.getScope(), type));
                scope.addOrUpdateSymbol("@@" + qualifiedName, new MemoryContextLiteral(line.getScope(), type));
            }
        }
        pendingAlias.clear();
    }

    PtrExpression getPtrExpression(Context scope, DataLineNode.ParameterNode param) {
        Token pre = null, ptr, post = null;
        boolean immediate = false;
        Expression index = null;
        Token token;
        int idx = 0;

        if (idx < param.getTokenCount()) {
            token = param.getToken(idx);
            if ("++".equals(token.getText()) || "--".equals(token.getText())) {
                pre = param.getToken(idx++);
            }
        }

        if (idx < param.getTokenCount()) {
            ptr = param.getToken(idx++);
            if (!("PTRA".equalsIgnoreCase(ptr.getText()) || "PTRB".equalsIgnoreCase(ptr.getText()))) {
                return null;
            }
            if (idx < param.getTokenCount()) {
                token = param.getTokens().get(idx);
                if ("++".equals(token.getText()) || "--".equals(token.getText())) {
                    post = param.getTokens().get(idx++);
                }
            }

            if (idx < param.getTokenCount()) {
                throw new CompilerException("syntax error", param.getTokens());
            }

            if (param.count != null) {
                idx = 0;
                if (idx < param.count.getTokenCount()) {
                    token = param.count.getToken(idx);
                    if ("##".equals(token.getText())) {
                        immediate = true;
                        idx++;
                    }
                }
                Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(scope);
                while (idx < param.count.getTokenCount()) {
                    expressionBuilder.addToken(param.count.getToken(idx++));
                }
                index = expressionBuilder.getExpression();
                index.setData(param.count);
            }

            PtrExpression expression = new PtrExpression(pre != null ? pre.getText() : null, ptr.getText(), post != null ? post.getText() : null, immediate, index);
            expression.setData(param);
            return expression;
        }

        return null;
    }

    protected boolean isDebugEnabled() {
        if (compiler.isDebugEnabled()) {
            Expression debugDisabled = scope.getLocalSymbol("DEBUG_DISABLE");
            if (debugDisabled != null) {
                return debugDisabled.getNumber().intValue() == 0;
            }
            return true;
        }
        return false;
    }

    protected abstract void compileDatInclude(Node root);

    protected byte[] getBinaryFile(String fileName) {
        return compiler.getBinaryFile(fileName);
    }

}
