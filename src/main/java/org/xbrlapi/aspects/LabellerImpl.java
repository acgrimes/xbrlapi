package org.xbrlapi.aspects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xbrlapi.data.Store;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A generic labeller that returns aspect IDs as the aspect labels
 * and that returns aspect value IDs as the aspect value labels.
 * It ignores XLink roles and locales when generating the labels.
 * </p>
 * 
 * <p>
 * Labeller implementations should extend this class.
 * </p>
 * 
 * <p>
 * This labeller can be used for all aspects but it is pretty ordinary in 
 * terms of the quality of the labels it generates.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class LabellerImpl implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = 519089390627009535L;

    protected final static Logger logger = Logger.getLogger(LabellerImpl.class);
    
    /**
     * The aspect that this labeller deals with.
     */
    private Aspect aspect;
    
    /**
     * @param aspect The aspect to be a labeller for.
     */
    public LabellerImpl(Aspect aspect) throws XBRLException {
        super();
        if (aspect == null) throw new XBRLException("The aspect must not be null.");
        this.aspect = aspect;
    }

    /**
     * @see Labeller#getAspect()
     */
    public Aspect getAspect() {
        return aspect;
    }

    /**
     * @see Labeller#getDomain()
     */
    public Domain getDomain() {
        return aspect.getDomain();
    }
    
    /**
     * @see Labeller#getStore()
     */
    public Store getStore() throws XBRLException {
        return getDomain().getStore();
    }
    
    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    public String getAspectLabel(String locale, String resourceRole, String linkRole) {
        return this.getAspectLabelWithoutFallback(locale, resourceRole, linkRole);
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    public String getAspectLabelWithoutFallback(String locale,
            String resourceRole, String linkRole) {
        return aspect.getId().toString();
    }

    /**
     * @see Labeller#getAspectValueLabel(AspectValue, String, String, String)
     */
    public String getAspectValueLabel(AspectValue value, String locale, String resourceRole, String linkRole) {
        return this.getAspectValueLabelWithoutFallback(value, locale,resourceRole, linkRole);
    }
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale, String resourceRole, String linkRole) {
        return value.getId();
    }

    /**
     * @see Labeller#getAspectValueLabelGivenLists(AspectValue, List, List, List)
     */
    public String getAspectValueLabelGivenLists(AspectValue value, List<String> locales,
            List<String> resourceRoles, List<String> linkRoles) {

        if (locales == null) {
            locales = new ArrayList<String>();
            locales.add(null);
        }        
        if (resourceRoles == null) {
            resourceRoles = new ArrayList<String>();
            resourceRoles.add(null);
        }
        if (linkRoles == null) {
            linkRoles = new ArrayList<String>();
            linkRoles.add(null);
        }        
        
        for (String linkRole: resourceRoles) {
            for (String resourceRole: resourceRoles) {
                for (String locale: locales) {
                    String label = this.getAspectValueLabelWithoutFallback(value,locale, resourceRole, linkRole);
                    if (label != null) {
                        return label;
                    }
                }
            }
        }
        
        return this.getAspectValueLabel(value,locales.get(0), resourceRoles.get(0), linkRoles.get(0));
    }
    
    /**
     * @see Labeller#getAspectLabelGivenLists(List, List, List)
     */
    @Override
    public String getAspectLabelGivenLists(List<String> locales, List<String> resourceRoles, List<String> linkRoles) {

        if (locales == null) {
            locales = new ArrayList<String>();
            locales.add(null);
        }        
        if (resourceRoles == null) {
            resourceRoles = new ArrayList<String>();
            resourceRoles.add(null);
        }
        if (linkRoles == null) {
            linkRoles = new ArrayList<String>();
            linkRoles.add(null);
        }        
        
        for (String linkRole: resourceRoles) {
            for (String resourceRole: resourceRoles) {
                for (String locale: locales) {
                    String label = this.getAspectLabelWithoutFallback(locale, resourceRole, linkRole);
                    if (label != null) {
                        return label;
                    }
                }
            }
        }
        
        return this.getAspectLabel(null, null, null);
    }
    
    
    
    /**
     * @see Labeller#duplicate()
     */
    public Labeller duplicate() throws XBRLException {
        
        try {
            Class<?> modelClass = this.getClass();
            Constructor<?> constructor = modelClass.getConstructor(Aspect.class);
            Labeller duplicate = (Labeller) constructor.newInstance(this.getAspect());
            return duplicate;
        } catch (NoSuchMethodException nsme) {
            throw new XBRLException("The aspect model constructor does not support duplication.",nsme);
        } catch (InvocationTargetException ite) {
            throw new XBRLException("The aspect model constructor does not support duplication.",ite);
        } catch (IllegalAccessException iae) {
            throw new XBRLException("The aspect model constructor does not support duplication.",iae);
        } catch (InstantiationException ie) {
            throw new XBRLException("The aspect model constructor does not support duplication.",ie);
        }
        
    }    
}
