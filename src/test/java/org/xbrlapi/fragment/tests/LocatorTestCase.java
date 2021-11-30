package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.Arc;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fragment;
import java.util.List;
import org.xbrlapi.Locator;

/**
 * Tests the implementation of the org.xbrlapi.Locator interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LocatorTestCase extends DOMLoadingTestCase {
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
	 * Test getting xlink href value.
	 */
	@Test
    public void testGetHref() {	

		try {
			List<Locator> fragments = store.<Locator>getXMLResources("Locator");
			Locator fragment = fragments.get(0);
			AssertJUnit.assertEquals("397-ABC.xsd#A", fragment.getHref());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

	/**
	 * Test getting absolute href value.
	 */
	@Test
    public void testGetAbsoluteHref() {	

		try {
			List<Locator> fragments = store.<Locator>getXMLResources("Locator");
			Locator fragment = fragments.get(0);
			AssertJUnit.assertEquals(configuration.getProperty("test.data.baseURI") + "Common/instance/397-ABC.xsd#A", fragment.getAbsoluteHref().toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting target fragment.
	 */
	@Test
    public void testGetTarget() {

		try {
			List<Locator> fragments = store.<Locator>getXMLResources("Locator");
			Locator fragment = fragments.get(0);
			Fragment target = fragment.getTarget();
			AssertJUnit.assertEquals(fragment.getTargetDocumentURI().toString(), target.getURI().toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting arcs from the locator.
	 */
	@Test
    public void testGetArcsFrom() {

		try {
			List<Locator> fragments = store.<Locator>getXMLResources("Locator");
			Locator fragment = fragments.get(0);
			List<Arc> arcs = fragment.getArcsFrom();
			AssertJUnit.assertEquals(1, arcs.size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting arcs to the locator.
	 */
	@Test
    public void testGetArcsTo() {

		try {
			List<Locator> fragments = store.<Locator>getXMLResources("Locator");
			Locator fragment = fragments.get(0);
			List<Arc> arcs = fragment.getArcsTo();
			AssertJUnit.assertEquals(0, arcs.size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
