package g2.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class C1TitleExtractor {
	private C1TitleExtractor() { // static methods.
	}

	public static void main(String[] args) throws Exception {
		String urls[] = Utils.getUrlsFromFile("urls/urls-paragraphs.txt");
		
		Multimap<String, String> titles = extractTitles(urls);
		
		for(String key: titles.keySet()) {
			System.out.println("host: " + key);
			for(String course : titles.get(key)) {
				System.out.println("\t" + course);
			}
		}
	}

	public static Multimap<String, String> extractTitles(String[] urls) {
		Multimap<String, String> titles = LinkedHashMultimap.create();
		
		String host;
		Set<String> potentialTitles;
		for(String url : urls) {
			try {
				host = Utils.getHost(url);
				
				potentialTitles = process(url);
				
				titles.putAll(host, potentialTitles);
				
			} catch (MalformedURLException e) {
				System.out.println("Unable to turn into url: " + url);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error reading from: " + url );
				e.printStackTrace();
			}
		}
		
		return titles;
	}

	private static Set<String> process(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select("p");
		System.out.println("===" + url + "===");
		
		Set<String> potentialTitles = new HashSet<String>();

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
				if (likelyCourseName(text)) {
					potentialTitles.add(text);
				}
			}
		}
		
		return potentialTitles;
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
