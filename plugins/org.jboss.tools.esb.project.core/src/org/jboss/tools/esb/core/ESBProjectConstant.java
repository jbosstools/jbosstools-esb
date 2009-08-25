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
package org.jboss.tools.esb.core;

public class ESBProjectConstant {

	public final static String ESB_PROJECT_FACET = "jst.jboss.esb";
	public final static String ESB_PROJECT_FACET_TEMPLATE = "template.jst.jboss.esb";
	public final static String BUILD_CLASSES = "build/classes";
	public final static String META_INF = "META-INF";
	public final static String ESB_CLASS = ""; //put ESB classes into the root of ESB archive
	public final static String ESB_INF_LIB = "lib";
	
	public final static String ESB_CONFIG_JBOSSESB = "jboss-esb.xml";
	public final static String ESB_CONFIG_DEPLOYMENT = "deployment.xml";
	public final static String ESB_CONFIG_QUEUE_SERVICE_JBM = "jbm-queue-service.xml";
	public final static String ESB_CONFIG_QUEUE_SERVICE_JBMQ = "jbmq-queue-service.xml";
	
	public final static String DEFAULT_ESB_CONFIG_RESOURCE_FOLDER = "esbcontent";
	public final static String DEFAULT_ESB_SOURCE_FOLDER = "src";
	public final static String CONTEXTROOT  = "context-root";
	
	public final static String ESB_PROJECT_NATURE = "org.jboss.tools.esb.project.ESBNature";
}
