package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ESBRuntimeResolver_47 extends AbstractESBRuntimeResolver implements
		IESBRuntimeResolver {

	private final static String CONFIG_MODEL_JAR_11 = "jbossesb-config-model-1.1.0.jar";
	private final static String CONFIG_MODEL_JAR_12 = "jbossesb-config-model-1.2.0.jar";

	
	public boolean isValidESBRuntime(String location, String version){
		List<String> jarNames = new ArrayList<String>();
		
		for(File file : getAllRuntimeJars(location)){
			jarNames.add(file.getName());
		}
			
		return jarNames.contains(ROSETTA_JAR)
				&& jarNames.contains(CONFIG_MODEL_JAR_11)
				&& jarNames.contains(CONFIG_MODEL_JAR_12);
	}
	
	
	public List<IPath> getJarDirectories(String runtimeLocation) {
		List<IPath> directories = super.getJarDirectories(runtimeLocation);
		IPath rtHome = new Path(runtimeLocation);
		IPath soapDeployPath = rtHome.append(SOAP_AS_LOCATION).append("server").append("default").append(
		"deployers").append("esb.deployer").append("lib");
		
		IPath deployPath = rtHome.append("server").append("default").append(
				"deployers").append("esb.deployer").append("lib");
		directories.add(soapDeployPath);
		directories.add(deployPath);
		
		return directories;
	}
	

}
