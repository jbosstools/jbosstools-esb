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

package org.jboss.tools.esb.project.ui.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Grid Qian
 */
public final class JBossESBUIMessages extends NLS {

	private static final String BUNDLE_NAME = 
			"org.jboss.tools.esb.project.ui.messages.JBossESBUI";		//$NON-NLS-1$

	private JBossESBUIMessages() {
		// Do not instantiate
	}

	public static String Label_JBoss_Runtime_Load_Error;
	
	public static String Error_JBoss_Basic_Editor_Composite;
	public static String Error_JBoss_Basic_Editor_Support;
	public static String Error_JBoss_Basic_Editor_Different;
	public static String JBoss_Runtime_List_Field_Editor_Name;
	public static String JBoss_Runtime_List_Field_Editor_Version;
	public static String JBoss_Runtime_List_Field_Editor_Path;
	public static String JBoss_Runtime_List_Field_Editor_Inputelement_Must_Be_List;
	public static String JBoss_Runtime_Delete_Confirm_Title;
	public static String JBoss_Runtime_Delete_Used_Confirm;
	public static String JBoss_Runtime_Delete_Not_Used_Confirm;
	public static String JBoss_Runtime_List_Field_Editor_Configuration_Description;
	public static String JBoss_Runtime_List_Field_Editor_Edit_Runtime;
	public static String JBoss_Runtime_List_Field_Editor_Modify_Runtime;
	public static String JBoss_Runtime_List_Field_Editor_New_Runtime;
	public static String Error_JBoss_Runtime_List_Field_Editor_Path_To_Home_Diretory_Cannot_Be_Empty;
	public static String JBoss_Runtime_List_Field_Editor_Runtime_Already_Exists;
	public static String JBoss_Runtime_List_Field_Editor_Runtime;
	public static String Error_JBoss_Runtime_List_Field_Editor_Runtime_Name_Is_Not_Correct;
	public static String Error_JBoss_Runtime_List_Field_Editor_Name_Cannot_Be_Empty;
	public static String Error_JBoss_Runtime_List_Field_Editor_Version_Cannot_Be_Empty;
	public static String JBoss_Runtime_List_Field_Editor_Create_A_Runtime;
	public static String JBoss_Runtime_List_Field_Editor_Home_Folder;
	public static String JBoss_Composite_Editor_This_Method_Can_Be_Invoked;
	public static String JBoss_Button_Field_Editor_Browse;
	public static String JBoss_ESBRuntime_Classpath_Container_5;
	public static String JBoss_Runtime_List_Field_Editor_Configuration;
	
	public static String ESBRuntimeContainerPage_Button_Text;

	public static String JBoss_ESBRuntime_Classpath_Container_Description;

	public static String JBoss_ESBRuntime_Classpath_Container_Name;
	public static String JBoss_ESBRuntime_Classpath_Container_Manage_Runtimes_Button;
	public static String JBoss_ESBRuntime_Classpath_Container_RuntimeType;

	public static String JBoss_ESBRuntime_Classpath_Container_RuntimeType_ESBLibrariesOnly;

	public static String JBoss_ESBRuntime_Classpath_Container_RuntimeType_ServerContained;

	public static String JBoss_ESBRuntime_Classpath_Container_Title;

	public static String Error_JBoss_Button_Field_Editor_Not_Implemented_Yet;
	public static String JBoss_SWT_Field_Editor_Factory_Browse;
	public static String JBoss_SWT_Field_Editor_Factory_Select_Home_Folder;
	public static String JBoss_Runtime_List_Field_Editor_Name2;
	public static String JBoss_Runtime_Check_Field_Default_Classpath;
	public static String JBoss_Preference_Page_Runtimes;

	public static String JBossLibraryListFieldEditor_ActionAdd;

	public static String JBossLibraryListFieldEditor_ActionRemove;

	public static String JBossLibraryListFieldEditor_LIBRARY_JARS;

	public static String JBossRuntimeListFieldEditor_ActionAdd;

	public static String JBossRuntimeListFieldEditor_ActionEdit;

	public static String JBossRuntimeListFieldEditor_ActionRemove;

	public static String JBossRuntimeListFieldEditor_ErrorMessageAtLeastOneJar;
	
	
	//esb facet messages
	public static String ESBFacetInstallationPage_Button_Text_New;

	public static String ESBFacetInstallationPage_Default_SRC_Folder;

	public static String ESBFacetInstallationPage_Description;

	public static String ESBFacetInstallationPage_Error_Message_Have_Not_Set_Target_Runtime;

	public static String ESBFacetInstallationPage_Error_Message_Invalid_ESB_Runtime;

	public static String ESBFacetInstallationPage_Error_Message_No_Target_Runtime;

	public static String ESBFacetInstallationPage_Error_Message_Specify_Content_Folder;

	public static String ESBFacetInstallationPage_Error_Message_Specify_ESB_Runtime;

	public static String ESBFacetInstallationPage_Error_Message_Specify_Source_Folder;

	public static String ESBFacetInstallationPage_Group_Runtime_Text;

	public static String ESBFacetInstallationPage_Group_Text_Folder;

	public static String ESBFacetInstallationPage_Label_Content_Directory;

	public static String ESBFacetInstallationPage_Label_Server_Supplied_Runtime;

	public static String ESBFacetInstallationPage_Label_Source_Directory;

	public static String ESBFacetInstallationPage_Title;

	 
	
	
	public static String ESBProjectFirstPage_Description;

	public static String ESBProjectFirstPage_Title;

	public static String ESBProjectWizard_Title;

	public static String ESBExportWizard_NotValidProject;
	public static String ESBExportWizard_Title;
	public static String ESBExportWizard_Description;
	public static String ESBExportWizard_ESBProject;
	

	static {
		NLS.initializeMessages(BUNDLE_NAME, JBossESBUIMessages.class);
	}
}
