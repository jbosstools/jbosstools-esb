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
package org.jboss.tools.esb.core.module;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.internal.flat.IChildModuleReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.web.internal.deployables.FlatComponentDeployable;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTProjectModuleDelegate;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTProjectModuleFactory;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

public class JBossESBModuleFactory extends JBTFlatProjectModuleFactory {
	public static final String FACTORY_ID = "org.jboss.tools.esb.project.core.moduleFactory";
	public static final String MODULE_TYPE = IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID;

	public String getFactoryId() {
		return FACTORY_ID;
	}
	
	public JBossESBModuleFactory() {
		super();
	}

	protected FlatComponentDeployable createModuleDelegate(IProject project, IVirtualComponent component) {
		return new JBossESBModuleDelegate(project, component, this);
	}
	
	protected boolean canHandleProject(IProject p) {
		return hasProjectFacet(p, MODULE_TYPE);
	}
	
	@Override
	protected String getModuleType(IProject project) {
		return MODULE_TYPE;
	}

	@Override
	protected String getModuleVersion(IProject project) {
		return getFacetVersion(project, MODULE_TYPE);
	}

	@Override
	protected String getModuleType(File binaryFile) {
		// esb allows no child modules
		return null;
	}

	@Override
	protected String getModuleVersion(File binaryFile) {
		// esb allows no child modules
		return null;
	}

	@Override 
	public IModule createChildModule(FlatComponentDeployable parent, IChildModuleReference child) {
		// esb allows no child modules
		return null;
	}

}
