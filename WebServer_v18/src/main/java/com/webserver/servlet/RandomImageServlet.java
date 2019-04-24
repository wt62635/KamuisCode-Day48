package com.webserver.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 随机验证码
 * @author Administrator
 *
 */
public class RandomImageServlet extends HttpServlet{
	private static char chars[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static int IMAGE_WIDTH = 65 ;
	private static int IMAGE_HEIGHT = 25 ;
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("RandomImageServlet:生成随机验证码图片……");
		//创建画布 xxx.jpg
		BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		//获取对这张图的画笔，用于往这张图上画内容
		Graphics g = image.getGraphics();
		//创建一个颜色（R,G,B）
		Color coloer = new Color(255,210,210);
		//设置画笔为这个颜色
		g.setColor(coloer);
		//使用画笔填充一个矩形，颜色为当前画笔指定颜色
		g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		//使用画笔画字符
		g.setColor(new Color(100,180,160));
		g.setFont(new Font("Default",Font.BOLD,20));
		//随机生成4个数
		Random rd = new Random();
		for (int i = 0; i < 4; i++) {
			//随机字符颜色
			g.setColor(new Color(rd.nextInt(100),rd.nextInt(180),rd.nextInt(160)));
			//随机生成字符间距15
			g.drawString(chars[rd.nextInt(chars.length)] + "", i*15+5, 18);
		}
		//画干扰线
		for (int i = 0; i < 6; i++) {
			g.setColor(new Color(rd.nextInt(100),rd.nextInt(180),rd.nextInt(160)));
			g.drawLine(rd.nextInt(IMAGE_HEIGHT), rd.nextInt(IMAGE_WIDTH), rd.nextInt(IMAGE_HEIGHT), rd.nextInt(IMAGE_WIDTH));
		}
		
		try {
			/*
			 * ByteArrayOutputStream是一个低级流，通过这个流写出的字节会保存到它内部的一个字节数组中。
			 */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", out);
			//获取输出流里已经写出来的字节（图片的所有字节）
			byte data[] = out.toByteArray();
			//将图片数据作为正文设置到响应对象中
			response.setContentData(data);
			response.putHeader("Content-Type", "image/jpeg");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("图片已生成");
	}
}
