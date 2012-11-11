package g2.util.cleaners;

public class WikipediaURLToName {
	/**
	 * Retrieve the page name from a Wikipedia URL.
	 * 
	 * Removes the prefix (http://.../wiki/).
	 * Replaces underscores with spaces.
	 * Removes clauses within parentheses.
	 * 
	 * 
	 * Example:
	 * http://en.wikipedia.org/wiki/Topology_(Mathematics) => Topology
	 * @param url
	 * @return
	 */
	public static String getPageNameFromURL(String url) {
		final String wikiPart = "/wiki/";
		final int wikiPartIndex = url.indexOf(wikiPart);
		final int pageStartIndex = wikiPartIndex + wikiPart.length();
		
		url = url.substring(pageStartIndex);
		url = url.replaceAll("_", " ");
		url = url.replaceAll("\\(.*?\\)", " ");
		
		return url;
	}
}
