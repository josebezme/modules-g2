package g2.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import g2.model.Course;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class TermFilter {
	private static final Logger logger = Logger.getLogger(TermFilter.class);
	
	private static final Predicate<String> FILTER_SENTENCES = new Predicate<String>() {
		
		@Override
		public boolean apply(String sentence) {
			if(sentence.contains("prereq") || sentence.contains("semester")) {
				return false;
			}
			
			Pattern p = Pattern.compile("[a-z ]");
			
			Matcher m = p.matcher(sentence);
			int letterCount = 0;
			while(m.find()) {
				letterCount++;
			}
			
			if(letterCount < sentence.length() * 0.75) {
				return false;
			}
			
			if(sentence.length() < 4) {
				return false;
			}
			
			return true;
		}
		
	};
	
	private static final Predicate<String> FILTER_TERMS = new Predicate<String>() {
		
		@Override
		public boolean apply(String term) {
			if(term.trim().length() < 2) {
				return false;
			}
			
			return true;
		}
	};
	
	
	public static void main(String[] args) {
		String urls[] = {"http://www.uh.edu/academics/catalog/colleges/nsm/courses/math/"};
		Multimap<String, Course> hosts = C1CourseExtractor.extractCourses(urls);
		
		filterTerms(hosts);
	}
	
	public static void filterTerms(Multimap<String, Course> hosts2courses) {
		for(String host : hosts2courses.keySet()) {
			List<Course> courses = new LinkedList<Course>(hosts2courses.get(host));
//			for(int i = 0; i < 3; i++) {
//				Course c = courses.get(i);
			for(Course c : courses) {
				logger.info("Getting terms for course: " + c);
				String desc = c.htmlElement.text().toLowerCase();
				logger.debug(desc);
				
				String sentences[] = desc.split("(\\.\\s|\\.$)");
				
				logger.debug("Sentences:");
				for(String sentence : sentences) {
					logger.debug("\t" + sentence);
				}
				
				Iterable<String> it = Iterables.filter(Arrays.asList(sentences), FILTER_SENTENCES);
				sentences = Iterables.toArray(it, String.class);
				
				List<String> termList = new LinkedList<String>();
				for(String sentence : sentences) {
					String terms[] = sentence.split("(,|;|\\sand\\s|\\sor\\s)");
					
					for(String term : terms) {
						
						if(term.contains("with")) {
							String firstTerm = term.substring(0, term.indexOf("with"));
							term = term.substring(term.indexOf("with") + "with".length(), term.length());
							
							termList.add(firstTerm);
							termList.add(term);
						} else if (term.contains("from")) {
							term = term.substring(term.indexOf("from") + "from".length(), term.length());
							termList.add(term);
						} else {
							termList.add(term.trim());
						}
						
					}
				}
				
				it = Iterables.filter(termList, FILTER_TERMS);
				String terms[] = Iterables.toArray(it, String.class);
				termList.clear();
				termList.addAll(Arrays.asList(terms));
				
				for(String term : termList) {
					logger.info(term);
				}
				
			}
		}
	}
}
