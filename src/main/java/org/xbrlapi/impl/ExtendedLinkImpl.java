package org.xbrlapi.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.xbrlapi.Arc;
import org.xbrlapi.ArcEnd;
import org.xbrlapi.ExtendedLink;
import org.xbrlapi.Locator;
import org.xbrlapi.Resource;
import org.xbrlapi.XlinkDocumentation;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ExtendedLinkImpl extends LinkImpl implements ExtendedLink {

    /**
     * 
     */
    private static final long serialVersionUID = -5810751426509975700L;

    /**
     * @see ExtendedLink#getLocators()
     */
    public List<Locator> getLocators() throws XBRLException {
    	List<Locator> locators = this.<Locator>getChildren("org.xbrlapi.impl.LocatorImpl");
    	return locators;
    }

    /**
     * @see ExtendedLink#getArcEndsWithLabel(String)
     */
    public <E extends ArcEnd> List<E> getArcEndsWithLabel(String label) throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and "+ Constants.XBRLAPIPrefix+ ":" + "data/*/@xlink:label='" + label + "']";
    	List<E> ends = getStore().<E>queryForXMLResources(xpath);
    	logger.debug("Extended link " + getIndex() + " has " + ends.size() + " ends with label " + label);
    	return ends;
    }    
    
    /**
     * @see ExtendedLink#getLocatorsWithLabel(String)
     */
    public List<Locator> getLocatorsWithLabel(String label) throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and @type='org.xbrlapi.impl.LocatorImpl' and " + Constants.XBRLAPIPrefix + ":" + "data/link:loc/@xlink:label='" + label + "']";
    	return getStore().<Locator>queryForXMLResources(xpath);
    }

    /**
     * @see ExtendedLink#getLocatorsWithHref(String)
     */
    public List<Locator> getLocatorsWithHref(String href) throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and @type='org.xbrlapi.impl.LocatorImpl' and @absoluteHref='" + href + "']";
    	return getStore().<Locator>queryForXMLResources(xpath);
    }
    
    /**
     * @see ExtendedLink#getArcs()
     */
    public List<Arc> getArcs() throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and " + Constants.XBRLAPIPrefix + ":" + "data/*/@xlink:type='arc']";
    	return getStore().<Arc>queryForXMLResources(xpath);
    }

    /**
	 * @see ExtendedLink#getArcsWithToLabel(String)
	 */
	public List<Arc> getArcsWithToLabel(String to) throws XBRLException {
		String query = "#roots#[@parentIndex='" + getIndex() + "' and xbrlapi:data/*/@xlink:to='" + to + "']";
		logger.debug(query);
		return getStore().<Arc>queryForXMLResources(query);
	}

	/**
     * @see ExtendedLink#getArcsWithFromLabel(String)
     */
    public List<Arc> getArcsWithFromLabel(String from) throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and "+ Constants.XBRLAPIPrefix+ ":" + "data/*/@xlink:from='" + from + "']";
    	List<Arc> arcs = getStore().<Arc>queryForXMLResources(xpath);
    	return arcs;
    }
    
    /**
     * @see ExtendedLink#getArcsWithFromLabelAndArcrole(String,String)
     */
    public List<Arc> getArcsWithFromLabelAndArcrole(String from, String arcrole) throws XBRLException {
        String xpath = "#roots#[@parentIndex='" + getIndex() + "' and */*[@xlink:from='" + from + "' and @xlink:arcrole='" + arcrole + "']]";
        List<Arc> arcs = getStore().<Arc>queryForXMLResources(xpath);
        return arcs;
    }
    
    /**
     * @see ExtendedLink#getArcsWithArcrole(String)
     */
    public List<Arc> getArcsWithArcrole(String arcrole) throws XBRLException {
        String xpath = "#roots#[@parentIndex='" + getIndex() + "' and */*/@xlink:arcrole='" + arcrole + "']";
        List<Arc> arcs = getStore().<Arc>queryForXMLResources(xpath);
        return arcs;
    }    
    
    /**
     * @see ExtendedLink#getArcsWithToLabelAndArcrole(String,String)
     */
    public List<Arc> getArcsWithToLabelAndArcrole(String to, String arcrole) throws XBRLException {
        String query = "#roots#[@parentIndex='" + getIndex() + "' and xbrlapi:data/*[@xlink:to='" + to + "' and @xlink:arcrole='" + arcrole + "']]";
        logger.debug(query);
        List<Arc> arcs = getStore().<Arc>queryForXMLResources(query);
        return arcs;
    }
    
    /**
     * @see ExtendedLink#getResources()
     * 
     */
    public List<Resource> getResources() throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and " + Constants.XBRLAPIPrefix + ":" + "data/*/@xlink:type='resource']";
    	return getStore().<Resource>queryForXMLResources(xpath);
    }
    
    /**
     * @see ExtendedLink#getResourcesWithLabel(String)
     */
    public List<Resource> getResourcesWithLabel(String label) throws XBRLException {
    	String xpath = "#roots#[@parentIndex='" + getIndex() + "' and " + Constants.XBRLAPIPrefix + ":" + "data/*[@xlink:type='resource']/@xlink:label='" + label + "']";
    	return getStore().<Resource>queryForXMLResources(xpath);
    }

    /**
     * Get the list of documentation fragments contained by the extended link.
     * Returns the list of documentation fragments in the extended link.
     * @throws XBRLException
     * @see ExtendedLink#getDocumentations()
     */
    public List<XlinkDocumentation> getDocumentations() throws XBRLException {
    	return this.<XlinkDocumentation>getChildren("org.xbrlapi.impl.XlinkDocumentationImpl");
    }
    
    /**
     * @see ExtendedLink#getArcEndIndicesByLabel()
     */
    public Map<String,List<String>> getArcEndIndicesByLabel() throws XBRLException {
        
        Map<String,List<String>> result = new HashMap<String,List<String>>();        
        String query = "for $fragment in #roots#[@parentIndex='" + getIndex() + "' and */*[@xlink:type='resource' or @xlink:type='locator']] return concat($fragment/@index,' ',$fragment/*/*/@xlink:label)";
        Set<String> pairs = getStore().queryForStrings(query);
        for (String pair: pairs) {
            int split = pair.indexOf(" ");
            String index = pair.substring(0,split);
            String label = pair.substring(split+1);
            if (result.containsKey(label)) {
                result.get(label).add(index);
            } else {
                List<String> list = new Vector<String>();
                list.add(index);
                result.put(label,list);
            }
        }

        return result;
    }
    
    /**
     * @see ExtendedLink#getLocatorTargetIndices()
     */
    public Map<String,String> getLocatorTargetIndices() throws XBRLException {
        
        Map<String,String> result = new HashMap<String,String>();
        
        String query = "for $locator in #roots#[@parentIndex='" + getIndex() + "' and */*/@xlink:type='locator'] return concat($locator/@index,' ',#roots#[@uri=$locator/@targetDocumentURI and $locator/@targetPointerValue=" + Constants.XBRLAPIPrefix + ":xptr/@value]/@index)";
        Set<String> pairs = getStore().queryForStrings(query);
        for (String pair: pairs) {
            int split = pair.indexOf(" ");
            String locatorIndex = pair.substring(0,split);
            String targetIndex = pair.substring(split+1);
            result.put(locatorIndex,targetIndex);
        }

        return result;
    }    


}
