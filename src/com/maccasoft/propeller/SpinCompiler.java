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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class SpinCompiler {

    public static final String APP_TITLE = "Spin Tools Compiler";
    public static final String APP_VERSION = SpinTools.APP_VERSION;

    static class Spin1CompilerAdapter extends Spin1Compiler {

        List<File> libraryPaths;

        public Spin1CompilerAdapter(List<File> libraryPaths) {
            this.libraryPaths = libraryPaths;
        }

        @Override
        protected Node getParsedObject(String fileName) {
            File file = new File(fileName);
            if (!file.exists()) {
                for (File f : libraryPaths) {
                    file = new File(f, fileName);
                    if (file.exists()) {
                        break;
                    }
                }
            }

            if (file.exists()) {
                try {
                    Spin2TokenStream stream = new Spin2TokenStream(loadFromFile(file));
                    Spin2Parser subject = new Spin2Parser(stream);
                    return subject.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            try {
                File fileToLoad = new File(fileName);
                if (!fileToLoad.exists()) {
                    for (File f : libraryPaths) {
                        fileToLoad = new File(f, fileName);
                        if (fileToLoad.exists()) {
                            break;
                        }
                    }
                }
                InputStream is = new FileInputStream(fileToLoad);
                try {
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return b;
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                // Do nothing
            }

            return null;
        }

    }

    static class Spin2CompilerAdapter extends Spin2Compiler {

        List<File> libraryPaths;

        public Spin2CompilerAdapter(List<File> libraryPaths) {
            this.libraryPaths = libraryPaths;
        }

        @Override
        protected Node getParsedObject(String fileName) {
            File file = new File(fileName);
            if (!file.exists()) {
                for (File f : libraryPaths) {
                    file = new File(f, fileName);
                    if (file.exists()) {
                        break;
                    }
                }
            }

            if (file.exists()) {
                try {
                    Spin2TokenStream stream = new Spin2TokenStream(loadFromFile(file));
                    Spin2Parser subject = new Spin2Parser(stream);
                    return subject.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            try {
                File fileToLoad = new File(fileName);
                if (!fileToLoad.exists()) {
                    for (File f : libraryPaths) {
                        fileToLoad = new File(f, fileName);
                        if (fileToLoad.exists()) {
                            break;
                        }
                    }
                }
                InputStream is = new FileInputStream(fileToLoad);
                try {
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return b;
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                // Do nothing
            }

            return null;
        }

    }

    public static void main(String[] args) {
        List<File> libraryPaths = new ArrayList<File>();

        try {
            Options options = new Options();
            options.addOption("l", "library", true, "add a directory to the library path");

            OptionGroup uploadOptions = new OptionGroup();
            uploadOptions.addOption(new Option("u", "upload", false, "compile and upload to ram"));
            uploadOptions.addOption(new Option("f", "flash", false, "compile and upload to flash/eeprom"));
            options.addOptionGroup(uploadOptions);

            options.addOption("p", "port", true, "serial port");

            CommandLine cmd = new DefaultParser().parse(options, args);
            if (cmd.getArgList().size() != 1) {
                System.out.println(APP_TITLE + " - Version " + APP_VERSION);
                System.out.println("Copyright (c) 2021-22 Marco Maccaferri and others. All rights reserved.\n");

                HelpFormatter help = new HelpFormatter();
                help.setOptionComparator(null);
                help.printHelp("spinc [options] <file.spin | file.spin2>", null, options, null, false);

                System.exit(1);
            }

            if (cmd.hasOption("l")) {
                for (String s : cmd.getOptionValues("l")) {
                    libraryPaths.add(new File(s));
                }
            }

            File fileToCompile = new File(cmd.getArgList().get(0));

            String name = fileToCompile.getName();
            if (!name.toLowerCase().endsWith(".spin") && !name.toLowerCase().endsWith(".spin2")) {
                name += ".spin2";
            }
            int i = name.lastIndexOf('.');
            File binaryFile = new File(name.substring(0, i) + ".binary");
            File listingFile = new File(name.substring(0, i) + ".lst");

            ByteArrayOutputStream binaryData = new ByteArrayOutputStream();

            if (name.toLowerCase().endsWith(".spin")) {
                Spin1TokenStream stream = new Spin1TokenStream(loadFromFile(fileToCompile));
                Spin1Parser parser = new Spin1Parser(stream);
                Node root = parser.parse();

                Spin1Compiler compiler = new Spin1CompilerAdapter(libraryPaths);
                compiler.setRemoveUnusedMethods(true);

                Spin1Object object = compiler.compile(name, root);
                for (CompilerMessage msg : compiler.getMessages()) {
                    System.out.println(msg);
                }

                if (compiler.hasErrors()) {
                    System.exit(1);
                }

                object.generateBinary(binaryData);

                FileOutputStream os = new FileOutputStream(listingFile);
                object.generateListing(new PrintStream(os));
                os.close();

                os = new FileOutputStream(binaryFile);
                os.write(binaryData.toByteArray());
                os.close();

                if (cmd.hasOption("u") || cmd.hasOption("f")) {
                    String serialPort = cmd.getOptionValue("p");
                    Propeller1Loader loader = new Propeller1Loader(serialPort);
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY);
                }
            }
            else {
                Spin2TokenStream stream = new Spin2TokenStream(loadFromFile(fileToCompile));
                Spin2Parser parser = new Spin2Parser(stream);
                Node root = parser.parse();

                Spin2Compiler compiler = new Spin2CompilerAdapter(libraryPaths);
                compiler.setRemoveUnusedMethods(true);

                Spin2Object object = compiler.compile(name, root);
                for (CompilerMessage msg : compiler.getMessages()) {
                    System.out.println(msg);
                }

                if (compiler.hasErrors()) {
                    System.exit(1);
                }

                object.generateBinary(binaryData);

                FileOutputStream os = new FileOutputStream(listingFile);
                object.generateListing(new PrintStream(os));
                os.close();

                os = new FileOutputStream(binaryFile);
                os.write(binaryData.toByteArray());
                os.close();

                if (cmd.hasOption("u") || cmd.hasOption("f")) {
                    String serialPort = cmd.getOptionValue("p");
                    Propeller2Loader loader = new Propeller2Loader(serialPort);
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM);
                }
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
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
