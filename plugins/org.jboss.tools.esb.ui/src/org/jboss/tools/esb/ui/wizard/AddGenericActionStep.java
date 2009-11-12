package org.jboss.tools.esb.ui.wizard;

import java.beans.PropertyChangeEvent;

import org.jboss.tools.common.model.ui.wizards.special.SpecialWizardStep;
import org.jboss.tools.esb.ui.editor.form.ESBActionForm;

public class AddGenericActionStep extends SpecialWizardStep {
	boolean lock = false;

	public AddGenericActionStep() {}

	public void propertyChange(PropertyChangeEvent event) {
		if(lock) return;
		if(event.getSource() == attributes.getPropertyEditorAdapterByName("class")) { //$NON-NLS-1$
			lock = true;
			try {
				ESBActionForm.setProcessMethods(attributes, support.getTarget());
			} finally {
				lock = false;
			}
		}
		super.propertyChange(event);
	}
}
