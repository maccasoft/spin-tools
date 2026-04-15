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
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class PackageFile {

    public String file;
    public Set<String> bundles = new HashSet<>();

    public PackageFile() {

    }

    public PackageFile(File file, Set<String> bundles) {
        this.file = file.getAbsolutePath();
        this.bundles.addAll(bundles);
    }

    public PackageFile(File file) {
        this.file = file.getAbsolutePath();
    }

    public File getFile() {
        return file != null ? new File(file) : null;
    }

    public void setFile(File file) {
        this.file = file.getAbsolutePath();
    }

    public Set<String> getBundles() {
        return bundles;
    }

    public void setBundleEnabled(String id, boolean enabled) {
        if (enabled) {
            bundles.add(id);
        }
        else {
            bundles.remove(id);
        }
    }

    public boolean getBundleEnabled(String id) {
        return bundles.contains(id);
    }

    public boolean hasBundlesEnabled() {
        return !bundles.isEmpty();
    }

}
