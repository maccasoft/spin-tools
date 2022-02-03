package com.maccasoft.propeller;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

@TestInstance(Lifecycle.PER_CLASS)
class OutlineViewTest {

    Display display;
    Shell shell;
    OutlineView view;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
        view = new OutlineView(shell);
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
    void testPubComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/PUB main()", view.getPath(root.getChild(0)));
    }

    @Test
    void testPubTreepathComparer() {
        String text = ""
            + "PUB main\n"
            + "'\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });

        Assertions.assertEquals("/PUB main()", view.getPath(path0));
    }

    @Test
    void testDatComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/driver", view.getPath(root.getChild(0).getChild(1)));
    }

    @Test
    void testMultipleDatComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver1\n"
            + "'\n"
            + "DAT         org     $000\n"
            + "'\n"
            + "driver2\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Assertions.assertEquals("/DAT0", view.getPath(root.getChild(0)));
        Assertions.assertEquals("/DAT0/driver1", view.getPath(root.getChild(0).getChild(1)));
        Assertions.assertEquals("/DAT1", view.getPath(root.getChild(1)));
        Assertions.assertEquals("/DAT1/driver2", view.getPath(root.getChild(1).getChild(1)));
    }

    @Test
    void testDatTreepathComparer() {
        String text = ""
            + "DAT         org     $000\n"
            + "'\n"
            + "driver\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        TreePath path0 = new TreePath(new Object[] {
            root.getChild(0)
        });
        TreePath path1 = new TreePath(new Object[] {
            root.getChild(0), root.getChild(0).getChild(1)
        });

        Assertions.assertEquals("/DAT0", view.getPath(path0));
        Assertions.assertEquals("/DAT0/driver", view.getPath(path1));
    }

}
