/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.validator;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.validation.ValidationErrorManager;

abstract public class ESBValidationErrorManager extends ValidationErrorManager {

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return ESBPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	@Override
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return ESBPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}
}