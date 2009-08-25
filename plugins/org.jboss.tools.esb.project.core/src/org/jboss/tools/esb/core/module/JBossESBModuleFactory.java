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
package org.jboss.tools.esb.core.module;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

public class JBossESBModuleFactory extends ProjectModuleFactoryDelegate {
	public static final String FACTORY_ID = "org.jboss.tools.esb.project.core.moduleFactory";
	public static final String MODULE_TYPE = IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID;
	public static final String MODULE_ID_PREFIX = IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID + ".";
	private static ModuleFactory factory;
	private static JBossESBModuleFactory factDelegate;

	public static JBossESBModuleFactory getFactory() {
		if (factDelegate == null) {
			ModuleFactory[] factories = ServerPlugin.getModuleFactories();
			for (int i = 0; i < factories.length; i++) {
				if (factories[i].getId().equals(FACTORY_ID)) {
					Object o = factories[i]
							.getDelegate(new NullProgressMonitor());
					if (o instanceof JBossESBModuleFactory) {
						factory = factories[i];
						factDelegate = (JBossESBModuleFactory) o;
						return factDelegate;
					}
				}
			}
		}
		return factDelegate;
	}

	
	private HashMap<IModule, JBossESBModuleDelegate> moduleToDelegate;
	public JBossESBModuleFactory() {
		moduleToDelegate = new HashMap<IModule, JBossESBModuleDelegate>();
	}

	@Override
	protected void clearCache(IProject project) {
		super.clearCache(project);
		moduleToDelegate.remove(project);
	}
	
	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		return moduleToDelegate.get(module);
	}

	protected IModule[] createModules(IProject project) {
		IFacetedProject facetProject;
		try {
			facetProject = ProjectFacetsManager.create(project);
			if (facetProject == null) {
				return null;
			}
			IProjectFacet esbFacet = ProjectFacetsManager
					.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);

			if (facetProject.hasProjectFacet(esbFacet)) {
				IProjectFacetVersion version = facetProject.getProjectFacetVersion(esbFacet);
				IModule module = createModule(
						MODULE_ID_PREFIX + project.getName(), 
						project.getName(), 
						MODULE_TYPE, 
						version.getVersionString(), 
						project);
				moduleToDelegate.put(module, new JBossESBModuleDelegate(project));
				return new IModule[] { module };
			}
		} catch (CoreException e) {
			ESBProjectCorePlugin.getDefault().getLog().log(e.getStatus());
		}
		return null;

	}

}
