/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.project.ui.visualizer;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.ConstraintAdapter;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.constraints.BasicEntityConstraint;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.esb.project.ui.ESBProjectPlugin;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;
import org.jboss.tools.esb.project.ui.visualizer.ESBNode.ESBType;

/**
 * This class serves as a simple read-only way to visualize an ESB configuration
 * graphically. 
 * 
 * @author bfitzpat
 *
 */
public class ESBVisualizerView extends ViewPart  implements IZoomableWorkbenchPart, IShowInTarget {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.jboss.tools.esb.visualizer.views.ESBVisualizerView";//$NON-NLS-1$
	
	private static final String ACTION_ICON_TAG = "/icons/esb/action.gif";//$NON-NLS-1$
	private static final String SERVICE_ICON_TAG = "/icons/esb/service.gif";//$NON-NLS-1$
	private static final String PROVIDER_ICON_TAG = "/icons/esb/provider.gif";//$NON-NLS-1$
	private static final String BUS_ICON_TAG = "/icons/esb/bus.gif";//$NON-NLS-1$
	private static final String PROPERTY_ICON_TAG = "/icons/esb/property.gif";//$NON-NLS-1$
	private static final String LISTENER_ICON_TAG = "/icons/esb/listener.gif";//$NON-NLS-1$
	private static final String ESB_FILE_ICON_TAG = "/icons/esb/esb_file.gif";//$NON-NLS-1$
	private static final String REFRESH_ICON_TAG = "/icons/refresh.gif";//$NON-NLS-1$
	private static final String HORIZONTAL_TREE_LAYOUT_ICON_TAG = "/icons/horizontalTreeLayout.gif";//$NON-NLS-1$
	private static final String VERTICAL_TREE_LAYOUT_ICON_TAG = "/icons/verticalTreeLayout.gif";//$NON-NLS-1$
	private static final String RADIAL_LAYOUT_ICON_TAG = "/icons/radialLayout.gif";//$NON-NLS-1$

	private GraphViewer gv;
	
	private IFile currentFile;
	
	// Some stashed colors
	private Color defaultBorder;

	// menu items
	private Action openESBFileAction;
	private Action openESBFileInEditorAction;
	private Action doubleClickAction;
	
	private boolean doubleClickFreezesNode = false;
	
	// toolbar buttons for different layouts
	private IAction horizontalLayoutAction;
	private IAction verticalLayoutAction;
	private IAction radialLayoutAction;
	private Action refreshLayoutAction;
	
	// the listener we register with the selection service 
	private ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if (sourcepart != ESBVisualizerView.this) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					if (ssel.getFirstElement() instanceof IFile) {
						IFile selectedFile = (IFile) ssel.getFirstElement();
						String path = selectedFile.getLocation().toOSString();
						ESBDomParser parser = new ESBDomParser();
						if (parser.isFileESBConfig(path)) {
							currentFile = selectedFile;
							if (!gv.getGraphControl().isDisposed()) {
								visualizeESB(path);
								refreshLayoutAction.run();
							}
						}
					}
				}
			}
		}
	};

	/**
	 * The constructor.
	 */
	public ESBVisualizerView() {
		// empty
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.gv = new GraphViewer(parent, SWT.NONE);
		gv.getGraphControl().addConstraintAdapter(new ESBViewerConstraintAdapter());
		gv.addDoubleClickListener(new FixNodeDoubleClickListener());
		makeActions();
		hookContextMenu();
		fillToolBar();
//		visualizeESB(null);
		setEmptyGraph();
		horizontalLayoutAction.setChecked(true);
		horizontalLayoutAction.run();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}
	
	/*
	 * Clear all the graph nodes and connections 
	 */
	private void clearGraph() {
		while (gv.getGraphControl().getNodes().size() > 0) {
			GraphNode node = (GraphNode) gv.getGraphControl().getNodes().remove(0);
			node.dispose();
		}
		while (gv.getGraphControl().getConnections().size() > 0) {
			GraphConnection node = (GraphConnection) gv.getGraphControl().getConnections().remove(0);
			node.dispose();
		}
	}
	
	/*
	 * Clear the graph and add a default node
	 */
	private void setEmptyGraph() {
		clearGraph();
		new GraphNode (gv.getGraphControl(), ZestStyles.NODES_CACHE_LABEL, 
				JBossESBUIMessages.ESBVisualizerView_EmptyNodeLabel);
	}
	
	/**
	 * Import the esb XML file and render nodes and connections 
	 * @param filepath
	 */
	public void visualizeESB ( String filepath ) {
		ESBDomParser parser = new ESBDomParser();
		parser.parseXmlFile(filepath);
		clearGraph();
		ESBNodeWithChildren root = parser.getRoot();
		root.setEsbObjectType(ESBType.ESB);
		GraphNode rootnode = new GraphNode(gv.getGraphControl(), 
				ZestStyles.NODES_CACHE_LABEL, 
				root.getName());
		rootnode.setImage(getImageFromPlugin("/icons/esb/esb_file.gif")); //$NON-NLS-1$
		this.defaultBorder = rootnode.getBorderColor();
		rootnode.setData(root);
		drawNodes(rootnode, root);
		drawRefs(rootnode);
		if (horizontalLayoutAction.isChecked()) {
			horizontalLayoutAction.run();
		} else if (verticalLayoutAction.isChecked()) {
			verticalLayoutAction.run();
		} else if (radialLayoutAction.isChecked()) {
			radialLayoutAction.run();
		}
	}
	
	/*
	 * Simple image management
	 * @param path
	 * @return
	 */
	private Image getImageFromPlugin( String path ) {
		if (ESBProjectPlugin.getDefault().getImageRegistry().get(path) == null) {
			ImageDescriptor descriptor = ESBProjectPlugin.getImageDescriptor(path);
			if (descriptor != null) {
				ESBProjectPlugin.getDefault().getImageRegistry().put(path, descriptor);
				return ESBProjectPlugin.getDefault().getImageRegistry().get(path);
			}
		}
		return ESBProjectPlugin.getDefault().getImageRegistry().get(path);
	}
	
	/*
	 * Actually draw the nodes and connections from the parsed ESB xml file
	 * @param root
	 * @param parent
	 */
	private void drawNodes ( GraphNode root, ESBNodeWithChildren parent ) {
		if (parent.hasChildren()) {
			for (int i = 0; i < parent.getChildren().length; i++) {
				ESBNodeWithChildren tp = (ESBNodeWithChildren) parent.getChildren()[i];
				GraphNode p = new GraphNode(gv.getGraphControl(), SWT.NONE, tp.getName());
				if (tp.getEsbObjectType() != null) {
					switch (tp.getEsbObjectType()) {
					case ACTION:
						p.setImage(getImageFromPlugin(ACTION_ICON_TAG));
						break;
					case SERVICE:
						p.setImage(getImageFromPlugin(SERVICE_ICON_TAG));
						break;
					case PROVIDER:
						p.setImage(getImageFromPlugin(PROVIDER_ICON_TAG));
						break;
					case BUS:
						p.setImage(getImageFromPlugin(BUS_ICON_TAG));
						break;
					case PROPERTY:
						p.setImage(getImageFromPlugin(PROPERTY_ICON_TAG));
						break;
					case LISTENER:
						p.setImage(getImageFromPlugin(LISTENER_ICON_TAG));
						break;
					case ESB:
						p.setImage(getImageFromPlugin(ESB_FILE_ICON_TAG));
						break;
					default:
						break;
					}
				}
				p.setData(tp);
				drawNodes(p, tp);
				new GraphConnection(gv.getGraphControl(), ZestStyles.CONNECTIONS_DASH, root, p);
			}
		}
	}

	/*
	 * Draw any references between items
	 * @param root
	 */
	private void drawRefs ( GraphNode root ) {
		if (gv.getGraphControl().getGraphModel().getNodes() != null && gv.getGraphControl().getGraphModel().getNodes().size() > 0) {
			@SuppressWarnings("unchecked")
			Iterator<GraphNode> nodeIter = gv.getGraphControl().getGraphModel().getNodes().iterator();
			while (nodeIter.hasNext()) {
				GraphNode node = nodeIter.next();
				if (node.getData() != null && node.getData() instanceof ESBNodeWithChildren) {
					ESBNodeWithChildren tp = (ESBNodeWithChildren) node.getData();
					if (tp.getRef() != null && tp.getRef().trim().length() > 0) {
						GraphNode refNode = findNode(tp.getRef(), root);
						if (refNode != null) {
							GraphConnection refConnection =
									new GraphConnection(gv.getGraphControl(), ZestStyles.CONNECTIONS_DIRECTED, node, refNode);
							refConnection.changeLineColor(gv.getGraphControl().getDisplay().getSystemColor(
									SWT.COLOR_BLUE));
						}
					}
				}
			}
		}
	}
	
	/*
	 * Look for a node by name (for reference connections)
	 * @param name
	 * @param root
	 * @return
	 */
	private GraphNode findNode ( String name, GraphNode root ) {
		if (name != null && name.trim().length() > 0) {
			if (gv.getGraphControl().getGraphModel().getNodes() != null && gv.getGraphControl().getGraphModel().getNodes().size() > 0) {
				@SuppressWarnings("unchecked")
				Iterator<GraphNode> nodeIter = gv.getGraphControl().getGraphModel().getNodes().iterator();
				while (nodeIter.hasNext()) {
					GraphNode node = nodeIter.next();
					if (node.getText().equalsIgnoreCase(name)) {
						return node;
					}
				}
			}			
		}
		return null;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		gv.getGraphControl().setFocus();
	}

	/*
	 * Fill the view menu 
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");//$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ESBVisualizerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(gv.getGraphControl());
		gv.getGraphControl().setMenu(menu);
		
		// if we decide to add a popup menu...
//		getSite().registerContextMenu(menuMgr, );
	}

	/*
	 * Add any menus
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(doubleClickAction);
	}

	/*
	 * Make the few actions we have 
	 */
	private void makeActions() {
		
		openESBFileInEditorAction = new OpenESBEditorAction();
		
		doubleClickAction = new DoubleClickToggleAction();
		
		refreshLayoutAction = new Action() {
			@Override
			public void run() {
				if (gv != null && gv.getGraphControl().getLayoutAlgorithm() != null) {
					gv.setLayoutAlgorithm(gv.getGraphControl().getLayoutAlgorithm(), true);
				}
			}
		};
		
		refreshLayoutAction.setText(JBossESBUIMessages.ESBVisualizerView_Refresh_Layout_Action_Label);
		refreshLayoutAction.setToolTipText(JBossESBUIMessages.ESBVisualizerView_Refresh_Layout_Action_Label);
		refreshLayoutAction.setImageDescriptor(ESBProjectPlugin.getImageDescriptor(REFRESH_ICON_TAG));

		openESBFileAction = new Action() {
			public void run() {
				
				WorkbenchFileSelectionDialog dialog = new WorkbenchFileSelectionDialog(
						ESBVisualizerView.this.getSite().getShell().getShell(), 
						null, 
						JBossESBUIMessages.ESBVisualizerView_Open_ESB_Config_Dialog_Field_Text,
						".xml");//$NON-NLS-1$
				dialog.setTitle(JBossESBUIMessages.ESBVisualizerView_Open_ESB_Config_Dialog_Title);
				dialog.setImage(getImageFromPlugin(ESB_FILE_ICON_TAG));
				dialog.setValidator(new ISelectionValidator() {
					
					public String isValid(Object selection) {
						if (selection instanceof IPath) {
							IPath selectedpath = (IPath) selection;
							IPath totalPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(selectedpath);
							String path = totalPath.toOSString();
							ESBDomParser parser = new ESBDomParser();
							if (parser.isFileESBConfig(path)) {
								return null;
							}
							return JBossESBUIMessages.ESBVisualizerView_Select_ESB_File_Warning;
						} else if (selection == null) {
							return JBossESBUIMessages.ESBVisualizerView_Select_ESB_File_Warning;
						}
						return null;
					}
				});
				int rtn_code = dialog.open();
				
				if (rtn_code == WorkbenchFileSelectionDialog.OK) {
					IPath resultPath = dialog.getFullPath();
					IPath totalPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(resultPath);
					IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(dialog.getFullPath().toFile().toURI());
					if (files != null && files.length > 0) {
						currentFile = files[0];
					}
					String path = totalPath.toOSString();
					visualizeESB(path);
				}
			}
		};
		openESBFileAction.setText(JBossESBUIMessages.ESBVisualizerView_Open_ESB_Config_Action_Label);
		openESBFileAction.setToolTipText(JBossESBUIMessages.ESBVisualizerView_Open_ESB_Config_Action_Label);
		openESBFileAction.setImageDescriptor(ESBProjectPlugin.getImageDescriptor(ESB_FILE_ICON_TAG));
		
	}

	/*
	 * Fill the toolbar with a few buttons 
	 */
	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(openESBFileAction);
		bars.getMenuManager().add(openESBFileInEditorAction);
		bars.getMenuManager().add(doubleClickAction);

		IToolBarManager toolbar = bars.getToolBarManager();
		
		toolbar.add(refreshLayoutAction);

		horizontalLayoutAction = new LayoutAction(gv, new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		horizontalLayoutAction.setImageDescriptor(ESBProjectPlugin.getImageDescriptor(HORIZONTAL_TREE_LAYOUT_ICON_TAG));
		horizontalLayoutAction.setToolTipText(JBossESBUIMessages.ESBVisualizerView_Use_Horizontal_Tree_Layout_Action_Label);
		horizontalLayoutAction.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equalsIgnoreCase("checked")) {//$NON-NLS-1$
					Boolean state = (Boolean)event.getNewValue();
					if (state.equals(Boolean.TRUE)) {
						verticalLayoutAction.setChecked(false);
						radialLayoutAction.setChecked(false);
					} else {
						if (!verticalLayoutAction.isChecked() &&
								!radialLayoutAction.isChecked())
							horizontalLayoutAction.setChecked(true);
					}
				}
			}
		});
		toolbar.add(horizontalLayoutAction);
		
		verticalLayoutAction = new LayoutAction(gv, new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		verticalLayoutAction.setImageDescriptor(ESBProjectPlugin.getImageDescriptor(VERTICAL_TREE_LAYOUT_ICON_TAG));
		verticalLayoutAction.setToolTipText(JBossESBUIMessages.ESBVisualizerView_Use_Vertical_Tree_Layout_Action_Label);
		verticalLayoutAction.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equalsIgnoreCase("checked")) {//$NON-NLS-1$
					Boolean state = (Boolean)event.getNewValue();
					if (state.equals(Boolean.TRUE)) {
						horizontalLayoutAction.setChecked(false);
						radialLayoutAction.setChecked(false);
					} else {
						if (!horizontalLayoutAction.isChecked() &&
								!radialLayoutAction.isChecked())
							verticalLayoutAction.setChecked(true);
					}
				}
			}
		});
		toolbar.add(verticalLayoutAction);

		radialLayoutAction = new LayoutAction(gv, new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		radialLayoutAction.setImageDescriptor(ESBProjectPlugin.getImageDescriptor(RADIAL_LAYOUT_ICON_TAG));
		radialLayoutAction.setToolTipText(JBossESBUIMessages.ESBVisualizerView_Use_Radial_Layout_Action_Label);
		radialLayoutAction.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equalsIgnoreCase("checked")) {//$NON-NLS-1$
					Boolean state = (Boolean)event.getNewValue();
					if (state.equals(Boolean.TRUE)) {
						horizontalLayoutAction.setChecked(false);
						verticalLayoutAction.setChecked(false);
					} else {
						if (!horizontalLayoutAction.isChecked() &&
								!verticalLayoutAction.isChecked())
							radialLayoutAction.setChecked(true);
					}
				}
			}
		});
		toolbar.add(radialLayoutAction);

		toolbar.add(toolbarZoomContributionViewItem);
	}

	public AbstractZoomableViewer getZoomableViewer() {
		return gv;
	}		

	/*
	 * @author bfitzpat
	 * Check to see if the node has been "frozen" and if so, don't move it
	 * when we redo the layout
	 */
	private class ESBViewerConstraintAdapter implements ConstraintAdapter {
		public void populateConstraint(Object object, LayoutConstraint constraint) {
			if (constraint instanceof BasicEntityConstraint) {

				BasicEntityConstraint basicEntityConstraint = (BasicEntityConstraint) constraint;
				GraphNode graphnode = (GraphNode) object;

				Object data = graphnode.getData();
				if (data != null && data instanceof ESBNode) {
					boolean wasMoved = ((ESBNode)data).isMovementLocked();
					basicEntityConstraint.hasPreferredLocation = wasMoved;	
				}
			}
		}
	}
	
	/*
	 * Opens the currently displayed file in the ESB editor with the path pre-selected 
	 */
	private void openESBAction ( ) {
		if (currentFile != null) {
			Graph graph = gv.getGraphControl();
			String modelpath = ""; //$NON-NLS-1$
			if (!graph.getSelection().isEmpty()) {
				GraphNode node = (GraphNode) graph.getSelection().get(0);
				if (node.getData() != null && node.getData() instanceof ESBNode) {
					ESBNode esbnode = (ESBNode) node.getData();
					while (esbnode != null && esbnode.getParent() != null) {
						if (modelpath.trim().length() > 0) 
							modelpath = "/" + modelpath; //$NON-NLS-1$
						modelpath = esbnode.getName() + modelpath;
						esbnode = esbnode.getParent();
					}

//					boolean useIdeMethod = false;
//					if (useIdeMethod) {
//						modelpath = "FileSystems/project/" + currentFile.getProjectRelativePath().toString() + "/" + modelpath;
//						// modelpath = "FileSystems/project/esbcontent/META-INF/jboss-esb.xml/Services/SimpleListener";
//						
//						IWorkbenchPage page = getSite().getPage();
//						HashMap<String, String> map = new HashMap<String, String>();
//						map.put("xpath", modelpath);// "action/path/whatever");
//						map.put(IDE.EDITOR_ID_ATTR,
//								"org.jboss.tools.common.model.ui.editor.EditorPartWrapper");
//						try {
//							IMarker marker = currentFile.createMarker(IMarker.TEXT);
//							marker.setAttributes(map);
//							IDE.openEditor(page, marker); //3.0 API
//							marker.delete();
//						} catch (CoreException e) {
//							e.printStackTrace();
//						}
//					} else {
					// if we have an empty path, the user double-clicked on the root
					// so just open the file
					if (modelpath.trim().length() == 0) {
						IWorkbenchPage page = getSite().getPage();
						try {
							IDE.openEditor(page, currentFile);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					} else {
						// otherwise use the FindObjectHelper to find the actual node 
						// in the ESB editor
						XModelObject fileObject =
								EclipseResourceUtil.createObjectForResource(currentFile);
						XModelObject actionObject =
							fileObject.getChildByPath(modelpath); //"Services/SimpleListener/Actions/displayAction");
						FindObjectHelper.findModelObject(actionObject, FindObjectHelper.IN_EDITOR_ONLY);
					}
	//				}
				}
			}
		}
	}
	
	/*
	 * Action to open the ESB editor 
	 * @author bfitzpat
	 *
	 */
	private class OpenESBEditorAction extends Action {

		public OpenESBEditorAction() {
			super(JBossESBUIMessages.ESBVisualizerView_Open_ESB_Editor_Action_Label);
		}
		
		public void run() {
			openESBAction();
		}
		
	}

	/*
	 * Action to toggle the double-click behavior from freezing a node to opening 
	 * the ESB editor to the selected object.
	 * By default it opens the ESB editor.
	 * @author bfitzpat
	 *
	 */
	private class DoubleClickToggleAction extends Action {

		public DoubleClickToggleAction() {
			super(JBossESBUIMessages.ESBVisualizerView_DoubleClick_Toggle_Action_label);
			this.setChecked(doubleClickFreezesNode);
		}
		
		public void run() {
			doubleClickFreezesNode = !doubleClickFreezesNode;
			this.setChecked(doubleClickFreezesNode);
		}
		
	}

	/*
	 * @author bfitzpat
	 * If the user double-clicks on a node, it does one of two things:
	 * * if the double-click toggle is set to freeze the node, it will "freeze" it in place. 
	 *   If they double-click again, un-"freeze" it. Frozen nodes don't move when the layout
	 *   changes or is refreshed.
	 * * if not, the ESB editor is opened and the object double-clicked on is selected
	 */
	private class FixNodeDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent e) {
			if (e.getSource() instanceof GraphViewer) {
				Graph graph = ((GraphViewer) e.getSource()).getGraphControl();
				if (!graph.getSelection().isEmpty()) {
					GraphNode node = (GraphNode) graph.getSelection().get(0);
					if (node.getData() != null && node.getData() instanceof ESBNode) {
						if (doubleClickFreezesNode) {
							ESBNode to = (ESBNode)node.getData();
							to.setIsMovementLocked(!to.isMovementLocked());
							if (to.isMovementLocked()) {
								node.setBorderWidth(3);
								node.setBorderColor(gv.getGraphControl().getDisplay().getSystemColor(
										SWT.COLOR_BLUE));
							} else {
								node.setBorderWidth(1);
								node.setBorderColor(defaultBorder);
							}
						} else {
							openESBAction();
						}
					}
				}
			}
		}
	}

	/*
	 * @author bfitzpat
	 * Handle layout duties
	 */
	private class LayoutAction extends Action {
		
		private LayoutAlgorithm[] layouts;
		private int currentLayout = 0;
		private GraphViewer gv;
		
		public LayoutAction(GraphViewer gv, LayoutAlgorithm layout) {
			super(JBossESBUIMessages.ESBVisualizerView_Change_Layout_Action_Label);
			this.gv = gv;
			layouts = new LayoutAlgorithm[1];
			layouts[0] = layout;
		}

		@Override
		public void run() {
			super.run();
			int temp = currentLayout + 1;
			if (temp == layouts.length)
				temp = 0;
			currentLayout = temp;
			if (gv.getGraphControl().getLayoutAlgorithm() == null || 
					!gv.getGraphControl().getLayoutAlgorithm().equals(layouts[currentLayout]))
				gv.setLayoutAlgorithm(layouts[currentLayout], true);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
	 */
	public boolean show(ShowInContext context) {
		if (gv == null || context == null)
			return false;
		ISelection sel = context.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)sel;
			Object first = ss.getFirstElement();
			if (first instanceof IFile) {
				String path = ((IFile)first).getLocation().toOSString();
				ESBDomParser parser = new ESBDomParser();
				if (parser.isFileESBConfig(path)) {
					this.currentFile = (IFile) first;
					visualizeESB(path);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}


}
