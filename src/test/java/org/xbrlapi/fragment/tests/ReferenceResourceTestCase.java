package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.ReferencePart;
import org.xbrlapi.ReferenceResource;

/**
 * Tests the implementation of the org.xbrlapi.ReferenceResource interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ReferenceResourceTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.reference.links";
	
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
	 * Test getting the reference parts in the reference.
	 */
	@Test
    public void testGetReferenceParts() {	

		try {
			List<ReferenceResource> fragments = store.<ReferenceResource>getXMLResources("ReferenceResource");
			ReferenceResource fragment = fragments.get(0);
			AssertJUnit.assertEquals(1, fragment.getReferenceParts().size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting the reference parts in the reference.
	 */
	@Test
    public void testGetReferencePart() {	

		try {
			List<ReferenceResource> fragments = store.<ReferenceResource>getXMLResources("ReferenceResource");
			ReferenceResource fragment = fragments.get(0);
			ReferencePart part = fragment.getReferenceParts().get(0);
			AssertJUnit.assertEquals("org.xbrlapi.impl.ReferencePartImpl", fragment.getReferencePart(part.getNamespace(),part.getLocalname()).getType());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
