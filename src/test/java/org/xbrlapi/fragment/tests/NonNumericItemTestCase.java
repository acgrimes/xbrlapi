package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.NonNumericItem;

/**
 * Tests the implementation of the org.xbrlapi.NonNumericItem interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class NonNumericItemTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.tuple.instance";
	
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
	 * Test getting all child facts.
	 */
	@Test
    public void testGetAllChildFacts() {
		try {
		    List<NonNumericItem> items = store.<NonNumericItem>getXMLResources("NonNumericItem");
		    AssertJUnit.assertTrue(items.size() > 0);
		    for (NonNumericItem fact: items) {
		        if (fact.getLocalname().equals("managementTitle") ) {
		            AssertJUnit.assertEquals("My Title", fact.getValue().substring(0,fact.getValue().length()-1));
		        }
		    }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
