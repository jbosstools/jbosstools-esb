/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.esb.core.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ESBRuntimeResolver_42 extends AbstractESBRuntimeResolver implements IESBRuntimeResolver {
	
	
	private final static String CONFIG_MODEL_JAR = "jbossesb-config-model-1.1.0.jar";

	
	public boolean isValidESBRuntime(String location, String version, String configuration){
		List<String> jarNames = new ArrayList<String>();
		
		for(File file : getAllRuntimeJars(location, configuration)){
			jarNames.add(file.getName());
		}
		
		if("4.5".equals(version) || "4.6".equals(version)){
			return jarNames.contains(ROSETTA_JAR) && jarNames.contains(CONFIG_MODEL_JAR);
		}
		
		return jarNames.contains(ROSETTA_JAR);
	}

}
