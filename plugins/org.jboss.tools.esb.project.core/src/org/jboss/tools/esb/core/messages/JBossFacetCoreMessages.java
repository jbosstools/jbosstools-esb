/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.esb.core.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Grid Qian
 */
public class JBossFacetCoreMessages {

	private static final String BUNDLE_NAME = "org.jboss.tools.esb.core.messages.JBossFacetCore"; //$NON-NLS-1$

	private JBossFacetCoreMessages() {
		// Do not instantiate
	}

	public static String Error_Copy;
	public static String ESB_Location;
	public static String JBoss_Runtime;
	public static String Error_Add_Facet_JBossESB;
	public static String Error_Remove_Facet_JBossESB;


	static {
		NLS.initializeMessages(BUNDLE_NAME, JBossFacetCoreMessages.class);
	}
}