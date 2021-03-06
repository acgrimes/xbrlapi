package org.xbrlapi.impl;

import org.w3c.dom.Element;
import org.xbrlapi.Link;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class LinkImpl extends XlinkImpl implements Link {

    /**
     * 
     */
    private static final long serialVersionUID = 6703189602306256819L;

    /**
     * @see Link#getLinkRole()
     */
    public String getLinkRole() throws XBRLException {
    	Element root = getDataRootElement();
    	if (root.hasAttributeNS(Constants.XLinkNamespace,"role")) {
    	    return root.getAttributeNS(Constants.XLinkNamespace,"role");
    	}
    	return null;
    }
    
}
