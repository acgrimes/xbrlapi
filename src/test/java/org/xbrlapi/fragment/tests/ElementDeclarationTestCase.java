package org.xbrlapi.fragment.tests;

/**
 * Tests the implementation of the org.xbrlapi.ElementDeclaration interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.net.URI;
import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.ElementDeclaration;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;
public class ElementDeclarationTestCase extends DOMLoadingTestCase {

	private final String STARTING_POINT = "test.data.multi.concept.schema";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	private String documentStart = 
		"<?xml version=\"1.0\"?>\n"
		+"<schema targetNamespace=\"http://www.xbrlapi.org/targetNamespace/\"\n"
		+  "xmlns:tns=\"http://www.xbrlapi.org/targetNamespace/\"\n"
		+  "xmlns=\"http://www.w3.org/2001/XMLSchema\"\n"
		+  "xmlns:xbrli=\"http://www.xbrl.org/2003/instance\"\n"
		+  "xmlns:link=\"http://www.xbrl.org/2003/linkbase\"\n"
		+  "xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" 
		+  "elementFormDefault=\"qualified\">\n"

		+	"<import namespace=\"http://www.xbrl.org/2003/instance\"\n" 
		+          "schemaLocation=\"http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd\"/>\n"
		+	"<element  name=\"A\"\n" 
		+            "id=\"A\"\n"
		+            "type=\"xbrli:monetaryItemType\"\n"
		+            "substitutionGroup=\"xbrli:item\"\n";
		
	private String documentEnd = "xbrli:periodType=\"instant\"/>\n</schema>\n";

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test retrieval of the value for the nillable attribute.
	 */
	@Test
    public void testGetNillable() {		
		try {
			String xml = documentStart + documentEnd;
			URI uri = new URI("http://www.xbrlapi.org/default.xsd");
			
			loader.discover(uri,xml);
		
			List<ElementDeclaration> fragments = store.<ElementDeclaration>queryForXMLResources("/" + Constants.XBRLAPIPrefix + ":" + "fragment[" + Constants.XBRLAPIPrefix + ":" + "data/xsd:element/@nillable='true']");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertTrue(fragment.getIndex() + " is nillable", fragment.isNillable());				
			}
			fragments = store.<ElementDeclaration>queryForXMLResources("/" + Constants.XBRLAPIPrefix + ":" + "fragment[" + Constants.XBRLAPIPrefix + ":" + "data/xsd:element/@nillable='false']");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertFalse(fragment.getIndex() + " is not nillable", fragment.isNillable());				
			}

			fragments = store.<ElementDeclaration>queryForXMLResources("#roots#/" + Constants.XBRLAPIPrefix + ":" + "data/xsd:element[count(@nillable)=0]");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertFalse(fragment.getIndex() + " is not nillable", fragment.isNillable());		
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test retrieval of the value for the nillable attribute.
	 */
	@Test
    public void testGetFormQualified() {		
		try {
			List<ElementDeclaration> fragments = store.<ElementDeclaration>getXMLResources("ElementDeclaration");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertTrue(fragment.getIndex() + " is element form qualified", fragment.getSchema().isElementFormQualified());				
			}
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
		
	}	
	
	
	/**
	 * Test retrieval of the value for the default attribute.
	 */
	@Test
    public void testGetDefault() {		

		try {
			String xml = 
				documentStart +
				" default=\"12.4\" " + 
				documentEnd;
			URI uri = new URI("http://www.xbrlapi.org/default.xsd");
			loader.discover(uri,xml);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		
		// Missing default attribute
		try {
			List<ElementDeclaration> fragments = store.<ElementDeclaration>queryForXMLResources("#roots#/" + Constants.XBRLAPIPrefix + ":" + "data/xsd:element[count(@default)=0]");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertNull(fragment.getIndex() + " has not default", fragment.getDefault());				
			}
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}

		// Available attribute
		try {
			List<ElementDeclaration> fragments = store.<ElementDeclaration>queryForXMLResources("#roots#/" + Constants.XBRLAPIPrefix + ":" + "data/xsd:element[count(@default)=1]");
			for (int i=0; i< fragments.size(); i++) {
				ElementDeclaration fragment = fragments.get(i);
				AssertJUnit.assertNotNull(fragment.getIndex() + " has not default", fragment.getDefault());				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test retrieval of the value for the fixed attribute.
	 */
	@Test
    public void testGetFixed() {		

		// Missing default attribute
		try {
			List<ElementDeclaration> fragments = store.<ElementDeclaration>getXMLResources("ElementDeclaration");
			ElementDeclaration fragment = fragments.get(0);
			Assert.assertNull(fragment.getFixed());
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}

		// Available attribute
		try {
			String xml = 
				documentStart +
				" fixed=\"12.4\" " + 
				documentEnd;
			URI uri = new URI("http://www.xbrlapi.org/default.xsd");
			loader.discover(uri,xml);

			String query = "#roots#[" + Constants.XBRLAPIPrefix + ":" + "data/*/@fixed='12.4']";
			List<ElementDeclaration> fragments = store.<ElementDeclaration>queryForXMLResources(query);
			ElementDeclaration fragment = fragments.get(0);
			//store.serialize(fragment.getMetadataRootElement());
			AssertJUnit.assertEquals("12.4", fragment.getFixed());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}
	
	/**
	 * Test getting the information about the data type.
	 */
	@Test
    public void testGetTypeInformation() {	
		try {
		    
            List<Concept> fragments = store.getXMLResources("Concept");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Concept fragment: fragments) {

                String type = fragment.getDataRootElement().getAttribute("type");
                AssertJUnit.assertEquals(type, fragment.getTypeQName());

                String prefix = fragment.getPrefixFromQName(type);
                Assert.assertNotNull(prefix);
                AssertJUnit.assertEquals(Constants.XBRL21Prefix , fragment.getTypeNamespaceAlias());

                String namespace = fragment.getNamespaceFromQName(type,fragment.getDataRootElement());
                AssertJUnit.assertEquals(namespace, fragment.getTypeNamespace());
                
                String localname = fragment.getLocalnameFromQName(type);
                AssertJUnit.assertEquals(fragment.getTypeLocalname(), localname);
                
            }
			
		} catch (XBRLException e) {
		    e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting the information about the substitution group.
	 */
	@Test
    public void testGetSubstitutionGroupInformation() {		
	    try {
	        
            List<Concept> fragments = store.getXMLResources("Concept");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Concept fragment: fragments) {

                String sg = fragment.getDataRootElement().getAttribute("substitutionGroup");
                AssertJUnit.assertEquals(sg, fragment.getSubstitutionGroupQName());

                String prefix = fragment.getPrefixFromQName(sg);
                Assert.assertNotNull(prefix);
                AssertJUnit.assertEquals(Constants.XBRL21Prefix , fragment.getSubstitutionGroupNamespaceAlias());

                String namespace = fragment.getNamespaceFromQName(sg,fragment.getDataRootElement());
                AssertJUnit.assertEquals(namespace, fragment.getSubstitutionGroupNamespace());
                
                String localname = fragment.getLocalnameFromQName(sg);
                AssertJUnit.assertEquals(fragment.getSubstitutionGroupLocalname(), localname);
                
            }
			
		} catch (XBRLException e) {
			Assert.fail(e.getMessage());
		}
	}
		
    @Test
    public void testDeterminationOfBeingATuple() {     

        try {
            List<Concept> fragments = store.getXMLResources("Concept");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Concept fragment: fragments) {
                AssertJUnit.assertTrue(fragment.isTuple() || fragment.isItem());
            }
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

	}	
	
    @Test
    public void testGetSubstitutionGroupDeclaration() {     

        try {
            List<Concept> fragments = store.getXMLResources("Concept");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Concept fragment: fragments) {
                ElementDeclaration declaration = fragment.getSubstitutionGroupDeclaration();
                AssertJUnit.assertTrue(declaration.getLocalname().equals("element"));
                AssertJUnit.assertTrue(declaration.getNamespace().equals(Constants.XMLSchemaNamespace));
                AssertJUnit.assertTrue(declaration.getName().equals("item") || declaration.getName().equals("tuple"));
            }
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }   

    
}
