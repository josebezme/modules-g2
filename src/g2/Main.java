package g2;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import g2.bing.Bing;
import g2.bing.SubTopic;
import g2.model.Course;
import g2.model.Hierarchy;
import g2.testing.hierarchy.TestPrerequisiteDepth;
import g2.util.C1CourseExtractor;
import g2.util.TermFilter;
import g2.util.Utils;

import com.google.common.collect.Multimap;

/**
 * The big kahuna
 */
public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		if(args.length < 0) {
			
		}
		// Arguments (get from cmd line)
		String area = "mathematics";
		String urls[] = Utils.getUrlsFromFile("urls/math-urls.txt");
		boolean pruning = true;
		
		// Get courses from urls
		logger.info("Retrieving courses...");
		Multimap<String, Course> host2courses = C1CourseExtractor.extractCourses(urls);
		// Filter terms from courses
		logger.info("Filtering terms from descriptions...");
		TermFilter.filterTerms(host2courses);
		
		
//		/* Daniel's testing labelling here */
//		Collection<Course> coursesCollection = host2courses.values();
//		TestPrerequisiteDepth.label(coursesCollection);
//		System.exit(0);
//		/* End labelling testing */
		
		
		
		
		// Estimate topics based on terms
		Set<SubTopic> topics = new HashSet<SubTopic>();
		
		logger.info("Counting terms...");
		
		int termCount = 0;
		for(Course c : host2courses.values()) {
			termCount += c.getTerms().size();
		}
		
		logger.info("Found " + termCount + " terms.");
		
		logger.info("Retreiving topics from courses for ...");
		try {
			Bing bing = new Bing(new File("output.qc"));
			Map<SubTopic, Integer> subtopicOccurence = new HashMap<SubTopic, Integer>();
			for(String host : host2courses.keySet()) {
				Collection<Course> courses = host2courses.get(host);
				Multimap<Course, SubTopic> newTopics = bing.getTopicsFromCourses(courses, area);
				
				Set<SubTopic> topicSet = new HashSet<SubTopic>(newTopics.values());
				for(SubTopic topic : topicSet) {
					
					Integer occurence = null;
					if(urls.length > 1) {
						if((occurence = subtopicOccurence.get(topic)) == null) {
							subtopicOccurence.put(topic, 1);
						} else {
							subtopicOccurence.put(topic, ++occurence);
							
							if(occurence > 1) {
								topics.add(topic);
							}
						}
					} else {
						topics.add(topic);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Create modules based on topics
		
		logger.info("Building hierarchy for " + topics.size() +  " topics ...");
		Hierarchy h = new Hierarchy(topics.toArray(new SubTopic[0]), 0.0, pruning);
		h.writeDotFile("kahuna");
	}
}
