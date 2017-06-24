/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.load.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.load.LoadScheduling;
import io.netty.cproxy.transaction.model.ChannelStatus;

/**
 * rr（Round Robin）轮询调度，轮叫调度<br>
 * 轮询调度算法的原理是每一次把来自用户的请求轮流分配给内部中的服务器，从1开始，直到N(内部服务器个数)，然后重新开始循环。算法的优点是其简洁性，它无需记录当前所有连接的状态，所以它是一种无状态调度。【提示：
 * 这里是不考虑每台服务器的处理能力】。
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class SimpleRoundRobinScheduling implements LoadScheduling {
	protected final static Logger logger = LoggerFactory.getLogger(SimpleRoundRobinScheduling.class);

	private int index;

	/**
	 * 服务器集合
	 */
	private List<BaseServiceChannel> channelList;

	/**
	 * 
	 */
	public SimpleRoundRobinScheduling() {
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
		index = 0;
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
		if (channelList.size() == 0)
			return null;

		BaseServiceChannel c;
		boolean single = channelList.size() == 1 ? true : false;
		while (true) {
			c = channelList.get(index++);
			if (index >= channelList.size())
				index = 0;

			if (c.getStatus().equals(ChannelStatus.ONLINE)) {
				return c;
			} else if (single) {
				break;
			}
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
