package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Instance;

/**
 * Tests the period summary methods of the Instance interface.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class InstanceContextSummaryTestCase extends DOMLoadingTestCase {
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	@Test
    public void testGetLatestPeriod() {
	    try {
	        URI uri = this.getURI("test.data.local.xbrl.instance.period.summary.1");
	        loader.discover(uri);
	        Instance instance = store.<Instance>getRootFragmentForDocument(uri);
	        AssertJUnit.assertEquals("2008-12-31",instance.getLatestPeriod());
	    } catch (Exception e) {
	        e.printStackTrace();
	        Assert.fail(e.getMessage());
	    }
	}
	
    @Test
    public void testGetEarliestPeriod() {
        try {
            URI uri = this.getURI("test.data.local.xbrl.instance.period.summary.1");
            loader.discover(uri);
            Instance instance = store.<Instance>getRootFragmentForDocument(uri);
            AssertJUnit.assertEquals("2007-12-31",instance.getEarliestPeriod());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }	
	
    
    @Test
    public void testGetEntityResources() {
        try {
            URI uri = this.getURI("test.data.local.xbrl.instance.period.summary.1");
            loader.discover(uri);
            Instance instance = store.<Instance>getRootFragmentForDocument(uri);
            AssertJUnit.assertEquals(0,instance.getEntityResources().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetConcepts() {
        try {
            URI uri = this.getURI("test.data.local.xbrl.instance.period.summary.1");
            loader.discover(uri);
            Instance instance = store.<Instance>getRootFragmentForDocument(uri);
            List<Concept> concepts = instance.getChildConcepts();
            AssertJUnit.assertEquals(2,concepts.size());
            AssertJUnit.assertEquals(2,instance.getChildConceptsCount());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }    

    @Test
    public void testGetEntityIdentifiers() {
        try {
            URI uri = this.getURI("test.data.local.xbrl.instance.period.summary.1");
            loader.discover(uri);
            Instance instance = store.<Instance>getRootFragmentForDocument(uri);
            Map<String,Set<String>> map = instance.getEntityIdentifiers();
            AssertJUnit.assertEquals(1,map.size());
            AssertJUnit.assertEquals("http://xbrl.org/entity/identification/scheme",map.keySet().iterator().next());
            AssertJUnit.assertEquals(1,map.get("http://xbrl.org/entity/identification/scheme").size());
            AssertJUnit.assertEquals("AAA001",map.get("http://xbrl.org/entity/identification/scheme").iterator().next());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }   
    
}
