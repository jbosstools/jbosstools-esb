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

import java.util.Map;

import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.AnyElementObjectImpl;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * Class for converting 'destinations' property of out-of-the-box action org.jboss.soa.esb.actions.Notifier 
 * that may have list of child tags <target> with specific structure. Target entity depends on attribute 'class'.
 * 
 * @author Viacheslav Kabanovich
 */
public class NotificationListConverter extends BasicListConverter implements ESBConstants {

	public NotificationListConverter(String propertyName, String itemEntityName) {
		super(propertyName, itemEntityName);
	}

	protected XModelObject getBasicProperty(XModelObject basicAction) {
		XModelObject result = super.getBasicProperty(basicAction);
		if(result != null) return result;
		XModelObject[] ps = basicAction.getChildren();
		for (XModelObject p: ps) {
			XModelObject[] as = p.getChildren();
			if(as.length > 0) {
				String tag = as[0].getAttributeValue("tag");
				if("NotificationList".equals(tag)) return p;
			}
		}
		return null;
	}

	protected String getToChildEntity(XModelObject any, XModelEntity parent) {
		if(ENT_ESB_NOTIFICATION_120.equals(parent.getName())) {
			String tag = any.getAttributeValue("tag");
			if("target".equalsIgnoreCase(tag)) {
				Map<String, String> attr = toMap(((AnyElementObjectImpl)any).getAttributes());
				String targetClass = attr.get("class");
				if(targetClass != null && targetClass.length() > 0) {
					String result = ENT_ESB_TARGET + targetClass;
					if(parent.getChild(result) != null) {
						return result;
					}
				}
			}
			return ENT_ESB_TARGET;
		}
		return super.getToChildEntity(any, parent);
	}
}
