package org.xbrlapi.data.exist.embedded.tests;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.data.Store;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the eXist XBRLAPI Store implementation store persistence.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class AddToExistingStoreTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.small.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}
	
	/**
	 * Test the retrieval of a list of URIs from the data store.
	 */
	@Test
    public void testReconnectionToAnExistingStore() {
	    Store store = null, secondStore = null;
		try {
		    store = createStore("testReconnectionToAnExistingStore");
		    Loader loader = createLoader(store);
    	    loader.discover(this.getURI(STARTING_POINT));       
			secondStore = createStore("testReconnectionToAnExistingStore");
			Loader secondLoader = createLoader(secondStore);
			int original = secondStore.getDocumentURIs().size(); 
			AssertJUnit.assertTrue(original >= 1);
			secondLoader.discover(this.getURI("test.data.small.instance"));
			AssertJUnit.assertTrue(secondStore.getDocumentURIs().size() > original);

		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
		    try {
                if (secondStore != null) secondStore.close();
    		    if (store != null) store.delete();
		    } catch (Exception x) {
		        Assert.fail("The stores could not be deleted.");
		    }
		}

	}
		
}
