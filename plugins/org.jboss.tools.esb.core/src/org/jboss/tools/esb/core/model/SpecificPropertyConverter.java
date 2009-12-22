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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.esb.core.model.converters.FTPListenerConverter;
import org.jboss.tools.esb.core.model.converters.IPropertyConverter;

/**
 * When loading from xml, reads 'raw' list of esb property children of loaded object 
 * and converts them into 'rich' specific properties (attributes or children).
 * Since just loaded objects are not yet used anywhere, there is no need to make a copy.
 * 
 * When saving object to xml, converts 'rich' properties into basic esb properties
 * and adds them as children; this is done with copy object, so that leave working 
 * model object unmodified.
 * 
 * Contrary to conversion of actions, no entity change occurs.
 * 
 * @author Viacheslav Kabanovich
 */
public class SpecificPropertyConverter implements ESBConstants {
	public static SpecificPropertyConverter instance = new SpecificPropertyConverter();

	public SpecificPropertyConverter() {}

	public boolean isListenersFolder(String entity) {
		return entity.startsWith(ENT_ESB_LISTENERS);
	}

	public void convertChildrenToSpecific(XModelObject actions) {
		boolean modified = false;

		XModelObject[] as = actions.getChildren();
		for (int i = 0; i < as.length; i++) {
			XModelObject action = convertBasicToSpecific(actions, as[i]);
			if(action != null) {
				as[i] = action;
				modified = true;
			}
		}
		if(modified) {
			((RegularObjectImpl)actions).replaceChildren(as);
		}
		
	}

	public XModelObject convertBasicToSpecific(XModelObject parent, XModelObject basic) {
		String entity = basic.getModelEntity().getName();
		if("ESBFTPListener120".equals(entity)) {
			IPropertyConverter converter = new FTPListenerConverter();
			converter.toSpecific(basic, basic);
		}
		//returns null because no new object is created
		return null;
	}

	public XModelObject convertSpecificToBasic(XModelObject specific) {
		String entity = specific.getModelEntity().getName();
		XModelObject basic = specific;
		if("ESBFTPListener120".equals(entity)) {
			basic = basic.copy();
			IPropertyConverter converter = new FTPListenerConverter();
			converter.toBasic(basic, specific);
		}
		return basic;
	}

}
