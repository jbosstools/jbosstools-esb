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

package org.jboss.tools.esb.core.module;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.StatusUtils;

public class JBossESBModuleFactory extends ProjectModuleFactoryDelegate {

	private static final String ID = "org.jboss.tools.esb.project.core.moduleFactory"; //$NON-NLS-1$
	protected ArrayList moduleDelegates = new ArrayList();

	/*
	 * @see DeployableProjectFactoryDelegate#getFactoryID()
	 */
	public static String getFactoryId() {
		return ID;
	}
	protected IModule[] createModules(ModuleCoreNature nature) {
		
		IProject project = nature.getProject();
		try {
			IVirtualComponent comp = ComponentCore.createComponent(project);
			return createModuleDelegates(comp);
		} catch (Exception e) {
			ESBProjectCorePlugin.getDefault().getLog().log(StatusUtils.errorStatus(e));
		}
		return null;
	}
	/**
	 * Returns true if the project represents a deployable project of this type.
	 * 
	 * @param project
	 *            org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	protected boolean isValidModule(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null)
				return false;
			IProjectFacet webFacet = ProjectFacetsManager.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
			return facetedProject.hasProjectFacet(webFacet);
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModuleDelegate(org.eclipse.wst.server.core.IModule)
	 */
	public ModuleDelegate getModuleDelegate(IModule module) {
		for (Iterator iter = moduleDelegates.iterator(); iter.hasNext();) {
			ModuleDelegate element = (ModuleDelegate) iter.next();
			if (module == element.getModule())
				return element;
		}
		return null;

	}

	protected IModule[] createModules(IProject project) {
		try {
			if (project.exists()) {
			ModuleCoreNature nature = (ModuleCoreNature) project.getNature(IModuleConstants.MODULE_NATURE_ID);
			if (nature != null)
				return createModules(nature);
			}
		} catch (CoreException e) {
			ESBProjectCorePlugin.getDefault().getLog().log(StatusUtils.errorStatus(e));
		}
		return null;
	}

	protected IModule[] createModuleDelegates(IVirtualComponent component) throws CoreException {
		JBossESBModuleDeployable moduleDelegate = null;
		IModule module = null;
		try {
			if(isValidModule(component.getProject())) {
				moduleDelegate = new JBossESBModuleDeployable(component.getProject(),component);
				module = createModule(component.getName(), component.getName(),ESBProjectConstant.ESB_PROJECT_FACET, moduleDelegate.getVersion(), moduleDelegate.getProject());
				moduleDelegate.initialize(module);
			}
		} catch (Exception e) {
			ESBProjectCorePlugin.getDefault().getLog().log(StatusUtils.errorStatus(e));
		} finally {
			if (module != null) {
				if (getModuleDelegate(module) == null)
					moduleDelegates.add(moduleDelegate);
			}
		}
		if (module == null)
			return null;
		return new IModule[] {module};
	}
	
	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return a possibly empty array of paths
	 */
	protected IPath[] getListenerPaths() {
		return new IPath[] {
			new Path(".project"), // nature //$NON-NLS-1$
			new Path(StructureEdit.MODULE_META_FILE_NAME), // component
			new Path(".settings/org.eclipse.wst.common.project.facet.core.xml") // facets //$NON-NLS-1$
		};
	}

}
