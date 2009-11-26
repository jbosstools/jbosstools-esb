package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;

public interface IESBRuntimeResolver {

	
	public boolean isValidESBRuntime(String location, String version);
	
	public List<IPath> getJarDirectories(String runtimeLocation);
	
	public List<File> getAllRuntimeJars(String runtimeLocation);
}
