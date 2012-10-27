package g2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test {
	
	public static void main(String[] args) {
		try {
			// Get URL
			Document doc = Jsoup.connect("http://www.math.purdue.edu/academic/courses/").get();
			
			// Strip all html.
			System.out.println(doc.text());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
