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
package org.jboss.tools.esb.core.facet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.esb.core.StatusUtils;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;
import org.jboss.tools.esb.core.runtime.JBossRuntimeClassPathInitializer;

public class UninstallJBossClassPathCommand {
	IProject project;

	public UninstallJBossClassPathCommand(IProject project, IDataModel model) {
		this.project = project;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return executeOverride(monitor);
	}

	public IStatus executeOverride(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			boolean isServerSupplied = Boolean
					.getBoolean(project
							.getPersistentProperty(IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_SERVER_SUPPLIED_RUNTIME));
			if (isServerSupplied) {
				// TODO test
			} else {
				String runtimeName = project
						.getPersistentProperty(IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME);
				status = removeClassPath(project, runtimeName);
			}
		} catch (CoreException e) {
			status = StatusUtils.errorStatus(
					JBossFacetCoreMessages.Error_Remove_Facet_JBossESB, e);
		}
		return status;
	}

	public IStatus removeClassPath(IProject project, String segment) {
		IStatus status = Status.OK_STATUS;
		try {
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] oldClasspathEntries = javaProject
					.readRawClasspath();

			boolean isFolderInClassPathAlready = false;
			List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
			for (int i = 0; i < oldClasspathEntries.length
					&& !isFolderInClassPathAlready; i++) {
				if (!oldClasspathEntries[i].getPath().equals(
						new Path(JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID)
								.append(segment))) {
					classpathEntries.add(oldClasspathEntries[i]);
				}
			}
			if (classpathEntries.size() < oldClasspathEntries.length) {
				javaProject.setRawClasspath(classpathEntries
						.toArray(new IClasspathEntry[classpathEntries.size()]),
						new NullProgressMonitor());
			}
		} catch (JavaModelException e) {
			status = StatusUtils.errorStatus(NLS.bind(
					JBossFacetCoreMessages.Error_Remove_Facet_JBossESB,
					new String[] { e.getLocalizedMessage() }), e);
			return status;
		}

		return status;
	}

}
