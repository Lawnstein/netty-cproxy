/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.cproxy.exception.ConnectServiceProviderException;
import io.netty.cproxy.exception.NoServiceProviderException;
import io.netty.cproxy.transaction.ProxyServer;
import io.netty.cproxy.transaction.channel.BaseServiceChannel;
import io.netty.cproxy.transaction.handler.RequestChannelHandler;
import io.netty.cproxy.transaction.handler.ResponseChannelHandler;
import io.netty.cproxy.transaction.heartbeat.HeartBeatHandler;
import io.netty.cproxy.transaction.load.LoadScheduling;

/**
 * 联机服务组.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class ProxyGroup {
	protected final static Logger logger = LoggerFactory.getLogger(ProxyGroup.class);

	public final static String DEFAULT_GROUP = ProxyServer.DEFAULT_GROUP;
	public final static String PROP_NIOLOOP_THREADS = "io.netty.cproxy.eventLoopThreads";
	public final static String PROP_NETTY_THREADS = "io.netty.eventLoopThreads";

	public static int PROP_NIOLOOP_THREADS_VALUE = 0;
	static {
		String nioThreads = System.getProperty(PROP_NIOLOOP_THREADS);
		if (nioThreads == null || nioThreads.length() == 0)
			nioThreads = System.getProperty(PROP_NETTY_THREADS);
		if (nioThreads != null && nioThreads.length() != 0)
			PROP_NIOLOOP_THREADS_VALUE = Integer.valueOf(nioThreads);
	}

	/**
	 * Group-ID
	 */
	private String id;

	/**
	 * 当前监听服务地址
	 */
	private String servIP;

	/**
	 * 当前监听服务端口
	 */
	private int servPort;

	/**
	 * 当前服务状态/离线
	 */
	private ChannelStatus status;

	/**
	 * 心跳检测间隔毫秒数.millionseconds
	 */
	private int heartbeatInterval = 5000;

	/**
	 * 心跳检测器.
	 */
	private HeartBeatHandler heartbeatHandler;

	/**
	 * 负载在线服务数.
	 */
	private int onlineChannelCount = 0;

	/**
	 * 负载器.
	 */
	private LoadScheduling loader;

	/**
	 * 服务器集合Provider<responsor>
	 */
	private List<BaseServiceChannel> channelList;

	/**
	 * 连接对<request,response>。
	 */
	private Map<SocketChannel, ProxySession> channelMap;

	/**
	 * 网络io事件执行线程数
	 */
	private int eventThreadsNumb = 0;
	
	private EventLoopGroup serverBossGroup;
	private EventLoopGroup serverWorkerGroup;
	private EventLoopGroup clientsWorkerGroup;

	private Thread proxyThread = null;
	private Thread chkhtThread = null;
	
	public ProxyGroup() {
		id = ProxyServer.DEFAULT_GROUP;
		status = ChannelStatus.ONLINE;
		channelList = new ArrayList<BaseServiceChannel>();
		channelMap = new HashMap<SocketChannel, ProxySession>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public int getOnlineChannelCount() {
		return onlineChannelCount;
	}

	public void setOnlineChannelCount(int onlineChannelCount) {
		this.onlineChannelCount = onlineChannelCount;
	}

	public LoadScheduling getLoader() {
		return loader;
	}

	public void setLoader(LoadScheduling loader) {
		this.loader = loader;
		this.loader.setChannelList(channelList);
	}

	public List<BaseServiceChannel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<BaseServiceChannel> channelList) {
		this.channelList = channelList;
	}

	public Map<SocketChannel, ProxySession> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<SocketChannel, ProxySession> channelMap) {
		this.channelMap = channelMap;
	}

	public HeartBeatHandler getHeartbeatHandler() {
		return heartbeatHandler;
	}

	public void setHeartbeatHandler(HeartBeatHandler heartbeatHandler) {
		this.heartbeatHandler = heartbeatHandler;
	}

	public int getEventThreadsNumb() {
		return eventThreadsNumb;
	}

	public void setEventThreadsNumb(int eventThreadsNumb) {
		this.eventThreadsNumb = eventThreadsNumb;
	}

	public EventLoopGroup getServerBossGroup() {
		return serverBossGroup;
	}

	public void setServerBossGroup(EventLoopGroup serverBossGroup) {
		this.serverBossGroup = serverBossGroup;
	}

	public EventLoopGroup getServerWorkerGroup() {
		return serverWorkerGroup;
	}

	public void setServerWorkerGroup(EventLoopGroup serverWorkerGroup) {
		this.serverWorkerGroup = serverWorkerGroup;
	}

	public EventLoopGroup getClientsWorkerGroup() {
		return clientsWorkerGroup;
	}

	public void setClientsWorkerGroup(EventLoopGroup clientsWorkerGroup) {
		this.clientsWorkerGroup = clientsWorkerGroup;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProxyGroup [id=" + id + ", servIP=" + servIP + ", servPort=" + servPort + ", status=" + status + ", heartbeatInterval="
				+ heartbeatInterval + ", heartbeatHandler=" + heartbeatHandler + ", onlineChannelCount=" + onlineChannelCount + ", loader="
				+ loader + ", channelList=" + channelList + ", channelMap=" + channelMap + ", eventThreadsNumb=" + eventThreadsNumb + "]";
	}

	/**
	 * 注册服务Provider。
	 * 
	 * @param serviceProvider
	 */
	public void registerProvider(BaseServiceChannel serviceProvider) {
		if (this.channelList.contains(serviceProvider)) {
			logger.warn("serviceProvider has exists: " + serviceProvider);
			return;
		} else {
			for (BaseServiceChannel s : channelList) {
				if (s.equals(serviceProvider)) {
					logger.warn("serviceProvider has exists: " + s);
					s.setStatus(ChannelStatus.ONLINE);
					return;
				}
			}
		}
		this.channelList.add(serviceProvider);
	}

	/**
	 * 注销服务Provider。
	 * 
	 * @param serviceProvider
	 */
	public void unregisterProvider(BaseServiceChannel serviceProvider) {
		if (this.channelList.contains(serviceProvider))
			this.channelList.remove(serviceProvider);
		else {
			for (BaseServiceChannel s : channelList) {
				if (s.equals(serviceProvider)) {
					channelList.remove(s);
					break;
				}
			}
		}
	}

	/**
	 * 使Provider离线状态。
	 * 
	 * @param serviceProvider
	 */
	public boolean offlineProvider(BaseServiceChannel serviceProvider) {
		BaseServiceChannel c = null;
		if (this.channelList.contains(serviceProvider))
			c = serviceProvider;
		else {
			for (BaseServiceChannel s : channelList) {
				if (s.equals(serviceProvider)) {
					c = s;
					break;
				}
			}
		}
		if (c == null) {
			logger.error("no such provider registered:" + serviceProvider);
			return false;
		}
		c.setStatus(ChannelStatus.OFFLINE);
		return true;
	}

	public ProxySession createConnection(SocketChannel requestChannel) throws Exception {
		SocketChannel responseChannel = null;
		BaseServiceChannel serviceProvider = loader.pickup(requestChannel);
		if (serviceProvider == null)
			throw new NoServiceProviderException("no service provider found.");

		final ProxySession proxySession = new ProxySession();
		proxySession.setRequestChannel(requestChannel);
		proxySession.setServiceConfig(serviceProvider);
		proxySession.setParent(this);

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 32 * 1024);
		bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024);
		// 超时等待3seconds
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		// 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.group(clientsWorkerGroup);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel responseChannel) throws Exception {
				responseChannel.pipeline().addLast(new ResponseChannelHandler(proxySession));
			}
		});
		ChannelFuture future = bootstrap.connect(serviceProvider.getServIP(), serviceProvider.getServPort()).sync();
		if (future.isSuccess()) {
			responseChannel = (SocketChannel) future.channel();
			serviceProvider.setLastTalkTime(System.currentTimeMillis());
			logger.info("Proxy connect response provider success:" + serviceProvider);
		} else {
			serviceProvider.setStatus(ChannelStatus.OFFLINE);
			throw new ConnectServiceProviderException("connect " + serviceProvider + " failed.");
		}

		proxySession.setResponseChannel(responseChannel);
		channelMap.put(requestChannel, proxySession);
		return proxySession;
	}

	public void destroyConnection(ProxySession proxySession) {
		logger.trace("try to destroy ProxySession " + proxySession);
		if (!channelMap.containsValue(proxySession)) {
			logger.warn("ProxySession " + proxySession + " maybe has destroyed, no need to do it again.");
			return;
		}
		if (proxySession.getRequestChannel() != null) {
			channelMap.remove(proxySession.getRequestChannel());
			proxySession.getRequestChannel().close();
		}
		if (proxySession.getResponseChannel() != null) {
			proxySession.getResponseChannel().close();
			// proxyChannel.getResponseWorkerGroup().shutdownGracefully();
		}
		logger.info("ProxySession " + proxySession + " destroyed successfully.");

	}


	public void start() {

		if (loader != null) {
			loader.setChannelList(getChannelList());
		}
		
		proxyThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("ProxyService-" + id);
				loader.init();
				if (eventThreadsNumb > 0)
					clientsWorkerGroup = new NioEventLoopGroup(eventThreadsNumb);
				else if (PROP_NIOLOOP_THREADS_VALUE > 0)
					clientsWorkerGroup = new NioEventLoopGroup(PROP_NIOLOOP_THREADS_VALUE);
				else
					clientsWorkerGroup = new NioEventLoopGroup();
				serverBossGroup = new NioEventLoopGroup(1);
				if (eventThreadsNumb > 0)
					serverWorkerGroup = new NioEventLoopGroup(eventThreadsNumb);
				else if (PROP_NIOLOOP_THREADS_VALUE > 0)
					serverWorkerGroup = new NioEventLoopGroup(PROP_NIOLOOP_THREADS_VALUE);
				else
					serverWorkerGroup = new NioEventLoopGroup();

				ServerBootstrap bootstrap = new ServerBootstrap();
				bootstrap.group(serverBossGroup, serverWorkerGroup);
				bootstrap.channel(NioServerSocketChannel.class);
				// 等待连接超时5seconds
				bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
				// 服务端处理线程全忙后，允许多少个新请求进入等待。
				bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
				// 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
				bootstrap.option(ChannelOption.TCP_NODELAY, true);
				bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 32 * 1024);
				bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024);
				// bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
				// 启用心跳机制，保持长连接状态
				bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel requestChannel) throws Exception {
						logger.info("Accept a request connection " + requestChannel);
						requestChannel.pipeline().addLast(new RequestChannelHandler(createConnection(requestChannel)));
					}
				});
				logger.info("try to bind port " + servPort + " for ProxyServer ...");
				try {
					ChannelFuture f = bootstrap.bind(servPort).sync();
					if (f.isSuccess()) {
						logger.info("Proxyserver start on port " + servPort);
					}
					f.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				} finally {
					serverBossGroup.shutdownGracefully();
					serverWorkerGroup.shutdownGracefully();
					logger.info("Proxy server shutdownGracefully");
				}
			}
		});
		proxyThread.start();


		if (heartbeatHandler != null) {
			chkhtThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						Thread.currentThread().setName("ProxyService-checkht-" + id);
						
						for (BaseServiceChannel service : channelList) {
							long currentTimeMillis = System.currentTimeMillis();
							long lastTimeMillis = service.getLastHeartbeatTime() > service.getLastTalkTime()
									? service.getLastHeartbeatTime() : service.getLastTalkTime();
							if (service.getStatus().equals(ChannelStatus.ONLINE)) {
								if (currentTimeMillis - lastTimeMillis > heartbeatInterval) {
									heartbeatHandler.check(heartbeatInterval, service);
								}
							} else if (service.getStatus().equals(ChannelStatus.OFFLINE)) {
								if (currentTimeMillis - lastTimeMillis > heartbeatInterval * 2) {
									heartbeatHandler.check(heartbeatInterval, service);
								}
							}
						}
						
						try {
							Thread.currentThread().sleep(heartbeatInterval);
						} catch (InterruptedException e) {
							logger.error("Loop for monit InterruptedException " + e);
							e.printStackTrace();
						}
					}
				}
			});
			chkhtThread.start();
		} else {
			logger.debug("no heartbeatHandler implemented for " + id);
		}
		
		
		logger.info("ProxyGrou has started." + this) ;
	}

	public void stop() {
		for (Entry<SocketChannel, ProxySession> entry : channelMap.entrySet()) {
			entry.getValue().destroy();
		}		

		if (chkhtThread != null) {
			chkhtThread.interrupt();
			chkhtThread.stop();
			chkhtThread = null;
		}
		if (proxyThread != null) {
			proxyThread.interrupt();
			proxyThread.stop();
			proxyThread = null;
		}

		logger.info("ProxyGrou has stoped." + this) ;
	}

}
