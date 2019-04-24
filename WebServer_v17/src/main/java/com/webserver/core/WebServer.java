package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 服务端
 * @author Administrator
 *
 */

//通过http://localhost:8088/可以实现本地访问
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool ;//线程池
	/**
	 * 构造方法，用于初始化
	 */
	public WebServer() {
		try {
			System.out.println("正在启动服务器……");
			server = new ServerSocket(8088);
			//设置有50个线程的线程池
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("服务器启动完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			while(true) {
			System.out.println("等待客户端连接……");
			Socket socket = server.accept();
			System.out.println("一个客户端连接了！");
			//启动一个线程处理该客户端请求
			ClientHandler handler = new ClientHandler(socket);
			threadPool.execute(handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}
