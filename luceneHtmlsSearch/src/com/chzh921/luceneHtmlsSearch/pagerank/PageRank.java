package com.chzh921.luceneHtmlsSearch.pagerank;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/****
 * 
 * 
 * @author 天空之殇
 *
 */
public class PageRank {

	/***
	 * 阈值
	 */
	public static double MAX = 0.00000000001;

	/*** 阻尼系数 ***/
	public static double alpha = 0.85;

	public static String htmldoc = "E:\\webdatabase__workspace\\luceneHtmlsSearch\\htmls";

	public static HashMap<String, HtmlEntity> htmlsMap = new HashMap<String, HtmlEntity>();

	public static List<HtmlEntity> list = new ArrayList<HtmlEntity>();

	public static double[] init;

	public static double[] prs;

	public static List<HtmlEntity> finalList=new ArrayList<HtmlEntity>();
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
 
		getPageRank();
		 		 
	}

	
	
	
	
	@SuppressWarnings("unchecked")
	public static void getPageRank(){
		loadHtml();
		prs=computePageRank();
		while(!checkMax()){
			//把prs的值复制给init数组，迭代运算
			System.arraycopy(prs, 0, init, 0, init.length);
			prs=computePageRank();
		}
          for (int i = 0; i < prs.length; i++) {
			HtmlEntity he=list.get(i);
			he.setPr(prs[i]);
			htmlsMap.get(he.getPath()).setPr(prs[i]);
		} 
          
            finalList=list;
          Collections.sort(finalList, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				 HtmlEntity h1=(HtmlEntity)o1;
				 HtmlEntity h2=(HtmlEntity)o2;
				int em=0;
				if(h1.getPr()>h2.getPr())
				{
					em=-1;
				}
				else{
					em=1;
				}
				return em;
			}
        	  
          });
          
		for (HtmlEntity he : finalList) {
			
			System.out.println(he.getPath()+":"+he.getPr());
		}
		 			
	}
	
	
	
	
	
	/****
	 * 判断前后两次的pr数组之间的差别是是否大于我们定义的阈值，如果大于则返回false,继续迭代计算prs
	 * @param prs
	 * @param init
	 * @param max
	 * @return
	 */
	
	private static boolean checkMax() {
		 boolean flag=true;
		 for (int i = 0; i < prs.length; i++) {
		   if(Math.abs(prs[i]-init[i])>MAX)
		   {
			   flag=false;
			   break;
		   }
		}
		return flag ;
	}

	/***
	 * 计算pagarank
	 */
	private static double[] computePageRank() {
		double[] pr = new double[init.length];
		for (int i = 0; i < init.length; i++) {

			double temp = 0.0;
			HtmlEntity he0 = list.get(i);
			//
			for (int j = 0; j < init.length; j++) {
				HtmlEntity he = list.get(j);
				//計算本页面链接相关总值
				if (i != j && he.getOutLinks().size() != 0&& he.getOutLinks().contains(he0.getPath())) {
					temp = temp + init[j] / he.getOutLinks().size();
				} 
			}
             //經典的pagerank公式        
			pr[i] = alpha + (1 - alpha) * temp;
		}
      return  pr;
	}

	/*****
	 * 加载文件夹下边的网页文件，并且初始化pr值（即init数组），计算每个网页的外链和内链
	 * 
	 */
	private static void loadHtml() {
		File file = new File(htmldoc);
		
		File[] htmlfiles = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.getAbsolutePath().endsWith(".html")) {
					return true;
				}
				return false;
			}
		});

		System.out.println(htmlfiles.length);
		init = new double[htmlfiles.length];
		for (int i = 0; i < htmlfiles.length; i++) {

			File f = htmlfiles[i];
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
				String line = br.readLine();
				StringBuffer html = new StringBuffer();
				while (line != null) {
					line = br.readLine();
					html.append(line);
				}// while

				HtmlEntity he = new HtmlEntity();
				he.setPath(f.getAbsolutePath());
				 
				he.setContent(html.toString());
				// html分析工具
				Parser parser = Parser.createParser(html.toString(), "gbk");
				HtmlPage page = new HtmlPage(parser);
				parser.visitAllNodesWith(page);
			 
				NodeList nodelist = page.getBody();
				nodelist = nodelist.extractAllNodesThatMatch(new TagNameFilter(
						"A"), true);

				for (int j = 0; j < nodelist.size(); j++) {
					LinkTag outlink = (LinkTag) nodelist.elementAt(j);
					he.getOutLinks().add(outlink.getAttribute("href"));
				}

				htmlsMap.put(he.getPath(), he); // 用绝对路径（含文件名）来唯一标识
				list.add(he);
				init[i] = 0.0;
			}

			catch (FileNotFoundException e) {
		 
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserException e) {

				e.printStackTrace();
			}

		}// for

		for (int i = 0; i < list.size(); i++) {
			HtmlEntity he = list.get(i);
			List<String> outlink = he.getOutLinks();
			for (String ol : outlink) {
				HtmlEntity he0 = htmlsMap.get(ol); 
      if(he0!=null)
				he0.getInLinks().add(he.getPath());
			}
		}

	}

}
