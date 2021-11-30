package org.xbrlapi.grabber.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;

import org.xbrlapi.data.dom.tests.BaseTestCase;
import org.xbrlapi.grabber.SECGrabber;
import org.xbrlapi.grabber.SecGrabberImpl;

public class SecGrabberImplTest extends BaseTestCase {


    
    private SECGrabber grabber;
    
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		URI feedURI = getURI("test.data.local.sec");             
        grabber = new SecGrabberImpl(feedURI);
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	

	@Test
    public void testResourceRetrieval() {
		try {

		    List<URI> resources = grabber.getResources();
		    AssertJUnit.assertTrue(resources.size() > 100);			long start = System.currentTimeMillis();
			for (URI resource: resources) {
				if (! loader.getStore().hasDocument(resource))
				loader.discover(resource);
				System.out.println("Time taken = " + ((System.currentTimeMillis() - start) / 1000));
				if (loader.getStore().getDocumentURIs().size() > 3) {
				    break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("An unexpected exception was thrown.");
		}
	}	

    @Test
    public void testResourceSummary() {
        try {

            URI resource = grabber.getResources().get(0);
            AssertJUnit.assertEquals("AMERICAN INTERNATIONAL GROUP INC",grabber.getEntityName(resource));
            AssertJUnit.assertEquals("0000005272",grabber.getCIK(resource));
            AssertJUnit.assertEquals("8-K",grabber.getFormType(resource));
            AssertJUnit.assertEquals("2009-01-23",grabber.getPeriod(resource));
            AssertJUnit.assertEquals("2009-01-23",grabber.getFilingDate(resource));
            AssertJUnit.assertEquals(URI.create("http://www.sec.gov/Archives/edgar/data/5272/000110465909003850/0001104659-09-003850-index.htm"),grabber.getWebpage(resource));
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown.");
        }
    }   
	
	
}
