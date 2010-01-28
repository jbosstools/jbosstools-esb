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
package org.jboss.tools.esb.project.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.RuntimeClasspathContainer;
import org.eclipse.jst.server.core.internal.RuntimeClasspathProviderWrapper;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.facet.JBossClassPathCommand;
import org.jboss.tools.esb.project.ui.ESBSharedImages;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;
import org.jboss.tools.esb.project.ui.wizards.pages.ESBProjectFirstPage;

public class ESBProjectWizard extends NewProjectDataModelFacetWizard implements
		INewWizard {

	public ESBProjectWizard() {
		super();
		setWindowTitle(JBossESBUIMessages.ESBProjectWizard_Title);
		setDefaultPageImageDescriptor(ESBSharedImages.getImageDescriptor(ESBSharedImages.WIZARD_NEW_PROJECT));
	}

	public ESBProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle(JBossESBUIMessages.ESBProjectWizard_Title);
		setDefaultPageImageDescriptor(ESBSharedImages.getImageDescriptor(ESBSharedImages.WIZARD_NEW_PROJECT));
		
	}

	@Override
	protected IDataModel createDataModel() {
		return DataModelFactory.createDataModel(new JBossESBFacetProjectCreationDataModelProvider());
	}

	@Override
	protected IWizardPage createFirstPage() {
		return new ESBProjectFirstPage(model, "first.page"); //$NON-NLS-1$
	}

	@Override
	protected ImageDescriptor getDefaultPageImageDescriptor() {
		return ESBSharedImages.getImageDescriptor(ESBSharedImages.WIZARD_NEW_PROJECT);
	}

	@Override
	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate(ESBProjectConstant.ESB_PROJECT_FACET_TEMPLATE);
	}

	@Override
	protected void postPerformFinish() throws InvocationTargetException {
		super.postPerformFinish();
		String prjName = this.getProjectName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(prjName);
		if(!project.exists()) return;
		
		try {
			String esbcontent = project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
			IPath esbPath = new Path(esbcontent).append(ESBProjectConstant.META_INF);
			IFile esbFile = project.getFolder(esbPath).getFile(ESBProjectConstant.ESB_CONFIG_JBOSSESB);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, esbFile);
			
			// Add the server runtime as well
			IFacetedProject fp = ProjectFacetsManager.create(project);
			IRuntime runtime = fp.getPrimaryRuntime();
			
			if(runtime == null) return;
			
			String name = runtime.getName();
			org.eclipse.wst.server.core.IRuntime serverRuntime = ServerCore.findRuntime(name);
			RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(serverRuntime.getRuntimeType());
			IPath serverContainerPath = new Path(RuntimeClasspathContainer.SERVER_CONTAINER)
				.append(rcpw.getId()).append(serverRuntime.getId());
			JBossClassPathCommand.addClassPath(project, serverContainerPath);

		} catch (CoreException e) {
			ESBProjectCorePlugin.getDefault().getLog().log(e.getStatus());
		}
		
	}
	
	

}