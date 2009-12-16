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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denny Xu
 */
public class JBossESBRuntime {

	String name = null;

	String homeDir = null;
	
	String version = null;
	
	String configuration = "";

	boolean defaultRt = false;
	
	List<String> libraries;

	private boolean userConfigClasspath;

	/**
	 * Default constructor
	 */
	public JBossESBRuntime() {
		libraries = new ArrayList<String>();
	}

	/**
	 * Get JBossWSRuntime name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get path to home directory
	 * 
	 * @return home directory path as string
	 */
	public String getHomeDir() {
		return homeDir;
	}
	
	
	/**
	 * Get jboss server configuration
	 * 
	 * @return JBoss configuration as string
	 */
	public String getConfiguration(){
		return configuration;
	}

	/**
	 * Set JBossWSRuntime name
	 * 
	 * @param name
	 *            new JBossWSRuntime name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set home directory
	 * 
	 * @param homeDir
	 *            new JBossWSRuntime's home directory
	 */
	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}
	
	
	
	/**
	 * Set JBoss server configuration, it is only for jboss AS contained ESB runtime
	 * 
	 * @param configuration
	 *            new JBossWSRuntime's configuration
	 */
	public void setConfiguration(String configuration){
		this.configuration = configuration;
	}

	/**
	 * Mark runtime as default
	 * 
	 * @param b
	 *            new value for default property
	 */
	public void setDefault(boolean b) {
		this.defaultRt = b;
	}

	/**
	 * Get default flag
	 * 
	 * @return default property
	 */
	public boolean isDefault() {
		return defaultRt;
	}
	
	public  List<String> getLibraries(){
		
		return this.libraries;
	}
	
	public void setLibraries(List<String> libraries){
		this.libraries = libraries;
	}
	
	public boolean isUserConfigClasspath(){
		return this.userConfigClasspath;
	}
	
	public void setUserConfigClasspath(boolean userConfigClasspath){
		this.userConfigClasspath = userConfigClasspath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
