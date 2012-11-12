package g2.model;

import g2.util.Utils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class WikiPage {
	private static final String wikiUrlPrefix = "http://en.wikipedia.org/wiki/";
	
	private ArrayList<String> outLinks;
	private String fullText;
	
	public WikiPage(String urlTitle) throws Exception {
		fullText = "";
		
		Document doc = Jsoup.connect(wikiUrlPrefix + urlTitle).get();
		Elements paragraphs = doc.select("p");
		for (Element p : paragraphs) 
			fullText = fullText.concat(p.text());
	
	}
	
	public boolean refersTo(Module that) {
		String text = fullText.toLowerCase();
		for (String title : that.titles) {
			if (text.contains(title.toLowerCase()))
				return true;
		}
		return false;
	}
}
