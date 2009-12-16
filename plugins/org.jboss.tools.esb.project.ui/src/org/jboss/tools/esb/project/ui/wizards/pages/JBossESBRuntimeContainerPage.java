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
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUICore;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeClassPathInitializer;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.esb.project.ui.ESBProjectPlugin;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;

public class JBossESBRuntimeContainerPage extends WizardPage implements
		IClasspathContainerPage {

	private IClasspathEntry entry;
	private TableViewer runtimeViewer;
	private Object selectedRuntime;
	
	public JBossESBRuntimeContainerPage(){
		super(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_Title);
		setTitle(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_Title);
		setDescription(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_Description);
	}
	
	public JBossESBRuntimeContainerPage(String pageName) {
		super(pageName);
	}
	

	public JBossESBRuntimeContainerPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public void createControl(Composite parent) {
		Composite com = new Composite(parent, SWT.NONE);
		com.setLayout(new GridLayout());
		com.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Table table = new Table(com, SWT.BORDER);
		runtimeViewer = new TableViewer(table);
		runtimeViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		runtimeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (StructuredSelection)event.getSelection();
				selectedRuntime = selection.getFirstElement();
				setPageComplete(isPageComplete());
				
			}});
		runtimeViewer.addFilter(new ESBRuntimeFilter());
		
		TableLayout tablelayout = new TableLayout();
		table.setLayout(tablelayout);
		
		tablelayout.addColumnData(new ColumnWeightData(60));
		TableColumn tc1 = new TableColumn(table, SWT.NONE);
		tc1.setText(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_Name);		
		tablelayout.addColumnData(new ColumnWeightData(40));
		TableColumn tc2 = new TableColumn(table, SWT.NONE);
		tc2.setText(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_RuntimeType);
		
		tc1.pack();
		tc2.pack();
		table.setHeaderVisible(true);
		
		
		
		runtimeViewer.setContentProvider(new ArrayContentProvider());
		runtimeViewer.setLabelProvider(new RuntimeLabelProvider());
		
		runtimeViewer.setInput(getAllAvailableESBRuntimes());
		setControl(com);
	}

	public boolean finish() {
		IStructuredSelection selection = (StructuredSelection)runtimeViewer.getSelection();
		Object obj = selection.getFirstElement();
		IPath path = new Path(JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_CONTAINER_ID);
		if(obj instanceof IRuntime){
			path = path.append(JBossRuntimeClassPathInitializer.JBOSS_ESB_RUNTIME_CLASSPATH_SERVER_SUPPLIED);
			path = path.append(((IRuntime)obj).getId());
		}else if(obj instanceof JBossESBRuntime){
			path = path.append(((JBossESBRuntime)obj).getName());
		}
		entry = JavaCore.newContainerEntry(path);
		return true;
	}

	public IClasspathEntry getSelection() {

		return entry;
	}

	public void setSelection(IClasspathEntry containerEntry) {
		entry = containerEntry;
	}

	private List getAllAvailableESBRuntimes(){
		List runtimes = new ArrayList();
		JBossESBRuntime[] preRuntimes = JBossRuntimeManager.getInstance().getRuntimes();
		IRuntime[] serverRuntimes = ServerCore.getRuntimes();
		List<JBossESBRuntime> preList = Arrays.asList(preRuntimes);
		List<IRuntime> serverRuntimeList = Arrays.asList(serverRuntimes); 
		runtimes.addAll(preList);
		runtimes.addAll(serverRuntimeList);
		
		return runtimes;	
		
	}
	
	
	
	@Override
	public boolean isPageComplete() {
		 
		return selectedRuntime != null;
	}

	class ESBRuntimeFilter extends ViewerFilter{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if(element instanceof IRuntime){
				IPath location = ((IRuntime)element).getLocation();
				String runtimeType = ((IRuntime)element).getRuntimeType().getId();
				if(location == null) return false;
				IJBossServerRuntime jbossRuntime = (IJBossServerRuntime)((IRuntime)element).loadAdapter(IJBossServerRuntime.class, new NullProgressMonitor());
				if( jbossRuntime == null )
					return false;
				return JBossRuntimeManager.isValidESBServer(location.toOSString(), runtimeType, jbossRuntime.getJBossConfiguration());
			}
			return true;
		}
		
	}

	class RuntimeLabelProvider implements ITableLabelProvider{

		private ILabelProvider serverLabel = ServerUICore.getLabelProvider();
		
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == 0){
				if(element instanceof IRuntime){
					return serverLabel.getImage(element);
				}else{
					return ESBProjectPlugin.getDefault().getImageRegistry().get(JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_5);
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				if(element instanceof IRuntime){
					return serverLabel.getText(element);
				}else if(element instanceof JBossESBRuntime){
					return ((JBossESBRuntime)element).getName();
				}
				break;
			case 1:
				if(element instanceof IRuntime){
					return JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_RuntimeType_ServerContained;
				}else if(element instanceof JBossESBRuntime){
					return JBossESBUIMessages.JBoss_ESBRuntime_Classpath_Container_RuntimeType_ESBLibrariesOnly;
				}
				break;
			}
			
				
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
