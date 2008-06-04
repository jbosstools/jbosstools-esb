package org.jboss.tools.esb.core.model.converters;

import org.jboss.tools.common.model.XModelObject;

public interface IPropertyConverter {
	
	public void toSpecific(XModelObject basicAction, XModelObject specificAction);

	public void toBasic(XModelObject basicAction, XModelObject specificAction);

}
