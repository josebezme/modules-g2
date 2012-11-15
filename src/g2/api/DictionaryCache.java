package g2.api;

import g2.bing.QueryCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class DictionaryCache implements Serializable {
	private final HashMap<String, String> query2Body = new
			HashMap<String, String>();

	public static DictionaryCache dc;
	private static final File STORE_FILE = new File("words.dc");
	
	static {
		try {
			dc = load(new File("words.dc"));
		} catch (Exception e) {
			try {
				new DictionaryCache().save(new File("words.dc"));
				dc = load(STORE_FILE);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void store(String query, String body) throws IOException {
		query2Body.put(query, body);
//		System.out.println("Saved: " + query + " " + body);
		dc.save(STORE_FILE);
	}
	
	public String get(String query) {
		return query2Body.get(query);
	}

	public static DictionaryCache load(File dictCache) throws IOException,
			ClassNotFoundException {

		if (!dictCache.exists())
			throw new IOException("Missing file: " + dictCache);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				dictCache));

		return (DictionaryCache) ois.readObject();
	}

	public void save(File queryCache) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				queryCache));
		oos.writeObject(this);
		oos.close();
	}

}
