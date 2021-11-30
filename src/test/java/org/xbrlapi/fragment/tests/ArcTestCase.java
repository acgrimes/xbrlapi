package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Arc;
import org.xbrlapi.DOMLoadingTestCase;

/**
 * Tests the implementation of the org.xbrlapi.Arc interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ArcTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.multi.concept.schema";
	
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
	 * Test getting the xlink:to attribute value.
	 */
	@Test
    public void testGetTo() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			AssertJUnit.assertEquals("contributingItem", fragment.getTo());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

	/**
	 * Test getting xlink from value.
	 */
	@Test
    public void testGetFrom() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			AssertJUnit.assertEquals("summationItem", fragment.getFrom());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting xlink show value.
	 */
	@Test
    public void testGetShow() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			Assert.assertNull(fragment.getShow());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}		
	
	/**
	 * Test getting xlink actuate value.
	 */
	@Test
    public void testGetActuate() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			Assert.assertNull(fragment.getActuate());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test getting xbrl order attribute value.
	 */
	@Test
    public void testGetOrder() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			AssertJUnit.assertEquals(new Double(1.0), fragment.getOrder());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Test getting list of fragments that the arc runs from.
	 */
	@Test
    public void testGetSourceFragments() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			AssertJUnit.assertEquals(1, fragment.getSourceFragments().size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting list of fragments that the arc runs to.
	 */
	@Test
    public void testGetTargets() {	

		try {
			List<Arc> fragments = store.<Arc>queryForXMLResources("#roots#[*/*/@xlink:type='arc']");
			Arc fragment = fragments.get(0);
			AssertJUnit.assertEquals(2, fragment.getTargetFragments().size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
