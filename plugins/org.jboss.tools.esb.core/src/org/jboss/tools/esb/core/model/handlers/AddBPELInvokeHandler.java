package org.jboss.tools.esb.core.model.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class AddBPELInvokeHandler extends DefaultCreateHandler {

    public boolean isEnabled(XModelObject object) {
    	if(super.isEnabled(object)) {
    		IResource resource = EclipseResourceUtil.getResource(object);
    		if(resource != null) {
    			IProject project = resource.getProject();
    			IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
    			if(jp != null) {
    				try {
    					IType t = EclipseJavaUtil.findType(jp, "org.jboss.soa.esb.actions.bpel.BPELInvoke");
    					return t != null;
    				} catch (JavaModelException e) {
    					//ignore
    				}
    			}
    		}
    	}
    	return false;
    }

}
