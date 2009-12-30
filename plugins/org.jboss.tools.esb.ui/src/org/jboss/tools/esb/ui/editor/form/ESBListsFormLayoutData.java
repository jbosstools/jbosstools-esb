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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.impl.XModelMetaDataImpl;
import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.common.model.ui.forms.ModelFormLayoutData;
import org.jboss.tools.esb.core.model.ESBConstants;
import org.jboss.tools.esb.core.model.converters.ConverterConstants;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBListsFormLayoutData implements ESBConstants {

	static IFormData ESB_PROPERTY_LIST_DEFINITION = new FormData(
		"Properties", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
													//special ATTR_PRESENTATION needed as value may be inner XML
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 35), new FormAttributeData(ATTR_PROPERTY_VALUE_PRESENTATION, 65, "Value")}, 
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

	static String[] getActionEntities(String actionsEntity) {
		XModelEntity entity = XModelMetaDataImpl.getInstance().getEntity(actionsEntity);
		if(entity == null) {
			return new String[]{ENT_ESB_ACTION};
		}
		XChild[] cs = entity.getChildren();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < cs.length; i++) {
			list.add(cs[i].getName());
		}
		return list.toArray(new String[0]);
	}
	static IFormData ESB_ACTION_101_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		getActionEntities(ENT_ESB_ACTIONS_101),
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyAction") //$NON-NLS-1$
	);

	static IFormData ESB_ACTION_110_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		getActionEntities(ENT_ESB_ACTIONS_110),
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyAction") //$NON-NLS-1$
	);

	static IFormData ESB_ACTION_120_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		getActionEntities(ENT_ESB_ACTIONS_120),
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyAction") //$NON-NLS-1$
	);

	static IFormData ESB_ACTION_SUB_LIST_DEFINITION = new FormData(
		"Actions", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Actions", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_ACTION,ENT_ESB_ACTION_120},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAction") //$NON-NLS-1$
	);

	static IFormData ESB_SERVICE_LIST_DEFINITION = new FormData(
		"Services", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_SERVICE_101, ENT_ESB_SERVICE_110},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddService") //$NON-NLS-1$
	);
				
	static IFormData ESB_SERVICE_SUB_LIST_DEFINITION = new FormData(
		"Services", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Services", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_SERVICE_101, ENT_ESB_SERVICE_110},
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
		"Channel list", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ESBConstants.ATTR_BUS_ID, 100)}, 
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

	static IFormData ESB_ROUTE_LIST_DEFINITION = new FormData(
		"Route List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("destination name", 50), new FormAttributeData("service name", 50)}, 
		new String[]{ENT_ESB_ROUTE_TO},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddRouteTo") //$NON-NLS-1$
	);

	static IFormData ESB_OBJECT_PATH_LIST_DEFINITION = new FormData(
		"Object Paths List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("esb", 100)}, 
		new String[]{ENT_ESB_OBJECT_PATH},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddObjectPath") //$NON-NLS-1$
	);

	static IFormData ALIAS_LIST_DEFINITION = new FormData(
		"Alias List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("name", 100)}, 
		new String[]{ConverterConstants.ALIAS_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAlias") //$NON-NLS-1$
	);

	static IFormData ATTRIBUTE_ALIAS_LIST_DEFINITION = new FormData(
		"Attribute Alias List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("name", 100)}, 
		new String[]{ConverterConstants.ATTRIBUTE_ALIAS_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAttributeAlias") //$NON-NLS-1$
	);

	static IFormData FIELD_ALIAS_LIST_DEFINITION = new FormData(
		"Field Alias List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("alias", 100)}, 
		new String[]{ConverterConstants.FIELD_ALIAS_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFieldAlias") //$NON-NLS-1$
	);

	static IFormData BPM_VAR_LIST_DEFINITION = new FormData(
		"BPM Var List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("esb", 50), new FormAttributeData("bpm", 50)}, 
		new String[]{ConverterConstants.BPM_VAR_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddBPMVar") //$NON-NLS-1$
	);

	static IFormData NAMESPACE_LIST_DEFINITION = new FormData(
		"Namespace List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("namespace uri", 100)}, 
		new String[]{ConverterConstants.NAMESPACE_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddNamespace") //$NON-NLS-1$
	);

	static IFormData ROUTER_NAMESPACE_LIST_DEFINITION = new FormData(
		"Namespace List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("prefix", 50), new FormAttributeData("uri", 50)}, 
		new String[]{ConverterConstants.ROUTER_NAMESPACE_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddNamespace") //$NON-NLS-1$
	);

	static IFormData ESB_NOTIFICATION_LIST_DEFINITION = new FormData(
		"Notification Lists", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("type", 100)}, //$NON-NLS-1$ 
		new String[]{ENT_ESB_NOTIFICATION, ENT_ESB_NOTIFICATION_120},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddNotificationList") //$NON-NLS-1$
	);

	static String[] getTargetEntities(String targetsEntity) {
		XModelEntity entity = XModelMetaDataImpl.getInstance().getEntity(targetsEntity);
		if(entity == null) {
			return new String[]{ENT_ESB_TARGET};
		}
		XChild[] cs = entity.getChildren();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < cs.length; i++) {
			list.add(cs[i].getName());
		}
		return list.toArray(new String[0]);
	}

	static IFormData ESB_TARGET_LIST_DEFINITION = new FormData(
		"Targets", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("class", 100)}, //$NON-NLS-1$ 
		getTargetEntities(ENT_ESB_NOTIFICATION),
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateTarget") //$NON-NLS-1$
	);
	static IFormData ESB_TARGET_120_LIST_DEFINITION = new FormData(
		"Targets", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("class", 100)}, //$NON-NLS-1$ 
		getTargetEntities(ENT_ESB_NOTIFICATION_120),
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyTarget") //$NON-NLS-1$
	);

}
