/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.ui.wizards.NewClassWizardPageEx;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.common.ui.widget.editor.RadioFieldEditor;
import org.jboss.tools.esb.core.ESBCorePlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewActionWizardPage extends NewClassWizardPageEx {
	public static String PROCESS = "org.jboss.soa.esb.actions.annotation.Process";
	
	RadioFieldEditor pojo = null;

	public NewActionWizardPage() {		
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		pojo = createISPOJOControls(composite, nColumns);

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

// use reflection?
//		createMethodStubSelectionControls(composite, nColumns);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	RadioFieldEditor createISPOJOControls(Composite composite, int nColumns) {
		String name = "pojo";
		String defaultValue = "true";
		List<String> labels = new ArrayList<String>();
		labels.add("As AbstractActionPipelineProcessor implementation");
		labels.add("As annotated POJO");
		List values = new ArrayList();
		values.add("false");
		values.add("true");
		RadioFieldEditor radio = new RadioFieldEditor(name, "", labels, values, defaultValue);
		CompositeEditor editor = new CompositeEditor(name,"", defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,""), radio});
		editor.doFillIntoGrid(composite);
		editor.addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				onPOJOChange(evt.getNewValue());				
			}
		});
		onPOJOChange(defaultValue);
		return radio;
	}
	
	void onPOJOChange(Object value) {
		if("true".equals(value)) {
			setSuperClass("", true);
		} else {
			setSuperClass(adapter.getSuperClass(), adapter.isCanBeModified());
		}
		
	}

	protected void createTypeMembers(IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(newType, imports, monitor);
		if("true".equals(pojo.getValueAsString())) {
			modifyJavaSourceForPOJO(newType, imports);
		} else {
			modifyJavaSourceForAbstractImplementation(newType, imports);
		}		
	}

	void modifyJavaSourceForAbstractImplementation(IType type, ImportsManager imports) {
		try {
			String name = type.getElementName();
			String sc = type.getSuperclassTypeSignature();
			if(sc != null) {
				sc = EclipseJavaUtil.resolveTypeAsString(type, sc);
			}
			if(type != null && "org.jboss.soa.esb.actions.AbstractActionPipelineProcessor".equals(sc)) {
				ICompilationUnit w = type.getCompilationUnit();
				IBuffer b = w.getBuffer();
				String s = b.getContents();
				String lineDelimiter =  StubUtility.getLineDelimiterUsed(type.getJavaProject());
				
				imports.addImport("org.jboss.soa.esb.actions.AbstractActionPipelineProcessor");
				imports.addImport("org.jboss.soa.esb.actions.ActionProcessingException");
				imports.addImport("org.jboss.soa.esb.helpers.ConfigTree");
				imports.addImport("org.jboss.soa.esb.message.Message");
//				imports.addImport("");
				
				
				s = b.getContents();
				boolean hasOverrideAnnotation = s.indexOf("@Override") > 0;
				
				int i = s.indexOf('{');
				int j = s.lastIndexOf('}');
				if(i > 0 && j > i) {
					String tab = "\t";
					String content = lineDelimiter 
						+ tab + "protected ConfigTree _config;" + lineDelimiter
						+ lineDelimiter
						+ tab + "public " + name + "(ConfigTree config) {" + lineDelimiter
						+ tab + tab + "_config = config;"+ lineDelimiter
						+ tab + "}" + lineDelimiter
						+ lineDelimiter
						+ (hasOverrideAnnotation ? tab + "@Override" + lineDelimiter : "")
						+ tab + "public Message process(Message message) throws ActionProcessingException {" + lineDelimiter
						+ tab + tab + "//ADD CUSTOM ACTION CODE HERE" + lineDelimiter
						+ tab + tab + "return message;" + lineDelimiter
						+ tab + "}" + lineDelimiter;
					b.replace(i + 1, j - i - 1, content);
				}
			}
		} catch (CoreException e) {
			ESBCorePlugin.log(e);
		}
	}

	void modifyJavaSourceForPOJO(IType type, ImportsManager imports) {
		try {
			StringBuffer buf= new StringBuffer();
			final String lineDelim= "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			String comment= CodeGeneration.getMethodComment(type.getCompilationUnit(), type.getTypeQualifiedName('.'), "process", new String[] {"message"}, new String[0], Signature.createTypeSignature("void", true), null, lineDelim); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
			imports.addImport(PROCESS); //$NON-NLS-1$
			buf.append("@Process").append(lineDelim); //$NON-NLS-1$
			buf.append("public Message process("); //$NON-NLS-1$
			buf.append(imports.addImport("org.jboss.soa.esb.message.Message")); //$NON-NLS-1$
			buf.append(" message) {"); //$NON-NLS-1$
			buf.append(lineDelim);
			String content= "//ADD CUSTOM ACTION CODE HERE" + lineDelim + "return message;";
//				CodeGeneration.getMethodBodyContent(type.getCompilationUnit(), type.getTypeQualifiedName('.'), "process", false, "//ADD CUSTOM ACTION CODE HERE", lineDelim); //$NON-NLS-1$ //$NON-NLS-2$
			if (content != null && content.length() != 0)
				buf.append(content);
			buf.append(lineDelim);
			buf.append("}"); //$NON-NLS-1$
			type.createMethod(buf.toString(), null, false, null);
		} catch (CoreException e) {
			ESBCorePlugin.log(e);
		}
	}

}
