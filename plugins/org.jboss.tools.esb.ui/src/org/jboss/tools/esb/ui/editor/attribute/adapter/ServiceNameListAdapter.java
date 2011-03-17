package org.jboss.tools.esb.ui.editor.attribute.adapter;

import java.util.TreeSet;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.action.XAttributeData;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;
import org.jboss.tools.esb.core.model.ESBConstants;

public class ServiceNameListAdapter extends DefaultComboBoxValueAdapter {

	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		ServiceNameListContentProvider p = new ServiceNameListContentProvider();
		p.setContext(modelObject);
		p.setAttribute(attribute);
		p.setData(this.attributeData);
		return p;	
	}

}

class ServiceNameListContentProvider extends DefaultXAttributeListContentProvider {
	static String ATTR_SERVICE_CATEGORY = "service category";
	private XModelObject context;
	XAttributeData data;
	
	public void setContext(XModelObject context) {
		this.context = context;
	}

	public void setData(XAttributeData data) {
		this.data = data;		
	}

	protected void loadTags() {
		XModelObject f = context;
		while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
		if(f == null) return;
		XModelObject servicesFolder = f.getChildByPath("Services");
		if(servicesFolder == null) return;
		String category = getCategory();
		if(category != null && category.trim().length() == 0) category = null;
		TreeSet<String> set = new TreeSet<String>();
		XModelObject[] services = servicesFolder.getChildren();		
		for (int i = 0; i < services.length; i++) {
			if(category != null && !services[i].getAttributeValue("category").equals(category)) {
				continue;
			}
			set.add(services[i].getAttributeValue("name"));
		}
		tags = set.toArray(new String[0]);
	}

	String getCategory() {
		if(data != null) {
			return data.getEntityData().getValue(ATTR_SERVICE_CATEGORY);
		} else if(context != null && context.getModelEntity().getAttribute(ATTR_SERVICE_CATEGORY) != null) {
			return context.getAttributeValue(ATTR_SERVICE_CATEGORY);
		}
		return ATTR_SERVICE_CATEGORY;
	}
	
}

