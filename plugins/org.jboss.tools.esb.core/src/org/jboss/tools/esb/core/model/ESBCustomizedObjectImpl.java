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

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.common.model.impl.RegularChildren;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBCustomizedObjectImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public ESBCustomizedObjectImpl() {}
	
    protected RegularChildren createChildren() {
    	String children = getModelEntity().getProperty("children"); //$NON-NLS-1$
    	if(children != null && "%ESBOrderedService%".equals(children)) { //$NON-NLS-1$
    		return new ESBOrderedServiceChildren();
    	}
    	if(children != null && children.equals("%ESBOrdered%")) { //$NON-NLS-1$
    		return new ESBOrderedChildren();
    	}
    	return super.createChildren();    	
    }

}
