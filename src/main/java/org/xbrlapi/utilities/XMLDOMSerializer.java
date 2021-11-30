package org.xbrlapi.utilities;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLDOMSerializer {

    private static Logger logger = Logger.getLogger(XMLDOMSerializer.class);

    public static void serialize(Node what, File destination) throws FileNotFoundException, XBRLException {
        serialize(what,new FileOutputStream(destination));
    }
    
    public static String serialize(Node what) throws XBRLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(what, baos);
        return baos.toString();
    }

    public static void serialize(Node what, OutputStream outputStream) throws XBRLException {
    	try {
        	if (what.getNodeType() == Node.TEXT_NODE) {
                OutputStreamWriter osw = new OutputStreamWriter(outputStream, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(what.getTextContent());
                bw.flush();
                bw.close();
            } else {    	
    	        OutputFormat format = new OutputFormat(what.getOwnerDocument(), "UTF-8", true);
    	        format.setOmitXMLDeclaration(true);
                XMLSerializer output = new org.apache.xml.serialize.XMLSerializer(outputStream, format);
                output.setNamespaces(true);
    	        if (what.getNodeType() == Node.DOCUMENT_NODE) {
    	            output.serialize(((Document) what).getDocumentElement());
    	        } else if (what.getNodeType() == Node.ELEMENT_NODE) {
    	            output.serialize((Element) what);
    	        }
            }
    	} catch (IOException e) {
    		throw new XBRLException("The node could not be serialized.",e);
    	}

/*    	
		DOMImplementation domImplementation = null;
		if (what.getNodeType() == Node.DOCUMENT_NODE) {
			domImplementation = ((Document) what).getImplementation();
		} else {
			domImplementation = what.getOwnerDocument().getImplementation();
		}
		logger.info(domImplementation.getClass().getName());
		if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
			LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
			DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
			
			if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
				lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
				LSOutput lsOutput = domImplementationLS.createLSOutput();
				lsOutput.setEncoding("UTF-8");
				lsOutput.setByteStream(outputStream);
				lsSerializer.write(what, lsOutput);
			} else {
				throw new XBRLException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
			}
		 } else {
			 throw new XBRLException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
		 }*/
    }

}
