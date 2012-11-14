package g2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class Hierarchical {
	/* The minimum path length to reach this node from any root (root is a
	 * node without any prerequisites). A root has depth 0.
	 * 
	 * The depth is externally set. The depth is -1 if not set.
	 */
	private int depth = DEPTH_NOT_SET;
	public static final int DEPTH_NOT_SET = -1;
	
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
	
	public void removeCycle() {
		HashSet<Hierarchical> visited = new HashSet<Hierarchical>();
		HashMap<Hierarchical, Hierarchical> path = new HashMap<Hierarchical, Hierarchical>();
		ArrayList<Hierarchical[]> toVisit = new ArrayList<Hierarchical[]>();
		
		for (Hierarchical h : prereqs) {
			toVisit.add(new Hierarchical[] {h, this} );
		}
		Hierarchical[] next = toVisit.remove(0);
		
		while (next[0] != this) {
			visited.add(next[0]);
			path.put(next[0], next[1]);
			
			for (Hierarchical h : next[0].prereqs) {
				if (!visited.contains(h))
					toVisit.add(new Hierarchical[] {h, next[0]});
			}
			
			next = toVisit.remove(0);
		}
		
		ArrayList<Hierarchical> cycle = new ArrayList<Hierarchical>();
		cycle.add(next[0]);
		Hierarchical prev = next[1];
		while (prev != this) {
			cycle.add(prev);
			prev = path.get(prev);
		}
		cycle.add(prev);
		
		int min = -1;
		int argmin = -1;
		for (int i = 0; i < cycle.size()-1; i++) {
			int temp = cycle.get(i+1).linkScore(cycle.get(i));
			if (temp > min) {
				min = temp;
				argmin = i+1;
			}
		}
		cycle.get(argmin).removePrereq(cycle.get(argmin-1));
	}
	
	public abstract int linkScore(Hierarchical that);
	
	public abstract String toString();
	
	/**
	 * See comments for field depth.
	 */
	public void setDepth(int depth) {
		System.out.println("Labelled: " + depth + " [" + toString() + "]");
		this.depth = depth;
	}
	
	/**
	 * See comments for field depth.
	 */
	public int getDepth() {
		return depth;
	}

}
