package org.xbrlapi.loader.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;

import org.xbrlapi.Stub;
import org.xbrlapi.data.dom.tests.BaseTestCase;

/**
 * Test the loader handling of partial loading situations.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class PartialLoadingTestCase extends BaseTestCase {

	private final String VALID_URI = "test.data.small.schema";
    private final String NONEXISTENT_URI = "nonexistent.uri";

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}
	
	@Test
    public void testLoaderHandlingOfAnIOException() throws Exception {
	    try {
	        loader.stashURI(getURI(this.NONEXISTENT_URI));
	        loader.stashURI(getURI(this.VALID_URI));
            loader.discover();
            List<Stub> stubs = store.getStubs();
            AssertJUnit.assertEquals(1,stubs.size());
            List<URI> uris = store.getDocumentsToDiscover();
            AssertJUnit.assertEquals(1,uris.size());
            AssertJUnit.assertEquals(uris.get(0),getURI(this.NONEXISTENT_URI));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

    @Test
    public void testLoaderHandlingOfASAXException() throws Exception {
        try {
            loader.discover(new URI("http://www.xbrlapi.org/invalid.xml"),"<xbrl>");
            loader.stashURI(getURI(this.VALID_URI));
            loader.discover();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
	

	


}
