package org.xbrlapi.impl;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xbrlapi.Arc;
import org.xbrlapi.ArcEnd;
import org.xbrlapi.ExtendedLink;
import org.xbrlapi.Fragment;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * Used for all extended link arcs.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ArcImpl extends ExtendedLinkContentImpl implements Arc {

    /**
     * The serial version UID.
     * @see 
     * http://java.sun.com/javase/6/docs/platform/serialization/spec/version.html#6678
     * for information about what changes will require the serial version UID to be
     * modified.
     */
    private static final long serialVersionUID = -5826492379826212317L;

    /**
     * @see Arc#getAttribute(String,String)
     */
    public String getAttribute(String namespace, String name) throws XBRLException {
        Element root = getDataRootElement();
        String ns = namespace.toString();
        if (! root.hasAttributeNS(ns,name)) return null;
        return root.getAttributeNS(ns,name);
    }
    
    /**
     * @see Arc#hasAttribute(String,String)
     */    
    public boolean hasAttribute(String namespace, String name) throws XBRLException {
        return getDataRootElement().hasAttributeNS(namespace.toString(),name);
    }    
    
    /**
     * @see Arc#getAttribute(String)
     */
    public String getAttribute(String name) throws XBRLException {
        Element root = getDataRootElement();
        if (! root.hasAttribute(name)) return null;
        return root.getAttribute(name);
    }
    
    /**
     * @see Arc#hasAttribute(String)
     */
    public boolean hasAttribute(String name) throws XBRLException {
        return getDataRootElement().hasAttribute(name);
    }
    
    /**
     * @see org.xbrlapi.ExtendedLinkContent#getExtendedLink()
     */
    public ExtendedLink getExtendedLink() throws XBRLException {
        Fragment parent = this.getParent();
        if (! parent.isa(ExtendedLinkImpl.class)) throw new XBRLException("The parent of arc " + this.getIndex() + " is not an extended link.");
        return (ExtendedLink) parent;
    }
    
    /**
     * @see Arc#getShow()
     */
    public String getShow() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttributeNS(Constants.XLinkNamespace.toString(),"show")) return null;
    	return getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"show");
    }
	
    /**
     * @see Arc#getActuate()
     */
    public String getActuate() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttributeNS(Constants.XLinkNamespace.toString(),"actuate")) return null;
    	return getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"actuate");
    }
	
    /**
     * @see Arc#getFrom()
     */
    public String getFrom() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttributeNS(Constants.XLinkNamespace.toString(),"from")) return null;
    	return getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"from");
    }
    
    /**
     * @see Arc#getTo()
     */
    public String getTo() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttributeNS(Constants.XLinkNamespace.toString(),"to")) return null;
    	return getDataRootElement().getAttributeNS(Constants.XLinkNamespace.toString(),"to");
    }
    
    /**
     * @see Arc#getArcrole()
     */
    public String getArcrole() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttributeNS(Constants.XLinkNamespace,"arcrole")) return null;
    	return getDataRootElement().getAttributeNS(Constants.XLinkNamespace,"arcrole");
    }
    
    /**
     * @see Arc#getOrder()
     */
    public Double getOrder() throws XBRLException {
    	Element root = getDataRootElement();
    	if (! root.hasAttribute("order")) return new Double(1);
    	String value = getDataRootElement().getAttribute("order").trim();
    	try {
    	    return new Double(value);    
    	} catch (Exception e) {
    	    throw new XBRLException("The arc order '" + value + "' is not a valid decimal value.",e);
    	}
    }
    
    /**
     * @see Arc#getPriority()
     */
    public Integer getPriority() throws XBRLException {
        Element root = getDataRootElement();
        if (! root.hasAttribute("priority")) return new Integer(1);
        return new Integer(getDataRootElement().getAttribute("priority"));    
    }
    
    
    /**
     * @see Arc#getUse()
     */
    public String getUse() throws XBRLException {
        Element root = getDataRootElement();
        if (! root.hasAttribute("use")) return "optional";
        return getDataRootElement().getAttribute("use");    
    }
    
    /**
     * @see Arc#isProhibited()
     */
    public boolean isProhibited() throws XBRLException {
        return (getUse().equals("prohibited"));
    }    
    
    /**
     * @see Arc#getSourceFragments()
     */
    public <E extends ArcEnd> List<E> getSourceFragments() throws XBRLException {
        long start = System.currentTimeMillis();
        String query = "#roots#[@parentIndex='" + this.getParentIndex() + "' and */*/@xlink:label='" + this.getFrom() + "']";
        List<E> result = this.getStore().<E>queryForXMLResources(query);
        logger.debug("MS to get source fragments = " + (System.currentTimeMillis()-start));
        return result;
    }
    
    /**
     * @see Arc#getTargetFragments()
     */
    public <E extends ArcEnd> List<E> getTargetFragments() throws XBRLException {
        long start = System.currentTimeMillis();
        String query = "#roots#[@parentIndex='" + this.getParentIndex() + "' and */*/@xlink:label='" + this.getTo() + "']";
        List<E> result = this.getStore().<E>queryForXMLResources(query);
        logger.debug("MS to get target fragments = " + (System.currentTimeMillis()-start));
        return result;
    }
    
    /**
     * TODO Add in PSVI information re default and fixed attribute values.
     * @see Arc#getSemanticAttributes()
     */
    public NamedNodeMap getSemanticAttributes() throws XBRLException {

    	// Clone the node to stop the attribute removal from impacting on the XML.
    	NamedNodeMap attributes = this.getDataRootElement().cloneNode(true).getAttributes();
    	
    	List<Node> badNodes = new Vector<Node>();
    	for (int i=0; i< attributes.getLength(); i++) {
    	    Node attribute = attributes.item(i);
    	    String ns = attribute.getNamespaceURI();
            if (ns != null) {
                if (attribute.getNamespaceURI().equals(Constants.XLinkNamespace.toString())) {
                    badNodes.add(attribute);
                } else if (attribute.getNamespaceURI().equals(Constants.XMLNSNamespace.toString())) {
                    badNodes.add(attribute);
                }                
            } else {
        	    if (attribute.getNodeName().startsWith("xmlns:") || attribute.getNodeName().startsWith("xmlns=")) {
                    badNodes.add(attribute);
                } else if (attribute.getNodeName().equals("use") || attribute.getNodeName().equals("priority")) {
                    badNodes.add(attribute);
        	    }
            }
    	}

    	for (Node node: badNodes) {
            String ns = node.getNamespaceURI();
            if (ns == null)
                attributes.removeNamedItem(node.getNodeName());
            else 
                attributes.removeNamedItemNS(ns,node.getLocalName());
    	}
    	
        return attributes;
    	
	}

    /**
     * @see Arc#getSemanticKey()
     */
    public String getSemanticKey() throws XBRLException {
    	NamedNodeMap attributes = getSemanticAttributes();
    	SortedMap<String,String> map = new TreeMap<String,String>();
    	
    	// Handle the order attribute which takes a default value of 1 when not specified.
    	String order = getOrder().toString();
    	if (attributes.getNamedItem("order") != null) {
    		attributes.removeNamedItem("order");
    	}
    	map.put("order",order);

    	for (int i=0; i<attributes.getLength(); i++) {
    		Node node = attributes.item(i);
			String namespace = node.getNamespaceURI();
	    	String key = null;
    		if (namespace == null) {
        		key = node.getNodeName();
    		} else {
    			key = namespace + ":" + node.getLocalName();
    		}
    		map.put(key,node.getNodeValue());
    	}

    	String semanticKey = "";
    	while (! map.isEmpty()) {
    		String key = map.firstKey();
    		semanticKey += key + "=" + map.get(key) + " ";
    		map.remove(key);
    	}
    	
    	return semanticKey.trim();

    }    
    
    /**
     * @param other The other arc to compare against.
     * @return true if and only if the maps have the 
     * same attributes and the same attribute values.
     * @throws XBRLException
     */
    private boolean semanticAttributesEqual(Arc other) throws XBRLException {
    	NamedNodeMap a = getSemanticAttributes();
    	NamedNodeMap b = other.getSemanticAttributes();

    	// Handle the order attribute which takes a default value of 1 when not specified.
    	String aOrder = "1";
    	String bOrder = "1";
    	if (a.getNamedItem("order") != null) {
    		aOrder = a.getNamedItem("order").getNodeValue();
    		a.removeNamedItem("order");
    	}
    	if (b.getNamedItem("order") != null) { 
    		bOrder = b.getNamedItem("order").getNodeValue();
    		b.removeNamedItem("order");
    	}
    	if (! aOrder.equals(bOrder)) {
    		return false;
    	}
    	
    	for (int i=0; i<a.getLength(); i++) {
    		Node aNode = a.item(i);
			String aNamespace = aNode.getNamespaceURI();
    		if (aNamespace == null) {
    			String aName = aNode.getNodeName();
        		Node bNode = b.getNamedItem(aName);
        		if (bNode == null) { 
        			return false;
        		}
    			b.removeNamedItem(aName);
    		} else {
    			String aName = aNode.getLocalName();
        		Node bNode = b.getNamedItemNS(aNamespace,aName);
        		if (bNode == null) {
        			return false;
        		}
    			b.removeNamedItemNS(aNamespace,aName);
    		}
    	}
		if (b.getLength() == 0)  return true;
		
		return false;
    	
    }    
    
    /**
     * @see Arc#semanticEquals(Arc)
     */
    public boolean semanticEquals(Arc other) throws XBRLException {
    	
    	if (! this.getNamespace().equals(other.getNamespace())) return false;
    	if (! this.getLocalname().equals(other.getLocalname())) return false;

    	ExtendedLink aLink = this.getExtendedLink();
    	ExtendedLink bLink = other.getExtendedLink();
    	
    	if (! aLink.getNamespace().equals(bLink.getNamespace())) return false;
    	if (! aLink.getLocalname().equals(bLink.getLocalname())) return false;
    	if (! aLink.getLinkRole().equals(bLink.getLinkRole())) return false;
    	
    	if (! semanticAttributesEqual(other)) return false;
    	
    	return true;
    }

    /**
     * @see Arc#getPreferredLabelRole()
     */
    public String getPreferredLabelRole() throws XBRLException {
        String value = this.getMetaAttribute("preferredLabel");
        if (! this.getLocalname().equals("labelArc")) {
            return null;
        }
        if (! this.getNamespace().equals(Constants.XBRL21LinkNamespace)) {
            return null;
        }
        if (! this.getArcrole().equals(Constants.LabelArcrole)) {
            return null;
        }
        if (value == null) return Constants.StandardLabelRole;
    
        return this.getMetaAttribute("preferredLabel");
    }

    /**
     * @see Arc#getWeight()
     */
    public Double getWeight() throws XBRLException {
        String value = this.getAttribute("weight");
        if (! this.getLocalname().equals("calculationArc")) {
            return null;
        }
        if (! this.getNamespace().equals(Constants.XBRL21LinkNamespace)) {
            return null;
        }
        if (! this.getArcrole().equals(Constants.CalculationArcrole)) {
            return null;
        }
        if (value == null) throw new XBRLException("The weight attribute cannot be missing on summation-item relationships.");
        
        return new Double(value);
        
    }

}
