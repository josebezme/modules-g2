package g2.bing;

import g2.bing.json.QueryOutput;

import java.io.File;
import java.io.IOException;

/**
 * A wrapper for a QueryCache object that is associated with a particular
 * File; creates the File if non-existent, otherwise loads; saves the File
 * upon every store.
 * @author idn2104
 */
public class QueryCacheWrapper {
	private final File file;
	private final QueryCache queryCache;
	
	public QueryCacheWrapper(File file) throws IOException,
			ClassNotFoundException {
		this.file = file;

		if (file.exists()) {
			queryCache = QueryCache.load(file);
		} else {
			queryCache = new QueryCache();
			queryCache.save(file);
		}
	}
	
	public QueryCache getQC() {
		return queryCache;
	}
	
	public QueryOutput find(String query) {
		return queryCache.find(query);
	}

	public void store(String query, QueryOutput output) throws IOException {
		queryCache.store(query, output);
		queryCache.save(file);
	}
}
