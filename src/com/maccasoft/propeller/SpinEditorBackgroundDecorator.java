/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.eclipse.swt.graphics.Color;

import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.VariablesNode;

public class SpinEditorBackgroundDecorator {

    int[] sectionCount = new int[6];

    public SpinEditorBackgroundDecorator() {

    }

    public Color getLineBackground(Node root, int lineOffset) {
        Color color = getSectionBackground(null);

        if (root != null) {
            for (Node child : root.getChilds()) {
                if (lineOffset < child.getStartIndex()) {
                    break;
                }
                color = getSectionBackground(child);
            }
        }

        return color;
    }

    Color getSectionBackground(Node node) {
        Color result = null;

        if (node == null) {
            sectionCount[0] = sectionCount[1] = sectionCount[2] = sectionCount[3] = sectionCount[4] = sectionCount[5] = 0;
        }

        if (node instanceof VariablesNode) {
            result = getColor(255, 223, 191, sectionCount[1] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[1] = sectionCount[1] == 0 ? 1 : 0;
            }
        }
        else if (node instanceof ObjectsNode) {
            result = getColor(255, 191, 191, sectionCount[2] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[2] = sectionCount[2] == 0 ? 1 : 0;
            }
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).isPublic()) {
                result = getColor(191, 223, 255, sectionCount[3] == 0 ? 0 : -6);
                if (node != null) {
                    sectionCount[3] = sectionCount[3] == 0 ? 1 : 0;
                }
            }
            else {
                result = getColor(191, 248, 255, sectionCount[4] == 0 ? 0 : -6);
                if (node != null) {
                    sectionCount[4] = sectionCount[4] == 0 ? 1 : 0;
                }
            }
        }
        else if (node instanceof DataNode) {
            result = getColor(191, 255, 200, sectionCount[5] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[5] = sectionCount[5] == 0 ? 1 : 0;
            }
        }
        else {
            result = getColor(255, 248, 192, sectionCount[0] == 0 ? 0 : -6);
            if (node != null) {
                sectionCount[0] = sectionCount[0] == 0 ? 1 : 0;
            }
        }

        return result;
    }

    Color getColor(int r, int g, int b, int percent) {
        r += (int) (r / 100.0 * percent);
        g += (int) (g / 100.0 * percent);
        b += (int) (b / 100.0 * percent);
        return ColorRegistry.getColor(r, g, b);
    }
}
