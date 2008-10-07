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

	public static String Progress_Install_JBossWS_Runtime;
	public static String Progress_UnInstall_JBossWS_Runtime;
	public static String Dir_Lib;
	public static String Dir_Client;
	public static String Dir_Web_Inf;
	public static String Dir_Web_Content;
	public static String Error_Copy;
	public static String ESB_Location;
	public static String JBoss_Runtime;
	public static String JBossAS;
	public static String Error_WS_Location;
	public static String Error_WS_Classpath;
	public static String Error_Add_Facet_JBossESB;
	public static String Error_Remove_Facet_JBossWS;


	static {
		NLS.initializeMessages(BUNDLE_NAME, JBossFacetCoreMessages.class);
	}
}