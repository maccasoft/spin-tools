/*
 * Copyright (c) 2015-2017 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Preferences {

    public static final String PROP_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String PROP_EDITOR_FONT = "editorFont";
    public static final String PROP_LRU = "lru";

    public static final String PREFERENCES_NAME = ".spin-tools";

    private static Preferences instance;
    private static File preferencesFile = new File(System.getProperty("user.home"), PREFERENCES_NAME);

    public static Preferences getInstance() {
        if (instance != null) {
            return instance;
        }
        if (preferencesFile.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                instance = mapper.readValue(preferencesFile, Preferences.class);
            } catch (Exception e) {
                e.printStackTrace();
                instance = new Preferences();
                instance.showLineNumbers = true;
            }
        }
        else {
            instance = new Preferences();
        }
        return instance;
    }

    boolean showLineNumbers;
    String editorFont;
    final List<String> lru = new ArrayList<String>();

    final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    Preferences() {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        changeSupport.firePropertyChange(PROP_SHOW_LINE_NUMBERS, this.showLineNumbers, this.showLineNumbers = showLineNumbers);
    }

    public String getEditorFont() {
        return editorFont;
    }

    public void setEditorFont(String editorFont) {
        if (this.editorFont != editorFont) {
            changeSupport.firePropertyChange(PROP_EDITOR_FONT, this.editorFont, this.editorFont = editorFont);
        }
    }

    public List<String> getLru() {
        return lru;
    }

    public void addToLru(File file) {
        lru.remove(file.getAbsolutePath());
        lru.add(0, file.getAbsolutePath());
        while (lru.size() > 10) {
            lru.remove(lru.size() - 1);
        }
        changeSupport.firePropertyChange(PROP_LRU, null, this.lru);
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(preferencesFile, this);
    }
}
