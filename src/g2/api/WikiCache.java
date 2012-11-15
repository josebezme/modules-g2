package g2.api;

import g2.model.WikiPage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class WikiCache implements Serializable {
	private final HashMap<String, WikiPage> url2WikiPage = new
			HashMap<String, WikiPage>();

	public static WikiCache wc;
	private static final File STORE_FILE = new File("wikicache.wc");
	
	static {
		try {
			wc = load(STORE_FILE);
		} catch (Exception e) {
			try {
				new WikiCache().save(STORE_FILE);
				wc = load(STORE_FILE);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void store(String url, WikiPage wp) throws IOException {
		url2WikiPage.put(url, wp);
//		System.out.println("Saved: " + query + " " + body);
		wc.save(STORE_FILE);
	}
	
	public WikiPage get(String url) {
		return url2WikiPage.get(url);
	}

	public static WikiCache load(File wikiCache) throws IOException,
			ClassNotFoundException {

		if (!wikiCache.exists())
			throw new IOException("Missing file: " + wikiCache);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				wikiCache));

		return (WikiCache) ois.readObject();
	}

	public void save(File queryCache) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				queryCache));
		oos.writeObject(this);
		oos.close();
	}

}
