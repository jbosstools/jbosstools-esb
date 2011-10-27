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

import java.util.*;

import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.refactoring.RenameProcessorRunner;
import org.jboss.tools.esb.core.model.ESBConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class RenameScheduleIDHandler extends AbstractHandler {

	public boolean isEnabled(XModelObject object) {
		return (object != null && object.isObjectEditable());
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		RenameScheduleIDProcessor processor = new RenameScheduleIDProcessor();
		processor.setModelObject(object);
		String name = object.getAttributeValue(ESBConstants.ATTR_SCHEDULE_ID);
		RenameProcessorRunner.run(processor, name);
	}

}
