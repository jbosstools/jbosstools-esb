/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.validator;

import org.eclipse.osgi.util.NLS;

public class ESBValidatorMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.esb.validator.messages"; //$NON-NLS-1$

	public static String ESB_CORE_PLUGIN_NO_MESSAGE;

	public static String VALIDATING_RESOURCE;
	public static String VALIDATING_PROJECT;

	public static String LISTENER_REFERENCES_NON_EXISTENT_CHANNEL;
	public static String LISTENER_REFERENCES_INCOMPATIBLE_CHANNEL;

	public static String INVALID_RULE_SET_FOR_RULE_LANGUAGE;
	public static String INVALID_RULE_AUDIT_TYPE_AND_INTERVAL;
	
	public static String INVALID_OBJECT_PATH_WRONG_LOCATION;
	public static String INVALID_RULE_MAX_THREADS;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ESBValidatorMessages.class);
	}
}
