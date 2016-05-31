package com.chzh921.luceneHtmlsSearch.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.chzh921.luceneHtmlsSearch.bean.Information;
import com.chzh921.luceneHtmlsSearch.pagerank.PageRank;
import com.chzh921.luceneHtmlsSearch.utils.CommenUtils;
import com.chzh921.luceneHtmlsSearch.utils.Pageinate;
import com.chzh921.luceneHtmlsSearch.utils.PublicUtilDict;

 
/**
 * Lucene搜索WEB实例
 * 
 * 
 */
public class LuceneServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final String filePath = "E:\\webdatabase__workspace\\luceneHtmlsSearch\\htmls";
	
	private final String suffix = ".html";
	
    private final String CHZHINDEX="chzhindex";
	
	private String[] descs = new String[13];
	
	private List<Information> dataList = new ArrayList<Information>();
	
	private String changepage;
	
	private IndexSearcher isearcher = null;
	
	private Analyzer analyzer = null;
	
	public String getChangepage() {
		return changepage;
	}

	public void setChangepage(String changepage) {
		this.changepage = changepage;
	}

	public LuceneServlet() { }
	
	public void doExec(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("-----doExec-------");
		doPost(request, response);
		
	}
	
	/**
	 * 数据初始化
	 */
	@Override
	public void init() throws ServletException {
		System.out.println("-----init-------");
		initData();
		createIndex();
		
	}

	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("--------doPost---------");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		Pageinate page = new Pageinate();
		System.out.println("init_page:"+page);
		String keyword = request.getParameter("searchkey");
		System.out.println("keyword:"+keyword);
		String pageKey = request.getParameter("changepage");
		System.out.println("pageKey:"+pageKey);
		String startparam = request.getParameter("startparam");
		System.out.println("startparam:"+startparam);
		try {
			if(startparam != null){
				page.setStart(Integer.parseInt(startparam));
			}
			if(keyword != null){
				searchFile(keyword,pageKey,page);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		request.setAttribute("dataList", dataList);
		request.setAttribute("searchkey", keyword);
		request.setAttribute("startparam", page.getStart());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataList.jsp");
	    dispatcher.forward(request, response);
	}

	/**
	 * 创建索引文件
	 * @author 天空之殇
	 */
	private void createIndex(){
		// 实例化IKAnalyzer分词器
		analyzer = new IKAnalyzer(true);
		Directory directory = null;
		IndexReader ireader = null;
		IndexWriter iwriter = null;
		
		try {
			// 建立内存索引对象CHZHINDEX 
			 directory = FSDirectory.open(new File(CHZHINDEX));
			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE);
			iwriter = new IndexWriter(directory, iwConfig);
			FileInputStream fis = null;
			//获取要索引的所有文件
			File file=new File(filePath);
			File[] htmlfiles=file.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					 if(pathname.getAbsolutePath().endsWith(suffix))
						 return true;
					return false;
				}});
			
			//为每一个html内容建立索引			
			for(int i=0;i<htmlfiles.length;i++){			 
				// 写入索引
				Document doc = null;
				doc=this.getDocument(htmlfiles[i]); 				
				doc.add(new StringField("id", i+"", Field.Store.YES)); 
				//doc.add(new TextField("description", descs[i], Field.Store.YES));
				iwriter.addDocument(doc);
				 
				
			}
			iwriter.close();

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void searchFile(String keyword,String pageKey,Pageinate page) throws IOException, ParseException, InvalidTokenOffsetsException{
		System.out.println("pageKey:"+pageKey);
		// 实例化搜索器
		IndexReader reader = DirectoryReader.open(FSDirectory
				.open(new File(CHZHINDEX)));
		isearcher = new IndexSearcher(reader);
		//keyword = "IT学习者-螃蟹";
		// 使用QueryParser查询分析器构造Query对象
		QueryParser qp = new QueryParser(Version.LUCENE_4_10_2,
				PublicUtilDict.CONTENT, analyzer);
		Query query = qp.parse(keyword);
		
		System.out.println("Query = " + query);

		// 搜索相似度最高的5条记录
		TopDocs topDocs = isearcher.search(query, page.getPageSize());
		System.out.println("匹配文件个数：" + topDocs.totalHits+",topDocs.totalHits:"+topDocs.totalHits);

		 
        CommenUtils.doPagingSearchIK(pageKey, isearcher, query, page,dataList,analyzer);
	}
	
	/**
	 * 初始化描述
	 * @author IT学习者-螃蟹
	 */
	public void initData(){
		descs[0]="IT学习者，关注程序员的就业、工作和生活——『www.itxxz.com』";
		descs[1]="那么，我们不妨用java来Lucene一下，看看都有哪些爆料。。。";
		descs[2]="最近螃蟹很火，抢占各种微博、门户头条，长城内外甚是妖娆。";
		descs[3]="我是IT学习者-螃蟹，每天最喜欢的事情就是，喝一杯咖啡";
		descs[4]="有时候写一篇文章，敲一行代码，然后和bug愉快的玩耍。";
		descs[5]="或许是唐伯虎的那首诗，居然让我喜欢上了螃蟹。";
		descs[6]="我愿做一个自由的螃蟹，可以游行世间；我可以去高山聆听最清新的自然";
		descs[7]="可以在深海躲避阳光的刺眼，听鸟啼幽谷，看白云吹散";
		descs[8]="为什么，不自觉间竟然看不清眼前的世界。。。";
		descs[9]="有时候我很庆幸自己是一只螃蟹，我不能飞的想海鸥那么高，或者也没有像那只鸟一样眷恋一颗石子。";
		descs[10]="我可以懒懒散散的躺在沙滩上，只要回到大海，再次登岸的时候，或许就会是另一番景象。";
		descs[11]="我没有期待的明天，也没有眷恋的过往，每天都是看看阳光，听听海浪，然后好好的睡上一觉。";
		descs[12]="直到有一天，一只海鸥告诉我，我真羡慕你，你有可以回到的大海，而我们永远也无法拥抱天空。";
		
	   //计算网页排名pagerank值
		PageRank.getPageRank();
		
		
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
		document.add(new TextField("description",text.substring(0, 100), Field.Store.YES));
		document.add(new TextField("contents",contents));
		
		return document;
	}


}
