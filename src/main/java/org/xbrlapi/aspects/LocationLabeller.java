package org.xbrlapi.aspects;

import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.Fact;
import org.xbrlapi.LabelResource;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the location aspect based upon generic XBRL labels. The label
 * for a location aspect value is the XBRL 2.1 or XBRL generic label for the
 * concept underpinning the fact that is at the location associated with the
 * location aspect value.
 * </p>
 * 
 * <p>
 * This labeller does not make use of label caching systems. XBRL 2.1 and
 * generic XBRL labels are used where possible. Otherwise, the fact's namespace
 * and local name are used.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class LocationLabeller extends LabellerImpl implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -5874874175254475089L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public LocationLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(LocationAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + LocationAspect.ID);
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return "location";
    }

    /**
     * @see LabellerImpl#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabelWithoutFallback(AspectValue value,
            String locale, String resourceRole, String linkRole) {
        try {
            LocationAspectValue v = (LocationAspectValue) value;
            if (v.isRootLocation()) return "report";
            Fact fact = getStore().<Fact>getXMLResource(v.getParentFactIndex());
            Concept concept = fact.getConcept();
            List<LabelResource> labels = concept.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole, linkRole);
            if (! labels.isEmpty()) return labels.get(0).getStringValue();
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

}
