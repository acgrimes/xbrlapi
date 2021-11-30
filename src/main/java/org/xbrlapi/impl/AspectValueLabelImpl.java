package org.xbrlapi.impl;

import org.apache.log4j.Logger;
import org.xbrlapi.AspectValueLabel;
import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.utilities.XBRLException;

/**
 * An aspect value label XML resource stores information about
 * a label for an aspect value.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class AspectValueLabelImpl extends NonFragmentXMLImpl implements AspectValueLabel {
	
    /**
     * 
     */
    private static final long serialVersionUID = 4657502706837130397L;
    
    private static final Logger logger = Logger.getLogger(AspectValueLabelImpl.class);
    
    /**
	 * No argument constructor.
	 * @throws XBRLException
	 */
	public AspectValueLabelImpl() throws XBRLException {
		super();
	}
	
	public AspectValueLabelImpl(Class<?> labellerClass, String id, String aspectId, String valueId, String locale, String resourceRole, String linkRole, String label, boolean isFallbackLabel) throws XBRLException {
		this();
        setBuilder(new BuilderImpl());
        if (id == null) throw new XBRLException("The XML resource ID must not be null.");
        this.setIndex(id);
        this.setLabellerClass(labellerClass.getName());
		this.setAspectId(aspectId);
        if (valueId == null) throw new XBRLException("The aspect value ID must not be null.");
        this.setValueId(valueId);
        this.setLocale(locale);
        this.setResourceRole(resourceRole);
        this.setLinkRole(linkRole);
        this.setLabel(label);
        this.setFallbackFlag(isFallbackLabel);
        
        // Up to here all of the properties have been stored in an XML DOM being
        // put together by the builder.
        
		this.finalizeBuilder();
	}

    private void setLabellerClass(String className) throws XBRLException {
        this.setMetaAttribute("labellerClass",className);
    }
    
	private void setAspectId(String aspectId) throws XBRLException {
        if (aspectId == null) throw new XBRLException("The aspectId must not be null.");
        this.setMetaAttribute("aspectId",aspectId);
    }

    private void setValueId(String valueId) throws XBRLException {
        if (valueId == null) throw new XBRLException("The valueId must not be null.");
        this.setMetaAttribute("valueId",valueId);
    }

    private void setLocale(String locale) throws XBRLException {
        if (locale == null) this.setMetaAttribute("locale","null");
        else this.setMetaAttribute("locale",locale);
    }

    private void setResourceRole(String resourceRole) throws XBRLException {
        if (resourceRole == null) this.setMetaAttribute("resourceRole","null");
        else this.setMetaAttribute("resourceRole",resourceRole);
    }
    
    private void setLinkRole(String linkRole) throws XBRLException {
        if (linkRole == null) setMetaAttribute("linkRole","null");
        else setMetaAttribute("linkRole",linkRole);
    }
    
    private void setLabel(String label) throws XBRLException {
        if (label == null) throw new XBRLException("The label must not be null.");
        this.setMetaAttribute("label",label);
    }
    
    /**
     * The attribute is omitted if the label is not a fallback label.
     * It has a value of true if the label is a fallback label.
     * @param isFallbackLabel
     * @throws XBRLException
     */
    private void setFallbackFlag(boolean isFallbackLabel) throws XBRLException {
        if (isFallbackLabel) this.setMetaAttribute("fallback","true");
    }    

    /**
     * @see AspectValueLabel#getAspectId()
     */
    public String getAspectId() throws XBRLException {
        return this.getMetaAttribute("aspectId");
    }

    /**
     * @see AspectValueLabel#getLabel()
     */
    public String getLabel() throws XBRLException {
        return this.getMetaAttribute("label");
    }

    /**
     * @see AspectValueLabel#getLinkRole()
     */
    public String getLinkRole() throws XBRLException {
        String value = this.getMetaAttribute("linkRole");
        if (value.equals("null")) return null;
        return value;
    }

    /**
     * @see AspectValueLabel#getLocale()
     */
    public String getLocale() throws XBRLException {
        String value = this.getMetaAttribute("locale");
        if (value.equals("null")) return null;
        return value;
    }

    /**
     * @see AspectValueLabel#getResourceRole()
     */
    public String getResourceRole() throws XBRLException {
        String value = this.getMetaAttribute("resourceRole");
        if (value.equals("null")) return null;
        return value;
    }

    /**
     * @see AspectValueLabel#getValueId()
     */
    public String getValueId() throws XBRLException {
        return this.getMetaAttribute("resourceRole");
    }

}
