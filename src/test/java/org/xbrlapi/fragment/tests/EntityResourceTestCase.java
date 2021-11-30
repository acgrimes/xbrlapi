package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;
import java.util.Set;

import org.xbrlapi.Context;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Entity;
import org.xbrlapi.EntityResource;
import org.xbrlapi.LabelResource;

/**
 * Tests the implementation of the org.xbrlapi.EntityResource interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class EntityResourceTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.local.entities.simple";
	private final String ENTITY_MAP = "real.data.sec.entity.map";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));	
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
    @Test
    public void testEntityResourceManipulations() {
        try {
            
            List<Context> contexts = store.<Context>getXMLResources("Context");
            AssertJUnit.assertTrue("There are two contexts.",contexts.size() == 2);

            for (Context context: contexts) {
                if (context.getId().equals("c1")) {
                    Entity entity = context.getEntity();
                    AssertJUnit.assertEquals("http://xbrlapi.org/integer/entity/scheme/", entity.getIdentifierScheme().toString());
                    AssertJUnit.assertEquals("1", entity.getIdentifierValue());
                    List<EntityResource> resources = entity.getEntityResources();
                    AssertJUnit.assertEquals(1,resources.size());
                    EntityResource resource = resources.get(0);
                    Set<EntityResource> equivalents = resource.getEquivalents();
                    AssertJUnit.assertEquals(2,equivalents.size());
                    List<LabelResource> labels = resource.getLabels();
                    AssertJUnit.assertEquals(1,labels.size());
                    labels = entity.getAllEntityLabels();
                    AssertJUnit.assertEquals(2,labels.size());
                    AssertJUnit.assertEquals("http://xbrlapi.org/integer/entity/scheme/",resource.getIdentifierScheme());
                    AssertJUnit.assertEquals("1",resource.getIdentifierValue());
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
