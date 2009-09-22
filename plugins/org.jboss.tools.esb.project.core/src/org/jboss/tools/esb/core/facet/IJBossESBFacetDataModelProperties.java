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

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;

public interface IJBossESBFacetDataModelProperties extends IFacetDataModelProperties {

	public static final String USER_DEFINED_LOCATION = "IProjectCreationPropertiesNew.USER_DEFINED_LOCATION"; //$NON-NLS-1$
	public static final String DEFAULT_LOCATION = "IProjectCreationPropertiesNew.DEFAULT_LOCATION"; //$NON-NLS-1$
	public static final String USE_DEFAULT_LOCATION = "IProjectCreationPropertiesNew.USE_DEFAULT_LOCATION"; //$NON-NLS-1$
	public static final String PROJECT_LOCATION = "IProjectCreationPropertiesNew.PROJECT_LOCATION";
	
	public static final String ESB_CONTENT_FOLDER = "JBoss.Project.Content_Folder";
	public static final String ESB_SOURCE_FOLDER = "JBoss.Project.Src_Folder";
	
	public static final String ESB_CONFIG_VERSION = "JBoss.Project.Config_Version";
	
	public static final QualifiedName QNAME_ESB_CONTENT_FOLDER = new QualifiedName("jboss", ESB_CONTENT_FOLDER);
	public static final QualifiedName QNAME_ESB_SRC_FOLDER = new QualifiedName("jboss", ESB_SOURCE_FOLDER);
	public static final String ESB_PROJECT_VERSION = "jboss.esb.project.project.version";

	
	
	public static final String JBOSS_ESB_FACET_ID = "jst.jboss.esb";
	public static final String RUNTIME_DEPLOY = "jboss.deploy";	
	public static final String QUALIFIEDNAME_IDENTIFIER = "jboss.tools";	
	public static final String RUNTIME_IS_SERVER_SUPPLIED = "jboss.is.server.supplied";
	public static final String RUNTIME_ID = "jboss.runtime_id";
	
	public static final String PERSISTENT_PROPERTY_IS_SERVER_SUPPLIED_RUNTIME = "is.server.supplied.runtime";
	public static final String RUNTIME_HOME = "jboss.runtime.home";
	public static final String DEFAULT_VALUE_IS_SERVER_SUPPLIED = "1";
	static QualifiedName PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME = new QualifiedName(QUALIFIEDNAME_IDENTIFIER,
			RUNTIME_ID);
	static QualifiedName PERSISTENCE_PROPERTY_RNTIME_LOCATION = new QualifiedName(QUALIFIEDNAME_IDENTIFIER,
			RUNTIME_HOME);
	static QualifiedName PERSISTENCE_PROPERTY_SERVER_SUPPLIED_RUNTIME = new QualifiedName(
			QUALIFIEDNAME_IDENTIFIER,
			PERSISTENT_PROPERTY_IS_SERVER_SUPPLIED_RUNTIME);
}
