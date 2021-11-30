package org.xbrlapi.networks;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xbrlapi.Arc;
import org.xbrlapi.ArcEnd;
import org.xbrlapi.Fragment;
import org.xbrlapi.Locator;
import org.xbrlapi.Relationship;
import org.xbrlapi.data.Store;
import org.xbrlapi.impl.ArcImpl;
import org.xbrlapi.impl.LocatorImpl;
import org.xbrlapi.impl.RelationshipImpl;
import org.xbrlapi.impl.RelationshipOrderComparator;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */
public class NetworksImpl implements Networks, Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -2248000947521760765L;

    private static final Logger logger = Logger.getLogger(NetworksImpl.class);	
	
	private HashMap<String,Arc> arcs = new HashMap<String,Arc>();
	
	/**
	 * @param arcIndex The index to check for in testing for whether
	 * the relationships defined by the arc are already in the networks.
	 * @return true if the relationships defined by the arc are already 
	 * in the networks.
	 */
	private boolean hasArc(String arcIndex) {
	    return arcs.containsKey(arcIndex);
	}
	
	/**
	 * The data store containing the information defining the networks.
	 */
	private Store store = null;
	
	// Map of networks: indexed by the arc role then the link role.
	private HashMap<String,HashMap<String,Network>> networks = new HashMap<String,HashMap<String,Network>>();
	
	/**
	 * @param store The data store containing the information defining the networks.
	 * @throws XBRLException if the data store is null.
	 */
	public NetworksImpl(Store store) throws XBRLException {
		super();
		if (store == null) throw new XBRLException("The data store must not be null.");
		this.store = store;
	}

	/**
	 * @see Networks#addNetwork(Network)
	 */
	public void addNetwork(Network network) throws XBRLException {
		String arcrole = network.getArcrole();
		String linkRole = network.getLinkRole();
		
		HashMap<String,Network> map = null;
		
		if (networks.containsKey(arcrole)) {
			map = networks.get(arcrole);
			if (map.containsKey(linkRole)) {
			    Network existingNetwork = this.getNetwork(linkRole,arcrole);
			    existingNetwork.add(network);
			}
			map.put(linkRole,network);
			return;
		}
		
		map = new HashMap<String,Network>();
		map.put(linkRole,network);
		networks.put(arcrole,map);		
		
	}

	/**
	 * @see Networks#getNetwork(String, String)
	 */
	public Network getNetwork(String linkRole, String arcrole)
			throws XBRLException {
		if (networks.containsKey(arcrole)) {
			Map<String,Network> map = networks.get(arcrole);
			if (map.containsKey(linkRole))
				return map.get(linkRole);
		}
		return null;
	}
	
	/**
	 * @see Networks#getNetworks(String)
	 */
	public Networks getNetworks(String arcrole) throws XBRLException {
		
	    Networks result = new NetworksImpl(this.getStore());
		List<Network> selectedNetworks = new LinkedList<Network>();
		List<String> linkRoles = getLinkRoles(arcrole);
		if (linkRoles.isEmpty()) return result;
		for (String linkRole: linkRoles) {
			selectedNetworks.add(this.getNetwork(linkRole,arcrole));
		}
		result.addAll(selectedNetworks);
		return result;
	}
	
	/**
	 * @see Networks#getSources(String, String)
	 */
	public <F extends Fragment> List<F>  getSources(String targetIndex, String arcrole) throws XBRLException {
		
		List<F> fragments = new Vector<F>();
		
    	Networks selectedNetworks = this.getNetworks(arcrole);
    	for (Network network: selectedNetworks) {
    		SortedSet<Relationship> relationships = network.getActiveRelationshipsTo(targetIndex);
        	for (Relationship relationship: relationships) {
        		fragments.add(relationship.<F>getSource());
        	}
    	}
		return fragments;
	}
	
	/**
	 * @see Networks#getSources(String, String, String)
	 */
	public <F extends Fragment> List<F>  getSources(String targetIndex, String linkRole, String arcrole) throws XBRLException {
		List<F> fragments = new Vector<F>();
		if (! hasNetwork(linkRole,arcrole)) return fragments;
    	Network network = this.getNetwork(linkRole,arcrole);
		SortedSet<Relationship> relationships = network.getActiveRelationshipsTo(targetIndex);
    	for (Relationship relationship: relationships) {
    		fragments.add(relationship.<F>getSource());
    	}
		return fragments;
	}	
	
	/**
	 * @see Networks#getTargets(String, String)
	 */
	public <F extends Fragment> List<F>  getTargets(String sourceIndex, String arcrole) throws XBRLException {
		
		List<F> fragments = new Vector<F>();
		
    	Networks selectedNetworks = getNetworks(arcrole);
    	for (Network network: selectedNetworks) {
    		SortedSet<Relationship> relationships = network.getActiveRelationshipsFrom(sourceIndex);
        	for (Relationship relationship: relationships) {
        		fragments.add(relationship.<F>getTarget());
        	}
    	}
		return fragments;
	}
	
	/**
	 * @see Networks#getTargets(String, String, String)
	 */
	public <F extends Fragment> List<F>  getTargets(String sourceIndex, String linkRole, String arcrole) throws XBRLException {
		
		List<F> fragments = new Vector<F>();
		if (! hasNetwork(linkRole, arcrole)) return fragments;
    	Network network = this.getNetwork(linkRole, arcrole);
		SortedSet<Relationship> relationships = network.getActiveRelationshipsFrom(sourceIndex);
    	for (Relationship relationship: relationships) {
    		fragments.add(relationship.<F>getTarget());
    	}
		return fragments;
	}	

	/**
	 * @see Networks#hasNetwork(String, String)
	 */
	public boolean hasNetwork(String linkRole, String arcrole) {
		if (networks.containsKey(arcrole)) {
			Map<String,Network> map = networks.get(arcrole);
			if (map.containsKey(linkRole)) return true;
		}
		return false;
	}
	
	/**
	 * @see Networks#addRelationship(Relationship)
	 */
	public void addRelationship(Relationship relationship) throws XBRLException {
		String arcrole = relationship.getArcrole();
		String linkRole = relationship.getLinkRole();
		
		if (hasNetwork(linkRole,arcrole)) {
			getNetwork(linkRole,arcrole).addRelationship(relationship);
			return;
		}
		Network network = new NetworkImpl(getStore(),linkRole,arcrole);
		network.addRelationship(relationship);
		addNetwork(network);
	}
	
    /**
     * @see Networks#addRelationships(String)
     */
    public void addRelationships(String arcrole) throws XBRLException {

        String query = "#roots#[@type='"+ ArcImpl.class.getName() +"' and */*[@xlink:type='arc' and @xlink:arcrole='"+ arcrole +"']]";
        List<Arc> arcs = this.getStore().<Arc>queryForXMLResources(query);
        for (Arc arc: arcs) {
            List<ArcEnd> sources = arc.getSourceFragments();
            List<ArcEnd> targets = arc.getTargetFragments();
            for (Fragment source: sources) {
                Fragment s = source;
                if (source.isa(LocatorImpl.class)) s = ((Locator) source).getTarget();
                for (Fragment target: targets) {
                    Fragment t = target;
                    if (target.isa(LocatorImpl.class)) t = ((Locator) target).getTarget();
                    this.addRelationship(new RelationshipImpl(arc,s,t));
                }
            }
        }

    }
	
	/**
	 * @see Networks#getSize()
	 */
	public int getSize() throws XBRLException {
		int size = 0;
		for (String arcrole: getArcroles()) {
			List<String > linkroles = getLinkRoles(arcrole);
			size += linkroles.size();
		}
		return size;
	}
	
	/**
	 * @see Networks#getArcroles()
	 */
	public List<String> getArcroles() throws XBRLException {
		List<String> roles = new Vector<String>();
		for (String value: networks.keySet()) {
			roles.add(value);
		}
		return roles;
	}
	
	/**
	 * @see Networks#getLinkRoles(String)
	 */
	public List<String> getLinkRoles(String arcrole) throws XBRLException {
		List<String> roles = new Vector<String>();

		if (networks.containsKey(arcrole)) {
			Map<String,Network> map = networks.get(arcrole);
			if (map == null) return roles;
			for (String value: map.keySet()) {
				roles.add(value);
			}
		}
		return roles;
	}
	
    /**
     * @see Iterable#iterator()
     */
    public Iterator<Network> iterator() {
        Set<Network> set = new HashSet<Network>();
        for (Map<String,Network> map: networks.values()) {
            set.addAll(map.values());
        }
        return set.iterator();
    }

    /**
     * @see Networks#getStore()
     */
    public Store getStore() {
        return this.store;
    }
 
    /**
     * @see Networks#addAll(Networks)
     */
    public void addAll(Networks networks) throws XBRLException {
        for (Network network: networks) {
            this.addNetwork(network);
        }
    }
    
    /**
     * @see Networks#addAll(List)
     */
    public void addAll(List<Network> networks) throws XBRLException {
        for (Network network: networks) {
            this.addNetwork(network);
        }
    }

    /**
     * @see Networks#complete()
     */
    public void complete() throws XBRLException {
        for (Network network: this) {
            network.complete();
        }
    }
    
    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arcs == null) ? 0 : arcs.hashCode());
        result = prime * result
                + ((networks == null) ? 0 : networks.hashCode());
        result = prime * result + ((store == null) ? 0 : store.hashCode());
        return result;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NetworksImpl other = (NetworksImpl) obj;
        if (arcs == null) {
            if (other.arcs != null)
                return false;
        } else if (!arcs.equals(other.arcs))
            return false;
        if (networks == null) {
            if (other.networks != null)
                return false;
        } else if (!networks.equals(other.networks))
            return false;
        if (store == null) {
            if (other.store != null)
                return false;
        } else if (!store.equals(other.store))
            return false;
        return true;
    }

    /**
     * @see Networks#addRelationships(Collection)
     */
    public void addRelationships(Collection<Relationship> relationships) throws XBRLException {
        for (Relationship relationship: relationships) 
            this.addRelationship(relationship);
    }

    /**
     * @see Networks#getActiveRelationships()
     */
    public List<Relationship> getActiveRelationships() throws XBRLException {
        List<Relationship> result = new Vector<Relationship>();
        for (Network network: this) {
            result.addAll(network.getAllActiveRelationships());
        }
        return result;
    }

    /**
     * @see Networks#getActiveRelationshipsFrom(String)
     */
    public SortedSet<Relationship> getActiveRelationshipsFrom(String index)
            throws XBRLException {
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        for (Network network: this) {
            result.addAll(network.getActiveRelationshipsFrom(index));
        }
        return result;
    }

    /**
     * @see Networks#getActiveRelationshipsTo(String)
     */
    public SortedSet<Relationship> getActiveRelationshipsTo(String index)
            throws XBRLException {
        SortedSet<Relationship> result = new TreeSet<Relationship>(new RelationshipOrderComparator());
        for (Network network: this) {
            result.addAll(network.getActiveRelationshipsTo(index));
        }
        return result;
    }
     
}
