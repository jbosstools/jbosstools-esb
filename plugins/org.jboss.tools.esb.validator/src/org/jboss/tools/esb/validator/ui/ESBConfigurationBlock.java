/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.validator.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.esb.validator.ESBPreferences;
import org.jboss.tools.esb.validator.ESBValidatorPlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBConfigurationBlock extends SeverityConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME = "CDIValidatorConfigurationBlock";

	private static SectionDescription SECTION_CHANNEL_ID_REF = new SectionDescription(
		ESBPreferencesMessages.ESBValidatorConfigurationBlock_section_channelidref,
		new String[][]{
			{ESBPreferences.LISTENER_REFERENCES_NON_EXISTENT_CHANNEL, ESBPreferencesMessages.ESBValidatorConfigurationBlock_pb_listenerReferencesNonExistentChannel_label},
			{ESBPreferences.LISTENER_REFERENCES_INCOMPATIBLE_CHANNEL, ESBPreferencesMessages.ESBValidatorConfigurationBlock_pb_listenerReferencesIncompatibleChannel_label},
		},
		ESBValidatorPlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = new SectionDescription[]{
		SECTION_CHANNEL_ID_REF,
	};

	private static Key[] getKeys() {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (int i = 0; i < ALL_SECTIONS.length; i++) {
			for (int j = 0; j < ALL_SECTIONS[i].options.length; j++) {
				keys.add(ALL_SECTIONS[i].options[j].key);
			}
		}
		return keys.toArray(new Key[0]);
	}

	public ESBConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	@Override
	protected SectionDescription[] getAllSections() {
		return ALL_SECTIONS;
	}

	@Override
	protected String getCommonDescription() {
		return ESBPreferencesMessages.ESBValidatorConfigurationBlock_common_description;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return ESBValidatorPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
	}
}