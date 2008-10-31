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
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.ModuleFolder;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.jboss.tools.esb.core.ESBProjectConstant;
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
		if(esbFolder == null || "".equals(esbFolder)){
			esbFolder = ESBProjectConstant.DEFAULT_ESB_CONFIG_RESOURCE_FOLDER;
		}
		IFolder configFolder = project.getFolder(esbFolder);
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
	
	private List<IModuleResource> getConfigModuleFile(IProject project, IFolder configFolder) throws CoreException {
		List<IModuleResource> mrs = new ArrayList<IModuleResource>();		
		IFolder metainf = configFolder.getFolder(ESBProjectConstant.META_INF);
		IResource res = metainf.findMember(ESBProjectConstant.ESB_CONFIG_JBOSSESB);
		IModuleFile tmpmf;
		if(res == null){
			tmpmf = getModuleResourcesOutofESBContent(new Path(ESBProjectConstant.META_INF), project, ESBProjectConstant.ESB_CONFIG_JBOSSESB);
			if( tmpmf != null){
				mrs.add(tmpmf);
			}
		}
		
		//check the deployment.xml just like jboss-esb.xml
		res = metainf.findMember(ESBProjectConstant.ESB_CONFIG_DEPLOYMENT);
		if(res == null){
			tmpmf = getModuleResourcesOutofESBContent(new Path(ESBProjectConstant.META_INF), project, ESBProjectConstant.ESB_CONFIG_DEPLOYMENT);
			if(tmpmf != null){
				mrs.add(tmpmf);
			}
		}
		
		res = configFolder.findMember(ESBProjectConstant.ESB_CONFIG_QUEUE_SERVICE_JBM);
		if(res == null){
			tmpmf = getModuleResourcesOutofESBContent(Path.EMPTY, project, ESBProjectConstant.ESB_CONFIG_QUEUE_SERVICE_JBM);
			if(tmpmf != null){
				mrs.add(tmpmf);
			}
		}
		res = configFolder.findMember(ESBProjectConstant.ESB_CONFIG_QUEUE_SERVICE_JBMQ);
		if(res == null){
			tmpmf = getModuleResourcesOutofESBContent(Path.EMPTY, project, ESBProjectConstant.ESB_CONFIG_QUEUE_SERVICE_JBMQ);
			if( tmpmf != null ){
				mrs.add(tmpmf);
			}
		}
		
		return mrs;
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
	
	// create moduleresource for a name specified resource 
	protected IModuleFile getModuleResourcesOutofESBContent(IPath path, IContainer container, String fileName) throws CoreException {
		
		IResource file = container.findMember(fileName, false);
		if(file != null){
			return new ModuleFile((IFile)file, file.getName(), path);
		}
		
		IResource[] resources = container.members();
		if (resources != null) {
			int size = resources.length;
			for (int i = 0; i < size; i++) {
				IResource resource = resources[i];
				if (resource != null && resource.exists()) {
					if (resource instanceof IContainer) {
						IModuleFile mf = getModuleResourcesOutofESBContent(path, (IContainer)resource, fileName);
						
						if(mf != null) return mf;
					}  
				}
			}
			
		}
		
		return null;
	}
}
