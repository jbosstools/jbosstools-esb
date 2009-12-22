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

import org.jboss.tools.common.model.XModelObject;

/**
 * @author Viacheslav Kabanovich
 */
public class EJBParamConverter extends BasicListConverter {

	public EJBParamConverter(String propertyName, String itemEntityName) {
		super(propertyName, itemEntityName);
	}

	protected boolean isRelevantTag(String tag, XModelObject object) {
		if(object == null || tag == null) {
			return false;
		}
		String baseName = object.getModelEntity().getXMLSubPath();
		if(!tag.startsWith(baseName)) {
			return false;
		}
		String suff = tag.substring(baseName.length());
		if(suff.length() == 0) {
			return false;
		}
		int index = 0;
		try {
			index = Integer.parseInt(suff);
		} catch (NumberFormatException e) {
			return false;
		}
		object.setAttributeValue("index", "" + index);
		return true;
	}

	protected String getTagName(XModelObject specific, int index) {
		return "" + super.getTagName(specific, index) + "" + specific.getAttributeValue("index");
	}

}
