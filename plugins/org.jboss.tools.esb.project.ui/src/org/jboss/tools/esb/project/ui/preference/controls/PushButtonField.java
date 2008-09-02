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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.esb.project.ui.preference.controls.ButtonFieldEditor.ButtonPressedAction;

/**
 * @author Grid Qian
 *
 */
public class PushButtonField extends BaseField {
	
	Button button;
	
	/**
	 * 
	 */
	@Override
	public Control getControl() {
		return button;
	}


	public PushButtonField(Composite composite, int style, ButtonPressedAction listener) {
		button = new Button(composite, style);
		button.setText(listener.getText());
		button.addSelectionListener(listener);
	}
}
