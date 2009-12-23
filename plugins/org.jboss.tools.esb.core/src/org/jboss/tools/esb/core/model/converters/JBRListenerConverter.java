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
package org.jboss.tools.esb.core.model.converters;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.esb.core.model.SpecificActionLoader;

/**
 * @author Viacheslav Kabanovich
 */
public class JBRListenerConverter implements IPropertyConverter {
	public static JBRListenerConverter instance = new JBRListenerConverter();
	
	public JBRListenerConverter() {}

	public void toBasic(XModelObject basic, XModelObject specific) {
		XModelObject config = specific.getChildByPath("Config");
		if(config != null) {
			SpecificActionLoader.copySpecificAtttributesToBasicProperties(config, basic);
		}
	}

	public void toSpecific(XModelObject basic, XModelObject specific) {
		XModelObject config = specific.getChildByPath("Config");
		if(config != null) {
			SpecificActionLoader.copyBasicPropertiesToSpecificAtttributes(basic, config);
		}
	}

}
