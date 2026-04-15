/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class SerializedPreferences {

    public SerializedPreferences() {
        folderWeights = new HashMap<>();

        lru = new ArrayList<>();
        lruBookmarks = new HashMap<>();
        lruPositions = new HashMap<>();

        openTabs = new String[0];

        showLineNumbers = true;
        showIndentLines = true;
        showEditorOutline = true;
        highlightCurrentLine = true;
        expandOutlineSelection = true;
        reloadOpenTabs = true;

        hoverDocModifiers = 0;
        hyperlinkModifiers = 1;

        spin1RemovedUnusedMethods = true;
        spin1FastByteConstants = true;
        spin1FoldConstants = true;
        spin1WarnUnusedMethods = true;
        spin1WarnUnusedMethodVariables = true;
        spin1WarnUnusedVariables = true;

        spin2RemovedUnusedMethods = true;
        spin2WarnUnusedMethods = true;
        spin2WarnUnusedMethodVariables = true;
        spin2WarnUnusedVariables = true;

        terminal = new TerminalPreferences();
        console = new ConsolePreferences();
        spinFormat = new SpinFormatPreferences();

        packageLru = new ArrayList<>();

        autoDiscoverDevice = true;
        blacklistedPorts = new ArrayList<>();
    }

    public Bounds window;
    public String windowFont;
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
    public boolean showEditorOutlineSectionsBackground;
    public boolean expandOutlineSelection;
    public boolean highlightCurrentLine;
    public int hoverDocModifiers;
    public int hyperlinkModifiers;
    public SpinFormatPreferences spinFormat;

    public String port;

    public String[] spin1LibraryPath;
    public boolean spin1RemovedUnusedMethods;
    public boolean spin1CaseSensitiveSymbols;
    public boolean spin1FastByteConstants;
    public boolean spin1FoldConstants;
    public boolean spin1WarnUnusedMethods;
    public boolean spin1WarnUnusedMethodVariables;
    public boolean spin1WarnUnusedVariables;
    public String spin1Template;
    public String spin1ObjectTemplate;
    @JsonInclude(Include.NON_ABSENT)
    public Map<String, String> spin1Defines;

    public String[] spin2LibraryPath;
    public boolean spin2RemovedUnusedMethods;
    public boolean spin2CaseSensitiveSymbols;
    public boolean spin2ClockSetter;
    public boolean spin2WarnUnusedMethods;
    public boolean spin2WarnUnusedMethodVariables;
    public boolean spin2WarnUnusedVariables;
    public String spin2Template;
    public String spin2ObjectTemplate;
    @JsonInclude(Include.NON_ABSENT)
    public Map<String, String> spin2Defines;
    public boolean spin2Compress;

    public List<String> lru;
    public Map<String, Integer[]> lruPositions;
    public Map<String, Integer[]> lruBookmarks;
    public String currentTab;

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

    public RemoteDevice[] remoteDevices;

    public List<PackageFile> packageLru;

    public String p1ResetControl;
    public int p1ResetDelay;
    public String p2ResetControl;
    public int p2ResetDelay;

    public boolean autoDiscoverDevice;
    public List<String> blacklistedPorts;
}
