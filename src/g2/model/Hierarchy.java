package g2.model;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import g2.bing.SubTopic;

public class Hierarchy {
	private static final Logger logger = Logger.getLogger(Hierarchy.class);
	
	private List<Module> modules;
	private boolean doPruning;
	
	public Hierarchy(ArrayList<Module> testMods, boolean doPruning) {
		setModules(testMods);
		mergeCycles();
		if (doPruning)
			prune();
	}
	
	public Hierarchy(SubTopic[] topics, double threshold, boolean doPruning) {
		this.doPruning = doPruning;
		
		setModules(new ArrayList<Module>());
			
		logger.info("Creating modules...");
		// TODO: Check redirects so that we're not creating redundant modules
		for (SubTopic t : topics) {
			logger.info("Creating module for topic: " + t);
			Module m = new Module(t);
			if (m.titles != null && !m.titles.contains("Mathematics") && !m.synonyms.contains("Mathematics"))
				modules.add(m);
		}
			
		logger.info("Checking for module dependencies...");
		for (Module a : modules) {
			for (Module b : modules) {
				if (a != b && a.hasCourseIntersection(b))
					a.checkWikiForDependencyOn(b, threshold);
			}
		}
		
		logger.info("Merging cycles...");
		//mergeCycles();
		mergeCycles(2);
		//mergePairs();
		removeCycles();
		//mergeCycles();
		
		logger.info("Pruning redundant edges...");
		if (doPruning)
			prune();
	}
	
	public void mergeCycles(int limit) {

		boolean foundCycle;
		do {
			foundCycle = false;
			for (Module a : modules) {
				Module b = (Module) a.reachableFrom(a);
				while (b != null && a.size + b.size <= limit) {
					//System.out.println("SIZE: " + (a.size + b.size));
					foundCycle = true;
					a.addModule(b);
					
					for (Module c : modules) {
						if (c.hasPrereq(b)) {
							c.removePrereq(b);
							if (!c.hasPrereq(a))
								c.addPrereq(a);
						}
					}
					
					b = (Module) a.reachableFrom(a);
				}
			}
		} while (foundCycle);
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0 || numPostreqs(m) > 0)
				newModules.add(m);
		}
		modules = newModules;
	}
	
	public void mergePairs() {

		boolean foundCycle;
		do {
			foundCycle = false;
			for (Module a : modules) {
				Module b = (Module) a.reachableFrom(a);
				while (a.hasPrereq(b)) {
					foundCycle = true;
					a.addModule(b);
					
					for (Module c : modules) {
						if (c.hasPrereq(b)) {
							c.removePrereq(b);
							if (!c.hasPrereq(a))
								c.addPrereq(a);
						}
					}
					
					b = (Module) a.reachableFrom(a);
				}
			}
		} while (foundCycle);
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0 || numPostreqs(m) > 0)
				newModules.add(m);
		}
		setModules(newModules);
	}
	
	public void removeCycles() {

		boolean foundCycle;
		do {
			foundCycle = false;
			for (Module a : modules) {
				Module b = (Module) a.reachableFrom(a);
				while (b != null) {
					foundCycle = true;
					a.removeCycle();
					
					b = (Module) a.reachableFrom(a);
				}
			}
		} while (foundCycle);
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0 || numPostreqs(m) > 0)
				newModules.add(m);
		}
		modules = newModules;
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
							if (!c.hasPrereq(a))
								c.addPrereq(a);
						}
					}
					
					b = (Module) a.reachableFrom(a);
				}
			}
		} while (foundCycle);
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0 || numPostreqs(m) > 0)
				newModules.add(m);
		}
		setModules(newModules);
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
		modules.add(SubTopic.getSubtopic("Continuity", "http://en.wikipedia.org/wiki/Continuous_function"));
		modules.add(SubTopic.getSubtopic("Integral", "http://en.wikipedia.org/wiki/Integral_(mathematics)"));
		modules.add(SubTopic.getSubtopic("Implicit Function", "http://en.wikipedia.org/wiki/Implicit_function"));
		modules.add(SubTopic.getSubtopic("Lagrange error bound", "http://en.wikipedia.org/wiki/Lagrange_error_bound"));
		modules.add(SubTopic.getSubtopic("Derivative", "http://en.wikipedia.org/wiki/Derivative_(mathematics)"));
		modules.add(SubTopic.getSubtopic("Euclidean space", "http://en.wikipedia.org/wiki/Euclidian_space"));
		modules.add(SubTopic.getSubtopic("Transformation", "http://en.wikipedia.org/wiki/Transformation_(mathematics)"));
		modules.add(SubTopic.getSubtopic("Uniform continuity", "http://en.wikipedia.org/wiki/Uniform_continuity"));
		modules.add(SubTopic.getSubtopic("Uniformly convergent", "http://en.wikipedia.org/wiki/Uniformly_convergent"));	
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

	private boolean containsSubstring(List<String> synonyms, String match) {
		for (String s : synonyms) {
			if (s.toLowerCase().contains(match))
				return true;
		}
		return false;
	}
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

}
