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
		provider = new TestProjectProvider("org.jboss.tools.esb.core.test", null, "Test", true); 
		project = provider.getProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-jms.xml");
		
		StringBuffer errorList = new StringBuffer();
		checkAttributeValue(object, 
				"Providers/JBossMQ", 
				"connection factory", 
				"ConnectionFactory", 
			errorList);
		checkAttributeValue(object, 
				"Providers/JBossMQ/quickstartGwChannel/Filter", 
				"destination name", 
				"queue/quickstart_helloworld_file_notifier_Request_gw", 
			errorList);
		checkAttributeValue(object, 
				"Services/FileRouterListener/Actions/notificationAction/notification-details/NotificationList1", 
				"tag",
				"NotificationList", 
			errorList);
		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}
	
	public void testFTPExample() {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-ftp.xml");
		
		StringBuffer errorList = new StringBuffer();
		checkAttributeValue(object, 
				"Providers/FTPprovider", 
				"hostname", 
				"@FTP_HOSTNAME@", 
			errorList);
		
		String ftpFilterPath = "Providers/FTPprovider/helloFTPChannel/Filter";
		String[][] ftpFilterAttrValues = {
			{"username", "@FTP_USERNAME@"},
			{"password", "@FTP_PASSWORD@"},
			{"read only", "true"},
			{"passive", "false"},
			{"directory", "@FTP_DIRECTORY@"},
			{"input suffix", ".dat"},
			{"work suffix", ".esbWorking"},
			{"post delete", "false"},
			{"post suffix", ".COMPLETE"},
			{"error delete", "false"},
			{"error suffix", ".HAS_ERROR"},
			 
		};
		checkAttributes(object, ftpFilterPath, ftpFilterAttrValues, errorList);

		String ftpGatewayPath = "Services/myFileListener/Listeners/FtpGateway";
		checkAttributeValue(object, 
				ftpGatewayPath, 
				"channel id ref", 
				"helloFTPChannel", 
			errorList);
		checkAttributeValue(object, 
				ftpGatewayPath + "/remoteFileSystemStrategy-configFile", 
				"value", 
				"/ftpfile-cache-config.xml", 
			errorList);

		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}
	
	public void testHibernateExample() {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-hibernate.xml");
		StringBuffer errorList = new StringBuffer();
		
		String hibProviderPath = "Providers/Hibernateprovider";
		checkAttributeValue(object, 
				hibProviderPath, 
				"hibernate cfg file", 
				"hibernate.cfg.xml", 
			errorList);
		
		String hibFilterPath = hibProviderPath + "/helloHibernateChannel/org.jboss.soa.esb.samples.quickstart.hibernateaction.Order"; 
		String[][] hibFilterAttrValues = {
				{"class name", "org.jboss.soa.esb.samples.quickstart.hibernateaction.Order"},
				{"event", "onLoad,onDelete"},
		};
		checkAttributes(object, hibFilterPath, hibFilterAttrValues, errorList);
		
		String hibListenerPath = "Services/myJmsListener/Listeners/HibernateGateway";
		String[][] hibListeneAttrValues = {
			{"channel id ref", "helloHibernateChannel"},
			{"max threads", "1"},
			{"is gateway", "true"}
		};
		checkAttributes(object, hibListenerPath, hibListeneAttrValues, errorList);
		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testJBRExample() {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-jbr.xml");
		
		StringBuffer errorList = new StringBuffer();
		
		String jbrProviderPath = "Providers/JBR-Http";
		String[][] jbrProviderAttrValues = {
				{"protocol", "http"},
				{"host", "localhost"},
		};
		checkAttributes(object, jbrProviderPath, jbrProviderAttrValues, errorList);
		
		String jbrBusPath = jbrProviderPath + "/Http-1";
		String[][] jbrBusAttrValues = {
			{"port", "9876"},
		};
		checkAttributes(object, jbrBusPath, jbrBusAttrValues, errorList);
		
		String jbrListenerPath = "Services/MyWssService/Listeners/Http-Gateway";
		String[][] jbrListenerAttrValues = {
			{"channel id ref", "Http-1"},
			{"max threads", "1"},
			{"is gateway", "true"},
		};
		checkAttributes(object, jbrListenerPath, jbrListenerAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testFSExample() {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-fs.xml");
		
		StringBuffer errorList = new StringBuffer();
		
		String fsProviderPath = "Providers/FSprovider1";
		
		String fsFilterPath = fsProviderPath + "/helloFileChannel/Filter";
		String[][] fsFilterAttrValues = {
			{"directory", "@INPUTDIR@"},
			{"input suffix", ".dat"},
			{"work suffix", ".esbWorking"},
			{"post delete", "false"},
			{"post directory", "@OUTPUTDIR@"},
			{"post suffix", ".sentToEsb"},
			{"error delete", "false"},
			{"error directory", "@ERRORDIR@"},
			{"error suffix", ".IN_ERROR"},
			 
		};
		checkAttributes(object, fsFilterPath, fsFilterAttrValues, errorList);

		String fsListenerPath = "Services/myFileListener/Listeners/FileGateway";
		String[][] fsListenerAttrValues = {
			{"channel id ref", "helloFileChannel"},
			{"max threads", "1"},
			{"is gateway", "true"},
			{"poll frequency seconds", "10"}
		};
		checkAttributes(object, fsListenerPath, fsListenerAttrValues, errorList);
		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testSQLExample() {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-sql.xml");
		
		StringBuffer errorList = new StringBuffer();
		
		String sqlProviderPath = "Providers/SQLprovider";
		String[][] sqlProviderAttrValues = {
			{"url", "jdbc:hsqldb:hsql://localhost:1703"},
			{"driver", "org.hsqldb.jdbcDriver"},
			{"username", "sa"},
			{"password", ""},
		};
		checkAttributes(object, sqlProviderPath, sqlProviderAttrValues, errorList);

		String sqlFilterPath = "Providers/SQLprovider" + "/helloSQLChannel/Filter";
		String[][] sqlFilterAttrValues = {
			{"tablename", "GATEWAY_TABLE"},
			{"status column", "STATUS_COL"},
			{"order by", "DATA_COLUMN"},
			{"where condition", "DATA_COLUMN like 'data%'"},
			{"message id column", "UNIQUE_ID"}
		};
		checkAttributes(object, sqlFilterPath, sqlFilterAttrValues, errorList);

		String sqlListenerPath = "Services/myJmsListener/Listeners/SqlGateway";
		String[][] sqlListenerAttrValues = {
			{"channel id ref", "helloSQLChannel"},
			{"max threads", "1"},
			{"is gateway", "true"}
		};
		checkAttributes(object, sqlListenerPath, sqlListenerAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}


	XModelObject getFileObject(String parentPath, String xmlname) {
		IFile f = project.getFile(new Path(parentPath + "/" + xmlname));
		assertTrue("Cannot find " + xmlname, f != null);
		XModelObject object = EclipseResourceUtil.createObjectForResource(f);
		assertTrue("Cannot create model for " + xmlname, object != null);
		assertTrue("Wrong entity for " + xmlname, ESBConstants.ENT_ESB_FILE_101.equals(object.getModelEntity().getName()));
		return object;
	}

	void checkAttributes(XModelObject object, String path, String[][] attrValuePairs, StringBuffer errorList) {
		for (int i = 0; i < attrValuePairs.length; i++) {
			String attrName = attrValuePairs[i][0];
			String attrValue = attrValuePairs[i][1];
			checkAttributeValue(object, path, attrName, attrValue, errorList);
		}
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
