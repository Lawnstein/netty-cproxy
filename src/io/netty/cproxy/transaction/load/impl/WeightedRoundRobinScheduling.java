/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.load.impl;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.channel.WeightedRoundRobinChannel;
import io.netty.cproxy.transaction.load.LoadScheduling;
import io.netty.cproxy.transaction.model.ChannelStatus;

/**
 * wrr：weight,加权（以权重之间的比例实现在各主机之间进行调度）.<br>
 * 由于每台服务器的配置、安装的业务应用等不同，其处理能力会不一样。所以，我们根据服务器的不同处理能力，给每个服务器分配不同的权值，使其能够接受相应权值数的服务请求。<br>
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class WeightedRoundRobinScheduling implements LoadScheduling {
	protected final static Logger logger = LoggerFactory.getLogger(WeightedRoundRobinScheduling.class);

	/**
	 * 上一次选择的服务器
	 */
	private int currentIndex = -1;

	/**
	 * 当前调度的权值
	 */
	private int currentWeight = 0;

	/**
	 * 最大权重
	 */
	private int maxWeight = 100;

	/**
	 * 所有服务器权重的最大公约数
	 */
	private int gcdWeight = 0;

	/**
	 * 服务器集合
	 */
	private List<WeightedRoundRobinChannel> channelList;

	public WeightedRoundRobinScheduling() {
		init();
	}

	public List<WeightedRoundRobinChannel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List channelList) {
		this.channelList = channelList;

	}

	/**
	 * 返回最大公约数
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static int gcd(int a, int b) {
		BigInteger b1 = new BigInteger(String.valueOf(a));
		BigInteger b2 = new BigInteger(String.valueOf(b));
		BigInteger gcd = b1.gcd(b2);
		return gcd.intValue();
	}

	/**
	 * 返回所有服务器权重的最大公约数
	 * 
	 * @param channelList
	 * @return
	 */
	private static int getGCDForChannels(List<WeightedRoundRobinChannel> channelList) {
		int w = 0;
		for (int i = 0, len = channelList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = gcd(channelList.get(i).getWeight(), channelList.get(i + 1).getWeight());
			} else {
				w = gcd(w, channelList.get(i + 1).getWeight());
			}
		}
		return w;
	}

	/**
	 * 返回所有服务器中的最大权重
	 * 
	 * @param channelList
	 * @return
	 */
	public static int getMaxWeightForChannels(List<WeightedRoundRobinChannel> channelList) {
		int w = 0;
		for (int i = 0, len = channelList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = Math.max(channelList.get(i).getWeight(), channelList.get(i + 1).getWeight());
			} else {
				w = Math.max(w, channelList.get(i + 1).getWeight());
			}
		}
		return w;
	}

	public static int[] getWeights(List<WeightedRoundRobinChannel> channelList) {
		int[] wg = new int[2];
		int m = 0, w = 0;
		for (int i = 0, len = channelList.size(); i < len - 1; i++) {
			int wi = channelList.get(i).getWeight();
			int wi1 = channelList.get(i + 1).getWeight();
			if (m == 0) {
				m = Math.max(wi, wi1);
			} else {
				m = Math.max(m, wi1);
			}
			if (w == 0) {
				w = gcd(wi, wi1);
			} else {
				w = gcd(w, wi1);
			}
		}
		wg[0] = m;
		wg[1] = w;
		return wg;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#init()
	 */
	@Override
	public void init() {
		reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#reset()
	 */
	@Override
	public void reset() {
		int[] mw = getWeights(channelList);
		currentIndex = -1;
		currentWeight = 0;
		maxWeight = mw[0];
		gcdWeight = mw[1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#close()
	 */
	@Override
	public void close() {
	}

	/*
	 * 算法流程： 假设有一组服务器 S = {S0, S1, …, Sn-1} 有相应的权重，<br> 变量currentIndex表示上次选择的服务器权值currentWeight初始化为0，<br>
	 * currentIndex初始化为-1 ，<br> 当第一次的时候返回 权值取最大的那个服务器， 通过权重的不断递减寻找适合的服务器返回，直到轮询结束，权值返回为0.<br>
	 * 
	 * @see io.netty.cproxy.transaction.load.LoadScheduling#pickup()
	 */
	@Override
	public BaseServiceChannel pickup(SocketChannel requestChannel) {
		while (true) {
			currentIndex = (currentIndex + 1) % channelList.size();
			if (currentIndex == 0) {
				currentWeight = currentWeight - gcdWeight;
				if (currentWeight <= 0) {
					currentWeight = maxWeight;
					if (currentWeight == 0)
						return null;
				}
			}
			if (channelList.get(currentIndex).getWeight() >= currentWeight) {
				WeightedRoundRobinChannel channel = channelList.get(currentIndex);
				if (channel.getStatus().equals(ChannelStatus.ONLINE))
					return (BaseServiceChannel) channel;
			}
		}
	}

}
