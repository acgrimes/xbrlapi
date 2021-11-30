package org.xbrlapi;

import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface NonNumericItem extends Item {
	
	/** 
	 * @return the value of fact with leading and trailing spaces deleted 
	 * or null if the fact is nil.
	 * @throws XBRLException
	 */
	public String getValue() throws XBRLException;
	
	   /** 
     * @return null if the fact is nil. Otherwise value of fact with leading and trailing spaces deleted
     * and with some attempts made to ensure that the content is ready to be rendered as a chunk within
     * an XHTML document.  This involves:
     * <ol>
     * <li>Replacing all &amp;nbsp; occurrences with &amp;#160;</li>
     * <li>Replacing all &amp;ndash; occurrences with &amp;#8211;</li>
     * <li>Replacing all &amp;lt; ... &amp;gt; occurrences with &lt; ... &gt; to retrieve escaped angle bracket pairs</li>
     * </ol>
     * <p>
     * This will not be effective in some cases where there are escaped characters that are intended to be escaped but
     * that are falsely recognised as being part of the XML markup and thus unescaped.
     * </p>
     * TODO refine the algorithm for handling text content of fact values where characters have been escaped.
     * @throws XBRLException
     */
    public String getXmlValue() throws XBRLException;
	
	
}