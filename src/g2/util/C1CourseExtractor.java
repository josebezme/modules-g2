package g2.util;

import g2.model.Course;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class C1CourseExtractor {
	private static final Logger logger = Logger.getLogger(C1CourseExtractor.class);
	
	private C1CourseExtractor() { // static methods.
	}

	public static void main(String[] args) throws Exception {		
//		String urls[] = Utils.getUrlsFromFile("urls/me-urls-usable.txt");
		String urls[] = {"http://apps.college.columbia.edu/unify/bulletinSearch.php?school=EN&departmentVar=MECE&header=me.columbia.edu/admin/html/course_header.html&footer=me.columbia.edu/admin/html/course_footer.html"};
		System.out.println("Extracting urls : " + Arrays.toString(urls));
		
		Multimap<String, Course> titles = extractCourses(urls);
		
		if(titles.size() == 0) {
			System.out.println("no titles found.");
		}
		for(String key: titles.keySet()) {
			logger.info("host: " + key);
			for(Course course : titles.get(key)) {
				logger.info("\t" + course);
			}
		}
	}

	public static Multimap<String, Course> extractCourses(String[] urls) {
		Multimap<String, Course> courses = LinkedHashMultimap.create();
		
		String host;
		Set<Course> potentialCourses;
		for(String url : urls) {
			try {
				host = Utils.getHost(url);
				
				potentialCourses = process(url);
				CourseIdUtil.populateCourseIds(potentialCourses);
				
				courses.putAll(host, Iterables.filter(potentialCourses, filterNullIds));
				
			} catch (MalformedURLException e) {
				logger.error("Unable to turn into url: " + url);
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error reading from: " + url );
				e.printStackTrace();
			}
		}
		
		PrereqUtil.populatePrereqs(courses);
		
		return courses;
	}
	
	private static final Predicate<Course> filterNullIds = new Predicate<Course>() {
		@Override
		public boolean apply(Course c) {
			if(c.courseId == null) {
				return false;
			}
			
			return true;
		}
	};
	
	private static Set<Course> process(String url) throws IOException {
		Set<Course> potentialTitles = process(url, "p");
		
		if(potentialTitles.size() < 1) {
			// try differen tag.
			potentialTitles = process(url, "li");
		}
		
		return potentialTitles;
	}

	private static Set<Course> process(String url, String tag) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select(tag);
		logger.debug("===" + url + "===");
		
		Set<Course> potentialCourses = new HashSet<Course>();

		Course c;
		for (int i = 0; i < paragraphs.size(); i++) {

			Element paragraph = paragraphs.get(i);
			Elements strong = paragraph.select("strong");
			Elements bold = paragraph.select("b");
			
			Element first = null;
			if (strong.size() != 0) {
				first = strong.first();
			} else if (bold.size() != 0) {
				first = bold.first();
			} else {
				continue;					
			}
			
			String text = first.text();
			if(likelyCourseName(text)) {
				c = new Course(text, paragraph, first);
				potentialCourses.add(c);
			}
		}
		
		return potentialCourses;
	}

	private static boolean likelyCourseName(final String text) {
		final String textLow = text.toLowerCase();
		final String textTrim = text.trim();
		if (textTrim.length() <= 4)
			return false;
		if (textLow.contains("semester"))
			return false;
		if (textLow.contains("prerequisite"))
			return false;
		if (textLow.contains("instructor"))
			return false;
		if (textLow.startsWith("note:"))
			return false;
		if (textLow.startsWith("credits:"))
			return false;
		return true;
	}
}
