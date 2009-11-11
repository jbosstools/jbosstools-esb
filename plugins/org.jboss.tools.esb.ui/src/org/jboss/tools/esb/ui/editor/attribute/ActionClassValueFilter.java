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
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IValueFilter;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

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
		

		return b;
	}

	public boolean init(XModelObject context, XAttribute attribute) {
		project = EclipseResourceUtil.getProject(context);
		try {
			return (EclipseJavaUtil.findType(EclipseResourceUtil.getJavaProject(project), AbstractActionLifecycle) != null);
		} catch (JavaModelException e) {
			return false;
		}
	}

}
