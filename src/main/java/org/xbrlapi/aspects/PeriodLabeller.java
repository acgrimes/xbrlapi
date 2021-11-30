package org.xbrlapi.aspects;


import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the period aspect.  This labeller ignores 
 * link role and resource role values.
 * </p>
 * 
 * <p>
 * It is also limited in that it ignores the locale parameter and just
 * gives period aspect labels and period aspect value labels in English.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class PeriodLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = 3865956219152605139L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public PeriodLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(PeriodAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + PeriodAspect.ID);
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
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
            
            if (v.isMissing()) return "missing";
            
            if (v.isForever()) return "forever";

            if (v.isFiniteDuration()) {
                
                String endLabel = v.getRawEnd();
                logger.info(v.instantIsDateOnly());
                if (! v.endIsDateOnly()) endLabel = v.getEnd().toXMLFormat();
                if (v.isFiniteDuration()) {
                    String startLabel = v.getRawStart();
                    if (! v.startIsDateOnly()) startLabel = v.getStart().toXMLFormat();
                    return startLabel + " - " + endLabel;
                } 
                if (v.endIsDateOnly()) return v.getRawEnd();
                return v.getEnd().toXMLFormat();
            } 
            
            String instLabel = v.getRawInstant();
            if (! v.instantIsDateOnly()) instLabel = v.getInstant().toXMLFormat();
            return instLabel;
            
        } catch (Throwable e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            return null;
        }
        
    }

}
