/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * client 存在TCP粘包问题
 * 
 * @author
 *
 */
public class EchoClientLoop {
	protected final static Logger logger = LoggerFactory.getLogger(EchoClientLoop.class);

	public static void main(String[] args) throws Exception {
		String ip = "127.0.0.1";
		int port = 8000;
		String ips = System.getProperty("echoserver.ip");
		String ports = System.getProperty("echoserver.port");
		if (ips != null && ips.length() > 0) {
			logger.debug("set ip " + ip + ".");
			ip = ips;
		}
		if (ports != null && ports.length() > 0) {
			logger.debug("set port " + ports + ".");
			port = Integer.valueOf(ports);
		}

		final String IP = ip;
		final int PORT = port;
		for (int i = 0; i < 10; i++) {
			logger.debug("Thread " + i + " running ...");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						for (int j = 0; j < 100; j++) {
							// new EchoClient().connect(PORT, IP);

							AskClient.ask(IP, PORT, "TIME");
							// AskClient.ask(IP, PORT,
							// Thread.currentThread().getName() +
							// " what's time?");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).run();
		}
		System.exit(0);
	}

}