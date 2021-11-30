package org.xbrlapi;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.xbrlapi.utilities.XBRLException;

/**
 * 
 * This interface defines the functions that can be used to interact with a data
 * store XML resource that summarises the information expressed by an XLink
 * network relationship graph. The nodes of the graph are the fragments
 * connected by the XLink relationships.
 * Each node in graph summary has a name that is the index of the node fragment,
 * prefixed by an underscore '_'.
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */

public interface NetworkGraph extends NonFragmentXML {

    /**
     * @return the set of root elements in the graph. A root is defined
     *         as a node that has no relationships to it from other nodes.
     * @throws XBRLException
     */
    public Set<Element> getRoots() throws XBRLException;

    /**
     * @return the custom type declaration for the XLink extended link role
     *         defining the graph.
     * @throws XBRLException
     */
    public RoleType getCustomLinkRole() throws XBRLException;

    /**
     * @return the custom type declaration for the XLink arcrole defining the
     *         graph.
     * @throws XBRLException
     */
    public ArcroleType getCustomArcrole() throws XBRLException;

    /**
     * @return the index of the XLink extended link role defining the graph.
     * @throws XBRLException
     */
    public String getLinkRoleIndex() throws XBRLException;

    /**
     * @return the index of the Link arcrole defining the graph.
     * @throws XBRLException
     */
    public String getArcroleIndex() throws XBRLException;

    /**
     * @return the XLink extended link role defining the graph.
     * @throws XBRLException
     */
    public String getLinkRole() throws XBRLException;

    /**
     * @return the XLink arcrole defining the graph.
     * @throws XBRLException
     */
    public String getArcrole() throws XBRLException;

    /**
     * @param parent
     *            The element in the graph resource representing the parent.
     * @return the set elements in the graph resource representing the children.
     *         The children are ordered by the order attribute on the arcs
     *         expressing the relationships. Where the order attribute is
     *         identical for two relationships, the child index ordering cannot
     *         be relied upon to be consistent.
     * @throws XBRLException
     */
    public List<Element> getChildren(Element parent) throws XBRLException;

    /**
     * @param child
     *            The index of the child fragment.
     * @return the index of the child fragment's parent or null if the child
     *         fragment is a root.
     * @throws XBRLException
     */
    public Element getParent(Element child) throws XBRLException;


}
