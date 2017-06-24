/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.launch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.cproxy.console.ConsoleContent;
import io.netty.cproxy.transaction.model.ProxyGroup;
import io.netty.tcp.client.TcpClient;

/**
 * 命令行.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class CProxyClient {
	protected final static Logger logger = LoggerFactory.getLogger(CProxyClient.class);

	public final static String PROP_CONSOLE_ADDRESS = "console.address";
	public final static String PROP_CONSOLE_PORT = "console.port";

	public CProxyClient() {
	}

	public static void main(String[] args) throws Exception {
		String proxyServerAddress = System.getProperty(PROP_CONSOLE_ADDRESS);
		String proxyServerPort = System.getProperty(PROP_CONSOLE_PORT);
		String sProxyServerAddress = proxyServerAddress == null ? "127.0.0.1" : proxyServerAddress;
		int iProxyServerPort = Integer.valueOf(proxyServerPort == null ? "8001" : sProxyServerAddress);
		logger.info(
				PROP_CONSOLE_ADDRESS + "=" + sProxyServerAddress + ", " + PROP_CONSOLE_PORT + "=" + iProxyServerPort);

		TcpClient client = new TcpClient(sProxyServerAddress, iProxyServerPort);
		boolean commandMode = false;
		if (args.length == 0)
			commandMode = true;

		logger.info("ProxyClient command mode ? " + commandMode);
		String[] argv = null;
		while (true) {
			if (commandMode) {
				System.out.println("Please input your command！");
				System.out.print("> ");

				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String input = reader.readLine();
				argv = input.split("[ \t]");
				if (argv.length == 0)
					continue;
				if (argv[0].equals("exit")) {
					System.exit(0);
				}
			} else {
				argv = args;
			}

			ConsoleContent c = new ConsoleContent();
			Map m = new HashMap();
			if (ConsoleContent.CMD_REGISTER.equals(argv[0]) || ConsoleContent.CMD_UNREGISTER.equals(argv[0])) {
				if (argv.length != 3) {
					System.err.println("Invalid arguments: register/unregister ip port");
					if (commandMode)
						continue;
					else
						break;
				}
				c.setCommand(argv[0]);
				m.put(ConsoleContent.FLD_GROUP, ProxyGroup.DEFAULT_GROUP);
				m.put(ConsoleContent.FLD_IP, argv[1]);
				m.put(ConsoleContent.FLD_PORT, argv[2]);
			} else if (ConsoleContent.CMD_QUERY.equals(argv[0])) {
				c.setCommand(argv[0]);
				if (argv.length >= 2)
					m.put(ConsoleContent.FLD_GROUP, argv[1]);
				if (argv.length >= 3)
					m.put(ConsoleContent.FLD_TYPE, argv[2]);
				c.setContext(m);
			} else {
				System.err.println("Invalid arguments!");
				if (commandMode)
					continue;
				else
					break;
			}
			c.setContext(m);
			ConsoleContent r = (ConsoleContent) client.call(c);
			System.out.println(r);
			System.out.println("\n");

			if (!commandMode) {
				System.exit(0);
			}
		}

	}

}
