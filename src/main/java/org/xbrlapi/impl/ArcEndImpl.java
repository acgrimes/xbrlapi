package org.xbrlapi.impl;

import java.util.List;

import org.w3c.dom.Element;
import org.xbrlapi.Arc;
import org.xbrlapi.ArcEnd;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class ArcEndImpl extends ExtendedLinkContentImpl implements ArcEnd {
	
    /**
     * 
     */
    private static final long serialVersionUID = 8789093562540648488L;

    /**
     * @see ArcEnd#getLabel()
     */
    public String getLabel() throws XBRLException {
    	Element root = getDataRootElement();
    	if (root.hasAttributeNS(Constants.XLinkNamespace.toString(),"label"))
    		return root.getAttributeNS(Constants.XLinkNamespace.toString(),"label");
    	throw new XBRLException("XLink arc ends must have an xlink:label attribute");
    }

    /**
     * @see ArcEnd#getRole()
     */
    public String getRole() throws XBRLException {
    	Element root = getDataRootElement();
    	if (root.hasAttributeNS(Constants.XLinkNamespace.toString(),"role"))
    		return root.getAttributeNS(Constants.XLinkNamespace.toString(),"role");
    	return null;
    }

    /**
     * @see ArcEnd#getArcsFrom()
     */
    public List<Arc> getArcsFrom() throws XBRLException {
    	return getExtendedLink().getArcsWithFromLabel(this.getLabel());
    }

    /**
     * @see ArcEnd#getArcsFromWithArcrole(String)
     */
    public List<Arc> getArcsFromWithArcrole(String arcrole) throws XBRLException {
        return getExtendedLink().getArcsWithFromLabelAndArcrole(this.getLabel(),arcrole);
    }

    /**
     * @see ArcEnd#getArcsToWithArcrole(String)
     */
    public List<Arc> getArcsToWithArcrole(String arcrole) throws XBRLException {
        logger.debug("Getting arcs to " + this.getLabel() + " with arcrole " + arcrole);
        return getExtendedLink().getArcsWithToLabelAndArcrole(this.getLabel(),arcrole);
    }

    /**
     * @see ArcEnd#getArcsTo()
     */
    public List<Arc> getArcsTo() throws XBRLException {
    	return getExtendedLink().getArcsWithToLabel(this.getLabel());
    }

    /**
     * @see ArcEnd#getArcEndId()
     */
    public String getArcEndId() throws XBRLException {
    	Element root = getDataRootElement();
    	if (root.hasAttribute("id"))
    		return root.getAttribute("id");
    	return null;
    }
    
}
