package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.MixedContentResource;

/**
 * Tests the implementation of the org.xbrlapi.MixedContentResource interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class MixedContentResourceTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.label.links";
	
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
	 * Test getting the language code.
	 */
	@Test
    public void testGetLanguageCode() {	

		try {			
			List<MixedContentResource> fragments = store.<MixedContentResource>getXMLResources("LabelResource");
			MixedContentResource fragment = fragments.get(0);
			AssertJUnit.assertEquals("en", fragment.getLanguage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting the content as a node list.
	 */
	@Test
    public void testGetContent() {	

		try {
			List<MixedContentResource> fragments = store.<MixedContentResource>getXMLResources("LabelResource");
			MixedContentResource fragment = fragments.get(0);
			AssertJUnit.assertEquals(1, fragment.getContentAsNodeList().getLength());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

}
