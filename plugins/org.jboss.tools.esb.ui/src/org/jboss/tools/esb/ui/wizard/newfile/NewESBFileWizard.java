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
package org.jboss.tools.esb.ui.wizard.newfile;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileContextEx;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileWizardEx;

/**
 * @author Viacheslav Kabanovich
 */
public class NewESBFileWizard extends NewFileWizardEx {

	public NewESBFileWizard() {
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(NewESBFileWizard.class, "EclipseCreateNewProject.png")); //$NON-NLS-1$
	}
	
	protected NewFileContextEx createNewFileContext() {
		return new NewESBFileContext();
	}
	
	class NewESBFileContext extends NewFileContextEx {
		protected String getActionPath() {
			return "CreateActions.CreateFiles.ESB.CreateFileESB"; //$NON-NLS-1$
		}
	}

}
