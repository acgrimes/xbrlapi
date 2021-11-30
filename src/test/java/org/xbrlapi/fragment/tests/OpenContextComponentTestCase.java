package org.xbrlapi.fragment.tests;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.w3c.dom.NodeList;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Scenario;

/**
 * Tests the implementation of the org.xbrlapi.OpenContextComponent interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class OpenContextComponentTestCase extends DOMLoadingTestCase {
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
	 * Test getting complex content.
	 */
	@Test
    public void testGetComplexContent() {

		try {
            List<Scenario> scenarios = store.getXMLResources("Scenario");
            AssertJUnit.assertTrue(scenarios.size() > 0);
            for (Scenario scenario: scenarios) {
                NodeList children = scenario.getDataRootElement().getChildNodes();
                AssertJUnit.assertEquals(children.getLength(), scenario.getComplexContent().getLength());
            }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test c-equality.
	 */
	@Test
    public void testGetCEquality() {

		try {
            List<Scenario> scenarios = store.getXMLResources("Scenario");
            AssertJUnit.assertTrue(scenarios.size() > 0);
            for (Scenario scenario: scenarios) {
                AssertJUnit.assertTrue(scenario.equals(scenario));
            }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
}
