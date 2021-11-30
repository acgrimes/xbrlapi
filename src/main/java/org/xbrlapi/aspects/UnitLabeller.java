package org.xbrlapi.aspects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.xbrlapi.LabelResource;
import org.xbrlapi.Measure;
import org.xbrlapi.MeasureResource;
import org.xbrlapi.impl.MeasureResourceImpl;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * <p>
 * A labeller for the unit aspect.  This labeller attempts
 * to use generic labels for XLink Unit resources.
 * </p>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class UnitLabeller extends BaseLabeller implements Labeller {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6541770175584344223L;
    
    /**
     * @param aspect The aspect to be a labeller for.
     */
    public UnitLabeller(Aspect aspect) throws XBRLException {
        super(aspect);
        if (! aspect.getId().equals(UnitAspect.ID)) throw new XBRLException("This labeller only works for the aspect: " + UnitAspect.ID);
    }


    protected String getLabel(Measure measure,String locale,String resourceRole,String linkRole) throws XBRLException {
        
        String namespace = measure.getNamespace();
        String name = measure.getLocalname();
        String query = "for $root in #roots#[@type='"+MeasureResourceImpl.class.getName()+"'] let $data:=$root/xbrlapi:data/* where $data/@namespace='"+namespace+"' and $data/@name='"+name+"' return $root";
        List<MeasureResource> unitResources = getStore().<MeasureResource>queryForXMLResources(query);
        for (MeasureResource unitResource: unitResources) {
            List<LabelResource> labels = unitResource.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole,linkRole);
            if (! labels.isEmpty()) {
                return labels.get(0).getStringValue();
            }
        }

        // Try for a label on an equivalent unit resource.
        Set<MeasureResource> equivalentResources = new HashSet<MeasureResource>();
        for (MeasureResource unitResource: unitResources) {
            equivalentResources.addAll(unitResource.getEquivalents());
        }
        equivalentResources.removeAll(unitResources);
        for (MeasureResource unitResource: equivalentResources) {
            List<LabelResource> labels = unitResource.getLabelsWithLanguageAndResourceRoleAndLinkRole(locale,resourceRole,linkRole);
            if (! labels.isEmpty()) {
                return labels.get(0).getStringValue();
            }
        }        
        
        if (namespace.equals(Constants.ISO4217)) return "Currency:" + name;
        if (namespace.equals(Constants.XBRL21Namespace) && name.equals("pure")) return "none";
        
        return namespace + "#" + name;

    }
    
    /**
     * @see Labeller#getAspectValueLabelWithoutFallback(AspectValue, String, String, String)
     */
    public String getAspectValueLabelWithoutFallback(AspectValue value, String locale, String resourceRole, String linkRole) {
        
        try {
            UnitAspectValue v = (UnitAspectValue) value;
            List<String> numerators = new Vector<String>();
            List<String> denominators = new Vector<String>();
            for (Measure measure: v.getNumerators()) {
                numerators.add(getLabel(measure,locale,resourceRole,linkRole));
            }
            
            if (v.hasDenominators()) {
                for (Measure measure: v.getDenominators()) {
                    denominators.add(getLabel(measure,locale,resourceRole,linkRole));
                }
            } else {
                if (numerators.size() == 1) return numerators.get(0);
            }
            
            String label = "";
            if (numerators.size() > 1) label+= "(";
            for (int i=0; i<numerators.size(); i++) {
                if (i != 0) label += " x ";
                label += numerators.get(i);
            }
            if (numerators.size() > 1) label+= ")";

            if (! denominators.isEmpty()) {
                if (denominators.size() > 1) label+= "/(";
                else label += " / ";
                for (int i=0; i < denominators.size(); i++) {
                    if (i != 0) label += " x ";
                    label += denominators.get(i);
                }
                if (denominators.size() > 1) label+= ")";
            }
            return label;
            
        } catch (Throwable e) {
            logger.warn(e.getMessage());
            return null;
        }
        
    }    
    /**
     * @see Labeller#getAspectLabelWithoutFallback(String, String, String)
     */
    @Override
    public String getAspectLabelWithoutFallback(String locale, String resourceRole, String linkRole) {
        return "units";
    }    
    
}
