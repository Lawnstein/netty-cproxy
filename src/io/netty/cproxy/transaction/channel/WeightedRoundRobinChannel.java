/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.channel;

/**
 * 基于权重轮询调度的通道.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class WeightedRoundRobinChannel extends BaseServiceChannel {

	private int weight;

	public WeightedRoundRobinChannel() {
		super();
		this.weight = 100;
	}

	public WeightedRoundRobinChannel(int weight) {
		super();
		if (weight < 0 || weight > 100)
			throw new RuntimeException("Unexpected weight value " + weight
					+ ", the range is 0 to 100.");
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WeightedRoundRobinChannel [weight=" + weight + ", getServIP()=" + getServIP() + ", getServPort()=" + getServPort()
				+ ", getStatus()=" + getStatus() + ", getLastTalkTime()=" + getLastTalkTime() + ", getNoheartbeatTimes()="
				+ getNoheartbeatTimes() + ", getLastHeartbeatTime()=" + getLastHeartbeatTime() + ", toString()=" + super.toString()
				+ ", hashCode()=" + hashCode() + ", getClass()=" + getClass() + "]";
	}
	
	
}
