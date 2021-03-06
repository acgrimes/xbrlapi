package org.xbrlapi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Instance extends Fragment {

    /**
     * Get the list of schemaRef fragments in the instance
     * Returns null if none are contained by the XBRL instance.
     * @throws XBRLException
     */
    public List<SimpleLink> getSchemaRefs() throws XBRLException;    

    /**
     * Get the list of linkbaseRef fragments in the instance
     * Returns null if none are contained by the XBRL instance.
     * @throws XBRLException
     */
    public List<SimpleLink> getLinkbaseRefs() throws XBRLException;    

    /**
     * Get list contexts contained in the instance.
     * Returns null if none are contained by the XBRL instance.
     * @throws XBRLException
     */
    public List<Context> getContexts() throws XBRLException;    

    /**
     * Get a specified context from the instance based on its id.
     * @param id The id of the context fragment
     * @return the context fragment
     * @throws XBRLException if the context does not exist
     */
    public Context getContext(String id) throws XBRLException;

    /**
     * Get the list of units contained in the instance.
     * Returns null if none are contained by the XBRL instance.
     *
     * @throws XBRLException
     */
    public List<Unit> getUnits() throws XBRLException;    

    /**
     * Get a specified unit from the instance based on its id.
     * @param id The id of the unit fragment
     * @return the unit fragment
     * @throws XBRLException if the unit is not in this instance.
     */
    public Unit getUnit(String id) throws XBRLException;

    /**
     * Get a list of footnote link fragments
     * Returns null if none are contained by the XBRL instance.
     * @throws XBRLException
     */
    public List<ExtendedLink> getFootnoteLinks() throws XBRLException;    

    /**
     * @return a list containing all of the footnote resources in the instance.
     * @throws XBRLException
     */
    public List<FootnoteResource> getFootnotes() throws XBRLException;
    /**
     * Get the list of facts that are children of the instance.
     * Facts that are within tuples are not included in this list.
     * @return the list of facts that are children of the instance.
     * @throws XBRLException
     */
    public List<Fact> getChildFacts() throws XBRLException;
    
    /**
     * @return all facts (tuples and facts within tuples also) in this instance.
     * @throws XBRLException
     */
    public List<Fact> getAllFacts() throws XBRLException;
    
    /**
     * @return the list of concepts that have facts that are children of 
     * this XBRL instance (rather than being children of tuples).
     * @throws XBRLException
     */
    public List<Concept> getChildConcepts() throws XBRLException;
    
    /**
     * @return the list of all concepts that have facts in this 
     * XBRL instance, including nested within tuples.
     * @throws XBRLException
     */
    public List<Concept> getAllConcepts() throws XBRLException;
    
    /**
     * @return the number of concepts that have facts that are children of
     * this XBRL instance (rather than being nested within tuples).
     * @throws XBRLException
     */
    public long getChildConceptsCount() throws XBRLException;
    
    /**
     * @return the number of concepts that have facts in this instance.
     * @throws XBRLException
     */
    public long getAllConceptsCount() throws XBRLException;    
    
    /**
     * @return the number of child facts in the instance (excludes 
     * facts that are contained within tuples).
     * @throws XBRLException
     */
    public long getChildFactsCount() throws XBRLException;
    
    /**
     * @return the number of facts in the instance including tuples and
     * those facts within tuples.
     * @throws XBRLException
     */
    public long getAllFactsCount() throws XBRLException;
    
    /**
     * @return the value of the earliest start 
     * date or instance in a context period in the XBRL instance.
     * Returns null if the XBRL instance does not contain a context that does not
     * have a value of forever.
     * @throws XBRLException
     */
    public String getEarliestPeriod() throws XBRLException;
    
    /**
     * @return the value of the latest start 
     * date or instance in a context period in the XBRL instance.
     * Returns null if the XBRL instance does not contain a context that does not
     * have a value of forever.
     * @throws XBRLException
     */
    public String getLatestPeriod() throws XBRLException;
    
    /**
     * @return the list of entity resources for entities with facts in the instance.
     * @throws XBRLException
     */
    public List<EntityResource> getEntityResources() throws XBRLException; 
    
    /**
     * @return A map, indexed by entity identifier schemes, of sets of the entity identifiers
     * for those schemes, as contained in this XBRL instance.
     * @throws XBRLException
     */
    public Map<String, Set<String>> getEntityIdentifiers() throws XBRLException;
    
    /**
     * @return the list of tuples that are children of the instance.
     * Tuples that are within tuples are not included in this list.
     * The list is empty if the instance does not contain tuples.
     * @throws XBRLException
     */
    public List<Tuple> getTuples() throws XBRLException;    
    
    /**
     * @return the list of all items that are children of the instance.
     * Tuples (and the items that they contain are not included in the list).
     * @throws XBRLException
     */
    public List<Item> getChildItems() throws XBRLException;
    
    /**
     * @return the list of all items in the instance including those in
     * tuples.
     * @throws XBRLException
     */
    public List<Item> getAllItems() throws XBRLException;    

    /**
     * @param namespace The namespace of the facts to select.
     * @param localname The local name of the facts to select.
     * @return the list of facts in the instance with the given 
     * namespace and local name.
     * @throws XBRLException if either parameter is null.
     */
    public List<Fact> getFacts(String namespace, String localname) throws XBRLException;

    /**
     * @param concept the concept to get the facts for.
     * @return the list of facts in this instance that are
     * reporting values for the specified concept.
     * @throws XBRLException if the parameter is null.
     */
    public List<Fact> getFacts(Concept concept) throws XBRLException;
    
}
