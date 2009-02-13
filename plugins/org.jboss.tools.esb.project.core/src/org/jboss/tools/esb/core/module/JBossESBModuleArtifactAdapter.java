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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;
import org.eclipse.wst.server.core.util.WebResource;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.utils.JBossESBProjectUtil;

public class JBossESBModuleArtifactAdapter extends
		ModuleArtifactAdapterDelegate {

	public JBossESBModuleArtifactAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IModuleArtifact getModuleArtifact(Object obj) {

		IResource resource = null;
		if (obj instanceof IResource)
			resource = (IResource) obj;
		else if (obj instanceof IAdaptable)
			resource = (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
		
		if (resource == null)
			return null;
		
		if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			if (JBossESBProjectUtil.isESBProject(project))
				return new WebResource(getModule(project), new Path("")); //$NON-NLS-1$
			return null;	
		}
		IProject project = ProjectUtilities.getProject(resource);
		if (project != null && !JBossESBProjectUtil.isESBProject(project))
			return null;
		
		IVirtualComponent comp = ComponentCore.createComponent(project);
		// determine path
		IPath rootPath = comp.getRootFolder().getProjectRelativePath();
		IPath resourcePath = resource.getProjectRelativePath();

		// Check to make sure the resource is under the Application directory
		if (resourcePath.matchingFirstSegments(rootPath) != rootPath.segmentCount())
			return null;


		// return Web resource type
		return new WebResource(getModule(project), resourcePath);

	}
	
	protected static IModule getModule(IProject project) {
		if (JBossESBProjectUtil.isESBProject(project))
			return ServerUtil.getModule(project);
		return null;
	}
	


}
