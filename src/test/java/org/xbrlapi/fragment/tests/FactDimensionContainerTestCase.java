package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Context;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Unit;

/**
 * Tests the implementation of the org.xbrlapi.FactDimensionContainer interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class FactDimensionContainerTestCase extends DOMLoadingTestCase {
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
	 * Test getting the id of a context.
	 */
	@Test
    public void testGetContextId() {

		try {
		    List<Context> contexts = store.getXMLResources("Context");
		    for (Context context: contexts) {
	            AssertJUnit.assertEquals(context.getId(), context.getDataRootElement().getAttribute("id"));		        
		    }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

	/**
	 * Test getting the id of a unit.
	 */
	@Test
    public void testGetUnitId() {

		try {
            List<Unit> units= store.getXMLResources("Unit");
            for (Unit unit: units) {
                AssertJUnit.assertEquals(unit.getId(), unit.getDataRootElement().getAttribute("id"));             
            }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
