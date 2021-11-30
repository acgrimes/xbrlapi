package org.xbrlapi.aspects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xbrlapi.Context;
import org.xbrlapi.Fact;
import org.xbrlapi.Unit;
import org.xbrlapi.data.Store;
import org.xbrlapi.utilities.XBRLException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Implementation of common aspect model methods.
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public class AspectModelImpl implements AspectModel {

    /**
     * 
     */
    private static final long serialVersionUID = 4240229374124118822L;

    private static final Logger logger = Logger
            .getLogger(AspectModelImpl.class);

    /**
     * Map from axis names to lists of the aspects in that axis.
     * @serial
     */
    private ListMultimap<String, Aspect> axes = ArrayListMultimap.<String, Aspect> create();

    /**
     * Map from aspect IDs to the aspect implementations being used.
     * @serial
     */
    private Map<String,Aspect> aspects = new HashMap<String,Aspect>();

    /**
     * Map from aspect IDs to the aspect labellers being used.
     * @serial
     */
    private Map<String,Labeller> labellers = new HashMap<String,Labeller>();

    /**
     * The axis of the aspect model to add new aspects to by default.
     * This defaults to "orphan".
     * @serial
     */
    private String defaultAxis = "orphan";

    /**
     * The data store used to define some of the standard aspects.
     * @serial
     */
    private Store store;
    
    /**
     * @param store The data store required to create domains for some aspects.
     * @throws XBRLException if the store is null.
     */
    public AspectModelImpl(Store store) throws XBRLException {
        super();
        if (store == null) throw new XBRLException("The data store must not be null.");
        this.store = store;
    }
    
    /**
     * @see AspectModel#initialise()
     */
    public void initialise() throws XBRLException {
        ;
    }

    /**
     * @return the data store underlying this aspect model.
     */
    protected Store getStore() {
        return this.store;
    }
    
    /**
     * @see AspectModel#getDefaultAxis()
     */
    public String getDefaultAxis() {
        return defaultAxis;
    }

    /**
     * @see AspectModel#setDefaultAxis(String)
     */
    public void setDefaultAxis(String defaultAxis) {
        this.defaultAxis = defaultAxis;
    }
        
    
    /**
     * @see AspectModel#hasAxis(String)
     */
    public boolean hasAxis(String axis) {
        return axes.containsKey(axis);
    }

    /**
     * @see AspectModel#hasAspect(String)
     */
    public boolean hasAspect(String aspectId) {
        return aspects.containsKey(aspectId);
    }
    
    /**
     * @see AspectModel#getAspect(String)
     */
    public Aspect getAspect(String aspectId) throws XBRLException {
        if (! aspects.containsKey(aspectId)) throw new XBRLException(aspectId + " is not in the aspect model.");
        return aspects.get(aspectId);
    }
    

    /**
     * @see AspectModel#getAspects()
     */
    public Collection<Aspect> getAspects() throws XBRLException {
        return aspects.values();
    }

    /**
     * @see AspectModel#getAspects(String)
     */
    public List<Aspect> getAspects(String axis) {
        if (axes.containsKey(axis))
            return axes.get(axis);
        return new Vector<Aspect>();
    }

    /**
     * @param aspect The aspect to remove from an axis.
     */
    private void deleteAspect(Aspect aspect) {
        if (axes.containsValue(aspect)) {
            for (String key : axes.keySet()) {
                if (axes.get(key).contains(aspect)) {
                    axes.remove(key, aspect);
                    break;
                }
            }
            aspects.remove(aspect.getId());
        }
    }

    /**
     * @see AspectModel#axisContainsAspect(String, String)
     */
    public boolean axisContainsAspect(String axis, String aspectId) {
        if (!axes.containsKey(axis)) return false;
        return axes.get(axis).contains(aspects.get(aspectId));
    }

    /**
     * @see AspectModel#addAspect(String, Aspect)
     */
    public void addAspect(String axis, Aspect aspect) {
        this.deleteAspect(aspect);
        axes.put(axis, aspect);
        aspects.put(aspect.getId(),aspect);
        try {
            if (! hasLabeller(aspect.getId()))
                labellers.put(aspect.getId(),new LabellerImpl(aspect));
        } catch (XBRLException e) {
            ; // Cannot be thrown.
        }
    }
    
    /**
     * @see AspectModel#addAspect(Aspect)
     */
    public void addAspect(Aspect aspect) {
        this.addAspect(this.getDefaultAxis(),aspect);
    }

    /**
     * @see AspectModel#addAspect(String, Aspect, Aspect)
     */
    public void addAspect(String axis, Aspect parentAspect, Aspect aspect)
            throws XBRLException {
        this.deleteAspect(aspect);
        aspects.put(aspect.getId(),aspect);
        if (!axes.containsKey(axis)) {
            addAspect(axis, aspect);
            return;
        }
        if (!axisContainsAspect(axis, parentAspect.getId())) {
            addAspect(axis, aspect);
            return;
        }
        List<Aspect> aspects = axes.get(axis);
        axes.removeAll(axis);
        for (Aspect a: aspects) {
            axes.put(axis, a);
            if (a.equals(parentAspect)) axes.put(axis, aspect);
        }
        try {
            labellers.put(aspect.getId(),new LabellerImpl(aspect));
        } catch (XBRLException e) {
            ; // Cannot be thrown.
        }
    }
    
    
    
    /**
     * @see AspectModel#getLabeller(String)
     */
    public Labeller getLabeller(String aspectId) throws XBRLException {
        if (aspectId == null) throw new XBRLException("The aspect ID must not be null.");
        if (labellers.containsKey(aspectId)) return labellers.get(aspectId);
        return new LabellerImpl(this.getAspect(aspectId));
    }
    
    /**
     * @see AspectModel#hasLabeller(String)
     */
    public boolean hasLabeller(String aspectId) {
        return labellers.containsKey(aspectId);
    }    

    /**
     * @see AspectModel#setLabeller(String, Labeller)
     */
    public void setLabeller(String aspectId, Labeller labeller)
            throws XBRLException {
        if (aspectId == null) throw new XBRLException("The aspect ID must not be null.");
        if (labeller == null) throw new XBRLException("The aspect labeller must not be null.");
        labellers.put(aspectId, labeller);
    }

    /**
     * @see AspectModel#addAspect(Aspect, Aspect)
     */
    public void addAspect(Aspect parentAspect, Aspect aspect)
            throws XBRLException {
        this.addAspect(this.getDefaultAxis(), parentAspect, aspect);
    }    

    /**
     * @see AspectModel#getAxes()
     */
    public Set<String> getAxes() throws XBRLException {
        return this.axes.keySet();
    }

    /**
     * @see AspectModel#getAspectValues(Fact)
     */
    public Map<String, AspectValue> getAspectValues(Fact fact) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
            result.put(aspect.getId(),aspect.getValue(fact));
        }
        return result;
    }

    /**
     * @see AspectModel#getAspectValuesFromFact(Fact, Map)
     */
    public Map<String, AspectValue> getAspectValuesFromFact(Fact fact, Map<String, AspectValue> existingValues) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
            String id = aspect.getId();
            if (existingValues.containsKey(id)) result.put(id,existingValues.get(id));
            else result.put(id,aspect.getValue(fact));
        }
        return result;
    }
    
    /**
     * @see AspectModel#getAspectValuesFromContext(Context)
     */
    public Map<String, AspectValue> getAspectValuesFromContext(Context context) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
            result.put(aspect.getId(),aspect.getValue(context));
        }
        return result;
    }
    
    /**
     * @see AspectModel#getAspectValuesFromContext(Context, Map)
     */
    public Map<String, AspectValue> getAspectValuesFromContext(Context context, Map<String, AspectValue> existingValues) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
        	String id = aspect.getId();
            if (existingValues.containsKey(id)) result.put(id,existingValues.get(id));
            else result.put(id,aspect.getValue(context));
        }
        return result;
    }    
 
    /**
     * @see AspectModel#getAspectValuesFromUnit(Unit)
     */
    public Map<String, AspectValue> getAspectValuesFromUnit(Unit unit) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
            result.put(aspect.getId(),aspect.getValue(unit));
        }
        return result;
    }
    
    /**
     * @see AspectModel#getAspectValuesFromUnit(Unit, Map)
     */
    public Map<String, AspectValue> getAspectValuesFromUnit(Unit unit, Map<String, AspectValue> existingValues) throws XBRLException {
        Map<String,AspectValue> result = new HashMap<String,AspectValue>();
        for (Aspect aspect: this.getAspects()) {
        	String id = aspect.getId();
            if (existingValues.containsKey(id)) result.put(id,existingValues.get(id));
            else result.put(id,aspect.getValue(unit));
        }
        return result;
    }        
    
    /**
     * @see AspectModel#moveAspects(String, String)
     */
    public void moveAspects(String originalAxis, String newAxis) throws XBRLException {
        if (! this.hasAxis(originalAxis)) throw new XBRLException(originalAxis + " is not an axis of this model.");
        Collection<Aspect> aspects = this.getAspects(originalAxis);
        this.axes.removeAll(originalAxis);
        for (Aspect aspect: aspects) {
            this.addAspect(newAxis, aspect);
        }
    }
    
    /**
     * @see AspectModel#duplicate()
     */
    public AspectModel duplicate() throws XBRLException {
        
        try {
            Class<?> modelClass = this.getClass();
            Constructor<?> constructor = modelClass.getConstructor(Store.class);
            AspectModel duplicate = (AspectModel) constructor.newInstance(this.getStore());
            
            for (String axis: this.getAxes()) {
                for (Aspect aspect: axes.get(axis)) {
                	String id = aspect.getId();
                    duplicate.addAspect(axis,aspect);
                    Labeller labeller = this.getLabeller(id);
                    duplicate.setLabeller(id,labeller);
                }
            }
            
            return duplicate;
        } catch (NoSuchMethodException nsme) {
            throw new XBRLException("The aspect model constructor does not support duplication.",nsme);
        } catch (InvocationTargetException ite) {
            throw new XBRLException("The aspect model constructor does not support duplication.",ite);
        } catch (IllegalAccessException iae) {
            throw new XBRLException("The aspect model constructor does not support duplication.",iae);
        } catch (InstantiationException ie) {
            throw new XBRLException("The aspect model constructor does not support duplication.",ie);
        }
        
    }
    
    /**
     * @see AspectModel#getAspectLabel(String, String, String, String)
     */
    public String getAspectLabel(String aspectId, String locale, String resourceRole, String linkRole) throws XBRLException {
        if (! hasAspect(aspectId)) {
        	logger.info("The aspect model does not have aspect " + aspectId);
        	throw new XBRLException("Aspect " + aspectId + " is not in this aspect model.");
        }
        String label = this.getLabeller(aspectId).getAspectLabel(locale, resourceRole, linkRole);
        return label;
    }

    /**
     * @see AspectModel#getAspectValueLabelGivenLists(AspectValue, List, List, List)
     */
    public String getAspectLabelGivenLists(String aspectId, List<String> locales, List<String> resourceRoles, List<String> linkRoles) throws XBRLException {

        if (! hasAspect(aspectId)) throw new XBRLException("Aspect " + aspectId + " is not in this aspect model.");
        if (! hasLabeller(aspectId)) this.setLabeller(aspectId, new LabellerImpl(getAspect(aspectId)));
        return getLabeller(aspectId).getAspectLabelGivenLists(locales, resourceRoles, linkRoles);
        
    }    
    
    /**
     * @see AspectModel#getAspectValueLabel(AspectValue, String, String, String)
     */
    public String getAspectValueLabel(AspectValue value, String locale, String resourceRole, String linkRole) throws XBRLException {
    	String aspectId = value.getAspectId();
        if (! hasAspect(aspectId)) throw new XBRLException("Aspect " + aspectId + " is not in this aspect model.");
        if (! hasLabeller(aspectId)) this.setLabeller(aspectId, new LabellerImpl(getAspect(aspectId)));
        return getLabeller(aspectId).getAspectValueLabel(value,locale, resourceRole, linkRole);
    }

    /**
     * @see AspectModel#getAspectValueLabelGivenLists(AspectValue, List, List, List)
     */
    public String getAspectValueLabelGivenLists(AspectValue value, List<String> locales,
            List<String> resourceRoles, List<String> linkRoles) throws XBRLException {

    	String aspectId = value.getAspectId();
        if (! hasAspect(aspectId)) throw new XBRLException("Aspect " + aspectId + " is not in this aspect model.");
        if (! hasLabeller(aspectId)) this.setLabeller(aspectId, new LabellerImpl(getAspect(aspectId)));
        return getLabeller(aspectId).getAspectValueLabelGivenLists(value,locales, resourceRoles, linkRoles);
        
    }

}
