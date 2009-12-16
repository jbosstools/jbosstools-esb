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
import java.util.List;

import org.eclipse.core.runtime.IPath;

public interface IESBRuntimeResolver {

	
	public boolean isValidESBRuntime(String location, String version, String configuration);
	
	public List<IPath> getJarDirectories(String runtimeLocation, String configuration);
	
	public List<File> getAllRuntimeJars(String runtimeLocation, String configuration);
}
