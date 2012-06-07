/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.core.model.converters;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.esb.core.model.ESBConstants;
import org.jboss.tools.esb.core.model.SpecificActionLoader;

/**
 * @author Viacheslav Kabanovich
 */
public class MessageFlowPriorityConverter implements IPropertyConverter {
	public static MessageFlowPriorityConverter instance = new MessageFlowPriorityConverter();
	
	public MessageFlowPriorityConverter() {}

	public void toBasic(XModelObject basic, XModelObject specific) {
		XAttribute a = specific.getModelEntity().getAttribute(ESBConstants.ATTR_MESSAGE_FLOW_PRIORITY);
		if(a != null) SpecificActionLoader.copySpecificAttributeToBasicProperty(specific, basic, a);
	}

	public void toSpecific(XModelObject basic, XModelObject specific) {
		XAttribute a = specific.getModelEntity().getAttribute(ESBConstants.ATTR_MESSAGE_FLOW_PRIORITY);
		if(a != null) SpecificActionLoader.copyBasicPropertyToSpecificAttribute(basic, specific, a);
	}

}
