package org.jboss.tools.esb.project.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.ui.INewWizard;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.esb.core.ESBProjectUtilities;
import org.jboss.tools.esb.project.ui.wizards.pages.ESBProjectFirstPage;

public class ESBProjectWizard extends NewProjectDataModelFacetWizard implements
		INewWizard {

	public ESBProjectWizard() {
		setWindowTitle("New ESB Project Wizard");
	}

	public ESBProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle("New ESB Project Wizard");
		
	}

	@Override
	protected IDataModel createDataModel() {
		return DataModelFactory.createDataModel(new JBossESBFacetProjectCreationDataModelProvider());
	}

	@Override
	protected IWizardPage createFirstPage() {
		return new ESBProjectFirstPage(model, "first.page");
	}

	@Override
	protected ImageDescriptor getDefaultPageImageDescriptor() {
		return null;
	}

	@Override
	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate(ESBProjectUtilities.ESB_PROJECT_FACET_TEMPLATE);
	}
	
	

}
