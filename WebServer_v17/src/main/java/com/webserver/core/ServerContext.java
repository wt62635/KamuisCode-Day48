package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;
import com.webserver.servlet.UpdateServlet;

/**
 * 服务器相关配置信息
 * @author Administrator
 *
 */
public class ServerContext {
	/*
	 * 请求与对应Servler的关系
	 * key:请求路径
	 * value:处理该请求的Sevrlet实例
	 */
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();
	//静态代码块
	static {
		try {
			initSevrlet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 初始化Servlet
	 * @throws Exception 
	 * @throws InstantiationException 
	 */
	private static void initSevrlet(){
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
//		servletMapping.put("/myweb/update", new UpdateServlet());
		/*
		 * 解析conf/servletx.xml文件
		 * 将根标签下所有的<servlet>标签解析出来
		 * 并用其属性path的值作为key
		 * className属性的值使用放射方式加载对应的Servlet类并进行实例化，
		 * 然后将对应的实例作为value保存到servletMapping这个Map中。
		 */
		try {
		SAXReader reader = new SAXReader(); //创建SAXReader对象
		Document doc = reader.read(new File("./conf/servletx.xml"));//读取Document的结果付给doc对象
		Element root = doc.getRootElement();//获取根元素
		List<Element> list = root.elements("servlet");//获取所有子标签
		
		for (Element element : list) {//将取得的值放入map集合
			String path = element.attributeValue("path");
			Class cls = Class.forName(element.attributeValue("className"));
			HttpServlet servlet = (HttpServlet) cls.newInstance();
			servletMapping.put(path, servlet);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据请求路径获取对应的Servlet实例
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
		
	}
}
