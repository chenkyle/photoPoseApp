package com.chzh921.luceneHtmlsSearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

public class DocumentUtil {
     //提取html文档
	public static Document getHtmlDocument(File html) {
		String htmlPath = html.getAbsolutePath();
		String text = "";

		Parser parser = null;

		try {
			parser = new Parser(htmlPath);
			parser.setEncoding("UTF-8");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HtmlPage visitor=new HtmlPage(parser);
		
		try {
			parser.visitAllNodesWith(visitor);
		} catch (ParserException e) {
		 
			e.printStackTrace();
		}
		NodeList nodes=visitor.getBody();
		int size=nodes.size();
		for (int i = 0; i <size; i++) {
			
			Node node=nodes.elementAt(i);
			text+=node.toPlainTextString();
		}
		
		String title=visitor.getTitle();
		
		Reader contents= new StringReader(text);
		Document document =new Document();
		document.add(new TextField("title",title , Field.Store.YES));
		document.add(new TextField("path",htmlPath, Field.Store.YES));
		document.add(new TextField("contents",contents));
		
		return document;
	}

	/******
	 * @author 天空之殇
	 * 提取doc文件内容
	 * 
	 */
	public static Document getDocument(File doc){
		String htmlPath = doc.getAbsolutePath();
		String title =doc.getName();
		
		InputStream inputStream=null;
		Reader contents=null;
		Document document=new Document();
		
		try {
			inputStream =new FileInputStream(doc);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			WordExtractor extractor=new WordExtractor(inputStream);
			
			contents=new StringReader(extractor.getText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		document.add(new TextField("title",title , Field.Store.YES));
		document.add(new TextField("path",htmlPath, Field.Store.YES));
		document.add(new TextField("contents",contents));
		return document;
	}
	
	
	
	
	

}
