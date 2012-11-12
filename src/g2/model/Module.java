package g2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module extends Hierarchical {
  
  public List<String> titles;
  private WikiPage wikiPage;
  
  public Module(String title, String urlTitle) throws Exception {
	  titles = new ArrayList<String>();
	  titles.add(title);
	  wikiPage = new WikiPage(urlTitle);
  }
  
  public void checkWikiForDependencyOn(Module that) {
	  if (wikiPage.refersTo(that)) 
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
	  for (Hierarchical pre : newPrereqs) {
		  addPrereq(pre);
		  m.removePrereq(pre);
	  }
  }
  
  public static void main(String[] args) throws Exception {
		List<Module> modules = new ArrayList<Module>();
		modules.add(new Module("Continuity", "Continuity_(mathematics)"));
		modules.add(new Module("Integral", "Integral_(mathematics)"));
		modules.add(new Module("Implicit Function", "Implicit_function"));
		modules.add(new Module("Lagrange error bound", "Lagrange_error_bound"));
		modules.add(new Module("Derivative", "Derivative_(mathematics)"));
		modules.add(new Module("Euclidean space", "Euclidian_space"));
		modules.add(new Module("Transformation", "Transformation_(mathematics)"));
		modules.add(new Module("Uniform continuity", "Uniform_continuity"));
		modules.add(new Module("Uniformly convergent", "Uniformly_convergent"));	
		
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
		
		Digraph.DigraphToFile("moduleStructure", nodes);
  }	
  
}