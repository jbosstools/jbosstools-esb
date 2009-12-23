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
public class FTPListenerConverter implements IPropertyConverter {
	
	public FTPListenerConverter() {}

	public void toBasic(XModelObject basic, XModelObject specific) {
		XModelObject filter = specific.getChildByPath("Filter");
		if(filter != null && !"true".equals(filter.getAttributeValue("read only"))) {
			return;
		}

		XModelObject cache = specific.getChildByPath("Cache");
		if(cache != null) {
			SpecificActionLoader.copySpecificAtttributesToBasicProperties(cache, basic);
		}

		XModelObject remote = specific.getChildByPath("Remote Filesystem Strategy");
		if(remote != null) {
			SpecificActionLoader.copySpecificAtttributesToBasicProperties(remote, basic);
		}
	}

	public void toSpecific(XModelObject basic, XModelObject specific) {
		XModelObject filter = specific.getChildByPath("Filter");
		if(filter != null && !"true".equals(filter.getAttributeValue("read only"))) {
			return;
		}
	
		XModelObject cache = specific.getChildByPath("Cache");
		if(cache != null) {
			SpecificActionLoader.copyBasicPropertiesToSpecificAtttributes(basic, cache);
		}

		XModelObject remote = specific.getChildByPath("Remote Filesystem Strategy");
		if(remote != null) {
			SpecificActionLoader.copyBasicPropertiesToSpecificAtttributes(basic, remote);
		}
	}

}
