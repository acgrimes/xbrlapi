package org.xbrlapi.networks;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xbrlapi.Fragment;
import org.xbrlapi.Relationship;
import org.xbrlapi.data.Store;
import org.xbrlapi.impl.RelationshipOrderComparator;
import org.xbrlapi.utilities.XBRLException;

/**
 * This Analyser returns information based upon all
 * <em>active</em> relationships.
 * Set this as the analyser used by a store when you want that
 * store to produce query results where only active relationships
 * are considered.
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class AnalyserImpl implements Analyser {

    private static final long serialVersionUID = -3842008045278037807L;
    
    protected final static Logger logger = Logger.getLogger(AnalyserImpl.class);
    
    /**
     * Algorithm involves:
     * <ol>
     *  <li>Getting all persisted relationships in the document</li>
     *  <li>Getting all arc+arcend+arcsource combinations in the document</li>
     *  <li>Matching each arc to its persisted relationships</li>
     *  <li>Returning false if any arc does not have persisted relationships</li>
     * </ol>
     * @see Analyser#hasAllRelationships(URI)
     */
    public boolean hasAllRelationships(URI document) throws XBRLException {
        String query = 
            "for $arc in #roots#[@uri='"+document+"' and */*/@xlink:type='arc'], " +
            "$start in #roots#[@parentIndex=$arc/@parentIndex and */*[@xlink:type='resource' or @xlink:type='locator']], " +
            "$end in #roots#[@parentIndex=$arc/@parentIndex and */*[@xlink:type='resource' or @xlink:type='locator']] where " +
            "$arc/*/*/@xlink:from=$start/*/*/@xlink:label " +
            "and $arc/*/*/@xlink:to=$end/*/*/@xlink:label " +
            "return concat($arc/@index,' ',$start/@index,' ',$end/@index)";
        long tripleCount = getStore().queryCount(query);
        logger.debug("#Triples =" + tripleCount);
        query = "for $relationship in #roots# where $relationship/@type='org.xbrlapi.impl.RelationshipImpl' and $relationship/@arcURI='" + document + "' return $relationship";
        long relationshipCount = store.queryCount(query);
        logger.debug("#Relationships =" + relationshipCount);
        if (tripleCount != relationshipCount) return false;
        return true;

    }
    
    /**
     * This method provides a place to filter relationships after retrieving them
     * to eliminate overridden and prohibited relationships as desired.
     * @param query The query to run to get the relationships.
     * @return the list of relationships returned by the query.
     * @throws XBRLException
     */
    protected List<Relationship> queryForRelationships(String query) throws XBRLException {
        List<Relationship> relationships = getStore().<Relationship>queryForXMLResources(query);
        Networks networks = new NetworksImpl(getStore());
        networks.addRelationships(relationships);
        return networks.getActiveRelationships();
    }
    
    /**
     * @see Analyser#getAllRelationships()
     */
    public List<Relationship> getAllRelationships() throws XBRLException {
        return queryForRelationships("#roots#[@type='org.xbrlapi.impl.RelationshipImpl']");
    }    
    
    /**
     * @param store The data store to use.
     * @throws XBRLException if the store is null.
     */
    public AnalyserImpl(Store store) throws XBRLException {
        super();
        setStore(store);
    }
    
    private Store store = null;
    
    /**
     * @param store The store that the active relationships are persisted in.
     * @throws XBRLException if the store is null.
     */
    private void setStore(Store store) throws XBRLException {
        if (store == null) throw new XBRLException("The store must not be null.");
        this.store = store;
    }
    
    /**
     * @return the store that the active relationships are persisted in.
     */
    protected Store getStore() {
        return this.store;
    }
    
    /**
     * @see Analyser#getArcroles()
     */
    public Set<String> getArcroles() throws XBRLException {
        return store.getArcroles();
    }

    /**
     * @see Analyser#getArcroles(String)
     */
    public Set<String> getArcroles(String linkRole) throws XBRLException {
        return store.getArcroles(linkRole);
    }

    /**
     * @see Analyser#getLinkRoles()
     */
    public Set<String> getLinkRoles() throws XBRLException {
        return store.getLinkRoles();
    }

    /**
     * @see Analyser#getLinkRoles(String)
     */
    public Set<String> getLinkRoles(String arcrole) throws XBRLException {
        return store.getLinkRoles(arcrole);
    }

    /**
     * @see Analyser#getRelationships(Set)
     */
    public List<Relationship> getRelationships(Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            relationships.addAll(this.getRelationships(arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationships(String, Set)
     */
    public List<Relationship> getRelationships(String linkRole, Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            relationships.addAll(getRelationships(linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationships(String, String)
     */
    public List<Relationship> getRelationships(String linkRole, String arcrole) throws XBRLException {
        String query = "#roots#[@arcRole='"+ arcrole +"' and @linkRole='"+ linkRole +"']";
        return queryForRelationships(query);
    }

    /**
     * @see Analyser#getRelationships(String)
     */
    public List<Relationship> getRelationships(String arcrole) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String linkRole: getLinkRoles(arcrole)) {
            relationships.addAll(this.getRelationships(linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsFrom(String, Set)
     */
    public List<Relationship> getRelationshipsFrom(String sourceIndex, Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            for (String linkRole: getLinkRoles(arcrole)) {
                relationships.addAll(this.getRelationshipsFrom(sourceIndex,linkRole, arcrole));
            }
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsFrom(String, String, Set)
     */
    public List<Relationship> getRelationshipsFrom(String sourceIndex, String linkRole, Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            relationships.addAll(this.getRelationshipsFrom(sourceIndex, linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsFrom(String, String, String)
     */
    public SortedSet<Relationship> getRelationshipsFrom(String sourceIndex, String linkRole, String arcrole) throws XBRLException {
        String lr = "";
        String ar = "";
        if (linkRole != null) {
            lr = " and @linkRole='"+ linkRole +"'";            
        }
        if (arcrole != null) {
            ar = " and @arcRole='"+ arcrole +"'";            
        }
        String query = "#roots#[@sourceIndex='"+ sourceIndex +"'" + ar + lr + "]";
        List<Relationship> list = queryForRelationships(query);
        SortedSet<Relationship> sortedSet = new TreeSet<Relationship>(new RelationshipOrderComparator());
        sortedSet.addAll(list);
        return sortedSet;
    }    

    /**
     * @see Analyser#getRelationshipsFrom(String, String)
     */
    public List<Relationship> getRelationshipsFrom(String sourceIndex, String arcrole) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String linkRole: getLinkRoles(arcrole)) {
            relationships.addAll(getRelationshipsFrom(sourceIndex,linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsTo(String, Set)
     */
    public List<Relationship> getRelationshipsTo(String targetIndex, Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            for (String linkRole: getLinkRoles(arcrole)) {
                relationships.addAll(getRelationshipsTo(targetIndex,linkRole, arcrole));
            }
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsTo(String, String, Set)
     */
    public List<Relationship> getRelationshipsTo(String targetIndex, String linkRole, Set<String> arcroles) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String arcrole: arcroles) {
            relationships.addAll(this.getRelationshipsTo(targetIndex, linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRelationshipsTo(String, String, String)
     */
    public SortedSet<Relationship> getRelationshipsTo(String targetIndex, String linkRole, String arcrole) throws XBRLException {
        String lr = "";
        String ar = "";
        if (linkRole != null) {
            lr = " and @linkRole='"+ linkRole +"'";            
        }
        if (arcrole != null) {
            ar = " and @arcRole='"+ arcrole +"'";            
        }
        String query = "#roots#[@targetIndex='"+ targetIndex +"'" + ar + lr + "]";
        List<Relationship> list = queryForRelationships(query);
        SortedSet<Relationship> sortedSet = new TreeSet<Relationship>(new RelationshipOrderComparator());
        sortedSet.addAll(list);
        return sortedSet;
    }

    /**
     * @see Analyser#getRelationshipsTo(String, String)
     */
    public List<Relationship> getRelationshipsTo(String targetIndex, String arcrole) throws XBRLException {
        List<Relationship> relationships = new Vector<Relationship>();
        for (String linkRole: getLinkRoles(arcrole)) {
            relationships.addAll(this.getRelationshipsTo(targetIndex,linkRole, arcrole));
        }
        return relationships;
    }

    /**
     * @see Analyser#getRootRelationships(String, String)
     */
    public List<Relationship> getRootRelationships(String linkRole, String arcrole) throws XBRLException {
        String lr = "";
        String ar = "";
        if (linkRole != null) {
            lr = " and @linkRole='"+ linkRole +"'";            
        }
        if (arcrole != null) {
            ar = " and @arcRole='"+ arcrole +"'";
        }
        Set<String> rootIndices = this.getRootIndices(linkRole,arcrole);
        List<Relationship> relationships = new Vector<Relationship>();
        for (String index: rootIndices) {
            String query = "#roots#[@sourceIndex='"+ index + "'" + ar + lr + "]";
            relationships.addAll(queryForRelationships(query));
        }
        return relationships;
    }
    
    /**
     * @see Analyser#getRoots(String, String)
     */
    public <F extends Fragment> Set<F> getRoots(String linkRole, String arcrole) throws XBRLException {
        Set<String> rootIndices = this.getRootIndices(linkRole,arcrole);
        Set<F> roots = new TreeSet<F>();
        for (String index: rootIndices) {
            roots.add(getStore().<F>getXMLResource(index));
        }
        return roots;
    }
    
    /**
     * @see Analyser#getRootIndices(String, String)
     */
    public Set<String> getRootIndices(String linkRole, String arcrole) throws XBRLException {
        String lr = "";
        String ar = "";
        if (linkRole != null) {
            lr = " and @linkRole='"+ linkRole +"'";            
        }
        if (arcrole != null) {
            ar = " and @arcRole='"+ arcrole +"'";
        }
        String sourcesQuery = "for $root in #roots#[@sourceIndex " + ar + lr + "] return string($root/@sourceIndex)";
        Set<String> sourceIndices = this.getStore().queryForStrings(sourcesQuery);
        String targetsQuery = "for $root in #roots#[@targetIndex " + ar + lr + "] return string($root/@targetIndex)";
        Set<String> targetIndices = this.getStore().queryForStrings(targetsQuery);
        sourceIndices.removeAll(targetIndices);
        return sourceIndices;
    }

    /**
     * @see Analyser#getLabelRelationships(String)
     */
    public List<Relationship> getLabelRelationships(String sourceIndex) throws XBRLException {
        String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }
    


    /**
     * @see Analyser#getLabelRelationshipsByLanguage(String, String)
     */
    public List<Relationship> getLabelRelationshipsByLanguage( 
            String sourceIndex, String language) 
            throws XBRLException {
        String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getLabelRelationshipsByLanguageAndRole(String, String, String)
     */
    public List<Relationship> getLabelRelationshipsByLanguageAndRole(
            String sourceIndex, String language, String role) throws XBRLException {
        String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"' and targetRole='"+ role +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getLabelRelationshipsByLanguages(String, List)
     */
    public List<Relationship> getLabelRelationshipsByLanguages(
            String sourceIndex, List<String> languages) throws XBRLException {
        for (String language: languages) {
            List<Relationship> relationships;
            if (language == null) { 
                relationships = this.getLabelRelationships(sourceIndex);
            } else {
                String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"']";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
            }
            if (! relationships.isEmpty()) return relationships;
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getLabelRelationshipsByRole(String, String)
     */
    public List<Relationship> getLabelRelationshipsByRole(
            String sourceIndex, String role) throws XBRLException {
        String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"' targetRole='"+ role +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getLabelRelationshipsByRoles(String, List, List)
     */
    public List<Relationship> getLabelRelationshipsByRoles(
            String sourceIndex, List<String> languages, List<String> roles)
            throws XBRLException {
        for (String language: languages) {
            for (String role: roles) {
                List<Relationship> relationships;
                String languagePhrase = "";
                if (language != null) languagePhrase = " and targetLanguage='"+ language +"'";
                String rolePhrase = "";
                if (role != null) rolePhrase = " and targetRole='"+ role +"'";
                String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"'" + languagePhrase + rolePhrase + "]";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
                if (! relationships.isEmpty()) return relationships;
            }
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getLabelRelationshipsByRoles(String, List)
     */
    public List<Relationship> getLabelRelationshipsByRoles(
            String sourceIndex, List<String> roles) throws XBRLException {
        for (String role: roles) {
            List<Relationship> relationships;
            if (role == null) { 
                relationships = this.getLabelRelationships(sourceIndex);
            } else {
                String query = "#roots#[@label and @sourceIndex='"+ sourceIndex+"' and targetRole='"+ role +"']";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
            }
            if (! relationships.isEmpty()) return relationships;
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getReferenceRelationships(String)
     */
    public List<Relationship> getReferenceRelationships(String sourceIndex) throws XBRLException {
        String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }
    


    /**
     * @see Analyser#getReferenceRelationshipsByLanguage(String, String)
     */
    public List<Relationship> getReferenceRelationshipsByLanguage( 
            String sourceIndex, String language) 
            throws XBRLException {
        String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getReferenceRelationshipsByLanguageAndRole(String, String, String)
     */
    public List<Relationship> getReferenceRelationshipsByLanguageAndRole(
            String sourceIndex, String language, String role) throws XBRLException {
        String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"' and targetRole='"+ role +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getReferenceRelationshipsByLanguages(String, List)
     */
    public List<Relationship> getReferenceRelationshipsByLanguages(
            String sourceIndex, List<String> languages) throws XBRLException {
        for (String language: languages) {
            List<Relationship> relationships;
            if (language == null) { 
                relationships = this.getReferenceRelationships(sourceIndex);
            } else {
                String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"' and targetLanguage='"+ language +"']";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
            }
            if (! relationships.isEmpty()) return relationships;
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getReferenceRelationshipsByRole(String, String)
     */
    public List<Relationship> getReferenceRelationshipsByRole(
            String sourceIndex, String role) throws XBRLException {
        String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"' targetRole='"+ role +"']";
        return this.getStore().<Relationship>queryForXMLResources(query);
    }

    /**
     * @see Analyser#getReferenceRelationshipsByRoles(String, List, List)
     */
    public List<Relationship> getReferenceRelationshipsByRoles(
            String sourceIndex, List<String> languages, List<String> roles)
            throws XBRLException {
        for (String language: languages) {
            for (String role: roles) {
                List<Relationship> relationships;
                String languagePhrase = "";
                if (language != null) languagePhrase = " and targetLanguage='"+ language +"'";
                String rolePhrase = "";
                if (role != null) rolePhrase = " and targetRole='"+ role +"'";
                String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"'" + languagePhrase + rolePhrase + "]";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
                if (! relationships.isEmpty()) return relationships;
            }
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getReferenceRelationshipsByRoles(String, List)
     */
    public List<Relationship> getReferenceRelationshipsByRoles(
            String sourceIndex, List<String> roles) throws XBRLException {
        for (String role: roles) {
            List<Relationship> relationships;
            if (role == null) { 
                relationships = this.getReferenceRelationships(sourceIndex);
            } else {
                String query = "#roots#[@reference and @sourceIndex='"+ sourceIndex+"' and targetRole='"+ role +"']";
                relationships = this.getStore().<Relationship>queryForXMLResources(query);
            }
            if (! relationships.isEmpty()) return relationships;
        }
        return new Vector<Relationship>();
    }

    /**
     * @see Analyser#getRelationships(String, String, String, String)
     */
    public List<Relationship> getRelationships(String sourceIndex, String targetIndex, String linkRole, String arcrole) throws XBRLException {
        String lr = "";
        String ar = "";
        if (linkRole != null) {
            lr = " and @linkRole='"+ linkRole +"'";            
        }
        if (arcrole != null) {
            ar = " and @arcRole='"+ arcrole +"'";            
        }
        String query = "#roots#[@sourceIndex='"+sourceIndex+"' and @targetIndex='"+targetIndex+"'" + ar + lr + "]";
        return getStore().<Relationship>queryForXMLResources(query);
    }
    
}
