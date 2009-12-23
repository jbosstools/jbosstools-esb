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
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.esb.core.model.ESBCustomizedObjectImpl;
import org.jboss.tools.esb.core.model.converters.FTPListenerConverter;

/**
 * @author Viacheslav Kabanovich
 */
public class FTPMessageFilterImpl extends ESBCustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public FTPMessageFilterImpl() {}

	public String setAttributeValue(String name, String value) {
		if("read only".equals(name) && isActive()) {
			String ov = getAttributeValue("read only");
			boolean changed = (value != null && !value.equals(ov));
			String result = super.setAttributeValue(name, value);
			if(changed) {
				XModelObject p = getParent();
				if(p != null) {
			        XModelImpl m = (XModelImpl)getModel();
			        m.fireStructureChanged(p, XModelTreeEvent.STRUCTURE_CHANGED, p);
				}
			}
			return result;
		} else {		
			return super.setAttributeValue(name, value);
		}
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) throws XModelException {
		if("read only".equals(name)) {
			XModelObject p = getParent();
			if(p != null) {
				if("true".equals(newValue)) {
					FTPListenerConverter.instance.toSpecificImpl(p, p);
				} else if("true".equals(oldValue)) {
					FTPListenerConverter.instance.toBasicImpl(p, p);
				}
			}
		}
	}

}
