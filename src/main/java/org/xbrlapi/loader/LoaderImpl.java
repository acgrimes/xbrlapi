package org.xbrlapi.loader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.w3c.dom.Document;
import org.xbrlapi.Fragment;
import org.xbrlapi.cache.Cache;
import org.xbrlapi.data.Store;
import org.xbrlapi.networks.Storer;
import org.xbrlapi.networks.StorerImpl;
import org.xbrlapi.sax.ContentHandlerImpl;
import org.xbrlapi.sax.EntityResolver;
import org.xbrlapi.sax.EntityResolverImpl;
import org.xbrlapi.utilities.XBRLException;
import org.xbrlapi.utilities.XMLDOMBuilder;
import org.xbrlapi.xlink.ElementState;
import org.xbrlapi.xlink.XLinkProcessor;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implementation of the XBRL API Loader interface that validates using
 * a pool of preparsed XML Schemas for XBRL documents.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LoaderImpl implements Loader {

    private static final long serialVersionUID = -1706025148098445175L;

    private static final Logger logger = Logger.getLogger(LoaderImpl.class);

    /**
     * The data store to be used to hold the DTS.
     */
    private Store store;

    /**
     * The cache to use when discovering XML materials specified as a String
     * rather than just via a URI that resolves to the required XML.
     */
    private Cache cache = null;

    /**
     * The Xlink processor
     */
    private XLinkProcessor xlinkProcessor;

    /**
     * The entity resolver to use for resolution of entities (URIs etc) during
     * the loading/discovery process.
     */
    private EntityResolver entityResolver;

    /**
     * The queue of documents remaining to load.
     */
    private TreeSet<URI> documentQueue = new TreeSet<>();

    /**
     * The document history recording system used to track the
     * documents being loaded, their URIs and their document
     * identifiers.  This defaults to a simple one that does basic logging.
     */
    private History history = new HistoryImpl();
    
    /**
     * The stack of fragments that are being built
     */
    transient private Stack<Fragment> fragments = new Stack<Fragment>();

    /**
     * A stack of element states, one per root element
     * of a fragment currently undergoing construction.
     */
    transient private Stack<ElementState> states = new Stack<ElementState>();

    /**
     * The absolute URI of the document currently being parsed. Used to record
     * this metadata in each fragment.
     */
    transient private URI documentURI = null;

    /**
     * The document Id (including the document hash and its counter)
     */
    transient private String documentId = null;

    /**
     * The sorted map of documents that have failed to load.
     * Each URI points to value that is the reason for the failure.
     */
    transient private TreeMap<URI, String> failures = new TreeMap<URI, String>();
    
    /**
     * The sorted set of documents that have successfully been loaded.
     */
    transient private TreeSet<URI> successes = new TreeSet<URI>();    
    
    /**
     * The unique fragment ID, that will be one for the first fragment. This is
     * incremented just before as it is retrieved for use with a new fragment
     * created during the loading process.
     */
    transient private int fragmentId = 0;

    /**
     * The flag, discovering, equals false if the loader is not currently doing document
     * discovery and equals true otherwise.
     */
    transient private boolean discovering = false;

    /**
     * Boolean flag that it set to true by an discovery interrupt request.
     */
    transient private boolean interrupt = false;

    /**
     * The XML DOM used by this loader's fragment builders.
     * This is initialised on creation of the loader.
     */
    transient private Document dom = null;
    
    /**
     * @param store The data store to hold the DTS
     * @param xlinkProcessor The XLink processor to use for link resolution
     * @throws XBRLException if the loader cannot be instantiated.
     */
    public LoaderImpl(Store store, XLinkProcessor xlinkProcessor, EntityResolver entityResolver) throws XBRLException {
        super();
        setStore(store);
        setXlinkProcessor(xlinkProcessor);
        setEntityResolver(entityResolver);
        this.dom = (new XMLDOMBuilder()).newDocument();
        this.initialize();
    }
    
    
    
    /**
     * @param store The data store to hold the DTS
     * @param xlinkProcessor The XLink processor to use for link resolution
     * @param entityResolver The entity resolver to use for resources being loaded.
     * @param uris The array of URIs for loading.
     * @throws XBRLException if the loader cannot be instantiated.
     */
    public LoaderImpl(Store store, XLinkProcessor xlinkProcessor, EntityResolver entityResolver, List<URI> uris)
            throws XBRLException {
        this(store, xlinkProcessor, entityResolver);
        setStartingURIs(uris);
    }

    /**
     * @see Loader#requestInterrupt()
     */
    public void requestInterrupt() {
        interrupt = true;
    }
    
    /**
     * @return true if an interrupt to the loading process 
     * has been requested and false otherwise.
     */
    private boolean interruptRequested() {
        return interrupt;
    }

    /**
     * @see Loader#cancelInterrupt()
     */
    public void cancelInterrupt() {
        interrupt = false;
    }
    
    /**
     * @see Loader#setCache(Cache)
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * @see Loader#getCache()
     */
    public Cache getCache() throws XBRLException {
        if (this.cache == null)
            throw new XBRLException(
                    "The loader cache is null and so cannot be used.");
        return this.cache;
    }
    
    /**
     * @see Loader#getBuilderDOM()
     */
    public Document getBuilderDOM() throws XBRLException {
        if (this.dom == null) {
            this.dom = (new XMLDOMBuilder()).newDocument();
        }
        return this.dom;
    }
    
    private void setDiscovering(boolean value) {
        if (value) logger.debug(Thread.currentThread().getName() + " starting discovery.");
        else logger.debug(Thread.currentThread().getName() + " stopping discovery.");
        discovering = value;
    }
    
    /**
     * @see Loader#isDiscovering()
     */
    public boolean isDiscovering() {
        return discovering;
    }

    /**
     * Set the data store to be used by the loader.
     * @throws XBRLException if the given store is null.
     */
    private void setStore(Store store) throws XBRLException {
        if (store == null) {
            throw new XBRLException("The data store must not be null.");
        }
        this.store = store;
    }

    /**
     * Get the data store used by a loader.
     */
    public Store getStore() {
        return store;
    }

    /**
     * Set the URI of the document now being parsed and set the
     * document ID for the document being parsed by using the store
     * to generate the ID from the URI.  The document ID is used as
     * part of the fragment naming scheme in the data store.
     * @param uri The URI of the document now being parsed.
     * @throws XBRLException if store is null.
     */
    private void setDocumentURI(URI uri) throws XBRLException {
        documentURI = uri;
        String identifier = this.getHistory().getIdentifier(uri);
        if (identifier == null) {
            documentId = getStore().getId(uri.toString());
            getHistory().addRecord(uri,documentId);
        } else {
            documentId = identifier;
        }
    }
    
    
    
    /**
     * @return the document ID for the document being analysed or
     * null if no document is being analysed.
     */
    private String getDocumentId() {
        return documentId;
    }

    /**
     * Get the URI for the document being parsed.
     * @return The original (non-cache) URI of the document being parsed.
     */
    public URI getDocumentURI() {
        return this.documentURI;
    }

    /**
     * Set the XLink processor used by the loader.
     * @throws XBRLException if the given XLink processor is null.
     */
    private void setXlinkProcessor(XLinkProcessor xlinkProcessor) throws XBRLException {
        if (xlinkProcessor == null) {
            throw new XBRLException("The XLink processor must not be null.");
        }
        this.xlinkProcessor = xlinkProcessor;
    }

    /**
     * @see Loader#getXlinkProcessor()
     */
    public XLinkProcessor getXlinkProcessor() {
        return xlinkProcessor;
    }















    /**
     * @see Loader#updateState(ElementState)
     */
    public void updateState(ElementState state) throws XBRLException {

        if (getStates().peek() == state) {
            this.removeFragment();
        }
    }
    
    /**
     * @return the stack of element states for the root
     * elements of the fragments currently being built.
     */
    private Stack<ElementState> getStates() {
        return this.states;
    }        

    /**
     * @see Loader#getFragment()
     */
    public Fragment getFragment() throws XBRLException {
        if (fragments.isEmpty()) return null;
        return fragments.peek();
    }
    
    /**
     * @see Loader#replaceCurrentFragment(Fragment)
     */
    public void replaceCurrentFragment(Fragment replacement) throws XBRLException {
        if (fragments.isEmpty()) throw new XBRLException("There is no current fragment to replace.");
        fragments.pop();
        fragments.push(replacement);
    }    
    /**
     * @see Loader#isBuildingAFragment()
     */
    public boolean isBuildingAFragment() {
        return (!fragments.isEmpty());
    }

    /**
     * @see Loader#add(Fragment, ElementState)
     */
    public void add(Fragment fragment, ElementState state)
            throws XBRLException {

        // Get the XPointer expressions that identify the root of this fragment
        // TODO Should the following xpointer code be contingent on children != null?
        Vector<String> pointers = state.getElementSchemePointers();
        for (String pointer : pointers) {
            fragment.appendElementSchemeXPointer(pointer);
        }

        // Set the document reconstruction metadata for the fragment
        Fragment parent = getFragment();
        if (parent != null) {
            String parentIndex = parent.getIndex();
            if (parentIndex == null) throw new XBRLException("The parent index is null.");
            fragment.setParentIndex(parentIndex);
            fragment.setSequenceToParentElement(parent);
        } else {
            fragment.setParentIndex("");
        }

        fragment.setURI(getDocumentURI());

        // Push the fragment onto the stack of fragments
        fragments.add(fragment);

        // Push the element state onto the stack of fragment root element states
        getStates().add(state);

    }
    


    /**
     * Remove a fragment from the stack of fragments that 
     * are being built by the loader.
     * @throws XBRLException if their are no fragments being built.
     */
    private Fragment removeFragment() throws XBRLException {
        try {
            
            getStates().pop();
//            getChildrenStack().pop();
            Fragment f = fragments.pop();
            getStore().persist(f);
            return f;
        } catch (EmptyStackException e) {
            throw new XBRLException(this.getDocumentURI() + " There are no fragments being built.  The stack of fragments is empty.",e);
        }
    }

    /**
     * @see Loader#discover(List)
     */
    public void discover(List<URI> startingURIs) throws XBRLException {
        for (URI uri: startingURIs) stashURI(uri);
        discover();
    }

    /**
     * @see Loader#discover(URI)
     */
    public void discover(URI uri) throws XBRLException {
        stashURI(uri);
        discover();
    }

    /**
     * @see Loader#discover(String)
     */
    public void discover(String uri) throws XBRLException {
        try {
            discover(new URI(uri));
        } catch (URISyntaxException e) {
            throw new XBRLException("The URI to discover, " + uri + " is malformed.", e);
        }
    }

    /**
     * @see Loader#getDocumentsStillToAnalyse()
     */
    public List<URI> getDocumentsStillToAnalyse() {
        List<URI> documents = new Vector<URI>();
        documents.addAll(documentQueue);
        return documents;
    }


    /**
     * @see Loader#discover()
     */
    public void discover() throws XBRLException {
        getStore().startLoading(this);
        
        Set<URI> newDocuments = new TreeSet<URI>();
        
        int discoveryCount = 1;

        if (isDiscovering()) {
            logger.warn("The loader is already doing discovery so starting discovery achieves nothing.");
            return;
        }
        setDiscovering(true);

        for (URI uri: getStore().getDocumentsToDiscover()) {
            logger.info(uri + " stashed for discovery.");
            this.stashURI(uri);
        }

        URI uri = getNextDocumentToExplore();
        DOCUMENTS: while (uri != null) {
            boolean documentClaimedByThisLoader = store.requestLoadingRightsFor(this,uri);
            if (! documentClaimedByThisLoader) {
                markDocumentAsExplored(uri);
                uri = getNextDocumentToExplore();
                continue DOCUMENTS;
            }
            
            long start = System.currentTimeMillis();

            if (!getStore().hasDocument(uri)) {
                setDocumentURI(uri);
                this.setNextFragmentId("1");
                try {
                    parse(uri);
                    long duration = (System.currentTimeMillis() - start) / 1000;
                    logger.info("#" + discoveryCount + " took " + duration + " seconds. " + (fragmentId-1) + " fragments in " + uri);
                    discoveryCount++;
                    markDocumentAsExplored(uri);
                    newDocuments.add(uri);
                    getStore().sync();
                } catch (XBRLException e) {
                    this.cleanupFailedLoad(uri,"XBRL-API related problems occurred: " + e.getMessage(),e);
                } catch (SAXException e) {
                    this.cleanupFailedLoad(uri,"The document could not be parsed.",e);
                } catch (IOException e) {
                    this.cleanupFailedLoad(uri,"The document could not be accessed.",e);
                }
            } else {
                logger.debug(uri + " is already in the data store.");
                markDocumentAsExplored(uri);
            }

            if (interruptRequested()) {
                cancelInterrupt();
                break DOCUMENTS;
            }

            uri = getNextDocumentToExplore();
        }

        storeDocumentsToAnalyse();
        setDiscovering(false);

        if (documentQueue.size() == 0 && failures.size() == 0) {
            logger.debug("Document discovery completed successfully.");
        } else {
            if (failures.size() > 0) {
                logger.warn("Some documents failed to load.");
            }
            if (documentQueue.size() > 0) {
                logger.info("Document discovery exited without completing.");
            }
        }

        getStore().stopLoading(this);
        
        try {
            if (getStore().isPersistingRelationships() && (newDocuments.size() > 0)) {
                
                // Wait till other loaders using the store have finished with their loading activities.
                while (getStore().isLoading()) {
                    logger.debug("Still doing some loading into the store ... ");
                    Thread.sleep(10000);
                }
                Storer storer = new StorerImpl(getStore());
                storer.storeRelationships(newDocuments);
            }
        } catch (InterruptedException e) {
            logger.error("Failed to persist relationships.");
            Map<URI,String> map = new HashMap<URI,String>();
            for (URI document : newDocuments) {
                map.put(getStore().getMatcher().getMatch(document),"Failed to store relationships.");
            }
            getStore().persistLoaderState(map);
        }
        
        failures = new TreeMap<URI,String>();
        documentQueue = new TreeSet<URI>();
        
    }

    /**
     * @see Loader#discoverNext()
     */
    public void discoverNext() throws XBRLException {

        Store store = this.getStore();
        
        if (isDiscovering()) {
            logger.warn("Already discovering data with this loader so loader will not discover next document as requested.");
            return;
        }
        setDiscovering(true);

        URI uri = getNextDocumentToExplore();
        boolean documentClaimedByThisLoader = store.requestLoadingRightsFor(this,uri);
        while ((store.hasDocument(uri) || !documentClaimedByThisLoader) && (uri != null)) {
            if (documentClaimedByThisLoader) {
                store.recindLoadingRightsFor(this,uri);
            } else {
                this.markDocumentAsExplored(uri);
            }
            uri = getNextDocumentToExplore();
        }

        if (uri != null) {
            logger.debug("Now parsing " + uri);
            setDocumentURI(uri);
            this.setNextFragmentId("1");
            try {
                parse(uri);
                markDocumentAsExplored(uri);
                getStore().sync();
                logger.info((this.fragmentId-1) + " fragments in " + uri);
            } catch (XBRLException e) {
                this.cleanupFailedLoad(uri,"XBRL Problem: " + e.getMessage(),e);
            } catch (SAXException e) {
                this.cleanupFailedLoad(uri,"SAX Problem: " + e.getMessage(),e);
            } catch (IOException e) {
                this.cleanupFailedLoad(uri,"IO Problem: " + e.getMessage(),e);
            }
        }

        logger.info("Finished discovery of " + uri);
        this.storeDocumentsToAnalyse();
        
        setDiscovering(false);

    }
    
    

    /**
     * Perform a discovery starting with an XML document that is represented as
     * a string.
     * 
     * @param uri
     *            The URI to be used for the document that is supplied as a
     *            string. This URI MUST be an absolute URI.
     * @param xml
     *            The string representation of the XML document to be parsed.
     * @throws XBRLException
     *             if the discovery process fails or if the supplied URI is not
     *             absolute or is not a valid URI syntax or the loader does not
     *             have a cache.
     */
    public void discover(URI uri, String xml) throws XBRLException {

        logger.debug("Discovering a resource supplied as a string and with URI: " + uri);

        if (!uri.isAbsolute()) throw new XBRLException("The URI " + uri + " must be absolute.");

        if (uri.isOpaque()) throw new XBRLException("The URI " + uri + " must NOT be opaque.");

        // Copy the XML to the local cache even if it is there already (possibly over-writing existing documents)
        this.getCache().copyToCache(uri, xml);

        try {
            this.stashURI(new URI("http://www.xbrlapi.org/xbrl/xbrl-2.1-roles.xsd"));
        } catch (URISyntaxException e) {
            throw new XBRLException("The standard roles URI could not be formed for discovery.",e);
        }
        
        discover(uri);
    }


    /**
     * Retrieve URI of the next document to parse from the list of starting
     * point URIs provided or URIs found during the discovery process.
     * @return the URI of the next document to explore or null if there are none.
     * @throws XBRLException
     */
    private URI getNextDocumentToExplore() throws XBRLException {
        if (documentQueue.isEmpty()) return null;
        return documentQueue.first();
    }
    
    /**
     * Flag the document as being explored.  Ensure that loading
     * rights for this document have been recinded so that other
     * loaders can act as they deem appropriate.
     * @throws XBRLException
     */
    protected void markDocumentAsExplored(URI uri) {
        documentQueue.remove(uri);
        successes.add(uri);
        getStore().recindLoadingRightsFor(this,uri);
    }

    /**
     * Parse an XML Document supplied as a URI.
     * @param uri The URI of the document to parse.
     * @throws XBRLException IOException ParserConfigurationException SAXException
     * @throws ParserConfigurationException 
     */
    protected void parse(URI uri) throws XBRLException, SAXException, IOException {
        InputSource inputSource = this.getEntityResolver().resolveEntity("", uri.toString());
        ContentHandler contentHandler = new ContentHandlerImpl(this, uri);
        parse(uri, inputSource, contentHandler);
    }

    /**
     * Parse an XML Document supplied as a string the next part of the DTS.
     * @param uri The URI to associate with the supplied XML.
     * @param xml The XML document as a string.
     * @throws XBRLException IOException SAXException ParserConfigurationException
     */
    protected void parse(URI uri, String xml) throws XBRLException, SAXException, IOException {
        InputSource inputSource = new InputSource(new StringReader(xml));
        ContentHandler contentHandler = new ContentHandlerImpl(this, uri, xml);
        parse(uri, inputSource, contentHandler);
    }



    /**
     * Set the starting points for DTSImpl discovery using a linked list
     * @param uris A list of starting point document URIs for DTSImpl discovery
     * @throws XBRLException
     */
    protected void setStartingURIs(List<URI> uris) throws XBRLException {
        if (uris == null)
            throw new XBRLException("Null list of URIs is not permitted.");

        for (int i = 0; i < uris.size(); i++) {
            stashURI(uris.get(i));
        }
    }

    /**
     * @see Loader#stashURI(URI)
     */
    public synchronized void stashURI(URI uri) throws XBRLException {

        // Validate the URI
        if (!uri.isAbsolute()) {
            throw new XBRLException("The URI: " + uri + " must be absolute.");                
        }
        if (uri.isOpaque()) {
            throw new XBRLException("The URI: " + uri + " must not be opaque.");                
        }

        URI dereferencedURI = null;
        try {
            dereferencedURI = new URI(uri.getScheme(),null,uri.getHost(), uri.getPort(), uri.getPath(),null,null);
        } catch (URISyntaxException e) {
            throw new XBRLException("Malformed URI found in DTS discovery process: " + uri, e);
        }

        // Stash the URI if it has not already been stashed
        if (!successes.contains(dereferencedURI)) {
            // Queue up the original URI - ignoring issues of whether it matches another document.
            documentQueue.add(dereferencedURI);
        }

    }

    /**
     * @see Loader#stashURIs(List)
     */
    public void stashURIs(List<URI> uris) throws XBRLException {
        for (URI uri: uris) {
            this.stashURI(uri);
        }
    }

    /**
     * Set the resolver for the resolution of entities found during the loading
     * and XLink processing.
     * @param resolver An entity resolver implementation or null if you want to use 
     * a default entity resolver (without any caching facilities)
     */
    public void setEntityResolver(EntityResolver resolver) {
        if (resolver == null) this.entityResolver = new EntityResolverImpl();
        else this.entityResolver = resolver;
    }

    public String getNextFragmentId() throws XBRLException {
        String id = getCurrentFragmentId();
        incrementFragmentId();
        return id;
    }

    public String getCurrentFragmentId() {
        return getDocumentId() + "_" + (new Integer(fragmentId)).toString();
    }

    public void incrementFragmentId() {
        fragmentId++;
    }

    /**
     * Used to set the next fragment id using the information in the data store.
     * This is useful when coming back to an existing data store to add
     * additional documents.
     * 
     * @param id
     * @throws XBRLException
     */
    private void setNextFragmentId(String id) {
        fragmentId = (new Integer(id)).intValue();
    }

    /**
     * Return the entity resolver being used by the loader.
     * 
     * @return the entity resolver being used by the loader.
     */
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    private boolean useSchemaLocationAttributes = false;

    /**
     * @see Loader#useSchemaLocationAttributes()
     */
    public boolean useSchemaLocationAttributes() {
        return this.useSchemaLocationAttributes;
    }

    /**
     * @see Loader#setSchemaLocationAttributeUsage(boolean)
     */
    public void setSchemaLocationAttributeUsage(boolean useThem) {
        this.useSchemaLocationAttributes = useThem;
    }

    /**
     * @see Loader#storeDocumentsToAnalyse()
     */
    public void storeDocumentsToAnalyse() throws XBRLException {
        Map<URI,String> map = new HashMap<URI,String>();
        for (URI document : documentQueue) {
            if (document.equals(getStore().getMatcher().getMatch(document))) {
                map.put(document,"Document has not yet been analysed");
            }
        }
        for (URI document : failures.keySet()) {
            map.put(document,failures.get(document));
        }
        if (map.size() > 0)  {
            logger.warn("Storing details of " + map.size() + " documents that are yet to be loaded.");
            getStore().persistLoaderState(map);
        }
    }
    
    private void cleanupFailedLoad(URI uri, String reason, Exception e) {
        logger.error(getDocumentURI() + " encountered a loading problem: " + e.getMessage());
        failures.put(uri,reason);
        documentQueue.remove(uri);
        getStore().recindLoadingRightsFor(this,getDocumentURI());
        try {
            getStore().deleteDocument(getDocumentURI());
            logger.info("Purged " + uri + " from the data store.");
        } catch (Exception exception) {
            logger.error("Failed to clean up the document from the data store. " + exception.getMessage());
        }
        fragments = new Stack<Fragment>();
        states = new Stack<ElementState>();
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cache == null) ? 0 : cache.hashCode());
        result = prime * result + ((entityResolver == null) ? 0 : entityResolver.hashCode());
        result = prime * result + ((store == null) ? 0 : store.hashCode());
        result = prime * result + ((documentQueue == null) ? 0 : documentQueue.hashCode());
        result = prime * result + (useSchemaLocationAttributes ? 1231 : 1237);
        result = prime * result + ((xlinkProcessor == null) ? 0 : xlinkProcessor.hashCode());
        return result;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoaderImpl other = (LoaderImpl) obj;
        if (cache == null) {
            if (other.cache != null)
                return false;
        } else if (!cache.equals(other.cache))
            return false;
        if (entityResolver == null) {
            if (other.entityResolver != null)
                return false;
        } else if (!entityResolver.equals(other.entityResolver))
            return false;
        if (store == null) {
            if (other.store != null)
                return false;
        } else if (!store.equals(other.store))
            return false;
        if (documentQueue == null) {
            if (other.documentQueue != null)
                return false;
        } else if (!documentQueue.equals(other.documentQueue))
            return false;
        if (useSchemaLocationAttributes != other.useSchemaLocationAttributes)
            return false;
        if (xlinkProcessor == null) {
            if (other.xlinkProcessor != null)
                return false;
        } else if (!xlinkProcessor.equals(other.xlinkProcessor))
            return false;
        return true;
    }

    /**
     * Handles object inflation.
     * @param in The input object stream used to access the object's serialization.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject( );
        try {
            this.dom = (new XMLDOMBuilder()).newDocument();
            this.initialize();
        } catch (XBRLException e) {
            throw new IOException("The XML resource builder could not be instantiated.",e);
        }
    }
    
    /**
     * Handles object serialization
     * @param out The input object stream used to store the serialization of the object.
     * @throws IOException if the loader is still doing discovery.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        if (this.isDiscovering()) throw new IOException("The loader could not be serialized because it is still loading data.");
        out.defaultWriteObject();
    }    
 
    /**
     * @see Loader#setHistory(History)
     */
    public void setHistory(History newHistory) {
        if (newHistory == null) this.history = new HistoryImpl();
        this.history = newHistory;
    }

    /**
     * @see Loader#getHistory()
     */
    public History getHistory() {
        return history;
    }
    
    /**
     * @see Loader#hasHistory()
     */
    public boolean hasHistory() {
        return (history != null);
    }
    
    private transient XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
    private transient SymbolTable symbolTable = new SymbolTable(BIG_PRIME);
    
    private void initialize() throws XBRLException {

        symbolTable = new SymbolTable(BIG_PRIME);
        grammarPool = new XMLGrammarPoolImpl();
        XMLGrammarPreparser preparser = new XMLGrammarPreparser(symbolTable);
        
        preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);

        preparser.setProperty(GRAMMAR_POOL, grammarPool);
        
        preparser.setFeature("http://xml.org/sax/features/namespaces", true);
        preparser.setFeature("http://xml.org/sax/features/validation", true);

        // note we can set schema features just in case ...
        preparser.setFeature("http://apache.org/xml/features/validation/schema", true);
        preparser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        preparser.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);

        // Specify the entity resolver to use for the schemas.
        preparser.setEntityResolver(getEntityResolver());

        // parse the grammars
        List<URI> schemas = new Vector<URI>();
        schemas.add(URI.create("http://www.xbrlapi.org/xml/schemas/s4s.xsd"));
        schemas.add(URI.create("http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd"));
        schemas.add(URI.create("http://www.xbrl.org/2003/xbrl-linkbase-2003-12-31.xsd"));
        schemas.add(URI.create("http://www.xbrl.org/2003/xl-2003-12-31.xsd"));
        schemas.add(URI.create("http://www.xbrl.org/2003/xlink-2003-12-31.xsd"));
        try {
            for (URI schema: schemas) {
                // This loads the schemas into the grammar pool
                preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, getEntityResolver().resolveSchemaURI(schema));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new XBRLException("The XBRL and related schemas could not be preloaded.");
        }
        grammarPool.lockPool();
    }

    /** Property identifier: symbol table. */
    public static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    /** Property identifier: grammar pool. */
    public static final String GRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;








    


    // a larg(ish) prime to use for a symbol table to be shared
    // among
    // potentially man parsers.  Start one as close to 2K (20
    // times larger than normal) and see what happens...
    public static final int BIG_PRIME = 2039;
    
    
    
    /**
     * Parse the supplied input source.
     * @param uri The URI to be associated with the supplied input source.
     * @param inputSource The input source to parse.
     * @param contentHandler The content handler to use for SAX parsing.
     * @throws XBRLException SAXException IOException
     */
    protected void parse(URI uri, InputSource inputSource, ContentHandler contentHandler) throws XBRLException, SAXException, IOException {

        SAXParser parser = new SAXParser(symbolTable, grammarPool);

        // now must reset features for actual parsing:
        try{
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature("http://xml.org/sax/features/namespaces", true);
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            parser.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
        } catch (Exception e) {
            throw new XBRLException("The parser features could not be set.",e);
        }
        
        parser.setEntityResolver(getEntityResolver());
        parser.setErrorHandler((ErrorHandler) contentHandler);        
        parser.setContentHandler(contentHandler);        
        parser.parse(inputSource);
        
    }

}
