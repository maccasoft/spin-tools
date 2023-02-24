/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

public interface IContentProposalListener2 {
    /**
     * A content proposal popup has been opened for content proposal assistance.
     *
     * @param adapter
     *            the ContentProposalAdapter which is providing content proposal
     *            behavior to a control
     */
    public void proposalPopupOpened(ContentProposalAdapter adapter);

    /**
     * A content proposal popup has been closed.
     *
     * @param adapter
     *            the ContentProposalAdapter which is providing content proposal
     *            behavior to a control
     */
    public void proposalPopupClosed(ContentProposalAdapter adapter);

}
