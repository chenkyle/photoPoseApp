package com.chzh921.luceneHtmlsSearch.pagerank;

import java.util.ArrayList;
import java.util.List;

public class HtmlEntity {

	private String path;
	private String content;
	//本页面链接的其他页面(外部)
	private List<String> outLinks=new ArrayList<String>();
	//内链（其他页面指向本页面）
	private List<String> inLinks=new ArrayList<String>();
	//排名值
	private double pr;
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getOutLinks() {
		return outLinks;
	}

	public void setOutLinks(List<String> outLinks) {
		this.outLinks = outLinks;
	}

	public List<String> getInLinks() {
		return inLinks;
	}

	public void setInLinks(List<String> inLinks) {
		this.inLinks = inLinks;
	}

	public double getPr() {
		return pr;
	}

	public void setPr(double pr) {
		this.pr = pr;
	}

	
	
	
}
