/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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

public class SourcePool {

    Map<File, String> sources = new HashMap<>();

    final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public SourcePool() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public String getSource(File key) {
        return sources.get(key);
    }

    public void setSource(File key, String text) {
        String oldText = sources.put(key, text);
        changeSupport.firePropertyChange(key.getAbsolutePath(), oldText, text);
    }

    public void removeSource(File key) {
        String oldText = sources.remove(key);
        changeSupport.firePropertyChange(key.getAbsolutePath(), oldText, null);
    }

    public boolean containsSource(File key) {
        return sources.containsKey(key);
    }

}
