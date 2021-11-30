package org.xbrlapi.cache.framework.tests;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.io.File;
import java.net.URI;
import java.net.URL;

import org.xbrlapi.utilities.BaseTestCase;

public class FileSchemeURITestCase extends BaseTestCase {

	private String cacheRoot;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		cacheRoot = configuration.getProperty("local.cache");
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	@Test
    public final void testURIWithAuthority() {
		try {
		    
		    String urlString = "file://localhost/home/geoff/rr-instance.xml";
            
		    AssertJUnit.assertEquals("/",File.separator);
		    
		    URI uri = new URI(urlString);
            AssertJUnit.assertEquals("file",uri.getScheme());
            AssertJUnit.assertEquals("localhost",uri.getAuthority());
            AssertJUnit.assertEquals("/home/geoff/rr-instance.xml",uri.getPath());
            AssertJUnit.assertEquals(-1,uri.getPort());

            URL url = new URL(urlString);
            AssertJUnit.assertEquals("file",url.getProtocol());
            AssertJUnit.assertEquals("localhost",url.getAuthority());
            AssertJUnit.assertEquals("/home/geoff/rr-instance.xml",url.getPath());
            AssertJUnit.assertEquals(-1,uri.getPort());
		    
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}
	
    @Test
    public final void testURIWithoutAuthority() {
        try {
            
            String urlString = "file:///home/geoff/rr-instance.xml";
            
            AssertJUnit.assertEquals("/",File.separator);
            
            URI uri = new URI(urlString);
            logger.info(uri);
            AssertJUnit.assertEquals("file",uri.getScheme());
            Assert.assertNull(uri.getAuthority());
            AssertJUnit.assertEquals("/home/geoff/rr-instance.xml",uri.getPath());
            AssertJUnit.assertEquals(-1,uri.getPort());
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception. " + e.getMessage());
        }
    }	
	
}
