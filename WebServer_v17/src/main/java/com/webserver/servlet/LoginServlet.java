package com.webserver.servlet;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理登录业务
 * 
 * @author Administrator
 *
 */
public class LoginServlet extends HttpServlet{
	//重写父类方法
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("LoginServlet:开始处理登录……");
		/*
		 * 1.通过request获取用户在页面上输入的注册信息
		 * 
		 * 2.匹配读取user.dat文件中的用户名和密码信息
		 * 
		 * 3.设置response响应登录结果页面
		 */
		// 1
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		System.out.println("username:" + username);
		System.out.println("password:" + password);
		/*
		 * 2
		 */
		try {

			RandomAccessFile raf = new RandomAccessFile("./user.dat", "rw");

			/*
			 * 首先读取user.dat文件中的每个用户信息 比对用户名和密码与当前登录用户的名字密码是否一致， 若一致则说明该用户可以登录，
			 * 这时设置response响应用户提示页面：登录成功！。 否则执行登录失败操作。
			 * 
			 * 用户提示页面：login_fail.html
			 */
			System.out.println("文本字节长度~~~~~~~~~~~~~~~~~~~~~~~~~~~：" + raf.length());
			for (int i = 0; i <= raf.length() / 100; i++) {
				raf.seek(i * 100);// 进入下一行
				byte name[] = new byte[32];// 定义字节组为32
				byte pass[] = new byte[32];
				raf.read(name);// 按字节组读取
				String un = new String(name, "utf-8").trim();// 读取username的字符串
				if (un.equals(username)) {// 判断
					raf.read(pass);
					String pw = new String(pass, "utf-8").trim();// 读取password的字符串
					if(pw.equals(password)) {
						//跳转登录成功页面
						forward("/myweb/login_success.html",request,response);
						return;
					}
					break;
				}
			}
			//跳转登录失败页面
			forward("/myweb/login_fail.html",request,response);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("LoginServlet:处理登录业务完毕！");
	}

}
