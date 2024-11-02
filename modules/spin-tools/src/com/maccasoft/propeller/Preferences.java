/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import java.util.Objects;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Preferences {

    public static final String PROP_SHOW_TOOLBAR = "showToolbar";
    public static final String PROP_SHOW_BROWSER = "showBrowser";
    public static final String PROP_SHOW_OBJECT_BROWSER = "showObjectBrowser";
    public static final String PROP_ROOTS = "roots";
    public static final String PROP_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String PROP_EDITOR_FONT = "editorFont";
    public static final String PROP_SHOW_INDENT_LINES = "showIndentLines";
    public static final String PROP_INDENT_LINES_SIZE = "showLinesSize";
    public static final String PROP_SHOW_SECTIONS_BACKGROUND = "showSectionsBackground";
    public static final String PROP_HIGHLIGHT_CURRENT_LINE = "highlightCurrentLine";
    public static final String PROP_SHOW_EDITOR_OUTLINE = "showEditorOutline";
    public static final String PROP_LRU = "lru";
    public static final String PROP_TOP_OBJECT = "topObject";
    public static final String PROP_PORT = "port";
    public static final String PROP_SPIN1_LIBRARY_PATH = "spin1LibraryPath";
    public static final String PROP_SPIN1_CASE_SENSITIVE_SYMBOLS = "spin1CaseSensitiveSymbols";
    public static final String PROP_SPIN2_LIBRARY_PATH = "spin2LibraryPath";
    public static final String PROP_SPIN2_CASE_SENSITIVE_SYMBOLS = "spin2CaseSensitiveSymbols";
    public static final String PROP_SPIN2_COMPRESS = "spin2Compress";
    public static final String PROP_TERMINAL_FONT = "terminalFont";
    public static final String PROP_TERMINAL_LINE_INPUT = "terminalLineInput";
    public static final String PROP_TERMINAL_LOCAL_ECHO = "terminalLocalEcho";
    public static final String PROP_CONSOLE_FONT = "consoleFont";
    public static final String PROP_CONSOLE_MAX_LINES = "consoleMaxLines";
    public static final String PROP_CONSOLE_WRITE_LOG_FILE = "consoleWriteLogFile";
    public static final String PROP_THEME = "theme";
    public static final String PROP_EXTERNAL_TOOLS = "externalTools";

    public static final String PREFERENCES_NAME = ".spin-tools";

    public static File defaultSpin1LibraryPath = new File(System.getProperty("APP_DIR"), "library/spin1").getAbsoluteFile();
    public static File defaultSpin2LibraryPath = new File(System.getProperty("APP_DIR"), "library/spin2").getAbsoluteFile();

    public static File[] defaultVisiblePaths = new File[] {
        new File(System.getProperty("user.home")).getAbsoluteFile()
    };

    static Map<Class<?>, String> sectionMap = new HashMap<>();
    static {
        sectionMap.put(ConstantsNode.class, "con");
        sectionMap.put(VariablesNode.class, "var");
        sectionMap.put(ObjectsNode.class, "obj");
        sectionMap.put(MethodNode.class, "pub");
        sectionMap.put(FunctionNode.class, "pub");
        sectionMap.put(DataNode.class, "dat");
    }

    static Map<String, int[]> defaultTabStops = new HashMap<>();
    static {
        defaultTabStops.put("con", new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put("var", new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put("obj", new int[] {
            4, 8, 16, 24, 32, 40, 48, 56, 64
        });
        defaultTabStops.put("pub", new int[] {
            4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 56, 64
        });
        defaultTabStops.put("dat", new int[] {
            8, 16, 24, 36, 40, 44, 48, 56, 64 // 4, 8, 16, 24, 28, 32, 36, 40, 44, 48, 56, 64
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

        @Override
        public int hashCode() {
            return Objects.hash(height, width, x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Bounds other = (Bounds) obj;
            return height == other.height && width == other.width && x == other.x && y == other.y;
        }

    }

    @JsonInclude(Include.NON_DEFAULT)
    public static class SerializedPreferences {

        public SerializedPreferences() {
            folderWeights = new HashMap<>();
            lru = new ArrayList<>();

            showLineNumbers = true;
            showIndentLines = true;
            showEditorOutline = true;
            highlightCurrentLine = true;
            reloadOpenTabs = true;

            terminal = new TerminalPreferences();
            console = new ConsolePreferences();
        }

        public Bounds window;
        public Map<String, int[]> folderWeights;

        public Boolean showToolbar;
        public Boolean showObjectBrowser;

        public Boolean showBrowser;
        public File[] roots;
        public File[] expandedPaths;

        public boolean showLineNumbers;
        public String editorFont;
        public boolean showIndentLines;
        public Boolean showSectionsBackground;
        public int indentLinesSize;
        public boolean showEditorOutline;
        public boolean highlightCurrentLine;
        public String port;

        public String[] spin1LibraryPath;
        public boolean spin1CaseSensitiveSymbols;
        public String spin1Template;
        @JsonInclude(Include.NON_ABSENT)
        public Map<String, String> spin1Defines;

        public String[] spin2LibraryPath;
        public boolean spin2CaseSensitiveSymbols;
        public boolean spin2ClockSetter;
        public String spin2Template;
        @JsonInclude(Include.NON_ABSENT)
        public Map<String, String> spin2Defines;
        public boolean spin2Compress;

        public List<String> lru;

        public boolean reloadOpenTabs;
        public Map<String, int[]> sectionTabStops;

        public String[] openTabs;
        public String lastPath;
        public String topObject;

        public TerminalPreferences terminal;

        public ConsolePreferences console;

        public SearchPreferences search;

        public String theme;

        public ExternalTool[] externalTools;

    }

    @JsonInclude(Include.NON_DEFAULT)
    public static class TerminalPreferences {

        public TerminalPreferences() {
            baudRate = 115200;
        }

        public Bounds window;
        public boolean lineInput;
        public boolean localEcho;
        public String[] history;
        public int type;
        public String font;
        public int baudRate;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(history);
            result = prime * result + Objects.hash(baudRate, font, lineInput, localEcho, type, window);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TerminalPreferences other = (TerminalPreferences) obj;
            return baudRate == other.baudRate && Objects.equals(font, other.font) && Arrays.equals(history, other.history) && lineInput == other.lineInput && localEcho == other.localEcho
                && type == other.type && Objects.equals(window, other.window);
        }

    }

    @JsonInclude(Include.NON_DEFAULT)
    public static class ConsolePreferences {

        public ConsolePreferences() {
            maxLines = 500;
            writeLogFile = true;
        }

        public String font;
        public int maxLines;
        public boolean writeLogFile;
        public boolean resetDeviceOnClose;

        @Override
        public int hashCode() {
            return Objects.hash(font, maxLines, writeLogFile, resetDeviceOnClose);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ConsolePreferences other = (ConsolePreferences) obj;
            return Objects.equals(font, other.font) && maxLines == other.maxLines && writeLogFile == other.writeLogFile && resetDeviceOnClose == other.resetDeviceOnClose;
        }

    }

    @JsonInclude(Include.NON_DEFAULT)
    public static class SearchPreferences {

        public SearchPreferences() {
            findHistory = new ArrayList<String>();
            replaceHistory = new ArrayList<String>();
            forwardSearch = true;
            wrapSearch = true;
        }

        public Bounds window;
        public List<String> findHistory;
        public List<String> replaceHistory;
        public boolean forwardSearch;
        public boolean caseSensitiveSearch;
        public boolean wrapSearch;
        public boolean wholeWordSearch;
        public boolean regexSearch;

    }

    @JsonInclude(Include.NON_DEFAULT)
    public static class ExternalTool {

        public String name;
        public String program;
        public String arguments;

        public ExternalTool() {

        }

        public ExternalTool(String name, String program, String arguments) {
            this.name = name;
            this.program = program;
            this.arguments = arguments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getArguments() {
            return arguments;
        }

        public void setArguments(String arguments) {
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return name;
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

    public int[] getWeights(String key) {
        return preferences.folderWeights.get(key);
    }

    public void setWeights(String key, int[] weights) {
        preferences.folderWeights.put(key, weights);
    }

    public boolean getShowBrowser() {
        return preferences.showBrowser != null ? preferences.showBrowser : true;
    }

    public void setShowBrowser(boolean showBrowser) {
        boolean oldValue = getShowBrowser();
        preferences.showBrowser = showBrowser ? null : showBrowser;
        changeSupport.firePropertyChange(PROP_SHOW_BROWSER, oldValue, showBrowser);
    }

    public File[] getRoots() {
        if (preferences.roots == null || preferences.roots.length == 0) {
            return defaultVisiblePaths;
        }
        return preferences.roots;
    }

    public void setRoots(File[] roots) {
        File[] oldValue = preferences.roots == null || preferences.roots.length == 0 ? null : getRoots();
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

    public File[] getExpandedPaths() {
        if (preferences.expandedPaths == null) {
            return new File[0];
        }
        return preferences.expandedPaths;
    }

    public void setExpandedPaths(File[] expandedPaths) {
        if (expandedPaths == null || expandedPaths.length == 0) {
            preferences.expandedPaths = null;
        }
        preferences.expandedPaths = expandedPaths;
    }

    public boolean getShowToolbar() {
        return preferences.showToolbar != null ? preferences.showToolbar : true;
    }

    public void setShowToolbar(boolean showToolbar) {
        boolean oldValue = getShowToolbar();
        preferences.showToolbar = showToolbar ? null : showToolbar;
        changeSupport.firePropertyChange(PROP_SHOW_TOOLBAR, oldValue, showToolbar);
    }

    public boolean getShowObjectBrowser() {
        return preferences.showObjectBrowser != null ? preferences.showObjectBrowser : true;
    }

    public void setShowObjectBrowser(boolean showObjectBrowser) {
        boolean oldValue = getShowObjectBrowser();
        preferences.showObjectBrowser = showObjectBrowser ? null : showObjectBrowser;
        changeSupport.firePropertyChange(PROP_SHOW_OBJECT_BROWSER, oldValue, showObjectBrowser);
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

    public boolean getShowSectionsBackground() {
        return preferences.showSectionsBackground != null ? preferences.showSectionsBackground : true;
    }

    public void setShowSectionsBackground(boolean showSectionsBackground) {
        boolean oldValue = getShowSectionsBackground();
        preferences.showSectionsBackground = showSectionsBackground ? null : showSectionsBackground;
        changeSupport.firePropertyChange(PROP_SHOW_SECTIONS_BACKGROUND, oldValue, showSectionsBackground);
    }

    public boolean getShowEditorOutline() {
        return preferences.showEditorOutline;
    }

    public void setShowEditorOutline(boolean showEditorOutline) {
        changeSupport.firePropertyChange(PROP_SHOW_EDITOR_OUTLINE, preferences.showEditorOutline, preferences.showEditorOutline = showEditorOutline);
    }

    public boolean getHighlightCurrentLine() {
        return preferences.highlightCurrentLine;
    }

    public void setHighlightCurrentLine(boolean highlightCurrentLine) {
        changeSupport.firePropertyChange(PROP_HIGHLIGHT_CURRENT_LINE, preferences.highlightCurrentLine, preferences.highlightCurrentLine = highlightCurrentLine);
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

    public File getTopObject() {
        return preferences.topObject != null ? new File(preferences.topObject) : null;
    }

    public void setTopObject(File topObject) {
        File oldTopObject = preferences.topObject != null ? new File(preferences.topObject) : null;
        if (topObject != null && !topObject.isAbsolute()) {
            topObject = topObject.getAbsoluteFile();
        }
        preferences.topObject = topObject != null ? topObject.getAbsolutePath() : null;
        changeSupport.firePropertyChange(PROP_TOP_OBJECT, oldTopObject, topObject);
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

    public File getSpin1Template() {
        return preferences.spin1Template != null ? new File(preferences.spin1Template) : null;
    }

    public void setSpin1Template(File spin1Template) {
        preferences.spin1Template = spin1Template != null ? spin1Template.getAbsolutePath() : null;
    }

    public boolean getSpin1CaseSensitiveSymbols() {
        return preferences.spin1CaseSensitiveSymbols;
    }

    public void setSpin1CaseSensitiveSymbols(boolean spin1CaseSensitiveSymbols) {
        changeSupport.firePropertyChange(PROP_SPIN1_CASE_SENSITIVE_SYMBOLS, preferences.spin1CaseSensitiveSymbols, preferences.spin1CaseSensitiveSymbols = spin1CaseSensitiveSymbols);
    }

    public Map<String, String> getSpin1Defines() {
        return preferences.spin1Defines != null ? preferences.spin1Defines : new HashMap<>();
    }

    public void setSpin1Defines(Map<String, String> spin1Defines) {
        if (spin1Defines.isEmpty()) {
            preferences.spin1Defines = null;
        }
        else {
            if (preferences.spin1Defines == null) {
                preferences.spin1Defines = new HashMap<>();
            }
            preferences.spin1Defines.putAll(spin1Defines);
        }
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

    public boolean getSpin2ClockSetter() {
        return preferences.spin2ClockSetter;
    }

    public void setSpin2ClockSetter(boolean spin2ClockSetter) {
        preferences.spin2ClockSetter = spin2ClockSetter;
    }

    public File getSpin2Template() {
        return preferences.spin2Template != null ? new File(preferences.spin2Template) : null;
    }

    public void setSpin2Template(File spin2Template) {
        preferences.spin2Template = spin2Template != null ? spin2Template.getAbsolutePath() : null;
    }

    public Map<String, String> getSpin2Defines() {
        return preferences.spin2Defines != null ? preferences.spin2Defines : new HashMap<>();
    }

    public void setSpin2Defines(Map<String, String> spin2Defines) {
        if (spin2Defines.isEmpty()) {
            preferences.spin2Defines = null;
        }
        else {
            if (preferences.spin2Defines == null) {
                preferences.spin2Defines = new HashMap<>();
            }
            preferences.spin2Defines.putAll(spin2Defines);
        }
    }

    public boolean getSpin2Compress() {
        return preferences.spin2Compress;
    }

    public void setSpin2Compress(boolean spin2Compress) {
        changeSupport.firePropertyChange(PROP_SPIN2_COMPRESS, preferences.spin2Compress, preferences.spin2Compress = spin2Compress);
    }

    public int[] getTabStops(Class<?> clazz) {
        int[] result = null;

        String section = sectionMap.get(clazz);
        if (preferences.sectionTabStops != null && preferences.sectionTabStops.get(section) != null) {
            result = preferences.sectionTabStops.get(section);
        }
        if (result == null) {
            result = defaultTabStops.get(section);
        }

        return result;
    }

    public void setTabStops(Class<?> clazz, int[] tabStops) {
        String section = sectionMap.get(clazz);
        if (Arrays.equals(tabStops, defaultTabStops.get(section))) {
            if (preferences.sectionTabStops != null) {
                preferences.sectionTabStops.remove(section);
                if (preferences.sectionTabStops.size() == 0) {
                    preferences.sectionTabStops = null;
                }
            }
        }
        else {
            if (preferences.sectionTabStops == null) {
                preferences.sectionTabStops = new HashMap<>();
            }
            preferences.sectionTabStops.put(section, tabStops);
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
        if (preferences.terminal.window == null) {
            return null;
        }
        return new Rectangle(preferences.terminal.window.x, preferences.terminal.window.y, preferences.terminal.window.width, preferences.terminal.window.height);
    }

    public void setTerminalWindow(Rectangle rect) {
        preferences.terminal.window = new Bounds(rect.x, rect.y, rect.width, rect.height);
    }

    public boolean getTerminalLineInput() {
        return preferences.terminal.lineInput;
    }

    public void setTerminalLineInput(boolean terminalLineInput) {
        changeSupport.firePropertyChange(PROP_TERMINAL_LINE_INPUT, preferences.terminal.lineInput, preferences.terminal.lineInput = terminalLineInput);
    }

    public String[] getTerminalHistory() {
        return preferences.terminal.history;
    }

    public void setTerminalHistory(String[] terminalHistory) {
        preferences.terminal.history = terminalHistory;
    }

    public void setTerminalType(int n) {
        preferences.terminal.type = n;
    }

    public int getTerminalType() {
        return preferences.terminal.type;
    }

    public String getTerminalFont() {
        return preferences.terminal.font;
    }

    public void setTerminalFont(String terminalFont) {
        if (preferences.terminal.font != terminalFont) {
            changeSupport.firePropertyChange(PROP_TERMINAL_FONT, preferences.terminal.font, preferences.terminal.font = terminalFont);
        }
    }

    public int getTerminalBaudRate() {
        return preferences.terminal.baudRate;
    }

    public void setTerminalBaudRate(int terminalBaudRate) {
        preferences.terminal.baudRate = terminalBaudRate;
    }

    public boolean getTerminalLocalEcho() {
        return preferences.terminal.localEcho;
    }

    public void setTerminalLocalEcho(boolean terminalLocalEcho) {
        changeSupport.firePropertyChange(PROP_TERMINAL_LOCAL_ECHO, preferences.terminal.localEcho, preferences.terminal.localEcho = terminalLocalEcho);
    }

    public String getConsoleFont() {
        return preferences.console.font;
    }

    public void setConsoleFont(String terminalFont) {
        if (preferences.console.font != terminalFont) {
            changeSupport.firePropertyChange(PROP_CONSOLE_FONT, preferences.console.font, preferences.console.font = terminalFont);
        }
    }

    public int getConsoleMaxLines() {
        return preferences.console.maxLines;
    }

    public void setConsoleMaxLines(int maxLines) {
        changeSupport.firePropertyChange(PROP_CONSOLE_MAX_LINES, preferences.console.maxLines, preferences.console.maxLines = maxLines);
    }

    public boolean getConsoleWriteLogFile() {
        return preferences.console.writeLogFile;
    }

    public void setConsoleWriteLogFile(boolean writeLogFile) {
        changeSupport.firePropertyChange(PROP_CONSOLE_WRITE_LOG_FILE, preferences.console.writeLogFile, preferences.console.writeLogFile = writeLogFile);
    }

    public boolean getConsoleResetDeviceOnClose() {
        return preferences.console.resetDeviceOnClose;
    }

    public void setConsoleResetDeviceOnClose(boolean resetDeviceOnClose) {
        preferences.console.resetDeviceOnClose = resetDeviceOnClose;
    }

    public SearchPreferences getSearchPreferences() {
        if (preferences.search == null) {
            preferences.search = new SearchPreferences();
        }
        return preferences.search;
    }

    public String getTheme() {
        return preferences.theme;
    }

    public void setTheme(String theme) {
        changeSupport.firePropertyChange(PROP_THEME, preferences.theme, preferences.theme = theme);
    }

    public ExternalTool[] getExternalTools() {
        if (preferences.externalTools == null) {
            return new ExternalTool[0];
        }
        return preferences.externalTools;
    }

    public void setExternalTools(ExternalTool[] externalTools) {
        changeSupport.firePropertyChange(PROP_EXTERNAL_TOOLS, preferences.externalTools, preferences.externalTools = externalTools.length != 0 ? externalTools : null);
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        if (instance == this) {
            mapper.writeValue(preferencesFile, preferences);
        }
    }
}
