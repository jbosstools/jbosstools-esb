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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.StatusUtils;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;

/**
 * @author Denny Xu
 */
public class JBossRuntimeClassPathInitializer extends
		ClasspathContainerInitializer {

	public final static String JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID = "org.jboss.esb.runtime.classpath";
	public final static String JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED = "server.supplied";
	public JBossRuntimeClassPathInitializer() {
	}

	private String segment;
	private boolean isServerSupplied = false;
	private IJavaProject javaProject;

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		this.javaProject = project;
		if (containerPath.segment(0).equals(
				JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID)) {
			if(containerPath.segmentCount() == 3 
					&& containerPath.segment(1).equals(JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED)){
				segment = containerPath.segment(2);
				isServerSupplied = true;
			}else{
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
		return new JBossRuntimeClasspathContainer(path, javaProject, isServerSupplied).getClasspathEntries();
	}

	public class JBossRuntimeClasspathContainer implements
			IClasspathContainer {
		private IPath path;
		private boolean isFromServer = false;
		private IClasspathEntry[] entries = null;
		private IJavaProject jproject;
		private List<String> jars;

		public JBossRuntimeClasspathContainer(IPath path, IJavaProject project, boolean isFromServer) {
			this.path = path;
			this.isFromServer = isFromServer;
			this.jproject = project;
		}

		public String getDescription() {
			return JBossFacetCoreMessages.JBoss_Runtime;
		}

		public int getKind() {
			return IClasspathContainer.K_APPLICATION;
		}

		public IPath getPath() {
			return path;
		}

		public IClasspathEntry[] getClasspathEntries() {
			if (entries == null) {
				ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
				if (isFromServer) {
						IRuntime serverRuntime = ServerCore.findRuntime(segment);

						if(serverRuntime == null){
							IStatus status = StatusUtils.errorStatus("Can not find the runtime: "+ segment);
							ESBProjectCorePlugin.getDefault().getLog().log(status);
						}
						String runtimeLocation = serverRuntime.getLocation().toOSString();
						jars = JBossRuntimeManager.getInstance().getAllRuntimeJars(runtimeLocation);

				} else {

					JBossRuntime jbws = JBossRuntimeManager.getInstance()
							.findRuntimeByName(segment);
					if (jbws != null) {
						jars = JBossRuntimeManager.getInstance()
								.getAllRuntimeJars(jbws);
					}
				}
				
				if(jars == null) return new IClasspathEntry[0];
				
				for (String jar : jars) {
					entryList.add(getEntry(new Path(jar)));
				}
				entries = entryList.toArray(new IClasspathEntry[entryList
						.size()]);
				if (entries == null)
					return new IClasspathEntry[0];
			}
			return entries;
		}

		protected IClasspathEntry getEntry(IPath path) {
			return JavaRuntime.newArchiveRuntimeClasspathEntry(path)
					.getClasspathEntry();
		}

		public void removeEntry(String jarName) {
			if (entries == null) {
				return;
			}
			IClasspathEntry[] newEntries = new IClasspathEntry[entries.length - 1];
			int i = 0;
			for (IClasspathEntry entry : entries) {
				if (!entry.toString().contains(jarName)) {
					newEntries[i++] = entry;
				}
			}
			entries = newEntries;
		}

	}

	public boolean filterJars(String jarName, ArrayList<IClasspathEntry> list) {
		for (IClasspathEntry entry : list) {
			if (entry.getPath().lastSegment().equals(jarName)) {
				return false;
			}
		}
		return true;
	}

}
