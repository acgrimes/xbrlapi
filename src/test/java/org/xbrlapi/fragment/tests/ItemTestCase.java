package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Item;

/**
 * Tests the implementation of the org.xbrlapi.Item interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ItemTestCase extends DOMLoadingTestCase {
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
	 * Test getting context.
	 */
	@Test
    public void testGetContext() {
        try {
            List<Item> fragments = store.<Item>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Item fragment: fragments) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.ContextImpl", fragment.getContext().getType());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
	
	/**
	 * Test is the item nill.
	 */
	@Test
    public void testIsNil() {
        try {
            List<Item> fragments = store.<Item>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Item fragment: fragments) {
                AssertJUnit.assertEquals(false, fragment.isNil());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
		
}
