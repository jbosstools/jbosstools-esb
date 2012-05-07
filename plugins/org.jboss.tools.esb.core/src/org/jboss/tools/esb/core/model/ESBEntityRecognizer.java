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


import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;

/**
 * @author Viacheslav Kabanovich
 */
public class ESBEntityRecognizer implements EntityRecognizer, ESBConstants {

    public ESBEntityRecognizer() {}

    public String getEntityName(EntityRecognizerContext context) {
    	return getEntityName(context.getExtension(), context.getBody());
    }

    String getEntityName(String ext, String body) {
        if(body == null) return null;

        String suffix = getSuffixBySchema(body);
    	if(suffix == null) {
    		return null;
    	}
    	
        return ENT_ESB_FILE + suffix;
    }
    
    private String getSuffixBySchema(String body) {
    	int i = body.indexOf("<jbossesb"); //$NON-NLS-1$
    	if(i < 0) return null;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	String s = body.substring(i, j);
    	i = s.indexOf("xmlns="); //$NON-NLS-1$
    	if(i < 0) return null;
    	j = s.indexOf("\"", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	int k = s.indexOf("\"", j + 1); //$NON-NLS-1$
    	if(k < 0) return null;
    	String schema = s.substring(j + 1, k);
    	return schema.equals(SCHEMA_101) ? SUFF_101 :
    		   schema.equals(SCHEMA_110) ? SUFF_110 :
    		   schema.equals(SCHEMA_120) ? SUFF_120 :
    		   schema.equals(SCHEMA_130) ? SUFF_130 :
       		   schema.equals(SCHEMA_131) ? SUFF_131 :
    		   schema.equals(NEW_SCHEMA_101) ? SUFF_101 :
    		   schema.equals(NEW_SCHEMA_110) ? SUFF_110 :
			   schema.equals(NEW_SCHEMA_120) ? SUFF_120 :
			   schema.equals(NEW_SCHEMA_130) ? SUFF_130 :
			   schema.equals(NEW_SCHEMA_131) ? SUFF_131 :
			   //Return the latest supported for unknown version
			   schema.startsWith(SCHEMA_PREFIX) ? SUFF_131 :
			   schema.startsWith(NEW_SCHEMA_PREFIX) ? SUFF_131 :
    		   null;
    }
    
}
