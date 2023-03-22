/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;
import com.maccasoft.propeller.spinc.CParser;
import com.maccasoft.propeller.spinc.CTokenStream;

public abstract class Parser {

    public static Node parse(String suffix, String text) {
        Parser parser = getInstance(suffix, text);
        if (parser != null) {
            return parser.parse();
        }
        return null;
    }

    public static Parser getInstance(String suffix, String text) {
        switch (suffix.toLowerCase()) {
            case ".spin":
                return new Spin1Parser(new Spin1TokenStream(text));
            case ".spin2":
                return new Spin2Parser(new Spin2TokenStream(text));
            case ".c":
            case ".cpp":
                return new CParser(new CTokenStream(text));
        }
        return null;
    }

    public abstract Node parse();
}
