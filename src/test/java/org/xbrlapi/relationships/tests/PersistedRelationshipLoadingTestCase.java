package org.xbrlapi.relationships.tests;

/**
 * @see org.xbrlapi.impl.Networks
 * @see org.xbrlapi.impl.NetworksImpl
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.LabelResource;
import org.xbrlapi.networks.AnalyserImpl;
import org.xbrlapi.networks.Networks;

public class PersistedRelationshipLoadingTestCase extends DOMLoadingTestCase {

	Networks networks = null;
	LabelResource label = null;
	Concept concept = null;

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        loader.stashURI(getURI("real.data.xbrlapi.languages"));
        loader.stashURI(getURI("real.data.xbrlapi.roles"));
        loader.stashURI(getURI("real.data.xbrlapi.entities"));
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
    /**
     * Test persisting of active relationships for all networks as part of data
     * discovery.
     */
    @Test
    public void testPersistingActiveRelationshipsAsPartOfDiscovery() { 
        try {
            store.setAnalyser(new AnalyserImpl(store));
            loader.discover();
                        
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
