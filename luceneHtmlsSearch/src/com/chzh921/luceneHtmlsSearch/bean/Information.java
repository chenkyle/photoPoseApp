package com.chzh921.luceneHtmlsSearch.bean;

 
public class Information {
	
	private String id;
	private String title;
	private String description;
	private String path;
	//新增一个pagerank值
	private double pr;
	
	
	public double getPr() {
		return pr;
	}
	public void setPr(double pr) {
		this.pr = pr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "Information [id=" + id + ", title=" + title + ", description="
				+ description + ", path=" + path + "]";
	}
	
}
