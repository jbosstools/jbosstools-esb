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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.ui.preferences.SeverityPreferencePage;
import org.jboss.tools.esb.validator.ESBValidatorPlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBValidatorPreferencePage extends SeverityPreferencePage {

	public static final String PREF_ID = "org.jboss.tools.esb.validator.ui.ESBValidatorPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.jboss.tools.esb.validator.ui.propertyPages.ESBValidatorPreferencePage"; //$NON-NLS-1$

	public ESBValidatorPreferencePage() {
		setPreferenceStore(ESBValidatorPlugin.getDefault().getPreferenceStore());
		setTitle(ESBPreferencesMessages.ESB_VALIDATOR_PREFERENCE_PAGE_ESB_VALIDATOR);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#getPreferencePageID()
	 */
	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#getPropertyPageID()
	 */
	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new ESBConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}
}