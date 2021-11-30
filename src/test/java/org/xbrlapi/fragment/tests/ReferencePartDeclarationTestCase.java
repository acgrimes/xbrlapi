package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.ReferencePartDeclaration;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Tests identification of reference part declarations during discovery.
 * Tests the ability to find the reference part declarations in a schema.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ReferencePartDeclarationTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.reference.links";
	
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
	 * Test get the reference part declarations
	 */
	@Test
    public void testGetReferencePartDeclarations() {	

		try {
			List<ReferencePartDeclaration> rpds = store.<ReferencePartDeclaration>getXMLResources("ReferencePartDeclaration");
			for (ReferencePartDeclaration rpd: rpds) {
				logger.info(rpd.getName() + " is a reference part");
				AssertJUnit.assertEquals(Constants.XBRL21LinkNamespace,rpd.getSubstitutionGroupNamespace());
				AssertJUnit.assertEquals("part",rpd.getSubstitutionGroupLocalname());
			}
		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
