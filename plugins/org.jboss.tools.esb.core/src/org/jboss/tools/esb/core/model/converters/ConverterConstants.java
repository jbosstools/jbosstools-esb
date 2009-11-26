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

	String NAMESPACE_ENTITY = "ESBPreNamespace";
	String NAMESPACES = "namespaces";
	IPropertyConverter NAMESPACES_CONVERTER = new BasicListConverter(NAMESPACES, NAMESPACE_ENTITY);

	String FIELD_ALIAS_ENTITY = "ESBPreFieldAlias";
	String FIELD_ALIASES = "fieldAliases";
	IPropertyConverter FIELD_ALIAS_CONVERTER = new BasicListConverter(FIELD_ALIASES, FIELD_ALIAS_ENTITY);

	String IMPLICIT_COLLECTION_ENTITY = "ESBPreImplicitCollection";
	String IMPLICIT_COLLECTIONS = "implicit-collections";
	IPropertyConverter IMPLICIT_COLLECTION_CONVERTER = new BasicListConverter(IMPLICIT_COLLECTIONS, IMPLICIT_COLLECTION_ENTITY);

	String CONVERTER_ENTITY = "ESBPreConverter";
	String CONVERTERS = "converters";
	IPropertyConverter CONVERTER_CONVERTER = new BasicListConverter(CONVERTERS, CONVERTER_ENTITY);

	String ATTRIBUTE_ALIAS_ENTITY = "ESBPreAttributeAlias";
	String ATTRIBUTE_ALIASES = "attribute-aliases";
	IPropertyConverter ATTRIBUTE_ALIAS_CONVERTER = new BasicListConverter(ATTRIBUTE_ALIASES, ATTRIBUTE_ALIAS_ENTITY);

}
