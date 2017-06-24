/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.console;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ConsoleContent implements Serializable {
	protected String command;
	protected Map context;
	
	final public static String CMD_REGISTER = "register";
	final public static String CMD_UNREGISTER = "unregister";
	final public static String CMD_QUERY = "query";
	final public static String CMD_APPLY = "apply";
	final public static String FLD_IP = "ip";
	final public static String FLD_PORT = "port";
	final public static String FLD_GROUP = "group";
	final public static String FLD_STATUS = "status";
	final public static String FLD_TYPE = "type";
	final public static String FLD_SERVICE_NUMB = "serviceNumb";
	final public static String FLD_SERVICE_LIST = "serviceList";
	final public static String FLD_LAST_TALKTIME = "lastTalkTime";
	final public static String FLD_CONNECT_NUMB = "connectNumb";
	final public static String FLD_CONNECT_LIST = "connectList";
	final public static String FLD_CONNECT_REQUEST = "connectRequest";
	final public static String FLD_CONNECT_RESPONSE = "connectResponse";
	final public static String FLD_RESPONSE_CODE = "responseCode";
	final public static String FLD_RESPONSE_MESSAGE = "responseMessage";
	final public static String VAL_RESPONSE_SUCCESS = "success";
	final public static String VAL_RESPONSE_NO_GROUP = "no_group";
	final public static String VAL_RESPONSE_NO_SERVICE = "no_service";
	final public static String VAL_TYPE_SERVICE = "service";
	final public static String VAL_TYPE_CONNECT = "connect";
	

	/**
	 * 
	 */
	public ConsoleContent() {
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Map getContext() {
		return context;
	}

	public void setContext(Map context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "ConsoleContent [command=" + command + ", context=" + context
				+ "]";
	}

}
