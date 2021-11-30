package org.xbrlapi.data.exist.tests.framework;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.utilities.BaseTestCase;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class CollectionCreationAndDeletionTestCase extends BaseTestCase {

	private Collection collection;
	private CollectionManagementService service;

	private String domain = null;
	private String port = null;
	private String db = null;
	private String username = null;
	private String password = null;	
	private String databaseURI = null;

	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
		domain = configuration.getProperty("exist.domain");
	    port = configuration.getProperty("exist.port");
	    db = configuration.getProperty("exist.database");
	    username = configuration.getProperty("exist.username");
	    password = configuration.getProperty("exist.password");  
	    databaseURI = "xmldb:exist://" + domain + ":" + port + "/" + db;		
        
	    Database database = new org.exist.xmldb.DatabaseImpl();
        try {
        	DatabaseManager.registerDatabase(database);
            collection = DatabaseManager.getCollection(databaseURI,username, password);
            service = (CollectionManagementService) collection.getService("CollectionManagementService", "1.0");
        } catch (XMLDBException e) {
        	Assert.fail(e.getMessage());
        }
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
		collection.close();
	}


	
	/**
	 * Test creation and deletion of a child collection
	 */
	@Test
    public final void testCreationAndDeletionOfAChildCollection() {
    	String childCollectionName = "testChildCollection";
    	Collection child = null;
    	try {
        	child = collection.getChildCollection(childCollectionName);
        	service.removeCollection(childCollectionName);
        } catch (XMLDBException e) {
        	 //Thrown if the child collection does not exist
        }

        try {
            service.createCollection(childCollectionName);
        } catch (XMLDBException e) {
        	Assert.fail("The collection failed to be added. " + e.getMessage());
        }

        try {
        	child = collection.getChildCollection(childCollectionName);
            AssertJUnit.assertEquals("/db/" + childCollectionName,child.getName());            
        	service.removeCollection(childCollectionName);
        } catch (XMLDBException e) {
        	Assert.fail("The newly added collection could not be retrieved, queried or dropped." + e.getMessage());
        }

	}
	
}
