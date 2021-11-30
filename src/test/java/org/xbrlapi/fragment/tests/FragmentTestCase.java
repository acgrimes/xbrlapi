package org.xbrlapi.fragment.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xbrlapi.DOMLoadingTestCase;
import org.xbrlapi.Fragment;
import org.xbrlapi.Measure;
import org.xbrlapi.Schema;
import org.xbrlapi.TypeDeclaration;
import org.xbrlapi.Unit;
import org.xbrlapi.impl.UnitImpl;
import org.xbrlapi.utilities.Constants;

/**
 * Tests the fragment interface implementation.
 * @see org.xbrlapi.impl.FragmentImpl
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class FragmentTestCase extends DOMLoadingTestCase {
	private final String STARTING_POINT = "test.data.local.xbrl.unit.namespace.resolution";
	
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
    public void testNamespaceResolution() {

        try {
            List<Unit> units = store.<Unit>getXMLResources(UnitImpl.class);
            AssertJUnit.assertTrue(units.size() > 0);
            for (Unit unit: units) {
                logger.info("Starting to get namespace for a new unit.");
                String namespace = unit.getNamespaceFromQName(unit.getNumeratorMeasures().item(0).getTextContent().trim(),unit.getNumeratorMeasures().item(0));
                logger.info(unit.getId() + " " + namespace);
                if (! unit.getId().equals("u8"))
                    AssertJUnit.assertEquals("http://xbrlapi.org/metric/"+ unit.getId(),namespace.toString());
                else 
                    AssertJUnit.assertEquals(Constants.XBRL21Namespace,namespace);
                    
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
	}
	
	@Test
    public void testIsRoot() {
        try {
            List<Fragment> roots= store.getRootFragments();
            AssertJUnit.assertTrue(roots.size() > 0);
            for (Fragment root: roots) {
                AssertJUnit.assertTrue(root.isRoot());
                AssertJUnit.assertFalse(root.isChild());
                List<Fragment> children = root.getAllChildren();
                for (Fragment child: children) {
                    AssertJUnit.assertTrue(child.isChild());
                    AssertJUnit.assertFalse(child.isRoot());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
	}
	
    @Test
    public void testNamespaceResolutionForQNamesWithoutPrefixes() {

        try {
            Schema schema = store.getSchema(Constants.XBRL21Namespace);
            TypeDeclaration type = schema.getGlobalDeclaration("monetaryItemType");
            TypeDeclaration parentType = type.getParentType();
            logger.info(parentType.serialize());
            String ns = parentType.getNamespaceFromQName("decimal",parentType.getDataRootElement());
            AssertJUnit.assertEquals(Constants.XMLSchemaNamespace,ns);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }	

    @Test
    public void testNamespaceResolutionGivenMultiplePrefixesForTheOneNamespace() {

        try {
            
            List<Unit> units = store.<Unit>getXMLResources("Unit");
            AssertJUnit.assertTrue(units.size() > 0);
            for (Unit unit: units) {
                if (unit.getId().equals("u8")) {
                    List<Measure> measures = unit.getResolvedNumeratorMeasures();
                    for (Measure measure: measures) {
                        if (measure.getLocalname().equals("shares")) {
                            AssertJUnit.assertEquals(Constants.XBRL21Namespace,measure.getNamespace());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    
	
	
}
