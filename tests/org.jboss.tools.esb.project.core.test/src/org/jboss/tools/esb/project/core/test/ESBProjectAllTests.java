 
package org.jboss.tools.esb.project.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ESBProjectDeploymentTest.class,
	EsbModuleServerCompatTest.class,
})
public class ESBProjectAllTests {
}
