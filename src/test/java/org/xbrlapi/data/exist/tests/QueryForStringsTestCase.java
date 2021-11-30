package org.xbrlapi.data.exist.tests;

import java.util.Set;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.loader.Loader;

/**
 * Tests the query for strings method.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class QueryForStringsTestCase extends BaseTestCase {
	private final String START = "test.data.small.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}	

	@Test
    public void testQueryForFragmentIndices() {
        StoreImpl store = null;
        try {
            store = createStore("testQueryForFragmentIndices");
            Loader loader = this.createLoader(store);
            loader.discover(getURI(START));
            String query = "#roots#";
            Set<String> results = store.queryForIndices(query);
            AssertJUnit.assertTrue(results.size() > 1);
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }
	}
}
