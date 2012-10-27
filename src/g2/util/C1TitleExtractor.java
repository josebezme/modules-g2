package g2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class C1TitleExtractor {
	public C1TitleExtractor() {

	}

	public static void main(String[] args) throws Exception {
		File inputFile = new File("urls/urls-paragraphs.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputFile));

		String url;
		while ((url = br.readLine()) != null) {
			process(url);
		}
	}

	public static void process(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select("p");
		System.out.println("===" + url + "===");

		for (int i = 0; i < paragraphs.size(); i++) {
			Elements possible = new Elements();
			{
				Element paragraph = paragraphs.get(i);
				Elements strong = paragraph.select("strong");
				Elements bold = paragraph.select("b");
				if (strong.size() != 0)
					possible.add(strong.first());
				if (bold.size() != 0)
					possible.add(bold.first());
			}

			for (int j = 0; j < possible.size(); j++) {
				Element ePossible = possible.get(j);
				String text = ePossible.text();
				if (likelyCourseName(text))
					System.out.println(text);
			}
		}
	}

	public static boolean likelyCourseName(final String text) {
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
