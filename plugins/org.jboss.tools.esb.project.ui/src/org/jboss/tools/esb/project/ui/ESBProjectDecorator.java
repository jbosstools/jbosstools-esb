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
package org.jboss.tools.esb.project.ui;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.jboss.tools.esb.core.ESBProjectConstant;

public class ESBProjectDecorator extends LabelProvider implements
		ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if(element instanceof IJavaProject) {
    		element = ((IJavaProject)element).getProject();
    	}
        if (element instanceof IProject) {  
    	
        	IProject project = (IProject) element;
        	ImageDescriptor overlay = null;
			if (hasFacet(project, ESBProjectConstant.ESB_PROJECT_FACET))
				overlay=getESBImageDescriptor();
			
			if(overlay != null){
				decoration.addOverlay(overlay); 
			}
        }
        
	}

	private boolean hasFacet(IProject project, String facet) {
		try {
			return FacetedProjectFramework.hasProjectFacet(project, facet);
		} catch (CoreException e) {
			return false;
		}
	}

	private static ImageDescriptor getESBImageDescriptor() {
		ImageDescriptor imageDescriptor = null;
		IPath path = new Path("icons/obj16/esb_module_ovr.gif");
		URL gifImageURL = FileLocator.find(Platform
				.getBundle(ESBProjectPlugin.PLUGIN_ID), path, null);
		if (gifImageURL != null)
			imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
		return imageDescriptor;
	}

}
