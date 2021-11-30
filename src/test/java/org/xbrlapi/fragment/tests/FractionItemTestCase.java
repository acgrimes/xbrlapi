package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.FractionItem;
import org.xbrlapi.impl.FractionItemImpl;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class FractionItemTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.local.xbrl.instance.tuples.with.units";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.stashURI(this.getURI(STARTING_POINT));
		loader.discoverNext();		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test adjusting value for precision.
	 */
	@Test
    public void testGetNumerator() {

        try {
            List<FractionItem> items = store.<FractionItem>getXMLResources(FractionItemImpl.class);
            AssertJUnit.assertTrue(items.size() > 0);
            for (FractionItem item: items) {
                AssertJUnit.assertEquals(1.0, item.getNumerator());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
}
