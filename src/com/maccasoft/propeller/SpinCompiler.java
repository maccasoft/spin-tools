/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin2.Spin2Compiler;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SpinCompiler {

    public static final String APP_TITLE = "Spin Tools Compiler";
    public static final String APP_VERSION = SpinTools.APP_VERSION;

    public static void main(String[] args) {
        List<File> libraryPaths = new ArrayList<File>();

        System.out.println(APP_TITLE + " - Version " + APP_VERSION);
        System.out.println("Copyright (c) 2021-22 Marco Maccaferri and others. All rights reserved.");

        try {
            Options options = new Options();
            options.addOption("L", true, "add a directory to the library path");

            options.addOption(new Option("b", false, "output binary file"));
            options.addOption(new Option("l", false, "output listing file"));
            options.addOption(new Option("d", false, "enable debug (P2 only)"));

            OptionGroup uploadOptions = new OptionGroup();
            uploadOptions.addOption(new Option("r", false, "upload program to ram and run"));
            uploadOptions.addOption(new Option("f", false, "upload program to flash/eeprom and run"));
            options.addOptionGroup(uploadOptions);

            options.addOption("p", true, "serial port");
            options.addOption("t", false, "enter terminal mode after upload");

            CommandLine cmd = new DefaultParser().parse(options, args);
            if (cmd.getArgList().size() != 1) {
                HelpFormatter help = new HelpFormatter();
                help.setOptionComparator(null);
                help.printHelp("spinc [options] <file.spin | file.spin2>", null, options, null, false);
                System.exit(1);
            }

            if (cmd.hasOption('L')) {
                for (String s : cmd.getOptionValues('L')) {
                    libraryPaths.add(new File(s));
                }
            }

            File fileToCompile = new File(cmd.getArgList().get(0));

            String name = fileToCompile.getName();
            if (!name.toLowerCase().endsWith(".spin") && !name.toLowerCase().endsWith(".spin2")) {
                name += ".spin";
                fileToCompile = new File(fileToCompile.getParentFile(), name);
            }

            if (!fileToCompile.exists()) {
                throw new FileNotFoundException("File " + fileToCompile.getAbsolutePath() + " not found");
            }

            libraryPaths.add(0, fileToCompile.getAbsoluteFile().getParentFile());

            int i = name.lastIndexOf('.');
            File binaryFile = new File(name.substring(0, i) + ".binary");
            File listingFile = new File(name.substring(0, i) + ".lst");

            ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
            PrintStream listingStream = cmd.hasOption('l') ? new PrintStream(new FileOutputStream(listingFile)) : null;

            System.out.println("Compiling...");

            Compiler compiler = name.toLowerCase().endsWith(".spin2") ? new Spin2Compiler() : new Spin1Compiler();
            compiler.addSourceProvider(new Compiler.FileSourceProvider(libraryPaths.toArray(new File[libraryPaths.size()])));
            if (cmd.hasOption('d')) {
                compiler.setDebugEnabled(true);
            }
            try {
                compiler.compile(fileToCompile, binaryData, listingStream);
            } finally {
                if (listingStream != null) {
                    listingStream.close();
                }
            }

            if (cmd.hasOption('b')) {
                FileOutputStream os = new FileOutputStream(binaryFile);
                os.write(binaryData.toByteArray());
                os.close();
            }

            System.out.println("Program size is " + binaryData.size() + " bytes");

            if (cmd.hasOption('r') || cmd.hasOption('f')) {
                String port = cmd.getOptionValue('p');
                SerialPort serialPort = new SerialPort(port);

                System.out.println("Uploading...");

                AtomicBoolean error = new AtomicBoolean();
                if (compiler instanceof Spin1Compiler) {
                    Propeller1Loader loader = new Propeller1Loader(serialPort, true) {

                        @Override
                        protected int hwfind() throws SerialPortException, IOException {
                            int version = super.hwfind();

                            if (version != 0) {
                                System.out.println(String.format("Propeller %d on port %s", version, getPortName()));
                            }
                            else {
                                System.out.println(String.format("No propeller chip on port %s", getPortName()));
                                error.set(true);
                            }

                            return version;
                        }

                        @Override
                        protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
                            System.out.print("Loading " + text + " to ");
                            switch (type) {
                                case DOWNLOAD_EEPROM:
                                case DOWNLOAD_RUN_EEPROM:
                                    System.out.print("EEPROM via ");
                                    // fall through
                                case DOWNLOAD_RUN_BINARY:
                                    System.out.println("hub memory");
                                    break;
                            }
                            super.bufferUpload(type, binaryImage, text);
                        }

                        @Override
                        protected void notifyProgress(int sent, int total) {
                            if (sent == total) {
                                System.out.print(String.format("                               \r"));
                                System.out.println(String.format("%d bytes sent", total));
                            }
                            else {
                                System.out.print(String.format("%d bytes remaining             \r", total - sent));
                            }
                            System.out.flush();
                        }

                        @Override
                        protected int skipIncomingBytes() throws SerialPortException {
                            int n = super.skipIncomingBytes();
                            if (n != 0) {
                                System.out.println(String.format("Ignoring %d bytes", n));
                            }
                            return n;
                        }

                        @Override
                        protected void verifyRam() throws SerialPortException, IOException {
                            System.out.print("Verifying RAM ... ");
                            System.out.flush();
                            super.verifyRam();
                            System.out.println("OK");
                        }

                        @Override
                        protected void eepromWrite() throws SerialPortException, IOException {
                            System.out.print("Programming EEPROM ... ");
                            System.out.flush();
                            super.eepromWrite();
                            System.out.println("OK");
                        }

                        @Override
                        protected void eepromVerify() throws SerialPortException, IOException {
                            System.out.print("Verifying EEPROM ... ");
                            System.out.flush();
                            super.eepromVerify();
                            System.out.println("OK");
                        }

                    };
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY);
                }
                else {
                    Propeller2Loader loader = new Propeller2Loader(serialPort, true) {

                        @Override
                        protected int hwfind() throws SerialPortException, IOException {
                            int version = super.hwfind();

                            if (version != 0) {
                                System.out.println(String.format("Propeller %d on port %s", version, getPortName()));
                            }
                            else {
                                System.out.println(String.format("No propeller chip on port %s", getPortName()));
                                error.set(true);
                            }

                            return version;
                        }

                        @Override
                        protected int skipIncomingBytes() throws SerialPortException {
                            int n = super.skipIncomingBytes();
                            if (n != 0) {
                                System.out.println(String.format("Ignoring %d bytes", n));
                            }
                            return n;
                        }

                        @Override
                        protected void notifyProgress(int sent, int total) {
                            if (sent >= total) {
                                System.out.print(String.format("                               \r"));
                                System.out.println(String.format("%d bytes sent", total));
                            }
                            else {
                                System.out.print(String.format("%d bytes remaining             \r", total - sent));
                            }
                            System.out.flush();
                        }

                        @Override
                        protected void verifyRam() throws SerialPortException, IOException {
                            System.out.print("Verifying checksum ... ");
                            System.out.flush();
                            super.verifyRam();
                            System.out.println("OK");
                        }

                    };
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM);
                }

                System.out.println("Done.");

                if (!error.get() && cmd.hasOption('t')) {
                    System.out.println("Entering terminal mode. CTRL-C to exit.");
                    serialPort.addEventListener(new SerialPortEventListener() {

                        @Override
                        public void serialEvent(SerialPortEvent serialPortEvent) {
                            if (serialPortEvent.getEventType() == SerialPortEvent.RXCHAR) {
                                try {
                                    final byte[] rx = serialPort.readBytes();
                                    if (rx != null) {
                                        System.out.write(rx);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

                serialPort.closePort();
            }
            else {
                System.out.println("Done.");
            }

        } catch (CompilerException e) {
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String loadFromFile(File file) throws Exception {
        String line;
        StringBuilder sb = new StringBuilder();

        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
        }

        return sb.toString();
    }

}
