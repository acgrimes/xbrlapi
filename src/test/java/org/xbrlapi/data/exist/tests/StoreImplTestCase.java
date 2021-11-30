package org.xbrlapi.data.exist.tests;

import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Fragment;
import org.xbrlapi.XML;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.impl.MockImpl;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.utilities.Constants;

/**
 * Test the eXist data store implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class StoreImplTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.small.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}
	


	@Test
    public void testAddFragment() {
        StoreImpl store = null;
        try {
            store = createStore("testAddFragment");
            String index = "1";
            MockImpl d = new MockImpl(index);
            store.persist(d);
            AssertJUnit.assertEquals(index,store.<XML>getXMLResource(index).getIndex());
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }       
	}

	@Test
    public void testRemoveFragmentUsingIndex() {
        StoreImpl store = null;
        try {
            store = createStore("testQueryLoadedFragments");            
            String index = "1";
            store.persist(new MockImpl(index));
            AssertJUnit.assertTrue(store.hasXMLResource(index));
            Fragment fragment = store.getXMLResource(index);
            Assert.assertNotNull(fragment);
            store.remove(index);
            Assert.assertFalse(store.hasXMLResource(index));
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }     	    
	}

	@Test
    public void testQueryData() {
        StoreImpl store = null;
        try {
            store = createStore("testQueryData");
            Loader loader = createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            String xpathQuery = "#roots#";
            List<XML> fragments = store.<XML>queryForXMLResources(xpathQuery);
            Assert.assertTrue(fragments.size() > 1);
            XML fragment = fragments.get(0);
            Assert.assertEquals(fragment.getMetadataRootElement().getLocalName(),"fragment");
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }
	}
	
    @Test
    public void testQueryForIndices() {
        StoreImpl store = null;
        try {
            store = createStore("testQueryForIndices");
            Loader loader = createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            String xpathQuery = "#roots#";
            Set<String> indices = store.queryForIndices(xpathQuery);
            Assert.assertTrue(! indices.isEmpty());
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        }        
    }

	@Test
    public void testQueryLoadedFragments() {
        StoreImpl store = null;
        try {
            store = createStore("testQueryLoadedFragments");
            Loader loader = createLoader(store);
            loader.discover(getURI(STARTING_POINT));
            String query = "#roots#[*/" + Constants.XMLSchemaPrefix + ":element]";
            List<Fragment> fragments = store.<Fragment>queryForXMLResources(query);
            Fragment fragment = fragments.get(0);
            Assert.assertEquals(fragment.getDataRootElement().getLocalName(),"element");
        } catch (Exception e) {
            Assert.fail("Unexpected: " + e.getMessage());
        } finally {
            try {
                if (store!= null) store.delete();
            } catch (Exception x) {
                Assert.fail("The store could not be deleted.");
            }
        } 	    
	}



}
