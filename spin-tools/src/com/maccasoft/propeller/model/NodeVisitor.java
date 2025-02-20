/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class NodeVisitor {

    public void visitDirective(DirectiveNode node) {

    }

    public void visitTypeDefinition(TypeDefinitionNode node) {

    }

    public boolean visitConstants(ConstantsNode node) {
        return true;
    }

    public void visitConstant(ConstantNode node) {

    }

    public boolean visitVariables(VariablesNode node) {
        return true;
    }

    public void visitVariable(VariableNode node) {

    }

    public boolean visitObjects(ObjectsNode node) {
        return true;
    }

    public void visitObject(ObjectNode node) {

    }

    public boolean visitMethod(MethodNode node) {
        return true;
    }

    public boolean visitFunction(FunctionNode node) {
        return true;
    }

    public boolean visitStatement(StatementNode node) {
        return true;
    }

    public boolean visitData(DataNode node) {
        return true;
    }

    public void visitDataLine(DataLineNode node) {

    }

}
