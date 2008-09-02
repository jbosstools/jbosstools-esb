package org.jboss.tools.esb.project.ui.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;
import org.jboss.tools.esb.core.ESBProjectUtilities;

public class ESBProjectFirstPage extends DataModelFacetCreationWizardPage {

	public ESBProjectFirstPage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
		setTitle("JBoss ESB Project");
		setDescription("Create a new JBoss ESB project.");
	}

	protected String getModuleTypeID() {
		return ESBProjectUtilities.ESB_PROJECT_FACET;
	}
	
	protected void createPresetPanel(Composite top) {
		new Label(top, SWT.NONE);
	}
	
}
