package g2.model;

import java.util.List;
import java.util.ArrayList;

public class Hierarchy {
	private List<Module> modules;
	private boolean doPruning;
	
	public Hierarchy(ArrayList<Module> testMods, boolean doPruning) {
		modules = testMods;
		mergeCycles();
		if (doPruning)
			prune();
	}
	
	public Hierarchy(String[] wikiURLTitle, boolean doPruning) {
		this.doPruning = doPruning;
		
		modules = new ArrayList<Module>();
			
		// TODO: Check redirects so that we're not creating redundant modules
		for (String s : wikiURLTitle)
			modules.add(new Module(s));
			
		for (Module a : modules) {
			for (Module b : modules) {
				if (a != b)
					a.checkWikiForDependencyOn(b);
			}
		}
		
		mergeCycles();
		
		/*for (Module end : modules) {
			for (Module start : modules) {
				if (end.reachableFrom(start) != null)
					System.out.println(end.toString() + " is reachable from " + start.toString());
				else 
					System.out.println(end.toString() + " is not reachable from " + start.toString());
			}
		}*/
		
		if (doPruning)
			prune();
	}
	
	public void mergeCycles() {
		// TODO: Remove this block (should be unnecessary)
		boolean foundMerge;
		do {
			foundMerge = false;
			for (Module a : modules) {
				for (Module b : modules) {
					if (a != b && a.hasPrereq(b) && b.hasPrereq(a)) {
						//System.out.println("MERGE: " + a.toString() + " AND " + b.toString());
						foundMerge = true;
						a.addModule(b);
						for (Module c : modules) {
							if (c.hasPrereq(b)) {
								c.removePrereq(b);
								c.addPrereq(a);
							}
						}
					}
				}
			}
		} while (foundMerge);	
		
		boolean foundCycle;
		do {
			foundCycle = false;
			for (Module a : modules) {
				Module b = (Module) a.reachableFrom(a);
				while (b != null) {
					foundCycle = true;
					a.addModule(b);
					
					for (Module c : modules) {
						if (c.hasPrereq(b)) {
							c.removePrereq(b);
							c.addPrereq(a);
						}
					}
					
					b = (Module) a.reachableFrom(a);
				}
			}
		} while (foundCycle);
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0)
				newModules.add(m);
			else if (numPostreqs(m) > 0)
				newModules.add(m);
		}
		modules = newModules;
	}
	
	private int numPostreqs(Module pre) {
		int count = 0;
		for (Module post : modules) {
			if (post.hasPrereq(pre))
				count++;
		}
		return count;		
	}
	
	private void prune() {
		for (Module end : modules) {
			for (Module start : modules) {
				if (end != start && end.hasPrereq(start)) {
					end.removePrereq(start);
					if (end.reachableFrom(start) == null)
						end.addPrereq(start);
				}
			}
		}
	}
	
	public void writeDotFile(String filePrefix) {
		ArrayList<Hierarchical> nodes = new ArrayList<Hierarchical>();
		for (Module m : modules)
			nodes.add(m);
		
		Digraph.DigraphToFile(filePrefix, nodes);
	}
	
	public static void main(String[] args) {
		String[] urlSuffixes = {"Continuous_function", "Integral_(mathematics)", "Implicit_function",
								"Lagrange_error_bound", "Derivative_(mathematics)", "Euclidian_space",
								"Transformation_(mathematics)", "Uniform_continuity", "Uniformly_convergent"};
		Hierarchy h1 = new Hierarchy(urlSuffixes, true);
		h1.writeDotFile("HierarchyPruning3");
		
		ArrayList<Module> testMods = new ArrayList<Module>();
		for (int i = 0; i < 10; i++) {
			testMods.add(new Module(i));
		}
		
		for (int i = 0; i < 9; i++) {
			testMods.get(i).addPrereq(testMods.get(i+1));
		}
		testMods.get(9).addPrereq(testMods.get(5));
		
		Hierarchy h2 = new Hierarchy(testMods, true);
		h2.writeDotFile("Test");
	}

}
