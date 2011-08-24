package org.jboss.tools.esb.validator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.PositionHolder;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.IValidator;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.common.validation.internal.ProjectValidationContext;
import org.jboss.tools.common.validation.internal.SimpleValidatingProjectTree;
import org.jboss.tools.common.validation.internal.ValidatingProjectSet;
import org.jboss.tools.esb.core.ESBProjectConstant;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.model.ESBConstants;
import org.jboss.tools.esb.core.model.converters.ConverterConstants;
import org.jboss.tools.esb.core.model.impl.BusinessRulesProcessor;
import org.w3c.dom.Element;

public class ESBCoreValidator extends ESBValidationErrorManager implements IValidator {
	public static final String ID = "org.jboss.tools.esb.validator.ESBCoreValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.esb.validator.esbproblem"; //$NON-NLS-1$

	static String XML_EXT = ".xml"; //$NON-NLS-1$
	static String ATTR_PATH = "path"; //$NON-NLS-1$
	static String ATTR_ATTRIBUTE = "attribute"; //$NON-NLS-1$

	String projectName;
	Map<IProject, IProjectValidationContext> contexts = new HashMap<IProject, IProjectValidationContext>();

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMarkerType()
	 */
	@Override
	public String getMarkerType() {
		return PROBLEM_TYPE;
	}

	public String getId() {
		return ID;
	}

	public String getBuilderId() {
		return null;
	}

	public IValidatingProjectTree getValidatingProjects(IProject project) {
		IProjectValidationContext rootContext = contexts.get(project);
		if(rootContext == null) {
			rootContext = new ProjectValidationContext();
			contexts.put(project, rootContext);
		}

		Set<IProject> projects = new HashSet<IProject>();
		projects.add(project);

		IValidatingProjectSet projectSet = new ValidatingProjectSet(project, projects, rootContext);
		return new SimpleValidatingProjectTree(projectSet);
	}

	public boolean shouldValidate(IProject project) {
		if(!project.isAccessible()) {
			return false;
		}

		String esbContentFolder = null;
		
		try {
			esbContentFolder = project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
		} catch (CoreException e) {
			//ignore
		}
		
		if(esbContentFolder != null) return isEnabled(project);
		
		
		try {
			IFacetedProject fp = ProjectFacetsManager.create(project);
			IProjectFacet f = ProjectFacetsManager.getProjectFacet(ESBProjectConstant.ESB_PROJECT_FACET);
			if(fp != null && f != null && fp.getInstalledVersion(f) != null) {
				return true;
			}
			return project.hasNature(ESBProjectConstant.ESB_PROJECT_NATURE) && isEnabled(project);
		} catch (CoreException e) {
			ESBValidatorPlugin.log(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);
		projectName = project.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);

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
		validateActions(object, file);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		displaySubtask(ESBValidatorMessages.VALIDATING_PROJECT, new String[]{projectName});

		String esbContentFolder = null;
		
		try {
			esbContentFolder = project.getPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER);
		} catch (CoreException e) {
			//ignore
		}
		
		if(esbContentFolder == null) {
			esbContentFolder = ESBProjectConstant.DEFAULT_ESB_CONFIG_RESOURCE_FOLDER;
		}
		
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
						bindMarkerToPathAndAttribute(marker, listener, ESBConstants.ATTR_BUS_ID_REF);
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

	public boolean isEnabled(IProject project) {
		return ESBPreferences.isValidationEnabled(project);
	}

	void validateActions(XModelObject object, IFile file) {
		XModelObject servicesFolder = object.getChildByPath("Services"); //$NON-NLS-1$
		if(servicesFolder == null) return;
	
		XModelObject[] services = servicesFolder.getChildren();
		for (XModelObject service: services) {
			XModelObject actionsFolder = service.getChildByPath("Actions"); //$NON-NLS-1$
			XModelObject[] actions = actionsFolder.getChildren();
			for (XModelObject action: actions) {
				String entity = action.getModelEntity().getName();
				if(entity.startsWith("ESBPreActionBusinessRulesProcessor")) { //$NON-NLS-1$
					validateBusinessRulesProcessor(action, file);
				} else {
					//TODO
				}
			}
		
		}
	}
	
	static String ATTR_RULE_LANGUAGE = "rule language"; //$NON-NLS-1$
	static String ATTR_RULE_SET = "rule set"; //$NON-NLS-1$
	static String ATTR_RULE_AUDIT_INTERVAL = "rule audit interval"; //$NON-NLS-1$
	static String ATTR_RULE_AUDIT_TYPE = "rule audit type"; //$NON-NLS-1$

	void validateBusinessRulesProcessor(XModelObject object, IFile file) {
		String lang = object.getAttributeValue(ATTR_RULE_LANGUAGE);
		if(lang != null && lang.length() > 0) {
			String ruleSet = object.getAttributeValue(ATTR_RULE_SET);
			if(ruleSet == null || ruleSet.length() == 0) {
				IMarker marker = addError(ESBValidatorMessages.INVALID_RULE_SET_FOR_RULE_LANGUAGE, 
						ESBPreferences.BUSINESS_RULES_PROCESSOR_PROBLEMS, getSourceReference(object, ATTR_RULE_LANGUAGE), file);
				bindMarkerToPathAndAttribute(marker, object, ATTR_RULE_LANGUAGE);
			} else if(!ruleSet.endsWith(".dslr")) { //$NON-NLS-1$
				IMarker marker = addError(ESBValidatorMessages.INVALID_RULE_SET_FOR_RULE_LANGUAGE, 
						ESBPreferences.BUSINESS_RULES_PROCESSOR_PROBLEMS, getSourceReference(object, ATTR_RULE_SET), file);
				bindMarkerToPathAndAttribute(marker, object, ATTR_RULE_SET);
			}
		}
		
		String auditInterval = object.getAttributeValue(ATTR_RULE_AUDIT_INTERVAL);
		if(auditInterval != null && auditInterval.length() > 0) {
			String auditAuditType = object.getAttributeValue(ATTR_RULE_AUDIT_TYPE);
			if(!"THREADED_FILE".equals(auditAuditType)) { //$NON-NLS-1$
				IMarker marker = addError(ESBValidatorMessages.INVALID_RULE_AUDIT_TYPE_AND_INTERVAL, 
						ESBPreferences.BUSINESS_RULES_PROCESSOR_PROBLEMS, getSourceReference(object, ATTR_RULE_AUDIT_INTERVAL), file);
				bindMarkerToPathAndAttribute(marker, object, ATTR_RULE_AUDIT_INTERVAL);
			}
		}

		XModelObject[] ps = object.getChildren(ConverterConstants.OBJECT_PATH_ENTITY);
		for (XModelObject path: ps) {
			validateObjectPathForBusinessRulesProcessor(path, object, file);			
		}
	
		String ruleMultithreadEvaluation = object.getAttributeValue(BusinessRulesProcessor.ATTR_RULE_MULTITHREAD_EVALUATION);
		String ruleMaxThreads = object.getAttributeValue(BusinessRulesProcessor.ATTR_RULE_MAX_THREADS);
		if(!"true".equals(ruleMultithreadEvaluation) && ruleMaxThreads != null && ruleMaxThreads.length() > 0) { //$NON-NLS-1$
			IMarker marker = addError(ESBValidatorMessages.INVALID_RULE_MAX_THREADS, 
					ESBPreferences.BUSINESS_RULES_PROCESSOR_PROBLEMS, getSourceReference(object, BusinessRulesProcessor.ATTR_RULE_MAX_THREADS), file);
			bindMarkerToPathAndAttribute(marker, object, BusinessRulesProcessor.ATTR_RULE_MAX_THREADS);
		}
	}

	static String ATTR_ESB = "esb"; //$NON-NLS-1$
	static Set<String> OBJECT_PATH_LOCATIONS = new HashSet<String>();
	static {
		OBJECT_PATH_LOCATIONS.add("body"); //$NON-NLS-1$
		OBJECT_PATH_LOCATIONS.add("header"); //$NON-NLS-1$
		OBJECT_PATH_LOCATIONS.add("properties"); //$NON-NLS-1$
		OBJECT_PATH_LOCATIONS.add("attachment"); //$NON-NLS-1$
	}

	void validateObjectPathForBusinessRulesProcessor(XModelObject path, XModelObject brp, IFile file) {
		String esb = path.getAttributeValue(ATTR_ESB);
		StringTokenizer st = new StringTokenizer(esb, "."); //$NON-NLS-1$
		if(!st.hasMoreTokens()) return;
		String location = st.nextToken();
		if(!OBJECT_PATH_LOCATIONS.contains(location)) {
			IMarker marker = addError(ESBValidatorMessages.INVALID_OBJECT_PATH_WRONG_LOCATION, 
					ESBPreferences.BUSINESS_RULES_PROCESSOR_PROBLEMS, getSourceReference(path, ATTR_ESB), file);
			bindMarkerToPathAndAttribute(marker, path, ATTR_ESB);
		}
	}

	void bindMarkerToPathAndAttribute(IMarker marker, XModelObject object, String attr) {
		if(marker != null) try {
			marker.setAttribute(ATTR_PATH, object.getPath());
			marker.setAttribute(ATTR_ATTRIBUTE, attr);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}

class XMLValueInfo implements IValueInfo {
	XModelObject object;
	String attribute;
	
	PositionHolder h = null;
	
	public XMLValueInfo() {
	}
	
	public XMLValueInfo(XModelObject object, String attribute) {
		this.object = object;
		this.attribute = attribute;
	}

	public int getLength() {
		getPositionHolder();
		int length = h.getEnd() - h.getStart();
		return length < 0 ? 0 : length;
	}

	public int getStartPosition() {
		getPositionHolder();
		return h.getStart();
	}

	public String getValue() {
		return object.getAttributeValue(attribute);
	}
	
	PositionHolder getPositionHolder() {
		if(h == null) {
			h = PositionHolder.getPosition(object, attribute);
		}
		h.update();
		return h;
	}
	
	public Element toXML(Element parent, Properties context) {
		return null;
	}

	public void loadXML(Element element, Properties context) {
	}
	
	public IFile getResource() {
		return object == null ? null : (IFile)object.getAdapter(File.class);
	}
	
}
