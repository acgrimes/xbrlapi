package org.xbrlapi.xdt;

import java.util.SortedSet;

import org.xbrlapi.Concept;
import org.xbrlapi.Relationship;
import org.xbrlapi.utilities.XBRLException;


/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface ExplicitDimension extends Dimension {

    /**
     * @return true if the explicit dimension has a default domain member
     * and false otherwise (including if it has more than one default domain member,
     * thus undermining the default property of the indicated defaults.
     * @throws XBRLException
     */
    public boolean hasDefaultDomainMember() throws XBRLException;

    /**
     * @return the default domain member for the explicit dimension.
     * @throws XBRLException if the explicit dimension does not have
     * a default domain member or if it has more than one default domain
     * members.
     */
    public Concept getDefaultDomainMember() throws XBRLException;
    
    /**
     * @param linkRole The link role of the relationships to the domains from
     * this dimension.
     * @return the order-based sorted set of relationships to the domains from
     * this dimension.
     * @throws XBRLException
     */
    public SortedSet<Relationship> getRelationshipsToDomains(String linkRole) throws XBRLException;
}
