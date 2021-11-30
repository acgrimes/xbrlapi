package org.xbrlapi.xdt;

import java.util.List;
import java.util.SortedSet;

import org.xbrlapi.Relationship;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Hypercube extends XDTConcept {

    /**
     * @return a list of the hypercube's XDT dimensions.
     * This gets all of the dimensions associated with the hypercube, 
     * regardless of the link role that the relationship to the dimension
     * is defined in.
     * @throws XBRLException
     */
    public List<Dimension> getDimensions() throws XBRLException;
    
    /**
     * @param linkRole the link role URI to use when selecting 
     * the dimensions of the hypercube.
     * @return a list of the hypercube's XDT dimensions defined
     * by hypercube-dimension relationships with link role values
     * as specified.
     * @throws XBRLException
     */
    public List<Dimension> getDimensions(String linkRole) throws XBRLException;
    
    /**
     * @param linkRole the link role URI to use when selecting 
     * the dimensions of the hypercube.
     * @return a list of the active (not prohibited or overridden) 
     * relationships to the hypercube's XDT dimensions 
     * with link role values as specified.
     * @throws XBRLException
     */
    public SortedSet<Relationship> getRelationshipsToDimensions(String linkRole) throws XBRLException;
    
    
}
