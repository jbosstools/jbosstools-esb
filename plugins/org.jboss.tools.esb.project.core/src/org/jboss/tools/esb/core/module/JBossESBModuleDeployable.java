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

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.web.internal.deployables.ComponentDeployable;
import org.jboss.tools.esb.core.ESBProjectConstant;

public class JBossESBModuleDeployable extends ComponentDeployable {

	public JBossESBModuleDeployable(IProject project, IVirtualComponent component) {
		super(project);
	}	

	
	 public String getURI(IModule module) {
	    IVirtualComponent comp = ComponentCore.createComponent(module.getProject());
	    String aURI = null;
	    if (comp !=null) {
	    	if (!comp.isBinary() && isProjectOfType(module.getProject(),ESBProjectConstant.ESB_PROJECT_FACET)) {
        		IVirtualReference ref = component.getReference(comp.getName());
        		aURI = ref.getRuntimePath().append(comp.getName()+".esb").toString(); //$NON-NLS-1$
        	}
	    }
	    	
    	if (aURI !=null && aURI.length()>1 && aURI.startsWith("/")) //$NON-NLS-1$
    		aURI = aURI.substring(1);
    	return aURI;
	 }
	 
	public String getVersion() {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(component.getProject());
			if (facetedProject !=null && ProjectFacetsManager.isProjectFacetDefined(ESBProjectConstant.ESB_PROJECT_FACET)) {
				IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
				return facetedProject.getInstalledVersion(projectFacet).getVersionString();
			}
		} catch (Exception e) {
			//Ignore
		}
		return "4.2"; //$NON-NLS-1$
	}


}
