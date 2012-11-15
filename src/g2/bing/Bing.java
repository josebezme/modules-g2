package g2.bing;
import g2.bing.json.QueryOutput;
import g2.bing.json.Result;
import g2.model.Course;
import g2.util.Utils;
import g2.util.cleaners.WikipediaURLToName;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;

/* Acknowledgements:
 * Bing: For 5000 free queries.
 * Prof. Gravano's E6111 webpage for the pointers to the Bing API and the sample code
 * the interaction with the search engine is based on.
 * http://jsongen.byingtondesign.com/ -- for saving precious minutes of my life.
 */

public class Bing {
	private final QueryCacheWrapper qcr;
	private final boolean replaceTalk = false;
	
	/* Doesn't cache queries :'( */
	private Bing() {
		qcr = null;
	}
	
	/* Caches queries. */
	public Bing(File queryCacheFile) throws IOException, ClassNotFoundException {
		qcr = new QueryCacheWrapper(queryCacheFile);
	}
	
	/**
	 * Given an area of study and keywords, attempts to identify a plausible subtopic
	 * for the keywords in the area.
	 * 
	 * Uses QueryGenerator to generate a query for the subject area and keywords, then
	 * uses Bing to look up this query; then, if a Wikipedia page is found, assume that
	 * the name of the subtopic area is the name of the Wikipedia page, and return the
	 * parsed subtopic name.
	 * 
	 * @param area
	 * @param terms
	 * @return
	 * @throws IOException
	 */
	public SubTopic getTopicFromTermArea(String area, String terms) throws IOException {
		final String query = QueryGenerator.generateQueryForSubjectArea(area, terms);

		final QueryOutput queryOutput = query(query);
		
		List<Result> results = queryOutput.getD().getResults();
		for(Result result : results) {
			String url = result.getUrl();
			if(!(url.contains("wikipedia")))
				continue;
			
			String pageName = WikipediaURLToName.getPageNameFromURL(url);
			
			if (replaceTalk) {
				url = url.replace("Talk:", "");
				pageName = pageName.replace("Talk:", "");
			}
			
			return Utils.toSubTopic(pageName, url);
		}
		
		return null;
	}
	
	/**
	 * LIMITED NUMBER OF QUERIES [5000 total]. PLEASE DON'T USE UNNECESSARILY.
	 * 
	 * Executes a query on Bing; returns JSON output. Note that we can
	 * also use Atom (see notes below).
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	private QueryOutput query(final String query) throws IOException {
		if(qcr != null) {
			QueryOutput cacheOutput = qcr.find(query);
			if (cacheOutput != null) {
				System.out.println("CACHE HIT!"  + query);
				return cacheOutput;
			}
		}
		
		final String encodedQuery = URLEncoder.encode(query);
		
		/* FYI: We can retrieve these in Atom as well [format=Atom], but used Json
		 * since we already had Gson lying around.
		 */
		final String bingURL = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=%27"
				+ encodedQuery + "%27&$top=10&$format=Json";

		InputStream propStream = Bing.class.getResourceAsStream("local.properties");
		String accountKeyEnc;
		if(propStream != null) {
			Properties p = new Properties();
			p.load(propStream);
			String accountKey;
			if((accountKey = p.getProperty("bing.key")) != null) {
				byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
				accountKeyEnc = new String(accountKeyBytes);
			} else {
				accountKeyEnc = "Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT06Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT0=";
			}
		} else {
			accountKeyEnc = "Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT06Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT0=";
		}
		
		accountKeyEnc = "Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT06Sk1SYmdlSGZpUHpOTjUzL1RYNzZkTm9WcmZYclQ3aStMMkhGeC95Tk16TT0=";
		//accountKeyEnc = "Yy9iYXVPV0xIS0R1cXpLbldXMEJWd1ZDbUhCWThQcXh4Zytpa3R6cXIzST06Yy9iYXVPV0xIS0R1cXpLbldXMEJWd1ZDbUhCWThQcXh4Zytpa3R6cXIzST0=";
		URL url = new URL(bingURL);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		InputStream inputStream = (InputStream) urlConnection.getContent();
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		
		/* We have a limited number of queries, so save output them for posterity. Please don't
		 * run queries unnecessarily.
		 */
		String content = new String(contentRaw);
		
		long time = System.currentTimeMillis();
		String fileName = query + "_" + time + ".json";
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		bw.write(content);
		bw.close();
		
		QueryOutput queryOutput = new Gson().fromJson(content, QueryOutput.class);
		
		if(qcr != null)
			qcr.store(query, queryOutput);
		
		return queryOutput;
	}
	
	public Multimap<Course, SubTopic> getTopicsFromCourses(Collection<Course> courses, String area) {
		Multimap<Course, SubTopic> courseTopics = LinkedHashMultimap.create();
		
		SubTopic topic;
		for(Course c : courses) {
			System.out.println("Getting topics for course: " + c);
			for(String term : c.getTerms()) {
				try {
					topic = getTopicFromTermArea(area, term);
					
					
					if(topic != null) {
						courseTopics.put(c, topic);
						/* Add the course to the topic's course store. */
						topic.addCourse(c);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return courseTopics;
	}

	public static void main(String[] args) throws IOException {
		// Just a sample query + creation of Json + reading some data from the objects.

//		String json = query("what is topology mathematics aaa");
		
		String json = "{\"d\":{\"results\":[{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=0&$top=1\",\"type\":\"WebResult\"},\"ID\":\"a0ad6ba9-6d4c-4fc4-b158-522cb08f5fcd\",\"Title\":\"Topology - Wikipedia, the free encyclopedia\",\"Description\":\"Topology (from the Greek τόπος, “place”, and λόγος, “study”) is a major area of mathematics concerned with the most basic properties of space, such ...\",\"DisplayUrl\":\"en.wikipedia.org/wiki/Topology_(Mathematics)\",\"Url\":\"http://en.wikipedia.org/wiki/Topology_(Mathematics)\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=1&$top=1\",\"type\":\"WebResult\"},\"ID\":\"22a15754-3a7e-4d32-ba3a-2a51672e141d\",\"Title\":\"What is Topology? - Wayne State University\",\"Description\":\"Topology is almost the most basic form of geometry there is. It is used in nearly all branches of mathematics in one form or another.\",\"DisplayUrl\":\"www.math.wayne.edu/~rrb/topology.html\",\"Url\":\"http://www.math.wayne.edu/~rrb/topology.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=2&$top=1\",\"type\":\"WebResult\"},\"ID\":\"d553b6d7-3c90-49f4-87fd-6a8bc887419c\",\"Title\":\"What is Topology? - Pepperdine University - Seaver College ...\",\"Description\":\"What is Topology? Topology is a branch of pure mathematics, related to Geometry. It unfortunately shares the name of an unrelated topic more commonly known as ...\",\"DisplayUrl\":\"math.pepperdine.edu/kiga/topology.html\",\"Url\":\"http://math.pepperdine.edu/kiga/topology.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=3&$top=1\",\"type\":\"WebResult\"},\"ID\":\"421e8a3a-7133-4bec-abf1-2690edb1ef40\",\"Title\":\"AAA Math\",\"Description\":\"AAA Math features a comprehensive set of interactive arithmetic lessons. Unlimited practice is available on each topic which allows thorough mastery of the concepts.\",\"DisplayUrl\":\"aaamath.com\",\"Url\":\"http://aaamath.com/\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=4&$top=1\",\"type\":\"WebResult\"},\"ID\":\"4a9254a3-c0c9-4874-b940-c9c4d43ecf0c\",\"Title\":\"Math Topology, Maths Topology, Mathematics Topology\",\"Description\":\"Euler - A New Branch of Mathematics: Topology, part 1 ... Euler - A New Branch of Mathematics: Topology PART I. Most of us tacitly assume that mathematics is a ...\",\"DisplayUrl\":\"www.math10.com/en/maths-history/math-topology/topology.html\",\"Url\":\"http://www.math10.com/en/maths-history/math-topology/topology.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=5&$top=1\",\"type\":\"WebResult\"},\"ID\":\"b4e3f30a-7376-4e20-ba3d-916941d7aa4e\",\"Title\":\"Mathematics Archives - Topics in Mathematics - Topology\",\"Description\":\"Algebraic and Geometric Topology ADD. KEYWORDS: Electronic and printed journal SOURCE: Geometry & Topology Publications, Mathematics Department of the University of ...\",\"DisplayUrl\":\"archives.math.utk.edu/topics/topology.html\",\"Url\":\"http://archives.math.utk.edu/topics/topology.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=6&$top=1\",\"type\":\"WebResult\"},\"ID\":\"ea602c0e-1dbc-4854-a3f8-d705d29475a5\",\"Title\":\"Topology - Wolfram MathWorld: The Web\u0027s Most Extensive Mathematics ...\",\"Description\":\"Topology is the mathematical study of the properties that are preserved through deformations, ... Math. 154, 27-39, 1996. Chinn, W. G. and Steenrod, N. E.\",\"DisplayUrl\":\"mathworld.wolfram.com/Topology.html\",\"Url\":\"http://mathworld.wolfram.com/Topology.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=7&$top=1\",\"type\":\"WebResult\"},\"ID\":\"53aa45d7-a588-4c02-a6d6-c128d0161a48\",\"Title\":\"Topology - Wikibooks, open books for an open world\",\"Description\":\"It could be said that mathematics in general owes its credibility to ancient Greece\u0027s Euclid. ... The Topology of CW Complexes (1969) Joerg Mayer, ...\",\"DisplayUrl\":\"en.wikibooks.org/wiki/Topology\",\"Url\":\"http://en.wikibooks.org/wiki/Topology\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=8&$top=1\",\"type\":\"WebResult\"},\"ID\":\"0559eeaf-6ed4-4541-9bc4-830c36004217\",\"Title\":\"Geometry and Topology - Department of Mathematics & Statistics ...\",\"Description\":\"History of Topology in the History of Mathematics archive at Saint Andrews University in Scotland. A Circular History of Knot Theory. - An essay by Bill Menasco.\",\"DisplayUrl\":\"www.math.mcmaster.ca/andy/nicas_top.html\",\"Url\":\"http://www.math.mcmaster.ca/andy/nicas_top.html\"},{\"__metadata\":{\"uri\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what is topology mathematics aaa\u0027&$skip=9&$top=1\",\"type\":\"WebResult\"},\"ID\":\"efba7a35-77d0-455d-aeae-c445d1151e70\",\"Title\":\"Math 655 - Introduction to Topology - Welcome | Department of ...\",\"Description\":\"Math 655 - Introduction to Topology. Math. 655 is an introduction to the basic concepts of modern topology: metric spaces, topological spaces, connectedness ...\",\"DisplayUrl\":\"www.math.osu.edu/~fiedorowicz.1/math655\",\"Url\":\"http://www.math.osu.edu/~fiedorowicz.1/math655/\"}],\"__next\":\"https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=\u0027what%20is%20topology%20mathematics%20aaa\u0027&$skip=10&$top=10\"}}";
		QueryOutput qq = new Gson().fromJson(json, QueryOutput.class);
		
		List<Result> results = qq.getD().getResults();
		for(Result result : results) {
			String url = result.getUrl();
			if(!(url.contains("wikipedia")))
				continue;
			
			String pageName = WikipediaURLToName.getPageNameFromURL(url);
			System.out.println(pageName);
		}
	}
}