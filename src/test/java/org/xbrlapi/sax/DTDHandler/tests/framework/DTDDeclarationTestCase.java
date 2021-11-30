package org.xbrlapi.sax.DTDHandler.tests.framework;

import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.utilities.BaseTestCase;
import org.xml.sax.XMLReader;

/**
 * Test the DTD declaration handling system.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public abstract class DTDDeclarationTestCase extends BaseTestCase {
	
    private final URI uri = this.getURI("real.data.schema.with.dtd");

	private SAXParser saxParser = null;
	
	private Handler handler = null;
	
	private XMLReader xmlReader = null;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		// Set up the SAX parser to analyse the input XML.
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			saxParserFactory.setNamespaceAware(true);
			saxParser = saxParserFactory.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			handler = new Handler();
			xmlReader.setContentHandler(handler);
			xmlReader.setErrorHandler(handler);
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
			xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
            xmlReader.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",XMLConstants.W3C_XML_SCHEMA_NS_URI);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	@Test
    public void testHandlingOfDTDEntities() {
		try {
			xmlReader.parse(uri.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testHandlingOfDTDs() {
		try {
			xmlReader.parse("http://www.w3.org/2001/XMLSchema.xsd");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testStorageOfDTDs() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}



}
