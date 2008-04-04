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
package org.jboss.tools.esb.ui.editor.form;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.common.model.ui.forms.ModelFormLayoutData;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBListsFormLayoutData implements ESBConstants {

	static IFormData ESB_PROPERTY_LIST_DEFINITION = new FormData(
		"Properties", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
													//TODO maybe special ATTR_PRESENTATION needed as value may be inner XML
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 35), new FormAttributeData(ATTR_VALUE, 65, "Value")}, 
		new String[]{ENT_ESB_PROPERTY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddProperty") //$NON-NLS-1$
	);
			
	static IFormData ESB_LISTENER_LIST_DEFINITION = new FormData(
		"Listeners", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		LISTENERS_101,
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyListener") //$NON-NLS-1$
	);

	static IFormData ESB_LISTENER_SUB_LIST_DEFINITION = new FormData(
		"Listeners", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Listeners", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		LISTENERS_101,
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyListener") //$NON-NLS-1$
	);

	static IFormData ESB_ACTION_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_ACTION},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAction") //$NON-NLS-1$
	);

	static IFormData ESB_ACTION_SUB_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Actions", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_ACTION},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAction") //$NON-NLS-1$
	);

	static IFormData ESB_SERVICE_LIST_DEFINITION = new FormData(
		"Services", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_SERVICE},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddService") //$NON-NLS-1$
	);
				
	static IFormData ESB_SERVICE_SUB_LIST_DEFINITION = new FormData(
		"Services", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Services", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_SERVICE},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddService") //$NON-NLS-1$
	);
					
	static IFormData ESB_PROVIDER_LIST_DEFINITION = new FormData(
		"Providers", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		PROVIDERS_101,
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyProvider") //$NON-NLS-1$
	);

	static IFormData ESB_PROVIDER_SUB_LIST_DEFINITION = new FormData(
		"Providers", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Providers", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		PROVIDERS_101,
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyProvider") //$NON-NLS-1$
	);

	static IFormData ESB_BUS_LIST_DEFINITION = new FormData(
		"Bus list", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("bus id", 100)}, 
		BUSES_101,
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddBus") //$NON-NLS-1$
	);

	static IFormData ESB_SCHEDULE_LIST_DEFINITION = new FormData(
		"Schedule List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("schedule id", 100)}, 
		new String[]{ENT_ESB_SIMPLE_SCHEDULE, ENT_ESB_CRON_SCHEDULE},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnySchedule") //$NON-NLS-1$
	);
}
