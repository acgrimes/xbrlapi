package org.xbrlapi.fragment.tests;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.NumericItem;

/**
 * Tests the implementation of the org.xbrlapi.NumericItem interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class NumericItemTestCase extends DOMLoadingTestCase {
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
	 * Test getting units
	 */
	@Test
    public void testGetUnits() {
        try {
            List<NumericItem> items = store.<NumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (NumericItem item: items) {
                AssertJUnit.assertEquals("unit", item.getUnit().getLocalname());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}

	/**
	 * Test getting precision
	 */
	@Test
    public void testGetPrecision() {
        try {
            List<NumericItem> items = store.<NumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (NumericItem item: items) {
                AssertJUnit.assertEquals("2", item.getPrecision());
                AssertJUnit.assertEquals(true, item.hasPrecision());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test getting decimals
	 */
	@Test
    public void testGetDecimals() {
        try {
            List<NumericItem> items = store.<NumericItem>getXMLResources("SimpleNumericItem");
            AssertJUnit.assertTrue(items.size() > 0);
            for (NumericItem item: items) {
                AssertJUnit.assertEquals(false, item.hasDecimals());
                try {
                    AssertJUnit.assertEquals("1", item.getDecimals());
                    Assert.fail("Exception expected when getting decimals for a fact without them");
                } catch (Exception e) {
                    ;
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
	
}
