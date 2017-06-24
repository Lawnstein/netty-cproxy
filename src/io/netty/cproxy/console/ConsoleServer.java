/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.tcp.server.ServiceAppHandler;
import io.netty.tcp.server.TcpServer;


/**
 * 控制台服务.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ConsoleServer {
	protected final static Logger logger = LoggerFactory.getLogger(ConsoleServer.class);

	private int port;

	private TcpServer server = null;
	
	private ServiceAppHandler handler;


	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServiceAppHandler getHandler() {
		return handler;
	}

	public void setHandler(ServiceAppHandler handler) {
		this.handler = handler;
	}

	@Override
	public String toString() {
		return "ConsoleServer [port=" + port + ", server=" + server + ", handler=" + handler + "]";
	}
	
	public void start() {
		server = new TcpServer();
		server.setName("ConsoleServer");
		server.setPort(port);
		server.setServiceHandler(handler);

		logger.trace("server : " + this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					server.start();
				} catch (Exception e) {
					logger.error("start ConsoleServer Exception : " + e);
					e.printStackTrace();
				}				
			}
			
		}).start();;
	}

	public void stop() {
		if (server != null)
			try {
				server.stop();
			} catch (Exception e) {
				logger.error("stop ConsoleServer Exception : " + e);
				e.printStackTrace();
			}
	}

}
