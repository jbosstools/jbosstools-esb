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
package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AddScheduleProviderSupport extends SpecialWizardSupport {
	String providerEntity;
	
	public AddScheduleProviderSupport() {}

    protected void reset() {
    	providerEntity = getEntityData()[0].getModelEntity().getName();
    }

    public String[] getActionNames(int stepId) {
    	if(getStepId() < getEntityData().length - 1) {
    		if(getStepId() == 0) {
    			return new String[]{NEXT, CANCEL};
    		} else {
    			return new String[]{BACK, NEXT, CANCEL};
    		}
    	} else if(getEntityData().length > 0) {
			return new String[]{BACK, FINISH, CANCEL};
    	}
        return new String[]{FINISH, CANCEL, HELP};
    }
    
    public boolean isActionEnabled(String name) {
    	if(FINISH.equals(name) && getStepId() < getEntityData().length - 1) {
    		return false;
    	}
        return true;
    }
    
    @Override
	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(BACK.equals(name)) {
			if(getStepId() == 0) return;
			setStepId(getStepId() - 1);
		} else if(NEXT.equals(name)) {
			if(getStepId() >= getEntityData().length - 1) return;
			setStepId(getStepId() + 1);
		} else if(HELP.equals(name)) {
			help();
		}
	}
	
	protected void execute() throws XModelException {
		Properties p0 = extractStepData(0);
		XModelObject provider = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), providerEntity, p0);
		
		Properties p1 = extractStepData(1);
		String scheduleEntity = getScheduleEntity(provider, p1);
		if(scheduleEntity != null) {
			XModelObject bus = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), scheduleEntity, p1);
			provider.addChild(bus);
		}
		
		DefaultCreateHandler.addCreatedObject(getTarget(), provider, FindObjectHelper.EVERY_WHERE);
	}
	
	private String getScheduleEntity(XModelObject provider, Properties p1) {
		String scheduleKind = p1.getProperty("schedule kind");
		XChild[] cs = provider.getModelEntity().getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].getName().indexOf(scheduleKind) >= 0) return cs[i].getName();
		}
		return null;
	}

    public boolean canBeProcessedByStandardWizard() {
    	return true;
    }

}
