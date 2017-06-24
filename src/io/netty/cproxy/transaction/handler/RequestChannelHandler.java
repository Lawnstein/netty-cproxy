/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package io.netty.cproxy.transaction.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.cproxy.transaction.model.ChannelStatus;
import io.netty.cproxy.transaction.model.ProxySession;

/**
 * service request端连接.
 * 
 * @author Lawnstein.Chan
 * @version $Revision:$
 */
public class RequestChannelHandler extends ChannelInboundHandlerAdapter {
	protected final static Logger logger = LoggerFactory.getLogger(RequestChannelHandler.class);

	private ProxySession proxySession;

	public RequestChannelHandler(ProxySession proxySession) {
		this.proxySession = proxySession;
	}

	// @Override
	// public void channelRegistered(ChannelHandlerContext ctx) throws Exception
	// {
	// logger.info(this + " on channelRegistered。");
	// ctx.fireChannelRegistered();
	// }

	// @Override
	// public void channelUnregistered(ChannelHandlerContext ctx) throws
	// Exception {
	// logger.info(this + " on channelUnregistered。");
	// ctx.fireChannelUnregistered();
	// }

	// @Override
	// public void channelActive(ChannelHandlerContext ctx) throws Exception {
	// logger.info(this + " on channelActived。");
	// ctx.fireChannelActive();
	// }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		if (logger.isDebugEnabled())
			logger.debug(this + " on channelInactive。");
		ctx.fireChannelInactive();
		proxySession.destroy();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// ctx.fireChannelRead(msg);
		if (logger.isDebugEnabled())
			logger.debug(this + " Transfer from RequestChannel "
					+ proxySession.getRequestChannel() + " to ResponseChannel "
					+ proxySession.getResponseChannel() + " with message (" + msg.getClass() + ")");
		//ChannelFuture f = proxySession.getResponseChannel().writeAndFlush(msg).await();
		ChannelFuture f = proxySession.getResponseChannel().writeAndFlush(msg);
		
//		if (msg instanceof ByteBuf) {
//			((ByteBuf) msg).release();
//		}
	}

	// @Override
	// public void channelReadComplete(ChannelHandlerContext ctx) throws
	// Exception {
	// logger.info(this + " on channelReadComplete。");
	// ctx.fireChannelReadComplete();
	// }

	// @Override
	// public void connect(ChannelHandlerContext ctx, SocketAddress
	// remoteAddress,
	// SocketAddress localAddress, ChannelPromise promise)
	// throws Exception {
	// logger.info(this + " on connected。");
	// ctx.connect(remoteAddress, localAddress, promise);
	// }

	// @Override
	// public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
	// throws Exception {
	// logger.info(this + " on disconnected。");
	// ctx.disconnect(promise);
	// }

	// @Override
	// public void close(ChannelHandlerContext ctx, ChannelPromise promise)
	// throws Exception {
	// logger.info(this + " on close。");
	// ctx.close(promise);
	// }

	// @Override
	// public void read(ChannelHandlerContext ctx) throws Exception {
	// logger.info(this + " on read。");
	// ctx.read();
	// }

	// @Override
	// public void write(ChannelHandlerContext ctx, Object msg,
	// ChannelPromise promise) throws Exception {
	// logger.info(this + " on write。");
	// ctx.write(msg, promise);
	// }

	// @Override
	// public void flush(ChannelHandlerContext ctx) throws Exception {
	// logger.info(this + " on flush。");
	// ctx.flush();
	// }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
		logger.error(this + " request channel exceptionCaught。");
		proxySession.destroy();
		if (cause instanceof java.net.ConnectException) {
			proxySession.getServiceConfig().setStatus(ChannelStatus.OFFLINE);
		}
	}
}
