package org.xbrlapi.xdt.aspects;

import java.util.List;

import org.xbrlapi.LabelResource;
import org.xbrlapi.SchemaContent;
import org.xbrlapi.aspects.Aspect;
import org.xbrlapi.aspects.AspectValue;
import org.xbrlapi.aspects.BaseLabeller;
import org.xbrlapi.aspects.Labeller;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the explicit dimension aspect based upon XBRL labels and generic XBRL labels.
 * </p>
 * 
 * <p>
 * This labeller does not make use of label caching systems.  Instead labels are obtained from
 * the XLink relationships from explicit dimensions to XBRL 2.1 and generic labels.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class ExplicitDimensionLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = -2179477289946077839L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public ExplicitDimensionLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        try {
            ExplicitDimensionAspect a = (ExplicitDimensionAspect) aspect;
        } catch (Throwable e) {
            throw new XBRLException("This labeller only works for explicit dimension aspects.");
        }
        
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        ExplicitDimensionAspect aspect = (ExplicitDimensionAspect) getAspect();
        try {
            SchemaContent sc = getStore().getSchemaContent(aspect.getDimensionNamespace(),aspect.getDimensionLocalname());
            List<LabelResource> labels = sc.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole, linkRole);
            if (! labels.isEmpty()) return labels.get(0).getStringValue();
        } catch (XBRLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }
    
    /**
     * @see Labeller#getAspectLabel(String, String, String)
     */
    @Override
    public String getAspectLabel(String locale, String resourceRole, String linkRole) {
        String label = this.getAspectLabelWithoutFallback(locale, resourceRole, linkRole);
        if (label != null) return label;
        return super.getAspectLabel(locale,resourceRole,linkRole);
    }
    
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    @Override
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale,
            String resourceRole, String linkRole) {
        
        try {
            if (value.isMissing()) return "";
            ExplicitDimensionAspectValue v = (ExplicitDimensionAspectValue) value;
            SchemaContent sc = getStore().getSchemaContent(v.getNamespace(),v.getLocalname());
            List<LabelResource> labels = sc.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole, linkRole);
            if (! labels.isEmpty()) return labels.get(0).getStringValue();
        } catch (Throwable e) {
            logger.warn(e.getMessage());
        }
        return null;
        
    }

}
