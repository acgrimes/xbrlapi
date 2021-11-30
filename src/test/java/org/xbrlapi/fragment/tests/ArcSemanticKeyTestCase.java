package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Arc;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.utilities.Constants;

/**
 * Tests the implementation of the org.xbrlapi.Arc interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ArcSemanticKeyTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.local.xbrl.arc.semantic.key";
	
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
    public void testGetSemanticKeyIgnoringNSDeclaration() {	

		try {
			List<Arc> fragments = store.<Arc>getXMLResources("Arc");
			Arc fragment = fragments.get(0);
			String key = fragment.getSemanticKey();
			logger.info(key);
			AssertJUnit.assertFalse(key.contains("xmlns"));
            AssertJUnit.assertFalse(key.contains(Constants.XLinkNamespace.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
}
