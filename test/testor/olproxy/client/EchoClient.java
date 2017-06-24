/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * client 存在TCP粘包问题
 * 
 * @author xwlaker
 *
 */
public class EchoClient {
	protected final static Logger logger = LoggerFactory.getLogger(EchoClient.class);

	/**
	 * 连接服务器
	 * 
	 * @param port
	 * @param host
	 * @throws Exception
	 */
	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// 客户端辅助启动类 对客户端配置
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(new EchoClientHandler());
						}
					});
			logger.debug(System.currentTimeMillis() + " Bootstrap create OK.");

			// 异步链接服务器 同步等待链接成功
			ChannelFuture f = b.connect(host, port).sync();
			logger.debug(System.currentTimeMillis() + " 异步链接服务器 同步等待链接成功.");
			// 等待链接关闭
			f.channel().closeFuture().sync();
			logger.debug(System.currentTimeMillis() + " 等待链接关闭.");

		} finally {
			group.shutdownGracefully();
			System.out
					.println(System.currentTimeMillis() + " 客户端优雅的释放了线程资源...");
		}

		while (!group.isTerminated()) {
			logger.debug(System.currentTimeMillis()
					+ " group.isShuttingDown() ? " + group.isShuttingDown());
			logger.debug(System.currentTimeMillis() + " group.isShutdown() ? "
					+ group.isShutdown());
			logger.debug(System.currentTimeMillis()
					+ " group.isTerminated() ? " + group.isTerminated());
			Thread.sleep(1000);
		}

	}

	public static void main(String[] args) throws Exception {
		new EchoClient().connect(8000, "127.0.0.1");
		System.exit(0);
	}

}