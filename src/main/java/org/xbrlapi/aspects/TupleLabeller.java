package org.xbrlapi.aspects;


import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the tuple aspect.  This labeller ignores 
 * link role and resource role values.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class TupleLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = 3161673708249240447L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public TupleLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(TupleAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + TupleAspect.ID);
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return "tuple?";
    }
    
}
