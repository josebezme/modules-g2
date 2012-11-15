package g2.model;

import g2.api.WikiCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	public boolean timedOut = false;
	
	public static WikiPage getByURL(String url) throws IOException {
		WikiPage wikiCache = WikiCache.wc.get(url);
		if (wikiCache != null) {
			return wikiCache;
		}
		
		WikiPage wp = new WikiPage(url);
		WikiCache.wc.store(url, wp);
		return wp;
	}
	
	private WikiPage(String url) {
		linkTitles = new ArrayList<String>();
		
		String[] split = url.split("/");
		this.urlTitle = split[split.length-1];

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
				}
			}
		}
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
				if (linkTitle.equals(title))
					count++;
			}
			for (String synonym : that.synonyms) {
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
		
		Elements allTags = doc.select("ul, i");
		Element ulTag = null;
		for (int i = 0; i < allTags.size()-1; i++) {
			Element tag = allTags.get(i);
			Elements iTags = tag.select("i");
			if (iTags.size() > 0 && iTags.get(0).attr("class").equals("nosections")) {
				ulTag = allTags.get(i+1);
				break;
			}
		}
		
		if (ulTag == null)
			return redirects;
		
		Elements links = ulTag.select("a");

		for (Element e : links) {
			
			if (e.text().matches(".*\\w.*")) {
				if (!redirects.contains(e.text())) {
					redirects.add(e.text());
					//System.out.println(e.text());
				}
			}
		}
		return redirects;
	}
	
	public static void main(String[] args) {
		WikiPage test = new WikiPage("Integral");
	}
}
