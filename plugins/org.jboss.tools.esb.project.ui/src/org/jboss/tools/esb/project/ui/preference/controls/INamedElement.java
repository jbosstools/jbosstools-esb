/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.esb.project.ui.preference.controls;

/**
 * @author Grid Qian
 */
public interface INamedElement {

	public abstract String getName();

	public abstract Object getValue();

	public abstract void setValue(Object newValue);

	public abstract String getValueAsString();
	
	public abstract void setValueAsString(String aValue);

}