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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Spin2InstructionContentProposalProvider implements IContentProposalProvider {

    @Override
    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        int start = position;
        while (start > 0) {
            if (contents.charAt(start - 1) != '_' && !Character.isAlphabetic(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }

        String token = contents.substring(start, position).toUpperCase();
        if ("".equals(token) || Spin2InstructionHelp.getString(token) != null) {
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            InputStream is = getClass().getResourceAsStream("Spin2Instructions.xml");
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);

                NodeList list = doc.getElementsByTagName("entry");
                for (int i = 0; i < list.getLength(); i++) {
                    Element node = (Element) list.item(i);
                    String key = node.getAttribute("name");
                    if (key.toUpperCase().startsWith(token)) {
                        String insert = node.getAttribute("insert");
                        if (insert == null || "".equals(insert)) {
                            insert = key;
                        }
                        proposals.add(new ContentProposal(insert, key, node.getTextContent()));
                    }
                }

                Collections.sort(proposals, new Comparator<IContentProposal>() {

                    @Override
                    public int compare(IContentProposal o1, IContentProposal o2) {
                        return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                    }

                });

                if (token.length() > 1) {
                    List<IContentProposal> otherProposals = new ArrayList<IContentProposal>();

                    for (int i = 0; i < list.getLength(); i++) {
                        Element node = (Element) list.item(i);
                        String key = node.getAttribute("name");
                        if (key.toUpperCase().contains(token) && !key.toUpperCase().startsWith(token)) {
                            String insert = node.getAttribute("insert");
                            if (insert == null || "".equals(insert)) {
                                insert = key;
                            }
                            proposals.add(new ContentProposal(insert, key, node.getTextContent()));
                        }
                    }

                    Collections.sort(otherProposals, new Comparator<IContentProposal>() {

                        @Override
                        public int compare(IContentProposal o1, IContentProposal o2) {
                            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                        }

                    });
                    proposals.addAll(otherProposals);
                }
            } finally {
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

}
