/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.core.model.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport.DefaultVersionResolver;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ESBDefaultVersionResolver implements DefaultVersionResolver {

	public String resolve(String[] versionList, XModelObject context) {
		IProject project = EclipseResourceUtil.getProject(context);
		if(project == null) return null;
		String qRuntimeName = null;
		
		try {
			qRuntimeName = project.getPersistentProperty(IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME);
		} catch (CoreException e) {
			//ignore
		}
		if(qRuntimeName == null) return null;
		
		JBossESBRuntime runtime = JBossRuntimeManager.getInstance().findRuntimeByName(qRuntimeName);
		if(runtime == null) return null;
		
		String v = runtime.getVersion();
		if(v == null) return null;
		String[] ts = v.split("\\.");
		int major = 0;
		int minor = 0;
		if(ts.length >= 1) {
			try {
				major = Integer.parseInt(ts[0]);
			} catch (NumberFormatException e) {
				//ignore
			}
		}
		if(ts.length >= 2) {
			try {
				minor = Integer.parseInt(ts[0]);
			} catch (NumberFormatException e) {
				//ignore
			}
		}

		if(major < 4) {
			return "1.0.1";
		}
		if(major > 4) {
			return null;
		}
		if(minor >= 7) {
			return "1.2.0";
		} else {
			return "1.1.0";
		}
		
	}

}
