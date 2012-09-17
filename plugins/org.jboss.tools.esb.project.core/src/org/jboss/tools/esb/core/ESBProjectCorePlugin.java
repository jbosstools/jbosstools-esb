/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.esb.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.componentcore.internal.util.VirtualReferenceUtilities;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class ESBProjectCorePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.esb.core";

	// The shared instance
	private static ESBProjectCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public ESBProjectCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		JBossRuntimeManager.loadParsers();
		Job job = new Job("ESB Facet Framework Initialization") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				VirtualReferenceUtilities.INSTANCE.addDefaultExtension(ESBProjectConstant.ESB_PROJECT_FACET, ESBProjectConstant.ESB_EXTENSION);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ESBProjectCorePlugin getDefault() {
		return plugin;
	}

	public static void log(String msg,Throwable e) {
		log(msg, e, Status.ERROR);
	}
	
	public static void log(String msg,Throwable e, int serverity) {
		ILog log = ESBProjectCorePlugin.getDefault().getLog();
        IStatus status = new Status(serverity,ESBProjectCorePlugin.PLUGIN_ID,msg,e);
        log.log(status);
	}
}
