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
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Preferences {

    public static final String PROP_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String PROP_EDITOR_FONT = "editorFont";
    public static final String PROP_LRU = "lru";
    public static final String PROP_PORT = "port";
    public static final String PROP_SPIN1_LIBRARY_PATH = "spin1LibraryPath";
    public static final String PROP_SPIN2_LIBRARY_PATH = "spin2LibraryPath";

    public static final String PREFERENCES_NAME = ".spin-tools";

    static final int[] defaultTabStops = new int[] {
        4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80
    };

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

    private boolean showLineNumbers;
    private String editorFont;
    private String port;
    private String spin1LibraryPath;
    private String spin2LibraryPath;
    private final List<String> lru = new ArrayList<String>();

    private String defaultSpin1LibraryPath = "library/spin1";
    private String defaultSpin2LibraryPath = "library/spin2";

    private boolean reloadOpenTabs;
    private int[] tabStops;

    String[] openTabs;

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    Preferences() {
        showLineNumbers = true;
        reloadOpenTabs = true;
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

    public boolean getShowLineNumbers() {
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        changeSupport.firePropertyChange(PROP_PORT, this.port, this.port = port);
    }

    @JsonGetter("spin1LibraryPath")
    public String getSerializableSpin1LibraryPath() {
        return spin1LibraryPath;
    }

    @JsonIgnore
    public String getSpin1LibraryPath() {
        return spin1LibraryPath != null ? spin1LibraryPath : defaultSpin1LibraryPath;
    }

    @JsonSetter("spin1LibraryPath")
    public void setSpin1LibraryPath(String path) {
        if (defaultSpin1LibraryPath.equals(path)) {
            this.spin1LibraryPath = null;
        }
        else {
            this.spin1LibraryPath = path;
        }
    }

    @JsonGetter("spin2LibraryPath")
    public String getSerializableSpin2LibraryPath() {
        return spin2LibraryPath;
    }

    @JsonIgnore
    public String getSpin2LibraryPath() {
        return spin2LibraryPath != null ? spin2LibraryPath : defaultSpin2LibraryPath;
    }

    @JsonSetter("spin2LibraryPath")
    public void setSpin2LibraryPath(String path) {
        if (defaultSpin2LibraryPath.equals(path)) {
            this.spin2LibraryPath = null;
        }
        else {
            this.spin2LibraryPath = path;
        }
    }

    @JsonIgnore
    public int[] getTabStops() {
        return tabStops != null ? tabStops : defaultTabStops;
    }

    @JsonSetter("tabStops")
    public void setTabStops(int[] tabStops) {
        this.tabStops = Arrays.equals(tabStops, defaultTabStops) ? null : tabStops;
    }

    @JsonGetter("tabStops")
    public int[] getSerializableTabStops() {
        return this.tabStops;
    }

    public boolean getReloadOpenTabs() {
        return reloadOpenTabs;
    }

    public void setReloadOpenTabs(boolean reloadOpenTabs) {
        this.reloadOpenTabs = reloadOpenTabs;
    }

    public String[] getOpenTabs() {
        return openTabs;
    }

    public void setOpenTabs(String[] openTabs) {
        this.openTabs = openTabs;
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(preferencesFile, this);
    }
}
