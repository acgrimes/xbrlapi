package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.ArcroleType;
import org.xbrlapi.DOMLoadingTestCase;

/**
 * Tests the implementation of the org.xbrlapi.CustomType interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ArcroleTypeTestCase extends DOMLoadingTestCase {

	private final String STARTING_POINT = "test.data.custom.link.arcrole";

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(getURI(STARTING_POINT));
	}
	
	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test getting the cyclesAllowed attribute value.
	 */
	@Test
    public void testGetCustomArcRoleCyclesAllowed() {

		try {
			List<ArcroleType> fragments = store.<ArcroleType>getXMLResources("ArcroleType");
			ArcroleType fragment = fragments.get(0); 
			AssertJUnit.assertEquals("none", fragment.getCyclesAllowed());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}
