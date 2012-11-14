package g2.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoogleDictionary {
	private static final Logger logger = Logger.getLogger(GoogleDictionary.class);
	private static final String url = "http://www.google.com/dictionary/json?callback=dict_api.callbacks.id100&sl=en&tl=en&restrict=pr%2Cde&client=te&q=";
	private static final String successRegEx = "dict_api.callbacks.id100\\((.*),200,null\\)";
	
	public static void main(String[] args) {
		
		SpeechPart part = getPartofSpeech("discrete");
		System.out.println("Got part: " + part);
	}
	
	public enum SpeechPart {
		ADJECTIVE,
		NOUN,
		VERB,
		UNKNOWN
	}
	
	public static SpeechPart getPartofSpeech(String word) {
		try {
			String encodedWord = URLEncoder.encode(word, "UTF-8");
			
			URL request = new URL(url + encodedWord);
	        BufferedReader in = new BufferedReader(new InputStreamReader(request.openStream()));

	        StringBuilder builder = new StringBuilder();
	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            builder.append(inputLine);
	        in.close();
			
	        Pattern p = Pattern.compile(successRegEx);
	        Matcher m = p.matcher(builder);
	        
	        if(m.matches()) {
	        	String json = m.group(1);
	        	
	        	JsonParser parser = new JsonParser();
	        	JsonElement root = parser.parse(json);
	        	
	        	JsonObject rootObject = root.getAsJsonObject();
	        	
	        	if(!rootObject.has("primaries")) {
	        		return SpeechPart.UNKNOWN;
	        	}
	        	JsonArray primaries = rootObject.get("primaries").getAsJsonArray();
	        	JsonObject primary = primaries.get(0).getAsJsonObject();
	        	
	        	if(!primary.has("terms")) {
	        		return SpeechPart.UNKNOWN;
	        	}
	        	JsonArray terms = primary.get("terms").getAsJsonArray();
	        	
	        	for(JsonElement termE : terms) {
	        		if(termE.getAsJsonObject().has("labels")) {
	        			JsonArray labels = termE.getAsJsonObject().get("labels").getAsJsonArray();
	        			
	        			for(JsonElement labelE : labels) {
	        				if(labelE.getAsJsonObject().has("text")) {
	        					String text = labelE.getAsJsonObject().get("text").getAsString();
	        					if("adjective".equalsIgnoreCase(text)) {
	        						return SpeechPart.ADJECTIVE;
	        					} else if("noun".equalsIgnoreCase(text)) {
	        						return SpeechPart.NOUN;
	        					} else if("verb".equalsIgnoreCase(text)) {
	        						return SpeechPart.VERB;
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	        
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to encode: " + word, e);
		} catch (MalformedURLException e) {
			logger.error("bad url?", e);
		} catch (IOException e) {
			logger.error("bad read", e);
		}
		
		return SpeechPart.UNKNOWN;
	}
}
