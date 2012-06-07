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
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.common.model.impl.OrderedByEntityChildren;
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
    		return new OrderedByEntityChildren();
    	}
    	if(children != null && children.equals("%ESBOrdered%")) { //$NON-NLS-1$
    		return new ESBOrderedChildren();
    	}
    	return super.createChildren();    	
    }

    public boolean isAttributeEditable(String name) {
    	if(ESBConstants.ATTR_MESSAGE_FLOW_PRIORITY.equals(name)) {
    		String entity = getModelEntity().getName();
    		if(entity.startsWith(ESBConstants.ENT_ESB_SERVICE)) {
    			XModelObject as = getChildByPath("Actions");
    			if(as != null) {
    				String inxsd = as.getAttributeValue("in xsd");
    				if(inxsd == null || inxsd.length() == 0 || "false".equals(as.getAttributeValue("webservice"))) {
    					return false;
    				}
    			}
    		}
    		if(getModelEntity().getAttribute("is gateway") != null && !"true".equals(getAttributeValue("is gateway"))) {
    			return false;
    		}
    		XModelObject p = FileSystemsHelper.getFile(this);
    		if(p != null) {
    			String fileEntity = p.getModelEntity().getName();
    			if(ESBConstants.ENT_ESB_FILE_101.equals(fileEntity)
    				|| ESBConstants.ENT_ESB_FILE_110.equals(fileEntity)
    				|| ESBConstants.ENT_ESB_FILE_120.equals(fileEntity)
    				|| ESBConstants.ENT_ESB_FILE_130.equals(fileEntity)) {
    				return false;
    			}
    		}
    	}
    	return super.isAttributeEditable(name);
    }

}
