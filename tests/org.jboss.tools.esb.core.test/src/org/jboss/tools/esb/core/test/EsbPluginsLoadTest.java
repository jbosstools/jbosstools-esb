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

import org.jboss.tools.tests.AbstractPluginsLoadTest;

/**
 * @author eskimo
 */
public class EsbPluginsLoadTest extends AbstractPluginsLoadTest {
	
	public EsbPluginsLoadTest() {}
	
	public void testEsbPluginsAreResolvedAndActivated() {
		testBundlesAreLoadedFor("org.jboss.tools.esb.feature");
	}
}
