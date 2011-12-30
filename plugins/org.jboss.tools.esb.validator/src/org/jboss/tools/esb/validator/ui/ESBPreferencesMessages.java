/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.esb.validator.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.esb.validator.ui.ESBPreferencesMessages"; //$NON-NLS-1$

	// Validator Preference page
	public static String ESBValidatorConfigurationBlock_common_description;

	// Section Channel ID Ref
	public static String ESBValidatorConfigurationBlock_section_channelidref;
	public static String ESBValidatorConfigurationBlock_pb_listenerReferencesNonExistentChannel_label;
	public static String ESBValidatorConfigurationBlock_pb_listenerReferencesIncompatibleChannel_label;

	// Section Schedule ID Ref
	public static String ESBValidatorConfigurationBlock_section_scheduleidref;
	public static String ESBValidatorConfigurationBlock_pb_listenerReferencesNonExistentSchedule_label;

	public static String ESBValidatorConfigurationBlock_section_actions;
	public static String ESBValidatorConfigurationBlock_pb_businessRulesProcessorProblems_label;

	public static String ESB_VALIDATOR_PREFERENCE_PAGE_ESB_VALIDATOR;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ESBPreferencesMessages.class);
	}
}