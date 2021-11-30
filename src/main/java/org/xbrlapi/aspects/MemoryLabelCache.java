package org.xbrlapi.aspects;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * Provides an in-memory label caching system.
 * This is really only useful for unit-testing purposes.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class MemoryLabelCache implements LabelCache {

    /**
     * 
     */
    private static final long serialVersionUID = -6478712477019768054L;

    protected final static Logger logger = Logger.getLogger(MemoryLabelCache.class);
    
    private Map<String,String> map = new HashMap<String,String>();
    private Map<String,String> fallbackMap = new HashMap<String,String>();
    
    public MemoryLabelCache() {
        super();
    }

    /**
     * @see LabelCache#getLabel(Class, String, String, String, String, String)
     */
    public String getLabel(Class<?> labellerClass, String aspectId, String valueId, String locale, String resourceRole, String linkRole) throws XBRLException {
        String key = labellerClass.getName() + aspectId + valueId + locale + resourceRole + linkRole;
        if (map.containsKey(key)) return map.get(key);
        if (fallbackMap.containsKey(key)) return fallbackMap.get(key);
        return null;
    }
    
    /**
     * @see LabelCache#getLabelWithoutFallback(Class, String, String, String, String, String)
     */
    public String getLabelWithoutFallback(Class<?> labellerClass, String aspectId, String valueId, String locale, String resourceRole, String linkRole) throws XBRLException {
        String key = labellerClass.getName() + aspectId + valueId + locale + resourceRole + linkRole;
        if (map.containsKey(key)) return map.get(key);
        return null;
    }    
    
    /**
     * @see LabelCache#cacheLabel(Class, String, String, String, String, String, String, boolean)
     */
    public void cacheLabel(Class<?> labellerClass, String aspectId, String valueId, String locale,
            String resourceRole, String linkRole, String label, boolean isFallbackLabel) {
        String key = labellerClass.getName() + aspectId + valueId + locale + resourceRole + linkRole;
        if (isFallbackLabel) fallbackMap.put(key,label);
        else map.put(key,label);
    }

}
