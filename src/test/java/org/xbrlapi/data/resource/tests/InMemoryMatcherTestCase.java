package org.xbrlapi.data.resource.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;

import org.xbrlapi.data.resource.InMemoryMatcherImpl;

/**
 * Provides a base test case for tests involving the XML DOM data store.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class InMemoryMatcherTestCase extends BaseTestCase {
    
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		matcher = new InMemoryMatcherImpl(cache);
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
            String URI = "real.data.sec";
            URI uri = getURI(URI);
            AssertJUnit.assertEquals(uri,matcher.getMatch(uri));
            AssertJUnit.assertEquals(uri,matcher.getMatch(uri));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown.");
        }
    }
}