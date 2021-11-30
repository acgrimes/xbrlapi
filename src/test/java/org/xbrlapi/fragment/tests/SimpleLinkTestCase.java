package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.SimpleLink;
import org.xbrlapi.utilities.Constants;

/**
 * Tests the implementation of the org.xbrlapi.SimpleLink interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SimpleLinkTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.custom.link.role";
	
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
	 * Test get Href
	 */
	@Test
    public void testGetHref() {	
        try {
            List<SimpleLink> links = store.<SimpleLink>getXMLResources("SimpleLink");
            AssertJUnit.assertTrue(links.size() > 0);
            for (SimpleLink link: links) {
                if (link.getLocalname().equals("roleTypeRef")) {
                    AssertJUnit.assertEquals("RoleE.xsd#newExtendedRoleType",link.getHref());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }     	    
	}	
	
	/**
	 * Test get absolute Href
	 */
	@Test
    public void testGetAbsoluteHref() {	
        try {
            List<SimpleLink> links = store.<SimpleLink>getXMLResources("SimpleLink");
            AssertJUnit.assertTrue(links.size() > 0);
            for (SimpleLink link: links) {
                if (link.getLocalname().equals("roleTypeRef")) {
                    AssertJUnit.assertEquals(configuration.getProperty("test.data.baseURI") + "Common/linkbase/RoleE.xsd#newExtendedRoleType",link.getAbsoluteHref().toString());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }           
	}

	/**
	 * Test get absolute target fragment
	 */
	@Test
    public void testGetTarget() {	
        try {
            List<SimpleLink> links = store.<SimpleLink>getXMLResources("SimpleLink");
            AssertJUnit.assertTrue(links.size() > 0);
            for (SimpleLink link: links) {
                if (link.getLocalname().equals("roleTypeRef")) {
                    AssertJUnit.assertEquals("roleType",link.getTarget().getLocalname());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }  
	}
	
	/**
	 * Test get arcrole
	 */
	@Test
    public void testGetArcrole() {	
        try {
            List<SimpleLink> links = store.<SimpleLink>getXMLResources("SimpleLink");
            AssertJUnit.assertTrue(links.size() > 0);
            for (SimpleLink link: links) {
                if (link.getLocalname().equals("roleTypeRef")) {
                    AssertJUnit.assertEquals(Constants.LinkbaseReferenceArcrole,link.getArcrole());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }    

	}	
	
}
