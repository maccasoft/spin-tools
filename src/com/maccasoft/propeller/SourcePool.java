/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

    public static final String PROP_DEBUG_ENABLED = "__debugEnabled__";

    Map<String, Node> sources = new HashMap<String, Node>();
    boolean debugEnabled;

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
        changeSupport.firePropertyChange(key, oldNode, node);
    }

    public void removeParsedSource(String key) {
        Node oldNode = sources.remove(key);
        changeSupport.firePropertyChange(key, oldNode, null);
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        changeSupport.firePropertyChange(PROP_DEBUG_ENABLED, this.debugEnabled, this.debugEnabled = enabled);
    }

}
