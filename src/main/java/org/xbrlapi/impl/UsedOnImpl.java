package org.xbrlapi.impl;


import org.w3c.dom.Node;
import org.xbrlapi.UsedOn;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class UsedOnImpl extends FragmentImpl implements UsedOn {

    /**
     * 
     */
    private static final long serialVersionUID = 2691745089747004020L;

    /**
     * @see UsedOn#getUsedOnNamespace()
     */
    public String getUsedOnNamespace() throws XBRLException {
    	Node rootNode = getDataRootElement();
    	String u = rootNode.getTextContent().trim();
    	if (u.equals("")) throw new XBRLException("The used on declaration does not declare the element that usage is allowed on.");
	    return getNamespaceFromQName(u, rootNode);
    }
    
    /**
     * @see UsedOn#getUsedOnLocalname()
     */
    public String getUsedOnLocalname() throws XBRLException {
        Node rootNode = getDataRootElement();
        String u = rootNode.getTextContent().trim();
        if (u.equals("")) throw new XBRLException("The used on declaration does not declare the element that usage is allowed on.");
        return this.getLocalnameFromQName(u);
    }    
    
    /**
     * @see UsedOn#isUsedOn(String, String)
     */
    public boolean isUsedOn(String namespaceURI, String localname) throws XBRLException {
    	if (! getUsedOnNamespace().equals(namespaceURI))
    		return false;
    	if (! getUsedOnLocalname().equals(localname))
    		return false;
    	return true;
    }

}
