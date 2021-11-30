package org.xbrlapi.data.exist.embedded.tests.framework;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.xbrlapi.utilities.BaseTestCase;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class DatabaseManagerInitialisationTestCase extends BaseTestCase {

    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}

	/**
	 * Test the creation and registration of an XMLDB database object
	 */
	@Test
    public final void testDatabaseManagerInitialisation() {
        try {
            Database database = new org.exist.xmldb.DatabaseImpl();
            database.setProperty("create-database", "true");
        	DatabaseManager.registerDatabase(database);
        } catch (XMLDBException e) {
        	Assert.fail(e.getMessage());
        }
	}
	
}
