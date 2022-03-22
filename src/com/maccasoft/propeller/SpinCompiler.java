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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin2.Spin2Compiler;

public class SpinCompiler {

    public static final String APP_TITLE = "Spin Tools Compiler";
    public static final String APP_VERSION = SpinTools.APP_VERSION;

    public static void main(String[] args) {
        List<File> libraryPaths = new ArrayList<File>();

        System.out.println(APP_TITLE + " - Version " + APP_VERSION);
        System.out.println("Copyright (c) 2021-22 Marco Maccaferri and others. All rights reserved.");

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
                name += ".spin";
                fileToCompile = new File(fileToCompile.getParentFile(), name);
            }

            if (!fileToCompile.exists()) {
                throw new FileNotFoundException("File " + fileToCompile.getAbsolutePath() + " not found");
            }

            int i = name.lastIndexOf('.');
            File binaryFile = new File(name.substring(0, i) + ".binary");
            File listingFile = new File(name.substring(0, i) + ".lst");

            ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
            PrintStream listingStream = new PrintStream(new FileOutputStream(listingFile));

            System.out.println("Compiling...");

            Compiler compiler = name.toLowerCase().endsWith(".spin2") ? new Spin2Compiler() : new Spin1Compiler();
            compiler.addSourceProvider(new Compiler.FileSourceProvider(libraryPaths.toArray(new File[0])));
            try {
                compiler.compile(fileToCompile, binaryData, listingStream);
            } finally {
                listingStream.close();
            }

            FileOutputStream os = new FileOutputStream(binaryFile);
            os.write(binaryData.toByteArray());
            os.close();

            System.out.println("Done.");
            System.out.println("Program size is " + binaryData.size() + " bytes");

            if (cmd.hasOption("u") || cmd.hasOption("f")) {
                String serialPort = cmd.getOptionValue("p");
                if (compiler instanceof Spin1Compiler) {
                    Propeller1Loader loader = new Propeller1Loader(serialPort);
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller1Loader.DOWNLOAD_RUN_EEPROM : Propeller1Loader.DOWNLOAD_RUN_BINARY);
                }
                else {
                    Propeller2Loader loader = new Propeller2Loader(serialPort);
                    loader.upload(binaryData.toByteArray(), cmd.hasOption("f") ? Propeller2Loader.DOWNLOAD_RUN_FLASH : Propeller2Loader.DOWNLOAD_RUN_RAM);
                }
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
