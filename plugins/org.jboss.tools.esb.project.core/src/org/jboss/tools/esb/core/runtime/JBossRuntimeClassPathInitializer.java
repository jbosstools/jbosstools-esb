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

package org.jboss.tools.esb.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.classpath.core.jee.AbstractClasspathContainer;
import org.jboss.ide.eclipse.as.classpath.core.jee.AbstractClasspathContainerInitializer;
import org.jboss.ide.eclipse.as.classpath.core.xpl.ClasspathDecorations;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;

/**
 * @author Denny Xu
 */
public class JBossRuntimeClassPathInitializer extends
		AbstractClasspathContainerInitializer {

	public final static String JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID = "org.jboss.esb.runtime.classpath";
	public final static String JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED = "server.supplied";

	public JBossRuntimeClassPathInitializer() {
	}

	private String segment;
	private boolean isServerSupplied = false;
	
	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		this.javaProject = project;
		if (containerPath.segment(0).equals(
				JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID)) {
			if (containerPath.segmentCount() == 3
					&& containerPath.segment(1).equals(
							JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED)) {
				segment = containerPath.segment(2);
				isServerSupplied = true;
			} else {
				segment = containerPath.segment(1);
				isServerSupplied = false;
			}

			JBossRuntimeClasspathContainer container = new JBossRuntimeClasspathContainer(
					containerPath, project, isServerSupplied);
			JavaCore.setClasspathContainer(containerPath,
					new IJavaProject[] { project },
					new IClasspathContainer[] { container }, null);
		}
	}

	public IClasspathEntry[] getEntries(IPath path) {
		return new JBossRuntimeClasspathContainer(path, javaProject,
				isServerSupplied).getClasspathEntries();
	}

//	private String getVersionNumber(IJavaProject project){
//		try {
//			IFacetedProject fp = ProjectFacetsManager.create(project.getProject());
//			if(fp == null){
//				return "";
//			}
//			IProjectFacet facet = ProjectFacetsManager.getProjectFacet(IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID);
//			IProjectFacetVersion pfVersion = fp.getProjectFacetVersion(facet);
//			
//			return pfVersion.getVersionString();
//			
//		} catch (CoreException e) {
//			ESBProjectCorePlugin.log(e.getLocalizedMessage(), e);
//		}
//		return "";
//	}
	
	public class JBossRuntimeClasspathContainer extends
			AbstractClasspathContainer {
		private IPath path;
		private boolean isFromServer = false;
		private IClasspathEntry[] entries = null;
		private List<String> jars;
		private boolean unbound = false;

		public JBossRuntimeClasspathContainer(IPath path, IJavaProject project,
				boolean isFromServer) {
			super(path, JBossFacetCoreMessages.JBoss_Runtime, null, project);
			this.path = path;
			this.isFromServer = isFromServer;
		}

		public String getDescription() {
			if(unbound){
				return JBossFacetCoreMessages.JBoss_Runtime + " [" + path.segment(path.segmentCount() - 1) + "] (unbound)";
			}
			return JBossFacetCoreMessages.JBoss_Runtime + " [" + path.segment(path.segmentCount() - 1) + "]";
		}

		public int getKind() {
			return IClasspathContainer.K_APPLICATION;
		}

		public IPath getPath() {
			return path;
		}
		
		public IClasspathEntry[] getClasspathEntries() {
			return computeEntries();
		}

		public IClasspathEntry[] computeEntries() {
			ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
			if (isFromServer) {
				IRuntime serverRuntime = ServerCore.findRuntime(segment);

				if (serverRuntime == null) {
//					IStatus status = StatusUtils
//							.errorStatus("Can not find the runtime: " + segment);
//					ESBProjectCorePlugin.getDefault().getLog().log(status);
					unbound = true;
					return new IClasspathEntry[0];
				}
				unbound = false;
				String runtimeLocation = serverRuntime.getLocation()
						.toOSString();
				IJBossServerRuntime jbossRuntime = (IJBossServerRuntime)serverRuntime.loadAdapter(IJBossServerRuntime.class, new NullProgressMonitor());

				jars = JBossRuntimeManager.getInstance().getAllRuntimeJars(
						runtimeLocation, serverRuntime.getRuntimeType().getId(), jbossRuntime.getJBossConfiguration());

			} else {

				JBossRuntime jbws = JBossRuntimeManager.getInstance()
						.findRuntimeByName(segment);
				if (jbws != null) {
					jars = JBossRuntimeManager.getInstance().getAllRuntimeJars(
							jbws, jbws.getVersion());
					unbound = false;
				}
				else{
					unbound = true;
				}
			}

			if (jars == null)
				return new IClasspathEntry[0];

			for (String jar : jars) {

				IPath entryPath = new Path(jar);

				IPath sourceAttachementPath = null;
				IPath sourceAttachementRootPath = null;

				final ClasspathDecorations dec = decorations.getDecorations(
						getDecorationManagerKey(getPath().toString()),
						entryPath.toString());

				IClasspathAttribute[] attrs = {};
				if (dec != null) {
					sourceAttachementPath = dec.getSourceAttachmentPath();
					sourceAttachementRootPath = dec
							.getSourceAttachmentRootPath();
					attrs = dec.getExtraAttributes();
				}

				IAccessRule[] access = {};
				IClasspathEntry entry = JavaCore.newLibraryEntry(entryPath,
						sourceAttachementPath, sourceAttachementRootPath,
						access, attrs, false);
				entryList.add(entry);
			}
			entries = entryList.toArray(new IClasspathEntry[entryList.size()]);
			return entries;
		}

		@Override
		public void refresh() {
			new JBossRuntimeClasspathContainer(path,javaProject,isServerSupplied).install();
		}

	}


	@Override
	protected AbstractClasspathContainer createClasspathContainer(IPath path) {
		return new JBossRuntimeClasspathContainer(path, javaProject,
				isServerSupplied);
	}

	@Override
	protected String getClasspathContainerID() {
		return JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID;
	}

}
