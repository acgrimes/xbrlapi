package org.xbrlapi.xdt;

import java.util.List;
import java.util.SortedSet;

import org.xbrlapi.Concept;
import org.xbrlapi.Relationship;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ExplicitDimensionImpl extends DimensionImpl implements ExplicitDimension {

    /**
     * 
     */
    private static final long serialVersionUID = -1286219707900264040L;

    /**
     * @see ExplicitDimension#getDefaultDomainMember()
     */
    public Concept getDefaultDomainMember() throws XBRLException {
        List<Concept> defaults = getStore().<Concept>getTargets(this.getIndex(),null,XDTConstants.DefaultDimensionArcrole);
        if (defaults.size() == 0) throw new XBRLException("There are no defaults for explicit dimension " + this.getTargetNamespace() + ":" + this.getName());
        if (defaults.size() > 1) throw new XBRLException("There are multiple defaults for explicit dimension " + this.getTargetNamespace() + ":" + this.getName());
        return defaults.get(0);
    }

    /**
     * @see ExplicitDimension#hasDefaultDomainMember()
     */
    public boolean hasDefaultDomainMember() throws XBRLException {
        List<Concept> defaults = getStore().<Concept>getTargets(this.getIndex(),null,XDTConstants.DefaultDimensionArcrole);
        return (defaults.size() == 1);
    }
    
    /**
     * @see ExplicitDimension#getRelationshipsToDomains(String)
     */
    public SortedSet<Relationship> getRelationshipsToDomains(String linkRole) throws XBRLException {
        return getStore().getRelationshipsFrom(getIndex(),linkRole, XDTConstants.DimensionDomainArcrole);
    }
    
    /**
     * @param linkRole THe link role of the relationships to retrieve
     * @param concept The concept that the domain-member relationships are to run from.
     * @return the order-sorted set of domain member relationships with the given link role
     * from the given concept.
     * @throws XBRLException
     */
    public static SortedSet<Relationship> getDomainMemberRelationshipsFrom(String linkRole, Concept concept) throws XBRLException {
        return concept.getStore().getRelationshipsFrom(concept.getIndex(),linkRole, XDTConstants.DomainMemberArcrole);
    }
    
    
}