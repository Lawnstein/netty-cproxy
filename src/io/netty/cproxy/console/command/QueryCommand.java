/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.console.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.console.ConsoleContent;
import io.netty.cproxy.transaction.ProxyServer;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.model.ProxyGroup;
import io.netty.cproxy.transaction.model.ProxySession;

/**
 * 注册、注销处理.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class QueryCommand {
	protected final static Logger logger = LoggerFactory.getLogger(QueryCommand.class);

	public QueryCommand() {
	}

	public boolean isEmpty(String s) {
		if (s == null || s.length() == 0)
			return true;
		return false;
	}

	public Map query(Map context) {
		Map result = new HashMap();
		String group = (String) context.get(ConsoleContent.FLD_GROUP);
		String type = (String) context.get(ConsoleContent.FLD_TYPE);
		if (isEmpty(group)) {
			for (Entry<String, ProxyGroup> entry : ProxyServer.serviceGroupMap
					.entrySet()) {
				ProxyGroup s = entry.getValue();

				Map detail = new HashMap();
				detail.put(ConsoleContent.FLD_IP, s.getServIP());
				detail.put(ConsoleContent.FLD_PORT, s.getServPort());
				detail.put(ConsoleContent.FLD_STATUS, s.getStatus().getSt());
				detail.put(ConsoleContent.FLD_SERVICE_NUMB, s.getChannelList()
						.size());
				detail.put(ConsoleContent.FLD_CONNECT_NUMB, s.getChannelMap()
						.size());

				result.put(s.getId(), detail);
			}
		} else {
			ProxyGroup s = ProxyServer.getServiceGroup(group);
			if (s == null) {
				result.put(ConsoleContent.FLD_RESPONSE_CODE,
						ConsoleContent.VAL_RESPONSE_NO_GROUP);
				return result;			
			}
			result.put(ConsoleContent.FLD_IP, s.getServIP());
			result.put(ConsoleContent.FLD_PORT, s.getServPort());
			result.put(ConsoleContent.FLD_STATUS, s.getStatus().getSt());
			result.put(ConsoleContent.FLD_SERVICE_NUMB, s.getChannelList().size());
			result.put(ConsoleContent.FLD_CONNECT_NUMB, s.getChannelMap().size());


			if (ConsoleContent.VAL_TYPE_CONNECT.equals(type)) {
				ArrayList connectList = new ArrayList();
				for (Entry<SocketChannel, ProxySession> entry : s.getChannelMap()
						.entrySet()) {
					ProxySession x = entry.getValue();
					Map m = new HashMap();
					m.put(ConsoleContent.FLD_CONNECT_REQUEST,
							x.getRequestChannel());
					m.put(ConsoleContent.FLD_CONNECT_RESPONSE,
							x.getResponseChannel());
					connectList.add(m);
				}
				result.put(ConsoleContent.FLD_CONNECT_LIST, connectList);
			} else {
			//if (type.equals(ConsoleContent.VAL_TYPE_SERVICE)) {
				ArrayList serviceList = new ArrayList();
				for (BaseServiceChannel c : s.getChannelList()) {
					Map m = new HashMap();
					m.put(ConsoleContent.FLD_IP, c.getServIP());
					m.put(ConsoleContent.FLD_PORT, c.getServPort());
					m.put(ConsoleContent.FLD_STATUS, c.getStatus().getSt());
					m.put(ConsoleContent.FLD_LAST_TALKTIME, c.getLastTalkTime());
					serviceList.add(m);
				}
				result.put(ConsoleContent.FLD_SERVICE_LIST, serviceList);
				
			}  

		} 

		result.put(ConsoleContent.FLD_RESPONSE_CODE,
				ConsoleContent.VAL_RESPONSE_SUCCESS);
		return result;
	}

}
