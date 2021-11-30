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
import org.xbrlapi.Fragment;
import org.xbrlapi.Item;
import org.xbrlapi.impl.ConceptImpl;
import org.xbrlapi.networks.Network;
import org.xbrlapi.networks.Networks;
import org.xbrlapi.networks.NetworksImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests the implementation of the org.xbrlapi.Concept interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ConceptTestCase extends DOMLoadingTestCase {
	private final String FOOTNOTELINKS = "test.data.footnote.links";
	private final String LABELLINKS = "test.data.label.links";
    private final String PRESENTATIONLINKS = "test.data.local.xbrl.presentation.simple";
    private final String TUPLES = "test.data.local.xbrl.instance.tuples.with.units";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	@Test
    public void testGetPeriodType() {	

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    AssertJUnit.assertEquals("duration", concept.getPeriodType());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
    @Test
    public void testGetFacts() {   

        boolean testDone = false;
        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                List<Fact> facts = concept.getFacts();
                for (Fact fact: facts) {
                    AssertJUnit.assertEquals(fact.getLocalname(), fact.getConcept().getName());
                    testDone = true;
                }
            }
            AssertJUnit.assertTrue(testDone);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetRootFacts() {   

        boolean testDone = false;
        try {
            loader.discover(this.getURI(TUPLES));
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                List<Fact> facts = concept.getRootFacts();
                logger.info(concept.getName() + " # facts = " + facts.size());
                for (Fact fact: facts) {
                    AssertJUnit.assertEquals(fact.getLocalname(), fact.getConcept().getName());
                    testDone = true;
                }
            }
            AssertJUnit.assertTrue(testDone);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }    
    
    @Test
    public void testGetFactCount() {   

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources(ConceptImpl.class);
            AssertJUnit.assertTrue(concepts.size() > 0);
            boolean foundFacts = false;
            for (Concept concept: concepts) {
                if (concept.getFactCount() > 0) {
                    foundFacts = true;
                }
                AssertJUnit.assertEquals(concept.getFactCount(),concept.getFacts().size());
                logger.info(concept.getFactCount());
            }
            AssertJUnit.assertTrue("no concepts found to have facts.", foundFacts);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }    
    
    @Test
    public void testNumericItemTypeDetection() {   

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));   
            loader.discover(this.getURI(PRESENTATIONLINKS));               
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                logger.info(concept.getTypeLocalname() + " - " + concept.isNumeric());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
	
	@Test
    public void testGetBalance() {	

		try {
	        loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    Assert.assertNull(concept.getBalance());
            }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testGetLocators() {	

        try {
            loader.discover(this.getURI(FOOTNOTELINKS));        
            List<Concept> concepts = store.<Concept>getXMLResources("Concept");
            AssertJUnit.assertTrue(concepts.size() > 0);
            for (Concept concept: concepts) {
                if (concept.getName().equals("CurrentAsset"))
                    AssertJUnit.assertEquals(0,concept.getReferencingLocators().size());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
	
	@Test
    public void testGetLabels() {

	    try {
	        loader.discover(this.getURI(LABELLINKS));

			List<Concept> concepts = store.getXMLResources("Concept");
			for (Concept concept: concepts) {
				if (concept.getName().equals("CurrentAsset"))
					AssertJUnit.assertEquals(3,concept.getLabels().size());
			}

		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
	
    @Test
    public void testGetPresentationNetworks() {
    
        try {
            loader.discover(this.getURI(this.PRESENTATIONLINKS));       

            Networks networks = new NetworksImpl(store);
            List<Item> facts = store.getItems();
            AssertJUnit.assertEquals(1,facts.size());
            for (Item fact: facts) {
                logger.info(fact.getConcept().getLabels().get(0).getStringValue());
                networks = store.getMinimalNetworksWithArcrole(fact.getConcept(),Constants.PresentationArcrole);
            }
            Networks presentationNetworks = networks.getNetworks(Constants.PresentationArcrole);
            
            AssertJUnit.assertEquals(1,presentationNetworks.getSize());
            
            for (Network network: presentationNetworks) {
                AssertJUnit.assertEquals(2,network.getNumberOfActiveRelationships());

                List<Fragment> roots = network.getRootFragments(); 
                AssertJUnit.assertEquals(1,roots.size());
                network.complete();
                
                AssertJUnit.assertEquals(6,network.getNumberOfActiveRelationships());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
	
}
