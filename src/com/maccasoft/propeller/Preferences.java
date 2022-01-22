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

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    private static String defaultSpin1LibraryPath = "library/spin1";
    private static String defaultSpin2LibraryPath = "library/spin2";

    static final int[] defaultTabStops = new int[] {
        4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80
    };

    public static class Bounds {
        public int x;
        public int y;
        public int width;
        public int height;

        public Bounds() {

        }

        public Bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

    }

    public static class SerializedPreferences {

        public boolean showLineNumbers;
        public String editorFont;
        public String port;
        public String spin1LibraryPath;
        public String spin2LibraryPath;
        public List<String> lru = new ArrayList<String>();

        public boolean reloadOpenTabs;
        public int[] tabStops;

        public String[] openTabs;

        public Bounds terminalWindow;

        public SerializedPreferences() {

        }

    }

    private static Preferences instance;
    private static File preferencesFile = new File(System.getProperty("user.home"), PREFERENCES_NAME);

    public static Preferences getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new Preferences();
        if (preferencesFile.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                instance.preferences = mapper.readValue(preferencesFile, SerializedPreferences.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    SerializedPreferences preferences = new SerializedPreferences();

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    Preferences() {
        preferences.showLineNumbers = true;
        preferences.reloadOpenTabs = true;
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
        return preferences.showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        changeSupport.firePropertyChange(PROP_SHOW_LINE_NUMBERS, preferences.showLineNumbers, preferences.showLineNumbers = showLineNumbers);
    }

    public String getEditorFont() {
        return preferences.editorFont;
    }

    public void setEditorFont(String editorFont) {
        if (preferences.editorFont != editorFont) {
            changeSupport.firePropertyChange(PROP_EDITOR_FONT, preferences.editorFont, preferences.editorFont = editorFont);
        }
    }

    public List<String> getLru() {
        return preferences.lru;
    }

    public void addToLru(File file) {
        preferences.lru.remove(file.getAbsolutePath());
        preferences.lru.add(0, file.getAbsolutePath());
        while (preferences.lru.size() > 10) {
            preferences.lru.remove(preferences.lru.size() - 1);
        }
        changeSupport.firePropertyChange(PROP_LRU, null, preferences.lru);
    }

    public String getPort() {
        return preferences.port;
    }

    public void setPort(String port) {
        changeSupport.firePropertyChange(PROP_PORT, preferences.port, preferences.port = port);
    }

    public String getSpin1LibraryPath() {
        return preferences.spin1LibraryPath != null ? preferences.spin1LibraryPath : defaultSpin1LibraryPath;
    }

    public void setSpin1LibraryPath(String path) {
        if (defaultSpin1LibraryPath.equals(path)) {
            preferences.spin1LibraryPath = null;
        }
        else {
            preferences.spin1LibraryPath = path;
        }
    }

    public String getSpin2LibraryPath() {
        return preferences.spin2LibraryPath != null ? preferences.spin2LibraryPath : defaultSpin2LibraryPath;
    }

    public void setSpin2LibraryPath(String path) {
        if (defaultSpin2LibraryPath.equals(path)) {
            preferences.spin2LibraryPath = null;
        }
        else {
            preferences.spin2LibraryPath = path;
        }
    }

    public int[] getTabStops() {
        return preferences.tabStops != null ? preferences.tabStops : defaultTabStops;
    }

    public void setTabStops(int[] tabStops) {
        preferences.tabStops = Arrays.equals(tabStops, defaultTabStops) ? null : tabStops;
    }

    public boolean getReloadOpenTabs() {
        return preferences.reloadOpenTabs;
    }

    public void setReloadOpenTabs(boolean reloadOpenTabs) {
        preferences.reloadOpenTabs = reloadOpenTabs;
    }

    public String[] getOpenTabs() {
        return preferences.openTabs;
    }

    public void setOpenTabs(String[] openTabs) {
        preferences.openTabs = openTabs;
    }

    public Rectangle getTerminalWindow() {
        if (preferences.terminalWindow == null) {
            return null;
        }
        return new Rectangle(preferences.terminalWindow.x, preferences.terminalWindow.y, preferences.terminalWindow.width, preferences.terminalWindow.height);
    }

    public void setTerminalWindow(Rectangle rect) {
        preferences.terminalWindow = new Bounds(rect.x, rect.y, rect.width, rect.height);
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(preferencesFile, preferences);
    }
}
