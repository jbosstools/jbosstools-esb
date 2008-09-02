package org.jboss.tools.esb.core.module;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import org.jboss.tools.esb.core.ESBProjectUtilities;

public class JBossESBModuleFactory extends ProjectModuleFactoryDelegate {

	public JBossESBModuleFactory() {
	}

	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		if(module instanceof JBossESBModule){
			IProject project = module.getProject();
			 return new JBossESBModuleDelegate(project);
		}
		return null;
	}

	protected IModule[] createModules(IProject project) {
		IFacetedProject facetProject;
		try {
			facetProject = ProjectFacetsManager.create(project);
			IProjectFacet esbFacet = ProjectFacetsManager.getProjectFacet(ESBProjectUtilities.ESB_PROJECT_FACET);
			if(facetProject.hasProjectFacet(esbFacet)){
				JBossESBModule module = new JBossESBModule(project, this, this.getId());
				return new IModule[]{ module };
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	 
}
