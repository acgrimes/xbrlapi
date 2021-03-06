package org.xbrlapi;

import java.util.List;

import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public interface Schema extends SchemaContent {
    
    /**
     * Checks if the element form is qualified.
     * @return true if the element form is qualified and false otherwise.
     * @throws XBRLException
     */
    public boolean isElementFormQualified() throws XBRLException;

    /**
     * @return a list of SimpleLink fragments, one per XML Schema 
     * import used by this schema.
     * @throws XBRLException
     */
    public List<SimpleLink> getImports() throws XBRLException;
    
    /**
     * @return the list of schemas that import this schema.
     * @throws XBRLException
     */
    public List<Schema> getImporters() throws XBRLException;
    
    
    /**
     * @return a list of SimpleLink fragments, one per XML Schema 
     * include used by this schema.
     * @throws XBRLException
     */
    public List<SimpleLink> getIncludes() throws XBRLException;
    
    /**
     * @return a list of the extended links contained by the schema.
     * @throws XBRLException
     */
    public List<ExtendedLink> getExtendedLinks() throws XBRLException;    
    
    /**
     * Get the fragment list of element declarations (that are not concepts) in the schema.
     * @return the list of element declarations in the schema.
     * @throws XBRLException.
     */
    public List<Concept> getOtherElementDeclarations() throws XBRLException;
    
    /**
     * Get the fragment list of concepts in the schema.
     * @return the list of concepts in the schema.
     * @throws XBRLException.
     */
    public List<Concept> getConcepts() throws XBRLException;
    
    /**
     * Get the number of concepts defined in the schema.
     * @return the number of XBRL concepts defined in the 
     * schema.  Note that concepts that are overloaded with 
     * and XBRL Dimensions interpretation are excluded from this
     * count if the XBRL Dimensions loader is being used.
     * @throws XBRLException.
     */
    public long getConceptCount() throws XBRLException;

    
    
    /**
     * Get a specific concept by its name.
     * return the chosen concept or null if the concept does not exist.
     * @param name The name of the concept
     * @throws XBRLException
     */
    public Concept getConceptByName(String name) throws XBRLException;    

    /**
     * Get a list of concepts based on their type.
     * Returns null if no concepts match the selection criteria.
     *
     * @param namespace The namespaceURI of the concept type
     * @param localName The local name of the concept type
     * @return A list of concept fragments in the containing schema that
     * match the specified element type.
     * 
     * @throws XBRLException
     */
    public List<Concept> getConceptsByType(String namespace, String localName) throws XBRLException;
    
    /**
     * Get a list concepts based on their substitution group.
     * Returns null if no concepts match the selection criteria.
     *
     * @param namespace The namespaceURI of the concept type
     * @param localname The local name of the concept type
     * 
     * @return a list of concepts in the schema that match the specified
     * substitution group
     * 
     * @throws XBRLException
     */
    public List<Concept> getConceptsBySubstitutionGroup(String namespace, String localname) throws XBRLException;

    /**
     * Get a reference part declaration in a schema.
     * Returns null if the reference part does not exist in the schema.
     *
     * @param name The name attribute value of the reference part to be retrieved.
     * @throws XBRLException
     */
    public ReferencePartDeclaration getReferencePartDeclaration(String name) throws XBRLException;
    
    /**
     * Get a list of the reference part declarations in a schema.
     * @return a list of reference part declarations in the schema.
     * @throws XBRLException
     */
    public List<ReferencePartDeclaration> getReferencePartDeclarations() throws XBRLException;    
    
    /**
     * @return the list of role type definitions in the schema
     * @throws XBRLException
     */
    public List<RoleType> getRoleTypes() throws XBRLException;
    
    /**
     * @return the list of arcrole type definitions in the schema
     * @throws XBRLException
     */
    public List<ArcroleType> getArcroleTypes() throws XBRLException;
 
    /**
     * @return the list of global complex type declarations in this schema.
     * @throws XBRLException
     */
    public List<ComplexTypeDeclaration> getGlobalComplexTypes() throws XBRLException;

    /**
     * @return the list of global simple type declarations in this schema.
     * @throws XBRLException
     */
    public List<SimpleTypeDeclaration> getGlobalSimpleTypes() throws XBRLException;

    /**
     * 
     * @param <D> The type of the declaration being retrieved.
     * @param name The name of the declaration being retrieved.
     * @return the fragment representing the global schema declaration or null
     * if none exists.
     * @throws XBRLException if the name is null or if the specified type of the global
     * declaration is not consistent with the actual type of the declaration.
     */
    public <D extends SchemaDeclaration> D getGlobalDeclaration(String name) throws XBRLException;
    
}
