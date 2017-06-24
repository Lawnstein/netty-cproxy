/**
 * netty-cproxy.
 * Copyright (C) 1999-2017, All rights reserved.
 *
 * This program and the accompanying materials are under the terms of the Apache License Version 2.0.
 */
package testor.olproxy.provider;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * server端网络IO事件处理
 * 
 * @author xwalker
 *
 */
public class EchoSercerHandler extends ChannelInboundHandlerAdapter {
	protected final static Logger logger = LoggerFactory.getLogger(EchoSercerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if (logger.isDebugEnabled())
			logger.debug(this + " Read from Channel "
					+ ctx.channel() + " with message (" + msg.getClass() + ")");		
		
		logger.debug("服务器读取到客户端请求...");
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		buf.release();
		String body = new String(req, "UTF-8");
		// String body = (String) msg;
		logger.debug("the echo server receive order: " + body);
		String curentTime = "TIME".equalsIgnoreCase(body) ? (new Date(
				System.currentTimeMillis()).toString() + "\n") : "?";
		ByteBuf resp = Unpooled.copiedBuffer(curentTime.getBytes());
		ctx.write(resp);
		logger.debug("服务器channelRead响应完成: " + curentTime + ",ctx:"
				+ ctx.getClass());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		logger.debug("服务器channelReadComplete响应完成。,ctx:" + ctx.getClass());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
		logger.debug("服务器异常退出" + cause.getMessage());
	}
}