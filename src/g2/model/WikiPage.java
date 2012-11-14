package g2.model;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPage {
	private static final String wikiUrlPrefix = "http://en.wikipedia.org/wiki/";
	private static final String redirectUrlPrefix = "http://toolserver.org/~dispenser/cgi-bin/rdcheck.py?page=";
	private ArrayList<String> outLinks;
	private String urlTitle;
	public String title;
	private String fullText;
	private String linkText;
	
	public WikiPage(String url) {
		String[] split = url.split("/");
		this.urlTitle = split[split.length-1];
		
		fullText = " ";
		linkText = " ";
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		//title = extractTitle(doc.select("title").text());
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
	
	public int linkScore(Module that) {
		String lowerFullText = fullText.toLowerCase() + " ";
		String lowerLinkText = linkText.toLowerCase() + " ";
		
		int count = 0;
		for (String title : that.titles) {
			count += lowerFullText.split(title.toLowerCase()).length-1;
			count += lowerLinkText.split(title.toLowerCase()).length-1;
		}
		//System.out.println(count);
		return count;
	}
	
	public List<String> redirects() {
		Document doc = null;
		ArrayList<String> redirects = new ArrayList<String>();
		try {
			doc = Jsoup.connect(redirectUrlPrefix + urlTitle).get();
		}
		catch (Exception e) {
			System.err.println(e.toString());
			return redirects;
		}
		Element linkSection = doc.select("ul").get(0);
		Elements links = linkSection.select("a");
		//System.out.println("---" + urlTitle + "---");
		for (Element e : links) {
			if (e.text().matches(".*\\w.*")) {
				//System.out.println(e.text());
				redirects.add(e.text());
			}
		}
		//System.out.println();
		return redirects;
	}
	
	public static void main(String[] args) {
		WikiPage test = new WikiPage("Integral");
	}
}
