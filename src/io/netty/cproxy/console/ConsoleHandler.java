/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.console;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.cproxy.console.command.ApplyCommand;
import io.netty.cproxy.console.command.QueryCommand;
import io.netty.cproxy.console.command.RegisterCommand;
import io.netty.tcp.server.ServiceAppHandler;

/**
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ConsoleHandler implements ServiceAppHandler {
	protected final static Logger logger = LoggerFactory.getLogger(ConsoleHandler.class);

	private RegisterCommand registerCmd;
	private QueryCommand queryCmd;
	private ApplyCommand applyCmd;

	public ConsoleHandler() {
		super();
		registerCmd = new RegisterCommand();
		queryCmd = new QueryCommand();
		applyCmd = new ApplyCommand();
	}

	@Override
	public Object call(Object paramObject, Channel paramChannel) {
		if (logger.isTraceEnabled())
			logger.trace(" Read from Channel "
					+ paramChannel + " with message (" + paramObject.getClass() + ") ： " + paramObject);		
		
		
		ConsoleContent requestContent = (ConsoleContent) paramObject;
		Map result = null;
		if (requestContent.getCommand().equals(ConsoleContent.CMD_REGISTER)) {
			result = registerCmd.register(requestContent.getContext());
		}
		if (requestContent.getCommand().equals(ConsoleContent.CMD_UNREGISTER)) {
			result = registerCmd.unregister(requestContent.getContext());
		}
		if (requestContent.getCommand().equals(ConsoleContent.CMD_QUERY)) {
			result = queryCmd.query(requestContent.getContext());
		}
		if (requestContent.getCommand().equals(ConsoleContent.CMD_APPLY)) {
			result = applyCmd.apply(requestContent.getContext());
		}
		
		ConsoleContent responseContent = new ConsoleContent();
		responseContent.setContext(result);
		responseContent.setCommand(requestContent.getCommand());
		if (logger.isTraceEnabled())
			logger.trace(" Write to Channel "
					+ paramChannel + " with message (" + responseContent.getClass() + ") ： " + responseContent);	
		return responseContent;
	}

	@Override
	public void onChannelClosed(Channel paramChannel) {		
	}

	@Override
	public void onChannelException(Channel paramChannel, Throwable paramThrowable) {
	}
}