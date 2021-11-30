package org.xbrlapi.data.dom.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.xbrlapi.Arc;
import org.xbrlapi.ArcroleType;
import org.xbrlapi.Fragment;
import org.xbrlapi.RoleType;
import org.xbrlapi.utilities.Constants;

/**
 * Test the XBRL Store implementation of the XBRL specific store functions.
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class StoreImplXBRLTestCase extends BaseTestCase {
	
    private final String STARTING_POINTA = "test.data.custom.link.role";
	private final String STARTING_POINTB = "test.data.custom.link.arcrole";
	private final String STARTING_POINTC = "test.data.custom.resource.role";
    private final String STARTING_POINTD = "real.data.xbrl.2.1.roles";	

    private final String STARTING_POINT_INSTANCE_1 = "test.data.small.instance.1";
    private final String STARTING_POINT_INSTANCE_2 = "test.data.small.instance.2";
		
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINTA));
		loader.discover(this.getURI(STARTING_POINTB));	
		loader.discover(this.getURI(STARTING_POINTC));	
        loader.discover(this.getURI(STARTING_POINTD));
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	@Test
    public void testGetLinkRoles() throws Exception {
		try {
			Set<String> roles = store.getLinkRoles();
			AssertJUnit.assertEquals(2, roles.size());
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testGetArcRoles() throws Exception {
		try {
			Set<String> roles = store.getArcroles();
			AssertJUnit.assertTrue(roles.size() > 7);
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetResourceRoles() throws Exception {
		try {
			List<String> roles = store.getResourceRoles();
			AssertJUnit.assertEquals(3, roles.size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetRoleTypes() throws Exception {
		try {
			List<RoleType> roleTypes = store.getRoleTypes();
            logger.info(roleTypes.size());
			AssertJUnit.assertTrue(roleTypes.size() > 40);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testGetArcroleTypes() throws Exception {
		try {
			List<ArcroleType> arcroleTypes = store.getArcroleTypes();
            for (ArcroleType at: arcroleTypes) {
                logger.debug(at.getCustomURI());
            }
			AssertJUnit.assertEquals(21, arcroleTypes.size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetLinkrolesForAGivenArcrole() throws Exception {
		try {
			Set<String> arcroles = store.getArcroles();
			AssertJUnit.assertTrue(arcroles.size() > 0);
			for (String arcrole: arcroles) {
				Set<String> linkroles = store.getLinkRoles(arcrole);
				logger.info(arcrole + " " + linkroles.size());
			}
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
    public void testGetSpecificArcroleTypes() throws Exception {
		try {
			List<ArcroleType> arcroleTypes = store.getArcroleTypes(Constants.PresentationArcrole);
			AssertJUnit.assertEquals(1, arcroleTypes.size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetSpecificRoleTypes() throws Exception {
		try {
			List<RoleType> roleTypes = store.getRoleTypes(Constants.VerboseLabelRole);
			AssertJUnit.assertEquals(1, roleTypes.size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testCustomArcroleIsUsedCorrectly() throws Exception {
		try {
			List<ArcroleType> roleTypes = store.getArcroleTypes(Constants.PresentationArcrole);
			ArcroleType type = roleTypes.get(0);
			AssertJUnit.assertTrue(type.isUsedOn(Constants.XBRL21LinkNamespace,"presentationArc"));
			AssertJUnit.assertFalse(type.isUsedOn(Constants.XBRL21LinkNamespace,"calculationArc"));
			List<Arc> arcs = store.getXMLResources("Arc");
			AssertJUnit.assertTrue(arcs.size() > 0);
			for (Arc arc: arcs) {
			    if (arc.getLocalname().equals("presentationArc"))
			        AssertJUnit.assertTrue(type.isUsedCorrectly(arc));
			    else
                    AssertJUnit.assertFalse(type.isUsedCorrectly(arc));
			}
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	

    @Test
    public void testFilteredQuerying() throws Exception {

        URI uri1 = this.getURI(this.STARTING_POINT_INSTANCE_1);
        loader.discover(uri1);

        URI uri2 = this.getURI(this.STARTING_POINT_INSTANCE_2);
        loader.discover(uri2);

        logger.info("Done with loading the data.");
        
        List<Fragment> allFragments = store.queryForXMLResources("#roots#");

        logger.info("Total number of fragments: " + allFragments.size());
        
        Set<URI> allURIs = store.getDocumentURIs();
        logger.info("# URIs in store = " + allURIs.size());
        Set<URI> uris = store.getMinimumDocumentSet(uri1);
        AssertJUnit.assertTrue(uris.size() > 1);

        AssertJUnit.assertTrue(allURIs.size() > uris.size());
        
        logger.info("Number of URIs in minimum set = " + uris.size());
        
        store.setFilteringURIs(uris);
        
        List<Fragment> filteredFragments = store.queryForXMLResources("#roots#");

        AssertJUnit.assertTrue(allFragments.size() > filteredFragments.size());        
        
        logger.info(allFragments.size());
        logger.info(filteredFragments.size());
        
    }
	
}
