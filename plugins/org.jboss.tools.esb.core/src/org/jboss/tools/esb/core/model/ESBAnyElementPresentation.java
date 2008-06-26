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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectPresentation;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBAnyElementPresentation implements XModelObjectPresentation {
	static String TAG_ROUTE_TO = "route-to";
	static String TAG_OBJECT_PATH = "object-path";
	static String PROPERTY_DESTINATIONS = "destinations";
	static String PROPERTY_OBJECT_PATHS = "object-paths";
	
	static String ATTR_DESTINATION_NAME = "destination-name";
	static String ATTR_ESB = "esb";

	public String getValue(XModelObject object) {
		String tag = object.getAttributeValue("tag");
		if(TAG_ROUTE_TO.equals(tag)) {
			if(!checkParentPropertyName(object, PROPERTY_DESTINATIONS)) return null;
			return getAnyElementAttributeValue(object, ATTR_DESTINATION_NAME);
		} else if(TAG_OBJECT_PATH.equals(tag)) {
			if(!checkParentPropertyName(object, PROPERTY_OBJECT_PATHS)) return null;
			return getAnyElementAttributeValue(object, ATTR_ESB);
		}
		return null;
	}
	
	private boolean checkParentPropertyName(XModelObject object, String name) {
		XModelObject p = object.getParent();
		if(p == null || !ESBConstants.ENT_ESB_PROPERTY.equals(p.getModelEntity().getName())) {
			return false;
		}
		if(!name.equals(p.getAttributeValue(ESBConstants.ATTR_NAME))) return false;
		return true;
	}
	
	private String getAnyElementAttributeValue(XModelObject object, String name) {
		String as = object.getAttributeValue("attributes");
		if(as == null || as.indexOf(name) < 0) {
			return null;
		}
		String[] s = as.split(";");
		for (int i = 0; i < s.length; i++) {
			if(!s[i].startsWith(name)) continue;
			int j = s[i].indexOf('=');
			String v = s[i].substring(j + 1).trim();
			return v.length() == 0 ? null : v;
		}
		return null;
	}

}
