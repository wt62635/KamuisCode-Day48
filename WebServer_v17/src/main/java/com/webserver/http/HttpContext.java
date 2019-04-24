package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HTTP协议规定的相关内容
 * @author Administrator
 *
 */
public class HttpContext {
	/**
	 * 回车符CR
	 * ASC编码对应值：13
	 */
	public static final int CR = 13;
	/**
	 * 换行符LF
	 * ASC编码对应值：10
	 */
	public static final int LF = 10;
	
	/*
	 * 资源后缀名与Content-Type响应头对应值的映射关系
	 * key:资源后缀名
	 * value:Content-Type对应的值
	 */
	private static Map<String,String> mimeMapping = new HashMap<>();
	//初始化所有静态资源
	static {
		initMimeMapping();
	}
	
	private static void initMimeMapping() {
//		mimeMapping.put("html", "text/html");
//		mimeMapping.put("css", "text/css");
//		mimeMapping.put("js", "application/javascript");
//		mimeMapping.put("png", "image/png");
//		mimeMapping.put("gif", "image/gif");
//		mimeMapping.put("jpg", "image/jpeg");
		/*
		 * 使用DOM4J解析conf/web.xml文件
		 * 将根标签下所有名为<mime-mapping>的子标签
		 * 获取回来，并将其中的子标签：
		 * <extension>中间的文本作为key
		 * <mime-type>中间的文本作为value
		 * 来初始化mimeMapping这个Map
		 * 
		 * 初始化完毕后，这个Map应当有1000多个元素。
		 */
		
		try {
			SAXReader reader = new SAXReader(); //创建SAXReader对象
			Document doc = reader.read(new File("./conf/web.xml"));//读取Document的结果付给doc对象
			Element root = doc.getRootElement();//获取根元素
			List<Element> list = root.elements("mime-mapping");//获取所有子标签
			
			for (Element element : list) {//将取得的值放入map集合
				String key = element.elementText("extension");
				String value = element.elementText("mime-type");
				mimeMapping.put(key, value);
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		System.out.println(mimeMapping.size());
	}
	
	/**
	 * 根据给定的资源后缀名获取对应的Content-Type值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeMapping.get(ext);
	}
	public static void main(String[] args) {
		String type = HttpContext.getMimeType("css");
		System.out.println(type);//  text/css
	}
}
