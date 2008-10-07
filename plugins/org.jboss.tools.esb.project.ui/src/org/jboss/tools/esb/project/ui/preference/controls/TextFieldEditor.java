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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

/**
 * 
 * @author Grid Qian
 *
 */
public class TextFieldEditor extends BaseFieldEditor implements PropertyChangeListener{
	
	public static final int UNLIMITED = -1;
	
	protected int style = -1;
	
	/**
	 * 
	 * @param name
	 * @param aLabelText
	 * @param defaultvalue
	 */
	public TextFieldEditor(String name,String aLabelText,String defaultvalue) {
		super(name, aLabelText, defaultvalue);
	}
	
	/**
	 * 
	 * @param name
	 * @param aLabelText
	 * @param defaultvalue
	 * @param editable
	 */
	public TextFieldEditor(String name,String aLabelText,String defaultvalue,boolean editable) {
		super(name, aLabelText, defaultvalue);
		setEditable(editable);
	}	
	
	protected TextField  fTextField = null;
	
	protected int fWidthInChars = 0;

	@Override
	public Object[] getEditorControls() {
		return new Control[] {getTextControl()};
	}

	@Override
	public void doFillIntoGrid(Object aParent) {
		Assert.isTrue(aParent instanceof Composite, JBossESBUIMessages.Error_JBoss_Basic_Editor_Composite);
		Assert.isTrue(((Composite)aParent).getLayout() instanceof GridLayout,JBossESBUIMessages.Error_JBoss_Basic_Editor_Support);
		Composite aComposite = (Composite) aParent;
		getEditorControls(aComposite);
		GridLayout gl = (GridLayout)((Composite)aParent).getLayout();
		getTextControl(aComposite);

        GridData gd = new GridData();
        
        gd.horizontalSpan = gl.numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
        fTextField.getTextControl().setLayoutData(gd);
	}

    /**
     * 
     * @param parent
     * @return
     */
    public Text getTextControl(Composite parent) {
        if (fTextField == null) {
        	fTextField = new TextField(parent, getInitialStyle());
            Text textField = fTextField.getTextControl();
            textField.setFont(parent.getFont());
            textField.setText(getValue().toString());
            textField.setEditable(isEditable());
            textField.setEnabled(isEnabled());
            fTextField.addPropertyChangeListener(this);
        } else if (parent!=null){
        	Assert.isTrue(parent==fTextField.getTextControl().getParent());
        }  
        return fTextField.getTextControl();
    }
    
	protected void updateWidgetValues() {
		setValueAsString(getValueAsString());
	}

	protected int getInitialStyle() {
		if(this.style >= 0) return style;
    	return SWT.SINGLE | SWT.BORDER;
    }

    /*
     * @param value
     * @return 
     */
    @SuppressWarnings({ "unchecked", "unused" })
	private String checkCollection(Object value){
    	
    	return value != null && (((Collection)value).size() > 0) ? prepareCollectionToString((Collection)value) : ""; //$NON-NLS-1$
    }
    
    /*
     * @param collection
     * @return 
     */
    @SuppressWarnings("unchecked")
	private String prepareCollectionToString(Collection collection)
    {
    	String stringValue = ""; //$NON-NLS-1$
    	Object[] objects = collection.toArray();
    	for(int i = 0; i < objects.length; i++){
    		stringValue += objects[i];
    		if(i < objects.length - 1)
    			stringValue += " "; //$NON-NLS-1$
    	}
    	return stringValue;
    }
    
    
    /*
     * @param value
     * @return 
     */
    @SuppressWarnings("unused")
	private String checkSimple(Object value){
    	return (value != null) ? value.toString() : ""; //$NON-NLS-1$
    }
    
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
     * Returns this field editor's text control.
     *
     * @return the text control, or <code>null</code> if no
     * text field is created yet
     */
    protected Text getTextControl() {
        return fTextField!=null?fTextField.getTextControl():null;
    }

    @Override
	public boolean setFocus() {
    	boolean setfocus = false;
        if(fTextField!=null && !fTextField.getTextControl().isDisposed())
        	setfocus = fTextField.getTextControl().setFocus();
        return setfocus;
    }

	@Override
	public Object[] getEditorControls(Object composite) {
		return new Control[]{getTextControl((Composite)composite)};
	}

	/**
	 * 
	 * @param object
	 */
	public void save(Object object) {
	}

	/**
	 * 
	 */
	@Override
	public void setValue(Object newValue) {
		super.setValue(newValue);
		if(fTextField!=null){
			fTextField.removePropertyChangeListener(this);
			fTextField.getTextControl().setText(newValue.toString());
			fTextField.addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void setEditable(boolean aEditable) {
		super.setEditable(aEditable);
		if(getTextControl()!=null) getTextControl().setEditable(aEditable);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		super.setValue(evt.getNewValue());
	}
}