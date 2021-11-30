package org.xbrlapi.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xbrlapi.Schema;
import org.xbrlapi.SchemaContent;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SchemaContentImpl extends FragmentImpl implements SchemaContent {

    /**
     * 
     */
    private static final long serialVersionUID = 1132807733882600038L;

    /**
     * @see SchemaContent#getSchema()
     */
    public Schema getSchema() throws XBRLException {
    	return (Schema) getAncestorOrSelf("org.xbrlapi.impl.SchemaImpl");
    }
    
    /**
     * @see SchemaContent#getTargetNamespace()
     */
    public String getTargetNamespace() throws XBRLException {
    	Schema s = getSchema();
    	Element e = s.getDataRootElement();
    	if (e.hasAttribute("targetNamespace")) return e.getAttribute("targetNamespace");
    	return null;
    }
    
    /**
     * @see SchemaContent#getAnnotations()
     */
    public List<Element> getAnnotations() throws XBRLException {
        List<Element> result = new Vector<Element>();
        NodeList nodes = this.getDataRootElement().getElementsByTagNameNS(Constants.XMLSchemaNamespace.toString(),"annotation");
        for (int i=0; i<nodes.getLength(); i++) {
            result.add((Element) nodes.item(i));
        }
        return result;
    }    

    /**
     * @see SchemaContent#hasOtherAttribute(String,String)
     * @see Element#hasAttributeNS(String, String)
     */
    public boolean hasOtherAttribute(String namespace, String localname) throws XBRLException {
        return getDataRootElement().hasAttributeNS(namespace.toString(), localname);
    }
    
    /**
     * @see SchemaContent#getOtherAttributes()
     */
    public LinkedList<Node> getOtherAttributes() throws XBRLException {
        NamedNodeMap attributes = getDataRootElement().getAttributes();
        LinkedList<Node> otherAttributes = new LinkedList<Node>();
        for (int i=0; i<attributes.getLength(); i++) {
            String ns = attributes.item(i).getNamespaceURI();
            if (! ns.equals(Constants.XMLSchemaNamespace.toString()) && ! ns.equals(Constants.XBRL21Namespace.toString())) {
                otherAttributes.add(attributes.item(i));
            }
        }
        return otherAttributes;
    }    
    
    /**
     * @see Element#getAttributeNS(String, String)
     * @see SchemaContent#getOtherAttribute(String,String)
     */
    public String getOtherAttribute(String namespace, String localname) throws XBRLException {
        if (this.hasOtherAttribute(namespace, localname)) 
            return getDataRootElement().getAttributeNS(namespace.toString(), localname);
        return null;
    }
    
    /**
     *  @see org.xbrlapi.SchemaDeclaration#getSchemaId()
     */
    public String getSchemaId() throws XBRLException {
        if (! getDataRootElement().hasAttributeNS(Constants.XMLSchemaNamespace.toString(),"id")) return null;
        return getDataRootElement().getAttributeNS(Constants.XMLSchemaNamespace.toString(),"id");
    }    
    
}