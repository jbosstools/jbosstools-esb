/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.esb.core.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.test.util.TestProjectProvider;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.esb.core.model.ESBConstants;

import junit.framework.TestCase;

public class ESBModelTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;
	
	public ESBModelTest() {}
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.esb.core.test", null, "Test", false); 
		project = provider.getProject();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*
	public void testPaths() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		String testName = "ESBModelTest:testPaths";
		ArrayList<TestDescription> tests = provider.getTestDescriptions(testName);
		System.out.println(testName + " " + (tests == null ? -1 : tests.size()));
		StringBuffer sb = new StringBuffer();
		int errorCount = 0;
		if(tests != null) for (int i = 0; i < tests.size(); i++) {
			TestDescription t = tests.get(i);
			String path = t.getProperty("path");
			XModelObject o = n.getModel().getByPath(path);
			if(o == null) {
				sb.append(path).append("\n");
				errorCount++;
			}
		}
		assertTrue("Cannot find objects at " + errorCount + " paths\n" + sb.toString(), errorCount == 0);
	}
*/
	
	public void testJMSExample() {
		IFile f = project.getFile(new Path("esb-1.0.1/jboss-esb-jms.xml"));
		assertTrue("Cannot find jboss-esb-jms.xml", f != null);
		XModelObject object = EclipseResourceUtil.createObjectForResource(f);
		assertTrue("Cannot create model for jboss-esb-jms.xml", object != null);
		assertTrue("Wrong entity for jboss-esb-jms.xml", ESBConstants.ENT_ESB_FILE_101.equals(object.getModelEntity().getName()));
		
		StringBuffer errorList = new StringBuffer();
		checkAttributeValue(object, 
				"Providers/JBossMQ", 
				"connection factory", 
				"ConnectionFactory", 
			errorList);
		checkAttributeValue(object, 
				"Providers/JBossMQ/quickstartGwChannel/Filter", 
				"dest name", 
				"queue/quickstart_helloworld_file_notifier_Request_gw", 
			errorList);
		checkAttributeValue(object, 
				"Services/FileRouterListener/Actions/notificationAction/notification-details/NotificationList1", 
				"tag",
				"NotificationList", 
			errorList);
		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}
	
	protected boolean checkAttributeValue(XModelObject object, String path, String attribute, String testValue, StringBuffer errorList) {
		XModelObject child = object.getChildByPath(path);
		if(child == null) {
			errorList.append("Cannot find object at " + path).append("\n");
			return false;
		}
		if(child.getModelEntity().getAttribute(attribute) == null) {
			errorList.append("Attribute " + attribute + " is not found in object " + path).append("\n");
			return false;
		}
		String realValue = child.getAttributeValue(attribute);
		if(realValue == null || !realValue.equals(testValue)) {
			errorList.append("Attribute " + attribute + " in object " + path + " has unexpected value '" + realValue + "'").append("\n");
			return false;
		}
		
		return true;
	}
	
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

}
