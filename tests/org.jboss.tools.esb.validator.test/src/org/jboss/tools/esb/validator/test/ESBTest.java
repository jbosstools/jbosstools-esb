package org.jboss.tools.esb.validator.test;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

public class ESBTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.esb.validator.test";
	protected static String PROJECT_NAME = "esbTest";
	protected static String PROJECT_PATH = "/projects/esbTest";

	protected static String WEB_CONTENT_SUFFIX = "/esbcontent";

	protected IProject project;

	public ESBTest() {
		project = getTestProject();
	}

	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject();
				if(project==null || !project.exists()) {
					project = importPreparedProject("/");
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import ESB test project: " + e.getMessage());
			}
		}
		return project;
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public static IProject importPreparedProject(String packPath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(project==null || !project.exists()) {
			project = ResourcesUtils.importProject(b, PROJECT_PATH);
		}
		try {
			project.setPersistentProperty(IJBossESBFacetDataModelProperties.QNAME_ESB_CONTENT_FOLDER, "esbcontent");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		return project;
	}


	public static void assertLocationEquals(Set<? extends ITextSourceReference> references, int startPosition, int length) {
		for (ITextSourceReference reference : references) {
			if(reference.getStartPosition()==startPosition) {
				assertLocationEquals(reference, startPosition, length);
				return;
			}
		}
		StringBuffer message = new StringBuffer("Location [start positopn=").append(startPosition).append(", lengt=").append(length).append("] has not been found among ");
		for (ITextSourceReference reference : references) {
			message.append("[").append(reference.getStartPosition()).append(", ").append(reference.getLength()).append("] ");
		}
		fail(message.toString());
	}

	public static void assertLocationEquals(ITextSourceReference reference, int startPosition, int length) {
		assertEquals("Wrong start position", startPosition, reference.getStartPosition());
		assertEquals("Wrong length", length, reference.getLength());
	}

	public static void cleanProject(String _resourcePath) throws Exception {
	}
}