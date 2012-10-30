package g2.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class C1TitleExtractor {
	private static final Logger logger = Logger.getLogger(C1TitleExtractor.class);
	
	private C1TitleExtractor() { // static methods.
	}

	public static void main(String[] args) throws Exception {
		String urls[] = Utils.getUrlsFromFile("urls/urls-paragraphs.txt");
		
		Multimap<String, Element> titles = extractTitles(urls);
		
		for(String key: titles.keySet()) {
			logger.debug("host: " + key);
			for(Element course : titles.get(key)) {
				logger.debug("\t" + course.text());
			}
		}
	}

	public static Multimap<String, Element> extractTitles(String[] urls) {
		Multimap<String, Element> titles = LinkedHashMultimap.create();
		
		String host;
		Set<Element> potentialTitles;
		for(String url : urls) {
			try {
				host = Utils.getHost(url);
				
				potentialTitles = process(url);
				
				titles.putAll(host, potentialTitles);
				
			} catch (MalformedURLException e) {
				logger.debug("Unable to turn into url: " + url);
				e.printStackTrace();
			} catch (IOException e) {
				logger.debug("Error reading from: " + url );
				e.printStackTrace();
			}
		}
		
		return titles;
	}

	private static Set<Element> process(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements paragraphs = doc.select("p");
		logger.debug("===" + url + "===");
		
		Set<Element> potentialTitles = new HashSet<Element>();

		HashMap<Element, Element> potentialTitleToDescription =
				new HashMap<Element, Element>();
		
		for (int i = 0; i < paragraphs.size(); i++) {
			{
				Element paragraph = paragraphs.get(i);
				Elements strong = paragraph.select("strong");
				Elements bold = paragraph.select("b");
				
				Element first = null;
				if (strong.size() != 0)
					first = strong.first();
				else if (bold.size() != 0)
					first = bold.first();
				else
					continue;
				
				String text = first.text();
				if(likelyCourseName(text)) {
					potentialTitles.add(first);
					potentialTitleToDescription.put(first, paragraph);
				}
			}

			Iterator<Map.Entry<Element, Element>> itTitleDescription =
					potentialTitleToDescription.entrySet().iterator();
			while(itTitleDescription.hasNext()) {
				Map.Entry<Element, Element> next = itTitleDescription.next();
//				logger.debug("Course: " + next.getKey().text());
//				logger.debug("Value: " + next.getValue().text());
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
