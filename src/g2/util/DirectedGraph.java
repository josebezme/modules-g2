package g2.util;

import g2.model.Hierarchical;
import g2.model.Module;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class DirectedGraph {
	
	public static List<Module> turnToDirected(List<Module> modules) {
		List<Module> sorted = new LinkedList<Module>(byDegree.sortedCopy(modules));
		
		
		Map<Module, Boolean> visited = new HashMap<Module, Boolean>();
		for(Module m : modules) {
			visited.put(m, false);
		}
		
		Stack<Module> stack = new Stack<Module>();
		List<Module> newGraph = new LinkedList<Module>();
		
		List<Module> leafModules = new LinkedList<Module>(sorted);
		for(Module m : modules) {
			for(Hierarchical h :m.prereqs()) {
				leafModules.remove(h);
			}
		}
		
		for(Module m : leafModules) {
			stack.push(m);
		}
		
		Module current;
		int count = 0;
		while(stack.size() > 0) {
			current = stack.pop();
			
			Module m = new Module(current);
			sorted.remove(current);
			newGraph.add(m);
			
			for(Hierarchical prereqH : current.prereqs()) {
				Module prereq = (Module) prereqH;
				if(sorted.contains(prereq)) {
					m.addPrereq(prereq);
					stack.add(prereq);
				}
			}
			
			if(stack.size() == 0 && sorted.size() != 0) {
				stack.push(sorted.get(0));
			}
		}
		
		
		return newGraph;
	}
	
	public static Ordering<Module> byDegree = new Ordering<Module>() {
		
		@Override
		public int compare(Module left, Module right) {
			return Ints.compare(left.numPrereqs(), right.numPrereqs());
		}
	};
}
