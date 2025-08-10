/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.util.Iterator;
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
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.spin1.instructions.FileInc;

public abstract class Spin1PAsmCompiler extends ObjectCompiler {

    protected Spin1Compiler compiler;

    private Map<Spin1PAsmLine, Context> pendingAlias = new HashMap<Spin1PAsmLine, Context>();

    protected List<Spin1PAsmLine> source = new ArrayList<>();

    public Spin1PAsmCompiler(Context scope, Spin1Compiler compiler, File file) {
        this(scope, compiler, null, file);
    }

    public Spin1PAsmCompiler(Context scope, Spin1Compiler compiler, ObjectCompiler parent, File file) {
        super(parent, file, scope);
        this.compiler = compiler;
    }

    protected void compileDataBlocks(Node root) {
        String namespace = "";
        Context datScope = new Context(scope);
        Context lineScope;

        for (Node n1 : root.getChilds()) {
            if (!n1.isExclude() && (n1 instanceof DataNode)) {
                DataNode node = (DataNode) n1;

                lineScope = datScope;

                TokenIterator iter = node.tokenIterator();
                if (iter.hasNext()) {
                    Token section = iter.peekNext();
                    if ("DAT".equalsIgnoreCase(section.getText())) {
                        iter.next();
                    }
                }
                if (iter.hasNext()) {
                    logMessage(new CompilerException("syntax error", iter.next()));
                }

                Iterator<Node> nodeIterator = node.getChilds().iterator();
                while (nodeIterator.hasNext()) {
                    Node n2 = nodeIterator.next();
                    if (!n2.isExclude() && (n2 instanceof DataLineNode)) {
                        DataLineNode lineNode = (DataLineNode) n2;
                        try {
                            if (lineNode.instruction != null && "DITTO".equalsIgnoreCase(lineNode.instruction.getText())) {
                                DataLineNode beginLineNode = lineNode;
                                DataLineNode endLineNode = null;

                                List<DataLineNode> list = new ArrayList<>();
                                while (nodeIterator.hasNext()) {
                                    n2 = nodeIterator.next();
                                    if (!n2.isExclude() && (n2 instanceof DataLineNode)) {
                                        lineNode = (DataLineNode) n2;
                                        if ("DITTO".equalsIgnoreCase(lineNode.instruction.getText())) {
                                            if (lineNode.parameters.size() != 0 && "END".equalsIgnoreCase(lineNode.parameters.get(0).getText())) {
                                                endLineNode = lineNode;
                                                break;
                                            }
                                        }
                                        list.add(lineNode);
                                    }
                                }
                                if (endLineNode == null) {
                                    logMessage(new CompilerException("missing DITTO END line", beginLineNode));
                                }
                                processDittoBlock(datScope, lineScope, beginLineNode, list, endLineNode);
                            }
                            else {
                                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                                    datScope = new Context(scope);
                                    lineScope = datScope;
                                }
                                else if (lineNode.label != null && !lineNode.label.getText().startsWith(":")) {
                                    lineScope = datScope;
                                }

                                Spin1PAsmLine pasmLine = compileDataLine(datScope, lineScope, lineNode, namespace);
                                pasmLine.setData(lineNode);
                                source.addAll(pasmLine.expand());

                                if (lineNode.label != null && !lineNode.label.getText().startsWith(":")) {
                                    lineScope = pasmLine.getScope();
                                }

                                if ("NAMESP".equalsIgnoreCase(pasmLine.getMnemonic())) {
                                    Iterator<Spin1PAsmExpression> args = pasmLine.getArguments().iterator();
                                    if (args.hasNext()) {
                                        namespace = args.next().toString() + ".";
                                    }
                                    else {
                                        namespace = "";
                                    }
                                }
                                else if ("INCLUDE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                                    if (lineNode.condition != null) {
                                        throw new CompilerException("not allowed", lineNode.condition);
                                    }
                                    if (lineNode.modifier != null) {
                                        throw new CompilerException("not allowed", lineNode.modifier);
                                    }
                                    int index = 0;
                                    for (Spin1PAsmExpression argument : pasmLine.getArguments()) {
                                        String fileName = argument.getString();
                                        File includeFile = compiler.getFile(fileName, ".spin");
                                        RootNode includedNode = compiler.getParsedSource(includeFile);
                                        try {
                                            if (includedNode == null) {
                                                throw new RuntimeException("file \"" + fileName + "\" not found");
                                            }
                                            compileDatInclude(includedNode);
                                        } catch (CompilerException e) {
                                            logMessage(e);
                                        } catch (Exception e) {
                                            logMessage(new CompilerException(e, lineNode.parameters.get(index)));
                                        }
                                        index++;
                                    }
                                }
                            }
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, lineNode));
                        }
                    }
                }

                processAliases("LONG", namespace);
            }
        }
    }

    private Spin1PAsmLine compileDataLine(Context globalScope, Context lineScope, DataLineNode node, String namespace) {
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
                    logMessage(new CompilerException("not allowed", node.condition));
                }

                String fileName = parameters.get(0).getString();
                byte[] data = getBinaryFile(fileName);
                if (data == null) {
                    throw new CompilerException("file \"" + fileName + "\" not found", node.parameters.get(0));
                }
                pasmLine.setInstructionObject(new FileInc(pasmLine.getScope(), data));

                if (node.modifier != null) {
                    logMessage(new CompilerException("not allowed", node.modifier));
                }
            }
            else if ("NAMESP".equalsIgnoreCase(mnemonic)) {
                if (node.condition != null) {
                    logMessage(new CompilerException("not allowed", node.condition));
                }

                Iterator<Spin1PAsmExpression> args = parameters.iterator();
                if (args.hasNext()) {
                    namespace = args.next().toString() + ".";
                    if (args.hasNext()) {
                        logMessage(new CompilerException("expected one argument", args.next()));
                    }
                }
                else {
                    namespace = "";
                }

                if (node.modifier != null) {
                    logMessage(new CompilerException("not allowed", node.modifier));
                }
            }
        } catch (RuntimeException e) {
            throw new CompilerException(e, node);
        }

        if (pasmLine.getLabel() != null) {
            try {
                Context labelScope = pasmLine.isLocalLabel() ? lineScope : globalScope;
                if (pasmLine.getMnemonic() == null) {
                    pendingAlias.put(pasmLine, labelScope);
                }
                else {
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

                    labelScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                    labelScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                    labelScope.addSymbol("@@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope(), type));

                    if (!pasmLine.isLocalLabel()) {
                        String qualifiedName = namespace + pasmLine.getLabel();
                        scope.addSymbol(qualifiedName, new DataVariable(pasmLine.getScope(), type));
                        scope.addSymbol("@" + qualifiedName, new ObjectContextLiteral(pasmLine.getScope(), type));
                        scope.addSymbol("@@@" + qualifiedName, new MemoryContextLiteral(pasmLine.getScope(), type));
                    }

                    processAliases(type, namespace);
                }
            } catch (Exception e) {
                logMessage(new CompilerException(e.getMessage(), node.label));
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
            processAliases(type, namespace);
        }

        return pasmLine;
    }

    protected void processAliases(String type, String namespace) {
        for (Entry<Spin1PAsmLine, Context> entry : pendingAlias.entrySet()) {
            Spin1PAsmLine line = entry.getKey();
            DataLineNode node = (DataLineNode) line.getData();
            try {
                Context aliasScope = entry.getValue();
                aliasScope.addSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                aliasScope.addSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                aliasScope.addSymbol("@@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));

                if (!line.isLocalLabel()) {
                    String qualifiedName = namespace + line.getLabel();
                    scope.addSymbol(qualifiedName, new DataVariable(line.getScope(), type));
                    scope.addSymbol("@" + qualifiedName, new ObjectContextLiteral(line.getScope(), type));
                    scope.addSymbol("@@@" + qualifiedName, new MemoryContextLiteral(line.getScope(), type));
                }
            } catch (RuntimeException e) {
                logMessage(new CompilerException(e, node.label));
            }
        }
        pendingAlias.clear();
    }

    protected abstract void compileDatInclude(RootNode root);

    protected byte[] getBinaryFile(String fileName) {
        return compiler.getBinaryFile(fileName);
    }

    void processDittoBlock(Context globalScope, Context localScope, DataLineNode beginLineNode, List<DataLineNode> list, DataLineNode endLineNode) {
        int count = 0;

        Spin1PAsmLine beginPasmLine = compileDataLine(globalScope, localScope, beginLineNode, "");
        if (beginLineNode.condition != null) {
            logMessage(new CompilerException("condition not allowed", beginLineNode.condition));
        }
        if (beginLineNode.parameters.size() == 0) {
            logMessage(new CompilerException("missing count parameter", beginLineNode.instruction));
        }
        source.addAll(beginPasmLine.expand());

        DataLineNode.ParameterNode param = beginLineNode.parameters.get(0);
        try {
            Spin1ExpressionBuilder expressionBuilder = new Spin1ExpressionBuilder(beginPasmLine.getScope(), param.getTokens());
            Expression expression = expressionBuilder.getExpression();
            if (!expression.isConstant()) {
                logMessage(new CompilerException("expecting constant expression", param));
                return;
            }
            count = expression.getNumber().intValue();
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, param));
        }

        if (beginLineNode.parameters.size() > 1) {
            logMessage(new CompilerException("expecting end of line", beginLineNode.parameters.get(1)));
        }
        else if (beginLineNode.modifier != null) {
            logMessage(new CompilerException("expecting end of line", beginLineNode.modifier));
        }

        for (int ii = 0; ii < count; ii++) {
            for (DataLineNode lineNode : list) {
                if (lineNode.label != null) {
                    logMessage(new CompilerException("labels not allowed in DITTO block", lineNode.label));
                }
                Spin1PAsmLine pasmLine = compileDataLine(globalScope, localScope, lineNode, "");
                pasmLine.getScope().addSymbol("$$", new NumberLiteral(Long.valueOf(ii)));
                pasmLine.setData(lineNode);
                source.addAll(pasmLine.expand());
            }
        }

        if (endLineNode.condition != null) {
            logMessage(new CompilerException("condition not allowed", endLineNode.condition));
        }
        if (endLineNode.parameters.size() > 1) {
            logMessage(new CompilerException("expecting end of line", endLineNode.parameters.get(1)));
        }
        else if (endLineNode.modifier != null) {
            logMessage(new CompilerException("expecting end of line", endLineNode.modifier));
        }
        Spin1PAsmLine endPasmLine = compileDataLine(globalScope, localScope, endLineNode, "");
        source.addAll(endPasmLine.expand());
    }

    @Override
    protected Expression buildPreprocessorExpression(TokenIterator iter) {
        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
        builder.setIgnoreMissing(true);

        while (iter.hasNext()) {
            Token token = iter.next();
            if ("defined".equals(token.getText())) {
                builder.addToken(token);
                if (iter.hasNext()) {
                    builder.addTokenLiteral(iter.next());
                }
                if (iter.hasNext()) {
                    builder.addTokenLiteral(iter.next());
                }
                if (iter.hasNext()) {
                    builder.addTokenLiteral(iter.next());
                }
            }
            else {
                builder.addToken(token);
            }
        }

        return builder.getExpression();
    }

}
