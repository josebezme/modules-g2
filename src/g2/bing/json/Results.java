package g2.bing.json;

import java.io.Serializable;
import java.util.List;

public class Results implements Serializable {
	private static final long serialVersionUID = 1L;
	private String __next;
	private List<Result> results;

	public String get__next() {
		return this.__next;
	}

	public void set__next(String __next) {
		this.__next = __next;
	}

	public List<Result> getResults() {
		return this.results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
}