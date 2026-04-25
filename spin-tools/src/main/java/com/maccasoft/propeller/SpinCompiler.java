/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.FileDataObject;
import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortEvent;
import com.maccasoft.propeller.devices.ComPortEventListener;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.devices.SerialComPort;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spinc.Spin1CCompiler;
import com.maccasoft.propeller.spinc.Spin2CCompiler;

import jssc.SerialPort;

public class SpinCompiler {

    public static final String APP_TITLE = "Spin Tools Compiler";
    public static final String APP_VERSION = SpinTools.APP_VERSION;

    static final int NONE = 0;

    static final int P1 = 1;
    static final int P2 = 2;

    static final int RAM = 1;
    static final int FLASH = 2;

    static final int STANDARD = 1;
    static final int PST = 2;

    static final String ipAddressPattern = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
    static final String macAddressPattern = "(?:[0-9A-Fa-f]{2}[:-]){5}(?:[0-9A-Fa-f]{2})";
    static final String pragmaTargetRegex = "/\\*[\\s\\S]*?\\*/|//.*|\"([^\"\\\\]|\\\\.)*\"|(#pragma\\s+target\\s+P1|P2)";

    static Set<String> knownExtensions = new HashSet<>(Arrays.asList(".spin", ".spin2", ".c", ".p1asm", ".p2asm"));

    List<File> libraryPaths = new ArrayList<File>();
    Map<String, String> defines = new HashMap<>();

    String outName;

    boolean writeBinary;
    boolean writeFlashBinary;
    boolean writeDatBinary;
    boolean writeListing;

    int target = NONE;
    boolean debug;
    boolean compressBinary;

    boolean noWarnings;
    boolean removeUnusedMethods;
    boolean warnUnusedMethods;
    boolean warnUnusedMethodVariables;
    boolean warnUnusedVariables;

    boolean caseSensitive;
    boolean fasterByteConstants;
    boolean foldConstants;

    String portName;
    int uploadMode = NONE;
    ComPort.Control resetControl = ComPort.Control.DtrRts;
    int terminalMode;
    int terminalModeBaud;
    boolean terminalEcho;

    boolean showWifiModules;
    String wifiResetPin;

    boolean quiet;

    public int run(String[] args) throws Exception {
        Options options = createOptions();
        String argument = parseCommandLine(args, options);

        println(APP_TITLE + " - Version " + APP_VERSION);
        println("Copyright (c) 2021-26 Marco Maccaferri and others. All rights reserved.");
        println("");

        if (showWifiModules) {
            Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
            for (DeviceDescriptor descr : list) {
                println(String.format("Name: '%s', IP: %s, MAC: %s", descr.name, descr.inetAddr.getHostAddress(), descr.mac_address));
            }
            if (argument == null) {
                return 0;
            }
        }

        if (argument == null && terminalMode == NONE) {
            HelpFormatter help = new HelpFormatter();
            help.setWidth(-1);
            help.setOptionComparator(null);
            help.printHelp("spinc [options] <file.spin | file.spin2 | file.c>", null, options, null, false);
            return 1;
        }

        if (argument != null) {
            byte[] binaryData;
            if (argument.toLowerCase().endsWith(".bin") || argument.toLowerCase().endsWith(".binary")) {
                binaryData = FileUtils.loadBinaryFromFile(new File(argument));
                if (target == NONE) {
                    byte checksum = 0;
                    for (int i = 0; i < binaryData.length; i++) {
                        checksum += binaryData[i];
                    }
                    target = checksum == 0x14 ? P1 : P2;
                }
            }
            else {
                File fileToCompile = getFileToCompile(argument);
                binaryData = compile(fileToCompile);
            }
            if (uploadMode != NONE) {
                ComPort serialPort = getSerialPort();
                if (serialPort == null) {
                    println("No serial port specified");
                    return 1;
                }
                upload(serialPort, binaryData);
                if (terminalMode != NONE) {
                    runTerminal(serialPort);
                }
            }
            else {
                println("Done.");
            }
        }
        else {
            ComPort serialPort = getSerialPort();
            if (serialPort == null) {
                println("No serial port specified");
                return 1;
            }
            runTerminal(serialPort);
        }

        return 0;
    }

    Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("L").desc("add a directory to the library path").hasArg().argName("path").build());
        options.addOption(Option.builder("D").desc("add a define").hasArg().argName("define").build());

        options.addOption(Option.builder("o").desc("output file name").hasArg().argName("file").build());

        OptionGroup binaryOptions = new OptionGroup();
        binaryOptions.addOption(new Option("b", false, "output binary file"));
        binaryOptions.addOption(new Option("e", false, "output flash binary file (P2 only)"));
        binaryOptions.addOption(new Option("c", false, "output only DAT sections"));
        options.addOptionGroup(binaryOptions);
        options.addOption(new Option("l", "list", false, "output listing file"));
        options.addOption(new Option("d", "debug", false, "enable debug (P2 only)"));
        options.addOption(new Option("z", false, "compress binary (P2 only)"));

        OptionGroup targetOptions = new OptionGroup();
        targetOptions.addOption(new Option("p1", false, "compile for P1 target (C source only)"));
        targetOptions.addOption(new Option("p2", false, "compile for P2 target (C source only)"));
        options.addOptionGroup(targetOptions);

        OptionGroup uploadOptions = new OptionGroup();
        uploadOptions.addOption(new Option("r", false, "upload program to ram and run"));
        uploadOptions.addOption(new Option("f", false, "upload program to flash/eeprom and run"));
        options.addOptionGroup(uploadOptions);

        options.addOption(new Option("u", false, "enable unused methods removal"));

        options.addOption(new Option(null, "no-warn", false, "disable all warnings"));
        options.addOption(new Option(null, "no-warn-unused-methods", false, "disable unused methods warning"));
        options.addOption(new Option(null, "no-warn-unused-method-variables", false, "disable unused method variabless warning"));
        options.addOption(new Option(null, "no-warn-unused-variables", false, "disable unused variabless warning"));

        options.addOption(new Option("C", "x-case", false, "case-sensitive spin symbols"));

        options.addOption(new Option("O", true, "optimizations"));
        options.addOption(new Option("Ob", false, "faster byte constants (P1)"));
        options.addOption(new Option("Of", false, "fold constants (P1)"));

        options.addOption(Option.builder("p").desc("serial port").hasArg().argName("port").build());
        OptionGroup terminalOptions = new OptionGroup();
        terminalOptions.addOption(Option.builder("t").desc("enter terminal mode after upload (optional baud rate)").argName("baud").optionalArg(true).build());
        terminalOptions.addOption(Option.builder("te").desc("enter terminal mode with local echo after upload (optional baud rate)").argName("baud").optionalArg(true).build());
        terminalOptions.addOption(Option.builder("T").desc("enter PST terminal mode after upload (optional baud rate)").argName("baud").optionalArg(true).build());
        terminalOptions.addOption(Option.builder("Te").desc("enter PST terminal mode with local echo after upload (optional baud rate)").argName("baud").optionalArg(true).build());
        options.addOptionGroup(terminalOptions);

        OptionGroup resetOptions = new OptionGroup();
        resetOptions.addOption(new Option("dtr", false, "use DTR for reset"));
        resetOptions.addOption(new Option("rts", false, "use RTS for reset"));
        resetOptions.addOption(new Option(null, "reset-control", true, "set reset control dtr-rts (default), dtr or rts"));
        options.addOptionGroup(resetOptions);

        options.addOption(new Option("W", false, "show all discovered wifi modules"));
        options.addOption(new Option(null, "reset-pin", true, "set wifi module reset pin number"));

        options.addOption("q", false, "quiet mode");

        return options;
    }

    String parseCommandLine(String[] args, Options options) throws ParseException {
        CommandLine cmd = new DefaultParser().parse(options, args);

        if (cmd.hasOption('L')) {
            for (String s : cmd.getOptionValues('L')) {
                libraryPaths.add(new File(s));
            }
        }

        if (cmd.hasOption('D')) {
            boolean hasErrors = false;
            Pattern p1 = Pattern.compile("([A-Za-z_][A-Za-z0-9_]+)=(.+)");
            Pattern p2 = Pattern.compile("([A-Za-z_][A-Za-z0-9_]+)");

            for (String s : cmd.getOptionValues('D')) {
                Matcher m = p1.matcher(s);
                if (m.matches()) {
                    defines.put(m.group(1), m.group(2));
                }
                else {
                    m = p2.matcher(s);
                    if (m.matches()) {
                        defines.put(m.group(1), "");
                    }
                    else {
                        println("Invalid command line option: " + s);
                        hasErrors = true;
                    }
                }
            }

            if (hasErrors) {
                System.exit(1);
            }
        }

        if (cmd.hasOption('o')) {
            outName = cmd.getOptionValue('o');
        }

        writeBinary = cmd.hasOption('b');
        writeFlashBinary = cmd.hasOption('e');
        writeDatBinary = cmd.hasOption('c');
        writeListing = cmd.hasOption('l');

        if (cmd.hasOption("p1")) {
            target = P1;
        }
        else if (cmd.hasOption("p2")) {
            target = P2;
        }
        debug = cmd.hasOption('d');
        compressBinary = cmd.hasOption('z');

        removeUnusedMethods = cmd.hasOption('u');
        warnUnusedMethods = !cmd.hasOption("no-warn-unused-methods");
        warnUnusedMethodVariables = !cmd.hasOption("no-warn-unused-method-variables");
        warnUnusedVariables = !cmd.hasOption("no-warn-unused-variables");
        noWarnings = cmd.hasOption("no-warn");

        caseSensitive = cmd.hasOption("C") || cmd.hasOption("x-case");

        String optimizations = cmd.hasOption("O") ? cmd.getOptionValue("O") : "";
        fasterByteConstants = cmd.hasOption("Ob") || optimizations.contains("b");
        foldConstants = cmd.hasOption("Of") || optimizations.contains("f");

        if (cmd.hasOption("p")) {
            portName = cmd.getOptionValue("p");
        }

        if (cmd.hasOption('r')) {
            uploadMode = RAM;
        }
        else if (cmd.hasOption("f")) {
            uploadMode = FLASH;
        }

        if (cmd.hasOption("dtr") || "dtr".equals(cmd.getOptionValue("reset-control"))) {
            resetControl = ComPort.Control.Dtr;
        }
        else if (cmd.hasOption("rts") || "rts".equals(cmd.getOptionValue("reset-control"))) {
            resetControl = ComPort.Control.Rts;
        }

        if (cmd.hasOption('t')) {
            terminalMode = STANDARD;
            String value = cmd.getOptionValue('t');
            if (value != null) {
                terminalModeBaud = Integer.parseInt(value);
            }
        }
        else if (cmd.hasOption("te")) {
            terminalMode = STANDARD;
            String value = cmd.getOptionValue("te");
            if (value != null) {
                terminalModeBaud = Integer.parseInt(value);
            }
            terminalEcho = true;
        }
        else if (cmd.hasOption('T')) {
            terminalMode = PST;
            String value = cmd.getOptionValue('T');
            if (value != null) {
                terminalModeBaud = Integer.parseInt(value);
            }
        }
        else if (cmd.hasOption("Te")) {
            terminalMode = PST;
            String value = cmd.getOptionValue("Te");
            if (value != null) {
                terminalModeBaud = Integer.parseInt(value);
            }
            terminalEcho = true;
        }

        showWifiModules = cmd.hasOption("W");
        wifiResetPin = cmd.getOptionValue("reset-pin");

        quiet = cmd.hasOption('q');
        if (cmd.getArgList().size() != 1 && !cmd.hasOption("W")) {
            quiet = false;
        }

        return cmd.getArgList().size() == 1 ? cmd.getArgList().getFirst() : null;
    }

    File getFileToCompile(String s) throws Exception {
        File fileToCompile = new File(s).getAbsoluteFile();

        String name = fileToCompile.getName();
        if (!name.contains(".")) {
            File file = new File(fileToCompile.getParentFile(), name + ".spin");
            if (file.exists()) {
                return fileToCompile;
            }
            file = new File(fileToCompile.getParentFile(), name + ".spin2");
            if (file.exists()) {
                return fileToCompile;
            }
            file = new File(fileToCompile.getParentFile(), name + ".p1asm");
            if (file.exists()) {
                return fileToCompile;
            }
            file = new File(fileToCompile.getParentFile(), name + ".p2asm");
            if (file.exists()) {
                return fileToCompile;
            }
            file = new File(fileToCompile.getParentFile(), name + ".c");
            if (file.exists()) {
                return fileToCompile;
            }
            throw new FileNotFoundException("file " + s + " not found");
        }

        String suffix = name.substring(name.lastIndexOf('.')).toLowerCase();
        if (!knownExtensions.contains(suffix)) {
            suffix = ".spin";
            fileToCompile = new File(fileToCompile.getParentFile(), name + suffix);
        }
        if (!fileToCompile.exists()) {
            throw new FileNotFoundException("file \"" + s + "\" not found");
        }

        return fileToCompile;
    }

    byte[] compile(File fileToCompile) throws Exception {
        String name = fileToCompile.getName();
        String suffix = name.substring(name.lastIndexOf('.')).toLowerCase();

        println("Compiling...");

        Compiler compiler = null;
        if (".spin2".equals(suffix) || ".p2asm".equals(suffix)) {
            compiler = new Spin2Compiler();
        }
        else if (".c".equals(suffix)) {
            if (target == P1) {
                compiler = new Spin1CCompiler();
            }
            else if (target == P2) {
                compiler = new Spin2CCompiler();
            }
            else {
                String text = FileUtils.loadFromFile(fileToCompile);
                Pattern pattern = Pattern.compile(pragmaTargetRegex);
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    if (matcher.group(2) != null) {
                        if (matcher.group(2).endsWith("P1")) {
                            compiler = new Spin1CCompiler();
                        }
                        else if (matcher.group(2).endsWith("P2")) {
                            compiler = new Spin2CCompiler();
                        }
                        break;
                    }
                }
                if (compiler == null) {
                    compiler = new Spin2CCompiler();
                }
            }
        }
        else {
            compiler = new Spin1Compiler();
        }

        compiler.setCaseSensitive(caseSensitive);
        compiler.setSourceProvider(new Compiler.FileSourceProvider(libraryPaths.toArray(new File[0])));
        compiler.setDebugEnabled(debug);
        compiler.setRemoveUnusedMethods(removeUnusedMethods);

        if (compiler instanceof Spin1Compiler spin1Compiler) {
            spin1Compiler.setFastByteConstants(fasterByteConstants);
            spin1Compiler.setFoldConstants(foldConstants);
        }
        if (compiler instanceof Spin2Compiler spin2Compiler) {
            spin2Compiler.setCompress(compressBinary);
        }

        compiler.setWarnUnusedMethods(warnUnusedMethods);
        compiler.setWarnUnusedMethodVariables(warnUnusedMethodVariables);
        compiler.setWarnUnusedVariables(warnUnusedVariables);

        for (Entry<String, String> entry : defines.entrySet()) {
            compiler.addDefine(entry.getKey(), entry.getValue());
        }

        try {
            SpinObject object = compiler.compile(fileToCompile);
            if (compiler.hasErrors()) {
                System.exit(1);
            }
            target = object instanceof Spin2Object ? P2 : P1;

            printObjectTree(0, object);

            for (CompilerException e : compiler.getMessages()) {
                if (e.type == CompilerException.WARNING && noWarnings) {
                    continue;
                }
                println(e);
            }

            byte[] binaryData = object.getBinary();
            println("Program size is " + binaryData.length + " bytes");

            if (outName == null) {
                outName = new File(fileToCompile.getParentFile(), name.substring(0, name.lastIndexOf('.'))).getAbsolutePath();
            }
            File fileToWrite = new File(outName).getAbsoluteFile();

            File binaryFile = null;
            if (writeBinary) {
                binaryFile = new File(fileToWrite.getParentFile(), fileToWrite.getName() + ".binary");
            }
            else if (writeFlashBinary) {
                binaryData = object.getEEPromBinary();
                if (object instanceof Spin2Object) {
                    binaryFile = new File(fileToWrite.getParentFile(), fileToWrite.getName() + ".p2img");
                }
                else {
                    binaryFile = new File(fileToWrite.getParentFile(), fileToWrite.getName() + ".eeprom");
                }
            }
            else if (writeDatBinary) {
                binaryData = object.getDatBinary();
                binaryFile = new File(fileToWrite.getParentFile(), fileToWrite.getName() + ".dat");
            }
            if (binaryFile != null) {
                FileOutputStream os = new FileOutputStream(binaryFile);
                os.write(binaryData);
                os.close();
                println("Wrote " + binaryFile.getAbsolutePath());
            }

            if (writeListing) {
                File listingFile = new File(fileToWrite.getParentFile(), fileToWrite.getName() + ".lst");
                PrintStream os = new PrintStream(new FileOutputStream(listingFile));
                object.generateListing(os);
                os.close();
                println("Wrote " + listingFile.getAbsolutePath());
            }

            return binaryData;
        } catch (Exception ignored) {
            for (CompilerException e : compiler.getMessages()) {
                println(e);
            }
            System.exit(1);
        }

        return null;
    }

    void printObjectTree(int indent, SpinObject object) {
        if (indent != 0) {
            print("     ".repeat(indent - 1) + " +-- ");
        }
        println(object.getFile().getName());

        for (SpinObject child : object.getChildObjects()) {
            printObjectTree(indent + 1, child);
        }
        for (DataObject data : object.getDataObjects()) {
            if (data instanceof FileDataObject fileData) {
                println("     ".repeat(indent) + " +-- " + fileData.getFile().getName());
            }
        }
    }

    ComPort getSerialPort() throws Exception {
        ComPort serialPort = null;

        if (uploadMode != NONE && portName == null) {
            throw new RuntimeException("No serial port specified");
        }

        if (Pattern.matches(ipAddressPattern, portName)) {
            serialPort = new NetworkComPort(InetAddress.getByName(portName));
        }
        else if (Pattern.matches(macAddressPattern, portName)) {
            Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
            for (DeviceDescriptor descr : list) {
                if (descr.mac_address.equals(portName)) {
                    serialPort = new NetworkComPort(descr);
                    break;
                }
            }
        }
        if (serialPort != null && wifiResetPin != null) {
            ((NetworkComPort) serialPort).setResetPin(wifiResetPin);
        }

        if (serialPort == null) {
            serialPort = new SerialComPort(portName);
        }

        return serialPort;
    }

    void upload(ComPort serialPort, byte[] binaryData) throws ComPortException {
        int flags = 0;
        PropellerLoader loader = null;

        println("Uploading...");

        if (target == P2) {
            flags = uploadMode == FLASH ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM;

            loader = new Propeller2Loader(serialPort, resetControl, true) {

                @Override
                protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
                    println(String.format("Propeller %d on port %s", 2, comPort.getPortName()));
                    print("Loading " + text + " to ");
                    switch (type) {
                        case Propeller2Loader.DOWNLOAD_RUN_FLASH:
                            print("Flash via ");
                            // fall through
                        case Propeller2Loader.DOWNLOAD_RUN_RAM:
                            println("hub memory");
                            break;
                    }
                    super.bufferUpload(type, binaryImage, text);
                }

                @Override
                protected void notifyProgress(int sent, int total) {
                    if (sent >= total) {
                        print("                               \r");
                        println(String.format("%d bytes sent", total));
                    }
                    else {
                        print(String.format("%d bytes remaining             \r", total - sent));
                    }
                }

                @Override
                protected void verifyRam() throws ComPortException {
                    print("Verifying checksum ... ");
                    super.verifyRam();
                    println("OK");
                }

            };
        }
        else {
            flags = uploadMode == FLASH ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY;

            loader = new Propeller1Loader(serialPort, resetControl, true) {

                @Override
                protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
                    println(String.format("Propeller %d on port %s", 1, comPort.getPortName()));
                    print("Loading " + text + " to ");
                    switch (type) {
                        case DOWNLOAD_EEPROM:
                        case DOWNLOAD_RUN_EEPROM:
                            print("EEPROM via ");
                            // fall through
                        case DOWNLOAD_RUN_BINARY:
                            println("hub memory");
                            break;
                    }
                    super.bufferUpload(type, binaryImage, text);
                }

                @Override
                protected void notifyProgress(int sent, int total) {
                    if (sent == total) {
                        print("                               \r");
                        println(String.format("%d bytes sent", total));
                    }
                    else {
                        print(String.format("%d bytes remaining             \r", total - sent));
                    }
                }

                @Override
                protected void verifyRam() throws ComPortException {
                    print("Verifying RAM ... ");
                    super.verifyRam();
                    println("OK");
                }

                @Override
                protected void eepromWrite() throws ComPortException {
                    print("Programming EEPROM ... ");
                    super.eepromWrite();
                    println("OK");
                }

                @Override
                protected void eepromVerify() throws ComPortException {
                    print("Verifying EEPROM ... ");
                    super.eepromVerify();
                    println("OK");
                }

            };
        }

        loader.upload(binaryData, flags);
    }

    void runTerminal(ComPort serialPort) throws ComPortException {
        String os = System.getProperty("os.name");
        boolean isNix = os != null && !os.startsWith("Windows");
        int lastByte = 0;

        println(String.format("Entering%s terminal mode%s. CTRL-C to exit.", terminalMode == PST ? " PST" : "", terminalEcho ? " (local echo)" : ""));

        if (!serialPort.isOpened()) {
            serialPort.openPort();
        }
        if (terminalModeBaud != 0) {
            serialPort.setParams(terminalModeBaud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        }

        serialPort.setEventListener(new ComPortEventListener() {
            int state = 0;
            int cmd, p0;
            boolean pendingEndSession = false;
            AtomicBoolean endSession = new AtomicBoolean();

            @Override
            public void serialEvent(ComPortEvent event) {
                if (event.isRXCHAR()) {
                    try {
                        final byte[] rx = event.getComPort().readBytes();
                        if (rx != null) {
                            if (terminalMode == PST) {
                                for (int i = 0; i < rx.length; i++) {
                                    pstWrite(rx[i]);
                                }
                            }
                            else {
                                for (int i = 0; i < rx.length; i++) {
                                    conWrite(rx[i]);
                                }
                            }
                            System.out.flush();
                            if (endSession.get()) {
                                event.getComPort().closePort();
                                System.exit(0);
                            }
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            }

            void conWrite(byte c) {
                if (debug) {
                    if (c == 0x1B) {
                        pendingEndSession = true;
                        return;
                    }
                    if (c == 0x0A && pendingEndSession) {
                        endSession.set(true);
                    }
                }
                System.out.write(c);
            }

            void pstWrite(byte c) {
                if (cmd == 2) { // PC: Position Cursor in x,y
                    if (state == 0) {
                        p0 = c;
                        state++;
                        return;
                    }
                    System.out.printf("\033[%d;%dH", c + 1, p0 + 1);
                    cmd = 0;
                    return;
                }
                else if (cmd == 14) { // PX: Position cursor in X
                    System.out.printf("\r\033[%dC", c + 1);
                    cmd = 0;
                    return;
                }
                else if (cmd == 15) { // PY: Position cursor in Y
                    System.out.printf("\033[999A\033[%dB", c + 1);
                    cmd = 0;
                    return;
                }

                switch (c) {
                    case 1: // HM: HoMe cursor
                        System.out.print("\033[H");
                        break;

                    case 2: // PC: Position Cursor in x,y
                    case 14: // PX: Position cursor in X
                    case 15: // PY: Position cursor in Y
                        cmd = c;
                        state = 0;
                        break;

                    case 3: // ML: Move cursor Left
                        System.out.print("\033[D");
                        break;

                    case 4: // MR: Move cursor Right
                        System.out.print("\033[C");
                        break;

                    case 5: // MU: Move cursor Up
                        System.out.print("\033[A");
                        break;

                    case 6: // MR: Move cursor Down
                        System.out.print("\033[B");
                        break;

                    case 10: // LF: Line Feed
                        System.out.write(10);
                        if (pendingEndSession) {
                            endSession.set(true);
                        }
                        break;

                    case 13: // NL: New Line
                        System.out.write(13);
                        System.out.write(10);
                        break;

                    case 16: // CS: Clear Screen
                        System.out.print("\033[H\033[2J");
                        break;

                    case 0x1B:
                        if (debug) {
                            pendingEndSession = true;
                        }
                        break;

                    default:
                        System.out.write(c);
                        break;
                }
            }

        });

        if (isNix) {
            while (true) {
                try {
                    int available = System.in.available();
                    if (available != 0) {
                        byte[] b = System.in.readNBytes(available);
                        for (int i = 0; i < b.length; i++) {
                            if (b[i] == 0x0A) {
                                serialPort.writeByte(lastByte == 0x0D ? (byte) 0x0A : (byte) 0x0D);
                                if (terminalEcho) {
                                    System.out.write(lastByte == 0x0D ? (byte) 0x0A : (byte) 0x0D);
                                }
                            }
                            else if (b[i] == 0x03) { // CTRL-C
                                serialPort.closePort();
                                System.exit(0);
                            }
                            else {
                                serialPort.writeByte(b[i]);
                            }
                            if (terminalEcho) {
                                System.out.write(b[i]);
                            }
                            lastByte = b[i];
                        }
                        if (terminalEcho) {
                            System.out.flush();
                        }
                    }
                    synchronized (serialPort) {
                        serialPort.wait(100);
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }
        else {
            while (true) {
                try {
                    int b = System.in.read();
                    if (b == 0x03) { // CTRL-C
                        serialPort.closePort();
                        System.exit(0);
                    }
                    serialPort.writeInt(b);
                    if (terminalEcho) {
                        System.out.write(b);
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }
    }

    void println(String obj) {
        if (!quiet) {
            System.out.println(obj);
            System.out.flush();
        }
    }

    void print(String obj) {
        if (!quiet) {
            System.out.print(obj);
            System.out.flush();
        }
    }

    void println(CompilerException obj) {
        if (!quiet) {
            System.out.println(obj);
            System.out.flush();
        }
    }

    public static void main(String[] args) {
        try {
            SpinCompiler app = new SpinCompiler();
            int rc = app.run(args);
            System.exit(rc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.exit(1);
    }

}
