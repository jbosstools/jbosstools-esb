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

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.project.facet.J2EEModuleFacetInstallDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.esb.core.ESBProjectConstant;

public class JBossESBFacetDataModelProvider extends J2EEModuleFacetInstallDataModelProvider implements IJBossESBFacetDataModelProperties{

	private static final String JBOSSESB_PROJECT_FACET = ESBProjectConstant.ESB_PROJECT_FACET;

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(CONFIG_FOLDER);
		names.add(IJBossESBFacetDataModelProperties.ESB_SOURCE_FOLDER);
		names.add(IJBossESBFacetDataModelProperties.ESB_CONTENT_FOLDER);
		names.add(IJBossESBFacetDataModelProperties.ESB_SOURCE_FOLDER);
		names.add(RUNTIME_ID);
		names.add(RUNTIME_HOME);
		names.add(RUNTIME_IS_SERVER_SUPPLIED);
		names.add(IJBossESBFacetDataModelProperties.ESB_CONFIG_VERSION);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return JBOSSESB_PROJECT_FACET;
		}
		else if(IJBossESBFacetDataModelProperties.ESB_CONTENT_FOLDER.equals(propertyName)){
			return ESBProjectConstant.DEFAULT_ESB_CONFIG_RESOURCE_FOLDER;
		}
		else if(IJBossESBFacetDataModelProperties.ESB_SOURCE_FOLDER.equals(propertyName)){
			return ESBProjectConstant.DEFAULT_ESB_SOURCE_FOLDER;
		}
		 else if(propertyName.equals(RUNTIME_ID)){
			return "";
		}else if(propertyName.equals(FACET_ID)){
			return IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID;
		} else if( propertyName.equals(RUNTIME_IS_SERVER_SUPPLIED)){
			return true;
		}
		return super.getDefaultProperty(propertyName);
	}

	protected int convertFacetVersionToJ2EEVersion(IProjectFacetVersion version) {
		return J2EEVersionConstants.J2EE_1_4_ID;
	}

	public boolean isPropertyEnabled(String propertyName) {
		return super.isPropertyEnabled(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		boolean status = false;
		status = super.propertySet(propertyName, propertyValue);
		return status;
	}

	public IStatus validate(String propertyName) {
		return OK_STATUS;
	}
}
