/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.client;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Client 网络IO事件处理
 * 
 * @author xwalker
 *
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	protected final static Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

	private ByteBuf firstMessage;

	public EchoClientHandler() {
		byte[] req = "TIME".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelRegistered();
		logger.debug(System.currentTimeMillis() + " " + this
				+ " channelRegistered。");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelUnregistered();
		logger.debug(System.currentTimeMillis() + " " + this
				+ " channelUnregistered。");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
		logger.debug(System.currentTimeMillis() + " " + this
				+ " channelActived。");
		ctx.writeAndFlush(firstMessage);
		logger.debug(System.currentTimeMillis()
				+ " 客户端active and writeAndFlush. ctx:" + ctx.getClass());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
		logger.debug(System.currentTimeMillis() + " " + this
				+ " channelInactive。");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// ctx.fireChannelRead(msg);
		logger.debug(System.currentTimeMillis() + " " + this + " channelRead。");
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		// logger.debug("Now is:" + body);
		logger.debug(System.currentTimeMillis() + " 客户端收到服务器响应数据：" + body);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelReadComplete();
		logger.debug(System.currentTimeMillis() + " " + this
				+ " channelReadComplete。");
		ctx.flush();
		logger.debug(System.currentTimeMillis() + " 客户端收到服务器响应数据处理完成");
		ctx.close();
	}

	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
			SocketAddress localAddress, ChannelPromise promise)
			throws Exception {
		ctx.connect(remoteAddress, localAddress, promise);
		logger.debug(System.currentTimeMillis() + " " + this + " connected。");
	}

	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		ctx.disconnect(promise);
		logger.debug(System.currentTimeMillis() + " " + this + " disconnected。");
	}

	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		ctx.close(promise);
		logger.debug(System.currentTimeMillis() + " " + this + " close。");
	}

	public void read(ChannelHandlerContext ctx) throws Exception {
		ctx.read();
		logger.debug(System.currentTimeMillis() + " " + this + " read。");
	}

	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		ctx.write(msg, promise);
		logger.debug(System.currentTimeMillis() + " " + this + " write。");
	}

	public void flush(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		logger.debug(System.currentTimeMillis() + " " + this + " flush。");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
		logger.warn("Unexpected exception from downstream:"
				+ cause.getMessage());
		ctx.close();
		logger.debug(System.currentTimeMillis() + " 客户端异常退出");
	}

	/*
	 * @Override public void channelActive(ChannelHandlerContext ctx) throws
	 * Exception { ctx.fireChannelActive(); ctx.writeAndFlush(firstMessage);
	 * logger.debug(System.currentTimeMillis() +
	 * " 客户端active and writeAndFlush. ctx:"+ ctx.getClass()); }
	 * 
	 * @Override public void channelRead(ChannelHandlerContext ctx, Object msg)
	 * throws Exception { logger.debug(System.currentTimeMillis() +
	 * " 客户端收到服务器响应数据"); ByteBuf buf = (ByteBuf) msg; byte[] req = new
	 * byte[buf.readableBytes()]; buf.readBytes(req); String body = new
	 * String(req, "UTF-8"); logger.debug("Now is:" + body);
	 * 
	 * }
	 * 
	 * @Override public void channelReadComplete(ChannelHandlerContext ctx)
	 * throws Exception { ctx.flush(); logger.debug(System.currentTimeMillis() +
	 * " 客户端收到服务器响应数据处理完成"); }
	 * 
	 * @Override public void exceptionCaught(ChannelHandlerContext ctx,
	 * Throwable cause) throws Exception {
	 * logger.warning("Unexpected exception from downstream:" +
	 * cause.getMessage()); ctx.close(); logger.debug(System.currentTimeMillis()
	 * + " 客户端异常退出"); }
	 */
}