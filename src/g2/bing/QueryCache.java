package g2.bing;

import g2.bing.json.QueryOutput;
import g2.bing.json.Result;
import g2.util.Utils;
import g2.util.cleaners.WikipediaURLToName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Stores objectified query output associated with a set of keywords, as to not
 * repeat a query we have already performed.
 * @author idn2104
 */
public class QueryCache implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QueryCache.class);
	private final HashMap<String, QueryOutput> query2Output =
			new HashMap<String, QueryOutput>();
	
	public static QueryCache load(File queryCache) throws IOException,
			ClassNotFoundException {
		
		if (!queryCache.exists())
			throw new IOException("Missing file: " + queryCache);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				queryCache));
		
		return (QueryCache) ois.readObject();
	}
	
	public void save(File queryCache) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(queryCache));
		oos.writeObject(this);
		oos.close();
	}
	
	public QueryOutput find(String query) {
		if(query2Output.containsKey(query)) {
			logger.debug("Cache hit!: " + query);
			return query2Output.get(query);
		}
		
		return null;
	}
	
	public void store(String query, QueryOutput queryOutput) {
		query2Output.put(query, queryOutput);
	}
	
	public List<SubTopic> enumerateTopics() {
		ArrayList<SubTopic> list = new ArrayList<SubTopic>();
		
		for(Map.Entry<String, QueryOutput> entry : query2Output.entrySet()) {
			QueryOutput qo = entry.getValue();
			
			List<Result> results = qo.getD().getResults();
			
			String pageName = null;
			String url = null;
			for(Result result : results) {
				url = result.getUrl();
				if(!(url.contains("wikipedia")))
					continue;
				
				pageName = WikipediaURLToName.getPageNameFromURL(url);
				break;
			}
			
			if (pageName == null)
				continue;
			
			SubTopic subtopic = Utils.toSubTopic(pageName, url);
			
			System.out.println(subtopic);
			list.add(subtopic);
		}
		
		return list;
	}
}
