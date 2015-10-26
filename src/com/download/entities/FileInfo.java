package com.download.entities;

import java.io.Serializable;

/**
 * 文件信息
 * 
 * @author admin
 * 
 */
public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 文件id
	 */
	private int id;
	/**
	 * 下载地址
	 */
	private String url;
	/**
	 * 下载文件名
	 */
	private String fileName;
	/**
	 * 总长度
	 */
	private int length;
	/**
	 * 已下载长度
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName + ", length=" + length + ", finished="
				+ finished + "]";
	}

	public FileInfo(int id, String url, String fileName, int length, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
	}

	public FileInfo() {
		super();
	}

}
