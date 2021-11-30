package org.xbrlapi.xlink;

/**
 * Default XLinkHandler implementation, does nothing for any of the events.
 * Extend this class to create your own XLinkHandler.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
import java.io.Serializable;

import org.xml.sax.Attributes;


public class XLinkHandlerDefaultImpl implements XLinkHandler, Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -6060131708671079013L;

    /**
	 * Default XLink handler constructor
	 */
	public XLinkHandlerDefaultImpl() {
		super();
	}
	
	/**
	 * @see XLinkHandler#startSimpleLink(String, String, String, Attributes, String, String, String, String, String, String)
	 */
	public void startSimpleLink(String namespaceURI, String lName,
			String qName, Attributes attrs, String href, String role,
			String arcrole, String title, String show, String actuate)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endSimpleLink(String, String, String)
	 */
	public void endSimpleLink(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#startTitle(String, String, String, Attributes)
	 */
	public void startTitle(String namespaceURI, String lName, String qName,
			Attributes attrs) throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endTitle(String, String, String)
	 */
	public void endTitle(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#titleCharacters(char[], int, int)
	 */
	public void titleCharacters(char[] buf, int offset, int len)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#startExtendedLink(String, String, String, Attributes, String, String)
	 */
	public void startExtendedLink(String namespaceURI, String lName,
			String qName, Attributes attrs, String role, String title)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endExtendedLink(String, String, String)
	 */
	public void endExtendedLink(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#startResource(String, String, String, Attributes, String, String, String)
	 */
	public void startResource(String namespaceURI, String lName, String qName,
			Attributes attrs, String role, String title, String label)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endResource(String, String, String)
	 */
	public void endResource(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#startLocator(String, String, String, Attributes, String, String, String, String)
	 */
	public void startLocator(String namespaceURI, String lName, String qName,
			Attributes attrs, String href, String role, String title,
			String label) throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endLocator(String, String, String)
	 */
	public void endLocator(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#startArc(String, String, String, Attributes, String, String, String, String, String, String)
	 */
	public void startArc(String namespaceURI, String lName, String qName,
			Attributes attrs, String from, String to, String arcrole,
			String title, String show, String actuate) throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#endArc(String, String, String)
	 */
	public void endArc(String namespaceURI, String sName, String qName)
			throws XLinkException {
		;
	}
	
	/**
	 * @see XLinkHandler#xmlBaseStart(String)
	 */
	public void xmlBaseStart(String value) throws XLinkException {
		;
	}

	/**
	 * @see XLinkHandler#xmlBaseEnd()
	 */
	public void xmlBaseEnd() throws XLinkException {
		;
	}
	
	/**
	 * Default error behaviour is to throw an XLink Exception
	 * @see XLinkHandler#error(String, String, String, Attributes, String)
	 */
	public void error(String namespaceURI, String lName, String qName,
			Attributes attrs,String message) throws XLinkException {
		throw new XLinkException(message);
	}
	
	/**
	 * Default warning behaviour is to ignore the warning
	 * @see XLinkHandler#warning(String, String, String, Attributes, String)
	 */
	public void warning(String namespaceURI, String lName, String qName,
			Attributes attrs,String message) throws XLinkException {
		;
	}
	
    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }
	
}
