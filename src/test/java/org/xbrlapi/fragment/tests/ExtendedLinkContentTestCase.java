package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Arc;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.utilities.Constants;
/**
 * Tests the implementation of the org.xbrlapi.ExtendedLinkContent interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ExtendedLinkContentTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.custom.link.role";
	
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
	 * Test getting the containing extended link.
	 */
	@Test
    public void testGetExtendedLink() {

		try {
		    List<Arc> arcs = store.<Arc>getXMLResources("Arc");
		    AssertJUnit.assertTrue(arcs.size() > 0);
		    for (Arc arc: arcs) {
	            AssertJUnit.assertEquals("extended", arc.getExtendedLink().getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"type"));
		    }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
