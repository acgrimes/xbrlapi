package org.xbrlapi.aspects;

import java.util.List;

import org.apache.log4j.Logger;
import org.xbrlapi.AspectValueLabel;
import org.xbrlapi.data.Store;
import org.xbrlapi.impl.AspectValueLabelImpl;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * Provides a label caching system based on the underlying data store.
 * </p>
 * 
 * <p>
 * Do not use this with a DOM based data store.  It will cause concurrency problems.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class StoreLabelCache implements LabelCache {

    /**
     * 
     */
    private static final long serialVersionUID = -1322629918281873648L;

    protected final static Logger logger = Logger.getLogger(StoreLabelCache.class);
    
    /**
     * The data store used to cache aspect value labels.
     */
    private Store store;
    
    /**
     * @param store The data store to use.
     */
    public StoreLabelCache(Store store) throws XBRLException {
        super();
        if (store == null) throw new XBRLException("The store must not be null.");
        this.store = store;
    }

    /**
     * @see LabelCache#getLabel(Class, String, String, String, String, String)
     */
    public String getLabel(Class<?> labellerClass, String aspectId, String valueId, String locale, String resourceRole, String linkRole) throws XBRLException {
        AspectValueLabel label = this.getAspectValueLabel(labellerClass, aspectId, valueId, locale, resourceRole, linkRole);
        if (label == null) return null;
        return label.getLabel();
    }

    /**
     * @see LabelCache#getLabelWithoutFallback(Class, String, String, String, String, String)
     */
    public String getLabelWithoutFallback(Class<?> labellerClass, String aspectId, String valueId, String locale, String resourceRole, String linkRole) throws XBRLException {
        AspectValueLabel label = this.getAspectValueLabelWithoutFallback(labellerClass, aspectId, valueId, locale, resourceRole, linkRole);
        if (label == null) return null;
        return label.getLabel();
    }


    private AspectValueLabel getAspectValueLabel(Class<?> labellerClass, String aspectId, String valueId, String locale, String resourceRole, String linkRole) throws XBRLException {
        String query = "for $root in #roots#[@type='"+AspectValueLabelImpl.class.getName()+"'] where " +
                "$root/@labellerClass='"+ labellerClass.getName() +"' and " +
                "$root/@aspectId='"+ aspectId +"' and " +
                "$root/@valueId='"+ valueId +"' and " +
                "$root/@locale='"+ locale +"' and " +
                "$root/@resourceRole='"+ resourceRole +"' and " +
                "$root/@linkRole='"+ linkRole +"' " +
                "return $root";
        List<AspectValueLabel> labels = store.<AspectValueLabel>queryForXMLResources(query);
        if (labels.size() == 0) return null;
        return labels.get(0);
    }
    
    private AspectValueLabel getAspectValueLabelWithoutFallback(
            Class<?> labellerClass, String aspectId, String valueId,
            String locale, String resourceRole, String linkRole) throws XBRLException {
    String query = "for $root in #roots#[@type='"+AspectValueLabelImpl.class.getName()+"'] where " +
                "$root/@labellerClass='"+ labellerClass.getName() +"' and " +
                "$root/@aspectId='"+ aspectId +"' and " +
                "$root/@valueId='"+ valueId +"' and " +
                "$root/@locale='"+ locale +"' and " +
                "$root/@resourceRole='"+ resourceRole +"' and " +
                "$root/@linkRole='"+ linkRole +"' and " +
                "not($root/@fallback) " +
                "return $root";
        List<AspectValueLabel> labels = store.<AspectValueLabel>queryForXMLResources(query);
        if (labels.size() == 0) return null;
        return labels.get(0);
    }    
    
    /**
     * @see LabelCache#cacheLabel(Class, String, String, String, String, String, String, boolean)
     */
    public void cacheLabel(Class<?> labellerClass, String aspectId, String valueId, String locale,
            String resourceRole, String linkRole, String label, boolean isFallbackLabel) throws XBRLException {
        AspectValueLabel avl = getAspectValueLabel(labellerClass, aspectId, valueId, locale, resourceRole, linkRole);
        if (avl != null) store.remove(avl);
        String id = store.getId(aspectId + valueId + locale + resourceRole + linkRole);
        AspectValueLabelImpl xml = new AspectValueLabelImpl(labellerClass, id,aspectId,valueId, locale,resourceRole,linkRole,label, isFallbackLabel);
        store.persist(xml);
    }

}
