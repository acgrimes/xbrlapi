package org.xbrlapi.sax.identifiers.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;

import org.xbrlapi.Concept;
import org.xbrlapi.data.dom.tests.BaseTestCase;

/**
 * Test the basic operation of the SAX identifiers.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class BasicIdentifierTestCase extends BaseTestCase {
	
	private final String STARTING_POINT = "real.data.sec.usgaap.3";
	private URI uri = null;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		uri = getURI(this.STARTING_POINT);
	}
	
	@Test
    public void testFragmentIdentification() {
		try {
			loader.stashURI(uri);
			loader.discoverNext();
			Concept concept = store.getConcept("http://www.microsoft.com/msft/xbrl/taxonomy/2005-02-28","CoverInformation");
			AssertJUnit.assertEquals("CoverInformation",concept.getName());
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected " + e.getMessage());
		}
	}

}
