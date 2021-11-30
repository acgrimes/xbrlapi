package org.xbrlapi.relationships.tests;

/**
 * @see org.xbrlapi.impl.Networks
 * @see org.xbrlapi.impl.NetworksImpl
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.LabelResource;
import org.xbrlapi.Relationship;
import org.xbrlapi.networks.Network;
import org.xbrlapi.networks.Networks;
import org.xbrlapi.utilities.Constants;

public class NetworksTestCase extends DOMLoadingTestCase {

	Networks networks = null;
	LabelResource label = null;
	Concept concept = null;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(getURI("test.data.xlink.titles"));
		List<LabelResource> labels = store.<LabelResource>getXMLResources("LabelResource");
		label = labels.get(0);
		List<Concept> concepts = store.<Concept>getXMLResources("Concept");
		concept = concepts.get(0);
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test whether the networks loading process operates.
	 */
	@Test
    public void testRetrievalOfArcAndLinkRoles() {	

		try {

		    networks = store.getNetworks(Constants.LabelArcrole);

			List<String> arcroles = networks.getArcroles();
			AssertJUnit.assertEquals(1, arcroles.size());
			String arcrole = arcroles.get(0);
			AssertJUnit.assertEquals(Constants.LabelArcrole,arcroles.get(0));

			List<String> linkroles = networks.getLinkRoles(arcroles.get(0));
            AssertJUnit.assertEquals(1, linkroles.size());
            String linkRole = linkroles.get(0);
			AssertJUnit.assertEquals(Constants.StandardLinkRole,linkRole);
			
			Network network = networks.getNetwork(linkRole,arcrole);
			AssertJUnit.assertNotNull(network);
			
			AssertJUnit.assertEquals(1,network.getNumberOfActiveRelationships());
            AssertJUnit.assertEquals(1,network.getNumberOfRelationships());
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
    @Test
    public void testMultipleAdditionsOfTheOneRelationship() {  

        try {
            
            networks = store.getNetworks(Constants.LabelArcrole);
            
            List<String> arcroles = networks.getArcroles();
            AssertJUnit.assertEquals(1, arcroles.size());
            AssertJUnit.assertEquals(Constants.LabelArcrole,arcroles.get(0));

            List<String> linkRoles = networks.getLinkRoles(arcroles.get(0));
            AssertJUnit.assertEquals(1, linkRoles.size());
            AssertJUnit.assertEquals(Constants.StandardLinkRole,linkRoles.get(0));
            
            Networks myNetworks = store.getNetworks();
            networks.addAll(myNetworks);
            AssertJUnit.assertEquals(networks.getSize(),myNetworks.getSize());
            
            for (Network network: networks) {
                Network myNetwork = myNetworks.getNetwork(network.getLinkRole(),network.getArcrole());
                AssertJUnit.assertEquals(network.getNumberOfRelationships(),myNetwork.getNumberOfRelationships());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }	

	/**
	 * Test whether the networks loading process operates.
	 */
	@Test
    public void testRetrievalOfANetwork() {	

		try {
            networks = store.getNetworks(Constants.LabelArcrole);

			List<String> arcroles = networks.getArcroles();
			List<String> linkroles = networks.getLinkRoles(arcroles.get(0));
			Network network = networks.getNetwork(linkroles.get(0),arcroles.get(0));
			AssertJUnit.assertEquals(Constants.LabelArcrole,network.getArcrole());
			AssertJUnit.assertEquals(Constants.StandardLinkRole,network.getLinkRole());
			SortedSet<Relationship> relationships = network.getActiveRelationshipsFrom(label.getIndex());
			AssertJUnit.assertEquals(0,relationships.size());
			relationships = network.getActiveRelationshipsTo(label.getIndex());
			AssertJUnit.assertEquals(1,relationships.size());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test information provided by active relationships in a network.
	 */
	@Test
    public void testRelationshipRetrieval() {	
		
		try {

		    networks = store.getNetworks(Constants.LabelArcrole);
			
		    List<String> arcroles = networks.getArcroles();
			List<String> linkroles = networks.getLinkRoles(arcroles.get(0));
			Network network = networks.getNetwork(linkroles.get(0),arcroles.get(0));
			SortedSet<Relationship> relationships = network.getActiveRelationshipsTo(label.getIndex());
			Relationship relationship = relationships.first();
			AssertJUnit.assertEquals(Constants.LabelArcrole,relationship.getArc().getArcrole());
			AssertJUnit.assertEquals(relationship.getArc().getURI(),relationship.getArcURI());
            AssertJUnit.assertEquals(relationship.getSource().getURI(),relationship.getSourceURI());
            AssertJUnit.assertEquals(relationship.getTarget().getURI(),relationship.getTargetURI());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
	/**
	 * Test retrieval of fragments given a source or target and arc role and link role
	 * information.
	 */
	@Test
    public void testFragmentRetrieval() {	
		
		try {

			List<LabelResource> labels = store.getTargets(concept.getIndex(),null,Constants.LabelArcrole);
			AssertJUnit.assertEquals(1,labels.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
	/**
	 * Test retrieval of networks for a given arc role.
	 */
	@Test
    public void testGeneralNetworkRetrievalForAGivenArcrole() {	
		
		try {

			networks = store.getNetworks(Constants.LabelArcrole);
			AssertJUnit.assertEquals(1,networks.getSize());
			Network network = networks.getNetwork(Constants.StandardLinkRole,Constants.LabelArcrole);
			AssertJUnit.assertNotNull(network);
			Set<String> rootIndexes = network.getRootFragmentIndices();
			AssertJUnit.assertEquals(1,rootIndexes.size());
			List<Concept> roots = network.getRootFragments();
			AssertJUnit.assertEquals(1,roots.size());
			AssertJUnit.assertEquals(concept.getPeriodType(),roots.get(0).getPeriodType());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
