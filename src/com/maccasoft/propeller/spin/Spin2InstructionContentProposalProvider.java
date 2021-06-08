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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class Spin2InstructionContentProposalProvider implements IContentProposalProvider {

    @Override
    public IContentProposal[] getProposals(String contents, int position) {
        int start = position;
        while (start > 0) {
            if (contents.charAt(start - 1) != '_' && !Character.isAlphabetic(contents.charAt(start - 1))) {
                break;
            }
            start--;
        }

        String token = contents.substring(start, position).toUpperCase();
        if (Spin2InstructionHelp.getString(token) != null) {
            return null;
        }

        String[] keys = Spin2InstructionHelp.getKeys();
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].toUpperCase().startsWith(token)) {
                proposals.add(new ContentProposal(keys[i], keys[i], Spin2InstructionHelp.getString(keys[i])));
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
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].toUpperCase().contains(token) && !keys[i].toUpperCase().startsWith(token)) {
                    proposals.add(new ContentProposal(keys[i], keys[i], Spin2InstructionHelp.getString(keys[i])));
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

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

}
