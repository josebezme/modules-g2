package g2.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public abstract class Hierarchical {
	private Set<Hierarchical> prereqs = new HashSet<Hierarchical>();
	
	public void addPrereq(Hierarchical prereq) {
		if (!prereqs.contains(prereq))
			prereqs.add(prereq);
	}
	
	public void removePrereq(Hierarchical prereq) {
		if (prereqs.contains(prereq))
			prereqs.remove(prereq);
	}
	
	public void clearPrereqs() {
		prereqs.clear();
	}

	public Set<Hierarchical> prereqs() {
		return prereqs;
	}
	
	public boolean hasPrereq(Hierarchical pre) {
		return prereqs.contains(pre);
	}
	
	public int numPrereqs() {
		return prereqs.size();
	}
	
	// Returns null if this hierarhcial object is not reachable from start
	public Hierarchical reachableFrom(Hierarchical start) {
		HashSet<Hierarchical> visited = new HashSet<Hierarchical>();
		ArrayList<Hierarchical> toVisit = new ArrayList<Hierarchical>();
		
		toVisit.add(this);
		while (toVisit.size() > 0) {
			Hierarchical next = toVisit.remove(0);
			visited.add(next);
			
			for (Hierarchical h : next.prereqs()) {
				if (h == start)
					return next;
				if (!visited.contains(h) && !toVisit.contains(h))
					toVisit.add(h);
			}
		}
		return null;
	}
	
	public abstract String toString();

}
