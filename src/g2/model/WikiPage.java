package g2.model;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPage {
	private static final String wikiUrlPrefix = "http://en.wikipedia.org/wiki/";
	private ArrayList<String> outLinks;
	private String fullText;
	private String linkText;
	
	public WikiPage(String urlTitle) throws Exception {
		fullText = "";
		linkText = "";
		
		Document doc = Jsoup.connect(wikiUrlPrefix + urlTitle).get();
		Elements paragraphs = doc.select("p");
		for (Element p : paragraphs) 
			fullText = fullText.concat(p.text());
		Elements links = doc.select("a");
		for (Element a : links)
			linkText = linkText.concat(a.text());
	}
	
	public boolean refersTo(Module that) {
		String lowerFullText = fullText.toLowerCase();
		String lowerLinkText = linkText.toLowerCase();
		for (String title : that.titles) {
			if (lowerFullText.contains(title.toLowerCase()) && lowerLinkText.contains(title.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		WikiPage test = new WikiPage("Integral");
	}
}
