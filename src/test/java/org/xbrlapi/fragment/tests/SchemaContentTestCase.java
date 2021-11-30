package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Locator;
import org.xbrlapi.Schema;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests the implementation of the org.xbrlapi.SchemaContent interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SchemaContentTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.multi.concept.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test getting the schema fragment.
	 */
	@Test
    public void testGetSchema() {		
		try {
		    List<Concept> concepts = store.<Concept>getXMLResources("Concept");
		    AssertJUnit.assertTrue(concepts.size() > 0);
		    for (Concept concept: concepts) {
	            AssertJUnit.assertEquals(concept.getParent().getIndex(), concept.getSchema().getIndex());
		    }
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting the schema target namespace URI.
	 */
	@Test
    public void testGetSchemaTargetNamespace() {		

	    try {
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                Schema schema = concept.getSchema();
                AssertJUnit.assertEquals(concept.getTargetNamespace().toString(), schema.getDataRootElement().getAttribute("targetNamespace"));
            }
        } catch (XBRLException e) {
            Assert.fail(e.getMessage());
        }
	}	

	/**
	 * Test getting the locators that target a schema component.
	 */
	@Test
    public void testGetLocators() {
        try {
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                List<Locator> locators = concept.getReferencingLocators();
                AssertJUnit.assertTrue(locators.size() > 0);
            }
        } catch (XBRLException e) {
            Assert.fail(e.getMessage());
        }
	}	
}
