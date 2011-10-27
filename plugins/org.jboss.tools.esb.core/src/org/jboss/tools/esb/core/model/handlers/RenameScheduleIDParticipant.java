/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.esb.core.model.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.ltk.core.refactoring.participants.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.refactoring.RenameModelObjectChange;
import org.jboss.tools.common.model.refactoring.RenameProcessorRunner;
import org.jboss.tools.esb.core.ESBCoreMessages;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class RenameScheduleIDParticipant extends RenameParticipant implements ISharableParticipant {
	public static final String PARTICIPANT_NAME="esb-RenameScheduleIDParticipant"; //$NON-NLS-1$
	private XModelObject object;

	public RenameScheduleIDParticipant() {}

	protected boolean initialize(Object element) {
		if(element instanceof XModelObject) {
			object = (XModelObject)element;
			if(object.getModelEntity().getAttribute(ESBConstants.ATTR_SCHEDULE_ID) == null) {
				object = null;
			}
		}
		return object != null;
	}

	public void addElement(Object element, RefactoringArguments arguments) {
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}
	
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (!pm.isCanceled()) {
			if(!updateReferences() && object == null) {
				return null;
			}
			String newName = getArguments().getNewName();
			XModelObject f = FileSystemsHelper.getFile(object);
			if(f != null) {
				RenameModelObjectChange c1 = RenameModelObjectChange.createChange(f, newName);
				if(c1 != null) {
					c1.addEdits(new XModelObject[]{object}, ESBConstants.ATTR_SCHEDULE_ID, ESBCoreMessages.SCHEDULE_ID);
				
					if(updateReferences()) {
						String oldName = object.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID);
						XModelObject[] rs = getRefs(oldName);
						if(rs.length > 0) {
							c1.addEdits(rs, ESBConstants.ATTR_SCHEDULE_ID_REF, ESBCoreMessages.SCHEDULE_ID_REF);
						}
					}
				}
				
				return c1;
			}
		}
		return null;
	}
	
	protected boolean updateReferences() {
		return RenameProcessorRunner.updateReferences(getProcessor());
	}

	private XModelObject[] getRefs(String oldName) {
		List<XModelObject> list = new ArrayList<XModelObject>();
		collectRefs(list, FileSystemsHelper.getFile(object), oldName);
		return list.toArray(new XModelObject[list.size()]);
	}

	private void collectRefs(List<XModelObject> list, XModelObject o, String oldName) {
		if(oldName.equals(o.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID_REF))) {
			list.add(o);
		}
		XModelObject[] cs = o.getChildren();
		for (XModelObject c: cs) {
			collectRefs(list, c, oldName);
		}
	}

}
