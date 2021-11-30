package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.SimpleNumericItem;

/**
 * Tests the implementation of the org.xbrlapi.SimpleNumericItem interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SimpleNumericItemTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.decimals";
	
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
	 * Test getting value
	 */
	@Test
    public void testGetValue() {
        try {
            List<SimpleNumericItem> items = store.<SimpleNumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (SimpleNumericItem item: items) {
                AssertJUnit.assertEquals("5.6", item.getValue());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test getting inferred precision.
	 */
	@Test
    public void testGetInferredPrecision() {
        try {
            List<SimpleNumericItem> items = store.<SimpleNumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (SimpleNumericItem item: items) {
                AssertJUnit.assertEquals("5", item.getInferredPrecision());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test adjusting value for precision.
	 */
	@Test
    public void testGetPrecisionAdjustedValue() {

        try {
            List<SimpleNumericItem> items = store.<SimpleNumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (SimpleNumericItem item: items) {
                AssertJUnit.assertEquals("5.6", item.getPrecisionAdjustedValue());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
}
