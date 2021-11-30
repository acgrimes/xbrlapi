package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.CustomType;
import org.xbrlapi.DOMLoadingTestCase;

/**
 * Tests the implementation of the org.xbrlapi.CustomType interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class CustomTypeTestCase extends DOMLoadingTestCase {
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
	 * Test getting the roleType definition.
	 */
	@Test
    public void testGetCustomRoleTypeDefinition() {
        try {
            List<CustomType> fragments = store.<CustomType>getXMLResources("RoleType");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (CustomType fragment: fragments) {
                if (fragment.getCustomURI().equals("http://mycompany.com/xbrl/roleE/newExtendedRoleType")) {
                    AssertJUnit.assertEquals("Test variation for defining a new role on a presentationLink", fragment.getDefinition());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	

	

	/**
	 * Test getting the custom type ID.
	 */
	@Test
    public void testGetCustomTypeId() {
        try {
            List<CustomType> fragments = store.<CustomType>getXMLResources("RoleType");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (CustomType fragment: fragments) {
                if (fragment.getCustomURI().equals("http://mycompany.com/xbrl/roleE/newExtendedRoleType")) {
                    AssertJUnit.assertEquals("newExtendedRoleType", fragment.getCustomTypeId());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}		
	
	/**
	 * Test getting the list of used on fragments.
	 */
	@Test
    public void testGetUsedOns() {

		try {
		    List<CustomType> types = store.<CustomType>getXMLResources("RoleType");
		    for (CustomType type: types) {
	            AssertJUnit.assertTrue(type.getUsedOns().size() > 0);
		    }
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
	
}
