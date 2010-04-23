package org.jboss.tools.esb.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.PositionSearcher;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.model.ESBConstants;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ProjectValidationContext;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.jst.web.model.project.ext.store.XMLValueInfo;

public class ESBCoreValidator extends ESBValidationErrorManager implements IValidator {
	public static final String ID = "org.jboss.tools.esb.validator.ESBCoreValidator"; //$NON-NLS-1$

	static String XML_EXT = ".xml"; //$NON-NLS-1$
	static String ATTR_PATH = "path"; //$NON-NLS-1$
	static String ATTR_ATTRIBUTE = "attribute"; //$NON-NLS-1$

	String projectName;
	Map<IProject, IValidationContext> contexts = new HashMap<IProject, IValidationContext>();

	public String getId() {
		return ID;
	}

	public IValidatingProjectSet getValidatingProjects(IProject project) {
		IValidationContext rootContext = contexts.get(project);
		if(rootContext == null) {
			rootContext = new ProjectValidationContext();
			contexts.put(project, rootContext);
		}

		List<IProject> projects = new ArrayList<IProject>();
		projects.add(project);
		return new ValidatingProjectSet(project, projects, rootContext);
	}

	public boolean shouldValidate(IProject project) {
		String esbContentFolder = null;
		
		try {
			esbContentFolder = project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
		} catch (CoreException e) {
			//ignore
		}
		
		if(esbContentFolder != null) return true;
		
		try {
			return project != null && project.isAccessible() && project.hasNature(ESBProjectConstant.ESB_PROJECT_NATURE);
		} catch (CoreException e) {
			ESBValidatorPlugin.log(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);
//		cdiProject = CDICorePlugin.getCDIProject(project, false);
		projectName = project.getName();
	}

	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);

		for (IFile file: changedFiles) {
			String name = file.getName();
			if(!name.endsWith(XML_EXT)) continue;
			XModelObject o = EclipseResourceUtil.createObjectForResource(file);
			if(o != null && o.getModelEntity().getName().startsWith(ESBConstants.ENT_ESB_FILE)) {
				validateESBConfigFile(o, file);
			}
		}

		return OK_STATUS;
	}

	private void validateESBConfigFile(XModelObject object, IFile file) {
//		System.out.println("Validate ESB Config " + file);
		validateChannelIDRefs(object, file);
	}

	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		displaySubtask(ESBValidatorMessages.VALIDATING_PROJECT, new String[]{projectName});

		String esbContentFolder = null;
		
		try {
			esbContentFolder = project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
		} catch (CoreException e) {
			//ignore
		}
		
		if(esbContentFolder != null) {
			IFolder esbContent = project.getFolder(new Path(esbContentFolder + "/META-INF")); //$NON-NLS-1$
			if(esbContent != null && esbContent.exists()) {
				IResource[] rs = null;
				try {
					rs = esbContent.members();
				} catch (CoreException e) {
					ESBValidatorPlugin.log(e);
				}
				for (IResource r: rs) {
					if(r instanceof IFile) {
						IFile file = (IFile)r;
						String name = file.getName();
						if(!name.endsWith(XML_EXT)) continue;
						XModelObject o = EclipseResourceUtil.createObjectForResource(file);
						if(o != null && o.getModelEntity().getName().startsWith(ESBConstants.ENT_ESB_FILE)) {
							validateESBConfigFile(o, file);
						}
					}
				}
			}
		}
		
		return OK_STATUS;
	}

	
	void validateChannelIDRefs(XModelObject object, IFile file) {
		XModelObject servicesFolder = object.getChildByPath("Services"); //$NON-NLS-1$
		if(servicesFolder == null) return;
		Map<String, String> ids = getAllChannelRefIDs(object);
		XModelObject[] services = servicesFolder.getChildren();
		for (XModelObject service: services) {
			XModelObject listenersFolder = service.getChildByPath("Listeners"); //$NON-NLS-1$
			XModelObject[] listeners = listenersFolder.getChildren();
			for (XModelObject listener: listeners) {
				String channelIDRef = listener.getAttributeValue(ESBConstants.ATTR_BUS_ID_REF);
				if(channelIDRef == null) continue;
				String entity = ids.get(channelIDRef);
				if(channelIDRef.length() == 0) {
					//no id set, it is not an error
				} else if(entity == null) {
					//addError - no id found
					IMarker marker = addError(ESBValidatorMessages.LISTENER_REFERENCES_NON_EXISTENT_CHANNEL, 
							ESBPreferences.LISTENER_REFERENCES_NON_EXISTENT_CHANNEL, getSourceReference(listener, ESBConstants.ATTR_BUS_ID_REF), file);
					if(marker != null) try {
						marker.setAttribute(ATTR_PATH, listener.getPath());
						marker.setAttribute(ATTR_ATTRIBUTE, ESBConstants.ATTR_BUS_ID_REF);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				} else {
					String busEntityPrefix = getBusEntityPrefix(listener.getModelEntity().getName());
					if(busEntityPrefix != null && !entity.startsWith(busEntityPrefix)) {
						IMarker marker = addError(ESBValidatorMessages.LISTENER_REFERENCES_INCOMPATIBLE_CHANNEL, 
								ESBPreferences.LISTENER_REFERENCES_INCOMPATIBLE_CHANNEL, getSourceReference(listener, ESBConstants.ATTR_BUS_ID_REF), file);
						if(marker != null) try {
							marker.setAttribute(ATTR_PATH, listener.getPath());
							marker.setAttribute(ATTR_ATTRIBUTE, ESBConstants.ATTR_BUS_ID_REF);
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}		
	}
	private String getBusEntityPrefix(String listenerEntity) {
		if(listenerEntity == null) return null;
		if(listenerEntity.startsWith("ESBListener")) { //$NON-NLS-1$
			return null;
		}
		if(listenerEntity.startsWith("ESBJCAGateway")) { //$NON-NLS-1$
			return "ESBJMSBus"; //$NON-NLS-1$
		}
		int i = listenerEntity.indexOf("Listener"); //$NON-NLS-1$
		if(i < 0) return null;
		return listenerEntity.substring(0, i) + "Bus"; //$NON-NLS-1$
	}
	//id - bus entity
	private Map<String, String> getAllChannelRefIDs(XModelObject object) {
		Map<String, String> result = new HashMap<String, String>();
		XModelObject[] ps = object.getChildByPath("Providers").getChildren(); //$NON-NLS-1$
		for (int i = 0; i < ps.length; i++) {
			XModelObject[] cs = ps[i].getChildren();
			for (int j = 0; j < cs.length; j++) {
				if(cs[j].getModelEntity().getAttribute(ESBConstants.ATTR_BUS_ID) != null) {
					String v = cs[j].getAttributeValue(ESBConstants.ATTR_BUS_ID);
					if(v != null && v.length() > 0) {
						result.put(v, cs[j].getModelEntity().getName());
					}
				}
			}
		}
		
		return result;
	}

	ITextSourceReference getSourceReference(XModelObject o, String attr) {
		return new XMLValueInfo(o, attr);
	}
}
