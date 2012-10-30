package g2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class CourseIdExtractor {
	private static final Logger logger = Logger.getLogger(CourseIdExtractor.class);

	private static final String[] SPECIAL_CHARS = { 
			";", 
			":",
			".",
			"*",
			",", 
			")", 
			"(", 
			"[", 
			"]"
		};
	
	private static final String[] COMMON = {
		"for",
		"to",
		"the",
		"in"
	};
	
	private static final String ID_SUF = "( )?[0-9]+";

	private static final Function<String, String> FORMAT_ONE_WORDS = new Function<String, String>() {
		@Override
		public String apply(String orig) {
			for (String s : SPECIAL_CHARS) {
				orig = orig.replace(s, "");
			}
			return orig.trim().toUpperCase();
		}
	};

	private static final Predicate<String> REMOVE = new Predicate<String>() {

		public boolean apply(String input) {
			for(String common : COMMON) {
				if(common.equalsIgnoreCase(input)) {
					return false;
				}
			}
			
			return input.length() > 1;
		}
	};

	private static class Sequence {
		private Set<String> oneWord = new HashSet<String>();
		private List<FreqWrapper> lineFreq = new ArrayList<FreqWrapper>();
		private Set<String> twoWords = new HashSet<String>();

		@Override
		public String toString() {
			return  "oneWord: " + oneWord + "\n" + 
					"twoWords:" + twoWords + "\n" + 
					"freq:    " + lineFreq;
		}
	}

	private static class FreqWrapper implements Comparable<FreqWrapper> {
		private String word;
		private Integer freq;

		public FreqWrapper(String word, int freq) {
			this.word = word;
			this.freq = freq;
		}

		@Override
		public int compareTo(FreqWrapper wrapper) {
			return wrapper.freq.compareTo(freq);//reverse
		}
		
		@Override
		public String toString() {
			return word + ":" + freq;
		}
	}

	public static void main(String[] args) {
		String urls[] = Utils.getUrlsFromFile("urls/urls-paragraphs.txt");
		Multimap<String, Element> titles = C1TitleExtractor.extractTitles(urls);
		Map<Element, String> courseIds = extractCourseIds(titles);
	}
	
	public static Map<Element, String> extractCourseIds(Multimap<String, Element> titles) {
		Map<Element, String>  courseIds = new HashMap<Element, String>();
		
		 for(String host : titles.keySet()) {
		// Get all possible Alpha-numeric sequences of 1 or 2 words.
			logger.debug("host: " + host);
			Collection<Element> titleElements = titles.get(host);
			Sequence seq = splitSequences(titleElements);
	
			for (String word : seq.oneWord) {
				int freq = 0;
	
				for (String pair : seq.twoWords) {
					if (pair.contains(word)) {
						freq++;
					}
				}
	
				seq.lineFreq.add(new FreqWrapper(word, freq));
			}
			logger.debug("twoWord size: " + seq.twoWords.size());
			Collections.sort(seq.lineFreq);
			
			Matcher m;
			Pattern p;
			for(FreqWrapper wrap : seq.lineFreq) {
				p = Pattern.compile(wrap.word + ID_SUF, Pattern.CASE_INSENSITIVE);
				
				if(wrap.freq < 2) { // We only look for common patterns and theses are sorted
					break;
				}
				
				for(Element e: titleElements) {
					m = p.matcher(e.text());
					
					if(m.find()) {
						String id = m.group();
						logger.debug("Found id: " + id);
						if(!courseIds.containsKey(e)) {
							courseIds.put(e, id);
						}
					}
				}
			}
	
			logger.debug(seq);
		 }
		 
		 return courseIds;
	}

	private static Sequence splitSequences(Collection<Element> elements) {
		Sequence seq = new Sequence();
		
		Pattern p = Pattern.compile("([a-zA-Z]+)([0-9]+)");
		Matcher m;

		Iterable<String> it;
		for (Element e : elements) {
			List<String> words = Arrays.asList(e.text().split("\\s"));
			
			it = Iterables.transform(words, FORMAT_ONE_WORDS);
			it = Iterables.filter(it, REMOVE);
			
			words = Lists.newArrayList(it);

			String word1;
			String word2;
			String combined;
			for (int i = 0; i < words.size() - 1; i++) {
				word1 = words.get(i);
				
				m = p.matcher(word1);
				if(m.matches()) {
					word1 = m.group(1);
					word2 = m.group(2);
					words.add(word1);
				} else {
					word2 = words.get(i + 1);
				}
				
				combined = word1 + " " + word2;
				if(combined.matches(".*\\d.*")) {
					seq.twoWords.add(combined);
				}
			}
			seq.oneWord.addAll(words);
		}

		return seq;
	}

}
