package org.xbrlapi.xdt.validation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.xbrlapi.Arc;
import org.xbrlapi.Concept;
import org.xbrlapi.Relationship;
import org.xbrlapi.impl.RelationshipOrderComparator;
import org.xbrlapi.utilities.XBRLException;
import org.xbrlapi.xdt.ExplicitDimensionImpl;
import org.xbrlapi.xdt.Hypercube;
import org.xbrlapi.xdt.HypercubeImpl;
import org.xbrlapi.xdt.XDTConstants;

import com.google.common.collect.ListMultimap;

/**
 * This class gives easy access to static methods
 * from environments like Freemarker.  It makes it more
 * straightforward to analyse XDT structures by enabling 
 * determination of hypercube structures that are pertinent
 * to a given concept.
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class XDTAnalyser implements Serializable {

    private static final long serialVersionUID = -5684731124197015160L;

    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getInheritedHypercubes(Concept)
     */
    public ListMultimap<String, Hypercube> getInheritedHypercubes(Concept concept) throws XBRLException {
        return HypercubeImpl.getInheritedHypercubes(concept);
    }

    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getOwnHypercubes(Concept)
     */
    public ListMultimap<String, Hypercube> getOwnHypercubes(Concept concept) throws XBRLException {
        return HypercubeImpl.getOwnHypercubes(concept);
    }

    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getInheritedHasHypercubeRelationships(Concept)
     */
    public ListMultimap<String, Relationship> getInheritedHasHypercubeRelationships(Concept concept) throws XBRLException {
        return HypercubeImpl.getInheritedHasHypercubeRelationships(concept);
    }
    
    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getOwnHasHypercubeRelationships(Concept)
     */
    public ListMultimap<String, Relationship> getOwnHasHypercubeRelationships(Concept concept) throws XBRLException {
        return HypercubeImpl.getOwnHasHypercubeRelationships(concept);
    }    
    
    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getDomainMemberRelationshipsTo(Concept)
     */
    public SortedSet<Relationship> getDomainMemberRelationshipsTo(Concept concept) throws XBRLException {
        SortedSet<Relationship> result = concept.getStore().getRelationshipsTo(concept.getIndex(),null,XDTConstants.DomainMemberArcrole);
        return result;
    }
    
    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getPreviousDomainMemberRelationships(Relationship)
     */
    public SortedSet<Relationship> getPreviousDomainMemberRelationships(Relationship relationship) throws XBRLException {
        
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
     * Provides access to static methods.
     * @see HypercubeImpl#getAncestorDomainMemberRelationships(Relationship)
     */
    public SortedSet<Relationship> getAncestorDomainMemberRelationships(Relationship relationship) throws XBRLException {
        
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
     * Provides access to static methods.
     * @see HypercubeImpl#getHypercubeInheritanceConcepts(Concept)
     */
    public Set<Concept> getHypercubeInheritanceConcepts(Concept concept) throws XBRLException {
        
        Set<Concept> result = new HashSet<Concept>();
        result.add(concept);
        SortedSet<Relationship> relationships = getInheritedDomainMemberRelationships(concept);
        for (Relationship relationship: relationships) {
            result.add((Concept) relationship.getSource());
        }
        return result;
    }
    
    /**
     * Provides access to static methods.
     * @see HypercubeImpl#getInheritedDomainMemberRelationships(Concept)
     */
    public SortedSet<Relationship> getInheritedDomainMemberRelationships(Concept concept) throws XBRLException {
        SortedSet<Relationship> parentRelationships = getDomainMemberRelationshipsTo(concept);
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        result.addAll(parentRelationships);
        for (Relationship relationship: parentRelationships) {
            result.addAll(getAncestorDomainMemberRelationships(relationship));
        }
        return result;
    }        
    
    /**
     * Provides access to static methods.
     * @see ExplicitDimensionImpl#getDomainMemberRelationshipsFrom(String, Concept)
     */
    public SortedSet<Relationship> getDomainMemberRelationshipsFrom(String linkRole, Concept concept) throws XBRLException {
        return ExplicitDimensionImpl.getDomainMemberRelationshipsFrom(linkRole, concept);
    }    
    
    /**
     * @param relationship The XDT relationship to be analysed
     * @return the actual value of the targetRole attribute if it is an
     * attribute on the arc expressing the relationship and the link role 
     * of the arc otherwise.
     * @throws XBRLException if the arc does not have an XDT arcrole.
     */
    public String getTargetRole(Relationship relationship) throws XBRLException {
        if (! relationship.getArcrole().toString().startsWith("http://xbrl.org/int/dim/arcrole/"))
            throw new XBRLException("The arcrole is not specified in the XBRL Dimensions specification.");
        Arc arc = relationship.getArc();
        if (arc.hasAttribute(XDTConstants.XBRLDTNamespace,"targetRole")) {
            return arc.getAttribute(XDTConstants.XBRLDTNamespace,"targetRole");
        }
        return relationship.getLinkRole();
    }
    
    /**
     * @param relationship the has-hypercube relationship.
     * @return true if the relationship is closed and false otherwise.
     * @throws XBRLException if the relationship is not a has-hypercube relationship.
     */
    public boolean isClosedCube(Relationship relationship) throws XBRLException {
        Arc arc = relationship.getArc();
        if (arc.hasAttribute(XDTConstants.XBRLDTNamespace,"closed")) {
            if (arc.getAttribute(XDTConstants.XBRLDTNamespace,"closed").equals("true")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param relationship The has-hypercube relationship
     * @return true if the relationship is for a segment hypercube and
     * false otherwise.
     * @throws XBRLException if the relationship is not a has-hypercube relationship.
     */
    public boolean isSegmentCube(Relationship relationship) throws XBRLException {
        String arcrole = relationship.getArcrole().toString(); 
        if (arcrole.equals("http://xbrl.org/int/dim/arcrole/all") || arcrole.equals("http://xbrl.org/int/dim/arcrole/notall")) {
            Arc arc = relationship.getArc();
            if (arc.getAttribute(XDTConstants.XBRLDTNamespace,"contextElement").equals("segment")) {
                return true;
            }
            return false;
        }
        throw new XBRLException("The relationship must be a has-hypercube relationship.");
        
    }    
    
    /**
     * @param relationship The has-hypercube relationship
     * @return true if the relationship is for a segment hypercube and
     * false otherwise.
     * @throws XBRLException if the relationship is not a has-hypercube relationship.
     */
    public boolean isScenarioCube(Relationship relationship) throws XBRLException {
        return ! isSegmentCube(relationship);
    }    

    /**
     * @param relationship The dimension-domain or domain-member relationship
     * @return true if the relationship target is usable as a dimension value and false otherwise.
     * @throws XBRLException if the relationship is not of the right kind.
     */
    public boolean isUsableMember(Relationship relationship) throws XBRLException {
        String arcrole = relationship.getArcrole().toString(); 
        if (arcrole.equals("http://xbrl.org/int/dim/arcrole/dimension-domain") || arcrole.equals("http://xbrl.org/int/dim/arcrole/domain-member")) {
            Arc arc = relationship.getArc();
            if (arc.hasAttribute(XDTConstants.XBRLDTNamespace,"usable")) {
                if (arc.getAttribute(XDTConstants.XBRLDTNamespace,"usable").equals("false")) {
                    return false;
                }
            }
            return true;
        }
        throw new XBRLException("The relationship must be to a domain member.");
    }    

}
