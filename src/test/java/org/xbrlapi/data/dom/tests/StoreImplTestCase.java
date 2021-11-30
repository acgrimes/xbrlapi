package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.Set;

import org.xbrlapi.Fragment;
import java.util.List;
import org.xbrlapi.Mock;
import org.xbrlapi.impl.MockImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Test the XML DOM XBRLAPI Store implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class StoreImplTestCase extends BaseTestCase {
	private final String STARTING_POINT = "test.data.small.schema";
	
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
    public void testAddFragment() {
		try {
			String index = loader.getNextFragmentId();
			MockImpl d = null;
			d = new MockImpl(index);
			store.persist(d);
			Fragment f = null;
			f = store.getXMLResource(index);
			AssertJUnit.assertNotNull(f);
			AssertJUnit.assertEquals(index,f.getIndex());
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testRemoveFragmentUsingIndex() {
		try {
			String index = "1";
			store.persist(new MockImpl(index));
			AssertJUnit.assertTrue(store.hasXMLResource(index));
			store.remove(index);
			AssertJUnit.assertFalse(store.hasXMLResource(index));
		} catch (XBRLException e) {
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}

	@Test
    public void testRemoveFragmentUsingFragment() {
		try {
			String index = "1";
			store.persist(new MockImpl(index));
			AssertJUnit.assertTrue(store.hasXMLResource(index));
			MockImpl document = (MockImpl) store.getXMLResource(index);
			AssertJUnit.assertNotNull(document);
			store.remove(index);
			AssertJUnit.assertFalse(store.hasXMLResource(index));
		} catch (XBRLException e) {
			Assert.fail("Unexpected exception. " + e.getMessage());
		}
	}
	
	@Test
    public void testQueryData() throws Exception {
	    Mock fragment = new MockImpl("WooHoo");
	    fragment.appendDataElement(Constants.XBRLAPINamespace,"info",Constants.XBRLAPIPrefix + ":info");
	    store.persist(fragment);
		String index = "1";
		//store.serialize(fragment);
		List<Fragment> fragments = null;
		try {
	        String query = "#roots#[@type='org.xbrlapi.impl.MockImpl' and */xbrlapi:info]";
	        logger.debug(query);
	        fragments = store.<Fragment>queryForXMLResources(query);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		try {
			AssertJUnit.assertEquals("1",(new Integer(fragments.size())).toString());
	        AssertJUnit.assertEquals("info",fragments.get(0).getDataRootElement().getLocalName());
			store.remove(index);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
    @Test
    public void testQueryForIndices() {
        try {
            String xpathQuery = "#roots#";
            Set<String> indices = store.queryForIndices(xpathQuery);
            AssertJUnit.assertTrue(! indices.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
	@Test
    public void testQueryLoadedFragments() {
		try {
			loader.discover();			
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		} 
		
		try {
	        String query = "#roots#[*/" + Constants.XMLSchemaPrefix + ":element]";
	        List<Fragment> fragments = store.<Fragment>queryForXMLResources(query);
	        Fragment fragment = fragments.get(0);
	        AssertJUnit.assertEquals("element",fragment.getDataRootElement().getLocalName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}	
	
    @Test
    public void testGetFragments() {
        try {
            List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
            List<Fragment> sameFragments = store.<Fragment>getXMLResources("org.xbrlapi.impl.SchemaImpl");
            AssertJUnit.assertTrue(sameFragments.size() > 0);
            AssertJUnit.assertTrue(fragments.size() == sameFragments.size());
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
    }

	@Test
    public void testHasDocument() {
		try {
			AssertJUnit.assertTrue(store.hasDocument(getURI(STARTING_POINT)));
			AssertJUnit.assertFalse(store.hasDocument(new URI("http://www.rubbish.gcs/crazy.xyz")));
		} catch (Exception e) {
			Assert.fail("Unexpected " + e.getMessage());
		}
	}
}
