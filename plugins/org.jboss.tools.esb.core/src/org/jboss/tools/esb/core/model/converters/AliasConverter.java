package org.jboss.tools.esb.core.model.converters;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.AnyElementObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.esb.core.model.ESBConstants;

public class AliasConverter implements IPropertyConverter {
	static String ALIAS_ENTITY = "ESBPreAlias";

	public void toSpecific(XModelObject basicAction, XModelObject specificAction) {
		XModelObject p = basicAction.getChildByPath("aliases");
		if(p == null) return;
		XModelObject[] as = p.getChildren();
		for (int i = 0; i < as.length; i++) {
			if(as[i] instanceof AnyElementObjectImpl) {
				Map<String, String> attr = toMap(((AnyElementObjectImpl)as[i]).getAttributes());
				String n = attr.get(ESBConstants.ATTR_NAME);
				String v = attr.get(ESBConstants.ATTR_VALUE);
				if(n == null || v == null) {
					continue;
				}
				XModelObject a = specificAction.getModel().createModelObject(ALIAS_ENTITY, null);
				a.setAttributeValue(ESBConstants.ATTR_NAME, n);
				a.setAttributeValue(ESBConstants.ATTR_VALUE, v);
				specificAction.addChild(a);
			}
		}
		p.removeFromParent();
	}

	public void toBasic(XModelObject basicAction, XModelObject specificAction) {
		XModelObject[] as = specificAction.getChildren(ALIAS_ENTITY);
		if(as.length == 0) return;
		XModelObject p = basicAction.getModel().createModelObject(ESBConstants.ENT_ESB_PROPERTY, null);
		p.setAttributeValue(ESBConstants.ATTR_NAME, "aliases");
		basicAction.addChild(p);
		for (int i = 0; i < as.length; i++) {
			XModelObject t = basicAction.getModel().createModelObject("AnyElement", null);
			t.setAttributeValue("tag", "alias");
			String attrs = ESBConstants.ATTR_NAME + "="
				+ as[i].getAttributeValue(ESBConstants.ATTR_NAME)
				+ ";" + ESBConstants.ATTR_VALUE + "="
				+ as[i].getAttributeValue(ESBConstants.ATTR_VALUE);
			t.setAttributeValue("attributes", attrs);
			t.setAttributeValue(XModelObjectLoaderUtil.ATTR_ID_NAME, "" + (i + 1));
			p.addChild(t);
		}
	}

	static Map<String, String> toMap(String[][] attributes) {
		Map<String,String> map = new HashMap<String, String>();
		for (int i = 0; i < attributes.length; i++) {
			map.put(attributes[i][0], attributes[i][1]);
		}
		return map;
	}

}
