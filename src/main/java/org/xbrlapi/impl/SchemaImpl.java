package org.xbrlapi.impl;

import java.util.List;
import java.util.Vector;

import org.xbrlapi.ArcroleType;
import org.xbrlapi.ComplexTypeDeclaration;
import org.xbrlapi.Concept;
import org.xbrlapi.ExtendedLink;
import org.xbrlapi.Linkbase;
import org.xbrlapi.ReferencePartDeclaration;
import org.xbrlapi.RoleType;
import org.xbrlapi.Schema;
import org.xbrlapi.SchemaDeclaration;
import org.xbrlapi.SimpleLink;
import org.xbrlapi.SimpleTypeDeclaration;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SchemaImpl extends SchemaContentImpl implements Schema {
		
    /**
     * 
     */
    private static final long serialVersionUID = -7823792162479874585L;

    /**
     * @see Schema#getTargetNamespace()
     */
    public String getTargetNamespace() throws XBRLException {
    	if (! getDataRootElement().hasAttribute("targetNamespace")) return null;
		return getDataRootElement().getAttribute("targetNamespace");
    }
    
    /**
     * @see Schema#isElementFormQualified()
     */
    public boolean isElementFormQualified() throws XBRLException {
    	if (! getDataRootElement().getAttribute("elementFormDefault").equals("qualified")) return false;
		return true;
    }

    /**
     * @see Schema#getImports()
     */
    public List<SimpleLink> getImports() throws XBRLException {
    	String query = "#roots#[@parentIndex='" + this.getIndex() + "' and @type='org.xbrlapi.impl.SimpleLinkImpl' and */xsd:import]";
    	return getStore().<SimpleLink>queryForXMLResources(query);
    }

    /**
     * @see Schema#getImporters()
     */
    public List<Schema> getImporters() throws XBRLException {
        String query = "for $root in #roots#[@type='"+SimpleLinkImpl.class.getName()+"'] where $root/xbrlapi:data/xsd:import/@namespace='" + getSchema().getTargetNamespace() + "' order by $root/xbrlapi:data/xsd:import/@namespace ascending return $root";
        List<Schema> result = new Vector<Schema>();
        List<SimpleLink> links = this.getStore().queryForXMLResources(query);
        for (SimpleLink link: links) {
            try {
                result.add((Schema) link.getTarget());
            } catch (ClassCastException e) {
                throw new XBRLException("The schema import points illegally to a non-schema fragment.", e);
            }
        }
        return result;
    }    
    
    /**
     * @see Schema#getIncludes()
     */
    public List<SimpleLink> getIncludes() throws XBRLException {
        String query = "#roots#[@parentIndex='" + this.getIndex() + "' and @type='org.xbrlapi.impl.SimpleLinkImpl' and */xsd:include]";
        return getStore().<SimpleLink>queryForXMLResources(query);
    }
    
    /**
     * @see Schema#getExtendedLinks()
     */
    public List<ExtendedLink> getExtendedLinks() throws XBRLException {
    	List<Linkbase> linkbases = getStore().<Linkbase>getChildFragments(LinkbaseImpl.class,getIndex());
    	logger.debug("The schema contains " + linkbases.size() + " linkbases.");
    	List<ExtendedLink> links = new Vector<ExtendedLink>();
    	for (Linkbase linkbase: linkbases) {
        	links.addAll(getStore().<ExtendedLink>getChildFragments(ExtendedLinkImpl.class,linkbase.getIndex()));
    	}
    	return links;
    }
    
    /**
     * @see Schema#getConcepts()
     */
    public List<Concept> getConcepts() throws XBRLException {
    	return this.<Concept>getChildren("Concept");
    }
    
    /**
     * @see Schema#getConceptCount()
     */
    public long getConceptCount() throws XBRLException {
        String query = "for $root in #roots#[@parentIndex='" + this.getIndex() + "' and @type='"+ ConceptImpl.class.getName() +"'] return $root";
        return this.getStore().queryCount(query);
    }    
    
    /**
     * @see Schema#getOtherElementDeclarations()
     */
    public List<Concept> getOtherElementDeclarations() throws XBRLException {
        return this.<Concept>getChildren("org.xbrlapi.impl.ElementDeclarationImpl");
    }
    

    /**
     * @see Schema#getConceptByName(String)
     */
    public Concept getConceptByName(String name) throws XBRLException {
    	String query = "#roots#[@type='org.xbrlapi.impl.ConceptImpl' and @parentIndex='" + getIndex() + "' and "+ Constants.XBRLAPIPrefix+ ":" + "data/*/@name='" + name + "']";
    	List<Concept> concepts = getStore().<Concept>queryForXMLResources(query);
    	if (concepts.size() == 0) return null;
    	if (concepts.size() > 1) throw new XBRLException("The concept name is not unique to the schema.");
    	return concepts.get(0);
    }

    /**
     * @see Schema#getConceptsByType(String, String)
     */
    public List<Concept> getConceptsByType(String namespace, String localName) throws XBRLException {
    	List<Concept> matches = new Vector<Concept>();
    	List<Concept> concepts = getConcepts();
    	for (Concept concept: concepts) {
			if (concept.getTypeNamespace().equals(namespace) && 
				concept.getTypeLocalname().equals(localName)) {
				matches.add(concept);
			}
		}
		return matches;
	}
    
    /**
     * @see Schema#getConceptsBySubstitutionGroup(String, String)
     */
    public List<Concept> getConceptsBySubstitutionGroup(String namespace, String localname) throws XBRLException {
    	Vector<Concept> matches = new Vector<Concept>();
    	List<Concept> concepts = getConcepts();
    	for (Concept concept: concepts) {
			if (concept.getSubstitutionGroupNamespace().equals(namespace) && 
					concept.getSubstitutionGroupLocalname().equals(localname)) {
					matches.add(concept);
			}
    	}
		return matches;
    }



	

    /**
     * @see Schema#getReferencePartDeclaration(String)
     */
    public ReferencePartDeclaration getReferencePartDeclaration(String name) throws XBRLException {
    	for (int i=0; i<getReferencePartDeclarations().size(); i++) {
    		if (getReferencePartDeclarations().get(i).getLocalname().equals(name))
				return getReferencePartDeclarations().get(i);
    	}
    	return null;
    }
    
    /**
     * @see Schema#getReferencePartDeclarations()
     */
    public List<ReferencePartDeclaration> getReferencePartDeclarations() throws XBRLException {
    	return this.getChildren("ReferencePartDeclaration");
    }

    /**
     * @see Schema#getArcroleTypes()
     */
    public List<ArcroleType> getArcroleTypes() throws XBRLException {
        return this.<ArcroleType>getChildren("ArcroleType");
    }

    /**
     * @see Schema#getRoleTypes()
     */
    public List<RoleType> getRoleTypes() throws XBRLException {
        return this.<RoleType>getChildren("RoleType");
    }

    /**
     * @see Schema#getGlobalComplexTypes()
     */
    public List<ComplexTypeDeclaration> getGlobalComplexTypes()
            throws XBRLException {
        return getStore().<ComplexTypeDeclaration>getChildFragments(ComplexTypeDeclarationImpl.class,getIndex());
    }

    /**
     * @see Schema#getGlobalDeclaration(String)
     */
    public <D extends SchemaDeclaration> D getGlobalDeclaration(String name)
            throws XBRLException {
        if (name == null) throw new XBRLException("The name must not be null.");
        String query = "for $root in #roots#[@parentIndex='"+getIndex()+"'] where $root/*/*/@name='"+name+"' return $root";
        try {
            List<D> results = getStore().<D>queryForXMLResources(query);
            if (results.size() == 1) return results.get(0);
            if (results.size() == 0) return null;
            throw new XBRLException("The schema must not contain more than one global declaration with name " + name);
        } catch (ClassCastException e) {
            throw new XBRLException("The declaration is not of the specified data type.",e);
        }
    }

    /**
     * @see Schema#getGlobalSimpleTypes()
     */
    public List<SimpleTypeDeclaration> getGlobalSimpleTypes()
            throws XBRLException {
        return getStore().<SimpleTypeDeclaration>getChildFragments(SimpleTypeDeclarationImpl.class,getIndex());
    }
    
}