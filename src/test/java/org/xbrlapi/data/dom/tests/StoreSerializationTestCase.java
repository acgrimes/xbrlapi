package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */
public class StoreSerializationTestCase extends BaseTestCase {
    
    private final String START = "test.data.small.schema";    
    
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

    @Test
    public final void testStoreSerialization() {
        try {
            loader.discover(this.getURI(START));
            Object copy = getDeepCopy(store);
            this.assessCustomEquality(store,copy);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception. " + e.getMessage());
        }
    }	
	


	
}
