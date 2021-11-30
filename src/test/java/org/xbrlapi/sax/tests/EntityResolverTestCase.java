package org.xbrlapi.sax.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xbrlapi.cache.Cache;
import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.sax.EntityResolver;
import org.xbrlapi.sax.EntityResolverImpl;
import org.xbrlapi.utilities.BaseTestCase;
import org.xbrlapi.utilities.XBRLException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class EntityResolverTestCase extends BaseTestCase {

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	/**
	* Test that the entity resolver constructor 
	* throws an xbrlapiexception when the cache
	* root does not exist.
	*/
	@Test
    public final void testEntityResolverImplFailure() throws Exception {
		try {
			String cache = configuration.getProperty("nonexistent.cache");
			logger.info("Trying to create an entity resolver using cache " + cache);
			new EntityResolverImpl(new File(cache));
			Assert.fail("The entity resolver failed to detect a non-existent cache root");
		} catch (XBRLException expected) {
		}
	}

	@Test
    public final void testInputSourceCreatedByResolveEntity() {
		String originalURI = "http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd";
		try {
			logger.info("Trying to create an entity resolver using cache " + cachePath);
			EntityResolverImpl resolver = new EntityResolverImpl(new File(cachePath));
			InputSource is = resolver.resolveEntity("",originalURI);
			AssertJUnit.assertNull("Public ID for the input source is not null.",is.getPublicId());
			AssertJUnit.assertEquals("System ID is wrong.",new File(cachePath + "/http/null/www.xbrl.org/-1/null/null/2003/xbrl-instance-2003-12-31.xsd").toURI().toString(),is.getSystemId());
		} catch (XBRLException e) {
			Assert.fail("The entity resolver constructor failed unexpectedly.");
		} finally {
			try {
				Cache cache = new CacheImpl(new File(cachePath));
				cache.purge(new URI(originalURI));
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	
	@Test
    public final void testSAXParserUsageOfCustomEntityResolver() {
		
		URI uri = getURI("test.data.cacheableURI.A");

        ContentHandler contentHandler = new ContentHandler();
        XMLReader reader = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			reader = parser.getXMLReader();
			reader.setContentHandler(contentHandler);
        } catch (ParserConfigurationException e) {
            Assert.fail(e.getMessage());
        } catch (SAXException e) {
            Assert.fail(e.getMessage());
        }
        
        logger.info("Trying to create an entity resolver using cache " + cachePath);
        EntityResolver entityResolver = null;
        try {
			entityResolver = new EntityResolverImpl(new File(cachePath));
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
        reader.setEntityResolver(entityResolver);
		
		try {
            reader.parse(uri.toString());
        } catch (SAXException e) {
            Assert.fail(e.getMessage());
		} catch (IOException e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			try {
				Cache cache = new CacheImpl(new File(cachePath));
				cache.purge(uri);
			} catch (Exception e) {
			    e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
	}
	
	class ContentHandler extends DefaultHandler {
		
	    public void startDocument() throws SAXException 
		{
	    	;
	    }
	    
	    public void startElement( String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException 
		{
	        ;
		}
		
	}
	
	
}
