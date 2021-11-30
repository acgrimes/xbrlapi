package org.xbrlapi.aspects;


import javax.xml.datatype.XMLGregorianCalendar;

import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the period aspect that buckets up period
 * values based on the quarter that they end in or occur in.
 * The quarters are:
 * </p>
 * <ol>
 * <li>January-March</li>
 * <li>April-June</li>
 * <li>July-September</li>
 * <li>October-December</li>
 * </ol>
 * 
 * <p>
 * It is also limited in that it ignores the locale parameter and just
 * gives period aspect labels and period aspect value labels in English.
 * </p>
 * 
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class PeriodLabellerQuarters extends LabellerImpl implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -7514872059852959452L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public PeriodLabellerQuarters(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(PeriodAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + PeriodAspect.ID);
    }

    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return "quarter";
    }    
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale, String resourceRole, String linkRole) {
        
        try {
            PeriodAspectValue v = (PeriodAspectValue) value;
            if (v.isForever()) return "forever";
            XMLGregorianCalendar end = v.getEnd();
            if (! v.isFiniteDuration()) end = v.getInstant();
            int year = end.getYear();
            int quarter = 1 + (end.getMonth() / 3);
            return "" + year + ":Q" + quarter;


        } catch (Throwable e) {
            logger.warn(e.getMessage());
            return null;
        }
        
    }

}
