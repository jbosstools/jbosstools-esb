package org.jboss.tools.esb.project.core.test;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.Module;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.tools.as.test.core.internal.utils.MatrixUtils;
import org.jboss.tools.as.test.core.internal.utils.ServerCreationTestUtils;
import org.jboss.tools.esb.core.module.JBossESBModuleFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class EsbModuleServerCompatTest extends Assert {
	 private static String[] esbVersions = new String[]{
			 "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", 
			 "4.9", "4.10", "4.11", "4.12", "4.13"
	 };
	 private static String[] for40 = new String[]{"4.2", "4.3", "4.4"};
	 private static String[] for4x = new String[]{"4.2", "4.3", "4.4","4.5","4.6","4.7"};
	 private static String[] full = esbVersions;
	 
	private static HashMap<String,String[]> expected = new HashMap<String, String[]>();
	static {
		expected.put(IJBossToolingConstants.SERVER_AS_40, for40);
		expected.put(IJBossToolingConstants.SERVER_AS_42, for4x);
		expected.put(IJBossToolingConstants.SERVER_EAP_43, for4x);
		expected.put(IJBossToolingConstants.DEPLOY_ONLY_SERVER, full);
		expected.put(IJBossToolingConstants.SERVER_AS_50, full);
		expected.put(IJBossToolingConstants.SERVER_AS_51, full);
		expected.put(IJBossToolingConstants.SERVER_AS_60, full);
		
		// AS 7 not supported by ESB
//        expected.put(IJBossToolingConstants.SERVER_AS_70, full);
//        expected.put(IJBossToolingConstants.SERVER_AS_71, full);
		expected.put(IJBossToolingConstants.SERVER_EAP_50, full);

		// EAP 6 not supported by ESB
//		expected.put(IJBossToolingConstants.SERVER_EAP_60, full);
//        expected.put(IJBossToolingConstants.SERVER_EAP_61, full);
	}
	
	 @Parameters
	 public static Collection<Object[]> data() {
		 String[] servers = IJBossToolingConstants.ALL_JBOSS_SERVERS;
		 Object[][] blocks = new Object[][]{
				 servers, esbVersions
		 };
		 return MatrixUtils.toMatrix(blocks);
	 }
	
	 private String serverType, version;
	 private IServer server;
	 private IModule module;
	 public EsbModuleServerCompatTest(String serverType, String esbVersion) {
		 this.serverType = serverType;
		 this.version = esbVersion;
	 }
	 
		@Before
		public void setUp() {
			server = ServerCreationTestUtils.createMockServerWithRuntime(serverType, getClass().getName() + serverType);
			module = createModule(version);
		}
		
		private IModule createModule(String v) {
			String name = "id" + version;
			return new Module( null, name, name, 
					JBossESBModuleFactory.MODULE_TYPE, version, null);
		}

		@After
		public void tearDown() throws Exception {
			ServerCreationTestUtils.deleteAllServersAndRuntimes();
		}

	@Test
	public void testeServerModuleSupport() {
        boolean canDeploy = ServerUtil.isSupportedModule(server.getServerType().getRuntimeType().getModuleTypes(), module.getModuleType());
        boolean shouldDeploy = expected.get(serverType) == null ? false : 
            contains(expected.get(serverType), version);
        System.out.println("Version " + version + " supported on " + serverType + 
                "? Expected:actual=" + shouldDeploy + ":" + canDeploy);
        assertEquals("Version " + version + " supported on " + serverType + 
                "? Expected:actual=" + shouldDeploy + ":" + canDeploy, 
                canDeploy, shouldDeploy);
	}
	
	private boolean contains(String[] versions, String version) {
		for( int i = 0; i < versions.length; i++ ) {
			if( versions[i].equals(version))
				return true;
		}
		return false;
	}
}