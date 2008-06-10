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

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.AnyElementObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * @author Viacheslav Kabanovich
 */
public abstract class ListConverter implements IPropertyConverter {

	public ListConverter() {}

	protected abstract String getPropertyName();

	protected abstract String getItemEntityName();

	public void toSpecific(XModelObject basicAction, XModelObject specificAction) {
		XModelObject p = basicAction.getChildByPath(getPropertyName());
		if(p == null) return;
		XModelObject[] as = p.getChildren();
		for (int i = 0; i < as.length; i++) {
			if(as[i] instanceof AnyElementObjectImpl) {
				String tag = as[i].getAttributeValue("tag");
				Map<String, String> attr = toMap(((AnyElementObjectImpl)as[i]).getAttributes());

				XModelObject a = specificAction.getModel().createModelObject(getItemEntityName(), null);
				if(a == null || tag == null || !tag.equals(a.getModelEntity().getXMLSubPath())) {
					continue;
				}
				
				XModelEntity entity = a.getModelEntity();
				XAttribute[] attrs = entity.getAttributes();
				for (int j = 0; j < attrs.length; j++) {
					String name = attrs[j].getName();
					String xml = attrs[j].getXMLName();
					if(xml == null || xml.length() == 0) continue;
					String value = attr.get(xml);
					if(value == null || value.length() == 0) {
						value = attrs[j].getDefaultValue();
					}
					if(value != null) {
						a.setAttributeValue(name, value);
					}
				}
				if(a.getModelEntity().getChild("AnyElement") != null) {
					XModelObject[] cs = as[i].getChildren();
					for (int j = 0; j < cs.length; j++) {
						a.addChild(cs[j].copy());
					}
				}
				specificAction.addChild(a);
			}
		}
		p.removeFromParent();
	}

	public void toBasic(XModelObject basicAction, XModelObject specificAction) {
		XModelObject[] as = specificAction.getChildren(getItemEntityName());
		if(as.length == 0) return;
		XModelObject p = basicAction.getModel().createModelObject(ESBConstants.ENT_ESB_PROPERTY, null);
		p.setAttributeValue(ESBConstants.ATTR_NAME, getPropertyName());
		for (int i = 0; i < as.length; i++) {
			XModelObject t = basicAction.getModel().createModelObject("AnyElement", null);
			t.setAttributeValue("tag", as[i].getModelEntity().getXMLSubPath());
			StringBuffer sb = new StringBuffer();
			XModelEntity entity = as[i].getModelEntity();
			XAttribute[] attrs = entity.getAttributes();
			for (int j = 0; j < attrs.length; j++) {
				String name = attrs[j].getName();
				String xml = attrs[j].getXMLName();
				if(xml == null || xml.length() == 0) continue;
				String value = as[i].getAttributeValue(name);
				if(value == null || value.length() == 0 || value.equals(attrs[j].getDefaultValue())) {
					if(!"always".equals(attrs[j].getProperty("save"))) continue;
				}
				if(sb.length() > 0) {
					sb.append(';');
				}
				sb.append(xml).append('=').append(value);
			}
			String attributes = sb.toString();
			t.setAttributeValue("attributes", attributes);
			t.setAttributeValue(XModelObjectLoaderUtil.ATTR_ID_NAME, "" + (i + 1));
			if(as[i].getModelEntity().getChild("AnyElement") != null) {
				XModelObject[] cs = as[i].getChildren();
				for (int j = 0; j < cs.length; j++) {
					t.addChild(cs[j].copy());
				}
			}
			p.addChild(t);
		}
		basicAction.addChild(p);
	}

	static Map<String, String> toMap(String[][] attributes) {
		Map<String,String> map = new HashMap<String, String>();
		for (int i = 0; i < attributes.length; i++) {
			map.put(attributes[i][0], attributes[i][1]);
		}
		return map;
	}

}
