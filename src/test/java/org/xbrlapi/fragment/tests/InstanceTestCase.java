package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.Instance;

/**
 * Tests the implementation of the org.xbrlapi.Instance interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class InstanceTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.tuple.instance";
	
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
	 * Test getting contexts.
	 */
	@Test
    public void testGetContexts() {
	    try {
	        List<Instance> instances = store.<Instance>getXMLResources("Instance");
	        AssertJUnit.assertTrue(instances.size() > 0);
	        for (Instance instance: instances) {
	            AssertJUnit.assertEquals(1, instance.getContexts().size());    
	        }
	    } catch (Exception e) {
	        Assert.fail(e.getMessage());
	    }
	}
	
	/**
	 * Test getting a specific context.
	 */
	@Test
    public void testGetSpecificContext() {
	    try {
	        List<Instance> instances = store.<Instance>getXMLResources("Instance");
	        AssertJUnit.assertTrue(instances.size() > 0);
	        for (Instance instance: instances) {
	            AssertJUnit.assertEquals("context", instance.getContext("ci").getLocalname());
	        }
	    } catch (Exception e) {
	        Assert.fail(e.getMessage());
	    }
	}
	
	/**
	 * Test getting units.
	 */
	@Test
    public void testGetUnits() {
	    try {
	        List<Instance> instances = store.<Instance>getXMLResources("Instance");
	        AssertJUnit.assertTrue(instances.size() > 0);
	        for (Instance instance: instances) {
	            AssertJUnit.assertEquals(2, instance.getUnits().size());
            }
	    } catch (Exception e) {
	        Assert.fail(e.getMessage());
	    }
	}
	
	/**
	 * Test getting a specific unit.
	 */
	@Test
    public void testGetSpecificUnit() {
	    try {
	        List<Instance> instances = store.<Instance>getXMLResources("Instance");
	        AssertJUnit.assertTrue(instances.size() > 0);
	        for (Instance instance: instances) {
	            AssertJUnit.assertEquals("unit", instance.getUnit("u1").getLocalname());
	        }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
	
	/**
	 * Test getting schemaRefs.
	 */
	@Test
    public void testGetSchemaRefs() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                AssertJUnit.assertEquals(1, instance.getSchemaRefs().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test getting linkbaseRefs.
	 */
	@Test
    public void testGetLinkbaseRefs() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                AssertJUnit.assertEquals(0, instance.getLinkbaseRefs().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	@Test
    public void testGetFacts() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                AssertJUnit.assertEquals(2, instance.getChildFacts().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
    @Test
    public void testGetAllFactCount() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                logger.info(instance.getAllFactsCount());
                logger.info(instance.getChildFactsCount());
                AssertJUnit.assertTrue(instance.getAllFactsCount() > 0);
                AssertJUnit.assertTrue(instance.getAllFactsCount() > instance.getChildFactsCount());
                AssertJUnit.assertTrue(instance.getAllFacts().size() > 0);
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }	
	
    @Test
    public void testGetTuples() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                AssertJUnit.assertEquals(2, instance.getTuples().size());
                AssertJUnit.assertEquals(0, instance.getChildItems().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }	
	
	/**
	 * Test getting footnote links.
	 */
	@Test
    public void testGetFootnoteLinks() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                AssertJUnit.assertEquals(0, instance.getFootnoteLinks().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
		
    @Test
    public void testGetFactsForAGivenConcept() {
        try {
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                
                List<Fact> facts = instance.getAllFacts();
                AssertJUnit.assertTrue(facts.size() > 0);
                for (Fact fact: facts) {
                    Concept concept = fact.getConcept();
                    List<Fact> conceptFacts = instance.getFacts(concept);
                    AssertJUnit.assertTrue(conceptFacts.size() > 0);
                    List<Fact> nameFacts = instance.getFacts(concept.getTargetNamespace(), concept.getName());
                    AssertJUnit.assertTrue(nameFacts.size() > 0);
                    AssertJUnit.assertTrue(conceptFacts.equals(nameFacts));
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }   	
	
}
