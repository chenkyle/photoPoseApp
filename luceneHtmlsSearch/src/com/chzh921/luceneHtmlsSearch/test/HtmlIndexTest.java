package com.chzh921.luceneHtmlsSearch.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class HtmlIndexTest {

	private static String CHZHINDEX = "chzhindex";
	private static String FILEPATH = "";

	public void createIndex() throws IOException {
		// 索引文件夹对象
		Directory dir = FSDirectory.open(new File(CHZHINDEX));
		// 中文分词器
		Analyzer analyzer = new IKAnalyzer(true);
		// IndexWriter的配置
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		// 创建IndexWriter，它用于写索引
		IndexWriter writer = new IndexWriter(dir, iwc);
        
	}

	public Document getDocument(File html) {
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

}