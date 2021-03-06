package org.xbrlapi;

import java.util.List;

import org.xbrlapi.utilities.XBRLException;

/**
 * Custom type of XLink arcrole or role
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface CustomType extends Fragment {

    /**
     * Get the id of the customType.
     *
     * @throws XBRLException
     */
    public String getCustomTypeId() throws XBRLException;
    


    /**
     * Get the custom URI being defined.
     * @throws XBRLException
     */
    public String getCustomURI() throws XBRLException;
    


	/**
     * @return the string value of the role definition element or null if none is provided.
	 * @throws XBRLException.
	 */
    public String getDefinition() throws XBRLException;
        
    /**
     * @param fragment The fragment to be tested to determine if the custom
     * role has been used correctly based on usedOn constraints.
     * @return true if the custom role has been used correctly on the fragment
     * being checked.  Returns false otherwise.
     * @throws XBRLException
     */
    public boolean isUsedCorrectly(Fragment fragment) throws XBRLException;

    /**
     * Returns true only if the arcrole can be used on the specified element
     *
     * @param namespace The namespace of the element being tested for
     * @param localname The local name of the element being tested for
     * @throws XBRLException
     */
    public boolean isUsedOn(String namespace, String localname) throws XBRLException;
    
	/**
	 * @return the list of usedOn child fragments.
	 * @throws XBRLException.
	 */
	public List<UsedOn> getUsedOns() throws XBRLException;    

}
