/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Spin2InstructionHelp {

    public static String getString(String context, String key) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            InputStream is = Spin2InstructionHelp.class.getResourceAsStream("Spin2Instructions.xml");
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);

                NodeList rootNodeList = doc.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < rootNodeList.getLength(); i++) {
                    if (!(rootNodeList.item(i) instanceof Element)) {
                        continue;
                    }
                    Element node = (Element) rootNodeList.item(i);
                    if ("entry".equals(node.getTagName())) {
                        if (key.equalsIgnoreCase(node.getAttribute("name"))) {
                            return node.getTextContent();
                        }
                    }
                    else if ("section".equals(node.getTagName())) {
                        if (context != null && node.getAttribute("class").contains(context)) {
                            NodeList childList = node.getChildNodes();
                            for (int ii = 0; ii < childList.getLength(); ii++) {
                                if (!(childList.item(ii) instanceof Element)) {
                                    continue;
                                }
                                Element element = (Element) childList.item(ii);
                                if ("entry".equals(element.getTagName())) {
                                    if (key.equalsIgnoreCase(element.getAttribute("name"))) {
                                        return element.getTextContent();
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

    public static void fillProposals(String context, String token, List<IContentProposal> proposals) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            InputStream is = Spin2InstructionHelp.class.getResourceAsStream("Spin2Instructions.xml");
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);

                NodeList rootNodeList = doc.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < rootNodeList.getLength(); i++) {
                    if (!(rootNodeList.item(i) instanceof Element)) {
                        continue;
                    }
                    Element node = (Element) rootNodeList.item(i);
                    if ("entry".equals(node.getTagName())) {
                        String key = node.getAttribute("name");
                        if (key.toUpperCase().startsWith(token)) {
                            String insert = node.getAttribute("insert");
                            if (insert == null || "".equals(insert)) {
                                insert = key;
                            }
                            proposals.add(new ContentProposal(insert, key, node.getTextContent()));
                        }
                    }
                    else if ("section".equals(node.getTagName())) {
                        if (context != null && node.getAttribute("class").contains(context)) {
                            NodeList childList = node.getChildNodes();
                            for (int ii = 0; ii < childList.getLength(); ii++) {
                                if (!(childList.item(ii) instanceof Element)) {
                                    continue;
                                }
                                Element element = (Element) childList.item(ii);
                                if ("entry".equals(element.getTagName())) {
                                    String key = element.getAttribute("name");
                                    if (key.toUpperCase().startsWith(token)) {
                                        String insert = element.getAttribute("insert");
                                        if (insert == null || "".equals(insert)) {
                                            insert = key;
                                        }
                                        proposals.add(new ContentProposal(insert, key, element.getTextContent()));
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
    }

}
