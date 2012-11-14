package g2.bing.json;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String Description;
	private String DisplayUrl;
	private String ID;
	private String Title;
	private String Url;
	
	private __metadata __metadata;

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getDisplayUrl() {
		return this.DisplayUrl;
	}

	public void setDisplayUrl(String displayUrl) {
		this.DisplayUrl = displayUrl;
	}

	public String getID() {
		return this.ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

	public String getTitle() {
		return this.Title;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

	public String getUrl() {
		return this.Url;
	}

	public void setUrl(String url) {
		this.Url = url;
	}

	public __metadata get__metadata() {
		return this.__metadata;
	}

	public void set__metadata(__metadata __metadata) {
		this.__metadata = __metadata;
	}
}