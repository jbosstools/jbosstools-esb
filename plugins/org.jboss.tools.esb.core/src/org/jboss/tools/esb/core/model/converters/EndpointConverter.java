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
import org.jboss.tools.common.model.impl.AnyElementObjectImpl;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * @author Viacheslav Kabanovich
 */
public class EndpointConverter extends ListConverter {
	static String ATTR_FILE = "file";
	static String ATTR_ENDPOINT_URL = "endpoint url";

	public void toBasic(XModelObject basicAction, XModelObject specificAction) {
		XModelObject[] as = specificAction.getChildren(getItemEntityName());
		boolean empty = true;
		if(as.length > 0) empty = false;
		XModelObject p = basicAction.getModel().createModelObject(ESBConstants.ENT_ESB_PROPERTY, null);
		p.setAttributeValue(ESBConstants.ATTR_NAME, getPropertyName());
		for (int i = 0; i < as.length; i++) {
			XModelObject t = toAnyElement(as[i], i);
			p.addChild(t);
		}
		String endpointURL = specificAction.getAttributeValue(ATTR_ENDPOINT_URL);
		if(endpointURL != null && endpointURL.length() > 0) {
			empty = false;
			p.setAttributeValue(ESBConstants.ATTR_VALUE, endpointURL);
		}
		String file = specificAction.getAttributeValue(ATTR_FILE);
		if(file != null && file.length() > 0) {
			empty = false;
			XModelObject f = basicAction.getModel().createModelObject(getItemEntityName(), null);
			f.setAttributeValue(ESBConstants.ATTR_NAME, ATTR_FILE);
			f.setAttributeValue(ESBConstants.ATTR_VALUE, file);
			XModelObject t = toAnyElement(f, p.getChildren().length);
			p.addChild(t);
		}

		if(!empty) {
			basicAction.addChild(p);
		}
	}

	public void toSpecific(XModelObject basicAction, XModelObject specificAction) {
		XModelObject p = basicAction.getChildByPath(getPropertyName());
		if(p == null) return;
		XModelObject[] as = p.getChildren();
		for (int i = 0; i < as.length; i++) {
			if(as[i] instanceof AnyElementObjectImpl) {
				XModelObject a = fromAnyElement(as[i], getItemEntityName());
				if(a != null) {
					if(ATTR_FILE.equals(a.getAttributeValue(ESBConstants.ATTR_NAME))) {
						specificAction.setAttributeValue(ATTR_FILE, a.getAttributeValue(ESBConstants.ATTR_VALUE));
					} else {
						specificAction.addChild(a);
					}
				}
			}
		}
		String endpointURL = p.getAttributeValue(ESBConstants.ATTR_VALUE);
		if(endpointURL != null && endpointURL.length() > 0) {
			specificAction.setAttributeValue(ATTR_ENDPOINT_URL, endpointURL);
		}
		p.removeFromParent();		
	}

	protected String getItemEntityName() {
		return ConverterConstants.HTTP_CLIENT_PROP_ENTITY;
	}

	protected String getPropertyName() {
		return ConverterConstants.END_POINT_URL;
	}

}
