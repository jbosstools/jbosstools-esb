package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.meta.action.impl.WizardDataValidator;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateSupport;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.esb.core.model.impl.ContentBasedRouter;

public class AddContentBasedRouterSupport extends DefaultCreateSupport {

	public AddContentBasedRouterSupport() {}

	protected void finish() throws XModelException {
		String entity = getEntityName();
		Properties p = extractStepData(0);
		String cbrAlias = action.getProperty("cbrAlias");
		if(cbrAlias != null) {
			p.setProperty(ContentBasedRouter.ATTR_CBR_ALIAS, cbrAlias);
		}
		String ruleSet = p.getProperty(ContentBasedRouter.ATTR_RULE_SET);
		if(ruleSet == null || ruleSet.length() == 0) {
			p.remove(ContentBasedRouter.ATTR_RULE_RELOAD);
		}
		XModelObject action = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		DefaultCreateHandler.addCreatedObject(getTarget(), action, getProperties());
	}

    public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
    	if(ContentBasedRouter.ATTR_RULE_RELOAD.equals(name)) {
    		String ruleSet = values.getProperty(ContentBasedRouter.ATTR_RULE_SET);
    		if(ruleSet == null || ruleSet.trim().length() == 0) {
    			return false;
    		}
    	} else if(ContentBasedRouter.ATTR_RULE_LANGUAGE.equals(name)) {
    		String alias = values.getProperty(ContentBasedRouter.ATTR_CBR_ALIAS);
    		if(alias == null) alias = action.getProperty("cbrAlias");
    		if(isRegexOrXPathOrEmpty(alias)) {
    			return false;
    		}
    	}
    	return true;
    }
   
    private boolean isRegexOrXPathOrEmpty(String alias) {
    	return (alias == null || alias.length() == 0 || "Regex".equalsIgnoreCase(alias) || "Xpath".equalsIgnoreCase(alias));
    }
    
    protected DefaultWizardDataValidator cbrValidator = new CBRValidator();
    
    public WizardDataValidator getValidator(int step) {
    	cbrValidator.setSupport(this, step);
		return cbrValidator;    	
    }
    
    class CBRValidator extends DefaultWizardDataValidator {
    	public void validate(Properties data) {
    		message = null;
    		super.validate(data);
    		if(message != null) return;
    		String alias = data.getProperty(ContentBasedRouter.ATTR_CBR_ALIAS);
    		if("Drools".equalsIgnoreCase(alias)) {
    			String ruleSet = data.getProperty(ContentBasedRouter.ATTR_RULE_SET);
    			if(ruleSet == null || ruleSet.length() == 0) {
    				message = "Rule Set must be set for Drools Router";
    				return;
    			}
    		}
    	}
    }
    
}
