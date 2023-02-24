/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class FileBrowserTest {

    Display display;
    Shell shell;
    FileBrowser view;

    TestFile root;
    TestFile homeUser;
    TestFile optParallax;

    @SuppressWarnings("serial")
    static class TestFile extends File {

        boolean directory = false;
        TestFile[] childs = new TestFile[0];

        public TestFile(TestFile parent, String child) {
            super(parent, child);
        }

        public TestFile(String pathname) {
            super(pathname);
        }

        public TestFile createFile(String name) {
            TestFile f = new TestFile(this, name);
            TestFile[] newChilds = new TestFile[childs.length + 1];
            System.arraycopy(childs, 0, newChilds, 0, childs.length);
            newChilds[childs.length] = f;
            childs = newChilds;
            return f;
        }

        public TestFile createDirectory(String name) {
            TestFile f = new TestFile(this, name);
            f.directory = true;
            TestFile[] newChilds = new TestFile[childs.length + 1];
            System.arraycopy(childs, 0, newChilds, 0, childs.length);
            newChilds[childs.length] = f;
            childs = newChilds;
            return f;
        }

        @Override
        public boolean isDirectory() {
            return directory;
        }

        @Override
        public File[] listFiles() {
            return childs;
        }

        @Override
        public File[] listFiles(FilenameFilter filter) {
            ArrayList<File> files = new ArrayList<>();
            for (TestFile f : childs) {
                if ((filter == null) || filter.accept(this, f.getPath())) {
                    files.add(f);
                }
            }
            return files.toArray(new File[files.size()]);
        }

        @Override
        public File[] listFiles(FileFilter filter) {
            ArrayList<File> files = new ArrayList<>();
            for (TestFile f : childs) {
                if ((filter == null) || filter.accept(f)) {
                    files.add(f);
                }
            }
            return files.toArray(new File[files.size()]);
        }

    };

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
        view = new FileBrowser(shell);

        root = new TestFile("/");
        root.directory = true;
        root.createDirectory("bin");
        root.createDirectory("dev");
        root.createDirectory("etc");
        homeUser = root.createDirectory("home").createDirectory("myuser");
        root.createDirectory("lib");
        optParallax = root.createDirectory("opt").createDirectory("parallax");
        root.createDirectory("root");
        root.createDirectory("usr");
        root.createDirectory("var");

        TestFile examples = homeUser.createDirectory("examples");
        examples.createFile("1-wire_demo.spin2");
        examples.createFile("ez_spi_demo.spin2");
        examples.createFile("i2c_devices.spin2");

        TestFile library = homeUser.createDirectory("library");
        library.createFile("1-wire.spin2");
        library.createFile("spi.spin2");
        library.createFile("i2c.spin2");
        library.createFile("serial.spin2");

        homeUser.createFile("blink.spin");
        homeUser.createFile("blink.spin2");

        optParallax.createFile("vga_text_demo.spin");
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        shell.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    void testDefaultTree() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return new File[] {
                    root
                };
            }

        });
        view.setVisiblePaths(new String[] {
            homeUser.getAbsolutePath()
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [home]\n"
            + "      +-- [myuser]\n"
            + "           +-- [examples]\n"
            + "                +-- [1-wire_demo.spin2]\n"
            + "                +-- [ez_spi_demo.spin2]\n"
            + "                +-- [i2c_devices.spin2]\n"
            + "           +-- [library]\n"
            + "                +-- [1-wire.spin2]\n"
            + "                +-- [i2c.spin2]\n"
            + "                +-- [serial.spin2]\n"
            + "                +-- [spi.spin2]\n"
            + "           +-- [blink.spin]\n"
            + "           +-- [blink.spin2]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    @Test
    void testFilteredTree() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return new File[] {
                    root
                };
            }

        });
        view.setVisiblePaths(new String[] {
            homeUser.getAbsolutePath() + "/examples"
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [home]\n"
            + "      +-- [myuser]\n"
            + "           +-- [examples]\n"
            + "                +-- [1-wire_demo.spin2]\n"
            + "                +-- [ez_spi_demo.spin2]\n"
            + "                +-- [i2c_devices.spin2]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    @Test
    void testFilteredTreeOutsideHome() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return new File[] {
                    root
                };
            }

        });
        view.setVisiblePaths(new String[] {
            optParallax.getAbsolutePath()
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [opt]\n"
            + "      +-- [parallax]\n"
            + "           +-- [vga_text_demo.spin]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    @Test
    void testMixedFilteredTree() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return new File[] {
                    root
                };
            }

        });
        view.setVisiblePaths(new String[] {
            homeUser.getAbsolutePath() + "/examples",
            optParallax.getAbsolutePath()
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [home]\n"
            + "      +-- [myuser]\n"
            + "           +-- [examples]\n"
            + "                +-- [1-wire_demo.spin2]\n"
            + "                +-- [ez_spi_demo.spin2]\n"
            + "                +-- [i2c_devices.spin2]\n"
            + " +-- [opt]\n"
            + "      +-- [parallax]\n"
            + "           +-- [vga_text_demo.spin]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    @Test
    void testMultipleRoot() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return root.listFiles();
            }

        });
        view.setVisiblePaths(new String[] {
            homeUser.getAbsolutePath()
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [home]\n"
            + "      +-- [myuser]\n"
            + "           +-- [examples]\n"
            + "                +-- [1-wire_demo.spin2]\n"
            + "                +-- [ez_spi_demo.spin2]\n"
            + "                +-- [i2c_devices.spin2]\n"
            + "           +-- [library]\n"
            + "                +-- [1-wire.spin2]\n"
            + "                +-- [i2c.spin2]\n"
            + "                +-- [serial.spin2]\n"
            + "                +-- [spi.spin2]\n"
            + "           +-- [blink.spin]\n"
            + "           +-- [blink.spin2]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    @Test
    void testFilteredMultipleRoot() {
        view.viewer.setInput(new FileBrowser.Model() {

            @Override
            public File[] getElements() {
                return root.listFiles();
            }

        });
        view.setVisiblePaths(new String[] {
            homeUser.getAbsolutePath() + "/examples",
            optParallax.getAbsolutePath()
        });
        view.viewer.expandAll();
        Assertions.assertEquals(""
            + " +-- [home]\n"
            + "      +-- [myuser]\n"
            + "           +-- [examples]\n"
            + "                +-- [1-wire_demo.spin2]\n"
            + "                +-- [ez_spi_demo.spin2]\n"
            + "                +-- [i2c_devices.spin2]\n"
            + " +-- [opt]\n"
            + "      +-- [parallax]\n"
            + "           +-- [vga_text_demo.spin]\n"
            + "", getTreeText(view.viewer.getTree()));
    }

    String getTreeText(Tree tree) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tree.getItemCount(); i++) {
            sb.append(getTreeText(tree.getItem(i), 1));
        }

        return sb.toString();
    }

    String getTreeText(TreeItem item, int indent) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < indent; i++) {
            sb.append("     ");
        }
        sb.append(" +-- ");

        sb.append("[" + item.getText() + "]");
        sb.append("\n");

        for (int i = 0; i < item.getItemCount(); i++) {
            sb.append(getTreeText(item.getItem(i), indent + 1));
        }

        return sb.toString();
    }

}
