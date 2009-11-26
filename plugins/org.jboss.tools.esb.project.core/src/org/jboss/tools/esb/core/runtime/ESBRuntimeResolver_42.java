package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ESBRuntimeResolver_42 extends AbstractESBRuntimeResolver implements IESBRuntimeResolver {
	
	
	private final static String CONFIG_MODEL_JAR = "jbossesb-config-model-1.1.0.jar";

	
	public boolean isValidESBRuntime(String location, String version){
		List<String> jarNames = new ArrayList<String>();
		
		for(File file : getAllRuntimeJars(location)){
			jarNames.add(file.getName());
		}
		
		if("4.5".equals(version) || "4.6".equals(version)){
			return jarNames.contains(ROSETTA_JAR) && jarNames.contains(CONFIG_MODEL_JAR);
		}
		
		return jarNames.contains(ROSETTA_JAR);
	}

}
