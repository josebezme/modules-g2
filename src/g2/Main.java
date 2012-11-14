package g2;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import g2.bing.Bing;
import g2.bing.SubTopic;
import g2.model.Course;
import g2.model.Hierarchy;
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
			Multimap<Course, SubTopic> newTopics = bing.getTopicsFromCourses(host2courses.values(), area);
			topics.addAll(newTopics.values());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Create modules based on topics
		Hierarchy h = new Hierarchy(topics.toArray(new SubTopic[0]),0.0, pruning);
		h.writeDotFile("kahuna");
	}
}
