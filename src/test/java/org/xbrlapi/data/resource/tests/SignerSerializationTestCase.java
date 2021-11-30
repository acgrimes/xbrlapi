package org.xbrlapi.data.resource.tests;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.xbrlapi.data.resource.MD5SignerImpl;
import org.xbrlapi.utilities.BaseTestCase;

public class SignerSerializationTestCase extends BaseTestCase {
    
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

    @Test
    public final void testMD5SignerSerialization() {
        try {
            Object object = new MD5SignerImpl();
            Object copy = getDeepCopy(object);
            this.assessCustomEquality(object,copy);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception. " + e.getMessage());
        }
    }	
	


	
}
