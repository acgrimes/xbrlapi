package org.xbrlapi.fragment.tests;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Fragment;
import org.xbrlapi.impl.MockImpl;
import org.xbrlapi.utilities.BaseTestCase;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests the implementation of the org.xbrlapi.Fragment interface without 
 * doing a full data discovery using a LoaderImpl.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class Fragment_LoaderIndependentTestCase extends BaseTestCase {

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	




	
	/**
	 * Test retrieval of the type of fragment from an unstored fragment.
	 */
	@Test
    public void testGetFragmentTypeForAnUnstoredFragment() {

    try {
			Fragment f = new MockImpl("1");
			AssertJUnit.assertEquals("org.xbrlapi.impl.MockImpl",f.getType());
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test that a null index throws an exception when creating a fragment.
	 */
	@Test
    public void testExpectedExceptionCreatingFragmentWithNullIndex() {

		try {
			new MockImpl(null);
			Assert.fail("An exception should be thrown if you create a fragment with a null index.");
		} catch (XBRLException expected) {
		}

	}
	
	/**
	 * Test retrieval of the namespace URI of root node in the fragment
	 * when the fragment has a namespace for the root element.
	 */
	@Test
    public void testGetNamespaceOfAnInConstructionFragmentWithANamespace() {
	    try {
			String ns = Constants.XBRLAPINamespace;
			MockImpl f = new MockImpl("Mockery");
			f.appendDataElement(ns, "root", "my:root");
			AssertJUnit.assertEquals(ns, f.getNamespace());
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}

}
