/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.transaction.model.ProxyGroup;

/**
 * 代理负载服务.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ProxyServer {
	protected final static Logger logger = LoggerFactory.getLogger(ProxyGroup.class);

	public final static String DEFAULT_GROUP = "default";

	public final static Map<String, ProxyGroup> serviceGroupMap = new HashMap<String, ProxyGroup>();

	private List<ProxyGroup> groups;
	
	public List<ProxyGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ProxyGroup> groups) {
		this.groups = groups;
		logger.debug("initialized " + groups.size() + " groups.");
		for (ProxyGroup g : groups) {
			if (g.getId() == null || g.getId().length() == 0) {
				g.setId(DEFAULT_GROUP);
			}

			if (serviceGroupMap.get(g.getId()) == null)
				serviceGroupMap.put(g.getId(), g);
			else
				logger.warn("ProxyGroup " + g.getId() + " has exist, check it." + g);
		}
	}

	public ProxyServer() {
	}

	public static Map<String, ProxyGroup> getServicegroupmap() {
		return serviceGroupMap;
	}

	public static ProxyGroup getServiceGroup(String groupID) {
		return serviceGroupMap.get(groupID);
	}

	public void start() {
		logger.info("configured " + serviceGroupMap.size() + " ProxyGroups to start.");
		for (Entry<String, ProxyGroup> group : serviceGroupMap.entrySet()) {
			ProxyGroup proxy = group.getValue();
			proxy.start();
		}
	}

	public void stop() {
		logger.info("current " + serviceGroupMap.size() + " ProxyGroups to stop.");
		for (Entry<String, ProxyGroup> group : serviceGroupMap.entrySet()) {
			ProxyGroup proxy = group.getValue();
			proxy.stop();
		}
	}

}
