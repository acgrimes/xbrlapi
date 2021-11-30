package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Entity;
import org.xbrlapi.EntityResource;
import org.xbrlapi.Segment;
/**
 * Tests the implementation of the org.xbrlapi.Entity interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class EntityTestCase extends DOMLoadingTestCase {
	private final String BASIC_ENTITIES = "test.data.segments";

   private final String TOKEN_DATA = "test.data.local.xbrl.entity.identifier.token.treatment";

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test getting the scenario.
	 */
	@Test
    public void testGetScenario() {
        try {
            loader.discover(this.getURI(BASIC_ENTITIES));       
            List<Entity> entities = store.<Entity>getXMLResources("Entity");
            AssertJUnit.assertTrue(entities.size() > 0);
            for (Entity entity: entities) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.SegmentImpl", entity.getSegment().getType());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test getting the identifier information.
	 */
	@Test
    public void testGetSchemeAndValue() {

        try {
            loader.discover(this.getURI(BASIC_ENTITIES));       
            List<Entity> entities = store.<Entity>getXMLResources("Entity");
            AssertJUnit.assertTrue(entities.size() > 0);
            for (Entity entity: entities) {
                AssertJUnit.assertEquals("www.dnb.com", entity.getIdentifierScheme().toString());
                AssertJUnit.assertEquals("121064880", entity.getIdentifierValue());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	
	/**
	 * Test equality comparison of entity identifier values where
	 * treatment of the XML Schema token data type is relevant to 
	 * getting the comparisons correct.
	 */
	@Test
    public void testEntityIdentifierTokenComparisons() {
        try {
            loader.discover(this.getURI(TOKEN_DATA));       
            List<Entity> entities = store.<Entity>getXMLResources("Entity");
            AssertJUnit.assertTrue(entities.size() > 0);
            for (Entity entity: entities) {
                AssertJUnit.assertTrue(entity.getIdentifierValue().equals("00001"));
            }
            
            List<EntityResource> entityResources = store.<EntityResource>getXMLResources("EntityResource");
            AssertJUnit.assertTrue(entityResources.size() > 0);
            for (EntityResource entity: entityResources) {
                AssertJUnit.assertTrue(entity.getIdentifierValue().startsWith("0000"));
            }

            // Doing a side check that the XML Schema validation is supplied default values.
            // TODO put this side effect test into its own package of Schema validation unit tests.
            for (Segment segment: store.<Segment>getXMLResources("Segment")) {
                String result = store.serialize(segment.getDataRootElement());
                AssertJUnit.assertTrue(result.contains("gronk"));
            }

            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }	    
	}
}
