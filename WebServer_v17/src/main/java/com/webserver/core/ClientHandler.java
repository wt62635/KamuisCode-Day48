package com.webserver.core;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.webserver.http.HttpContext;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;
import com.webserver.servlet.UpdateServlet;

/**
 * 用于处理客户端请求
 * 
 * @author Administrator
 *
 */
public class ClientHandler implements Runnable {
	private Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			System.out.println("ClientHandler:开始处理请求");
			//1.准备工作
			//1.1实例化请求对象，解析请求
			HttpRequest request = new HttpRequest(socket);
			//1.2实例化响应对象
			HttpResponse response = new HttpResponse(socket);
			// 2.处理请求
			/*	
			 * 2.1通过request获取requestURI,用来得知用户请求的资源的路径 
			 * 2.2从webapps目录下根据该资源路径找到对应资源 
			 * 2.3判断该资源时是否真实存在
			 * 2.4存在则响应该资源 
			 * 2.5不存在则响应404页面
			 */
			// myweb/index.html
			String path = request.getRequestURI();
			//判断该请求是否为请求业务处理
			HttpServlet servlet = ServerContext.getServlet(path);
			if(servlet!=null) {
				servlet.service(request, response);

			}else {
				
			// 通过路径找到webapps目录下对应资源。
			File file = new File("webapps" + path);
			// 判断用户请求的资源是否真实存在
			if (file.exists()) {
				// 将该资源以标准的HTTP响应格式发送给客户端
				// 将要相应的资源设置到response的entity属性上
				System.out.println("ClientHandler:资源已找到！");
				
				//获取资源名称
				response.setEntity(file);
				String fileName = file.getName();
				System.out.println("资源名：" + fileName + "-----------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				
				//截取资源名的后缀。
				String ext = fileName.substring(fileName.lastIndexOf(".")+1);
				System.out.println("资源名后缀：" + ext + "--------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				
				//添加响应头
				response.putHeader("Content-Type", HttpContext.getMimeType(ext));
				response.putHeader("Content-Length", file.length()+"");
			} else {
				System.out.println("ClientHandler:资源不存在！");
				File file404 = new File("webapps/root/404.html");
				// 设置状态代码和描述
				response.setStatusCode(404);
				response.setStatusReason("NOT FOUND");
				
				// 设置响应正文为404页面
				response.setEntity(file404);
			}
		}
			// 3.发送响应
			response.flush();

			System.out.println("ClientHandler:处理完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 处理完毕后与客户端断开连接
			try {
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
