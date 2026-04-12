/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class ExternalTool {

    public static final String EDITOR_NONE = "none";
    public static final String EDITOR_WARN_UNSAVED = "warn-unsaved";
    public static final String EDITOR_AUTOSAVE = "autosave";

    public static final String DEFAULT_ACTION = EDITOR_WARN_UNSAVED;

    public String name;
    public String program;
    public String arguments;
    public String editorAction;
    public boolean showConsole;
    public boolean runInBackground;

    public ExternalTool() {
        this.name = "";
        this.program = "";
        this.arguments = "";
        this.editorAction = DEFAULT_ACTION;
        this.showConsole = true;
    }

    public ExternalTool(ExternalTool other) {
        this.name = other.name;
        this.program = other.program;
        this.arguments = other.arguments;
        this.editorAction = other.editorAction;
        this.runInBackground = other.runInBackground;
        this.showConsole = other.showConsole;
    }

    public ExternalTool(String name, String program, String arguments) {
        this.name = name;
        this.program = program;
        this.arguments = arguments;
        this.editorAction = DEFAULT_ACTION;
        this.showConsole = true;
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

    public String getEditorAction() {
        return editorAction != null ? editorAction : DEFAULT_ACTION;
    }

    public void setEditorAction(String editorAction) {
        this.editorAction = editorAction;
    }

    public boolean isShowConsole() {
        return showConsole;
    }

    public void setShowConsole(boolean showConsole) {
        this.showConsole = showConsole;
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public void setRunInBackground(boolean runInBackground) {
        this.runInBackground = runInBackground;
    }

    @Override
    public String toString() {
        return name;
    }

}
