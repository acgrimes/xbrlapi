package org.xbrlapi.aspects.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.aspects.AspectModel;
import org.xbrlapi.aspects.AspectValue;
import org.xbrlapi.aspects.Combinations;
import org.xbrlapi.aspects.CombinationsImpl;
import org.xbrlapi.aspects.ConceptAspect;
import org.xbrlapi.aspects.FactSet;
import org.xbrlapi.aspects.FactSetImpl;
import org.xbrlapi.aspects.LocationAspect;
import org.xbrlapi.aspects.StandardAspectModel;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class CombinationsTestCase extends DOMLoadingTestCase {
	private final String FIRST_SMALL_INSTANCE = "test.data.small.instance";
    private final String SECOND_SMALL_INSTANCE = "test.data.small.instance.2";
    
    private final String TUPLE_INSTANCE = "test.data.local.xbrl.instance.tuples.with.units";
	
    private AspectModel model;
    private FactSet factSet;
    private final String axis = "row";
    
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		// Load and retrieve the facts
        loader.discover(this.getURI(FIRST_SMALL_INSTANCE));       
        loader.discover(this.getURI(SECOND_SMALL_INSTANCE));

        // Set up the aspect model
        model = new StandardAspectModel(store);
        model.initialise();

        // Create a fact set
        List<Fact> facts = store.getAllFacts();
        AssertJUnit.assertEquals(2,facts.size());
        factSet = new FactSetImpl(model);
        factSet.addFacts(facts);

        // Set up a row axis in the model
        model.addAspect(axis,model.getAspect(ConceptAspect.ID));
        model.addAspect(axis,model.getAspect(LocationAspect.ID));
        
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
    @Test
    public void testAspectValueCombinations() {
        try {
            
            AssertJUnit.assertEquals(2,factSet.getSize());
            
            // Create an aspect value combinations object
            Combinations combinations = new CombinationsImpl(model,"row");

            AssertJUnit.assertEquals(2,combinations.getAspects().size());
            AssertJUnit.assertEquals(ConceptAspect.ID,combinations.getAspects().get(0).getId());
            AssertJUnit.assertEquals(LocationAspect.ID,combinations.getAspects().get(1).getId());
            
            // Populate aspect values
            List<AspectValue> conceptAspectValues = new Vector<AspectValue>(factSet.getAspectValues(ConceptAspect.ID));
            Collections.sort(conceptAspectValues,model.getAspect(ConceptAspect.ID).getDomain());
            combinations.setAspectValues(ConceptAspect.ID,conceptAspectValues);
            AssertJUnit.assertEquals(3, combinations.getAspectValueCount(ConceptAspect.ID));
            AssertJUnit.assertEquals(1, combinations.getAncestorCount(ConceptAspect.ID));
            AssertJUnit.assertEquals(1, combinations.getDescendantCount(ConceptAspect.ID));
            
            List<AspectValue> locationAspectValues = new Vector<AspectValue>(factSet.getAspectValues(LocationAspect.ID));
            AssertJUnit.assertEquals(2, locationAspectValues.size());
            Collections.sort(locationAspectValues,model.getAspect(LocationAspect.ID).getDomain());
            combinations.setAspectValues(LocationAspect.ID,locationAspectValues);
            AssertJUnit.assertEquals(2, combinations.getAspectValueCount(LocationAspect.ID));

            AssertJUnit.assertEquals(1, combinations.getAncestorCount(ConceptAspect.ID));
            AssertJUnit.assertEquals(2, combinations.getDescendantCount(ConceptAspect.ID));

            AssertJUnit.assertEquals(3, combinations.getAncestorCount(LocationAspect.ID));
            AssertJUnit.assertEquals(1, combinations.getDescendantCount(LocationAspect.ID));
            
            AssertJUnit.assertEquals(6, combinations.getCombinationCount());

            for (int i=0; i<combinations.getCombinationCount(); i++) {
                logger.info(combinations.getCombinationValue(ConceptAspect.ID,i).getId() + " " + combinations.getCombinationValue(LocationAspect.ID,i).getId());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }	
	
	
	
}
