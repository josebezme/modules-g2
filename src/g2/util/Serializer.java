package g2.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import g2.model.Course;
import g2.model.Hierarchical;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Serializer {
	private static final Logger logger = Logger.getLogger(Serializer.class);
	
	public static final Gson gson = new GsonBuilder()
										.excludeFieldsWithoutExposeAnnotation()
										.create();
	
	public static void main(String[] args) {
		String urls[] = Utils.getUrlsFromFile("urls/urls-paragraphs.txt");
		
		Multimap<String, Course> hosts = C1CourseExtractor.extractCourses(urls);
		
		logger.info("Printing hosts:");
		for(String host : hosts.keySet()) {
			logger.info(host + ":" + hosts.get(host).size());
		}
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		writeOutCourses(hosts, out);
//		logger.error(baos.toString());
		
		String json = baos.toString();
		
		Multimap<String, Course> hosts2 = readInCourses(json);
		
		validateHosts(hosts, hosts2);
		
		logger.info("Printing read in hosts:");
		for(String host : hosts2.keySet()) {
			logger.info(host + ":" + hosts2.get(host).size());
		}
	}

	public static void validateHosts(Multimap<String, Course> origHosts,
			Multimap<String, Course> nextHosts) {
		Collection<Course> courses;
		Collection<Course> courses2;
		List<Course> courseList;
		for(String host : origHosts.keySet()) {
			if(!nextHosts.containsKey(host)) {
				logger.error("Failed to find host: " + host);
				continue;
			}
			
			courses = origHosts.get(host);
			courses2 = nextHosts.get(host);
			
			courseList = new LinkedList<Course>(courses2);
			for(Course c : courses) {
				if(!courseList.contains(c)) {
					logger.error("Failed to find course: " + c);
					continue;
				}
				
				Course c2 = courseList.get(courseList.indexOf(c));
				
				for(Hierarchical pre : c.prereqs()) {
					Course prereq = (Course) pre;
					if(!c2.prereqs().contains(prereq)) {
						logger.error("Failed to find prereq: " + prereq.toShortString() + 
								" in c: " + c.toShortString());
					}
				}
			}
		}
	}
	
	public static Multimap<String, Course> readInCourses(String json) {
		Multimap<String, Course> hosts = LinkedHashMultimap.create();
		
		JsonArray hostArray = new JsonParser().parse(json).getAsJsonArray();
		
		JsonObject hostObject;
		String host;
		
		Map<Course, JsonArray> prereqMap = new HashMap<Course, JsonArray>();
		for(JsonElement e : hostArray) {
			hostObject = e.getAsJsonObject();
			host = hostObject.get("host").getAsString();
			
			for(JsonElement e2 : hostObject.get("courses").getAsJsonArray()) {
				Course c = gson.fromJson(e2, Course.class);
				hosts.put(host, c);
				
				prereqMap.put(c, e2.getAsJsonObject().get("pre-reqs").getAsJsonArray());
			}
			
			List<Course> courses = new LinkedList<Course>(hosts.get(host));
			for(Course c : hosts.get(host)) {
				JsonArray prereqs = prereqMap.get(c);
				
				for(JsonElement preE : prereqs) {
					Course prereq = courses.get(courses.indexOf(gson.fromJson(preE, Course.class)));
					logger.debug("added: " + prereq.toShortString() + " to " + c.toShortString());
					c.addPrereq(prereq);
				}
			}
		}
		
		return hosts;
	}

	public static void writeOutCourses(Multimap<String, Course> titles,
			PrintStream out) {
		JsonArray hostArray = new JsonArray();
		
		JsonObject jsonObject;
		JsonArray courseArray;
		JsonObject courseJson;
		JsonArray prereqArray;
		for(String host : titles.keySet()) {
			Collection<Course> courses = titles.get(host);
			
			jsonObject = new JsonObject();
			jsonObject.addProperty("host", host);
			
			courseArray = new JsonArray();
			for(Course c : courses) {
				courseJson = gson.toJsonTree(c).getAsJsonObject();
				
				prereqArray = new JsonArray();
				for(Hierarchical prereq : c.prereqs()) {
					prereqArray.add(gson.toJsonTree(prereq));
				}
				courseJson.add("pre-reqs", prereqArray);
				
				courseArray.add(courseJson);
			}
			
			jsonObject.add("courses", courseArray);
			
			hostArray.add(jsonObject);
		}
		
		out.println(hostArray.toString());
	}

}
