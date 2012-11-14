package g2;

import g2.model.Course;
import g2.model.Digraph;
import g2.util.C1CourseExtractor;
import g2.util.Utils;

import java.util.ArrayList;

import com.google.common.collect.Multimap;

public class DigraphTester {

	public static void main(String[] args) {

		String urls[] = Utils.getUrlsFromFile("urls/urls-paragraphs.txt");

		System.out.println("Getting courses...");
		Multimap<String, Course> host2courses = C1CourseExtractor.extractCourses(urls);
		System.out.println("Got courses...");
		ArrayList<Course> courses = new ArrayList<Course>(host2courses.get("www.uh.edu"));

		//Digraph.DigraphToFile("digr", courses);
		
		System.out.println("Done.");
	}
}