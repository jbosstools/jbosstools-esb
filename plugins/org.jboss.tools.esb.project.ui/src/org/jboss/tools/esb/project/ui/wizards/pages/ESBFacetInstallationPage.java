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
package org.jboss.tools.esb.project.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.esb.project.ui.ESBProjectPlugin;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;
import org.jboss.tools.esb.project.ui.preference.controls.JBossRuntimeListFieldEditor;

public class ESBFacetInstallationPage extends AbstractFacetWizardPage implements IFacetWizardPage, IJBossESBFacetDataModelProperties {

	private Label configFolderLabel;
	private Text configFolder;
	private Label contextRootLabel;
	private Text contentFolder;
	private IDataModel model;
	private boolean hasValidContentFolder = true;
	private boolean hasValidSrc = true;
	private boolean hasRuntime = false;
	private Combo cmbRuntimes;
	private Button btnUserSupplied;
	private Button btnServerSupplied;
	private Button btnNew;

	private Combo cmbConfigVersions;

	private IFacetedProjectListener fpListerner;
	private IFacetedProjectWorkingCopy fpwc;
	
	public ESBFacetInstallationPage() {
		super( "esb.facet.install.page"); //$NON-NLS-1$
		setTitle(JBossESBUIMessages.ESBFacetInstallationPage_Title);
		setDescription(JBossESBUIMessages.ESBFacetInstallationPage_Description);
		
	}

	private void setDefaultOutputFolder(){
		JavaFacetInstallConfig cfg = findJavaFacetInstallConfig();
		if(cfg != null){
			cfg.setDefaultOutputFolder(new Path(ESBProjectConstant.BUILD_CLASSES));
		}
	}

	protected Composite createTopLevelComposite(Composite parent) {
		//setInfopopID(IWstWebUIContextIds.NEW_STATIC_WEB_PROJECT_PAGE3);
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		createProjectGroup(composite);
		createRuntimeGroup(composite);
		createConfigVersionGroup(composite);
		setPageComplete(false);
		setDefaultOutputFolder();
		
		//synchHelper.synchText(configFolder, CONTENT_DIR, null);
	    Dialog.applyDialogFont(parent);
	    
	    // add listener to listen the changes on the project facet
	    fpwc = getFacetedProjectWorkingCopy();
	    if(fpListerner == null){
	    	fpListerner = new IFacetedProjectListener(){

				public void handleEvent(IFacetedProjectEvent event) {
					IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
					final IProjectFacetVersion version = fpwc.getProjectFacetVersion(facet);
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							if(version != null){
								initializeRuntimesCombo(cmbRuntimes, null, version.getVersionString());
								initializeConfigVersionCombo(cmbConfigVersions, null, version.getVersionString());
								
							}else{
								initializeRuntimesCombo(cmbRuntimes, null);
								initializeConfigVersionCombo(cmbConfigVersions, null);
							}
							changePageStatus();
						}
						
					});
					
				}
			};
	    }
	    
	    if(fpwc != null){
	    	fpwc.addListener(fpListerner, IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED, 
	    			IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED);
	    }
		//set page status
		changePageStatus();
		
		return composite;
	}
	
	private void createProjectGroup(Composite parent){
		
		Group prjGroup = new Group(parent, SWT.NONE);
		prjGroup.setText(JBossESBUIMessages.ESBFacetInstallationPage_Group_Text_Folder);
		prjGroup.setLayout(new GridLayout(1, false));
		prjGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		this.contextRootLabel = new Label(prjGroup, SWT.NONE);
		this.contextRootLabel.setText(JBossESBUIMessages.ESBFacetInstallationPage_Label_Content_Directory);
		this.contextRootLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.contentFolder = new Text(prjGroup, SWT.BORDER);
		this.contentFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.contentFolder.setData("label", this.contextRootLabel); //$NON-NLS-1$
		this.contentFolder.setText(model.getStringProperty(ESB_CONTENT_FOLDER));
		contentFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				 String content = contentFolder.getText();
				 if(content != null && !content.equals("")){ //$NON-NLS-1$
					 model.setProperty(ESB_CONTENT_FOLDER, content);
				 }
				 changePageStatus();
			}
		});
		
		configFolderLabel = new Label(prjGroup, SWT.NONE);
		configFolderLabel.setText(JBossESBUIMessages.ESBFacetInstallationPage_Label_Source_Directory);
		configFolderLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		configFolder = new Text(prjGroup, SWT.BORDER);
		configFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		configFolder.setData("label", configFolderLabel); //$NON-NLS-1$
		configFolder.setText(JBossESBUIMessages.ESBFacetInstallationPage_Default_SRC_Folder);
		configFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				String srcFolder = configFolder.getText();
				 if(srcFolder != null && !srcFolder.equals("")){ //$NON-NLS-1$
					 model.setProperty(ESB_SOURCE_FOLDER, srcFolder);
					 setConfigFolder(srcFolder);
				 }
				 changePageStatus();
			}

		});
	}
	
	private void setConfigFolder(String folderName){
		JavaFacetInstallConfig cfg = findJavaFacetInstallConfig();
		if(cfg != null){
			cfg.setSourceFolder(new Path(folderName));
		}
	}
	
	
	private IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy(){
		Object obj = model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
		if(obj instanceof IFacetedProjectWorkingCopy){
			return (IFacetedProjectWorkingCopy)obj;
		}
		
		return null;
	}
	private JavaFacetInstallConfig findJavaFacetInstallConfig()
	{
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        if( fpjwc != null )
        {
            final IFacetedProject.Action javaInstallAction
                = fpjwc.getProjectFacetAction( JavaFacetUtils.JAVA_FACET );
            
            if(javaInstallAction == null) return null;
            
            final Object config = javaInstallAction.getConfig();
            
            if( config instanceof JavaFacetInstallConfig )
            {
                return (JavaFacetInstallConfig) config;
            }
            else
            {
                return (JavaFacetInstallConfig) Platform.getAdapterManager().getAdapter( config, JavaFacetInstallConfig.class );
            }
        }
        
        return null;
	}

	private void createRuntimeGroup(Composite parent){
		
		Group runtimeGroup = new Group(parent, SWT.NONE);
		runtimeGroup.setText(JBossESBUIMessages.ESBFacetInstallationPage_Group_Runtime_Text);
		runtimeGroup.setLayout(new GridLayout(3, false));
		runtimeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btnServerSupplied = new Button(runtimeGroup, SWT.RADIO);
		btnServerSupplied.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e) {
				setServerSuppliedSelection(e);
			}
		});
		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		btnServerSupplied.setLayoutData(gd);

		Label lblServerSupplied = new Label(runtimeGroup, SWT.NONE);
		lblServerSupplied.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e) {
				btnServerSupplied.setSelection(true);
				setServerSuppliedSelection(e);
			}
		});
		
		lblServerSupplied.setText(JBossESBUIMessages.ESBFacetInstallationPage_Label_Server_Supplied_Runtime);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		lblServerSupplied.setLayoutData(gd);

		btnUserSupplied = new Button(runtimeGroup, SWT.RADIO);
		btnUserSupplied.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setUserSuppliedSelection(e);
			}
		});

		cmbRuntimes = new Combo(runtimeGroup, SWT.READ_ONLY);
		cmbRuntimes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmbRuntimes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String runtimeName = cmbRuntimes.getText();
				JBossESBRuntime jr = (JBossESBRuntime) cmbRuntimes
						.getData(runtimeName);
				saveJBossESBRuntimeToModel(jr);
				changePageStatus();
			}
		});
		initializeRuntimesCombo(cmbRuntimes, null);

		btnNew = new Button(runtimeGroup, SWT.NONE);
		btnNew.setText(JBossESBUIMessages.ESBFacetInstallationPage_Button_Text_New);
		btnNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newJBossRuntime();
				changePageStatus();
			}
		});
		
		if("".equals(cmbRuntimes.getText())){ //$NON-NLS-1$
			hasRuntime = false;
		}
		
		//set default ESB runtime option
		btnServerSupplied.setSelection(true);
		enableUserSupplied(false);
		
	}
 
	protected void setServerSuppliedSelection(EventObject e) {
		btnServerSupplied.setSelection(true);
		btnUserSupplied.setSelection(false);
		model
				.setBooleanProperty(
						IJBossESBFacetDataModelProperties.RUNTIME_IS_SERVER_SUPPLIED,
						true);
		//remove user supplied properties
		model.setStringProperty(
				IJBossESBFacetDataModelProperties.RUNTIME_ID, null);
		model.setStringProperty(
				IJBossESBFacetDataModelProperties.RUNTIME_HOME,	null);		
		enableUserSupplied(false);		
		//checkServerSuppliedESBRuntime();
		changePageStatus();

	}

	protected void setUserSuppliedSelection(EventObject e) {
		btnServerSupplied.setSelection(false);
		btnUserSupplied.setSelection(true);
		model
				.setBooleanProperty(
						IJBossESBFacetDataModelProperties.RUNTIME_IS_SERVER_SUPPLIED,
						false);
		String runtimename = cmbRuntimes.getText();	
		if(runtimename == null || runtimename.equals("")){ //$NON-NLS-1$
			hasRuntime = false;
		}
		JBossESBRuntime jbRuntime = JBossRuntimeManager.getInstance().findRuntimeByName(runtimename);
		
		
		if (jbRuntime != null) {
			saveJBossESBRuntimeToModel(jbRuntime);
		}
		enableUserSupplied(true);
		changePageStatus();

	}
	
	private boolean checkServerSuppliedESBRuntime() {

		try {
			IFacetedProjectWorkingCopy ifpwc = getFacetedProjectWorkingCopy();
			//when the UI is loaded from esb project creation wizard
			if (ifpwc != null) {
				IRuntime runtime = ifpwc.getPrimaryRuntime();
				if (runtime == null) {
					setMessage(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Have_Not_Set_Target_Runtime, WARNING);
					hasRuntime = true;
					setPageComplete(isPageComplete());
					return false;
				}

				org.eclipse.wst.server.core.IRuntime serverRuntime = ServerCore
						.findRuntime(runtime.getProperty("id")); //$NON-NLS-1$
				IJBossServerRuntime jbossRuntime = (IJBossServerRuntime)serverRuntime.loadAdapter(IJBossServerRuntime.class, new NullProgressMonitor());

				if (!JBossRuntimeManager.isValidESBServer(serverRuntime
						.getLocation().toOSString(), getSelectedESBVersion().getVersionString(), jbossRuntime.getJBossConfiguration())) {
					hasRuntime = true;
					setMessage(NLS.bind(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Invalid_ESB_Runtime, getSelectedESBVersion().getVersionString()), WARNING);
					setPageComplete(isPageComplete());
					return true;
				}
			} 
			// when the UI loaded from project facet properties page 
			else {
				String prjname = model.getStringProperty(FACET_PROJECT_NAME);
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(prjname);
				IFacetedProject fp = ProjectFacetsManager.create(project);
				// if fp != null , the UI will be loaded for an exist project
				if (fp != null) {
					IRuntime runtime = fp.getPrimaryRuntime();
					if (runtime == null) {
						setErrorMessage(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_No_Target_Runtime);
						hasRuntime = false;
						setPageComplete(isPageComplete());
						return false;
					}
					org.eclipse.wst.server.core.IRuntime serverRuntime = ServerCore
							.findRuntime(runtime.getProperty("id"));
					IJBossServerRuntime jbossRuntime = (IJBossServerRuntime)serverRuntime.loadAdapter(IJBossServerRuntime.class, new NullProgressMonitor());

					if (!JBossRuntimeManager.isValidESBServer(serverRuntime
							.getLocation().toOSString(), getSelectedESBVersion().getVersionString(), jbossRuntime.getJBossConfiguration())) {
						setMessage(NLS.bind(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Invalid_ESB_Runtime, getSelectedESBVersion().getVersionString()), WARNING);
						hasRuntime = true;
						setPageComplete(isPageComplete());
						return true;
					}
				} 
			}
			
		} catch (CoreException e) {
			ESBProjectPlugin.getDefault().getLog().log(e.getStatus());
			return false;
		}
		// remove the warning message when users change server runtime to a server runtime that contains  ESB runtime 
		setMessage(null);
		return true;

	}
	
	
	
	private void enableUserSupplied(boolean enabled){
		cmbRuntimes.setEnabled(enabled);
		btnNew.setEnabled(enabled);
	}
	/*
	 * create a new jboss ESB runtime and set user supplied runtime to the new one
	 */
	protected void newJBossRuntime() {
		List<JBossESBRuntime> exists = new ArrayList<JBossESBRuntime>(Arrays.asList(JBossRuntimeManager.getInstance().getRuntimes()));
		List<JBossESBRuntime> added = new ArrayList<JBossESBRuntime>();
		
		JBossRuntimeListFieldEditor.JBossRuntimeNewWizard newRtwizard = new JBossRuntimeListFieldEditor.JBossRuntimeNewWizard(
				exists, added) {
			public boolean performFinish() {
				JBossESBRuntime rt = getRuntime();
				rt.setDefault(true);
				JBossRuntimeManager.getInstance().addRuntime(rt);
				JBossRuntimeManager.getInstance().save();

				return true;
			}
		};
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), newRtwizard);
		if (dialog.open() == WizardDialog.OK) {
			initializeRuntimesCombo(cmbRuntimes, null);
		}
	}
	
	protected void initializeRuntimesCombo(Combo cmRuntime, String runtimeName, String version) {
		JBossESBRuntime selectedJbRuntime = null;
		JBossESBRuntime defaultJbws = null;
		int selectIndex = 0;
		int defaultIndex = 0;
		
		cmRuntime.removeAll();
		if(runtimeName == null || "".equals(runtimeName)){ //$NON-NLS-1$
			runtimeName = model.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_ID);
		}
		JBossESBRuntime[] runtimes = JBossRuntimeManager.getInstance()
				.findRuntimeByVersion(version);
		for (int i = 0; i < runtimes.length; i++) {
			JBossESBRuntime jr = runtimes[i];
			cmRuntime.add(jr.getName());
			cmRuntime.setData(jr.getName(), jr);
			
			if(jr.getName().equals(runtimeName)){
				selectedJbRuntime = jr;
				selectIndex = i;
			}
			// get default jbossws runtime from esb runtime preference 
			if (jr.isDefault()) {
				defaultJbws = jr;
				defaultIndex = i;
			}
		}
		
		if(selectedJbRuntime != null){
			cmRuntime.select(selectIndex);
			if(btnUserSupplied.getSelection()){
				saveJBossESBRuntimeToModel(selectedJbRuntime);
			}
		}else if(defaultJbws != null){
			cmRuntime.select(defaultIndex);
			if(btnUserSupplied.getSelection()){
				saveJBossESBRuntimeToModel(defaultJbws);
			}
		}
	}
	
	
	protected void initializeRuntimesCombo(Combo cmRuntime, String runtimeName) {
		IProjectFacetVersion version = getSelectedESBVersion();
		if(version != null){
			initializeRuntimesCombo(cmbRuntimes, null, version.getVersionString());

		}else{
			initializeRuntimesCombo(cmbRuntimes, null, ""); //$NON-NLS-1$

		}
	}
	private IProjectFacetVersion getSelectedESBVersion(){
		IFacetedProjectWorkingCopy fpwc = getFacetedProjectWorkingCopy();
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
		if(fpwc != null){
			return fpwc.getProjectFacetVersion(facet);
		}else{
			return null;
		}
		
	}
	
	protected void saveJBossESBRuntimeToModel(JBossESBRuntime jbws) {

		if (jbws != null) {
			model.setStringProperty(
					IJBossESBFacetDataModelProperties.RUNTIME_HOME,
					jbws.getHomeDir());
			model.setStringProperty(
					IJBossESBFacetDataModelProperties.RUNTIME_ID, jbws
							.getName());
			hasRuntime = true;
		}else{
			model.setStringProperty(
					IJBossESBFacetDataModelProperties.RUNTIME_ID, null);
			model.setStringProperty(
					IJBossESBFacetDataModelProperties.RUNTIME_HOME,	null);
			hasRuntime = false;
		}
	}
	
	
	 
	private void changePageStatus(){
		//String duplicateMsg = "";
		/*try {
			duplicateMsg = ESBProjectUtil.getDuplicateJars(model, cmbRuntimes.getText());
		} catch (JavaModelException e1) {
			JBossESBPlugin.getDefault().getLog().log(
					StatusUtils.errorStatus(e1));
		}*/
		
		if(!validFolderName(contentFolder.getText())){
			setErrorMessage(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Specify_Content_Folder);
			hasValidContentFolder = false;
			setPageComplete(isPageComplete());
		}
		else if(!validFolderName(configFolder.getText())){
			setErrorMessage(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Specify_Source_Folder);
			hasValidSrc = false;
			setPageComplete(isPageComplete());
		}else if(btnUserSupplied.getSelection() && !hasRuntime){
			setErrorMessage(JBossESBUIMessages.ESBFacetInstallationPage_Error_Message_Specify_ESB_Runtime);
			setPageComplete(isPageComplete());
		}else if(btnServerSupplied.getSelection() && !checkServerSuppliedESBRuntime()){
			return;
		}else{
			setErrorMessage(null);
			// try to remove the warning message once the target server runtime doesn't contains a ESB runtime.
			if(btnUserSupplied.getSelection()){
				setMessage(null);
			}
			hasRuntime = true;
			hasValidSrc = true;
			hasValidContentFolder = true;
			setPageComplete(isPageComplete());

		}
	}
	
	private boolean validFolderName(String folderName) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		return ws.validateName(folderName, IResource.FOLDER).isOK();
	}
	
	@Override
	public boolean isPageComplete() {
		return hasValidContentFolder 
					&& hasValidSrc
						&& hasRuntime;
	}


/*	private void fillMessageGroup(Composite parent){
		Group messageGroup = new Group(parent, SWT.);
		messageGroup.setText("Target Message Product");
		messageGroup.setLayout(new GridLayout(1, false));
		messageGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}
*/
	
	public void setConfig(Object config) {
		this.model = (IDataModel)config;
	}

	
	public void createControl(Composite parent) {

		setControl(createTopLevelComposite(parent));
	}


	public Object create() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		if(fpwc != null){
			fpwc.removeListener(fpListerner);
		}
		super.dispose();
	}

	private void createConfigVersionGroup(Composite parent) {		
		Group configGroup = new Group(parent, SWT.NONE);
		configGroup.setText("ESB Config Version"); //TODO move to messages
		configGroup.setLayout(new GridLayout(3, false));
		configGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData gd = new GridData();

		cmbConfigVersions = new Combo(configGroup, SWT.READ_ONLY);
		cmbConfigVersions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmbConfigVersions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String convigVersionName = cmbConfigVersions.getText();
				saveESBConfigVersionToModel(convigVersionName);
				changePageStatus();
			}
		});
		initializeConfigVersionCombo(cmbConfigVersions, null);
	}

	protected void saveESBConfigVersionToModel(String convigVersionName) {
		model.setStringProperty(
				IJBossESBFacetDataModelProperties.ESB_CONFIG_VERSION, convigVersionName);
	}
 
	protected void initializeConfigVersionCombo(Combo cmVersions, String runtimeName) {
		IProjectFacetVersion version = getSelectedESBVersion();
		if(version != null) {
			initializeConfigVersionCombo(cmbConfigVersions, null, version.getVersionString());

		} else {
			initializeConfigVersionCombo(cmbConfigVersions, null, ""); //$NON-NLS-1$
		}
	}

	protected void initializeConfigVersionCombo(Combo cmVersions, String currentName, String version) {
		cmVersions.removeAll();
		cmVersions.add("1.0.1"); //$NON-NLS-1$
		double versionNumber = 0.0;
		try{
		versionNumber = Double.valueOf(version);
		}
		catch(NumberFormatException ex){
			versionNumber = 0.0;
		}
		if(versionNumber >= 4.5) { //$NON-NLS-1$
			cmVersions.add("1.1.0"); //$NON-NLS-1$
		}
		if(versionNumber >= 4.7){
			cmVersions.add("1.2.0"); //$NON-NLS-1$
		}
		int index = cmVersions.getItemCount() - 1;
		String convigVersionName = cmVersions.getItem(index);
		cmVersions.select(index);
		saveESBConfigVersionToModel(convigVersionName);
	}
	 
}