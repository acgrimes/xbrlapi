package org.xbrlapi.uri.framework.tests;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.utilities.AnyURIHandler;
import org.xbrlapi.utilities.BaseTestCase;

public class URIEncodingAndDecodingTestCase extends BaseTestCase {

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

    public final void runTest(String anyURI, String uri) {
		try {
		    logger.info("Original = " + anyURI);
		    logger.info("Expected = " + uri);
		    String value = AnyURIHandler.encode(anyURI);
		    logger.info("Encoded = " + value);
		    Assert.assertEquals(value.toString(),uri);
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}

    @Test
    public final void testBackslashConversion() {
	    runTest("http://galexy.net/path\\junk",
	    		"http://galexy.net/path%5Cjunk");
	}

    @Test
    public final void testSpaceConversion() {
	    runTest("http://galexy.net/path junk",
	    		"http://galexy.net/path%20junk");
	}

    
}
