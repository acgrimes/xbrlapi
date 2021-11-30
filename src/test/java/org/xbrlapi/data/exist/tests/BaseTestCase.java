package org.xbrlapi.data.exist.tests;

import java.io.File;

import org.xbrlapi.cache.CacheImpl;
import org.xbrlapi.data.Store;
import org.xbrlapi.data.exist.StoreImpl;
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
 * Provides a base test case for tests involving the eXist database.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
abstract public class BaseTestCase extends org.xbrlapi.utilities.BaseTestCase {

	protected String host;
	protected String port;
	protected String database;
	protected String storeParentPath;

	protected String username;
	protected String password;
	
	protected String cache = null;
	
	protected final String configurationRoot = "/system/config";	

    protected void setUp() throws Exception {
	    super.setUp();
		host = configuration.getProperty("exist.domain");
		port = configuration.getProperty("exist.port");
		database = configuration.getProperty("exist.database");
		storeParentPath = configuration.getProperty("exist.store.parent.path");			
		username = configuration.getProperty("exist.username");
		password = configuration.getProperty("exist.password");
		cache = configuration.getProperty("local.cache");
	}


    protected void tearDown() throws Exception {
	    super.tearDown();
	}
	
    public StoreImpl createStore(String dataCollectionName) throws XBRLException {
        StoreImpl store = new StoreImpl(host,port,database,username,password,storeParentPath,dataCollectionName);
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