package com.download.entities;

import java.io.Serializable;

/**
 * 线程信息
 * 
 * @author admin
 * 
 */
public class ThreadInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 线程id
	 */
	private int id;
	/**
	 * 下载地址
	 */
	private String url;
	/**
	 * 该线程下载起始位置
	 */
	private int start;
	/**
	 * 该线程下载结束位置
	 */
	private int end;
	/**
	 * 该线程已下载的总大小
	 */
	private int finished;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start + ", end=" + end + ", finished=" + finished
				+ "]";
	}

	public ThreadInfo(int id, String url, int start, int end, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
	}

	public ThreadInfo() {
		super();
	}

}
