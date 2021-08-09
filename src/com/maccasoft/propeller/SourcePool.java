/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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
import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.model.Node;

public class SourcePool {

    Map<String, Node> sources = new HashMap<String, Node>();

    final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public SourcePool() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public Node getParsedSource(String key) {
        return sources.get(key);
    }

    public void setParsedSource(String key, Node node) {
        Node oldNode = sources.put(key, node);
        if (oldNode != null) {
            changeSupport.firePropertyChange(key, oldNode, node);
        }
    }

    public void removeParsedSource(String key, boolean notify) {
        Node oldNode = sources.remove(key);
        if (notify) {
            changeSupport.firePropertyChange(key, oldNode, null);
        }
    }

    public void removeParsedSource(String key) {
        sources.remove(key);
    }

}
