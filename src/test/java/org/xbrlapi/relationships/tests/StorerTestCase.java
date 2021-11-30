package org.xbrlapi.relationships.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;

import org.w3c.dom.Document;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.LabelResource;
import org.xbrlapi.networks.Network;
import org.xbrlapi.networks.Networks;
import org.xbrlapi.networks.Storer;
import org.xbrlapi.networks.StorerImpl;

/**
 * Added to address concerns raised by R Oldenburg
 * about the {@link StorerImpl#StoreRelationships(Document)} method.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class StorerTestCase extends DOMLoadingTestCase {

	Networks networks = null;
	LabelResource label = null;
	Concept concept = null;

	URI uri;
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		uri = getURI("test.data.xlink.titles");
		logger.info(uri);
		loader.discover(uri);
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	private int initialSize = 0;
	private Storer storer = null;
	
    /**
     * Test persisting of active relationships for all networks.
     */
    @Test
    public void testPersistingOfAllActiveRelationships() { 
        try {

            storer = new StorerImpl(store);
            networks = store.getNetworks();
            AssertJUnit.assertTrue(networks.getSize() > 0);
            initialSize = store.getSize();
            logger.info("Initial size of store = " + initialSize);
            storer.storeRelationships(networks);

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
     * Test persisting of active relationships for each document in turn.
     */
    @Test
    public void testPersistenceOfRelationshipsInEachDocument() { 
        try {
            initialSize = store.getSize();
            logger.info("Initial size = " + initialSize);
            storer = new StorerImpl(store);
            storer.storeRelationships(store.getDocumentURIs());
            int augmentedSize = store.getSize();
            logger.info("Augmented size = " + augmentedSize);
            AssertJUnit.assertTrue(augmentedSize > initialSize);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    

    
    
}
