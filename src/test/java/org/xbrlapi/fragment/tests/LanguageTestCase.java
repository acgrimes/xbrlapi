package org.xbrlapi.fragment.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.DOMLoadingTestCase;
import java.util.List;
import org.xbrlapi.Language;

/**
 * Tests the implementation of the org.xbrlapi.Language interface.
 * Uses the DOM-based data store to ensure rapid testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class LanguageTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "real.data.languages";
	
	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
		loader.discover(this.getURI(STARTING_POINT));		
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
		
	}	
	
	@Test
    public void testLanguagePropertyAccessors() {

		try {
			List<Language> fragments = store.<Language>getXMLResources("Language");
			Language fragment = fragments.get(0);
			AssertJUnit.assertEquals("Afar", fragment.getName());
			AssertJUnit.assertEquals("aa", fragment.getCode());
			AssertJUnit.assertEquals("en", fragment.getEncoding());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
    public void testGetLanguageName() {

		try {
			Language language = store.getLanguage("en","en");
			AssertJUnit.assertEquals("English", language.getName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}	
	
}
