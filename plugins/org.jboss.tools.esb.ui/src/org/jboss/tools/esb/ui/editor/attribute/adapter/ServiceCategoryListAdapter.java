package org.jboss.tools.esb.ui.editor.attribute.adapter;

import java.util.TreeSet;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;
import org.jboss.tools.esb.core.model.ESBConstants;

public class ServiceCategoryListAdapter extends DefaultComboBoxValueAdapter {

	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		ServiceCategoryListContentProvider p = new ServiceCategoryListContentProvider();
		p.setContext(modelObject);
		p.setAttribute(attribute);
		return p;	
	}

}

class ServiceCategoryListContentProvider extends DefaultXAttributeListContentProvider {
	private XModelObject context;
	
	public void setContext(XModelObject context) {
		this.context = context;
	}

	protected void loadTags() {
		XModelObject f = context;
		while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
		if(f == null) return;
		XModelObject servicesFolder = f.getChildByPath("Services");
		if(servicesFolder == null) return;
		TreeSet<String> set = new TreeSet<String>();
		XModelObject[] services = servicesFolder.getChildren();		
		for (int i = 0; i < services.length; i++) {
			set.add(services[i].getAttributeValue("category"));
		}
		tags = set.toArray(new String[0]);
	}
	
}

