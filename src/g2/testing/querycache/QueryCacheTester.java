package g2.testing.querycache;

import g2.bing.QueryCacheWrapper;

import java.io.File;

public class QueryCacheTester {
	public static void main(String[] args) throws Exception {
		QueryCacheWrapper qc = new QueryCacheWrapper(new File("output.qc"));
		qc.getQC().enumerateTopics();
	}
}
