package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRedirectHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class OpenESBResourceHandler extends DefaultRedirectHandler {
	
    public boolean isEnabled(XModelObject object) {
    	if(super.isEnabled(object)) return true;
    	return object != null && object.isObjectEditable();
    }

	protected XModelObject getTrueSource(XModelObject source) {
		String attr = action.getProperty("attribute");
		String path = source.getAttributeValue(attr);
		if(path == null || path.length() == 0) return null;
		XModelObject o = source.getModel().getByPath(path);
		if(o != null) return o;
		XModelObject r = FileSystemsHelper.getWebRoot(source.getModel());
		if(r == null) return null;
		return r.getChildByPath(path);
	}

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
        if(!isEnabled(object)) return;
    	if(super.isEnabled(object)) {
    		super.executeHandler(object, p);
    	} else {
    		XModelObject r = FileSystemsHelper.getWebRoot(object.getModel());

    		IResource resource = r != null ? (IResource)r.getAdapter(IResource.class) : null;
    		if(resource == null) resource = EclipseResourceUtil.getProject(object);
    		
    		String attr = action.getProperty("attribute");
    		String path = object.getAttributeValue(attr);
    		if(path == null) return;
    		int s = path.lastIndexOf('/');
    		String folder = (s < 0) ? "" : path.substring(0, s);
    		if(folder.length() > 0 && !folder.startsWith("/")) folder = "/" + folder;
    		String fileName = (s < 0) ? path : path.substring(s + 1);
    		Properties p1 = new Properties();
    		p1.setProperty(CreateFileSupport.INITIAL_FOLDER_PROPERTY, resource.getFullPath() + folder);
    		p1.setProperty(CreateFileSupport.INITIAL_FILE_PROPERTY, fileName);
    		if(r == null) {
    			XModelObject fo = object.getParent();
    			while(fo != null && fo.getFileType() <= XModelObject.FILE) fo = fo.getParent();
    			r = fo;
    		}
    		XActionInvoker.invoke("CreateActions/CreateFiles/Common/CreateFile", r, p1);
    		XModelObject newFile = (XModelObject)p1.get("created");
    		if(newFile == null) return;
    		IResource newResource = (IResource)newFile.getAdapter(IResource.class);
    		if(newResource == null) return;
    		String path1 = resource.getFullPath().toString();
    		String path2 = newResource.getFullPath().toString();
    		if(path2.startsWith(path1)) {
    			String value = path2.substring(path1.length());
    			object.setAttributeValue(attr, value);
    		}
    		
    	}
    }

}
