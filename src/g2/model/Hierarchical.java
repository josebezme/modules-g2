package g2.model;

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

	public Set<Hierarchical> prereqs() {
		return prereqs;
	}
	
	public boolean hasPrereq(Hierarchical pre) {
		return prereqs.contains(pre);
	}
	
	public int numPrereqs() {
		return prereqs.size();
	}
	
	public abstract String toString();

}
