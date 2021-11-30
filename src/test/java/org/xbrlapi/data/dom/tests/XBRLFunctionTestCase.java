package org.xbrlapi.data.dom.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.Concept;
import org.xbrlapi.Fact;
import org.xbrlapi.Instance;
import org.xbrlapi.Schema;
import org.xbrlapi.Tuple;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net) 
*/
public class XBRLFunctionTestCase extends BaseTestCase {
	private final String STARTING_POINT_1 = "test.data.small.instance";
	private final String STARTING_POINT_2 = "test.data.tuple.instance";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

    @Test
    public void testGetConcepts() {
        try {
            loader.discover(this.getURI(STARTING_POINT_1));     
            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            for (Instance instance: instances) {
                for (Fact fact: instance.getChildFacts()) {
                    Concept concept = store.getConcept(fact.getNamespace(),fact.getLocalname());
                    logger.debug("Testing concept " + concept.getName() + " " + concept.getTargetNamespace());
                    AssertJUnit.assertEquals(fact.getLocalname(),concept.getDataRootElement().getAttribute("name"));
                    AssertJUnit.assertEquals(fact.getNamespace(),concept.getTargetNamespace());
                }
            }
            
            try {
                store.getConcept(Constants.XBRL21Namespace,"rubbishConceptName");
                Assert.fail("There is no such concept as " + Constants.XBRL21Namespace + ":rubbishConceptName");
            } catch (Exception e) {
                ;
            }
            
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
    }

    
    @Test
    public void testGetTuples() {
        try {
            loader.discover(this.getURI(STARTING_POINT_2));
            List<Tuple> tuples = store.getTuples();
            AssertJUnit.assertTrue(tuples.size() > 0);

            List<Instance> instances = store.<Instance>getXMLResources("Instance");
            AssertJUnit.assertTrue(instances.size() > 0);
            for (Instance instance: instances) {
                List<Fact> facts = instance.getChildFacts();
                AssertJUnit.assertTrue(facts.size() > 0);
            }
            
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
    }
    
    @Test
    public void testGetSchemaBasedOnTargetNamespace() {
        Schema schema;
        try {
            loader.discover(this.getURI(STARTING_POINT_2));
            schema = store.getSchema(Constants.XBRL21Namespace);
            AssertJUnit.assertTrue(schema.getTargetNamespace().equals(Constants.XBRL21Namespace));
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
        try {
            schema = store.getSchema("http://rubbish.namespace/");
            AssertJUnit.assertNull(schema);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
    }    
    
    @Test
    public void testGetFacts() {
        try {
            loader.discover(this.getURI(STARTING_POINT_1));
            loader.discover(this.getURI(STARTING_POINT_2));
            List<Fact> facts = store.getFacts();
            AssertJUnit.assertTrue(facts.size() > 0);
        } catch (XBRLException e) {
            e.printStackTrace();
            Assert.fail("Unexpected " + e.getMessage());
        }
    }    

}
