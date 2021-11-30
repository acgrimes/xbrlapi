package org.xbrlapi.xsd.framework.tests;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */

import java.io.File;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.utilities.BaseTestCase;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XSDValidationTestCase extends BaseTestCase {

    private SAXParser parser;
    private XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
    private SymbolTable symbolTable = new SymbolTable(2039);
    private ContentHandler contentHandler = new SimpleContentHandler();
    // now must reset features for actual parsing:
    
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        parser = new SAXParser(symbolTable, grammarPool);
        try{
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature("http://xml.org/sax/features/namespaces", true);
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            parser.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("The parser features could not be set. " + e.getMessage());
        }
        parser.setErrorHandler((ErrorHandler) contentHandler);        
        parser.setContentHandler(contentHandler);
  	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

    public final void run(File file) {
    	InputSource inputSource;
    	inputSource = new InputSource(file.toURI().toString());
		try {
	        parser.parse(inputSource);
		} catch (Exception e) {
			e.printStackTrace();
	    	Assert.fail();
		}
    }

    @Test
    public final void testSimpleInstance() {
    	run(new File("/home/geoff/Eclipse/org.xbrlapi/www.xbrlapi.org/xbrl/test/simple.xml"));
    }

    @Test
    public final void testHarderInstance() {
    	run(new File("/home/geoff/Eclipse/org.xbrlapi/www.xbrlapi.org/xbrl/test/harder.xml"));
    }

    @Test
    public final void testEntitiesInstance() {
    	run(new File("/home/geoff/Eclipse/org.xbrlapi/www.xbrlapi.org/xbrl/test/entities.xml"));
    }

	public class SimpleContentHandler extends DefaultHandler implements ContentHandler {
		    	    
		@Override
	    public void error(SAXParseException exception) throws SAXException {
			System.err.println(":" + exception.getMessage() + ": on line number " + exception.getLineNumber());
		}
	
	   @Override
		public void fatalError(SAXParseException exception) throws SAXException {
		   System.err.println(exception.getMessage() + ": on line number " + exception.getLineNumber());
		}
	
	   @Override
		public void warning(SAXParseException exception) throws SAXException {
			System.err.println(exception + "  Carrying on with parsing without doing validation.");
		}

	}

}
