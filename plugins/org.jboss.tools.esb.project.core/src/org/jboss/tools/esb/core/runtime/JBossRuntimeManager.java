/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;

/**
 * @author Denny Xu
 */
public class JBossRuntimeManager {

	private static JBossRuntimeListConverter converter = new JBossRuntimeListConverter();

	private Map<String, JBossESBRuntime> runtimes = new HashMap<String, JBossESBRuntime>();
	

	static final String PLUGIN_ID = "org.jboss.tools.esb.project.core"; //$NON-NLS-1$
	static String ATT_CLASS = "class"; //$NON-NLS-1$
	static String ATT_VERSION = "esbVersion"; //$NON-NLS-1$
	static String ATT_ID = "id";
	static String VERSION_SEPARATOR = ",";
	static String VERSION_FILE_NAME = "VERSION";
	static String VERSION_PROPERTIES_KEY = "Version";
	static String VERSION_PROPERTIES_SEPERATOR = "_";
	

	
	static Map<String, IESBRuntimeResolver> parserMap = new HashMap<String, IESBRuntimeResolver>();


	/**
	 * Private constructor
	 */
	private JBossRuntimeManager() {
		load();
	}

	/**
	 * This class make Java Runtime responsible for solving synchronization
	 * problems during initialization if there is any
	 * 
	 */
	static class JBossRuntimeManagerHolder {
		private static final JBossRuntimeManager INSTANCE = new JBossRuntimeManager();
	}

	/**
	 * Return JBossWSRuntimeManaher instance
	 * 
	 * @return JBossWSRuntimeManager instance
	 */
	public static JBossRuntimeManager getInstance() {
		return JBossRuntimeManagerHolder.INSTANCE;
	}

	/**
	 * Return Array of configured JBossWSRuntimes
	 * 
	 * @return JBossWSRuntime[]
	 */
	public JBossESBRuntime[] getRuntimes() {
		Collection<JBossESBRuntime> c = runtimes.values();
		return c.toArray(new JBossESBRuntime[runtimes.size()]);
	}

	/**
	 * Add new JBossWSRuntime
	 * 
	 * @param runtime
	 *            JBossWSRuntime
	 */
	public void addRuntime(JBossESBRuntime runtime) {
		if (runtimes.size() == 0) {
			runtime.setDefault(true);
		}

		JBossESBRuntime oldDefaultRuntime = getDefaultRuntime();
		if (oldDefaultRuntime != null && runtime.isDefault()) {
			oldDefaultRuntime.setDefault(false);
		}
		runtimes.put(runtime.getName(), runtime);
		save();
	}

	/**
	 * Add new JBossWSRuntime with given parameters
	 * 
	 * @param name
	 *            String - runtime name
	 * @param path
	 *            String - runtime home folder
	 * @param version
	 *            String - string representation of version number
	 * @param defaultRt
	 *            boolean - default flag
	 */
	public void addRuntime(String name, String path, String version,
			boolean defaultRt) {
		JBossESBRuntime jbossWSRt = new JBossESBRuntime();
		jbossWSRt.setHomeDir(path);
		jbossWSRt.setName(name);
		jbossWSRt.setVersion(version);
		jbossWSRt.setDefault(defaultRt);
		addRuntime(jbossWSRt);
	}

	/**
	 * Return JBossWSRuntime by given name
	 * 
	 * @param name
	 *            String - JBossWSRuntime name
	 * @return JBossWSRuntime - found JBossWSRuntime instance or null
	 */
	public JBossESBRuntime findRuntimeByName(String name) {
		for (JBossESBRuntime jbossWSRuntime : runtimes.values()) {
			if (jbossWSRuntime.getName().equals(name)) {
				return jbossWSRuntime;
			}
		}
		return null;
	}

	public JBossESBRuntime[]  findRuntimeByVersion(String version) {
		if(version == null || "".equals(version)){
			return getRuntimes();
		}
		List<JBossESBRuntime> rts = new ArrayList<JBossESBRuntime>();
		for (JBossESBRuntime jbossWSRuntime : runtimes.values()) {
			if (jbossWSRuntime.getVersion().equals(version)) {
				rts.add(jbossWSRuntime);
			}
		}
		return rts.toArray(new JBossESBRuntime[]{});
	}
	
	public List<String> getAllRuntimeJars(JBossESBRuntime rt, String esbVersion){
		List<String> jarList = new ArrayList<String>();
		if (rt != null) {
			if (rt.isUserConfigClasspath()) {
				jarList.addAll(rt.getLibraries());
				 
			} else {

				jarList = getAllRuntimeJars(rt.getHomeDir(), esbVersion, rt.getConfiguration());
			}
			
		}
		return jarList;
	}
	
	public List<String> getAllRuntimeJars(String runtimeLocation, String esbVersion, String configuration) {
		List<String> jarList = new ArrayList<String>();
		IESBRuntimeResolver resolver = null;
		if(parserMap.get(esbVersion) != null){
			resolver = (IESBRuntimeResolver)parserMap.get(esbVersion);
		}
		
		if( resolver != null){
			List<File> jars = resolver.getAllRuntimeJars(runtimeLocation, configuration);
			for(File file : jars){
				jarList.add(file.getAbsolutePath());
			}
		}
		else{
			ESBProjectCorePlugin.log("No ESB runtime resolver defined for ESB "+ esbVersion, null, Status.WARNING);
		}
		
		return jarList;
	}
	
	/**
	 * Remove given JBossWSRuntime from manager
	 * 
	 * @param rt
	 *            JBossWSRuntime
	 */
	public void removeRuntime(JBossESBRuntime rt) {
		runtimes.remove(rt.getName());
	}

	/**
	 * Save preference value and force save changes to disk
	 */
	public void save() {
		ESBProjectCorePlugin.getDefault().getPreferenceStore().setValue(
				JBossFacetCoreMessages.ESB_Location, converter.getString(runtimes));
		IPreferenceStore store = ESBProjectCorePlugin.getDefault()
				.getPreferenceStore();
		if (store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore) store).save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Marks this runtime as default. Marks other runtimes with the same version
	 * as not default.
	 * 
	 * @param runtime
	 */
	public void setDefaultRuntime(JBossESBRuntime runtime) {
		JBossESBRuntime[] runtimes = getRuntimes();
		for (int i = 0; i < runtimes.length; i++) {
			runtimes[i].setDefault(false);
		}
		runtime.setDefault(true);
	}

	/**
	 * Return first default JBossWSRuntime
	 * 
	 * @return JBossWSRuntime
	 */
	public JBossESBRuntime getDefaultRuntime() {
		for (JBossESBRuntime rt : runtimes.values()) {
			if (rt.isDefault()) {
				return rt;
			}
		}
		return null;
	}

	/**
	 * Return list of available JBossWSRuntime names
	 * 
	 * @return List&lt;String&gt;
	 */
	public List<String> getRuntimeNames() {
		JBossESBRuntime[] rts = getRuntimes();
		List<String> result = new ArrayList<String>();
		for (JBossESBRuntime jbossWSRuntime : rts) {
			result.add(jbossWSRuntime.getName());
		}
		return result;
	}

	/**
	 * Return a list of all runtime names
	 * 
	 * @return List of all runtime names
	 */
	public List<String> getAllRuntimeNames() {
		JBossESBRuntime[] rts = getRuntimes();
		List<String> result = new ArrayList<String>();
		for (JBossESBRuntime jbossWSRuntime : rts) {
			result.add(jbossWSRuntime.getName());
		}
		return result;
	}

	/**
	 * TBD
	 * 
	 * @param oldName
	 *            old runtime name
	 * @param newName
	 *            new runtime name
	 */
	public void changeRuntimeName(String oldName, String newName) {
		JBossESBRuntime o = findRuntimeByName(oldName);
		if (o == null) {
			return;
		}
		o.setName(newName);
		onRuntimeNameChanged(oldName, newName);
	}

	private void onRuntimeNameChanged(String oldName, String newName) {
		IProjectFacet facet = ProjectFacetsManager
				.getProjectFacet(IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID);
		Set<IFacetedProject> facetedProjects = null;
		try {
			facetedProjects = ProjectFacetsManager.getFacetedProjects(facet);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (IFacetedProject facetedProject : facetedProjects) {
			QualifiedName qRuntimeName = IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME;
			String name = null;
			try {
				name = facetedProject.getProject().getPersistentProperty(
						qRuntimeName);
				if (name != null && name.equals(oldName)) {
					facetedProject.getProject().setPersistentProperty(
							qRuntimeName, newName);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static boolean isRuntimeUsed(String name) {
		IProjectFacet facet = ProjectFacetsManager
				.getProjectFacet(IJBossESBFacetDataModelProperties.JBOSS_ESB_FACET_ID);
		Set<IFacetedProject> facetedProjects = null;
		try {
			facetedProjects = ProjectFacetsManager.getFacetedProjects(facet);
		} catch (CoreException e) {
			return false;
		}
		for (IFacetedProject facetedProject : facetedProjects) {
			QualifiedName qRuntimeName = IJBossESBFacetDataModelProperties.PERSISTENCE_PROPERTY_QNAME_RUNTIME_NAME;
			try {
				if (name.equals(facetedProject.getProject()
						.getPersistentProperty(qRuntimeName))) {
					return true;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void load() {
		IPreferenceStore ps = ESBProjectCorePlugin.getDefault()
				.getPreferenceStore();

		String runtimeListString = ps
				.getString(JBossFacetCoreMessages.ESB_Location);

		runtimes = converter.getMap(runtimeListString);
	}
	
	public static boolean isValidESBServer(String path, String version, String configuration){
		
		return isValidESBStandaloneRuntimeDir(path, version, configuration);
	}
	
//	private static boolean isValidSoapContainedESBRuntime(String path, String version){
//		return  isValidESBStandaloneRuntimeDir(path, version);
//	}
	
	@Deprecated
	public static boolean getResttaJar(String path, String asFoldername, String version, String configuration){
		return isValidESBStandaloneRuntimeDir(path, version, configuration);
	}
	
	
	
	
	public static boolean isValidESBStandaloneRuntimeDir(String path, String version, String configuration) {
		IESBRuntimeResolver resolver = null;
		if( parserMap.get(version) != null){
			resolver = (IESBRuntimeResolver)parserMap.get(version);
		}
		
		if(resolver != null){
			return resolver.isValidESBRuntime(path, version, configuration);
		}
		else{
			ESBProjectCorePlugin.log("No ESB runtime resolver defined for ESB "+ version, null, Status.WARNING);
		}
		
		return false;
		
//		IPath location = new Path(path);
//		IPath esblocation = location.append("lib").append("jbossesb.esb");
//		IPath sarLocation = location.append("lib").append("jbossesb.sar");
//		if (!esblocation.toFile().isDirectory()) {
//			return false;
//		}
//		if (!sarLocation.toFile().isDirectory()) {
//			return false;
//		}
//		
//		try{
//			double versionNumber = Double.valueOf(version);
//			if(versionNumber >= 4.5){
//				return isVersion45(sarLocation);
//			}
//		}catch(NumberFormatException e){
//		}
//
//		return true;
	}
	

	public String getESBVersionNumber(File rosettaJar){
		
		return "";
		
	}
	
	public static void loadParsers() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(PLUGIN_ID, "esbRuntimeResolver");
		for (IConfigurationElement element : extensionPoint
				.getConfigurationElements()) {
			String clazz = element.getAttribute(ATT_CLASS);
			String esbVersion = element.getAttribute(ATT_VERSION);
			
			IConfigurationElement[] supportedRTs = element.getChildren("supportedRuntimeType");
			List<String> runtimeTypeIds = new ArrayList<String>();
			for(IConfigurationElement supportedRT : supportedRTs){
				runtimeTypeIds.add(supportedRT.getAttribute(ATT_ID));
			}
			
			if (clazz == null || (esbVersion == null && runtimeTypeIds.size() == 0)) {
				continue;
			}
			
			
			IESBRuntimeResolver parser = null;
			try {
				parser = (IESBRuntimeResolver) element
						.createExecutableExtension(ATT_CLASS);
				
				if (esbVersion != null && !"".equals(esbVersion.trim())) {
					String[] versions = esbVersion.split(VERSION_SEPARATOR);
					for (String version : versions) {
						parserMap.put(version, parser);
					}
				}
				
				for(String typeId : runtimeTypeIds){
					parserMap.put(typeId, parser);
				}

			} catch (CoreException e) {
				ESBProjectCorePlugin.log(e.getLocalizedMessage(), e);
			}
		}
	}

	
	public  String getVersion(String location, String configuration){
		String version = "";
		File rosettaJar = null;
		Collection<IESBRuntimeResolver> resolvers = parserMap.values();
		for(IESBRuntimeResolver resolver : resolvers){
			rosettaJar = resolver.getRosettaJar(location, configuration);
			if(rosettaJar != null && rosettaJar.exists()){
				break;
			}
		}
		
		if(rosettaJar == null || !rosettaJar.exists()){
			return "";
		}
		
		try {
			ZipFile zfile = new ZipFile(rosettaJar);
			ZipEntry entry = zfile.getEntry(VERSION_FILE_NAME);
			
			if(entry == null) return "";
			
			InputStream input = zfile.getInputStream(entry);
			Properties properties = new Properties();
			properties.load(input);
			version = properties.getProperty(VERSION_PROPERTIES_KEY);
			
			
			if(version == null){
				return "";
			}
			// soa-p5.0 and higher
			else if(version.indexOf(VERSION_PROPERTIES_SEPERATOR) > 0){
				version = version.substring(0, version.indexOf(VERSION_PROPERTIES_SEPERATOR));
			}
			//soa-p 4.3
			else if(version.equals("4.3.0")) {
				version = "4.4";
			}
			else if(version.length() > 3){
				version = version.substring(0,3);
			}
			
		} catch (ZipException e) {
			ESBProjectCorePlugin.log(e.getLocalizedMessage(), e);
		} catch (IOException e) {
			ESBProjectCorePlugin.log(e.getLocalizedMessage(), e);
		}
		
		return version;
	}
	
	
}