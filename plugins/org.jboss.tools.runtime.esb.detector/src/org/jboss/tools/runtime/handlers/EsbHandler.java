/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.runtime.handlers;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.ide.eclipse.as.core.server.bean.JBossServerType;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.runtime.core.model.AbstractRuntimeDetectorDelegate;
import org.jboss.tools.runtime.core.model.RuntimeDefinition;

public class EsbHandler extends AbstractRuntimeDetectorDelegate {

	private static final String DEFAULT_CONFIGURATION = "default";
	private static final String ESB = "ESB"; //$NON-NLS-1$
	private static final String ESB_PREFIX = ESB + " - ";
	private static final String JBOSS_ESB_FOLDER = "jboss-esb";
	
	public void initializeRuntimes(List<RuntimeDefinition> runtimeDefinitions) {
		for (RuntimeDefinition runtimeDefinition : runtimeDefinitions) {
			String type = runtimeDefinition.getType();
			if (runtimeDefinition.isEnabled() && !esbExists(runtimeDefinition)) {
				if (ESB.equals(type)) {
					JBossESBRuntime runtime = new JBossESBRuntime();
					if( !runtimeDefinition.getName().startsWith(ESB_PREFIX))
						runtime.setName(ESB_PREFIX + runtimeDefinition.getName());
					else
						runtime.setName(runtimeDefinition.getName());
					runtime.setHomeDir(runtimeDefinition.getLocation().getAbsolutePath());
					runtime.setConfiguration(DEFAULT_CONFIGURATION);
					runtime.setVersion(runtimeDefinition.getVersion());
					JBossRuntimeManager.getInstance().addRuntime(runtime);
				}
			}
			initializeRuntimes(runtimeDefinition.getIncludedRuntimeDefinitions());
		}
	}

	/**
	 * @param serverDefinition
	 * @return
	 */
	private static boolean esbExists(RuntimeDefinition runtimeDefinition) {
		JBossESBRuntime[] runtimes = JBossRuntimeManager.getInstance().getRuntimes();
		for (JBossESBRuntime runtime:runtimes) {
			String location = runtime.getHomeDir();
			if (location != null && location.equals(runtimeDefinition.getLocation().getAbsolutePath())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean exists(RuntimeDefinition runtimeDefinition) {
		if (runtimeDefinition == null || runtimeDefinition.getLocation() == null) {
			return false;
		}
		return esbExists(runtimeDefinition);
	}
	
	@Override
	public String getVersion(RuntimeDefinition runtimeDefinition) {
		if (runtimeDefinition == null || runtimeDefinition.getLocation() == null) {
			return null;
		}
		return getVersion(runtimeDefinition.getLocation().getAbsolutePath(),DEFAULT_CONFIGURATION );
	}
	
    private String getVersion(String location, String configuration){
		return JBossRuntimeManager.getInstance().getVersion(location, configuration);
    }
	@Override
	public RuntimeDefinition getRuntimeDefinition(File root,
			IProgressMonitor monitor) {
		if (monitor.isCanceled() || root == null) {
			return null;
		}
		ServerBeanLoader loader = new ServerBeanLoader(root);
		ServerBean serverBean = loader.getServerBean();
		if( serverBean.getType().getId() != null) {
			File esbRoot = null;
			String type = serverBean.getType().getId();
			if (JBossServerType.SOAP.getId().equals(type)) {
				esbRoot = root;
			} if (JBossServerType.SOAP_STD.getId().equals(type)) {
				esbRoot = new File(root, JBOSS_ESB_FOLDER); 
			}
			return createRuntimeDefinition(esbRoot, root.getName(), monitor);
		}
		return null;
	}
	
	private RuntimeDefinition createRuntimeDefinition(File esbRoot, String parentName, IProgressMonitor monitor) {
		if( esbRoot != null ) {
			if (esbRoot.isDirectory()) {
				String name = ESB_PREFIX + parentName;
				String version=getVersion(esbRoot.getAbsolutePath(), DEFAULT_CONFIGURATION);
				RuntimeDefinition esbDefinition = new RuntimeDefinition(
						name, version, ESB, esbRoot);
				return esbDefinition;
			}
		}
		return null;
	}
	
	@Override
	public void computeIncludedRuntimeDefinition(RuntimeDefinition runtimeDefinition) {
		File esbRoot = null;
		if (JBossServerType.SOAP.getId().equals(runtimeDefinition.getType())) {
			esbRoot = runtimeDefinition.getLocation();
		} if (JBossServerType.SOAP_STD.getId().equals(runtimeDefinition.getType())) {
			esbRoot = new File(runtimeDefinition.getLocation(), JBOSS_ESB_FOLDER);
		}
		RuntimeDefinition child = createRuntimeDefinition(esbRoot, runtimeDefinition.getName(), new NullProgressMonitor());
		if( child != null ) {
			child.setParent(runtimeDefinition);
			runtimeDefinition.getIncludedRuntimeDefinitions().add(child);
		}
	}
}
