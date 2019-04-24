package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理注册业务
 * 
 * @author Administrator
 *
 */
public class RegServlet extends HttpServlet{
	//重写父类方法
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("RegServlet:开始处理注册……");
		/*
		 * 1.通过request获取用户在页面上输入的注册信息
		 * 
		 * 2.将该用户的注册信息写入到文件user.dat中
		 * 
		 * 3.设置response响应注册结果页面
		 */
		// 1
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int age = Integer.parseInt(request.getParameter("age"));
		String nickname = request.getParameter("nickname");

		System.out.println("username:" + username);
		System.out.println("password:" + password);
		System.out.println("age:" + age);
		System.out.println("nickname:" + nickname);
		/*
		 * 2
		 */
		try {

			RandomAccessFile raf = new RandomAccessFile("./user.dat", "rw");

			/*
			 * 首先读取user.dat文件中的每个用户信息 比对用户名与当前注册用户的名字是否一致， 若一致则说明该用户名已经被使用，
			 * 这时设置response响应用户提示页面：该用户已存在。 否则执行下面原有的注册操作。
			 * 
			 * 用户提示页面：reg_fail.html
			 */
			System.out.println("文本字节长度~~~~~~~~~~~~~~~~~~~~~~~~~~~：" + raf.length());
			for (int i = 0; i <= raf.length() / 100; i++) {
				byte name[] = new byte[32];// 定义字节组为32
				raf.read(name);// 按字节组读取
				String un = new String(name, "utf-8").trim();// 读取username的字符串

				if (un.equals(username)) {// 判断
					//跳转注册失败页面
					forward("/myweb/reg_fail.html",request,response);
					return;
				}
				raf.seek(i * 100);// 进入下一行
			}
			// 指针拨至最后
			raf.seek(raf.length());
			// 写入用户名
			byte data[] = username.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			// 写入密码
			data = password.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			// 写入年龄
			raf.writeInt(age);
			// 写入昵称
			data = nickname.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);

			raf.close();
			// 3
			//跳转注册成功画面
			forward("/myweb/reg_success.html",request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RegServlet:处理注册业务完毕！");
	}

}
