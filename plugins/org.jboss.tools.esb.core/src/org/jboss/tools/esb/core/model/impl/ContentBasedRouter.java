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
package org.jboss.tools.esb.core.model.impl;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.esb.core.model.ESBCustomizedObjectImpl;

/**
 * @author Viacheslav Kabanovich
 */
public class ContentBasedRouter extends ESBCustomizedObjectImpl {
	private static final long serialVersionUID = 1L;
	public static String ATTR_CBR_ALIAS = "cbr alias";
	public static String ATTR_RULE_LANGUAGE = "rule language";
	public static String ATTR_RULE_SET = "rule set";
	public static String ATTR_RULE_RELOAD = "rule reload";

	public static String ATTR_RULE_AGENT_PROPERTIES = "rule agent properties";
	public static String ATTR_DECISION_TABLE = "decision table";
	public static String ATTR_RULE_MAX_THREADS = "rule max threads";
	public static String ATTR_RULE_MULTITHREAD_EVALUATION = "rule multithread evaluation";

	static int RULE_SET_BASED = 0;
	static int RULE_AGENT_BASED = 1;
	static int DECISION_TABLE_BASED = 2;

	public ContentBasedRouter() {}

	@Override
	public boolean isAttributeEditable(String name) {
		if(ATTR_RULE_LANGUAGE.equals(name)) {
			String alias = getAttributeValue(ATTR_CBR_ALIAS);
			if(isRegexOrXPath(alias)) {
				return false;
			}
		} else if(ATTR_RULE_RELOAD.equals(name)) {
			String ruleSet = getAttributeValue(ATTR_RULE_SET);
			if(ruleSet == null || ruleSet.length() == 0) {
				return false;
			}
		}
	
		if(ContentBasedRouter.ATTR_RULE_SET.equals(name)) {
			int kind = getKind();
			return kind < 0 || kind == RULE_SET_BASED;			
		} else if(ATTR_RULE_AGENT_PROPERTIES.equals(name)) {
			int kind = getKind();
			return kind < 0 || kind == RULE_AGENT_BASED;			
		} else if(ATTR_DECISION_TABLE.equals(name)) {
			int kind = getKind();
			return kind < 0 || kind == DECISION_TABLE_BASED;			
		}
		if(ATTR_RULE_MAX_THREADS.equals(name)) {
			String a = getAttributeValue(ATTR_RULE_MULTITHREAD_EVALUATION);
			if("true".equals(a)) return true;
			String b = getAttributeValue(ATTR_RULE_MAX_THREADS);
			return b != null && b.length() > 0;
			
		}

		return super.isAttributeEditable(name);
	}

	private boolean isRegexOrXPath(String alias) {
		return ("Regex".equalsIgnoreCase(alias) || "Xpath".equalsIgnoreCase(alias));
	}

	@Override
	protected void onAttributeValueEdit(String name, String oldValue,
			String newValue) throws XModelException {
		if(ATTR_CBR_ALIAS.equals(name)) {
			if(isRegexOrXPath(newValue)) {
				setAttributeValue(ATTR_RULE_LANGUAGE, "");
			}
		} else if(ATTR_RULE_SET.equals(name)) {
			if(newValue == null || newValue.length() == 0) {
				setAttributeValue(ATTR_RULE_RELOAD, getModelEntity().getAttribute(ATTR_RULE_RELOAD).getDefaultValue());
			}
		}
	}

	int getKind() {
		String ruleSet = getAttributeValue(ContentBasedRouter.ATTR_RULE_SET);
		if(ruleSet != null && ruleSet.length() > 0) return RULE_SET_BASED;
		String ruleAgent = getAttributeValue(ATTR_RULE_AGENT_PROPERTIES);
		if(ruleAgent != null && ruleAgent.length() > 0) return RULE_AGENT_BASED;
		String decisionTable = getAttributeValue(ATTR_DECISION_TABLE);
		if(decisionTable != null && decisionTable.length() > 0) return DECISION_TABLE_BASED;
		return -1;
	}
	
}
