package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fragment;
import java.util.List;
import org.xbrlapi.SimpleLink;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests the features of XML Schema Import Handling as a simple link.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SchemaImportTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.import.custom.link";
	
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
	 * Test get target fragment
	 */
	@Test
    public void testGetTarget() {	

		try {
			List<SimpleLink> links = store.<SimpleLink>getXMLResources("SimpleLink");
			for (SimpleLink link: links) {
				try {
					Fragment target = link.getTarget();
					AssertJUnit.assertNotNull(target);
				} catch (XBRLException e) {
					logger.info(link.serialize());
					e.printStackTrace();
					Assert.fail("No exception expected.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}
