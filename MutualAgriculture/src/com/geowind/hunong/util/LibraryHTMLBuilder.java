package com.geowind.hunong.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.geowind.hunong.jpa.Article;
import com.geowind.hunong.jpa.ArticleDAO;

/*
//Library data
//文库数据预览
String _articleId; 		//ID		0
String _classification;	//分类		1
String _title;			//标题     		2 
String _list;			//文章目录	3	如有多项，##分割
String _summary;		//简介   		4
String _keyword;		//关键字		5	如有多项，##分割
String _content;		//内容		6	不同目录下文章内容用##分割，换行：<p>...</p>，空格:&nbsp
String _imgURL;			//图片链接	7
String _videoURL;		//视频链接	8
String _other;
*/

public class LibraryHTMLBuilder {
	String HTMLModlePath = null;
	String HTMLModelName = null;
	List<Article> articleList = null;

	public LibraryHTMLBuilder() {
		HTMLModlePath = PathUtil.Util_HTMLpath;

		System.out.println(HTMLModlePath);
		articleList = new ArticleDAO().findAll();

		HTMLModelName = PathUtil.Util_HTMLModelName;
	}

	String[] dataMes = new String[9];

	public void SetHTMLModel(String ModelPath, String ModeName) {
		HTMLModlePath = ModelPath;
		HTMLModelName = ModeName;
	}
	// 设置文章信息，提取数据库文件

	public void CreateAllHTML() {
		try {
			SetArticle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 设置每一条文章
	private void SetArticle() throws IOException {

		// 此处的每个都加i只是为了区别是不同的元组
		int articleNum = articleList.size();
		for (int i = 0; i < articleNum; i++) {
			Article now = articleList.get(i);
			/* article content format */
			dataMes[0] = now.getArticleId().toString();
			dataMes[1] = now.getClassification();
			dataMes[2] = now.getTitle();
			dataMes[3] = now.getList();
			dataMes[4] = now.getSummary();
			dataMes[5] = now.getKeyword();
			dataMes[6] = now.getContent();
			dataMes[7] = now.getImgUrl();
			// dataMes[8] = now.getVideoUrl();
			// 每设置好一个元组，便生成Html
			CreateHTML();
		}
	}

	/*
	 * 开始创建html
	 */
	private void SetHtmlContent(String ModelPath, String NewPath) throws IOException {
		File file = new File(ModelPath);

		Document doc = Jsoup.parse(file, "UTF-8");
		// point1:set title
		Element elmt = doc.getElementById("title_h1");// title
		elmt.html(dataMes[2]);

		// point2:set list
		elmt = doc.getElementById("list");
		String[] airticlList = dataMes[3].split("##");

		int listLen = airticlList.length;
		for (int i = 0; i < listLen; i++) {
			String html = "<li><a href=\"#" + ("info_title" + i) + "\">" + airticlList[i] + "</a><li>";
			elmt.append(html);
		}
		// point3: set description 简介
		elmt = doc.getElementById("summary");
		elmt.html(dataMes[4]);

		// point4:set articles
		elmt = doc.getElementById("article_div");
		String[] articles = dataMes[6].split("##");
		int minLen = Math.min(airticlList.length, articles.length);
		// int articleLen = articles.length;//listLen和articlelen应该是相等的
		for (int i = 0; i < minLen; i++) {
			String html = "<div><h1 id=\"info_title" + i + "\">" + airticlList[i] + "</h1>" + articles[i] + "</div>";
			elmt.append(html);
		}
		// set pictures
		String imgTag = "<img src=\"images\\" + dataMes[7] + "\" width=\"100%\" height=\"100%\">";
		elmt = doc.getElementById("header_right");
		elmt.append(imgTag);

		// set finished
		file = new File(NewPath);
		if (file.exists())
			file.delete();

		FileOutputStream fos = new FileOutputStream(NewPath, true);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

		osw.write(doc.html());
		osw.flush();
		osw.close();

		// fos.write(doc.html().getBytes());
		// fos.close();

	}

	private void CreateHTML() throws IOException {
		// step1 build path 制作路径
		String ModelPath = HTMLModlePath + "/" + HTMLModelName; // 模板文件完整路径
		String NewPath = HTMLModlePath + "/" + dataMes[0] + ".html";// 新建文件完整路径

		// System.out.println(ModelPath);
		// step2 set html and setp3 write html 解析html并设置新值，写出html
		SetHtmlContent(ModelPath, NewPath);
	}

	// public static void main(String[] args) {
	// LibraryHTMLBuilder lhb = new LibraryHTMLBuilder();
	// lhb.CreateAllHTML();
	// }

}
