package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Context;
import org.xbrlapi.DOMLoadingTestCase;

/**
 * Tests the implementation of the org.xbrlapi.Context interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ContextTestCase extends DOMLoadingTestCase {
    
	private final String STARTING_POINT = "test.data.scenarios";
	
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
	 * Test getting entity.
	 */
	@Test
    public void testGetEntity() {

		try {
		    List<Context> contexts = store.<Context>getXMLResources("Context");
		    AssertJUnit.assertTrue(contexts.size() > 0);
		    for (Context context: contexts) {
	            AssertJUnit.assertEquals("org.xbrlapi.impl.EntityImpl", context.getEntity().getType());
		    }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting period.
	 */
	@Test
    public void testGetPeriod() {

		try {
            List<Context> contexts = store.<Context>getXMLResources("Context");
            AssertJUnit.assertTrue(contexts.size() > 0);
            for (Context context: contexts) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.PeriodImpl", context.getPeriod().getType());
            }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting scenario.
	 */
	@Test
    public void testGetScenario() {

		try {
            List<Context> contexts = store.<Context>getXMLResources("Context");
            AssertJUnit.assertTrue(contexts.size() > 0);
            int scenarios = 0;
            for (Context context: contexts) {
                //store.serialize(context);
/*                List<Fragment> children = context.getAllChildren();
                for (Fragment child: children) {
                    store.serialize(child);
                }
*/                if (context.getScenario() != null) {
                    AssertJUnit.assertEquals("org.xbrlapi.impl.ScenarioImpl", context.getScenario().getType());                    
                    scenarios++;
                }
            }
            AssertJUnit.assertTrue(scenarios > 0);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test c-equality assessment.
	 */
	@Test
    public void testCEquality() {

		try {
			List<Context> fragments = store.getXMLResources("Context");
			Context fragment = fragments.get(0); 
			Context other = fragments.get(0);
			AssertJUnit.assertTrue(fragment.equals(other));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
