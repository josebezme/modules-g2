package g2.util.cleaners;

public class CourseNameCleaner {
	/**
	 * Attempts to remove extraneous bits of the course name.
	 * For example:
	 * MATH 3336: Discrete Mathematics => Discrete Mathematics
	 * MATH 2311: Introduction to Probability and Statistics => Probability and Statistics
	 * 
	 * Rules:
	 * Removes all numbers [0-9]+
	 * Removes entirely upper case words [A-Z]+
	 * Removes selected punctuation: [\.:]
	 * 
	 * @param name
	 * @return
	 */
	public static String clean(String name) {
		name = name.replaceAll("\\b[A-Z]+\\b", "");
		name = name.replaceAll("\\b[0-9]+\\b", "");
		name = name.replaceAll("[\\.:]", "");
		name = name.trim();
		return name;
	}
}
