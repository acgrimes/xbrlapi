package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the XBRLAPI Store implementation 
 * of Document DOM recovery methods.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class DocumentRecoveryFromStoreTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.small.schema";
	
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
	 * Test the retrieval of a list of URIs
	 */
	@Test
    public void testGettingURIList() {
		try {
			Set<URI> uris = store.getDocumentURIs();
			AssertJUnit.assertTrue(uris.size() > 0);
			
			boolean foundStartingPoint = false;
			for (URI uri: uris) {
			    if (uri.equals(getURI(this.STARTING_POINT))) {
			        foundStartingPoint = true;
			    }
			}
			AssertJUnit.assertTrue(foundStartingPoint);
			
			Element e = store.getDocumentAsDOM(uris.iterator().next());
			Assert.assertNotNull(e);
			
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test whether the full store can be recovered as a DOM.
	 */
	@Test
    public void testGetDOM() {
		try {
			Document d = store.getStoreAsDOM();
			Assert.assertNotNull(d);
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test recovery of a single document from a store.
	 */
	@Test
    public void testGetDocument() {
		try {
			Element root = store.getDocumentAsDOM(this.getURI(STARTING_POINT));
			Assert.assertNotNull(root);
			AssertJUnit.assertEquals(root.getLocalName(),"schema");
			//store.serialize(root);
		} catch (XBRLException e) {
			Assert.fail("Unexpected " + e.getMessage());
		}
	}

}
