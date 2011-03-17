package org.jboss.tools.esb.ui.editor.attribute.adapter;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.widgets.Display;
import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.XAttributeSupport.IAttributeDependency;
import org.jboss.tools.common.model.ui.attribute.editor.ComboBoxFieldEditor;
import org.jboss.tools.common.model.ui.attribute.editor.IPropertyEditor;

public class RoutingFormDependency implements IAttributeDependency {
	XAttributeSupport support;

	public RoutingFormDependency() {}

	public void setSupport(XAttributeSupport support) {
		this.support = support;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		if(source instanceof ServiceCategoryListAdapter) {
			final ComboBoxFieldEditor f = (ComboBoxFieldEditor)support.getFieldEditorByName("service name");
			if(f == null) return;
			final PropertyChangeEvent evt1 = new PropertyChangeEvent(this, IPropertyEditor.LIST_CONTENT, "old", "new");
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ComboBoxFieldEditor f1 = (ComboBoxFieldEditor)support.getFieldEditorByName("service category");
					if(f1 == null) return;
					f.propertyChange(evt1);
					f1.setFocusAndKeepSelection();
				}
			});
		}
		
	}

}
