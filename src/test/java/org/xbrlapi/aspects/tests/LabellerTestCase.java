package org.xbrlapi.aspects.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.aspects.AspectModel;
import org.xbrlapi.aspects.AspectValue;
import org.xbrlapi.aspects.FactSet;
import org.xbrlapi.aspects.FactSetImpl;
import org.xbrlapi.aspects.Labeller;
import org.xbrlapi.aspects.PeriodAspect;
import org.xbrlapi.aspects.PeriodLabellerNaive;
import org.xbrlapi.aspects.PeriodLabellerQuarters;
import org.xbrlapi.aspects.StandardAspectModelWithMemoryCachingLabellers;
import org.xbrlapi.aspects.TupleAspect;
import org.xbrlapi.aspects.TupleDomain;

/**
 * Tests basic aspect labelling classes
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LabellerTestCase extends DOMLoadingTestCase {
	private final String FIRST_SMALL_INSTANCE = "test.data.small.instance";
    private final String INSTANCE_WITH_LABELLED_CONCEPTS = "test.data.local.xbrl.presentation.simple";
    private final String SECOND_SMALL_INSTANCE = "test.data.small.instance.2";
    private final String MEASURES = "real.data.xbrl.api.measures";
    private final String TUPLE_INSTANCE = "test.data.local.xbrl.instance.tuples.with.units";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	@Test
    public void testAspectLabels() {
		try {

			// Set up the aspect model
            AspectModel model = new StandardAspectModelWithMemoryCachingLabellers(store);
            model.initialise();
            model.addAspect(new TupleAspect(new TupleDomain(store)));

            // Load and retrieve the facts
            loader.discover(this.getURI(MEASURES));       
            loader.discover(this.getURI(FIRST_SMALL_INSTANCE));       
            loader.discover(this.getURI(SECOND_SMALL_INSTANCE));
            loader.discover(this.getURI(INSTANCE_WITH_LABELLED_CONCEPTS));

            List<Fact> facts = store.getAllFacts();
            AssertJUnit.assertEquals(3,facts.size());
            
            // Create the fact set
            FactSet factSet = new FactSetImpl(model);
            factSet.addFacts(facts);

            for (Fact fact: factSet.getFacts()) {
                for (AspectValue value: factSet.getAspectValues(fact)) {
                    Labeller labeller = model.getLabeller(value.getAspectId());
                    logger.info(labeller.getAspectLabel(null,null,null) + " " + labeller.getAspectValueLabel(value,"en",null,null));
                }
            }
            
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	   @Test
    public void testQuarterlyPeriodAspectLabels() {
	        try {

	            // Set up the aspect model
	            AspectModel model = new StandardAspectModelWithMemoryCachingLabellers(store);
	            model.initialise();
	            model.setLabeller(PeriodAspect.ID, new PeriodLabellerQuarters(model.getAspect(PeriodAspect.ID)));

	            // Load and retrieve the facts
	            loader.discover(this.getURI(MEASURES));       
	            loader.discover(this.getURI(FIRST_SMALL_INSTANCE));       
	            loader.discover(this.getURI(SECOND_SMALL_INSTANCE));
	            loader.discover(this.getURI(INSTANCE_WITH_LABELLED_CONCEPTS));

	            List<Fact> facts = store.getAllFacts();
	            AssertJUnit.assertEquals(3,facts.size());
	            
	            // Create the fact set
	            FactSet factSet = new FactSetImpl(model);
	            factSet.addFacts(facts);

	            for (Fact fact: factSet.getFacts()) {
	                for (AspectValue value: factSet.getAspectValues(fact)) {
	                    Labeller labeller = model.getLabeller(value.getAspectId());
	                    logger.info(labeller.getAspectLabel(null,null,null) + " " + labeller.getAspectValueLabel(value,"en",null,null));
	                }
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            Assert.fail(e.getMessage());
	        }
	    }
	   
       @Test
    public void testNaivePeriodAspectLabels() {
           try {

               // Set up the aspect model
               AspectModel model = new StandardAspectModelWithMemoryCachingLabellers(store);
               model.initialise();
               model.setLabeller(PeriodAspect.ID, new PeriodLabellerNaive(model.getAspect(PeriodAspect.ID)));

               // Load and retrieve the facts
               loader.discover(this.getURI(MEASURES));       
               loader.discover(this.getURI(FIRST_SMALL_INSTANCE));       
               loader.discover(this.getURI(SECOND_SMALL_INSTANCE));
               loader.discover(this.getURI(INSTANCE_WITH_LABELLED_CONCEPTS));

               List<Fact> facts = store.getAllFacts();
               AssertJUnit.assertEquals(3,facts.size());
               
               // Create the fact set
               FactSet factSet = new FactSetImpl(model);
               factSet.addFacts(facts);

               for (Fact fact: factSet.getFacts()) {
                   for (AspectValue value: factSet.getAspectValues(fact)) {
                       Labeller labeller = model.getLabeller(value.getAspectId());
                       logger.info(labeller.getAspectLabel(null,null,null) + " " + labeller.getAspectValueLabel(value,"en",null,null));
                   }
               }
               
           } catch (Exception e) {
               e.printStackTrace();
               Assert.fail(e.getMessage());
           }
       }	   
	
}
