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

import org.jboss.tools.common.model.XFilteredTreeConstraint;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBTreeConstraint implements XFilteredTreeConstraint {

	public boolean accepts(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if("ESBFTPCache".equals(entity) || "ESBFTPRemote".equals(entity)) {
			XModelObject p = object.getParent();
			if(p != null) {
				XModelObject filter = p.getChildByPath("Filter");
				if(filter != null && !"true".equals(filter.getAttributeValue("read only"))) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isHidingAllChildren(XModelObject object) {
		return false;
	}

	public boolean isHidingSomeChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if("ESBFTPListener120".equals(entity)) {
			return true;
		}
		return false;
	}

	public void update(XModel model) {
	}

}
