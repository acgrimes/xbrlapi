package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.ExtendedLink;
import java.util.List;
import org.xbrlapi.Link;
import org.xbrlapi.utilities.Constants;

/**
 * Tests the implementation of the org.xbrlapi.Link interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LinkTestCase extends DOMLoadingTestCase {
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
	 * Test getting link role value when it is missing.
	 */
	@Test
    public void testGetLinkRoles() {	

		try {
		    List<ExtendedLink> links = store.<ExtendedLink>getXMLResources("ExtendedLink");
		    for (Link link: links) {
    		    String role = link.getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"role");
    		    if (! role.equals(""))
    		        AssertJUnit.assertEquals(role,link.getLinkRole().toString());
    		    else
    		        Assert.assertNull(link.getLinkRole());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting link role value when it is provided.
	 */
	@Test
    public void testGetLinkRoleWhenItProvided() {	
        try {
            List<Link> fragments = store.<Link>getXMLResources("ExtendedLink");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Link fragment: fragments) {
                //store.serialize(fragment);
                if (fragment.getLocalname().equals("presentationLink")) {
                    AssertJUnit.assertEquals("http://mycompany.com/xbrl/roleE/newExtendedRoleType",fragment.getLinkRole().toString());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
		
	
}
