package org.jboss.tools.esb.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.common.model.ui.wizards.NewClassWizard;
import org.jboss.tools.common.model.ui.wizards.NewTypeWizardAdapter;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
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
		super.addPages();
		mainPage.setTitle(ESBUIMessages.newESBActionWizardPageTitle);
	}

	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		mainPage.createType(monitor);
		if(mainPage.getCreatedType() != null) {
			modifyJavaSource();
		}
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

	void modifyJavaSource() {
//		String newValue = getQualifiedClassName();
		try {
			IType type = mainPage.getCreatedType();
			if(type == null) {
				return;
			}
			String name = type.getElementName();
			String sc = type.getSuperclassTypeSignature();
			if(sc != null) {
				sc = EclipseJavaUtil.resolveTypeAsString(type, sc);
			}
			if(type != null && "org.jboss.soa.esb.actions.AbstractActionPipelineProcessor".equals(sc)) {
				ICompilationUnit w = type.getCompilationUnit().getWorkingCopy(new NullProgressMonitor());
				IBuffer b = w.getBuffer();
				String s = b.getContents();
				String lineDelimiter = "\r\n";
				
				String IMPORT = "import org.jboss.soa.esb.actions.AbstractActionPipelineProcessor;";
				int i1 = s.indexOf(IMPORT);
				if(i1 >= 0) {
					String content = "";
					String[] imports = {
							"import org.jboss.soa.esb.actions.ActionProcessingException;",
							"import org.jboss.soa.esb.helpers.ConfigTree;",
							"import org.jboss.soa.esb.message.Message;"
					};
					for (String is: imports) {
						if(s.indexOf(is) < 0) {
							content += lineDelimiter + is;
						}
					}
					if(content.length() > 0) {
						b.replace(i1 + IMPORT.length(), 0, content);
					}
				}
				
				s = b.getContents();
				
				int i = s.indexOf('{');
				int j = s.lastIndexOf('}');
				if(i > 0 && j > i) {
					String tab = "\t";
					String content = lineDelimiter 
						+ tab + "protected ConfigTree _config;" + lineDelimiter
						+ lineDelimiter
						+ tab + "public " + name + "(ConfigTree config) {" + lineDelimiter
						+ tab + tab + "_config = config;"+ lineDelimiter
						+ tab + "}" + lineDelimiter
						+ lineDelimiter
						+ tab + "@Override" + lineDelimiter
						+ tab + "public Message process(Message message) throws ActionProcessingException {" + lineDelimiter
						+ tab + tab + "//ADD CUSTOM ACTION CODE HERE" + lineDelimiter
						+ tab + tab + "return message;" + lineDelimiter
						+ tab + "}" + lineDelimiter;
					b.replace(i + 1, j - i - 1, content);
					w.commitWorkingCopy(true, new NullProgressMonitor());
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
