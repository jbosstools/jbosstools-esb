package org.jboss.tools.esb.ui.editor.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.meta.impl.XModelMetaDataImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultValueAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.IPropertyEditor;
import org.jboss.tools.common.model.ui.attribute.editor.StringButtonFieldEditorEx;
import org.jboss.tools.common.model.ui.forms.Form;
import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.common.model.ui.forms.ModelFormLayoutData;
import org.jboss.tools.common.model.ui.widgets.IWidgetSettings;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.core.model.ESBConstants;

public class ESBActionForm extends Form {
	static IFormData formData = ModelFormLayoutData.createGeneralFormData(
		XModelMetaDataImpl.getInstance().getEntity(ESBConstants.ENT_ESB_ACTION)
	);
	
	static {
		((FormAttributeData)formData.getAttributes()[2]).setWrapperClassName(StringButtonFieldEditorEx.class.getName());
	}

	public ESBActionForm() {
		super(formData);
	}

	protected Control createClientArea(Composite parent, IWidgetSettings settings) {
		Control c = super.createClientArea(parent, settings);
		XAttributeSupport support = getSupport();
		setProcessMethods(getSupport(), getModelObject());
		support.addPropertyChangeListener(new PCL());
		return c;
	}

	public void dispose() {
		DefaultValueAdapter a = (DefaultValueAdapter)getSupport().getPropertyEditorAdapterByName("process");
		XAttribute attr = a.getAttribute();
		if(attr == null) attr = a.getAttributeData().getAttribute();
        XAttributeConstraintAList c = (XAttributeConstraintAList)attr.getConstraint();
        c.setValues(new String[0]);
        super.dispose();
	}

	class PCL implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if(StringButtonFieldEditorEx.BUTTON_SELECTED.equals(evt.getPropertyName())
				&& evt.getSource() == getSupport().getPropertyEditorAdapterByName("process")) {
				setProcessMethods(getSupport(), getModelObject());
			} else if(IPropertyEditor.VALUE.equals(evt.getPropertyName())
				&& evt.getSource() == getSupport().getPropertyEditorAdapterByName("class")) {
				setProcessMethods(getSupport(), getModelObject());
			}
		}
		
	}

	public static void setProcessMethods(XAttributeSupport support, XModelObject object) {
		IProject p = EclipseResourceUtil.getProject(object);
		IJavaProject jp = EclipseResourceUtil.getJavaProject(p);
		String cls = support.getPropertyEditorAdapterByName("class").getStringValue(true);
		Set<String> list = new TreeSet<String>();
		try {
			collectMethods(jp, cls, list);
		} catch (JavaModelException e) {
			//ignore
		}

		DefaultValueAdapter a = (DefaultValueAdapter)support.getPropertyEditorAdapterByName("process");
		XAttribute attr = a.getAttribute();
		if(attr == null) attr = a.getAttributeData().getAttribute();
        XAttributeConstraintAList c = (XAttributeConstraintAList)attr.getConstraint();
        c.setValues(list.toArray(new String[0]));			
	}

	private static void collectMethods(IJavaProject jp, String typeName, Set<String> list) throws JavaModelException {
		IType type = EclipseJavaUtil.findType(jp, typeName);
		if(type == null) return;
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(ms[i].isConstructor()) continue;
			if(!Flags.isPublic(ms[i].getFlags())) continue;
			String[] ps = ms[i].getParameterTypes();
			if(ps == null || ps.length != 1) continue;
			String t = EclipseJavaUtil.resolveTypeAsString(type, ps[0]);
			if(!t.endsWith("Message")) continue;
			list.add(ms[i].getElementName());
		}
		String st = type.getSuperclassName();
		if(st != null && st.length() > 0) {
			st = EclipseJavaUtil.resolveType(type, st);
			if(st != null && st.length() > 0) {
				collectMethods(jp, st, list);
			}
		}
		
	}
}
