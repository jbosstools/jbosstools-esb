package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateSupport;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class AddContentBasedRouterSupport extends DefaultCreateSupport {

	public AddContentBasedRouterSupport() {}

	protected void finish() throws XModelException {
		String entity = getEntityName();
		Properties p = extractStepData(0);
		String cbrAlias = action.getProperty("cbrAlias");
		if(cbrAlias != null) {
			p.setProperty("cbr alias", cbrAlias);
		}
		XModelObject action = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		DefaultCreateHandler.addCreatedObject(getTarget(), action, getProperties());
	}

}
