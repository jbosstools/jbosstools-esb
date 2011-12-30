/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.ui.editor.attribute.adapter;

import java.util.TreeSet;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ScheduleListAdapter extends DefaultComboBoxValueAdapter {

	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		ScheduleListContentProvider p = new ScheduleListContentProvider();
		p.setContext(modelObject);
		p.setAttribute(attribute);
		return p;	
	}

}

class ScheduleListContentProvider extends DefaultXAttributeListContentProvider {
	private XModelObject context;
	
	public void setContext(XModelObject context) {
		this.context = context;
	}

	protected void loadTags() {
		XModelObject f = FileSystemsHelper.getFile(context);
		if(f == null) return;
		XModelObject[] ps = f.getChildByPath("Providers").getChildren();
		TreeSet<String> set = new TreeSet<String>();
		for (int i = 0; i < ps.length; i++) {
			if("schedule-provider".equals(ps[i].getModelEntity().getXMLSubPath())) {
				XModelObject[] cs = ps[i].getChildren();
				for (int j = 0; j < cs.length; j++) {
					if(cs[j].getModelEntity().getAttribute(ESBConstants.ATTR_SCHEDULE_ID) != null) {
						String v = cs[j].getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID);
						if(v != null && v.length() > 0) {
							set.add(v);
						}
					}
				}
			}
		}
		tags = set.toArray(new String[0]);
		
	}
}

