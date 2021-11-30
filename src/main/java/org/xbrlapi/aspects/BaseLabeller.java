package org.xbrlapi.aspects;


import org.apache.log4j.Logger;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class BaseLabeller extends LabellerImpl implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -5977720545872502374L;
    protected final static Logger logger = Logger.getLogger(BaseLabeller.class);

    public BaseLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
    }

    /**
     * @see Labeller#getAspectValueLabel(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabel(AspectValue value, String locale, String resourceRole, String linkRole) {
        try {
            String label = this.getAspectValueLabelWithoutFallback(value, locale,resourceRole, linkRole);
            if (label != null) return label;
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return super.getAspectValueLabel(value,locale,resourceRole,linkRole);
    }


    
    
}
