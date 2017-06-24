/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.console.command;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.console.ConsoleContent;
import io.netty.cproxy.transaction.ProxyServer;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.model.ProxyGroup;

/**
 * 注册、注销处理.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class RegisterCommand {
	protected final static Logger logger = LoggerFactory.getLogger(RegisterCommand.class);

	public RegisterCommand() {
	}

	public boolean isEmpty(String s) {
		if (s == null || s.length() == 0) return true;
		return false;
	}
	public Map register(Map context) {
		Map result = new HashMap();
		String group = (String) context.get(ConsoleContent.FLD_GROUP);
		String ip = (String) context.get(ConsoleContent.FLD_IP);
		String port = (String) context.get(ConsoleContent.FLD_PORT);
		
		if (isEmpty(group)) group = ProxyGroup.DEFAULT_GROUP;
		BaseServiceChannel baseServiceChannel = new BaseServiceChannel(ip, Integer.valueOf(port));
		ProxyGroup serviceGroup = ProxyServer.getServiceGroup(group);
		if (serviceGroup == null) {
			result.put(ConsoleContent.FLD_RESPONSE_CODE,
					ConsoleContent.VAL_RESPONSE_NO_GROUP);
			return result;			
		}
		
		serviceGroup.registerProvider(baseServiceChannel);
		result.put(ConsoleContent.FLD_RESPONSE_CODE,
				ConsoleContent.VAL_RESPONSE_SUCCESS);
		return result;
	}

	public Map unregister(Map context) {
		Map result = new HashMap();
		String group = (String) context.get(ConsoleContent.FLD_GROUP);
		String ip = (String) context.get(ConsoleContent.FLD_IP);
		String port = (String) context.get(ConsoleContent.FLD_PORT);
		BaseServiceChannel baseServiceChannel = new BaseServiceChannel(ip,
				Integer.valueOf(port));
		ProxyGroup serviceGroup = ProxyServer.getServiceGroup(group);
		if (serviceGroup == null) {
			result.put(ConsoleContent.FLD_RESPONSE_CODE,
					ConsoleContent.VAL_RESPONSE_NO_GROUP);
			return result;			
		}
		serviceGroup.unregisterProvider(baseServiceChannel);
		result.put(ConsoleContent.FLD_RESPONSE_CODE,
				ConsoleContent.VAL_RESPONSE_SUCCESS);
		return result;
	}

}
