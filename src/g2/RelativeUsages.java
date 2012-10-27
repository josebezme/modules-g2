package g2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

public class RelativeUsages {
	private static HashMap<String, TagUsage> urlsToTagUsages;
	
	static class TagUsage {
		private HashMap<String, Integer> tagUsage;
		private int totalTags;
		
		public TagUsage() {
			tagUsage = new HashMap<String, Integer>();
		}
		
		void increment(String tagName) {
			int usages = tagUsage.containsKey(tagName) ?
					tagUsage.get(tagName) : 0;
			usages++;
			tagUsage.put(tagName, usages);
			totalTags++;
		}
		
		int getCount(String tagName) {
			return tagUsage.containsKey(tagName) ? tagUsage.get(tagName) : 0;
		}
		
		int getTotalCount() {
			return totalTags;
		}
		
		Set<String> getTags() {
			return tagUsage.keySet();
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: java RelativeUsages urlList.txt");
			System.err.println("\t(one URL per line)");
			System.exit(1);
		}
		
		String fileName = args[0];
		
		urlsToTagUsages = new HashMap<String, TagUsage>();
		File inputFile = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));

		String url;
		while ((url = br.readLine()) != null) {
			process(url);
		}
	}

	public static void process(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		
		TagUsage tu = new TagUsage();
		urlsToTagUsages.put(url, tu);
		
		Elements children = new Elements();
		children.add(doc);
		
		Elements enqueue;
		while(children.size() != 0) {
			enqueue = new Elements();
			Iterator<Element> iterator = children.iterator();
			while(iterator.hasNext()) {
				Element element = iterator.next();
				tu.increment(element.tagName());
				enqueue.addAll(element.children());
				iterator.remove();
			}
			children = enqueue;
		}
		
		display();
	}
	
	public static void display() {
		for(Map.Entry<String, TagUsage> pair : urlsToTagUsages.entrySet()) {
			System.out.println(pair.getKey());
			TagUsage tu = pair.getValue();
			displayPretty(tu);
		}
	}
	
	private static void displayPretty(TagUsage tu) {
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(tu.getTags());
		Collections.sort(keys, Ordering.natural().reverse().onResultOf(Functions.forMap(tu.tagUsage)));

		int count = tu.getTotalCount();
		for(String tag : keys) {
			System.out.printf("%s\t\t%.2f\n", tag, tu.getCount(tag) / (double) count);
		}
	}
}
