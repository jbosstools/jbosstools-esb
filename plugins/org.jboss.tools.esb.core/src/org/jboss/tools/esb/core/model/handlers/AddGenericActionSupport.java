package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateSupport;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.esb.core.model.SpecificActionLoader;

public class AddGenericActionSupport extends DefaultCreateSupport {

    public String getStepImplementingClass(int stepId) {
        return "org.jboss.tools.esb.ui.wizard.AddGenericActionStep"; //$NON-NLS-1$
    }

	protected void finish() throws XModelException {
		String entity = getEntityName();
		Properties p = extractStepData(0);
		XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		XModelObject action = SpecificActionLoader.instance.convertBasicActionToSpecific(getTarget(), c);
		if(action == null) action = c;
		DefaultCreateHandler.addCreatedObject(getTarget(), action, getProperties());
	}
}
