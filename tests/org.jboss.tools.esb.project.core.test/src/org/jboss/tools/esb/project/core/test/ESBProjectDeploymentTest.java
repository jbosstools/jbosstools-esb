package org.jboss.tools.esb.project.core.test;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.jboss.ide.eclipse.as.core.server.IDeployableServer;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.facet.JBossClassPathCommand;
import org.jboss.tools.esb.core.facet.JBossESBFacetDataModelProvider;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeClassPathInitializer;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.test.util.TestProjectProvider;

public class ESBProjectDeploymentTest extends TestCase {
	private static final String SOAP_HOME_5_0_PROP_NAME = "jbosstools.test.soap.home.5.0";

	private static final String SOAP_HOME_4_3_PROP_NAME = "jbosstools.test.soap.home.4.3";

	public static final String BOSS_HOME_5_1_PROP_NAME = "jbosstools.test.jboss.home.5.1";

	public static final String JBOSS_HOME_5_0_PROP_NAME = "jbosstools.test.jboss.home.5.0";

	public static final String JBOSS_HOME_4_2_PROP_NAME = "jbosstools.test.jboss.home.4.2";

	public static final IVMInstall VM_INSTALL = JavaRuntime
			.getDefaultVMInstall();

		public static final String JBOSS_AS_42_HOME;
		public static final String JBOSS_AS_50_HOME;
		public static final String JBOSS_AS_51_HOME;
		public static final String SERVER_SOAP43_HOME;
		public static final String SERVER_SOAP50_HOME;
	

	static {
		String message = "Use -D{0}=path/to/server to define system property value in command line or " 
			           + "in tab Arguments of Eclipse Application Launch Configuration dialog";
		String syspropValue = System.getProperty(JBOSS_HOME_4_2_PROP_NAME);
		if(syspropValue!=null) {
			JBOSS_AS_42_HOME = new File(System.getProperty(JBOSS_HOME_4_2_PROP_NAME)).getAbsolutePath();
		} else {
			throw new IllegalArgumentException(MessageFormat.format(message, JBOSS_HOME_4_2_PROP_NAME));
		}
		syspropValue = System.getProperty(JBOSS_HOME_5_0_PROP_NAME);
		if(syspropValue!=null) {
			JBOSS_AS_50_HOME =  new File(System.getProperty(JBOSS_HOME_5_0_PROP_NAME)).getAbsolutePath();
		} else {
			throw new IllegalArgumentException(MessageFormat.format(message, JBOSS_HOME_5_0_PROP_NAME));
		}
		syspropValue = System.getProperty(BOSS_HOME_5_1_PROP_NAME);
		if(syspropValue!=null) {
			JBOSS_AS_51_HOME =  new File(System.getProperty(BOSS_HOME_5_1_PROP_NAME)).getAbsolutePath();
		} else {
			throw new IllegalArgumentException(MessageFormat.format(message, BOSS_HOME_5_1_PROP_NAME));
		}
		syspropValue = System.getProperty(SOAP_HOME_4_3_PROP_NAME);
		if(syspropValue!=null) {
			SERVER_SOAP43_HOME =  new File(System.getProperty(SOAP_HOME_4_3_PROP_NAME) + "//jboss-as").getAbsolutePath();
		} else {
			throw new IllegalArgumentException(MessageFormat.format(message, SOAP_HOME_4_3_PROP_NAME));
		}
		syspropValue = System.getProperty(SOAP_HOME_5_0_PROP_NAME);
		if(syspropValue!=null) {
			SERVER_SOAP50_HOME =  new File(System.getProperty(SOAP_HOME_5_0_PROP_NAME) + "//jboss-as").getAbsolutePath();
		} else {
			throw new IllegalArgumentException(MessageFormat.format(message, SOAP_HOME_5_0_PROP_NAME));
		}
	}
	
	static String userCustomizedESBRuntime = "UserCustomizedESBRuntime";
	
	private JBossRuntimeManager jrm = JBossRuntimeManager.getInstance();

	static String BUNDLE = "org.jboss.tools.esb.project.core.test";
	IProject project;

	private IRuntime soap50_runtime;
	private IRuntime currentRuntime;
	private IServer currentServer;

	private JBossESBRuntime jer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		soap50_runtime = createRuntime(IJBossToolingConstants.EAP_50,
				SERVER_SOAP50_HOME, "default");
		
		jer = new JBossESBRuntime();
		jer.setName(userCustomizedESBRuntime);
		jer.setConfiguration("default");
		jer.setDefault(true);
		jer.setHomeDir(SERVER_SOAP50_HOME);
		jer.setVersion("4.7");
		jrm.addRuntime(jer);
		jrm.save();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		jrm.removeRuntime(jer);
		jrm.save();
		soap50_runtime.delete();
		if (project != null) {
			project.delete(true, null);
		}

		if (currentRuntime != null) {
			currentRuntime.delete();
			currentServer.delete();
		}
	}

	public IProject createESBProject(String prjName) throws CoreException {
		TestProjectProvider provider = new TestProjectProvider(BUNDLE,
				null, prjName, true);
		IProject prj = provider.getProject();
		IFacetedProject ifp = ProjectFacetsManager.create(prj);

		org.eclipse.wst.common.project.facet.core.runtime.IRuntime fruntime = RuntimeManager
				.getRuntime(IJBossToolingConstants.EAP_50);
		ifp.addTargetedRuntime(fruntime, null);
		ifp.setPrimaryRuntime(fruntime, null);
		prj.build(IncrementalProjectBuilder.FULL_BUILD, null);

		return prj;
	}

	public void testCheckModuleFactoryByESBFacet() throws CoreException {
		project = createESBProject("esbTestProject");
		
		Map<String, ModuleFactory> mpESBFactories = getAllAvailableESBModuleFactories();

		IProjectFacet facets = ProjectFacetsManager
				.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
		for (IProjectFacetVersion fv : facets.getVersions()) {
			String version = fv.getVersionString();
			ModuleFactory factory = mpESBFactories.get(version);
			assertNotNull("There is no module factory for ESB " + version,
					factory);

			IModule[] modules = factory.getModules();

			IModuleResource[] res = factory.getDelegate(null)
					.getModuleDelegate(modules[0]).members();
			assertEquals(
					"Deployment logic is not correct for ESB"+ fv.getVersionString()+", some artifacts were lost",
					3, res.length);
		}

	}
	
	public void testUserCustomizedESBRuntime() throws Exception{
//		project = createESBProject("esbTestProject_2"); 
		TestProjectProvider provider = new TestProjectProvider(BUNDLE,
				null , "esbTestProject_2", true);
		project = provider.getProject();

		IDataModel model = DataModelFactory.createDataModel(new JBossESBFacetDataModelProvider());
		model.setBooleanProperty(IJBossESBFacetDataModelProperties.RUNTIME_IS_SERVER_SUPPLIED, false);
		model.setStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_ID, jer.getName());
		model.setStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_HOME, jer.getHomeDir());
		model.setStringProperty(IJBossESBFacetDataModelProperties.ESB_CONTENT_FOLDER, "esbcontent");
		
		JBossClassPathCommand jcc = new JBossClassPathCommand(project, model);
		IStatus status = jcc.executeOverride(null);
		
		assertEquals("Failed to add User Customized ESB runtime classpath container.", IStatus.OK, status.getSeverity());
		
		IJavaProject jproject = JavaCore.create(project);
		
		JBossRuntimeClassPathInitializer initializer = new JBossRuntimeClassPathInitializer();
		IPath path = new Path("org.jboss.esb.runtime.classpath/UserCustomizedESBRuntime");
		initializer.initialize(path, jproject);
		
		IClasspathEntry[] entry = initializer.getEntries(path);//jproject.getRawClasspath();
		List<String> jars = new ArrayList<String>();
		
		/* ldimaggi - display list of jars in the assert text - https://issues.jboss.org/browse/JBDS-2152 */
		String jarsString = null;
		for(IClasspathEntry ent :entry){
			jars.add(ent.getPath().lastSegment());
			jarsString = jarsString + ent.getPath().lastSegment() + " ";
		}
		assertEquals("unalbe to read User customized ESB runtime, jbossesb-rosetta.jar is not in classpath." + jarsString, true, jars.contains("jbossesb-rosetta.jar"));
		
	}

	public void testSOAP50Deployment() throws Exception {
		testESBDeployment(IJBossToolingConstants.EAP_50,
				IJBossToolingConstants.SERVER_EAP_50, SERVER_SOAP50_HOME);
	}

	public void testSOAP43Deployment() throws Exception {
		testESBDeployment(IJBossToolingConstants.EAP_43,
				IJBossToolingConstants.SERVER_EAP_43, SERVER_SOAP43_HOME);
	}

	public void testJBossServer50Deployment() throws Exception {
		testESBDeployment(IJBossToolingConstants.AS_50,
				IJBossToolingConstants.SERVER_AS_50, JBOSS_AS_50_HOME);
	}

	public void testJbossServer51Deployment() throws Exception {
		testESBDeployment(IJBossToolingConstants.AS_51,
				IJBossToolingConstants.SERVER_AS_51, JBOSS_AS_51_HOME);
	}

	public void testJbossServer42Deployment() throws Exception {
		testESBDeployment(IJBossToolingConstants.AS_42,
				IJBossToolingConstants.SERVER_AS_42, JBOSS_AS_42_HOME);
	}

	private void testESBDeployment(String runtimeId, String serverid,
			String serverHome) throws Exception {
		File archive = null;
		try {
			createServer(runtimeId, serverid, serverHome, "default");
			project = createESBProject("esbTestProject");
			publishESBProject();

			// check deployment result
			IPath serverPath = new Path(serverHome);
			serverPath.append("server").append("default").append("deploy");
			serverPath.append("esbTestProject.esb");
			archive = serverPath.toFile();
			assertTrue("Deploy failed, nothing was deployed to server", archive
					.exists());

			if (archive.isDirectory()) {
				serverPath.append("META-INF");
				assertTrue(
						"generated a wrong ESB archive, no META-INF in the archive",
						serverPath.toFile().exists());

				serverPath.removeLastSegments(1);
				serverPath
						.append("org\\jboss\\soa\\esb\\samples\\quickstart\\helloworld");
				serverPath.append("MyJMSListenerAction.class");
				assertTrue("no class was pick up into the archive", serverPath
						.toFile().exists());
			}
			else{
					ZipFile azip = new ZipFile(archive);
					ZipEntry entry = azip.getEntry("META-INF");
					assertNotNull("generated a wrong ESB archive, no META-INF in the archive", entry);
					entry = azip.getEntry("org");
					assertNotNull("there is no class in the esb archive", entry);
				
			}
		} finally {
			if (archive != null) {
				archive.delete();
			}
		}
	}

	protected void publishESBProject() throws CoreException {
		IModule[] modules = ServerUtil.getModules(currentServer.getServerType()
				.getRuntimeType().getModuleTypes());
		IServerWorkingCopy serverWC = currentServer.createWorkingCopy();
		serverWC.modifyModules(modules, null, null);
		serverWC.save(true, null).publish(0, null);
		currentServer.publish(IServer.PUBLISH_FULL, null);

	}


	private Map<String, ModuleFactory> getAllAvailableESBModuleFactories() {
		Map<String, ModuleFactory> mpFactory = new HashMap<String, ModuleFactory>();
		for (ModuleFactory factory : ServerPlugin.getModuleFactories()) {
			for (IModuleType type : factory.getModuleTypes()) {
				if (type.getId().equals(ESBProjectConstant.ESB_PROJECT_FACET)) {
					mpFactory.put(type.getVersion(), factory);
				}
			}
		}

		return mpFactory;
	}

	private IRuntime createRuntime(String runtimeId, String homeDir,
			String config) throws CoreException {
		IRuntimeType[] runtimeTypes = ServerUtil.getRuntimeTypes(null, null,
				runtimeId);
		assertEquals("expects only one runtime type", runtimeTypes.length, 1);
		IRuntimeType runtimeType = runtimeTypes[0];
		IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(null,
				new NullProgressMonitor());
		runtimeWC.setName(runtimeId);
		runtimeWC.setLocation(new Path(homeDir));
		((RuntimeWorkingCopy) runtimeWC).setAttribute(
				IJBossServerRuntime.PROPERTY_VM_ID, VM_INSTALL.getId());
		((RuntimeWorkingCopy) runtimeWC).setAttribute(
				IJBossServerRuntime.PROPERTY_VM_TYPE_ID, VM_INSTALL
						.getVMInstallType().getId());
		((RuntimeWorkingCopy) runtimeWC).setAttribute(
				IJBossServerRuntime.PROPERTY_CONFIGURATION_NAME, config);
		IRuntime savedRuntime = runtimeWC.save(true, new NullProgressMonitor());
		return savedRuntime;
	}

	protected void createServer(String runtimeID, String serverID,
			String location, String configuration) throws CoreException {
		// if file doesnt exist, abort immediately.
		assertTrue("server location " + location + " does not exist", new Path(location).toFile().exists());

		currentRuntime = createRuntime(runtimeID, location, configuration);
		IServerType serverType = ServerCore.findServerType(serverID);
		IServerWorkingCopy serverWC = serverType.createServer(null, null,
				new NullProgressMonitor());
		serverWC.setRuntime(currentRuntime);
		serverWC.setName(serverID);
		serverWC.setServerConfiguration(null);
		IPath path = new Path(location).append("server").append("default")
				.append("deploy");
		((ServerWorkingCopy) serverWC).setAttribute(
				IDeployableServer.DEPLOY_DIRECTORY, path.toOSString());
		currentServer = serverWC.save(true, new NullProgressMonitor());

	}

}
