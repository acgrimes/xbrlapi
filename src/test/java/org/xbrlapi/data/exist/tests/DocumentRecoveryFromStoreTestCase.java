package org.xbrlapi.data.exist.tests;


import java.net.URI;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.loader.Loader;
/**
 * Test the Exist XBRLAPI Store implementation of DOM recovery methods.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class DocumentRecoveryFromStoreTestCase extends BaseTestCase {

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
    public void testGettingURIList() {
        StoreImpl store = null;
        try {
            store = createStore("testGettingURIList");
            Loader loader = this.createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            Set<URI> uris = store.getDocumentURIs();
            Assert.assertTrue(uris.size() >= 1);
            Element e = store.getDocumentAsDOM(uris.iterator().next());
            Assert.assertNotNull(e);
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
	
	/**
	 * Test recovery of a store as a single DOM.
	 */
	@Test
    public void testGetDOM() {
        StoreImpl store = null;
        try {
            store = createStore("testGetDOM");
            Loader loader = this.createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            Document dom = store.getStoreAsDOM();
            NodeList documents = dom.getDocumentElement().getChildNodes();
            Assert.assertTrue(documents.getLength() > 1);
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

	/**
	 * Test recovery of a single document from a store.
	 */
	@Test
    public void testGetDocument() {
        StoreImpl store = null;
        try {
            store = createStore("testGetDocument");
            Loader loader = this.createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            Element root = store.getDocumentAsDOM(this.getURI(STARTING_POINT));
            Assert.assertEquals(root.getLocalName(),"schema");
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
