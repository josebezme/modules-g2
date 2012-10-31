package g2.model;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.google.common.base.Joiner;

public class Course {

	public static class CourseId {
		public String dept;
		public String id;
		
		public CourseId(String dept, String id) {
			this.dept = dept;
			this.id = id;
		}
		
		@Override
		public String toString() {
			return "[" + dept + " " + id + "]";
		}
		
		@Override
		public boolean equals(Object obj) {
			if( obj instanceof CourseId ) {
				CourseId other = (CourseId) obj;
				return dept.equals(other.dept) && id.equals(other.id);
			}
			return false;
		}
	}
	
	public CourseId courseId;
	public String name;
	public Element titleElement;
	public Element htmlElement;

	private List<String> terms;
	private Set<Course> prereqs = new HashSet<Course>();
	
	public Course(String name, Element e, Element titleElement) {
		this.name = name;
		this.htmlElement = e;
		this.titleElement = titleElement;
		titleElement.remove();
	}

	public Course(String dept, String id, String name) {
		this.courseId = new CourseId(dept, id);
		this.name = name;
		terms = new ArrayList<String>();
	}

	public void setPrereq(Course c) {
		prereqs.add(c);
	}

	public Set<Course> prereqs() {
		return prereqs;
	}
	
	@Override
	public String toString() {
		Set<CourseId> courses = new HashSet<CourseId>();
		for(Course c : prereqs) {
			courses.add(c.courseId);
		}
		
		return courseId + " - " + name + " Prereqs[" + Joiner.on(",").join(courses) + "]";
	}

	public void addPrereq(Course prereq) {
		prereqs.add(prereq);
	}

}
