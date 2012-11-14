package g2.util;

import g2.bing.SubTopic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

public class Utils {
	
	public static String[] getUrlsFromFile(String file) {

		List<String> urls = new ArrayList<String>();
		
		String url;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((url = br.readLine()) != null) {
				if(!Strings.isNullOrEmpty(url)) {
					urls.add(url);
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return urls.toArray(new String[0]);
	}
	
	public static String getHost(String url) throws MalformedURLException {
		URL urlObject = new URL(url);
		return urlObject.getHost();
	}

	public static SubTopic toSubTopic(String pageName, String url) {
		pageName = URLDecoder.decode(pageName);
		url = URLDecoder.decode(url);
		return SubTopic.getSubtopic(pageName, url);
	}

}
