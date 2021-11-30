package org.xbrlapi.grabber;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of an XBRL document URI grabber for the SEC
 * RSS feed.
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class SecGrabberImpl extends AbstractGrabberImpl implements SECGrabber {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Defines the SEC resource that describes a single filing recorded in the SEC RSS feed.
	 */
	private class SECResource {
	    
	    private URI uri;
	    private String entityName = null;
        private String CIK = null;
        private String formType = null;
	    private URI webpage = null;
	    private String filingDate = null;
	    private String period = null;
	    
        public SECResource(URI uri) {
            super();
            this.uri = uri;
        }
        public URI getUri() {
            return uri;
        }
        public String getEntityName() {
            return entityName;
        }
        public String getCIK() {
            return CIK;
        }
        public String getFormType() {
            return formType;
        }
        public URI getWebpage() {
            return webpage;
        }
        public String getFilingDate() {
            return filingDate;
        }
        public String getPeriod() {
            return period;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }
        public void setCIK(String cIK) {
            CIK = cIK;
        }
        public void setFormType(String formType) {
            this.formType = formType;
        }
        public void setWebpage(URI webpage) {
            this.webpage = webpage;
        }
        public void setFilingDate(String filingDate) {
            this.filingDate = filingDate.substring(6) + "-" + filingDate.substring(0,2) + "-" + filingDate.substring(3,5);
        }
        public void setPeriod(String period) {
            this.period = period.substring(0,4) + "-" + period.substring(4,6) + "-" + period.substring(6);
        }

	}
	
	public SecGrabberImpl(URI source) {
		setSource(source);
		parse();
	}

	/**
	 * The SEC RSS feed URI.
	 */
	URI source;	
	private void setSource(URI source) {this.source = source; }
	private URI getSource() { return source; }

	/**
	 * The result of parsing the SEC feed.
	 */
	private Map<URI,SECResource> secResources;
	
	private static final String EDGAR_NAMESPACE = "http://www.sec.gov/Archives/edgar";
	
	/**
	 * Builds a map from filing instance URI to SEC resource summary information.
	 */
    private void parse() {
        secResources = new HashMap<URI,SECResource>();

        Document feed = getDocument(getSource());
        NodeList nodes = feed.getElementsByTagNameNS(EDGAR_NAMESPACE,"xbrlFile");

        logger.info("# XBRL files = " + nodes.getLength());
        
        LOOP: for (int i=0; i<nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String type = element.getAttributeNS(EDGAR_NAMESPACE, "type");
            String uri = element.getAttributeNS(EDGAR_NAMESPACE, "url");
            if (! (type.equals("EX-100.INS") || type.equals("EX-101.INS"))) {
                logger.debug("Skipping " + uri);
                continue LOOP;// Only interested in XBRL instances as entry points.
            }
            if (
                    (uri != null) &&
                    (
                        (uri.endsWith(".xml")) 
                        || (uri.endsWith(".xbrl"))
                    )
                ) {
                try {
                    URI key = new URI(uri);
                    SECResource secResource = getSECResource(key, element);
                    secResources.put(key, secResource);
                } catch (URISyntaxException e) {
                    logger.warn("SEC source URI: " + uri + " is malformed and has been ignored.");
                }
            }
        }
        logger.info("# URIs in SEC feed = " + secResources.size());        
    }
	
    /**
     * @param uri The URI of the XBRL instance that has been filed with the SEC.
     * @param xbrlFile The Element containing the URI of the XBRL instance, in the RSS feed.
     * @return the SEC resource object that summarises the filing.
     */
    private SECResource getSECResource(URI uri, Element xbrlFile) {

        SECResource secResource = new SECResource(uri);

        Element xbrlFiling = (Element) xbrlFile.getParentNode().getParentNode();
        
        NodeList nodes = xbrlFiling.getElementsByTagNameNS(EDGAR_NAMESPACE,"companyName");
        if (nodes.getLength() == 1)
            secResource.setEntityName(((Element) nodes.item(0)).getTextContent());

        nodes = xbrlFiling.getElementsByTagNameNS(EDGAR_NAMESPACE,"cikNumber");
        if (nodes.getLength() == 1)
            secResource.setCIK(((Element) nodes.item(0)).getTextContent());

        nodes = xbrlFiling.getElementsByTagNameNS(EDGAR_NAMESPACE,"formType");
        if (nodes.getLength() == 1)
            secResource.setFormType(((Element) nodes.item(0)).getTextContent());

        nodes = xbrlFiling.getElementsByTagNameNS(EDGAR_NAMESPACE,"period");
        if (nodes.getLength() == 1)
            secResource.setPeriod(((Element) nodes.item(0)).getTextContent());
        
        nodes = xbrlFiling.getElementsByTagNameNS(EDGAR_NAMESPACE,"filingDate");
        if (nodes.getLength() == 1)
            secResource.setFilingDate(((Element) nodes.item(0)).getTextContent());
        
        Element item = (Element) xbrlFiling.getParentNode();
        nodes = item.getElementsByTagName("link");
        if (nodes.getLength() == 1)
            secResource.setWebpage(URI.create(((Element) nodes.item(0)).getTextContent()));
        
        return secResource;
    }
	
	/**
	 * @see Grabber#getResources()
	 */
	public List<URI> getResources() {
		return new ArrayList<URI>(secResources.keySet());
	}
    /**
     * @see SECGrabber#getCIK(URI)
     */
    public String getCIK(URI resource) {
        return secResources.get(resource).getCIK();
    }
    /**
     * @see SECGrabber#getEntityName(URI)
     */
    public String getEntityName(URI resource) {
        return secResources.get(resource).getEntityName();
        
    }
    /**
     * @see SECGrabber#getFilingDate(URI)
     */
    public String getFilingDate(URI resource) {
        return secResources.get(resource).getFilingDate();
    }
    /**
     * @see SECGrabber#getFormType(URI)
     */
    public String getFormType(URI resource) {
        return secResources.get(resource).getFormType();
    }
    /**
     * @see SECGrabber#getPeriod(URI)
     */
    public String getPeriod(URI resource) {
        return secResources.get(resource).getPeriod();
    }
    /**
     * @see SECGrabber#getWebpage(URI)
     */
    public URI getWebpage(URI resource) {
        return secResources.get(resource).getWebpage();   
    }
    /**
     * @see SECGrabber#hasInstance(URI)
     */
    public boolean hasInstance(URI resource) {
        return this.secResources.containsKey(resource);
    }

}
