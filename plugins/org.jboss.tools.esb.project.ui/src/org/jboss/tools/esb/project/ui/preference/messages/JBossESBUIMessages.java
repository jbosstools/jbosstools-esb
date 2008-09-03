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

package org.jboss.tools.esb.project.ui.preference.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Grid Qian
 */
public final class JBossESBUIMessages extends NLS {

	private static final String BUNDLE_NAME = 
			"org.jboss.tools.esb.project.ui.preference.messages.JBossESBUI";		//$NON-NLS-1$

	private JBossESBUIMessages() {
		// Do not instantiate
	}

	public static String Label_JBossWS_Runtime_Load_Error;

	public static String Bin;
	public static String Command;
	public static String Client;
	public static String Lib;
	public static String Endorsed;
	
	public static String Error_JBossWS_Basic_Editor_Composite;
	public static String Error_JBossWS_Basic_Editor_Support;
	public static String Error_JBossWS_Basic_Editor_Different;
	public static String JBossWS_Runtime_List_Field_Editor_Name;
	public static String JBossWS_Runtime_List_Field_Editor_Version;
	public static String JBossWS_Runtime_List_Field_Editor_Path;
	public static String JBossWS_Runtime_List_Field_Editor_Inputelement_Must_Be;
	public static String JBossWS_Runtime_List_Field_Editor_An_Instance_Of_List;
	public static String JBossWS_Runtime_Delete_Confirm_Title;
	public static String JBossWS_Runtime_Delete_Used_Confirm;
	public static String JBossWS_Runtime_Delete_Not_Used_Confirm;
	public static String JBossWS_Runtime_List_Field_Editor_Edit_Runtime;
	public static String JBossWS_Runtime_List_Field_Editor_Modify_Runtime;
	public static String JBossWS_Runtime_List_Field_Editor_New_Runtime;
	public static String Error_JBossWS_Runtime_List_Field_Editor_Path_To_Home_Diretory_Cannot_Be_Empty;
	public static String JBossWS_Runtime_List_Field_Editor_Already_Exists;
	public static String JBossWS_Runtime_List_Field_Editor_Runtime;
	public static String Error_JBossWS_Runtime_List_Field_Editor_Runtime_Name_Is_Not_Correct;
	public static String Error_JBossWS_Runtime_List_Field_Editor_Name_Cannot_Be_Empty;
	public static String Error_JBossWS_Runtime_List_Field_Editor_Version_Cannot_Be_Empty;
	public static String JBossWS_Runtime_List_Field_Editor_Create_A_Runtime;
	public static String JBossWS_Runtime_List_Field_Editor_Home_Folder;
	public static String JBossWS_Composite_Editor_This_Method_Can_Be_Invoked;
	public static String JBossWS_Button_Field_Editor_Browse;
	public static String Error_JBossWS_Button_Field_Editor_Not_Implemented_Yet;
	public static String JBossWS_SWT_Field_Editor_Factory_Browse;
	public static String JBossWS_SWT_Field_Editor_Factory_Select_Home_Folder;
	public static String JBossWS_Runtime_List_Field_Editor_Name2;
	public static String JBossWS_Runtime_Check_Field_Default_Classpath;
	public static String JBossWS_Preference_Page_Runtimes;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, JBossESBUIMessages.class);
	}
}
