package org.xbrlapi;

import java.util.List;

import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Entity extends ContextComponent {

    /**
     * Get the entity identifier scheme.
     *
     * @return the entity identifier scheme URI 
     * @throws XBRLException
     */
    public String getIdentifierScheme() throws XBRLException;
    


    /**
     * @return the entity identification value as an 
     * XML Schema token (basically eliminates unnecessary white spaces
     * but see XML Schema datatypes for the gory details.)
     * @throws XBRLException
     */
    public String getIdentifierValue() throws XBRLException;
    

    
    /**
     * Get the segment of the entity
     *
     * @return the segment information for the entity
     * or null if the entity does not include segment information.
     * @throws XBRLException
     */
    public Segment getSegment() throws XBRLException;
    
    /**
     * @return true iff the entity has explicit segment content.
     * @throws XBRLException
     */
    public boolean hasSegment() throws XBRLException;
    
    /**
     * @return a list of all the entity resources with the entity scheme and value 
     * of this entity fragment. The list is empty if there are no entity resources
     * that match the relevant criteria.
     * Note that the entity identifier value is trimmed before being compared to the identifier value
     * in entity resources.  If you are failing to get matches, avoid leading or trailing 
     * spaces in entity identification resource metadata value attributes.
     * @throws XBRLException
     */
    public List<EntityResource> getEntityResources() throws XBRLException;
    
    /**
     * @return the list of labels for the entity identified by this fragment,
     * NOT taking into account the labels for the given identifier and the labels
     * for any equivalent identifiers.
     * @throws XBRLException
     */
    public List<LabelResource> getEntityLabels() throws XBRLException;
    
    /**
     * @return the list of labels for the entity identified by this fragment,
     * taking into account the labels for the given identifier and the labels
     * for any equivalent identifiers.
     * @throws XBRLException
     */
    public List<LabelResource> getAllEntityLabels() throws XBRLException;   
    
}
