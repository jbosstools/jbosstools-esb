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
public interface ConverterConstants {
	String ALIAS_ENTITY = "ESBPreAlias";
	String ALIASES = "aliases";
	IPropertyConverter ALIAS_CONVERTER = new BasicListConverter(ALIASES, ALIAS_ENTITY);

	String ROUTE_TO_ENTITY = "ESBPreRouteTo";
	String DESTINATIONS = "destinations";
	IPropertyConverter ROUTE_CONVERTER = new BasicListConverter(DESTINATIONS, ROUTE_TO_ENTITY);

	String OBJECT_PATH_ENTITY = "ESBPreObjectPath";
	String OBJECT_PATHS = "object-paths";
	IPropertyConverter OBJECT_PATHS_CONVERTER = new BasicListConverter(OBJECT_PATHS, OBJECT_PATH_ENTITY);

	String NOTIFICATION_ENTITY = "ESBPreNotificationList";
	IPropertyConverter NOTIFICATION_CONVERTER = new BasicListConverter(DESTINATIONS, NOTIFICATION_ENTITY);

	String BPM_VAR_ENTITY = "ESBPreBPMVar";
	String BPM_VARS = "esbToBpmVars";
	IPropertyConverter BPM_VAR_CONVERTER = new BasicListConverter(BPM_VARS, BPM_VAR_ENTITY);

	String HTTP_CLIENT_PROP_ENTITY = "ESBPreClientProp";
	String END_POINT_URL = "endpointUrl";
	IPropertyConverter ENDPOINT_CONVERTER = new EndpointConverter();

	String HEADER_ENTITY = "ESBPreHeader";
	String HEADERS = "headers";
	IPropertyConverter HEADER_CONVERTER = new BasicListConverter(HEADERS, HEADER_ENTITY);

}
