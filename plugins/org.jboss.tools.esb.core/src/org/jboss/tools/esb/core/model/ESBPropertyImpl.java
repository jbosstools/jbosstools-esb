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

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBPropertyImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public ESBPropertyImpl() {}
	
	public String getAttributeValue(String name) {
		if(ESBConstants.ATTR_PROPERTY_VALUE_PRESENTATION.equals(name)) {
			String v = getAttributeValue(ESBConstants.ATTR_VALUE);
			if(v != null && v.length() > 0) return v;
			int xmlChildren = getChildren("AnyElement").length;
			if(xmlChildren > 0) return "XML";
		}
		return super.getAttributeValue(name);
	}
}
