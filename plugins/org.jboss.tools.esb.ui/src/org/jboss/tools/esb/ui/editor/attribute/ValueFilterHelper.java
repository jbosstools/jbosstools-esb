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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.ui.ESBUiPlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ValueFilterHelper {

	/**
	 * Quick check, that ESB tools do not need to look into the Java type in searching for ESB features.
	 * ESB core qualified names start with org.jboss.* and clients of ESB are not likely to start 
	 * qualified names with
	 *  	java.*
	 *  	javax.*
	 *  	com.sun.*
	 *  	sun.*
	 *  	org.apache.*
	 *  which are excluded by this method.
	 *  
	 * @param qualifiedName
	 * @return
	 */
	public static boolean isNotESBPackage(String qualifiedName) {
		return qualifiedName.startsWith("java.") 
				|| qualifiedName.startsWith("javax.") 
				|| qualifiedName.startsWith("com.sun.") 
				|| qualifiedName.startsWith("sun.")
				|| qualifiedName.startsWith("org.apache.");
	}

	/**
	 * Utility method. Returns true if the project is an accessible Java project 
	 * that contains class qualifiedName in its class path.
	 * 
	 * @param project
	 * @param qualifiedName
	 * @return
	 */
	public static boolean isInClassPath(IProject project, String qualifiedName) {
		if(project == null || !project.isAccessible()) return false;
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
			return jp != null && (EclipseJavaUtil.findType(jp, qualifiedName) != null);
		} catch (JavaModelException e) {
			return false;
		}
	}

	/**
	 * Returns existing package Java model object by qualified package name.
	 * 
	 * @param javaProject
	 * @param packageName
	 * @return
	 */
	public static IPackageFragment findPackageFragment(IProject project, String packageName) {
		if(project == null || !project.isAccessible()) return null;
		IJavaProject javaProject = EclipseUtil.getJavaProject(project);
		if(javaProject == null || packageName == null || packageName.length() == 0) return null;
		IPackageFragmentRoot[] rs = null;
		try {
			rs = javaProject.getPackageFragmentRoots();
		} catch (JavaModelException e) {
			ESBUiPlugin.log(e);
		}
		if(rs != null) for (int i = 0; i < rs.length; i++) {
			IPackageFragment f = rs[i].getPackageFragment(packageName);
			if(f != null && f.exists()) return f;
		}
		return null;
	}

}
