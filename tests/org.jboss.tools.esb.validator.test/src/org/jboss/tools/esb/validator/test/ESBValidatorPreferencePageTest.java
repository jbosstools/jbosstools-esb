package org.jboss.tools.esb.validator.test;

import org.jboss.tools.esb.validator.ui.ESBValidatorPreferencePage;
import org.jboss.tools.test.util.PreferencePageAbstractTest;

public class ESBValidatorPreferencePageTest extends PreferencePageAbstractTest {
	
	public void testFreemarkerPreferencePageShow() {
		assertTrue(createPreferencePage(ESBValidatorPreferencePage.PREF_ID, ESBValidatorPreferencePage.class)); //$NON-NLS-1$
	}
	
	public void testFreemarkerPreferencePagePerformOk() {
		pressOkOnPreferencePage(ESBValidatorPreferencePage.PREF_ID);
	}
}
