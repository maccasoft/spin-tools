/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootNode extends Node {

    List<Token> comments = new ArrayList<>();

    List<ConstantNode> constants = new ArrayList<>();
    List<StructNode> structs = new ArrayList<>();
    List<VariableNode> variables = new ArrayList<>();
    List<ObjectNode> objects = new ArrayList<>();
    List<MethodNode> methods = new ArrayList<>();
    List<FunctionNode> functions = new ArrayList<>();

    Map<String, RootNode> objectRoots = new HashMap<>();

    public RootNode() {

    }

    public void addComment(Token token) {
        comments.add(token);
    }

    public void addAllComments(Collection<Token> c) {
        comments.addAll(c);
    }

    public List<Token> getComments() {
        return comments;
    }

    public void addConstant(ConstantNode node) {
        constants.add(node);
    }

    public List<ConstantNode> getConstants() {
        return constants;
    }

    public void addStruct(StructNode node) {
        structs.add(node);
    }

    public List<StructNode> getStructs() {
        return structs;
    }

    public void addVariable(VariableNode node) {
        variables.add(node);
    }

    public List<VariableNode> getVariables() {
        return variables;
    }

    public void addObject(ObjectNode node) {
        objects.add(node);
    }

    public List<ObjectNode> getObjects() {
        return objects;
    }

    public void addMethod(MethodNode node) {
        methods.add(node);
    }

    public List<MethodNode> getMethods() {
        return methods;
    }

    public void addFunction(FunctionNode node) {
        functions.add(node);
    }

    public List<FunctionNode> getFunctions() {
        return functions;
    }

    public void addObjectRoot(String name, RootNode node) {
        objectRoots.put(name, node);
    }

    public RootNode getObjectRoot(String name) {
        return objectRoots.get(name);
    }

    public Map<String, RootNode> getObjectRoots() {
        return objectRoots;
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String getText() {
        return "";
    }

}
