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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Grid Qian
 */
public class LabelFieldEditor extends BaseFieldEditor {

	public LabelFieldEditor(String name, String label) {
		super(name, label, ""); //$NON-NLS-1$
	}

	@Override
	public void doFillIntoGrid(Object parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getEditorControls(Object composite) {
		// TODO Auto-generated method stub
		return new Control[]{createLabelControl((Composite)composite)};
	}

	@Override
	public Object[] getEditorControls() {
		return getEditorControls(null);
	}

	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void save(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEditable(boolean ediatble) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setValue(Object value) {
	}

	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 1;
	}
}
