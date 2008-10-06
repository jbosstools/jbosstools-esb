package org.jboss.tools.esb.project.ui.wizards.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.jboss.tools.esb.core.ESBProjectUtilities;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.runtime.JBossRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.esb.project.ui.preference.controls.JBossRuntimeListFieldEditor;

public class ESBFacetInstallationPage extends AbstractFacetWizardPage implements IFacetWizardPage, IJBossESBFacetDataModelProperties {

	private Label configFolderLabel;
	private Text configFolder;
	private Label contextRootLabel;
	private Text contentFolder;
	private IDataModel model;
	private boolean hasValidContentFolder = true;
	private boolean hasValidSrc = true;
	private boolean hasRuntime = true;
	private Combo cmbRuntimes;
	
	public ESBFacetInstallationPage() {
		super( "esb.facet.install.page"); //$NON-NLS-1$
		setTitle("Install ESB Facet");
		setDescription("Configure project structure and classpath");
		
	}

	private void setDefaultOutputFolder(){
		JavaFacetInstallConfig cfg = findJavaFacetInstallConfig();
		if(cfg != null){
			cfg.setDefaultOutputFolder(new Path(ESBProjectUtilities.BUILD_CLASSES));
		}
	}

	protected Composite createTopLevelComposite(Composite parent) {
		//setInfopopID(IWstWebUIContextIds.NEW_STATIC_WEB_PROJECT_PAGE3);
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		createProjectGroup(composite);
		createRuntimeGroup(composite);
		
		setDefaultOutputFolder();
		
		//synchHelper.synchText(configFolder, CONTENT_DIR, null);
	    Dialog.applyDialogFont(parent);
	    
	    // add listener to listen the changes on the project facet
	    final IFacetedProjectWorkingCopy fpwc = getFacetedProjectWorkingCopy();
		fpwc.addListener(new IFacetedProjectListener(){

			public void handleEvent(IFacetedProjectEvent event) {
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ESBProjectUtilities.ESB_PROJECT_FACET);
				IProjectFacetVersion version = fpwc.getProjectFacetVersion(facet);
				if(version != null){
					initializeRuntimesCombo(cmbRuntimes, null, version.getVersionString());
				}else{
					initializeRuntimesCombo(cmbRuntimes, null);
				}
				
			}
		}, IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED);
	    
		return composite;
	}
	
	private void createProjectGroup(Composite parent){
		
		Group prjGroup = new Group(parent, SWT.BORDER);
		prjGroup.setText("Project Folders");
		prjGroup.setLayout(new GridLayout(1, false));
		prjGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		this.contextRootLabel = new Label(prjGroup, SWT.NONE);
		this.contextRootLabel.setText("Content Directory");
		this.contextRootLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.contentFolder = new Text(prjGroup, SWT.BORDER);
		this.contentFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.contentFolder.setData("label", this.contextRootLabel); //$NON-NLS-1$
		this.contentFolder.setText(model.getStringProperty(ESB_CONTENT_FOLDER));
		contentFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				 String content = contentFolder.getText();
				 if(content != null && !content.equals("")){
					 model.setProperty(ESB_CONTENT_FOLDER, content);
				 }
				 changePageStatus();
			}
		});
		
		configFolderLabel = new Label(prjGroup, SWT.NONE);
		configFolderLabel.setText("Java Source Directory");
		configFolderLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		configFolder = new Text(prjGroup, SWT.BORDER);
		configFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		configFolder.setData("label", configFolderLabel); //$NON-NLS-1$
		configFolder.setText("src");
		configFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				String srcFolder = configFolder.getText();
				 if(srcFolder != null && !srcFolder.equals("")){
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
		
		Group runtimeGroup = new Group(parent, SWT.BORDER);
		runtimeGroup.setText("JBoss ESB Runtime");
		runtimeGroup.setLayout(new GridLayout(2, false));
		runtimeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
	/*	Button btnServerSupplied = new Button(runtimeGroup, SWT.RADIO);
		 
		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		btnServerSupplied.setLayoutData(gd);*/

		/*Label lblServerSupplied = new Label(runtimeGroup, SWT.NONE);
		
		lblServerSupplied.setText("Server supplied ESB Runtime");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		lblServerSupplied.setLayoutData(gd);

		Button btnUserSupplied = new Button(runtimeGroup, SWT.RADIO);*/
		

		cmbRuntimes = new Combo(runtimeGroup, SWT.READ_ONLY);
		cmbRuntimes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmbRuntimes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String runtimeName = cmbRuntimes.getText();
				JBossRuntime jr = (JBossRuntime) cmbRuntimes
						.getData(runtimeName);
				saveJBosswsRuntimeToModel(jr);
			}
		});
		initializeRuntimesCombo(cmbRuntimes, null);

		Button btnNew = new Button(runtimeGroup, SWT.NONE);
		btnNew.setText("New");
		btnNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newJBossWSRuntime();
				changePageStatus();
			}
		});
		
		if("".equals(cmbRuntimes.getText())){
			hasRuntime = false;
		}
		/*Composite chkcom = new Composite(runtimeGroup, SWT.NONE);
		chkcom.setLayout(new GridLayout(3, true));
		chkcom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button jbpm = new Button(chkcom, SWT.CHECK);
		jbpm.setText("Depends on jbpm.esb");
		
		Button smooks = new Button(chkcom, SWT.CHECK);
		smooks.setText("Depends on Smooks.esb");
		
		Button jbrules = new Button(chkcom, SWT.CHECK);
		jbrules.setText("Depends on jrules.esb");

		Button spring = new Button(chkcom, SWT.CHECK);
		spring.setText("Depends on spring.esb");
		
		Button soap = new Button(chkcom, SWT.CHECK);
		soap.setText("Depends on soap.esb");*/
		
	}
 

	
	/*
	 * create a new jbossws runtime and set user supplied runtime to the new one
	 */
	protected void newJBossWSRuntime() {
		List<JBossRuntime> exists = new ArrayList<JBossRuntime>(Arrays.asList(JBossRuntimeManager.getInstance().getRuntimes()));
		List<JBossRuntime> added = new ArrayList<JBossRuntime>();
		
		JBossRuntimeListFieldEditor.JBossRuntimeNewWizard newRtwizard = new JBossRuntimeListFieldEditor.JBossRuntimeNewWizard(
				exists, added) {
			public boolean performFinish() {
				JBossRuntime rt = getRuntime();
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
		JBossRuntime selectedJbws = null;
		JBossRuntime defaultJbws = null;
		int selectIndex = 0;
		int defaultIndex = 0;
		
		cmRuntime.removeAll();
		if(runtimeName == null || "".equals(runtimeName)){
			runtimeName = model.getStringProperty(IJBossESBFacetDataModelProperties.RUNTIME_ID);
		}
		JBossRuntime[] runtimes = JBossRuntimeManager.getInstance()
				.findRuntimeByVersion(version);
		for (int i = 0; i < runtimes.length; i++) {
			JBossRuntime jr = runtimes[i];
			cmRuntime.add(jr.getName());
			cmRuntime.setData(jr.getName(), jr);
			
			if(jr.getName().equals(runtimeName)){
				selectedJbws = jr;
				selectIndex = i;
			}
			// get default jbossws runtime
			if (jr.isDefault()) {
				defaultJbws = jr;
				defaultIndex = i;
			}
		}
		
		if(selectedJbws != null){
			cmRuntime.select(selectIndex);
			saveJBosswsRuntimeToModel(selectedJbws);
		}else if(defaultJbws != null){
			cmRuntime.select(defaultIndex);
			saveJBosswsRuntimeToModel(defaultJbws);
		}
	}
	
	
	protected void initializeRuntimesCombo(Combo cmRuntime, String runtimeName) {
		IProjectFacetVersion version = getSelectedESBVersion();
		if(version != null){
			initializeRuntimesCombo(cmbRuntimes, null, version.getVersionString());

		}else{
			initializeRuntimesCombo(cmbRuntimes, null, "");

		}
	}
	private IProjectFacetVersion getSelectedESBVersion(){
		IFacetedProjectWorkingCopy fpwc = getFacetedProjectWorkingCopy();
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ESBProjectUtilities.ESB_PROJECT_FACET);
		if(fpwc != null){
			return fpwc.getProjectFacetVersion(facet);
		}else{
			return null;
		}
		
	}
	
	protected void saveJBosswsRuntimeToModel(JBossRuntime jbws) {
	/*	String duplicateMsg = "";
		try {
			duplicateMsg = ESBProjectUtil.getDuplicateJars(model, jbws.getName());
		} catch (JavaModelException e1) {
			JBossESBPlugin.getDefault().getLog().log(
					StatusUtils.errorStatus(e1));
		}*/
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
		
		if(contentFolder.getText().trim().equals("")){
			setErrorMessage("Please specify a valid content folder.");
			hasValidContentFolder = false;
			setPageComplete(isPageComplete());
		}
		else if(configFolder.getText().trim().equals("")){
			setErrorMessage("Please specify a valid source folder.");
			hasValidSrc = false;
			setPageComplete(isPageComplete());
		}else if(!hasRuntime){
			setErrorMessage("Please specify a ESB runtime");
			setPageComplete(isPageComplete());
		}
	/*	else if (!duplicateMsg.equals("")) {
			setErrorMessage("Duplicated jar on classpath:" + duplicateMsg);
		}*/
		else{
			setErrorMessage(null);			
			hasValidSrc = true;
			hasValidContentFolder = true;
			setPageComplete(isPageComplete());

		}
	}
	
	@Override
	public boolean isPageComplete() {
		return hasValidContentFolder 
					&& hasValidSrc
						&& hasRuntime;
	}


/*	private void fillMessageGroup(Composite parent){
		Group messageGroup = new Group(parent, SWT.BORDER);
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

	 
}