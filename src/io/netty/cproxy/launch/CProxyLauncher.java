/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.launch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.context.Bootstrap;

/**
 * 基于tcp连接负载服务启动。
 * 
 * @author Lawnstein.Chan
 */
public class CProxyLauncher {
	protected final static Logger logger = LoggerFactory.getLogger(CProxyLauncher.class);

	public static void main(String args[]) {
		try {
			Bootstrap boot = new Bootstrap();
			boot.setContextConfigLocation("classpath:olproxy.xml");
			boot.init();
			System.out.println();
			System.exit(0);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			t.printStackTrace();
			System.exit(1);
		}
	}
}
