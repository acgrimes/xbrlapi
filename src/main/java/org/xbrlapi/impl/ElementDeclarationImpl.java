package org.xbrlapi.impl;


import java.util.List;

import org.xbrlapi.ComplexTypeDeclaration;
import org.xbrlapi.ElementDeclaration;
import org.xbrlapi.utilities.Constants;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class ElementDeclarationImpl extends SchemaContentDeclarationImpl implements ElementDeclaration {

    /**
     * The serial version UID.
     * @see 
     * http://java.sun.com/javase/6/docs/platform/serialization/spec/version.html#6678
     * for information about what changes will require the serial version UID to be
     * modified.
     */
    private static final long serialVersionUID = -4686068793132435426L;

    /**
     * @see ElementDeclaration#isAbstract()
     */
    public boolean isAbstract() throws XBRLException {
        if (getDataRootElement().getAttribute("abstract").equals("true")) {
            return true;
        }
        return false;
    }
    
    /**
     * @see ElementDeclaration#isNillable()
     */
    public boolean isNillable() throws XBRLException {
    	if (getDataRootElement().getAttribute("nillable").equals("true")) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * @see ElementDeclaration#isItem()
     */
     public boolean isItem() throws XBRLException {
         String sgName = this.getSubstitutionGroupLocalname();
         logger.debug(sgName);
         if (sgName == null) return false;
         String sgNamespace = this.getSubstitutionGroupNamespace();
         logger.debug(sgNamespace);
         String query = "#roots#[*/xsd:element/@name='" + sgName + "']";
         logger.debug(query);
         List<ElementDeclaration> declarations = getStore().<ElementDeclaration>queryForXMLResources(query);
         for (ElementDeclaration declaration: declarations) {
             if (declaration.getTargetNamespace().equals(sgNamespace)) {
                 if (declaration.getName().equals("item") && declaration.getTargetNamespace().equals(Constants.XBRL21Namespace)) {
                     return true;
                 }
                 try {
                     return declaration.isItem();
                 } catch (XBRLException e) {
                     return false;
                 }
             }
         }
         throw new XBRLException("The substitution group is invalid.");
     }
     
     /**
      * @see ElementDeclaration#isTuple()
      */
      public boolean isTuple() throws XBRLException {
          if (this.getName().equals("tuple") && this.getTargetNamespace().equals(Constants.XBRL21Namespace)) return true;
          if (! this.hasSubstitutionGroup()) return false;
          return this.getSubstitutionGroupDeclaration().isTuple();
      }
      
      /**
       * @see ElementDeclaration#substitutesFor(ElementDeclaration)
       */
      public boolean substitutesFor(ElementDeclaration candidate) throws XBRLException {
          if (! this.hasSubstitutionGroup() ) return false;
          ElementDeclaration parent = this.getSubstitutionGroupDeclaration();
          if (parent.equals(candidate)) return true;
          return parent.substitutesFor(candidate);
      }

      /**
       * @see ElementDeclaration#hasSubstitutionGroup()
       */
      public boolean hasSubstitutionGroup() throws XBRLException {
          return getDataRootElement().hasAttribute("substitutionGroup");
      }      
    
    /**
     * @see ElementDeclaration#getSubstitutionGroupNamespace()
     */
    public String getSubstitutionGroupNamespace() throws XBRLException {
    	String qname = getSubstitutionGroupQName();
    	if (qname.equals("") || (qname == null)) throw new XBRLException("The element declaration does not declare its XML Schema substitution group via a substitutionGroup attribute.");   	
	    return getNamespaceFromQName(qname, getDataRootElement());
    }
    
    /**
     * @see ElementDeclaration#getSubstitutionGroupNamespaceAlias()
     */    
    public String getSubstitutionGroupNamespaceAlias() throws XBRLException {
    	String sg = getSubstitutionGroupQName();
    	if (sg.equals("") || (sg == null))
			throw new XBRLException("The element declaration does not declare its substitution group via a substitutionGroup attribute.");    	
    	return getPrefixFromQName(sg);
    }

    /**
     * @see ElementDeclaration#getSubstitutionGroupQName()
     */  
    public String getSubstitutionGroupQName() throws XBRLException {
    	if (getDataRootElement().hasAttribute("substitutionGroup"))
    		return getDataRootElement().getAttribute("substitutionGroup");
    	return null;
    }

    /**
     * @see ElementDeclaration#getSubstitutionGroupDeclaration()
     */
    public ElementDeclaration getSubstitutionGroupDeclaration() throws XBRLException {
        
        ElementDeclaration result = null;
        if (this.hasSubstitutionGroup()) {
            try {
                result = (ElementDeclaration) getStore().getSchemaContent(this.getSubstitutionGroupNamespace(),this.getSubstitutionGroupLocalname());
                if (result == null) throw new XBRLException("The substitution group element declaration is not declared in a schema contained in the data store.");
            } catch (ClassCastException cce) {
                throw new XBRLException("The Substitution Group XML Schema element declaration is  of the wrong fragment type.",cce);
            }
        }
        return result;
    }
    
    /**
     * @see ElementDeclaration#getSubstitutionGroupLocalname()
     */  
    public String getSubstitutionGroupLocalname() throws XBRLException {
    	String sg = getSubstitutionGroupQName();
    	if (sg == null) return null;
    	if (sg.equals(""))
			throw new XBRLException("The element declaration must not have an empty substitution group attribute.");    	
    	return getLocalnameFromQName(sg);
    }

    /**
     * @see ElementDeclaration#hasLocalComplexType()
     */
    public boolean hasLocalComplexType() throws XBRLException {
        return (getStore().queryCount("#roots#[@parentIndex='"+getIndex()+"' and @type='org.xbrlapi.impl.ComplexTypeDeclarationImpl']") == 1);
    }    
    
    /**
     * @see ElementDeclaration#getLocalComplexType()
     */
    public ComplexTypeDeclaration getLocalComplexType() throws XBRLException {
        List<ComplexTypeDeclaration> ctds = this.getChildren("ComplexTypeDeclaration");
        if (ctds.size() > 1) throw new XBRLException("The element has too many local complex types.");
        if (ctds.size() == 0) throw new XBRLException("The element does not have a local complex type.");
        return ctds.get(0);
    }

    /**
     * @see ElementDeclaration#isFinalForRestriction()
     */
    public boolean isFinalForRestriction() throws XBRLException {
        String value = getDataRootElement().getAttribute("final");
        if (value.matches("restriction")) return true;
        return value.equals("#all");
    }
    
    /**
     * @see ElementDeclaration#isFinalForRestriction()
     */
    public boolean isFinalForExtension() throws XBRLException {
        String value = getDataRootElement().getAttribute("final");
        if (value.matches("extension")) return true;
        return value.equals("#all");
    }
    
    /**
     * @see ElementDeclaration#isBlockingSubstitution()
     */
    public boolean isBlockingSubstitution() throws XBRLException {
        String value = getDataRootElement().getAttribute("block");
        if (value.matches("substitution")) return true;
        return value.equals("#all");
    }
    
    /**
     * @see ElementDeclaration#isBlockingRestriction()
     */
    public boolean isBlockingRestriction() throws XBRLException {
        String value = getDataRootElement().getAttribute("block");
        if (value.matches("restriction")) return true;
        return value.equals("#all");
    }
    
    /**
     * @see ElementDeclaration#isBlockingRestriction()
     */
    public boolean isBlockingExtension() throws XBRLException {
        String value = getDataRootElement().getAttribute("block");
        if (value.matches("extension")) return true;
        return value.equals("#all");
    }


    
    /**
     * @see ElementDeclaration#getMaxOccurs()
     */
    public String getMaxOccurs() throws XBRLException {
        if (this.isGlobal()) throw new XBRLException("The element is global.");
        if (getDataRootElement().hasAttribute("maxOccurs")) return getDataRootElement().getAttribute("maxOccurs");
        return "1";
    }

    /**
     * @see ElementDeclaration#getMinOccurs()
     */
    public String getMinOccurs() throws XBRLException {
        if (this.isGlobal()) throw new XBRLException("The element is global.");
        if (getDataRootElement().hasAttribute("minOccurs")) return getDataRootElement().getAttribute("minOccurs");
        return "1";
    }
    
    
}