package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.XlinkDocumentation;

/**
 * Tests the implementation of the org.xbrlapi.XlinkDocumentation interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class XlinkDocumentationTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.extended.link.documentation.element";
	
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
	 * Test getting the roleType definition.
	 */
	@Test
    public void testGetValue() {

		try {
			List<XlinkDocumentation> fragments = store.<XlinkDocumentation>getXMLResources("XlinkDocumentation");
			XlinkDocumentation fragment = fragments.get(0);
			AssertJUnit.assertEquals("Value of the documentation", fragment.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}
