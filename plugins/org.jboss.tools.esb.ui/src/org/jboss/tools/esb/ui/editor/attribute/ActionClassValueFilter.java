/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.esb.ui.editor.attribute;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IValueFilter;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.ui.wizard.NewActionWizardPage;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ActionClassValueFilter implements IValueFilter {
	IProject project;
	
	static String AbstractActionLifecycle = "org.jboss.soa.esb.actions.AbstractActionLifecycle";
	static String AbstractActionPipelineProcessor = "org.jboss.soa.esb.actions.AbstractActionPipelineProcessor";

	public boolean accept(String value) {
		if(value.startsWith("java.") 
			|| value.startsWith("javax.") 
			|| value.startsWith("com.sun.") 
			|| value.startsWith("sun.")
			|| value.startsWith("org.apache.")
		) {
			return false;
		}
		boolean b = value.startsWith("org.jboss.soa.esb.actions.")
			|| EclipseJavaUtil.isDerivedClass(value, AbstractActionLifecycle, project)
			|| EclipseJavaUtil.isDerivedClass(value, AbstractActionPipelineProcessor, project);
		if(!b) {
			IType t = EclipseResourceUtil.getValidType(project, value);
			if(t != null) {
				try {
					boolean q = NewActionWizardPage.PROCESS.equals(EclipseJavaUtil.resolveType(t, "Process")) ;
					IMethod[] ms = t.getMethods();
					for (int i = 0; i < ms.length; i++) {
						IAnnotation a = ms[i].getAnnotation(NewActionWizardPage.PROCESS);
						if((a == null || !a.exists()) && q) {
							a = ms[i].getAnnotation("Process");
						}
						if(a != null && a.exists()) {
							b = true;
							break;
						}
					}
				} catch (CoreException e) {
					
				}
			}			
		}

		return b;
	}

	public boolean init(XModelObject context, XAttribute attribute) {
		project = EclipseResourceUtil.getProject(context);
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
			return jp != null && (EclipseJavaUtil.findType(jp, AbstractActionLifecycle) != null);
		} catch (JavaModelException e) {
			return false;
		}
	}

}
