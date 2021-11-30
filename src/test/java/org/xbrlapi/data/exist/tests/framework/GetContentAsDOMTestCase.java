package org.xbrlapi.data.exist.tests.framework;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xbrlapi.utilities.BaseTestCase;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
 */
public class GetContentAsDOMTestCase extends BaseTestCase {

	// The collection to hold the test fragments
	private Collection collection = null;
	
    private String domain = null;
    private String port = null;
    private String db = null;
    private String username = null;
    private String password = null; 
    private String databaseURI = null;

	// Test data
	private String childCollectionName = "XMLResourceTestCollection";
	private String name = "test.xml";
	private String content = "<root attribute='value'>content</root>";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
		domain = configuration.getProperty("exist.domain");
        port = configuration.getProperty("exist.port");
        db = configuration.getProperty("exist.database");
        username = configuration.getProperty("exist.username");
        password = configuration.getProperty("exist.password");  
        databaseURI = "xmldb:exist://" + domain + ":" + port + "/" + db;  		
		//Establish the connection to the database
		Database database = new org.exist.xmldb.DatabaseImpl();

		DatabaseManager.registerDatabase(database);
		
		//Create a collection to hold the resources
		Collection container = DatabaseManager.getCollection(databaseURI);
		
		CollectionManagementService service = (CollectionManagementService) container.getService("CollectionManagementService", "1.0");
    	
        service.createCollection(childCollectionName);
        collection = DatabaseManager.getCollection(databaseURI + "/" + childCollectionName,username,password);
		container.close();
		
        // Insert the document to update
		try {
			XMLResource r = (XMLResource) collection.createResource(name, XMLResource.RESOURCE_TYPE);
			r.setContent(content);
			collection.storeResource(r);
		} catch (XMLDBException e) {
			Assert.fail("XMLResource creation or storage failed.  " + e.getMessage());
		}
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
		try {
			collection.removeResource(collection.getResource(name));
	        collection.close();
		} catch (XMLDBException e) {
			Assert.fail("Unexpected XMLDB exception when tearing down test data.");
		}
		
		// Remove the temporary test collection
		Collection container = DatabaseManager.getCollection(databaseURI);
		CollectionManagementService service = (CollectionManagementService) container.getService("CollectionManagementService", "1.0");
        service.removeCollection(childCollectionName);
        container.close();
	}
	
	/**
	 * Test the features of the DOM retrieved from an XMLResource in eXist.
	 */
	@Test
    public final void testGetContentAsDOM() {

		XMLResource resource = null;

		try {
			resource = (XMLResource) collection.getResource(name);
		} catch (XMLDBException e) {
			Assert.fail("XML Resource retrieval failed. " + e.getMessage());
		}

		if (resource == null) {
			Assert.fail("The XML resource that was retrieved is a null.");
		}

		try {
			String content = (String) resource.getContent();
			System.out.println(content);
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a string failed. " + e.getMessage());
		}

		Node node = null;
		try {
			node = resource.getContentAsDOM();
			if (node == null) {
				Assert.fail("The content could not be retrieved as a DOM.");
			}
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a DOM failed. " + e.getMessage());
		}
		
		Document document = null;
		boolean gotADocument = false;
		try {
			document = (Document) node;
			gotADocument = true;
		} catch (ClassCastException e) {
			System.out.println("The node does not cast to document.");
		}
		if (! gotADocument) {
			try {
				document = node.getOwnerDocument();
				if (document == null) {
					Assert.fail("No obvious way to get a document from the DOM node.");
				}
			} catch (ClassCastException e) {
				Assert.fail("Exception thrown trying to get the document from the DOM node.");
			}			
		}
		
		// Test that the document retrieved is what we expected
        AssertJUnit.assertEquals("root",document.getDocumentElement().getLocalName());
	}
	
	/**
	 * Test the ability to modify the dom obtained from the resource
	 */
	@Test
    public final void testChangesToDOMFromXMLResource() {
		try {
			XMLResource resource = (XMLResource) collection.getResource(name);			
			Node node = resource.getContentAsDOM();			
			Document document = (Document) node;
			Element element = document.createElement("new");
			document.getDocumentElement().appendChild(element);
			resource.setContentAsDOM(document);
			collection.storeResource(resource);

			resource = (XMLResource) collection.getResource(name);			
			String content = (String) resource.getContent();			
			System.out.println(content);
			
		} catch (Exception e) {
			Assert.fail("An unexpected exception was thrown when trying to modify the DOM from an XML Resource.");
		}
	}	
	
}
