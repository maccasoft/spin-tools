/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.Compiler;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.ByteDataObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;

public class Spin1Compiler extends Compiler {

    boolean fastByteConstants;
    boolean foldConstants;
    boolean optimizeStrings;

    protected List<ObjectInfo> childObjects = new ArrayList<>();

    protected boolean errors;
    protected List<CompilerException> messages = new ArrayList<>();

    Spin1ObjectCompiler objectCompiler;

    public Spin1Compiler() {

    }

    public Spin1Compiler(boolean caseSensitive) {
        super(caseSensitive);
    }

    public boolean isFastByteConstants() {
        return fastByteConstants;
    }

    public void setFastByteConstants(boolean fastByteConstants) {
        this.fastByteConstants = fastByteConstants;
    }

    public boolean isFoldConstants() {
        return foldConstants;
    }

    public void setFoldConstants(boolean foldConstants) {
        this.foldConstants = foldConstants;
    }

    public boolean isOptimizeStrings() {
        return optimizeStrings;
    }

    public void setOptimizeStrings(boolean optimizeStrings) {
        this.optimizeStrings = optimizeStrings;
    }

    @Override
    public void addDefine(String identifier, String value) {
        Token token;

        List<Token> list = new ArrayList<>();
        TokenStream stream = new Spin1TokenStream(value);
        while ((token = stream.nextToken()).type != Token.EOF) {
            list.add(token);
        }
        addDefine(identifier, list);
    }

    @Override
    public Spin1Object compile(File file) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        Spin1Parser parser = new Spin1Parser(text);
        Spin1Object object = compile(file, parser.parse());

        if (hasErrors()) {
            throw new CompilerException(messages);
        }

        return object;
    }

    @Override
    public Spin1Object compile(File rootFile, RootNode root) {
        Spin1Object obj = compileObject(rootFile, root);

        Spin1Object object = new Spin1Object();
        object.setClkFreq(obj.getClkFreq());
        object.setClkMode(obj.getClkMode());

        object.writeLong(object.getClkFreq(), "CLKFREQ");
        object.writeByte(object.getClkMode(), "CLKMODE");
        ByteDataObject checksum = object.writeByte(0, "Checksum");

        WordDataObject pbase = object.writeWord(0, "PBASE");
        WordDataObject vbase = object.writeWord(0, "VBASE");
        WordDataObject dbase = object.writeWord(0, "DBASE");

        WordDataObject pcurr = object.writeWord(0, "PCURR");
        WordDataObject dcurr = object.writeWord(0, "DCURR");

        pbase.setValue(object.getSize());

        object.writeObject(obj);

        vbase.setValue(object.getSize());

        int offset = obj.getVarSize() + 8;
        dbase.setValue(object.getSize() + offset);

        if (!(obj.getObject(4) instanceof LongDataObject)) {
            logMessage(new CompilerException(CompilerException.ERROR, rootFile, "No PUB routines found", (Object) null));
            return null;
        }
        pcurr.setValue((int) (pbase.getValue() + (((LongDataObject) obj.getObject(4)).getValue() & 0xFFFF)));

        offset = 4 + obj.getDcurr();
        dcurr.setValue(dbase.getValue() + offset);

        try {
            byte[] data = object.getBinary();

            byte sum = 0;
            for (int i = 0; i < data.length; i++) {
                sum += data[i];
            }
            checksum.setValue(0x14 - sum);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;
    }

    public ObjectInfo getObjectInfo(String name) {
        return objectCompiler.objects.get(name);
    }

    public Spin1Object compileObject(File rootFile, RootNode root) {
        int memoryOffset = 16;

        objectCompiler = new Spin1ObjectCompiler(this, rootFile);
        objectCompiler.compileStep1(root);

        objectCompiler.compileStep2(true);
        for (ObjectInfo info : childObjects) {
            info.compiler.compileStep2(false);
        }

        Spin1Object object = objectCompiler.generateObject(memoryOffset);
        object.getObject(0).setText("Object \"" + rootFile.getName() + "\" header (var size " + object.getVarSize() + ")");
        memoryOffset += object.getSize();

        for (ObjectInfo info : childObjects) {
            info.offset = object.getSize();
            SpinObject linkedObject = info.compiler.generateObject(memoryOffset);
            linkedObject.getObject(0).setText("Object \"" + info.file.getName() + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
            memoryOffset += linkedObject.getSize();
        }

        for (ObjectInfo info : childObjects) {
            for (LinkDataObject linkData : info.compiler.getObjectLinks()) {
                for (ObjectInfo info2 : childObjects) {
                    if (linkData.isObject(info2.compiler)) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info2.file.getName(), linkData.getOffset(), linkData.getVarOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : objectCompiler.getObjectLinks()) {
            for (ObjectInfo info : childObjects) {
                if (linkData.isObject(info.compiler)) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info.file.getName(), linkData.getOffset(), linkData.getVarOffset()));
                    break;
                }
            }
        }

        tree = buildFrom(objectCompiler);

        errors = objectCompiler.hasErrors();

        int stackRequired = 16;
        if (objectCompiler.getScope().hasSymbol("_STACK")) {
            stackRequired = objectCompiler.getScope().getLocalSymbol("_STACK").getNumber().intValue();
        }
        if (objectCompiler.getScope().hasSymbol("_FREE")) {
            stackRequired += objectCompiler.getScope().getLocalSymbol("_FREE").getNumber().intValue();
        }

        if (stackRequired > 0x2000) {
            logMessage(new CompilerException(rootFile, "_STACK and _FREE must sum to under 8k longs.", null));
        }
        else {
            int requiredSize = object.getSize() + object.getVarSize() + (stackRequired << 2);
            if (requiredSize >= 0x8000) {
                logMessage(new CompilerException(rootFile, "program exceeds runtime memory limit by " + ((requiredSize - 0x8000) >> 2) + " longs.", null));
            }
        }

        messages.addAll(objectCompiler.getMessages());
        for (ObjectInfo info : childObjects) {
            messages.addAll(info.compiler.getMessages());
        }

        return object;

    }

    @Override
    public Context getContext() {
        return objectCompiler.getScope();
    }

    @Override
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        RootNode objectRoot = getParsedSource(file);
        ObjectCompiler objectCompiler = new Spin1ObjectCompiler(this, parent, file);

        while (parent != null) {
            if (file.equals(parent.getFile())) {
                throw new Exception("illegal circular reference");
            }
            parent = parent.getParent();
        }

        ObjectInfo info = new ObjectInfo(file, objectCompiler, parameters);

        int index = childObjects.indexOf(info);
        if (index != -1) {
            info = childObjects.remove(index);
        }
        childObjects.add(info);
        objectCompiler.compileStep1(objectRoot);

        return info;
    }

    public ObjectInfo getObjectInclude(String fileName, Map<String, Expression> parameters) {
        File objectFile = getFile(fileName, ".spin");
        if (objectFile != null) {
            RootNode objectRoot = getParsedSource(objectFile);
            if (objectRoot != null) {
                ObjectCompiler objectCompiler = new Spin1ObjectCompiler(this, objectFile);
                ObjectInfo info = new ObjectInfo(objectFile, objectCompiler, parameters);
                int index = childObjects.indexOf(info);
                if (index != -1) {
                    return childObjects.get(index);
                }
                objectCompiler.compileStep1(objectRoot);
                return info;
            }
        }
        return null;
    }

    protected byte[] getBinaryFile(String fileName) {
        return getResource(fileName);
    }

    protected void logMessage(CompilerException message) {
        if (message.type == CompilerException.ERROR) {
            errors = true;
        }
        messages.add(message);
    }

    @Override
    public boolean hasErrors() {
        return errors;
    }

    @Override
    public List<CompilerException> getMessages() {
        return messages;
    }

}
