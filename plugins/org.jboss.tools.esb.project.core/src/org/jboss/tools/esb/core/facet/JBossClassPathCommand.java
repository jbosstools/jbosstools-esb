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
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.jboss.tools.esb.core.StatusUtils;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;
import org.jboss.tools.esb.core.runtime.JBossRuntimeClassPathInitializer;

/**
 * @author Denny Xu
 */
public class JBossClassPathCommand extends AbstractDataModelOperation {

	IProject project;
	private IDataModel model;
	private String serverRuntimeId;

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
				boolean isServerSupplied = model.getBooleanProperty(IJBossESBFacetDataModelProperties.RUNTIME_IS_SERVER_SUPPLIED);
				if(isServerSupplied){
					serverRuntimeId = getProjectTargetRuntimeID(project);
					String[] segments = new String[]{JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED,
														serverRuntimeId};
					status = addClassPath(project, segments);
				}else{
					String runtimeName = model
							.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_ID);
					String runtimeLocation = model
							.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_HOME);
					String esbcontentFolder = model.getStringProperty(IJBossESBFacetDataModelProperties.ESB_CONTENT_FOLDER);
					project
							.setPersistentProperty(
									IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME,
									runtimeName);
					project.setPersistentProperty(
									IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_RNTIME_LOCATION,
									runtimeLocation);
					project.setPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER, esbcontentFolder);
					status = addClassPath(project, new String[]{runtimeName});
				}
		} catch (CoreException e) {
			status = StatusUtils.errorStatus(
					JBossFacetCoreMessages.Error_Add_Facet_JBossESB, e);
		}
		return status;
	}

	private String getProjectTargetRuntimeID(IProject project) throws CoreException{
		IFacetedProject fp = ProjectFacetsManager.create(project);
		IRuntime runtime = fp.getPrimaryRuntime();
		if(runtime == null){
			return "";
		}
		return runtime.getProperty("id");
		
	}
	
	public IStatus addClassPath(IProject project, String[] segments) {
		IStatus status = Status.OK_STATUS;
		try {

			IClasspathEntry newClasspath;
			IJavaProject javaProject = JavaCore.create(project);
			IPath path = new Path(JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID);
			for(String segment: segments ){
				path = path.append(segment);
			}
			newClasspath = JavaCore.newContainerEntry(path);

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