package org.xbrlapi.aspects;

import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.LabelResource;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the concept aspect based upon XBRL labels and generic XBRL labels.
 * </p>
 * 
 * <p>
 * This labeller does not make use of label caching systems.  Instead labels are obtained from
 * the XLink relationships from concepts to XBRL 2.1 and generic labels.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class ConceptLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -2179477289946077839L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public ConceptLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(ConceptAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + ConceptAspect.ID);
    }
    
    /**
     * @see LabellerImpl#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale,
            String resourceRole, String linkRole) {
        return "concept";
    }

    /**
     * @see Labeller#getAspectValueLabel(AspectValue, String, String, String)
     */
    public String getAspectValueLabel(AspectValue value, String locale, String resourceRole, String linkRole) {
        String label = this.getAspectValueLabelWithoutFallback(value, locale,resourceRole, linkRole);
        if (label != null) return label;
    	return super.getAspectValueLabelWithoutFallback(value, locale, resourceRole, linkRole);
    }
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale,
            String resourceRole, String linkRole) {
        
        try {
            ConceptAspectValue v = (ConceptAspectValue) value;
            Concept concept = getStore().getConcept(v.getNamespace(),v.getLocalname());
            List<LabelResource> labels = concept.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole, linkRole);
            if (! labels.isEmpty()) return labels.get(0).getStringValue();
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return null;
        
    }

}
