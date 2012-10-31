package g2.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import g2.model.Course;

import com.google.common.collect.Multimap;

public class PrereqUtil {
	
	private static final String PRE_REQ_REGEX = "[pP]rereq(.*)\\.\\s";

	public static void populatePrereqs(Multimap<String, Course> host2course) {
		
		Pattern p = Pattern.compile(PRE_REQ_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher m;
		String prereqText;
		for(String host : host2course.keySet()) {
			Collection<Course> courses = host2course.get(host);
			
			for(Course c : courses) {
				String text = c.htmlElement.text();
				m = p.matcher(text);
				
				if(m.find()) {
					prereqText = m.group(1);
					
					for(Course prereq : courses) {
						if(
							c.courseId != null &&
							prereq.courseId != null && 
							prereqText.contains(prereq.courseId.id) &&
							c.courseId != prereq.courseId) {
							c.addPrereq(prereq);
						}
					}
				}
				
			}
		}
		
	}
}
