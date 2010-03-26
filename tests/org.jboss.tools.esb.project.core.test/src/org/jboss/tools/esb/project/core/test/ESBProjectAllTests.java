 
package org.jboss.tools.esb.project.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ESBProjectAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test ESB Project Core");
		suite.addTestSuite(ESBProjectDeploymentTest.class);
		return suite;
	}

}
