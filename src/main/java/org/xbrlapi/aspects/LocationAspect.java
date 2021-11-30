package org.xbrlapi.aspects;

import org.apache.log4j.Logger;
import org.xbrlapi.Fact;
import org.xbrlapi.Fragment;
import org.xbrlapi.impl.InstanceImpl;
import org.xbrlapi.utilities.XBRLException;

/**
 * <h2>Location aspect details</h2>
 * 
 * <p>
 * All facts have a value for the location aspect.  The location aspect reflects 
 * the containers of the fact.  This includes the containing XBRL instance and
 * any tuple ancestors of the fact.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class LocationAspect extends AspectImpl implements Aspect {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7841916801129980666L;

    private static final Logger logger = Logger.getLogger(LocationAspect.class);

    /**
     * The URI uniquely identifying this concept aspect.
     */
    public static String ID = "http://xbrlapi.org/aspect/location/1.0";

    /**
     * @see Aspect#getId()
     */
    public String getId() {
        return ID;
    }
    
    /**
     * @param domain The domain for this aspect.
     * @throws XBRLException
     */
    public LocationAspect(Domain domain) throws XBRLException {
        super(domain);
    }
    
    /**
     * @see Aspect#getValue(Fact)
     */
    public LocationAspectValue getValue(Fact fact) throws XBRLException {
        Fragment parent = fact.getParent();
        return getValue(fact, parent);
    }

    /**
     * @param fact The fact
     * @param parent The parent fragment of the fact
     * @return The location aspect value.
     * @throws XBRLException
     */
    public LocationAspectValue getValue(Fact fact, Fragment parent) throws XBRLException {
        if (parent.isa(InstanceImpl.class)) return new LocationAspectValue(fact.getIndex());
        return new LocationAspectValue(parent.getIndex(),fact.getIndex());
    }

    /**
     * @see Aspect#getMissingValue()
     */
    public LocationAspectValue getMissingValue() {
        return new LocationAspectValue();
    }

}
