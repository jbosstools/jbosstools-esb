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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	static Map<String, IFormData> singleChildLists = new HashMap<String, IFormData>();
	
	static IFormData createOneAttributeSingleChildList(String header, String attrName, String childEntity, String actionPath) {
		IFormData result = new FormData(
			header,
			ModelFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(attrName, 100)},
			new String[]{childEntity},
			FormLayoutDataUtil.createDefaultFormActionData(actionPath)
		);
		singleChildLists.put(childEntity, result);
		return result;
	}

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
		new String[]{ENT_ESB_SERVICE_101, ENT_ESB_SERVICE_110, ENT_ESB_SERVICE_120},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddService") //$NON-NLS-1$
	);
				
	static IFormData ESB_SERVICE_SUB_LIST_DEFINITION = new FormData(
		"Services", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		"Services", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_ESB_SERVICE_101, ENT_ESB_SERVICE_110, ENT_ESB_SERVICE_120},
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
		new FormAttributeData[]{new FormAttributeData("destination name", 25), new FormAttributeData("service name", 25), new FormAttributeData("expression", 50)}, 
		new String[]{ENT_ESB_ROUTE_TO},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddRouteTo") //$NON-NLS-1$
	);

	static IFormData ESB_OBJECT_PATH_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Object Paths List", "esb", ENT_ESB_OBJECT_PATH, "CreateActions.AddObjectPath"		
	);

	static IFormData ALIAS_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Alias List", "name", ConverterConstants.ALIAS_ENTITY, "CreateActions.AddAlias"		
	);

	static IFormData ATTRIBUTE_ALIAS_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Attribute Alias List", "name", ConverterConstants.ATTRIBUTE_ALIAS_ENTITY, "CreateActions.AddAttributeAlias"		
	);

	static IFormData FIELD_ALIAS_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Field Alias List", "alias", ConverterConstants.FIELD_ALIAS_ENTITY, "CreateActions.AddFieldAlias"		
	);

	static IFormData BPM_VAR_LIST_DEFINITION = new FormData(
		"BPM Var List", //$NON-NLS-1$
		ModelFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("esb", 50), new FormAttributeData("bpm", 50)}, 
		new String[]{ConverterConstants.BPM_VAR_ENTITY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddBPMVar") //$NON-NLS-1$
	);

	static IFormData NAMESPACE_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Namespace List", "namespace uri", ConverterConstants.NAMESPACE_ENTITY, "CreateActions.AddNamespace"		
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

	static IFormData ESB_NOTIFY_ATTACHMENT_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Attachments", "file", "ESBPreNotifyAttachment", "CreateActions.CreateAttachment"		
	);
	
	static IFormData ESB_NOTIFY_COLUMN_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Columns", "name", "ESBPreNotifyColumn", "CreateActions.CreateColumn"		
	);
	
	static IFormData ESB_NOTIFY_FTP_LIST_DEFINITION = createOneAttributeSingleChildList(
		"FTP", "url", "ESBPreNotifyFTP", "CreateActions.CreateFTP"		
	);

	static IFormData ESB_NOTIFY_FTP_L_LIST_DEFINITION = createOneAttributeSingleChildList(
		"FTP", "url", "ESBPreNotifyFTPList", "CreateActions.CreateFTPList"		
	);

	static IFormData ESB_NOTIFY_FILE_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Files", "uri", "ESBPreNotifyFile", "CreateActions.CreateFile"		
	);

	static IFormData ESB_NOTIFY_PROP_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Message Properties", "name", "ESBPreNotifyProp", "CreateActions.CreateProp"		
	);

	static IFormData ESB_NOTIFY_QUEUE_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Queues", "jndi name", "ESBPreNotifyQueue", "CreateActions.CreateQueue"		
	);

	static IFormData ESB_NOTIFY_TOPIC_LIST_DEFINITION = createOneAttributeSingleChildList(
		"Topics", "jndi name", "ESBPreNotifyTopic", "CreateActions.CreateTopic"		
	);

	static IFormData ESB_NOTIFY_TCP_LIST_DEFINITION = createOneAttributeSingleChildList(
		"TCP", "uri", "ESBPreNotifyTCP", "CreateActions.CreateTCP"		
	);

}
