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
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
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
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

/**
 * 
 * @author Viacheslav Kabanovich 
 *
 */
public class ESBUtil {
	static String ESB_CHECKED = "esbChecked"; //$NON-NLS-1$
	static String ESB_ROOT = "ESB-ROOT"; //$NON-NLS-1$
	
	public static XModelObject getESBRoot(XModel model) {
		return model.getByPath(FileSystemsHelper.FILE_SYSTEMS + "/" + ESB_ROOT); //$NON-NLS-1$
	}

	public static void updateModel(XModel model) {
		if("true".equals(model.getProperties().getProperty(ESB_CHECKED))) { //$NON-NLS-1$
			return;
		}
		model.getProperties().setProperty(ESB_CHECKED, "true"); //$NON-NLS-1$

		IProject project = EclipseResourceUtil.getProject(model.getRoot());
		
		IContainer[] roots = getESBRootFolders(project, true);
		
		if(roots.length == 0) return;
		
		IContainer esbContentFolder = roots.length > 1 ? roots[1] : roots[0];
		if(!esbContentFolder.exists()) {
			return;
		}

		String fsLoc = esbContentFolder.getLocation().toString().replace('\\', '/');
		XModelObject fs = FileSystemsHelper.getFileSystems(model);
		XModelObject[] cs = fs.getChildren();
		for (XModelObject c: cs) {
			if(fsLoc.equals(c.getAttributeValue(XModelObjectConstants.ATTR_NAME_LOCATION))) {
				String name = c.getAttributeValue(XModelObjectConstants.ATTR_NAME);
				if(ESB_ROOT.equals(name)) return;
				if(fsLoc.endsWith("/" + name)) { //$NON-NLS-1$
					c.setAttributeValue(XModelObjectConstants.ATTR_NAME, ESB_ROOT);
					return;
				}
			}
		}
		Properties properties = new Properties();
		properties.setProperty(XModelObjectConstants.ATTR_NAME_LOCATION, fsLoc);
		properties.setProperty(XModelObjectConstants.ATTR_NAME, ESB_ROOT);
		FileSystemImpl s = (FileSystemImpl)model.createModelObject(XModelObjectConstants.ENT_FILE_SYSTEM_FOLDER, properties);
		fs.addChild(s);
	}

	public static IContainer[] getESBRootFolders(IProject project, boolean ignoreDerived) {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
		} catch (CoreException e) {
			CommonPlugin.getDefault().logError(e);
		}
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet(IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID);
		if(facet != null && facetedProject!=null && facetedProject.getProjectFacetVersion(facet)!=null) {
			IVirtualComponent component = ComponentCore.createComponent(project);
			if(component!=null) {
				IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

				IContainer[] folders = webRootVirtFolder.getUnderlyingFolders();
				if(folders.length > 1){
					ArrayList<IContainer> containers = new ArrayList<IContainer>();
					for(IContainer container : folders){
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
}
