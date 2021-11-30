package org.xbrlapi.sax.identifiers.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;

import org.xbrlapi.AttributeDeclaration;
import org.xbrlapi.AttributeGroupDeclaration;
import org.xbrlapi.ComplexTypeDeclaration;
import org.xbrlapi.ElementDeclaration;
import org.xbrlapi.Fragment;
import org.xbrlapi.Schema;
import org.xbrlapi.SchemaSequenceCompositor;
import org.xbrlapi.SimpleTypeDeclaration;
import org.xbrlapi.data.dom.tests.BaseTestCase;
import org.xbrlapi.impl.SchemaImpl;
import org.xbrlapi.utilities.Constants;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class SchemaIdentifierTestCase extends BaseTestCase {
	
	private final String STARTING_POINT = "real.data.xbrl.instance.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}
	
	/**
	 * Test basic fragment identification
	 */
	@Test
    public void testSchemaFragmentIdentification() {
		try {
	        URI uri = getURI(this.STARTING_POINT);
			loader.discover(uri);
			List<Schema> schemas = store.<Schema>getXMLResources(SchemaImpl.class);
			AssertJUnit.assertTrue(schemas.size() > 0);
            List<AttributeDeclaration> attributes = store.<AttributeDeclaration>getXMLResources("AttributeDeclaration");
            AssertJUnit.assertTrue(attributes.size() > 0);
            List<SimpleTypeDeclaration> simpleTypes = store.<SimpleTypeDeclaration>getXMLResources("SimpleTypeDeclaration");
            AssertJUnit.assertTrue(simpleTypes.size() > 0);            
            List<ComplexTypeDeclaration> complexTypes = store.<ComplexTypeDeclaration>getXMLResources("ComplexTypeDeclaration");
            AssertJUnit.assertTrue(complexTypes.size() > 0);
            List<AttributeGroupDeclaration> attributeGroups = store.<AttributeGroupDeclaration>getXMLResources("AttributeGroupDeclaration");
            AssertJUnit.assertTrue(attributeGroups.size() > 0);
            List<ElementDeclaration> elementDeclarations = store.<ElementDeclaration>getXMLResources("ElementDeclaration");
            AssertJUnit.assertTrue(elementDeclarations.size() > 0);
            List<SchemaSequenceCompositor> ssc = store.<SchemaSequenceCompositor>getXMLResources("SchemaSequenceCompositor");
            AssertJUnit.assertTrue(ssc.size() > 0);
            
            ComplexTypeDeclaration type = store.<ComplexTypeDeclaration>getSchemaContent(Constants.XBRL21Namespace,"fractionItemType");
            List<Fragment> children = type.getAllChildren();
            AssertJUnit.assertTrue(children.size() > 0);
            
            ElementDeclaration element = store.<ElementDeclaration>getSchemaContent(Constants.XBRL21Namespace,"identifier");
            AssertJUnit.assertTrue(element.hasLocalComplexType());
            ComplexTypeDeclaration ctd = element.getLocalComplexType();
            AssertJUnit.assertNotNull(ctd);
            
		} catch (Exception e) {
		    e.printStackTrace();
			Assert.fail("Unexpected " + e.getMessage());
		}
	}

}
