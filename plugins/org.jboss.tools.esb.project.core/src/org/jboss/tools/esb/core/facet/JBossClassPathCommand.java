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

package org.jboss.tools.esb.core.facet;

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
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.esb.core.StatusUtils;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;
import org.jboss.tools.esb.core.runtime.JBossRuntimeClassPathInitializer;

/**
 * @author Grid Qian
 */
public class JBossClassPathCommand extends AbstractDataModelOperation {

	IProject project;
	private IDataModel model;

	public JBossClassPathCommand(IProject project, IDataModel model) {
		this.project = project;
		this.model = model;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return executeOverride(monitor);
	}

	public IStatus executeOverride(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {

				// store runtime name and runtime location to the project

				String runtimeName = model
						.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_ID);
				String runtimeLocation = model
						.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_HOME);
				String esbcontentFolder = model.getStringProperty(IJBossESBFacetDataModelProperties.ESB_CONTENT_FOLDER);
				project
						.setPersistentProperty(
								IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME,
								runtimeName);
				project
						.setPersistentProperty(
								IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_RNTIME_LOCATION,
								runtimeLocation);
				project.setPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER, esbcontentFolder);
				status = addClassPath(project, runtimeName);
			

		} catch (CoreException e) {
			status = StatusUtils.errorStatus(
					JBossFacetCoreMessages.Error_Add_Facet_JBossWS, e);
		}
		return status;
	}

	public IStatus addClassPath(IProject project, String segment) {
		IStatus status = Status.OK_STATUS;
		try {

			IClasspathEntry newClasspath;
			IJavaProject javaProject = JavaCore.create(project);

			newClasspath = JavaCore
					.newContainerEntry(new Path(
							JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID)
							.append(segment));

			IClasspathEntry[] oldClasspathEntries = javaProject
					.readRawClasspath();

			boolean isFolderInClassPathAlready = false;
			for (int i = 0; i < oldClasspathEntries.length
					&& !isFolderInClassPathAlready; i++) {
				if (oldClasspathEntries[i].getPath().equals(
						newClasspath.getPath())) {
					isFolderInClassPathAlready = true;
					break;
				}
			}

			if (!isFolderInClassPathAlready) {

				IClasspathEntry[] newClasspathEntries = new IClasspathEntry[oldClasspathEntries.length + 1];
				for (int i = 0; i < oldClasspathEntries.length; i++) {
					newClasspathEntries[i] = oldClasspathEntries[i];
				}
				newClasspathEntries[oldClasspathEntries.length] = newClasspath;
				javaProject.setRawClasspath(newClasspathEntries,
						new NullProgressMonitor());
			}
		} catch (JavaModelException e) {
			status = StatusUtils.errorStatus(NLS.bind(
					JBossFacetCoreMessages.Error_Copy, new String[] { e
							.getLocalizedMessage() }), e);
			return status;
		}

		return status;
	}

}