package org.xbrlapi.aspects;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.xbrlapi.Fact;
import org.xbrlapi.utilities.XBRLException;

/**
 * <h2>Fact Set</h2>
 * 
 * <p>
 * A FactSet is a set of facts and a set of aspect values and a two-way mapping between the facts and the aspect values
 * </p>
 * 
 * <p>
 * The FactSet guarantees to have all aspect values for all of the facts that it contains.
 * It also guarantees not to have any aspect values that are not values for facts in the fact set.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public interface FactSet extends Serializable {

    /**
     * @return the aspect model underpinning the fact set.
     */
    public AspectModel getModel();
    
    /**
     * Adds the fact to the fact set, computing the aspect values
     * for the fact for each aspect in the fact set's aspect model.
     * @param fact The fact to add.
     * @throws XBRLException
     */
    public void addFact(Fact fact) throws XBRLException;


    /**
     * Adds the facts to the fact set, computing the aspect values
     * for the facts for each aspect in the fact set's aspect model.
     * @param facts The facts to add.
     * @throws XBRLException
     */
    public <F extends Fact> void addFacts(Collection<F> facts) throws XBRLException;
    
    /**
     * @param fact The fact to test for.
     * @return true if the fact is in the set and false otherwise.
     */
    public boolean hasFact(Fact fact);

    /**
     * @param value The aspect value to test for.
     * @return true if the aspect value is in the set and false otherwise.
     */
    public boolean hasAspectValue(AspectValue value);

    /**
     * @return the set of all facts.
     */
    public Set<Fact> getFacts();

    /**
     * @return the set of all aspect values.
     */
    public Set<AspectValue> getAspectValues();
    
    /**
     * @param aspectId The ID of the aspect.
     * @return the set of all values for the aspect, also always including the missing value.
     */
    public Collection<AspectValue> getAspectValues(String aspectId);
    
    /**
     * @param aspectId The ID of the aspect.
     * @return the number of non-missing aspect values for the given aspect.
     */
    public int getAspectValueCount(String aspectId);    

    /**
     * @param aspectId
     *            The ID of the aspect.
     * @return true if the fact set has non-missing values for the specified
     *         aspect.
     */
    public boolean isPopulated(String aspectId);
    
    /**
     * @param aspectId
     *            The ID of the aspect.
     * @return true if the fact set has only one non-missing values for the specified
     *         aspect.
     */
    public boolean isSingular(String aspectId);    
    
    /**
     * @param fact The fact.
     * @return the set of aspect values for the given fact, including missing
     * values for those aspects for which the fact does not have a value.
     */
    public Collection<AspectValue> getAspectValues(Fact fact) throws XBRLException;
    
    /**
     * @param aspectId the ID of the aspect to get the aspect value for.
     * @param fact The fact.
     * @return the aspect value for the given fact and aspect.
     * @throws XBRLException
     */
    public AspectValue getAspectValue(String aspectId, Fact fact) throws XBRLException;

    /**
     * @param value The aspect value.
     * @return the set of facts with the given aspect value.
     */
    public Set<Fact> getFacts(AspectValue value);
 
    /**
     * @param values The collection of aspect values that the 
     * returned facts must have.
     * @return the set of facts that have all of 
     * the given aspect values.
     */
    public Set<Fact> getFacts(Collection<AspectValue> values);    

    /**
     * @return the number of facts in the fact set.
     */
    public long getSize();
    
    /**
     * @return the aspect model underpinning this fact set.
     */
    public AspectModel getAspectModel();

    /**
     * @param aspectId
     *            The ID of the aspect of interest.
     * @return a list of the facts in the fact set that have values for the
     *         specified aspect that are roots of the heirarchy of aspect values
     *         defined by the domain of the specified aspect.
     * @throws XBRLException
     */
    public List<Fact> getRootFacts(String aspectId) throws XBRLException;

    /**
     * @return a collection of the populated aspects in the underlying aspect model.
     * This leaves out all aspects that only have the missing value as their aspect value,
     * for all facts in the fact set.
     * @throws XBRLException
     */
    public Collection<Aspect> getPopulatedAspects() throws XBRLException;
    
    /**
     * This is a convenience method to give access to the labels generated by
     * the labellers for the values of each aspect in the fact set's aspect model.
     * Labels are cached, based on the full set of information about the label selection criteria.
     * The label cache, can be emptied.
     * @param value The aspect value
     * @param locale The label locale
     * @param resourceRole The label XLink resource role
     * @param linkRole The label link role
     * @return the label for the given aspect value.
     * @throws XBRLException if the aspect is not in the aspect model.
     */
    public String getAspectValueLabel(AspectValue value, String locale, String resourceRole, String linkRole) throws XBRLException;
    
    /**
     * This method simply empties the label cache that is a local property of the fact set.
     */
    public void emptyLocalLabelCache();

    /**
     * This is a convenience method to give access to the labels generated by
     * the labellers for the values of each aspect in the fact set's aspect model.
     * 
     * @param value
     *            The aspect value
     * @param locales
     *            The list of label locales from first, most preferred to last,
     *            least preferred. The list can include nulls or it can be null.
     * @param resourceRoles
     *            The list of label XLink resource roles from first, most
     *            preferred to last, least preferred. The list can include nulls
     *            or it can be null.
     * @param linkRoles
     *            The label link role from first, most preferred to last, least
     *            preferred. The list can include nulls or it can be null.
     * @return the label for the given aspect value.
     * @throws XBRLException
     *             if the aspect is not in the aspect model.
     */
    public String getAspectValueLabelGivenLists(AspectValue value, List<String> locales, List<String> resourceRoles, List<String> linkRoles) throws XBRLException;    
    
}
