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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RootNode extends Node {

    List<Token> comments = new ArrayList<Token>();

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

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String toString() {
        return "[Root]";
    }

}
