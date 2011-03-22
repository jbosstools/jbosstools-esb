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
public class MessageStoreClassValueFilter implements IValueFilter {
	IProject project;
	
	static String MessageStoreInterface = "org.jboss.soa.esb.services.persistence.MessageStore";

	public boolean accept(String value) {
		return !ValueFilterHelper.isNotESBPackage(value)
			&& (EclipseJavaUtil.isDerivedClass(value, MessageStoreInterface, project)
					|| (ValueFilterHelper.findPackageFragment(project, value) != null));
	}

	public boolean init(XModelObject context, XAttribute attribute) {
		project = EclipseResourceUtil.getProject(context);
		return ValueFilterHelper.isInClassPath(project, MessageStoreInterface);
	}

}
