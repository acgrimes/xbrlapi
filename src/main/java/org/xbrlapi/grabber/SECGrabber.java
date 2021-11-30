package org.xbrlapi.grabber;

import java.net.URI;


/**
 * Defines an SEC XBRL grabber that exposes a lot more
 * information than XBRL resource URIs from an SEC RSS feed.
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public interface SECGrabber extends Grabber {

    /**
     * @param resource The URI to test for.
     * @return true if the SEC RSS feed contains
     * a reference to an XBRL instance with the specified URI
     * and false otherwise.
     */
    public boolean hasInstance(URI resource);
    
    /**
     * @param resource The URI of the XBRL filing.
     * @return the name of the filing entity.
     */
    public String getEntityName(URI resource);
    
    /**
     * @param resource The URI of the XBRL filing.
     * @return the CIK code of the filing entity,
     * without any leading or trailing spaces.
     * The CIK code is used as the entity identifier
     * in the XBRL filings.
     */
    public String getCIK(URI resource);

    /**
     * @param resource The URI of the XBRL filing.
     * @return the type of filing.
     */
    public String getFormType(URI resource);

    /**
     * @param resource The URI of the XBRL filing.
     * @return the URI of the corresponding web page.
     */
    public URI getWebpage(URI resource);

    /**
     * @param resource The URI of the XBRL filing.
     * @return the URI of the corresponding web page in 
     * YYYY-MM-DD format.
     */
    public String getFilingDate(URI resource);
    
    /**
     * @param resource The URI of the XBRL filing.
     * @return the filing period in YYYY-MM-DD format.
     */
    public String getPeriod(URI resource);

}
