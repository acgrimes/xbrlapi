package org.xbrlapi.loader.tests;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Fragment;
import org.xbrlapi.data.dom.tests.BaseTestCase;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.loader.LoaderImpl;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LoaderImplTestCase extends BaseTestCase {
	
	private final String STARTING_POINT = "test.data.small.schema";
	private final String STARTING_POINT_2 = "test.data.small.instance";
	private URI uri1 = null;
	private URI uri2 = null;
	private List<URI> uris = new LinkedList<URI>();
	
 
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		uri1 = getURI(this.STARTING_POINT);
		uri2 = getURI(this.STARTING_POINT_2);
		uris.add(uri1);
		uris.add(uri2);
	}
	
	/**
	 * Test the ability to create a loader given a 
	 * null data store and a XLink Processor.
	 * Class under test for void LoaderImpl(Store, XLinkProcessor)
	 */
	@Test
    public void testLoaderImplStoreXLinkProcessor_FailOnNullStore() throws Exception {
		try {
			new LoaderImpl(null,loader.getXlinkProcessor(),resolver);
			Assert.fail("Null data stores are not allowed for loaders.");
		} catch (XBRLException expected) {
			;
		}
	}

	/**
	 * Test the ability to create a loader given a 
	 * data store and a null XLink Processor.
	 * Class under test for void LoaderImpl(Store, XLinkProcessor)
	 */
	@Test
    public void testLoaderImplStoreXLinkProcessor_FailOnNullXLinkProcessor() {
		try {
			new LoaderImpl(store,null,resolver);
			Assert.fail("Null XLink processors are not allowed for loaders.");
		} catch (XBRLException expected) {
			;
		}
	}

	/**
	 * Tests the creation of loaders with empty and populated lists
	 * Class under test for void LoaderImpl(Store, XLinkProcessor, List).
	 */
	@Test
    public void testLoaderImplStoreXLinkProcessorList() {
		try {
			new LoaderImpl(store,loader.getXlinkProcessor(),resolver, uris);
			new LoaderImpl(store,loader.getXlinkProcessor(),resolver, new LinkedList<URI>());
		} catch (XBRLException e) {
			Assert.fail("Unexpected exception creating a loader using a list of URIs.");
		}
	}

	@Test
    public void testLoaderImplStoreXLinkProcessorList_FailOnNullList() {
		try {
			List<URI> list = null;
			new LoaderImpl(store,loader.getXlinkProcessor(),resolver,list);
			Assert.fail("Null list is not allowed for loaders.");
		} catch (XBRLException expected) {
			;
		}
	}
	
	/**
	 * Test the ability to get the store being used by the loader.
	 *
	 */
	@Test
    public void testGetStore() {
		try {
			Loader l = new LoaderImpl(store,loader.getXlinkProcessor(),resolver);
			AssertJUnit.assertEquals(store,l.getStore());
		} catch (XBRLException e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test the ability to get the Xlink processor
	 * used by the loader.
	 */
	@Test
    public void testGetXlinkProcessor() {
		try {
			Loader l = new LoaderImpl(store,loader.getXlinkProcessor(),resolver);
			AssertJUnit.assertEquals(loader.getXlinkProcessor(),l.getXlinkProcessor());
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test discovery given an array of URI starting points
	 */
	@Test
    public void testDiscover() {
		try {
			loader.stashURI(uri1);
			loader.discover();
		} catch (XBRLException e) {
			Assert.fail("Unexpected " + e.getMessage());
		}
	}

	/**
	 * Test discovery given an XBRL instance as a starting point.
	 */
	@Test
    public void testInstanceDiscover() {
		try {
			loader.stashURI(uri1);
			loader.stashURI(uri2);
			loader.discover();
			
            Set<URI> uris = store.getDocumentURIs();
            AssertJUnit.assertTrue(uris.size() > 0);
            
            boolean found1 = false;
            boolean found2 = false;
            for (URI uri: uris) {
                if (uri.equals(uri1)) {
                    found1 = true;
                }
                if (uri.equals(uri2)) {
                    found2 = true;
                }
            }
            AssertJUnit.assertTrue(found1 && found2);
			
		} catch (XBRLException e) {
		    e.printStackTrace();
			Assert.fail("Unexpected " + e.getMessage());
		}
	}
	
	/**
	 * Test discovery of a list of URIs passed in
	 * as arguments to the discover method.
	 */
	@Test
    public void testDiscoverURIList() {
		try {
			loader.discover(uris);
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}		
	}

	/**
	 * Test discovery given an array of URI starting points
	 */
	@Test
    public void testProcessSchemaLocationAttributes() {
		try {
			loader.setSchemaLocationAttributeUsage(true);
			loader.discover(uris);
		} catch (XBRLException e) {
			Assert.fail("Unexpected " + e.getMessage());
		}
	}

	/**
	 * Test discovery one URI at a time
	 */
	@Test
    public void testDiscoverNext() {

	    try {
			loader.stashURI(uri1);
			loader.stashURI(uri2);
			while (! loader.getDocumentsStillToAnalyse().isEmpty()) {
				loader.discoverNext();
			}
		} catch (XBRLException e) {
			Assert.fail("Unexpected " + e.getMessage());
		}
	}
	
	
	/** 
	 * Test null return from a call to get a fragment from a loader
	 * where no data has been loaded.
	 */
	@Test
    public void testGetFragmentWhenNoFragmentsAreAvailable() {

	    try {
			Fragment f = loader.getFragment();
			AssertJUnit.assertNull(f);
		} catch (XBRLException e) {
		    Assert.fail("null should have been returned.");
		}
	}





}
