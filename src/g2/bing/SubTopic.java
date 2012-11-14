package g2.bing;

public class SubTopic {
	public final String topic;
	public final String url;
	
	public SubTopic(String topic, String url) {
		this.url = url;
		this.topic = topic;
	}
	
	public String toString() {
		return topic + "\t" + url;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SubTopic) {
			return url.equals(((SubTopic) obj).url);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
