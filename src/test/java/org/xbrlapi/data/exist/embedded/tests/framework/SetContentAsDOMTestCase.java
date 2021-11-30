package org.xbrlapi.data.exist.embedded.tests.framework;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class SetContentAsDOMTestCase extends BaseTestCase {

	// The collection to hold the test fragments
	private Collection collection = null;
    private String db = null;
    private String username = null;
    private String password = null; 
    private String databaseURI = null;

	private String childCollectionName = "testCollection";
	
	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
        db = configuration.getProperty("exist.embedded.database");
        username = configuration.getProperty("exist.username");
        password = configuration.getProperty("exist.password");  
        databaseURI = "xmldb:exist://" + db;

		//Establish the connection to the database
		Database database = new org.exist.xmldb.DatabaseImpl();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		
		//Create a collection to hold the resources
		Collection container = DatabaseManager.getCollection(databaseURI);
		
		CollectionManagementService service = (CollectionManagementService) container.getService("CollectionManagementService", "1.0");
    	
        service.createCollection(childCollectionName);
        collection = DatabaseManager.getCollection(databaseURI + "/testCollection",username,password);
		container.close();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
		// Remove the temporary test collection
        collection.close();
		Collection container = DatabaseManager.getCollection(databaseURI);
		CollectionManagementService service = (CollectionManagementService) container.getService("CollectionManagementService", "1.0");
        service.removeCollection(childCollectionName);
        container.close();
	}
	
	@Test
    public final void testSetContentAsDOMWithoutNamespaces() {
		
		Document doc = null;
		System.setProperty(
        		"javax.xml.parsers.DocumentBuilderFactory", 
        		"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(false);
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Assert.fail("The DOM parser was not configured correctly to build an XML DOM.");
		}
		doc = builder.newDocument();
		Element rootElem = doc.createElement("element");
		rootElem.setAttribute("attribute", "value of the attribute");
		Element propertyElem = doc.createElement("property");
		propertyElem.setAttribute("key", "value");
		propertyElem.appendChild(doc.createTextNode("text"));
		propertyElem.setAttribute("a", "b");
		propertyElem.setAttribute("c", "d");
		rootElem.appendChild(propertyElem);
		doc.appendChild(rootElem);
		
		XMLResource document = null;
		String documentId = "1";
    	try {
			// Create the resource
    		
			document = (XMLResource) collection.createResource(documentId, XMLResource.RESOURCE_TYPE);
			document.setContentAsDOM(doc);
		} catch (XMLDBException e) {
			Assert.fail("XMLResource creation and population with the DOM object failed.");
		}
			
		// Store the document
		try {
			collection.storeResource(document);
		} catch (XMLDBException e) {
			Assert.fail("Document storage failed. " + e.getMessage());
		}

		XMLResource resource = null;
		try {
			resource = (XMLResource) collection.getResource(documentId);
		} catch (XMLDBException e) {
			Assert.fail("XML Resource retrieval failed. " + e.getMessage());
		}
			
		// Retrieve the content as a string
		String content = null;
		try {
			content = (String) resource.getContent();
			AssertJUnit.assertTrue(content.length() > 0);
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a string failed. " + e.getMessage());
		}
		
		// Retrieve the DOM
		doc = null;
		try {
			doc = (Document) resource.getContentAsDOM();
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a DOM failed. " + e.getMessage());
		}
		
		// Test that the document retrieved is what we expected
        AssertJUnit.assertEquals("element",doc.getDocumentElement().getLocalName());

	}
	
	@Test
    public final void testSetContentAsDOMWithNamespaces() {

		Document doc = null;
		System.setProperty(
        		"javax.xml.parsers.DocumentBuilderFactory", 
        		"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(false);
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Assert.fail("The DOM parser was not configured correctly to build an XML DOM.");
		}
		doc = builder.newDocument();
		Element rootElem = doc.createElementNS("http://www.xbrlapi.org/","c:element");
		rootElem.setAttribute("attribute", "value of the attribute");
		Element propertyElem = doc.createElementNS("http://www.xbrlapi.org/","c:property");
		propertyElem.setAttribute("key", "value");
		propertyElem.appendChild(doc.createTextNode("text"));
		propertyElem.setAttribute("a", "b");
		propertyElem.setAttribute("c", "d");
		rootElem.appendChild(propertyElem);
		doc.appendChild(rootElem);
		
		XMLResource document = null;
		String documentId = "2";
    	try {
			// Create the resource
    		
			document = (XMLResource) collection.createResource(documentId, "XMLResource");//XMLResource.RESOURCE_TYPE
			document.setContentAsDOM(doc);
		} catch (XMLDBException e) {
			Assert.fail("XMLResource creation and population with the DOM object failed.");
		}
			
		// Store the document
		try {
			collection.storeResource(document);
		} catch (XMLDBException e) {
			Assert.fail("Document storage failed. " + e.getMessage());
		}

		XMLResource resource = null;
		try {
			resource = (XMLResource) collection.getResource(documentId);
		} catch (XMLDBException e) {
			Assert.fail("XML Resource retrieval failed. " + e.getMessage());
		}
			
		// Retrieve the content as a string
		String content = null;
		try {
			content = (String) resource.getContent();
            AssertJUnit.assertTrue(content.length() > 0);
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a string failed. " + e.getMessage());
		}
		
		// Retrieve the DOM
		doc = null;
		try {
			doc = (Document) resource.getContentAsDOM();
		} catch (XMLDBException e) {
			Assert.fail("Getting content of resource as a DOM failed. " + e.getMessage());
		}
		
		// Test that the document retrieved is what we expected
        AssertJUnit.assertEquals("element",doc.getDocumentElement().getLocalName());
	}
}
