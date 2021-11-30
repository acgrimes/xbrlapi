package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Segment;

/**
 * Tests the implementation of the org.xbrlapi.Segment interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SegmentTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.segments";
	
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
	 * Test getting complex content for the segment.
	 */
	@Test
    public void testGetComplexContent() {
        try {
            List<Segment> fragments = store.<Segment>getXMLResources("Segment");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Segment fragment: fragments) {
                AssertJUnit.assertEquals(3, fragment.getComplexContent().getLength());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test getting the segment entity.
	 */
	@Test
    public void testGetEntity() {
        try {
            List<Segment> fragments = store.<Segment>getXMLResources("Segment");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Segment fragment: fragments) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.EntityImpl", fragment.getEntity().getType());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
}
