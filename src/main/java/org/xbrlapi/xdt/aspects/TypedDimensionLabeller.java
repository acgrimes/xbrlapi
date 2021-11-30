package org.xbrlapi.xdt.aspects;

import java.util.List;

import org.xbrlapi.LabelResource;
import org.xbrlapi.SchemaContent;
import org.xbrlapi.aspects.Aspect;
import org.xbrlapi.aspects.BaseLabeller;
import org.xbrlapi.aspects.Labeller;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the typed dimension aspect based upon XBRL labels and generic XBRL labels.
 * </p>
 * 
 * <p>
 * This labeller does not make use of label caching systems.  Instead labels are obtained from
 * the XLink relationships from typed dimensions to XBRL 2.1 and generic labels.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class TypedDimensionLabeller extends BaseLabeller implements Labeller {

    /**
     * 
     */
    private static final long serialVersionUID = 3050503522357576606L;

    /**
     * @param aspect The aspect to be a labeller for.
     */
    public TypedDimensionLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        try {
            TypedDimensionAspect a = (TypedDimensionAspect) aspect;
        } catch (Throwable e) {
            throw new XBRLException("This labeller only works for typed dimension aspects.");
        }
        
    }

    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        TypedDimensionAspect aspect = (TypedDimensionAspect) getAspect();
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
        

}
