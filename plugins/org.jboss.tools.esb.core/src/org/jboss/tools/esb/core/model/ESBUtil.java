/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.CommonPlugin;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

/**
 * 
 * @author Viacheslav Kabanovich 
 *
 */
public class ESBUtil {

	@Deprecated
	public static XModelObject getESBRoot(XModel model) {
		return model.getByPath(FileSystemsHelper.FILE_SYSTEMS + "/ESB-ROOT"); //$NON-NLS-1$
	}

	public static void updateModel(XModel model) {
		XModelObject fs = FileSystemsHelper.getFileSystems(model);

		IProject project = EclipseResourceUtil.getProject(model.getRoot());
		
		IContainer[] roots = getESBRootFolders(project, true);		
		
		if(roots.length == 0) return;
		
		List<XModelObject> existingRoots = getExistingWebRoots(fs);
		boolean rootsChanged = rootsChanged(roots, existingRoots);
		if(rootsChanged) {
			for (XModelObject c: existingRoots) {
				c.removeFromParent();
			}
			int i = 0;
			for (IContainer root: roots) {
				String webRootLocation = root.getLocation().toString().replace('\\', '/');
				String name = WEB_ROOT;
				if(i > 0) name += "-" + i;
				XModelObject webroot = createFileSystemFolder(model, name, webRootLocation);
				fs.addChild(webroot);
				i++;		
			}
		}

	}

	static XModelObject createFileSystemFolder(XModel model, String name, String location) {
		XModelObject f = model.createModelObject(XModelObjectConstants.ENT_FILE_SYSTEM_FOLDER, null);
		f.setAttributeValue(XModelObjectConstants.ATTR_NAME, name);
		f.setAttributeValue(XModelObjectConstants.ATTR_NAME_LOCATION, location);
		return f;
	}

	public static IContainer[] getESBRootFolders(IProject project, boolean ignoreDerived) {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
		} catch (CoreException e) {
			CommonPlugin.getDefault().logError(e);
		}
		Set<IFolder> srcs = EclipseResourceUtil.getSourceFolders(project);
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet(IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID);
		if(facet != null && facetedProject!=null && facetedProject.getProjectFacetVersion(facet)!=null) {
			IVirtualComponent component = ComponentCore.createComponent(project);
			if(component!=null) {
				IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

				IContainer[] folders = webRootVirtFolder.getUnderlyingFolders();
				if(folders.length > 1){
					ArrayList<IContainer> containers = new ArrayList<IContainer>();
					for(IContainer container : folders){
						if(srcs.contains(container)) {
							continue; //all sources are added as 'src-' file systems. 
						}
						if(!ignoreDerived || !container.isDerived(IResource.CHECK_ANCESTORS)) {
							containers.add(container);
						}
					}
					return containers.toArray(new IContainer[containers.size()]);
				} else {
					return folders;
				}
			}
		}
		return EMPTY_ARRAY;
	}

	private static final IContainer[] EMPTY_ARRAY = new IContainer[0];

	static String WEB_ROOT = "WEB-ROOT"; //$NON-NLS-1$

	static List<XModelObject> getExistingWebRoots(XModelObject fs) {
		List<XModelObject> result = new ArrayList<XModelObject>();
		XModelObject[] cs = fs.getChildren(XModelObjectConstants.ENT_FILE_SYSTEM_FOLDER);
		for (XModelObject c: cs) {
			if(c.getAttributeValue(XModelObjectConstants.ATTR_NAME).startsWith(WEB_ROOT)) {
				result.add(c);
			}
		}
		return result;
	}

	static boolean rootsChanged(IContainer[] webRoots, List<XModelObject> rs) {
		if(webRoots.length != rs.size()) return true;
		for (int i = 0; i < webRoots.length; i++) {
			XModelObject o = rs.get(i);
			IResource r = (IResource)o.getAdapter(IResource.class);
			if(r == null || !r.equals(webRoots[i])) {
				return true;
			}
		}
		return false;
	}

}
