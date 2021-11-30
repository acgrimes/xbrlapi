package org.xbrlapi.data.exist.embedded.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.data.Store;
import org.xbrlapi.data.exist.embedded.StoreImpl;
import org.xbrlapi.data.resource.InStoreMatcherImpl;
import org.xbrlapi.loader.Loader;
import org.xbrlapi.loader.LoaderImpl;
import org.xbrlapi.sax.EntityResolver;
import org.xbrlapi.sax.EntityResolverImpl;
import org.xbrlapi.utilities.XBRLException;
import org.xbrlapi.xlink.XLinkProcessor;
import org.xbrlapi.xlink.XLinkProcessorImpl;
import org.xbrlapi.xlink.handler.XBRLCustomLinkRecogniserImpl;
import org.xbrlapi.xlink.handler.XBRLXLinkHandlerImpl;

/**
 * Provides a base test case for tests involving the embedded eXist database.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
abstract public class BaseTestCase extends org.xbrlapi.utilities.BaseTestCase {

	protected Store store = null;
	protected Loader loader = null;
	
	protected String database;
	protected String storeParentPath;
	protected String dataCollectionName;
	protected String username;
	protected String password;
	
	protected String cache = null;
	
	protected final String configurationRoot = "/system/config";	

	protected List<Store> stores = new LinkedList<Store>();


	@BeforeMethod
    protected void setUp() throws Exception {
	    super.setUp();
		database = configuration.getProperty("exist.embedded.database");
		storeParentPath = configuration.getProperty("exist.store.parent.path");
		username = configuration.getProperty("exist.username");
		password = configuration.getProperty("exist.password");
		cache = configuration.getProperty("local.cache");		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
	    super.tearDown();
	}	

	public StoreImpl createStore(String dataCollectionName) throws XBRLException {
	    StoreImpl store = new StoreImpl(database,username,password,storeParentPath,dataCollectionName);
	    store.setMatcher(new InStoreMatcherImpl(store,new CacheImpl(new File(cache))));
	    return store;
	}
	
	public Loader createLoader(Store store) throws XBRLException {
		XBRLXLinkHandlerImpl xlinkHandler = new XBRLXLinkHandlerImpl();
		XBRLCustomLinkRecogniserImpl clr = new XBRLCustomLinkRecogniserImpl(); 
		XLinkProcessor xlinkProcessor = new XLinkProcessorImpl(xlinkHandler ,clr);
		EntityResolver entityResolver = new EntityResolverImpl(new File(cache));
		Loader myLoader = new LoaderImpl(store,xlinkProcessor, entityResolver);
		myLoader.setEntityResolver(entityResolver);
		xlinkHandler.setLoader(myLoader);
		return myLoader;
	}	

}