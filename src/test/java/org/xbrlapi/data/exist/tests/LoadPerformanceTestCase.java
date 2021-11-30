package org.xbrlapi.data.exist.tests;

import java.net.URI;
import java.util.Set;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.loader.Loader;
/**
 * Tests performance with larger data sets.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */
public class LoadPerformanceTestCase extends BaseTestCase {
	private final String STARTING_POINT = "real.data.large.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}
	
	/**
	 * Test loading into a large data store.
	 */
	@Test(enabled=false)
    public void testLargerStore() {
        StoreImpl store = null;
        try {
            store = createStore("testLargerStore");
            Loader loader = this.createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            Set<URI> uris = store.getDocumentURIs();
            AssertJUnit.assertTrue(uris.size() >= 22);
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
