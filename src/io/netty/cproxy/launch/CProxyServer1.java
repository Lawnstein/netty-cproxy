/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.launch;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.console.ConsoleHandler;
import io.netty.cproxy.transaction.load.LoadScheduling;
import io.netty.cproxy.transaction.load.impl.RandomScheduling;
import io.netty.cproxy.transaction.load.impl.WeightedRoundRobinScheduling;
import io.netty.cproxy.transaction.model.ProxyGroup;
import io.netty.tcp.server.TcpServer;

/**
 * 在线负载服务.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class CProxyServer1 {
	protected final static Logger logger = LoggerFactory.getLogger(CProxyServer1.class);

	private static int consolePort = 8001;
	private static int transactPort = 8000;
	private static String loaderScheduling = RandomScheduling.RANDOM;
	public static Map<String, ProxyGroup> serviceGroupMap = new HashMap<String, ProxyGroup>();

	public final static String PROP_CONSOLE_PORT = "console.port";
	public final static String PROP_TRANSACT_PORT = "transaction.port";
	public final static String PROP_LOADER_SCHEDULING = "proxy.loader";

	public CProxyServer1() {
	}

	/**
	 * 获取注册组。
	 * 
	 * @param group
	 * @return
	 */
	public static ProxyGroup getServiceGroup(String group) {
		return serviceGroupMap.get(group);
	}

	public static boolean isEmpty(String s) {
		if (s == null || s.length() == 0)
			return true;
		return false;
	}

	public static void main(String[] args) throws Exception {

		String transactionPortS = System.getProperty(PROP_TRANSACT_PORT);
		String consolePortS = System.getProperty(PROP_CONSOLE_PORT);
		String loaderSettingS = System.getProperty(PROP_LOADER_SCHEDULING);
		if (!isEmpty(transactionPortS))
			transactPort = Integer.valueOf(transactionPortS);
		if (!isEmpty(consolePortS))
			consolePort = Integer.valueOf(consolePortS);
		if (!isEmpty(loaderSettingS)) {
			if (loaderSettingS.equals(LoadScheduling.RANDOM))
				loaderScheduling = LoadScheduling.RANDOM;
			if (loaderSettingS.equals(LoadScheduling.WEIGHTED_ROUND))
				loaderScheduling = LoadScheduling.WEIGHTED_ROUND;
		}

		/**
		 * 调度总中心。
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("TranService");
				
				ProxyGroup serviceGroup = new ProxyGroup();
				serviceGroup.setServPort(transactPort);
				if (loaderScheduling.equals(LoadScheduling.RANDOM)) {
					serviceGroup.setLoader(new RandomScheduling());
				} else if (loaderScheduling
						.equals(LoadScheduling.WEIGHTED_ROUND)) {
					serviceGroup.setLoader(new WeightedRoundRobinScheduling());
				}
				serviceGroupMap.put(serviceGroup.getId(), serviceGroup);
				serviceGroup.start();

			}

		}).start();

		/**
		 * 监控接入。
		 */
		TcpServer consoleServer = new TcpServer();
		consoleServer.setPort(consolePort);
		consoleServer.setServiceHandler(new ConsoleHandler());
		consoleServer.start();

	}
}
