/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.esb.ui;

import org.eclipse.osgi.util.NLS;

public final class ESBUIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.jboss.tools.esb.ui.messages";//$NON-NLS-1$

	private ESBUIMessages() {
		// Do not instantiate
	}

	public static String ESB_UI_PLUGIN_NO_MESSAGES;
	public static String newESBActionWizardTitle;
	public static String newESBActionWizardPageTitle;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ESBUIMessages.class);
	}
}