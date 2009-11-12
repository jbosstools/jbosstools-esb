package org.jboss.tools.esb.core.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateSupport;

public class AddGenericActionSupport extends DefaultCreateSupport {

    public String getStepImplementingClass(int stepId) {
        return "org.jboss.tools.esb.ui.wizard.AddGenericActionStep"; //$NON-NLS-1$
    }

}
