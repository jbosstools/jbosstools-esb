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
public class AliasConverter extends ListConverter {
	static final String ALIAS_ENTITY = "ESBPreAlias";
	static final String ALIASES = "aliases";

	protected String getPropertyName() {
		return ALIASES;
	}

	protected String getItemEntityName() {
		return ALIAS_ENTITY;
	}

}
