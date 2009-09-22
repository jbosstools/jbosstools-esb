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

import org.eclipse.core.resources.IProject;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTProjectModuleDelegate;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTProjectModuleFactory;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;

public class JBossESBModuleFactory extends JBTProjectModuleFactory {
	public static final String FACTORY_ID = "org.jboss.tools.esb.project.core.moduleFactory";
	public static final String MODULE_TYPE = IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID;
	private static JBossESBModuleFactory factDelegate;

	public static JBossESBModuleFactory getFactory() {
		if (factDelegate == null)
			factDelegate = (JBossESBModuleFactory)getFactory(FACTORY_ID);
		return factDelegate;
	}

	public JBossESBModuleFactory() {
		super(MODULE_TYPE, ESBProjectConstant.ESB_PROJECT_FACET);
	}

	protected JBTProjectModuleDelegate createDelegate(IProject project) {
		return new JBossESBModuleDelegate(project);
	}

}
