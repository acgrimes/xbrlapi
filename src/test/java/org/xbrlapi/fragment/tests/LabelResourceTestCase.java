package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.LabelResource;
/**
 * Tests the implementation of the org.xbrlapi.LabelResource interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LabelResourceTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.label.links";
	
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
	 * Test getting the string value of the resource.
	 */
	@Test
    public void testGetStringValue() {	

		try {
			List<LabelResource> fragments = store.<LabelResource>getXMLResources("LabelResource");
			LabelResource fragment = fragments.get(0);
			AssertJUnit.assertEquals("Current Asset", fragment.getStringValue());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
    /**
     * Test getting the set of concepts with this label.
     */
    @Test
    public void testGetConcepts() {  

        try {
            List<LabelResource> fragments = store.<LabelResource>getXMLResources("LabelResource");
            LabelResource fragment = fragments.get(0);
            List<Concept> concepts = fragment.getConcepts();
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.ConceptImpl",concept.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }	
}
