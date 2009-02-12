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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.ModuleFolder;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.jboss.tools.esb.core.StatusUtils;

public class JBossESBModuleDelegate extends ProjectModule {

	public JBossESBModuleDelegate(IProject project){
		super(project);
	}
	@Override
	public IModule[] getChildModules() {
		return null;
	}

	public IModuleResource[] members() throws CoreException {
		IProject project = getProject();
		final IVirtualComponent c = ComponentCore.createComponent(project);
		IVirtualFolder vf = c.getRootFolder();
		IContainer[] folders = vf.getUnderlyingFolders();
		if(folders == null || folders.length == 0){
			throw new CoreException(StatusUtils.errorStatus("The project is not a valid JBoss ESB project."));
		}
		
		//this is because of the beta1 workspace meta data, so try to get esbcontent folder name by this way,
		//in the JBT CR1, the length of folders will be 1.
		IContainer contentFolder = folders.length > 1? folders[1] : folders[0];
		
		IFolder configFolder = project.getFolder(contentFolder.getProjectRelativePath());
		IJavaProject javaPrj = JavaCore.create(project);
		IPath output = javaPrj.getOutputLocation();		
		// if the jboss-esb.xml file is not in META-INF folder, try to get it from other folder of the project
		// block this logic as Max's suggestion
		//List<IModuleResource> mrs = getConfigModuleFile(project, configFolder);
		
		List<IModuleResource> mrs = new ArrayList<IModuleResource>();
		
		IModuleResource[] esbContent = getModuleResources(Path.EMPTY, configFolder);
		IModuleResource[] classes = getModuleResources(Path.EMPTY, project.getWorkspace().getRoot().getFolder(output));
		IModuleResource[] allResource = new IModuleResource[esbContent.length + classes.length + mrs.size()];
		System.arraycopy(esbContent, 0, allResource, 0, esbContent.length);
		System.arraycopy(classes, 0, allResource, esbContent.length, classes.length);
		if(mrs.size() > 0){
			IModuleResource[] mr = mrs.toArray(new IModuleResource[]{});
			System.arraycopy(mr, 0, allResource, esbContent.length + classes.length, mr.length);
		}
		return allResource;
	}
	 
	
	
	@Override
	public IStatus validate() {
		return null;
	}

	@Override
	protected IModuleResource[] getModuleResources(IPath path, IContainer container) throws CoreException {
		
		IResource[] resources = container.members();
		if (resources != null) {
			int size = resources.length;
			List<IModuleResource> list = new ArrayList<IModuleResource>(size);
			for (int i = 0; i < size; i++) {
				IResource resource = resources[i];
				if (resource != null && resource.exists()) {
					String name = resource.getName();
					if (resource instanceof IContainer) {
						IContainer container2 = (IContainer) resource;
						ModuleFolder mf = new ModuleFolder(container2, name, path);
						mf.setMembers(getModuleResources(path.append(name), container2));
						list.add(mf);
					} else if (resource instanceof IFile) {
						list.add(new ModuleFile((IFile) resource, name, path));
					}
				}
			}
			IModuleResource[] moduleResources = new IModuleResource[list.size()];
			list.toArray(moduleResources);
			return moduleResources;
		}
		return new IModuleResource[0];
	}
	 
}
