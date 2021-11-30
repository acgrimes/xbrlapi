package org.xbrlapi.fragment.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.Item;
import org.xbrlapi.Tuple;

/**
 * Tests the implementation of the org.xbrlapi.Tuple interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class TupleTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.local.xbrl.instance.tuples.with.units";
		
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
	 * Test getting all child facts.
	 */
	@Test
    public void testGetAllChildFacts() {

		try {
	        List<Tuple> fragments = store.getXMLResources("Tuple");
	        AssertJUnit.assertTrue(fragments.size() > 0);
	        for (Tuple tuple: fragments) {
	            AssertJUnit.assertEquals(tuple.getAllChildren().size(), tuple.getChildFacts().size());	            
	        }
		
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting some child facts.
	 */
	@Test
    public void testGetChildFactsByNameAndOrContextRef() {

		try {

		    List<Tuple> tuples = store.getXMLResources("Tuple");
            AssertJUnit.assertTrue(tuples.size() > 0);
            for (Tuple tuple: tuples) {
                List<Fact> children = tuple.getChildFacts();
                logger.info(tuple.serialize());
                AssertJUnit.assertTrue(children.size() > 0);
                for (Fact child: children) {
                    logger.info(child.serialize());
                    String namespace = child.getNamespace();
                    String localname = child.getLocalname();
                    AssertJUnit.assertTrue(tuple.getChildFacts(namespace,localname).size() > 0);
                    if (! child.isTuple()) {
                        String cr = ((Item) child).getContext().getId();
                        AssertJUnit.assertTrue(tuple.getChildFacts(namespace,localname,cr).size() > 0);
                    }
                }
            }

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	

}
