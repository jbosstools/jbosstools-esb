package org.jboss.tools.esb.core.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.plugin.ModelMessages;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class OpenScheduleHandler extends AbstractHandler {

	public OpenScheduleHandler() {}

	public boolean isEnabled(XModelObject object) {
		return object != null 
				&& object.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID_REF) != null 
				&& object.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID_REF).length() > 0; 
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		String scheduleID = object.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID_REF);
		XModelObject schedule = findSchedule(FileSystemsHelper.getFile(object), scheduleID);
		if(schedule != null) {
			FindObjectHelper.findModelObject(schedule, FindObjectHelper.IN_EDITOR_ONLY);
		} else {
			String message = "Cannot find schedule " + scheduleID;
			object.getModel().getService().showDialog(ModelMessages.WARNING, message, new String[]{SpecialWizardSupport.CLOSE}, null, ServiceDialog.WARNING);
		}
	}

	XModelObject findSchedule(XModelObject file, String scheduleID) {
		XModelObject[] ps = file.getChildByPath("Providers").getChildren(); //$NON-NLS-1$
		for (int i = 0; i < ps.length; i++) {
			XModelObject[] cs = ps[i].getChildren();
			for (int j = 0; j < cs.length; j++) {
				if(cs[j].getModelEntity().getAttribute(ESBConstants.ATTR_SCHEDULE_ID) != null) {
					String v = cs[j].getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID);
					if(scheduleID.equals(v)) {
						return cs[j];
					}
				}
			}
		}
		return null;
	}

}
