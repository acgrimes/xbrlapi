package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;

import org.xbrlapi.utilities.XBRLException;

/**
 * Test the XML DOM XBRLAPI Store implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class DocumentDeletionTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.label.links";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	@Test
    public void testDeleteSingleDocument() {
		try {
		    URI documentToDelete = this.getURI(STARTING_POINT);
			store.deleteDocument(documentToDelete);
			AssertJUnit.assertFalse(store.getDocumentURIs().contains(documentToDelete));
		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testDeleteRelatedDocuments() {
		try {
			int initialSize = store.getDocumentURIs().size();
			store.deleteRelatedDocuments(this.getURI(STARTING_POINT));
			AssertJUnit.assertFalse(store.getDocumentURIs().contains(this.getURI(STARTING_POINT)));
			AssertJUnit.assertEquals(initialSize-3,store.getDocumentURIs().size());
		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
}
