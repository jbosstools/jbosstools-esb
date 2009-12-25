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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.common.project.facet.core.internal.JavaFacetUtil;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
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
	
//	protected void createPresetPanel(Composite top) {
//		new Label(top, SWT.NONE);
//	}

	@Override
	protected void validatePage() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				validatePage(true);
			}
			
		});
		
	}
	
	protected IDialogSettings getDialogSettings() {
        return J2EEUIPlugin.getDefault().getDialogSettings();
    }
	
	
	@Override
	protected Set<IProjectFacetVersion> getFacetConfiguration( final IProjectFacetVersion primaryFacetVersion )
	{
	    final Set<IProjectFacetVersion> config = new HashSet<IProjectFacetVersion>();
		IFacetedProjectWorkingCopy fpjwc = (IFacetedProjectWorkingCopy) this.model
				.getProperty(FACETED_PROJECT_WORKING_COPY);
		for (IProjectFacet fixedFacet : fpjwc.getFixedProjectFacets()) {
			if (fixedFacet == primaryFacetVersion.getProjectFacet()) {
				config.add(primaryFacetVersion);
			} else if (fixedFacet == JavaFacetUtils.JAVA_FACET) {
				String compilerLevel = JavaFacetUtil.getCompilerLevel();
				IProjectFacetVersion facetVersion = JavaFacetUtil.compilerLevelToFacet(compilerLevel);
				config.add(facetVersion);
			} else {
				config.add(fpjwc.getHighestAvailableVersion(fixedFacet));
			}
		}
	    
	    return config;
	}
	
}
