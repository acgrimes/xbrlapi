package org.xbrlapi.cache.tests;

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
import java.util.List;

import org.xbrlapi.cache.Cache;
import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.utilities.BaseTestCase;

public class CacheImplTestCase extends BaseTestCase {

	private String cacheRoot;
	
	private String XBRL_INSTANCE = "real.data.xbrl.instance.schema";

	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		cacheRoot = configuration.getProperty("local.cache");
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	/**
	 * @param arg0
	 */
	@Test
    public final void testHTTP_URI() {
		try {
			this.examineURI(this.getURI(XBRL_INSTANCE));
		} catch (Exception e) {
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}
	
	@Test
    public final void testGetSubtreeOfUris() {
        try {
        
            URI uri = getURI(XBRL_INSTANCE);
            Cache cache = new CacheImpl(new File(cacheRoot));
            cache.getCacheURI(uri);
            File file = cache.getCacheFile(uri);
            file = file.getParentFile().getParentFile();
            uri = cache.getOriginalURI(file);
            List<URI> uris = cache.getAllUris(uri);
            AssertJUnit.assertTrue(uris.size() > 0);
        
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception was thrown. " + e.getMessage());
        }
	}
	
	public final void examineURI(URI originalURI) {
		try {

            logger.info("Original URI: " + originalURI);
			Cache cache = new CacheImpl(new File(cacheRoot));
			AssertJUnit.assertFalse(cache.isCacheURI(originalURI));
			File cacheFile = cache.getCacheFile(originalURI);
			Assert.assertNotNull(cacheFile);
			URI cacheURI = cache.getCacheURI(originalURI);
			logger.info("Cache URI: " + cacheURI);
			AssertJUnit.assertTrue(cache.isCacheURI(cacheURI));
			URI newURI = cache.getOriginalURI(cacheURI);
            logger.info("New Original URI: " + newURI);
			AssertJUnit.assertEquals(originalURI, newURI);

		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected exception was thrown. " + e.getMessage());
		}
	}

    /**
     * Test operations on a simple URI
     */
    @Test
    public final void testFile_URI() {
    	try {
    		this.examineURI(new URI("file:///home/geoff/document.xml"));
    	} catch (Exception e) {
    		Assert.fail("Unexpected exception. " + e.getMessage());
    	}
    }

    /**
     * Test operations on a simple URI
     */
    @Test
    public final void testMSWindowsFile_URI() {
    	try {
    		this.examineURI(new URI("file:/C:/home/geoff/document.xml"));
    	} catch (Exception e) {
    		Assert.fail("Unexpected exception. " + e.getMessage());
    	}
    }	
	
}
