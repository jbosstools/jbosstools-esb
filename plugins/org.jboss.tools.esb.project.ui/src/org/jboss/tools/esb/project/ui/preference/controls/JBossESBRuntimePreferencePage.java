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

package org.jboss.tools.esb.project.ui.preference.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

/**
 * @author Grid Qian
 */
public class JBossESBRuntimePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public JBossESBRuntimePreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	private static final int COLUMNS = 3;

	JBossRuntimeListFieldEditor jbossRuntimes = new JBossRuntimeListFieldEditor(
			"rtlist", JBossESBUIMessages.JBoss_Preference_Page_Runtimes, new ArrayList<JBossESBRuntime>(Arrays.asList(JBossRuntimeManager.getInstance().getRuntimes()))); //$NON-NLS-1$

	/**
	 * Create contents of JBoss ESB preferences page.  list editor
	 * is created
	 * 
	 * @return Control
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(COLUMNS, false);
		root.setLayout(gl);
		jbossRuntimes.doFillIntoGrid(root);

		return root;
	}

	/**
	 * Inherited from IWorkbenchPreferencePage
	 * 
	 * @param workbench
	 *            {@link IWorkbench}
	 * 
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Save JBossRuntime list
	 */
	@Override
	protected void performApply() {
		for (JBossESBRuntime rt : jbossRuntimes.getAddedJBossRuntimes()) {
			JBossRuntimeManager.getInstance().addRuntime(rt);
		}
		jbossRuntimes.getAddedJBossRuntimes().clear();
		for (JBossESBRuntime rt : jbossRuntimes.getRemoved()) {
			JBossRuntimeManager.getInstance().removeRuntime(rt);
		}
		jbossRuntimes.getRemoved().clear();
		JBossESBRuntime defaultRuntime = jbossRuntimes
				.getDefaultJBossRuntime();
		// reset default runtime
		for (JBossESBRuntime jbossWSRuntime : JBossRuntimeManager
				.getInstance().getRuntimes()) {
			jbossWSRuntime.setDefault(false);
		}
		// set deafult runtime
		if (defaultRuntime != null) {
			defaultRuntime.setDefault(true);
		}
		jbossRuntimes.setDefaultJBossRuntime(null);
		Map<JBossESBRuntime, JBossESBRuntime> changed = jbossRuntimes
				.getChangedJBossRuntimes();
		for (JBossESBRuntime c : changed.keySet()) {
			JBossESBRuntime o = changed.get(c);
			o.setHomeDir(c.getHomeDir());
			o.setVersion(c.getVersion());
			o.setConfiguration(c.getConfiguration());
			String oldName = o.getName();
			String newName = c.getName();
			if (!oldName.equals(newName)) {
				JBossRuntimeManager.getInstance().changeRuntimeName(oldName,
						newName);
			}
			o.setDefault(c.isDefault());
			o.setUserConfigClasspath(c.isUserConfigClasspath());
			o.setLibraries(c.getLibraries());
		}
		jbossRuntimes.getChangedJBossRuntimes().clear();

		JBossRuntimeManager.getInstance().save();
	}

	/**
	 * Restore original preferences values
	 */
	@Override
	protected void performDefaults() {
		setValid(true);
		setMessage(null);
		performApply();
	}

	/**
	 * See {@link PreferencePage} for details
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}

	public JBossRuntimeListFieldEditor getJBossRuntimes() {
		return jbossRuntimes;
	}
}
