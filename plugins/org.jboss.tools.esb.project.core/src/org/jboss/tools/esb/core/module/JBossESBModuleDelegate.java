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
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.ModuleFolder;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

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
		String esbFolder = (String)project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
		IFolder configFolder = project.getFolder(esbFolder);
		IJavaProject javaPrj = JavaCore.create(project);
		IPath output = javaPrj.getOutputLocation();		
		IModuleResource[] esbContent = getModuleResources(Path.EMPTY, configFolder);
		IModuleResource[] classes = getModuleResources(Path.EMPTY, project.getWorkspace().getRoot().getFolder(output));
		IModuleResource[] allResource = new IModuleResource[esbContent.length + classes.length];
		System.arraycopy(esbContent, 0, allResource, 0, esbContent.length);
		System.arraycopy(classes, 0, allResource, esbContent.length, classes.length);
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
