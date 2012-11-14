package g2.model;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPage {
	private static final String wikiUrlPrefix = "http://en.wikipedia.org/wiki/";
	private ArrayList<String> outLinks;
	public String title;
	private String fullText;
	private String linkText;
	
	public WikiPage(String urlTitle) {
		fullText = "";
		linkText = "";
		
		Document doc = null;
		try {
			doc = Jsoup.connect(wikiUrlPrefix + urlTitle).get();
		}
		catch (Exception e) {
			System.err.println(e.toString());
		}
		
		title = extractTitle(doc.select("title").text());
		Elements paragraphs = doc.select("p");
		for (Element p : paragraphs) 
			fullText = fullText.concat(p.text());
		Elements links = doc.select("a");
		for (Element a : links)
			linkText = linkText.concat(a.text());
	}
	
	private String extractTitle(String titleTag) {
		String[] spl = titleTag.split("-");
		String title = spl[0];
		for (int i = 1; i < spl.length-1; i++)
			title += "-" + spl[i];
		spl = title.split("\\(");
		return spl[0].trim();
	}
	
	// TODO: return a numerical score, instead of a boolean
	public boolean refersTo(Module that) {
		String lowerFullText = fullText.toLowerCase();
		String lowerLinkText = linkText.toLowerCase();
		for (String title : that.titles) {
			if (lowerFullText.contains(title.toLowerCase()) && lowerLinkText.contains(title.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		WikiPage test = new WikiPage("Integral");
	}
}
