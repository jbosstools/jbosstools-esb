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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.application.internal.operations.J2EEComponentExportDataModelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.utils.JBossESBProjectUtil;
import org.jboss.tools.esb.project.ui.ESBProjectPlugin;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

public class ESBComponentExportDataModelProvider extends
		J2EEComponentExportDataModelProvider {

	public IDataModelOperation getDefaultOperation() {
		return new ESBComponentExportOperation(model);
	}

	public Set getPropertyNames() {
		return super.getPropertyNames();
	}

	public Object getDefaultProperty(String propertyName) {
		return super.getDefaultProperty(propertyName);
	}

	protected String getProjectType() {
		return ESBProjectConstant.ESB_PROJECT_FACET;
	}

	protected String getWrongComponentTypeString(String projectName) {
		return NLS.bind(JBossESBUIMessages.ESBExportWizard_NotValidProject, projectName);
	}

	protected String getModuleExtension() {
		return ".esb"; //$NON-NLS-1$
	}
	
	public IStatus validate(String propertyName) {
		if (PROJECT_NAME.equals(propertyName)) {
			String projectName = (String) model.getProperty(PROJECT_NAME);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if( project != null ) {
				if(JBossESBProjectUtil.isESBProject(project))
					return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, ESBProjectPlugin.PLUGIN_ID, 
					NLS.bind(JBossESBUIMessages.ESBExportWizard_NotValidProject, projectName));
		}
		return super.validate(propertyName);
	}
	
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (propertyName.equals(PROJECT_NAME)) {
			List componentNames = new ArrayList();
			IVirtualComponent[] wbComps = ComponentUtilities.getAllWorkbenchComponents();

			List relevantComponents = new ArrayList();
			IProject aProj = null;
			for (int i = 0; i < wbComps.length; i++) {
				aProj = wbComps[i].getProject();
				if( JBossESBProjectUtil.isESBProject(aProj) ) {
					relevantComponents.add(wbComps[i]);
					getComponentMap().put(wbComps[i].getName(), wbComps[i]);
				}
			}

			if (relevantComponents == null || relevantComponents.size() == 0)
				return null;

			for (int j = 0; j < relevantComponents.size(); j++) {
				componentNames.add(((IVirtualComponent) relevantComponents.get(j)).getName());
			}
			String[] names = (String[]) componentNames.toArray(new String[componentNames.size()]);

			return DataModelPropertyDescriptor.createDescriptors(names);
		}
		return super.getValidPropertyDescriptors(propertyName);
	}
}
