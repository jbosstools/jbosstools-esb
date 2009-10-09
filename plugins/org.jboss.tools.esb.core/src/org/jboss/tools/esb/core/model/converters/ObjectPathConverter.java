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
package org.jboss.tools.esb.core.model.converters;

/**
 * @author Viacheslav Kabanovich
 */
public class ObjectPathConverter extends ListConverter {
	static final String OBJECT_PATH_ENTITY = "ESBPreObjectPath";
	static final String OBJECT_PATHS = "object-paths";

	protected String getPropertyName() {
		return OBJECT_PATHS;
	}

	protected String getItemEntityName() {
		return OBJECT_PATH_ENTITY;
	}

}
