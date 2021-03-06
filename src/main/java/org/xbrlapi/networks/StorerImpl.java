package org.xbrlapi.networks;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xbrlapi.Arc;
import org.xbrlapi.ArcEnd;
import org.xbrlapi.Fragment;
import org.xbrlapi.Locator;
import org.xbrlapi.Relationship;
import org.xbrlapi.data.Store;
import org.xbrlapi.impl.ErrorImpl;
import org.xbrlapi.impl.LocatorImpl;
import org.xbrlapi.impl.RelationshipImpl;
import org.xbrlapi.impl.RelationshipPriorityComparator;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class StorerImpl implements Storer {

    protected static Logger logger = Logger.getLogger(StorerImpl.class);   
    
    private Store store;
    
    public StorerImpl(Store store) throws XBRLException {
        super();
        setStore(store);
    }

    /**
     * @return The data store in which the relationships are
     * to be persisted.
     * @throws XBRLException if the data store is null.
     */
    private Store getStore() {
        return store;
    }
    
    /**
     * @param store The data store in which the relationships are
     * to be persisted.
     * @throws XBRLException if the data store is null.
     */
    private void setStore(Store store) throws XBRLException {
        if (store == null) throw new XBRLException("The store must not be null.");
        this.store = store;
    }

    /**
     * @see Storer#storeRelationship(Relationship)
     */
    public void storeRelationship(Relationship relationship) throws XBRLException {
        try {
            getStore().persist(relationship);
        } catch (XBRLException e) {
            String arcIndex = relationship.getArcIndex();
            URI document = relationship.getArc().getURI();
            logger.error("Had problems persisting relationships for arc " + arcIndex);
            store.persist(new ErrorImpl(getStore().getId(document.toString() + arcIndex + "_error"), document,arcIndex,"Relationship persistence failed."));
        }
    }

    /**
     * @see Storer#storeRelationship(Arc, Fragment, Fragment)
     */
    public void storeRelationship(Arc arc, Fragment source, Fragment target) throws XBRLException {
        try {
            Relationship relationship = new RelationshipImpl(arc, source, target);
            if (! getStore().hasXMLResource(relationship.getIndex())) getStore().persist(relationship);
        } catch (XBRLException e) {
            String arcIndex = arc.getIndex();
            URI document = arc.getURI();
            logger.error("Had problems persisting relationships for arc " + arcIndex);
            store.persist(new ErrorImpl(getStore().getId(document.toString() + arcIndex + "_error"), document,arcIndex,"Relationship persistence failed."));
        }            
    }    
    

    /**
     * @see Storer#storeRelationships(Network)
     */
    public void storeRelationships(Network network) throws XBRLException {
        for (Relationship relationship: network.getAllRelationships()) {            
            storeRelationship(relationship);
        }
    }

    /**
     * @see Storer#storeRelationships(Networks)
     */
    public void storeRelationships(Networks networks) throws XBRLException {
        for (Network network: networks) {
            storeRelationships(network);
        }
    }

    /**+++
     * @see Storer#storeAllRelationships()
     */
    public void storeAllRelationships() throws XBRLException {
        this.storeRelationships(getStore().getDocumentURIs());
    }

    /**
     * @see Storer#deleteRelationships(URI, URI)
     */
    public void deleteRelationships(URI linkRole, URI arcrole) throws XBRLException {
        Store store = getStore();
        Set<String> indices = store.queryForIndices("#roots#[@type='org.xbrlapi.impl.RelationshipImpl' and @arcRole='"+arcrole+"' and @linkRole='"+linkRole+"']");
        for (String index: indices) {
            store.remove(index);
        }
    }
    
    /**
     * @see Storer#deleteRelationships(URI)
     */
    public void deleteRelationships(URI document) throws XBRLException {
        Store store = getStore();
        Set<String> indices = store.queryForIndices("#roots#[@type='org.xbrlapi.impl.RelationshipImpl' and @arcURI='"+document+"']");
        for (String index: indices) {
            store.remove(index);
        }
    }

    /**
     * @see Storer#deleteRelationships()
     */
    public void deleteRelationships() throws XBRLException {
        Store store = getStore();
        Set<String> indices = store.queryForIndices("#roots#[@type='org.xbrlapi.impl.RelationshipImpl']");
        for (String index: indices) {
            store.remove(index);
        }
    }

    /**
     * @see Storer#storeRelationships(Collection)
     */
    public void storeRelationships(Collection<URI> documents) throws XBRLException {
        for (URI document: documents) {
            storeRelationships(document);
            this.getStore().sync();
        }
    }
    
    /**
     * This implementation uses just 3 database queries for the document.
     * @see Storer#storeRelationships(URI)
     */
    public void storeRelationships(URI document) throws XBRLException {

        Store store = getStore();

        try {
            // Get indices of all arcs in the document.
            Set<String> arcIndices = store.getFragmentIndicesFromDocument(document,"Arc");

            if (arcIndices.size() > 0) {
                logger.info("Persisting relationships for " + arcIndices.size() + " arcs in " + document);

                // Get indices of arc ends in the document.
                Map<String,List<String>> endIndices = new HashMap<String,List<String>>();
                String query = "for $fragment in #roots#[@uri='" + document + "' and */*[@xlink:type='resource' or @xlink:type='locator']] return concat($fragment/@index,' ',$fragment/@parentIndex,$fragment/*/*/@xlink:label)";
                Set<String> pairs = getStore().queryForStrings(query);
                for (String pair: pairs) {
                    int split = pair.indexOf(" ");
                    String index = pair.substring(0,split);
                    String parentIndexPlusLabel = pair.substring(split+1);
                    if (endIndices.containsKey(parentIndexPlusLabel)) {
                        endIndices.get(parentIndexPlusLabel).add(index);
                    } else {
                        List<String> list = new Vector<String>();
                        list.add(index);
                        endIndices.put(parentIndexPlusLabel,list);
                    }
                }
        
                // Get indices of locator target fragments
                Map<String,String> locatorTargets = new HashMap<String,String>();
                //query = "for $locator in #roots#[@uri='" + document + "' and */*/@xlink:type='locator'] return concat($locator/@index,' ',#roots#[@uri=$locator/@targetDocumentURI and xbrlapi:xptr/@value=$locator/@targetPointerValue]/@index)";
                query = "for $locator in #roots#[@uri='" + document + "' and xbrlapi:data/*/@xlink:type='locator'], $target in #roots# let $uri := $locator/@targetDocumentURI let $pointer := $locator/@targetPointerValue where $target/@uri=$uri and $target/xbrlapi:xptr/@value=$pointer return concat($locator/@index,' ',$target/@index)";                
                pairs = getStore().queryForStrings(query);
                for (String pair: pairs) {
                    int split = pair.indexOf(" ");
                    String locatorIndex = pair.substring(0,split);
                    String targetIndex = pair.substring(split+1);
                    locatorTargets.put(locatorIndex,targetIndex);
                }
                
                // Iterate arcs, storing relationships defined by each
                for (String arcIndex: arcIndices) {
                    Arc arc = getStore().<Arc>getXMLResource(arcIndex);
                    String parentIndex = arc.getParentIndex();
                    String fromKey = parentIndex + arc.getFrom();
                    String toKey = parentIndex + arc.getTo();
                    if (endIndices.containsKey(fromKey) && endIndices.containsKey(toKey)) {
                        for (String sourceIndex: endIndices.get(fromKey)) {
                            for (String targetIndex: endIndices.get(toKey)) {
                                try {
                                    boolean storeThisRelationship = true;
                                    Fragment source = null;
                                    if (locatorTargets.containsKey(sourceIndex)) {
                                        source = store.getXMLResource(locatorTargets.get(sourceIndex));
                                    } else {
                                        source = store.getXMLResource(sourceIndex);
                                        if (source.isa(LocatorImpl.class)) {
                                            storeThisRelationship = false;
                                            store.persist(new ErrorImpl(getStore().getId(document.toString() + "_error"), document,"Locator " + sourceIndex + " does not reference an external resource.  Check its href attribute."));
                                        }
                                    }
                                    Fragment target = null;
                                    if (locatorTargets.containsKey(targetIndex)) {
                                        target = store.getXMLResource(locatorTargets.get(targetIndex));
                                    } else {
                                        target = store.getXMLResource(targetIndex);
                                        if (target.isa(LocatorImpl.class)) {
                                            storeThisRelationship = false;
                                            store.persist(new ErrorImpl(getStore().getId(document.toString() + "_error"), document,"Locator " + targetIndex + " does not reference an external resource.  Check its href attribute."));
                                        }
                                    }
                                    if (storeThisRelationship) {
                                        this.storeRelationship(arc,source,target);
                                    }
                                } catch (XBRLException e) {
                                    logger.error("Had problems persisting relationships for arc " + arcIndex);
                                    store.persist(new ErrorImpl(getStore().getId(document.toString() + arcIndex + "_error"), document,arcIndex,"Relationship persistence failed because of trouble finding sources/targets."));
                                }
                            }
                        }
                    }
                }
            }
        } catch (XBRLException e) {
            logger.error("Had problems persisting relationships for " + document);
            e.printStackTrace();
            store.persist(new ErrorImpl(getStore().getId(document.toString() + "_error"), document,"Relationship persistence failed."));
        }
        
        getStore().sync();

    }    
    
    /**
     * @param arc The arc to store relationships for.
     * @throws XBRLException
     */
    private void storeRelationships(Arc arc) throws XBRLException {

        long start = System.currentTimeMillis();

        try {
            List<ArcEnd> sources = arc.getSourceFragments();
            List<ArcEnd> targets = arc.getTargetFragments();
            for (ArcEnd source: sources) {
                for (ArcEnd target: targets) {
                    Fragment s = source;
                    Fragment t = target;
                    if (source.getType().equals("org.xbrlapi.impl.LocatorImpl")) s = ((Locator) source).getTarget();
                    if (target.getType().equals("org.xbrlapi.impl.LocatorImpl")) t = ((Locator) target).getTarget();
                    storeRelationship(arc,s,t);
                }
            }
            
            logger.debug("" + (System.currentTimeMillis() - start) + " ms to store relationships for arc " + arc.getIndex());
            
        } catch (XBRLException e) {
            logger.error("The relationship expressed by arc " + arc.getIndex() + " could not be persisted. " + e.getMessage());
        }

        
    }

    /**
     * @see Storer#deleteInactiveRelationships()
     */
    public void deleteInactiveRelationships() throws XBRLException {

        logger.info("Deleting inactive persisted relationships.");
        Analyser analyser = new AnalyserImpl(getStore());
        
        Set<String> arcroles = analyser.getArcroles();
        for (String arcrole: arcroles) {
            Set<String> linkRoles = analyser.getLinkRoles(arcrole);
            for (String linkRole: linkRoles) {
                this.deleteInactiveRelationships(linkRole,arcrole);
            }
        }
    }

    /**
     * @see Storer#deleteInactiveRelationships(String, String)
     */
    public void deleteInactiveRelationships(String linkRole, String arcrole)
            throws XBRLException {

        logger.info("Deleting inactive persisted relationships for linkRole: " + linkRole + " and arcrole " + arcrole);

        String query = "#roots#[@linkRole='"+linkRole+"' and @arcRole='"+arcrole+"']/@sourceIndex";
        Set<String> sourceIndices = getStore().queryForStrings(query);
        logger.info("# sources = " + sourceIndices.size());
        for (String sourceIndex: sourceIndices) {
            query = "#roots#[@linkRole='"+linkRole+"' and @arcRole='"+arcrole+"' and @sourceIndex='"+sourceIndex+"']/@targetIndex";
            Set<String> targetIndices = getStore().queryForStrings(query);
            for (String targetIndex: targetIndices) {
                Map<String,SortedSet<Relationship>> map = getEquivalentRelationships(linkRole,arcrole,sourceIndex,targetIndex);
                for (String key: map.keySet()) {
                    SortedSet<Relationship> equivalents = map.get(key);
                    Relationship active = equivalents.first();
                    for (Relationship equivalent: equivalents) {
                        if (equivalent != active) {
                            logger.info("removing " + equivalent.getArc().getURI() + " " + equivalent.getIndex());
                        }
                    }
                }
            }
        }
        
    }    

    /**
     * @param linkRole The link role of the relationships to mark.
     * @param arcrole the arcrole  of the relationships to mark.
     * @throws XBRLException
     */
    private void markActiveRelationships(String linkRole, String arcrole)
            throws XBRLException {
        
        String query = "#roots#[@linkRole='"+linkRole+"' and @arcRole='"+arcrole+"']/@sourceIndex";
        Set<String> sourceIndices = getStore().queryForStrings(query);
        logger.info("# sources = " + sourceIndices.size());
        for (String sourceIndex: sourceIndices) {
            query = "#roots#[@linkRole='"+linkRole+"' and @arcRole='"+arcrole+"' and @sourceIndex='"+sourceIndex+"']/@targetIndex";
            Set<String> targetIndices = getStore().queryForStrings(query);
            for (String targetIndex: targetIndices) {
                Map<String,SortedSet<Relationship>> map = getEquivalentRelationships(linkRole,arcrole,sourceIndex,targetIndex);
                for (String key: map.keySet()) {
                    map.get(key).first().setMetaAttribute("active","");
                }
            }
        }
        
    }

    /**
     * @param linkRole The network link role
     * @param arcrole The network arcrole
     * @return a map of sorted sets of equivalent relationships in the network.
     * @throws XBRLException
     */
    private Map<String,SortedSet<Relationship>> getEquivalentRelationships(String linkRole, String arcrole, String sourceIndex, String targetIndex)
            throws XBRLException {
        
        Map<String,SortedSet<Relationship>> map = new HashMap<String,SortedSet<Relationship>>();
        String query = "#roots#[@linkRole='"+linkRole+"' and @arcRole='"+arcrole+"' and @sourceIndex='"+sourceIndex+"' and @targetIndex='"+targetIndex+"']";
        List<Relationship> relationships = this.getStore().<Relationship>queryForXMLResources(query);
        for (Relationship relationship: relationships) {
            String key = relationship.getSourceIndex() + relationship.getTargetIndex() + relationship.getLinkRole() + relationship.getArcrole() + relationship.getSignature();
            if (map.containsKey(key)) {
                map.get(key).add(relationship);
            } else {
                SortedSet<Relationship> set = new TreeSet<Relationship>(new RelationshipPriorityComparator());
                set.add(relationship);
                map.put(key,set);
            }
        }

        return map;
    }

}
