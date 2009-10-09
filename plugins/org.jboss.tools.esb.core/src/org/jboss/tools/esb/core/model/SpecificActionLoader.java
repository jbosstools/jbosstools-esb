/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.core.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.esb.core.ESBCorePlugin;
import org.jboss.tools.esb.core.model.converters.AliasConverter;
import org.jboss.tools.esb.core.model.converters.NotificationConverter;
import org.jboss.tools.esb.core.model.converters.ObjectPathConverter;
import org.jboss.tools.esb.core.model.converters.RouteToConverter;

/**
 * @author Viacheslav Kabanovich
 */
public class SpecificActionLoader implements ESBConstants {

	static final String ACTION_ENTITY = "ESBAction";
	static final String ACTIONS_FOLDER_ENTITY = "ESBActions";

	/**
	 * Version suffix should be added to entity name
	 */
	static final String[][] SPECIFIC_ACTIONS = {
		{"org.jboss.soa.esb.actions.converters.ByteArrayToString", "ESBPreActionByteArrayToString"},
			//encoding
		{"org.jboss.soa.esb.actions.converters.LongToDateConverter", "ESBPreActionLongToDateConverter"},
			//-
		{"org.jboss.soa.esb.actions.converters.ObjectInvoke", "ESBPreActionObjectInvoke"},
			//class-processor, class-method
		{"org.jboss.soa.esb.actions.converters.ObjectToCSVString", "ESBPreActionObjectToCSVString"},
			//bean-properties, fail-on-missing-property
		{"org.jboss.soa.esb.actions.converters.ObjectToXStream", "ESBPreActionObjectToXStream"},
			//class-alias, exclude-package
		{"org.jboss.soa.esb.actions.converters.SmooksTransformer", "ESBPreActionSmooksTransformer"},
			//resource-config; from, from-type, to, to-type
		{"org.jboss.soa.esb.smooks.SmooksAction", "ESBPreActionSmooksAction"},
			//smooksConfig
		{"org.jboss.soa.esb.actions.MessagePersister", "ESBPreActionMessagePersister"},
			//classification, message-store-class
		{"org.jboss.soa.esb.actions.converters.XStreamToObject", "ESBPreActionXStreamToObject"},
			//class-alias, exclude-package, incoming-type, root-node, aliases
		{"org.jboss.soa.esb.actions.jbpm.CommandInterpreter", "ESBPreActionCommandInterpreter"},
			//
		{"org.jboss.soa.esb.actions.scripting.GroovyActionProcessor", "ESBPreActionGroovyProcessor"},
			//script
		{"org.jboss.soa.esb.actions.Aggregator", "ESBPreActionAggregator"},
			//timeoutInMillies
		{"org.jboss.soa.esb.actions.ContentBasedRouter", "ESBPreActionContentBasedRouter"},
			//ruleSet, ruleLanguage, ruleReload, destinations!
		{"org.jboss.soa.esb.actions.StaticRouter", "ESBPreActionStaticRouter"},
			//destinations!
		{"org.jboss.soa.esb.actions.StaticWiretap", "ESBPreActionStaticWiretap"},
			//destinations!
		{"org.jboss.soa.esb.actions.Notifier", "ESBPreActionNotifier"},
			//destinations! NotificationList/target ...
		{"org.jboss.soa.esb.actions.soap.SOAPProcessor", "ESBPreActionSOAPProcessor"},
			//jbossws-endpoint
		{"org.jboss.soa.esb.actions.soap.SOAPClient", "ESBPreActionSOAPClient"},
			//wsdl, operation +other
		{"org.jboss.soa.esb.actions.SystemPrintln", "ESBPreActionSystemPrintln"},
			//message, printfull, outputstream
		{"org.jboss.soa.esb.actions.BusinessRulesProcessor", "ESBPreActionBusinessRulesProcessor"},
			//ruleSet, ruleLanguage, ruleReload, object-paths!
	};

	public static final SpecificActionLoader instance = new SpecificActionLoader();

	Map<String,String> classToEntity = new HashMap<String, String>();
	Map<String,String> entityToClass = new HashMap<String, String>();

	SpecificActionLoader() {
		for (int i = 0; i < SPECIFIC_ACTIONS.length; i++) {
			String[] action = SPECIFIC_ACTIONS[i];
			classToEntity.put(action[0], action[1]);
			entityToClass.put(action[1], action[0]);
		}
	}

	public boolean isPreActionEntity(XModelObject object) {
		String entityName = object.getModelEntity().getName();
		return isPreActionEntity(entityName);		
	}

	public boolean isPreActionEntity(String entity) {
		return entity.startsWith(PREACTION_PREFIX);
	}

	public boolean isActionsFolder(String entity) {
		return entity.startsWith(ACTIONS_FOLDER_ENTITY);
	}

	private String addSuffix(String entityName, XModelObject actions) {
		for (String suff: KNOWN_SUFFIXES) {
			if(actions.getModelEntity().getChild(entityName + suff) != null) {
				return entityName + suff;
			}
		}
		return entityName;
	}

	public void convertChildrenToSpecific(XModelObject actions) {
		if(!isActionsFolder(actions.getModelEntity().getName())) return;

		boolean modified = false;

		XModelObject[] as = actions.getChildren();
		for (int i = 0; i < as.length; i++) {
			String cls = as[i].getAttributeValue("class");
			if(cls == null) continue;
			String entityName = classToEntity.get(cls);
			if(entityName == null) continue;
			entityName = addSuffix(entityName, actions);
			XModelObject action = convertBasicActionToSpecific(as[i], entityName);
			if(action != null) {
				as[i] = action;
				modified = true;
			}
		}
		if(modified) {
			((RegularObjectImpl)actions).replaceChildren(as);
		}
		
	}

	public XModelObject convertBasicActionToSpecific(XModelObject basic, String entityName) {
		XModelEntity entity = basic.getModelEntity().getMetaModel().getEntity(entityName);
		if(entity == null) return null;;
		XModelObject action = basic.getModel().createModelObject(entityName, null);
		try {
			XModelObjectLoaderUtil.mergeAttributes(action, basic);
		} catch (XModelException e) {
			ESBCorePlugin.log(e);
		}
		XAttribute[] as = entity.getAttributes();
		for (int i = 0; i < as.length; i++) {
			String pre = as[i].getProperty("pre");
			if(pre == null || pre.length() == 0) continue;
			if("true".equals(pre)) {
				String name = as[i].getXMLName();
				XModelObject p = basic.getChildByPath(name);
				if(p == null) continue;
				String value = p.getAttributeValue("value");
				action.setAttributeValue(as[i].getName(), value);
				action.set(as[i].getXMLName() + ".#comment", p.getAttributeValue("comment"));
				p.removeFromParent();
			} else {
				//very specific cases
			}
		}

		XChild[] ce = action.getModelEntity().getChildren();
		for (int i = 0; i < ce.length; i++) {
			String childEntityName = ce[i].getName();
			if(ESBConstants.ENT_ESB_PROPERTY.equals(childEntityName)) continue;
			XModelEntity childEntity = action.getModelEntity().getMetaModel().getEntity(childEntityName);
			if(childEntity == null) continue;
			String converter = childEntity.getProperty("converter");
			if("alias".equals(converter)) {
				new AliasConverter().toSpecific(basic, action);
			} else if("route".equals(converter)) {
				new RouteToConverter().toSpecific(basic, action);
			} else if("path".equals(converter)) {
				new ObjectPathConverter().toSpecific(basic, action);
			} else if("notification".equals(converter)) {
				new NotificationConverter().toSpecific(basic, action);
			}
		}
		
		XModelObject[] cs = basic.getChildren(ESBConstants.ENT_ESB_PROPERTY);
		for (int i = 0; i < cs.length; i++) {
			action.addChild(cs[i]);
		}
		
		return action;
	}

	public XModelObject convertSpecificActionToBasic(XModelObject action) {
		String entityName = action.getModelEntity().getName();
		if(!isPreActionEntity(entityName)) return action;

		String basicActionEntity = addSuffix(ACTION_ENTITY, action.getParent());
		
		XModelObject result = action.getModel().createModelObject(basicActionEntity, null);
		try {
			XModelObjectLoaderUtil.mergeAttributes(result, action);
		} catch (XModelException e) {
			ESBCorePlugin.log(e);
		}
		
		XModelEntity entity = action.getModelEntity();
		XAttribute[] as = entity.getAttributes();
		for (int i = 0; i < as.length; i++) {
			String pre = as[i].getProperty("pre");
			if(pre == null || pre.length() == 0) continue;
			if("true".equals(pre)) {
				String value = action.getAttributeValue(as[i].getName());
				if(value == null || value.length() == 0 || value.equals(as[i].getDefaultValue())) {
					if(!"always".equals(as[i].getProperty("save"))) continue;
				}
				XModelObject p = action.getModel().createModelObject(ESBConstants.ENT_ESB_PROPERTY, null);
				p.setAttributeValue("name", as[i].getXMLName());
				p.setAttributeValue("value", value);
				p.setAttributeValue("comment", action.get(as[i].getXMLName() + ".#comment"));
				result.addChild(p);
			} else {
				//very specific cases
			}
		}

		XChild[] ce = entity.getChildren();
		for (int i = 0; i < ce.length; i++) {
			String childEntityName = ce[i].getName();
			if(ESBConstants.ENT_ESB_PROPERTY.equals(childEntityName)) continue;
			XModelEntity childEntity = entity.getMetaModel().getEntity(childEntityName);
			if(childEntity == null) continue;
			String converter = childEntity.getProperty("converter");
			if("alias".equals(converter)) {
				new AliasConverter().toBasic(result, action);
			} else if("route".equals(converter)) {
				new RouteToConverter().toBasic(result, action);
			} else if("path".equals(converter)) {
				new ObjectPathConverter().toBasic(result, action);
			} else if("notification".equals(converter)) {
				new NotificationConverter().toBasic(result, action);
			}
		}
		
		XModelObject[] cs = action.getChildren(ESBConstants.ENT_ESB_PROPERTY);
		for (int i = 0; i < cs.length; i++) {
			result.addChild(cs[i].copy());
		}
		return result;
	}
}
