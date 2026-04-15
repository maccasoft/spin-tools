/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.io.File;

import com.maccasoft.propeller.internal.FileUtils;

public abstract class SourceProvider {

    public static final SourceProvider NULL = new SourceProvider() {

        @Override
        public RootNode getParsedSource(File file) {
            return null;
        }

        @Override
        public File getFile(String name) {
            return null;
        }

    };

    public RootNode getParsedSource(File file) {
        try {
            String fileName = file.getName();
            if (fileName.indexOf('.') != -1) {
                String suffix = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
                String source = getSource(file);
                if (source != null) {
                    return Parser.parse(suffix, source);
                }
            }
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

    public String getSource(File file) {
        try {
            return FileUtils.loadFromFile(file);
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

    public abstract File getFile(String name);

}
