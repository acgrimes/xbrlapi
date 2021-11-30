package org.xbrlapi.data.exist.embedded.tests.framework;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.data.exist.embedded.DBConnection;
import org.xbrlapi.utilities.BaseTestCase;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class DatabaseManagerTestCase extends BaseTestCase {

	private DBConnection connection;
	private Collection collection;

	private String domain;
	private String port;
	private String db;
	private String databaseURI;

	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
	    db = configuration.getProperty("exist.embedded.database");
	    databaseURI = "xmldb:exist://" + db;

        try {
            Database database = new org.exist.xmldb.DatabaseImpl();
            database.setProperty("create-database", "true");
        	DatabaseManager.registerDatabase(database);
            collection = DatabaseManager.getCollection(databaseURI);
            AssertJUnit.assertNotNull("Collection is null", collection);
        } catch (XMLDBException e) {
        	Assert.fail("Collection creation failed using " + databaseURI + " " + e.getMessage());
        }
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
		collection.close();
	}
	
	/**
	 * Test the usage of the database manager to get a list of child collections
	 */
	@Test
    public final void testListingOfChildCollections() {
        try {
            String[] childCollections = collection.listChildCollections();     
            for (int i=0; i<childCollections.length; i++) {
                Collection child = collection.getChildCollection(childCollections[i]);
                int nameLength = childCollections[i].length();
                String name = child.getName();
                name = name.substring(name.length() - nameLength);
                AssertJUnit.assertEquals(childCollections[i],name);
            }
            
        } catch (XMLDBException e) {
        	Assert.fail(e.getMessage());
        }
	}	

	/**
	 * Test retrieval of a child collection
	 */
	@Test
    public final void testRetrievalOfAChildCollection() {
        try {
            Collection system = collection.getChildCollection("system");
            AssertJUnit.assertEquals("/db/system",system.getName());
        } catch (XMLDBException e) {
        	Assert.fail(e.getMessage());
        }
	}	

	/**
	 * Test instantiation of a collection management service
	 */
	@Test
    public final void testInstantiationOfACollectionManagementService() {
        try {
            collection.getService("CollectionManagementService", "1.0");
        } catch (XMLDBException e) {
        	Assert.fail("Collection management service could not be instantiated. " + e.getMessage());
        }
	}	
	
}
