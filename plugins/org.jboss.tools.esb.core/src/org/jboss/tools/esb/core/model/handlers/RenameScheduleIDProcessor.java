/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.esb.core.model.handlers;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.model.refactoring.ModelRenameProcessor;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class RenameScheduleIDProcessor extends ModelRenameProcessor {
	public static final String IDENTIFIER = "org.jboss.tools.esb.core.renameScheduleIDProcessor"; //$NON-NLS-1$

	public String[] getAffectedProjectNatures() throws CoreException {
		return new String[]{"org.eclipse.wst.common.project.facet.core.nature"};
	}

	@Override
	protected String getPropertyName() {
		return ESBConstants.ATTR_SCHEDULE_ID;
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

}
