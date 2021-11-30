package org.xbrlapi.cache.tests;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.xbrlapi.cache.Cache;
import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.utilities.BaseTestCase;

public class CacheImplSerializationTestCase extends BaseTestCase {

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

	/**
	 * @param arg0
	 */
	@Test
    public final void testSerialization() {
		try {
			Cache cache = new CacheImpl(new File(cacheRoot));

			ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream( );
			ObjectOutputStream serializer = new ObjectOutputStream(memoryOutputStream);
			serializer.writeObject(cache);
			serializer.flush( );

			ByteArrayInputStream memoryInputStream = new ByteArrayInputStream(memoryOutputStream.toByteArray( ));
	        ObjectInputStream deserializer = new ObjectInputStream(memoryInputStream);
	        Object copy = deserializer.readObject( );
	        
	        AssertJUnit.assertEquals(cache,copy);
            AssertJUnit.assertEquals(cache.hashCode(),copy.hashCode());
			
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}
	
}
