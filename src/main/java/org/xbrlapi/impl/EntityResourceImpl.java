package org.xbrlapi.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xbrlapi.Entity;
import org.xbrlapi.EntityResource;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Entity resource implementation.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class EntityResourceImpl extends MixedContentResourceImpl implements EntityResource {

    /**
     * 
     */
    private static final long serialVersionUID = 5964964039469483783L;

    /**
     * @see EntityResource#getIdentifierScheme()
     */
    public String getIdentifierScheme() throws XBRLException {
        return this.getDataRootElement().getAttribute("scheme");
    }    

    /**
     * @see EntityResource#getIdentifierValue()
     */
    public String getIdentifierValue() throws XBRLException {
        return this.getDataRootElement().getAttribute("value");
    }

    /**
     * @see EntityResource#getEquivalents()
     */
    public Set<EntityResource> getEquivalents() throws XBRLException { 
        logger.debug("Getting equivalents to " + this.getStringIdentifier());
        Set<EntityResource> result = new HashSet<EntityResource>();
        this.getEquivalentsSet(result);
        return result;        
    }    
    
    /**
     * @return a string identifier for the entity resource
     * @throws XBRLException
     */
    protected String getStringIdentifier() throws XBRLException {
        return this.getIdentifierScheme() + "#" + this.getIdentifierValue();
    }

    /**
     * @return a list of entity resources that are parent or children 
     * of equivalent-entity relationships to this entity resource.
     * @throws XBRLException
     */
    protected List<EntityResource> getDirectEquivalents() throws XBRLException {
        List<EntityResource> equivalents = this.getParentEquivalents();
        equivalents.addAll(this.getChildEquivalents());
        return equivalents;
    }
    
    /**
     * @return a list of entity resources that are parents
     * of equivalent-entity relationships with this entity resource
     * as a target.
     * @throws XBRLException
     */
    protected List<EntityResource> getParentEquivalents() throws XBRLException {
        List<EntityResource> equivalents = getStore().<EntityResource>getTargets(this.getIndex(),null,Constants.XBRLAPIEquivalentEntitiesArcrole);
        return equivalents;
    }
    
    /**
     * @return a list of entity resources that are children
     * of equivalent-entity relationships with this entity resource
     * as a source.
     * @throws XBRLException
     */
    protected List<EntityResource> getChildEquivalents() throws XBRLException {
        return getStore().<EntityResource>getSources(this.getIndex(),null,Constants.XBRLAPIEquivalentEntitiesArcrole);
    }    
    
    /**
     * Augments a map of equivalent entities
     * @throws XBRLException
     */
    protected void getEquivalentsSet(Set<EntityResource> set) throws XBRLException {

        if (set == null) {
            set = new HashSet<EntityResource>();
        }
        
        set.add(this);
        
        Set<EntityResource> newEquivalents = new HashSet<EntityResource>(this.getDirectEquivalents());
        newEquivalents.removeAll(set);
        if (newEquivalents.isEmpty()) return;
        set.addAll(newEquivalents);
        
        for (EntityResource entityResource: newEquivalents) {
            ((EntityResourceImpl) entityResource).getEquivalentsSet(set);
        }
        
    }
    
    /**
     * @see EntityResource#getEntities()
     */
    public List<Entity> getEntities() throws XBRLException {
        String query = "#roots#[@type='org.xbrlapi.impl.EntityImpl' and */*/*[@scheme='" + this.getIdentifierScheme() + "' and .='" + this.getIdentifierValue() + "']]";
        List<Entity> entities = getStore().<Entity>queryForXMLResources(query);
        return entities;
    }

    /**
     * @see EntityResource#getEntities(URI)
     */
    public List<Entity> getEntities(URI uri) throws XBRLException {
        URI matchURI = getStore().getMatcher().getMatch(uri);
        String query = "#roots#[@type='" + EntityImpl.class.getName() + "' and @uri='" + matchURI + "' and */*/*[@scheme='" + this.getIdentifierScheme() + "' and .='" + this.getIdentifierValue() + "']]";
        List<Entity> entities = getStore().<Entity>queryForXMLResources(query);
        return entities;
    }
    
}
