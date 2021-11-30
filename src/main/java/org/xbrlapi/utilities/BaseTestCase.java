package org.xbrlapi.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Assert;
/**
 * Provides a base test case for all tests in the XBRL API test suite.
 *  This package provides some base testcase classes
 *  that extend in useful ways on the JUnit base test case.
 *  Te XMLBase subclass helps with tests related to XML.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
abstract public class BaseTestCase {

	protected static Logger logger = Logger.getLogger(BaseTestCase.class);	
	
	/**
	 * Use this constant to determine the properties file that configures the Unit testing of the XBRLAPI.
	 */  
	private final String configurationPropertiesIdentifier = "test.configuration.properties";
	protected Properties configuration = null;
	
	/**
	 * Absolute path to the local document cache
	 */  	
	protected String cachePath;
	
	/**
	 * Base URI for test data
	 */
	protected String baseURI;
	

	protected void setUp() throws Exception {

        try {
            // Load the test configuration details from the specified properties file
            configuration = new Properties();
            File configurationFile = new File(System.getProperty("xbrlapi.test.configuration"));
            if (!configurationFile.exists()) {
                configurationFile = new File(configurationPropertiesIdentifier);
            }
            if (!configurationFile.exists()) {
                Assert.fail("The configuration File " + configurationFile + " does not exist.");
            }
            configuration.load(new FileInputStream(configurationFile));
            if (!configuration.containsKey("local.cache")) {
                Assert.fail("The test configuration file: " + configurationFile + " is not valid.");
            }

            // Local Cache location
            cachePath = configuration.getProperty("local.cache");
            
            // Base URI for test data
            baseURI = configuration.getProperty("test.data.baseURI");

        } catch (NullPointerException e) {
            // This additional error handling was contributed by Walter Hamscher (25 July 2008)
            Assert.fail("Null system property xbrlapi.test.configuration");            
        } catch (FileNotFoundException e) {
            Assert.fail("The test suite configuration properties file was not found.");            
        } catch (IOException e) {
            Assert.fail("An I/O error occurred while accessing the test suite configuration properties file.");
        }
		GrammarCacheImpl.emptyGrammarPool();
	}
	

    protected void tearDown() throws Exception {
       ;
    }	
	
	/**
	 * @param property determining the URI of the test data
	 * @return the absolute URI of the test data
	 */
	public URI getURI(String property) {

		String myProperty = configuration.getProperty(property);
		logger.debug("Getting URI given test config property " + property);
        logger.debug("property value is " + myProperty);

		URI uri = null;
		try {
	        if (myProperty.startsWith("http:/")) {
	            uri = new URI(myProperty);
	        } else if (myProperty.startsWith("file:/")) {
	            uri = new URI(myProperty);
	        } else if (property.startsWith("test.data.local.")) {
	            String rootProperty = configuration.getProperty("local.test.data.root");
	            logger.debug("Local test data root directory is " + rootProperty);
	            File root = new File(rootProperty);
	            File file = new File(root,myProperty);
	            logger.debug("Local test file is " + file);
	            uri = file.toURI();
	            logger.debug("Local test file URI is " + uri);
	        } else {
	            logger.debug("Making a new URI from " + baseURI + myProperty);
	            uri = new URI(baseURI + myProperty);		    
	        }
		} catch (URISyntaxException e) {
		    Assert.fail("The URI syntax is invalid.");
		}
        logger.debug("Test URI is " + uri);
		return uri;

	}	

    /**
     * @param object The object to create a deep copy of.
     * @return the deep copy of the object, obtained via serialization.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object getDeepCopy(Object object) throws IOException,ClassNotFoundException {

        ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream( );
        ObjectOutputStream serializer = new ObjectOutputStream(memoryOutputStream);
        serializer.writeObject(object);
        serializer.flush();

        ByteArrayInputStream memoryInputStream = new ByteArrayInputStream(memoryOutputStream.toByteArray( ));
        ObjectInputStream deserializer = new ObjectInputStream(memoryInputStream);
        return deserializer.readObject();

    }    
    
    /**
     * Tests if the object and the copy are equal and have the same hash code.
     * @param object The object to use.
     * @param copy The supposed deep copy of the object.
     * @throws Exception
     */
    public final void assessCustomEquality(Object object, Object copy) throws Exception {
        Assert.assertEquals(object,copy);
        Assert.assertEquals(object.hashCode(),copy.hashCode());
    }
	
}
