package org.xbrlapi.aspects;


import org.apache.log4j.Logger;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A generic aspect labeller that uses a caching system.  It is made specific
 * to a given aspect by setting its labeller property as part of the constructor.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class CachingLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = 5414469666286671643L;

    protected final static Logger logger = Logger
    .getLogger(CachingLabeller.class);
    
    private Labeller labeller;
    
    /**
     * The aspect value label caching system.
     */
    private LabelCache cache;

    /**
     * @param cache The label cache to use.
     * @param labeller The labeller to cache labels for.
     * @throws XBRLException
     */
    public CachingLabeller(LabelCache cache, Labeller labeller) throws XBRLException {
        super(labeller.getAspect());
        this.labeller = labeller;
        if (cache == null) throw new XBRLException("The label cache must not be null.");
        this.cache = cache;
    }

    /**
     * @return The labeller used by this caching labeller to produce the actual
     * labels prior to being cached.
     */
    public Labeller getLabeller() {
        return labeller;
    }
    
    /**
     * @return the aspect of the embedded labeller.
     * @see Labeller#getAspect()
     */
    @Override
    public Aspect getAspect() {
        return labeller.getAspect();
    }
    
    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    @Override
    public String getAspectLabel(String locale, String resourceRole, String linkRole) {
        return labeller.getAspectLabel(locale, resourceRole, linkRole);
    }

    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return labeller.getAspectLabelWithoutFallback(locale, resourceRole, linkRole);
    }

    /**
     * @see Labeller#getAspectValueLabel(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabel(AspectValue value, String locale,
            String resourceRole, String linkRole) {

    	String aspectId = getAspect().getId();
        String valueId = value.getId();

        try {
            
            String label = cache.getLabel(getLabeller().getClass(), aspectId, valueId, locale, resourceRole, linkRole);
            if (label != null) return label;
            label = labeller.getAspectValueLabelWithoutFallback(value, locale, resourceRole, linkRole);
            if (label != null) {
                this.cache.cacheLabel(getLabeller().getClass(), aspectId, valueId, locale, resourceRole,linkRole, label, false);
                return label;
            }
            label = labeller.getAspectValueLabel(value, locale, resourceRole, linkRole);
            if (label != null) {
                this.cache.cacheLabel(getLabeller().getClass(), aspectId, valueId, locale, resourceRole,linkRole, label, true);
                return label;
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return labeller.getAspectValueLabel(value, locale, resourceRole, linkRole);

    }

    /**
     * @see Labeller#getAspectValueLabel(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale,
            String resourceRole, String linkRole) {
        
    	String aspectId = getAspect().getId();
        String valueId = value.getId();

        try {
            
            // Try the external caching system
            String label = cache.getLabelWithoutFallback(getLabeller().getClass(), aspectId, valueId, locale, resourceRole, linkRole);
            if (label != null) {
                return label;
            }
            
            // Try the original source
            label = labeller.getAspectValueLabelWithoutFallback(value, locale, resourceRole, linkRole);
            if (label != null) {
                this.cache.cacheLabel(getLabeller().getClass(), aspectId, valueId, locale, resourceRole,linkRole, label, false);
            }
            return label;

        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return null;

    }
    
    /**
     * Duplicates the nested labeller but not the caching system.
     * A reference to the same caching system is used by the duplicate
     * caching labeller.
     * @see LabellerImpl#duplicate()
     */
    @Override
    public Labeller duplicate() throws XBRLException {
        return new CachingLabeller(this.cache, getLabeller().duplicate());
    }

}
