/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.esb.project.ui.visualizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;


public class WorkbenchFileSelectionDialog
	extends org.eclipse.ui.dialogs.SelectionDialog {

	// the initial selection
	private IResource initialSelection;

	// message and title
	private String title;
	private String message;
	
	// image
	private Image image;

	// Filters
	private String filterPatterns;
	// the result
	private IPath resultPath;

	// the validation message
	private Label statusMessage;

	//for validating the selection
	private org.eclipse.ui.dialogs.ISelectionValidator validator;

	// the widget group;
	private FileSelectionGroup resourceGroup;
	/**
	 * A WorkbenchFileSelectionDialog takes the following arguments:
	 * <ul>
	 * <li> parentShell, the parent shell of the caller.(required)</li>
	 * <li> initialSelection, (resuired)</li>
	 *           = null, takes the initial selection of the file from store, where it was memorizaed
	 *          != null, starts the selection right from the specified path.
	 * <li> message, shows the message on titel bar for this dialog.(required)</li>
	 * <li> filterPatterns, a string of extentions separated with "," as deliminator (eg; "wsdl, xsd, java")(optional)
	 */
	public WorkbenchFileSelectionDialog(
		Shell parentShell,
		IPath initialSelection,
		String message) {
		this(parentShell, initialSelection, message, null);
	}
	/**
	 * A WorkbenchFileSelectionDialog takes the following arguments:
	 * <ul>
	 * <li> parentShell, the parent shell of the caller.(required)</li>
	 * <li> initialSelection, (resuired)</li>
	 *           = null, takes the initial selection of the file from store, where it was memorizaed
	 *          != null, starts the selection right from the specified path.
	 * <li> message, shows the message on titel bar for this dialog.(required)</li>
	 * <li> filterPatterns, a string of extentions separated with "," as deliminator (eg; "wsdl, xsd, java")(optional)
	 */
	public WorkbenchFileSelectionDialog(
		Shell parentShell,
		IPath initialSelection,
		String message,
		String filterPatterns) {
		super(parentShell);

		IPath initial = initialSelection;
		if (initial == null) {
			// Before launch the dialog, get the path in WorkbenchSlectionDialogStore, which is the path recently selected
			//String pathString = (String)WorkbenchSlectionDialogStore.getInstance().getPreferences(workbenchFileSlectionStoreID);
			//if (pathString.length()>0) {
			//	initial=(IPath)new Path(pathString);
			//}
		}
		try {
			if (initial != null)  {
				this.initialSelection =
					ResourcesPlugin.getWorkspace().getRoot().getFile(initial);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.title = JBossESBUIMessages.WorkbenchFileSelectionDialog_Title; 
		this.message = message;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.filterPatterns = filterPatterns;
	}

	/* (non-Javadoc)
	 * Method declared in Window.
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
		if (image != null)
			shell.setImage(image);
	}
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(
			parent,
			IDialogConstants.CANCEL_ID,
			IDialogConstants.CANCEL_LABEL,
			false);
	}
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		// create composite 
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (statusMessage != null && validator != null) {
					String errorMsg =
						validator.isValid(resourceGroup.getResourceFullPath());
					if (errorMsg == null || errorMsg.equals("")) { //$NON-NLS-1$
						statusMessage.setText(""); //$NON-NLS-1$
						getOkButton().setEnabled(true);
					} else {
						statusMessage.setForeground(
							statusMessage.getDisplay().getSystemColor(SWT.COLOR_RED));
						statusMessage.setText(errorMsg);
						getOkButton().setEnabled(false);
					}
				}
			}
		};

		// file selection group
		resourceGroup =
			new FileSelectionGroup(dialogArea, listener, message, filterPatterns);
		resourceGroup.getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					if (!selection.isEmpty()) {
						if (((IStructuredSelection) selection).getFirstElement() instanceof IFile) {
							IFile resultFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
							resultPath = resultFile.getFullPath();
							okPressed();
						}
					}
				}
			}
		});

		if (initialSelection != null) {
			resourceGroup.setSelectedResource(initialSelection);
		}

		statusMessage = new Label(parent, SWT.NONE);
		statusMessage.setLayoutData(new GridData(GridData.FILL_BOTH));

		return dialogArea;
	}

	/**
	 * Returns the "full path" (i.e. first segment is project name) of the selected file.
	 */
	public IPath getFullPath() {
		return resultPath;
	}
	/**
	 * Closes this dialog.
	 */
	@Override
	protected void okPressed() {
		resultPath = resourceGroup.getResourceFullPath();
		// after file selected, save the path to WorkbenchSlectionDialogStore
		//WorkbenchSlectionDialogStore.getInstance().setPreferences(workbenchFileSlectionStoreID,result.toString());

		super.okPressed();
	}
	/**
	 * Sets the validator to use.
	 */
	public void setValidator(org.eclipse.ui.dialogs.ISelectionValidator validator) {
		this.validator = validator;
	}
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		this.title = title;
	}
	public void setImage(Image image) {
		this.image = image;
	}
}
