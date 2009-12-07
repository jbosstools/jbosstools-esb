package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;

public interface IESBRuntimeResolver {

	
	public boolean isValidESBRuntime(String location, String version, String configuration);
	
	public List<IPath> getJarDirectories(String runtimeLocation, String configuration);
	
	public List<File> getAllRuntimeJars(String runtimeLocation, String configuration);
}
