package org.xbrlapi.data.resource.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;

import org.xbrlapi.data.Store;
import org.xbrlapi.data.dom.StoreImpl;
import org.xbrlapi.data.resource.InStoreMatcherImpl;

/**
 * Provides a base test case for tests involving the XML DOM data store.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class InStoreMatcherTestCase extends BaseTestCase {
    
    private Store store = null;

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		store = new StoreImpl();
        matcher = new InStoreMatcherImpl(store,cache);
	}
	
    @AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
    }
    
	
	
    @Test
    public void testGetSignatureFromSmallFile() {
        try {
            URI uri = getURI("test.data.small.schema");
            logger.info(matcher.getSignature(uri));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown.");
        }
    }
    
    @Test
    public void testGetSignatureFromLargeFile() {
        try {
            URI uri = getURI("real.data.sec");
            logger.info(matcher.getSignature(uri));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown.");
        }
    }    

    @Test
    public void testRepeatedGetMatchFromLargeFile() {
        try {
            URI uri1 = getURI("real.data.sec.usgaap.1");
            AssertJUnit.assertEquals(uri1,matcher.getMatch(uri1));

            URI uri2 = getURI("real.data.sec.usgaap.2");
            AssertJUnit.assertEquals(uri1,matcher.getMatch(uri2));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown.");
        }
    }
    


	

}