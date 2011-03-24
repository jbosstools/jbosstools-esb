package org.jboss.tools.esb.core.model.impl;

/**
 * It seems that all ESB action classes extending ContentBasedRouter use the same set of attributes.
 * It means that we may not need specific implementations for the inherited actions.
 *  
 * @author slava
 *
 */

public class BusinessRulesProcessor extends ContentBasedRouter {
	private static final long serialVersionUID = 1L;

	public BusinessRulesProcessor() {}

}
