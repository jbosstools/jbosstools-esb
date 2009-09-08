/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.project.ui.wizards.export;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.ide.eclipse.as.wtp.core.vcf.ModuleExportOperation;
import org.jboss.tools.esb.core.ESBProjectConstant;

public class ESBComponentExportOperation extends ModuleExportOperation {
	public ESBComponentExportOperation() {
		super();
	}

	public ESBComponentExportOperation(IDataModel model) {
		super(model);
	}

	protected String getModuleTypeID() {
		return ESBProjectConstant.ESB_PROJECT_FACET;
	}

	protected String archiveString() {
		return "ESB File";
	}
}
