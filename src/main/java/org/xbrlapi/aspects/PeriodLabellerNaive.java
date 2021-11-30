package org.xbrlapi.aspects;


import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the period aspect.  This labeller just reproduces
 * the lexical representation of the period start and end or the period instant,
 * as given in the XML.  It does not attempt to transform the period
 * values based on the rules for handling cases where the time component is omitted.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class PeriodLabellerNaive extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -3350666647416995000L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public PeriodLabellerNaive(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(PeriodAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + PeriodAspect.ID);
    }

    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return "period";
    }    
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale, String resourceRole, String linkRole) {
        
        try {
            
            PeriodAspectValue v = (PeriodAspectValue) value;
            if (v.isForever()) return "forever";
            if (v.isFiniteDuration()) return v.getRawStart() + " - " + v.getRawEnd();
            return v.getRawEnd();

        } catch (Throwable e) {
            logger.warn(e.getMessage());
            return null;
        }
        
    }

}
