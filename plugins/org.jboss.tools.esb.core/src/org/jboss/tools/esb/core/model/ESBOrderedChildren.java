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
import org.jboss.tools.common.model.impl.GroupOrderedChildren;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBOrderedChildren extends GroupOrderedChildren {

    protected int getGroupCount() {
        return 3;
    }

    protected int getGroup(XModelObject o) {
    	if(o != null && ESBConstants.ENT_ESB_PROPERTY.equals(o.getModelEntity().getName())) {
    		return 0;
    	}
    	if("ESBJBRConfig".equals(o.getModelEntity().getName())) {
    		return 1;
    	}
        return 2;
    }

}
