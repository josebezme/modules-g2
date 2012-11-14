package g2.model;

import g2.bing.SubTopic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Module extends Hierarchical {
  
  public List<String> titles;
  public List<String> synonyms;
  private WikiPage wikiPage;
  private SubTopic origTopic;
  
  private List<SubTopic> subtopics;
  
  public Module(SubTopic t) {
	  wikiPage = new WikiPage(t.url);
	  titles = new ArrayList<String>();
	  titles.add(t.topic.trim());
	  synonyms = wikiPage.redirects();
	  subtopics = new ArrayList<SubTopic>();
	  subtopics.add(t);
	  origTopic = t;
  }
  
  public Module(Module m) {
	  wikiPage = m.wikiPage;
	  synonyms = m.synonyms;
	  titles = new ArrayList<String>();
	  titles.add(m.origTopic.topic.trim());
	  subtopics = new ArrayList<SubTopic>();
	  subtopics.add(m.origTopic);
	  origTopic = m.origTopic;
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
		  s += "\\n" + titles.get(i);
	  return s;
  }
  
	public List<Course> getCourses() {
		LinkedList<Course> courses = new LinkedList<Course>();
		for(SubTopic st : subtopics) {
			courses.addAll(st.getCourses());
		}
		return courses;
	}
	
	@Override
		public boolean equals(Object obj) {
			if(obj instanceof Module) {
				return origTopic.equals(((Module) obj).origTopic);
			}
			return false;
		}
	
	public boolean hasCourseIntersection(Module other) {
		List<Course> a = getCourses();
		List<Course> b = other.getCourses();
		final int sum = a.size() + b.size();
		HashSet<Course> union = new HashSet<Course>();
		union.addAll(a);
		union.addAll(b);
		return union.size() < sum;
	}
  
  public void addModule(Module m) {
	  for (String t : m.titles) {
		  if (!titles.contains(t))
			  titles.add(t);
		  if (synonyms.contains(t))
			  synonyms.remove(t);
	  }
	  for (String s : m.synonyms) {
		  if (!titles.contains(s) && !synonyms.contains(s))
			  synonyms.add(s);
	  }
	  
	  for (SubTopic s : m.subtopics) {
			  subtopics.add(s);
	  }
	  
	  removePrereq(m);
	  m.removePrereq(this);
	  
	  Set<Hierarchical> newPrereqs = m.prereqs();
	  for (Hierarchical pre : newPrereqs) {
		  if (!hasPrereq(pre))
			  addPrereq(pre);
	  }
	  m.clearPrereqs();
  }
  
  public static void main(String[] args) {
		List<Module> modules = new ArrayList<Module>();
		//modules.add(new Module("Continuity", "Continuity_(mathematics)"));
		modules.add(new Module(SubTopic.getSubtopic("Continuity", "http://en.wikipedia.org/wiki/Continuous_function")));
		modules.add(new Module(SubTopic.getSubtopic("Integral", "http://en.wikipedia.org/wiki/Integral_(mathematics)")));
		modules.add(new Module(SubTopic.getSubtopic("Implicit Function", "http://en.wikipedia.org/wiki/Implicit_function")));
		modules.add(new Module(SubTopic.getSubtopic("Lagrange error bound", "http://en.wikipedia.org/wiki/Lagrange_error_bound")));
		modules.add(new Module(SubTopic.getSubtopic("Derivative", "http://en.wikipedia.org/wiki/Derivative_(mathematics)")));
		modules.add(new Module(SubTopic.getSubtopic("Euclidean space", "http://en.wikipedia.org/wiki/Euclidian_space")));
		modules.add(new Module(SubTopic.getSubtopic("Transformation", "http://en.wikipedia.org/wiki/Transformation_(mathematics)")));
		modules.add(new Module(SubTopic.getSubtopic("Uniform continuity", "http://en.wikipedia.org/wiki/Uniform_continuity")));
		modules.add(new Module(SubTopic.getSubtopic("Uniformly convergent", "http://en.wikipedia.org/wiki/Uniformly_convergent")));	
		
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