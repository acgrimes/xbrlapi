package org.xbrlapi.aspects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xbrlapi.Fact;
import org.xbrlapi.utilities.XBRLException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * <h2>Fact Set Implementation</h2>
 * 
 * <p>
 * The implementation requires high performance for
 * the {@link FactSet#getAspectValues(Fact)} method
 * and the {@link FactSet#getFacts(AspectValue)} method.
 * It should be noted that a fact will map to multiple aspect values
 * and that an aspect value will, in general, map to multiple facts.
 * </p>
 * 
 * <p>
 * To support this performance requirement, a FactSet is implemented
 * using three one-to-many maps . These are based on synchronised versions of the 
 * Google Collections Multimap.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class FactSetImpl implements FactSet {

    /**
     * 
     */
    private static final long serialVersionUID = 4346339645057432855L;

    private static final Logger logger = Logger
    .getLogger(FactSetImpl.class);
    
    /**
     * A map from one aspect value to multiple facts.
     */
    Multimap<AspectValue, Fact> valueMap;

    /**
     * A map from one fact to multiple aspect values.
     */
    Multimap<Fact, AspectValue> factMap;
    
    /**
     * A map from aspect IDs to the values for that aspect.
     */
    Multimap<String, AspectValue> aspectMap;
        
    /** 
     * The aspect model that is being used.
     */
    AspectModel model;
    
    /**
     * @see FactSet#getModel()
     */
    public AspectModel getModel() {
        return model;
    }

    /**
     * @param model The model to set.
     * @throws XBRLException if the model is null.
     */
    private void setModel(AspectModel model) throws XBRLException {
        if (model == null) throw new XBRLException("The model must not be null.");
        this.model = model;
    }

    /**
     * @param model The aspect model determining the aspects to work with.
     * @throws XBRLException
     */
    public FactSetImpl(AspectModel model) throws XBRLException {
        this.setModel(model);
        
        valueMap =  Multimaps.synchronizedSetMultimap(HashMultimap.<AspectValue, Fact>create()); 
        
        factMap = Multimaps.synchronizedSetMultimap(HashMultimap.<Fact, AspectValue>create()); 

        aspectMap = Multimaps.synchronizedSetMultimap(HashMultimap.<String, AspectValue>create()); 

    }
    
    /**
     * @see FactSet#getAspectModel()
     */
    public AspectModel getAspectModel() {
        return this.model;
    }
    
    /**
     * @see FactSet#addFacts(Collection)
     */
    public <F extends Fact> void addFacts(Collection<F> facts) throws XBRLException {
        for (F fact: facts) {
            this.addFact(fact);
        }
    }

    /**
     * @see FactSet#addFact(Fact)
     */
    public void addFact(Fact fact) throws XBRLException {

        Map<String,AspectValue> values = model.getAspectValues(fact);
        for (String id: values.keySet()) {
            AspectValue value = values.get(id);
            if (! value.isMissing()) {
                if (! valueMap.containsEntry(value,fact)) valueMap.put(value,fact);
                if (! factMap.containsEntry(fact,value)) factMap.put(fact,value);
                if (! aspectMap.containsEntry(id,value)) aspectMap.put(id,value);
            }
        }
    }
    

    
    /**
     * @see FactSet#getAspectValues()
     */
    public Set<AspectValue> getAspectValues() {
        return valueMap.keySet();
    }

    /**
     * @see FactSet#getAspectValues(Fact)
     */
    public Collection<AspectValue> getAspectValues(Fact fact) throws XBRLException {
        Collection<AspectValue> values = factMap.get(fact);
        Set<String> aspectsWithValues = new HashSet<String>();
        for (AspectValue value: values) {
            aspectsWithValues.add(value.getAspectId());
        }
        for (Aspect aspect: model.getAspects()) {
            if (! aspectsWithValues.contains(aspect.getId()))
                values.add(aspect.getMissingValue());
        }
        return values;
    }
    
    /**
     * @see FactSet#getAspectValue(String, Fact)
     */
    public AspectValue getAspectValue(String aspectId, Fact fact) throws XBRLException {
        for (AspectValue value: factMap.get(fact)) {
            if (value.getAspectId().equals(aspectId)) return value;
        }
        return this.getAspectModel().getAspect(aspectId).getMissingValue();
            
    }    

    /**
     * @see FactSet#getAspectValues(String)
     */
    public Collection<AspectValue> getAspectValues(String aspectId) {
        Collection<AspectValue> values = aspectMap.get(aspectId);
        try {
            values.add(model.getAspect(aspectId).getMissingValue());
        } catch (XBRLException e) {
            ; // Generally unreachable.
        }
        return values;
    }
    
    /**
     * @see FactSet#getAspectValueCount(String)
     */
    public int getAspectValueCount(String aspectId) {
        return aspectMap.get(aspectId).size();
    }

    /**
     * @see FactSet#getFacts()
     */
    public Set<Fact> getFacts() {
        return factMap.keySet();
    }

    /**
     * @see FactSet#getFacts(AspectValue)
     */
    public Set<Fact> getFacts(AspectValue value) {
        return new HashSet<Fact>(valueMap.get(value));
    }

    /**
     * @see FactSet#getFacts(Collection)
     */
    public Set<Fact> getFacts(Collection<AspectValue> values) {
        Set<Fact> result = new HashSet<Fact>();
        boolean doneFirstValue = false;
        for (AspectValue value: values) {
            Collection<Fact> facts = valueMap.get(value);
            if (! doneFirstValue) {
                result.addAll(facts);
            } else {
                result.retainAll(facts);
            }
            doneFirstValue = true;
        }
        return result;
    }    
    
    /**
     * @see FactSet#hasAspectValue(AspectValue)
     */
    public boolean hasAspectValue(AspectValue value) {
        return valueMap.containsKey(value);
    }

    /**
     * @see FactSet#hasFact(Fact)
     */
    public boolean hasFact(Fact fact) {
        return factMap.containsKey(fact);
    }

    /**
     * @see FactSet#getSize()
     */
    public long getSize() {
        return this.factMap.keySet().size();
    }

    /**
     * @see FactSet#isPopulated(String)
     */
    public boolean isPopulated(String aspectId) {
        return (!aspectMap.get(aspectId).isEmpty());
    }
    
    /**
     * @see FactSet#isSingular(String)
     */
    public boolean isSingular(String aspectId) {
        return (aspectMap.get(aspectId).size() == 1);
    }    

    /**
     * @see FactSet#getRootFacts(String)
     */
    public List<Fact> getRootFacts(String aspectId) throws XBRLException {
        List<Fact> rootFacts = new Vector<Fact>();
        for (AspectValue value: this.getAspectValues(aspectId)) {
            Domain domain = model.getAspect(aspectId).getDomain();
            if (domain.isRoot(value)) {
                rootFacts.addAll(this.getFacts(value));
            } 
        }
        return rootFacts;
    }

    /**
     * @see FactSet#getPopulatedAspects()
     */
    public Collection<Aspect> getPopulatedAspects() throws XBRLException {
        Collection<Aspect> result = new Vector<Aspect>();
        for (Aspect aspect: getModel().getAspects())
            if (this.isPopulated(aspect.getId())) result.add(aspect);
        return result;
    }

    /**
     * Local hash map for caching aspect value labels.
     */
    private HashMap<String,String> localCache = new HashMap<String,String>();
    
    /**
     * @see FactSet#emptyLocalLabelCache()
     */
    public void emptyLocalLabelCache() {
        localCache = new HashMap<String,String>();
    }
    
    /**
     * @see FactSet#getAspectValueLabelGivenLists(AspectValue, List, List, List)
     */
    public String getAspectValueLabelGivenLists(AspectValue value, List<String> locales,
            List<String> resourceRoles, List<String> linkRoles) throws XBRLException {
        String key = value.getAspectId() + value.getId();
        if (locales == null) key += "null";
        else for (String locale: locales) key+= locale;
        if (resourceRoles == null) key += "null";
        else for (String role: resourceRoles) key+= role;
        if (linkRoles == null) key += "null";
        else for (String role: linkRoles) key+= role;
        if (localCache.containsKey(key)) return localCache.get(key);
        String label = this.getAspectModel().getAspectValueLabelGivenLists(value, locales, resourceRoles, linkRoles);
        localCache.put(key,label);
        return label;
    }

    /**
     * @see FactSet#getAspectValueLabel(AspectValue, String, String, String)
     */
    public String getAspectValueLabel(AspectValue value, String locale,
            String resourceRole, String linkRole) throws XBRLException {
        String key = value.getAspectId() + value.getId();
        key+= locale;
        key+= resourceRole;
        key+= linkRole;
        if (localCache.containsKey(key)) return localCache.get(key);
        String label = this.getAspectModel().getAspectValueLabel(value, locale, resourceRole, linkRole);
        localCache.put(key,label);
        return label;
    }

}
