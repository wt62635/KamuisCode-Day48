package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理修改密码业务
 * @author Administrator
 *
 */
public class UpdateServlet extends HttpServlet{
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		/*
		 * 1.通过request获取用户在页面上输入的密码信息
		 * 
		 * 2.匹配读取user.dat文件中的用户名和密码信息
		 * 
		 * 3.设置response响应登录结果页面
		 */
		// 1
		String username = request.getParameter("username");
		String oldPassword = request.getParameter("oldpassword");
		String newPassword = request.getParameter("newpassword");
		
		System.out.println("username:" + username);
		System.out.println("oldPassword:" + oldPassword);
		System.out.println("newPassword:" + newPassword);
		/*
		 * 2
		 */
		try {

			RandomAccessFile raf = new RandomAccessFile("./user.dat", "rw");

			/*
			 * 首先读取user.dat文件中的每个用户信息 比对用户名和密码与当前登录用户的名字与旧密码是否一致， 若一致则说明该用户可以登录，
			 * 这时设置response响应用户提示页面：密码修改成功！。 否则执行密码修改失败操作。
			 * 
			 * 用户提示页面：update_fail.html
			 */
			System.out.println("文本字节长度~~~~~~~~~~~~~~~~~~~~~~~~~~~：" + raf.length());
			for (int i = 0; i <= raf.length() / 100; i++) {
				raf.seek(i * 100);// 进入下一行
				byte name[] = new byte[32];// 定义字节组为32
				byte pass[] = new byte[32];
				raf.read(name);// 按字节组读取
				String un = new String(name, "utf-8").trim();// 读取username的字符串
				//判断用户名是否符合
				if (un.equals(username)) {
					raf.read(pass);
					String pw = new String(pass, "utf-8").trim();// 读取password的字符串
					//判断旧密码是否符合
					if(pw.equals(oldPassword)) {
						//修改旧密码
						raf.seek(i*100+32);
						byte data[] = newPassword.getBytes("utf-8");
						data = Arrays.copyOf(data, 32);
						raf.write(data);
						System.out.println("修改完毕！");
						
						forward("/myweb/update_success.html",request,response);
						return;
					}
					break;
				}
			}
			//跳转登录失败页面
			forward("/myweb/update_fail.html",request,response);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UpdateServlet:处理密码修改业务完毕！");
		
	}
}
