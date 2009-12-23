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

import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBLoaderUtil extends XModelObjectLoaderUtil {
	
	public ESBLoaderUtil() {}

    protected String getChildEntity(XModelEntity entity, Element e) {
    	String result = super.getChildEntity(entity, e);
    	if(result != null && result.startsWith(ESBConstants.PREACTION_PREFIX)) {
    		if(entity.getChild(ESBConstants.ENT_ESB_ACTION) != null) {
    			return ESBConstants.ENT_ESB_ACTION;
    		} else if(entity.getChild(ESBConstants.ENT_ESB_ACTION_120) != null) {
    			return ESBConstants.ENT_ESB_ACTION_120;
    		}
    		//TODO when different versions appear, support them!
    	}
    	return result;
    }

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

	public void loadChildren(Element element, XModelObject o) {
		super.loadChildren(element, o);
		String entity = o.getModelEntity().getName();
		if(SpecificActionLoader.instance.isActionsFolder(entity)) {
			SpecificActionLoader.instance.convertChildrenToSpecific(o);
		} else if("true".equals(o.getModelEntity().getProperty("hasConvertedProperties"))) {
			SpecificPropertyConverter.instance.convertBasicToSpecific(o);
		}
	}

    public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	if(SpecificActionLoader.instance.isPreActionEntity(o)) {
    		o = SpecificActionLoader.instance.convertSpecificActionToBasic(o);
    	} else if("true".equals(o.getModelEntity().getProperty("hasConvertedProperties"))) {
    		o = SpecificPropertyConverter.instance.convertSpecificToBasic(o);
    	}
    	return super.save(parent, o);
    }

    protected boolean needToSave(XModelObject o) {
    	String s = o.getModelEntity().getProperty("saveDefault"); //$NON-NLS-1$
    	if(!"false".equals(s)) return true; //$NON-NLS-1$
    	if(hasSetAttributes(o)) return true;
    	XModelObject[] cs = o.getChildren();
    	if(o.getChildren().length > 2) return true;
    	for (int i = 0; i < cs.length; i++) {
    		if(needToSave(cs[i])) return true;
    	}
    	return false;
    }

    private boolean hasSetAttributes(XModelObject o) {
    	XAttribute[] as = o.getModelEntity().getAttributes();
    	for (int i = 0; i < as.length; i++) {
    		String xml = as[i].getXMLName();
    		// it would be more safe to check isSavable
    		if(xml == null || xml.length() == 0 || "NAME".equals(xml)) continue; //$NON-NLS-1$
    		String v = o.getAttributeValue(as[i].getName());
    		if(v != null && v.length() > 0 && !v.equals(as[i].getDefaultValue())) return true;
    	}
    	String finalComment = o.get("#final-comment"); //$NON-NLS-1$
    	if(finalComment != null && finalComment.length() > 0) return true;
    	return false;
    }

    public void saveAttribute(Element element, String xmlname, String value) {
    	if(ESBConstants.XML_ATTR_PROTECTED_METHODS.equals(xmlname)) {
    		StringTokenizer st = new StringTokenizer(value, ",");
    		Element c = XMLUtilities.createElement(element, xmlname);
    		while(st.hasMoreTokens()) {
    			String t = st.nextToken();
    			Element m = XMLUtilities.createElement(c, "method");
    			m.setAttribute("name", t);
    		}
    	} else {
    		super.saveAttribute(element, xmlname, value);
    	}
    }

    public String getAttribute(Element element, String xmlname, XAttribute attr) {
    	if(ESBConstants.XML_ATTR_PROTECTED_METHODS.equals(xmlname)) {
    		Element c = XMLUtilities.getUniqueChild(element, xmlname);
    		if(c == null) return "";
    		Element[] ms = XMLUtilities.getChildren(c, "method");
    		StringBuffer sb = new StringBuffer();
    		for (Element m: ms) {
    			if(sb.length() > 0) sb.append(',');
    			sb.append("" + m.getAttribute("name"));
    		}
    		return sb.toString();
    	} else {    	
    		return super.getAttribute(element, xmlname, attr);
    	}
    }

	protected Set<String> getAllowedChildren(XModelEntity entity) {
		Set<String> children = super.getAllowedChildren(entity);
		if(entity.getName().equals(ESBConstants.ENT_ESB_HTTP_BUS_120)) {
			children.add(ESBConstants.XML_ATTR_PROTECTED_METHODS);
		}
		return children;
	}

    public boolean saveChildren(Element element, XModelObject o) {
    	boolean b = super.saveChildren(element, o);
    	if(o.getModelEntity().getName().equals(ESBConstants.ENT_ESB_HTTP_BUS_120)) {
    		checkHTTPBusDTD(element);
    	}
    	return b;
    }

    private void checkHTTPBusDTD(Element element) {
    	Element pm = XMLUtilities.getUniqueChild(element, ESBConstants.XML_ATTR_PROTECTED_METHODS);
    	if(pm == null) return;
    	NodeList list = element.getChildNodes();
    	Element reference = null;
    	boolean hasProperties = false;
    	for (int i = 0; i < list.getLength(); i++) {
    		Node n = list.item(i);
    		if(n instanceof Element) {
    			Element e = (Element)n;
    			if("property".equals(e.getNodeName())) {
    				hasProperties = true;
    				reference = null;
    			} else if(hasProperties) {
    				reference = e;
    				hasProperties = false;
    			} 
    		}
    	}
    	if(reference != null) {
    		element.insertBefore(pm, reference);
    	} else if(hasProperties) {
    		element.appendChild(pm);
    	}
    }
}
