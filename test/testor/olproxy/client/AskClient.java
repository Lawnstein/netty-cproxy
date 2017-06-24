/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 请填写注释.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class AskClient {
	protected final static Logger logger = LoggerFactory.getLogger(AskClient.class);

	/**
	 * 
	 */
	public AskClient() {
		// TODO 自动生成的构造函数存根
	}

	public static String ask1(String ip, int port, String question) throws Exception {
		logger.info("try to create connection :" + ip + "/" + port + " with message " + question);
		logger.debug("try to create connection :" + ip + "/" + port + " with message " + question);
		logger.trace("try to create connection :" + ip + "/" + port + " with message " + question);
		System.in.read();
		return null;
	}
	public static String ask(String ip, int port, String question)
			throws Exception {
		logger.trace("try to create connection :" + ip + "/" + port + " with message " + question);
		Socket socket = new Socket(ip, port);
		String out  = question + "\n";
		logger.debug("request:" + out);
		
		// 向端口发出客户请求
		// 由Socket对象得到输出流，并构造PrintWriter对象
		PrintWriter os = new PrintWriter(socket.getOutputStream());
		os.print(out);
		os.flush();

		// 由系统标准输入设备构造BufferedReader对象
		// BufferedReader sin = new BufferedReader(
		// new InputStreamReader(System.in));
		// 由Socket对象得到输入流，并构造相应的BufferedReader对象
		BufferedReader is = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String readline = is.readLine(); // 从系统标准输入读入一字符串
		// while (!readline.equals("bye")) {
		// // 若从标准输入读入的字符串为 "bye"则停止循环
		// os.println(readline);
		// // 将从系统标准输入读入的字符串输出到Server
		// os.flush();
		// // 刷新输出流，使Server马上收到该字符串
		// logger.debug("Client:" + readline);
		// // 在系统标准输出上打印读入的字符串
		// logger.debug("Server:" + is.readLine());
		// // 从Server读入一字符串，并打印到标准输出上
		// readline = sin.readLine(); // 从系统标准输入读入一字符串
		// } // 继续循环
		os.close(); // 关闭Socket输出流
		is.close(); // 关闭Socket输入流
		socket.close(); // 关闭Socket
		
		logger.debug("response:" + readline);
		return readline;
	}

	public static void main(String[] args) {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>");
		try {
			AskClient.ask("127.0.0.1", 8000, "TIME");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("<<<<<<<<<<<<<<<<<<<<<<<");
	}
}
