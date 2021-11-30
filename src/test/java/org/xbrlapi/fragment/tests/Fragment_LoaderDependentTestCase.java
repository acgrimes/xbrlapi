package org.xbrlapi.fragment.tests;

/**
 * Tests the implementation of the org.xbrlapi.Fragment interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.w3c.dom.Element;
import org.xbrlapi.Fragment;
import org.xbrlapi.Schema;
import org.xbrlapi.SimpleLink;
import org.xbrlapi.data.dom.tests.BaseTestCase;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

public class Fragment_LoaderDependentTestCase extends BaseTestCase {

	private final String STARTING_POINT = "test.data.small.schema";
	
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
	 * Test throwing of an exception when an attempt is
	 * made to change the data store that the fragment is stored in.
	 */
	@Test
    public void testExceptionExpectedChangingTheFragmentStore() {
		try {
		    List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
		    for (Fragment fragment: fragments) {
		        Fragment f = store.getXMLResource(fragment.getIndex());
		        try {
		            f.setStore(store);
	                Assert.fail("The store for a fragment cannot be changed once it is set.");
		        } catch (Exception e) {
		            ; // Expected
		        }
		    }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test retrieval of the type of fragment from a stored fragment.
	 */
	@Test
    public void testGetFragmentTypeForAStoredFragment() {

        try {
            List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                AssertJUnit.assertEquals("org.xbrlapi.impl.SchemaImpl",fragment.getType());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	    
	}	

	/**
	 * Test retrieval of the URI of a stored fragment.
	 * TODO Figure out how to test operations on an unstored fragment using the mockfragmentimpl.
	 */
	@Test
    public void testGetURIOfAStoredFragment() {
        try {
            Fragment fragment = store.getRootFragmentForDocument(this.getURI(STARTING_POINT));
            AssertJUnit.assertEquals(this.getURI(STARTING_POINT), fragment.getURI());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }	    
	}	

	/**
	 * Test retrieval of the namespace URI of root node in the fragment
	 * when the fragment has a namespace for the root element.
	 */
	@Test
    public void testGetNamespaceOfAStoredFragmentWithANamespace() {
        try {
            List<Schema> schemas = store.<Schema>getXMLResources("Schema");
            AssertJUnit.assertTrue(schemas.size() > 0);
            for (Fragment fragment: schemas) {
                AssertJUnit.assertEquals(Constants.XMLSchemaNamespace,fragment.getNamespace());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }	    
	}
	
	/**
	 * Test retrieval of the local name of root node in the fragment.
	 */
	@Test
    public void testGetLocalNameOfAStoredFragment() {

        try {
            List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                AssertJUnit.assertEquals("schema",fragment.getLocalname());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

	}	
	
	
	
	/**
	 * Test retrieval of the sequence to the parent element.
	 */
	@Test
    public void testGetSequenceToParentElement() {

        try {
            List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                List<Fragment> children = fragment.getAllChildren();
                for (Fragment child: children) {
                    String required = "";
                    if (child.getMetaAttribute("sequenceToParentElement") != null) {
                        required = child.getMetaAttribute("sequenceToParentElement");
                    }
                    AssertJUnit.assertEquals(required,child.getSequenceToParentElementAsString());
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }	    
		
	}
	
	/**
	 * Test retrieval of the sequence of child simple link fragments
	 */
	@Test
    public void testGetChildSimpleLinks() {

		try {
			List<Schema> schemas = store.<Schema>getXMLResources("Schema");
			for (Schema schema: schemas) {
				if (schema.getURI().equals(this.getURI(STARTING_POINT))) {
					List<SimpleLink> links = schema.getSimpleLinks();
					AssertJUnit.assertEquals(2,links.size());		
				}
			}
		} catch (XBRLException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}
	

	/**
	 * Test retrieval of the parent fragment.
	 */
	@Test
    public void testGetParentFragment() {

        try {
            List<Fragment> fragments = store.<Fragment>getXMLResources("Schema");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                List<Fragment> children = fragment.getAllChildren();
                for (Fragment child: children) {
                    AssertJUnit.assertEquals(fragment.getIndex(),child.getParent().getIndex());
                }
            }    
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}	
	
	/**
	 * Test retrieval of XPath to parent element.
	 */
	@Test
    public void testGetXPathToParentElement() {
	    
        try {
            List<Schema> fragments = store.<Schema>queryForXMLResources("#roots#[@uri='" + this.getURI(STARTING_POINT) + "' and @parentIndex='']");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                List<Fragment> children = fragment.getAllChildren();
                Fragment child = children.get(0);
                AssertJUnit.assertEquals("./*[1]/*[1]",child.getXPath());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }   	    

	}	
	
	/**
	 * Test retrieval of parent element.
	 */
	@Test
    public void testGetParentElement() {
        try {
            List<Schema> fragments = store.<Schema>queryForXMLResources("#roots#[@uri='" + this.getURI(STARTING_POINT) + "' and @parentIndex='']");
            AssertJUnit.assertTrue(fragments.size() > 0);
            for (Fragment fragment: fragments) {
                List<Fragment> children = fragment.getAllChildren();
                Fragment child = children.get(0);
                Fragment parent = child.getParent();
                Element parentElement = child.getParentElement(parent.getDataRootElement());
                AssertJUnit.assertEquals("appinfo",parentElement.getLocalName());       
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }           

	}
	
	
	
	

}
