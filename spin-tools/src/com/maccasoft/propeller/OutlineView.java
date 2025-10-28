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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.internal.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.internal.ColorRegistry;
import com.maccasoft.propeller.internal.StyledStringBuilder;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TypeDefinitionNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class OutlineView {

    TreeViewer viewer;

    class Includes extends Node {

        public Includes() {
            super(Collections.singleton(new Token(0, "include directives")));
        }

    }

    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

        @Override
        public Object[] getElements(Object inputElement) {
            List<Object> list = new ArrayList<Object>();

            Includes includes = new Includes();

            ((Node) inputElement).accept(new NodeVisitor() {

                @Override
                public void visitDirective(DirectiveNode node) {
                    if (node instanceof DirectiveNode.IncludeNode) {
                        includes.addChild(node);
                    }
                    if (node instanceof DirectiveNode.DefineNode) {
                        list.add(node);
                    }
                }

                @Override
                public boolean visitConstants(ConstantsNode node) {
                    if (node.getTokenCount() == 0) {
                        node.accept(new NodeVisitor() {

                            @Override
                            public void visitTypeDefinition(TypeDefinitionNode node) {
                                if (node.getIdentifier() != null) {
                                    list.add(node);
                                }
                            }

                            @Override
                            public void visitConstant(ConstantNode node) {
                                if (node.getIdentifier() != null) {
                                    list.add(node);
                                }
                            }

                        });
                    }
                    else if (!node.isExclude()) {
                        list.add(node);
                    }
                    return true;
                }

                @Override
                public boolean visitVariables(VariablesNode node) {
                    list.add(node);
                    return false;
                }

                @Override
                public boolean visitObjects(ObjectsNode node) {
                    list.add(node);
                    return true;
                }

                @Override
                public boolean visitMethod(MethodNode node) {
                    if (node.getName() != null) {
                        list.add(node);
                    }
                    return true;
                }

                @Override
                public boolean visitFunction(FunctionNode node) {
                    if (node.getIdentifier() != null) {
                        list.add(node);
                    }
                    return true;
                }

                @Override
                public boolean visitData(DataNode node) {
                    list.add(node);
                    return true;
                }

            });

            if (includes.getChildCount() != 0) {
                list.add(0, includes);
            }

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            List<Object> list = new ArrayList<Object>();

            if (parentElement instanceof Includes) {
                list.addAll(((Includes) parentElement).getChilds());
                return list.toArray(new Object[list.size()]);
            }

            ((Node) parentElement).accept(new NodeVisitor() {

                @Override
                public void visitConstant(ConstantNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitTypeDefinition(TypeDefinitionNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitVariable(VariableNode node) {
                    if (node.identifier != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitObject(ObjectNode node) {
                    if (node.name != null) {
                        list.add(node);
                    }
                }

                @Override
                public void visitDataLine(DataLineNode node) {
                    if (node.label != null && !node.label.getText().startsWith(".") && !node.label.getText().startsWith(":")) {
                        list.add(node);
                    }
                }

            });

            return list.toArray(new Object[list.size()]);
        }

        @Override
        public Object getParent(Object element) {
            return ((Node) element).getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            return (element instanceof ConstantsNode) || (element instanceof VariablesNode) || (element instanceof ObjectsNode) || (element instanceof DataNode) || (element instanceof Includes);
        }

    };

    final OwnerDrawLabelProvider labelProvider = new StyledCellLabelProvider() {

        @Override
        public void update(ViewerCell cell) {
            StyledStringBuilder sb = null;

            try {
                Node element = (Node) cell.getElement();
                if (element instanceof ConstantsNode || element instanceof VariablesNode || element instanceof ObjectsNode || element instanceof DataNode) {
                    sb = decorateBlockStart(element);
                }
                else if (element instanceof Includes) {
                    sb = decorateBlockStart(element);
                }
                else if (element instanceof DirectiveNode.IncludeNode) {
                    sb = decorateIncludes((DirectiveNode.IncludeNode) element);
                }
                else if (element instanceof DirectiveNode.DefineNode) {
                    sb = decorateDefines((DirectiveNode.DefineNode) element);
                }
                else if (element instanceof ConstantNode) {
                    sb = decorateConstant((ConstantNode) element);
                }
                else if (element instanceof TypeDefinitionNode) {
                    sb = decorateTypeDefinition((TypeDefinitionNode) element);
                }
                else if (element instanceof VariableNode) {
                    sb = decorateVariable((VariableNode) element);
                }
                else if (element instanceof ObjectNode) {
                    sb = decorateObject((ObjectNode) element);
                }
                else if (element instanceof MethodNode) {
                    sb = decorateMethod((MethodNode) element);
                }
                else if (element instanceof FunctionNode) {
                    sb = decorateFunction((FunctionNode) element);
                }
                else if (element instanceof DataLineNode) {
                    sb = decorateData((DataLineNode) element);
                }

                if (sb != null) {
                    cell.setText(sb.getText());
                    if (element.isExclude()) {
                        StyleRange range = new StyleRange(commentStyle);
                        range.start = 0;
                        range.length = sb.length();
                        cell.setStyleRanges(new StyleRange[] {
                            range
                        });
                    }
                    else {
                        cell.setStyleRanges(sb.getTextStyles());
                    }
                }

                cell.setBackground(showSectionsBackground ? getLineBackground(getInput(), element.getStartIndex()) : null);
            } catch (Exception e) {
                // Do nothing
            }

            super.update(cell);
        }

    };

    final IElementComparer elementComparer = new IElementComparer() {

        @Override
        public int hashCode(Object element) {
            if (element == null) {
                return 0;
            }
            return getPath(element).hashCode();
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (a == null || b == null) {
                return false;
            }
            return getPath(a).equals(getPath(b));
        }

    };

    TextStyle commentStyle;
    TextStyle blockStyle;
    TextStyle methodLocalStyle;
    TextStyle methodReturnStyle;
    TextStyle typeStyle;
    TextStyle stringStyle;
    Font fontBold;

    boolean showSectionsBackground;

    public OutlineView(Composite parent) {
        viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setComparer(elementComparer);

        FontData[] fd = viewer.getControl().getFont().getFontData();
        fd[0].setStyle(SWT.BOLD);
        fontBold = new Font(viewer.getControl().getDisplay(), fd[0]);

        applyTheme(Preferences.getInstance().getTheme());
    }

    public void applyTheme(String id) {
        styleMap.clear();

        commentStyle = new TextStyle(null, new Color(0x7E, 0x7E, 0x7E), null);
        blockStyle = new TextStyle(fontBold, null, null);
        methodLocalStyle = new TextStyle(null, new Color(0x80, 0x80, 0x00), null);
        methodReturnStyle = new TextStyle(null, new Color(0x90, 0x00, 0x00), null);
        typeStyle = new TextStyle(fontBold, null, null);
        stringStyle = new TextStyle(null, new Color(0x7E, 0x00, 0x7E), null);

        if ("win32".equals(Platform.PLATFORM) && id == null) {
            if (Display.isSystemDarkTheme()) {
                id = "dark";
            }
        }

        if (id == null) {
            id = Display.isSystemDarkTheme() ? "dark" : "light";
        }

        if ("dark".equals(id)) {
            // Do nothing
        }
        else if ("light".equals(id)) {
            styleMap.put(TokenId.CON, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xF8, 0xC0)));
            styleMap.put(TokenId.CON_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xF3, 0xA8)));
            styleMap.put(TokenId.VAR, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xDF, 0xBF)));
            styleMap.put(TokenId.VAR_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xD2, 0xA7)));
            styleMap.put(TokenId.OBJ, new TextStyle(null, null, ColorRegistry.getColor(0xFF, 0xBF, 0xBF)));
            styleMap.put(TokenId.OBJ_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xFD, 0xA7, 0xA7)));
            styleMap.put(TokenId.PUB, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xDF, 0xFF)));
            styleMap.put(TokenId.PUB_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xD2, 0xFD)));
            styleMap.put(TokenId.PRI, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xF8, 0xFF)));
            styleMap.put(TokenId.PRI_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xF3, 0xFD)));
            styleMap.put(TokenId.DAT, new TextStyle(null, null, ColorRegistry.getColor(0xBF, 0xFF, 0xC8)));
            styleMap.put(TokenId.DAT_ALT, new TextStyle(null, null, ColorRegistry.getColor(0xA7, 0xFD, 0xB3)));
        }
    }

    public void setLayoutData(Object layoutData) {
        viewer.getControl().setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return viewer.getControl().getLayoutData();
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        viewer.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        viewer.removeSelectionChangedListener(listener);
    }

    public void addOpenListener(IOpenListener listener) {
        viewer.addOpenListener(listener);
    }

    public void removeOpenListener(IOpenListener listener) {
        viewer.addOpenListener(listener);
    }

    public Node getInput() {
        return (Node) viewer.getInput();
    }

    public void setInput(Node node) {
        viewer.getTree().setRedraw(false);
        try {
            Object[] expandedElements = viewer.getExpandedElements();
            viewer.setInput(node);
            viewer.setExpandedElements(expandedElements);
        } finally {
            viewer.getTree().setRedraw(true);
        }
    }

    public void setSelection(IStructuredSelection selection) {
        viewer.setSelection(selection, true);
    }

    public Control getControl() {
        return viewer.getControl();
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    String getPath(Object element) {

        if (element instanceof TreePath) {
            element = ((TreePath) element).getLastSegment();
        }
        return ((Node) element).getPath();
    }

    public void setVisible(boolean visible) {
        viewer.getControl().setVisible(visible);
    }

    public boolean getVisible() {
        return viewer.getControl().getVisible();
    }

    public void dispose() {
        viewer.getControl().dispose();
    }

    public void refresh() {
        viewer.refresh(true);
    }

    public void setBackground(Color color) {
        viewer.getControl().setBackground(color);
    }

    public void setForeground(Color color) {
        viewer.getControl().setForeground(color);
    }

    StyledStringBuilder decorateBlockStart(Node node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        String text = "";
        if (node.getStartToken() != null) {
            text = node.getStartToken().getText().toUpperCase();
        }
        sb.append(text, blockStyle);

        String comment = node.getDescription();
        if (comment != null) {
            while (comment.startsWith("'")) {
                comment = comment.substring(1);
            }
            comment = comment.trim();
            if (!comment.isEmpty()) {
                sb.append(" ");
                sb.append(comment, commentStyle);
            }
        }

        return sb;
    }

    StyledStringBuilder decorateVariable(VariableNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        if (node.identifier != null) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(node.identifier.getText());
        }
        if (node.type != null) {
            sb.append(" : ");
            sb.append(node.type.getText(), methodReturnStyle);
        }
        else if (node.getParent() instanceof VariablesNode) {
            VariablesNode parent = (VariablesNode) node.getParent();
            int index = 0;
            while (!(parent.getChild(index) instanceof VariableNode)) {
                index++;
            }
            Token type = ((VariableNode) parent.getChild(index)).type;
            if (type != null) {
                sb.append(" : ");
                sb.append(type.getText(), methodReturnStyle);
            }
        }

        return sb;
    }

    StyledStringBuilder decorateObject(ObjectNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        if (node.name != null) {
            sb.append(node.name.getText());
            sb.append(" : ");
            if (node.file != null) {
                sb.append(node.getFileName(), stringStyle);
            }
        }

        return sb;
    }

    StyledStringBuilder decorateMethod(MethodNode node) {
        boolean first;
        StyledStringBuilder sb = new StyledStringBuilder();

        if (node.getType() != null) {
            sb.append(node.getType().getText().toUpperCase(), blockStyle);
            sb.append(" ");
        }

        if (node.getName() != null) {
            sb.append(node.getName().getText());
        }

        first = true;
        sb.append("(");
        for (Node child : node.getParameters()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(child.getText(), methodLocalStyle);
            first = false;
        }
        sb.append(")");

        if (node.getReturnVariables().size() != 0) {
            first = true;
            sb.append(" : ");
            for (Node child : node.getReturnVariables()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(child.getText(), methodReturnStyle);
                first = false;
            }
        }

        return sb;
    }

    StyledStringBuilder decorateData(DataLineNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        sb.append(node.label.getText());

        return sb;
    }

    StyledStringBuilder decorateIncludes(DirectiveNode.IncludeNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        sb.append(node.getFile().getText(), stringStyle);

        return sb;
    }

    StyledStringBuilder decorateDefines(DirectiveNode.DefineNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        sb.append("# ", blockStyle);
        sb.append(node.getIdentifier().getText());

        return sb;
    }

    StyledStringBuilder decorateConstant(ConstantNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        sb.append(node.getIdentifier().getText());

        return sb;
    }

    StyledStringBuilder decorateTypeDefinition(TypeDefinitionNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        sb.append(node.getIdentifier().getText());

        return sb;
    }

    StyledStringBuilder decorateFunction(FunctionNode node) {
        StyledStringBuilder sb = new StyledStringBuilder();

        if (node.getIdentifier() != null) {
            sb.append(node.getIdentifier().getText(), blockStyle);
        }

        sb.append("(");
        boolean first = true;
        for (FunctionNode.ParameterNode child : node.getParameters()) {
            if (!first) {
                sb.append(", ");
            }
            if (child.type != null) {
                sb.append(child.type.getText(), methodLocalStyle);
            }
            else {
                sb.append(child.identifier.getText(), methodLocalStyle);
            }
            first = false;
        }
        sb.append(")");

        if (node.getType() != null) {
            sb.append(" : ");
            sb.append(node.getType().getText(), methodReturnStyle);
        }

        return sb;
    }

    boolean[] blockToggle = new boolean[6];
    Map<TokenId, TextStyle> styleMap = new HashMap<TokenId, TextStyle>();

    public Color getLineBackground(Node root, int lineOffset) {
        TokenId id = getSectionBackgroundId(null);

        if (root != null) {
            for (Node child : root.getChilds()) {
                if (lineOffset < child.getStartIndex()) {
                    break;
                }
                id = getSectionBackgroundId(child);
            }
        }

        if (id != null) {
            TextStyle style = styleMap.get(id);
            if (style != null) {
                return style.background;
            }
        }

        return null;
    }

    TokenId getSectionBackgroundId(Node node) {
        TokenId result = null;

        if (node == null) {
            blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }

        if (node instanceof VariablesNode) {
            result = blockToggle[1] ? TokenId.VAR_ALT : TokenId.VAR;
            blockToggle[1] = !blockToggle[1];
            blockToggle[0] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }
        else if (node instanceof ObjectsNode) {
            result = blockToggle[2] ? TokenId.OBJ_ALT : TokenId.OBJ;
            blockToggle[2] = !blockToggle[2];
            blockToggle[0] = blockToggle[1] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).isPublic()) {
                result = blockToggle[3] ? TokenId.PUB_ALT : TokenId.PUB;
                blockToggle[3] = !blockToggle[3];
                blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[4] = blockToggle[5] = false;
            }
            else {
                result = blockToggle[4] ? TokenId.PRI_ALT : TokenId.PRI;
                blockToggle[4] = !blockToggle[4];
                blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[5] = false;
            }
        }
        else if (node instanceof DataNode) {
            result = blockToggle[5] ? TokenId.DAT_ALT : TokenId.DAT;
            blockToggle[5] = !blockToggle[5];
            blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = false;
        }
        else {
            result = blockToggle[0] ? TokenId.CON_ALT : TokenId.CON;
            blockToggle[0] = !blockToggle[0];
            blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }

        return result;
    }

    public void setShowSectionsBackground(boolean showSectionsBackground) {
        this.showSectionsBackground = showSectionsBackground;
        if (viewer != null && !viewer.getControl().isDisposed()) {
            viewer.refresh(true);
        }
    }

}
