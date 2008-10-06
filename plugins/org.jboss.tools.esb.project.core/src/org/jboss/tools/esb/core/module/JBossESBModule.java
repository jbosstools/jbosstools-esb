package org.jboss.tools.esb.core.module;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.ModuleType;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
import org.jboss.tools.esb.core.ESBProjectUtilities;

public class JBossESBModule implements IModule{
 
	private IProject project;
	private ModuleFactoryDelegate factory;
	private String factoryId;
	public JBossESBModule(IProject project, ModuleFactoryDelegate factory, String factoryId){
		this.project = project;
		this.factory = factory;
		this.factoryId = factoryId;
		 
	}
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		return factoryId + ":" + project.getName();
	}

	public IModuleType getModuleType() {
		IFacetedProject facetPrj;
		try {
			facetPrj = ProjectFacetsManager.create(project);
			if (facetPrj == null) {
				return null;
			}
			final IProjectFacet esbfacet = ProjectFacetsManager.getProjectFacet(ESBProjectUtilities.ESB_PROJECT_FACET);
			final IProjectFacetVersion fv = facetPrj.getInstalledVersion(esbfacet);
			return ModuleType.getModuleType(esbfacet.getId(), fv.getVersionString());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String getName() {
		return project.getName();
	}

	public IProject getProject() {
		return project;
	}

	public boolean isExternal() {
		return false;
	}

	public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
		ModuleDelegate delegate = factory.getModuleDelegate(this);
		if(adapter.isInstance(delegate))
			return delegate;
		return null;
	}

}
