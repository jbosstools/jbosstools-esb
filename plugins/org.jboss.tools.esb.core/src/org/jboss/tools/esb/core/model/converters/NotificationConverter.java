/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.core.model.converters;

/**
 * @author Viacheslav Kabanovich
 */
public class NotificationConverter extends ListConverter {
	static final String NOTIFICATION_ENTITY = "ESBPreNotificationList";
	static final String DESTINATIONS = "destinations";

	protected String getPropertyName() {
		return DESTINATIONS;
	}

	protected String getItemEntityName() {
		return NOTIFICATION_ENTITY;
	}

}
