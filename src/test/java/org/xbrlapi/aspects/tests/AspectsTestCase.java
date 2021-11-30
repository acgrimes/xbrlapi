package org.xbrlapi.aspects.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

import org.xbrlapi.Concept;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fact;
import org.xbrlapi.aspects.Aspect;
import org.xbrlapi.aspects.AspectModel;
import org.xbrlapi.aspects.ConceptAspect;
import org.xbrlapi.aspects.ConceptAspectValue;
import org.xbrlapi.aspects.ConceptDomain;
import org.xbrlapi.aspects.FactSet;
import org.xbrlapi.aspects.FactSetImpl;
import org.xbrlapi.aspects.Filter;
import org.xbrlapi.aspects.FilterImpl;
import org.xbrlapi.aspects.LocationAspect;
import org.xbrlapi.aspects.LocationAspectValue;
import org.xbrlapi.aspects.PeriodAspect;
import org.xbrlapi.aspects.StandardAspectModel;
import org.xbrlapi.impl.ConceptImpl;


/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class AspectsTestCase extends DOMLoadingTestCase {
	private final String FIRST_SMALL_INSTANCE = "test.data.small.instance";
    private final String SECOND_SMALL_INSTANCE = "test.data.small.instance.2";
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
    public void testAspectModel() {
		try {

		    // Load and retrieve the facts
		    loader.discover(this.getURI(FIRST_SMALL_INSTANCE));       
	        loader.discover(this.getURI(SECOND_SMALL_INSTANCE));
			List<Fact> facts = store.getAllFacts();
			AssertJUnit.assertEquals(2,facts.size());
			
			// Set up the aspect model
            AspectModel model = new StandardAspectModel(store);
            model.initialise();
            ConceptAspect conceptAspect = (ConceptAspect) model.getAspect(ConceptAspect.ID);
            ConceptDomain conceptDomain = (ConceptDomain) conceptAspect.getDomain();
            AssertJUnit.assertEquals(store.<Concept>getXMLResources(ConceptImpl.class).size(),conceptDomain.getSize());
            AssertJUnit.assertEquals(conceptDomain.getSize(), conceptDomain.getAllAspectValues().size());
            model.addAspect("row", conceptAspect);
            AssertJUnit.assertEquals(7,model.getAspects().size());
            AssertJUnit.assertTrue(model.hasAxis("row"));
            AssertJUnit.assertFalse(model.hasAxis("col"));
            AssertJUnit.assertEquals(1,model.getAspects("row").size());
            AssertJUnit.assertEquals(2,model.getAxes().size());

            // Add in the location aspect
            LocationAspect locationAspect = (LocationAspect) model.getAspect(LocationAspect.ID);
            model.addAspect("row", locationAspect);
            AssertJUnit.assertEquals(2,model.getAspects("row").size());
            model.addAspect("col", locationAspect);
            AssertJUnit.assertEquals(1,model.getAspects("row").size());
            AssertJUnit.assertEquals(3,model.getAxes().size());
            AssertJUnit.assertTrue(model.hasAxis("col"));

            // Set up the filtration system
            Filter filter = new FilterImpl();
            Fact firstFact = facts.get(0);
            ConceptAspectValue conceptAspectValue = new ConceptAspectValue(firstFact.getNamespace(), firstFact.getLocalname());
            filter.addCriterion(conceptAspectValue);
            AssertJUnit.assertTrue(filter.filtersOn(ConceptAspect.ID));
            AssertJUnit.assertFalse(filter.filtersOn(LocationAspect.ID));
            LocationAspectValue locationAspectValue = new LocationAspectValue(firstFact.getIndex());
            filter.addCriterion(locationAspectValue);
            AssertJUnit.assertTrue(filter.filtersOn(LocationAspect.ID));
            filter.removeCriterion(LocationAspect.ID);
            AssertJUnit.assertFalse(filter.filtersOn(LocationAspect.ID));

            // Create a fact set
            FactSet factSet = new FactSetImpl(model);
            factSet.addFacts(facts);

            AssertJUnit.assertEquals(3, model.getAxes().size());
            AssertJUnit.assertTrue(model.hasAxis("orphan"));
            AssertJUnit.assertTrue(model.hasAspect(PeriodAspect.ID));

            for (String axis: model.getAxes()) {
                logger.info(axis);
                for (Aspect aspect: model.getAspects(axis)) {
                    logger.info(aspect.getId());
                }
            }
            
/*            List<List<AspectValue>> rowMatrix = model.getAspectValueCombinationsForAxis("row");
            
            assertEquals(2,rowMatrix.size());
            assertEquals(1,rowMatrix.get(0).size());

            for (List<AspectValue> rowCombination: rowMatrix) {
                for (AspectValue rValue: rowCombination) {
                    logger.debug("R: " + rValue.getAspect().getType() + " = " + rValue.getLabel());
                }
                Set<Fact> matchingFacts = filter.getMatchingFacts();
                for (Fact matchingFact: matchingFacts) {
                    logger.debug(matchingFact.getIndex());
                }
            }
*/ 
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	
	
	
}
