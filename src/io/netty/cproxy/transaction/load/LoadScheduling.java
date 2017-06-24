/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.load;

import java.util.List;

import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;

/**
 * 负载调度算法.
 * @author Lawnstein.Chan 
 * @version $Revision:$
 */
public interface LoadScheduling {
	public final static String RANDOM = "random";
	public final static String WEIGHTED_ROUND = "weightedround";
	
	public void setChannelList(List channelList);
	public void init();
	public void reset();
	public void close();
	public BaseServiceChannel pickup(SocketChannel requestChannel);
}
