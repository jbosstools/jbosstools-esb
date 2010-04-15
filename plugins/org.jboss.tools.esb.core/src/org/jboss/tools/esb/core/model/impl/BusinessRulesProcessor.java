package org.jboss.tools.esb.core.model.impl;

import org.jboss.tools.esb.core.model.ESBCustomizedObjectImpl;

public class BusinessRulesProcessor extends ESBCustomizedObjectImpl {
	private static final long serialVersionUID = 1L;
	public static String ATTR_RULE_AGENT_PROPERTIES = "rule agent properties";
	public static String ATTR_DECISION_TABLE = "decision table";

	static int RULE_SET_BASED = 0;
	static int RULE_AGENT_BASED = 1;
	static int DECISION_TABLE_BASED = 2;

	public BusinessRulesProcessor() {}

	@Override
	public boolean isAttributeEditable(String name) {
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
		return super.isAttributeEditable(name);
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
