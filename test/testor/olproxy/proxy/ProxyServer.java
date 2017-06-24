/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.proxy;

import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.load.impl.RandomScheduling;
import io.netty.cproxy.transaction.model.ChannelStatus;
import io.netty.cproxy.transaction.model.ProxyGroup;

/**
 * TODO 请填写注释.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ProxyServer {

	/**
	 * 
	 */
	public ProxyServer() {

	}

	public static void main(String[] args) throws Exception {
		int port = 8000;
		String ports = System.getProperty("proxyserver.port");
		if (ports != null && ports.length() > 0) port = Integer.valueOf(ports);
		
		
		
		BaseServiceChannel s1 = new BaseServiceChannel("127.0.0.1", 9001);
		BaseServiceChannel s2 = new BaseServiceChannel("127.0.0.1", 9002);
		BaseServiceChannel s3 = new BaseServiceChannel("127.0.0.1", 9003);
		BaseServiceChannel s4 = new BaseServiceChannel("127.0.0.1", 9004);
		BaseServiceChannel s5 = new BaseServiceChannel("127.0.0.1", 9005);
		
		RandomScheduling loader = new RandomScheduling();
		ProxyGroup g = new ProxyGroup();
		g.setId("Proxy");
		g.setStatus(ChannelStatus.ONLINE);
		g.setServPort(8000);
		g.setLoader(loader);
		g.registerProvider(s1);
		g.registerProvider(s2);
		g.registerProvider(s3);
		g.registerProvider(s4);
		g.registerProvider(s5);
		g.start();
	}
}
