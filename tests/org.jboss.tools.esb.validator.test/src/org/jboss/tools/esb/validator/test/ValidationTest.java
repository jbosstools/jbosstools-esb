/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.validator.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.esb.validator.ESBValidatorMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Viacheslav Kabanovich
 */
public class ValidationTest extends ESBTest {

	/**
	 *  FTP Listener cannot reference FS Channel.
	 * 
	 * @throws Exception
	 */
	public void testIncompatibleChannelReference() throws Exception {
		IProject project = ESBTest.findTestProject();
		IFile file = project.getFile("esbcontent/META-INF/jboss-esb-01.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, ESBValidatorMessages.LISTENER_REFERENCES_INCOMPATIBLE_CHANNEL, 13);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("jboss-esb-01.xml should have one error marker.", markerNumbers, 1);
	}

	public void testBusenessRulesProcessor() throws Exception {
		IProject project = ESBTest.findTestProject();
		IFile file = project.getFile("esbcontent/META-INF/jboss-esb-brp-broken.xml"); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, ESBValidatorMessages.INVALID_RULE_SET_FOR_RULE_LANGUAGE, 52);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, ESBValidatorMessages.INVALID_RULE_AUDIT_TYPE_AND_INTERVAL, 34);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, ESBValidatorMessages.INVALID_OBJECT_PATH_WRONG_LOCATION, 58);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, ESBValidatorMessages.INVALID_RULE_MAX_THREADS, 60);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("jboss-esb-brp-broken.xml should have 4 error markers.", markerNumbers, 4);
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}