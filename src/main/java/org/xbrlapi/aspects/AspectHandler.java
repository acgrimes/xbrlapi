package org.xbrlapi.aspects;


import org.xbrlapi.utilities.XBRLException;

public interface AspectHandler {

    /**
     * @return the aspect itself
     * @throws XBRLException if the aspect cannot be retrieved.
     */
    public String getAspectId() throws XBRLException;
}
