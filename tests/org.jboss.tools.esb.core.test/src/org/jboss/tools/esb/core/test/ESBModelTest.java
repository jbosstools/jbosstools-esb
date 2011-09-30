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
import org.jboss.tools.test.util.TestProjectProvider;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelMetaData;
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

	public void testListener() {
		XModelObject object = getFileObject("esb-1.2", "jboss-esb-listener.xml", ESBConstants.ENT_ESB_FILE_120);
		
		StringBuffer errorList = new StringBuffer();

		checkAttributeValue(object, 
				"Services/custom-listener-example/Listeners/custom-listener", 
				"is gateway", 
				"true", 
			errorList);

		checkAttributeValue(object, 
				"Services/arrival-service/Listeners/arrival-queue-listener", 
				"is gateway", 
				"true", 
			errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

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

	public void testNotifiers() {
		XModelObject object = getFileObject("esb-1.2", "jboss-esb-notifiers.xml", ESBConstants.ENT_ESB_FILE_120);
		
		StringBuffer errorList = new StringBuffer();
		
		String sendResponseNotifierPath = "Services/s/Actions/SendResponseNotifier";
		
		String[][] sendResponseNotifierAttrValues = {
			{"name", "SendResponseNotifier"},
			{"ok method", "notifyOK"},
			{"exception method", "notifyError"},
		};
		checkAttributes(object, sendResponseNotifierPath, sendResponseNotifierAttrValues, errorList);

		String errQueuePath = sendResponseNotifierPath + "/err/NotifyQueues/queue#MincomJMS_reply";
		String[][] errQueueAttrValues = {
				{"jndi name", "queue/MincomJMS_reply"},
		};
		checkAttributes(object, errQueuePath, errQueueAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testNotifyEmail() {
		XModelObject object = getFileObject("esb-1.2", "jboss-esb-notifiers.xml", ESBConstants.ENT_ESB_FILE_120);
		
		StringBuffer errorList = new StringBuffer();
		
		String notifyEmailPath = "Services/s/Actions/SendResponseNotifier/ok/NotifyEmail";
		
		String[][] notifyEmailAttrValues = {
			{"from", "person@somewhere.com"},
			{"send to", "person@elsewhere.com"},
			{"subject", "theSubject"},
			{"host", "localhost"},
			{"port", "8801"},
			{"username", "smtpUsername"},
			{"password", "smtpPassword"},
			{"auth", "true"},
			{"copy to", "person@somewhereelse.com"},
			{"attachment name", "attachment"},
		};
		checkAttributes(object, notifyEmailPath, notifyEmailAttrValues, errorList);

		String attachmentPath = notifyEmailPath + "/attachThisFile.txt";
		String[][] attachmentAttrValues = {
				{"file", "attachThisFile.txt"},
		};
		checkAttributes(object, attachmentPath, attachmentAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testNonUniqueProviders() {
		XModelObject object = getFileObject("esb-1.2", "jboss-esb-uninique.xml", ESBConstants.ENT_ESB_FILE_120);
		XModelObject[] os = object.getChildByPath("Providers").getChildren();
		assertEquals(2, os.length);
		
		for (int i = 0; i < os.length; i++) {
			String name = os[i].getAttributeValue("name");
			assertEquals("http", name);
		}

	}

	public void testRiftsawBPELExample() {
		XModelObject object = getFileObject("esb-1.2", "jboss-esb-bpel.xml", ESBConstants.ENT_ESB_FILE_120);
		assertNotNull(object);
		doTestRiftsawBPELExample(object);
		
		object = getFileObject("esb-1.3", "jboss-esb-bpel.xml", ESBConstants.ENT_ESB_FILE_130);
		assertNotNull(object);
		doTestRiftsawBPELExample(object);
	}

	public void doTestRiftsawBPELExample(XModelObject object) {
		StringBuffer errorList = new StringBuffer();
		
		String bpelActionPath = "Services/s/Actions/action2";
		String[][] bpelActionAttrValues = {
			{"name", "action2"},
			{"class", "org.jboss.soa.esb.actions.bpel.BPELInvoke"},
			{"service", "{http://www.jboss.org/bpel/examples/wsdl}HelloService"},
			{"operation", "hello"},
			{"port", "HelloPort"},
			{"request part name", "TestPart"},
			{"response part name", "TestPart"}
		};
		checkAttributes(object, bpelActionPath, bpelActionAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testCamelExample() {
		XModelObject object = getFileObject("esb-1.3", "jboss-esb-camel.xml", ESBConstants.ENT_ESB_FILE_130);
		
		StringBuffer errorList = new StringBuffer();
		
		String camelProviderPath = "Providers/camel";
		String[][] camelProviderAttrValues = {
				{"name", "camel"},
		};
		checkAttributes(object, camelProviderPath, camelProviderAttrValues, errorList);
		
		String camelBusPath = camelProviderPath + "/11";
		String[][] camelBusAttrValues = {
			{"async", "false"},
			{"timeout", "123"},
		};
		checkAttributes(object, camelBusPath, camelBusAttrValues, errorList);
		
		String fromPath = camelBusPath + "/www.yandex.ru";
		String[][] fromAttrValues = {
			{"uri", "www.yandex.ru"},
		};
		checkAttributes(object, fromPath, fromAttrValues, errorList);
		
		String camelGatewayPath = "Services/s/Listeners/camel";
		String[][] camelGatewayAttrValues = {
			{"channel id ref", "11"},
			{"async", "true"},
			{"timeout", "1000"},
		};
		checkAttributes(object, camelGatewayPath, camelGatewayAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testBusinessRulesProcessor_4_9() {
		XModelObject object = getFileObject("esb-1.3", "jboss-esb-brp.xml", ESBConstants.ENT_ESB_FILE_130);
		
		StringBuffer errorList = new StringBuffer();

		// 1.
		String orderDiscountRuleServicePath = "Services/s/Actions/OrderDiscountRuleService";
		
		String[][] orderDiscountRuleServiceAttrValues = {
			{"rule set", "drl/OrderDiscount.drl"},
			{"rule reload", "true"},
			{"rule multithread evaluation", "true"},
			{"rule max threads", "7"},
		};
		checkAttributes(object, orderDiscountRuleServicePath, orderDiscountRuleServiceAttrValues, errorList);

		String routePath = orderDiscountRuleServicePath + "/body.Order";
		String[][] routeAttrValues = {
			{"esb", "body.Order"},
		};
		checkAttributes(object, routePath, routeAttrValues, errorList);

		// 3.
		String orderEventsRuleServiceStatefulPath = "Services/s/Actions/OrderEventsRuleServiceStateful";
		String[][] orderEventsRuleServiceStatefulAttrValues = {
			{"rule set", "drl/OrderEvents.drl"},
			{"rule reload", "false"},
			{"stateful", "true"},
			{"rule audit type", "THREADED_FILE"},
			{"rule audit file", "myaudit"},
			{"rule audit interval", "1000"},
			{"rule clock type", "REALTIME"},
			{"rule event processing type", "STREAM"},
			{"rule fire method", "FIRE_UNTIL_HALT"},
		};
		checkAttributes(object, orderEventsRuleServiceStatefulPath, orderEventsRuleServiceStatefulAttrValues, errorList);
		
		String channelPath = orderEventsRuleServiceStatefulPath + "/chan2";
		String[][] channelAttrValues = {
			{"channel name", "chan2"},
			{"service category", "cat1"},
			{"service name", "svc1"},
			{"channel class", "org.jboss.soa.esb.services.rules.ServiceChannel"},
			{"set payload location", "org.jboss.soa.esb.message.defaultEntry"},
			{"timeout", "30000"},
		};
		checkAttributes(object, channelPath, channelAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testContentBasedRouter() {
		XModelObject object = getFileObject("esb-1.3", "jboss-esb-cbr.xml", ESBConstants.ENT_ESB_FILE_130);
		
		StringBuffer errorList = new StringBuffer();

		// 1. Drools
		String cbrRouterPath = "Services/s/Actions/cbr-router";
		
		String[][] orderDiscountRuleServiceAttrValues = {
			{"cbr alias", "Drools"},
			{"rule set", "/META-INF/drools/airport-code.drl"},
		};
		checkAttributes(object, cbrRouterPath, orderDiscountRuleServiceAttrValues, errorList);

		String objectPath = cbrRouterPath + "/body.'org.jboss.soa.esb.message.defaultEntry'";
		String[][] objectAttrValues = {
			{"esb", "body.'org.jboss.soa.esb.message.defaultEntry'"},
		};
		checkAttributes(object, objectPath, objectAttrValues, errorList);

		String routePath = cbrRouterPath + "/error-service";
		String[][] routeAttrValues = {
			{"service name", "error-service"},
			{"service category", "com.example.soa"},
			{"destination name", "ERROR"},
		};
		checkAttributes(object, routePath, routeAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testContentBasedWiretap() {
		//Check that ContentBasedWiretap is treated in the same way as ContentBasedRouter
		XModelObject object = getFileObject("esb-1.3", "jboss-esb-cbr.xml", ESBConstants.ENT_ESB_FILE_130);
		
		StringBuffer errorList = new StringBuffer();

		String cbrWiretapPath = "Services/s/Actions/cb-wiretap";
		
		String[][] orderDiscountRuleServiceAttrValues = {
			{"cbr alias", "Drools"},
			{"rule set", "/META-INF/drools/airport-code.drl"},
		};
		checkAttributes(object, cbrWiretapPath, orderDiscountRuleServiceAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	public void testMessageFilter() {
		//Check that MessageFilter is treated in the same way as ContentBasedRouter
		XModelObject object = getFileObject("esb-1.3", "jboss-esb-cbr.xml", ESBConstants.ENT_ESB_FILE_130);
		
		StringBuffer errorList = new StringBuffer();

		String cbrWiretapPath = "Services/s/Actions/message-filter";
		
		String[][] orderDiscountRuleServiceAttrValues = {
			{"cbr alias", "Drools"},
			{"rule set", "/META-INF/drools/airport-code.drl"},
		};
		checkAttributes(object, cbrWiretapPath, orderDiscountRuleServiceAttrValues, errorList);

		assertTrue(errorList.toString(), errorList.length() == 0);
	}

	XModelObject getFileObject(String parentPath, String xmlname) {
		return getFileObject(parentPath, xmlname, ESBConstants.ENT_ESB_FILE_101);
	}

	XModelObject getFileObject(String parentPath, String xmlname, String entity) {
		IFile f = project.getFile(new Path(parentPath + "/" + xmlname));
		assertTrue("Cannot find " + xmlname, f != null);
		XModelObject object = EclipseResourceUtil.createObjectForResource(f);
		assertTrue("Cannot create model for " + xmlname, object != null);
		assertTrue("Wrong entity for " + xmlname, entity.equals(object.getModelEntity().getName()));
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

	static String VALUE_FILTER_PROPERTY = "valueFilter";

	static String ATTR_MESSAGE_STORE_CLASS = "message store class";
	static String MESSAGE_STORE_CLASS_VALUE_FILTER_ID = "org.jboss.tools.esb.ui.editor.attribute.MessageStoreClassValueFilter";
	
	static String ATTR_CLASS = "class";
	static String ACTION_CLASS_VALUE_FILTER_ID = "org.jboss.tools.esb.ui.editor.attribute.ActionClassValueFilter";

	public void testValueFilters() throws Exception {
		XModelObject object = getFileObject("esb-1.0.1", "jboss-esb-jms.xml");
		XModelMetaData meta = object.getModel().getMetaData();

		XAttribute a = meta.getEntity("ESBPreActionMessagePersister101").getAttribute(ATTR_MESSAGE_STORE_CLASS);
		assertNotNull(a);
		String valueFilter = a.getProperty(VALUE_FILTER_PROPERTY);
		assertEquals(MESSAGE_STORE_CLASS_VALUE_FILTER_ID, valueFilter);
		
		a = meta.getEntity("ESBPreActionMessagePersister101").getAttribute(ATTR_MESSAGE_STORE_CLASS);
		assertNotNull(a);
		valueFilter = a.getProperty(VALUE_FILTER_PROPERTY);
		assertEquals(MESSAGE_STORE_CLASS_VALUE_FILTER_ID, valueFilter);

		a = meta.getEntity("ESBAction101").getAttribute(ATTR_CLASS);
		assertNotNull(a);
		valueFilter = a.getProperty(VALUE_FILTER_PROPERTY);
		assertEquals(ACTION_CLASS_VALUE_FILTER_ID, valueFilter);

		a = meta.getEntity("ESBAction120").getAttribute(ATTR_CLASS);
		assertNotNull(a);
		valueFilter = a.getProperty(VALUE_FILTER_PROPERTY);
		assertEquals(ACTION_CLASS_VALUE_FILTER_ID, valueFilter);
	}
	
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

}
