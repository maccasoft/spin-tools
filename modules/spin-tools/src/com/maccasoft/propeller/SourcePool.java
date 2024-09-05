/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.model.Node;

public class SourcePool {

    Map<File, Node> sources = new HashMap<>();

    final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public SourcePool() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public Node getParsedSource(File key) {
        return sources.get(key);
    }

    public void setParsedSource(File key, Node node) {
        Node oldNode = sources.put(key, node);
        changeSupport.firePropertyChange(key.getAbsolutePath(), oldNode, node);
    }

    public void removeParsedSource(File key) {
        Node oldNode = sources.remove(key);
        changeSupport.firePropertyChange(key.getAbsolutePath(), oldNode, null);
    }

}
