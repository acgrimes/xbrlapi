package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.xbrlapi.DOMLoadingTestCase;

/**
 * Tests the implementation of the org.xbrlapi.Scenario interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ScenarioTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.scenarios";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * TODO Implement specific scenario fragment tests.
	 */
	@Test
    public void testGetComplexContent() {

/*		try {
			OpenContextComponent fragment = (OpenContextComponent) store.get("6");
			assertEquals(4, fragment.getComplexContent().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
*/	}
	
}
