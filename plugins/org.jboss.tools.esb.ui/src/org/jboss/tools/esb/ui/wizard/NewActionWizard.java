/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.common.model.ui.wizards.NewClassWizard;
import org.jboss.tools.common.model.ui.wizards.NewTypeWizardAdapter;
import org.jboss.tools.esb.ui.ESBUIMessages;
import org.jboss.tools.esb.ui.ESBUiPlugin;

public class NewActionWizard extends NewClassWizard implements INewWizard {
	boolean openCreatedType = false;

	public NewActionWizard() {
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(ESBUIMessages.newESBActionWizardTitle);
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
	}

	public void addPages() {
//		super.addPages();
		mainPage = new NewActionWizardPage();
		addPage(mainPage);
		if (adapter!=null) mainPage.init(adapter);

		mainPage.setTitle(ESBUIMessages.newESBActionWizardPageTitle);
	}

	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		mainPage.createType(monitor);
	}

	@Override
	public boolean performFinish() {
		boolean b = super.performFinish();
		if(b) {
			if(openCreatedType) {
				Display.getDefault().asyncExec(new Runnable(){
					public void run() {
						try {
							JavaUI.openInEditor(mainPage.getCreatedType());
						} catch (CoreException e) {
							ESBUiPlugin.getDefault().log(e);
						}
					}
				});
			}
		}
		return b;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		IProject p = getProject(selection);
		adapter = new NewTypeWizardAdapter(p);
		adapter.setRawSuperClassName("org.jboss.soa.esb.actions.AbstractActionPipelineProcessor");
		IPackageFragment f = getPackageFragment(selection);
		if(f != null) {
			String name = "";
			IPackageFragment cf = f;
			while(cf != null) {
				if(name.length() == 0) {
					name = cf.getElementName();
				} else {
					name = cf.getElementName() + "." + name;
				}
				cf = (cf.getParent() instanceof IPackageFragment) ? (IPackageFragment)cf.getParent() : null;
			}
			adapter.setRawPackageName(name);
		}
		adapter.setRawClassName("");
		openCreatedType = true;
	}

	IProject getProject(IStructuredSelection selection) {
		if(selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if(o instanceof IProject) {
			return (IProject)o;
		} else if(o instanceof IJavaElement) {
			IJavaElement e = (IJavaElement)o;
			return e.getJavaProject().getProject();
		} else if(o instanceof IAdaptable) {
			return (IProject)((IAdaptable)o).getAdapter(IProject.class);
		}
		return null;
	}

	IPackageFragment getPackageFragment(IStructuredSelection selection) {
		if(selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if(o instanceof IPackageFragment) {
			return (IPackageFragment)o;
		}
		return null;
	}

}
