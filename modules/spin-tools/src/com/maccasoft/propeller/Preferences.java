/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Preferences {

    public static final String PROP_SHOW_BROWSER = "showBrowser";
    public static final String PROP_ROOTS = "roots";
    public static final String PROP_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String PROP_EDITOR_FONT = "editorFont";
    public static final String PROP_SHOW_INDENT_LINES = "showIndentLines";
    public static final String PROP_INDENT_LINES_SIZE = "showLinesSize";
    public static final String PROP_SHOW_EDITOR_OUTLINE = "showEditorOutline";
    public static final String PROP_LRU = "lru";
    public static final String PROP_PORT = "port";
    public static final String PROP_SPIN1_LIBRARY_PATH = "spin1LibraryPath";
    public static final String PROP_SPIN1_CASE_SENSITIVE_SYMBOLS = "spin1CaseSensitiveSymbols";
    public static final String PROP_SPIN2_LIBRARY_PATH = "spin2LibraryPath";
    public static final String PROP_SPIN2_CASE_SENSITIVE_SYMBOLS = "spin2CaseSensitiveSymbols";
    public static final String PROP_TERMINAL_FONT = "terminalFont";
    public static final String PROP_TERMINAL_LINE_INPUT = "terminalLineInput";
    public static final String PROP_TERMINAL_LOCAL_ECHO = "terminalLocalEcho";

    public static final String PREFERENCES_NAME = ".spin-tools";

    public static File defaultSpin1LibraryPath = new File(System.getProperty("APP_DIR"), "library/spin1").getAbsoluteFile();
    public static File defaultSpin2LibraryPath = new File(System.getProperty("APP_DIR"), "library/spin2").getAbsoluteFile();

    public static String[] defaultVisiblePaths = new String[] {
        new File(System.getProperty("user.home")).getAbsolutePath()
    };

    static Map<Class<?>, String> sectionMap = new HashMap<>();
    static {
        sectionMap.put(ConstantsNode.class, "con");
        sectionMap.put(VariablesNode.class, "var");
        sectionMap.put(ObjectsNode.class, "obj");
        sectionMap.put(MethodNode.class, "pub");
        sectionMap.put(DataNode.class, "dat");
    }

    static Map<Class<?>, int[]> defaultTabStops = new HashMap<>();
    static {
        defaultTabStops.put(ConstantsNode.class, new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put(VariablesNode.class, new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put(ObjectsNode.class, new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put(MethodNode.class, new int[] {
            4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 56, 64
        });
        defaultTabStops.put(DataNode.class, new int[] {
            4, 8, 16, 24, 28, 32, 36, 40, 44, 48, 56, 64
        });
    }

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

        public SerializedPreferences() {
            showLineNumbers = true;
            showIndentLines = true;
            showEditorOutline = true;
            reloadOpenTabs = true;
            terminalBaudRate = 115200;
        }

        public Bounds window;
        public int[] weights;

        public Boolean showBrowser;
        public String[] roots;

        public boolean showLineNumbers;
        public String editorFont;
        public boolean showIndentLines;
        public int indentLinesSize;
        public boolean showEditorOutline;
        public String port;
        public String[] spin1LibraryPath;
        public boolean spin1CaseSensitiveSymbols;
        public String[] spin2LibraryPath;
        public boolean spin2CaseSensitiveSymbols;
        public List<String> lru = new ArrayList<String>();

        public boolean reloadOpenTabs;
        public Map<String, int[]> sectionTabStops;

        public String[] openTabs;
        public String lastPath;

        public Bounds terminalWindow;
        public boolean terminalLineInput;
        public boolean terminalLocalEcho;
        public String[] terminalHistory;
        public int terminalType;
        public String terminalFont;
        public int terminalBaudRate;

        public SearchPreferences search;

    }

    public static class SearchPreferences {
        public Bounds window;
        public List<String> findHistory = new ArrayList<String>();
        public List<String> replaceHistory = new ArrayList<String>();
        public boolean forwardSearch = true;
        public boolean caseSensitiveSearch;
        public boolean wrapSearch = true;
        public boolean wholeWordSearch;
        public boolean regexSearch;

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

    public Rectangle getWindowBounds() {
        if (preferences.window == null) {
            return null;
        }
        return new Rectangle(preferences.window.x, preferences.window.y, preferences.window.width, preferences.window.height);
    }

    public void setWindowBounds(Rectangle rect) {
        preferences.window = new Bounds(rect.x, rect.y, rect.width, rect.height);
    }

    public int[] getWeights() {
        return preferences.weights;
    }

    public void setWeights(int[] weights) {
        preferences.weights = weights;
    }

    public boolean getShowBrowser() {
        return preferences.showBrowser != null ? preferences.showBrowser : true;
    }

    public void setShowBrowser(boolean showBrowser) {
        boolean oldValue = getShowBrowser();
        preferences.showBrowser = showBrowser ? null : showBrowser;
        changeSupport.firePropertyChange(PROP_SHOW_BROWSER, oldValue, showBrowser);
    }

    public String[] getRoots() {
        if (preferences.roots == null || preferences.roots.length == 0) {
            return defaultVisiblePaths;
        }
        return preferences.roots;
    }

    public void setRoots(String[] roots) {
        String[] oldValue = preferences.roots;

        if (oldValue == roots) {
            return;
        }
        if (oldValue != null && (roots == null || roots.length == 0 || Arrays.deepEquals(roots, defaultVisiblePaths))) {
            preferences.roots = null;
            changeSupport.firePropertyChange(PROP_ROOTS, oldValue, defaultVisiblePaths);
            return;
        }
        if (oldValue == null && roots != null && (roots.length == 0 || Arrays.deepEquals(roots, defaultVisiblePaths))) {
            return;
        }
        if (Arrays.deepEquals(oldValue, roots)) {
            return;
        }
        changeSupport.firePropertyChange(PROP_ROOTS, preferences.roots, preferences.roots = roots);
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

    public boolean getShowIndentLines() {
        return preferences.showIndentLines;
    }

    public void setShowIndentLines(boolean showIndentLines) {
        changeSupport.firePropertyChange(PROP_SHOW_INDENT_LINES, preferences.showIndentLines, preferences.showIndentLines = showIndentLines);
    }

    public int getIndentLinesSize() {
        return preferences.indentLinesSize;
    }

    public void setIndentLinesSize(int indentLinesSize) {
        changeSupport.firePropertyChange(PROP_INDENT_LINES_SIZE, preferences.indentLinesSize, preferences.indentLinesSize = indentLinesSize);
    }

    public boolean getShowEditorOutline() {
        return preferences.showEditorOutline;
    }

    public void setShowEditorOutline(boolean showEditorOutline) {
        changeSupport.firePropertyChange(PROP_SHOW_EDITOR_OUTLINE, preferences.showEditorOutline, preferences.showEditorOutline = showEditorOutline);
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

    public File getLastPath() {
        if (preferences.lastPath == null || preferences.lastPath.isEmpty()) {
            return null;
        }
        return new File(preferences.lastPath);
    }

    public void setLastPath(File lastPath) {
        if (lastPath != null) {
            preferences.lastPath = lastPath.getAbsolutePath();
        }
        else {
            preferences.lastPath = null;
        }
    }

    public String getPort() {
        return preferences.port;
    }

    public void setPort(String port) {
        changeSupport.firePropertyChange(PROP_PORT, preferences.port, preferences.port = port);
    }

    public File[] getSpin1LibraryPath() {
        if (preferences.spin1LibraryPath != null) {
            if (preferences.spin1LibraryPath != null) {
                List<File> l = new ArrayList<>();
                for (int i = 0; i < preferences.spin1LibraryPath.length; i++) {
                    l.add(preferences.spin1LibraryPath[i] != null ? new File(preferences.spin1LibraryPath[i]) : defaultSpin1LibraryPath);
                }
                return l.toArray(new File[l.size()]);
            }
        }
        return new File[] {
            defaultSpin1LibraryPath
        };
    }

    public void setSpin1LibraryPath(File[] path) {
        List<String> l = new ArrayList<String>();
        for (int i = 0; i < path.length; i++) {
            l.add(defaultSpin1LibraryPath.equals(path[i]) ? null : path[i].getAbsolutePath());
        }
        String[] newValue = l.toArray(new String[l.size()]);
        if (newValue.length == 0 || (newValue.length == 1 && newValue[0] == null)) {
            if (preferences.spin1LibraryPath != null) {
                changeSupport.firePropertyChange(PROP_SPIN1_LIBRARY_PATH, preferences.spin1LibraryPath, preferences.spin1LibraryPath = null);
            }
        }
        else if (!Arrays.equals(newValue, preferences.spin1LibraryPath)) {
            changeSupport.firePropertyChange(PROP_SPIN1_LIBRARY_PATH, preferences.spin1LibraryPath, preferences.spin1LibraryPath = newValue);
        }
    }

    public boolean getSpin1CaseSensitiveSymbols() {
        return preferences.spin1CaseSensitiveSymbols;
    }

    public void setSpin1CaseSensitiveSymbols(boolean spin1CaseSensitiveSymbols) {
        changeSupport.firePropertyChange(PROP_SPIN1_CASE_SENSITIVE_SYMBOLS, preferences.spin1CaseSensitiveSymbols, preferences.spin1CaseSensitiveSymbols = spin1CaseSensitiveSymbols);
    }

    public File[] getSpin2LibraryPath() {
        if (preferences.spin2LibraryPath != null) {
            List<File> l = new ArrayList<>();
            for (int i = 0; i < preferences.spin2LibraryPath.length; i++) {
                l.add(preferences.spin2LibraryPath[i] != null ? new File(preferences.spin2LibraryPath[i]) : defaultSpin2LibraryPath);
            }
            return l.toArray(new File[l.size()]);
        }
        return new File[] {
            defaultSpin2LibraryPath
        };
    }

    public boolean getSpin2CaseSensitiveSymbols() {
        return preferences.spin2CaseSensitiveSymbols;
    }

    public void setSpin2CaseSensitiveSymbols(boolean spin2CaseSensitiveSymbols) {
        changeSupport.firePropertyChange(PROP_SPIN2_CASE_SENSITIVE_SYMBOLS, preferences.spin2CaseSensitiveSymbols, preferences.spin2CaseSensitiveSymbols = spin2CaseSensitiveSymbols);
    }

    public void setSpin2LibraryPath(File[] path) {
        List<String> l = new ArrayList<String>();
        for (int i = 0; i < path.length; i++) {
            l.add(defaultSpin2LibraryPath.equals(path[i]) ? null : path[i].getAbsolutePath());
        }
        String[] newValue = l.toArray(new String[l.size()]);
        if (newValue.length == 0 || (newValue.length == 1 && newValue[0] == null)) {
            if (preferences.spin2LibraryPath != null) {
                changeSupport.firePropertyChange(PROP_SPIN2_LIBRARY_PATH, preferences.spin2LibraryPath, preferences.spin2LibraryPath = null);
            }
        }
        else if (!Arrays.equals(newValue, preferences.spin2LibraryPath)) {
            changeSupport.firePropertyChange(PROP_SPIN2_LIBRARY_PATH, preferences.spin2LibraryPath, preferences.spin2LibraryPath = newValue);
        }
    }

    public int[] getTabStops(Class<?> clazz) {
        int[] result = null;

        String section = sectionMap.get(clazz);
        if (preferences.sectionTabStops != null && preferences.sectionTabStops.get(section) != null) {
            result = preferences.sectionTabStops.get(section);
        }
        if (result == null) {
            result = defaultTabStops.get(clazz);
        }

        return result;
    }

    public void setTabStops(Class<?> clazz, int[] tabStops) {
        if (Arrays.equals(tabStops, defaultTabStops.get(clazz))) {
            if (preferences.sectionTabStops != null) {
                preferences.sectionTabStops.remove(sectionMap.get(clazz));
                if (preferences.sectionTabStops.size() == 0) {
                    preferences.sectionTabStops = null;
                }
            }
        }
        else {
            if (preferences.sectionTabStops == null) {
                preferences.sectionTabStops = new HashMap<>();
            }
            preferences.sectionTabStops.put(sectionMap.get(clazz), tabStops);
        }
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

    public boolean getTerminalLineInput() {
        return preferences.terminalLineInput;
    }

    public void setTerminalLineInput(boolean terminalLineInput) {
        changeSupport.firePropertyChange(PROP_TERMINAL_LINE_INPUT, preferences.terminalLineInput, preferences.terminalLineInput = terminalLineInput);
    }

    public String[] getTerminalHistory() {
        return preferences.terminalHistory;
    }

    public void setTerminalHistory(String[] terminalHistory) {
        preferences.terminalHistory = terminalHistory;
    }

    public void setTerminalType(int n) {
        preferences.terminalType = n;
    }

    public int getTerminalType() {
        return preferences.terminalType;
    }

    public String getTerminalFont() {
        return preferences.terminalFont;
    }

    public void setTerminalFont(String terminalFont) {
        if (preferences.terminalFont != terminalFont) {
            changeSupport.firePropertyChange(PROP_TERMINAL_FONT, preferences.terminalFont, preferences.terminalFont = terminalFont);
        }
    }

    public int getTerminalBaudRate() {
        return preferences.terminalBaudRate;
    }

    public void setTerminalBaudRate(int terminalBaudRate) {
        preferences.terminalBaudRate = terminalBaudRate;
    }

    public boolean getTerminalLocalEcho() {
        return preferences.terminalLocalEcho;
    }

    public void setTerminalLocalEcho(boolean terminalLocalEcho) {
        changeSupport.firePropertyChange(PROP_TERMINAL_LOCAL_ECHO, preferences.terminalLocalEcho, preferences.terminalLocalEcho = terminalLocalEcho);
    }

    public SearchPreferences getSearchPreferences() {
        if (preferences.search == null) {
            preferences.search = new SearchPreferences();
        }
        return preferences.search;
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(preferencesFile, preferences);
    }
}
