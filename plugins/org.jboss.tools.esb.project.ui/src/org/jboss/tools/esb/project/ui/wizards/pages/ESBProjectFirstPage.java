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
