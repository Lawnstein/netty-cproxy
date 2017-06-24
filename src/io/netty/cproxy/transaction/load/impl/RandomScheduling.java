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
 * Random算法<br>
 * 对于无状态服务比较适用，随便选取一台机器就可以。
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class RandomScheduling implements LoadScheduling {
	protected final static Logger logger = LoggerFactory.getLogger(RandomScheduling.class);

	private Random random;

	/**
	 * 服务器集合
	 */
	private List<BaseServiceChannel> channelList;

	/**
	 * 
	 */
	public RandomScheduling() {
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
