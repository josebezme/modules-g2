package g2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPage {
	private static final String wikiUrlPrefix = "http://en.wikipedia.org/wiki/";
	private static final String redirectUrlPrefix = "http://toolserver.org/~dispenser/cgi-bin/rdcheck.py?page=";
	private ArrayList<String> linkTitles;
	private String urlTitle;
	public String title;
	private String fullText;
	private String linkText;
	public boolean timedOut = false;
	
	public WikiPage(String url) {
		linkTitles = new ArrayList<String>();
		
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
			timedOut = true;
			return;
		}
		
		title = extractTitle(doc.select("title").text());
		Elements paragraphs = doc.select("p");
		for (Element p : paragraphs) {
			Elements linkTags = p.select("a");
			for (Element l : linkTags) {
				String linkTitle = l.attr("title").trim();
				if (linkTitle.length() > 0) {
					linkTitles.add(l.attr("title"));
					//System.out.println("ADD: " + l.attr("title"));
				}
			}
			fullText = fullText.concat(p.text());
		}
		Elements links = doc.select("a");
		for (Element a : links)
			linkText = linkText.concat(a.text());
	}
	
	private String extractTitle(String titleTag) {
		String[] spl = titleTag.split("-");
		String title = spl[0];
		for (int i = 1; i < spl.length-1; i++)
			title += "-" + spl[i];
		return title.trim();
		/*spl = title.split("\\(");
		return spl[0].trim();*/
	}
	
	// TODO: return a numerical score, instead of a boolean
	/*public boolean refersTo(Module that) {
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
			count += lowerFullText.split(Pattern.quote(title.toLowerCase())).length-1;
			count += lowerLinkText.split(Pattern.quote(title.toLowerCase())).length-1;
		}
		for (String synonym : that.synonyms) {
			count += lowerFullText.split(Pattern.quote(synonym.toLowerCase())).length-1;
			count += lowerLinkText.split(Pattern.quote(synonym.toLowerCase())).length-1;
		}
		//System.out.println(count);
		return count;
	}*/
	
	public boolean refersTo(Module that) {
		for (String linkTitle : linkTitles) {
			for (String title : that.titles) {
				if (linkTitle.equals(title))
					return true;
			}
			for (String synonym : that.synonyms) {
				if (linkTitle.equals(synonym))
					return true;
			}
		}
		return false;
	}
	
	public int linkScore(Module that) {
		int count = 0;
		for (String linkTitle : linkTitles) {
			for (String title : that.titles) {
				/*System.out.println("Link title: " + linkTitle);
				System.out.println("Title: " + title);
				System.out.println();*/
				if (linkTitle.equals(title))
					count++;
			}
			for (String synonym : that.synonyms) {
				/*System.out.println("Link title: " + linkTitle);
				System.out.println("Synonym: " + synonym);
				System.out.println();*/
				if (linkTitle.equals(synonym))
					count++;
			}
		}
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

		for (Element e : links) {
			
			if (e.text().matches(".*\\w.*")) {
				if (!redirects.contains(e.text()))
					redirects.add(e.text());
			}
		}
		return redirects;
	}
	
	public static void main(String[] args) {
		WikiPage test = new WikiPage("Integral");
	}
}
