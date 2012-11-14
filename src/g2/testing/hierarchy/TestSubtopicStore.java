package g2.testing.hierarchy;

import g2.Main;
import g2.bing.SubTopic;

public class TestSubtopicStore {
	public static void main(String[] args) {
		Main.main(args);
		
		for (SubTopic t : SubTopic.getSubtopics()) {
			System.out.println("Topic: " + t);
			System.out.println("Courses: " + t.getCourses());
		}
	}
}
