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

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class EditorCompiler implements Runnable {

    final String text;

    private Thread thread;

    public EditorCompiler(String text) {
        this.text = text;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Spin2Compiler compiler = new Spin2Compiler();
        try {
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
            Node root = subject.parse();

            compiler.compile(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
        onTerminated(compiler);
    }

    protected void onTerminated(Spin2Compiler compiler) {

    }

}
