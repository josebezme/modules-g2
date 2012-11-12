package g2.model;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.google.common.base.Joiner;
import com.google.gson.annotations.Expose;

public class Course extends Hierarchical {

	public static class CourseId {
		@Expose
		public String dept;
		@Expose
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
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
	}
	
	@Expose 
	public CourseId courseId;
	
	@Expose
	public String name;
	
	public Element titleElement;
	public Element htmlElement;

	private List<String> terms = new ArrayList<String>();
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
		setTerms(new ArrayList<String>());
	}
	
	public Course(String dept, String id) {
		this.courseId = new CourseId(dept, id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Course) {
			Course other = (Course) obj;
			if(courseId != null && other.courseId != null) {
				return courseId.equals(other.courseId);
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		Set<CourseId> courses = new HashSet<CourseId>();
		for(Course c : prereqs) {
			courses.add(c.courseId);
		}
		
		return courseId + " - " + name + " Prereqs[" + Joiner.on(",").join(courses) + "]";
	}
	
	@Override
	public int hashCode() {
		return (courseId != null) ? courseId.hashCode() : 
			(name != null) ? name.hashCode() :
				1000;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	
	public String toString() {
		return name;
	}
}
