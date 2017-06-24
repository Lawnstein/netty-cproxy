/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.heartbeat.impl;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.heartbeat.HeartBeatHandler;
import io.netty.tcp.util.ClientSocket;

/**
 * Digits头系统心跳检测.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class DigitsHeadHeartbeatHandler implements HeartBeatHandler {
	protected final static Logger logger = LoggerFactory.getLogger(DigitsHeadHeartbeatHandler.class);

	public DigitsHeadHeartbeatHandler() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.cproxy.transaction.heartbeat.HeartBeatHandler#check(int,
	 * io.netty.cproxy.transaction.model.BaseServiceChannel)
	 */
	@Override
	public boolean check(int heartbeatInterval, BaseServiceChannel channel) {
		Socket client = null;
		try {
			client = ClientSocket.connect(channel.getServIP(), channel.getServPort());
			String requestStr = "00000009HEARTBEAT";
			byte[] requestBytes = requestStr.getBytes();
			ClientSocket.write(client, requestBytes, 0, requestBytes.length);
			logger.debug("send request " + requestStr);

			byte[] resplen = new byte[8];
			ClientSocket.read(client, resplen, 0, 8, 3);
			String responseStr1 = new String(resplen);
			logger.debug("recv response length " + responseStr1);
			int iresplength = Integer.valueOf(responseStr1).intValue();
			byte[] respcnt = new byte[iresplength];
			ClientSocket.read(client, respcnt, 0, iresplength, 3);
			String responseStr2 = new String(respcnt);
			logger.debug("recv response content " + responseStr2);
		} catch (Exception e) {
			logger.error("ClientSocket proccess failed." + e);
			return false;
		} finally {
			if (client != null)
				ClientSocket.close(client);
		}

		return true;
	}

}
