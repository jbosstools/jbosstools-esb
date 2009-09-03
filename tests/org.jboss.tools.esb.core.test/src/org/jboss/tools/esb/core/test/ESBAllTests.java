/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.esb.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ESBAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test ESB Corel");
		suite.addTestSuite(EsbPluginsLoadTest.class);
		suite.addTestSuite(ESBModelTest.class);	
		return suite;
	}

}
