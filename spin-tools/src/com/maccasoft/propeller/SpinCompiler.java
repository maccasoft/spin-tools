/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortEvent;
import com.maccasoft.propeller.devices.ComPortEventListener;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.devices.SerialComPort;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spinc.CParser;
import com.maccasoft.propeller.spinc.Spin1CCompiler;
import com.maccasoft.propeller.spinc.Spin2CCompiler;

import jssc.SerialPort;

public class SpinCompiler {

    public static final String APP_TITLE = "Spin Tools Compiler";
    public static final String APP_VERSION = SpinTools.APP_VERSION;

    static String ipAddressPattern = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
    static String macAddressPattern = "(?:[0-9A-Fa-f]{2}[:-]){5}(?:[0-9A-Fa-f]{2})";

    static boolean quiet;
    static boolean filterUnusedMethodWarning = false;

    public static void main(String[] args) {
        File binaryFile = null, listingFile = null;
        List<File> libraryPaths = new ArrayList<File>();

        try {
            Options options = new Options();
            options.addOption(Option.builder("L").desc("add a directory to the library path").hasArg().argName("path").build());
            options.addOption(Option.builder("D").desc("add a define").hasArg().argName("define").build());

            options.addOption(Option.builder("o").desc("output file name").hasArg().argName("file").build());

            OptionGroup binaryOptions = new OptionGroup();
            binaryOptions.addOption(new Option("b", false, "output binary file"));
            binaryOptions.addOption(new Option("e", false, "output flash binary file (P2 only)"));
            binaryOptions.addOption(new Option("c", false, "output only DAT sections"));
            options.addOptionGroup(binaryOptions);
            options.addOption(new Option("l", false, "output listing file"));
            options.addOption(new Option("d", false, "enable debug (P2 only)"));
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

            options.addOption(new Option(null, "no-warn-unused-methods", false, "disable unused methods warning"));
            options.addOption(new Option(null, "no-warn-unused-method-variables", false, "disable unused method variabless warning"));
            options.addOption(new Option(null, "no-warn-unused-variables", false, "disable unused variabless warning"));

            options.addOption(new Option("C", "x-case", false, "case-sensitive spin symbols"));

            options.addOption(new Option("O", true, "optimizations"));
            options.addOption(new Option("Ob", false, "faster byte constants (P1)"));
            options.addOption(new Option("Of", false, "fold constants (P1)"));

            options.addOption(Option.builder("p").desc("serial port").hasArg().argName("port").build());
            OptionGroup terminalOptions = new OptionGroup();
            terminalOptions.addOption(Option.builder("t").desc("enter terminal mode after upload (optional baud rate)").hasArg().argName("baud").optionalArg(true).build());
            terminalOptions.addOption(Option.builder("T").desc("enter PST terminal mode after upload (optional baud rate)").hasArg().argName("baud").optionalArg(true).build());
            options.addOptionGroup(terminalOptions);

            OptionGroup resetOptions = new OptionGroup();
            resetOptions.addOption(new Option("dtr", false, "use DTR for reset"));
            resetOptions.addOption(new Option("rts", false, "use RTS for reset"));
            resetOptions.addOption(new Option(null, "reset-control", true, "set reset control dtr-rts (default), dtr or rts"));
            options.addOptionGroup(resetOptions);

            options.addOption(new Option("W", false, "show all discovered wifi modules"));
            options.addOption(new Option(null, "reset-pin", true, "set wifi module reset pin number"));

            options.addOption("q", false, "quiet mode");

            CommandLine cmd = new DefaultParser().parse(options, args);

            quiet = cmd.hasOption('q');
            if (cmd.getArgList().size() != 1 && !cmd.hasOption("W")) {
                quiet = false;
            }

            filterUnusedMethodWarning = cmd.hasOption('u');

            println(APP_TITLE + " - Version " + APP_VERSION);
            println("Copyright (c) 2021-25 Marco Maccaferri and others. All rights reserved.");

            if (cmd.hasOption("W")) {
                println("");
                Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
                for (DeviceDescriptor descr : list) {
                    System.out.println(String.format("Name: '%s', IP: %s, MAC: %s", descr.name, descr.inetAddr.getHostAddress(), descr.mac_address));
                }
                if (cmd.getArgList().size() != 1) {
                    System.exit(0);
                }
            }

            if (cmd.getArgList().size() != 1) {
                HelpFormatter help = new HelpFormatter();
                help.setWidth(-1);
                help.setOptionComparator(null);
                help.printHelp("spinc [options] <file.spin | file.spin2 | file.c>", null, options, null, false);
                System.exit(1);
            }

            if (cmd.hasOption('L')) {
                for (String s : cmd.getOptionValues('L')) {
                    libraryPaths.add(new File(s));
                }
            }

            File fileToCompile = new File(cmd.getArgList().get(0));

            String name = fileToCompile.getName();
            if (!name.toLowerCase().endsWith(".spin") && !name.toLowerCase().endsWith(".spin2") && !name.toLowerCase().endsWith(".c")) {
                name += ".spin";
                fileToCompile = new File(fileToCompile.getParentFile(), name);
            }

            if (!fileToCompile.exists()) {
                throw new CompilerException("file " + fileToCompile + " not found", (Object) null);
            }

            libraryPaths.add(0, fileToCompile.getAbsoluteFile().getParentFile());

            String outName = name.substring(0, name.lastIndexOf('.'));
            if (cmd.hasOption('o')) {
                outName = cmd.getOptionValue('o');
                binaryFile = new File(outName);
                if (outName.lastIndexOf('.') != -1) {
                    listingFile = new File(outName.substring(0, outName.lastIndexOf('.')) + ".lst");
                }
            }

            if (listingFile == null) {
                listingFile = new File(fileToCompile.getParentFile(), outName + ".lst");
            }

            println("Compiling...");

            String suffix = name.substring(name.lastIndexOf('.')).toLowerCase();

            Compiler compiler = null;
            if (".spin2".equals(suffix)) {
                compiler = new Spin2Compiler();
            }
            else if (".c".equals(suffix)) {
                if (cmd.hasOption("p1")) {
                    compiler = new Spin1CCompiler();
                }
                else if (cmd.hasOption("p2")) {
                    compiler = new Spin2CCompiler();
                }
                else {
                    String text = FileUtils.loadFromFile(fileToCompile);
                    CParser parser = new CParser(text);
                    for (Node node : parser.parse().getChilds()) {
                        if (node instanceof DirectiveNode) {
                            int index = 1;
                            if (index < node.getTokenCount()) {
                                if ("pragma".equals(node.getToken(index).getText())) {
                                    index++;
                                    if (index < node.getTokenCount()) {
                                        if ("target".equals(node.getToken(index).getText())) {
                                            index++;
                                            if (index < node.getTokenCount()) {
                                                if ("P1".equals(node.getToken(index).getText())) {
                                                    compiler = new Spin1CCompiler();
                                                }
                                                else if ("P2".equals(node.getToken(index).getText())) {
                                                    compiler = new Spin2CCompiler();
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
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
            compiler.setCaseSensitive(cmd.hasOption("C") || cmd.hasOption("x-case"));
            compiler.setSourceProvider(new Compiler.FileSourceProvider(libraryPaths.toArray(new File[libraryPaths.size()])));
            compiler.setDebugEnabled(cmd.hasOption('d'));
            compiler.setRemoveUnusedMethods(cmd.hasOption('u'));

            if (compiler instanceof Spin1Compiler) {
                String optimizations = cmd.hasOption("O") ? cmd.getOptionValue("O") : "";
                ((Spin1Compiler) compiler).setFastByteConstants(cmd.hasOption("Ob") || optimizations.contains("b"));
                ((Spin1Compiler) compiler).setFoldConstants(cmd.hasOption("Of") || optimizations.contains("f"));
            }

            compiler.setWarnUnusedMethods(!cmd.hasOption("no-warn-unused-methods"));
            compiler.setWarnUnusedMethodVariables(!cmd.hasOption("no-warn-unused-method-variables"));
            compiler.setWarnUnusedVariables(!cmd.hasOption("no-warn-unused-variables"));

            if (compiler instanceof Spin2Compiler) {
                ((Spin2Compiler) compiler).setCompress(cmd.hasOption('z'));
            }

            if (cmd.hasOption('D')) {
                Pattern p1 = Pattern.compile("([A-Za-z_][A-Za-z0-9_]+)=(.+)");
                Pattern p2 = Pattern.compile("([A-Za-z_][A-Za-z0-9_]+)");
                for (String s : cmd.getOptionValues('D')) {
                    Matcher m = p1.matcher(s);
                    if (m.matches()) {
                        compiler.addDefine(m.group(1), m.group(2));
                    }
                    else {
                        m = p2.matcher(s);
                        if (m.matches()) {
                            compiler.addDefine(m.group(1), "");
                        }
                        else {
                            println("Invalid command line option: " + s);
                            System.exit(1);
                        }
                    }
                }
            }

            SpinObject object = compiler.compile(fileToCompile);

            byte[] binaryData = null;
            if (cmd.hasOption('b')) {
                binaryData = object.getBinary();
                if (binaryFile == null) {
                    binaryFile = new File(fileToCompile.getParentFile(), outName + ".binary");
                }
            }
            else if (cmd.hasOption('e')) {
                binaryData = object.getEEPromBinary();
                if (binaryFile == null) {
                    if ((compiler instanceof Spin2Compiler) || (compiler instanceof Spin2CCompiler)) {
                        binaryFile = new File(fileToCompile.getParentFile(), outName + ".p2img");
                    }
                    else {
                        binaryFile = new File(fileToCompile.getParentFile(), outName + ".eeprom");
                    }
                }
            }
            else if (cmd.hasOption('c')) {
                binaryData = object.getDatBinary();
                if (binaryFile == null) {
                    binaryFile = new File(fileToCompile.getParentFile(), outName + ".dat");
                }
            }
            if (binaryData != null) {
                FileOutputStream os = new FileOutputStream(binaryFile);
                os.write(binaryData);
                os.close();
            }

            if (binaryData == null && (cmd.hasOption('r') || cmd.hasOption('f'))) {
                binaryData = object.getBinary();
            }

            if (cmd.hasOption('l')) {
                PrintStream os = new PrintStream(new FileOutputStream(listingFile));
                object.generateListing(os);
                os.close();
            }

            print(compiler.getObjectTree().toString());

            for (CompilerException e : compiler.getMessages()) {
                println(e);
            }

            if (binaryData != null) {
                println("Program size is " + binaryData.length + " bytes");
            }

            if (binaryData != null && (cmd.hasOption('r') || cmd.hasOption('f'))) {
                ComPort serialPort = null;

                println("Uploading...");

                if (cmd.hasOption("p")) {
                    String portName = cmd.getOptionValue("p");
                    if (portName != null) {
                        try {
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
                            if (serialPort != null) {
                                String resetPin = cmd.getOptionValue("reset-pin");
                                if (resetPin != null) {
                                    ((NetworkComPort) serialPort).setResetPin(resetPin);
                                }
                            }
                        } catch (Exception e) {
                            println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    if (serialPort == null) {
                        serialPort = new SerialComPort(portName);
                    }
                }

                ComPort.Control resetControl;
                if (cmd.hasOption("dtr") || "dtr".equals(cmd.getOptionValue("reset-control"))) {
                    resetControl = ComPort.Control.Dtr;
                }
                else if (cmd.hasOption("rts") || "rts".equals(cmd.getOptionValue("reset-control"))) {
                    resetControl = ComPort.Control.Rts;
                }
                else {
                    resetControl = ComPort.Control.DtrRts;
                }

                AtomicBoolean error = new AtomicBoolean();

                int flags = 0;
                PropellerLoader loader = null;

                if (compiler instanceof Spin1Compiler) {
                    flags = cmd.hasOption("f") ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY;

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
                                print(String.format("                               \r"));
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
                else if (compiler instanceof Spin2Compiler) {
                    flags = cmd.hasOption("f") ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM;

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
                                print(String.format("                               \r"));
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

                serialPort = loader.upload(binaryData, flags);

                println("Done.");

                if (!error.get() && (cmd.hasOption('t') || cmd.hasOption('T'))) {
                    boolean pst = cmd.hasOption('T');

                    String baud = pst ? cmd.getOptionValue('T') : cmd.getOptionValue('t');
                    if (baud != null) {
                        serialPort.setParams(Integer.valueOf(baud), 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    }

                    println(String.format("Entering%s terminal mode. CTRL-C to exit.", pst ? " PST" : ""));

                    serialPort.setEventListener(new ComPortEventListener() {

                        int state = 0;
                        int cmd, p0;

                        @Override
                        public void serialEvent(ComPortEvent event) {
                            if (event.isRXCHAR()) {
                                try {
                                    final byte[] rx = event.getComPort().readBytes();
                                    if (rx != null) {
                                        if (pst) {
                                            for (int i = 0; i < rx.length; i++) {
                                                write(rx[i]);
                                            }
                                        }
                                        else {
                                            System.out.write(rx);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        void write(byte c) {

                            if (cmd == 2) { // PC: Position Cursor in x,y
                                if (state == 0) {
                                    p0 = c;
                                    state++;
                                    return;
                                }
                                System.out.print("\033[");
                                System.out.print(String.valueOf(c + 1));
                                System.out.print(";");
                                System.out.print(String.valueOf(p0 + 1));
                                System.out.print("H");
                                cmd = 0;
                                return;
                            }
                            else if (cmd == 14) { // PX: Position cursor in X
                                System.out.print("\r\033[");
                                System.out.print(String.valueOf(c + 1));
                                System.out.print("C");
                                cmd = 0;
                                return;
                            }
                            else if (cmd == 15) { // PY: Position cursor in Y
                                System.out.print("\033[999A");
                                System.out.print("\033[");
                                System.out.print(String.valueOf(c + 1));
                                System.out.print("B");
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
                                case 13: // NL: New Line
                                    System.out.write(13);
                                    System.out.write(10);
                                    break;

                                case 16: // CS: Clear Screen
                                    System.out.print("\033[H\033[2J");
                                    break;

                                default:
                                    System.out.write(c);
                                    break;
                            }
                        }

                    });
                    while (true) {
                        int ch = System.in.read();
                        if (ch == -1) {
                            break;
                        }
                        serialPort.writeInt(ch);
                    }
                }

                if (serialPort != null) {
                    serialPort.closePort();
                }
            }
            else {
                println("Done.");
            }

        } catch (ParseException | ComPortException e) {
            System.out.println(e.getMessage());
        } catch (CompilerException e) {
            println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void println(String obj) {
        if (!quiet) {
            System.out.println(obj);
            System.out.flush();
        }
    }

    static void print(String obj) {
        if (!quiet) {
            System.out.print(obj);
            System.out.flush();
        }
    }

    static void println(CompilerException obj) {
        if (!quiet) {
            String msg = obj.getText();
            if (!msg.isEmpty()) {
                System.out.println(msg);
            }
            for (CompilerException e : obj.getChilds()) {
                println(e);
            }
            System.out.flush();
        }
    }

}
