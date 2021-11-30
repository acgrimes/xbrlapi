package org.xbrlapi.data.exist.tests;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xbrlapi.data.exist.DBConnection;
import org.xbrlapi.data.exist.StoreImpl;
import org.xbrlapi.utilities.XBRLException;
import org.xbrlapi.utilities.XMLDOMBuilder;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class DBConnectionTestCase extends BaseTestCase {

	private String databaseURI = null;

	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
		databaseURI = "xmldb:exist://" + host + ":" + port + "/" + database;
        
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}

	/**
	 * Test the formation of the database URI.
	 */
	@Test
    public final void testDatabaseURI() {
	    StoreImpl store = null;
		try {
		    store = createStore("testDatabaseURI");
	        DBConnection connection = store.getConnection();
			AssertJUnit.assertEquals("Exist connection databaseURI is faulty.",databaseURI,connection.getDatabaseURI());
		} catch (Exception e) {
			Assert.fail("An exception should not be thrown testing the URI of the database connection.");
		} finally {
	          try {
	              store.delete();
	          } catch (Exception x) {
	              Assert.fail("The store could not be deleted.");
	          }
		}
	}

	/**
	 * Test the ability to get an existing root collection
	 */
	@Test
    public final void testGetRootCollection() {
        StoreImpl store = null;
        try {
            store = createStore("testGetRootCollection");
            DBConnection connection = store.getConnection();
            Collection c = connection.getCollection("/");
            AssertJUnit.assertEquals("Collection name was " + c.getName(),"/db",c.getName());
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown when testing getCollection");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}	
	
	/**
	 * Test the ability to get an existing collection.
	 */
	@Test
    public final void testGetCollection() {
        StoreImpl store = null;
        try {
            store = createStore("testGetCollection");
            DBConnection connection = store.getConnection();
            Collection c = connection.getCollection("/");
            Assert.assertEquals(c.getName(),"/db"); 
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown when testing a non-root collection.");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}		
	
	/**
	 * Test the ability to get an existing nested collection
	 */
	@Test
    public final void testGetNestedCollection() {
        StoreImpl store = null;
        try {
            store = createStore("testGetNestedCollection");
            DBConnection connection = store.getConnection();
            Collection c = connection.getCollection("/system/config");
            AssertJUnit.assertEquals("/db/system/config",c.getName());   
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown when testing getCollection");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}	

	/**
	 * Test the ability to get an existing nested collection given parent collection
	 */
	@Test
    public final void testGetNestedCollectionGivenParentCollection() {
        StoreImpl store = null;
        try {
            store = createStore("testGetNestedCollectionGivenParentCollection");
            DBConnection connection = store.getConnection();
            Collection c = connection.getCollection("/system");
            c = connection.getCollection("config", c);
            AssertJUnit.assertEquals("/db/system/config",c.getName());   
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown.");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}	

	/**
	 * Test the ability to return null when getting a non-existent collection
	 */
	@Test
    public final void testReturnNullWhenGettingANonExistentCollection() {
        StoreImpl store = null;
        try {
            store = createStore("testReturnNullWhenGettingANonExistentCollection");
            DBConnection connection = store.getConnection();
            Collection c = connection.getCollection("/doesnotexist");
            AssertJUnit.assertNull(c);     
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown when testing getCollection for a non-existent collection");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}
	
	/**
	 * Test the creation of a root level collection 
	 */
	@Test
    public final void testCreateRootLevelCollection() throws Exception {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testCreateRootLevelCollection");
            connection = store.getConnection();
            Collection c = connection.createRootCollection("rootCollection");
            AssertJUnit.assertEquals("/db/rootCollection",c.getName().toString());
        } catch (Exception e) {
            Assert.fail("The collection failed to be created. " + e.getMessage());
        } finally {
              try {
                  connection.deleteCollection("/rootCollection");
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }	    
		
	}

	/**
	 * Test the creation of a nested collection 
	 */
	@Test
    public final void testCreateNestedCollection() throws Exception {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testCreateNestedCollection");
            connection = store.getConnection();
            Collection parentCollection = null;
            try {           
                parentCollection = connection.createRootCollection("parentCollection");
            } catch (XBRLException e) {
                Assert.fail("The collection failed to be created. " + e.getMessage());
            } 
            
            Collection nestedCollection = connection.createCollection("nestedCollection",parentCollection);
            AssertJUnit.assertEquals("/db/parentCollection/nestedCollection",nestedCollection.getName().toString());
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown when testing getCollection for a non-existent collection");
        } finally {
              try {
                  connection.deleteCollection("/parentCollection");                  
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}

	@Test
    public final void testDeleteEmptyCollection() {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testDeleteEmptyCollection");
            connection = store.getConnection();
            Collection c = connection.createRootCollection("emptyCollection");
            AssertJUnit.assertEquals("/db/emptyCollection",c.getName());
        } catch (Exception e) {
            Assert.fail("Unexpected exception when deleting an empty collection.");
        } finally {
              try {
                  connection.deleteCollection("/emptyCollection");
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}

	@Test
    public final void testDeleteNonEmptyCollection() {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testDeleteNonEmptyCollection");
            connection = store.getConnection();
            Collection c = connection.createRootCollection("nonEmptyCollection");
            XMLResource document = (XMLResource) c.createResource("2", XMLResource.RESOURCE_TYPE);
            document.setContent("<root><child/></root>");
            c.storeResource(document);          
            AssertJUnit.assertEquals(1,c.getResourceCount());
            c.close();
            connection.deleteCollection("/nonEmptyCollection");
            c = connection.getCollection("/nonEmptyCollection");
            Assert.assertNull(c,"The non empty collection was not deleted because it could still be instantiated.");
        } catch (Exception e) {
            Assert.fail("Unexpected exception when deleting a non-empty collection.");
        } finally {
              try {
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
	}

	@Test
    public final void testStoreXMLStringResource() {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testStoreXMLStringResource");
            connection = store.getConnection();
            Collection c = connection.createRootCollection("stringXMLCollection");
            XMLResource document = (XMLResource) c.createResource("document.xml", XMLResource.RESOURCE_TYPE);
            document.setContent("<root><child/></root>");
            c.storeResource(document);
            Document dom = (Document) ((XMLResource) c.getResource("document.xml")).getContentAsDOM();
            AssertJUnit.assertEquals("root",dom.getDocumentElement().getLocalName());
        } catch (Exception e) {
            Assert.fail("Unexpected exception when retrieving an XMLResource.");
        } finally {
              try {
                  connection.deleteCollection("/stringXMLCollection");
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }
		
	}

	@Test
    public final void testStoreXMLDOMResource() {
        StoreImpl store = null;
        DBConnection connection = null;
        try {
            store = createStore("testStoreXMLDOMResource");
            connection = store.getConnection();
            XMLDOMBuilder builder = new XMLDOMBuilder();
            Document d = builder.newDocument();
            d.appendChild(d.createElementNS("http://www.xbrlapi.org/","c:root"));
            Collection c = connection.createRootCollection("xmlNamespacesCollection");
            XMLResource document = (XMLResource) c.createResource("indexValue", XMLResource.RESOURCE_TYPE);
            document.setContentAsDOM(d);
            c.storeResource(document);
            String id = document.getId();
            Assert.assertEquals("indexValue",id,"The resource index was not set properly.");
            Document dom = (Document) ((XMLResource) c.getResource(id)).getContentAsDOM();
            Assert.assertEquals(dom.getDocumentElement().getLocalName(),"root");
            c.close();
        } catch (Exception e) {
            Assert.fail("Unexpected exception when storing an XML Resource.");
        } finally {
              try {
                  connection.deleteCollection("/xmlNamespacesCollection");
                  store.delete();
              } catch (Exception x) {
                  Assert.fail("The store could not be deleted.");
              }
        }	    
	}
	
}
