/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.preferences;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maccasoft.propeller.Formatter.Align;
import com.maccasoft.propeller.Formatter.Case;

@JsonInclude(Include.NON_DEFAULT)
public class SpinFormatPreferences {

    public String sectionCase;
    public String builtInConstantsCase;
    public String pasmInstructionsCase;

    public String lineCommentAlign;
    public int lineCommentColumn;

    public boolean blockCommentIndentAlign;

    public SpinFormatPreferences() {
        sectionCase = "upper";
        builtInConstantsCase = "upper";
        pasmInstructionsCase = "lower";

        lineCommentAlign = "fixed";
        lineCommentColumn = 64;
        blockCommentIndentAlign = true;
    }

    public Case getSectionCase() {
        if (sectionCase != null) {
            switch (sectionCase.toLowerCase()) {
                case "lower":
                    return Case.Lower;
                case "upper":
                    return Case.Upper;
            }
        }
        return Case.None;
    }

    public void setSectionCase(Case sectionCase) {
        switch (sectionCase) {
            case Lower:
                this.sectionCase = "lower";
                break;
            case Upper:
                this.sectionCase = "upper";
                break;
            default:
                this.sectionCase = null;
                break;
        }
    }

    public Case getBuiltInConstantsCase() {
        if (builtInConstantsCase != null) {
            switch (builtInConstantsCase.toLowerCase()) {
                case "lower":
                    return Case.Lower;
                case "upper":
                    return Case.Upper;
            }
        }
        return Case.None;
    }

    public void setBuiltInConstantsCase(Case builtInConstantsCase) {
        switch (builtInConstantsCase) {
            case Lower:
                this.builtInConstantsCase = "lower";
                break;
            case Upper:
                this.builtInConstantsCase = "upper";
                break;
            default:
                this.builtInConstantsCase = null;
                break;
        }
    }

    public Case getPasmInstructionsCase() {
        if (pasmInstructionsCase != null) {
            switch (pasmInstructionsCase.toLowerCase()) {
                case "lower":
                    return Case.Lower;
                case "upper":
                    return Case.Upper;
            }
        }
        return Case.None;
    }

    public void setPasmInstructionsCase(Case pasmInstructionsCase) {
        switch (pasmInstructionsCase) {
            case Lower:
                this.pasmInstructionsCase = "lower";
                break;
            case Upper:
                this.pasmInstructionsCase = "upper";
                break;
            default:
                this.pasmInstructionsCase = null;
                break;
        }
    }

    public Align getLineCommentAlign() {
        if (lineCommentAlign != null) {
            switch (lineCommentAlign.toLowerCase()) {
                case "tabstop":
                    return Align.TabStop;
                case "fixed":
                    return Align.Fixed;
            }
        }
        return Align.None;
    }

    public void setLineCommentAlign(Align lineCommentAlign) {
        switch (lineCommentAlign) {
            case TabStop:
                this.lineCommentAlign = "tabstop";
                break;
            case Fixed:
                this.lineCommentAlign = "fixed";
                break;
            default:
                this.lineCommentAlign = null;
                break;
        }
    }

    public int getLineCommentColumn() {
        return lineCommentColumn;
    }

    public void setLineCommentColumn(int lineCommentColumn) {
        this.lineCommentColumn = lineCommentColumn;
    }

    public boolean getBlockCommentIndentAlign() {
        return blockCommentIndentAlign;
    }

    public void setBlockCommentIndentAlign(boolean blockCommentIndentAlign) {
        this.blockCommentIndentAlign = blockCommentIndentAlign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockCommentIndentAlign, builtInConstantsCase, lineCommentAlign, lineCommentColumn, pasmInstructionsCase, sectionCase);
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
        SpinFormatPreferences other = (SpinFormatPreferences) obj;
        return blockCommentIndentAlign == other.blockCommentIndentAlign && Objects.equals(builtInConstantsCase, other.builtInConstantsCase)
            && Objects.equals(lineCommentAlign, other.lineCommentAlign) && lineCommentColumn == other.lineCommentColumn && Objects.equals(pasmInstructionsCase, other.pasmInstructionsCase)
            && Objects.equals(sectionCase, other.sectionCase);
    }

}
