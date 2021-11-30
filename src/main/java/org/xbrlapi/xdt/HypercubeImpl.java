package org.xbrlapi.xdt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.xbrlapi.Arc;
import org.xbrlapi.Concept;
import org.xbrlapi.Relationship;
import org.xbrlapi.impl.RelationshipOrderComparator;
import org.xbrlapi.utilities.XBRLException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * This class also provides a range of static methods to explore the
 * hypercubes that are "had" by a given concept.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class HypercubeImpl extends XDTConceptImpl implements Hypercube, HCI {

    /**
     * 
     */
    private static final long serialVersionUID = -3395259164875316193L;

    /**
     * @see Hypercube#getDimensions()
     */
    public List<Dimension> getDimensions() throws XBRLException {
        List<Dimension> result = new Vector<Dimension>();
        SortedSet<Relationship> relationships = getStore().getRelationshipsFrom(getIndex(), null, XDTConstants.HypercubeDimensionArcrole);
        for (Relationship relationship: relationships) {
            result.add((Dimension) relationship.getTarget());
        }
        return result;
    }
    
    /**
     * @see Hypercube#getDimensions(String)
     */
    public List<Dimension> getDimensions(String linkRole) throws XBRLException {
        List<Dimension> result = new Vector<Dimension>();
        SortedSet<Relationship> relationships = getRelationshipsToDimensions(linkRole);
        for (Relationship relationship: relationships) {
            result.add((Dimension) relationship.getTarget());
        }
        return result;
    }
    
    /**
     * @see Hypercube#getRelationshipsToDimensions(String)
     */
    public SortedSet<Relationship> getRelationshipsToDimensions(String linkRole) throws XBRLException {
        return getStore().getRelationshipsFrom(getIndex(),linkRole, XDTConstants.HypercubeDimensionArcrole);
    }
    
    /**
     * @param concept The concept of interest
     * @return the hypercubes inherited by this concept, grouped based on the link role
     * of the relationships to the hypercube from the concept.  This includes the hypercubes
     * directly related to the concept of interest.
     * @throws XBRLException
     */
    public static ListMultimap<String, Hypercube> getInheritedHypercubes(Concept concept) throws XBRLException {
        ListMultimap<String, Hypercube> result = ArrayListMultimap.<String, Hypercube> create();
        Set<Concept> concepts = getHypercubeInheritanceConcepts(concept);
        for (Concept c: concepts) {
            result.putAll(getOwnHypercubes(c));
        }
        return result;
    }

    /**
     * @param concept The concept of interest
     * @return the hypercubes directly related to this concept by has-hypercube relationships.
     * @throws XBRLException
     */
    public static ListMultimap<String, Hypercube> getOwnHypercubes(Concept concept) throws XBRLException {
        ListMultimap<String, Hypercube> result = ArrayListMultimap.<String, Hypercube> create();
        for(Relationship r: concept.getStore().getRelationshipsFrom(concept.getIndex(),null,XDTConstants.AllArcrole)) {
            result.put(r.getLinkRole(),(Hypercube) r.getTarget());
        }
        for(Relationship r: concept.getStore().getRelationshipsFrom(concept.getIndex(),null,XDTConstants.NotAllArcrole)) {
            result.put(r.getLinkRole(),(Hypercube) r.getTarget());
        }
        return result;
    }

    /**
     * @param concept The concept of interest
     * @return the has-hypercube relationships inherited by this concept, grouped based on the link role
     * of the relationships.  This includes the relationships from the concept itself.
     * @throws XBRLException
     */
    public static ListMultimap<String, Relationship> getInheritedHasHypercubeRelationships(Concept concept) throws XBRLException {
        ListMultimap<String, Relationship> result = ArrayListMultimap.<String, Relationship> create();
        Set<Concept> concepts = getHypercubeInheritanceConcepts(concept);
        for (Concept c: concepts) {
            result.putAll(getOwnHasHypercubeRelationships(c));
        }
        return result;
    }
    
    /**
     * @param concept The concept of interest
     * @return the active has-hypercube relationships directly from this concept.
     * @throws XBRLException
     */
    public static ListMultimap<String, Relationship> getOwnHasHypercubeRelationships(Concept concept) throws XBRLException {
        ListMultimap<String, Relationship> result = ArrayListMultimap.<String, Relationship> create();
        for(Relationship r: concept.getStore().getRelationshipsFrom(concept.getIndex(),null,XDTConstants.AllArcrole)) {
            result.put(r.getLinkRole(), r);
        }
        for(Relationship r: concept.getStore().getRelationshipsFrom(concept.getIndex(),null,XDTConstants.NotAllArcrole)) {
            result.put(r.getLinkRole(), r);
        }
        return result;
    }    
    
    /**
     * @param concept The concept.
     * @return the concepts that are parents of the given concept, via, domain-member relationships
     * in any linkrole.
     * @throws XBRLException
     */
    public static SortedSet<Relationship> getDomainMemberRelationshipsTo(Concept concept) throws XBRLException {
        SortedSet<Relationship> result = concept.getStore().getRelationshipsTo(concept.getIndex(),null,XDTConstants.DomainMemberArcrole);
        return result;
    }
    
    /**
     * @param relationship The consecutive domain-member relationship
     * @return the set of domain-member relationships that have this relationship as a consecutive domain member relationship.
     * This is useful for tracing hypercube inheritance.
     * @throws XBRLException
     */
    public static SortedSet<Relationship> getPreviousDomainMemberRelationships(Relationship relationship) throws XBRLException {
        
        SortedSet<Relationship> candidates = relationship.getStore().getRelationshipsTo(relationship.getSourceIndex(),null,XDTConstants.DomainMemberArcrole);
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        for (Relationship candidate: candidates) {
            Arc arc = candidate.getArc();
            if (arc.hasAttribute(XDTConstants.XBRLDTNamespace,"targetRole")) {
                if (arc.getAttribute(XDTConstants.XBRLDTNamespace,"targetRole").equals(relationship.getLinkRole())) {
                    result.add(candidate);
                }
            } else {
                if (candidate.getLinkRole().equals(relationship.getLinkRole())) {
                    result.add(candidate);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Recursive implementation
     * @param relationship The domain-member relationship of interest.
     * @return The ancestor domain-member relationships of this relationship.
     * @throws XBRLException
     */
    public static SortedSet<Relationship> getAncestorDomainMemberRelationships(Relationship relationship) throws XBRLException {
        
        SortedSet<Relationship> previousRelationships = getPreviousDomainMemberRelationships(relationship);
        if (previousRelationships.isEmpty()) return previousRelationships;
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        result.addAll(previousRelationships);
        for (Relationship previousRelationship: previousRelationships) {
            result.addAll(getAncestorDomainMemberRelationships(previousRelationship));
        }
        return result;
    }
    
    /**
     * @param concept The concept of interest
     * @return The set of concepts that this concept inherits hypercubes from (including itself).
     * @throws XBRLException
     */
    public static Set<Concept> getHypercubeInheritanceConcepts(Concept concept) throws XBRLException {
        
        Set<Concept> result = new HashSet<Concept>();
        result.add(concept);
        SortedSet<Relationship> relationships = getInheritedDomainMemberRelationships(concept);
        for (Relationship relationship: relationships) {
            result.add((Concept) relationship.getSource());
        }
        return result;
    }
    
    /**
     * @param concept The concept of interest
     * @return The ancestor domain-member relationships of this concept.
     * @throws XBRLException
     */
    public static SortedSet<Relationship> getInheritedDomainMemberRelationships(Concept concept) throws XBRLException {
        SortedSet<Relationship> parentRelationships = getDomainMemberRelationshipsTo(concept);
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        result.addAll(parentRelationships);
        for (Relationship relationship: parentRelationships) {
            result.addAll(getAncestorDomainMemberRelationships(relationship));
        }
        return result;
    }    
    
}