package org.xbrlapi.relationships.tests;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Arc;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.LabelResource;
import org.xbrlapi.Relationship;
import org.xbrlapi.networks.Analyser;
import org.xbrlapi.networks.AnalyserImpl;
import org.xbrlapi.networks.Network;
import org.xbrlapi.networks.Networks;
import org.xbrlapi.networks.Storer;
import org.xbrlapi.networks.StorerImpl;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class PersistedNetworksTestCase extends DOMLoadingTestCase {

	Networks networks = null;
	LabelResource label = null;
	Concept concept = null;

	URI uri;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		uri = getURI("test.data.xlink.titles");
		loader.discover(uri);
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	private int initialSize = 0;
	private Storer storer = null;
	private void storeNetworks() throws Exception {
        storer = new StorerImpl(store);
        networks = store.getNetworks();
        AssertJUnit.assertTrue(networks.getSize() > 0);
        initialSize = store.getSize();
        logger.info("Initial size of store = " + initialSize);
        storer.storeRelationships(networks);
	}
	
    /**
     * Test persisting of active relationships for all networks.
     */
    @Test
    public void testPersistingOfAllActiveRelationships() { 
        try {
            storeNetworks();
            int augmentedSize = store.getSize();
            logger.info("Augmented size = " + augmentedSize);
            AssertJUnit.assertTrue(augmentedSize > initialSize);
            storer.deleteRelationships();
            List<String> arcroles = networks.getArcroles();
            AssertJUnit.assertEquals(1,arcroles.size());
            List<String> linkRoles = networks.getLinkRoles(arcroles.get(0));
            AssertJUnit.assertEquals(1,linkRoles.size());
            Network network = networks.getNetwork(linkRoles.get(0),arcroles.get(0));
            storer.storeRelationships(network);
            AssertJUnit.assertEquals(augmentedSize,store.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test retrieval of arcroles
     */
    @Test
    public void testAnalyserArcroleRetrieval() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                logger.info(arcrole);
                Set<String> linkRoles = analyser.getLinkRoles(arcrole);
                AssertJUnit.assertTrue(linkRoles.size() > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
    }
    
    /**
     * Test retrieval of link roles
     */
    @Test
    public void testAnalyserLinkRoleRetrieval() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> linkRoles = analyser.getLinkRoles();
            AssertJUnit.assertTrue(linkRoles.size() > 0);
            for (String linkRole: linkRoles) {
                logger.info(linkRole);
                Set<String> arcroles = analyser.getArcroles(linkRole);
                AssertJUnit.assertTrue(arcroles.size() > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
    }
    
    @Test
    public void testRelationshipRetrievalByArcrole() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                List<Relationship> relationships = analyser.getRelationships(arcrole);
                if (relationships.size() == 0) {
                    logger.info(arcrole);
                }
                AssertJUnit.assertTrue(relationships.size() > 0);
                for (Relationship relationship: relationships) {
                    AssertJUnit.assertEquals(relationship.getArcrole(),arcrole);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testRelationshipRetrievalByArcroleAndLinkRole() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            for (String linkRole: analyser.getLinkRoles()) {
                Set<String> arcroles = analyser.getArcroles(linkRole);
                AssertJUnit.assertTrue(arcroles.size() > 0);
                for (String arcrole: arcroles) {
                    List<Relationship> relationships = analyser.getRelationships(linkRole,arcrole);
                    AssertJUnit.assertTrue(relationships.size() > 0);
                    for (Relationship relationship: relationships) {
                        AssertJUnit.assertEquals(relationship.getArcrole(),arcrole);
                        AssertJUnit.assertEquals(relationship.getLinkRole(),linkRole);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testRelationshipRetrievalByArcroleAndSourceIndex() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                List<Relationship> relationships = analyser.getRelationships(arcrole);
                AssertJUnit.assertTrue(relationships.size() > 0);
                for (Relationship relationship: relationships) {
                    String sourceIndex = relationship.getSourceIndex();
                    List<Relationship> otherRelationships = analyser.getRelationshipsFrom(sourceIndex,arcrole);
                    AssertJUnit.assertEquals(relationship.getArcrole(),arcrole);
                    AssertJUnit.assertEquals(sourceIndex,otherRelationships.get(0).getSourceIndex());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testRelationshipRetrievalByArcroleAndTargetIndex() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                List<Relationship> relationships = analyser.getRelationships(arcrole);
                AssertJUnit.assertTrue(relationships.size() > 0);
                for (Relationship relationship: relationships) {
                    String targetIndex = relationship.getTargetIndex();
                    List<Relationship> otherRelationships = analyser.getRelationshipsTo(targetIndex,arcrole);
                    AssertJUnit.assertEquals(relationship.getArcrole(),arcrole);
                    AssertJUnit.assertEquals(targetIndex,otherRelationships.get(0).getTargetIndex());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetRootRelationshipsByArcroleAndLinkRole() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                Set<String> linkRoles= analyser.getLinkRoles(arcrole);
                for (String linkRole: linkRoles) {
                    List<Relationship> relationships = analyser.getRootRelationships(linkRole, arcrole);
                    AssertJUnit.assertTrue(relationships.size() > 0);
                    for (Relationship relationship: relationships) {
                        String sourceIndex = relationship.getSourceIndex();
                        List<Relationship> otherRelationships = analyser.getRelationshipsTo(sourceIndex,arcrole);
                        AssertJUnit.assertEquals(0,otherRelationships.size());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }        
 
    @Test
    public void testRelationshipRetrievalByArcrolesAndLinkRole() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> linkRoles = analyser.getLinkRoles();
            for (String linkRole: linkRoles) {
                Set<String> arcroles = analyser.getArcroles(linkRole);
                AssertJUnit.assertTrue(arcroles.size() > 0);
                for (String arcrole: arcroles) {
                    Set<String> arcroleSet = new TreeSet<String>();
                    arcroleSet.add(arcrole);
                    List<Relationship> relationships = analyser.getRelationships(linkRole,arcroleSet);
                    AssertJUnit.assertTrue(relationships.size() > 0);
                    for (Relationship relationship: relationships) {
                        AssertJUnit.assertEquals(relationship.getArcrole(),arcrole);
                        AssertJUnit.assertEquals(relationship.getLinkRole(),linkRole);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testLabelRelationshipRetrievalByArcroleAndSourceIndex() { 
        try {
            storeNetworks();
            Analyser analyser = new AnalyserImpl(store);
            Set<String> arcroles = analyser.getArcroles();
            AssertJUnit.assertTrue(arcroles.size() > 0);
            for (String arcrole: arcroles) {
                List<Relationship> relationships = analyser.getRelationships(arcrole);
                AssertJUnit.assertTrue(relationships.size() > 0);
                for (Relationship relationship: relationships) {
                    String sourceIndex = relationship.getSourceIndex();
                    List<Relationship> labelRelationships = analyser.getLabelRelationships(sourceIndex);
                    for (Relationship r: labelRelationships) {
                        AssertJUnit.assertEquals("label", r.getTargetName());
                        AssertJUnit.assertTrue(r.isToLabel());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testDocumentHasPersistedRelationships() { 
        try {
            Storer storer = new StorerImpl(store);
            storer.deleteRelationships();
            Analyser analyser = new AnalyserImpl(store);
            Set<URI> uris = store.getDocumentURIs();
            for (URI uri: uris) {
                if (store.<Arc>getFragmentsFromDocument(uri,"Arc").size() > 0) {
                    AssertJUnit.assertFalse("Not persisted relationships for " + uri.toString(),analyser.hasAllRelationships(uri));
                }
            }
            storer.storeAllRelationships();
            for (URI uri: uris) {
                if (store.<Arc>getXMLResources("Arc").size() > 0) {
                    AssertJUnit.assertTrue("Not persisted relationships for " + uri.toString(),analyser.hasAllRelationships(uri));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }    
}
