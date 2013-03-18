/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.core.runtime;

public interface IRuntimeManagerListener {
	/**
	 * A runtime has been added to the model
	 * @param rt
	 */
	public void runtimeAdded(JBossESBRuntime rt);
	
	/**
	 * A runtime has been removed from the model
	 * @param rt
	 */
	public void runtimeRemoved(JBossESBRuntime rt);
	
}
