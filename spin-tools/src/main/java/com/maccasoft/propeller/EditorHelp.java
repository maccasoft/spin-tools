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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.Strings;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EditorHelp {

    final String helpFile;
    final File sourceFolder;
    final String sourceFilter;

    public EditorHelp(String helpFile, File sourceFolder, String sourceFilter) {
        this.helpFile = helpFile;
        this.sourceFolder = sourceFolder;
        this.sourceFilter = sourceFilter.toLowerCase();
    }

    public String getString(String context, String key) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            InputStream is = EditorHelp.class.getResourceAsStream(helpFile);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);

                NodeList rootNodeList = doc.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < rootNodeList.getLength(); i++) {
                    if (!(rootNodeList.item(i) instanceof Element)) {
                        continue;
                    }
                    Element node = (Element) rootNodeList.item(i);
                    if ("section".equals(node.getTagName())) {
                        if (context != null && node.getAttribute("class").contains(context)) {
                            NodeList childList = node.getChildNodes();
                            for (int ii = 0; ii < childList.getLength(); ii++) {
                                if (!(childList.item(ii) instanceof Element)) {
                                    continue;
                                }
                                Element element = (Element) childList.item(ii);
                                if ("entry".equals(element.getTagName())) {
                                    String[] s = element.getAttribute("name").split(",");
                                    for (int n = 0; n < s.length; n++) {
                                        if (key.equalsIgnoreCase(s[n])) {
                                            return element.getTextContent();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IContentProposal> fillProposals(String context, String filterText, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try (InputStream is = EditorHelp.class.getResourceAsStream(helpFile)) {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            NodeList rootNodeList = doc.getChildNodes().item(0).getChildNodes();
            for (int i = 0; i < rootNodeList.getLength(); i++) {
                if (!(rootNodeList.item(i) instanceof Element node)) {
                    continue;
                }
                if ("section".equals(node.getTagName())) {
                    if (context != null && node.getAttribute("class").contains(context)) {
                        List<IContentProposal> list = new ArrayList<>();

                        NodeList childList = node.getChildNodes();
                        for (int ii = 0; ii < childList.getLength(); ii++) {
                            if (!(childList.item(ii) instanceof Element element)) {
                                continue;
                            }
                            if ("entry".equals(element.getTagName())) {
                                for (String text : element.getAttribute("name").split(",")) {
                                    int foundAt = Strings.CI.indexOf(text, filterText);
                                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                        String insert = element.getAttribute("insert");
                                        if (!insert.isEmpty()) {
                                            list.add(new ContentProposal(insert, text, element.getTextContent()));
                                            break;
                                        }
                                        else {
                                            list.add(new ContentProposal(text, text, element.getTextContent()));
                                        }
                                    }
                                }
                            }
                        }
                        list.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
                        proposals.addAll(list);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return proposals;
    }

    public List<IContentProposal> fillSourceProposals(String prefix, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>(getSourceProposals(sourceFolder, prefix, startsOnly));

        File[] searchPaths = ".spin2".equals(sourceFilter) ? Preferences.getInstance().getSpin2LibraryPath() : Preferences.getInstance().getSpin1LibraryPath();
        for (File searchPath : searchPaths) {
            proposals.addAll(getSourceProposals(searchPath, prefix, startsOnly));
        }

        return proposals;
    }

    List<IContentProposal> getSourceProposals(File folder, String filterText, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>();

        File[] list = folder.listFiles((dir, name) -> Strings.CI.endsWith(name, sourceFilter));
        if (list != null) {
            for (File file : list) {
                String fileName = file.getName();
                String text = fileName.substring(0, Strings.CI.lastIndexOf(fileName, sourceFilter));
                int foundAt = Strings.CI.indexOf(text, filterText);
                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                    proposals.add(new ContentProposal(text, text, null));
                }
            }
            proposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
        }

        return proposals;
    }

}
