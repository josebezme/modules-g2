package g2.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
	
	public static String getHost(String url) throws MalformedURLException {
		URL urlObject = new URL(url);
		return urlObject.getHost();
	}

}
