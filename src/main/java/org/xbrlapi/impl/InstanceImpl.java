package org.xbrlapi.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.xbrlapi.Concept;
import org.xbrlapi.Context;
import org.xbrlapi.EntityResource;
import org.xbrlapi.ExtendedLink;
import org.xbrlapi.Fact;
import org.xbrlapi.FootnoteResource;
import org.xbrlapi.Instance;
import org.xbrlapi.Item;
import org.xbrlapi.Resource;
import org.xbrlapi.SimpleLink;
import org.xbrlapi.Tuple;
import org.xbrlapi.Unit;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class InstanceImpl extends FragmentImpl implements Instance {

    /**
     * 
     */
    private static final long serialVersionUID = -3931256042051791132L;

    /**
     * @see Instance#getSchemaRefs()
     */
    public List<SimpleLink> getSchemaRefs() throws XBRLException {
    	List<SimpleLink> candidates = this.<SimpleLink>getChildren("org.xbrlapi.impl.SimpleLinkImpl");
    	int i = 0;
    	while (i<candidates.size()) {
    		SimpleLink c = candidates.get(i);
    		if (! c.getLocalname().equals("schemaRef")) candidates.remove(c); else  i++;
    	}
    	return candidates;
    }
    
    /**
     * @see Instance#getLinkbaseRefs()
     */
    public List<SimpleLink> getLinkbaseRefs() throws XBRLException {
    	List<SimpleLink> candidates = this.<SimpleLink>getChildren("org.xbrlapi.impl.SimpleLinkImpl");
    	int i = 0;
    	while (i<candidates.size()) {
    		SimpleLink c = candidates.get(i);
    		if (! c.getLocalname().equals("linkbaseRef")) candidates.remove(c); else  i++;
    	}
    	return candidates;
    }

    /**
     * @see Instance#getContexts()
     */
    public List<Context> getContexts() throws XBRLException {
    	return this.<Context>getChildren(ContextImpl.class.getName());
    }

    /**
     * @see Instance#getContext(String)
     */
    public Context getContext(String id) throws XBRLException {
    	String xpath = "#roots#[@type='org.xbrlapi.impl.ContextImpl' and @parentIndex='" + getIndex() + "' and */*/@id='" + id + "']";
    	List<Context> list = getStore().<Context>queryForXMLResources(xpath);
    	if (list.size() == 0) throw new XBRLException("The instance does not contain a context with id: " + id);
    	if (list.size() > 1) throw new XBRLException("The instance contains more than one context with id: " + id);
    	return (list.get(0));
    }
    
    /**
     * @see Instance#getUnits()
     */
    public List<Unit> getUnits() throws XBRLException {
    	return this.<Unit>getChildren("org.xbrlapi.impl.UnitImpl");
    }    

    /**
     * @see Instance#getUnit(String)
     */
    public Unit getUnit(String id) throws XBRLException {
    	List<Unit> list = getStore().queryForXMLResources("#roots#[@type='org.xbrlapi.impl.UnitImpl' and @parentIndex='" + this.getIndex() + "' and "+ Constants.XBRLAPIPrefix+ ":" + "data/*/@id='" + id + "']");
    	if (list.size() == 0) throw new XBRLException("The instance does not contain a unit with id: " + id);
    	if (list.size() > 1) throw new XBRLException("The instance contains more than one unit with id: " + id);
    	return list.get(0);
    }
    
    /**
     * @see Instance#getFootnoteLinks()
     */
    public List<ExtendedLink> getFootnoteLinks() throws XBRLException {
    	return this.<ExtendedLink>getChildren("org.xbrlapi.impl.ExtendedLinkImpl");
    }
    
    /**
     * @see Instance#getFootnotes()
     */
    public List<FootnoteResource> getFootnotes() throws XBRLException {
        List<FootnoteResource> result = new Vector<FootnoteResource>();
        for (ExtendedLink footnoteLink: this.getFootnoteLinks()) {
            List<Resource> resources = footnoteLink.getResources();
            try {
                for (Resource resource: resources) result.add((FootnoteResource) resource);
            } catch (ClassCastException e) {
                ; // Ignore resources that are not footnotes.
            }
        }
        return result;
    }    

    /**
     * @return the XQuery used to get all the child facts of the XBRL instance.
     */
    private String getChildFactsQuery() {
        return "#roots#[@parentIndex='" + this.getIndex() + "' and @fact]"; 
    }
    
    /**
     * Assumes that each URI corresponds to a separate XBRL instance.
     * @return the XQuery used to get all the child facts of the XBRL instance.
     */
    private String getAllFactsQuery() throws XBRLException {
        return "#roots#[@fact and @uri='" + this.getURI() + "']";
    }    
    
    /**
     * @see Instance#getChildFacts()
     */
    public List<Fact> getChildFacts() throws XBRLException {
    	return getStore().<Fact>queryForXMLResources(getChildFactsQuery());
    }
    
    /**
     * @see Instance#getAllFacts()
     */
    public List<Fact> getAllFacts() throws XBRLException {
        return getStore().<Fact>queryForXMLResources(getAllFactsQuery());
    }    
    
    /**
     * @see Instance#getChildItems()
     */
    public List<Item> getChildItems() throws XBRLException {
        return getStore().<Item>queryForXMLResources("#roots#[@parentIndex='" + this.getIndex() + "' and (@type='"+SimpleNumericItemImpl.class.getName() +"' or @type='"+FractionItemImpl.class.getName()+"' or @type='"+NonNumericItemImpl.class.getName()+"')]");
    }

    /**
     * @see Instance#getTuples()
     */
    public List<Tuple> getTuples() throws XBRLException {
        return this.<Tuple>getChildren("Tuple");
    }

    /**
     * @see Instance#getEarliestPeriod()
     */
    public String getEarliestPeriod() throws XBRLException {

        String query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + PeriodImpl.class.getName() + "']/*/*/xbrli:instant return string($root)";
        Set<String> values = getStore().queryForStrings(query);
        query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + PeriodImpl.class.getName() + "']/*/*/xbrli:startDate return string($root)";
        values.addAll(getStore().queryForStrings(query));
        String result = null;
        for (String candidate: values) {
            if (result == null) result = candidate;
            else if (result.compareTo(candidate) > 0) result = candidate;
        }
        return result;        
    }

    /**
     * @see Instance#getChildFactsCount()
     */
    public long getChildFactsCount() throws XBRLException {
        return getStore().queryCount(getChildFactsQuery());
    }
    
    /**
     * @see Instance#getAllFactsCount()
     */
    public long getAllFactsCount() throws XBRLException {
        return getStore().queryCount(getAllFactsQuery());
    }    

    /**
     * @see Instance#getLatestPeriod()
     */
    public String getLatestPeriod() throws XBRLException {
        
        String query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + PeriodImpl.class.getName() + "']/*/*/xbrli:instant return string($root)";
        Set<String> values = getStore().queryForStrings(query);
        query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + PeriodImpl.class.getName() + "']/*/*/xbrli:endDate return string($root)";
        values.addAll(getStore().queryForStrings(query));
        String result = null;
        for (String candidate: values) {
            if (result == null) result = candidate;
            else if (result.compareTo(candidate) < 0) result = candidate;
        }
        return result;
        
    }

    /**
     * @see Instance#getEntityResources()
     */
    public List<EntityResource> getEntityResources() throws XBRLException {
        String query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + EntityImpl.class.getName() + "'] let $identifier := $root/xbrlapi:data/xbrli:entity/xbrli:identifier return concat($identifier/@scheme,'|||',$identifier)";
        Set<String> uniqueValues = new HashSet<String>();
        Set<String> values = getStore().queryForStrings(query);
        for (String value: values) {
            String[] qname = value.split("\\|\\|\\|");
            String scheme = qname[0].trim();
            String identifier = qname[1].trim();
            uniqueValues.add(scheme + "|||" + identifier);            
        }
        List<EntityResource> result = new Vector<EntityResource>();
        for (String value: uniqueValues) {
            String[] qname = value.split("\\|\\|\\|");
            String scheme = qname[0];
            String identifier = qname[1];
            query = "#roots#[@type='"+EntityResourceImpl.class.getName()+"' and */*/@scheme='"+ scheme +"' and */*/@value='" + identifier + "']";
            result.addAll(getStore().<EntityResource>queryForXMLResources(query));
        }
        return result;
    }
    
    /**
     * @see Instance#getEntityIdentifiers()
     */
    public Map<String, Set<String>> getEntityIdentifiers() throws XBRLException {
        String query = "for $root in #roots#[@uri='" + this.getURI() + "' and @type='" + EntityImpl.class.getName() + "'] let $identifier := $root/xbrlapi:data/xbrli:entity/xbrli:identifier return concat($identifier/@scheme,'|||',$identifier)";
        Set<String> uniqueValues = new HashSet<String>();
        Set<String> values = getStore().queryForStrings(query);
        for (String value: values) {
            String[] qname = value.split("\\|\\|\\|");
            String scheme = qname[0].trim();
            String identifier = qname[1].trim();
            uniqueValues.add(scheme + "|||" + identifier);            
        }
        Map<String,Set<String>> result = new HashMap<String,Set<String>>();
        for (String value: uniqueValues) {
            String[] qname = value.split("\\|\\|\\|");
            String scheme = qname[0];
            String identifier = qname[1];
            if (result.containsKey(scheme))
                result.get(scheme).add(identifier);
            else {
                Set<String> identifiers = new HashSet<String>();
                identifiers.add(identifier);
                result.put(scheme,identifiers);
            }
        }
        return result;

    }

    /**
     * @see Instance#getChildConcepts()
     */
    public List<Concept> getChildConcepts() throws XBRLException {
        String query = "for $root in #roots#[@fact and @parentIndex='"+getIndex()+"'] let $data:=$root/xbrlapi:data/* return concat(namespace-uri($data),'|||',local-name($data))";
        Set<String> qnames = getStore().queryForStrings(query);
        List<Concept> concepts = new Vector<Concept>();
        for (String value: qnames) {
            String[] qname = value.split("\\|\\|\\|");
            String namespace = qname[0];
            String localname = qname[1];
            concepts.add(getStore().getConcept(namespace,localname));
        }
        
        return concepts;
    }
    
    public List<Concept> getAllConcepts() throws XBRLException {
        String query = "for $root in #roots#[@fact and @uri='"+getURI()+"'] let $data:=$root/xbrlapi:data/* return concat(namespace-uri($data),'|||',local-name($data))";
        Set<String> qnames = getStore().queryForStrings(query);
        List<Concept> concepts = new Vector<Concept>();
        for (String value: qnames) {
            String[] qname = value.split("\\|\\|\\|");
            String namespace = qname[0];
            String localname = qname[1];
            concepts.add(getStore().getConcept(namespace,localname));
        }
        
        return concepts;
    }    

    /**
     * @see Instance#getChildConceptsCount()
     */
    public long getChildConceptsCount() throws XBRLException {
        String query = "for $root in #roots#[@parentIndex='"+getIndex()+"'] let $data:=$root/xbrlapi:data/* where namespace-uri($data)!='"+Constants.XBRL21Namespace+"' and namespace-uri($data)!='"+Constants.XBRL21LinkNamespace+"' return concat(namespace-uri($data),local-name($data))";
        Set<String> result = getStore().queryForStrings(query);
        return result.size();
    }
    
    /**
     * @see Instance#getAllConceptsCount()
     */
    public long getAllConceptsCount() throws XBRLException {
        String query = "for $root in #roots#[@fact and @uri='" + this.getURI() + "'] let $data:=$root/xbrlapi:data/* return concat(namespace-uri($data),'#',local-name($data))";
        Set<String> result = getStore().queryForStrings(query);
        return result.size();    
    }

    /**
     * @see Instance#getAllItems()
     */
    public List<Item> getAllItems() throws XBRLException {
        List<Fact> facts = this.getAllFacts();
        List<Item> result = new Vector<Item>();
        for (Fact fact: facts) {
            if (! fact.isTuple()) result.add((Item) fact);
        }
        return result;
    }

    /**
     * @see Instance#getFacts(String, String)
     */
    public List<Fact> getFacts(String namespace, String localname)
            throws XBRLException {
        if (namespace == null) throw new XBRLException("The namespace must not be null.");
        if (localname == null) throw new XBRLException("The local name must not be null.");
        getStore().setNamespaceBinding(namespace,"xbrlapi_factNamespacePrefix");
        String query = "for $root in #roots#[@uri='"+this.getURI()+"' and @fact and xbrlapi:data/xbrlapi_factNamespacePrefix:" + localname + "] return $root";
		return getStore().queryForXMLResources(query); 
	}
    
    /**
     * @see Instance#getFacts(Concept)
     */
    public List<Fact> getFacts(Concept concept)
            throws XBRLException {
        if (concept == null) throw new XBRLException("The concept must not be null.");
        return this.getFacts(concept.getTargetNamespace(), concept.getName());
    }    
}
