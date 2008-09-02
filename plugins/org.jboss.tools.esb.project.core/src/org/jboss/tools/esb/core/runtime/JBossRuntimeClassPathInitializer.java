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
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;

/**
 * @author Grid Qian
 */
public class JBossRuntimeClassPathInitializer extends
		ClasspathContainerInitializer {

	public final static String JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID = "org.jboss.esb.runtime.classpath";
	public JBossRuntimeClassPathInitializer() {
	}

	private String segment;

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {

		if (containerPath.segment(0).equals(
				JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID)) {
			JBossRuntimeClasspathContainer container = new JBossRuntimeClasspathContainer(
					containerPath);
			segment = containerPath.segment(1);
			JavaCore.setClasspathContainer(containerPath,
					new IJavaProject[] { project },
					new IClasspathContainer[] { container }, null);
		}
	}

	public IClasspathEntry[] getEntries(IPath path) {
		return new JBossRuntimeClasspathContainer(path).getClasspathEntries();
	}

	public class JBossRuntimeClasspathContainer implements
			IClasspathContainer {
		private IPath path;
		private IClasspathEntry[] entries = null;

		public JBossRuntimeClasspathContainer(IPath path) {
			this.path = path;
		}

		public String getDescription() {
			return JBossFacetCoreMessages.JBossWS_Runtime;
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
				JBossRuntime jbws = JBossRuntimeManager.getInstance()
						.findRuntimeByName(segment);
				if (jbws != null) {
					
					List<String> jars = JBossRuntimeManager.getInstance().getAllRuntimeJars(jbws);
					for (String jar : jars) {
						entryList.add(getEntry(new Path(jar)));
					}
					entries = entryList.toArray(new IClasspathEntry[entryList.size()]);
					if (entries == null)
						return new IClasspathEntry[0];
				}
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
