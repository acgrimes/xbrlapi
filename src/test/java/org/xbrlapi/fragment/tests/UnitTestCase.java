package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Unit;

/**
 * Tests the implementation of the org.xbrlapi.Unit interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class UnitTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.units.simple";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test checking for denopminator.
	 */
	@Test
    public void testHasDenominator() {

		try {
		    List<Unit> units = store.<Unit>getXMLResources("Unit");
		    AssertJUnit.assertTrue(units.size() > 0);
		    for (Unit unit: units) {
	            AssertJUnit.assertFalse(unit.hasDenominator());
		    }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting numerator measures.
	 */
	@Test
    public void testGetNumeratorMeasures() {

		try {
            List<Unit> units = store.<Unit>getXMLResources("Unit");
            AssertJUnit.assertTrue(units.size() > 0);
            for (Unit unit: units) {
                AssertJUnit.assertFalse(unit.hasDenominator());
                AssertJUnit.assertEquals(2,unit.getNumeratorMeasures().getLength());
            }
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test getting denominator measures.
	 */
	@Test
    public void testGetDenominatorMeasures() {

        try {
            List<Unit> units = store.<Unit>getXMLResources("Unit");
            AssertJUnit.assertTrue(units.size() > 0);
            for (Unit unit: units) {
                Assert.assertNull(unit.getDenominatorMeasures());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
	/**
	 * Test checking equality.
	 */
	@Test
    public void testCheckEquality() {

        try {
            List<Unit> units = store.<Unit>getXMLResources("Unit");
            AssertJUnit.assertTrue(units.size() > 0);
            for (Unit unit: units) {
                AssertJUnit.assertTrue(unit.equals(unit));
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}	
}
