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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

/**
 * @author Grid Qian
 */
public class ButtonFieldEditor extends BaseFieldEditor {

	PushButtonField button= null;
	int style;
	
	private ButtonPressedAction buttonAction = new ButtonPressedAction(JBossESBUIMessages.JBoss_Button_Field_Editor_Browse) {
		@Override
		public void run() {
			throw new RuntimeException(JBossESBUIMessages.Error_JBoss_Button_Field_Editor_Not_Implemented_Yet);
		}
	};
	
	public ButtonFieldEditor(String name, String label, int style) {
		super(name, label, new Object());
		this.style = style;
	}
	
	public ButtonFieldEditor(String name, ButtonPressedAction action, Object defaultValue) {
		super(name, action.getText(), defaultValue);
		buttonAction = action;
		buttonAction.setFieldEditor(this);
	}

	@Override
	public void doFillIntoGrid(Object parent) {
	}

	@Override
	public Object[] getEditorControls() {
		if(button==null) {
			return null;
		}
		return new Control[]{button.getControl()};
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	public void save(Object object) {
	}

	@Override
	public void setEditable(boolean ediatble) {
	}

	@Override
	public Object[] getEditorControls(Object composite) {
		if(button==null && composite!=null) {
			button = new PushButtonField((Composite)composite, style, buttonAction);
			setEnabled(isEnabled());
		}
		return new Control[]{button.getControl()};
	}

	public ButtonPressedAction getButtonaction() {
		return buttonAction;
	}

	public static class ButtonPressedAction extends Action implements SelectionListener{

		private IFieldEditor editor = null;

		public ButtonPressedAction(String label) {
			super(label);
		}
		
		public void setFieldEditor(IFieldEditor newEditor) {
			editor = newEditor;
		}
		
		public IFieldEditor getFieldEditor() {
			return editor;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		public void widgetSelected(SelectionEvent e) {
				run();
		}
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}
}