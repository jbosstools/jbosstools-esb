package org.jboss.tools.esb.core.model.converters;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.esb.core.model.SpecificActionLoader;

public class FTPListenerConverter implements IPropertyConverter {
	
	public FTPListenerConverter() {}

	public void toBasic(XModelObject basic, XModelObject specific) {
		XModelObject cache = specific.getChildByPath("Cache");
		SpecificActionLoader.copySpecificAtttributesToBasicProperties(cache, basic);

		XModelObject remote = specific.getChildByPath("Remote Filesystem Strategy");
		SpecificActionLoader.copySpecificAtttributesToBasicProperties(remote, basic);
	}

	public void toSpecific(XModelObject basic, XModelObject specific) {
		XModelObject cache = specific.getChildByPath("Cache");
		SpecificActionLoader.copyBasicPropertiesToSpecificAtttributes(basic, cache);

		XModelObject remote = specific.getChildByPath("Remote Filesystem Strategy");
		SpecificActionLoader.copyBasicPropertiesToSpecificAtttributes(basic, remote);
	}

}
