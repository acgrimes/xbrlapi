package org.xbrlapi.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xbrlapi.ArcroleType;
import org.xbrlapi.NetworkGraph;
import org.xbrlapi.Relationship;
import org.xbrlapi.RoleType;
import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.networks.Network;
import org.xbrlapi.utilities.XBRLException;


/**
 * A network graph XML resource stores information about
 * a graph of XLink relationships between XML fragments.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class NetworkGraphImpl extends NonFragmentXMLImpl implements NetworkGraph {
    
    /**
     * 
     */
    private static final long serialVersionUID = -2784004296852207174L;
    
    private static final Logger logger = Logger.getLogger(NetworkGraphImpl.class);
    
    /**
	 * No argument constructor.
	 * @throws XBRLException
	 */
	public NetworkGraphImpl() throws XBRLException {
		super();
	}
	
	
	/**
	 * The network graph XML resource index is made up from the linkRole
	 * and the arcrole fragment indices.
	 * @param linkRole The custom link role type declaration fragment.
	 * @param arcrole The custom arcrole type declaration fragment.
	 * @throws XBRLException
	 */
	public NetworkGraphImpl(RoleType linkRole, ArcroleType arcrole) throws XBRLException {
		this();
        if (linkRole == null) throw new XBRLException("The network link role must not be null.");
        if (arcrole == null) throw new XBRLException("The network arcrole must not be null.");
        setBuilder(new BuilderImpl());
        setStore(linkRole.getStore());

        this.setIndex(linkRole.getIndex() + "_" + arcrole.getIndex());
		
        this.setArcroleIndex(arcrole.getIndex());
        this.setArcrole(arcrole.getCustomURI());

        this.setLinkRoleIndex(linkRole.getIndex());
        this.setLinkRole(linkRole.getCustomURI());
        
        this.finalizeBuilder();

        Network network = getStore().getNetworks(getLinkRole(),getArcrole()).getNetwork(getLinkRole(),getArcrole());

        Document document = getMetadataRootElement().getOwnerDocument();
        Element insertionPoint = document.createElement("graph");
        getMetadataRootElement().appendChild(insertionPoint);
        insertChildren(insertionPoint, network);

	}
	
	/**
	 * @return the element that is the parent of the elements describing the graph.
	 */
    private Element getGraphElement() {
        NodeList nodes = this.getMetadataRootElement().getElementsByTagName("graph");
        return (Element) nodes.item(0);
    }

	private void insertChildren(Element insertionPoint, Network network) throws XBRLException {
	    
	    Document document = insertionPoint.getOwnerDocument();
	    if (insertionPoint.getLocalName().equals("graph")) {
	        for (String rootIndex: network.getRootFragmentIndices()) {
	            Element node = document.createElement("_" + rootIndex);
	            insertionPoint.appendChild(node);
	            insertChildren(node,network);
	        }
	    } else {
	        for (Relationship relationship: network.getActiveRelationshipsFrom(insertionPoint.getLocalName().substring(1))) {
                Element node = document.createElement("_" + relationship.getTargetIndex());
                insertionPoint.appendChild(node);
                insertChildren(node,network);
	        }
	    }
	    
	}

    /**
     * @see NetworkGraph#getLinkRole()
     */
    public String getLinkRole() throws XBRLException {
        return this.getMetaAttribute("linkRole");
    }
    
    private void setLinkRole(String linkRole) throws XBRLException {
        if (linkRole == null) throw new XBRLException("The link role must not be null.");
        setMetaAttribute("linkRole",linkRole.toString());
    }
    
    /**
     * @see NetworkGraph#getLinkRoleIndex()
     */
    public String getLinkRoleIndex() throws XBRLException {
        return this.getMetaAttribute("linkRoleIndex");
    }
    
    private void setLinkRoleIndex(String index) throws XBRLException {
        if (index == null) throw new XBRLException("The link role index must not be null.");
        setMetaAttribute("linkRoleIndex",index);
    }    
    
    /**
     * @see NetworkGraph#getCustomLinkRole()
     */
    public RoleType getCustomLinkRole() throws XBRLException {
        return getStore().<RoleType>getXMLResource(this.getLinkRoleIndex());
    }

    /**
     * @see NetworkGraph#getArcrole()
     */
    public String getArcrole() throws XBRLException {
        String value = this.getMetaAttribute("arcrole");
        return value;
    }
    
    private void setArcrole(String arcrole) throws XBRLException {
        if (arcrole == null) throw new XBRLException("The arcrole must not be null.");
        setMetaAttribute("arcrole",arcrole.toString());
    }
    
    /**
     * @see NetworkGraph#getArcroleIndex()
     */
    public String getArcroleIndex() throws XBRLException {
        return this.getMetaAttribute("arcroleIndex");
    }
    
    private void setArcroleIndex(String index) throws XBRLException {
        if (index == null) throw new XBRLException("The arcrole index must not be null.");
        setMetaAttribute("arcroleIndex",index);
    }    

    /**
     * @see NetworkGraph#getCustomArcrole()
     */
    public ArcroleType getCustomArcrole() throws XBRLException {
        return getStore().<ArcroleType>getXMLResource(this.getArcroleIndex());
    }

    /**
     * @see NetworkGraph#getChildren(Element)
     */
    public List<Element> getChildren(Element parent)
            throws XBRLException {
        List<Element> result = new Vector<Element>();
        NodeList nodes = parent.getChildNodes();
        if (nodes.getLength() == 0) return result;
        for (int i=0; i<nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                result.add((Element) node);
        }
        return result;
    }

    /**
     * @see NetworkGraph#getParent(Element)
     */
    public Element getParent(Element child) throws XBRLException {
        Element parent = (Element) child.getParentNode();
        if (parent.getLocalName().equals("graph")) return null;
        return parent;
    }

    /**
     * @see NetworkGraph#getRoots()
     */
    public Set<Element> getRoots() throws XBRLException {
        Set<Element> result = new HashSet<Element>();
        NodeList nodes = getGraphElement().getChildNodes();
        for (int i=0; i<nodes.getLength(); i++) {
            result.add((Element) nodes.item(i));
        }
        return result;
    }
}
