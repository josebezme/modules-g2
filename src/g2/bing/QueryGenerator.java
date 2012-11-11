package g2.bing;

public class QueryGenerator {
	public static String generateQueryForCourse(String course, String term) {
		return term + " " + course;
	}
	
	public static String generateQueryForSubjectArea(String area, String term) {
		return term + " " + area;
	}
}
