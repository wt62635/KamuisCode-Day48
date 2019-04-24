package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.webserver.core.EmptyRequestException;

/**
 * 请求对象 该类的每一个实例用于表示浏览器发送过来的一个请求内容， 每个请求由三部分构成（请求行，消息头，消息正文）
 * 
 * @author Administrator
 *
 */
public class HttpRequest {
	/*
	 * 请求行相关信息
	 */
	// 请求方式
	private String method;
	// 请求资源的抽象路径
	private String url;
	// 请求时用的协议版本
	private String protocol;

	// 保存url中"?"左侧的请求部分
	private String requestURI;
	// 保存url中"?"右侧的参数部分
	private String queryString;
	// 保存解析出来的每一个参数（key:参数名 value：参数值）
	private Map<String, String> parameters = new HashMap<>();

	/*
	 * 消息头相关信息
	 */
	/*
	 * key:消息头名 value:消息头对应的值
	 */
	private Map<String, String> headers = new HashMap<String, String>();

	/*
	 * 消息正文相关信息
	 */
	// 消息正文的数据
	private byte[] data;

	/*
	 * 与连接相关的属性
	 */
	private Socket socket;
	private InputStream in;

	/**
	 * 初始化HttpRequest对象 初始化的过程就是解析请求的过程。 当前HttpRequest对象就表示浏览器发送过来的 这个请求内容了。
	 */
	public HttpRequest(Socket socket) {
		System.out.println("HttpRequest:开始解析请求……");
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * 解析请求的三步： 1.解析请求行 2.解析消息头 3.解析消息正文
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpRequest:解析请求完毕！");
	}

	/**
	 * 解析请求行
	 * 
	 * @throws EmptyRequestException
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("开始解析请求行……");
		/*
		 * 1.通过输入流读取第一行字符串（请求行内容） 2.将请求行内容按照空格拆分为三部分
		 * 3.将三部分内容设置到对应属性上（method,url,protoco）
		 */
		try {
			String line = readLine();
			System.out.println("开始解析请求行：" + line);
			/*
			 * 判断是否为空请求
			 */
			if ("".equals(line)) {
				throw new EmptyRequestException();
			}
			/*
			 * 后期循环接受客户端连接后，下面代码可能会出现 数组下标越界，这是由于空请求引起的，后面会解决
			 */

			String data[] = line.split("\\s");
			this.method = data[0];
			this.url = data[1];// 这里会出现下标越界
			this.protocol = data[2];
			System.out.println("method:" + method);
			System.out.println("url:" + url);
			System.out.println("protocol:" + protocol);

			// 进一步解析抽象路径部分
			parseURL();

		} catch (EmptyRequestException e) {
			System.out.println("空请求……");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析请求行完毕！");
	}

	private void parseURL() {
		System.out.println("开始解析抽象路径……");

		/*
		 * 一个请求的请求行大众抽象路径部分有两种情况： 1.不含有参数的，如： /myweb/index.html 2.含有参数的，如：
		 * /myweb/reg?username=xxx&password=xxx&... 因此我们对抽象路径进一步解析： 1.首先判断url的值是否含有"？"
		 * 1.1若不含有"?"则直接将url的值赋给属性requestURI,当前方法结束。 1.2若含有"?"执行步骤2
		 * 
		 * 2.将url按照"?"拆分为两部分，第一部分应当是请求部分，赋值给属性requestURI. 第二部分应当是参数部分，赋值给属性queryString
		 * 
		 * 3.进一步解析queryString,将其按照"&"拆分为若干个参数， 每一个参数再按照"="拆分为参数名与参数值。
		 * 并将参数名作为key,参数值作为value保存到属性parameters这个Map中完成解析工作。
		 */

		if (url.contains("?") == false) {
			requestURI = url;
		} else {
			String data[] = url.split("\\?");
			requestURI = data[0];
			if (data.length > 1) {
				queryString = data[1];
				putParameter(queryString);
			}
		}

			System.out.println("requestURI:" + requestURI);
			System.out.println("queryString:" + queryString);
			System.out.println("parameters:" + parameters);

			// 进一步解析
			System.out.println("解析抽象路径完毕！");
		}

	/**
	 * 解析消息头
	 */
	private void parseHeaders() {
		System.out.println("开始解析消息头……");
		/*
		 * 1.循环调用readLine方法读取每一个消息头 2.将消息头按照：“拆分，并将消息头的名字作为key，
		 * 消息头的值作为value保存到属性headers这个Map中” 3.如果调用readLine方法返回的是一个空字符串，
		 * 则说明了本次单独读取到了CRLF，那么就可以停止解析消息头了。
		 */
		try {
			String line = null;
			while (true) {
				line = readLine();
				if ("".equals(line)) {
					break;
				}
				String data[] = line.split(": ");
				headers.put(data[0], data[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("消息头：" + headers);
		System.out.println("解析消息头完毕！");
	}

	/**
	 * 解析消息正文
	 */
	private void parseContent() {
		System.out.println("开始解析消息正文……");
		try {
		if("post".equals(this.method.toLowerCase())) {
			String type = headers.get("Content-Type");
			if("application/x-www-form-urlencoded".equals(type)) {
				//获取消息正文
				int length = Integer.parseInt(headers.get("Content-Length"));
				System.out.println("消息正文长度为：" + length);
				data = new byte[length];
				//读取消息正文数据
				in.read(data);
				String text = new String(data,"ISO8859-1");
				System.out.println("消息正文为：" + text);
				putParameter(text);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("解析消息正文完毕！");
	}

	/**
	 * 通过对应客户端的输入流读取一行字符串 （以CRLF结尾）
	 * 
	 * @return
	 * @throws IOException
	 */
	private String readLine() throws IOException {
		// 读取一行字符串，以CRLF结尾
		StringBuilder builder = new StringBuilder();
		// c1表示上次读取到的字符，c2表示本次读取到的字符
		int c1 = -1, c2 = -1;
		while ((c2 = in.read()) != -1) {
			// 是否连续读取到了CR,LF
			if (c1 == HttpContext.CR && c2 == HttpContext.LF) {
				break;
			}
			builder.append((char) c2);
			c1 = c2;
		}
		return builder.toString().trim();
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	/**
	 * 获取指定名字消息头对应的值
	 * 
	 * @param name
	 * @return
	 */
	public String getHeaders(String name) {
		return headers.get(name);
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getQueryString() {
		return queryString;
	}

	/**
	 * 根据给定的参数名获取对应的参数值
	 * 
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}
	/**
	 * 根据给定的参数文本，通过“&”和“=”的分割后，
	 * 将值分别保存为parameter集合的键值
	 * @param text
	 */
	public void putParameter(String text) {
		String data[] = text.split("&");
		for (String pm : data) {
			String kv[] = pm.split("=");
			if (kv.length > 1) {
				parameters.put(kv[0], kv[1]);
			} else {
				parameters.put(kv[0], null);
			}
		}
	}
}
