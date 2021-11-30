package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Entity;

/**
 * Tests the implementation of the org.xbrlapi.ContextComponent interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ContextComponentTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.tuple.instance";
	
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
	 * Test getting context.
	 */
	@Test
    public void testGetContext() {

		try {
		    List<Entity> entities = store.getXMLResources("Entity");
		    AssertJUnit.assertTrue(entities.size() > 0);
	        AssertJUnit.assertEquals("org.xbrlapi.impl.ContextImpl", entities.get(0).getContext().getType());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
