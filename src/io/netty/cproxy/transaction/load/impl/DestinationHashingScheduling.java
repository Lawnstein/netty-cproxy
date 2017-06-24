/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.load.impl;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.load.LoadScheduling;
import io.netty.cproxy.transaction.model.ChannelStatus;

/**
 * Dh:Destination hashing:目标地址散列。把同一个IP地址的请求，发送给同一个server。<br>
 * 目标地址散列调度算法也是针对目标IP地址的负载均衡，它是一种静态映射算法，通过一个散列（Hash）函数将一个目标IP地址映射到一台服务器。目标地址散列调度算法先根据请求的目标IP地址，作为散列键（Hash
 * Key）从静态分配的散列表找出对应的服务器，若该服务器是可用的且未超载，将请求发送到该服务器，否则返回空。<br>
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class DestinationHashingScheduling implements LoadScheduling {
	protected final static Logger logger = LoggerFactory.getLogger(DestinationHashingScheduling.class);

	private Random random;

	/**
	 * 服务器集合
	 */
	private List<BaseServiceChannel> channelList;

	/**
	 * 
	 */
	public DestinationHashingScheduling() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#init()
	 */
	@Override
	public void init() {
		reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#reset()
	 */
	@Override
	public void reset() {
		random = new Random(System.nanoTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#close()
	 */
	@Override
	public void close() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#pickup()
	 */
	@Override
	public BaseServiceChannel pickup(SocketChannel requestChannel) {
		logger.trace("current channelList.size=" + channelList.size());
		for (int i = 0, s = channelList.size(); i < s; i++) {
			int r = random.nextInt();
			if (r < 0)
				r = (-1) * r;
			logger.trace("random:" + r + ", i:" + r % s + ", size:" + channelList.size());
			BaseServiceChannel c = channelList.get(r % s);
			if (c.getStatus().equals(ChannelStatus.ONLINE))
				return c;
		}
		return null;
	}

	public List<BaseServiceChannel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List channelList) {
		this.channelList = channelList;
	}

}
