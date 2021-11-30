package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Resource;

/**
 * Tests the implementation of the org.xbrlapi.Resource interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ResourceTestCase extends DOMLoadingTestCase {
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
	 * Test getting resource role value.
	 */
	@Test
    public void testGetResourceRole() {	

		try {
			List<Resource> fragments = store.<Resource>queryForXMLResources("#roots#[*/*/@xlink:type='resource']");
			Resource fragment = fragments.get(0);
			AssertJUnit.assertEquals("http://www.xbrl.org/2003/role/label", fragment.getResourceRole().toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting resource title attribute value.
	 */
	@Test
    public void testGetTitleAttribute() {	

		try {
			List<Resource> fragments = store.<Resource>queryForXMLResources("#roots#[*/*/@xlink:type='resource']");
			Resource fragment = fragments.get(0);
			AssertJUnit.assertEquals("label_CurrentAsset", fragment.getTitleAttribute());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

}
