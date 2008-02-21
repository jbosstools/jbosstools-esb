package org.jboss.tools.esb.ui.editor.attribute.adapter;

import java.util.TreeSet;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;

public class BusListAdapter extends DefaultComboBoxValueAdapter {

	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		BusListContentProvider p = new BusListContentProvider();
		p.setContext(modelObject);
		p.setAttribute(attribute);
		return p;	
	}

}

class BusListContentProvider extends DefaultXAttributeListContentProvider {
	private XModelObject context;
	
	public void setContext(XModelObject context) {
		this.context = context;
	}

	protected void loadTags() {
		XModelObject f = context;
		while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
		if(f == null) return;
		XModelObject[] ps = f.getChildByPath("Providers").getChildren();
		TreeSet<String> set = new TreeSet<String>();
		for (int i = 0; i < ps.length; i++) {
			XModelObject[] cs = ps[i].getChildren();
			for (int j = 0; j < cs.length; j++) {
				if(cs[j].getModelEntity().getAttribute("bus id") != null) {
					String v = cs[j].getAttributeValue("bus id");
					if(v != null && v.length() > 0) {
						set.add(v);
					}
				}
			}
		}
		tags = set.toArray(new String[0]);
		
	}

}

