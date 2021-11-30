package org.xbrlapi.impl;

import org.xbrlapi.NonNumericItem;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class NonNumericItemImpl extends ItemImpl implements NonNumericItem {

	/**
     * 
     */
    private static final long serialVersionUID = -679088686747723914L;

    /** 
	 * @see NonNumericItem#getValue()
	 */
	public String getValue() throws XBRLException {
		if (this.isNil()) return null;
		return getDataRootElement().getTextContent().trim();
	}
	
    /** 
     * @see NonNumericItem#getXmlValue()
     */
	public String getXmlValue() throws XBRLException {
        String value = this.getValue();
        if (value == null) return value;
        value.replaceAll("&nbsp;","&#160;");
        value.replaceAll("&ndash;","&#8211;");
        value.replaceAll("&lt;(.+)&gt;","<$1>");
        return value;
	}
	
}
