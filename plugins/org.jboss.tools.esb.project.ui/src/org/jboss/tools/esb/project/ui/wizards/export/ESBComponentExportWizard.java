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
package org.jboss.tools.esb.project.ui.wizards.export;

import org.eclipse.jst.j2ee.internal.wizard.J2EEArtifactExportWizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.jboss.tools.esb.project.ui.ESBSharedImages;

public class ESBComponentExportWizard extends J2EEArtifactExportWizard
		implements IExportWizard {

	public ESBComponentExportWizard() {
		super();
	}
	
	public ESBComponentExportWizard(IDataModel model) {
		super(model);
	}
    
    protected IDataModelProvider getDefaultProvider() {
        return new ESBComponentExportDataModelProvider();
    }

    public void doAddPages() {
		addPage(new ESBComponentExportPage(getDataModel(), MAIN_PG, getSelection()));
	}

	protected void doInit() {
		setDefaultPageImageDescriptor(ESBSharedImages.getImageDescriptor(ESBSharedImages.WIZARD_NEW_PROJECT));
	}
}
