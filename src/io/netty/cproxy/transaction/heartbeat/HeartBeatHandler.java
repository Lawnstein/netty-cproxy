/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.heartbeat;

import io.netty.cproxy.transaction.channel.BaseServiceChannel;

/**
 * 心跳检测.<br>
 * 一般为异步处理.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public interface HeartBeatHandler {
	public boolean check(int heartbeatInterval, BaseServiceChannel channel);
}
