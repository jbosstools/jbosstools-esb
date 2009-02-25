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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.esb.core.ESBProjectCorePlugin;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.messages.JBossFacetCoreMessages;

/**
 * @author Denny Xu
 */
public class JBossRuntimeManager {

	private static JBossRuntimeListConverter converter = new JBossRuntimeListConverter();

	private Map<String, JBossRuntime> runtimes = new HashMap<String, JBossRuntime>();
	
	private final static String JBOSSESB_ESB = "jbossesb.esb";
	private final static String JBOSSESB_SAR = "jbossesb.sar";
	private final static String SOAP_AS_LOCATION = "jboss-as";

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
	public JBossRuntime[] getRuntimes() {
		Collection<JBossRuntime> c = runtimes.values();
		return c.toArray(new JBossRuntime[runtimes.size()]);
	}

	/**
	 * Add new JBossWSRuntime
	 * 
	 * @param runtime
	 *            JBossWSRuntime
	 */
	public void addRuntime(JBossRuntime runtime) {
		if (runtimes.size() == 0) {
			runtime.setDefault(true);
		}

		JBossRuntime oldDefaultRuntime = getDefaultRuntime();
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
		JBossRuntime jbossWSRt = new JBossRuntime();
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
	public JBossRuntime findRuntimeByName(String name) {
		for (JBossRuntime jbossWSRuntime : runtimes.values()) {
			if (jbossWSRuntime.getName().equals(name)) {
				return jbossWSRuntime;
			}
		}
		return null;
	}

	public JBossRuntime[]  findRuntimeByVersion(String version) {
		if(version == null || "".equals(version)){
			return getRuntimes();
		}
		List<JBossRuntime> rts = new ArrayList<JBossRuntime>();
		for (JBossRuntime jbossWSRuntime : runtimes.values()) {
			if (jbossWSRuntime.getVersion().equals(version)) {
				rts.add(jbossWSRuntime);
			}
		}
		return rts.toArray(new JBossRuntime[]{});
	}
	
	public List<String> getAllRuntimeJars(JBossRuntime rt){
		List<String> jarList = new ArrayList<String>();
		if (rt != null) {
			if (rt.isUserConfigClasspath()) {
				jarList.addAll(rt.getLibraries());
				 
			} else {

				jarList = getAllRuntimeJars(rt.getHomeDir());
			}
			
		}
		return jarList;
	}
	
	public List<String> getAllRuntimeJars(String runtimeLocation) {
		List<String> jarList = new ArrayList<String>();

		IPath rtHome = new Path(runtimeLocation);
		IPath soapDeployPath = rtHome.append(SOAP_AS_LOCATION).append("server").append("default").append(
		"deploy");
		IPath deployPath = rtHome.append("server").append("default").append(
				"deploy");

		IPath esbPath = deployPath.append(JBOSSESB_ESB);
		IPath sarPath = deployPath.append(JBOSSESB_SAR);

		IPath libPath = rtHome.append("lib");

		//if it's not a normal jboss AS , try to treat it as standalone esb runtme
		if (!esbPath.toFile().exists() || !sarPath.toFile().exists()) {
			esbPath = libPath.append(JBOSSESB_ESB);
			sarPath = libPath.append(JBOSSESB_SAR);
		}
		
		if (!esbPath.toFile().exists() || !sarPath.toFile().exists()) {
			esbPath = soapDeployPath.append(JBOSSESB_ESB);
			sarPath = soapDeployPath.append(JBOSSESB_SAR);
		}
		
		List<File> esblibs = getJarsOfFolder(esbPath.toFile());
		IPath sarLibPath = sarPath.append("lib");
		List<File> sarJars = getJarsOfFolder(sarLibPath.toFile());
		// /List<File> commonLibs = getJarsOfFolder(libPath.toFile());
		// List<File> tmpLibs = mergeTwoFileList(esblibs, sarJars);
		// libs.addAll(commonLibs);

		jarList = mergeTwoList(esblibs, sarJars);

		return jarList;
	}
	
	private List<File> getJarsOfFolder(File folder){
		List<File> jars = new ArrayList<File>();
		if(folder.isDirectory()){
			for(File file: folder.listFiles()){
				if(file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))){
					jars.add(file);
				}else if (folder.isDirectory()){
					jars.addAll(getJarsOfFolder(file));
				}
			}
		}
		
		return jars;
	}
	
	// if two folders have the same jar file, one of them will be ignored.
	private List<String> mergeTwoList(List<File> jarList1, List<File> jarList2){
		List<String> rtList = new ArrayList<String>();
		List<String> distinctFileNames = new ArrayList<String>();
		
		for(File jarFile: jarList1){
			distinctFileNames.add(jarFile.getName());
			rtList.add(jarFile.getAbsolutePath());
		}
		for(File jarFile: jarList2){
			if(!distinctFileNames.contains(jarFile.getName())){
				rtList.add(jarFile.getAbsolutePath());
			}
		}
		
		return rtList;
		
	}
	
	private List<File> mergeTwoFileList(List<File> jarList1, List<File> jarList2){
		List<File> rtList = new ArrayList<File>();
		List<String> distinctFileNames = new ArrayList<String>();
		
		for(File jarFile: jarList1){
			distinctFileNames.add(jarFile.getName());
			rtList.add(jarFile);
		}
		for(File jarFile: jarList2){
			if(!distinctFileNames.contains(jarFile.getName())){
				rtList.add(jarFile);
			}
		}
		
		return rtList;
		
	}
	
	/**
	 * Remove given JBossWSRuntime from manager
	 * 
	 * @param rt
	 *            JBossWSRuntime
	 */
	public void removeRuntime(JBossRuntime rt) {
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
	public void setDefaultRuntime(JBossRuntime runtime) {
		JBossRuntime[] runtimes = getRuntimes();
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
	public JBossRuntime getDefaultRuntime() {
		for (JBossRuntime rt : runtimes.values()) {
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
		JBossRuntime[] rts = getRuntimes();
		List<String> result = new ArrayList<String>();
		for (JBossRuntime jbossWSRuntime : rts) {
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
		JBossRuntime[] rts = getRuntimes();
		List<String> result = new ArrayList<String>();
		for (JBossRuntime jbossWSRuntime : rts) {
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
		JBossRuntime o = findRuntimeByName(oldName);
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
	
	public static boolean isValidESBServer(String path, String version){
		return getResttaJar(path, "" , version) || isValidSoapContainedESBRuntime(path, version);
	}
	
	private static boolean isValidSoapContainedESBRuntime(String path, String version){
		return  getResttaJar(path, SOAP_AS_LOCATION, version);
	}
	
	public static boolean getResttaJar(String path, String asFoldername, String version){
		IPath serverLocation = new Path(path);
		if(asFoldername != null && !"".equals(asFoldername)){
			serverLocation = serverLocation.append(asFoldername);
		}
		IPath sarLocation = serverLocation.append( "server" + File.separator + "default"
		+ File.separator + "deploy" + File.separator
		+ "jbossesb.sar");
		IPath rosettaJar  = sarLocation.append("lib" + File.separator +  "jbossesb-rosetta.jar");
		
		try{
			double versionNumber = Double.valueOf(version);
			if(versionNumber >= 4.5){
				return isVersion45(sarLocation);
			}
		}catch(NumberFormatException e){
		}
		return rosettaJar.toFile().exists(); 
	}
	
	public static boolean isValidESBStandaloneRuntimeDir(String path, String version) {
		IPath location = new Path(path);
		IPath esblocation = location.append("lib").append("jbossesb.esb");
		IPath sarLocation = location.append("lib").append("jbossesb.sar");
		if (!esblocation.toFile().isDirectory()) {
			return false;
		}
		if (!sarLocation.toFile().isDirectory()) {
			return false;
		}
		
		try{
			double versionNumber = Double.valueOf(version);
			if(versionNumber >= 4.5){
				return isVersion45(sarLocation);
			}
		}catch(NumberFormatException e){
		}

		return true;
	}
	
	private static boolean isVersion45(IPath sarLocation){
		IPath libPath = sarLocation.append("lib");
		File jbossesbConfigModel110 = libPath.append("jbossesb-config-model-1.1.0.jar").toFile();
		return jbossesbConfigModel110 != null && jbossesbConfigModel110.exists();
	}

	public String getESBVersionNumber(File rosettaJar){
		
		return "";
		
	}
	
}