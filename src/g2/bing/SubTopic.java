package g2.bing;

import g2.model.Course;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SubTopic {
	public final String topic;
	public final String url;
	private LinkedList<Course> courses = new LinkedList<Course>();
	
	/**
	 * Maps topic to itself; however, note that we can use an incomplete
	 * subtopic to retrieve the fully specified subtopic. Perhaps hackish?
	 */
	private static HashMap<SubTopic, SubTopic> canonicalSet =
			new HashMap<SubTopic, SubTopic>();
	
	private SubTopic(String topic, String url) {
		this.url = url;
		this.topic = topic;
	}
	
	public String toString() {
		return topic + "\t" + url;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SubTopic) {
			return url.equals(((SubTopic) obj).url);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
	public static SubTopic getSubtopic(String topic, String url) {
		SubTopic st = new SubTopic(topic, url);
		if (canonicalSet.containsKey(st))
			return canonicalSet.get(st);
		
		canonicalSet.put(st, st);
		return st;
	}
	
	public void addCourse(Course c) {
		courses.add(c);
	}
	
	public List<Course> getCourses() {
		return (LinkedList<Course>) courses.clone();
	}
	
	public static Collection<SubTopic> getSubtopics() {
		return canonicalSet.values();
	}
}
