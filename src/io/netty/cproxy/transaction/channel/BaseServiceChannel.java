/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.cproxy.transaction.model.ChannelStatus;

/**
 * 联机服务通道.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class BaseServiceChannel {
	protected final static Logger logger = LoggerFactory.getLogger(BaseServiceChannel.class);

	/**
	 * 服务地址
	 */
	private String servIP;
	/**
	 * 服务端口
	 */
	private int servPort;
	/**
	 * 服务状态：在线、离线
	 */
	private ChannelStatus status;
	
	/**
	 * 最大无心跳的连续次数.<br>
	 * 如果连续几次无心跳，则该Channel状态将置为挂起，服务不再可用。
	 */
	private int noheartbeatTimes;
	/**
	 * 上一次心跳时间.
	 */
	private int lastHeartbeatTime;
	/**
	 * 最新会话时间
	 */
	private long lastTalkTime;

	public BaseServiceChannel() {
		status = ChannelStatus.ONLINE;
	}

	public BaseServiceChannel(String servIP, int servPort) {
		status = ChannelStatus.ONLINE;
		this.servIP = servIP;
		this.servPort = servPort;
	}
	
	public String getServIP() {
		return servIP;
	}

	public void setServIP(String servIP) {
		this.servIP = servIP;
	}

	public int getServPort() {
		return servPort;
	}

	public void setServPort(int servPort) {
		this.servPort = servPort;
	}

	public ChannelStatus getStatus() {
		return status;
	}

	public void setStatus(ChannelStatus status) {
		this.status = status;
	}

	public long getLastTalkTime() {
		return lastTalkTime;
	}

	public void setLastTalkTime(long lastTalkTime) {
		this.lastTalkTime = lastTalkTime;
	}
	
	public boolean equals(BaseServiceChannel channel) {
		if (this == channel) return true;
		if (this.getServIP().equals(channel.getServIP()) && this.getServPort() == channel.getServPort()) return true;
		return false;
	}

	public int getNoheartbeatTimes() {
		return noheartbeatTimes;
	}

	public void setNoheartbeatTimes(int noheartbeatTimes) {
		this.noheartbeatTimes = noheartbeatTimes;
	}

	public int getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}

	public void setLastHeartbeatTime(int lastHeartbeatTime) {
		this.lastHeartbeatTime = lastHeartbeatTime;
	}

	@Override
	public String toString() {
		return "BaseServiceChannel [servIP=" + servIP + ", servPort=" + servPort
				+ ", status=" + status + ", noheartbeatTimes="
				+ noheartbeatTimes + ", lastHeartbeatTime=" + lastHeartbeatTime
				+ ", lastTalkTime=" + lastTalkTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((servIP == null) ? 0 : servIP.hashCode());
		result = prime * result + servPort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseServiceChannel other = (BaseServiceChannel) obj;
		if (servIP == null) {
			if (other.servIP != null)
				return false;
		} else if (!servIP.equals(other.servIP))
			return false;
		if (servPort != other.servPort)
			return false;
		return true;
	}
	
}
