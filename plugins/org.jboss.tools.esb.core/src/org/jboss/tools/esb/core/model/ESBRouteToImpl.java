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
package org.jboss.tools.esb.core.model;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBRouteToImpl extends ESBCustomizedObjectImpl {
	private static final long serialVersionUID = 1L;
	
	public static final String ATTR_DESTINATION_NAME = "destination name";
	public static final String ATTR_SERVICE_NAME = "service name";
	public static final String ATTR_SERVICE_CATEGORY = "service category";

	public ESBRouteToImpl() {}

	public String name() {
		String dn = getAttributeValue(ATTR_DESTINATION_NAME);
		if(dn != null && dn.length() > 0) {
			return dn;
		}
		String category = getAttributeValue(ATTR_SERVICE_CATEGORY);
		String name = getAttributeValue(ATTR_SERVICE_NAME);
		return "" + category + "-" + name;
	}

}
