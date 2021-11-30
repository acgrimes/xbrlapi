package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.xbrlapi.Context;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Period;
import org.xbrlapi.impl.ContextImpl;

/**
 * Tests the implementation of the org.xbrlapi.Period interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class PeriodTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.period.durations";

    private final String INSTANCE = "test.data.local.xbrl.period.interpretation";
    private final String DATE_ONLY = "date-only";
    private final String DATE_AND_TIME = "date-and-time";
    private final String DATE_AND_FRACTIONAL_TIME = "date-and-fractional-time";
    private final String UTC_DATE_AND_TIME = "utc-date-and-time";
    private final String UTC_OFFSET_DATE_AND_TIME = "utc-offset-date-and-time";
    private final String INFERRED_END = "inferred-end";
    private final String INFERRED_START_AND_END = "inferred-start-and-end";

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}	
	
	/**
	 * Test is period type determination
	 */
	@Test
    public void testPeriodTypeDetermination() {

        try {
            loader.discover(this.getURI(STARTING_POINT));       
            List<Period> periods = store.<Period>getXMLResources("Period");
            AssertJUnit.assertTrue(periods.size() > 0);
            for (Period period: periods) {
                AssertJUnit.assertTrue(period.isFiniteDurationPeriod());
                AssertJUnit.assertFalse(period.isForeverPeriod());
                AssertJUnit.assertFalse(period.isInstantPeriod());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        
	}
	
	/**
	 * Test getting the period information.
	 */
	@Test
    public void testGetPeriodInformation() {

        try {
            loader.discover(this.getURI(STARTING_POINT));       
            List<Period> periods = store.<Period>getXMLResources("Period");
            AssertJUnit.assertTrue(periods.size() > 0);
            for (Period period: periods) {
                AssertJUnit.assertEquals("2001-08-01",period.getStart());
                AssertJUnit.assertEquals("2001-08-31",period.getEnd());
                try {
                    period.getInstant();
                    Assert.fail("An exception should have been thrown because the period is not an instant.");
                } catch (Exception e) {
                    ;
                }       
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
	}
	
    @Test
    public void testPeriodInterpretations() {
        try {

            // Load and retrieve the facts
            loader.discover(this.getURI(INSTANCE));
            Map<String,Context> contexts = new HashMap<String,Context>();
            for (Context context: store.<Context>getXMLResources(ContextImpl.class)) {
                contexts.put(context.getId(),context);
            }
            AssertJUnit.assertTrue(contexts.size() > 0);
            
            Period period = contexts.get(DATE_ONLY).getPeriod();
            XMLGregorianCalendar calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2008,calendar.getYear());
            AssertJUnit.assertEquals(1,calendar.getMonth());
            AssertJUnit.assertEquals(1,calendar.getDay());
            AssertJUnit.assertTrue(period.instantIsDateOnly());
            AssertJUnit.assertFalse(period.instantHasTimezone());

            period = contexts.get(DATE_AND_TIME).getPeriod();
            calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2007,calendar.getYear());
            AssertJUnit.assertEquals(12,calendar.getMonth());
            AssertJUnit.assertEquals(31,calendar.getDay());
            AssertJUnit.assertEquals(11, calendar.getHour());
            AssertJUnit.assertEquals(35, calendar.getMinute());
            AssertJUnit.assertEquals(01, calendar.getSecond());
            AssertJUnit.assertEquals(null, calendar.getFractionalSecond());
            AssertJUnit.assertFalse(period.instantIsDateOnly());
            AssertJUnit.assertFalse(period.instantHasTimezone());

            period = contexts.get(DATE_AND_FRACTIONAL_TIME).getPeriod();
            calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2007,calendar.getYear());
            AssertJUnit.assertEquals(12,calendar.getMonth());
            AssertJUnit.assertEquals(31,calendar.getDay());
            AssertJUnit.assertEquals(11, calendar.getHour());
            AssertJUnit.assertEquals(35, calendar.getMinute());
            AssertJUnit.assertEquals(01, calendar.getSecond());
            AssertJUnit.assertEquals("0.0035", calendar.getFractionalSecond().toString());
            AssertJUnit.assertFalse(period.instantIsDateOnly());
            AssertJUnit.assertFalse(period.instantHasTimezone());
            
            period = contexts.get(UTC_DATE_AND_TIME).getPeriod();
            calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2007,calendar.getYear());
            AssertJUnit.assertEquals(12,calendar.getMonth());
            AssertJUnit.assertEquals(31,calendar.getDay());
            AssertJUnit.assertEquals(11, calendar.getHour());
            AssertJUnit.assertEquals(35, calendar.getMinute());
            AssertJUnit.assertEquals(01, calendar.getSecond());
            Assert.assertNull(calendar.getFractionalSecond());
            AssertJUnit.assertFalse(period.instantIsDateOnly());
            AssertJUnit.assertTrue(period.instantHasTimezone());
            AssertJUnit.assertEquals(0, period.getInstantCalendar().getTimeZone(0).getRawOffset());

            period = contexts.get(UTC_OFFSET_DATE_AND_TIME).getPeriod();
            calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2007,calendar.getYear());
            AssertJUnit.assertEquals(12,calendar.getMonth());
            AssertJUnit.assertEquals(31,calendar.getDay());
            AssertJUnit.assertEquals(11, calendar.getHour());
            AssertJUnit.assertEquals(00, calendar.getMinute());
            AssertJUnit.assertEquals(00, calendar.getSecond());
            Assert.assertNull(calendar.getFractionalSecond());
            AssertJUnit.assertFalse(period.instantIsDateOnly());
            AssertJUnit.assertTrue(period.instantHasTimezone());
            AssertJUnit.assertEquals(14400000, period.getInstantCalendar().getTimeZone(0).getRawOffset());
            
            period = contexts.get(this.INFERRED_END).getPeriod();
            calendar = period.getInstantCalendar();
            AssertJUnit.assertEquals(2008,calendar.getYear());
            AssertJUnit.assertEquals(1,calendar.getMonth());
            AssertJUnit.assertEquals(1,calendar.getDay());
            AssertJUnit.assertTrue(period.instantIsDateOnly());
            AssertJUnit.assertFalse(period.instantHasTimezone());
            
            period = contexts.get(this.INFERRED_START_AND_END).getPeriod();
            calendar = period.getEndCalendar();
            AssertJUnit.assertEquals(2008,calendar.getYear());
            AssertJUnit.assertEquals(1,calendar.getMonth());
            AssertJUnit.assertEquals(1,calendar.getDay());
            AssertJUnit.assertTrue(period.endIsDateOnly());
            AssertJUnit.assertFalse(period.endHasTimezone());
            calendar = period.getStartCalendar();
            AssertJUnit.assertEquals(2007,calendar.getYear());
            AssertJUnit.assertEquals(1,calendar.getMonth());
            AssertJUnit.assertEquals(1,calendar.getDay());
            AssertJUnit.assertTrue(period.startIsDateOnly());
            AssertJUnit.assertFalse(period.startHasTimezone());
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    

}
