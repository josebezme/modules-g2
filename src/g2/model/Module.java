package g2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module extends Hierarchical {
  
  public List<String> titles;
  public List<String> synonyms;
  private WikiPage wikiPage;
  
  public Module(String title, String url) {
	  wikiPage = new WikiPage(url);
	  titles = new ArrayList<String>();
	  titles.add(title);
	  synonyms = wikiPage.redirects();
  }
  
  public Module(Integer i) {
	  titles = new ArrayList<String>();
	  titles.add(i.toString());
  }
  
  public void checkWikiForDependencyOn(Module that) {
	  if (wikiPage.refersTo(that)) 
		  addPrereq(that);
  }
  
  public void checkWikiForDependencyOn(Module that, double threshold) {
	  if (wikiPage.linkScore(that) > threshold) 
		  addPrereq(that);
  }
  
  public String toString() {
	  String s = titles.get(0);
	  for (int i = 1; i < titles.size(); i++)
		  s += ", " + titles.get(i);
	  return s;
  }
  
  public void addModule(Module m) {
	  for (String t : m.titles)
		  titles.add(t);
	  
	  removePrereq(m);
	  m.removePrereq(this);
	  
	  Set<Hierarchical> newPrereqs = m.prereqs();
	  for (Hierarchical pre : newPrereqs)
		  addPrereq(pre);
	  m.clearPrereqs();
  }
  
  public static void main(String[] args) {
		List<Module> modules = new ArrayList<Module>();
		//modules.add(new Module("Continuity", "Continuity_(mathematics)"));
		modules.add(new Module("Continuity", "http://en.wikipedia.org/wiki/Continuous_function"));
		modules.add(new Module("Integral", "http://en.wikipedia.org/wiki/Integral_(mathematics)"));
		modules.add(new Module("Implicit Function", "http://en.wikipedia.org/wiki/Implicit_function"));
		modules.add(new Module("Lagrange error bound", "http://en.wikipedia.org/wiki/Lagrange_error_bound"));
		modules.add(new Module("Derivative", "http://en.wikipedia.org/wiki/Derivative_(mathematics)"));
		modules.add(new Module("Euclidean space", "http://en.wikipedia.org/wiki/Euclidian_space"));
		modules.add(new Module("Transformation", "http://en.wikipedia.org/wiki/Transformation_(mathematics)"));
		modules.add(new Module("Uniform continuity", "http://en.wikipedia.org/wiki/Uniform_continuity"));
		modules.add(new Module("Uniformly convergent", "http://en.wikipedia.org/wiki/Uniformly_convergent"));	
		
		/*modules.add(new Module("Continuous_function"));
		modules.add(new Module("Integral_(mathematics)"));
		modules.add(new Module("Implicit_function"));
		modules.add(new Module("Lagrange_error_bound"));
		modules.add(new Module("Derivative_(mathematics)"));
		modules.add(new Module("Euclidian_space"));
		modules.add(new Module("Transformation_(mathematics)"));
		modules.add(new Module("Uniform_continuity"));
		modules.add(new Module("Uniformly_convergent"));	*/
		
		for (Module a : modules) {
			for (Module b : modules) {
				if (a != b)
					a.checkWikiForDependencyOn(b);
			}
		}
		
		boolean foundMerge;
		do {
			foundMerge = false;
			for (Module a : modules) {
				for (Module b : modules) {
					if (a != b && a.hasPrereq(b) && b.hasPrereq(a)) {
						System.out.println("MERGE: " + a.toString() + " AND " + b.toString());
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
		
		ArrayList<Module> newModules = new ArrayList<Module>();
		for (Module m : modules) {
			if (m.numPrereqs() > 0)
				newModules.add(m);
		}
		modules = newModules;
		
		ArrayList<Hierarchical> nodes = new ArrayList<Hierarchical>();
		for (Module m : modules)
			nodes.add(m);
		
		Digraph.DigraphToFile("Modules", nodes);
  }	
  
}