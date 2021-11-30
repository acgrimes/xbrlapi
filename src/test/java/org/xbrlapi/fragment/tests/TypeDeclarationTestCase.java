package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.ComplexTypeDeclaration;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Schema;
import org.xbrlapi.TypeDeclaration;
import org.xbrlapi.utilities.Constants;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class TypeDeclarationTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.tuple.instance";
	
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
    public void testTypeDerivationAnalysis() {
        try {
            Schema schema = store.getSchema(Constants.XBRL21Namespace);
            ComplexTypeDeclaration monetaryItemType = schema.getGlobalDeclaration("monetaryItemType");
            TypeDeclaration parentType = monetaryItemType.getParentType();
            AssertJUnit.assertNotNull(parentType);
            AssertJUnit.assertTrue(monetaryItemType.isDerivedFrom(parentType));
            AssertJUnit.assertTrue(monetaryItemType.isDerivedFrom(parentType.getTargetNamespace(),parentType.getName()));
            AssertJUnit.assertTrue(monetaryItemType.isDerivedFrom(Constants.XMLSchemaNamespace,"decimal"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("1: " + e.getMessage());
        }
    }
    
}
