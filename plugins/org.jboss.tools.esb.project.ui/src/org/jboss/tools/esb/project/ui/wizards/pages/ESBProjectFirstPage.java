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
package org.jboss.tools.esb.project.ui.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

public class ESBProjectFirstPage extends DataModelFacetCreationWizardPage {

	public ESBProjectFirstPage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
		setTitle(JBossESBUIMessages.ESBProjectFirstPage_Title);
		setDescription(JBossESBUIMessages.ESBProjectFirstPage_Description);
	}

	protected String getModuleTypeID() {
		return ESBProjectConstant.ESB_PROJECT_FACET;
	}
	
	protected void createPresetPanel(Composite top) {
		new Label(top, SWT.NONE);
	}

	@Override
	protected void validatePage() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				validatePage(true);
			}
			
		});
		
	}
	
}
