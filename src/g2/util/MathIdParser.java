package g2.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class MathIdParser {
	
	private static final String PREFIXES[] = {
		"m",
		"ma",
		"mat",
		"math",
		"mathematics"
	};
	
	private static final String MATH_REGEX;
	
	static {
		String regex = 
		
		// Add prefixes
			"(" +
			Joiner.on('|').join(PREFIXES) +
			")" +
		
		// Optional space.
			"( )?" +
		
		// Numbers
			"[0-9]+";
		
		MATH_REGEX = regex;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println("Regex pattern: " + MATH_REGEX);
		
		List<String> urls = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("urls/urls-paragraphs.txt"));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				if(!Strings.isNullOrEmpty(line)) {
					urls.add(line);
				}
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Multimap<String, String> multiMap = parseCourseIds(urls.toArray(new String[0]));
		
		for(String key: multiMap.keySet()) {
			System.out.println("host: " + key);
			for(String course : multiMap.get(key)) {
				System.out.println("\t" + course);
			}
		}
	}
	
	public static final Multimap<String, String> parseCourseIds(String[] links) {
		Multimap<String, String> multiMap = LinkedHashMultimap.create();
		
		Document doc;
		URL url;
		String host;
		Pattern p = Pattern.compile(MATH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher m;
		Set<String> uniqueIds;
		for(String link : links) {
			System.out.println("Scanning url: " + link);
			try {
				url = new URL(link);
				host = url.getHost();
				
				doc = Jsoup.connect(link).get();
				
				m = p.matcher(doc.text());
				
				uniqueIds = new HashSet<String>();
				while(m.find()) {
					uniqueIds.add(m.group());
				}
				
				multiMap.putAll(host, uniqueIds);
				
			} catch (MalformedURLException e) {
				System.out.println("Failed to parse url: " + link);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Failed to connect to url: " + link);
				e.printStackTrace();
			}
			
			
		}
		
		return multiMap;
	}

}
