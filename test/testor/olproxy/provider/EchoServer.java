/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

/**
 * server 有粘包问题
 * 
 * @author xwalker
 */
public class EchoServer {
	protected final static Logger logger = LoggerFactory.getLogger(EchoServer.class);

	public void bind(int port) throws Exception {
		// 服务器线程组 用于网络事件的处理 一个用于服务器接收客户端的连接
		// 另一个线程组用于处理SocketChannel的网络读写
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			// NIO服务器端的辅助启动类 降低服务器开发难度
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)// 类似NIO中serverSocketChannel
					.option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
					.childHandler(
							new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel ch)
										throws Exception {
									logger.debug("accept a connection " + ch);
									ch.pipeline().addFirst("decoder", new DelimiterBasedFrameDecoder(100000000,true,Delimiters.lineDelimiter()));
									ch.pipeline().addLast("handler", new EchoSercerHandler());
								}
							});// 最后绑定I/O事件的处理类

			// 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler
			ChannelFuture f = serverBootstrap.bind(port).sync();
			System.out.println("timeServer启动，端口：" + port);
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();

		} finally {
			// 优雅退出 释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			logger.debug("服务器优雅的释放了线程资源...");
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 8000;
		// if (args.length >= 1) port = Integer.valueOf(args[0]);
		String ports = System.getProperty("transaction.port");
		if (ports != null && ports.length() > 0) {
			logger.debug("set port " + ports + ".");
			port = Integer.valueOf(ports);
		}
		logger.debug("try to start server on port " + port + "...");
		new EchoServer().bind(port);
	}

}