package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.ReferencePart;
import org.xbrlapi.ReferencePartDeclaration;
import org.xbrlapi.ReferenceResource;

/**
 * Tests the implementation of the org.xbrlapi.ReferencePart interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ReferencePartTestCase extends DOMLoadingTestCase {
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
	
	@Test
    public void testGetValue() {	

		try {
			List<ReferenceResource> fragments = store.<ReferenceResource>getXMLResources("ReferenceResource");
			AssertJUnit.assertTrue(fragments.size() > 0);
			ReferenceResource fragment = fragments.get(0);
			
			List<ReferencePart> parts = fragment.getReferenceParts();
			AssertJUnit.assertTrue(parts.size() > 0);
			
			ReferencePart part = parts.get(0);
			AssertJUnit.assertEquals("Fixed Assets", part.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetReferencePartDeclaration() {	
		try {
			List<ReferenceResource> fragments = store.<ReferenceResource>getXMLResources("ReferenceResource");
			ReferenceResource fragment = fragments.get(0);			
			ReferencePart part = fragment.getReferenceParts().get(0);
			//store.serialize(part.getDataRootElement());
			ReferencePartDeclaration declaration = part.getDeclaration();
			AssertJUnit.assertEquals(part.getLocalname(),declaration.getName());
			AssertJUnit.assertEquals(part.getNamespace(),declaration.getTargetNamespace());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}		
}
