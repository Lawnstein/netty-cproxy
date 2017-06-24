/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.model;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;

/**
 * 连接代理会话.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ProxySession {
	private BaseServiceChannel serviceConfig;
	private SocketChannel requestChannel;
	private SocketChannel responseChannel;
	private EventLoopGroup responseWorkerGroup;
	private ProxyGroup parent;

	public ProxySession() {
	}

	public BaseServiceChannel getServiceConfig() {
		return serviceConfig;
	}

	public void setServiceConfig(BaseServiceChannel serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	public ProxyGroup getParent() {
		return parent;
	}

	public void setParent(ProxyGroup parent) {
		this.parent = parent;
	}

	public void destroy() {
		this.parent.destroyConnection(this);
	}

	public SocketChannel getRequestChannel() {
		return requestChannel;
	}

	public void setRequestChannel(SocketChannel requestChannel) {
		this.requestChannel = requestChannel;
	}

	public SocketChannel getResponseChannel() {
		return responseChannel;
	}

	public void setResponseChannel(SocketChannel responseChannel) {
		this.responseChannel = responseChannel;
	}

	@Override
	public String toString() {
		return "ProxySession [serviceConfig=" + serviceConfig
				+ ", requestChannel=" + requestChannel + ", responseChannel="
				+ responseChannel + "]";
	}

	public EventLoopGroup getResponseWorkerGroup() {
		return responseWorkerGroup;
	}

	public void setResponseWorkerGroup(EventLoopGroup responseWorkerGroup) {
		this.responseWorkerGroup = responseWorkerGroup;
	}
}
