package g2.bing.json;

import java.io.Serializable;

public class __metadata implements Serializable {
	private static final long serialVersionUID = 1L;
   	private String type;
   	private String uri;

 	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type = type;
	}
 	public String getUri(){
		return this.uri;
	}
	public void setUri(String uri){
		this.uri = uri;
	}
}