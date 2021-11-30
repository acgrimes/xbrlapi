package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.w3c.dom.Document;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the XBRLAPI Store composition algorithm.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class ComposeTestCase extends BaseTestCase {
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
	 * Test the composition of a Store.
	 */
	@Test
    public final void testCompose() {
		try {
			Document d = store.getCompositeDocument();
			AssertJUnit.assertEquals("dts",d.getDocumentElement().getLocalName());			
		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception thrown.");
		}
	}

}
