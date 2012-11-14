package g2.model;

import java.util.List;
import java.util.ArrayList;

import g2.bing.SubTopic;

public class Hierarchy {
	private List<Module> modules;
	private boolean doPruning;
	
	public Hierarchy(ArrayList<Module> testMods, boolean doPruning) {
		modules = testMods;
		mergeCycles();
		if (doPruning)
			prune();
	}
	
	public Hierarchy(SubTopic[] topics, double threshold, boolean doPruning) {
		this.doPruning = doPruning;
		
		modules = new ArrayList<Module>();
			
		// TODO: Check redirects so that we're not creating redundant modules
		for (SubTopic t : topics)
			modules.add(new Module(t.topic, t.url));
			
		for (Module a : modules) {
			for (Module b : modules) {
				if (a != b)
					a.checkWikiForDependencyOn(b, threshold);
			}
		}
		
		mergeCycles();
		
		if (doPruning)
			prune();
	}
	
	public void mergeCycles() {

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
		/*String[] urlSuffixes = {"Continuous_function", "Integral_(mathematics)", "Implicit_function",
								"Lagrange_error_bound", "Derivative_(mathematics)", "Euclidian_space",
								"Transformation_(mathematics)", "Uniform_continuity", "Uniformly_convergent"};*/
		ArrayList<SubTopic> modules = new ArrayList<SubTopic>();
		modules.add(new SubTopic("Continuity", "Continuous_function"));
		modules.add(new SubTopic("Integral", "Integral_(mathematics)"));
		modules.add(new SubTopic("Implicit Function", "Implicit_function"));
		modules.add(new SubTopic("Lagrange error bound", "Lagrange_error_bound"));
		modules.add(new SubTopic("Derivative", "Derivative_(mathematics)"));
		modules.add(new SubTopic("Euclidean space", "Euclidian_space"));
		modules.add(new SubTopic("Transformation", "Transformation_(mathematics)"));
		modules.add(new SubTopic("Uniform continuity", "Uniform_continuity"));
		modules.add(new SubTopic("Uniformly convergent", "Uniformly_convergent"));	
		Hierarchy h1 = new Hierarchy(modules.toArray(new SubTopic[0]), 0.0, true);
		h1.writeDotFile("Hierarchy");
		
		/*ArrayList<Module> testMods = new ArrayList<Module>();
		for (int i = 0; i < 10; i++) {
			testMods.add(new Module(i));
		}
		
		for (int i = 0; i < 9; i++) {
			testMods.get(i).addPrereq(testMods.get(i+1));
		}
		//testMods.get(9).addPrereq(testMods.get(5));
		//testMods.get(6).addPrereq(testMods.get(4));
		//testMods.get(6).removePrereq(testMods.get(4));
		testMods.get(4).addPrereq(testMods.get(1));
		
		Hierarchy h2 = new Hierarchy(testMods, true);
		h2.writeDotFile("Test5");*/
	}

}
