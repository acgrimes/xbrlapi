package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Set;

/**
 * Tests the query for strings method.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class QueryForStringsTestCase extends BaseTestCase {
	private final String START = "test.data.small.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	@Test
    public void testQueryForStrings () {
		try {
	        loader.discover(this.getURI(START));
		    
	        String query = "#roots#/@index";
	        Set<String> results = store.queryForStrings(query);
	        AssertJUnit.assertTrue(results.size() > 1);
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
