package org.xbrlapi.data.exist.embedded.tests.networks;

import java.net.URI;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xbrlapi.data.exist.embedded.StoreImpl;
import org.xbrlapi.data.exist.embedded.tests.BaseTestCase;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.networks.Storer;
import org.xbrlapi.networks.StorerImpl;

/**
 * Added to address concerns raised by R Oldenburg
 * about the {@link StorerImpl#StoreRelationships(Document)} method.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class StorerTestCase extends BaseTestCase {

    private URI uri;
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
		uri = getURI("test.data.xlink.titles");
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}	
	
    /**
     * Test persisting of active relationships for each document in turn.
     */
    @Test
    public void testPersistenceOfRelationshipsInEachDocument() { 
        StoreImpl store = null;
        try {
            store = createStore("testPersistenceOfRelationshipsInEachDocument");
            Loader loader = createLoader(store);
            loader.discover(uri);
            int initialSize = store.getSize();
            logger.debug("Initial size = " + initialSize);
            Storer storer = new StorerImpl(store);
            storer.storeRelationships(store.getDocumentURIs());
            int augmentedSize = store.getSize();
            logger.debug("Augmented size = " + augmentedSize);
            Assert.assertTrue(augmentedSize > initialSize);
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }
    }
    
}
