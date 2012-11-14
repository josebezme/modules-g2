package g2.bing.json;

import java.io.Serializable;

public class QueryOutput implements Serializable {
	private static final long serialVersionUID = 1L;
	private Results d;

	public Results getD() {
		return this.d;
	}

	public void setD(Results d) {
		this.d = d;
	}
}