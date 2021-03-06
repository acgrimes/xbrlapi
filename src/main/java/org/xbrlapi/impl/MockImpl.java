package org.xbrlapi.impl;

/**
 * A mock fragment object that is used only for testing.
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */


import org.xbrlapi.Mock;
import org.xbrlapi.builder.BuilderImpl;
import org.xbrlapi.utilities.XBRLException;

public class MockImpl extends FragmentImpl implements Mock {
	
	/**
     * 
     */
    private static final long serialVersionUID = 31223298171306717L;

    /**
	 * No argument constructor.
	 * @throws XBRLException
	 */
	public MockImpl() throws XBRLException {
		super();
        setBuilder(new BuilderImpl());
	}
	
	/**
	 * @param id The unique id of the fragment being created,
	 * within the scope of the containing data store.
	 * @throws XBRLException
	 */
	public MockImpl(String id) throws XBRLException {
		this();
		this.setIndex(id);
	}

	/**
	 * @param id The unique id of the fragment being created,
	 * within the scope of the containing DTS.
	 * @param namespace The namespace for the root element of the data in the fragment.
	 * @param name The local name for the root element of the data in the fragment.
	 * @param qName The QName for the root element of the data in the fragment.
	 * @throws XBRLException
	 */
	protected MockImpl(String id, String namespace, String name, String qName) throws XBRLException {
		this(id);
		getBuilder().appendElement(namespace, name, qName);
	}
	
    /**
     * @see Mock#appendDataElement(String, String, String)
     */
    public void appendDataElement(String namespace, String name, String qName) throws XBRLException {
        if (this.getBuilder() == null) throw new XBRLException("The fragment is not still being built.");
        getBuilder().appendElement(namespace, name, qName);
    }

	/**
	 * Set the information about the sequence to be followed to reach the parent element 
	 * of the fragment.
	 */
    public void setSequenceToParentElement(String sequence) throws XBRLException {
    	setMetaAttribute("sequenceToParentElement", sequence);
    }


	
}
