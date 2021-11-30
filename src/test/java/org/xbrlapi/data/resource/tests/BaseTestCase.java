package org.xbrlapi.data.resource.tests;

import java.io.File;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.xbrlapi.cache.Cache;
import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.data.resource.Matcher;

/**
 * Provides a base test case for tests involving the XML DOM data store.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public abstract class BaseTestCase extends org.xbrlapi.utilities.BaseTestCase {
    
    protected Matcher matcher = null;
    protected Cache cache = null;
    File cacheFile = null;
	protected String cacheLocation = "";

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		cacheLocation = configuration.getProperty("local.cache");
		cacheFile = new File(cacheLocation);
        cache = new CacheImpl(cacheFile);
	}
	
	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}
	
}