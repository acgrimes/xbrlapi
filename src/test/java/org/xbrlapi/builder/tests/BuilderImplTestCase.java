package org.xbrlapi.builder.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.xbrlapi.builder.Builder;
import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.utilities.BaseTestCase;
import org.xbrlapi.utilities.Constants;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class BuilderImplTestCase extends BaseTestCase {

	@BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
	}

	@AfterMethod
    protected void tearDown() throws Exception {
       super.tearDown();
	}

	/**
	 * Constructor for BuilderImplTests.
	 * @param arg0
	 */
	@Test
    public void testGetInsertionPoint() {
		try {
			Builder b = new BuilderImpl();
			b.appendElement(Constants.XBRLAPINamespace,"root","my:root");
			logger.info(b.getInsertionPoint().getClass().getName());
			AssertJUnit.assertEquals("org.apache.xerces.dom.ElementNSImpl",b.getInsertionPoint().getClass().getName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
    public void testAppendText() {
		//TODO Test builder appendTextTest().
	}

	@Test
    public void testAppendProcessingInstruction() {
		//TODO Test builder appendProcessingInstruction().
	}

	@Test
    public void testAppendComment() {
		//TODO Test builder appendComment().
	}

	@Test
    public void testAppendElement() {
		try {
			Builder b = new BuilderImpl();
			b.appendElement(Constants.XBRLAPINamespace,"root","xbrlapi:root");
			AssertJUnit.assertEquals(Constants.XBRLAPINamespace.toString(),(b.getInsertionPoint()).getNamespaceURI());
			AssertJUnit.assertEquals("root",(b.getInsertionPoint()).getLocalName());
		} catch (Exception e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
    public void testEndElement() {
		try {
			Builder b = new BuilderImpl();
			b.appendElement(Constants.XBRLAPINamespace,"root","xbrlapi:root");
			AssertJUnit.assertEquals("org.apache.xerces.dom.ElementNSImpl",b.getInsertionPoint().getClass().getName());
			b.endElement("http://www.xbrlapi.org/","root","xbrlapi:root");
			AssertJUnit.assertEquals("org.apache.xerces.dom.ElementNSImpl",b.getInsertionPoint().getClass().getName());
		} catch (Exception e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
    public void testAppendNotationDecl() {
		//TODO Test builder appendNotationDecl().
	}

	@Test
    public void testAppendUnparsedEntityDecl() {
		//TODO Test builder appendUnparsedEntityDecl().
	}

	@Test
    public void testAppendElementDecl() {
		//TODO Test builder appendElementDecl().
	}

	@Test
    public void testAppendInternalEntityDecl() {
		//TODO Test builder appendInternalEntityDecl().
	}

	@Test
    public void testAppendExternalEntityDecl() {
		//TODO Test builder appendExternalEntityDecl().
	}

	@Test
    public void testAppendAttributeDecl() {
		//TODO Test builder appendAttributeDecl().
	}

}
