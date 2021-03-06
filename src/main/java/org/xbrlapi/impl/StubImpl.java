package org.xbrlapi.impl;

/**
 * A stub XML resource in the database used to 
 * store information about documents that have not
 * loaded yet or correctly.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

import java.net.URI;
import java.net.URISyntaxException;

import org.xbrlapi.Stub;
import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.utilities.XBRLException;

public class StubImpl extends NonFragmentXMLImpl implements Stub {
	
	/**
     * 
     */
    private static final long serialVersionUID = 4672320267418877015L;

    /**
	 * No argument constructor.
	 * @throws XBRLException
	 */
	public StubImpl() throws XBRLException {
		super();
	}
	
	/**
	 * @param id The unique id of the fragment being created,
	 * within the scope of the containing data store.
	 * @throws XBRLException
	 */
	public StubImpl(String id, URI uri, String reason) throws XBRLException {
		this();
        setBuilder(new BuilderImpl());
        if (id == null) throw new XBRLException("The stub index must not be null.");
		this.setIndex(id);
		if (uri == null) throw new XBRLException("The stub URI must not be null.");
		this.setResourceURI(uri);
        if (reason == null) throw new XBRLException("The reason must not be null.");		
        this.setReason(reason);
        
        // Up to here all of the properties have been stored in an XML DOM being
        // put together by the builder.
        
		this.finalizeBuilder();
	}

    /**
     * @see Stub#getReason()
     */
    public String getReason() throws XBRLException {
        return this.getMetaAttribute("reason"); 
    }

    /**
     * @see Stub#getResourceURI()
     */
    public URI getResourceURI() throws XBRLException {
        String uri = "";
        try {
            uri  = this.getMetaAttribute("resourceURI");
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new XBRLException(" URI: " + uri + " has incorrect syntax .", e);
        }
    }
    
    /**
     * @see Stub#setResourceURI(URI)
     */
    public void setResourceURI(URI uri) throws XBRLException {
        if (uri == null) throw new XBRLException("The stub URI must not be null.");
        this.setMetaAttribute("resourceURI",uri.toString());
    }
    
    /**
     * @see Stub#setReason(String)
     */
    public void setReason(String reason) throws XBRLException {
        if (reason == null) throw new XBRLException("The reason must not be null.");
        this.setMetaAttribute("reason",reason);
    }    
	
	
	
}
