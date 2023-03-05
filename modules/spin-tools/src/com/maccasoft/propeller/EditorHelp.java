/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    public List<IContentProposal> fillProposals(String context, String token) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

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
                            List<IContentProposal> list = new ArrayList<>();

                            NodeList childList = node.getChildNodes();
                            for (int ii = 0; ii < childList.getLength(); ii++) {
                                if (!(childList.item(ii) instanceof Element)) {
                                    continue;
                                }
                                Element element = (Element) childList.item(ii);
                                if ("entry".equals(element.getTagName())) {
                                    String[] key = element.getAttribute("name").split(",");
                                    for (int n = 0; n < key.length; n++) {
                                        if (key[n].toUpperCase().startsWith(token)) {
                                            String insert = element.getAttribute("insert");
                                            if (insert != null && !"".equals(insert)) {
                                                list.add(new ContentProposal(insert, key[n], element.getTextContent()));
                                                break;
                                            }
                                            else {
                                                list.add(new ContentProposal(key[n], key[n], element.getTextContent()));
                                            }
                                        }
                                    }
                                }
                            }

                            Collections.sort(list, new Comparator<IContentProposal>() {

                                @Override
                                public int compare(IContentProposal o1, IContentProposal o2) {
                                    return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                                }

                            });
                            proposals.addAll(list);
                        }
                    }
                }
            } finally {
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return proposals;
    }

    public List<IContentProposal> fillSourceProposals(String prefix) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        proposals.addAll(getSourceProposals(sourceFolder, prefix));

        File[] searchPaths = ".spin2".equals(sourceFilter) ? Preferences.getInstance().getSpin2LibraryPath() : Preferences.getInstance().getSpin1LibraryPath();
        for (int i = 0; i < searchPaths.length; i++) {
            proposals.addAll(getSourceProposals(searchPaths[i], prefix));
        }

        return proposals;
    }

    List<IContentProposal> getSourceProposals(File folder, String prefix) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        List<IContentProposal> prefixProposals = new ArrayList<IContentProposal>();
        List<IContentProposal> containsProposal = new ArrayList<IContentProposal>();
        Set<File> included = new HashSet<File>();

        File[] list = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(sourceFilter);
            }

        });
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                String name = list[i].getName();
                name = name.substring(0, name.indexOf(sourceFilter));
                if (name.toUpperCase().startsWith(prefix)) {
                    prefixProposals.add(new ContentProposal(name, name, null));
                    included.add(list[i]);
                }
            }
            for (int i = 0; i < list.length; i++) {
                String name = list[i].getName();
                name = name.substring(0, name.indexOf(sourceFilter));
                if (name.toUpperCase().contains(prefix) && !included.contains(list[i])) {
                    containsProposal.add(new ContentProposal(name, name, null));
                    included.add(list[i]);
                }
            }
        }
        else {
            System.err.println(folder);
        }

        Collections.sort(prefixProposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });
        Collections.sort(containsProposal, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });

        proposals.addAll(prefixProposals);
        proposals.addAll(containsProposal);

        return proposals;
    }

}
