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
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.RootNode;
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

    public Spin2PasmCompiler(Context scope, Spin2Compiler compiler, File file) {
        this(scope, compiler, null, file);
    }

    public Spin2PasmCompiler(Context scope, Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(parent, file, scope);
        this.compiler = compiler;
    }

    protected void compileDataBlocks(Node root) {
        String namespace = "";
        Context datScope = new Context(scope);
        Context localScope;

        for (Node n1 : root.getChilds()) {
            if (!n1.isExclude() && (n1 instanceof DataNode)) {
                DataNode node = (DataNode) n1;

                localScope = datScope;

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
                                processDittoBlock(scope, datScope, localScope, beginLineNode, list, endLineNode);
                            }
                            else {
                                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                                    datScope = new Context(scope);
                                    localScope = datScope;
                                }
                                else if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                                    localScope = datScope;
                                }

                                Spin2PAsmLine pasmLine = compileDataLine(scope, datScope, localScope, lineNode, namespace);
                                pasmLine.setData(lineNode);
                                source.addAll(pasmLine.expand());

                                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                                    localScope = pasmLine.getScope();
                                }

                                if ("NAMESP".equalsIgnoreCase(pasmLine.getMnemonic())) {
                                    Iterator<Spin2PAsmExpression> args = pasmLine.getArguments().iterator();
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
                                    for (Spin2PAsmExpression argument : pasmLine.getArguments()) {
                                        String fileName = argument.getString();
                                        File includeFile = compiler.getFile(fileName, ".spin2");
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

                processAliases(scope, "LONG", namespace);
            }
        }
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
            else if ("DEBUG".equalsIgnoreCase(mnemonic)) {
                List<Token> tokens = new ArrayList<>(node.getTokens());
                tokens.remove(node.label);
                tokens.remove(node.condition);

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

                if (node.modifier != null) {
                    logMessage(new CompilerException("not allowed", node.modifier));
                }
            }
            else if ("NAMESP".equalsIgnoreCase(mnemonic)) {
                if (node.condition != null) {
                    logMessage(new CompilerException("not allowed", node.condition));
                }

                Iterator<Spin2PAsmExpression> args = parameters.iterator();
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
                    else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.DataType) {
                        type = pasmLine.getMnemonic();
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
            else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.DataType) {
                type = pasmLine.getMnemonic();
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
            DataLineNode node = (DataLineNode) line.getData();
            try {
                Context aliasScope = entry.getValue();
                aliasScope.addSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                aliasScope.addSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                aliasScope.addSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                aliasScope.addSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));

                if (!line.isLocalLabel() && scope != null) {
                    String qualifiedName = namespace + line.getLabel();
                    scope.addSymbol(qualifiedName, new DataVariable(line.getScope(), type));
                    scope.addSymbol("#" + qualifiedName, new RegisterAddress(line.getScope(), type));
                    scope.addSymbol("@" + qualifiedName, new ObjectContextLiteral(line.getScope(), type));
                    scope.addSymbol("@@" + qualifiedName, new MemoryContextLiteral(line.getScope(), type));
                }
            } catch (RuntimeException e) {
                logMessage(new CompilerException(e, node.label));
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

    protected abstract void compileDatInclude(RootNode root);

    protected byte[] getBinaryFile(String fileName) {
        return compiler.getBinaryFile(fileName);
    }

    void processDittoBlock(Context scope, Context datScope, Context localScope, DataLineNode beginLineNode, List<DataLineNode> list, DataLineNode endLineNode) {
        int count = 0;

        Spin2PAsmLine beginPasmLine = compileDataLine(scope, datScope, localScope, beginLineNode, "");
        if (beginLineNode.condition != null) {
            logMessage(new CompilerException("condition not allowed", beginLineNode.condition));
        }
        if (beginLineNode.parameters.size() == 0) {
            logMessage(new CompilerException("missing count parameter", beginLineNode.instruction));
        }
        source.addAll(beginPasmLine.expand());

        DataLineNode.ParameterNode param = beginLineNode.parameters.get(0);
        try {
            Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(beginPasmLine.getScope(), param.getTokens());
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
                Spin2PAsmLine pasmLine = compileDataLine(scope, datScope, localScope, lineNode, "");
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
        Spin2PAsmLine endPasmLine = compileDataLine(scope, datScope, localScope, endLineNode, "");
        source.addAll(endPasmLine.expand());
    }

    @Override
    protected Expression buildPreprocessorExpression(TokenIterator iter) {
        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
        builder.setIgnoreMissing(true);

        while (iter.hasNext()) {
            Token token = iter.next();
            if ("defined".equals(token.getText())) {
                builder.addTokenLiteral(token);
                if (iter.hasNext() && "(".equals(iter.peekNext().getText())) {
                    builder.addToken(iter.next());
                    if (iter.hasNext()) {
                        builder.addTokenLiteral(iter.next());
                        if (iter.hasNext() && ")".equals(iter.peekNext().getText())) {
                            builder.addToken(iter.next());
                        }
                    }
                }
            }
            else {
                builder.addToken(token);
            }
        }

        return builder.getExpression();
    }

}
