package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 利用thymeleaf框架，将user.dat文件中用户信息
 * 展现在静态页面showAllUser.html中，并响应给客户端
 * @author Administrator
 *
 */

public class ShowAllUserServlet extends HttpServlet{
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("ShowAllUserServlet:开始展示用户信息……");
		
		/*
		 * 传建一个集合用于保存从user.dat文件中读取出来的所有用户信息，
		 * 集合中的每个元素表示一个公户的信息。
		 * 我们使用Map作为集合元素，其中key的用户的属性（用户名，密码等），
		 * value是该属性对应的值（张三，123456），这意味着每个Map实例表示一个员工的相关信息
		 * 
		 * OGNL表达式的思想……
		 */
		List<Map<String,String>> list = new ArrayList<>();
		
		try (RandomAccessFile raf = new RandomAccessFile("user.dat","rw")){
			for (int i = 0; i < raf.length()/100; i++) {//以100个字节码的长度作为数据块区分
				//读取用户名
				byte data[] = new byte[32];//取32个字节
				raf.read(data);//读取数据
				String userName = new String(data,"utf-8").trim();
				
				raf.read(data);
				String passWord = new String(data,"utf-8").trim();
				
				int age = raf.readInt();
				
				raf.read(data);
				String nickName = new String(data,"utf-8").trim();
				
				Map<String,String> user = new HashMap<>();
				user.put("username", userName);
				user.put("password", passWord);
				user.put("nickname", nickName);
				user.put("age", age+"");
				list.add(user);
			}
		} catch (Exception e) {
		}
		/*
		 * thymeleaf提供的模板引擎
		 * 就是利用这个类完成数据与静态页面的绑定
		 */
		TemplateEngine eng = new TemplateEngine();
		
		/*
		 * 解释器
		 * 用于给引擎设置各种参数，以及从哪里寻找静态页面等。
		 * FileTemplateResolver是基于文件系统寻找静态资源，加载静态文件资源使用的,路径与File类一致
		 */
		FileTemplateResolver resolver =  new FileTemplateResolver();
		//设置字符集，以便模板引擎可以按照正确的字符集加载页面
		resolver.setCharacterEncoding("utf-8");
		//将解释其设置到引擎中
		eng.setTemplateResolver(resolver);
		//以上操作后，引擎初始化完毕
		/*
		 * Context是以树状结构组件所有需要在页面上呈现的数据
		 */
		Context context = new Context();
		context.setVariable("list", list);
		
		/*
		 * 利用引擎将静态页面与数据进行绑定
		 * 返回值是一个字符串，内容就是绑定好数据的html代码
		 */
		String html = eng.process("./webapps/myweb/showAllUser.html", context);
		
		//响应客户端
		try {
			response.setContentData(html.getBytes("utf-8"));
			response.putHeader("Content-Type", "text/html");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
