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
public class SearchPreferences {

    public SearchPreferences() {
        searchFromTop = true;
        forwardSearch = true;
        wrapSearch = true;
    }

    public Bounds window;
    public String[] findHistory;
    public String[] replaceHistory;
    public boolean searchFromTop;
    public boolean forwardSearch;
    public boolean caseSensitiveSearch;
    public boolean wrapSearch;
    public boolean wholeWordSearch;
    public boolean regexSearch;

}
