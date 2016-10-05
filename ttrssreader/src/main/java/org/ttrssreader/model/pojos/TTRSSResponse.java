package org.ttrssreader.model.pojos;

public class TTRSSResponse<T> {

	public int seq;
	public int status;
	public T content;

	@Override
	public String toString() {
		return "TTRSSResponse{" +
				"content=" + content +
				", seq=" + seq +
				", status=" + status +
				'}';
	}

}
