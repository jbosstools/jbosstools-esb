package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.esb.core.model.impl.ContentBasedRouter;

public class AddRouteToForCBRHandler extends AbstractHandler {

	public AddRouteToForCBRHandler() {}

	@Override
	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	@Override
	public void executeHandler(XModelObject object, Properties p)
			throws XModelException {
		String s = object.getAttributeValue(ContentBasedRouter.ATTR_RULE_SET);
		if(s != null && s.trim().length() > 0) {
			XActionInvoker.invoke("CreateActions.AddRouteToWithRuleSet", object, p);
		} else {
			XActionInvoker.invoke("CreateActions.AddRouteToWithoutRuleSet", object, p);
		}
	}

}
