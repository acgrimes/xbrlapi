package org.xbrlapi.sax;

import java.io.Serializable;
import java.net.URI;

import org.apache.xerces.xni.parser.XMLInputSource;

/**
 * General entity resolver interface that enforces serializability.
 * 
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface EntityResolver extends org.xml.sax.EntityResolver,
        org.apache.xerces.xni.parser.XMLEntityResolver, Serializable {

    /**
     * @param uri the URI to be resolved.
     * @return the XMLInputSource for the given URI.
     */
    public XMLInputSource resolveSchemaURI(URI uri);
    
}
